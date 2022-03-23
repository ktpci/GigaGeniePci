package com.kt.gigagenie.pci.system;

import android.content.ContextWrapper;
import android.content.res.Resources;

import com.gnifrix.debug.GLog;
import com.gnifrix.system.GniProperty;
import com.kt.gigagenie.pci.R;

/**
 * Created by LeeBaeng on 2018-09-04.
 */
public class PciProperty extends GniProperty {
    public static final int PCI_SND_TEST_USEPOLICY        = 0;
    public static final int PCI_SND_TEST_DOWNLOAD         = 1;
    public static final int PCI_SND_TEST_NONDOWNLOAD      = 2;


    /* Initial value Last Update : 2018/09/05 */
    private String appEnv                           = "live";
    private int appLogLevel                         = 4;
    private String appPlatform                      = "stb";
    private boolean app_debug_usePciDb              = true;
    private boolean pci_splitdownload_use           = true;
    private int pci_splitdownload_rnd_min           = 5;
    private int pci_splitdownload_rnd_max           = 30;
    private int pci_sndfile_ignorePolicy            = 0; // 0 : Normal , 1: Download, 2:Non-Download
    private boolean pci_sndfile_useAudibleTest      = false;
    private int pci_updatepolicy_split_min = 5;
    private int pci_updatepolicy_split_max = 30;

    private boolean pci_service_keep_alive          = true;
    private boolean pci_service_restart_test = false;
    private boolean use_boot_complete               = true;

    private String api_pci_url                  = "";
    private String api_pci_url_dev              = "https://pcibmt_paran_com:8000/";
    private String api_pci_url_bmt              = "https://pcibmt_ktipmedia_co_kr:8000/";
    private String api_pci_url_live             = "https://pcitv_ktipmedia_co_kr/";

    /** ↓↓↓↓↓  PCI AD 추가 - by dalkommJK | 2022-02-22 ↓↓↓↓↓ */
    private String api_pciad_url                  = "";
    private String api_pciad_url_dev              = "https://pcibmt_paran_com:8000/";
    private String api_pciad_url_bmt              = "https://pcibmt_ktipmedia_co_kr:8000/";
    private String api_pciad_url_live             = "https://pcitv_ktipmedia_co_kr/";


    // 임시 테스트용 1000번대 API
//    private String api_pci_uri_001              = "V100/PCI_1001/get_pci_key";
//    private String api_pci_uri_002              = "V100/PCI_1002/power_on_stb";
//    private String api_pci_uri_003              = "V100/PCI_1003/get_policy";
//    private String api_pci_uri_004              = "V100/PCI_1004/send_non_sound";
//    private String api_pci_uri_005              = "V100/PCI_1005/report_channel";
//    private String api_pci_uri_006              = "V100/PCI_1006/send_entry_information";
//    private String api_pci_uri_007              = "V100/PCI_1007/cant_play_sound";
//    private String api_pci_uri_008              = "V100/PCI_1008/send_smart_push";
//    private String api_pci_uri_009              = "V100/PCI_1009/temp";

    private String api_pci_uri_001              = "V100/PCI_6001/get_pci_key";
    private String api_pci_uri_002              = "V100/PCI_6002/stb_info";
    private String api_pci_uri_003              = "V100/PCI_6003/get_policy";
    private String api_pci_uri_004              = "V100/PCI_6004/send_non_sound";
    private String api_pci_uri_005              = "V100/PCI_6005/voice_info";
    private String api_pci_uri_006              = "V100/PCI_6006/check_push_result";
    private String api_pci_uri_007              = "V100/PCI_6007/stb_fail_info";
    private String api_pci_uri_008              = "V100/PCI_6008/checkin_list";
    private String api_pci_uri_009              = "V100/PCI_6009/stop_sound_play";
    private String api_pci_uri_6010              = "V100/PCI_6010/activation_token";  /** KPNS 추가 - by dalkommJK | 2019-09-30 **/

    /** ↓↓↓↓↓  PCI AD 추가 - by dalkommJK | 2022-02-22 ↓↓↓↓↓ */
    private String api_pciad_uri_1001              = "V100/PCIAD_1001/get_policy";
    private String api_pciad_uri_1002              = "V100/PCIAD_1002/check_in";
    private String api_pciad_uri_1003             = "V100/PCIAD_1003/check_out";
    private String api_pciad_uri_1004             = "V100/PCIAD_1004/check_in_list";
    private String api_pciad_uri_1005             = "V100/PCIAD_1005/regist_push_token";
    private String api_pciad_uri_1006             = "V100/PCIAD_1006/stb_fail_info";

