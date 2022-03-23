package com.kt.gigagenie.pci.data;

import com.gnifrix.debug.GLog;
import com.gnifrix.util.HsUtil_Date;
import com.kt.gigagenie.pci.PciManager;
import com.kt.gigagenie.pci.data.pci_db.ErrorInfo;

import java.text.ParseException;
import java.util.Date;

/**
 * Created by LeeBaeng on 2018-11-29.
 */
public class CheckInData implements Comparable<CheckInData> {
    public static final int CHECKIN_TYPE_P = 0x1; // 비가청
    public static final int CHECKIN_TYPE_V = 0x2; // 보이스
    public static final int CHECKIN_TYPE_W = 0x4; // 와이파이
    public static final int CHECKIN_TYPE_B = 0x8; // 블루투스
    public static final int CHECKIN_TYPE_U = 0x10; // 알수없음

    String checkInDateStr;
    Date checkInDate;
    int checkInType;

    public CheckInData(String _checkInDateStr, int _checkInType){
        checkInType = _checkInType;
        setCheckInDateStr(_checkInDateStr);
    }

    public String getCheckInDateStr() { return checkInDateStr; }
    public Date getCheckInDate() { return checkInDate; }
    public int getCheckInType() { return checkInType; }
    public void setCheckInType( int _checkInType ) { checkInType = _checkInType; }

    public void setCheckInDateStr( String _checkInDateStr ) {
        if(_checkInDateStr != null && !_checkInDateStr.trim().equals("")){
            try{
                checkInDateStr = _checkInDateStr;
                checkInDate = HsUtil_Date.getDateObject(_checkInDateStr, PciManager.getInstance().getPciProperty().getDate_format_pcisrv_checkin_list());
                GLog.printInfo("CheckInData:" + getCheckInTypeStr(), "set CheckIn time finished. >> " + toString());
            }catch (ParseException e) {
                GLog.printExceptWithSaveToPciDB(this, "Wrong Date Type. CheckInData parse Fail.", e, ErrorInfo.CODE_ETC_CHECKIN_DATE_PARSE_ERR, ErrorInfo.CATEGORY_ETC);
                checkInDate = null;
                checkInDateStr = null;
            }
        }else{
            GLog.printInfo(this, "CheckIn time is null");
            checkInDate = null;
            checkInDateStr = null;
        }
    }

    public String getCheckInTypeStr(){
        switch (checkInType){
            case CHECKIN_TYPE_P : return "NON-AUDIBLE";
            case CHECKIN_TYPE_W : return "WIFI";
            case CHECKIN_TYPE_V : return "VOICE";
            case CHECKIN_TYPE_B : return "BLUETOOTH";
            default: return "UNKNOWN";
        }
    }

    public static String getCheckInTypeStr(int checkInType){
        switch (checkInType){
            case CHECKIN_TYPE_P : return "NON-AUDIBLE";
            case CHECKIN_TYPE_W : return "WIFI";
            case CHECKIN_TYPE_V : return "VOICE";
            case CHECKIN_TYPE_B : return "BLUETOOTH";
            default: return "UNKNOWN";
        }
    }

    @Override
    public int compareTo(CheckInData o1) {
        if(checkInDateStr == null || checkInDate == null) return 1;
        if(o1.getCheckInDateStr() == null || o1.getCheckInDate() == null) return -1;
        return o1.getCheckInDate().compareTo(getCheckInDate());
    }

    public void dispose(){
        checkInDateStr = null;
        checkInDate = null;
        checkInType = 0;
    }

    public String toString(){
        if(checkInDate != null) return "[CHECK_IN-DATA | " + getCheckInTypeStr() + "] : " + checkInDateStr + " / " + HsUtil_Date.getTimeString(checkInDate);
        else return "[CHECK_IN-DATA | " + getCheckInTypeStr() + "] : checkInDateStr is null";
    }
}