package com.ktpci.beacon.pcireceiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.ktpci.beacon.pciredux.state.PCIState;
import com.ktpci.beacon.pciutil.PCILog;
import com.ktpci.beacon.pciutil.PCIStorage;

import static com.ktpci.beacon.pciutil.PCIAlarm.NightTime;
import static com.ktpci.beacon.pciutil.PCIAlarm.WakeTime;
import static com.ktpci.beacon.pciutil.PCIPackageUtil.isWorkScheduled;

import static com.ktpci.beacon.pciutil.PCISave.save;
import static com.ktpci.beacon.pciutil.PCIStorageKey.RUNNINGABLE;
import static com.ktpci.beacon.pciutil.PCIStorageKey.WMSTATE;
import static com.ktpci.beacon.pciutil.PCIWork.WorkA;
import static com.ktpci.beacon.pciutil.PCIWork.WorkB;


public class WakeReceiver extends BroadcastReceiver {
    private boolean wmA_State ;
    private boolean wmB_State ;
    private String JobOne= "KtWMOne";
    private String JobTwo= "KtWMTwo";


    @Override
    public void onReceive(Context context, Intent intent) {
//        save(context, "****알람수신" );
        PCILog.d("WakeAlarm receive");
        wmA_State = isWorkScheduled(context, JobOne);  //Job 여부확인
        wmB_State = isWorkScheduled(context,JobTwo);
        save(context, "****알람수신 : JobOne -> " + wmA_State );
        PCIStorage.put(context, WMSTATE, true);
        /** PCIMODE 관련 */
        switch (PCIState.from(context).getPcimode()){
            case 1:   //일반모드  -- 주기적으로 비콘송출
                WakeTime(context);
                if (!wmA_State) { WorkA(context); }
                if (!wmB_State) { WorkB(context); }
                break;
            case 2:  //야간모드 -- 야간에는 비콘송출 금지
                NightTime(context);
                if (!wmA_State) { WorkA(context); }
                if (!wmB_State) { WorkB(context); }
                break;
            default:
                break;
        }
    }
}

