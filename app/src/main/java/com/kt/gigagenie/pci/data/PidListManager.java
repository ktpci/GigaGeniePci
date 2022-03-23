package com.kt.gigagenie.pci.data;

import com.gnifrix.debug.GLog;
import com.kt.gigagenie.pci.data.pci_db.ErrorInfo;
import com.kt.gigagenie.pci.net.json.JsonArray;
import com.kt.gigagenie.pci.net.json.JsonObject;
import com.kt.gigagenie.pci.system.PciRuntimeException;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;

/**
 * PID 리스트 관리
 * Created by LeeBaeng on 2018-11-20.
 */
public class PidListManager {
    public static final String logHeader = "PidListManager";

//    private HashMap<String, PciSrv_PidData> pidList; // 체크아웃된 pid 정보까지 포함 → PIDList삭제(주석처리) 클라이언트는 Check-In된 리스트만 관리.(2019.02.13) cause 서버에 pid가 변경될 수 있음(확률매칭)
    private HashMap<String, PciSrv_PidData> checkedInList; // Key = PID, 현재 체크인된 PID 리스트(체크아웃된 pid 정보 제외), 외부에서 접근 불가능(체크인, 체크아웃을 통해서 리스트만 관리)


    public PidListManager(){
        checkedInList = new HashMap<>();
    }


    /**
     * PCI서버를 통해 전달받은 체크인 리스트에 맞춰 리스트를 업데이트 하고, 체크인된 PID List를 갱신한다.
     * @param chekcedInPidList
     */
    public void updateCheckInPidList(ArrayList<PciSrv_PidData> chekcedInPidList, String caller){
        clearCheckInList();

        for(PciSrv_PidData newPidData : chekcedInPidList){
            checkedInList.put(newPidData.getPid(), newPidData);
        }
        printCheckInList(caller);
    }



    public PciSrv_PidData getPidDataByVoiceID(String voiceId){
        for( String key : checkedInList.keySet() ){
            if(checkedInList.get(key) != null && checkedInList.get(key).getVoiceId() != null && checkedInList.get(key).getVoiceId().equals(voiceId)){
                return checkedInList.get(key);
            }
        }
        return null;
    }

    public PciSrv_PidData getPidDataByUserKey(String userKey){
        for( String key : checkedInList.keySet() ){
            if(checkedInList.get(key) != null && checkedInList.get(key).getUserKey() != null && checkedInList.get(key).getUserKey().equals(userKey)){
                return checkedInList.get(key);
            }
        }
        return null;
    }


    public PciSrv_PidData getPidDataByPID(String pid){
        if(checkedInList.containsKey(pid)) return checkedInList.get(pid);
        return null;
    }


    public void checkOut(String pid, int eventType){
        if(checkedInList != null){
            if(checkedInList.containsKey(pid)){
                PciSrv_PidData pidData = checkedInList.get(pid);
                switch (eventType){
                    case CheckInData.CHECKIN_TYPE_W : pidData.checkOut_w(); break;
                    case CheckInData.CHECKIN_TYPE_V : pidData.checkOut_v(); break;
                    case CheckInData.CHECKIN_TYPE_P : pidData.checkOut_p(); break;
                    case CheckInData.CHECKIN_TYPE_B : pidData.checkOut_b(); break;
                    default: break;
                }

                if(!pidData.isCheckIn()){
                    checkedInList.remove(pid);
                    GLog.printInfo(this, "check-out complete !! > " + pid + " / eventType : " + CheckInData.getCheckInTypeStr(eventType) + " // nothing more check-in data. remove from checkedIn-List");
                }else{
                    GLog.printInfo(this, "check-out complete !! > " + pid + " / eventType : " + CheckInData.getCheckInTypeStr(eventType));
                }
            }else{
                GLog.printWtf(this, pid + " remove from Check-In List failed(Check-out Fail), PID=[" + pid + "] not found in checkedInList" + " / eventType : " + CheckInData.getCheckInTypeStr(eventType));
//                printCheckInList(this.toString());
            }
        }else{
            GLog.printExceptWithSaveToPciDB(this, pid + "", new PciRuntimeException("Remove from Check-In List failed(Check-out Fail), PID=[" + pid + "] / checkedInList is null"), ErrorInfo.CODE_INAPP_CHECKOUT_FAIL, ErrorInfo.CATEGORY_INAPP);
        }
    }

