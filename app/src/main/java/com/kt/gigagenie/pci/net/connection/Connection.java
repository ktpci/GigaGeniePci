package com.kt.gigagenie.pci.net.connection;

import com.kt.gigagenie.pci.net.NetException;
import com.kt.gigagenie.pci.net.json.JsonObject;

import java.util.Properties;

/**
 * Created by LeeBaeng on 2018-09-17.
 */

public interface Connection {
    public JsonObject httpGet(String tag, String url, Properties header) throws NetException, InterruptedException;
    public JsonObject httpPost(String tag, String url, Properties header, JsonObject reqParam, boolean isBeacon) throws NetException, InterruptedException;
}
