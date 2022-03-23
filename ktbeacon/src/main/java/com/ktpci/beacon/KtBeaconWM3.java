package com.ktpci.beacon;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.IntentFilter;

import androidx.annotation.NonNull;
import androidx.concurrent.futures.ResolvableFuture;
import androidx.work.ListenableWorker;
import androidx.work.WorkerParameters;

import com.google.common.util.concurrent.ListenableFuture;
import com.ktpci.beacon.pcireceiver.RunningReceiver;
import com.ktpci.beacon.pciutil.PCILog;

import java.util.Timer;
import java.util.TimerTask;

import static com.ktpci.beacon.PCI.runable;


public class KtBeaconWM3 extends ListenableWorker {
    String TAG = "emarttest";
    String Sub = "KTBeaocnWM2";
    private RunningReceiver runReceiver;
    /**
     * @param appContext   The application {@link Context}
     * @param workerParams Parameters to setup the internal state of this worker
     */
    public KtBeaconWM3(@NonNull Context appContext, @NonNull WorkerParameters workerParams) {
        super(appContext, workerParams);
        PCILog.d("WM3 start");
        if(runable == true) {
            runable = false;
            runReceiver = new RunningReceiver();
            IntentFilter intentFilter = new IntentFilter();
            intentFilter.addAction("com.ktpci.running.req");
            intentFilter.addAction("com.ktpci.running.true");
            intentFilter.addAction("com.ktpci.running.false");
            appContext.registerReceiver(runReceiver, intentFilter);

        }
    }

    @SuppressLint("RestrictedApi")
    @NonNull
    @Override
    public ListenableFuture<Result> startWork() {
        ResolvableFuture<Result> mFuture = ResolvableFuture.create();
        Timer timer = new Timer(true);
        TimerTask timerTask = new TimerTask() {
            @Override
            public void run() {
                Result result = Result.success();
                mFuture.set(result);
                getApplicationContext().unregisterReceiver(runReceiver);
                runable = true;
                PCILog.d("WM3 success");

            }
        };
        timer.schedule(timerTask,  7 * 60 * 1000);

        return mFuture;
    }

}