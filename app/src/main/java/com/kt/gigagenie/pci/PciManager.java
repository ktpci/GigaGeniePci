package com.kt.gigagenie.pci;

import android.content.BroadcastReceiver;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;

import com.gnifrix.debug.GLog;
import com.gnifrix.system.AppContext;
import com.gnifrix.system.GRunnable;
import com.gnifrix.system.GThread;
import com.gnifrix.util.HsUtil_Date;
import com.kt.gigagenie.pci.broadcast.BroadcastContext;
import com.kt.gigagenie.pci.broadcast.PciBroadcastReceiver;
import com.kt.gigagenie.pci.data.AD_DataManager;
import com.kt.gigagenie.pci.data.AD_PciSrv_PolicyData;
import com.kt.gigagenie.pci.data.CheckInData;
import com.kt.gigagenie.pci.data.DataManager;
import com.kt.gigagenie.pci.data.PciSrv_PidData;
import com.kt.gigagenie.pci.data.PciSrv_PolicyData;
import com.kt.gigagenie.pci.data.pci_db.PciDB;
import com.kt.gigagenie.pci.data.stb_db.StbDb;
import com.kt.gigagenie.pci.net.AD_NetManager;
import com.kt.gigagenie.pci.net.NetException;
import com.kt.gigagenie.pci.net.NetManager;
import com.kt.gigagenie.pci.snd.NonAudiblePlayer;
import com.kt.gigagenie.pci.system.PciProperty;
import com.kt.gigagenie.pci.system.PciRuntimeException;
import com.ktpci.beacon.BeaconManager;


import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;

import static com.kt.gigagenie.pci.Pci_Service.KPNSInit;

/**
 * Created by LeeBaeng on 2018-09-04.
 */
public class PciManager implements AppContext {
    private static PciManager INSTANCE = null;
    private static final String logHeader = "PciManager";

    private PciBroadcastReceiver pciBroadcastReceiver;
    private PushReceiver pushreceiver;
    private PciProperty pciProperty = null;
    private DataManager dataManager;
    private AD_DataManager addataManager; // AD PCI by dalkommjk -v16.00.02 | 2022.02.22
    private  NetManager netManager;
    private AD_NetManager adnetManager; // AD PCI by dalkommjk -v16.00.02 | 2022.02.22
    private Pci_Service mainService;

    private NonAudiblePlayer nonAudiblePlayer;
    private GThread stbMonitoringThread;
    private GThread idleTimeUpdateThread;
    private GThread checkOutThread;
    private GThread keepAliveTestThread;
    private GThread standbyUpdateThread;
    private GThread kpnsUpdateThread;   // MR 11차 반영 by dalkommjk | 2020-03-17
    private GThread timetickyUpdateThread;  // MR 11차 반영 by dalkommjk | 2020-03-17
    public static GThread NetworkCheckThread;

    private Date wifiTestCheckoutTime = null;
    private Date voiceTestCheckoutTime = null;

    private Date _prevCheckT;
    private String idleDateFormat = "HHmmss";
    BroadcastReceiver mReceiver;

    public PciManager(){}

    public static boolean isInitialized(){
        return (INSTANCE != null);
    }



    public boolean startPci(PciProperty _pciProperty, Pci_Service _mainService){

        GLog.printDbg(this, "Start Initializing Pci Manager...");
        pciProperty = _pciProperty;
        mainService = _mainService;

        // Initialize Data
        dataManager = new DataManager();
        addataManager = new AD_DataManager(); // ADPCI by dalkommjk - v16.00.02 | 2022-02-22

        // Initialize Net
        try{
            netManager = new NetManager(this);
        }catch (Exception ex){
            //GLog.printExceptWithSaveToPciDB("START_PCI", "netManager initialize failed. ", ex, ErrorInfo.CODE_PCI_SERVICE_INITIALIZE_FAIL, ErrorInfo.CATEGORY_INAPP);
            return false;
        }

        // Initialize Broadcast Receiver
        initBroadcastReceiver();
        initKPNSReceiver(); /** ATV 버전에 따른 추가 by dalkommjk | 2021-09-04 */
        setNonAudiblePlayer(new NonAudiblePlayer(this, _mainService));

        try {
            if (pciProperty.getPci_splitdownload_use()) {
                int minTime = pciProperty.getPci_splitdownload_rnd_min();
                int maxTime = pciProperty.getPci_splitdownload_rnd_max();
                int waitTime = (int) ((Math.random() * (maxTime - minTime)) + minTime) * 1000;
                GLog.printDbg(this, "start timer: " + waitTime + " msec");
                if(waitTime > 0) Thread.sleep(waitTime);
                //if(waitTime > 0) Thread.sleep(1000);
                GLog.printDbg(this, "timer finished");
                NetworkCheck();  // 이더넷 연결이 아닌 실제 인터넷 정상 체크 by dalkommjk | 2021-01-28
            }
        } catch (InterruptedException e) {
            //GLog.printExceptWithSaveToPciDB(this, "Waiting for Network Split interrupted on startPci", e, ErrorInfo.CODE_PCI_SERVICE_INITIALIZE_FAIL, ErrorInfo.CATEGORY_INAPP);
            return false;
        }
/////////////////////////////////// API 호출 /////////////////////////////////
        try{
            netManager.apiPci6001_getPciKey();
        } catch (NetException e) {
           // GLog.printExceptWithSaveToPciDB(this,"request PCI key fail on startPci", e, ErrorInfo.CODE_PCI_PLATFORM_NETWORK_ERR, ErrorInfo.CATEGORY_PCIPLATFORM);
            return false;
        } catch (Exception e) {
           // GLog.printExceptWithSaveToPciDB(this, "request PCI key failed. unhandled except on startPci : " + e.toString(), e, ErrorInfo.CODE_PCI_SERVICE_INITIALIZE_FAIL, ErrorInfo.CATEGORY_ETC);
            return false;
        }

        netManager.apiPci6002_sendStbInfo(false);
        netManager.updatePolicy();
        netManager.apiPci6008_reqCheckInList(true, BroadcastContext.PKG_MC, null);

        netManager.apiPci6006_UpdateUserKey(null);
        /** ↓↓↓↓↓ KPNS추가 - by dalkommJK | 2019-09-30 ↓↓↓↓↓↓ */
        KPNSReq(true);
        // TimeTic(); /**  KPNS 추가 - by dalkommJK | 2019-09-03  **/

        /** ↑↑↑↑↑ KPNS추가 - by dalkommJK | 2019-09-30  ↑↑↑↑↑ */
        //playSound(false);               /**  삭제 -by dalkommJK | 2019-04-19  */
        GLog.printDbg(this, "Pci Manager initialized....");

        startStbInfoMonitoringThread();
        startUpdateCheckLoop();
        startCheckOutThread();
        startKeepAliveTestThread();

        if(pciProperty.getPci_checkin_list_wifi_out_time() > 0){
            wifiTestCheckoutTime = new Date(Calendar.getInstance().getTimeInMillis() + (pciProperty.getPci_checkin_list_wifi_out_time() * 1000));
            GLog.printInfo(this, "WIFI Checkout Test > checkout Time = " + HsUtil_Date.getTimeString(wifiTestCheckoutTime));
        }
        if(pciProperty.getPci_checkin_list_voice_out_time() > 0){
            voiceTestCheckoutTime = new Date(Calendar.getInstance().getTimeInMillis() + (pciProperty.getPci_checkin_list_voice_out_time() * 1000));
            GLog.printInfo(this, "VOICE Checkout Test > checkout Time = " + HsUtil_Date.getTimeString(voiceTestCheckoutTime));
        }

        return true;
    }


