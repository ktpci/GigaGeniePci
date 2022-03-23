package com.gnifrix.util.timechecker;

import com.gnifrix.debug.GLog;
import com.gnifrix.util.HsUtil_Date;

import java.util.Date;
import java.util.HashMap;

/**
 * Created by LeeBaeng on 2019-01-30.
 */

public class TimeChecker {
    public static final String LOG_HEADER = "TimeChecker";
    HashMap<String, CheckInfo> checkList = null;

    public TimeChecker(){
        checkList = new HashMap<>();
    }


    public void startTimeCheck(CheckInfo checkInfo){
        if(checkList != null && !checkList.containsKey(checkInfo.getId())){
            checkInfo.start(System.currentTimeMillis());
            checkList.put(checkInfo.getId(), checkInfo);
        }else{
            GLog.printInfo(LOG_HEADER, "Can't Start TimeCheck. Duplicated checkInfo's ID. > " + checkInfo.getId());
        }
    }

    public void startTimeCheck(String checkId){
        if(checkList != null && !checkList.containsKey(checkId)){
            startTimeCheck(new CheckInfo(checkId));
        }else{
            GLog.printInfo(LOG_HEADER, "Can't Start TimeCheck. Duplicated checkInfo's ID. > " + checkId);
        }

    }

    private void finishTimeCheck(CheckInfo checkInfo){
        if(checkInfo != null){
            if(checkInfo.getStartTime() != -1){
                checkInfo.end(System.currentTimeMillis());
            }
        }
    }

    public void finishTimeCheck(String checkInfoTag){
        if(checkList != null && checkList.containsKey(checkInfoTag)){
            finishTimeCheck(checkList.get(checkInfoTag));
        }
    }

    public CheckInfo getCheckInfo(String checkInfoTag){
        return checkList.get(checkInfoTag);
    }

    public void destroy(){
        if(checkList != null){
            checkList.clear();
        }
        checkList = null;
    }

    public void clearList(){
        if(checkList != null){
            checkList.clear();
        }
    }

    public void removeCheckInfo(String id){
        if(checkList != null && checkList.containsKey(id)){
            checkList.remove(id);
        }
    }

    public void printTimeCheckList(){
        if(checkList != null && checkList.size() > 0){
            for(String key : checkList.keySet()){
                CheckInfo checkInfo = checkList.get(key);
                GLog.printInfo(LOG_HEADER, checkInfo.getId() + " :: LeadTime=" + checkInfo.getLeadTime() +
                        " / S="+ HsUtil_Date.getTimeString(new Date(checkInfo.getStartTime())) +
                        " / E="+ HsUtil_Date.getTimeString(new Date(checkInfo.getEndTime())) +
                        " STATE="+checkInfo.getState());
            }
        }else{
            GLog.printInfo(LOG_HEADER, "TimeChecker is empty !!!!!!");
        }
    }

    public double convertToSeconds(long time){
        return time / 1000;
    }
}
