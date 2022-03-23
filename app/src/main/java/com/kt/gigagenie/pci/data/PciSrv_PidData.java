package com.kt.gigagenie.pci.data;

import com.gnifrix.debug.GLog;
import com.kt.gigagenie.pci.data.pci_db.ErrorInfo;
import com.kt.gigagenie.pci.net.json.JsonObject;
import com.kt.gigagenie.pci.system.PciRuntimeException;

import java.util.ArrayList;
import java.util.Collections;

/**
 * Created by LeeBaeng on 2018-11-01.
 */
public class PciSrv_PidData implements Comparable<PciSrv_PidData> {
    private static final String logHeader = "PciSrv_PidData";


    String pid;
    String userKey;
    String voiceId;

    String srv_gender; // 서버의 gender 정보. 화자인식App에서 잔달되는 값과 서버에서 전달되는 값이 다를 수 있음. 서버 데이터 우선 (화자인식 app에서 전달된 값은 voiceChkInData 참조)
    String srv_ageGroup; // 서버의 age_group 정보. 화자인식App에서 잔달되는 값과 서버에서 전달되는 값이 다를 수 있음. 서버 데이터 우선 (화자인식 app에서 전달된 값은 voiceChkInData 참조)

    PciSrv_VoiceChkInData voiceChkInData; // 6005를 통한 체크인이 이루어지지 않은 경우 null

    CheckInData checkInData_p; // 비가청 음원 체크인 시간
    CheckInData checkInData_v; // 보이스 체크인 시간
    CheckInData checkInData_w; // wifi 체크인 시간
    CheckInData checkInData_b; // 블루투스 체크인 시간

    private static final String FILEDNAME_Srv_PID = "p_id";
    private static final String FILEDNAME_Srv_USERKEY = "userKey";
    private static final String FILEDNAME_Srv_VOICEID = "voiceId";
    private static final String FILEDNAME_Srv_P = "p";
    private static final String FILEDNAME_Srv_V = "v";
    private static final String FILEDNAME_Srv_W = "w";
    private static final String FILEDNAME_Srv_B = "b";
    private static final String FILEDNAME_Srv_GENDER = "gender";
    private static final String FILEDNAME_Srv_AGE_GROUP = "age";

    private static final String FILEDNAME_Push_PID = "PID";
    private static final String FILEDNAME_Push_USERKEY = "UserKey";
    private static final String FILEDNAME_Push_P = "P";
    private static final String FILEDNAME_Push_V = "V";
    private static final String FILEDNAME_Push_W = "W";
    private static final String FILEDNAME_Push_B = "B";
    private static final String FILEDNAME_Push_GENDER = "gender";
    private static final String FILEDNAME_Push_AGE_GROUP = "age";

    public static final String FILEDNAME_MC_PID = "PID";
    private static final String FILEDNAME_MC_USERKEY = "UserKey";
    private static final String FILEDNAME_MC_P = "P";
    private static final String FILEDNAME_MC_V = "V";
    private static final String FILEDNAME_MC_W = "W";
    private static final String FILEDNAME_MC_B = "B";
    public static final String FILEDNAME_MC_GENDER = "GENDER_TYPE";
    public static final String FILEDNAME_MC_AGE_GROUP = "AGE_GROUP";

    public PciSrv_PidData(String _pid){
        pid = _pid;
    }

    public PciSrv_PidData(String _pid, String _userKey, String _p_CheckInTime, String _v_CheckInTime, String _w_CheckInTime, String _b_CheckInTime){
        pid = _pid;
        userKey = _userKey;
        checkInData_p = new CheckInData(_p_CheckInTime, CheckInData.CHECKIN_TYPE_P);
        checkInData_v = new CheckInData(_v_CheckInTime, CheckInData.CHECKIN_TYPE_V);
        checkInData_w = new CheckInData(_w_CheckInTime, CheckInData.CHECKIN_TYPE_W);
        checkInData_b = new CheckInData(_b_CheckInTime, CheckInData.CHECKIN_TYPE_B);
    }

    public PciSrv_PidData(JsonObject jsonObject, boolean isPciSvr){
        if(isPciSvr) pid = jsonObject.getString(FILEDNAME_Srv_PID);
        else pid = jsonObject.getString(FILEDNAME_Push_PID);
        setCheckInInfo(jsonObject, isPciSvr);
    }

