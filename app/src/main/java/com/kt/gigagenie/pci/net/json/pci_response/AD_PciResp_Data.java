package com.kt.gigagenie.pci.net.json.pci_response;

import com.kt.gigagenie.pci.net.json.JsonObject;

public class AD_PciResp_Data extends AD_PciResp{
    JsonObject data;

    public AD_PciResp_Data(JsonObject jsonObject){
        super(jsonObject);
        data = (JsonObject) jsonObject.get("data");
    }

    public JsonObject getData() {
        return data;
    }

    public String getRespValues(){
        return super.getRespValues() + " / data=" + data.toJsonStringPretty();
    }

    public void destory(){
        super.destory();
        data = null;
    }
}
