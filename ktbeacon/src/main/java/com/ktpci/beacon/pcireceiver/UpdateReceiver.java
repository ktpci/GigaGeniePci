package com.ktpci.beacon.pcireceiver;

import android.annotation.SuppressLint;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.ktpci.beacon.KtAdvertise;
import com.ktpci.beacon.pciredux.state.PCIState;
import com.ktpci.beacon.pciutil.PCIChiper;
import com.ktpci.beacon.pciutil.PCILog;
import com.ktpci.beacon.pciutil.PCIStorage;
import com.ktpci.beacon.pciutil.PCIStorageKey;

import static com.ktpci.beacon.pciutil.PCIAlarm.UpdateTime;
import static com.ktpci.beacon.pciutil.PCITime.currentDate;
import static com.ktpci.beacon.pciutil.PCITime.currentTime;

public class UpdateReceiver extends BroadcastReceiver {
    private int nowTime = 0;
    private String originAdid = "000000001";
    private String secAdid = "000000001";

    @SuppressLint("WrongConstant")
    @Override
    public void onReceive(Context context, Intent intent) {
        PCILog.d("UpdateAlarm receive");
//        save(context, "***알람수신 | Adid변경목적" );
        originAdid = PCIState.from(context).getAdid();
        nowTime = currentTime("HHmm");
        secAdid = PCIChiper.Encrypt(originAdid, currentDate("yyyyMMdd"));
//        secAdid = PCIChiper.Encrypt(originAdid, preDate("yyyyMMdd"));
        PCIStorage.put(context, PCIStorageKey.SECRETADID, secAdid);
//        save(context, " ADID Changed --> 이전날짜 : " + preDate("yyyyMMdd") + ", 현재날짜 : " + currentDate("yyyyMMdd")  + "/ 기존adid: "+ originAdid + " / 암호화된adid : " + secAdid );
        if(KtAdvertise.getInstance().isStarted()){
            KtAdvertise.getInstance().finish();
            PCILog.d("Beacon Advertising stop!!");
        }
        PCILog.d("SecretADID is changed.");

//        if(nowTime < 400){     // Compare... AM 4hour
//            secAdid = PCIChiper.Encrypt(originAdid, preDate("yyyyMMdd"));
//            PCIStorage.put(context, PCIStorageKey.SECRETADID, secAdid);
//            save(context, " ADID Changed --> 이전날짜 : " + preDate("yyyyMMdd") + ", 현재날짜 : " + currentDate("yyyyMMdd")  + "/ 기존adid: "+ originAdid + " / 암호화된adid : " + secAdid );
//            PCILog.d("SecretADID is changed.");
//        }else{
//            secAdid = PCIChiper.Encrypt(originAdid, currentDate("yyyyMMdd"));
//            PCIStorage.put(context, PCIStorageKey.SECRETADID, secAdid);
//            save(context, " ADID Changed --> 이전날짜 : " + preDate("yyyyMMdd") + ", 현재날짜 : " + currentDate("yyyyMMdd")  + "/ 기존adid: "+ originAdid + " / 암호화된adid : " + secAdid );
//            PCILog.d("SecretADID is changed.");
//        }

        UpdateTime(context); // next UpdatTime set

    }
}
