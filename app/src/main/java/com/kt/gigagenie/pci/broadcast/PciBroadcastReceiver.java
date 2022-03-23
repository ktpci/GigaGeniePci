package com.kt.gigagenie.pci.broadcast;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.util.Log;

import com.gnifrix.debug.GLog;
import com.gnifrix.system.GRunnable;
import com.gnifrix.system.GThread;
import com.gnifrix.util.HsUtil_Date;
import com.kt.gigagenie.pci.Global;
import com.kt.gigagenie.pci.MainActivity;
import com.kt.gigagenie.pci.PciManager;
import com.kt.gigagenie.pci.Pci_Service;
import com.kt.gigagenie.pci.data.DataManager;
import com.kt.gigagenie.pci.data.PciSrv_PidData;
import com.kt.gigagenie.pci.data.PidListManager;
import com.kt.gigagenie.pci.data.StbData;
import com.kt.gigagenie.pci.data.PciSrv_VoiceChkInData;
import com.kt.gigagenie.pci.data.pci_db.ErrorInfo;
import com.kt.gigagenie.pci.data.stb_db.EventPidData;
import com.kt.gigagenie.pci.data.stb_db.StbDb_Common;
import com.kt.gigagenie.pci.net.json.JsonArray;
import com.kt.gigagenie.pci.net.json.JsonException;
import com.kt.gigagenie.pci.net.json.JsonObject;
import com.kt.gigagenie.pci.net.json.JsonParser;
import com.kt.gigagenie.pci.net.json.pci_response.PciResp_CheckIn;
import com.kt.gigagenie.pci.snd.NonAudiblePlayer;
import com.kt.gigagenie.pci.system.PciRuntimeException;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;

import static android.content.Context.MODE_PRIVATE;
import static com.kt.gigagenie.pci.Pci_Service.pciProperty;

/**
 * Pci Service 구동 중 MC 및 GiGaGenie 의 타 App 과의 통신을 위한 동적 Receiver
 * Created by LeeBaeng on 2018-10-02.
 */
public class PciBroadcastReceiver extends BroadcastReceiver implements BroadcastContext {
    public static final String logHeader = "PciBroadcastReceiver";

    Pci_Service service;
    IntentFilter intentFilter_Recv;

    public PciBroadcastReceiver(Pci_Service _service){
        service = _service;
        initIntentFilter();
    }



    /**
     * intentFilter를 초기화 시킨다.
     */
    private void initIntentFilter(){
        intentFilter_Recv = new IntentFilter();
        intentFilter_Recv.addAction(RECV_ACTION_KEEPALIVE_CHECK);
        intentFilter_Recv.addAction(RECV_ACTION_MCSTATE_RESP);
        intentFilter_Recv.addAction(RECV_ACTION_VOICE_ALARM);
        intentFilter_Recv.addAction(RECV_ACTION_PCILIST_REQ);
        intentFilter_Recv.addAction(RECV_ACTION_PCI_SETTING_REQ);
        intentFilter_Recv.addAction(RECV_ACTION_PCI_POLICY_REQ);
        intentFilter_Recv.addAction(RECV_ACTION_USER_CHANGE);
        intentFilter_Recv.addAction(RECV_ACTION_USER_DISCONNECT);

        intentFilter_Recv.addAction(RECV_ACTION_PUSH_CHECKIN_LIST);
        intentFilter_Recv.addAction(RECV_ACTION_PUSH_ALARM);
        intentFilter_Recv.addAction(RECV_ACTION_PUSH_SETTING);

        intentFilter_Recv.addAction(RECV_ACTION_POWER_STANDBY);
        intentFilter_Recv.addAction(RECV_ACTION_POWER_NORMAL);
        intentFilter_Recv.addAction(RECV_ACTION_TVADID_RESP);  //PCI AD by dalkommjk - v16.00.01 | 2022-03-23

    }