    private String api_beacon_password          = "1da7bffb-988e-4662-84d3-ada7a69b1857";
    private String api_beacon_password_dev      = "1da7bffb-988e-4662-84d3-ada7a69b1857";
    private String api_beacon_password_bmt      = "1da7bffb-988e-4662-84d3-ada7a69b1857";
    private String api_beacon_password_live     = "1da7bffb-988e-4662-84d3-ada7a69b1857";
    private String api_beacon_url               = "https://dev-pci_soundl_ly/";
    private String api_beacon_url_dev           = "https://dev-pci_soundl_ly/";
    private String api_beacon_url_bmt           = "https://pcisound_ktipmedia_co_kr/";
    private String api_beacon_url_live          = "https://pcisound_ktipmedia_co_kr/";
    private String api_beacon_uri_0001          = "v1/beacon/highfreq/regist";

    private boolean useDebugMode                = false;
    private boolean useAutostartSvcOnDebugMode  = false;
    private int debugScreen_logLevel            = 0;
    private int debugScreen_Buffer              = 500;

    private boolean pci_updateloop_use          = true;
    private boolean pci_checkoutloop_use        = true;
    private boolean pci_updateinfo_split_use    = true;
    private int pci_updatetime                  = 4;
    private int pci_updateinfo_split_min        = 5;
    private int pci_updateinfo_split_max        = 1800;
    private int pci_update_time_idle            = -1;
    private int pci_update_time_expired         = -1;
    private int pci_update_wait_time            = -1;
    private int pci_update_stb_info             = -1;
    private int pci_checkin_list_wifi_out_hour  = 2;
    private int pci_checkin_list_wifi_out_time  = -1;
    private int pci_checkin_list_voice_out_time = -1;


    private String date_format_pcisrv           = "yyyyMMddHHmmss";
    private String date_format_pcisrv_checkin_list = "yyyyMMddHHmmssSSS";
    private String date_format_mc               = "yyyyMMddHHmmss";
    private String aes_key                      = "12345678901234567890123456789012";
    private boolean aes_encrypt_use             = true;




