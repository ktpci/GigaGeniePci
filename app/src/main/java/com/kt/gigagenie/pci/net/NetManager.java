package com.kt.gigagenie.pci.net;

import android.net.ConnectivityManager;
import android.net.Network;
import android.net.NetworkInfo;
import android.net.NetworkRequest;
import android.util.Log;

import com.gnifrix.debug.GLog;
import com.gnifrix.system.GRunnable;
import com.gnifrix.system.GThread;
import com.gnifrix.util.HsUtil_Date;
import com.gnifrix.util.HsUtil_Encrypter;
import com.kt.gigagenie.pci.Global;
import com.kt.gigagenie.pci.PciManager;
import com.kt.gigagenie.pci.Pci_Service;
import com.kt.gigagenie.pci.data.DataManager;
import com.kt.gigagenie.pci.data.PciSrv_PidData;
import com.kt.gigagenie.pci.data.PciSrv_PolicyData;
import com.kt.gigagenie.pci.data.PciSrv_VoiceChkInData;
import com.kt.gigagenie.pci.data.PidListManager;
import com.kt.gigagenie.pci.data.SndSvrData;
import com.kt.gigagenie.pci.data.pci_db.ErrorInfo;
import com.kt.gigagenie.pci.data.pci_db.PciDB;
import com.kt.gigagenie.pci.data.stb_db.StbDb_Common;
import com.kt.gigagenie.pci.data.stb_db.StbDb_Userproperty;
import com.kt.gigagenie.pci.net.connection.Connection;
import com.kt.gigagenie.pci.net.connection.InterruptedConnection;
import com.kt.gigagenie.pci.net.connection.RealConnection;
import com.kt.gigagenie.pci.net.json.JsonArray;
import com.kt.gigagenie.pci.net.json.JsonObject;
import com.kt.gigagenie.pci.net.json.pci_response.PciResp;
import com.kt.gigagenie.pci.net.json.pci_response.PciResp_CheckIn;
import com.kt.gigagenie.pci.net.json.pci_response.PciResp_Data;
import com.kt.gigagenie.pci.net.retry.Param6008;
import com.kt.gigagenie.pci.net.retry.RetryAPI;
import com.kt.gigagenie.pci.snd.NonAudiblePlayer;
import com.kt.gigagenie.pci.system.PciProperty;
import com.kt.gigagenie.pci.system.PciRuntimeException;

import java.io.UnsupportedEncodingException;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Properties;

import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;

import static android.os.SystemClock.sleep;
import static com.kt.gigagenie.pci.data.PciSrvData.kpns_token;
import static com.kt.gigagenie.pci.data.stb_db.StbDb_Common.COLUMN_NAME_DEVMACID;

/**
 * Created by LeeBaeng on 2018-09-17.
 */
public class NetManager {
    public static final String API_CODE_001 = "001";
    public static final String API_CODE_002 = "002";
    public static final String API_CODE_003 = "003";
    public static final String API_CODE_004 = "004";
    public static final String API_CODE_005 = "005";
    public static final String API_CODE_006 = "006";
    public static final String API_CODE_007 = "007";
    public static final String API_CODE_008 = "008";
    public static final String API_CODE_009 = "009";

    private static final String logHeader = "NetManager";

    private  PciManager pciManager;
    private Connection connection;
    private RealConnection realCon;
    private InterruptedConnection interruptedCon;
    private DataManager dataManager;
    private PciProperty pciProperty;

    private HashMap<String, RetryAPI> retryAPIList = null; // ???????????? ?????? ?????? ?????? ??? ????????? ?????????

    ConnectivityManager cm = null;
    ConnectivityManager.NetworkCallback networkCallback = null;


    public NetManager(PciManager _pciManager) throws NoSuchPaddingException, InvalidAlgorithmParameterException, UnsupportedEncodingException, IllegalBlockSizeException, BadPaddingException, NoSuchAlgorithmException, InvalidKeyException {
        realCon = new RealConnection();
        interruptedCon = new InterruptedConnection();
        connection = realCon;
        retryAPIList = new HashMap<>();
        pciManager = _pciManager;

        dataManager = _pciManager.getDataManager();
        pciProperty = _pciManager.getPciProperty();

        initStbInformation();
    }

    private void initStbInformation() throws NoSuchPaddingException, InvalidKeyException, UnsupportedEncodingException, IllegalBlockSizeException, BadPaddingException, NoSuchAlgorithmException, InvalidAlgorithmParameterException {
        // =================================================
//        dataManager.getStbData().setSaId("TT180523006");
//        dataManager.getStbData().setStbType("GIGA");
//        dataManager.getStbData().setModelNumber("CT1100");
//        dataManager.getStbData().setFirmwareVersion("15.0.1130"); // ????????? ?????? ???????????? ?????? ??????
//        dataManager.getStbData().setServiceType("OTV");  // OTV ???????
//        dataManager.getStbData().setDeviceId("d1234567890");
//        dataManager.getStbData().setMacAddress("1a2b3c4d5e6f");
        // =================================================

        String stbType = "STBType/" + dataManager.getStbData().getStbType() + ";";
        String stbModel = "STBModel/" + dataManager.getStbData().getModelNumber() + ";";
        String firmwareVer = "FirmwareVersion/" + dataManager.getStbData().getFirmwareVersion() + ";";
        String stbService = "STBService/" + dataManager.getStbData().getServiceType() + ";";
        //String mac = "MAC/" + dataManager.getStbData().getMacAddressAES128Base64();
        String mac = "MAC/" + dataManager.getStbData().getMacAddressAES128Base64() + ";"; // by dalkommjk | 15.00.04 | 2021-09-28
        String appVersion = "appVersion/" + dataManager.getStbData().getAppVersion(); // by dalkommjk | 15.00.04 | 2021-09-28


        StringBuffer usrAgntBuf = new StringBuffer();
        usrAgntBuf.append("PCITV(");
//        usrAgntBuf.append("Said/" + dataManager.getStbData().getSaId() + ";");
        usrAgntBuf.append(stbType);
        usrAgntBuf.append(stbModel);
        usrAgntBuf.append(firmwareVer);
        usrAgntBuf.append(stbService);
//        usrAgntBuf.append("DeviceId/" + dataManager.getStbData().getDeviceId() + ";");
        usrAgntBuf.append(mac);
        usrAgntBuf.append(appVersion); // by dalkommjk | 15.00.04 | 2021-09-28
        usrAgntBuf.append(")");
        String netHeader = usrAgntBuf.toString();
        dataManager.getPciSrvData().setNetHeader(netHeader);
        GLog.printInfo(this,"set User-Agent=" + netHeader);

        //PCITV(STBType/WEB;STBModel/IC1100;FirmwareVersion/15.0.1130;STBService/WEBDeviceId/WEBMAC/WEB)
        //PCITV(SAID/TT180523006;STBType/WEB;STBModel/IC1100;FirmwareVersion/15.0.1130;STBService/WEBDeviceId/WEBMAC/WEB)
        //PCITV(SAID/TT180523006;STBType/WEB;STBModel/IC1100;FirmwareVersion/15.0.1130;STBService/WEB;DeviceId/WEB;MAC/WEB)

        //PCITV(Said/TT161020041;STBType/ACAP;STBModel/NA1100;FirmwareVersion/5.1.1;STBService/OTV)
        //PCITV(SAID/TT180523006;STBType/WEB;STBModel/IC1100;FirmwareVersion/15.0.1130;STBService/OTV;DeviceId/d1234567890;MAC/1a2b3c4d5e6f)

        NetworkRequest.Builder builder = new NetworkRequest.Builder();
        cm = (ConnectivityManager) Global.getInstance().getPciService().getSystemService( Global.getInstance().getPciService().CONNECTIVITY_SERVICE );
        networkCallback = new ConnectivityManager.NetworkCallback(){
            @Override
            public void onAvailable( Network network )
            {
                // ??????????????? ?????? ??????????????? ????????? ?????? retryApi ?????? (PCI Service??? ?????? ???????????? ???)
                if(Global.isInitialized() && PciManager.isInitialized() && Pci_Service.pciReady){
                    GLog.printWtf("PCI|NetCallback", "Network State changed. network on available received.");
                    runRetryAPI();
                }
            }
        };
        cm.registerNetworkCallback(builder.build(), networkCallback);
    }

