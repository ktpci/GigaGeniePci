package com.kt.gigagenie.pci.data.stb_db;

import com.gnifrix.debug.GLog;

/**
 * Created by LeeBaeng on 2018-10-23.
 */
public class StbDb_Common {
    public static final String COLUMN_NAME_DEVSERVICEID = "devServiceId";
    public static final String COLUMN_NAME_DEVMACID = "devMacId";
    public static final String COLUMN_NAME_USERKEY = "userKey";
    public static final String COLUMN_NAME_SAID = "said";
    public static final String COLUMN_NAME_GIGAGENIE = "gigagenie";
    public static final String COLUMN_NAME_OTV = "otv";
    public static final String COLUMN_NAME_TAID = "taid"; // AD PCI by dalkommjk - v16.00.01 | 2022-02-22


    /** 기가지니 서비스 ID */
    String devServiceId;
    /** MAC address (무인증일 때 사용) */
    String devMacId;
        /** 현재 사용중인 userkey(사용자 식별자) */
    String userKey;
    /** 올레tv 셋탑 개통에 따른 SAID*/
    String said;
    /** false:미가입,  true:가입*/
    boolean gigagenie;
    /** false:미가입,  true:가입*/
    boolean otv;
    /** otv 광고ID */
    String taid;  // AD PCI by dalkommjk - v16.00.01 | 2022-02-22


    public StbDb_Common(String _devServiceId, String _devMacId, String _userKey, String _said, boolean _gigagenie, boolean _otv){
        devServiceId = _devServiceId;
        devMacId = _devMacId;
        devMacId = devMacId.replace(":","");

        userKey = _userKey;
        said = _said;
        gigagenie = _gigagenie;
        otv = _otv;
    }

    /** AD PCI by dalkommjk - v16.00.01 | 2022-02-22 */

    public StbDb_Common(String _taId){
        taid = _taId;
    }


    public String getDevServiceId() { return devServiceId; }
    public String getDevMacId() { return devMacId; }
    public String getUserKey() { return userKey; }
    public String getSaid() { return said; }
    public boolean getGigagenie() { return gigagenie; }
    public boolean getOtv() { return otv; }
    public String getTaid() { return taid; }   // AD PCI by dalkommjk - v16.00.01 | 2022-02-22

    public void setDevServiceId( String _devServiceId ) { devServiceId = _devServiceId; }
    public void setDevMacId( String _devMacId ) { devMacId = _devMacId; }
    public void setUserKey( String _userKey ) { userKey = _userKey; }
    public void setSaid( String _said ) { said = _said; }
    public void setGigagenie( boolean _gigagenie ) { gigagenie = _gigagenie; }
    public void setOtv( boolean _otv ) { otv = _otv; }
    public void setTaid( String _taid ) { taid = _taid; }  // AD PCI by dalkommjk - v16.00.01 | 2022-02-22

    /**
     * target과 정보가 일치하는지 확인
     * @param target 비교할 StbDb_Common Object
     * @return 일치하는 경우 true, 불일치할 경우 false
     */
    public boolean compare(StbDb_Common target){
        boolean result = true;

        if(target == null) return false;

        if(devServiceId != null) if(!devServiceId.equals(target.getDevServiceId())) result = false;
        if(devMacId != null) if(!devMacId.equals(target.getDevMacId())) result = false;
        if(userKey != null) if(!userKey.equals(target.getUserKey())) result = false;
        if(said != null) if(!said.equals(target.getSaid())) result = false;
        if(gigagenie != target.getGigagenie()) result = false;
        if(otv != target.getOtv()) result = false;

        return result;
    }

    public String toString(){
        return "StbDb_Common";
    }

    public void printInfo(){
        GLog.printInfo(this,
                "devServiceId : " + devServiceId +
                        ",  devMacId : " + devMacId +
                        ",  userKey : " + userKey +
                        ",  said : " + said +
                        ",  gigagenie : " + gigagenie +
                        ",  otv : " + otv
        );
    }

}

