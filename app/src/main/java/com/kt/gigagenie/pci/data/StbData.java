package com.kt.gigagenie.pci.data;

import android.util.Log;

import com.gnifrix.util.AES128Utils;
import com.gnifrix.util.HsUtil_Encrypter;

import java.io.UnsupportedEncodingException;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;

import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;

/**
 * Created by LeeBaeng on 2018-09-17.
 */

public class StbData {
    private String saId;
    private String deviceId;
    private String macAddress;
    private String stbType; // ACAP or WEB or GIGA
    private String modelNumber;
    private String firmwareVersion;
    private String serviceType; // OTV or OTS
    private PciSrv_VoiceChkInData lastPciSrvVoiceChkInData;
    private String appVersion; // by dalkommjk | v15.00.04 | 2021-09-28
    private String taId; // by dalkommjk - v16.00.01 | 2022-02-22

    public String getSaId() { return saId; }
    public String getStbType() { return stbType; }
    public String getDeviceId() { return deviceId; }
    public String getDeviceIdAES128Base64() throws NoSuchPaddingException, InvalidAlgorithmParameterException, UnsupportedEncodingException, IllegalBlockSizeException, BadPaddingException, NoSuchAlgorithmException, InvalidKeyException { return AES128Utils.encAES(deviceId); }
    public String getMacAddress(){ return macAddress; }
    //public String getMacAddressAES128Base64() throws NoSuchPaddingException, InvalidAlgorithmParameterException, UnsupportedEncodingException, IllegalBlockSizeException, BadPaddingException, NoSuchAlgorithmException, InvalidKeyException { return AES128Utils.encAES(macAddress); }
    public String getMacAddressAES128Base64() throws NoSuchPaddingException, InvalidAlgorithmParameterException, UnsupportedEncodingException, IllegalBlockSizeException, BadPaddingException, NoSuchAlgorithmException, InvalidKeyException {
        // mac 주소 오류 방어 코드 by dalkommjk | v15.00.05 | 2021-10-20
        String encMac = AES128Utils.encAES(macAddress);
        if(encMac.equals("lCh9wDpkxyD17wk8q97LmA==")){  // mac이 000000000000 경우 암호화 -> lCh9wDpkxyD17wk8q97LmA==
            android.os.Process.killProcess(android.os.Process.myPid());
        }
        //
        return encMac; }
    public String getTaId() { return taId; } // by dalkommjk - v16.00.01 | 2022-02-22

    public String getModelNumber() { return modelNumber; }
    public String getServiceType() { return serviceType; }
    public String getFirmwareVersion() { return firmwareVersion; }
    public PciSrv_VoiceChkInData getLastCheckInVoiceId() { return lastPciSrvVoiceChkInData; }
    public String getAppVersion() {return appVersion;}; // by dalkommjk | v15.00.04 | 2021-09-28

    public void setSaId( String _saId ) { saId = _saId; }
    public void setStbType( String _stbType ) { stbType = _stbType; }
    public void setDeviceId( String _deviceId ) { deviceId = _deviceId; }
    public void setMacAddress( String _macAddress ) { macAddress = _macAddress; }
    public void setModelNumber( String _modelNumber ) { modelNumber = _modelNumber; }
    public void setServiceType( String _serviceType ) { serviceType = _serviceType; }
    public void setFirmwareVersion( String _firmwareVersion ) { firmwareVersion = _firmwareVersion; }
    public void setLastVoiceChkInData(PciSrv_VoiceChkInData _lastPciSrvVoiceChkInData) { lastPciSrvVoiceChkInData = _lastPciSrvVoiceChkInData; }
    public void setAppVersion(String _appVersion) {appVersion = _appVersion;}; // by dalkommjk | v15.00.04 | 2021-09-28
    public void setTaId( String _taId ) { taId = _taId; } // by dalkommjk - v16.00.01 | 2022-02-22

    public void destroy(){
        saId = null;
        deviceId = null;
        macAddress = null;
        stbType = null;
        modelNumber = null;
        firmwareVersion = null;
        serviceType = null;
        lastPciSrvVoiceChkInData = null;
        appVersion = null; // by dalkommjk | v15.00.04 | 2021-09-28
        taId = null; // by dalkommjk - v16.00.01 | 2022-02-22
    }

}
