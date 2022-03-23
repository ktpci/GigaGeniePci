package com.kt.gigagenie.pci;

import android.app.AlarmManager;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;

import android.content.Context;
import android.content.Intent;

import android.content.SharedPreferences;
import android.media.MediaPlayer;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.IBinder;
import android.os.SystemClock;
//import android.support.annotation.Nullable;
//import android.support.v4.app.NotificationCompat;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.NotificationCompat;

import android.util.Log;


import com.gnifrix.debug.GLog;
import com.gnifrix.system.GRunnable;
import com.gnifrix.system.GThread;
import com.gnifrix.util.timechecker.CheckInfo;
import com.gnifrix.util.timechecker.TimeChecker;
import com.kt.gigagenie.pci.broadcast.BroadcastContext;
import com.kt.gigagenie.pci.broadcast.SystemBroadcastReceiver;
import com.kt.gigagenie.pci.data.pci_db.ErrorInfo;
import com.kt.gigagenie.pci.data.pci_db.PciDB;

import com.kt.gigagenie.pci.system.PciProperty;
import com.ktpci.beacon.BeaconManager;
import com.ktpns.lib.KPNSApis;
import com.ktpns.lib.OnKPNSInitializeEventListener;

import static com.kt.gigagenie.pci.PciManager.NetworkCheck;


public class Pci_Service extends Service implements BroadcastContext {
    private static final String LogHeader = "Pci_Service";
    public static boolean pciReady = false;  // pci Service의 초기화가(네트워크 및 기타 모듈등 모든 초기화 완료 상태)모두 완료 되었는지

    public static PciManager pciManager;
    public static PciProperty pciProperty;
    private static Thread pciThread;
    private static Context sContext;
    private static int kpns_retry_ctn = 0;