    public void initBroadcastReceiver(){
        pciBroadcastReceiver = new PciBroadcastReceiver(mainService);
        mainService.registerReceiver(pciBroadcastReceiver, pciBroadcastReceiver.getIntentFilter_Recv());
    }
    /** ↓↓↓↓↓ KPNS 동적 리시버 추가 - by dalkommJK | 2021-09-04 ↓↓↓↓↓↓ */
    public void initKPNSReceiver(){
        try {

            pushreceiver = new PushReceiver();
            mainService.registerReceiver(pushreceiver, PushReceiver.getIntentFilter_Recv());
            GLog.printInfo(this,"initKPNSReceiver - Success");
        }catch (Exception e)
        {
            GLog.printInfo(this,"initKPNSReceiver - Fail");
        }
    }
    /** ----------------------------------*/


    public void playSound(boolean immediately){
        PciSrv_PolicyData _getPolicy = dataManager.getPciSrvData().getPciSrvPolicyData();

        if (_getPolicy != null) {
            if (_getPolicy.getContinuousPlayYn().equals("Y")) {
                nonAudiblePlayer.playSound(immediately ? 0 : _getPolicy.getWaitingTimeForUpdate());
            } else {
                GLog.printInfo(this,"can't play sound : continuous_play_yn is not \"Y\" // continuous_play_yn="+_getPolicy.getContinuousPlayYn());
            }
        } else {
            if(pciProperty.getAppEnv().equals("none")){
                GLog.printError(this, "play sound fail: policy is null, AppEnvironment is none");
                return;
            }
            //GLog.printExceptWithSaveToPciDB(this, "play sound fail: policy is null", new PciRuntimeException("can't play sound. policy is null"), ErrorInfo.CODE_NONAUDIBLE_UNKNOWN_ERR, ErrorInfo.CATEGORY_NONAUDIBLE);
        }
    }



    private void startKeepAliveTestThread(){
        if(pciProperty.getPci_service_restart_test()){
            keepAliveTestThread = new GThread("KeepAliveTester", new GRunnable() {
                @Override
                public void run() {
                    try{
                        Thread.sleep(30000); // 10초뒤 App 종료할 Exception 강제 발생
                        throw new PciRuntimeException("Keep-Alive Test");
                    }catch (InterruptedException e){
                        GLog.printExcept(this, "", e);
                    }catch (Exception e){
                        GLog.printExcept(this, "Unhandled Exception !! ", e);
                        destory();

                        try{
                            Thread.sleep(100);
                        }catch (InterruptedException ex){
                            ex.printStackTrace();
                        }

                        Pci_Service pci_service = Global.getInstance().getPciService();
                        if(pci_service != null) pci_service.finishPciService();
                    }
                }
            });

            GLog.printWtf(this, " Started Keep Alive Test Thread !");
            keepAliveTestThread.start();
        }
    }