    public void destroy() {
        if(cm != null){
            cm.unregisterNetworkCallback(networkCallback);
            networkCallback = null;
            cm = null;
        }

        if(retryAPIList != null) retryAPIList.clear();

        retryAPIList = null;
        pciManager = null;
        connection = null;
        realCon = null;
        interruptedCon = null;
        dataManager = null;
        pciProperty = null;
    }

    public void interrupt() {
        connection = interruptedCon;
        GLog.printInfo(this, "Net Manager interrupted....");
    }

    private JsonObject httpGetPci(final String tag, final String url, String userAgent) throws NetException, InterruptedException {
        final Properties header = new Properties();
        header.setProperty("User-Agent", userAgent);
        return connection.httpGet(tag, url, header);
    }

    private JsonObject httpPostPci(String tag, String url, String userAgent, JsonObject reqParam) throws NetException, InterruptedException {
        Properties header = new Properties();
        header.setProperty("User-Agent", userAgent);
        return connection.httpPost(tag, url, header, reqParam, false);
    }

    private JsonObject httpPostBeacon(String tag,String url, JsonObject reqParam) throws NetException, InterruptedException {
        return connection.httpPost(tag, url, null, reqParam, true);
    }




    public void updatePolicy() {
        boolean getPolicy_result = apiPci6003_getPolicy(); // ???????????? ?????? ??? retry ?????? ?????? by dalkommjk | 2021-01-28
        int retry_cnt = 0;                                 // ???????????? ?????? ??? retry ?????? ?????? by dalkommjk | 2021-01-28
        while(getPolicy_result != true && retry_cnt < 5) { // ???????????? ?????? ??? retry ?????? ?????? by dalkommjk | 2021-01-28

            getPolicy_result = apiPci6003_getPolicy();     // ???????????? ?????? ??? retry ?????? ?????? by dalkommjk | 2021-01-28
            retry_cnt++;                                   // ???????????? ?????? ??? retry ?????? ?????? by dalkommjk | 2021-01-28
        }                                                  // ???????????? ?????? ??? retry ?????? ?????? by dalkommjk | 2021-01-28

        PciSrv_PolicyData _getPolicy = dataManager.getPciSrvData().getPciSrvPolicyData();
        //NonAudiblePlayer nonAudiblePlayer = pciManager.getNonAudiblePlayer(); // sound delete by dalkommJK | 2019-10-10

        if (_getPolicy != null) {
            try{
                GLog.printInfo(this, "IsIgnorePolicyForDownloadSndFile > " + pciProperty.getPci_sndfile_ignorePolicy());
                _getPolicy.printCurrentPolicy();
                //GLog.printInfo(this, "_soundFile.isExists() > " + nonAudiblePlayer.isExists()); // sound delete by dalkommJK | 2019-10-10
            }catch (Exception e) {
                GLog.printExcept(this, "read policy data error", e);
            }
            /** ??????????????? sound delete by dalkommJK | 2019-10-10  ??????????????? */
            /**
            // Non-Download ????????? ??? download State??? ????????? complete??? ??????(????????? ??????. ????????? ?????? ?????? ?????? ?????? ???. Non-Download ???????????? ????????? ????????? ???????????? ??? ????????? ??? ????????? ???)
            if(pciProperty.getPci_sndfile_ignorePolicy() == PciProperty.PCI_SND_TEST_NONDOWNLOAD){
                nonAudiblePlayer.setDownloadState(NonAudiblePlayer.DOWNLOAD_STATE_COMPLETE);
                return;
            }
            if (pciProperty.getPci_sndfile_ignorePolicy() == PciProperty.PCI_SND_TEST_DOWNLOAD || ( _getPolicy.getContinuousPlayYn().equals("Y") && (_getPolicy.getSoundUpdateYn() || !nonAudiblePlayer.isExists()))) {
                nonAudiblePlayer.setDownloadState(NonAudiblePlayer.DOWNLOAD_STATE_READY);
                GLog.printInfo(this,"try regist to beacon server.");

             **** ????????? ?????? ???????????? ??????
                apiBeacon0001_regist();   // ??????????????? ????????????

                SndSvrData sndSvrData = dataManager.getSndSvrData();
                if (sndSvrData != null) {
                    String url = sndSvrData.getFileUrl();
                    if (url != null) {
                        if (!url.startsWith("http")) {
                            if (url.startsWith("/")) {
                                url = url.substring(1);
                            }
                            url = new StringBuffer(new String(pciProperty.getApi_beacon_url() + url)).toString();
                        }
                        GLog.printInfo(this, "\n<beacon.regist.fileUrl> " + url + "\n");
                        nonAudiblePlayer.update(url);
                    } else {
                        GLog.printInfo(this, "download sound file fail: url=null");
                    }
                    apiPci6004_sendNonSound();
                }else{
                    GLog.printWarn(this, "Failed to regist beacon server. sndSvrData is null");
                }

            }
             */
        /** ?????????????????? sound delete by dalkommJK | 2019-10-10  ??????????????? */
        }
    }




