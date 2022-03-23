package com.gnifrix.debug;

import android.util.Log;
import android.view.View;
import android.widget.CheckBox;
import android.widget.TextView;

import com.gnifrix.system.AppContext;
import com.gnifrix.util.HsUtil_Date;
import com.kt.gigagenie.pci.Global;
import com.kt.gigagenie.pci.data.pci_db.ErrorInfo;
import com.kt.gigagenie.pci.data.pci_db.PciDB;
import com.kt.gigagenie.pci.system.PciRuntimeException;

import java.util.ArrayList;

/**
 * @author LeeBaeng
 * Created by LeeBaeng on 2018-09-04.
 */
public class GLog {
    public static final int LOG_LEVEL_TEST        =   0;
    public static final int LOG_LEVEL_DEBUG       =   1;
    public static final int LOG_LEVEL_INFO        =   2;
    public static final int LOG_LEVEL_WARN        =   3;
    public static final int LOG_LEVEL_ERROR       =   4;
    public static final int LOG_LEVEL_EXCEPT      =   5;
    public static final int LOG_LEVEL_NONE        =   6;
    public static final int LOG_LEVEL_SYSTEM      =   7;

    private static final String logHeader = AppContext.APP_TAG;
    private static ArrayList<GniLog> logList = new ArrayList<>();

    private static int screenLogScrollPosition = 0;
    private static TextView txtScreenLog = null;
    private static CheckBox chkAutoScrolling = null;

    // -1(none), 0(test, verbose), 1(debug), 2(info, message), 3(warning), 4(error), 5(exception)
    private static int debugScr_logLevel = 5;   // logLevel for debugScreen
    private static int app_logLevel = 5;        // logLevel for Logcat

    public static int getDebugScr_logLevel() { return debugScr_logLevel; }
    public static int getApp_logLevel() { return app_logLevel; }
    public static void setDebugScr_logLevel( int _debugScr_logLevel ) { debugScr_logLevel = _debugScr_logLevel; }
    public static void setApp_logLevel(int _logcat_logLevel ) { app_logLevel = _logcat_logLevel; }

    public static String getHeader(Object tag){
        if(tag != null) return "[" + logHeader + "|" + tag.toString() + "]";
        else return "[" + logHeader + "| UNKNOWN ]";
    }

    public static void printTest(Object tag, String log){
        if(debugScr_logLevel <= LOG_LEVEL_TEST){
            addScreenLog(new GniLog(getHeader(tag), log, LOG_LEVEL_TEST));
        }
        if(app_logLevel <= LOG_LEVEL_TEST){
            Log.v(getHeader(tag), log);
        }
    }

    public static void printDbg(Object tag, String log){
        if(debugScr_logLevel <= LOG_LEVEL_DEBUG){
            addScreenLog(new GniLog(getHeader(tag), log, LOG_LEVEL_DEBUG));
        }
        if(app_logLevel <= LOG_LEVEL_DEBUG){
            Log.d(getHeader(tag), log);
        }
    }

    public static void printInfo(Object tag, String log){
        if(debugScr_logLevel <= LOG_LEVEL_INFO){
            addScreenLog(new GniLog(getHeader(tag), log, LOG_LEVEL_INFO));
        }
        if(app_logLevel <= LOG_LEVEL_INFO){
            Log.i(getHeader(tag), log);
        }
    }

    public static void printWarn(Object tag, String log){
        if(debugScr_logLevel <= LOG_LEVEL_WARN){
            addScreenLog(new GniLog(getHeader(tag), log, LOG_LEVEL_WARN));
        }
        if(app_logLevel <= LOG_LEVEL_WARN){
            Log.w(getHeader(tag), log);
        }
    }

    public static void printError(Object tag, String log){
        if(debugScr_logLevel <= LOG_LEVEL_ERROR){
            addScreenLog(new GniLog(getHeader(tag), log, LOG_LEVEL_ERROR));
        }
        if(app_logLevel <= LOG_LEVEL_ERROR){
            Log.e(getHeader(tag), log);
        }
    }

    public static void printExcept(Object tag, String log, Throwable e){
        if(debugScr_logLevel <= LOG_LEVEL_EXCEPT){
            addScreenLog(new GniExceptLog(getHeader(tag), log, LOG_LEVEL_EXCEPT, e));
        }
        if(app_logLevel <= LOG_LEVEL_EXCEPT){
            Log.e(getHeader(tag), "\n━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━");
            Log.e(getHeader(tag), log + " → "  + e.toString());
            for (StackTraceElement st: e.getStackTrace()) {
                Log.e(getHeader(tag), "     " + st.toString());
            }
            if(e.getCause() != null){
                Throwable cuz = e.getCause();
                Log.e(getHeader(tag),"   cause: " + cuz.toString());
                for (StackTraceElement st: cuz.getStackTrace()) {
                    Log.e(getHeader(tag), "     " + st.toString());
                }
            }
            Log.e(getHeader(tag), "━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━\n");
        }
    }

