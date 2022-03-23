package com.kt.gigagenie.pci.data.stb_db;

import android.app.Activity;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;

import com.gnifrix.debug.GLog;
import com.kt.gigagenie.pci.Global;
import com.kt.gigagenie.pci.PciManager;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by YunHD on 2018-10-11.
 * Edited by LeeBaeng on 2018-10-23. 기가지니 STB용으로 변환
 */
public class StbDb {
    private static String AUTHORITY = "com.kt.gigagenie.launcher.provider";
    private static Uri AUTHORITY_URI = Uri.parse("content://" + AUTHORITY);

    private ArrayList<StbDb_Common> listCommon = new ArrayList<>();
    private ArrayList<StbDb_Userproperty> listUserproperty = new ArrayList<>();
    StbDb_Userproperty currentUserProperty = null;

    // MainActivity에 activity를 전달 받아서 사용 (Content Provider의 query를 사용하기 위해 필요)
    public StbDb(Context context) {
        refreshDbInfo(context);
        print_CommonDb();
        print_Userproperty();
    }

    /**
     * Common DB Object를 반환한다.
     * (Common DB는 STB상 1개의 row만 존재할 경우에만 사용,
     * 그외엔 getListCommon를 통해 리스트중 사용할 row 선택후 사용)
     */
    public StbDb_Common getStbDbCommon() {
        if(listCommon != null && listCommon.size() > 0)
            return listCommon.get(0);
        else
            return null;
    }
    public ArrayList<StbDb_Common> getListCommon() { return listCommon; }
    public ArrayList<StbDb_Userproperty> getListUserproperty() { return listUserproperty; }
    public int getCommonCount(){ if(listCommon != null) return listCommon.size(); else return -1; }
    public int getUserPropertyCount(){ if(listUserproperty != null) return listUserproperty.size(); else return -1; }


    public void print_CommonDb(){
        GLog.printInfo(this, "========= Common DB =========");
        if(listCommon != null){
            for(StbDb_Common stbDb_common : listCommon){
                stbDb_common.printInfo();
            }
        }
    }

    public void print_Userproperty(){
        GLog.printInfo(this, "========= UserProperty DB =========");
        if(listUserproperty != null){
            for(StbDb_Userproperty stbDb_userproperty : listUserproperty){
                stbDb_userproperty.printInfo();
            }
        }


        GLog.printInfo(this, "========= Current UserProperty DB =========");
        if(getCurrentUserProperty() != null) getCurrentUserProperty().printInfo();
    }

    public StbDb_Userproperty getCurrentUserProperty(){
        if(listUserproperty != null && listUserproperty.size() > 0 && listCommon != null && listCommon.size() > 0){
            for(StbDb_Userproperty stbDbUserproperty : listUserproperty){
                if(stbDbUserproperty.getUserkey().equals(listCommon.get(0).getUserKey())){
                    currentUserProperty = stbDbUserproperty;
                    break;
                }
            }
        }

        return currentUserProperty;
    }

