package com.ktpci.beacon.pciredux.state;

import android.os.Handler;

import androidx.annotation.NonNull;
import androidx.work.WorkManager;

import com.ktpci.beacon.pciredux.action.ActionDowngradeState;
import com.ktpci.beacon.pciredux.action.ActionFetchPolicy;
import com.ktpci.beacon.pciredux.core.PCIStore;
import com.ktpci.beacon.pciutil.PCIAlarm;
import com.ktpci.beacon.pciutil.PCILog;
import com.ktpci.beacon.pciutil.PCIStorage;

import java.util.Timer;
import java.util.TimerTask;

import static com.ktpci.beacon.PCI.runable;
import static com.ktpci.beacon.pciredux.state.PCIState.Type.INACTIVE;
import static com.ktpci.beacon.pciutil.PCIAlarm.RunningOneTime;
import static com.ktpci.beacon.pciutil.PCIAlarm.RunningTime;
import static com.ktpci.beacon.pciutil.PCIStorageKey.RUNNINGABLE;
import static com.ktpci.beacon.pciutil.PCIStorageKey.WMSTATE;
import static com.ktpci.beacon.pciutil.PCITime.currentDate;
import static com.ktpci.beacon.pciutil.PCIWork.JobOne;
import static com.ktpci.beacon.pciutil.PCIWork.JobTwo;
import static com.ktpci.beacon.pciutil.PCIWork.WorkC;

public class PCIState2Inactive extends PCIState {

    public PCIState2Inactive(PCIState oldState) {
        super(oldState);
        this.type = INACTIVE;
    }

    @Override
    public void onKeep() {

        if (this.maxType().getValue() < this.type.getValue()) {
            PCIStore.getInstance(context).dispatchTriggeredByInternalRedux(new ActionDowngradeState(this.maxType()));
        }
    }

    @Override
    public void onEnter(@NonNull PCIState.Type prevStateLevel) {
        if (this.type.getValue() < prevStateLevel.getValue()) { // downgraded
            this.setOptIn(false); // opt-in
            PCIStorage.put(context, RUNNINGABLE, false);
            WorkManager.getInstance(context).cancelAllWorkByTag(JobOne);
            WorkManager.getInstance(context).cancelAllWorkByTag(JobTwo);
            PCIAlarm.WakeCancel(context);
            PCIAlarm.UpdateCancel(context);
            PCIStorage.put(context, WMSTATE, false);
            WorkC(context);

        } else { // upgraded
            PCIStore.getInstance(context).dispatchTriggeredByInternalRedux(new ActionFetchPolicy());

            String cTime = currentDate("yyyyMMddHHmmss");
            this.setRuntime(cTime);

            Timer timer = new Timer(true);
            TimerTask timerTask = new TimerTask() {
                @Override
                public void run() {
                    new Handler(context.getMainLooper()).post(() -> {


                        switch (PCIState.from(context).getPcimode()) {
                            case 0:  // Never Run...
                                PCILog.d("None Mode");
                                break;
                            case 1:   //일반모드  -- 주기적으로 비콘송출
                                PCILog.d("INACTIVE :: General Mode");
                                runable = true;
                                RunningTime(context); // running 확인갱신 (24H)
                                RunningOneTime(context);
                                WorkC(context);

                                break;
                            case 2:  //야간모드 -- 야간에는 비콘송출 금지
                                PCILog.d("INACTIVE :: Night Mode");
                                runable = true;
                                RunningTime(context); // running 확인갱신 (24H)
                                RunningOneTime(context);
                                WorkC(context);
                                break;
                            default:
                                break;
                        }
                    });
                }
            };
            timer.schedule(timerTask, (1 * 1000)+(500));
        }
    }

    @Override
    public void onLeave(@NonNull PCIState.Type nextStateLevel) {
        if (nextStateLevel.getValue() < INACTIVE.getValue()) {
            new Handler(context.getMainLooper()).post(() -> {
                this.setTermAgreed(false);
            });
        }

    }

}
