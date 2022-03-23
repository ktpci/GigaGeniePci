package com.ktpci.beacon;

import android.annotation.SuppressLint;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;

import androidx.annotation.NonNull;
import androidx.concurrent.futures.ResolvableFuture;
import androidx.work.ListenableWorker;
import androidx.work.WorkerParameters;

import com.google.common.util.concurrent.ListenableFuture;
import com.ktpci.beacon.pcireceiver.RunningReceiver;
import com.ktpci.beacon.pcireceiver.UpdateReceiver;
import com.ktpci.beacon.pcireceiver.WakeReceiver;
import com.ktpci.beacon.pciredux.state.PCIState;
import com.ktpci.beacon.pciutil.PCILog;

import java.util.Timer;
import java.util.TimerTask;

import static com.ktpci.beacon.pciutil.PCIAlarm.NightTime;
import static com.ktpci.beacon.pciutil.PCIAlarm.RunningTime;
import static com.ktpci.beacon.pciutil.PCIAlarm.UpdateTime;
import static com.ktpci.beacon.pciutil.PCIAlarm.WakeTime;


public class KtBeaconWM2 extends ListenableWorker {
    String TAG = "emarttest";
    String Sub = "KTBeaocnWM2";
    /**
     * @param appContext   The application {@link Context}
     * @param workerParams Parameters to setup the internal state of this worker
     */

    public KtBeaconWM2(@NonNull Context appContext, @NonNull WorkerParameters workerParams) {
        super(appContext, workerParams);
        PCILog.d("WM2 Start");
//        Log.e(TAG, Sub + " *** 워크매니저2 시작  ");
//        save(getApplicationContext(), " *** 워크매니저2 시작  ");

        Intent intent = new Intent(appContext, WakeReceiver.class);
        PendingIntent sender = PendingIntent.getBroadcast(appContext, 1004, intent, PendingIntent.FLAG_NO_CREATE);
        if (sender == null) {
            PCILog.d("WM2 -> wake alarm Regi");
            switch (PCIState.from(appContext).getPcimode()) {
                case 1:   //일반모드
                    WakeTime(appContext);
                    break;
                case 2:  //야간모드
                    NightTime(appContext);
                    break;
                default:
                    break;
            }
        }
        else { PCILog.d("WM2 -> already wake alarm Regi"); }

        Intent intent2 = new Intent(appContext, UpdateReceiver.class);
        PendingIntent sender2 = PendingIntent.getBroadcast(appContext, 1005, intent2, PendingIntent.FLAG_NO_CREATE);
        if (sender2 == null) {
            PCILog.d("WM2 -> update alarm Regi");
            switch (PCIState.from(appContext).getPcimode()) {
                case 1:
                    UpdateTime(appContext);
                    break;
                case 2:
                    UpdateTime(appContext);
                    break;
                default:
                    break;
            }
        }
        else { PCILog.d("WM2 -> already update alarm Regi"); }

        Intent intent3 = new Intent(appContext, RunningReceiver.class);
        PendingIntent sender3 = PendingIntent.getBroadcast(appContext, 1006, intent3, PendingIntent.FLAG_NO_CREATE);
        if (sender3 == null) {
            PCILog.d("WM2 -> running alarm Regi");
            switch (PCIState.from(appContext).getPcimode()) {
                case 1:
                    RunningTime(appContext);
                    break;
                case 2:
                    RunningTime(appContext);
                    break;
                default:
                    break;
            }
        }
        else { PCILog.d("WM2 -> already running alarm Regi"); }

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
//                    Log.e(TAG, Sub + " --- 워크매니저2 완료  ");
//                    save(getApplicationContext(), " --- 워크매니저2 완료  ");
                    PCILog.d("WM2 success");
                }
            };
        timer.schedule(timerTask,  5 * 60 * 1000);

        return mFuture;
    }

}