    /**
     * 보이스, Wifi 체크아웃을 체크
     */
    public void startCheckOutThread(){
        checkOutThread = new GThread("CheckOutThread", new GRunnable() {
            @Override
            public void run() {
                try{
                    boolean processedWifiCheckOut = false;  // 동일 시간대 여러번 반복되는것을 막기 위해 검사

                    while (checkOutThread != null){

                        Thread.sleep(60000); // 1분에 1번씩 체크

                        try{
                            if(dataManager == null || dataManager.getPidManager() == null || dataManager.getPidManager().getCheckedInList() == null){
                                GLog.printError(this, "cannot scan checkout list. >> dataManager has problem.");
                                continue;
                            }

                            HashMap<String, PciSrv_PidData> pidList = dataManager.getPidManager().getCheckedInList();
                            ArrayList<PciSrv_PidData> checkOutListV = new ArrayList<>();
                            ArrayList<PciSrv_PidData> checkOutListW = new ArrayList<>();
                            ArrayList<PciSrv_PidData> checkOutListB = new ArrayList<>(); // ble비콘 체크아웃 추가 by dalkommjk | 2022-01-19 | v16.00.01
                            boolean processCheckOut = false; //

                            PciSrv_PidData tempPidData = null;
                            for( String key : pidList.keySet() ){
                                tempPidData = pidList.get(key);
                                if(tempPidData != null){
                                    Date currentTime = HsUtil_Date.getCurrentTime(); // 현재시간
                                    Date checkOutTime = null;

                                    // ========= VOICE 체크아웃 확인
                                    if(tempPidData.getCheckInData_v() != null && tempPidData.getCheckInData_v().getCheckInDate() != null){
                                        if(pciProperty.getPci_checkin_list_voice_out_time() <= 0){

                                            // 체크아웃 시간(체크인된 시간 + 정책의 보이스 체크인 수명(CheckIn-Term), CheckIn-Term은 분단위 이므로 * 60000
                                            // 정책을 가져오는 것을 실패했을 경우 default 5분 voice 체크아웃 by dalkommjk | 2021-01-28
                                            try {
                                                checkOutTime = new Date(tempPidData.getCheckInData_v().getCheckInDate().getTime() +
                                                        (dataManager.getPciSrvData().getPciSrvPolicyData().getVoiceCheckOutTerm_Minute() * 60000));
                                            }catch (Exception e){
                                                checkOutTime = new Date(tempPidData.getCheckInData_v().getCheckInDate().getTime() + (5 * 60000));
                                            }

                                            if(currentTime.compareTo(checkOutTime) != -1){ // 0=같은시간, 1=currentTime이 checkOutTime보다 이후 시간, -1=CurrentTime이 CheckOutTime보다 이전 시간
                                                String line = "Voice Check Out :: currentTime=" + HsUtil_Date.getTimeString(currentTime) + " / checkOutTime=" + HsUtil_Date.getTimeString(checkOutTime);
                                                StringBuffer bufLog = new StringBuffer(line);
                                                if(tempPidData.getCheckInData_v() != null && tempPidData.getCheckInData_v().getCheckInDate() != null) {
                                                    line = " / voiceCheckInTime=" + HsUtil_Date.getTimeString(tempPidData.getCheckInData_v().getCheckInDate());
                                                    bufLog.append(line);
                                                }else
                                                    bufLog.append(" / voiceCheckInTime is null");
                                                GLog.printDbg("CheckOutThread", bufLog.toString());
                                                checkOutListV.add(tempPidData);
                                            }

                                        }else{
                                            // 테스트 값이 0보다 크면 테스트시간으로 진행
                                            if(voiceTestCheckoutTime != null){
                                                checkOutTime = voiceTestCheckoutTime;
                                                if(currentTime.compareTo(checkOutTime) != -1){
                                                    String line = "Voice Check Out(TEST) :: currentTime=" + HsUtil_Date.getTimeString(currentTime) + " / checkOutTime=" + HsUtil_Date.getTimeString(checkOutTime);
                                                    StringBuffer bufLog = new StringBuffer(line);
                                                    if(tempPidData.getCheckInData_v() != null && tempPidData.getCheckInData_v().getCheckInDate() != null) {
                                                        line = " / voiceCheckInTime=" + HsUtil_Date.getTimeString(tempPidData.getCheckInData_v().getCheckInDate());
                                                        bufLog.append(line);
                                                    }else
                                                        bufLog.append(" / voiceCheckInTime is null");
                                                    GLog.printDbg("CheckOutThread", bufLog.toString());
                                                    checkOutListV.add(tempPidData);
                                                }
                                            }
                                        }
                                    }
                                    // ========= WIFI 체크아웃 확인
                                    if(tempPidData.getCheckInData_w() != null && tempPidData.getCheckInData_w().getCheckInDate() != null){
                                        if(pciProperty.getPci_checkin_list_wifi_out_time() <= 0){
                                            // 현재 시간이 2시일 경우에만
                                            if(Calendar.getInstance().get(Calendar.HOUR_OF_DAY) == pciProperty.getPci_checkin_list_wifi_out_hour()){
                                                if(!processedWifiCheckOut){ // 2시 중 1회만 수행
                                                    // 체크아웃 시간(86400000ms = 24시간)
                                                    checkOutTime = new Date(tempPidData.getCheckInData_w().getCheckInDate().getTime() + 86400000);
                                                    if(currentTime.compareTo(checkOutTime) != -1){
                                                        String line = "WIFI Check Out :: currentTime=" + HsUtil_Date.getTimeString(currentTime) + " / checkOutTime=" + HsUtil_Date.getTimeString(checkOutTime);
                                                        StringBuffer bufLog = new StringBuffer(line);
                                                        if(tempPidData.getCheckInData_w() != null && tempPidData.getCheckInData_w().getCheckInDate() != null){
                                                            line = " / wifiCheckInTime=" + HsUtil_Date.getTimeString(tempPidData.getCheckInData_w().getCheckInDate());
                                                            bufLog.append(line);
                                                        }else
                                                            bufLog.append(" / wifiCheckInTime is null");
                                                        GLog.printDbg("CheckOutThread", bufLog.toString());
                                                        checkOutListW.add(tempPidData);
                                                    }
                                                    processedWifiCheckOut = true;
                                                }
                                            }else{
                                                processedWifiCheckOut = false; // 2시가 아닐 경우 processed = false
                                            }
                                        }else{
                                            // 테스트 값이 0보다 크면 테스트시간으로 진행
                                            if(wifiTestCheckoutTime != null){
                                                checkOutTime = wifiTestCheckoutTime;
                                                if(currentTime.compareTo(checkOutTime) != -1){
                                                    String line = "WIFI Check Out(TEST) :: currentTime=" + HsUtil_Date.getTimeString(currentTime) + " / checkOutTime=" + HsUtil_Date.getTimeString(checkOutTime);
                                                    StringBuffer bufLog = new StringBuffer(line);
                                                    if(tempPidData.getCheckInData_w() != null && tempPidData.getCheckInData_w().getCheckInDate() != null){
                                                        line = " / wifiCheckInTime=" + HsUtil_Date.getTimeString(tempPidData.getCheckInData_w().getCheckInDate());
                                                        bufLog.append(line);

                                                    }else bufLog.append(" / wifiCheckInTime is null");
                                                    GLog.printDbg("CheckOutThread", bufLog.toString());
                                                    checkOutListW.add(tempPidData);
                                                }
                                            }
                                        }
                                    }
                                    // ========= ble 체크아웃 확인






                                }
                            }

                            if(checkOutListV != null && checkOutListV.size() > 0){
                                GLog.printInfo(this,"====================== CheckOut(VOICE) >> " + checkOutListV.size());
                                for(PciSrv_PidData pidDataCheckout : checkOutListV){
                                    GLog.printInfo(this,"try CheckOut(VOICE) >> " + pidDataCheckout.getPid());
                                }

                                for(PciSrv_PidData pidDataCheckout : checkOutListV){
                                    processCheckOut = true;
                                    voiceCheckOut(pidDataCheckout);
                                }

                                checkOutListV.clear();
                            }

                            if(checkOutListW != null && checkOutListW.size() > 0){
                                GLog.printInfo(this,"====================== CheckOut(WIFI) >> " + checkOutListW.size());
                                for(PciSrv_PidData pidDataCheckout : checkOutListW){
                                    GLog.printInfo(this,"try CheckOut(WIFI) >> " + pidDataCheckout.getPid());
                                }

                                for(PciSrv_PidData pidDataCheckout : checkOutListW){
                                    processCheckOut = true;
                                    wifiCheckOut(pidDataCheckout);
                                }

                                checkOutListW.clear();
                            }

                            if(processCheckOut){
                                dataManager.getPidManager().printCheckInList(this.toString());
                            }
                        }catch (Exception e){
                            //GLog.printExceptWithSaveToPciDB(this, "Unhandled Exception(in while) !! ", e, ErrorInfo.CODE_ETC_UNKNOWN, ErrorInfo.CATEGORY_ETC);
                            Pci_Service pci_service = Global.getInstance().getPciService(); // 오류발생시 pci_service 재시작 by dalkommjk | 2021-01-28
                            if(pci_service != null) pci_service.finishPciService(); // 오류발생시 pci_service 재시작 by dalkommjk | 2021-01-28
                        }
                    }
                }catch (InterruptedException e){
                    GLog.printWtf(this, "CheckOut Thread is Interrupted!");
                }catch (Exception e){
                    //GLog.printExceptWithSaveToPciDB(this, "Unhandled Exception !! ", e, ErrorInfo.CODE_ETC_UNKNOWN, ErrorInfo.CATEGORY_ETC);
                    Pci_Service pci_service = Global.getInstance().getPciService();
                    if(pci_service != null) pci_service.finishPciService();
                }
            }
        });

        if(pciProperty.getPci_checkoutloop_use()){
            checkOutThread.start();
            GLog.printWtf(this, " Started CheckOut Thread !");
        }
    }


