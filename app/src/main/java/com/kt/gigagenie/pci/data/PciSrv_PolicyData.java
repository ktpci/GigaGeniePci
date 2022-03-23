package com.kt.gigagenie.pci.data;

import com.gnifrix.debug.GLog;
import com.gnifrix.util.HsUtil_Date;
import com.kt.gigagenie.pci.PciManager;
import com.kt.gigagenie.pci.data.pci_db.ErrorInfo;
import com.kt.gigagenie.pci.net.json.JsonObject;
import com.kt.gigagenie.pci.system.PciProperty;
import com.kt.gigagenie.pci.system.PciRuntimeException;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

/**
 * Created by LeeBaeng on 2018-09-19.
 */
public class PciSrv_PolicyData {
    private static final String logHeader = "PciSrv_PolicyData";

    private String continuousPlayYn; // 비가청 On/OFF 여부. Y=ON, N=OFF, OFF=화면(메뉴)숨김
    private Date expiredDatetime;
    private int idleTimeForUpdate;
    private int waitingTimeForUpdate;
    private boolean soundUpdateYn;
    private int voiceCheckOutTerm_Minute; // 보이스 체크아웃 시간(분 단위, 체크인 한 시간으로 부터 voiceCheckOutTerm_Minute이 경과할 경우 체크아웃 처리)

    public PciSrv_PolicyData(JsonObject dataObj) {
        continuousPlayYn = dataObj.getString("continuous_play_yn");
        String expiredDatetime = dataObj.getString("expired_datetime");

        PciProperty property = PciManager.getInstance().getPciProperty();
        String dateFormat = property.getDate_format_pcisrv();
        int propertyTimeExpired = property.getPci_update_time_expired();
        int propertyTimeIdle = property.getPci_update_time_idle();
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


        String idleTimeForUpdate = null;
        idleTimeForUpdate = (propertyTimeIdle <= 0)? dataObj.getString("idle_time_for_update") : HsUtil_Date.getTimeString(new Date(HsUtil_Date.getCurrentTime().getTime() + propertyTimeIdle), "HHmmss");
        if (idleTimeForUpdate != null && idleTimeForUpdate.length() == 6) {
            try {
                this.idleTimeForUpdate = Integer.parseInt(idleTimeForUpdate);
            } catch (Exception e) {
                GLog.printExceptWithSaveToPciDB(this, "init IdleTime failed.. (recv idleTimeForUpdate : " + idleTimeForUpdate + ")" , e, ErrorInfo.CODE_PCI_PLATFORM_DATA_FORMAT_ERR, ErrorInfo.CATEGORY_PCIPLATFORM);
            }
        }

        int propertyWaitTime = property.getPci_update_wait_time();
        if(propertyWaitTime <= 0){
            String waitingTimeForUpdate = dataObj.getString("waiting_time_for_update");
            if (waitingTimeForUpdate != null) {
                try {
                    this.waitingTimeForUpdate = Integer.parseInt(waitingTimeForUpdate);
                } catch (Exception e) {
                    this.waitingTimeForUpdate = 10;
                }
            }
        }else{
            this.waitingTimeForUpdate = propertyWaitTime;
        }


        String soundUpdateYn = dataObj.getString("sound_update_yn");
        if (soundUpdateYn != null && soundUpdateYn.equalsIgnoreCase("Y")) {
            this.soundUpdateYn = true;
        } else {
            this.soundUpdateYn = false;
        }

        try{
            voiceCheckOutTerm_Minute = Integer.parseInt(dataObj.getString("checkout_term"));

        }catch (Exception e){
            GLog.printExceptWithSaveToPciDB(this, "parse voiceCheckOutTerm_Minute failed.", e, ErrorInfo.CODE_PCI_PLATFORM_DATA_FORMAT_ERR, ErrorInfo.CATEGORY_PCIPLATFORM);
        }
    }

//    /**
//     * 서버로 부터 전송받은 IdleTime(Type:String)을 Date 형식으로 반환 한다.
//     * 만약 업데이트 시각이 이미 경과한 경우(실행되는 시간 기준) 다음날로 변경.
//     */
//    public Date initIdleTime(String idleTimeForUpdate){
//        if (idleTimeForUpdate != null && idleTimeForUpdate.length() == 6) {
//            try {
//                Calendar cal = Calendar.getInstance();
//                Calendar updatetime = Calendar.getInstance();
//                int hour = Integer.parseInt(idleTimeForUpdate.substring(0,1));
//                int minute = Integer.parseInt(idleTimeForUpdate.substring(2,3));
//                int second = Integer.parseInt(idleTimeForUpdate.substring(4,5));
//
//                updatetime.set(Calendar.HOUR_OF_DAY, hour);
//                updatetime.set(Calendar.MINUTE, minute);
//                updatetime.set(Calendar.SECOND, second);
//                updatetime.set(Calendar.MILLISECOND, 0);
//
//                if(cal.getTimeInMillis() > updatetime.getTimeInMillis()){
//                    updatetime.set(Calendar.DATE, updatetime.get(Calendar.DAY_OF_MONTH) + 1);
//                }
//
//                this.idleTimeForUpdate = updatetime.getTime();
//            } catch (Exception e) {
//                GLog.printExceptWithSaveToPciDB(this, "init IdleTime failed.. (recv idleTimeForUpdate : " + idleTimeForUpdate + ")" , e, ErrorInfo.CODE_PCI_PLATFORM_DATA_FORMAT_ERR, ErrorInfo.CATEGORY_PCIPLATFORM);
//                this.idleTimeForUpdate = null;
//            }
//        }
//        return this.idleTimeForUpdate;
//    }

//    /**
//     * 업데이트 루프에 의해 업데이트가 완료 된 직후 업데이트 날짜를 내일로 설정한다.
//     * idleTimeForUpdate object가 null인 경우 idleTimeForUpdate을 현재시간으로 설정(업데이트 완료 직후 시점)
//     */
//    public Date idleTimeSetToNextDay() throws PciRuntimeException{
//        if(idleTimeForUpdate == null){
//            idleTimeForUpdate = HsUtil_Date.getCurrentTime();
//        }
//        Calendar cal = Calendar.getInstance();
//        cal.setTime(idleTimeForUpdate);
//        cal.set(Calendar.DATE, cal.get(Calendar.DAY_OF_MONTH) + 1);
//        idleTimeForUpdate = cal.getTime();
//
//        return idleTimeForUpdate;
//    }


