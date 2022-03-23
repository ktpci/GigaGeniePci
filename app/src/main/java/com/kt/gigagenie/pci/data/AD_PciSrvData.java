package com.kt.gigagenie.pci.data;

import com.kt.gigagenie.pci.net.json.JsonArray;

public class AD_PciSrvData {
    private static final String logHeader = "AD_PciServerData";
    public static String kpns_token;
    //    public static String kpns_token_new;
    AD_DataManager ADdataManager;

    String stbId = null;
    String netHeader = null;
    String adnetHeader = null; // by dalkommjk - v16.00.01 | 2022-02-22
    String taId = null; // by dalkommjk - v16.00.01 | 2022-02-22

    JsonArray pidCheckInData = null;
    JsonArray adCheckInData = null; // by dalkommjk - v16.00.01 | 2022-02-22

    PciSrv_PolicyData pciSrvPolicyData = null;
    AD_PciSrv_PolicyData pciADSrvPolicyData = null; // by dalkommjk - v16.00.01 | 2022-02-22

    public AD_PciSrvData(AD_DataManager _addataManager){
        ADdataManager = _addataManager;
    }


    public String getStbId() { return stbId; }
    public String getTaId() { return taId; } // by dalkommjk - v16.00.01 | 2022-02-22

    public String getNetHeader() { return netHeader; }
    public String getADNetHeader() { return adnetHeader; } // by dalkommjk - v16.00.01 | 2022-02-22
    public PciSrv_PolicyData getPciSrvPolicyData() { return pciSrvPolicyData; }
    public AD_PciSrv_PolicyData getPciADSrvPolicyData() { return pciADSrvPolicyData; } // by dalkommjk - v16.00.01 | 2022-02-22
    public JsonArray getPidCheckInData() { return pidCheckInData; }
    public JsonArray getADCheckInData() { return adCheckInData; }  // by dalkommjk - v16.00.01 | 2022-02-22
    public void setPidCheckInData( JsonArray _pidCheckInData ) { pidCheckInData = _pidCheckInData; }
    public void setADCheckInData( JsonArray _adCheckInData ) { adCheckInData = _adCheckInData; } // by dalkommjk - v16.00.01 | 2022-02-22
    public void setStbId( String _stbId ) { stbId = _stbId; }
    public void setTaId( String _taId ) { taId = _taId; } // by dalkommjk - v16.00.01 | 2022-02-22
    public void setNetHeader( String _netHeader ) { netHeader = _netHeader; }
    public void setADNetHeader( String _adnetHeader ) { adnetHeader = _adnetHeader; } // by dalkommjk - v16.00.01 | 2022-02-22
    public void setPciSrvPolicyData(PciSrv_PolicyData _pciSrvPolicyData) { pciSrvPolicyData = _pciSrvPolicyData; }
    public void setPciADSrvPolicyData(AD_PciSrv_PolicyData _pciADSrvPolicyData) { pciADSrvPolicyData = _pciADSrvPolicyData; } // by dalkommjk - v16.00.01 | 2022-02-22

    public String toString(){
        return logHeader;
    }

    public void destroy(){
        stbId = null;
        taId = null; // by dalkommjk - v16.00.01 | 2022-02-22
        netHeader = null;
        adnetHeader = null; // by dalkommjk - v16.00.01 | 2022-02-22
        if(pciSrvPolicyData != null){
            pciSrvPolicyData.destroy();
            pciSrvPolicyData = null;
        }
        if(pciADSrvPolicyData != null){    // by dalkommjk - v16.00.01 | 2022-02-22
            pciADSrvPolicyData.destroy();  // by dalkommjk - v16.00.01 | 2022-02-22
            pciADSrvPolicyData = null;    // by dalkommjk - v16.00.01 | 2022-02-22
        }

    }
}
