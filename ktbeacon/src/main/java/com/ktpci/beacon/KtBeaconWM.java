package com.ktpci.beacon;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.IntentFilter;
import android.os.Build;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.concurrent.futures.ResolvableFuture;
import androidx.work.ListenableWorker;
import androidx.work.WorkerParameters;

import com.google.common.util.concurrent.ListenableFuture;
import com.ktpci.beacon.pcireceiver.RunningReceiver;
import com.ktpci.beacon.pciredux.state.PCIState;
import com.ktpci.beacon.pciutil.PCILog;
import com.ktpci.beacon.pciutil.PCIStorage;
import com.ktpci.beacon.pciutil.PCIStorageKey;

import java.util.Timer;
import java.util.TimerTask;

import static com.ktpci.beacon.PCI.runable;
import static com.ktpci.beacon.pciutil.PCISave.save;
import static com.ktpci.beacon.pciutil.PCIStorageKey.RUNNINGABLE;
import static com.ktpci.beacon.pciutil.PCIStorageKey.WMSTATE;


@RequiresApi(api = Build.VERSION_CODES.KITKAT)
public class KtBeaconWM extends ListenableWorker {
//    private boolean dalkommEx = false;
    private String google_adid;
    private String partnercode;
    private RunningReceiver runReceiver;
    private boolean wmstate;


    /**
     * @param appContext   The application {@link Context}
     * @param workerParams Parameters to setup the internal state of this worker
     */

    @SuppressLint("WrongConstant")
    public KtBeaconWM(@NonNull Context appContext, @NonNull WorkerParameters workerParams) {
        super(appContext, workerParams);
        PCILog.d("WM1 start");


        if(runReceiver == null) {
            runReceiver = new RunningReceiver();
            IntentFilter intentFilter = new IntentFilter();
            intentFilter.addAction("com.ktpci.running.req");
            intentFilter.addAction("com.ktpci.running.true");
            intentFilter.addAction("com.ktpci.running.false");
            appContext.registerReceiver(runReceiver, intentFilter);
        }
        wmstate = PCIStorage.getBoolean(appContext, WMSTATE,true);

        save(appContext, "WM1 start -> wmstate : " + wmstate);
//        partnercode = PCIState.from(appContext).getPartnerCode();
//        if(!KtAdvertise.getInstance().isStarted()){
//            google_adid = PCIStorage.getString(appContext, PCIStorageKey.SECRETADID,"secretadid");
//            if(google_adid.isEmpty()) { KtAdvertise.getInstance().start(appContext,"start", "1004", partnercode);
//            }else{ KtAdvertise.getInstance().start(appContext,"start", google_adid, partnercode); }
//            PCILog.d("Beacon state -> start");
//            save(appContext, "WM1 start ::: Beacon Start");
//
//        }else{
//            PCILog.d("Beacon state -> running...");
//            save(appContext, "WM1 start ::: Beacon is running");
//        }
    }


    @SuppressLint("RestrictedApi")
    @NonNull
    @Override
    public ListenableFuture<Result> startWork() {
        ResolvableFuture<Result> mFuture = ResolvableFuture.create();
        partnercode = PCIState.from(getApplicationContext()).getPartnerCode();
        if(!KtAdvertise.getInstance().isStarted() && wmstate){
            PCIStorage.put(getApplicationContext(), WMSTATE, false);

            google_adid = PCIStorage.getString(getApplicationContext(), PCIStorageKey.SECRETADID,"secretadid");
            if(google_adid.isEmpty()) { KtAdvertise.getInstance().start(getApplicationContext(),"start", "1004", partnercode);
            }else{ KtAdvertise.getInstance().start(getApplicationContext(),"start", google_adid, partnercode); }
            PCILog.d("Beacon state -> start");
            save(getApplicationContext(), "WM1 start ::: Beacon Start");


            Timer timer = new Timer(true);
            TimerTask timerTask = new TimerTask() {
                @Override
                public void run() {
                    KtAdvertise.getInstance().finish();
                    Result result = Result.success();
                    mFuture.set(result);

                    if( runReceiver != null){
                        getApplicationContext().unregisterReceiver(runReceiver);
                    }
                    PCILog.d("WM1 success");
                    save(getApplicationContext(), "WM1 start ::: finish");
                    PCIStorage.put(getApplicationContext(), WMSTATE, true);
                }
            };
            timer.schedule(timerTask,  (4 * 60 * 1000) + (30 * 1000));



        }else{

            PCILog.d("Beacon state -> running...");
            save(getApplicationContext(), "WM1 start ::: Beacon is running and finish");
            Result result = Result.success();
            mFuture.set(result);

        }



        return mFuture;
    }
    @Override
    public void onStopped() {
        // Cleanup because you are being stopped.
    }


}