    /**
     * STB DB정보를 불러온다.
     * Common table 값이 이전에 있던 값과 다를경우(정보 변동사항이 있을 경우) return true
     */
    public boolean refreshDbInfo(Context context){
        StbDb_Common prevCommonData = null;
        ArrayList<StbDb_Userproperty> prevUserPropertyList = null;


        if(listCommon != null && listCommon.size() > 0){
            prevCommonData = listCommon.get(0);
            listCommon.clear();
        }
        if(listUserproperty != null && listUserproperty.size() > 0){
            prevUserPropertyList = (ArrayList<StbDb_Userproperty>) listUserproperty.clone();
            listUserproperty.clear();
        }

        // common DB datas
        StbDb_Common stbDb_common;
        String devServiceId;
        String devMacId;
        String userKey;
        String said;
        boolean gigagenie;
        boolean otv;

        // user property DB datas
        StbDb_Userproperty stbDb_userproperty;
        String userkey;
        String usernickname;
        boolean voiceHistory;
        boolean speakerAuth;

        Cursor cur = context.getContentResolver().query(Uri.withAppendedPath(AUTHORITY_URI, "common"), null, null, null, null);
        Cursor cur_up = context.getContentResolver().query(Uri.withAppendedPath(AUTHORITY_URI, "userproperty"), null, null, null, null);

        if(Global.getInstance().getPciProperty() == null || !Global.getInstance().getPciProperty().getAppPlatform().equalsIgnoreCase("stb") || cur == null){
            devServiceId = "D00000000tTTt0tTtTTT";
            devMacId = "00:00:00:00:00:00";
            userKey = "A0000000000";
            said = "TT000000000";
            gigagenie = true;
            otv = true;
            stbDb_common = new StbDb_Common(devServiceId, devMacId, userKey, said, gigagenie, otv);
            listCommon.add(stbDb_common);

            userkey = "A0000000000";
            usernickname = "에뮬테스트";
            voiceHistory = false;
            speakerAuth = false;

            stbDb_userproperty = new StbDb_Userproperty(userkey, usernickname, voiceHistory, speakerAuth);
            stbDb_userproperty.setSpeakerKind("0000000000");

            if(listUserproperty != null)
                listUserproperty.add(stbDb_userproperty);
        }else {
            // Common Table(공통 DB) 정보를 HashMap에 저장
            while (cur.moveToNext()) {
                devServiceId = cur.getString(cur.getColumnIndex(StbDb_Common.COLUMN_NAME_DEVSERVICEID));
                devMacId = cur.getString(cur.getColumnIndex(StbDb_Common.COLUMN_NAME_DEVMACID));
                userKey = cur.getString(cur.getColumnIndex(StbDb_Common.COLUMN_NAME_USERKEY));
                said = cur.getString(cur.getColumnIndex(StbDb_Common.COLUMN_NAME_SAID));
                gigagenie = (cur.getInt(cur.getColumnIndex(StbDb_Common.COLUMN_NAME_GIGAGENIE)) == 1);
                otv = (cur.getInt(cur.getColumnIndex(StbDb_Common.COLUMN_NAME_OTV)) == 1);
                stbDb_common = new StbDb_Common(devServiceId, devMacId, userKey, said, gigagenie, otv);

                if(listCommon != null)
                    listCommon.add(stbDb_common);
            }
            cur.close();

            // UserProperty Table(사용자정보 DB) 정보를 HashMap에 저장
            while (cur_up.moveToNext()) {
                userkey = cur_up.getString(cur_up.getColumnIndex(StbDb_Userproperty.COLUMN_NAME_USERKEY));
                usernickname = cur_up.getString(cur_up.getColumnIndex(StbDb_Userproperty.COLUMN_NAME_USERNICKNAME));
                voiceHistory = (cur_up.getInt(cur_up.getColumnIndex(StbDb_Userproperty.COLUMN_NAME_VOICEHISTORY)) == 1);
                speakerAuth = (cur_up.getInt(cur_up.getColumnIndex(StbDb_Userproperty.COLUMN_NAME_SPEAKERAUTH)) == 1);
                stbDb_userproperty = new StbDb_Userproperty(userkey, usernickname, voiceHistory, speakerAuth);
                stbDb_userproperty.setSpeakerKind(cur_up.getString(cur_up.getColumnIndex(StbDb_Userproperty.COLUMN_NAME_SPEAKERKIND)));

                if(listUserproperty != null)
                    listUserproperty.add(stbDb_userproperty);
            }
            cur_up.close();

        }

        if(PciManager.getInstance().getDataManager() != null)
            PciManager.getInstance().getDataManager().updateStbData();


        //================ 이전 데이터와 변동사항이 있는지 검사 =================
        StbDb_Common curCommonData = getStbDbCommon();
        if(curCommonData != null && prevCommonData != null){
            boolean result = !prevCommonData.compare(curCommonData);

            // common에서 이미 변동값이 있을 경우에는 up(userProperty)는 검사 안함(result == true). common이 변동값이 없을 경우에만(result == false) up 검사
            if(!result){
                if(prevUserPropertyList != null && prevUserPropertyList.size() >0 && listUserproperty != null && listUserproperty.size() > 0){
                    ArrayList<String> userKeyListPrev = new ArrayList<>();
                    ArrayList<String> userKeyListCur = new ArrayList<>();

                    for(StbDb_Userproperty up : listUserproperty){
                        userKeyListCur.add(up.getUserkey());
                    }

                    for(StbDb_Userproperty up : prevUserPropertyList){
                        userKeyListPrev.add(up.getUserkey());
                    }

                    if(userKeyListCur.size() == userKeyListPrev.size() && userKeyListCur.containsAll(userKeyListPrev)){
                        result = false;
                    }else{
                        result = true;
                    }
                }
            }
            GLog.printInfo(this, "compare STB Information. > isChanged?"+result);
            return result;
        }else{
            return true;
        }
    }

    public void destroy() {
        if(listCommon != null){
            listCommon.clear();
            listCommon = null;
        }
        if(listUserproperty != null){
            listUserproperty.clear();
            listUserproperty = null;
        }
    }

    public String toString(){
        return "StbDb";
    }
}
