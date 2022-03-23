package com.kt.gigagenie.pci;

import android.content.Context;
import android.content.ContextWrapper;

import com.gnifrix.debug.GLog;
import com.gnifrix.util.timechecker.TimeChecker;
import com.kt.gigagenie.pci.data.pci_db.PciDB;
import com.kt.gigagenie.pci.system.PciProperty;

/**
 * Created by LeeBaeng on 2018-09-20.
 */

public class Global {
    private static Global INSTANCE;

    public static final String STATE_ALIVE       = "alive";
    public static final String STATE_RESTART     = "restart";
    public static final String STATE_DEAD        = "dead";

    private PciProperty pciProperty;
    private MainActivity mainActivity;
    private Pci_Service pciService;
    private TimeChecker timeChecker;

    private PciDB pciDB;
    private String state = "alive";

    public Global(){}

    public static Global getInstance(){
        if(INSTANCE == null){
            INSTANCE = new Global();
        }
        return INSTANCE;
    }

    public static boolean isInitialized(){
        return INSTANCE != null;
    }

    public PciDB getPciDB() { return pciDB; }
    public String getState() { return state; }
    public Pci_Service getPciService() { return pciService; }
    public PciProperty getPciProperty() { return pciProperty; }
    public TimeChecker getTimeChecker() { return timeChecker; }
    public MainActivity getMainActivity() { return mainActivity; }

    public void setPciDB( PciDB _pciDB ) { pciDB = _pciDB; }
    public void setState( String _state ) { state = _state; }
    public void setPciService( Pci_Service _pciService ) { pciService = _pciService; }
    public void setPciProperty( PciProperty _pciProperty ) { pciProperty = _pciProperty; }
    public void setTimeChecker( TimeChecker _timeChecker ) { timeChecker = _timeChecker; }
    public void setMainActivity( MainActivity _mainActivity ) { mainActivity = _mainActivity; }


    public void destroy(){
        GLog.printInfo("Global", "          ### Finish PCI ###");
        if(pciProperty != null) pciProperty.destroy();
        if(pciDB != null) pciDB.destory();

        pciProperty = null;
        pciService = null;
        mainActivity = null;
        pciDB = null;

        GLog.destory();

        Global.getInstance().setState(Global.STATE_DEAD);

        INSTANCE = null;
    }
}


