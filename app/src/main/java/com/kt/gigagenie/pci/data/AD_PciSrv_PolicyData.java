package com.kt.gigagenie.pci.data;

import com.gnifrix.debug.GLog;
import com.gnifrix.util.HsUtil_Date;
import com.kt.gigagenie.pci.PciManager;
import com.kt.gigagenie.pci.data.pci_db.ErrorInfo;
import com.kt.gigagenie.pci.net.json.Json;
import com.kt.gigagenie.pci.net.json.JsonArray;
import com.kt.gigagenie.pci.net.json.JsonObject;
import com.kt.gigagenie.pci.net.json.JsonParser;
import com.kt.gigagenie.pci.system.PciProperty;
import com.kt.gigagenie.pci.system.PciRuntimeException;

import org.json.JSONArray;

import java.lang.reflect.Array;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

/**
 * Created by dalkommjk on 2022-02-22.
 */
public class AD_PciSrv_PolicyData {
    private static final String logHeader = "AD_PciSrv_PolicyData";

    private Date expiredDatetime;
    private String beacon_scan_YN = null;
    private int beacon_threshold = 0;
    private String beacon_major = null;
    JsonObject beacon_obj;
    JsonArray minor_arr;
    List<String> minor_list = new ArrayList<String>();
    private int bleCheckOutTerm_Minute; // ble 체크아웃 시간

    public AD_PciSrv_PolicyData(JsonObject dataObj) {
//        String expiredDatetime = dataObj.getString("expired_datetime");

        String expiredDatetime = null;
        SimpleDateFormat datetimeformat = new SimpleDateFormat("yyyyMMddHHmmss");
        Calendar cal = Calendar.getInstance();
        cal.add(Calendar.DATE,+1);
        expiredDatetime = datetimeformat.format(cal.getTime());

        PciProperty property = PciManager.getInstance().getPciProperty();
        String dateFormat = property.getDate_format_pcisrv();
        int propertyTimeExpired = property.getPci_update_time_expired();
        if(propertyTimeExpired <= 0){
            if (expiredDatetime != null && expiredDatetime.length() == 14) {
                SimpleDateFormat dateForm = new SimpleDateFormat(dateFormat);
                try {
                    this.expiredDatetime = dateForm.parse(expiredDatetime);
                } catch (Exception e) {
                    GLog.printExceptWithSaveToPciDB(this,"expiredDatetime parsing error! (recv expiredDatetime : " + expiredDatetime + ")", e, ErrorInfo.CODE_PCI_PLATFORM_DATA_FORMAT_ERR, ErrorInfo.CATEGORY_PCIPLATFORM);
                }
            }
            if (this.expiredDatetime == null) {
                GLog.printExceptWithSaveToPciDB(this,"unknown expiredDatetime!!", new PciRuntimeException("unknown expiredDatetime error! (recv expiredDatetime : " + expiredDatetime + ")"), ErrorInfo.CODE_PCI_PLATFORM_DATA_FORMAT_ERR, ErrorInfo.CATEGORY_PCIPLATFORM);
                this.expiredDatetime = null;
            }
        }else{
            this.expiredDatetime = new Date(Calendar.getInstance().getTimeInMillis() + (propertyTimeExpired * 1000));
        }


        try{
            this.beacon_scan_YN = dataObj.getString("beacon_scan");
        }catch (Exception e){
            GLog.printExceptWithSaveToPciDB(this, "parse beacon_scan failed.", e, ErrorInfo.CODE_PCIAD_PLATFORM_DATA_FORMAT_ERR, ErrorInfo.CATEGORY_PCIADPLATFORM);
        }

        try{
            this.beacon_threshold = dataObj.getInt("beacon_threshold"); // this.idleTimeForUpdate = Integer.parseInt(idleTimeForUpdate);
        }catch (Exception e){
            GLog.printExceptWithSaveToPciDB(this, "parse beacon_threshold failed.", e, ErrorInfo.CODE_PCIAD_PLATFORM_DATA_FORMAT_ERR, ErrorInfo.CATEGORY_PCIADPLATFORM);
        }

        try{

            beacon_obj = (JsonObject)dataObj.get("beacon");
            beacon_obj.get("major");
            this.minor_arr = (JsonArray) beacon_obj.get("minor");
            for(int i=0; i<minor_arr.size(); i++) {
                this.minor_list.add(minor_arr.getString(i));
            }

        }catch (Exception e){
            GLog.printExceptWithSaveToPciDB(this, "parse beacon major minor failed.", e, ErrorInfo.CODE_PCIAD_PLATFORM_DATA_FORMAT_ERR, ErrorInfo.CATEGORY_PCIADPLATFORM);
        }
///////////////////////////
        try{
            voiceCheckOutTerm_Minute = Integer.parseInt(dataObj.getString("checkout_term"));

        }catch (Exception e){
            GLog.printExceptWithSaveToPciDB(this, "parse voiceCheckOutTerm_Minute failed.", e, ErrorInfo.CODE_PCI_PLATFORM_DATA_FORMAT_ERR, ErrorInfo.CATEGORY_PCIPLATFORM);
        }
/////////////////
    }


    public Date getExpiredDatetime() { return expiredDatetime; }
    public String getBeaconScanYN() { return beacon_scan_YN; }
    public int getBeaconThreshold() { return beacon_threshold; }
    public String getBeaconMajor() { return beacon_major; }
    public List getBeaconMinor() { return minor_list; }
    public int getBleCheckOutTerm_Minute() { return bleCheckOutTerm_Minute; }
//    public int getIdleTimeForUpdate() { return idleTimeForUpdate; }
//    public int getWaitingTimeForUpdate() { return waitingTimeForUpdate; }

    public void setExpiredDatetime( Date _expiredDatetime ) { expiredDatetime = _expiredDatetime; }
    public void setBeaconScanYN( String _continuousPlayYn ) { beacon_scan_YN = _continuousPlayYn; }
    public void setBeaconThreshold( int _beaconThreshold ) { beacon_threshold = _beaconThreshold; }
    public void setBeaconMajor( String _beaconMajor ) { beacon_major = _beaconMajor; }
    public void setBeaconMinor( List _beaconMinor ) { minor_list = _beaconMinor; }
    public void setBleCheckOutTerm_Minute( int _bleCheckOutTerm_Minute ) { bleCheckOutTerm_Minute = _bleCheckOutTerm_Minute; }
//    public void setIdleTimeForUpdate( int _idleTimeForUpdate ) { idleTimeForUpdate = _idleTimeForUpdate; }
//    public void setWaitingTimeForUpdate( int _waitingTimeForUpdate ) { waitingTimeForUpdate = _waitingTimeForUpdate; }

    public void destroy(){
        expiredDatetime = null;
        beacon_scan_YN = null;
        beacon_threshold = 0;
        beacon_major = null;
        minor_list = null;

    }


    public void printCurrentPolicy(){
        GLog.printInfo(this, "==========[GetADPolicy Information]=====================================");
        GLog.printInfo(this, "_expiredDatetime > " + HsUtil_Date.getTimeString(expiredDatetime));
        GLog.printInfo(this, "_beacon_scan_YN > " + beacon_scan_YN);
        GLog.printInfo(this, "_beacon_threshold > " + beacon_threshold);
        GLog.printInfo(this, "_beacon_major > " + beacon_major);
        GLog.printInfo(this, "_beacon_minor > " + minor_list);
        GLog.printInfo(this, "_beacon_CheckOutTerm_Minute > " + bleCheckOutTerm_Minute);
        GLog.printInfo(this, "======================================================================");
    }


    public String toString(){
        return logHeader;
    }

}