    private void voiceCheckOut(PciSrv_PidData tempPidData){
        dataManager.getPidManager().checkOut(tempPidData.getPid(), CheckInData.CHECKIN_TYPE_V);
        // MC에 체크아웃 전송
        pciBroadcastReceiver.sendPciListToMC(null, tempPidData, "V", false, "VOICE_CHECK_OUT");
        // 서버에 보이스체크아웃 전송 (호출제거 : 2019_01_22)
//        netManager.apiPci6005_VoiceAction(new PciSrv_VoiceChkInData(tempPidData.getVoiceId(), tempPidData.getUserKey(), HsUtil_Date.getTimeString(HsUtil_Date.getCurrentTime(), pciProperty.getDate_format_pcisrv_checkin_list()), "O"));
    }

    private void wifiCheckOut(PciSrv_PidData tempPidData){
        dataManager.getPidManager().checkOut(tempPidData.getPid(), CheckInData.CHECKIN_TYPE_W);
        // MC에 체크아웃 전송(wifi는 서버에 전송 X)
        pciBroadcastReceiver.sendPciListToMC(null, tempPidData, "W", false, "WIFI_CHECK_OUT");
    }

    /**
     * 특정시간에 Stb정보 및 에러정보 일괄전송(Pci_Property에 시간 설정)
     */
    public void startStbInfoMonitoringThread(){
        stbMonitoringThread = new GThread("StbMonitoringThread", new GRunnable() {
            @Override
            public void run() {
                try {
                    while (stbMonitoringThread != null) {
                        Thread.sleep(1000);

                        long sleepTime;
                        /** ↓↓↓↓↓ 업데이트시간 랜덤으로 수정 - by dalkommJK | 2021-01-27 ↓↓↓↓↓↓ */
                        int randomHour = (int) (Math.random()*10 + 6);
                        int updateHour = randomHour % pciProperty.getPci_updatetime();
                        //int updateHour = pciProperty.getPci_updatetime();
                        /** ↑↑↑↑↑ 업데이트시간 랜덤으로 수정 - by dalkommJK | 2021-01-27  ↑↑↑↑↑ */
                        Calendar cal3 = Calendar.getInstance(); // add by dalkommjk | v15.00.03 | 2021-09-24
                        if(pciProperty.getPci_update_stb_info() <= 0){
                            Calendar cal = Calendar.getInstance();
                            Calendar cal2 = Calendar.getInstance();
                            cal2.set(Calendar.HOUR_OF_DAY, updateHour);
                            cal2.set(Calendar.MINUTE, 0);
                            cal3 = cal2; // add by dalkommjk | v15.00.03 | 2021-09-24

                            if(cal.get(Calendar.HOUR_OF_DAY) >= updateHour){
                                cal2.set(Calendar.DATE, cal.get(Calendar.DAY_OF_MONTH) + 1);
                                cal3 = cal2; // add by dalkommjk | v15.00.03 | 2021-09-24
                            }

                            //GLog.printDbg(this, "Calc Wait StbInfoMonitoring > checkTime:" + HsUtil_Date.getTimeString(cal2.getTime()) + " / cur:" + HsUtil_Date.getTimeString(cal.getTime()));
                            GLog.printDbg(this, "StbInfoMonitoring > currentTime: " + HsUtil_Date.getTimeString(cal.getTime())); // modify by dalkommjk | v15.00.03 | 2021-09-24
                            sleepTime = cal2.getTimeInMillis() - cal.getTimeInMillis();
                        }else{
                            sleepTime = pciProperty.getPci_update_stb_info() * 1000;
                            GLog.printDbg(this, "Wait StbInfoMonitoring work at " + HsUtil_Date.getTimeString(new Date(HsUtil_Date.getCurrentTime().getTime() + sleepTime)));
                        }

                        // 부하분산 추가
                        if(pciProperty.getPci_updateinfo_split_use()){
                            int minTime = pciProperty.getPci_updateinfo_split_min();
                            int maxTime = pciProperty.getPci_updateinfo_split_max();
                            int waitTime = (int) ((Math.random() * (maxTime - minTime)) + minTime) * 1000;
                            GLog.printDbg(this,"StbInfoMonitoring > wait for split time : " + waitTime + " msec");
                            sleepTime += waitTime;
                            cal3.add(Calendar.SECOND, Long.valueOf(waitTime/1000).intValue()); // add by dalkommjk | v15.00.03 | 2021-09-24
                        }

                        GLog.printDbg(this, "Wait StbInfoMonitoring > " + sleepTime + " msec");
                        GLog.printDbg(this, "StbInfoMonitoring > Next checkTime: " + HsUtil_Date.getTimeString(cal3.getTime())); // add by dalkommjk | v15.00.03 | 2021-09-24
                        if(sleepTime > 0) Thread.sleep(sleepTime);

                        StbDb stbDb = dataManager.getStbDb();
                        if(dataManager != null && dataManager.getStbDb() != null){
                            boolean stbInfoChanged = stbDb.refreshDbInfo(Global.getInstance().getPciService());
                            if(stbInfoChanged){
                                // 변동정보가 있을 경우에만 api 호출
                                netManager.apiPci6002_sendStbInfo(true);
                            }
                            stbDb.print_CommonDb();
                            stbDb.print_Userproperty();
                        }
                        /** ADPCI by dalkommjk - v16.00.02 | 2022.02.22 */
                        StbDb stbDb_AD = addataManager.getStbDb();
                        if(addataManager != null && addataManager.getStbDb() != null){
                            boolean stbInfoChanged = stbDb.refreshDbInfo(Global.getInstance().getPciService());
                            if(stbInfoChanged){
                                // 변동정보가 있을 경우에만 api 호출
                                adnetManager.apiPci6002_sendStbInfo(true);
                            }
                            stbDb.print_CommonDb();
                            stbDb.print_Userproperty();
                        }
                        /**************************/


                        PciDB pciDB = Global.getInstance().getPciDB();
                        if(pciDB != null){
                            if(pciDB.getErrorInfoCountOnDB() > 0){
                                // 에러정보가 1개이상 있을 경우에만 api 호출
                                netManager.apiPci6007_sendErrorInfo();
                            }
                        }
                    }
                }catch (InterruptedException ex){
                    GLog.printWtf(this, "StbInfo Monitoring Thread is Interrupted!");
                }catch (Exception e){
                    //GLog.printExceptWithSaveToPciDB(this, "Unhandled Exception !! ", e, ErrorInfo.CODE_ETC_UNKNOWN, ErrorInfo.CATEGORY_ETC);
                    Pci_Service pci_service = Global.getInstance().getPciService();
                    if(pci_service != null) pci_service.finishPciService();
                }
            }
        });

        if(pciProperty.getPci_updateloop_use()){
            stbMonitoringThread.start();
            GLog.printWtf(this, " Started StbInfo Monitoring Thread !");
        }
    }



