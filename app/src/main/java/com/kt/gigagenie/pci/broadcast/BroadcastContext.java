package com.kt.gigagenie.pci.broadcast;

/**
 * Created by LeeBaeng on 2018-10-02.
 */

public interface BroadcastContext {
    String PKG_MC                           = "com.kt.gigagenie.mc";
    String PKG_LAUNCHER                     = "com.kt.gigagenie.launcher";
    String PKG_PCI                          = "com.kt.gigagenie.pci"; // "com.kt.gigagenie.pci"

    String SEND_ACTION_KEEPALIVE_RESP       = "kt.action.service.resp";
    String SEND_ACTION_PCILIST_RESP         = "kt.action.pci.resp";                 // PCI 리스트 요청응답 to MC or other Apps
    String SEND_ACTION_MCSTATE_CHECK        = "kt.action.state.req";
    String SEND_ACTION_PCI_SETTING_RESP     = "kt.action.launcher.pci.status.rep";  // 비가청 설정 요청응답 to Launcher
    String SEND_ACTION_POLICY_RESP          = "kt.action.launcher.pci.service.rep"; // PCI정책 조회 요청응답 to Launcher
    String SEND_ACTION_OPEN_POPUP           = "kt.action.launcher.commonpop.open";
    String SEND_ACTION_PASSIVE_ACK          = "kt.action.power.passive.ack";

    String RECV_ACTION_KEEPALIVE_CHECK      = "kt.action.service.check";
    String RECV_ACTION_MCSTATE_RESP         = "kt.action.state.resp";
    String RECV_ACTION_VOICE_ALARM          = "kt.action.voice.alarm";
    String RECV_ACTION_PCILIST_REQ          = "kt.action.pci.req";                  // PCI 리스트 요청 from MC
    String RECV_ACTION_PCI_SETTING_REQ      = "kt.action.pci.setting";              // 비가청 설정 요청 from Launcher
    String RECV_ACTION_PCI_POLICY_REQ       = "kt.action.pci.service";              // PCI정책 조회 요청 from Launcher
    String RECV_ACTION_USER_CHANGE          = "kt.action.launcher.mode.change";     // 사용자 계정 변경
    String RECV_ACTION_USER_DISCONNECT      = "kt.action.launcher.auth.init";       // 사용자 연결해제
    String RECV_ACTION_POWER_STANDBY        = "kt.action.power.standby";            // 대기모드 진입(for 대기모드 업데이트)
    String RECV_ACTION_POWER_NORMAL         = "kt.action.power.normal";             // 동작모드 진입(for 대기모드 업데이트 중단)
    String RECV_ACTION_POWER_PASSIVE        = "kt.action.power.passive";            // 저전력모드 진입(for 서비스 중단 or 미디어플레이어, 브로드캐스트, api호출, 업데이트 중단)


    String RECV_ACTION_PUSH_CHECKIN_LIST    = "kt.action.push.checkinlist";         // CheckIn 리스트 수신 from PushClient
    String RECV_ACTION_PUSH_ALARM           = "kt.action.push.alarm";               // 알림정보(팝업 등 ) from PushClient
    String RECV_ACTION_PUSH_SETTING         = "kt.action.push.setting";             // PCI App 설정 정보 from PushClient


    String RECV_SYS_ACTION_RESTART_SERVICE  = "pci.service.restart";
    String RECV_SYS_ACTION_BOOT_COMPLETE    = "android.intent.action.BOOT_COMPLETED"; // 부팅완료
    String RECV_SYS_ACTION_NET_STATE_CHANGE = "android.net.conn.CONNECTIVITY_CHANGE";
    String RECV_SYS_ACTION_RUN_PCI_SERVICE  = "kt.action.pci.service.start";

    /** ↓↓↓ KPNS 추가 -by dalkommjk | 2019-11-21 ↓↓↓ */
    String RECV_KPNS_REGISTRATION = "com.tta.push.intent.receive.REGISTRATION";
    String RECV_KPNS_STATUSOFSERVICE = "com.tta.push.intent.receive.STATUS_OF_SERVICE";
    String RECV_KPNS_STATUSOFMYPUSH = "com.tta.push.intent.receive.STATUS_OF_MY_PUSH";
    String RECV_KPNS_REREGISTER = "com.tta.push.intent.receive.RE_REGISTER";
    String RECV_KPNS_SERVICEAVAILABLE = "com.tta.push.intent.receive.SERVICE_AVAILABLE";
    String RECV_KPNS_SERVICEUNAVAILABLE = "com.tta.push.intent.receive.SERVICE_UNAVAILABLE";
    String RECV_KPNS_SERVICEAVAILBILTY = "com.tta.push.intent.receive.SERVICE_AVAILBILTY";
    String RECV_KPNS_PAMESSAGE = "com.ktpns.pa.MESSAGE";
    String RECV_KPNS_MESSAGE = "com.tta.push.intent.receive.MESSAGE";

    String RECV_KPNS_PA_REGISTRATION = "com.ktpns.pa.receive.REGISTRATION";
    String RECV_KPNS_PA_SERVICESTATUS = "com.ktpns.pa.receive.SERVICE_STATUS";
    String RECV_KPNS_PA_UNREGISTERED = "com.ktpns.pa.receive.UNREGISTERED";
    /** ↑↑↑ KPNS 추가 -by dalkommjk | 2019-11-21 ↑↑↑ */

    /** ↓↓↓ PCI AD by dalkommjk - v16.00.1 | 2022-03-23 ↓↓↓ */
    String RECV_ACTION_TVADID_REQ = "kt.action.ollehtv.adid.req";
    String RECV_ACTION_TVADID_RESP = "kt.action.ollehtv.adid.resp";

}


