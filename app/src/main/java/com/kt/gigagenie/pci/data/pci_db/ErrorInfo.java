package com.kt.gigagenie.pci.data.pci_db;

import com.gnifrix.debug.GLog;
import com.gnifrix.util.HsUtil_Date;

import java.text.ParseException;
import java.util.Date;

/**
 * Created by LeeBaeng on 2018-10-18.
 */
public class ErrorInfo {
    public static final String CATEGORY_NONAUDIBLE       = "NONAUDIBLE";
    public static final String CATEGORY_INAPP            = "INAPP";
    public static final String CATEGORY_PCIPLATFORM      = "PCIPLATFORM";
    public static final String CATEGORY_PCIADPLATFORM      = "PCIADPLATFORM"; // AD PCI by dalkommjk - v16.00.01 | 2022-02-22
    public static final String CATEGORY_ETC              = "ETC";

    public static final int CODE_PCI_SERVICE_INITIALIZE_FAIL        = 100;
    public static final int CODE_INAPP_POLICY_IS_NULL               = 101;
    public static final int CODE_INAPP_WRONG_POLICY_DATA            = 102;
    public static final int CODE_INAPP_CHECKIN_FAIL                 = 103;
    public static final int CODE_INAPP_PID_NOT_FOUND                = 104;
    public static final int CODE_INAPP_CHECKOUT_FAIL                = 105;
    public static final int CODE_INAPP_VOICE_EVENT_FAIL             = 106;
    public static final int CODE_INAPP_UNKNOWN_ERR                  = 199;

    public static final int CODE_PCI_PLATFORM_NETWORK_ERR           = 300;
    public static final int CODE_PCI_PLATFORM_DATA_FORMAT_ERR       = 301;
    public static final int CODE_PCI_PLATFORM_RESPONSE_CODE_ERR     = 302;
    public static final int CODE_PCI_PLATFORM_GPUSH                 = 303;
    public static final int CODE_PCI_PLATFORM_UNKNOWN_ERR           = 399;

    // AD PCI by dalkommjk - v16.00.01 | 2022-02-22
    public static final int CODE_PCIAD_PLATFORM_NETWORK_ERR           = 400;
    public static final int CODE_PCIAD_PLATFORM_DATA_FORMAT_ERR       = 401;
    public static final int CODE_PCIAD_PLATFORM_RESPONSE_CODE_ERR     = 402;
    public static final int CODE_PCIAD_PLATFORM_GPUSH                 = 403;
    public static final int CODE_PCIAD_PLATFORM_UNKNOWN_ERR           = 499;


    public static final int CODE_NONAUDIBLE_INITIALIZE_FAIL         = 500;
    public static final int CODE_NONAUDIBLE_MEDIA_PLAYER_ERR        = 501;
    public static final int CODE_NONAUDIBLE_DOWNLOAD_ERR            = 502;
    public static final int CODE_NONAUDIBLE_DOWNLOAD_HTTPRES_ERR    = 503;
    public static final int CODE_NONAUDIBLE_FILEIO_ERR              = 504;
    public static final int CODE_NONAUDIBLE_NETWORK_ERR             = 505;
    public static final int CODE_NONAUDIBLE_RESPONSE_CODE_ERR       = 506;
    public static final int CODE_NONAUDIBLE_UNKNOWN_ERR             = 599;

    public static final int CODE_ETC_PLATFORM_INITIALIZE_FAIL       = 900;
    public static final int CODE_ETC_DECRYPTION_ERR                 = 901;
    public static final int CODE_ETC_ENCRYPTION_ERR                 = 902;
    public static final int CODE_ETC_CHECKIN_DATE_PARSE_ERR = 903;
    public static final int CODE_ETC_DEVICE_NET_STATE_ERR           = 904;
    public static final int CODE_ETC_UNKNOWN                        = 999;