    public void startStandbyUpdateLoop(){    // 대기모드에서의 업데이트
        standbyUpdateThread = new GThread("StandbyThread", new GRunnable() {
            @Override
            public void run() {
                PciSrv_PolicyData policyData = null;
                AD_PciSrv_PolicyData adpolicyData = null; // ADPCI by dalkommjk - v16.00.02 | 2022-02-26
                if(dataManager != null && dataManager.getPciSrvData() != null){
                    policyData = dataManager.getPciSrvData().getPciSrvPolicyData();
                }
                /** ADPCI by dalkommjk - v16.00.02 | 2022-02-26 */
                if(addataManager != null && addataManager.getPciADSrvData() != null){
                    adpolicyData = addataManager.getPciADSrvData().getPciADSrvPolicyData();
                }
                /**********************/

                if(policyData != null) {
                    long sleepTime = 0;
                    while (standbyUpdateThread != null) {
                        int waitingTimeForUpdate = (policyData != null) ? policyData.getWaitingTimeForUpdate() : 10;
                        try {
                            sleepTime = waitingTimeForUpdate * 1000;

                            GLog.printDbg(this, "Wait StandbyUpdateLoop(waitTimeForUpdate) > " + sleepTime);
                            if(sleepTime > 0) Thread.sleep(sleepTime);
                        } catch (InterruptedException e) {
                            GLog.printInfo(this,"wait update policy interrupted(StandbyUpdateLoop, waitTimeForUpdate)");
                            return;
                        }

                        if (policyData != null) {
                            Date expiredDate = policyData.getExpiredDatetime();
                            if(expiredDate == null){
                                GLog.printWarn(this, "stop UpdateCheckLoop cause expiredDate is null");
                                break;
                            }

                            Date curTime = Calendar.getInstance().getTime();
                            if (expiredDate.before(curTime)) {

                                // 부하분산 추가
                                if(pciProperty.getPci_updateinfo_split_use()){
                                    int minTime = pciProperty.getPci_updateinfo_split_min();
                                    int maxTime = pciProperty.getPci_updateinfo_split_max();
                                    int waitTime = (int) ((Math.random() * (maxTime - minTime)) + minTime) * 1000;
                                    GLog.printDbg(this,"StandbyUpdateLoop > wait for split time : " + waitTime + " msec");
                                    sleepTime = waitTime;

                                }

                                if(sleepTime > 0){
                                    GLog.printDbg(this, "Wait StandbyUpdate(Split time) > " + sleepTime);
                                    try{
                                        Thread.sleep(sleepTime);
                                    }catch (InterruptedException e){
                                        GLog.printInfo(this,"wait update policy interrupted(StandbyUpdateLoop, Split time)");
                                        return;
                                    }
                                }

                                GLog.printWtf(this, "try to Update Pci-Policy by standby mode !! > Current Time : " + HsUtil_Date.getTimeString(curTime) + "/ expired DateTime : " + HsUtil_Date.getTimeString(expiredDate));
                                netManager.updatePolicy();

                            } else {
                                GLog.printInfo(this,"before expired date time :: " + HsUtil_Date.getTimeString(expiredDate) + " / current time : " + HsUtil_Date.getTimeString(curTime));
                            }
                        } else {
                            /** 정책 업데이트 실패 시 콜드부팅 시까지 동작 없음.
                             updatePolicy(null);
                             */
                            break;
                        }
                    }

                    GLog.printInfo(this, "stop standby update loop complete !");
                }else{
                    GLog.printInfo(this, "Can't start standby update loop. policy data is null !!");
                }
//            }    // ADPCI by dalkommjk - v16.00.02 | 2022-02-26
//        });      // ADPCI by dalkommjk - v16.00.02 | 2022-02-26

        /** ADPCI by dalkommjk - v16.00.02 | 2022-02-26 */
                if(adpolicyData != null) {
                long sleepTime = 0;
                while (standbyUpdateThread != null) {
                    int waitingTimeForUpdate = (adpolicyData != null) ? adpolicyData.getWaitingTimeForUpdate() : 10;
                    try {
                        sleepTime = waitingTimeForUpdate * 1000;

                        GLog.printDbg(this, "[PCIAD] Wait StandbyUpdateLoop(waitTimeForUpdate) > " + sleepTime);
                        if(sleepTime > 0) Thread.sleep(sleepTime);
                    } catch (InterruptedException e) {
                        GLog.printInfo(this,"[PCIAD] wait update policy interrupted(StandbyUpdateLoop, waitTimeForUpdate)");
                        return;
                    }

                    if (adpolicyData != null) {
                        Date expiredDate = adpolicyData.getExpiredDatetime();
                        if(expiredDate == null){
                            GLog.printWarn(this, "[PCIAD] stop UpdateCheckLoop cause expiredDate is null");
                            break;
                        }

                        Date curTime = Calendar.getInstance().getTime();
                        if (expiredDate.before(curTime)) {

                            // 부하분산 추가
                            if(pciProperty.getPci_updateinfo_split_use()){
                                int minTime = pciProperty.getPci_updateinfo_split_min();
                                int maxTime = pciProperty.getPci_updateinfo_split_max();
                                int waitTime = (int) ((Math.random() * (maxTime - minTime)) + minTime) * 1000;
                                GLog.printDbg(this,"[PCIAD] StandbyUpdateLoop > wait for split time : " + waitTime + " msec");
                                sleepTime = waitTime;

                            }

                            if(sleepTime > 0){
                                GLog.printDbg(this, "[PCIAD] Wait StandbyUpdate(Split time) > " + sleepTime);
                                try{
                                    Thread.sleep(sleepTime);
                                }catch (InterruptedException e){
                                    GLog.printInfo(this,"[PCIAD] wait update policy interrupted(StandbyUpdateLoop, Split time)");
                                    return;
                                }
                            }

                            GLog.printWtf(this, "[PCIAD] try to Update PciAD-Policy by standby mode !! > Current Time : " + HsUtil_Date.getTimeString(curTime) + "/ expired DateTime : " + HsUtil_Date.getTimeString(expiredDate));
                            adnetManager.updatePolicy();

                        } else {
                            GLog.printInfo(this,"[PCIAD] before expired date time :: " + HsUtil_Date.getTimeString(expiredDate) + " / current time : " + HsUtil_Date.getTimeString(curTime));
                        }
                    } else {
                        /** 정책 업데이트 실패 시 콜드부팅 시까지 동작 없음.
                         updatePolicy(null);
                         */
                        break;
                    }
                }

                GLog.printInfo(this, "[PCIAD] stop standby update loop complete !");
            }else{
                GLog.printInfo(this, "[PCIAD] Can't start standby update loop. policy data is null !!");
            }
        }

    });
        /******************************************************************************************************************************************/
        if(pciProperty.getPci_updateloop_use()){
            standbyUpdateThread.start();
            GLog.printWtf(this, " Started Standby Update Thread !");
        }
    }

