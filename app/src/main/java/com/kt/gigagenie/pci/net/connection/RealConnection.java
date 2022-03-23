package com.kt.gigagenie.pci.net.connection;

import com.gnifrix.debug.GLog;
import com.kt.gigagenie.pci.net.NetException;
import com.kt.gigagenie.pci.net.json.JsonException;
import com.kt.gigagenie.pci.net.json.JsonObject;
import com.kt.gigagenie.pci.net.json.JsonParser;
import com.kt.gigagenie.pci.system.PciRuntimeException;

import java.io.BufferedInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.Properties;

/**
 * Created by LeeBaeng on 2018-09-17.
 */

public class RealConnection implements Connection {
    private static final String logHeader = "RealConnection";

    private static SimpleDateFormat _dateForm = null;
    public void log(String msg) {
        GLog.printInfo(this, msg);
    }

    public void log(String msg, Throwable t) {
        GLog.printExcept(this, msg, t);
    }

    public JsonObject httpGet(String tag, String url, Properties header) throws NetException, InterruptedException {
        log("\n<<HTTP-GET|" + tag + ">> Net.httpGet() ↓↓↓↓↓ \n --> URL : " + url + "\n");
        tag = " " + tag +" | ";
        HttpURLConnection httpCon = null;
        try {
            try {
                httpCon = (HttpURLConnection) new URL(url).openConnection();
            } catch (MalformedURLException e) {
                String msg = tag + "create URL instance fail: " + e.getMessage();
                log(msg, e);
                throw new NetException(0x01000, msg, url, e);
            } catch (IOException e) {
                String msg = tag + "open URL connection fail: " + e.getMessage();
                log(msg, e);
                throw new NetException(0x01001, msg, url, e);
            }
            try {
                httpCon.setRequestMethod("GET");
            } catch (ProtocolException e) {
                String msg = tag + "set request method GET fail: " + e.getMessage();
                log(msg, e);
                throw new NetException(0x01002, msg, url, e);
            }
            httpCon.setRequestProperty("Content-Type", "application/json;charset=UTF-8");
            if (header != null && !header.isEmpty()) {
                String[] keys = new String[header.size()];
                header.keySet().toArray(keys);
                for (int i = 0; i < keys.length; i++) {
                    String value = header.getProperty(keys[i]);
                    httpCon.setRequestProperty(keys[i], value);
                    log(tag + "set header: " + keys[i] + " / " + value);
                }
            }
            return getResponse(0x01000, httpCon, url, false);
        } finally {
            if (httpCon != null) {
                httpCon.disconnect();
                log(tag + "HttpURLConnection disconnected(GET)");
            }
        }
    }

    public JsonObject httpPost(String tag, String url, Properties header, JsonObject reqParam, boolean isBeacon) throws NetException, InterruptedException {
        log("\n<<HTTP-POST|" + tag + ">> Net.httpPost() ↓↓↓↓↓ \n --> URL : " + url + "\n" + " --> PARAM : " + (reqParam != null ? reqParam.toJsonString() : "null") + "\n");
        HttpURLConnection httpCon = null;
        try {
            try {
                httpCon = (HttpURLConnection) new URL(url).openConnection();
            } catch (MalformedURLException e) {
                String msg = "create URL instance fail: " + e.getMessage();
                log(msg, e);
                throw new NetException(0x02000, msg, url, e);
            } catch (IOException e) {
                String msg = "open URL connection fail: " + e.getMessage();
                log(msg, e);
                throw new NetException(0x02001, msg, url, e);
            }
            try {
                httpCon.setRequestMethod("POST");
            } catch (ProtocolException e) {
                String msg = "set request method POST fail: " + e.getMessage();
                log(msg, e);
                throw new NetException(0x02002, msg, url, e);
            }
            httpCon.setRequestProperty("Content-Type", "application/json;charset=UTF-8");
            if (header != null && !header.isEmpty()) {
                String[] keys = new String[header.size()];
                header.keySet().toArray(keys);
                for (int i = 0; i < keys.length; i++) {
                    String value = header.getProperty(keys[i]);
                    httpCon.setRequestProperty(keys[i], value);
                    log("set header: " + keys[i] + " / " + value);
                }
            }
            if (reqParam != null) {
                httpCon.setDoOutput(true);
                OutputStream outStrm = null;
                try {
                    outStrm = httpCon.getOutputStream();
                } catch (IOException e) {
                    String msg = "get output stream fail: " + e.getMessage();
                    log(msg, e);
                    throw new NetException(0x02003, msg, url, e);
                }
                try {
                    outStrm.write(reqParam.toJsonString().getBytes("UTF-8"));
                } catch (UnsupportedEncodingException e) {
                    String msg = "encode parameter UTF-8 fail: " + e.getMessage();
                    log(msg, e);
                    throw new NetException(0x02004, msg, url, e);
                } catch (IOException e) {
                    String msg = "write output stream fail: " + e.getMessage();
                    log(msg, e);
                    throw new NetException(0x02005, msg, url, e);
                } finally {
                    try {
                        outStrm.close();
                    } catch (IOException e) {
                        log("close stream fail: " + e.getMessage(), e);
                    }
                }
            }
            return getResponse(0x02000, httpCon, url, isBeacon);
        } finally {
            if (httpCon != null) {
                httpCon.disconnect();
                log("HttpURLConnection disconnected(POST)");
            }
        }
    }