    public PciProperty(ContextWrapper context) {
        super(context);
        Resources resources = context.getResources();

        appEnv = resources.getString(R.string.app_env);
        appLogLevel = resources.getInteger(R.integer.app_loglevel);
        appPlatform = resources.getString(R.string.app_platform);
        app_debug_usePciDb = resources.getBoolean(R.bool.app_debug_usePciDb);
        pci_splitdownload_use = resources.getBoolean(R.bool.pci_splitdownload_use);
        pci_splitdownload_rnd_min = resources.getInteger(R.integer.pci_splitdownload_rndrange_min);
        pci_splitdownload_rnd_max = resources.getInteger(R.integer.pci_splitdownload_rndrange_max);
        pci_sndfile_ignorePolicy = resources.getInteger(R.integer.pci_sndfile_ignorePolicy);
        pci_sndfile_useAudibleTest = resources.getBoolean(R.bool.pci_sndfile_useAudibleTest);
        pci_updatepolicy_split_min = resources.getInteger(R.integer.pci_updateinfo_split_rndrange_min);
        pci_updatepolicy_split_max = resources.getInteger(R.integer.pci_updateinfo_split_rndrange_max);
        pci_service_keep_alive = resources.getBoolean(R.bool.pci_service_keep_alive);
        pci_service_restart_test = resources.getBoolean(R.bool.pci_service_restart_test);
        use_boot_complete = resources.getBoolean(R.bool.use_boot_complete);

        api_pci_url_dev = resources.getString(R.string.api_pci_url_dev);
        api_pci_url_bmt = resources.getString(R.string.api_pci_url_bmt);
        api_pci_url_live = resources.getString(R.string.api_pci_url_live);
        api_pci_uri_001 = resources.getString(R.string.api_pci_uri_001);
        api_pci_uri_002 = resources.getString(R.string.api_pci_uri_002);
        api_pci_uri_003 = resources.getString(R.string.api_pci_uri_003);
        api_pci_uri_004 = resources.getString(R.string.api_pci_uri_004);
        api_pci_uri_005 = resources.getString(R.string.api_pci_uri_005);
        api_pci_uri_006 = resources.getString(R.string.api_pci_uri_006);
        api_pci_uri_007 = resources.getString(R.string.api_pci_uri_007);
        api_pci_uri_008 = resources.getString(R.string.api_pci_uri_008);
        api_pci_uri_009 = resources.getString(R.string.api_pci_uri_009);
        api_pci_uri_6010 = resources.getString(R.string.api_pci_uri_6010);   /** KPNS 추가 - by dalkommJK | 2019-09-30 **/

        /** ↓↓↓↓↓  PCI AD 추가 - by dalkommJK | 2022-02-22 ↓↓↓↓↓ */
        api_pciad_url_dev = resources.getString(R.string.api_pciad_url_dev);
        api_pciad_url_bmt = resources.getString(R.string.api_pciad_url_bmt);
        api_pciad_url_live = resources.getString(R.string.api_pciad_url_live);
        api_pciad_uri_1001 = resources.getString(R.string.api_pciad_uri_1001);
        api_pciad_uri_1002 = resources.getString(R.string.api_pciad_uri_1002);
        api_pciad_uri_1003 = resources.getString(R.string.api_pciad_uri_1003);
        api_pciad_uri_1004 = resources.getString(R.string.api_pciad_uri_1004);
        api_pciad_uri_1005 = resources.getString(R.string.api_pciad_uri_1005);
        api_pciad_uri_1006 = resources.getString(R.string.api_pciad_uri_1006);

        /** ↓↓↓↓↓  KISA 보완조치 - by dalkommJK | 2019-10-31 ↓↓↓↓↓ */
        api_beacon_password = resources.getString(R.string.sound_pw);
        api_beacon_password_dev = resources.getString(R.string.sound_pw);
        api_beacon_password_bmt = resources.getString(R.string.sound_pw);
        api_beacon_password_live = resources.getString(R.string.sound_pw);
        /** ↑↑↑↑↑  KISA 보완조치 - by dalkommJK | 2019-10-31 ↑↑↑↑↑ */

        api_beacon_url = resources.getString(R.string.api_beacon_url_dev); /** KISA 보완조치 - by dalkommJK | 2019-10-31 **/
        api_beacon_url_dev = resources.getString(R.string.api_beacon_url_dev);
        api_beacon_url_bmt = resources.getString(R.string.api_beacon_url_bmt);
        api_beacon_url_live = resources.getString(R.string.api_beacon_url_live);
        api_beacon_uri_0001 = resources.getString(R.string.api_beacon_uri_0001);

        useDebugMode = resources.getBoolean(R.bool.app_debugmode_use);
        useAutostartSvcOnDebugMode = resources.getBoolean(R.bool.app_debugmode_autostart_service);
        debugScreen_logLevel = resources.getInteger(R.integer.app_debugmode_loglevel);
        debugScreen_Buffer = resources.getInteger(R.integer.app_debugmode_logBufferLines);

        pci_updateloop_use = resources.getBoolean(R.bool.pci_updateloop_use);
        pci_checkoutloop_use = resources.getBoolean(R.bool.pci_checkoutloop_use);
        pci_updateinfo_split_use = resources.getBoolean(R.bool.pci_updateinfo_split_use);
        pci_updatetime = resources.getInteger(R.integer.pci_updatetime);
        pci_updateinfo_split_min = resources.getInteger(R.integer.pci_updateinfo_split_rndrange_min);
        pci_updateinfo_split_max = resources.getInteger(R.integer.pci_updateinfo_split_rndrange_max);
        pci_update_time_idle = resources.getInteger(R.integer.pci_update_time_idle);
        pci_update_time_expired = resources.getInteger(R.integer.pci_update_time_expired);
        pci_update_wait_time = resources.getInteger(R.integer.pci_update_wait_time);
        pci_update_stb_info = resources.getInteger(R.integer.pci_update_stbinfo);
        pci_checkin_list_wifi_out_hour = resources.getInteger(R.integer.pci_checkin_list_wifi_out_hour);
        pci_checkin_list_wifi_out_time = resources.getInteger(R.integer.pci_checkin_list_wifi_out_time);
        pci_checkin_list_voice_out_time = resources.getInteger(R.integer.pci_checkin_list_voice_out_time);



        switch (appEnv){
            case "bmt" : setApiEnvToBmt(); break;
            case "dev" : setApiEnvToDev(); break;
            case "live" : setApiEnvToLive(); break;
            case "none" : setApiEnvToNone(); break;
            default: break;
        }

        date_format_pcisrv = resources.getString(R.string.date_format_pcisrv);
        date_format_pcisrv_checkin_list = resources.getString(R.string.date_format_pcisrv_checkin_list);
        date_format_mc = resources.getString(R.string.date_format_mc);

        aes_key = resources.getString(R.string.aes_key);
        aes_encrypt_use = resources.getBoolean(R.bool.aes_encrypt_use);

        GLog.setDebugScr_logLevel(getDebugScreen_logLevel());
        GLog.setApp_logLevel(getAppLogLevel());
    }