    public void stopStandbyUpdateLoop(){    // 동작모드 이기 때문에 대기모드에서의 업데이트 중단
        if (standbyUpdateThread != null) {
            GLog.printInfo(this, "try to stop standbyUpdateThread. standbyUpdateThread.isAlive()? : " + standbyUpdateThread.isAlive());
            if (standbyUpdateThread.isAlive()) {
                standbyUpdateThread.interrupt();
            }
            standbyUpdateThread = null;
        }
    }

    /**
     * 최한시간(정책정보에 포함) 여부 판단하여 정책 업데이트
     */
    public void startUpdateCheckLoop(){
        idleTimeUpdateThread = new GThread("IdleTimeUpdateThread", new GRunnable() {
            @Override
            public void run() {
                try{
                    while (idleTimeUpdateThread != null){
                        if(dataManager != null){
                            checkIdleTimeForUpdate();
                        }
                        Thread.sleep(60000);
                    }
                }catch (InterruptedException e){
                    GLog.printWtf(this, "Pci Service Thread is Interrupted!");
                }catch (Exception e){
                    //GLog.printExceptWithSaveToPciDB(this, "Unhandled Exception !! ", e, ErrorInfo.CODE_ETC_UNKNOWN, ErrorInfo.CATEGORY_ETC);
                    Pci_Service pci_service = Global.getInstance().getPciService();
                    if(pci_service != null) pci_service.finishPciService();
                }
            }
        });

        if(pciProperty.getPci_updateloop_use()){
            idleTimeUpdateThread.start();
            GLog.printWtf(this, " Started Idle Time Update Thread !");
        }
    }


