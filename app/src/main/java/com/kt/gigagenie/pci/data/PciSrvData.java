package com.kt.gigagenie.pci.data;

import com.kt.gigagenie.pci.net.json.JsonArray;

/**
 * PCI 서버와 주고받을 때 사용되는 데이터
 * Created by LeeBaeng on 2018-09-19.
 */
public class PciSrvData {
    private static final String logHeader = "PciServerData";
    public static String kpns_token;
//    public static String kpns_token_new;
    DataManager dataManager;

    String stbId = null;
    String netHeader = null;

    JsonArray pidCheckInData = null;

    PciSrv_PolicyData pciSrvPolicyData = null;


    public PciSrvData(DataManager _dataManager){
        dataManager = _dataManager;
    }


    public String getStbId() { return stbId; }


    public String getNetHeader() { return netHeader; }

    public PciSrv_PolicyData getPciSrvPolicyData() { return pciSrvPolicyData; }

    public JsonArray getPidCheckInData() { return pidCheckInData; }

    public void setPidCheckInData( JsonArray _pidCheckInData ) { pidCheckInData = _pidCheckInData; }

    public void setStbId( String _stbId ) { stbId = _stbId; }

    public void setNetHeader( String _netHeader ) { netHeader = _netHeader; }

    public void setPciSrvPolicyData(PciSrv_PolicyData _pciSrvPolicyData) { pciSrvPolicyData = _pciSrvPolicyData; }


    public String toString(){
        return logHeader;
    }

    public void destroy(){
        stbId = null;

        netHeader = null;

        if(pciSrvPolicyData != null){
            pciSrvPolicyData.destroy();
            pciSrvPolicyData = null;
        }

    }
}