    /**
     * Pci서버로 부터 전달받은 Checkin 정보를 반영한다.
     */
    public void setCheckInInfo(JsonObject jsonObject, boolean isPciSvr){
        try{
            if(isPciSvr){
                String srvPid = jsonObject.getString(FILEDNAME_Srv_PID);
                if(srvPid == null)
                    throw new PciRuntimeException("invalid PID (pid : " + pid + " / input : srvPid is null");

                if(!srvPid.equals(pid))
                    throw new PciRuntimeException("invalid PID (pid : " + pid + " / input : " + srvPid + ")");

                userKey = jsonObject.getString(FILEDNAME_Srv_USERKEY);
                voiceId = jsonObject.getString(FILEDNAME_Srv_VOICEID);
                updatePTime(jsonObject.getString(FILEDNAME_Srv_P));
                updateVTime(jsonObject.getString(FILEDNAME_Srv_V));
                updateWTime(jsonObject.getString(FILEDNAME_Srv_W));
                updateBTime(jsonObject.getString(FILEDNAME_Srv_B));

                srv_gender = jsonObject.getString(FILEDNAME_Srv_GENDER);
                srv_ageGroup = jsonObject.getString(FILEDNAME_Srv_AGE_GROUP);

            }else{
                String srvPid = jsonObject.getString(FILEDNAME_Push_PID);

                if(srvPid == null || pid == null)
                    throw new PciRuntimeException("invalid PID (pid : " + pid + " / srvPid :" + srvPid + ")");

                if(!srvPid.equals(pid))
                    throw new PciRuntimeException("invalid PID (pid : " + pid + " / input : " + srvPid + ")");

                userKey = jsonObject.getString(FILEDNAME_Push_USERKEY);
                updatePTime(jsonObject.getString(FILEDNAME_Push_P));
                updateVTime(jsonObject.getString(FILEDNAME_Push_V));
                updateWTime(jsonObject.getString(FILEDNAME_Push_W));
                updateBTime(jsonObject.getString(FILEDNAME_Push_B));

                srv_gender = jsonObject.getString(FILEDNAME_Push_GENDER);
                srv_ageGroup = jsonObject.getString(FILEDNAME_Push_AGE_GROUP);
            }

            GLog.printInfo(this, "setCheckInInfo finished. PID : " + pid + " / userKey : " + userKey);
        }catch (Exception e){
            GLog.printExceptWithSaveToPciDB(this, "Check-In Info parse fail", e, ErrorInfo.CODE_PCI_PLATFORM_DATA_FORMAT_ERR, ErrorInfo.CATEGORY_PCIPLATFORM);
        }
    }

    /**
     * MC로 데이터 전달을 하기 위해 체크인 정보를 JsonObject 형태로 반환한다.
     */
    public JsonObject getCheckInInfo(){
        JsonObject jsonObject = new JsonObject();

        jsonObject.put(FILEDNAME_MC_PID, pid);

        if(userKey != null && !userKey.trim().equals("")) jsonObject.put(FILEDNAME_MC_USERKEY, userKey);

        if(srv_gender != null) jsonObject.put(FILEDNAME_MC_GENDER, srv_gender);
        else if(voiceChkInData != null && voiceChkInData.getGender() != null && !voiceChkInData.getGender().equals("")) jsonObject.put(FILEDNAME_MC_GENDER, voiceChkInData.getGender());

        if(srv_ageGroup != null) jsonObject.put(FILEDNAME_MC_AGE_GROUP, srv_ageGroup);
        else if(voiceChkInData != null && voiceChkInData.getAge_group() != null && !voiceChkInData.getAge_group().equals("")) jsonObject.put(FILEDNAME_MC_GENDER, voiceChkInData.getAge_group());

        if(checkInData_p != null && checkInData_p.getCheckInDate() != null && checkInData_p.getCheckInDateStr() != null) jsonObject.put(FILEDNAME_MC_P, checkInData_p.getCheckInDateStr());
        if(checkInData_v != null && checkInData_v.getCheckInDate() != null && checkInData_v.getCheckInDateStr() != null) jsonObject.put(FILEDNAME_MC_V, checkInData_v.getCheckInDateStr());
        if(checkInData_w != null && checkInData_w.getCheckInDate() != null && checkInData_w.getCheckInDateStr() != null) jsonObject.put(FILEDNAME_MC_W, checkInData_w.getCheckInDateStr());
        if(checkInData_b != null && checkInData_b.getCheckInDate() != null && checkInData_b.getCheckInDateStr() != null) jsonObject.put(FILEDNAME_MC_B, checkInData_b.getCheckInDateStr());

        return jsonObject;
    }


