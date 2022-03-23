package com.ktpci.beacon.pciredux.state;

import androidx.annotation.NonNull;
import androidx.work.WorkManager;

import com.ktpci.beacon.pciutil.PCIAlarm;

import static com.ktpci.beacon.pciredux.state.PCIState.Type.DEFAULT;

public class PCIState1Default extends PCIState {

    public PCIState1Default(PCIState oldState) {
        super(oldState);
        this.type = DEFAULT;
    }

    @Override
    public void onKeep() {

        /** 삼성단말 회피 부분 제거 by dalkommjk | 2020-03-15
         PCIBitsoundManager.configure(context, false);
         */

    }

    @Override
    public void onEnter(@NonNull PCIState.Type prevStateLevel) {
        if (this.type.getValue() < prevStateLevel.getValue()) { // downgraded

//            this.setUuid(null); // agree-terms
//            this.setAdid(null); // agree-term
//            this.setPhoneNumber(null); // agree-terms
            this.setTermAgreed(false); // agree-terms
//            this.setPartnerCode("1004");
//            this.setAdidUseAgreed(false); // agree-terms
//            this.setAdPushAgreed(false); // agree-terms
//            this.setBleUseAgreed(false); // agree-terms
            WorkManager.getInstance(context).cancelAllWork();
            PCIAlarm.WakeCancel(context);
            PCIAlarm.UpdateCancel(context);
            PCIAlarm.RunningCancel(context);

        } else { // upgraded
            throw new IllegalStateException("Must not reach upgrade block in default");
        }
    }

    @Override
    public void onLeave(@NonNull PCIState.Type nextStateLevel) {

    }
}
