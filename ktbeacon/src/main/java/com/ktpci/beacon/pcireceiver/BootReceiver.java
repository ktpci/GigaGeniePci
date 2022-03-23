package com.ktpci.beacon.pcireceiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.ktpci.beacon.PCI;
import com.ktpci.beacon.pciredux.state.PCIState;

import java.util.Timer;
import java.util.TimerTask;


public class BootReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {

        String cType = String.valueOf(PCIState.from(context).getType());
        if(cType.equals("ACTIVE") || cType.equals("INACTIVE")){
            String aId = PCIState.from(context).getAdid();
            String pCd = PCIState.from(context).getPartnerCode();
            int mode = PCIState.from(context).getPcimode();
//            save(context,"BootReceive -> ADID : "+aId +", partnercode : " +pCd + ", mode : " + mode + ", Type : " + cType);
            PCI.with(context).disagreeTerms();
            Timer timer = new Timer(true);
            TimerTask timerTask = new TimerTask() {
                @Override
                public void run() {
                    PCI.with(context).agreeTerms(aId, pCd, mode);
                }
            };
            timer.schedule(timerTask,  3 * 1000);
        }
    }
}