    /**
     * API 6001 STB key ??????
     */
    public String apiPci6001_getPciKey() throws NetException, InterruptedException {
        String stbId = dataManager.getPciSrvData().getStbId();

        String apiName = "apiPci6001_getPciKey";
        if(pciProperty.getAppEnv().equals("none")){
            GLog.printError(apiName, "Can't call API : Property.appEnv is none");
            return stbId;
        }

        String prevSaId = dataManager.getStbData().getSaId();
        dataManager.getStbDb().refreshDbInfo(Global.getInstance().getPciService());
        String curSaId = dataManager.getStbData().getSaId();

        boolean saidChanged = false;

        if(prevSaId == null && curSaId != null) saidChanged = true;
        else if(curSaId == null && prevSaId != null) saidChanged = true;
        else if(prevSaId != null && curSaId != null){
            saidChanged = !prevSaId.equals(curSaId);
        }

        GLog.printInfo(this, " * Check SAID * prevSaId=" + prevSaId + " // curSaId=" + curSaId + "// check result="+saidChanged);

        if (stbId == null || saidChanged) {
            String reqUrl = pciProperty.getApi_pci_url() + pciProperty.getApi_pci_uri_001();

            StbDb_Common stbDb_common = dataManager.getStbDb().getStbDbCommon();
            if(stbDb_common == null && Global.getInstance().getPciService() != null){
                dataManager.getStbDb().refreshDbInfo(Global.getInstance().getPciService());
            }

            if(stbDb_common != null){
                JsonObject param = new JsonObject();
                // SAID ??????x / 2019.01.28(????????????) ??? SAID ?????? / 2019.02.07
                if(stbDb_common.getSaid() != null && !stbDb_common.getSaid().trim().equals("") && !stbDb_common.getSaid().equals("null")) param.put("said", stbDb_common.getSaid());
                else param.put("said", "NONE");

                JsonObject result = httpPostPci(apiName, reqUrl, dataManager.getPciSrvData().getNetHeader(), param);

                GLog.printInfo(apiName, " ***** getPciKey Result > " + result.toJsonStringPretty());
                PciResp_Data pciRespData = new PciResp_Data(result);
                if(pciRespData.isSuccessed()){
                    stbId = pciRespData.getData().getString("stb_id");
                    GLog.printInfo(apiName, "***** stb_id :: " + stbId);
                    dataManager.getPciSrvData().setStbId(stbId);
                }else{
                    recordFailOnPciSrv(apiName, pciRespData);
                }
            }

        }
        return stbId;
    }


    public void apiPci6002_sendStbInfo(boolean reloadFromSTB) {
        apiPci6002_sendStbInfo(reloadFromSTB, null, false);
    }

    /**
     * API 6002 STB ?????? ??????
     * @param reloadFromSTB STB DB??? ?????? ?????? ???????????? ??????(false : ?????????, ????????? ????????? ?????? ??????(?????? ????????? ????????? ????????? ????????? param??? ???????????? ????????? ??????))
     * @param upList default == null, null ??? ????????? STB-DB??? ?????? ????????? ????????? ?????? (null??? ???????????? Test??????)
     * @param nonTest default == false / true?????? ???, STB-DB??? ?????? User Property ????????? ?????? ??????. (true??? ?????? Test??????)
     */
    public void apiPci6002_sendStbInfo(boolean reloadFromSTB, ArrayList<StbDb_Userproperty> upList, boolean nonTest) {
        String reqUrl = pciProperty.getApi_pci_url() + pciProperty.getApi_pci_uri_002();
        String apiName = "apiPci6002_sendStbInfo";

        if(pciProperty.getAppEnv().equals("none")){
            GLog.printError(apiName, "Can't call API : Property.appEnv is none");
            return;
        }

        try {
            StbDb_Common stbDb_common = dataManager.getStbDb().getStbDbCommon();

            if(stbDb_common == null || (reloadFromSTB && Global.getInstance().getPciService() != null)){
                dataManager.getStbDb().refreshDbInfo(Global.getInstance().getPciService());
            }

            if(stbDb_common != null){
                JsonObject param = new JsonObject();
                param.put("stb_id", apiPci6001_getPciKey());
                param.put("device_id", stbDb_common.getDevServiceId());
                param.put("reg_time", HsUtil_Date.getCurrentTimeStr(pciProperty.getDate_format_pcisrv()));

                // SAID :::: ????????? ??????(2019_01_22) >> ??????, ??????x(2018_01_23 KT????????? ??????) >> ??????????????? (2019_01_28) KT ????????? ??????
//                if(stbDb_common.getSaid() != null && !stbDb_common.getSaid().trim().equals("") && !stbDb_common.getSaid().equals("null")) param.put("said", stbDb_common.getSaid());

                // ?????? X : UserKey List ??? ?????? (2019.02.07) (????????? ???????????? ????????? MAC??????(":" ????????? ??????)??? ?????? ????????? ??????)
                JsonArray jArray = new JsonArray();

                if(upList == null && !nonTest) upList = dataManager.getStbDb().getListUserproperty();
                if(upList != null && upList.size() > 0){
                    boolean non_pairing = false;
                    for(StbDb_Userproperty updb : upList){
                        String userKey = updb.getUserkey();
                        if(userKey != null){
                            if(userKey.contains(":")){
                                non_pairing = true;
                                break;
                            }else{
                                jArray.add(updb.getUserkey());
                            }
                        }else{
                            non_pairing = true;
                        }
                    }

                    if(non_pairing) param.put("userkey_list", new JsonArray());
                    else param.put("userkey_list", jArray);
                }else{
                    param.put("userkey_list", jArray);
                }


                PciResp pciResp = new PciResp(httpPostPci(apiName, reqUrl, dataManager.getPciSrvData().getNetHeader(), param));

                if(!pciResp.isSuccessed()){
                    recordFailOnPciSrv(apiName, pciResp);
                }
            }
        } catch (NetException e) {
            //GLog.printExceptWithSaveToPciDB(this,apiName + "| request " + reqUrl + " fail: " + e.getMessage(), e, ErrorInfo.CODE_PCI_PLATFORM_NETWORK_ERR, ErrorInfo.CATEGORY_PCIPLATFORM);
        } catch (InterruptedException e) {
            //GLog.printExceptWithSaveToPciDB(this,apiName + "| request " + reqUrl + " interrupted", e, ErrorInfo.CODE_PCI_PLATFORM_NETWORK_ERR, ErrorInfo.CATEGORY_PCIPLATFORM);
        } catch (Exception e) {
            //GLog.printExceptWithSaveToPciDB(this,apiName + "| request " + reqUrl + " fail(Unhandled Exception): " + e.getMessage(), e, ErrorInfo.CODE_ETC_UNKNOWN, ErrorInfo.CATEGORY_PCIPLATFORM);
        }
    }


    /**
     * API 6003 STB ?????? ??????
     */
    public boolean apiPci6003_getPolicy() {
        String apiName = "apiPci6003_getPolicy";
        String reqUrl = pciProperty.getApi_pci_url() + pciProperty.getApi_pci_uri_003();

        if(pciProperty.getAppEnv().equals("none")){
            GLog.printError(apiName, "Can't call API : Property.appEnv is none");
            return false;
        }

        try {
            String param = "stb_id=" + apiPci6001_getPciKey();

            PciResp_Data pciResp = new PciResp_Data(httpGetPci(apiName, reqUrl + "?" + param, dataManager.getPciSrvData().getNetHeader()));

            if(pciResp.isSuccessed()){
                PciSrv_PolicyData getPolicy = new PciSrv_PolicyData(pciResp.getData());
                if (dataManager.getPciSrvData().getPciSrvPolicyData() != null) {
                    dataManager.getPciSrvData().getPciSrvPolicyData().destroy();
                }
                dataManager.getPciSrvData().setPciSrvPolicyData(getPolicy);
                return true;
            }else{
                recordFailOnPciSrv(apiName, pciResp);
                return false;
            }
        } catch (NetException e) {
            //GLog.printExceptWithSaveToPciDB(this,apiName + "| request " + reqUrl + " fail: " + e.getMessage(), e, ErrorInfo.CODE_PCI_PLATFORM_NETWORK_ERR, ErrorInfo.CATEGORY_PCIPLATFORM);

        } catch (InterruptedException e) {
            //GLog.printExceptWithSaveToPciDB(this,apiName + "| request " + reqUrl + " interrupted", e, ErrorInfo.CODE_PCI_PLATFORM_NETWORK_ERR, ErrorInfo.CATEGORY_PCIPLATFORM);
        } catch (Exception e) {
            //GLog.printExceptWithSaveToPciDB(this,apiName + "| request " + reqUrl + " fail(Unhandled Exception): " + e.getMessage(), e, ErrorInfo.CODE_ETC_UNKNOWN, ErrorInfo.CATEGORY_PCIPLATFORM);
        }

        return false;
    }



