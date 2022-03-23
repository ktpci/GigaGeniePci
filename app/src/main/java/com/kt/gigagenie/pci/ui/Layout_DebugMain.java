package com.kt.gigagenie.pci.ui;

import android.app.Activity;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.view.KeyEvent;
import android.view.View;
import android.view.animation.AlphaAnimation;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.SpinnerAdapter;
import android.widget.TextView;
import android.widget.Toast;

import com.gnifrix.debug.GLog;
import com.gnifrix.platform.Pc;
import com.gnifrix.system.GRunnable;
import com.gnifrix.system.GThread;
import com.gnifrix.ui.key.KeyCode_KT_GIGAGENIE;
import com.gnifrix.ui.key.KeyEventManager;
import com.gnifrix.ui.key.KeyInfo;
import com.gnifrix.ui.layout.GniLayout;
import com.gnifrix.util.HsUtil_Date;
import com.kt.gigagenie.pci.Global;
import com.kt.gigagenie.pci.MainActivity;
import com.kt.gigagenie.pci.PciManager;
import com.kt.gigagenie.pci.Pci_Service;
import com.kt.gigagenie.pci.R;
import com.kt.gigagenie.pci.broadcast.BroadcastContext;
import com.kt.gigagenie.pci.broadcast.PciBroadcastReceiver;
import com.kt.gigagenie.pci.data.PciSrv_VoiceChkInData;
import com.kt.gigagenie.pci.data.pci_db.ErrorInfo;
import com.kt.gigagenie.pci.data.pci_db.PciDB;
import com.kt.gigagenie.pci.data.stb_db.StbDb_Userproperty;
import com.kt.gigagenie.pci.net.NetManager;

import java.util.ArrayList;
import java.util.Calendar;

/**
 * Created by LeeBaeng on 2018-09-04.
 */

public class Layout_DebugMain extends GniLayout implements KeyCode_KT_GIGAGENIE, BroadcastContext {
    private static final String LogHeader = "Layout_DebugMain";
    private KeyEventManager keyEventManager;

    RelativeLayout layout_CommentMenu = null;
    RelativeLayout layout_main = null;
    TextView txtView_AlphaValue = null;
    TextView txtView_VersionValue = null;
    TextView txtView_LogScreen = null;
    TextView txtView_commentMenu = null;
    Button btnStartService = null;
    Button btnStopService = null;
    Button btnClearLog = null;
    CheckBox chkAutoScroll = null;
    Spinner spnrApiTest = null;
    Button btnApiTest = null;
    Spinner spnrBCastTest = null;
    Button btnBCastTest = null;
    Button btnReadPicDB = null;
    Button btnClearDB = null;
    Button btnRestart = null;
    Spinner spnrSelectServer = null;

    String finalTooltipMsg = "";