    public boolean getSoundUpdateYn() { return soundUpdateYn; }
    public Date getExpiredDatetime() { return expiredDatetime; }
    public int getIdleTimeForUpdate() { return idleTimeForUpdate; }
    public String getContinuousPlayYn() { return continuousPlayYn; }
    public int getWaitingTimeForUpdate() { return waitingTimeForUpdate; }
    public int getVoiceCheckOutTerm_Minute() { return voiceCheckOutTerm_Minute; }

    public void setSoundUpdateYn( boolean _soundUpdateYn ) { soundUpdateYn = _soundUpdateYn; }
    public void setExpiredDatetime( Date _expiredDatetime ) { expiredDatetime = _expiredDatetime; }
    public void setContinuousPlayYn( String _continuousPlayYn ) { continuousPlayYn = _continuousPlayYn; }
    public void setIdleTimeForUpdate( int _idleTimeForUpdate ) { idleTimeForUpdate = _idleTimeForUpdate; }
    public void setWaitingTimeForUpdate( int _waitingTimeForUpdate ) { waitingTimeForUpdate = _waitingTimeForUpdate; }
    public void setVoiceCheckOutTerm_Minute( int _voiceCheckOutTerm_Minute ) { voiceCheckOutTerm_Minute = _voiceCheckOutTerm_Minute; }

    public void destroy(){
        continuousPlayYn = null;
        expiredDatetime = null;
    }


    public void printCurrentPolicy(){
        GLog.printInfo(this, "==========[GetPolicy Information]=====================================");
        GLog.printInfo(this, "_continuousPlayYn > " + continuousPlayYn);
        GLog.printInfo(this, "_expiredDatetime > " + HsUtil_Date.getTimeString(expiredDatetime));
        GLog.printInfo(this, "_idleTimeForUpdate > " + idleTimeForUpdate);
        GLog.printInfo(this, "_waitingTimeForUpdate > " + waitingTimeForUpdate);
        GLog.printInfo(this, "_soundUpdateYn > " + soundUpdateYn);
        GLog.printInfo(this, "voiceCheckOutTerm_Minute > " + voiceCheckOutTerm_Minute);
        GLog.printInfo(this, "======================================================================");
    }


    public String toString(){
        return logHeader;
    }

}