    /**
     * 2019-04-17 Sound ID ?????? BLE Advertise ?????? ??????????????? ?????? by JK 2019-04-19
     * API 6004 ????????? ?????? ?????? ??????
     * ????????? ????????? ????????? ?????? ????????? ?????? SAID ??? ?????????????????? ????????????.
     */
    public void apiPci6004_sendNonSound() {
        String reqUrl = pciProperty.getApi_pci_url() + pciProperty.getApi_pci_uri_004();
//        SndSvrData _regist = dataManager.getSndSvrData();

        String apiName = "apiPci6004_sendNonSound";
        //String soundId = string2hex(COLUMN_NAME_DEVMACID);
        //String soundId =  dataManager.getStbDb().getStbDbCommon().getDevMacId();
        String soundId =  dataManager.getPciSrvData().getStbId();
        //soundId = string2hex(soundId);

        if(pciProperty.getAppEnv().equals("none")){
            GLog.printError(apiName, "Can't call API : Property.appEnv is none");
            return;
        }

        try {
            JsonObject param = new JsonObject();
            param.put("stb_id", apiPci6001_getPciKey());
            param.put("sound_id", soundId);

            if (soundId != null) {
//                param.put("sound_id", soundId);

                PciResp pciResp = new PciResp(httpPostPci(apiName, reqUrl, dataManager.getPciSrvData().getNetHeader(), param));
                if(!pciResp.isSuccessed()){
                    recordFailOnPciSrv(apiName, pciResp);
                }
            } else {
                //GLog.printExceptWithSaveToPciDB(this, apiName + "| failed.", new PciRuntimeException(apiName + " fail : sound_id=null"), ErrorInfo.CODE_ETC_UNKNOWN, ErrorInfo.CATEGORY_PCIPLATFORM);
            }
        } catch (NetException e) {
            //GLog.printExceptWithSaveToPciDB(this,apiName + "| request " + reqUrl + " fail: " + e.getMessage(), e, ErrorInfo.CODE_PCI_PLATFORM_NETWORK_ERR, ErrorInfo.CATEGORY_PCIPLATFORM);
        } catch (InterruptedException e) {
            //GLog.printExceptWithSaveToPciDB(this, apiName + "| request " + reqUrl + " interrupted", e, ErrorInfo.CODE_PCI_PLATFORM_NETWORK_ERR, ErrorInfo.CATEGORY_PCIPLATFORM);
        } catch (Exception e) {
            //GLog.printExceptWithSaveToPciDB(this,apiName + "| request " + reqUrl + " fail(Unhandled Exception): " + e.getMessage(), e, ErrorInfo.CODE_ETC_UNKNOWN, ErrorInfo.CATEGORY_PCIPLATFORM);
        }
    }


    /**
     * 2019-04-17 Soundly ???????????? ?????? ?????? ?????? - JK
     * API 6004 ????????? ?????? ?????? ??????
     * ????????? ????????? ????????? ?????? ????????? ?????? SAID ??? ?????????????????? ????????????.
     */
    /*
    public void apiPci6004_sendNonSound() {
        String reqUrl = pciProperty.getApi_pci_url() + pciProperty.getApi_pci_uri_004();
        SndSvrData _regist = dataManager.getSndSvrData();
        String apiName = "apiPci6004_sendNonSound";

        if(pciProperty.getAppEnv().equals("none")){
            GLog.printError(apiName, "Can't call API : Property.appEnv is none");
            return;
        }

        try {
            JsonObject param = new JsonObject();
            param.put("stb_id", apiPci6001_getPciKey());
            if (_regist == null) {
                GLog.printInfo(apiName, "_regist is null. try to regist to beacon server.");
                if(!apiBeacon0001_regist()) return;
            }
            _regist = dataManager.getSndSvrData();
            String soundId = (_regist != null)? _regist.getSoundId() : null;    // soundID ?????? ??????
            if (soundId != null) {
//                param.put("sound_id", soundId);
                param.put("sound_id", "1234567890abcdefghijklmnopqrstuvwxyz1234");
                PciResp pciResp = new PciResp(httpPostPci(apiName, reqUrl, dataManager.getPciSrvData().getNetHeader(), param));
                if(!pciResp.isSuccessed()){
                    recordFailOnPciSrv(apiName, pciResp);
                }
            } else {
                GLog.printExceptWithSaveToPciDB(this, apiName + "| failed.", new PciRuntimeException(apiName + " fail : sound_id=null"), ErrorInfo.CODE_ETC_UNKNOWN, ErrorInfo.CATEGORY_PCIPLATFORM);
            }
        } catch (NetException e) {
            GLog.printExceptWithSaveToPciDB(this,apiName + "| request " + reqUrl + " fail: " + e.getMessage(), e, ErrorInfo.CODE_PCI_PLATFORM_NETWORK_ERR, ErrorInfo.CATEGORY_PCIPLATFORM);
        } catch (InterruptedException e) {
            GLog.printExceptWithSaveToPciDB(this, apiName + "| request " + reqUrl + " interrupted", e, ErrorInfo.CODE_PCI_PLATFORM_NETWORK_ERR, ErrorInfo.CATEGORY_PCIPLATFORM);
        } catch (Exception e) {
            GLog.printExceptWithSaveToPciDB(this,apiName + "| request " + reqUrl + " fail(Unhandled Exception): " + e.getMessage(), e, ErrorInfo.CODE_ETC_UNKNOWN, ErrorInfo.CATEGORY_PCIPLATFORM);
        }
    }
    */


