package com.kt.gigagenie.pci.data;

import android.os.Build;
import android.util.Log;

import com.gnifrix.debug.GLog;
import com.kt.gigagenie.pci.BuildConfig;
import com.kt.gigagenie.pci.Global;
import com.kt.gigagenie.pci.PciManager;
import com.kt.gigagenie.pci.data.stb_db.StbDb;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;

/**
 * Created by LeeBaeng on 2018-09-20.
 */
public class DataManager {
    private PciSrvData pciSrvData;

    private StbData stbData;
    private SndSvrData sndSvrData;
    private StbDb stbDb;
    private PidListManager pidManager;

    public DataManager(){
        pciSrvData = new PciSrvData(this);
        pidManager = new PidListManager();
        stbData = new StbData();
        sndSvrData = null;
        if(PciManager.isInitialized() && Global.getInstance().getPciService() != null)
            stbDb = new StbDb(Global.getInstance().getPciService());
        else if(Global.getInstance().getMainActivity() != null)
            stbDb = new StbDb(Global.getInstance().getPciService());

        if(stbDb != null){
            String firmVersion = "UNKNOWN";
            int currentapiVersion = android.os.Build.VERSION.SDK_INT;
            /**↓↓↓ CT1107 에서 펌웨어 버전 조회 안되는 부분 수정(CT1107_v8.0.0) - by dalkommJK | 2019-11-22 ↓↓↓*/
            try {
                if (currentapiVersion >= 26) {
                    Process ps = Runtime.getRuntime().exec("/system/bin/getprop ro.build.version.incremental");   // ro.build.version.incremental  //ro.mainsoftware.ver
                    BufferedReader br = new BufferedReader(new InputStreamReader(ps.getInputStream()));
                    firmVersion = br.readLine();
                    /**↓↓↓ atv 적용 수정 - by dalkommJK | 2021-09-04 ↓↓↓*/
                    if(firmVersion.length() < 10 ) {
                        GLog.printInfo("DataManager", " ==> mainsoftware.ver : " + firmVersion );
                    }
                    else if ( 9 < firmVersion.length() ) {
                        //firmVersion = firmVersion.substring(10,18); //  펌웨어 길이조절 by dalkommjk | 2021-01-29
                        firmVersion = firmVersion.substring(firmVersion.length() - 8, firmVersion.length()); //펌웨어 길이조절 by dalkommjk | v15.00.05 | 2021-10-20
                        GLog.printInfo("DataManager", " ==> mainsoftware.ver : " + firmVersion);
                    }else{
                        firmVersion = "UNKNOWN";
                    }
                    /** ------------------------------------------------ */
                }else{
                    Process ps = Runtime.getRuntime().exec("/system/bin/getprop ro.mainsoftware.ver");   // ro.build.version.incremental  //ro.mainsoftware.ver
                    BufferedReader br = new BufferedReader(new InputStreamReader(ps.getInputStream()));
                    firmVersion = br.readLine();
                    GLog.printInfo("DataManager", " ==> mainsoftware.ver : " + firmVersion);

                }
            }
            catch(Exception e) {
                e.printStackTrace();
            }
            /**↑↑↑ CT1107 에서 펌웨어 버전 조회 안되는 부분 수정(CT1107_v8.0.0) - by dalkommJK | 2019-11-22 ↑↑↑*/
            stbData.setSaId(stbDb.getStbDbCommon().getSaid());
            stbData.setDeviceId(stbDb.getStbDbCommon().getDevServiceId());
            stbData.setMacAddress(stbDb.getStbDbCommon().getDevMacId());
            stbData.setStbType("GIGA");
            stbData.setModelNumber(Build.MODEL);
            stbData.setFirmwareVersion(firmVersion);
            stbData.setServiceType("OTV");  // OTV 고정?
            stbData.setAppVersion(BuildConfig.VERSION_NAME); // by dalkommjk | 15.00.04 | 2021-09-28
        }
    }


    /**
     * StbDb_Common 데이터를 StbData에 update
     */
    public void updateStbData(){
        if(stbDb != null && stbDb.getStbDbCommon() != null){
            stbData.setSaId(stbDb.getStbDbCommon().getSaid());
            stbData.setDeviceId(stbDb.getStbDbCommon().getDevServiceId());
            stbData.setMacAddress(stbDb.getStbDbCommon().getDevMacId());
        }else{
            GLog.printInfo(this, "updateStbData Failed !!. stbDb or stbDbCommon is null!");
        }
    }


    public StbDb getStbDb(){ return stbDb; }
    public StbData getStbData() { return stbData; }
    public PciSrvData getPciSrvData() { return pciSrvData; }
    public SndSvrData getSndSvrData() { return sndSvrData; }
    public PidListManager getPidManager() { return pidManager; }

    public void setStbDb( StbDb _stbDb ) { stbDb = _stbDb; }
    public void setStbData( StbData _stbData ) { stbData = _stbData; }
    public void setPciSrvData( PciSrvData _pciSrvData ) { pciSrvData = _pciSrvData; }
    public void setSndSvrData(SndSvrData _sndSvrData) { sndSvrData = _sndSvrData; }
    public void setPidManager( PidListManager _pidManager ) { pidManager = _pidManager; }

    public void destroy(){
        if(pciSrvData != null){
            pciSrvData.destroy();
            pciSrvData = null;
        }
        if(stbData != null){
            stbData.destroy();
            stbData = null;
        }
        if(sndSvrData != null){
            sndSvrData.destroy();
            sndSvrData = null;
        }

        if(stbDb != null){
            stbDb.destroy();
            stbDb = null;
        }

        if(pidManager != null){
            pidManager.destroy();
            pidManager = null;
        }

    }

}
