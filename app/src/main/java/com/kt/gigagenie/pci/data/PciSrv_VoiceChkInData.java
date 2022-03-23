package com.kt.gigagenie.pci.data;

/**
 * Voice Check in Data
 * Created by LeeBaeng on 2018-10-05.
 */
public class PciSrv_VoiceChkInData {
    private String voiceId = "";
    private String userKey = "";
    private String eventTime;
    private String action; // I=체크인, O=체크아웃, U=정보업데이트(vid -> userkey)
    private String gender;
    private String age_group;

    /**
     *
     * @param _voiceId
     * @param _userKey
     * @param _eventTime
     * @param _action I=체크인, O=체크아웃, U=정보업데이트(vid -> userkey)
     */
    public PciSrv_VoiceChkInData(String _voiceId, String _userKey, String _eventTime, String _action, String _gender, String _age_group){
        voiceId = _voiceId;
        userKey = _userKey;
        eventTime = _eventTime;
        action = _action;
        gender = _gender;
        age_group = _age_group;
    }

    public void destroy(){
        userKey = null;
        voiceId = null;
        eventTime = null;
        action = null;
        gender = null;
        age_group = null;
    }

    public String getAction() { return action; }
    public String getGender() { return gender; }
    public String getUserKey() { return userKey; }
    public String getVoiceId() { return voiceId; }
    public String getEventTime() { return eventTime; }
    public String getAge_group() { return age_group; }

    public void setAction( String _action ) { action = _action; }
    public void setGender( String _gender ) { gender = _gender; }
    public void setVoiceId( String _voiceId ) { voiceId = _voiceId; }
    public void setUserKey( String _userKey ) { userKey = _userKey; }
    public void setAge_group( String _age_group ) { age_group = _age_group; }
    public void setEventTime(String _checkInDate ) { eventTime = _checkInDate; }

}
