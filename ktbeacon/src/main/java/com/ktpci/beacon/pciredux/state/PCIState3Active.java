package com.ktpci.beacon.pciredux.state;

import android.annotation.SuppressLint;
import android.os.Handler;

import androidx.annotation.NonNull;
import androidx.work.WorkManager;

import com.ktpci.beacon.pciredux.action.ActionDowngradeState;
import com.ktpci.beacon.pciredux.core.PCIStore;
import com.ktpci.beacon.pciutil.PCILog;
import com.ktpci.beacon.pciutil.PCIStorage;

import java.util.Timer;
import java.util.TimerTask;

import static com.ktpci.beacon.PCI.runable;
import static com.ktpci.beacon.pciredux.state.PCIState.Type.ACTIVE;
import static com.ktpci.beacon.pciutil.PCIAlarm.NightTime;
import static com.ktpci.beacon.pciutil.PCIAlarm.UpdateTime;
import static com.ktpci.beacon.pciutil.PCIAlarm.WakeTime;
import static com.ktpci.beacon.pciutil.PCIStorageKey.RUNNINGABLE;
import static com.ktpci.beacon.pciutil.PCIStorageKey.WMSTATE;
import static com.ktpci.beacon.pciutil.PCIWork.JobThree;
import static com.ktpci.beacon.pciutil.PCIWork.WorkA;
import static com.ktpci.beacon.pciutil.PCIWork.WorkB;

public class PCIState3Active extends PCIState {

    public PCIState3Active(PCIState oldState) {
        super(oldState);
        this.type = ACTIVE;
    }

    @Override
    public void onKeep() {
        if (this.maxType().getValue() < this.type.getValue()) {
            PCIStore.getInstance(context).dispatchTriggeredByInternalRedux(new ActionDowngradeState(this.maxType()));
        }
    }

    @SuppressLint("WrongConstant")
    @Override
    public void onEnter(@NonNull Type prevStateLevel) {
        if (this.type.getValue() < prevStateLevel.getValue()) {
            // downgraded
//            this.setSaid(null); // check-in
        } else { // upgraded
            PCIStorage.put(context, RUNNINGABLE, true);
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
                                    PCILog.d("ACTIVE :: General Mode");
                                    PCIStorage.put(context, WMSTATE, true);
                                    WorkManager.getInstance(context).cancelAllWorkByTag(JobThree);
                                    UpdateTime(context); //adid 갱신 (4hour)
                                    WakeTime(context); //beacon 갱신 (5min)
                                    WorkA(context); //beaocon job (4m30s)
                                    WorkB(context); //timer backup (5m)
                                    break;
                                case 2:  //야간모드 -- 야간에는 비콘송출 금지
                                    PCILog.d("ACTIVE :: Night Mode");
                                    PCIStorage.put(context, WMSTATE, true);
                                    WorkManager.getInstance(context).cancelAllWorkByTag(JobThree);
                                    UpdateTime(context);
                                    NightTime(context); // waketime or nighttime
                                    WorkA(context);
                                    WorkB(context);
                                    break;
                                default:
                                    break;
                            }
                        });
                    }
                };
                timer.schedule(timerTask, (3 * 1000));

        }
    }

    @Override
    public void onLeave(@NonNull PCIState.Type nextStateLevel) {
        if (nextStateLevel.getValue() < ACTIVE.getValue()) {
            new Handler(context.getMainLooper()).post(() -> {
                this.setOptIn(false);
            });
        }
    }


}