    /**
     * Broadcast 수신
     * @param context
     * @param intent
     */
    @Override
    public void onReceive(Context context, Intent intent) {
        try{
            if(intent != null){
                String actionNamne = intent.getAction();

                GLog.printInfo(this, "Receive Broadcast : " + intent.toString() + "( action Name : " + intent.getAction() + " / pkg : " + intent.getPackage() + " )");
                printIntentExtras(intent);

                switch (actionNamne){
                    case RECV_ACTION_KEEPALIVE_CHECK : // KEEP_Alive 수신 from MC
                        // API : CO003
                        sendKeepAliveResp();
                        break;
                    case RECV_ACTION_MCSTATE_RESP : // MC 상태 수신
                        // API : CO002
                        break;

                    case RECV_ACTION_VOICE_ALARM : // VoiceID 수신 from 화자분류 App
                        // API : VID001
                        recvVoiceId(intent);
                        break;

                    case RECV_ACTION_PCILIST_REQ: // PCI리스트 요청 수신 from Other Apps
                        // API : PCI001
                        recvPciListReq(intent);
                        break;

                    case RECV_ACTION_PCI_SETTING_REQ : // 비가청 설정 요청 from Launcher
                        recvPciSettingReq(intent);
                        break;

                    case RECV_ACTION_PCI_POLICY_REQ :
                        recvPciPolicyReq();
                        break;

                    case RECV_ACTION_PUSH_CHECKIN_LIST :
                        recvPushCheckInList(intent);
                        break;

                    case RECV_ACTION_PUSH_ALARM :
                        recvPushAlarm(intent);
                        break;

                    case RECV_ACTION_PUSH_SETTING :
                        recvPushSetting(intent);
                        break;

                    case RECV_ACTION_POWER_NORMAL :
                        PciManager.getInstance().stopStandbyUpdateLoop();
                        break;

                    case RECV_ACTION_POWER_STANDBY :
                        PciManager.getInstance().startStandbyUpdateLoop();
                        break;

                    case RECV_ACTION_USER_CHANGE :
                        recvUserChange(intent);
                        break;

                    case RECV_ACTION_USER_DISCONNECT :
                        recvUserDisconnect(intent);
                        break;

                    case RECV_ACTION_POWER_PASSIVE :
                        break;

                    case RECV_ACTION_TVADID_RESP :   // PCI AD by dalkommjk - v16.00.01 | 2022-03-23
                        recvAdidResp(intent);
                        break;

                    default:
                        break;
                }
            }
            else GLog.printWarn(this, "Receive Broadcast : intent is null");
        }catch (Exception e){
            //GLog.printExceptWithSaveToPciDB(this, "Unhandled Exception !! ", e, ErrorInfo.CODE_INAPP_UNKNOWN_ERR, ErrorInfo.CATEGORY_INAPP);
            Pci_Service pci_service = Global.getInstance().getPciService();
            if(pci_service != null) pci_service.finishPciService();
        }
    }


    private void recvPushAlarm(Intent intent){
        if(Global.getInstance().getPciService() == null || !PciManager.isInitialized() && Pci_Service.pciReady){
            GLog.printWtf(this, "pciManager is not initialized. return recvPushAlarm");
            return;
        }

        String title = intent.getStringExtra("title");
        String text = intent.getStringExtra("text");
        int closeReq = 0;
        try{
            closeReq = Integer.parseInt(intent.getStringExtra("closeReq"));
        }catch (Exception e){
            closeReq = 0;
        }
        int popNum = 0;
        try{
            popNum = Integer.parseInt(intent.getStringExtra("popNum"));
        }catch (Exception e){
            popNum = 0;
        }
        int duration = 0;
        try{
            duration = Integer.parseInt(intent.getStringExtra("duration"));
        }catch (Exception e){
            duration = 0;
        }
        int btnCnt = (closeReq == 0)? 0 : 1;

        String btnTxt = intent.getStringExtra("btnTxt");
        ArrayList<String> btnTextList = new ArrayList<>();
        if(btnCnt > 0){
            btnTxt = (btnTxt != null)? btnTxt : "확인";
            btnTextList.add(btnTxt);
        }

        sendOpenPopup(title, text, closeReq, popNum, duration, btnCnt, btnTextList);
    }