    public void printCheckInList(String caller){
        GLog.printInfo(this, "<<<<<<<<<<<<<<<<<<< printCheckInList::" + caller + " >>>>>>>>>>>>>>>>>>>");
        printList(checkedInList);
        GLog.printInfo(this, "<<<<<<<<<<<<<<<<<<< [Finish] printCheckInList::" + caller + " >>>>>>>>>>>>>>>>>>>");
    }

    public void printList(HashMap<String, PciSrv_PidData> list){
        if(list == null){
            GLog.printWtf(this, "list is null. can't print list.");
            return;
        }

        for(String key : list.keySet()){
            PciSrv_PidData pidData = list.get(key);
            pidData.printPidData();
        }
    }


    public HashMap<String, PciSrv_PidData> getCheckedInList() { return checkedInList; }

    /**
     * MC 등에 Check-In List 전달을 위해 JsonObject 를 생성하여 반환한다.
     * @param eventPidData 이벤트가 발생한 PID 데이터
     * @param eventType P or V or W or B
     * @param isCheckIn true : Check-In 이벤트, false : Check-Out 이벤트
     * @return
     */
    public JsonObject getPidList_JObj(PciSrv_PidData eventPidData, String eventType, boolean isCheckIn){
        JsonObject jObj = new JsonObject();

        if(eventPidData != null && eventType != null){
            JsonObject evtJobj = eventPidData.getCheckInInfo();
            evtJobj.put("TYPE", eventType);
            evtJobj.put("EVENT_TYPE", isCheckIn? "IN" : "OUT");
            evtJobj.put(PciSrv_PidData.FILEDNAME_MC_PID, eventPidData.getPid());

//            if(eventPidData.getSrv_gender() != null) evtJobj.put(PciSrv_PidData.FILEDNAME_MC_GENDER, eventPidData.getSrv_gender());
//            else if(eventPidData.getVoiceChkInData() != null && eventPidData.getVoiceChkInData() .getGender() != null && !eventPidData.getVoiceChkInData() .getGender().equals("")) evtJobj.put(PciSrv_PidData.FILEDNAME_MC_GENDER, eventPidData.getVoiceChkInData() .getGender());
//            if(eventPidData.getSrv_ageGroup() != null) evtJobj.put(PciSrv_PidData.FILEDNAME_MC_AGE_GROUP, eventPidData.getSrv_ageGroup());
//            else if(eventPidData.getVoiceChkInData()  != null && eventPidData.getVoiceChkInData() .getAge_group() != null && !eventPidData.getVoiceChkInData() .getAge_group().equals("")) evtJobj.put(PciSrv_PidData.FILEDNAME_MC_GENDER, eventPidData.getVoiceChkInData() .getAge_group());
//            if(eventPidData.getUserKey() != null) evtJobj.put("UserKey", eventPidData.getUserKey());

            jObj.put("EVENT", evtJobj);
        }

        ArrayList<PciSrv_PidData> sortedPidList = new ArrayList<>();

        for( String key : checkedInList.keySet() ){
            sortedPidList.add(checkedInList.get(key));
        }

        // 정렬을 클라이언트에서 할 경우 아래 구문 수행. 정렬을 서버에서 할 경우 정렬없이 수행
        Collections.sort(sortedPidList, Collections.reverseOrder());

        JsonArray jArray = new JsonArray();
        for( PciSrv_PidData pidData : sortedPidList ){
            jArray.add(pidData.getCheckInInfo());
        }
        jObj.put("PIDList", jArray);

        GLog.printInfo(this,"PidList To JsonObjet : " + jObj.toJsonString());

        return jObj;
    }


    public void clearCheckInList(){
        if(checkedInList != null){
            for(String key : checkedInList.keySet()){
                checkedInList.get(key).destory();
            }
            checkedInList.clear();
        }
    }

    public void destroy(){
        clearCheckInList();
        checkedInList = null;

    }

    public String toString(){
        return logHeader;
    }
}