    /**
     * API 6005 ???????????? ?????????/??????/?????????????????? (PciSrv_VoiceChkInData??? action?????? ??????)
     */
    public PciSrv_PidData apiPci6005_VoiceAction(PciSrv_VoiceChkInData pciSrvVoiceChkInData) {
        String reqUrl = pciProperty.getApi_pci_url() + pciProperty.getApi_pci_uri_005();
        String apiName = "apiPci6005_VoiceAction";

        if(pciProperty.getAppEnv().equals("none")){
            GLog.printError(apiName, "Can't call API : Property.appEnv is none");
            return null;
        }

        try {
            String userKey = pciSrvVoiceChkInData.getUserKey();
            String voice_id = pciSrvVoiceChkInData.getVoiceId();
            String action = pciSrvVoiceChkInData.getAction();
            String eventTime = pciSrvVoiceChkInData.getEventTime();
            String gender = pciSrvVoiceChkInData.getGender();
            String age_group = pciSrvVoiceChkInData.getAge_group();

            JsonObject param = new JsonObject();

            if(voice_id == null || voice_id.trim().equals("")){
                param.put("voice_id", userKey);
                param.put("userkey", userKey);
            }else if(userKey == null || userKey.trim().equals("")){
                param.put("voice_id", voice_id);
            }else{
                // voice_id, userKey ?????? ????????????
                param.put("voice_id", voice_id);
                param.put("userkey", userKey);
            }

            param.put("action_type", action);
            param.put("reg_date", eventTime);

            if(gender != null) param.put("gender", gender);
            if(age_group != null) param.put("age", age_group);

            PciResp_Data pciRespData = new PciResp_Data(httpPostPci(apiName, reqUrl, dataManager.getPciSrvData().getNetHeader(), param));

            if(pciRespData.isSuccessed()){
                PidListManager pidMgr = dataManager.getPidManager();

                JsonObject data = pciRespData.getData();
                String pid = data.getString("p_id");
                String newGender = data.getString("gender");
                String newAgeGroup = data.getString("age");

                // ?????????????????? ?????? ????????? ID??? ???????????? PID??? ????????? ?????? ?????? PID???????????? PID??? ??????
                if(voice_id != null){
                    PciSrv_PidData prevPidData = pidMgr.getPidDataByVoiceID(voice_id);
                    if(prevPidData != null && prevPidData.getPid() != null && !prevPidData.getPid().equals(pid)){
                        prevPidData.setPid(pid);
                    }
                }

                // ????????? ??????????????? ?????? Pid??? ???????????? PidData??? ????????? ???????????? ?????? ?????? ?????? ?????? ??? ??????
                PciSrv_PidData pidData = pidMgr.getPidDataByPID(pid);
                if(pidData == null){
                    pidData = new PciSrv_PidData(pid, userKey, null, eventTime, null, null);
                    pidMgr.getCheckedInList().put(pid, pidData);
                }

                // PID???????????? userKey, VoiceId, Gender, AgeGroup ?????? ??????
                if(userKey != null && !userKey.trim().equals("")) pidData.setUserKey(userKey);
                if(voice_id != null && !voice_id.trim().equals("")) pidData.setVoiceId(voice_id);
                if(newGender != null && !newGender.trim().equals("")) pidData.setSrv_gender(newGender);
                if(newAgeGroup != null && !newAgeGroup.trim().equals("")) pidData.setSrv_ageGroup(newAgeGroup);

                GLog.printInfo(apiName, "VoiceCheckIn > ???????????? PID Data");
                pidData.printPidData();

                // ?????????, ???????????? ??? ????????? ?????? ??????
                if(action.trim().equalsIgnoreCase("I") || action.trim().equalsIgnoreCase("U")){
                    pidData.getCheckInData_v().setCheckInDateStr(eventTime);
                }

                return pidData;
            }else{
                recordFailOnPciSrv(apiName, pciRespData);
            }
        } catch (NetException e) {
            GLog.printExcept(apiName, apiName + "| request " + reqUrl + " fail: " + e.getMessage(), e);
        } catch (InterruptedException e) {
            //GLog.printWarn(apiName, apiName + "| request " + reqUrl + " interrupted");
        } catch (Exception e) {
            //GLog.printExceptWithSaveToPciDB(this,apiName + "| request " + reqUrl + " fail(Unhandled Exception): " + e.getMessage(), e, ErrorInfo.CODE_ETC_UNKNOWN, ErrorInfo.CATEGORY_PCIPLATFORM);
        }

        return null;
    }



    /**
     * API 6006 UserKey ?????? ??????
     */
    public void apiPci6006_UpdateUserKey(String userKey){
        String reqUrl = pciProperty.getApi_pci_url() + pciProperty.getApi_pci_uri_006();
        String apiName = "apiPci6006_UpdateUserKey";

        if(pciProperty.getAppEnv().equals("none")){
            GLog.printError(apiName, "Can't call API : Property.appEnv is none");
            return;
        }

        try {
            if(userKey == null)
                userKey = dataManager.getStbDb().getStbDbCommon().getUserKey();

            if(userKey == null){
                GLog.printExceptWithSaveToPciDB(apiName, "Can't call API : Property.appEnv is none", new PciRuntimeException("can't send updateUserKey. userkey is null"),ErrorInfo.CODE_PCI_PLATFORM_NETWORK_ERR, ErrorInfo.CATEGORY_INAPP);
                return;
            }

            if(userKey.contains(":")) userKey = "none";

            JsonObject param = new JsonObject();
            param.put("stb_id", apiPci6001_getPciKey());
            param.put("userkey", userKey);
            PciResp pciResp = new PciResp(httpPostPci(apiName, reqUrl, dataManager.getPciSrvData().getNetHeader(), param));

            if(!pciResp.isSuccessed()){
                recordFailOnPciSrv(apiName, pciResp);
            }
        } catch (NetException e) {
            GLog.printExceptWithSaveToPciDB(apiName,apiName + "| request " + reqUrl + " fail: " + e.getMessage(), e, ErrorInfo.CODE_PCI_PLATFORM_NETWORK_ERR, ErrorInfo.CATEGORY_PCIPLATFORM);
        } catch (InterruptedException e) {
            GLog.printExceptWithSaveToPciDB(apiName,apiName + "| request " + reqUrl + " interrupted", e, ErrorInfo.CODE_PCI_PLATFORM_NETWORK_ERR, ErrorInfo.CATEGORY_PCIPLATFORM);
        } catch (Exception e) {
            //GLog.printExceptWithSaveToPciDB(this,apiName + "| request " + reqUrl + " fail(Unhandled Exception): " + e.getMessage(), e, ErrorInfo.CODE_ETC_UNKNOWN, ErrorInfo.CATEGORY_PCIPLATFORM);
        }
    }

//    /**
//     * API 6006 Push???????????? ?????? ?????? -> 2019/01/11 ????????????
//     */
//    public void apiPci_GPushRecvResult(){
//        String reqUrl = pciProperty.getApi_pci_url() + pciProperty.getApi_pci_uri_006();
//        String apiName = "apiPci_GPushRecvResult : API_6006";
//
//        if(pciProperty.getAppEnv().equals("none")){
//            GLog.printError(apiName, "Can't call API : Property.appEnv is none");
//            return;
//        }
//
//        try {
//            JsonObject param = new JsonObject();
//            PciResp pciResp = new PciResp(httpPostPci(apiName, reqUrl, dataManager.getPciSrvData().getNetHeader(), param));
//
//            if(!pciResp.isSuccessed()){
//                recordFailOnPciSrv(apiName, pciResp);
//            }
//        } catch (NetException e) {
//            GLog.printExcept(this, "request " + reqUrl + " fail: " + e.getMessage(), e);
//        } catch (InterruptedException e) {
//            GLog.printWarn(this, "request " + reqUrl + " interrupted");
//        }
//    }



    // ????????? ?????? ????????? ?????? -> 2018/10/23 ????????????(????????? ????????? STB?????? ??????????????? ????????? / ??????????????? ?????????)
//    /**
//     * API 6007 ??????????????? ????????? ?????? ??????
//     * ???????????? ????????? ???????????? ?????????????????? ???????????? ?????? ??????, ?????? ????????? ?????? ?????????.
//     */
//    public void apiPci_cantPlaySound() {
//        String reqUrl = pciProperty.getApi_pci_url() + pciProperty.getApi_pci_uri_007();
//        try {
//            JsonObject param = new JsonObject();
//            param.put("stb_id", apiPci6001_getPciKey());
//            httpPostPci("apiPci_cantPlaySound", reqUrl, dataManager.getPciSrvData().getNetHeader(), param);
//        } catch (NetException e) {
//            GLog.printExceptWithSaveToPciDB(this, "request " + reqUrl + " fail: " + e.getMessage(), e, ErrorInfo.CODE_PCI_PLATFORM_NETWORK_ERR, ErrorInfo.CATEGORY_PCIPLATFORM);
//        } catch (InterruptedException e) {
//            GLog.printExceptWithSaveToPciDB(this, "request " + reqUrl + " interrupted.", e, ErrorInfo.CODE_PCI_PLATFORM_NETWORK_ERR, ErrorInfo.CATEGORY_PCIPLATFORM);
//        }
//    }



