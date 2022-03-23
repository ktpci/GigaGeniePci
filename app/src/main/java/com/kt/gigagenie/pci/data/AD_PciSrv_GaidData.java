package com.kt.gigagenie.pci.data;

import com.gnifrix.debug.GLog;
import com.kt.gigagenie.pci.data.pci_db.ErrorInfo;
import com.kt.gigagenie.pci.net.json.JsonObject;
import com.kt.gigagenie.pci.system.PciRuntimeException;

import java.util.ArrayList;
import java.util.Collections;

public class AD_PciSrv_GaidData implements Comparable<AD_PciSrv_GaidData> {
    private static final String logHeader = "AD_PciSrv_GaidData";


    String adsrv_gender; // 서버의 gender 정보. 화자인식App에서 잔달되는 값과 서버에서 전달되는 값이 다를 수 있음. 서버 데이터 우선 (화자인식 app에서 전달된 값은 voiceChkInData 참조)
    String adsrv_ageGroup; // 서버의 age_group 정보. 화자인식App에서 잔달되는 값과 서버에서 전달되는 값이 다를 수 있음. 서버 데이터 우선 (화자인식 app에서 전달된 값은 voiceChkInData 참조)

    String gaid;

    CheckInData checkInData_b; // 블루투스 체크인 시간

    private static final String FILEDNAME_ADSrv_GAID = "gaId";
    private static final String FILEDNAME_ADSrv_B = "b";
    private static final String FILEDNAME_ADSrv_GENDER = "gender";
    private static final String FILEDNAME_ADSrv_AGE_GROUP = "old";


    private static final String FILEDNAME_ADPush_GAID = "GAID";
    private static final String FILEDNAME_ADPush_B = "B";
    private static final String FILEDNAME_ADPush_GENDER = "gender";
    private static final String FILEDNAME_ADPush_AGE_GROUP = "old";



    public AD_PciSrv_GaidData(String _gaid){
        gaid = _gaid;
    }

    public AD_PciSrv_GaidData(String _gaid, String _b_CheckInTime){
        gaid = _gaid;
        checkInData_b = new CheckInData(_b_CheckInTime, CheckInData.CHECKIN_TYPE_B);
    }

    public AD_PciSrv_GaidData(JsonObject jsonObject, boolean isPciADSvr){
        if(isPciADSvr) gaid = jsonObject.getString(FILEDNAME_ADSrv_GAID);
        else gaid = jsonObject.getString(FILEDNAME_ADPush_GAID);
        setCheckInInfo(jsonObject, isPciADSvr);
    }

    /**
     * Pci서버로 부터 전달받은 Checkin 정보를 반영한다.
     */
    public void setCheckInInfo(JsonObject jsonObject, boolean isPciADSvr){
        try{
            if(isPciADSvr){
                String adsrvGaid = jsonObject.getString(FILEDNAME_ADSrv_GAID);
                if(adsrvGaid == null)
                    throw new PciRuntimeException("invalid GAID (gaid : " + gaid + " / input : adsrvGaid is null");

                if(!adsrvGaid.equals(gaid))
                    throw new PciRuntimeException("invalid GAID (gaid : " + gaid + " / input : " + adsrvGaid + ")");

                gaid = jsonObject.getString(FILEDNAME_ADSrv_GAID);
                updateBTime(jsonObject.getString(FILEDNAME_ADSrv_B));

                adsrv_gender = jsonObject.getString(FILEDNAME_ADSrv_GENDER);
                adsrv_ageGroup = jsonObject.getString(FILEDNAME_ADSrv_AGE_GROUP);

            }else{
                String adsrvGaid = jsonObject.getString(FILEDNAME_ADPush_GAID);

                if(adsrvGaid == null || gaid == null)
                    throw new PciRuntimeException("invalid GAID (gaid : " + gaid + " / adsrvGaid :" + adsrvGaid + ")");

                if(!adsrvGaid.equals(gaid))
                    throw new PciRuntimeException("invalid GAID (gaid : " + gaid + " / input : " + adsrvGaid + ")");

                updateBTime(jsonObject.getString(FILEDNAME_ADPush_B));

                adsrv_gender = jsonObject.getString(FILEDNAME_ADPush_GENDER);
                adsrv_ageGroup = jsonObject.getString(FILEDNAME_ADPush_AGE_GROUP);
            }

            GLog.printInfo(this, "setCheckInInfo finished. GAID : " + gaid );
        }catch (Exception e){
            GLog.printExceptWithSaveToPciDB(this, "Check-In Info parse fail", e, ErrorInfo.CODE_PCIAD_PLATFORM_DATA_FORMAT_ERR, ErrorInfo.CATEGORY_PCIADPLATFORM);
        }
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
        return ((checkInData_b != null && checkInData_b.getCheckInDate() != null));
    }


