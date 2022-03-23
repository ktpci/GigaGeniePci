package com.kt.gigagenie.pci.data;

/**
 * Created by dalkommjk - v16.00.01 | 2022-03-13
 */

public class AD_PciSrv_BleChkInData {
    private String gaId = "";
    private String checkInTime;
    private String action; // I=체크인, O=체크아웃, U=정보업데이트(vid -> userkey)
    private String gender;
    private String old;

    /**
     *
     * @param _gaId
     * @param _checkInTime
     * @param _gender
     * @param _old
     * @param _action I=체크인, O=체크아웃
     */
    public AD_PciSrv_BleChkInData(String _gaId, String _checkInTime, String _action, String _gender, String _old){
        gaId = _gaId;
        checkInTime = _checkInTime;
        action = _action;
        gender = _gender;
        old = _old;
    }

    public void destroy(){
        gaId = null;
        checkInTime = null;
        action = null;
        gender = null;
        old = null;
    }

    public String getGaId() { return gaId; }
    public String getCheckInTime() { return checkInTime; }
    public String getAction() { return action; }
    public String getGender() { return gender; }
    public String getOld() { return old; }

    public String setGaId(String _gaid) { return gaId = _gaid; }
    public void setCheckInTime(String _checkInDate ) { checkInTime = _checkInDate; }
    public void setAction( String _action ) { action = _action; }
    public void setGender( String _gender ) { gender = _gender; }
    public void setOld( String _old ) { old = _old; }

}
