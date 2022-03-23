package com.kt.gigagenie.pci.broadcast;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import com.gnifrix.debug.GLog;
import com.kt.gigagenie.pci.Global;
import com.kt.gigagenie.pci.Pci_Service;

/**
 * Pci 서비스 실행 여부와 관계없이 Alarm Manager 를 통해 Service Restart 를 위한 정적 receiver
 * Created by LeeBaeng on 2018-10-02.
 */
public class SystemBroadcastReceiver extends BroadcastReceiver implements BroadcastContext {
    private static final String logheader = "[PCI|SystemBrReceiver]";

    @Override
    public void onReceive(Context context, Intent intent) {
        try{
            Intent i = null;
            if(intent != null){
                GLog.printInfo(this, "Receive System Broadcast : " + intent.toString() + "( action Name : " + intent.getAction() + " / pkg : " + intent.getPackage() + " )");

                switch (intent.getAction()){
                    case RECV_SYS_ACTION_RESTART_SERVICE:
                        Global.getInstance().setState(Global.STATE_RESTART);
                        if(!Pci_Service.pciReady){
                            Log.wtf(logheader, "<<<<<<<<< ACTION_RESTART_SERVICE Received. Try to Restart Pci Service >>>>>>>>>");
                            i = new Intent(context, Pci_Service.class);
                            context.startService(i);
                        }else{
                            Log.wtf(logheader, "Restart Pci Service Failed !! PCI Service is already running(RECV_SYS_ACTION_RESTART_SERVICE)");
                        }
                        break;

                    case RECV_SYS_ACTION_BOOT_COMPLETE:
                        if(!Pci_Service.pciReady){
                            Log.wtf(logheader, "<<<<<<<<< ACTION_BOOT COMPLETE Received. Try to Start Pci Service >>>>>>>>>");
                            Log.wtf(logheader, "Request Background Service Start !");
                            i = new Intent(context, Pci_Service.class);
                            context.startService(i);
                        }else{
                            Log.wtf(logheader, "Start Pci Service Failed !! PCI Service is already running(RECV_SYS_ACTION_BOOT_COMPLETE)");
                        }
                        break;

                    case RECV_SYS_ACTION_RUN_PCI_SERVICE :
                        if(!Pci_Service.pciReady){
                            Log.wtf(logheader, "<<<<<<<<< ACTION_RUN_PCI_SERVICE Received. Try to Start Pci Service >>>>>>>>>");
                            Log.wtf(logheader, "Request Background Service Start !");
                            i = new Intent(context, Pci_Service.class);
                            context.startService(i);
                        }else{
                            Log.wtf(logheader, "Start Pci Service Failed !! PCI Service is already running(RECV_SYS_ACTION_RUN_PCI_SERVICE)");
                        }
                        break;

                    case RECV_SYS_ACTION_NET_STATE_CHANGE :
//                    Pci_Service service = Global.getInstance().getPciService();
//                    if(Global.isInitialized() && PciManager.isInitialized() && service != null && Pci_Service.pciReady){
//                        ConnectivityManager connectivityManager = (ConnectivityManager) service.getSystemService(service.CONNECTIVITY_SERVICE);
//                        NetworkInfo netInfo = connectivityManager.getActiveNetworkInfo();
//                        if(netInfo != null && netInfo.isConnected()){
//                            PciManager.getInstance().getNetManager().runRetryAPI();
//                        }
//                    }
                        break;

                    case RECV_ACTION_POWER_PASSIVE :
                        Pci_Service.pciReady = false;
                        Log.wtf(logheader, "<<<<<<<<< ACTION_RECV_ACTION_POWER_PASSIVE Received. Try to Stop Pci Service >>>>>>>>>");
                        Log.wtf(logheader, "STOP PCI SERVICE");
                        i = new Intent(context, Pci_Service.class);
                        context.stopService(i);

                        if(Global.isInitialized()) Global.getInstance().destroy();

                        i = new Intent(SEND_ACTION_PASSIVE_ACK);
                        i.addFlags(Intent.FLAG_INCLUDE_STOPPED_PACKAGES);
                        i.putExtra("packageName", PKG_PCI);
                        i.setPackage(PKG_MC);
                        context.sendBroadcast(i);

                        Bundle bundle = i.getExtras();
                        if (bundle != null) {
                            for (String key : bundle.keySet()) {
                                Object value = bundle.get(key);
                                Log.wtf(logheader, " └[" + key + "]=" + value.toString()+" (" + value.getClass().getName() + ")");
                            }
                        }
                        break;

                    case RECV_ACTION_POWER_NORMAL :
                        Log.wtf(logheader, "<<<<<<<<< ACTION_RECV_ACTION_POWER_NORMAL Received. Try to Start Pci Service >>>>>>>>>");
                        if(!Pci_Service.pciReady){
                            Log.wtf(logheader, "START PCI SERVICE");
                            i = new Intent(context, Pci_Service.class);
                            context.startService(i);
                        }
                        break;

                    default:
                        break;
                }
            }
            else GLog.printInfo(this, "Receive System Broadcast : intent is null");
        }catch (Exception e){
            if(intent != null)
                Log.e(logheader, "Occur Error in SystemBroadcastReceiver > " + e.toString() + " / intent info > " + intent.toString(), e);
            else Log.e(logheader, "Occur Error in SystemBroadcastReceiver > " + e.toString() + " / intent is null", e);
        }
    }
}
