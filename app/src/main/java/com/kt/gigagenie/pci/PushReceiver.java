package com.kt.gigagenie.pci;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.util.Log;


import com.gnifrix.debug.GLog;
import com.kt.gigagenie.pci.broadcast.BroadcastContext;
import com.kt.gigagenie.pci.data.pci_db.ErrorInfo;
import com.kt.gigagenie.pci.net.NetManager;
import com.kt.gigagenie.pci.system.PciProperty;
import java.io.UnsupportedEncodingException;
import java.util.HashMap;


import static com.kt.gigagenie.pci.PciManager.KPNSReq;

import static com.kt.gigagenie.pci.Pci_Service.AlarmPushMsg;
import static com.kt.gigagenie.pci.Pci_Service.CheckInPushMsg;
import static com.kt.gigagenie.pci.Pci_Service.SettingPushMsg;
import static com.kt.gigagenie.pci.Pci_Service.pciManager;
import static com.kt.gigagenie.pci.data.PciSrvData.kpns_token;



public  class PushReceiver extends BroadcastReceiver implements BroadcastContext {
    public static final String logHeader = "PushReceiver|KPNS_Receiver";
    /** ATV 버전에 따른 추가 by dalkommjk | v15.00.03 | 2021-09-24 */
    Pci_Service kpnsservice;
    static IntentFilter intentFilter_Recv;
    public void pushreceiver(Pci_Service _service){
        kpnsservice = _service;
    }
    public PushReceiver(){
        initIntentFilter();
    }


    /**
     * intentFilter를 초기화 시킨다.
     * kpns 동적 intent로 수정 By dalkommJK | v15.00.02  | 2021.09.13
     */
    public void initIntentFilter(){
        intentFilter_Recv = new IntentFilter();
        intentFilter_Recv.addAction(RECV_KPNS_REGISTRATION);
        intentFilter_Recv.addAction(RECV_KPNS_STATUSOFSERVICE);
        intentFilter_Recv.addAction(RECV_KPNS_STATUSOFMYPUSH);
        intentFilter_Recv.addAction(RECV_KPNS_REREGISTER);
        intentFilter_Recv.addAction(RECV_KPNS_SERVICEAVAILABLE);
        intentFilter_Recv.addAction(RECV_KPNS_SERVICEUNAVAILABLE);
        intentFilter_Recv.addAction(RECV_KPNS_SERVICEAVAILBILTY);
        intentFilter_Recv.addAction(RECV_KPNS_PAMESSAGE);
        intentFilter_Recv.addAction(RECV_KPNS_MESSAGE);
        intentFilter_Recv.addAction(RECV_KPNS_PA_REGISTRATION);
        intentFilter_Recv.addAction(RECV_KPNS_PA_SERVICESTATUS);
        intentFilter_Recv.addAction(RECV_KPNS_PA_UNREGISTERED);
        intentFilter_Recv.addCategory(PKG_PCI);

    }
    /** ------------ */

    @Override
    public void onReceive(Context context, Intent intent) {
        // 어플리케이션 등록 결과 수신
        if (intent.getAction().equals(RECV_KPNS_REGISTRATION)) {

            try {
                byte[] token = intent.getByteArrayExtra("token");
                String error = intent.getStringExtra("error");
                String tokenStr = new String(token, "UTF-8");
                kpns_token = tokenStr;

               // netManager.apiPci6010_activation_token(); /** KPNS추가 - by dalkommJK | 2019-09-30  */
                GLog.printDbg(this, "<<< KPNS Key : " +tokenStr +" >>> " + error);
                /** KPNS 추가 보완_v10.00.03 | 2019-12-18 by dalkommJK */
                if(token == null){
                    KPNSReq(true);
                }else{
                    pciManager.UpdateKPNSToken();
                }

            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        else if (intent.getAction().equals(RECV_KPNS_MESSAGE)) {  //com.ktpns.pa.receive.MESSAGE
            String strMsg="";
            try {

                byte[] message = intent.getByteArrayExtra("message");
                strMsg = new String (message,"UTF-8");
                HashMap<String,String> dataMap = new HashMap<String,String>();
                String[] pushDatas = strMsg.split("&");
                if(pushDatas != null){
                    for(String pushData : pushDatas){
                        String[] strings = pushData.split("=");
                        if(strings != null){
                            String key = strings[0];
                            String value = strings[1];
                            if(key.contains("data.Type")){
                                dataMap.put("Type", value);
                                if(dataMap.get("Type").equals("CheckInList")) {
                                    ChckInListRcv(intent);
                                }else if(dataMap.get("Type").equals("alarm")){
                                    AlarmPushMsg(intent);
                                }else{
                                    SettingPushMsg(intent);   // 재부팅 푸쉬
                                }
                            }
                        }
                    }
                }

            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }
        }else {
            GLog.printInfo(logHeader,"Not Intent Filter Registrated About This Action ");
        }

    }
    private void ChckInListRcv(Intent intent) {
        String strMsg = "";
        try {
            byte[] message = intent.getByteArrayExtra("message");
            strMsg = new String(message, "UTF-8");
            HashMap<String, String> dataMap = new HashMap<String, String>();
            String[] pushDatas = strMsg.split("&");
            if (pushDatas != null) {
                for (String pushData : pushDatas) {
                    String[] strings = pushData.split("=");
                    if (strings != null) {
                        String key = strings[0];
                        String value = strings[1];
                        if (key.contains("data.PIDList")) {
                            value = value.replace("\'", "\"");
                            dataMap.put("PIDList", value);
                        }
                        if (key.contains("data.EVENT")) {
                            value = value.replace("\'", "\"");
                            dataMap.put("EVENT", value);
                        }
                    }
                }
            }
//                String inentMsg = "{\"PIDList\":[" + dataMap.get("PIDList") + "]," + "\"EVENT\":" + dataMap.get("EVENT") + "}";
//                Log.e("pci",inentMsg );
            CheckInPushMsg("[" + dataMap.get("PIDList") + "]", dataMap.get("EVENT"));
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
    }

    public static IntentFilter getIntentFilter_Recv() { return intentFilter_Recv; }
    public void setIntentFilter_Recv(IntentFilter _intentFilter ) { intentFilter_Recv = _intentFilter; }
    public void destroy(){
        kpnsservice = null;
        intentFilter_Recv = null;
    }


}