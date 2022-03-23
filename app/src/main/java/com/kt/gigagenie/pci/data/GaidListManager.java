package com.kt.gigagenie.pci.data;

import com.gnifrix.debug.GLog;
import com.kt.gigagenie.pci.data.pci_db.ErrorInfo;
import com.kt.gigagenie.pci.net.json.JsonArray;
import com.kt.gigagenie.pci.net.json.JsonObject;
import com.kt.gigagenie.pci.system.PciRuntimeException;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;

public class GaidListManager {
    public static final String logHeader = "GaidListManager";

    private HashMap<String, AD_PciSrv_GaidData> checkedInList;
    public GaidListManager(){
        checkedInList = new HashMap<>();
    }


    /**
     * PCI서버를 통해 전달받은 체크인 리스트에 맞춰 리스트를 업데이트 하고, 체크인된 GAID List를 갱신한다.
     * @param chekcedInGaidList
     */
    public void updateCheckInGaidList(ArrayList<AD_PciSrv_GaidData> chekcedInGaidList, String caller){
        clearCheckInList();

        for(AD_PciSrv_GaidData newGaidData : chekcedInGaidList){
            checkedInList.put(newGaidData.getGaId(), newGaidData);
        }
        printCheckInList(caller);
    }


    public AD_PciSrv_GaidData getGaidDataByGAID(String gaid){
        if(checkedInList.containsKey(gaid)) return checkedInList.get(gaid);
        return null;
    }



    public void checkOut(String gaid, int eventType){
        if(checkedInList != null){
            if(checkedInList.containsKey(gaid)){
                AD_PciSrv_GaidData gaidData = checkedInList.get(gaid);
                switch (eventType){
                    case CheckInData.CHECKIN_TYPE_B : gaidData.checkOut_b(); break;
                    default: break;
                }

                if(!gaidData.isCheckIn()){
                    checkedInList.remove(gaid);
                    GLog.printInfo(this, "check-out complete !! > " + gaid + " / eventType : " + CheckInData.getCheckInTypeStr(eventType) + " // nothing more check-in data. remove from checkedIn-List");
                }else{
                    GLog.printInfo(this, "check-out complete !! > " + gaid + " / eventType : " + CheckInData.getCheckInTypeStr(eventType));
                }
            }else{
                GLog.printWtf(this, gaid + " remove from Check-In List failed(Check-out Fail), GAID=[" + gaid + "] not found in checkedInList" + " / eventType : " + CheckInData.getCheckInTypeStr(eventType));
//                printCheckInList(this.toString());
            }
        }else{
            GLog.printExceptWithSaveToPciDB(this, gaid + "", new PciRuntimeException("Remove from Check-In List failed(Check-out Fail), GAID=[" + gaid + "] / checkedInList is null"), ErrorInfo.CODE_INAPP_CHECKOUT_FAIL, ErrorInfo.CATEGORY_INAPP);
        }
    }

    public void printCheckInList(String caller){
        GLog.printInfo(this, "<<<<<<<<<<<<<<<<<<< printCheckInList::" + caller + " >>>>>>>>>>>>>>>>>>>");
        printList(checkedInList);
        GLog.printInfo(this, "<<<<<<<<<<<<<<<<<<< [Finish] printCheckInList::" + caller + " >>>>>>>>>>>>>>>>>>>");
    }

    public void printList(HashMap<String, AD_PciSrv_GaidData> list){
        if(list == null){
            GLog.printWtf(this, "list is null. can't print list.");
            return;
        }

        for(String key : list.keySet()){
            AD_PciSrv_GaidData gaidData = list.get(key);
            gaidData.printGaidData();
        }
    }


    public HashMap<String, AD_PciSrv_GaidData> getCheckedInList() { return checkedInList; }




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
