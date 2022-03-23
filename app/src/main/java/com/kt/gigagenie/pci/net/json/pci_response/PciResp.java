package com.kt.gigagenie.pci.net.json.pci_response;

import com.gnifrix.debug.GLog;
import com.kt.gigagenie.pci.net.json.JsonObject;

/**
 * Created by LeeBaeng on 2018-11-19.
 */

public class PciResp {
    int res_code = -1;
    String res_msg = null;
    boolean isSucceeded = false;

    public PciResp(JsonObject jsonObject){
        res_code = jsonObject.getInt("res_code");
        res_msg = jsonObject.getString("res_msg");
        if(res_code == 200) isSucceeded = true;
    }


    public int getRes_code() { return res_code; }
    public String getRes_msg() { return res_msg; }
    public boolean isSuccessed() { return isSucceeded; }
    public void printResp(String tag){
        GLog.printInfo(tag, getRespValues());
    }

    public String getRespValues(){
        return new StringBuffer("res_code=" + res_code + " / res_msg=" + res_msg + " / isSucceeded=" + isSucceeded).toString();
    }

    public void destory(){
        res_msg = null;
    }
}
