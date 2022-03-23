package com.kt.gigagenie.pci.net.json.pci_response;

import com.kt.gigagenie.pci.net.json.JsonObject;

/**
 * Created by LeeBaeng on 2018-11-19.
 */

public class PciResp_Data extends PciResp {
    JsonObject data;

    public PciResp_Data(JsonObject jsonObject){
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
