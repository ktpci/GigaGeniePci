package com.kt.gigagenie.pci.net;

import android.net.ConnectivityManager;
import android.net.Network;
import android.net.NetworkInfo;
import android.net.NetworkRequest;

import com.gnifrix.debug.GLog;
import com.gnifrix.system.GRunnable;
import com.gnifrix.system.GThread;
import com.kt.gigagenie.pci.Global;
import com.kt.gigagenie.pci.PciManager;
import com.kt.gigagenie.pci.Pci_Service;
import com.kt.gigagenie.pci.data.AD_DataManager;
import com.kt.gigagenie.pci.data.AD_PciSrv_BleChkInData;
import com.kt.gigagenie.pci.data.AD_PciSrv_GaidData;
import com.kt.gigagenie.pci.data.DataManager;
import com.kt.gigagenie.pci.data.GaidListManager;
import com.kt.gigagenie.pci.data.PciSrv_PidData;
import com.kt.gigagenie.pci.data.PciSrv_PolicyData;
import com.kt.gigagenie.pci.data.AD_PciSrv_PolicyData;
import com.kt.gigagenie.pci.data.PciSrv_VoiceChkInData;
import com.kt.gigagenie.pci.data.PidListManager;
import com.kt.gigagenie.pci.data.pci_db.ErrorInfo;
import com.kt.gigagenie.pci.net.connection.Connection;
import com.kt.gigagenie.pci.net.connection.InterruptedConnection;
import com.kt.gigagenie.pci.net.connection.RealConnection;
import com.kt.gigagenie.pci.net.json.JsonObject;
import com.kt.gigagenie.pci.net.json.pci_response.AD_PciResp;
import com.kt.gigagenie.pci.net.json.pci_response.AD_PciResp_CheckIn;
import com.kt.gigagenie.pci.net.json.pci_response.AD_PciResp_Data;
import com.kt.gigagenie.pci.net.json.pci_response.PciResp;
import com.kt.gigagenie.pci.net.json.pci_response.PciResp_CheckIn;
import com.kt.gigagenie.pci.net.json.pci_response.PciResp_Data;
import com.kt.gigagenie.pci.net.retry.Param6008;
import com.kt.gigagenie.pci.net.retry.RetryAPI;
import com.kt.gigagenie.pci.system.PciProperty;
import com.kt.gigagenie.pci.system.PciRuntimeException;

import java.io.UnsupportedEncodingException;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.HashMap;
import java.util.Properties;

import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;

public class AD_NetManager {
    public static final String API_CODE_AD1001 = "AD1001";
    public static final String API_CODE_AD1002 = "AD1002";
    public static final String API_CODE_AD1003 = "AD1003";
    public static final String API_CODE_AD1004 = "AD1004";
    public static final String API_CODE_AD1005 = "AD1005";
    public static final String API_CODE_AD1006 = "AD1006";
    public static final String API_CODE_AD1007 = "AD1007";
    public static final String API_CODE_AD1008 = "AD1008";
    public static final String API_CODE_AD1009 = "AD1009";

    private static final String logHeader = "AD_NetManager";

    private PciManager pciManager;
    private Connection connection;
    private RealConnection realCon;
    private InterruptedConnection interruptedCon;
    private AD_DataManager ad_dataManager;
    //    private DataManager dataManager;
    private PciProperty pciProperty;

    private HashMap<String, RetryAPI> retryAPIList = null; // ???????????? ?????? ?????? ?????? ??? ????????? ?????????

    ConnectivityManager cm = null;
    ConnectivityManager.NetworkCallback networkCallback = null;