    /**
     * API 6007 ???????????? ??????
     */
    public void apiPci6007_sendErrorInfo() {
        String reqUrl = pciProperty.getApi_pci_url() + pciProperty.getApi_pci_uri_007();
        String apiName = "apiPci6007_sendErrorInfo";

        if(pciProperty.getAppEnv().equals("none")){
            GLog.printError(apiName, "Can't call API : Property.appEnv is none");
            return;
        }

        try {
            if(Global.getInstance().getPciDB() == null){
                GLog.printWarn(this, "<" + apiName + "> failed. pciDb instance is null !! ");
                return;
            }

            PciDB pciDB = Global.getInstance().getPciDB();
            int errCount = pciDB.getErrorInfoCountOnDB();
            if(errCount > 0){
                ArrayList<ErrorInfo> pciErrorList = pciDB.getErrorListFromPciLocalDB();

                if(pciErrorList != null && pciErrorList.size() > 0){
                    JsonArray jArray = new JsonArray();
                    JsonObject errItem;

                    for(ErrorInfo errInfo : pciErrorList){
                        errItem = new JsonObject();
                        errItem.put("error_code", errInfo.getErrorCode());
                        errItem.put("category", errInfo.getCategory());

                        String errMsg = errInfo.getErrorCodeCount() + errInfo.getErrorMsg();
                        if(errMsg.matches(ErrorInfo.regex)) errMsg = errMsg.replaceAll("[[:]\\\\/?[*]]", "");
                        errMsg = errMsg.replace('\\',' ');
                        errMsg = errMsg.replace('\'',' ');
                        errMsg = errMsg.replace('[','<');
                        errMsg = errMsg.replace(']','>');

                        errItem.put("message", errMsg);
                        errItem.put("error_time", errInfo.getErrorOccurTimeStr());
                        jArray.add(errItem);
                    }

                    JsonObject param = new JsonObject();
                    param.put("stb_id", apiPci6001_getPciKey());
                    param.put("list", jArray);

                    PciResp pciResp = new PciResp(httpPostPci(apiName, reqUrl, dataManager.getPciSrvData().getNetHeader(), param));

                    if(pciResp.isSuccessed()){
                        pciDB.clearErrorInfo();
                    }else{
                        recordFailOnPciSrv(apiName, pciResp);
                    }
                }else{
                    GLog.printError(apiName, "Can't call API : STB ErrorInfo is null");
                    return;
                }
            }else{
                GLog.printError(apiName, "Can't call API : STB ErrorInfo count is " + errCount);
                return;
            }
        } catch (NetException e) {
            GLog.printExceptWithSaveToPciDB(this, apiName + "| request " + reqUrl + " fail: " + e.getMessage(), e, ErrorInfo.CODE_PCI_PLATFORM_NETWORK_ERR, ErrorInfo.CATEGORY_PCIPLATFORM);
        } catch (InterruptedException e) {
            GLog.printExceptWithSaveToPciDB(this, apiName + "| request " + reqUrl + " interrupted", e, ErrorInfo.CODE_PCI_PLATFORM_NETWORK_ERR, ErrorInfo.CATEGORY_PCIPLATFORM);
        } catch (Exception e) {
            //GLog.printExceptWithSaveToPciDB(this,apiName + "| request " + reqUrl + " fail(Unhandled Exception): " + e.getMessage(), e, ErrorInfo.CODE_ETC_UNKNOWN, ErrorInfo.CATEGORY_PCIPLATFORM);
        }
    }


    /**
     * API 6008 ????????? ????????? ??????
     * @param needResponseToMc ????????? ????????? ????????? ??? ?????? App?????? ???????????? ??????
     * @param reqPkgName ????????? ???????????? ????????? ???????????? ????????????(needResponseToMc == true ????????? ??????)
     * @param netHeader ??????????????? ?????? ????????? null ????????? ???
     */
    public void apiPci6008_reqCheckInList(final boolean needResponseToMc, final String reqPkgName, final String netHeader){
        final String reqUrl = pciProperty.getApi_pci_url() + pciProperty.getApi_pci_uri_008();
        final String apiName = "apiPci6008_reqCheckInList";

        if(pciProperty.getAppEnv().equals("none")){
            GLog.printError(apiName, "Can't call API : Property.appEnv is none");
            return;
        }

        new GThread("API6008 Connecting Thread", new GRunnable() {
            @Override
            public void run() {
                // ???????????? ?????? ?????? ??? ????????? ?????? ?????? (2019/01/25 KT??????)
                boolean success = false;
                int tryCnt = 0;

                while (!success){
                    Pci_Service service = Global.getInstance().getPciService();
                    ConnectivityManager connectivityManager = (ConnectivityManager) service.getSystemService(service.CONNECTIVITY_SERVICE);
                    NetworkInfo netInfo = connectivityManager.getActiveNetworkInfo();

                    if(netInfo != null && netInfo.isConnected()){
                        try{
                            JsonObject param = new JsonObject();
                            param.put("stb_id", apiPci6001_getPciKey());
                            String _netHeader = (netHeader == null)?  dataManager.getPciSrvData().getNetHeader() : netHeader;
                            PciResp_CheckIn pciResp = new PciResp_CheckIn(httpPostPci(apiName, reqUrl, _netHeader, param), true);
                            if(pciResp.isSuccessed()){
                                if(pciResp.getPidList() != null){
                                    dataManager.getPidManager().updateCheckInPidList(pciResp.getPidList(), "API6008");
                                }

                                if(needResponseToMc) PciManager.getInstance().getPciBroadcastReceiver().sendPciListToMC(reqPkgName, null, null, false, "API-6008");
                                success = true;
                                pciResp.destory();
                                break;
                            }else{
                                recordFailOnPciSrv(apiName, pciResp);
                                pciResp.destory();
                            }
                        } catch (NetException e) {
                            //GLog.printExceptWithSaveToPciDB(this, apiName + "| request " + reqUrl + " fail: " + e.getMessage(), e, ErrorInfo.CODE_PCI_PLATFORM_NETWORK_ERR, ErrorInfo.CATEGORY_PCIPLATFORM);
                        } catch (InterruptedException e) {
                            //GLog.printExceptWithSaveToPciDB(this, apiName + "| request " + reqUrl + " interrupted", e, ErrorInfo.CODE_PCI_PLATFORM_NETWORK_ERR, ErrorInfo.CATEGORY_PCIPLATFORM);
                        } catch (Exception e) {
                            //GLog.printExceptWithSaveToPciDB(this,apiName + "| request " + reqUrl + " fail(Unhandled Exception): " + e.getMessage(), e, ErrorInfo.CODE_ETC_UNKNOWN, ErrorInfo.CATEGORY_PCIPLATFORM);
                        }
                    }else{
                        if(netInfo == null) {
                            //GLog.printExceptWithSaveToPciDB(this, apiName + "| request " + reqUrl + " fail: ", new PciRuntimeException("Network State Error. isConnected=netInfo is null"), ErrorInfo.CODE_ETC_DEVICE_NET_STATE_ERR, ErrorInfo.CATEGORY_PCIPLATFORM);
                        }else {
                            //GLog.printExceptWithSaveToPciDB(this, apiName + "| request " + reqUrl + " fail: ", new PciRuntimeException("Network State Error. isConnected=" + netInfo.isConnected()), ErrorInfo.CODE_ETC_DEVICE_NET_STATE_ERR, ErrorInfo.CATEGORY_PCIPLATFORM);
                        }
                        addRetryAPI(new RetryAPI(API_CODE_008, new Param6008(null, needResponseToMc, reqPkgName)));
                        break;
                    }

                    tryCnt++;

                    if(tryCnt >= 3){
                        GLog.printWtf(this, "apiPci6008_reqCheckInList Retry Failed !!! retry thread finish. / total try Cnt="+tryCnt+"/3");
                        break;
                    }else{
                        try{
                            GLog.printInfo(this, "apiPci6008_reqCheckInList Failed. retry wait 60s. / total try Cnt="+tryCnt+"/3");
                            Thread.sleep(60000);
                        }catch (InterruptedException e){
                            break;
                        }
                    }
                }
            }
        }).start();
    }