    public static void printExceptWithSaveToPciDB(Object tag, String log, Throwable e, int errCode, String errCategory){
        int sameerror_count = 0;
        if(e == null) e = new PciRuntimeException("["+tag+"]" + log + " / " + "exception is null" + " / errCode : " + errCode + " / errCategory : " + errCategory);
        printExcept(tag, log, e);
        PciDB pciDB = Global.getInstance().getPciDB();

        if(Global.getInstance().getPciProperty().getApp_debug_usePciDb()){
            if(pciDB == null){
                printError("SaveToPciDB", "can't save ErrorInfo to pciDB, PciDB Instance is null (Error Info : " + e.toString() + ")" + pciDB.toString());
                return;
            }
            sameerror_count = pciDB.getErrorCountOnDB(errCode);
            printInfo("SaveToPciDB", "---- ErrorCode : " + errCode + " | ErrorCodeCount : " + sameerror_count );
            if(sameerror_count != 0){
                sameerror_count++;
                ErrorInfo errorInfo = new ErrorInfo(errCode, sameerror_count, tag + "|" + log + "|" + e.toString(), errCategory, HsUtil_Date.getCurrentTime());
                try {
                    pciDB.updateErrorCount(errorInfo);
                    printInfo("SaveToPciDB", "---- ErrorInfo Save Update to Pci-DB Success----");

                }catch (Exception exception){
                    printInfo("SaveToPciDB", "---- ErrorInfo Save Update to Pci-DB Fail----");
                }
                return;
            }else {
                sameerror_count++;
                ErrorInfo errorInfo = new ErrorInfo(errCode, sameerror_count, tag + "|" + log + "|" + e.toString(), errCategory, HsUtil_Date.getCurrentTime());

                printInfo("SaveToPciDB", "↓↓↓↓ ErrorInfo Save to Pci-DB ↓↓↓↓");
                errorInfo.printErrorInfo("SaveToPciDB");
                pciDB.insertNewErrorInfo(errorInfo);
            }
        }else{
            printError("SaveToPciDB", "can't save ErrorInfo to pciDB, Property.app_debug_usePciDb is false (Error Info : " + e.toString() + ")");
        }


    }

    public static void printWtf(Object tag, String log){
        addScreenLog( new GniLog(getHeader(tag), log, LOG_LEVEL_SYSTEM));
        Log.wtf(getHeader(tag), log);
    }


    public static void refreshScreenLog(){
        if(txtScreenLog == null) return;
        txtScreenLog.setText("Refresh Log at " + HsUtil_Date.getCurrentTimeStr() + "\n");
        if(logList != null && txtScreenLog != null){
            if(app_logLevel > 0) Log.d(getHeader("GLog"), "Refresh Log at " + HsUtil_Date.getCurrentTimeStr() + "\n");
            for (GniLog glog: logList) {
                txtScreenLog.append(glog.getLog() + "\n");
            }
        }
    }


    public static void clearScreenLog(){
        logList.clear();
        if(txtScreenLog == null) return;
        txtScreenLog.setText("Clear Log at " + HsUtil_Date.getCurrentTimeStr() + "\n");
    }

    private static void addScreenLog(GniLog glog){
        if(Global.getInstance() != null &&  Global.getInstance().getPciProperty() != null &&
                Global.getInstance().getPciProperty().getUseDebugMode() && logList != null){

            int debugBuffer = Global.getInstance().getPciProperty().getDebugScreen_Buffer();
            if(debugBuffer > 0 && logList.size() >= debugBuffer){
                logList.remove(0);
            }

            logList.add(glog);
            appendScreenLogToTextView(glog);
        }
    }