    public Layout_DebugMain(Activity activity){
        super(activity);
        keyEventManager = KeyEventManager.getInstance();

        layout_CommentMenu = (RelativeLayout) activity.findViewById(R.id.Layout_CommentMenu);
        layout_main = (RelativeLayout) activity.findViewById(R.id.Layout_main);
        txtView_AlphaValue = (TextView) activity.findViewById(R.id.textView_Alpha_value);
        txtView_VersionValue = (TextView) activity.findViewById(R.id.textView_version_value);
        txtView_LogScreen = (TextView) activity.findViewById(R.id.textView_log);
        txtView_commentMenu = (TextView) activity.findViewById(R.id.txtView_commentMenu);
        btnStartService = (Button) activity.findViewById(R.id.btn_StartService);
        btnStopService = (Button) activity.findViewById(R.id.btn_StopService);
        btnClearLog = (Button)activity.findViewById(R.id.btn_ClearLog);
        chkAutoScroll = (CheckBox)activity.findViewById(R.id.chkBox_AutoScroll);
        spnrApiTest = (Spinner)activity.findViewById(R.id.spnr_apiList);
        btnApiTest = (Button) activity.findViewById(R.id.btn_ApiTest);
        spnrBCastTest = (Spinner)activity.findViewById(R.id.spnr_broadcastList);
        btnBCastTest = (Button) activity.findViewById(R.id.btn_broadcastTest);
        btnReadPicDB = (Button) activity.findViewById(R.id.btn_readPciDB);
        btnRestart = (Button) activity.findViewById(R.id.btn_restart);
        btnClearDB = (Button) activity.findViewById(R.id.btn_clearPciDB);
        spnrSelectServer = (Spinner) activity.findViewById(R.id.spnr_appenv);

        txtView_LogScreen.setMaxLines(60);

        GLog.setScreenLogComponents(txtView_LogScreen, chkAutoScroll);
        GLog.refreshScreenLog();
        setLayout_Alpha(0.8f);

        btnClearLog.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                GLog.clearScreenLog();
            }
        });

        initUI();
    }

    public void initUI(){
        btnApiTest.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                apiTest(spnrApiTest.getSelectedItem().toString());
            }
        });

        btnBCastTest.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                broadcastTest(spnrBCastTest.getSelectedItem().toString());
            }
        });
        btnReadPicDB.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                readPciDB();
            }
        });
        btnClearDB.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                clearPciDB();
            }
        });
        btnRestart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(Global.getInstance().getPciService() != null) Global.getInstance().getPciService().finishPciService();

                Intent i = new Intent(context, MainActivity.class);
                int mPendingIntentId = 123456;
                PendingIntent mPendingIntent = PendingIntent.getActivity(context, mPendingIntentId,  i, PendingIntent.FLAG_CANCEL_CURRENT);
                AlarmManager mgr = (AlarmManager)context.getSystemService(Context.ALARM_SERVICE);
                mgr.set(AlarmManager.RTC, System.currentTimeMillis() + 100, mPendingIntent);
                android.os.Process.killProcess(android.os.Process.myPid()); /** KISA 보완조치 - by dalkommjk | 2019-10-30 */
            }
        });
        spnrSelectServer.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                if(!PciManager.isInitialized() || !Pci_Service.pciReady){
                    switch (adapterView.getSelectedItem().toString()){
                        case "bmt" : Global.getInstance().getPciProperty().setApiEnvToBmt(); break;
                        case "dev" : Global.getInstance().getPciProperty().setApiEnvToDev(); break;
                        case "live" : Global.getInstance().getPciProperty().setApiEnvToLive(); break;
                        case "none" : Global.getInstance().getPciProperty().setApiEnvToNone(); break;
                        default: break;
                    }
                }else{
                    GLog.printWtf("DEBUG", "can't change app Environment. pciService is running");
                    setSelectionSpnrSelectServer();
                    return;
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

        spnrApiTest.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                int[] location = new int[2];
                adapterView.getLocationOnScreen(location);
                showMenuTooltip(location[0], getAPIComment(spnrApiTest.getSelectedItem().toString()));
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

        spnrBCastTest.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                int[] location = new int[2];
                adapterView.getLocationOnScreen(location);
                showMenuTooltip(location[0], getBroadcastComment(spnrBCastTest.getSelectedItem().toString()));
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });
        setSelectionSpnrSelectServer();
    }

    public void setSelectionSpnrSelectServer(){
        if(Global.getInstance().getPciProperty() != null){
            String env = Global.getInstance().getPciProperty().getAppEnv();
            switch (env){
                case "none" : spnrSelectServer.setSelection(0); break;
                case "dev" : spnrSelectServer.setSelection(1); break;
                case "bmt" : spnrSelectServer.setSelection(2); break;
                case "live" : spnrSelectServer.setSelection(3); break;
                default: break;
            }
        }
    }

    @Override
    public void init() {

    }

    @Override
    public void start() {
        showMenuTooltip(251, getMenusComment(R.id.spnr_appenv));
    }

    @Override
    public void pause() {

    }

    @Override
    public void stop() {

    }

    @Override
    public void onKey(int keyCode, KeyEvent event) {
        KeyInfo realKey = keyEventManager.getMatchedKey(keyCode);

        if(realKey == null){
            GLog.printDbg(this, "Key Event-Matched key not found!! > code : " + keyCode);
            return;
        }

        int realKeyCode = realKey.getCode();

//        if(realKeyCode != KEY_VOL_UP || realKeyCode != KEY_VOL_DOWN || realKeyCode != KEY_UP ||realKeyCode != KEY_DOWN){
//            GLog.printDbg(this, "On Key Event : " + realKey.toString() + " / input : " + keyCode);
//        }

        switch (realKeyCode){
            case KEY_VOL_UP : addLayoutAlpha(0.1f); break;
            case KEY_VOL_DOWN : addLayoutAlpha(-0.1f); break;

            case KEY_BLUE :
                GLog.printDbg(this,"txtView_LogScreen.getScrollY() > " + txtView_LogScreen.getScrollY());
                GLog.printDbg(this, "txtView_LogScreen.getLineCount() > " + txtView_LogScreen.getLineCount());
                GLog.printDbg(this, "txtView_LogScreen.getLineTop() > " + txtView_LogScreen.getLayout().getLineTop(txtView_LogScreen.getLineCount()));
                GLog.printDbg(this, "txtView_LogScreen.getHeight() > " + txtView_LogScreen.getHeight());
                GLog.printDbg(this, "calc > " + (txtView_LogScreen.getLayout().getLineTop(txtView_LogScreen.getLineCount()) - txtView_LogScreen.getHeight()));
                break;

            case KEY_UP :
                txtView_LogScreen.scrollBy(0, -(txtView_LogScreen.getHeight() / 2));
                if(txtView_LogScreen.getScrollY() < 0) txtView_LogScreen.scrollTo(0, 0);
                GLog.setScreenLogScrollPosition(txtView_LogScreen.getScrollY());
                break;

            case KEY_DOWN :
                int scrollMax = txtView_LogScreen.getLayout().getLineTop(txtView_LogScreen.getLineCount()) - txtView_LogScreen.getHeight();
                if(scrollMax > 0){
                    txtView_LogScreen.scrollBy(0, +(txtView_LogScreen.getHeight() / 2));
                    if(txtView_LogScreen.getScrollY() > scrollMax)
                        txtView_LogScreen.scrollTo(0, scrollMax + 40);
                }
                GLog.setScreenLogScrollPosition(txtView_LogScreen.getScrollY());
                break;
            default: break;
        }

        View focusedItem =Global.getInstance().getMainActivity().getCurrentFocus();
        showMenuTooltip(focusedItem);
    }

    private String getMenusComment(int id){
        switch (id){
            case R.id.spnr_appenv : return "Pci Platform, 비가청 서버 연결 선택";
            case R.id.btn_ApiTest : return "선택된 API 테스트. PCI Service가 시작된 상태에서만 가능";
            case R.id.btn_broadcastTest : return "선택된 Broadcast 테스트. PCI Service가 시작된 상태에서만 가능";
            case R.id.btn_ClearLog : return "화면 로그 초기화";
            case R.id.btn_clearPciDB : return "Pci-DB에 저장된 에러로그 초기화";
            case R.id.btn_readPciDB : return "Pci-DB에 저장된 에러로그 읽기";
            case R.id.btn_restart : return "App 재시작(Activity)";
            case R.id.btn_StartService : return "Pci Service 시작";
            case R.id.btn_StopService : return "Pci Service 종료";
            case R.id.chkBox_AutoScroll : return "로그가 갱신될 경우 Auto Scroll 기능 활성화";
            case R.id.spnr_apiList : return "Test를 진행하고자 하는 API 선택";
            case R.id.spnr_broadcastList : return "Test를 진행하고자 하는 Broadcast 선택";
            default: return "";
        }
    }

    private String getAPIComment(String txt){
        GLog.printDbg(this, "getAPIComment : " + txt);
        switch (txt){
            case "api_pci_001" : return "STB Key 요청";
            case "api_pci_002_USER1" : return "STB 정보 갱신(유저1명)";
            case "api_pci_002_USER2" : return "STB 정보 갱신(유저2명)";
            case "api_pci_002_NONE" : return "STB 정보 갱신(유저리스트 없음)";
            case "api_pci_003" : return "STB 정책 조회";
            case "api_pci_004" : return "비가청 음원 정보 전송";
            case "api_pci_005_CheckIn" : return "보이스 체크인";
            case "api_pci_005_CheckOut" : return "보이스 체크아웃";
            case "api_pci_005_Update" : return "보이스ID → 유저키 변경 업데이트";
            case "api_pci_006" : return "활성화된 UserKey 정보 전송";
            case "api_pci_007" : return "장애정보 전송";
            case "api_pci_008" : return "체크인 리스트 조회";
            case "api_pci_009_Play" : return "비가청음원 재생 설정 ON(PLAY)";
            case "api_pci_009_Stop" : return "비가청음원 재생 설정 OFF(STOP)";
            case "api_pci_0er" : return "API 에러 테스트";
            default: return "";
        }
    }

    private String getBroadcastComment(String txt){
        GLog.printDbg(this, "getBroadcastComment : " + txt);
        switch (txt){
            case "SEND_KEEPALIVE_RESP/kt.action.service.resp" : return "Keep Alive 응답 to MC";
            case "SEND_PCILIST_RESP/kt.action.pci.resp" : return "체크인 리스트 응답 to MC";
            case "SEND_MCSTATE_CHECK/kt.action.state.req" : return "Mc App상태 체크요청 to MC";
            case "SEND_PCI_SETTING_RESP/kt.action.launcher.pci.status.rep" : return "비가청 설정 응답 to Launcher";
            case "SEND_POLICY_RESP/kt.action.launcher.pci.service.rep" : return "PCI정책 조회 응답 to Launcher";
            case "RECV_KEEPALIVE_CHECK/kt.action.service.check" : return "App 상태 요청(KeepAlive) from MC";
            case "RECV_MCSTATE_RESP/kt.action.state.resp" : return "MC App상태 수신 from MC";
            case "RECV_VOICE_ALARM/kt.action.voice.alarm/I" : return "보이스 체크인 이벤트 수신 from VoiceApp";
            case "RECV_VOICE_ALARM/kt.action.voice.alarm/U" : return "보이스 업데이트 이벤트 수신 from VoiceApp";
            case "RECV_PCILIST_REQ/kt.action.pci.req" : return "체크인 리스트 요청 from MC";
            case "RECV_PCI_SETTING_REQ/kt.action.pci.setting" : return "비가청 설정 요청 from Launcher";
            case "RECV_PCI_POLICY_REQ/kt.action.pci.service" : return "PCI정책 조회 요청 from Launcher";
            case "RECV_USER_CHANGE/kt.action.launcher.mode.change" : return "사용자 계정 변경정보 수신 from Launcher";
            case "RECV_USER_DISCONNECT/kt.action.launcher.auth.init" : return "사용자 계정 해제 수신 from Launcher";
            case "RECV_PUSH_CHECKIN_LIST/kt.action.push.checkinlist" : return "CheckIn 리스트 수신 from PushClient";
            case "RECV_ACTION_POWER_STANDBY/kt.action.power.standby" : return "대기모드(전원 OFF) 진입 from MC";
            case "RECV_ACTION_POWER_NORMAL/kt.action.power.normal" : return "노멀모드(전원 ON) 진입 from MC";
//            case "RECV_PUSH_ALARM/kt.action.push.alarm" : return "알림정보(팝업 등 ) from PushClient";
//            case "RECV_PUSH_SETTING/kt.action.push.setting" : return "PCI App 설정 정보 from PushClient";
            default: return "";
        }
    }

    private void showMenuTooltip(View focusedView){
        int[] location = new int[2];
        focusedView.getLocationOnScreen(location);
        showMenuTooltip(location[0], getMenusComment(focusedView.getId()));
    }
    private void showMenuTooltip(int x, final String tooltipMessage){
        if(tooltipMessage.trim().equals("")) return;
        if(finalTooltipMsg != null && finalTooltipMsg.equals(tooltipMessage)) return;

        txtView_commentMenu.setText(tooltipMessage);

        RelativeLayout.LayoutParams param = new RelativeLayout.LayoutParams(layout_CommentMenu.getLayoutParams());
        param.leftMargin = x + 15;
        layout_CommentMenu.setLayoutParams(param);

        final long showTime = tooltipMessage.length() * 150;

        AlphaAnimation animation1 = new AlphaAnimation(0.0f, 1.0f);
        animation1.setDuration(500);
        animation1.setFillAfter(true);
        txtView_commentMenu.clearAnimation();
        txtView_commentMenu.startAnimation(animation1);
        finalTooltipMsg = tooltipMessage;

        new Thread(new Runnable() {
            @Override
            public void run() {
                try{
                    if(showTime > 0) Thread.sleep(showTime);
                }catch (InterruptedException ex){
                    ex.printStackTrace();
                }

                if(Global.isInitialized() && Global.getInstance().getMainActivity() != null){
                    Global.getInstance().getMainActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            synchronized (finalTooltipMsg){
                                if(tooltipMessage.equals(finalTooltipMsg)){
                                    AlphaAnimation animation1 = new AlphaAnimation(1.0f, 0.0f);
                                    animation1.setDuration(500);
                                    animation1.setFillAfter(true);
                                    txtView_commentMenu.clearAnimation();
                                    txtView_commentMenu.startAnimation(animation1);
                                }
                            }
                        }
                    });
                }
            }
        }).start();

    }

    private void addLayoutAlpha(float addValue){
        float currentAlpha = getLayout_main().getAlpha();
        if(0.1f < currentAlpha + addValue && currentAlpha + addValue <= 1.01f) setLayout_Alpha(currentAlpha + addValue);
    }


    private void setLayout_Alpha(float alpha){
        getLayout_main().setAlpha(alpha);
        int showValue = ((int) Math.floor(alpha * 10)) * 10;
        GLog.printDbg(this,"Set Screen Alpha by User : " + showValue + "%");
//        getTxtView_AlphaValue().setText(showValue + "%");
    }


    private void clearPciDB(){
        if(!Global.getInstance().getPciProperty().getApp_debug_usePciDb()){
            GLog.printWtf("DEBUG", "can't read Pci-DB. PCI-DB set to not activate in pciProperty");
            return;
        }

        PciDB pciDB = Global.getInstance().getPciDB();
        if(pciDB == null){
            GLog.printWtf("DEBUG", "can't read Pci-DB. PCI-DB Instance is null");
            return;
        }

        pciDB.clearErrorInfo();

    }

    private void readPciDB(){
        if(!Global.getInstance().getPciProperty().getApp_debug_usePciDb()){
            GLog.printWtf("DEBUG", "can't read Pci-DB. PCI-DB set to not activate in pciProperty");
            return;
        }

        PciDB pciDB = Global.getInstance().getPciDB();
        if(pciDB == null){
            GLog.printWtf("DEBUG", "can't read Pci-DB. PCI-DB Instance is null");
            return;
        }

        GLog.printInfo(this, "read PciDB >> Error count(query) : " + pciDB.getErrorInfoCountOnDB());
        ArrayList<ErrorInfo> errorInfos = pciDB.getErrorListFromPciLocalDB();
        if(errorInfos != null && errorInfos.size() > 0){
            GLog.printInfo(this, "read PciDB >> Error count : " + errorInfos.size());
            for(ErrorInfo ei : errorInfos){

                ei.printErrorInfo("DEBUG");
            }
        }else{
            GLog.printInfo(this, "read PciDB >> errorList is null");
        }
    }

    private void apiTest(final String apiName){
        if(!PciManager.isInitialized() || !Pci_Service.pciReady){
            GLog.printWtf("DEBUG", "can't test api. pciManager is not initialized.");
            showToast_NotInitializedService();
            return;
        }
        new GThread("ApiTester", new GRunnable() {
            @Override
            public void run() {
                try{
                    String[] apiParse = apiName.split("_");
                    String apiNumber = apiParse[2];
                    if(apiNumber.startsWith("0") && apiParse.length >= 3) {

                        GLog.printWtf("DEBUG", "────────────────────────────────────────────────");
                        GLog.printWtf("DEBUG", "API TEST :: API_PCI_" + apiNumber);

                        NetManager netManager = PciManager.getInstance().getNetManager();

                        switch (apiNumber) {
                            case NetManager.API_CODE_001: netManager.apiPci6001_getPciKey(); break;
                            case NetManager.API_CODE_002:
                                if (apiParse.length > 3) {
                                    if (apiParse[3].equalsIgnoreCase("USER1")){
                                        StbDb_Userproperty u1 = new StbDb_Userproperty("U0000000000", "유저1", false, false);
                                        ArrayList<StbDb_Userproperty> upList = new ArrayList<>();
                                        upList.add(u1);
                                        netManager.apiPci6002_sendStbInfo(true, upList, false);
                                    }else if (apiParse[3].equalsIgnoreCase("USER2")){
                                        StbDb_Userproperty u1 = new StbDb_Userproperty("U0000000000", "유저1", false, false);
                                        StbDb_Userproperty u2 = new StbDb_Userproperty("U0000000001", "유저2", false, false);
                                        ArrayList<StbDb_Userproperty> upList = new ArrayList<>();
                                        upList.add(u1);
                                        upList.add(u2);
                                        netManager.apiPci6002_sendStbInfo(false, upList, false);
                                    }else if (apiParse[3].equalsIgnoreCase("NONE")) netManager.apiPci6002_sendStbInfo(false, null, true);
                                }
                                break;
                            case NetManager.API_CODE_003: netManager.apiPci6003_getPolicy(); break;
                            case NetManager.API_CODE_004: netManager.apiPci6004_sendNonSound(); break;
                            case NetManager.API_CODE_005:
                                String eventDate = HsUtil_Date.getTimeString(Calendar.getInstance().getTime(), Global.getInstance().getPciProperty().getDate_format_pcisrv_checkin_list());
                                if (apiParse.length > 3) {
                                    if (apiParse[3].equalsIgnoreCase("CheckIn")) netManager.apiPci6005_VoiceAction(new PciSrv_VoiceChkInData("V0000000000", "U0000000000", eventDate, "I", "M", "20"));
                                    else if (apiParse[3].equalsIgnoreCase("CheckOut")) netManager.apiPci6005_VoiceAction(new PciSrv_VoiceChkInData("V0000000000", "U0000000000", eventDate, "O", "M", "20"));
                                    else if (apiParse[3].equalsIgnoreCase("Update")) netManager.apiPci6005_VoiceAction(new PciSrv_VoiceChkInData("V0000000000", "U0000000000", eventDate, "U", "M", "20"));
                                }
                                break;
                            case NetManager.API_CODE_006: netManager.apiPci6006_UpdateUserKey(null); break;
                            case NetManager.API_CODE_007: netManager.apiPci6007_sendErrorInfo(); break;
                            case NetManager.API_CODE_008:
                                final NetManager netManager1 = netManager;
                                final String[] apiParse1 = apiParse;
                                new Thread(new Runnable() {
                                    @Override
                                    public void run() {
                                        try{
                                            Thread.sleep(3000);
                                            if (apiParse1.length > 3) {
                                                netManager1.apiPci6008_reqCheckInList(false, null, "PCITV(STBType/GAGI;STBModel/CT1100;FirmwareVersion/15.0.1130;STBService/OTV");
                                            }else{
                                                netManager1.apiPci6008_reqCheckInList(false, null, null);
                                            }
                                        }catch (InterruptedException e){
                                            e.printStackTrace();
                                        }
                                    }
                                }).start();
                                break;
                            case NetManager.API_CODE_009:
                                if (apiParse.length > 3) {
                                    if (apiParse[3].equalsIgnoreCase("Play")) netManager.apiPci6009_SetNonAudibleSndState(true);
                                    else if (apiParse[3].equalsIgnoreCase("Stop")) netManager.apiPci6009_SetNonAudibleSndState(false);
                                }
                                break;
                            case "0er" :
                                netManager.apiTest_Err();
                                break;

                            default:
                                break;
                        }
                    }
                }catch (Throwable e){
                    GLog.printExcept("DEBUG", "Failed to PCI Api Test", e);
                }
            }
        }).start();
    }

    private void showToast_NotInitializedService(){
        if( Global.getInstance().getMainActivity() != null ){
            Global.getInstance().getMainActivity().runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    Toast.makeText(Global.getInstance().getMainActivity(), "PCI 서비스 초기화작업이 아직 완료 되지 않았습니다.",Toast.LENGTH_SHORT).show();
                }
            });
        }
    }

    private void broadcastTest(final String broadcastName){
        if(!PciManager.isInitialized() || !Pci_Service.pciReady){
            GLog.printWtf("DEBUG", "can't test broadcast. pciManager is not initialized.");
            showToast_NotInitializedService();
            return;
        }

        new GThread("BCastTester", new GRunnable() {
            @Override
            public void run() {
                try{
                    String[] apiParse = broadcastName.split("/");
                    String actionName = apiParse[1];
                    if(actionName != null && actionName.trim().length() > 0) {

                        GLog.printWtf("DEBUG", "────────────────────────────────────────────────");
                        GLog.printWtf("DEBUG", "BROADCAST TEST :: " + actionName + "(selected item : " + broadcastName + ")");

                        PciBroadcastReceiver bCastReceiver = PciManager.getInstance().getPciBroadcastReceiver();
                        Intent i = new Intent(actionName);

                        switch (actionName) {
                            case SEND_ACTION_KEEPALIVE_RESP :
                            case SEND_ACTION_PCILIST_RESP :
                            case SEND_ACTION_MCSTATE_CHECK :
                            case SEND_ACTION_PCI_SETTING_RESP :
                            case SEND_ACTION_POLICY_RESP :
                                break;

                            case RECV_ACTION_KEEPALIVE_CHECK :
                            case RECV_ACTION_MCSTATE_RESP :
                            case RECV_ACTION_PCILIST_REQ :
                            case RECV_ACTION_PCI_SETTING_REQ :
                            case RECV_ACTION_PCI_POLICY_REQ :

                            case RECV_ACTION_PUSH_CHECKIN_LIST :
                            case RECV_ACTION_POWER_STANDBY :
                            case RECV_ACTION_POWER_NORMAL :
                                bCastReceiver.sendBroadcast(i, "BCAST-TEST :: " + actionName);
                                break;

                            case RECV_ACTION_PUSH_ALARM :
                                if(apiParse.length > 2 && apiParse[2] != null){
                                    i = new Intent(actionName);
                                    if(apiParse[2].equalsIgnoreCase("NO_BTN")){
                                        i.putExtra("title", "타이틀");
                                        i.putExtra("text", "메세지내용 입니다.");
                                        i.putExtra("btnCnt", 1);
                                        i.putExtra("closeReq", 0);
                                        i.putExtra("popNum", 1);
                                        i.putExtra("duration", 10);
                                        i.putExtra("btnTxt", "확인s");

                                    }else if(apiParse[2].equalsIgnoreCase("BTN")){
                                        i.putExtra("title", "타이틀");
                                        i.putExtra("text", "메세지내용 입니다.");
                                        i.putExtra("btnCnt", 1);
                                        i.putExtra("closeReq", 1);
                                        i.putExtra("popNum", 1);
                                        i.putExtra("duration", 10);
                                        i.putExtra("btnTxt", "확인s");
                                    }
                                    bCastReceiver.sendBroadcast(i, "BCAST-TEST :: " + actionName + "<" + apiParse[2] + ">");
                                }
                                break;
                            case RECV_ACTION_PUSH_SETTING :
                                if(apiParse.length > 2 && apiParse[2] != null){
                                    i = new Intent(actionName);
                                    i.putExtra("command_id", apiParse[2]);
                                    bCastReceiver.sendBroadcast(i, "BCAST-TEST :: " + actionName + "<" + apiParse[2] + ">");
                                }
                                break;

                            case RECV_ACTION_VOICE_ALARM :
                                if(apiParse.length > 2 && apiParse[2] != null){
                                    i = new Intent(actionName);
                                    if(apiParse[2].equalsIgnoreCase("I")){
                                        i.putExtra("VID", "V0000000010");
                                        i.putExtra("GENDER_TYPE", "M");
                                        i.putExtra("AGE_GROUP", "20");
                                    }else if(apiParse[2].equalsIgnoreCase("U")){
                                        i.putExtra("PreVID", "V0000000010");
                                        i.putExtra("VID", "U0000000011");
                                        i.putExtra("GENDER_TYPE", "M");
                                        i.putExtra("AGE_GROUP", "20");
                                    }
                                    bCastReceiver.sendBroadcast(i, "BCAST-TEST :: " + actionName + "<" + apiParse[2] + ">");
                                }
                                break;

                            case RECV_ACTION_USER_CHANGE :
                                i.putExtra("userkey", "U0000000030");
                                i.setPackage("com.kt.gigagenie.pci");
                                bCastReceiver.sendBroadcast(i, "BCAST-TEST :: " + actionName);
                                break;

                            case RECV_ACTION_USER_DISCONNECT :
                                i.putExtra("userkey", "none");
                                bCastReceiver.sendBroadcast(i, "BCAST-TEST :: " + actionName);
                                break;
                            default:
                                break;
                        }
                    }
                }catch (Throwable e){
                    GLog.printExcept("DEBUG", "Failed to PCI Api Test", e);
                }
            }
        }).start();
    }




    @Override
    public void dispose() {
        layout_main = null;
        txtView_AlphaValue = null;
        txtView_VersionValue = null;
        btnStartService = null;
        btnStopService = null;
    }


    public RelativeLayout getLayout_main() { return layout_main; }
    public TextView getTxtView_AlphaValue() { return txtView_AlphaValue; }
    public TextView getTxtView_VersionValue() { return txtView_VersionValue; }
    public TextView getTxtView_LogScreen() { return txtView_LogScreen; }
    public Button getBtnStartService() { return btnStartService; }
    public Button getBtnStopService() { return btnStopService; }
    public Button getBtnClearLog() { return btnClearLog; }
    public CheckBox getChkAutoScroll() { return chkAutoScroll; }
    public Spinner getSpnrApiTest() { return spnrApiTest; }
    public Button getBtnApiTest() { return btnApiTest; }
    public Button getBtnRestart() { return btnRestart; }
    public Button getBtnClearDB() { return btnClearDB; }



    public String toString(){
        return LogHeader;
    }
}
