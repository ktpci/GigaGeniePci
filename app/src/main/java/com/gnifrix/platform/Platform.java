package com.gnifrix.platform;

import com.gnifrix.debug.GLog;
import com.kt.gigagenie.pci.Global;
import com.kt.gigagenie.pci.PciManager;
import com.kt.gigagenie.pci.data.pci_db.ErrorInfo;
import com.kt.gigagenie.pci.system.PciProperty;
import com.kt.gigagenie.pci.system.PciRuntimeException;

/**
 * Created by LeeBaeng on 2018-10-04.
 */
public abstract class Platform {
    private static Platform	INSTANCE = null;

    private static final String logHeader = "Platform";
    public static final int PLATFORM_TYPE_STB              = 0;
    public static final int PLATFORM_TYPE_PC               = 1;

    protected int platformType = -1;

    protected Platform(){
        init();
    }

    public static Platform getInstance(){
        if(INSTANCE == null){
            PciProperty property = Global.getInstance().getPciProperty();
            if(property == null) throw new PciRuntimeException("PciProperty is not initialized...");
            if(!PciManager.isInitialized()) throw new PciRuntimeException("PciManager is not initialized...");

            String className;
            if(property.getAppPlatform().equalsIgnoreCase("emul")) className = "com.gnifrix.platform.Pc";
            else className = "com.gnifrix.platform.Kt";

            try{
                INSTANCE = (Platform) Class.forName(className).newInstance();
                GLog.printInfo(logHeader, "load Platform > " + className);
            }catch (Exception e){
                GLog.printExceptWithSaveToPciDB(logHeader, "Create Platform Instance failed...", e, ErrorInfo.CODE_ETC_PLATFORM_INITIALIZE_FAIL, ErrorInfo.CATEGORY_ETC);
            }
        }
        return INSTANCE;
    }

    public void destory(){
        dispose();
        INSTANCE = null;
    }


    protected abstract void init();
    protected abstract void dispose();

    public int getPlatformType() { return platformType; }
    public void setPlatformType( int _platformType ) { platformType = _platformType; }

    public String toString(){
        return logHeader;
    }

}