    private static void appendScreenLogToTextView(final GniLog glog){
        if(txtScreenLog == null) return;
        if(Global.getInstance().getMainActivity() != null){
            Global.getInstance().getMainActivity().runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    txtScreenLog.append(glog.getLog() + "\n");

                    if(txtScreenLog.getVisibility() == View.VISIBLE && chkAutoScrolling != null && chkAutoScrolling.isChecked()){
                        scrollToEnd();
                    }
                }
            });
        }
    }

    public static void scrollToEnd(){
        if(txtScreenLog == null || txtScreenLog.getLayout() == null) return;
        final int scrollAmount = txtScreenLog.getLayout().getLineTop(txtScreenLog.getLineCount()) - txtScreenLog.getHeight();
        if (scrollAmount > 0){
            screenLogScrollPosition = scrollAmount + 40;
            txtScreenLog.scrollTo(0, scrollAmount);
        }
        else{
            screenLogScrollPosition = 0;
            txtScreenLog.scrollTo(0, 0);
        }
    }

    public static int getLogLineCount(){
        if(logList != null) return logList.size();
        return 0;
    }

    public static void destory(){
        txtScreenLog = null;

        if(logList != null && logList.size() > 0) {
            for (GniLog logs : logList) {
                if (logs != null) logs.destroy();
            }
            logList.clear();
        }
        logList = null;
    }


    public static void setScreenLogComponents(TextView _txtScreenLog, CheckBox _chkAutoScrolling){
        txtScreenLog = _txtScreenLog;
        chkAutoScrolling = _chkAutoScrolling;
    }

    public static void setScreenLogScrollPosition(int scrollPosition){
        screenLogScrollPosition = scrollPosition;
    }
    public static int getScreenLogScrollPosition() { return screenLogScrollPosition; }



    public static String getLogLevelStr(int logLevel){
        switch (logLevel){
            case LOG_LEVEL_NONE : return "N";
            case LOG_LEVEL_TEST : return "V";
            case LOG_LEVEL_DEBUG : return "D";
            case LOG_LEVEL_INFO : return "I";
            case LOG_LEVEL_WARN : return "W";
            case LOG_LEVEL_ERROR : return "E";
            case LOG_LEVEL_EXCEPT : return "A";
            case LOG_LEVEL_SYSTEM : return "S";
            default: return "Unknown";
        }
    }
}

class GniLog{
    String tag;
    String msg;
    String scrLogHead;
    int logLevel;

    public GniLog(String _tag, String _msg, int _level){
        tag = _tag;
        msg = _msg;
        logLevel = _level;
        scrLogHead = HsUtil_Date.getCurrentTimeStr("MM-dd HH:mm:ss.SSS") + " " + GLog.getLogLevelStr(logLevel) + "/" + tag;
    }

    public String getLog() {
        switch (logLevel){
            case GLog.LOG_LEVEL_TEST :
                return "  " + scrLogHead + ":" + msg;

            case GLog.LOG_LEVEL_INFO :
                return scrLogHead + " : # " + msg;

            case GLog.LOG_LEVEL_WARN:
                return scrLogHead + " : =====> " + msg;

            case GLog.LOG_LEVEL_ERROR :
                return scrLogHead + "──────────────────────────────────────\n" + scrLogHead + " : " + msg + "\n" + scrLogHead + "──────────────────────────────────────";

            case GLog.LOG_LEVEL_SYSTEM :
                return scrLogHead + " : > " + msg;

            default :
                return scrLogHead + " : " + msg;
        }
    }
    public String getTag() { return tag; }
    public String getMsg() { return msg; }
    public int getLogLevel() { return logLevel; }

    public void destroy(){
        tag = null;
        msg = null;
    }
}

class GniExceptLog extends GniLog{
    Throwable e;

    public GniExceptLog(String _tag, String _msg, int _level, Throwable _e){
        super(_tag, _msg, _level);
        e = _e;
    }

    public String getLog() {
        StringBuffer stringBuffer = new StringBuffer();
        String line = scrLogHead + "\n" + scrLogHead + "━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━\n";
        stringBuffer.append(line);
        line = scrLogHead + msg  + " → "  + e.toString();
        stringBuffer.append(line);
        for (StackTraceElement st: e.getStackTrace()) {
            line = "\n                     " + tag + " : " + st.toString();
            stringBuffer.append(line);
        }
        if(e.getCause() != null){
            Throwable cuz = e.getCause();
            line = "\n" + scrLogHead + " cause: " + cuz.toString();
            stringBuffer.append(line);
            for (StackTraceElement st: cuz.getStackTrace()) {
                line = "\n                     " + tag + " : " + st.toString();
                stringBuffer.append(line);
            }
        }
        line = "\n" + scrLogHead + "━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━\n" + scrLogHead;
        stringBuffer.append(line);
        return stringBuffer.toString();
    }
    public Throwable getException() { return e; }
    public void destroy(){
        super.destroy();
        e = null;
    }
}

