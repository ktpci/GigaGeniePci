package com.kt.gigagenie.pci.data.stb_db;

import com.gnifrix.debug.GLog;
import com.kt.gigagenie.pci.Global;
import com.kt.gigagenie.pci.data.PciSrv_PidData;

/**
 * Created by LeeBaeng on 2019-01-24.
 */

public class EventPidData {
    public static int EVENT_TYPE_CHECKIN            = 0;
    public static int EVENT_TYPE_CHECKOUT           = 1;

    PciSrv_PidData pidData;
    boolean isCheckIn;
    String type; // P, V, W, B

    public EventPidData(PciSrv_PidData _pidData){
        pidData = _pidData;
    }

    public EventPidData(PciSrv_PidData _pidData, String _type, boolean _isCheckIn){
        pidData = _pidData;
        type = _type;
        isCheckIn = _isCheckIn;
    }

    public void printEventData(){
        GLog.printInfo("EVENT_DATA", "EventDataInfo > EventType=" + type + " / isCheckIn=" + isCheckIn + " / ↓↓↓↓ PID Data");
        if(pidData != null) pidData.printPidData();
    }

    public String getType() { return type; }
    public boolean getIsCheckIn() { return isCheckIn; }
    public PciSrv_PidData getPidData() { return pidData; }

    public void setType( String _type ) { type = _type; }
    public void setPidData( PciSrv_PidData _pidData ) { pidData = _pidData; }
    public void setIsCheckIn( boolean _isCheckIn ) { isCheckIn = _isCheckIn; }

}