    public void checkOut_p() {
        if(checkInData_p != null) checkInData_p.dispose();
        checkInData_p = null;
    }
    public void checkOut_v() {
        if(checkInData_v != null) checkInData_v.dispose();
        checkInData_v = null;
    }
    public void checkOut_w() {
        if(checkInData_w != null) checkInData_w.dispose();
        checkInData_w = null;
    }
    public void checkOut_b() {
        if(checkInData_b != null) checkInData_b.dispose();
        checkInData_b = null;
    }


    /**
     * 현재 체크인된 내역이 있는지 검사. p,v,w,b중 하나라도 체크인 된 내역이 있다면 return true
     * @return
     */
    public boolean isCheckIn(){
        return ((checkInData_p != null && checkInData_p.getCheckInDate() != null) || (checkInData_v != null && checkInData_v.getCheckInDate() != null)
                || (checkInData_w != null && checkInData_w.getCheckInDate() != null) || (checkInData_b != null && checkInData_b.getCheckInDate() != null));
    }



    /**
     * 서버로 부터 전송받은 UserKey 및 checkInTime 정보를 갱신 한다.
     * @param _userKey
     * @param _p_CheckInTime
     * @param _v_CheckInTime
     * @param _w_CheckInTime
     * @param _b_CheckInTime
     */
    public void updateData(String _userKey, String _p_CheckInTime, String _v_CheckInTime, String _w_CheckInTime, String _b_CheckInTime){
        userKey = _userKey;
        updatePTime(_p_CheckInTime);
        updateVTime(_v_CheckInTime);
        updateWTime(_w_CheckInTime);
        updateBTime(_b_CheckInTime);
    }

    public void updatePTime(String _p_CheckInTime){
        if(_p_CheckInTime == null) return;
        if(checkInData_p != null) checkInData_p.setCheckInDateStr(_p_CheckInTime);
        else checkInData_p = new CheckInData(_p_CheckInTime, CheckInData.CHECKIN_TYPE_P);
    }

    public void updateVTime(String _v_CheckInTime){
        if(_v_CheckInTime == null) return;
        if(checkInData_v != null) checkInData_v.setCheckInDateStr(_v_CheckInTime);
        else checkInData_v = new CheckInData(_v_CheckInTime, CheckInData.CHECKIN_TYPE_V);
    }

    public void updateWTime(String _w_CheckInTime){
        if(_w_CheckInTime == null) return;
        if(checkInData_w != null) checkInData_w.setCheckInDateStr(_w_CheckInTime);
        else checkInData_w = new CheckInData(_w_CheckInTime, CheckInData.CHECKIN_TYPE_W);
    }

    public void updateBTime(String _b_CheckInTime){
        if(_b_CheckInTime == null) return;
        if(checkInData_b != null) checkInData_b.setCheckInDateStr(_b_CheckInTime);
        else checkInData_b = new CheckInData(_b_CheckInTime, CheckInData.CHECKIN_TYPE_B);
    }

    /**
     * 서버로 부터 전송받은 UserKey 및 checkInTime 정보를 갱신 한다.
     */
    public void updateData(PciSrv_PidData newData){
        userKey = newData.getUserKey();
        if(newData.getCheckInData_p() != null) updatePTime(newData.getCheckInData_p().getCheckInDateStr());
        if(newData.getCheckInData_v() != null) updateVTime(newData.getCheckInData_v().getCheckInDateStr());
        if(newData.getCheckInData_w() != null) updateWTime(newData.getCheckInData_w().getCheckInDateStr());
        if(newData.getCheckInData_b() != null) updateBTime(newData.getCheckInData_b().getCheckInDateStr());
    }


    public void printPidData(){
        String line = "<<PID DATA>> pid : " + pid + "\n＊userKey : " + userKey + "\n＊voiceID : " + voiceId + "\n＊Gender : " + srv_gender + "\n＊AgeGroup : " + srv_ageGroup;
        StringBuffer bufLog = new StringBuffer(line);
        if(checkInData_p != null) bufLog.append(new String("\n＊p-time : " + checkInData_p.getCheckInDateStr()));
        if(checkInData_v != null) bufLog.append(new String("\n＊v-time : " + checkInData_v.getCheckInDateStr()));
        if(checkInData_w != null) bufLog.append(new String("\n＊w-time : " + checkInData_w.getCheckInDateStr()));
        if(checkInData_b != null) bufLog.append(new String("\n＊b-time : " + checkInData_b.getCheckInDateStr()));
        if(voiceChkInData!= null){
            bufLog.append(new String("\n＊gender_v : " + voiceChkInData.getGender() + "\n＊ageGroup_v : " + voiceChkInData.getAge_group()));
        }
        GLog.printInfo(this, bufLog.toString());
    }