    public void setApiEnvToNone(){
        appEnv = "none";
        api_beacon_password = null;
        api_beacon_url = null;
        api_pci_url = null;
        GLog.printInfo(this, "Set to App Environment : NONE");
    }

    public void setApiEnvToDev(){
        appEnv = "dev";
        api_beacon_password = api_beacon_password_dev;
        api_beacon_url = api_beacon_url_dev;
        api_pci_url = api_pci_url_dev;
        GLog.printInfo(this, "Set to App Environment : DEV");
    }

    public void setApiEnvToBmt(){
        appEnv = "bmt";
        api_beacon_password = api_beacon_password_bmt;
        api_beacon_url = api_beacon_url_bmt;
        api_pci_url = api_pci_url_bmt;
        GLog.printInfo(this, "Set to App Environment : BMT");
    }

    public void setApiEnvToLive(){
        appEnv = "live";
        api_beacon_password = api_beacon_password_live;
        api_beacon_url = api_beacon_url_live;
        api_pci_url = api_pci_url_live;
        GLog.printInfo(this, "Set to App Environment : LIVE");
    }



    public void printInitString(){
        super.printInitString();

        GLog.printWtf(this, "┌" + getAppendedStr("─", "─") + "");
        GLog.printWtf(this, "│             " + getAppName());
        GLog.printWtf(this, "│Version : " + getVersionString()) ;
        GLog.printWtf(this, "│Local Version : " + getLocalVersion());
        GLog.printWtf(this, "│Environment : " + getAppEnv());
        GLog.printWtf(this, "│appPlatform : " + getAppPlatform());
        GLog.printWtf(this, "│Log Level : " + getAppLogLevel());
        GLog.printWtf(this, "│Activate Debug Mode : " + getUseDebugMode());
        GLog.printWtf(this, "│Debug Mode Level : " + getDebugScreen_logLevel());
        GLog.printInfo(this, "│Pci Svr : " + getApi_pci_url());
        GLog.printInfo(this, "│Snd Svr : " + getApi_beacon_url());
        GLog.printInfo(this, "│Activate Keep-Alive : " + getPci_service_keep_alive());
        GLog.printInfo(this, "│App Restart Test : " + getPci_service_restart_test());
        GLog.printInfo(this, "│Use PciDB : " + getApp_debug_usePciDb());
        GLog.printInfo(this, "│Use CheckOut Loop : " + getPci_checkoutloop_use());
        GLog.printInfo(this, "│Snd Test Mode(Ignore Policy) : " + getPci_sndfile_ignorePolicyString());
        GLog.printInfo(this, "│Snd Test Mode(Audible Sound) : " + getPci_sndfile_useAudibleTest());
        GLog.printInfo(this, "│Aes_encrypt_use : " + getAes_encrypt_use());

        if(getPci_update_time_idle() != -1)
            GLog.printInfo(this, "│Pci_update_time_idle(ignore policy) : " + getPci_update_time_idle());
        if(getPci_update_time_expired() != -1)
            GLog.printInfo(this, "│Pci_update_time_Expired(ignore policy) : " + getPci_update_time_expired());
        if(getPci_update_wait_time() != -1)
            GLog.printInfo(this, "│Pci_update_wait_time(ignore policy) : " + getPci_update_wait_time());
        if(getPci_update_stb_info() != -1)
            GLog.printInfo(this, "│Pci_update_stb_info(ignore policy) : " + getPci_update_stb_info());

        if(getPci_splitdownload_use())
            GLog.printInfo(this, "│Split Download Time(sec.) : " + getPci_splitdownload_rnd_min() + " ~ " + getPci_splitdownload_rnd_max());
        else
            GLog.printInfo(this, "│Split Download : inactivated");

        if(getPci_updateloop_use() && getPci_updateinfo_split_use())
            GLog.printInfo(this, "│Split Update(STB Info) Time(sec.) : " + getPci_updateinfo_split_min() + " ~ " + getPci_updateinfo_split_max());
        else
            GLog.printInfo(this, "│Split Update(STB Info) : inactivated");

        if(getPci_splitdownload_use())
            GLog.printInfo(this, "│Split Update(Policy) Time(sec.) : " + getPci_updatepolicy_split_min() + " ~ " + getPci_updatepolicy_split_max());
        else
            GLog.printInfo(this, "│Split Update(Policy) : inactivated");


        GLog.printWtf(this, "└" + getAppendedStr("─", "─"));
    }

//    public boolean getAes_encrypt_use() { return aes_encrypt_use; }
//    public String getPci_update_time_idle() { return pci_update_time_idle; }
//    public String getPci_update_time_expired() { return pci_update_time_expired; }
//    public int getPci_update_wait_time() { return pci_update_wait_time; }




