package com.kt.gigagenie.pci.data.stb_db;

import com.gnifrix.debug.GLog;
import com.kt.gigagenie.pci.data.AD_PciSrv_GaidData;
import com.kt.gigagenie.pci.data.PciSrv_PidData;

public class AD_EventGaidData {
    public static int EVENT_TYPE_CHECKIN            = 0;
    public static int EVENT_TYPE_CHECKOUT           = 1;

    AD_PciSrv_GaidData gaidData;
    boolean isCheckIn;
    String type; // P, V, W, B

    public AD_EventGaidData(AD_PciSrv_GaidData _gaidData){
        gaidData = _gaidData;
    }

    public AD_EventGaidData(AD_PciSrv_GaidData _gaidData, String _type, boolean _isCheckIn){
        gaidData = _gaidData;
        type = _type;
        isCheckIn = _isCheckIn;
    }

    public void printEventData(){
        GLog.printInfo("EVENT_DATA", "EventDataInfo > EventType=" + type + " / isCheckIn=" + isCheckIn + " / ↓↓↓↓ GAID Data");
        if(gaidData != null) gaidData.printGaidData();
    }

    public String getType() { return type; }
    public boolean getIsCheckIn() { return isCheckIn; }
    public AD_PciSrv_GaidData getGaidData() { return gaidData; }

    public void setType( String _type ) { type = _type; }
    public void setGaidData( AD_PciSrv_GaidData _gaidData ) { gaidData = _gaidData; }
    public void setIsCheckIn( boolean _isCheckIn ) { isCheckIn = _isCheckIn; }

}
