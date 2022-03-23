package com.kt.gigagenie.pci.net.connection;

import com.kt.gigagenie.pci.net.NetException;
import com.kt.gigagenie.pci.net.json.JsonObject;

import java.util.Properties;

/**
 * Created by LeeBaeng on 2018-09-17.
 */

public class InterruptedConnection implements Connection{
    private static final String logHeader = "InterruptedConnection";

    public JsonObject httpGet(String tag, String url, Properties header) throws NetException, InterruptedException {
        throw new InterruptedException("http GET interrupted");
    }

    public JsonObject httpPost(String tag, String url, Properties header, JsonObject reqParam, boolean isBeacon) throws NetException, InterruptedException {
        throw new InterruptedException("http POST interrupted");
    }

    public String toString(){
        return logHeader;
    }
}