    /**
     * Gender정보를 반환한다.
     * Gender정보는 서버정보, 화자인식App 정보가 각각 있으며 우선순위는 1.서버정보 > 2.화자인식App
     * @return Gender정보. 데이터가 없는 경우 null 반환
     */
    public String getGender(){
        if(srv_gender != null && !srv_gender.trim().equals("")) return srv_gender;
        else if(voiceChkInData.getGender() != null && !voiceChkInData.getGender().trim().equals("")) return voiceChkInData.getGender();
        else return null;
    }

    /**
     * AgeGroup정보를 반환한다.
     * AgeGroup정보는 서버정보, 화자인식App 정보가 각각 있으며 우선순위는 1.서버정보 > 2.화자인식App
     * @return AgeGroup정보. 데이터가 없는 경우 null 반환
     */
    public String getAgeGroup(){
        if(srv_ageGroup != null && !srv_ageGroup.trim().equals("")) return srv_ageGroup;
        else if(voiceChkInData.getAge_group() != null && !voiceChkInData.getAge_group().trim().equals("")) return voiceChkInData.getAge_group();
        else return null;
    }


    public String getPid() { return pid; }
    public String getUserKey() { return userKey; }
    public String getVoiceId() { return voiceId; }
    public String getSrv_gender() { return srv_gender; }
    public String getSrv_ageGroup() { return srv_ageGroup; }
    public CheckInData getCheckInData_p() { return checkInData_p; }
    public CheckInData getCheckInData_v() { return checkInData_v; }
    public CheckInData getCheckInData_w() { return checkInData_w; }
    public CheckInData getCheckInData_b() { return checkInData_b; }
    public PciSrv_VoiceChkInData getVoiceChkInData() { return voiceChkInData; }

    public void setPid( String _pid ) { pid = _pid; }
    public void setUserKey( String _userKey ) { userKey = _userKey; }
    public void setVoiceId( String _voiceId ) { voiceId = _voiceId; }
    public void setSrv_gender( String _srv_gender ) { srv_gender = _srv_gender; }
    public void setSrv_ageGroup( String _srv_ageGroup ) { srv_ageGroup = _srv_ageGroup; }
    public void setCheckInData_p( CheckInData _checkInData_p ) { checkInData_p = _checkInData_p; }
    public void setCheckInData_v( CheckInData _checkInData_v ) { checkInData_v = _checkInData_v; }
    public void setCheckInData_w( CheckInData _checkInData_w ) { checkInData_w = _checkInData_w; }
    public void setCheckInData_b( CheckInData _checkInData_b ) { checkInData_b = _checkInData_b; }
    public void setVoiceChkInData( PciSrv_VoiceChkInData _voiceChkInData ) { voiceChkInData = _voiceChkInData; }






    // ========== 날짜순 정렬을 위한 비교 ==========


    public CheckInData getCompareData(){
        ArrayList<CheckInData> datas = new ArrayList<>();
        if(checkInData_p != null) datas.add(checkInData_p);
        if(checkInData_v != null) datas.add(checkInData_v);
        if(checkInData_w != null) datas.add(checkInData_w);
        if(checkInData_b != null) datas.add(checkInData_b);
        Collections.sort(datas);

        if(datas != null && datas.size() > 0) return datas.get(0);
        else return null;
    }

    public int compareTo(PciSrv_PidData targetData) {
        CheckInData checkInDataSrc = getCompareData();
        CheckInData checkInDataDst = targetData.getCompareData();

        if(checkInDataSrc == null || checkInDataSrc.getCheckInDateStr() == null || checkInDataSrc.getCheckInDate() == null) return 1;
        if(checkInDataDst == null || checkInDataDst.getCheckInDateStr() == null || checkInDataDst.getCheckInDate() == null) return -1;
        return checkInDataDst.compareTo(checkInDataSrc);
    }



    public String toString(){
        return logHeader;
    }


    public void destory(){
        if(voiceChkInData != null) voiceChkInData.destroy();
        if(checkInData_p != null){
            checkInData_p.dispose();
        }
        if(checkInData_v != null){
            checkInData_v.dispose();
        }
        if(checkInData_w != null){
            checkInData_w.dispose();
        }
        if(checkInData_b != null){
            checkInData_b.dispose();
        }

        pid = null;
        userKey = null;
        voiceId = null;
        srv_gender = null;
        srv_ageGroup = null;
        voiceChkInData = null;
        checkInData_p = null;
        checkInData_v = null;
        checkInData_w = null;
        checkInData_b = null;
    }
}
