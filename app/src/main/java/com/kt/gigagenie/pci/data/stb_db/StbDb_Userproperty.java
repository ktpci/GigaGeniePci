package com.kt.gigagenie.pci.data.stb_db;

import com.gnifrix.debug.GLog;

/**
 * Created by LeeBaeng on 2018-10-23.
 */

public class StbDb_Userproperty {
    public static final String COLUMN_NAME_USERKEY = "userkey";
    public static final String COLUMN_NAME_USERNICKNAME = "usernickname";
    public static final String COLUMN_NAME_VOICEHISTORY = "voiceHistory";
    public static final String COLUMN_NAME_SPEAKERAUTH = "speakerAuth";
    public static final String COLUMN_NAME_SPEAKERKIND = "speakerKind";

    /** 사용자 식별자(사용자 고유 ID) */
    String userkey;
    /** 유저 닉네임 */
    String usernickname;
    /** false: 음성 History 미등록, true: 음성History 등록 */
    boolean voiceHistory;
    /** false: 화자인증 목소리 미등록, true: 화자인증 목소리 등록 */
    boolean speakerAuth;
    /** 확인 필요 */
    String speakerKind;


    public StbDb_Userproperty(String _userkey, String _usernickname, boolean _voiceHistory, boolean _speakerAuth){
        userkey = _userkey;
        usernickname = _usernickname;
        voiceHistory = _voiceHistory;
        speakerAuth = _speakerAuth;
    }


    public boolean getVoiceHistory() { return voiceHistory; }
    public String getUsernickname() { return usernickname; }
    public boolean getSpeakerAuth() { return speakerAuth; }
    public String getSpeakerKind() { return speakerKind; }
    public String getUserkey() { return userkey; }

    public void setUserkey( String _userkey ) { userkey = _userkey; }
    public void setSpeakerKind( String _speakerKind ) { speakerKind = _speakerKind; }
    public void setSpeakerAuth( boolean _speakerAuth ) { speakerAuth = _speakerAuth; }
    public void setUsernickname( String _usernickname ) { usernickname = _usernickname; }
    public void setVoiceHistory( boolean _voiceHistory ) { voiceHistory = _voiceHistory; }


    public void printInfo(){
        GLog.printInfo(this,
                        ",  userKey : " + userkey +
                                ",  userNickname : " + usernickname +
                                ",  voiceHistory : " + voiceHistory +
                                ",  speakerAuth : " + speakerAuth +
                                ",  speakerKind : " + speakerKind
        );
    }

    public String toString(){
        return "StbDb_Userproperty";
    }

}