    /**
     * API 6009 ??????????????? ON/OFF ?????? ??????
     * ??????????????? ??????????????? ??????????????? ????????? ??? ????????? ??????.
     * Response??? ?????? continuous_play_yn?????? ?????? App?????? ??????????????? ?????? ????????? ??????(?????? ????????? ??????)
     * @return ???????????? ????????? ???????????? true, cms?????? ?????? ?????? ????????? ???????????? ???????????? ??????????????? ????????? ?????? false
     */
    public boolean apiPci6009_SetNonAudibleSndState(boolean isOn){
        String reqUrl = pciProperty.getApi_pci_url() + pciProperty.getApi_pci_uri_009();
        String apiName = "apiPci6009_SetSndPlayState";
        boolean result = false;

        if(pciProperty.getAppEnv().equals("none")){
            GLog.printError(apiName, "Can't call API : Property.appEnv is none");
            return false;
        }

        try{
            JsonObject param = new JsonObject();
            param.put("stb_id", apiPci6001_getPciKey());
            param.put("sound_play_yn", isOn? "Y" : "N");
            PciResp_Data pciResp = new PciResp_Data(httpPostPci(apiName, reqUrl, dataManager.getPciSrvData().getNetHeader(), param));
            if(pciResp.isSuccessed()){
                String cpYn = pciResp.getData().getString("continuous_play_yn");
                dataManager.getPciSrvData().getPciSrvPolicyData().setContinuousPlayYn(cpYn);
                if(cpYn.equals("Y")){
                    if(pciManager.getNonAudiblePlayer().isExists()){
                        pciManager.playSound(true);
                    }else{
                        pciManager.getNetManager().updatePolicy();
                        pciManager.playSound(true);
                    }
                }else if(cpYn.equals("N")){
                    pciManager.getNonAudiblePlayer().stopSound();
                }else{
                    pciManager.getNonAudiblePlayer().stopSound();
                }

                if(isOn && cpYn.equals("Y")) result = true;
                else if(!isOn && !cpYn.equals("Y")) result = true;

                return result;
            }else{
                recordFailOnPciSrv(apiName, pciResp);
                return false;
            }
        } catch (NetException e) {
            GLog.printExceptWithSaveToPciDB(this, apiName + "| request " + reqUrl + " fail: " + e.getMessage(), e, ErrorInfo.CODE_PCI_PLATFORM_NETWORK_ERR, ErrorInfo.CATEGORY_PCIPLATFORM);
        } catch (InterruptedException e) {
            GLog.printExceptWithSaveToPciDB(this, apiName + "| request " + reqUrl + " interrupted", e, ErrorInfo.CODE_PCI_PLATFORM_NETWORK_ERR, ErrorInfo.CATEGORY_PCIPLATFORM);
        } catch (Exception e) {
            GLog.printExceptWithSaveToPciDB(this,apiName + "| request " + reqUrl + " fail(Unhandled Exception): " + e.getMessage(), e, ErrorInfo.CODE_ETC_UNKNOWN, ErrorInfo.CATEGORY_PCIPLATFORM);
        }

        return false;
    }

/**  ??????????????? KPNS ?????? - by dalkommJK | 2019.09.30 ???????????????  **/
    public void apiPci6010_activation_token() {
        String reqUrl = pciProperty.getApi_pci_url() + pciProperty.getApi_pci_uri_6010();
        String apiName = "apiPci6010_activation_token";

        if (pciProperty.getAppEnv().equals("none")) {
            GLog.printError(apiName, "Can't call API : Property.appEnv is none");
            return;
        }
        try {
            JsonObject param = new JsonObject();
            param.put("stb_id", apiPci6001_getPciKey());
            param.put("token", kpns_token); //kpns token
            PciResp pciResp = new PciResp(httpPostPci(apiName, reqUrl, dataManager.getPciSrvData().getNetHeader(), param));
            if (!pciResp.isSuccessed()) {
                recordFailOnPciSrv(apiName, pciResp);
            }
        } catch (NetException e) {
            GLog.printExceptWithSaveToPciDB(this,apiName + "| request " + reqUrl + " fail: " + e.getMessage(), e, ErrorInfo.CODE_PCI_PLATFORM_NETWORK_ERR, ErrorInfo.CATEGORY_PCIPLATFORM);
        } catch (InterruptedException e) {
            GLog.printExceptWithSaveToPciDB(this, apiName + "| request " + reqUrl + " interrupted", e, ErrorInfo.CODE_PCI_PLATFORM_NETWORK_ERR, ErrorInfo.CATEGORY_PCIPLATFORM);
        } catch (Exception e) {
            //GLog.printExceptWithSaveToPciDB(this,apiName + "| request " + reqUrl + " fail(Unhandled Exception): " + e.getMessage(), e, ErrorInfo.CODE_ETC_UNKNOWN, ErrorInfo.CATEGORY_PCIPLATFORM);
        }
    }
/**  ?????????????????? KPNS ?????? - by dalkommJK | 2019.09.30 ??????????????????  **/


