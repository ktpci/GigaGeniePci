package com.kt.gigagenie.pci;

import android.app.Activity;
import android.content.Intent;

import android.os.Build;
import android.os.Bundle;

import android.view.KeyEvent;
import android.view.View;

import com.gnifrix.debug.GLog;
import com.gnifrix.ui.key.KeyCode_KT_GIGAGENIE;
import com.gnifrix.system.AppContext;

import com.kt.gigagenie.pci.data.pci_db.PciDB;
import com.kt.gigagenie.pci.system.PciProperty;
import com.kt.gigagenie.pci.ui.Layout_DebugMain;


public class MainActivity extends Activity implements AppContext, KeyCode_KT_GIGAGENIE {
    private static final String LogHeader = "PciActivity";

    private Layout_DebugMain layout_debugMain = null;
    private PciProperty pciProperty;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


        Global global = null;
        if(!Global.isInitialized()){
            global = Global.getInstance();
            global.setPciDB(new PciDB(this));
            pciProperty = new PciProperty(this);
            global.setPciProperty(pciProperty);
        }else{
            global = Global.getInstance();
            if((pciProperty = global.getPciProperty()) == null) {
                pciProperty = new PciProperty(this);
                global.setPciProperty(pciProperty);
            }
        }

        Global.getInstance().setMainActivity(this);
        GLog.printDbg(this, "PCI-Debug Mode : " + pciProperty.getUseDebugMode());
        if(pciProperty.getUseDebugMode()){
            GLog.printDbg(this, "Start GigaGenie-PCI Debug Mode.....");
            GLog.printDbg(this, "Ready");
        }

        if(pciProperty.getUseDebugMode()){
            initDebugUI();
            if(pciProperty.getUseAutostartSvcOnDebugMode()){
                startService();
            }
        }else{
            startService();
            finish();
        }
    }

    private void initDebugUI(){
        pciProperty.printInitString();

        setContentView(R.layout.activity_main);
        layout_debugMain = new Layout_DebugMain(this);
        layout_debugMain.getBtnStartService().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {startService();}
        });
        layout_debugMain.getBtnStopService().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {stopService();}
        });
        layout_debugMain.getTxtView_VersionValue().setText("v." + pciProperty.getVersionName() + " | " + pciProperty.getReleaseDate().toString() + " | local v." + pciProperty.getLocalVersion());
    }


    @Override
    protected void onStart() {
        super.onStart();
        if(pciProperty.getUseDebugMode())
            layout_debugMain.start();
    }

    @Override
    public boolean onKeyUp(int keyCode, KeyEvent event) {
        if(pciProperty.getUseDebugMode()) layout_debugMain.onKey(keyCode, event);
        return super.onKeyUp(keyCode, event);
    }

    private void startService(){
        GLog.printDbg(this, "Request Background Service Start !");
        Intent intent = new Intent(getApplicationContext(), Pci_Service.class);
        if( Build.VERSION.SDK_INT >= 26 ) {

            startForegroundService(intent);
        }else {
            startService(intent);
        }
    }

    private void stopService(){
        GLog.printDbg(this, "Request Background Service Stop !");
        Intent intent = new Intent(getApplicationContext(), Pci_Service.class);
        stopService(intent);
    }




    @Override
    protected void onDestroy() {
        super.onDestroy();
        if(pciProperty.getUseDebugMode() && Global.isInitialized() && !Pci_Service.pciReady){
            Global.getInstance().destroy();
        }
    }

    public String toString(){
        return LogHeader;
    }
    public Layout_DebugMain getLayout_debugMain() { return layout_debugMain; }

}