    public void checkIdleTimeForUpdate() throws Exception{
        Date curTime = Calendar.getInstance().getTime();
        int prevT = 0;
        long sleepTime = 0;

        if (_prevCheckT != null) prevT = Integer.parseInt(HsUtil_Date.getTimeString(_prevCheckT, idleDateFormat));
        int curT = Integer.parseInt(HsUtil_Date.getTimeString(curTime, idleDateFormat));
        PciSrv_PolicyData pciSrvPolicyData = dataManager.getPciSrvData().getPciSrvPolicyData();
        AD_PciSrv_PolicyData adPciSrvPolicyData = addataManager.getPciADSrvData().getPciADSrvPolicyData(); // AD PCI by dalkommjk - v16.00.01 | 2022-02-22

        if (pciSrvPolicyData != null) {
            int idleT = pciSrvPolicyData.getIdleTimeForUpdate();
            if (idleT < 240000 && prevT < idleT && curT > idleT && curTime.after(pciSrvPolicyData.getExpiredDatetime())) {
                // 부하분산 추가
                if(pciProperty.getPci_updateinfo_split_use()){
                    int minTime = pciProperty.getPci_updateinfo_split_min();
                    int maxTime = pciProperty.getPci_updateinfo_split_max();
                    int waitTime = (int) ((Math.random() * (maxTime - minTime)) + minTime) * 1000;
                    GLog.printDbg(this,"IdleTimeForUpdate > wait for split time : " + waitTime + " msec");
                    sleepTime = waitTime;
                }

                if(sleepTime > 0){
                    GLog.printDbg(this, "Wait IdleTime Update > " + sleepTime);
                    Thread.sleep(sleepTime);
                }

                GLog.printWtf(this, "try to Update Pci-Policy by idleTime !! > Current Time : " + HsUtil_Date.getTimeString(curTime) + "/ expired DateTime : " + HsUtil_Date.getTimeString(pciSrvPolicyData.getExpiredDatetime()));
                netManager.updatePolicy();
                adnetManager.updatePolicy(); // AD PCI by dalkommjk - v16.00.01 | 2022-02-22
                KPNSReq(true); // KPNS 토큰 업데이트
            }
        } else {
            /** 정책 업데이트 실패 시 콜드부팅 시까지 동작 없음.
             updatePolicy(null);
             */
        }
        _prevCheckT = curTime;
    }
    public static void NetworkCheck(){
        final String CONNECTION_CONFIRM_CLIENT_URL = "https://www.kt.com/";
        NetworkCheckThread = new GThread("NetworkCheckThread", new GRunnable() {
            public void run() {
                HttpURLConnection conn = null;
                try {
                    conn = (HttpURLConnection)new URL(CONNECTION_CONFIRM_CLIENT_URL).openConnection();
                    conn.setRequestProperty("User-Agent","Android");
                    conn.setConnectTimeout(3*1000);
                    conn.connect();
                    int responseCode = conn.getResponseCode();
                    if(responseCode == 200) GLog.printDbg(this, "Network Status Success");
                    else GLog.printDbg(this, "Network Status code " + responseCode);
                }
                catch (Exception e) {
                    e.printStackTrace();
                    GLog.printDbg(this, "Network Status fail cause :: " + e);

                    try {
                        GLog.printDbg(this, "Network retry connection after 10 min" );
                        NetworkCheckThread.sleep( 10*60*1000);
                        //NetworkCheckThread.sleep( 1000);
                        Pci_Service pci_service = Global.getInstance().getPciService(); // pci_service 재시작 by dalkommjk | 2021-01-28
                        if(pci_service != null) pci_service.finishPciService(); // pci_service 재시작 by dalkommjk | 2021-01-28
                    } catch (InterruptedException interruptedException) {
                        interruptedException.printStackTrace();
                    }

                }
                if(conn != null){
                    conn.disconnect();
                }

            }
        });
        NetworkCheckThread.start();
    }

    /** ↓↓↓↓↓ KPNS추가 - by dalkommJK | 2019-09-30 ↓↓↓↓↓↓ */   /** modify by dalkommjk | 2020-03-17 */
    public void UpdateKPNSToken(){
        kpnsUpdateThread = new GThread("kpnsUpdateThread", new GRunnable() {
            public void run() {
                try {
                    GLog.printDbg(this, "KPNS Token Update ...");
                    netManager.apiPci6010_activation_token();  // KPNS 토큰 업데이트
                    kpnsUpdateThread.sleep(1000);
                } catch (InterruptedException e) {
                    GLog.printDbg(this, "** KPNS Token Update Interrupted ** ");
                } catch (Exception e) {
                    GLog.printDbg(this, "** KPNS Token Update Fail ** ");
                }
            }});
        kpnsUpdateThread.start();
        GLog.printWtf(this, " Started kpnsUpdate Thread !");
    }