    @Override
    public void dispose() {
        appEnv = null;
        appPlatform = null;

        api_pci_url_dev = null;
        api_pci_url_bmt = null;
        api_pci_url_live = null;
        api_pci_uri_001 = null;
        api_pci_uri_002 = null;
        api_pci_uri_003 = null;
        api_pci_uri_004 = null;
        api_pci_uri_005 = null;
        api_pci_uri_006 = null;
        api_pci_uri_007 = null;
        api_pci_uri_008 = null;
        api_pci_uri_009 = null;

        /** ↓↓↓↓↓  PCI AD 추가 - by dalkommJK | 2022-02-22 ↓↓↓↓↓ */
        api_pciad_url_dev = null;
        api_pciad_url_bmt = null;
        api_pciad_url_live = null;
        api_pciad_uri_1001 = null;
        api_pciad_uri_1002 = null;
        api_pciad_uri_1003 = null;
        api_pciad_uri_1004 = null;
        api_pciad_uri_1005 = null;
        api_pciad_uri_1006 = null;


        api_beacon_password_dev = null;
        api_beacon_password_bmt = null;
        api_beacon_password_live = null;
        api_beacon_url_dev = null;
        api_beacon_url_bmt = null;
        api_beacon_url_live = null;
        api_beacon_uri_0001 = null;

    }


    public String getPci_sndfile_ignorePolicyString() {
        switch (pci_sndfile_ignorePolicy){
            case PCI_SND_TEST_USEPOLICY : return "Normal(Reference to Policy)";
            case PCI_SND_TEST_DOWNLOAD : return "Download Test";
            case PCI_SND_TEST_NONDOWNLOAD : return "Non-Download Test";
            default: return "Unknown";
        }
    }


    public String getAppEnv() { return appEnv; }
    public int getAppLogLevel() { return appLogLevel; }
    public int getPci_splitdownload_rnd_min() { return pci_splitdownload_rnd_min; }
    public int getPci_splitdownload_rnd_max() { return pci_splitdownload_rnd_max; }
    public boolean getPci_splitdownload_use() { return pci_splitdownload_use; }
    public int getPci_sndfile_ignorePolicy() { return pci_sndfile_ignorePolicy; }
    public String getApi_pci_url_dev() { return api_pci_url_dev; }
    public String getApi_pci_url_bmt() { return api_pci_url_bmt; }
    public String getApi_pci_url_live() { return api_pci_url_live; }
    public String getApi_pci_uri_001() { return api_pci_uri_001; }
    public String getApi_pci_uri_002() { return api_pci_uri_002; }
    public String getApi_pci_uri_003() { return api_pci_uri_003; }
    public String getApi_pci_uri_004() { return api_pci_uri_004; }
    public String getApi_pci_uri_005() { return api_pci_uri_005; }
    public String getApi_pci_uri_006() { return api_pci_uri_006; }
    public String getApi_pci_uri_007() { return api_pci_uri_007; }
    public String getApi_pci_uri_008() { return api_pci_uri_008; }
    public String getApi_pci_uri_009() { return api_pci_uri_009; }
    public String getApi_pci_uri_6010() { return api_pci_uri_6010; }   /** KPNS 추가 - by dalkommJK | 2019-09-30 **/