    // ==================================== ERROR TEST ==================================== //
    /**
     * API ?????? ??? ????????????????????? ?????? ?????????
     */
    public void apiTest_Err(){
        String apiName = "apiPci6001_ErrTEST";
        if(pciProperty.getAppEnv().equals("none")){
            GLog.printError(apiName, "Can't call API : Property.appEnv is none");
            return;
        }

        String reqUrl = pciProperty.getApi_pci_url() + pciProperty.getApi_pci_uri_001();
        GLog.printInfo(apiName, "try to api Error Test !! > " + reqUrl);

        try {
            JsonObject result = httpGetPci(apiName, reqUrl, "PCITV(STBType/GAGI;STBModel/CT1100;FirmwareVersion/15.0.1130;STBService/OTV");
            GLog.printInfo(apiName, " ***** getPciKey Result > " + result.toJsonStringPretty());
            PciResp_Data pciRespData = new PciResp_Data(result);
            if(pciRespData.isSuccessed()){
                String stbId = pciRespData.getData().getString("stb_id");
                GLog.printInfo(apiName, "***** stb_id :: " + stbId);
                dataManager.getPciSrvData().setStbId(stbId);
            }else{
                recordFailOnPciSrv(apiName, pciRespData);
            }
        }catch (NetException e){
            GLog.printExceptWithSaveToPciDB(this, apiName + "| request " + reqUrl + " fail: " + e.getMessage(), e, ErrorInfo.CODE_PCI_PLATFORM_NETWORK_ERR, ErrorInfo.CATEGORY_PCIPLATFORM);
        }catch (Exception e){
            GLog.printExceptWithSaveToPciDB(this,apiName + "| request " + reqUrl + " fail(Unhandled Exception): " + e.getMessage(), e, ErrorInfo.CODE_ETC_UNKNOWN, ErrorInfo.CATEGORY_PCIPLATFORM);
        }
        return;
    }
    // ==================================================================================== //






    /**
     * 2019-04-17 ????????? ?????? ???????????? ????????? BLE Advertise??? ?????? -JK
     * Beacon API 0001 regist


    public boolean apiBeacon0001_regist() {
        String apiName = "apiBeacon0001_regist";
        String reqUrl = pciProperty.getApi_beacon_url() + pciProperty.getApi_beacon_uri_0001();

        if(pciProperty.getAppEnv().equals("none")){
            GLog.printError(apiName, "Can't call API : Property.appEnv is none");
            return false;
        }

        if(dataManager.getStbData().getMacAddress() == null){
            GLog.printExceptWithSaveToPciDB(apiName,"request " + reqUrl + " failed.", new PciRuntimeException("can't regist beacon server. Mac Address is null."), ErrorInfo.CODE_NONAUDIBLE_NETWORK_ERR, ErrorInfo.CATEGORY_NONAUDIBLE);
            return false;
        }

        JsonObject jsonParam = new JsonObject();
        jsonParam.put("password", pciProperty.getApi_beacon_password());
        jsonParam.put("device_id", HsUtil_Encrypter.encryptSha256Base64(dataManager.getStbData().getMacAddress()));
        jsonParam.put("beacon_tp", 1);
        jsonParam.put("beacon_nm", "STB");

        try {
            JsonObject resObj = httpPostBeacon(apiName, reqUrl, jsonParam);
            SndSvrData sndSvrData = new SndSvrData(resObj);
            if(sndSvrData.getReturnCode().trim().equalsIgnoreCase("B0000")){
                dataManager.setSndSvrData(sndSvrData);
                return true;
            }else{
                recordFailOnSoundSrv(apiName, sndSvrData);
            }
        } catch (NetException e) {
            GLog.printExceptWithSaveToPciDB(apiName,apiName + "| request " + reqUrl + " fail: " + e.getMessage(), e, ErrorInfo.CODE_NONAUDIBLE_NETWORK_ERR, ErrorInfo.CATEGORY_NONAUDIBLE);
        } catch (InterruptedException e) {
            GLog.printExceptWithSaveToPciDB(apiName,apiName + "| request " + reqUrl + " interrupted.", e, ErrorInfo.CODE_NONAUDIBLE_NETWORK_ERR, ErrorInfo.CATEGORY_NONAUDIBLE);
        } catch (Exception e) {
            GLog.printExceptWithSaveToPciDB(this,apiName + "| request " + reqUrl + " fail(Unhandled Exception): " + e.getMessage(), e, ErrorInfo.CODE_ETC_UNKNOWN, ErrorInfo.CATEGORY_NONAUDIBLE);
        }

        return false;
    }

     */




    /**
     * Pci????????? ?????? 200(??????)??? ?????? ?????? ????????? ????????? ?????? ?????? ????????? PCI App DB??? ????????????.
     * (PCI App DB??? ????????? ??????????????? 1??? 1??? PCI ????????? ??????)
     */
    public void recordFailOnPciSrv(String apiName, PciResp respBase){
        GLog.printExceptWithSaveToPciDB(
                this,
                respBase.getRes_msg(),
                new PciRuntimeException(apiName + " failed. Pci-Platform Responded by " + respBase.getRes_code() + " / res msg : " + respBase.getRes_msg()),
                ErrorInfo.CODE_PCI_PLATFORM_RESPONSE_CODE_ERR,
                ErrorInfo.CATEGORY_PCIPLATFORM);
    }

    /**
     * ????????? ?????? ????????? ?????? B000(??????)??? ?????? ?????? ????????? ????????? ?????? ?????? ????????? PCI App DB??? ????????????.
     * (PCI App DB??? ????????? ??????????????? 1??? 1??? PCI ????????? ??????)
     */
    public void recordFailOnSoundSrv(String apiName, SndSvrData sndSvrData){
//        GLog.printExceptWithSaveToPciDB(
//                this,
//                sndSvrData.getMessage(),
//                new PciRuntimeException(apiName + " failed. Non-Audible Server Responded by " + sndSvrData.getReturnCode() + " / res msg : " + sndSvrData.getMessage()),
//                ErrorInfo.CODE_NONAUDIBLE_RESPONSE_CODE_ERR,
//                ErrorInfo.CATEGORY_NONAUDIBLE);
    }




    public void addRetryAPI(RetryAPI ra){
        if(retryAPIList != null)
            synchronized (retryAPIList){
                if(!retryAPIList.containsKey(ra.getApiCode())) retryAPIList.put(ra.getApiCode(), ra);
            }
    }

    public void runRetryAPI(){
        new GThread("Retry Network Thread", new GRunnable() {
            @Override
            public void run() {
                if(retryAPIList != null){
                    synchronized (retryAPIList){
                        GLog.printInfo(this, ">>>>> Start Retry API !! :: retryAPIList.size()="+retryAPIList.size());
                        if(retryAPIList.size() > 0){
                            for(String key : retryAPIList.keySet()){
                                RetryAPI ra = retryAPIList.get(key);
                                if(ra != null){
                                    switch (ra.getApiCode()){
                                        case API_CODE_008 :
                                            Param6008 param6008 = (Param6008) ra.getParam();
                                            apiPci6008_reqCheckInList(param6008.getIsNeedResponseToMc(), param6008.getReqPkgName(), null);
                                            break;
                                        default: continue;
                                    }
                                }
                            }

                            retryAPIList.clear();
                        }
                    }
                }

            }
        }).start();
    }

//    public ArrayList getRetryAPIList() { return retryAPIList; }

    public String toString(){
        return logHeader;
    }
    /** String to Hex
     *  by 2019-04-18 JK
     */
    public  String string2hex(String stb_id) {
        String s2h="";
        for (int i = 0; i < stb_id.length(); i++) {
            System.out.println();
            int ch = (int) stb_id.charAt(i);
            String s4 = Integer.toHexString(ch);
            s2h += s4;
            System.out.println(i + "output->" + s4); // String to Hex
        }
        GLog.printInfo(this, "MAC String to Hex finished > " + s2h );
        return s2h;
    }



}
