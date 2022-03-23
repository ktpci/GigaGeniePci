package com.gnifrix.system;

import android.app.Activity;
import android.content.ContextWrapper;
import android.content.res.Resources;
import android.net.Uri;
import android.os.Build;
import android.util.Log;

import com.gnifrix.debug.GLog;
import com.gnifrix.util.HsUtil_Date;
import com.gnifrix.util.HsUtil_String;
import com.kt.gigagenie.pci.Pci_Service;
import com.kt.gigagenie.pci.R;
import com.kt.gigagenie.pci.data.pci_db.ErrorInfo;
import com.kt.gigagenie.pci.system.PciRuntimeException;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import javax.microedition.khronos.opengles.GL;

/**
 * Created by LeeBaeng on 2018-09-04.
 */
public abstract class GniProperty implements AppContext{
    private static final String logHeader = "GniProperty";

    protected String appName = "";
    protected String versionCode = "", versionName = "", localVersion = "";
    protected String releaseDate = "";

    public GniProperty(ContextWrapper context){
        try{
            appName = context.getResources().getString(R.string.app_name);
            versionCode = context.getPackageManager().getPackageInfo(context.getPackageName(), 0).versionCode + "";
            versionName = context.getPackageManager().getPackageInfo(context.getPackageName(), 0).versionName;
            localVersion = context.getResources().getString(R.string.app_local_version);

            String[] splitStr = localVersion.split("\\|");
            if(splitStr != null && splitStr.length == 2 && splitStr[0].length() == 10 && splitStr[1].length() == 11){
                try{
                    localVersion = splitStr[0];
                    splitStr[1] = "20" + splitStr[1]; // 20 = 년도
                    int year = Integer.parseInt(splitStr[1].substring(0, 4));
                    int month = Integer.parseInt(splitStr[1].substring(4, 6));
                    int day = Integer.parseInt(splitStr[1].substring(6, 8));
                    int hour = Integer.parseInt(splitStr[1].substring(9, 11));
                    int min = Integer.parseInt(splitStr[1].substring(11, 13));

                    releaseDate = year +"/" +  String.format("%02d",month) + "/"  + String.format("%02d", day) + " " +  String.format("%02d", hour) + ":" +  String.format("%02d",min);
                }catch (Exception ex){
                    Log.e(this.toString(), "parse version info manifest failed.", ex);
                    throw new PciRuntimeException("Wrong Version type. check Manifest file, and check version format.");
                }

            }else{
                throw new PciRuntimeException("Wrong Version type. check Manifest file, and check version format.");
            }

        }catch (Exception e){
            if(appName.equals("")) appName = "read manifest failed";
            if(versionCode.equals("")) versionCode = "read manifest failed";
            if(versionName.equals("")) versionName = "read manifest failed";
            if(releaseDate.equals("")) releaseDate = "read manifest failed";

            Log.e(this.toString(), "read manifest failed.", e);
//            e.printStackTrace();
//            GLog.printExceptWithSaveToPciDB(this, "read manifest failed :: " + e.getMessage(), e, ErrorInfo.CODE_MANIFEST_PARSE_FAIL, ErrorInfo.CATEGORY_ETC);
        }
    }


    public void destroy(){
        dispose();
        appName = null;
        versionCode = null;
        versionName = null;
        releaseDate = null;
    }

    public abstract void dispose();



    public String getAppName() { return appName; }
    public String getVersionCode() { return versionCode; }
    public String getVersionName() { return versionName; }
    public String getReleaseDate() { return releaseDate; }
    public String getVersionString(){
        return "v." + versionName + "|" + releaseDate;
    }
    public String getLocalVersion() { return localVersion; }


    public void printInitString(){
        GLog.printInfo(this, "┌" + getAppendedStr("─", "─") + "");
        GLog.printInfo(this, "│ DEVICE : " + Build.DEVICE);
        GLog.printInfo(this, "│ BOARD : " + Build.BOARD);
        GLog.printInfo(this, "│ BRAND : " + Build.BRAND);
        GLog.printInfo(this, "│ DISPLAY : " + Build.DISPLAY);
        GLog.printInfo(this, "│ HARDWARE : " + Build.HARDWARE);
        GLog.printInfo(this, "│ ID : " + Build.ID);
        GLog.printInfo(this, "│ MANUFACTURER : " + Build.MANUFACTURER);
        GLog.printInfo(this, "│ MODEL : " + Build.MODEL);
        GLog.printInfo(this, "│ PRODUCT : " + Build.PRODUCT);
        GLog.printInfo(this, "│ TAGS : " + Build.TAGS);
        GLog.printInfo(this, "│ TYPE : " + Build.TYPE);
        GLog.printInfo(this, "│ USER : " + Build.USER);
        GLog.printInfo(this, "│ RELEASE : " + Build.VERSION.RELEASE + "(" + Build.VERSION.CODENAME + ")");
        GLog.printInfo(this, "└" + getAppendedStr("─", "─"));
    }

    protected String getAppendedStr(String srcStr, String appendStr){
        String appendedStr = HsUtil_String.getAppendedStr(srcStr, appendStr, INIT_STRING_LENGTH_MAX);
        return appendedStr;
    }

    public String toString(){
        return logHeader;
    }
}