    private void sendOpenPopup(String title, String text, int closeReq, int popNum, int duration, int btnCnt, ArrayList<String> btnTextList){
        Intent sendIntent = new Intent(SEND_ACTION_OPEN_POPUP);
        sendIntent.setPackage(PKG_LAUNCHER);

        sendIntent.putExtra("packagename", PKG_PCI);
        sendIntent.putExtra("title", title);
        sendIntent.putExtra("text", text);
        sendIntent.putExtra("closeReq", closeReq);
        sendIntent.putExtra("popNum", popNum);
        sendIntent.putExtra("btnCnt", btnCnt);

        sendIntent.putExtra("duration", duration);
        sendIntent.putExtra("btnTxt", btnTextList);

        sendBroadcast(sendIntent, "sendOpenPopup");
    }

    private void recvPushSetting(Intent intent){
        if(Global.getInstance().getPciService() == null || !PciManager.isInitialized() && Pci_Service.pciReady){
            GLog.printWtf(this, "pciManager is not initialized. return recvPushSetting");
            return;
        }

        final PciManager pciManager = PciManager.getInstance();
        String cmd_id = intent.getStringExtra("command_id");   //APP_REBOOT

        switch (cmd_id){
            case "APP_REBOOT":
                Context context = null;

                if(Global.getInstance().getPciService() != null){
                    Global.getInstance().getPciService().finishPciService();
                    context = Global.getInstance().getPciService().getApplicationContext();

                    Intent i = new Intent(context, MainActivity.class);
                    int mPendingIntentId = 123456;
                    PendingIntent mPendingIntent = PendingIntent.getActivity(context, mPendingIntentId,  i, PendingIntent.FLAG_CANCEL_CURRENT);
                    AlarmManager mgr = (AlarmManager)context.getSystemService(Context.ALARM_SERVICE);
                    mgr.set(AlarmManager.RTC, System.currentTimeMillis() + 100, mPendingIntent);
                    android.os.Process.killProcess(android.os.Process.myPid()); /** KISA 보완조치 -  by dalkommjk | 2019-10-30 */

                }
                break;

            case "API_6002":
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        pciManager.getNetManager().apiPci6002_sendStbInfo(true);
                    }
                }).start();

                break;

            case "API_6003":
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        pciManager.getNetManager().apiPci6003_getPolicy();
                    }
                }).start();

                break;
            case "bmt":
                Context context_bmt = null;
                if(Global.getInstance().getPciService() != null){
                    Global.getInstance().getPciService().finishPciService();
                    context_bmt = Global.getInstance().getPciService().getApplicationContext();
                    try {
                            SharedPreferences pref = Global.getInstance().getPciService().getSharedPreferences("pref", MODE_PRIVATE);
                            SharedPreferences.Editor editor = pref.edit();
                            editor.clear();
                            editor.putString("mode", "bmt");
                            editor.commit();
                            pciProperty.setApiEnvToBmt();
                            GLog.printWarn(this, "[Change Mode] =====> PCI Server BMT Mode" );
                    }catch (Exception e){
                            GLog.printWarn(this, "PCI Server Change Mode Fail");
                    }
                    Intent i = new Intent(context_bmt, MainActivity.class);
                    int mPendingIntentId = 123456;
                    PendingIntent mPendingIntent = PendingIntent.getActivity(context_bmt, mPendingIntentId,  i, PendingIntent.FLAG_CANCEL_CURRENT);
                    AlarmManager mgr = (AlarmManager)context_bmt.getSystemService(Context.ALARM_SERVICE);
                    mgr.set(AlarmManager.RTC, System.currentTimeMillis() + 100, mPendingIntent);
                    android.os.Process.killProcess(android.os.Process.myPid()); /** KISA 보완조치 -  by dalkommjk | 2019-10-30 */
                }
                break;

            case "live":
                Context context_live = null;
                if(Global.getInstance().getPciService() != null){
                    Global.getInstance().getPciService().finishPciService();
                    context_live = Global.getInstance().getPciService().getApplicationContext();
                    try {
                                SharedPreferences pref = Global.getInstance().getPciService().getSharedPreferences("pref", MODE_PRIVATE);
                                SharedPreferences.Editor editor = pref.edit();
                                editor.clear();
                                editor.putString("mode", "live");
                                editor.commit();
                                pciProperty.setApiEnvToLive();
                                GLog.printWarn(this, "[Change Mode] =====> PCI Server Live Mode" );
                    }catch (Exception e){
                                GLog.printWarn(this, "PCI Server Change Mode Fail");
                    }
                    Intent i = new Intent(context_live, MainActivity.class);
                    int mPendingIntentId = 123456;
                    PendingIntent mPendingIntent = PendingIntent.getActivity(context_live, mPendingIntentId,  i, PendingIntent.FLAG_CANCEL_CURRENT);
                    AlarmManager mgr = (AlarmManager)context_live.getSystemService(Context.ALARM_SERVICE);
                    mgr.set(AlarmManager.RTC, System.currentTimeMillis() + 100, mPendingIntent);
                    android.os.Process.killProcess(android.os.Process.myPid()); /** KISA 보완조치 -  by dalkommjk | 2019-10-30 */

                }
                break;

            case "SOUND_SET_Y":
                try{
                    new Thread(new Runnable() {
                        @Override
                        public void run() {
                            if(!pciManager.getNonAudiblePlayer().getMediaPlayer().isPlaying()){
                                pciManager.getNetManager().updatePolicy();
                                pciManager.playSound(true);
                                GLog.printDbg("recvPushSetting", "SOUND_SET_Y :: play sound req complete.");
                            }else{
                                GLog.printDbg("recvPushSetting", "SOUND_SET_Y :: can't play sound. is already playing");
                            }
                        }
                    }).start();
                }catch (Exception e){
                    //GLog.printExceptWithSaveToPciDB(this, "SOUND_SET_Y FAILED", e, ErrorInfo.CODE_ETC_UNKNOWN, ErrorInfo.CATEGORY_INAPP);
                }


                break;

            case "SOUND_SET_N":
                try{
                    if(pciManager.getNonAudiblePlayer().getMediaPlayer().isPlaying()){
                        pciManager.getNonAudiblePlayer().stopSound();
                        GLog.printDbg("recvPushSetting", "SOUND_SET_N :: stop sound req complete.");
                    }else{
                        GLog.printDbg("recvPushSetting", "SOUND_SET_N :: can't stop sound. is already stopping or media player instance is null");
                    }
                }catch (Exception e){
                    //GLog.printExceptWithSaveToPciDB(this, "SOUND_SET_N FAILED", e, ErrorInfo.CODE_ETC_UNKNOWN, ErrorInfo.CATEGORY_INAPP);
                }
                break;

            default:
                break;
        }
    }


    private void recvUserChange(final Intent intent){
        if(Global.getInstance().getPciService() == null || !PciManager.isInitialized() && Pci_Service.pciReady){
            GLog.printWtf(this, "pciManager is not initialized. return recvUserChange");
            return;

        }
        new Thread(new Runnable() {
            @Override
            public void run() {
                String prevUserKey = PciManager.getInstance().getDataManager().getStbDb().getStbDbCommon().getUserKey();
                String newUserkey = intent.getStringExtra("userkey");

                GLog.printInfo("recvUserChange","*** recvUserChange > prevUserKey=" + prevUserKey + " / newUserkey=" + newUserkey);
                PciManager.getInstance().getNetManager().apiPci6006_UpdateUserKey(newUserkey);
                if(prevUserKey != null && prevUserKey.contains(":")){
                    PciManager.getInstance().getNetManager().apiPci6002_sendStbInfo(true);
                }
            }
        }).start();
    }

    private void recvUserDisconnect(final Intent intent){
        if(Global.getInstance().getPciService() == null || !PciManager.isInitialized() && Pci_Service.pciReady){
            GLog.printWtf(this, "pciManager is not initialized. return recvUserDisconnect");
            return;
        }

        new Thread(new Runnable() {
            @Override
            public void run() {
                PciManager pciManager = PciManager.getInstance();
                DataManager dataManager = PciManager.getInstance().getDataManager();
                String newUserkey = null;

                dataManager.getStbDb().refreshDbInfo(Global.getInstance().getPciService());
                newUserkey = dataManager.getStbDb().getStbDbCommon().getUserKey();
                GLog.printInfo("recvUserDisconnect","*** recvUserDisconnect > newUserkey=" + newUserkey);
                if(dataManager != null && dataManager.getStbDb() != null) {
                    if (newUserkey == null || newUserkey.contains(":")) {
                        newUserkey = "none";
                        GLog.printInfo(this,"recvUserDisconnect > userkey is null or is mac address. set userKey to \"none\" newUserkey=" + newUserkey);
                    }
                }

                pciManager.getNetManager().apiPci6006_UpdateUserKey(newUserkey);
                if(newUserkey.equalsIgnoreCase("none")){
                    pciManager.getNetManager().apiPci6002_sendStbInfo(true);
                }
            }
        }).start();
    }



    private void recvPushCheckInList(Intent intent){
        if(Global.getInstance().getPciService() == null || !PciManager.isInitialized() && Pci_Service.pciReady){
            GLog.printWtf(this, "pciManager is not initialized. return recvPushCheckInList");
            return;
        }
        try{
            String strEvent = intent.getStringExtra("EVENT");
            String strPidList = intent.getStringExtra("PIDList");
            GLog.printInfo(this, "RECV from PushClient Data > EVENT=" + strEvent + " / PIDList=" + strPidList);

            if(strEvent != null){
                JsonObject jsonObject = new JsonObject();
                JsonObject jsonEvent = (JsonObject) JsonParser.parse(strEvent);
                JsonArray jsonPidList = (JsonArray) JsonParser.parse(strPidList);

                jsonObject.put("EVENT", jsonEvent);
                jsonObject.put("PIDList", jsonPidList);

                PciResp_CheckIn checkInList = new PciResp_CheckIn(jsonObject, false);
                PciManager.getInstance().getDataManager().getPidManager().updateCheckInPidList(checkInList.getPidList(), "G-PUSH");

                EventPidData eventPidData = checkInList.getEventData();
                if(eventPidData != null){
                    sendPciListToMC(intent.getStringExtra("packageName"), eventPidData.getPidData(), eventPidData.getType(), eventPidData.getIsCheckIn(), "G-PUSH_WITH_EVENT");
                }else{
                    sendPciListToMC(intent.getStringExtra("packageName"), null, null, false, "G-PUSH");
                }
                checkInList.destory();
            }else{
                GLog.printExceptWithSaveToPciDB(this, "recvPushCheckInList failed. data filed is null!!", new PciRuntimeException("G-Push Check-in Failed. data filed is null!"), ErrorInfo.CODE_PCI_PLATFORM_GPUSH, ErrorInfo.CATEGORY_PCIPLATFORM);
            }

        }catch (JsonException e){
            //GLog.printExceptWithSaveToPciDB(this, "recvPushCheckInList failed.(JsonException)", e, ErrorInfo.CODE_PCI_PLATFORM_GPUSH, ErrorInfo.CATEGORY_PCIPLATFORM);
        }catch (Exception e){
            //GLog.printExceptWithSaveToPciDB(this, "recvPushCheckInList failed.", e, ErrorInfo.CODE_PCI_PLATFORM_GPUSH, ErrorInfo.CATEGORY_PCIPLATFORM);
        }
    }


    private void recvPciPolicyReq(){
        if(Global.getInstance().getPciService() == null || !PciManager.isInitialized() && Pci_Service.pciReady){
            GLog.printWtf(this, "pciManager is not initialized. return recvPciPolicyReq");
            return;
        }

        try{

            new Thread(new Runnable() {
                @Override
                public void run() {
                    Intent sendIntent = new Intent(SEND_ACTION_POLICY_RESP);
                    final DataManager dataMgr = PciManager.getInstance().getDataManager();

                    // 0=OFF(Policy) , 1=ON(Policy) , 2=Launcher disable(OFF) = CMS에서 비가청 관련 강제설정한 경우 Launcher에서 화면(메뉴) disable을 하기 위한 값
                    int result = 2;

                    if(dataMgr == null || dataMgr.getPciSrvData() == null || dataMgr.getPciSrvData().getPciSrvPolicyData() == null){
//                        GLog.printExceptWithSaveToPciDB(this, "pci policy data is null. can't response", new PciRuntimeException("wrong pci policy data."), ErrorInfo.CODE_INAPP_POLICY_IS_NULL, ErrorInfo.CATEGORY_INAPP);
                        result = 2;
                        GLog.printError(this, "pci policy data is null. response value 2(OFF) to Launcher");
                    }else{
                        PciManager.getInstance().getNetManager().apiPci6003_getPolicy();

                        String continuousPlayYn =  dataMgr.getPciSrvData().getPciSrvPolicyData().getContinuousPlayYn();
                        if(continuousPlayYn.equalsIgnoreCase("Y")) result = 1;
                        else if(continuousPlayYn.equalsIgnoreCase("N")) result = 0;
                        else if(continuousPlayYn.equalsIgnoreCase("OFF")) result = 2;
                        else{
                            GLog.printExceptWithSaveToPciDB(this, "wrong pci policy data. continuousPlayYn is = "+ continuousPlayYn, new PciRuntimeException("wrong pci policy data."), ErrorInfo.CODE_INAPP_WRONG_POLICY_DATA, ErrorInfo.CATEGORY_INAPP);
                            return;
                        }
                    }

                    sendIntent.putExtra("pci", result);
                    sendIntent.setPackage(PKG_LAUNCHER);
                    sendBroadcast(sendIntent, "sendPciListToMC");
                }
            }).start();
        }catch (Exception e){
            //GLog.printExceptWithSaveToPciDB(this, "", e, ErrorInfo.CODE_INAPP_UNKNOWN_ERR, ErrorInfo.CATEGORY_INAPP);
        }
    }


    /**
     * 비가청 재생 설정 from launcher
     * @param intent
     */
    private void recvPciSettingReq(Intent intent){
        if(Global.getInstance().getPciService() == null || !PciManager.isInitialized() && Pci_Service.pciReady){
            GLog.printWtf(this, "pciManager is not initialized. return recvPciSettingReq");
            return;
        }

        final int newSetting = intent.getIntExtra("pci", -1); // 0=OFF, 1=ON, -1 or others = Error
        new GThread("recvPciSettingReq", new GRunnable() {
            @Override
            public void run() {
                boolean isToPlay = false;

                if (newSetting == 0) {      // 0 = OFF
                    isToPlay = false;
                }else if(newSetting == 1){  // 1 = ON
                    isToPlay = true;
                }

                boolean result = PciManager.getInstance().getNetManager().apiPci6009_SetNonAudibleSndState(isToPlay);

                // pciSetting 설정 응답
                Intent sendIntent = new Intent(SEND_ACTION_PCI_SETTING_RESP);
                sendIntent.setPackage(PKG_LAUNCHER);

                if(result) sendIntent.putExtra("pci_rep", 0); // 정상
                else sendIntent.putExtra("pci_rep", 1); // 비정상

                sendBroadcast(sendIntent, "sendPciListToMC");
            }
        }).start();
    }



    /**
     * 음성 발화 체크인 정보를 화자인식 App으로 부터 수신된 경우
     * 해당 데이터를 처리하고 Pci서버에 Check in 정보를 전달한다.
     * API : VID001
     */
    private void recvVoiceId(final Intent recvIntent){
        // pciService가 비 실행중일 경우 return
        if(Global.getInstance().getPciService() == null || !PciManager.isInitialized() && Pci_Service.pciReady){
            GLog.printWtf(this, "pciManager is not initialized. return recvVoiceEvent");
            return;
        }

        new Thread(new Runnable() {
            @Override
            public void run() {
                PciManager pciManager = PciManager.getInstance();
                StbData stbData = pciManager.getDataManager().getStbData();

                String vid = recvIntent.getStringExtra("VID");
                String preVid = recvIntent.getStringExtra("PreVID"); // 이전 보이스ID(VoiceId가 UserKey로 변경된 경우에만 값이 넘어옴)
                String senderPkgName = recvIntent.getStringExtra("packageName");
                String gender = recvIntent.getStringExtra("GENDER_TYPE");
                String age_group = recvIntent.getStringExtra("AGE_GROUP");
                String checkInDate = HsUtil_Date.getTimeString(Calendar.getInstance().getTime(), pciManager.getPciProperty().getDate_format_pcisrv_checkin_list());

                // 빈값, null, unknown일 경우 처리하지 않음 (2019.02.13)
                if(vid != null && !vid.equals("") && !vid.equalsIgnoreCase("UNKNOWN") && !vid.equalsIgnoreCase("NULL") ){
                    GLog.printInfo(this,"recvVoiceId=" + vid + "/ preVid=" + preVid + " / " + senderPkgName + " / " + checkInDate);

                    String realVoiceId = "";
                    String realUserkey = "";

                    // VID가 UserKey로 변경된 경우 preVID에 이전에 사용하던 VID가 넘어옴.
                    // boolean isNeedUpdate = false;
                    // isNeedUpdate -> action_type으로 변경 (Platform 1.7.1_1130)
                    String action;

                    if(preVid != null && !preVid.trim().equals("") && !vid.startsWith("V")){
                        action = "U";
                        realVoiceId = preVid;
                        realUserkey = vid;
                    }else{
                        action = "I";
                        if(vid.startsWith("V")){
                            realVoiceId = vid;
                            realUserkey = "";
                        }else{
                            realVoiceId = "";
                            realUserkey = vid;
                        }
                    }

                    // 수신된 데이터를 통해 PciSrv_VoiceChkInData Object를 생성하고, 현재 체크인된 보이스 사용자(LastVoiceChkInData)로 지정
                    // Voice체크인 데이타에 있는 gender, age 정보는 불확실하므로 서버에서 받은 정보를 우선시 함
                    PciSrv_VoiceChkInData newCheckInData = new PciSrv_VoiceChkInData(realVoiceId, realUserkey, checkInDate, action, gender, age_group);
                    stbData.setLastVoiceChkInData(newCheckInData);
                    PidListManager pidListMgr = pciManager.getDataManager().getPidManager();
                    PciSrv_PidData curPidData = null;

                    if(realVoiceId != null && !realVoiceId.equals("")) curPidData = pidListMgr.getPidDataByVoiceID(realVoiceId);
                    else if(realUserkey != null && !realUserkey.equals("")) curPidData = pidListMgr.getPidDataByUserKey(realUserkey);

                    // PidList에 PidData가 없을 경우 서버통신 우선, PidData가 있을 경우 MC전송 우선 (PidList에 존재하지 않을 경우 해당 voiceID의 Pid를 알수 없음)
                    if(curPidData != null){
                        curPidData.setVoiceChkInData(newCheckInData);

                        // MC에 현재 체크인이벤트(보이스) + 체크인리스트 전송
                        recvVoiceCheckInSendToMC(curPidData, checkInDate);

                        // 서버에 보이스 체크인 이벤트 전송
                        pciManager.getNetManager().apiPci6005_VoiceAction(newCheckInData);

                    }else{
                        // 서버에 보이스 체크인 이벤트 전송
                        curPidData = pciManager.getNetManager().apiPci6005_VoiceAction(newCheckInData);

                        if(curPidData == null){
                            GLog.printExceptWithSaveToPciDB(this, "voiceId & UserKey is not found.", new PciRuntimeException("receive VoiceId Exception. recvData=vid:" + vid + "/preVid:" + preVid + "/ realVoiceId:" + realVoiceId + "/ realUserkey:"+realUserkey), ErrorInfo.CODE_INAPP_VOICE_EVENT_FAIL, ErrorInfo.CATEGORY_INAPP);
                            return;
                        }
                        // MC에 현재 체크인이벤트(보이스) + 체크인리스트 전송
                        recvVoiceCheckInSendToMC(curPidData, checkInDate);
                    }
                }
            }
        }).start();
    }


    private void recvVoiceCheckInSendToMC(PciSrv_PidData curPidData, String checkInDate){
        if(curPidData != null && curPidData.getCheckInData_v() != null){
            curPidData.getCheckInData_v().setCheckInDateStr(checkInDate);
            sendPciListToMC(null, curPidData, "V", true, "VOICE_CHECK_IN");
        }else{
            if(curPidData == null)
                GLog.printWarn(this, "can't send voice event to MC. curPidData is null. >> " + (curPidData == null));
            else GLog.printWarn(this, "can't send voice event to MC. curPidData is not null but curPidData.checkInData_v is null. >> " + (curPidData == null) + " / " + (curPidData.getCheckInData_v() == null));
        }
    }



    /**
     * MC 상태 조회
     * API : CO001
     */
    private void sendMcStateCheck(){
        Intent sendIntent = new Intent(SEND_ACTION_MCSTATE_CHECK);
        sendIntent.setPackage(PKG_MC);
        sendIntent.putExtra("activity", "com.kt.gigagenie.pci");
        sendBroadcast(sendIntent, "sendMcStateCheck");
    }



    /**
     * MC로 부터 PCI List를 요청 받았을때 응답
     */
    public void recvPciListReq(Intent intent){
        if(Global.getInstance().getPciService() == null || !PciManager.isInitialized() && Pci_Service.pciReady){
            GLog.printWtf(this, "pciManager is not initialized. return recvPciListReq");
            return;
        }

        final String reqPkgName = intent.getStringExtra("packageName");
        PciManager.getInstance().getNetManager().apiPci6008_reqCheckInList(true, reqPkgName, null);
    }
    /**
     * OTV로 부터 ADID 수신 by dalkommjk - v16.00.02 | 2022-03-23
     */
    public void recvAdidResp(Intent intent){
        if(Global.getInstance().getPciService() == null || !PciManager.isInitialized() && Pci_Service.pciReady){
            GLog.printWtf(this, "pciManager is not initialized. return recvAdidResp");
            return;
        }

        try {
            String adid = intent.getStringExtra("adid");
            StbDb_Common stbDb_common = new StbDb_Common(adid);

            //PciManager.getInstance().getNetManager().apiPci6008_reqCheckInList(true, reqPkgName, null);
        }catch (Exception e){

        }

    }


    /**
     * 특정 이벤트 등이 발생했을 때 MC에게 PCI List 전달
     * @param reqAppPkgName PCI 체크인 리스트를 요청하는 App의 PkgName (MC에게 전달시 PKG_MC or null)
     * @param curCheckedInData 체크인 이벤트가 있을 경우 해당 체크인의 pidData(이벤트가 없을 경우 null)
     * @param eventType eventType P or V or W or B or null
     * @param isCheckIn true : Check-In 이벤트, false : Check-Out 이벤트
     */
    public void sendPciListToMC(String reqAppPkgName, PciSrv_PidData curCheckedInData, String eventType, boolean isCheckIn, String caller){
        Intent sendIntent = new Intent(SEND_ACTION_PCILIST_RESP);
        sendIntent.setPackage(PKG_MC);

        GLog.printInfo(this, "sendPciListToMC. caller="+caller);
        sendIntent.putExtra("PIDList", PciManager.getInstance().getDataManager().getPidManager().getPidList_JObj(curCheckedInData, eventType, isCheckIn).toJsonString());

        if(reqAppPkgName != null) sendIntent.putExtra("packageName", reqAppPkgName); // PCI 리스트 요청한 App의 PkgName
        else sendIntent.putExtra("packageName", PKG_MC);
        sendBroadcast(sendIntent, "sendPciListToMC");
    }



    /**
     * MC로 부터 KEEP-Alive Check 요청 수신시, MC에게 App State 응답
     * API : CO004
     */
    private void sendKeepAliveResp(){
        Intent sendIntent = new Intent(SEND_ACTION_KEEPALIVE_RESP);
        sendIntent.setPackage(PKG_MC);
        String currentState = (Global.isInitialized())? Global.getInstance().getState() : Global.STATE_DEAD;

        sendIntent.putExtra("activity", PKG_PCI);
        sendIntent.putExtra("state", currentState);

        sendBroadcast(sendIntent, "sendKeepAliveResp");
    }


    public void printIntentExtras(Intent intent){
        Bundle bundle = intent.getExtras();
        if (bundle != null) {
            for (String key : bundle.keySet()) {
                Object value = bundle.get(key);
                GLog.printWarn(this, " └[" + key + "]=" + value.toString()+" (" + value.getClass().getName() + ")");
            }
        }
    }

    public void sendBroadcast(Intent intent, String tag){
        if(intent != null){
            GLog.printInfo(this, "Send Broadcast : " + tag + " = " + intent.toString());
            printIntentExtras(intent);
        }
        else GLog.printWarn(this, "Send Broadcast : " + tag + " = " + "intent is null");

        if(service != null) service.sendBroadcast(intent);
        else GLog.printWtf(this, "Service INSTANCE is null!! can't send broadcast");
    }
    public IntentFilter getIntentFilter_Recv() { return intentFilter_Recv; }
    public void setIntentFilter_Recv(IntentFilter _intentFilter ) { intentFilter_Recv = _intentFilter; }
    public void destroy(){
        service = null;
        intentFilter_Recv = null;
    }

    public String toString(){
        return logHeader;
    }


}