    /**
     * 서버로 부터 전송받은 UserKey 및 checkInTime 정보를 갱신 한다.
     * @param _gaid
     * @param _b_CheckInTime
     */
    public void updateData(String _gaid, String _b_CheckInTime){
        gaid = _gaid;
        updateBTime(_b_CheckInTime);
    }


    public void updateBTime(String _b_CheckInTime){
        if(_b_CheckInTime == null) return;
        if(checkInData_b != null) checkInData_b.setCheckInDateStr(_b_CheckInTime);
        else checkInData_b = new CheckInData(_b_CheckInTime, CheckInData.CHECKIN_TYPE_B);
    }

    /**
     * 서버로 부터 전송받은 UserKey 및 checkInTime 정보를 갱신 한다.
     */
    public void updateData(AD_PciSrv_GaidData newData){
        gaid = newData.getGaId();
        if(newData.getCheckInData_b() != null) updateBTime(newData.getCheckInData_b().getCheckInDateStr());
    }

    public void printGaidData(){
        String line = "<<GAID DATA>> gaid : " + gaid + "\n＊Gender : " + adsrv_gender + "\n＊AgeGroup : " + adsrv_ageGroup;
        StringBuffer bufLog = new StringBuffer(line);
        if(checkInData_b != null) bufLog.append(new String("\n＊b-time : " + checkInData_b.getCheckInDateStr()));
        GLog.printInfo(this, bufLog.toString());
    }


    /**
     * Gender정보를 반환한다.
     * Gender정보는 서버정보, 화자인식App 정보가 각각 있으며 우선순위는 1.서버정보 > 2.화자인식App
     * @return Gender정보. 데이터가 없는 경우 null 반환
     */
    public String getGender(){
        if(adsrv_gender != null && !adsrv_gender.trim().equals("")) return adsrv_gender;
        else return null;
    }

    /**
     * AgeGroup정보를 반환한다.
     * AgeGroup정보는 서버정보, 화자인식App 정보가 각각 있으며 우선순위는 1.서버정보 > 2.화자인식App
     * @return AgeGroup정보. 데이터가 없는 경우 null 반환
     */
    public String getAgeGroup(){
        if(adsrv_ageGroup != null && !adsrv_ageGroup.trim().equals("")) return adsrv_ageGroup;
        else return null;
    }



    public String getGaId() { return gaid; }
    public String getSrv_gender() { return adsrv_gender; }
    public String getSrv_ageGroup() { return adsrv_ageGroup; }
    public CheckInData getCheckInData_b() { return checkInData_b; }

    public void setGaId( String _gaid ) { gaid = _gaid; }
    public void setADSrv_gender( String _adsrv_gender ) { adsrv_gender = _adsrv_gender; }
    public void setADSrv_ageGroup( String _adsrv_ageGroup ) { adsrv_ageGroup = _adsrv_ageGroup; }
    public void setCheckInData_b( CheckInData _checkInData_b ) { checkInData_b = _checkInData_b; }







    // ========== 날짜순 정렬을 위한 비교 ==========


    public CheckInData getCompareData(){
        ArrayList<CheckInData> datas = new ArrayList<>();
        if(checkInData_b != null) datas.add(checkInData_b);
        Collections.sort(datas);

        if(datas != null && datas.size() > 0) return datas.get(0);
        else return null;
    }

    public int compareTo(AD_PciSrv_GaidData targetData) {
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
        if(checkInData_b != null){
            checkInData_b.dispose();
        }

        gaid = null;
        adsrv_gender = null;
        adsrv_ageGroup = null;
        checkInData_b = null;
    }
}