    private static final String DATE_FORMAT = "yyyyMMddHHmmss";
    int errorCode;
    int errorCodeCount;
    String errorMsg;
    String category;
    Date errorOccurTime;
    long dbId = -1;

    public static final String regex = ".*[\\[\\][:]\\\\/?[*]].*";

    public ErrorInfo(int _errorCode, int _errorCodeCount, String _errorMsg, String _category, String _errorOccurTime){
        errorCode = _errorCode;
        errorCodeCount = _errorCodeCount;
        errorMsg = _errorMsg;
        if(errorMsg.matches(regex)) errorMsg = errorMsg.replaceAll("[[:]\\\\/?[*]]", "");
        errorMsg = errorMsg.replace('\\',' ');
        errorMsg = errorMsg.replace('\'',' ');
        errorMsg = errorMsg.replace('[','<');
        errorMsg = errorMsg.replace(']','>');
        category = _category;
        try{
            errorOccurTime = HsUtil_Date.getDateObject(_errorOccurTime, DATE_FORMAT);
        }catch (ParseException e){
            errorOccurTime = HsUtil_Date.getCurrentTime();
            errorMsg += "/ OccurTime Parse Error, OccurTime set to object created time. inputed Time: " + _errorOccurTime;
        }
        dbId = -1;
    }

    public ErrorInfo(int _errorCode, int _errorCodeCount, String _errorMsg, String _category, Date _errorOccurTime){
        errorCode = _errorCode;
        errorCodeCount = _errorCodeCount;
        errorMsg = _errorMsg;
        if(errorMsg.matches(regex)) errorMsg = errorMsg.replaceAll("[[:]\\\\/?[*]]", "");
        errorMsg = errorMsg.replace('\\',' ');
        errorMsg = errorMsg.replace('\'',' ');
        errorMsg = errorMsg.replace('[','<');
        errorMsg = errorMsg.replace(']','>');

        category = _category;
        errorOccurTime = _errorOccurTime;
        dbId = -1;
    }

    public int getErrorCode() { return errorCode; }
    public int getErrorCodeCount() { return errorCodeCount; }
    public String getErrorMsg() { return errorMsg; }
    public String getCategory() { return category; }
    public long getDbId() { return dbId; }

    public void setErrorCode( int _errorCode ) { errorCode = _errorCode; }
    public void setErrorCodeCount( int _errorCodeCount ) { errorCodeCount = _errorCodeCount; }
    public void setErrorMsg( String _errorMsg ) { errorMsg = _errorMsg; }
    public void setCategory( String _category ) { category = _category; }
    public void setDbId( long _dbId ) { dbId = _dbId; }

    public Date getErrorOccurTime() { return errorOccurTime; }
    public String getErrorOccurTimeStr() { return HsUtil_Date.getTimeString(errorOccurTime, DATE_FORMAT); }
    public void setErrorOccurTime( Date _errorOccurTime ) { errorOccurTime = _errorOccurTime; }
    public void setErrorOccurTime( String _errorOccurTime ) {
        try{
            errorOccurTime = HsUtil_Date.getDateObject(_errorOccurTime, DATE_FORMAT);
        }catch (ParseException e){
            errorOccurTime = HsUtil_Date.getCurrentTime();
            errorMsg += "/ OccurTime Parse Error, OccurTime set to method call time. inputed Time: " + _errorOccurTime;
        }
    }

    public void printErrorInfo(String tag){
        if( tag == null || tag.trim().equals("")) tag = "ErrorInfo";
        GLog.printInfo(tag,"───────────");
        GLog.printInfo(tag,"error_code : " + getErrorCode());
        GLog.printInfo(tag,"error_codeCount : " + getErrorCodeCount());
        GLog.printInfo(tag,"category : " + getCategory());
        GLog.printInfo(tag,"message : " + getErrorMsg());
        GLog.printInfo(tag,"error_time : " + getErrorOccurTimeStr());
    }

}