    /** ↓↓↓↓↓  PCI AD 추가 - by dalkommJK | 2022-02-22 ↓↓↓↓↓ */

    public String getApi_pciad_url_dev() { return api_pciad_url_dev; }
    public String getApi_pciad_url_bmt() { return api_pciad_url_bmt; }
    public String getApi_pciad_url_live() { return api_pciad_url_live; }
    public String getApi_pciad_uri_1001() { return api_pciad_uri_1001; }
    public String getApi_pciad_uri_1002() { return api_pciad_uri_1002; }
    public String getApi_pciad_uri_1003() { return api_pciad_uri_1003; }
    public String getApi_pciad_uri_1004() { return api_pciad_uri_1004; }
    public String getApi_pciad_uri_1005() { return api_pciad_uri_1005; }
    public String getApi_pciad_uri_1006() { return api_pciad_uri_1006; }



    public String getApi_beacon_password_dev() { return api_beacon_password_dev; }
    public String getApi_beacon_password_bmt() { return api_beacon_password_bmt; }
    public String getApi_beacon_password_live() { return api_beacon_password_live; }
    public String getApi_beacon_url_dev() { return api_beacon_url_dev; }
    public String getApi_beacon_url_bmt() { return api_beacon_url_bmt; }
    public String getApi_beacon_url_live() { return api_beacon_url_live; }
    public String getApi_beacon_uri_0001() { return api_beacon_uri_0001; }
    public String getApi_pci_url() { return api_pci_url; }
    public String getApi_pciad_url() { return api_pciad_url; }  /** PCI AD 추가 - by dalkommJK | 2022-02-22 */
    public String getApi_beacon_password() { return api_beacon_password; }
    public String getApi_beacon_url() { return api_beacon_url; }
    public boolean getUseDebugMode() { return useDebugMode; }
    public boolean getUseAutostartSvcOnDebugMode() { return useAutostartSvcOnDebugMode; }
    public String getAppPlatform() { return appPlatform; }
    public int getDebugScreen_logLevel() { return debugScreen_logLevel; }
    public int getDebugScreen_Buffer() { return debugScreen_Buffer; }
    public boolean getPci_sndfile_useAudibleTest() { return pci_sndfile_useAudibleTest; }
    public boolean getPci_service_keep_alive() { return pci_service_keep_alive; }
    public boolean getPci_updateloop_use() { return pci_updateloop_use; }
    public boolean getPci_updateinfo_split_use() { return pci_updateinfo_split_use; }
    public int getPci_updatetime() { return pci_updatetime; }
    public int getPci_updateinfo_split_min() { return pci_updateinfo_split_min; }
    public int getPci_updateinfo_split_max() { return pci_updateinfo_split_max; }
    public int getPci_updatepolicy_split_min() { return pci_updatepolicy_split_min; }
    public int getPci_updatepolicy_split_max() { return pci_updatepolicy_split_max; }
    public String getDate_format_mc() { return date_format_mc; }
    public String getDate_format_pcisrv() { return date_format_pcisrv; }
    public String getDate_format_pcisrv_checkin_list() { return date_format_pcisrv_checkin_list; }
    public boolean getApp_debug_usePciDb() { return app_debug_usePciDb; }
    public boolean getPci_checkoutloop_use() { return pci_checkoutloop_use; }
    public boolean getPci_service_restart_test() { return pci_service_restart_test; }
    public String getAes_key() { return aes_key; }
    public boolean getAes_encrypt_use() { return aes_encrypt_use; }
    public int getPci_update_time_idle() { return pci_update_time_idle; }
    public int getPci_update_time_expired() { return pci_update_time_expired; }
    public int getPci_update_wait_time() { return pci_update_wait_time; }
    public int getPci_update_stb_info() { return pci_update_stb_info; }
    public int getPci_checkin_list_wifi_out_hour() { return pci_checkin_list_wifi_out_hour; }
    public int getPci_checkin_list_wifi_out_time() { return pci_checkin_list_wifi_out_time; }
    public int getPci_checkin_list_voice_out_time() { return pci_checkin_list_voice_out_time; }
    public boolean getUse_boot_complete() { return use_boot_complete; }




}
