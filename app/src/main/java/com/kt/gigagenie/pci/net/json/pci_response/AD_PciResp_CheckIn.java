package com.kt.gigagenie.pci.net.json.pci_response;

import com.gnifrix.debug.GLog;
import com.kt.gigagenie.pci.data.AD_PciSrv_GaidData;
import com.kt.gigagenie.pci.data.PciSrv_PidData;
import com.kt.gigagenie.pci.data.stb_db.AD_EventGaidData;
import com.kt.gigagenie.pci.data.stb_db.EventPidData;
import com.kt.gigagenie.pci.net.json.JsonArray;
import com.kt.gigagenie.pci.net.json.JsonObject;
import com.kt.gigagenie.pci.system.PciRuntimeException;

import java.util.ArrayList;

public class AD_PciResp_CheckIn extends PciResp {
    ArrayList<AD_PciSrv_GaidData> gaidList;
    AD_EventGaidData eventData;

    public AD_PciResp_CheckIn(JsonObject jsonObject, boolean isPciADSvr){
        super(jsonObject);



        if(isPciADSvr){
//            JsonObject jRoot = (JsonObject) jsonObject.get("status");
//            JsonArray jArray = (JsonArray) jRoot.get("pidlist");
            JsonObject jRoot = (JsonObject) jsonObject.get("data");
            JsonArray jArray = (JsonArray) jRoot.get("list");
            if(jArray != null && jArray.size() > 0){
                gaidList = new ArrayList<>();
                for (int i=0; i<jArray.size(); i++){
                    GLog.printDbg("AD_PciResp_CheckIn", "jArray["+i+"] : " + jArray.get(i).toJsonString());
                    AD_PciSrv_GaidData gaidData = new AD_PciSrv_GaidData((JsonObject) jArray.get(i), isPciADSvr);
                    gaidList.add(gaidData);
                    GLog.printInfo("PciResp_CheckIn", "Add new GAID into gaidList. gaid=" + gaidData.getGaId() );
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

        }
    }

    public ArrayList<AD_PciSrv_GaidData> getGaidList() { return gaidList; }

    public String getRespValues(){
        StringBuffer bufRes = new StringBuffer(super.getRespValues());
//        String res = super.getRespValues();
        String line = "";
        for (int i=0; i<gaidList.size(); i++){
            line = " | gaid["+i+"]=" + gaidList.get(i).getGaId();
            bufRes.append(line);
        }

        return bufRes.toString();
    }

    public void destory(){
        super.destory();
        if(gaidList != null && gaidList.size() > 0){
            gaidList.clear();
        }
        gaidList = null;
    }
}