    MediaPlayer mp;

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }


    private void startTimeCheckIni(TimeChecker timeChecker){
        Global.getInstance().setTimeChecker(timeChecker);
        timeChecker.startTimeCheck(new CheckInfo(TimeCheckContext.TIME_CHECK_PCI_SERVICE_INIT));
        timeChecker.startTimeCheck(new CheckInfo(TimeCheckContext.TIME_CHECK_PCI_MODULE_INIT));
    }


    @Override
    public void onCreate(){
        super.onCreate();
        sContext = getApplicationContext();
        GLog.printWarn(this,":::: Start Background Service PCI ::::");


        Global global = null;
        TimeChecker timeChecker = new TimeChecker();
        if(!Global.isInitialized()){
            global = Global.getInstance();
            startTimeCheckIni(timeChecker);

            global.setPciDB(new PciDB(this));
            pciProperty = new PciProperty(this);
            global.setPciProperty(pciProperty);
        }else{
            global = Global.getInstance();
            startTimeCheckIni(timeChecker);

            if((pciProperty = global.getPciProperty()) == null) {
                pciProperty = new PciProperty(this);
                global.setPciProperty(pciProperty);
            }
        }

        global.setPciService(this);

        /** ↓↓↓↓↓ server 변경모드 추가 - by dalkommJK | 2019-11-28 ↓↓↓↓↓ **/
        SharedPreferences pref = getSharedPreferences("pref", MODE_PRIVATE);
        if(pref.getString("mode", "").equals("bmt")) {
            pciProperty.setApiEnvToBmt();
        }else if(pref.getString("mode", "").equals("live")){
            pciProperty.setApiEnvToLive();
        }
        /** ↑↑↑↑↑ server 변경모드 추가 - by dalkommJK | 2019-11-28 ↑↑↑↑↑ **/

        if(!pciProperty.getUseDebugMode() || Global.getInstance().getMainActivity() == null){
            pciProperty.printInitString();
        }

        final TimeChecker timeCheckerT = timeChecker;
        pciManager = PciManager.getInstance();
        pciThread = new GThread("PciInitThread", new GRunnable() {
            @Override
            public void run() {
                try{
                    if(!pciManager.startPci(pciProperty, Pci_Service.this)){
                        pciManager.destory();
                        try{
                            Thread.sleep(10*1000);
                        }catch (InterruptedException e){
                            e.printStackTrace();
                        }finally{
                            pciReady = false;
                            pciThread.interrupt();
                            stopService(new Intent(getApplicationContext(), Pci_Service.class));
                            timeCheckerT.finishTimeCheck(TimeCheckContext.TIME_CHECK_PCI_SERVICE_INIT);
                            timeCheckerT.printTimeCheckList();
                        }
                    }else{
                        pciReady = true;
                        timeCheckerT.finishTimeCheck(TimeCheckContext.TIME_CHECK_PCI_SERVICE_INIT);
                        timeCheckerT.printTimeCheckList();
                    }
                }catch (Exception e){
                    //GLog.printExceptWithSaveToPciDB(this, "Unhandled Exception !! ", e, ErrorInfo.CODE_ETC_UNKNOWN, ErrorInfo.CATEGORY_ETC);
                    stopService(new Intent(getApplicationContext(), Pci_Service.class));
                }
            }
        });

        registerRestartAlarm(false);
        startService();
    }

    public static void startService(){
        if(!pciThread.isAlive()){
            Global.getInstance().setState(Global.STATE_ALIVE);
            pciThread.start();
        }
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId){
        GLog.printWarn(this,":::: onStartCommand Pci Service ::::");

        // Pci Service를 Foreground Service로 등록(Foreground Service로 등록 시 Task killer 등에 의해 잘 죽지 않음)
        NotificationManager nm = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        Notification notification = new NotificationCompat.Builder(getApplicationContext()).setContentTitle("Pci-Service").setContentText("Running").build();
        notification.icon = R.drawable.icon;
        nm.notify(startId, notification);
        nm.cancel(startId);

        startForeground(1, notification);
        GLog.printWarn(this,"Pci Service set to foreground service. foreground id=1, startId=" + startId + ", notification=" + notification.toString());
        return START_STICKY; // START_STICKY = app이 task Killer에 의해 죽었을 경우 메모리 여유가 있으면 다시 되살아남
    }

    @Override
    public void onDestroy(){
        registerRestartAlarm(true);
        super.onDestroy();

        pciReady = false;
        pciThread.interrupt();
        pciThread = null;

        PciManager.getInstance().destory();

        GLog.printWarn(this,":::: Finish Background Service PCI ::::");
        if(!pciProperty.getUseDebugMode()){
            Global.getInstance().destroy();
            Global.getInstance().setState(Global.STATE_DEAD);
        }

    }


    public void finishPciService(){
        stopService(new Intent(getApplicationContext(), Pci_Service.class));
    }

    /**
     * App이 죽었을 경우 되살리기 위한 Alarm 설정
     * 서비스 종료 후 1초 뒤 실행 되며, 매 10초 마다 반복 체크
     * @param isOn
     */
    public void registerRestartAlarm(boolean isOn){
        if(pciProperty == null || !pciProperty.getPci_service_keep_alive()) return;

        Intent intent = new Intent(Pci_Service.this, SystemBroadcastReceiver.class);
        intent.setAction(BroadcastContext.RECV_SYS_ACTION_RESTART_SERVICE);
        PendingIntent sender = PendingIntent.getBroadcast(getApplicationContext(), 0, intent, 0);
        AlarmManager am = (AlarmManager)getSystemService(ALARM_SERVICE);
        if(isOn){
            am.setRepeating(AlarmManager.ELAPSED_REALTIME_WAKEUP, SystemClock.elapsedRealtime() + 1000, 10000, sender);
            GLog.printWarn(this, "register Restart Alarm :: " + isOn);
        }else{
            am.cancel(sender);
            GLog.printWarn(this, "register Restart Alarm :: " + isOn);
        }
    }

    public String toString(){
        return LogHeader;
    }

    /** ↓↓↓↓↓ KPNS 추가 - by dalkommJK | 2019-09-03 ↓↓↓↓↓ **/

    public static void KPNSInit(){

        KPNSApis.requestInstance(sContext, new OnKPNSInitializeEventListener() {
            @Override
            public void onSuccessInitialize(KPNSApis kpnsApis) { // "KPNS API 초기화 성공"
                GLog.printDbg(this, "<<< KPNS Key Init Success >>>");
                KPNSJoin();
                kpns_retry_ctn = 0;
            }
            @Override
            public void onFailInitialize() { // "
                // API 초기화 실패"
                kpns_retry_ctn++;
                if(kpns_retry_ctn < 4) {
                    GLog.printDbg(this, "<<< KPNS Key Init Fail >>>");
                    KPNSInit();
                }
            } });

    }

    public static void KPNSJoin(){
        KPNSApis.requestInstance(sContext, new OnKPNSInitializeEventListener() {
            @Override
            public void onSuccessInitialize(KPNSApis kpnsApis) {
                kpnsApis.register("0WZ8G106d0", "GiGAGeniePci");
                GLog.printDbg(this, "<<< KPNS Key Registration >>>");

            }
            @Override
            public void onFailInitialize() {
                GLog.printDbg(this, "<<< KPNS Key Join Fail >>>");
                KPNSInit();
            }
        });
    }
    /** ↑↑↑↑↑ KPNS 추가 - by dalkommJK | 2019-09-03 ↑↑↑↑↑ **/

    /** ↓↓↓↓↓ KPNS 추가 - by dalkommJK | 2019-11-21 ↓↓↓↓↓ **/
    public static void CheckInPushMsg(String checkinlist, String Event) {
        Intent intents = new Intent(RECV_ACTION_PUSH_CHECKIN_LIST);
        intents.putExtra("PIDList", checkinlist);
        intents.putExtra("EVENT", Event);
        intents.setPackage(PKG_PCI);
        sContext.sendBroadcast(intents);
    }
    public static void AlarmPushMsg(Intent intent) {  // 팝업메시지 노출
        String strMsg="";
        try {
            byte[] message = intent.getByteArrayExtra("message");
            strMsg = new String(message, "UTF-8");
            Intent intents = new Intent(RECV_ACTION_PUSH_ALARM);
            String[] pushDatas = strMsg.split("&");
            if(pushDatas != null) {
                for (String pushData : pushDatas) {
                    String[] strings = pushData.split("=");
                    if (strings != null) {
                        String key = strings[0];
                        String value = strings[1];
                        key = key.replace("data.","");
                        intents.putExtra(key, value);
                    }
                }
            }
            intents.setPackage(PKG_PCI);
            sContext.sendBroadcast(intents);
        }catch (Exception e) {}
    }
    public static void SettingPushMsg(Intent intent) {  // 재부팅 푸쉬  command_id : APP_REBOOT
        String strMsg="";
        try {
            byte[] message = intent.getByteArrayExtra("message");
            strMsg = new String(message, "UTF-8");
            Intent intents = new Intent(RECV_ACTION_PUSH_SETTING);
            String[] pushDatas = strMsg.split("&");
            if(pushDatas != null) {
                for (String pushData : pushDatas) {
                    String[] strings = pushData.split("=");
                    if (strings != null) {
                        String key = strings[0];
                        String value = strings[1];
                        key = key.replace("data.","");
                        intents.putExtra(key, value);
                    }
                }
            }
            intents.setPackage(PKG_PCI);
            sContext.sendBroadcast(intents);

        }catch (Exception e) {

        }
    }
    /** ↑↑↑↑↑ KPNS 추가 - by dalkommJK | 2019-11-21 ↑↑↑↑↑ **/
    public void BeaconScan(){
        BeaconManager beaconManager;
        beaconManager = BeaconManager.getInstanceForApplication(this);
    }





}