    private JsonObject getResponse(int exceptCodeHdr, HttpURLConnection httpCon, String url, boolean isBeacon) throws NetException {
        try {
            httpCon.setConnectTimeout(5000);
            httpCon.setReadTimeout(5000);
            int resCode = httpCon.getResponseCode();
            if (resCode != HttpURLConnection.HTTP_OK) {
                String msg = "connection response not ok: " + resCode + " (" + httpCon.getResponseMessage() + ")";
                log(msg);
                throw new NetException(exceptCodeHdr + resCode, msg, url, new PciRuntimeException(msg));
            }
        } catch (IOException e) {
            String msg = "get response code fail: " + e.getMessage();
            log(msg, e);
            throw new NetException(exceptCodeHdr + 0x10, msg, url, e);
        }

        InputStream inStrm = null;
        try {
            inStrm = new BufferedInputStream(httpCon.getInputStream());
        } catch (IOException e) {
            String msg = "get input stream fail: " + e.getMessage();
            log(msg, e);
            throw new NetException(exceptCodeHdr + 0x11, msg, url, e);
        }
        ByteArrayOutputStream byteOutStrm = new ByteArrayOutputStream();
        byte[] buf = new byte[4096];
        int size;
        try {
            while ((size = inStrm.read(buf)) != -1) {
                byteOutStrm.write(buf, 0, size);
            }
        } catch (IOException e) {
            String msg = "read input stream bytes fail: " + e.getMessage();
            log(msg, e);
            throw new NetException(exceptCodeHdr + 0x12, msg, url, e);
        } finally {
            try {
                inStrm.close();
                byteOutStrm.close();
            } catch (IOException e) {
                log("close stream fail: " + e.getMessage(), e);
            }
        }
        byte[] data = byteOutStrm.toByteArray();
        if (data == null || data.length == 0) {
            String msg = "return bytes not available";
            log(msg);
            throw new NetException(exceptCodeHdr + 0x13, msg, url, new PciRuntimeException(msg));
        }
        log("response. return " + data.length + " bytes");
        JsonObject resJsonObj;
        try {
            resJsonObj = (JsonObject) JsonParser.parse(new String(data, "UTF-8"));
            log("response --> " + resJsonObj.toJsonString() + "\n");
        } catch (UnsupportedEncodingException e) {
            String msg = "encode response to UTF-8 String fail: " + e.getMessage();
            log(msg, e);
            throw new NetException(exceptCodeHdr + 0x13, msg, url, e);
        } catch (JsonException e) {
            String msg = "parse response to JsonObject fail: " + e.getMessage();
            log(msg, e);
            throw new NetException(exceptCodeHdr + 0x14, msg, url, e);
        }

        return resJsonObj;
        //============== ACAP과 다르게 기가지니는 에러처리를 PciResp Ojbect를 통해 별도 처리(2018-12-17) ==============
//        if (isBeacon) {
//            return resJsonObj;
//        } else {
//            String resCode = resJsonObj.getString("res_code");
//            if (resCode != null && resCode.equalsIgnoreCase("200")) {
//                return (JsonObject) resJsonObj.get("data");
//            } else {
//                String msg = "response fail: " + resCode + ", " + resJsonObj.getString("res_msg");
//                log(msg);
//                throw new NetException(exceptCodeHdr + 0x15, msg, url, new PciRuntimeException(msg));
//            }
//        }
    }

    public String toString(){
        return logHeader;
    }
}