    public AD_NetManager(PciManager _pciManager) throws NoSuchPaddingException, InvalidAlgorithmParameterException, UnsupportedEncodingException, IllegalBlockSizeException, BadPaddingException, NoSuchAlgorithmException, InvalidKeyException {
        realCon = new RealConnection();
        interruptedCon = new InterruptedConnection();
        connection = realCon;
        retryAPIList = new HashMap<>();
        pciManager = _pciManager;
        ad_dataManager = _pciManager.getADDataManager();
//        dataManager = _pciManager.getDataManager();
        pciProperty = _pciManager.getPciProperty();

        initStbInformation();
    }
    private void initStbInformation() throws NoSuchPaddingException, InvalidKeyException, UnsupportedEncodingException, IllegalBlockSizeException, BadPaddingException, NoSuchAlgorithmException, InvalidAlgorithmParameterException {
// user-agent = PCIAD(STBType\Genie;STBModel\CT1100;FirmwareVersion\1.00.01;appVersion\16.00.01)
        // =================================================
//        dataManager.getStbData().setSaId("TT180523006"); AD?????? ??????
//        dataManager.getStbData().setStbType("GIGA");
//        dataManager.getStbData().setModelNumber("CT1100");
//        dataManager.getStbData().setFirmwareVersion("15.0.1130"); // ????????? ?????? ???????????? ?????? ??????
//        dataManager.getStbData().setServiceType("OTV");  AD?????? ??????
//        dataManager.getStbData().setDeviceId("d1234567890"); AD?????? ??????
//        dataManager.getStbData().setMacAddress("1a2b3c4d5e6f"); AD?????? ??????
        // =================================================

        String stbType = "STBType/" + ad_dataManager.getStbData().getStbType() + ";";
        String stbModel = "STBModel/" + ad_dataManager.getStbData().getModelNumber() + ";";
        String firmwareVer = "FirmwareVersion/" + ad_dataManager.getStbData().getFirmwareVersion() + ";";
//        String stbService = "STBService/" + dataManager.getStbData().getServiceType() + ";";
        //String mac = "MAC/" + dataManager.getStbData().getMacAddressAES128Base64();
        //String mac = "MAC/" + dataManager.getStbData().getMacAddressAES128Base64() + ";"; // by dalkommjk | 15.00.04 | 2021-09-28
        String appVersion = "appVersion/" + ad_dataManager.getStbData().getAppVersion(); // by dalkommjk | 15.00.04 | 2021-09-28


        StringBuffer usrAgntBuf = new StringBuffer();
        usrAgntBuf.append("PCIAD(");
//        usrAgntBuf.append("Said/" + dataManager.getStbData().getSaId() + ";");
        usrAgntBuf.append(stbType);
        usrAgntBuf.append(stbModel);
        usrAgntBuf.append(firmwareVer);
//        usrAgntBuf.append(stbService);
//        usrAgntBuf.append("DeviceId/" + dataManager.getStbData().getDeviceId() + ";");
        // usrAgntBuf.append(mac);
        usrAgntBuf.append(appVersion); // by dalkommjk | 15.00.04 | 2021-09-28
        usrAgntBuf.append(")");
        String netHeader = usrAgntBuf.toString();
        ad_dataManager.getPciADSrvData().setNetHeader(netHeader);
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
        ad_dataManager = null;
        pciProperty = null;
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
    /**
     * STB ?????? ????????????
     */
    public void updatePolicy() {
        boolean getPolicy_result = apiPciAD1001_getPolicy(); // ???????????? ?????? ??? retry ?????? ?????? by dalkommjk | 2021-01-28
        int retry_cnt = 0;                                 // ???????????? ?????? ??? retry ?????? ?????? by dalkommjk | 2021-01-28
        while(getPolicy_result != true && retry_cnt < 5) { // ???????????? ?????? ??? retry ?????? ?????? by dalkommjk | 2021-01-28

            getPolicy_result = apiPciAD1001_getPolicy();     // ???????????? ?????? ??? retry ?????? ?????? by dalkommjk | 2021-01-28
            retry_cnt++;                                   // ???????????? ?????? ??? retry ?????? ?????? by dalkommjk | 2021-01-28
        }                                                  // ???????????? ?????? ??? retry ?????? ?????? by dalkommjk | 2021-01-28

//        PciSrv_PolicyData _getPolicy = ad_dataManager.getPciADSrvData().getPciSrvPolicyData();
        AD_PciSrv_PolicyData _getPolicy = ad_dataManager.getPciADSrvData().getPciADSrvPolicyData();
        //NonAudiblePlayer nonAudiblePlayer = pciManager.getNonAudiblePlayer(); // sound delete by dalkommJK | 2019-10-10

        if (_getPolicy != null) {
            try{

                GLog.printInfo(this, "IsIgnorePolicyForDownloadSndFile > " + pciProperty.getPci_sndfile_ignorePolicy());
                _getPolicy.printCurrentPolicy();
                //GLog.printInfo(this, "_soundFile.isExists() > " + nonAudiblePlayer.isExists()); // sound delete by dalkommJK | 2019-10-10
            }catch (Exception e) {
                GLog.printExcept(this, "read policy data error", e);
            }
        }
    }

    /**
     * AD API 1001 STB ?????? ??????
     */
    public boolean apiPciAD1001_getPolicy() {
        String apiName = "apiPciAD1001_getPolicy";
        String reqUrl = pciProperty.getApi_pciad_url() + pciProperty.getApi_pciad_uri_1001();

        if(pciProperty.getAppEnv().equals("none")){
            GLog.printError(apiName, "Can't call API : Property.appEnv is none");
            return false;
        }

        try {
//            String param = "stb_id=" + apiPci6001_getPciKey();
            String param = "taid=" + apiPci6001_getPciKey();

            PciResp_Data pciadResp = new PciResp_Data(httpGetPci(apiName, reqUrl + "?" + param, ad_dataManager.getPciADSrvData().getNetHeader()));

            if(pciadResp.isSuccessed()){
                AD_PciSrv_PolicyData getPolicy = new AD_PciSrv_PolicyData(pciadResp.getData());
                if (ad_dataManager.getPciADSrvData().getPciADSrvPolicyData() != null) {
                    ad_dataManager.getPciADSrvData().getPciADSrvPolicyData().destroy();
                }
                ad_dataManager.getPciADSrvData().setPciADSrvPolicyData(getPolicy);
                return true;
            }else{
                recordFailOnPciADSrv(apiName, pciadResp);
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
     * AD API 1002 check-in
     */
    public AD_PciSrv_GaidData apiPci1002_BleChkIn(AD_PciSrv_BleChkInData pciSrvBleChkInData) {
        String reqUrl = pciProperty.getApi_pci_url() + pciProperty.getApi_pci_uri_005();
        String apiName = "apiPci1002_BleChkIn";

        if(pciProperty.getAppEnv().equals("none")){
            GLog.printError(apiName, "Can't call API : Property.appEnv is none");
            return null;
        }

        try {
            String checkintime = pciSrvBleChkInData.getCheckInTime();
//            String action = pciSrvBleChkInData.getAction();
            String gender = pciSrvBleChkInData.getGender();
            String old = pciSrvBleChkInData.getOld();

            JsonObject param = new JsonObject();

//            if(voice_id == null || voice_id.trim().equals("")){
//                param.put("voice_id", userKey);
//                param.put("userkey", userKey);
//            }else if(userKey == null || userKey.trim().equals("")){
//                param.put("voice_id", voice_id);
//            }else{
//                // voice_id, userKey ?????? ????????????
//                param.put("voice_id", voice_id);
//                param.put("userkey", userKey);
//            }
            param.put("checkin", checkintime);
//            param.put("action_type", action);
            if(gender != null) param.put("gender", gender);
            if(old != null) param.put("old", old);

//            if(gender != null) param.put("gender", gender);
//            if(age_group != null) param.put("age", age_group);

            AD_PciResp_Data adpciRespData = new AD_PciResp_Data(httpPostPci(apiName, reqUrl, ad_dataManager.getPciADSrvData().getNetHeader(), param));

            if(adpciRespData.isSuccessed()){
//                PidListManager pidMgr = dataManager.getPidManager();
                GaidListManager gaidMgr = ad_dataManager.getGaidManager();

                JsonObject data = adpciRespData.getData();
                String gaid = data.getString("gaid");
                String newGender = data.getString("gender");
                String newAgeGroup = data.getString("old");


                // ????????? ??????????????? ?????? Pid??? ???????????? PidData??? ????????? ???????????? ?????? ?????? ?????? ?????? ??? ??????
                AD_PciSrv_GaidData gaidData = gaidMgr.getGaidDataByGAID(gaid);
                if(gaidData == null){
                    gaidData = new AD_PciSrv_GaidData(gaid, null, eventTime, null, null);
                    gaidMgr.getCheckedInList().put(gaid, gaidData);
                }

                // PID???????????? userKey, VoiceId, Gender, AgeGroup ?????? ??????
                if(newGender != null && !newGender.trim().equals("")) gaidData.setADSrv_gender(newGender);
                if(newAgeGroup != null && !newAgeGroup.trim().equals("")) gaidData.setADSrv_ageGroup(newAgeGroup);

                GLog.printInfo(apiName, "BleCheckIn > ???????????? GAID Data");
                gaidData.printGaidData();

                // ?????????, ???????????? ??? ????????? ?????? ??????
//                if(action.trim().equalsIgnoreCase("I") || action.trim().equalsIgnoreCase("U")){
//                    pidData.getCheckInData_v().setCheckInDateStr(eventTime);
//                }
                gaidData.getCheckInData_b().setCheckInDateStr(checkintime);

                return gaidData;
            }else{
                recordFailOnPciADSrv(apiName, adpciRespData);
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
     * AD API 1003 check-out
     */






    /**
     * AD API 1004 check-in list
     */
    public void apiPciAD1004_reqCheckInList(final boolean needResponseToMc, final String reqPkgName, final String netHeader){
        final String reqUrl = pciProperty.getApi_pciad_url() + pciProperty.getApi_pciad_uri_1004();
        final String apiName = "apiPciAD_1004_reqCheckInList";

        if(pciProperty.getAppEnv().equals("none")){
            GLog.printError(apiName, "Can't call API : Property.appEnv is none");
            return;
        }

        new GThread("API_PCIAD_1004 Connecting Thread", new GRunnable() {
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
                            param.put("taid", apiPci6001_getPciKey());
                            String _netHeader = (netHeader == null)?  ad_dataManager.getPciADSrvData().getNetHeader() : netHeader;
                            AD_PciResp_CheckIn pciadResp = new AD_PciResp_CheckIn(httpPostPci(apiName, reqUrl, _netHeader, param), true);
                            if(pciadResp.isSuccessed()){
                                if(pciadResp.getGaidList() != null){
                                    ad_dataManager.getGaidManager().updateCheckInGaidList(pciadResp.getGaidList(), "API1004");
                                }

                                if(needResponseToMc) PciManager.getInstance().getPciBroadcastReceiver().sendPciListToMC(reqPkgName, null, null, false, "API-6008");
                                success = true;
                                pciadResp.destory();
                                break;
                            }else{
                                recordFailOnPciADSrv(apiName, pciadResp);
                                pciadResp.destory();
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
                        addRetryAPI(new RetryAPI(API_CODE_AD1004, new Param6008(null, needResponseToMc, reqPkgName)));
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
     * AD API 1005 KPNS regi
     */
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
                                        case API_CODE_AD1004 :
                                            Param6008 param6008 = (Param6008) ra.getParam();
                                            apiPciAD1004_reqCheckInList(param6008.getIsNeedResponseToMc(), param6008.getReqPkgName(), null);
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

    /**
     * Pci????????? ?????? 200(??????)??? ?????? ?????? ????????? ????????? ?????? ?????? ????????? PCI App DB??? ????????????.
     * (PCI App DB??? ????????? ??????????????? 1??? 1??? PCI ????????? ??????)
     */
    public void recordFailOnPciADSrv(String apiName, AD_PciResp respBase){
        GLog.printExceptWithSaveToPciDB(
                this,
                respBase.getRes_msg(),
                new PciRuntimeException(apiName + " failed. PciAD-Platform Responded by " + respBase.getRes_code() + " / res msg : " + respBase.getRes_msg()),
                ErrorInfo.CODE_PCI_PLATFORM_RESPONSE_CODE_ERR,
                ErrorInfo.CATEGORY_PCIPLATFORM);
    }
    public void addRetryAPI(RetryAPI ra){
        if(retryAPIList != null)
            synchronized (retryAPIList){
                if(!retryAPIList.containsKey(ra.getApiCode())) retryAPIList.put(ra.getApiCode(), ra);
            }
    }



}