    public static void KPNSReq(boolean state){
        Handler mHandler = new Handler(Looper.getMainLooper());
        if(state == true) {
            mHandler.postDelayed(new KPNSReqRunnalbe(), 0);
        }else {
            mHandler.removeMessages(0);
        }
    }
    private static final class KPNSReqRunnalbe implements Runnable {
        @Override
        public void run(){
            try {
                KPNSInit();
                GLog.printDbg(this, "<<< KPNS Key Update >>>");
            }
            catch (Exception e) {
                GLog.printDbg(this, "** KPNS Token Update Fail ** ");
            }

        }

    }





    /**
     * PciManager Instance를 반환 한다.
     */
    public static PciManager getInstance(){
        if(INSTANCE == null){
            INSTANCE = new PciManager();
        }
        return INSTANCE;
    }



    public void destory(){

        if(stbMonitoringThread != null && (stbMonitoringThread.isAlive() || stbMonitoringThread.getState() == Thread.State.RUNNABLE)){
            stbMonitoringThread.interrupt();
        }

        if(checkOutThread != null && (checkOutThread.isAlive() || checkOutThread.getState() == Thread.State.RUNNABLE)){
            checkOutThread.interrupt();
        }

        if(keepAliveTestThread != null && (keepAliveTestThread.isAlive() || keepAliveTestThread.getState() == Thread.State.RUNNABLE)){
            keepAliveTestThread.interrupt();
        }

        if(idleTimeUpdateThread != null && (idleTimeUpdateThread.isAlive() || idleTimeUpdateThread.getState() == Thread.State.RUNNABLE)){
            idleTimeUpdateThread.interrupt();
        }

        if(standbyUpdateThread != null && (standbyUpdateThread.isAlive() || standbyUpdateThread.getState() == Thread.State.RUNNABLE)){
            standbyUpdateThread.interrupt();
        }

        if(dataManager != null){
            dataManager.destroy();
        }
        if(netManager != null){
            netManager.destroy();
        }
        /**  AD PCI by dalkommjk - v16.00.01 | 2022-02-22 */
        if(adnetManager != null){
            adnetManager.destroy();
        }
        if(nonAudiblePlayer != null){
            nonAudiblePlayer.destroy();
        }
        if(kpnsUpdateThread != null && (kpnsUpdateThread.isAlive() || kpnsUpdateThread.getState() == Thread.State.RUNNABLE)){     // MR 11차 메모리릭 이슈 수정 by dalkommjk | 2020-03-17
            kpnsUpdateThread.interrupt();
        }
        if(timetickyUpdateThread != null && (timetickyUpdateThread.isAlive() || timetickyUpdateThread.getState() == Thread.State.RUNNABLE)){     // MR 11차 메모리릭 이슈 수정 by dalkommjk | 2020-03-17
            timetickyUpdateThread.interrupt();
        }
        if(NetworkCheckThread != null && (NetworkCheckThread.isAlive() || NetworkCheckThread.getState() == Thread.State.RUNNABLE)){    // Network real check  by dalkommjk | 2021-01-27
            NetworkCheckThread.interrupt();
        }
        if(pciBroadcastReceiver != null){
            mainService.unregisterReceiver(pciBroadcastReceiver);
            pciBroadcastReceiver.destroy();
        }
        if(pushreceiver != null){
            mainService.unregisterReceiver(pushreceiver);
            pushreceiver.destroy();
        }


        INSTANCE = null;
        mainService = null;
        nonAudiblePlayer = null;
        pciBroadcastReceiver = null;
        pushreceiver = null;
        netManager = null;
        adnetManager = null; // AD PCI by dalkommjk -v16.00.02 | 2022.02.22
        dataManager = null;
        addataManager = null; // AD PCI by dalkommjk -v16.00.02 | 2022.02.22
        pciProperty = null;
        //pciBroadcastReceiver = null;
        stbMonitoringThread = null;
        checkOutThread = null;
        idleTimeUpdateThread = null;
        keepAliveTestThread = null;
        wifiTestCheckoutTime = null;
        voiceTestCheckoutTime = null;
        kpnsUpdateThread = null; // MR 11차 메모리릭 이슈 수정 by dalkommjk | 2020-03-17
        timetickyUpdateThread = null;  // MR 11차 메모리릭 이슈 수정 by dalkommjk | 2020-03-17
        NetworkCheckThread = null; // network real check by dalkommjk | 2021-01-27
        GLog.printInfo(this, "destroy finished > PciManager");
    }



    public NetManager getNetManager() { return netManager; }
    public PciProperty getPciProperty() { return pciProperty; }
    public Pci_Service getMainService() { return mainService; }
    public DataManager getDataManager() { return dataManager; }
    public AD_DataManager getADDataManager() { return addataManager; } // AD PCI by dalkommjk -v16.00.02 | 2022.02.22
    public NonAudiblePlayer getNonAudiblePlayer() { return nonAudiblePlayer; }
    public PciBroadcastReceiver getPciBroadcastReceiver() { return pciBroadcastReceiver; }

    public void setPciProperty( PciProperty _pciProperty ) { pciProperty = _pciProperty; }
    public void setNonAudiblePlayer( NonAudiblePlayer _nonAudiblePlayer ) { nonAudiblePlayer = _nonAudiblePlayer; }

    public String toString(){
        return logHeader;
    }


}
