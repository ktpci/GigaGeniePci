package com.kt.gigagenie.pci.net.json.pci_response;

import com.gnifrix.debug.GLog;
import com.kt.gigagenie.pci.data.PciSrv_PidData;
import com.kt.gigagenie.pci.data.stb_db.EventPidData;
import com.kt.gigagenie.pci.net.json.JsonArray;
import com.kt.gigagenie.pci.net.json.JsonObject;
import com.kt.gigagenie.pci.system.PciRuntimeException;

import java.util.ArrayList;

/**
 * Created by LeeBaeng on 2018-11-19.
 */

public class PciResp_CheckIn extends PciResp {
    ArrayList<PciSrv_PidData> pidList;
    EventPidData eventData;

    public PciResp_CheckIn(JsonObject jsonObject, boolean isPciSvr){
        super(jsonObject);

//        // 체크인 리스트 테스트 데이터
//        if(isPciSvr){
//            String dateFormat = "yyyyMMddHHmmssSSS";
//            jsonObject = new JsonObject();
//            JsonArray jArr = new JsonArray();
//
//            JsonObject pid = new JsonObject();
//            pid.put("pid", "P0000000000");
//            pid.put("userKey", "U0000000000");
//            pid.put("voiceId", "V0000000000");
//            pid.put("v", HsUtil_Date.getCurrentTimeStr(dateFormat));
//            jArr.add(pid);
//
//            pid = new JsonObject();
//            pid.put("pid", "P0000000001");
//            pid.put("userKey", "U0000000001");
//            pid.put("voiceId", "V0000000001");
//            pid.put("v", HsUtil_Date.getTimeString(new Date(HsUtil_Date.getCurrentTime().getTime() - 5000), dateFormat));
//            pid.put("w", HsUtil_Date.getTimeString(new Date(HsUtil_Date.getCurrentTime().getTime() - 29000), dateFormat));
//            pid.put("p", HsUtil_Date.getTimeString(new Date(HsUtil_Date.getCurrentTime().getTime() - 3000), dateFormat));
//            jArr.add(pid);
//
//            pid = new JsonObject();
//            pid.put("pid", "P0000000002");
//            pid.put("userKey", "U0000000002");
//            pid.put("voiceId", "V0000000002");
//            pid.put("v", HsUtil_Date.getTimeString(new Date(HsUtil_Date.getCurrentTime().getTime() - 10000), dateFormat));
//            pid.put("w", HsUtil_Date.getTimeString(new Date(HsUtil_Date.getCurrentTime().getTime() - 200000000), dateFormat));
//            jArr.add(pid);
//
//            pid = new JsonObject();
//            pid.put("pid", "P0000000003");
//            pid.put("userKey", "U0000000003");
//            pid.put("p", HsUtil_Date.getTimeString(new Date(HsUtil_Date.getCurrentTime().getTime() - 200000), dateFormat));
//            jArr.add(pid);
//
//            JsonObject jList = new JsonObject();
//            jList.put("PIDList", jArr);
//            jsonObject.put("status", jList);
//
//            GLog.printWtf("PciResp_CheckIn", jsonObject.toJsonStringPretty());
//        }

        if(isPciSvr){
            JsonObject jRoot = (JsonObject) jsonObject.get("status");
            JsonArray jArray = (JsonArray) jRoot.get("pidlist");
            if(jArray != null && jArray.size() > 0){
                pidList = new ArrayList<>();
                for (int i=0; i<jArray.size(); i++){
                    GLog.printDbg("PciResp_CheckIn", "jArray["+i+"] : " + jArray.get(i).toJsonString());
                    PciSrv_PidData pidData = new PciSrv_PidData((JsonObject) jArray.get(i), isPciSvr);
                    pidList.add(pidData);
                    GLog.printInfo("PciResp_CheckIn", "Add new PID into pidList. pid=" + pidData.getPid() + " / userkey=" + pidData.getUserKey());
                }

//                if(pidList != null && pidList.size() > 0){
//                    GLog.printInfo("PciResp_CheckIn", "Check-In List(API-6008) >>>>>>>>>>>>>>>>>>>");
//                    for(PciSrv_PidData pidData : pidList){
//                        pidData.printPidData();
//                    }
//                }
            }
        }else{
            // G-Push
            String eventPid = null;
            JsonObject evtJObj = (JsonObject) jsonObject.get("EVENT");
            if(evtJObj != null) eventPid = evtJObj.getString("PID");

            JsonArray jArray = (JsonArray) jsonObject.get("PIDList");
            if(jArray != null && jArray.size() > 0){
                pidList = new ArrayList<>();
                for (int i=0; i<jArray.size(); i++){
                    GLog.printDbg("PciResp_CheckIn", "jArray["+i+"] : " + jArray.get(i).toJsonString());
                    PciSrv_PidData pidData = new PciSrv_PidData((JsonObject) jArray.get(i), isPciSvr);
                    pidList.add(pidData);

                    if(eventPid != null && pidData.getPid().equals(eventPid)){
                        eventData = new EventPidData(pidData);
                    }
                    GLog.printInfo("PciResp_CheckIn", "Add new PID into pidList. pid=" + pidData.getPid() + " / userkey=" + pidData.getUserKey());
                }

//                if(pidList != null && pidList.size() > 0){
//                    GLog.printInfo("PciResp_CheckIn", "Check-In List(PUSH) >>>>>>>>>>>>>>>>>>>");
//                    for(PciSrv_PidData pidData : pidList){
//                        pidData.printPidData();
//                    }
//                }

            }

            if(evtJObj != null && eventData != null && eventPid != null){
                String eventType = evtJObj.getString("EVENT_TYPE");
                String type = evtJObj.getString("TYPE");

                if(eventType != null && type != null){
                    eventData.setIsCheckIn(eventType.equals("IN")? true : false);
                    eventData.setType(type);
                    eventData.printEventData();
                }else{
                    throw new PciRuntimeException("PciResp_CheckIn parse fail. wrong Event >> " + evtJObj.toJsonString());
                    // throw Exception
                }
            }
        }
    }

    public EventPidData getEventData() { return eventData; }
    public ArrayList<PciSrv_PidData> getPidList() { return pidList; }
    public void setEventData( EventPidData _eventData ) { eventData = _eventData; }


    public String getRespValues(){
        StringBuffer bufRes = new StringBuffer(super.getRespValues());
//        String res = super.getRespValues();
        String line = "";
        for (int i=0; i<pidList.size(); i++){
            line = " | pid["+i+"]=" + pidList.get(i).getPid();
            bufRes.append(line);
        }

        return bufRes.toString();
    }

    public void destory(){
        super.destory();
        if(pidList != null && pidList.size() > 0){
            pidList.clear();
        }
        pidList = null;
    }
}
