package com.ktpci.beacon.pcireceiver;

import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;

import com.ktpci.beacon.PCI;
import com.ktpci.beacon.pciredux.state.PCIState;
import com.ktpci.beacon.pciutil.PCIStorage;

import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import static com.ktpci.beacon.pciutil.PCIAlarm.RunningTime;
import static com.ktpci.beacon.pciutil.PCIStorageKey.RUNNINGABLE;

public class RunningReceiver extends BroadcastReceiver {

    private final static String RunningReq="com.ktpci.running.req";
    private final static String RunningTrue="com.ktpci.running.true";
    private final static String RunningFalse="com.ktpci.running.false";


    @Override
    public void onReceive(Context context, Intent intent) {
        RunningTime(context);  //alarm re-regi

        String pCd = PCIState.from(context).getPartnerCode();
        String rTm = PCIState.from(context).getRuntime();
        String cType = String.valueOf(PCIState.from(context).getType());
        int running = 0;
        if(RunningReq.equals(intent.getAction())){
            running = 1;
        }else if(RunningTrue.equals(intent.getAction())){
            running = 2;
        }else if(RunningFalse.equals(intent.getAction())){
            running = 3;
        }

/////////////////////////
        switch (running) {
            case 0:
                if (cType.equals("INACTIVE")) {
                    RunReq(context, pCd, rTm);
                    PCIStorage.put(context, RUNNINGABLE, true);
                    Timer timer = new Timer(true);
                    TimerTask timerTask = new TimerTask() {
                        @Override
                        public void run() {
                            if (PCIStorage.getBoolean(context, RUNNINGABLE, true)) {
                                PCI.with(context).optIn();
//                                save(context, "OptIn");
                            }
                        }
                    };
                    timer.schedule(timerTask,3*1000);
                }
                break;

            case 1:
                if(!rTm.equals(intent.getStringExtra("runtime"))){ //this app -> exception
                    if(cType.equals("ACTIVE") && pCd.equals(intent.getStringExtra("partnercode"))){
                        RunFalse(context, pCd, rTm); // other app is running ...
                    }
                }
                break;
            case 2:  //runinng true
                if(!rTm.equals(intent.getStringExtra("runtime"))){
                    //do not working
                }
                break;
            case 3: //runinng false
                if(!rTm.equals(intent.getStringExtra("runtime"))){
                    if(cType.equals("INACTIVE") && pCd.equals(intent.getStringExtra("partnercode"))){
                        PCIStorage.put(context, RUNNINGABLE, false);
                    }else if(cType.equals("ACTIVE") && pCd.equals(intent.getStringExtra("partnercode"))){
                        PCI.with(context).optOut();
                    }
                }
                break;
            default:
                break;
        }
    }

    public static void RunReq(Context BCcontext, String code, String runtime){
        Intent runningsend = new Intent();
        runningsend.setAction(RunningReq);
        runningsend.putExtra("partnercode",code);
        runningsend.putExtra("runtime",runtime);
        //sendImplicitBroadcast(BCcontext, runningsend);
        BCcontext.sendBroadcast(runningsend);

    }
    private void RunTrue(Context BCcontext, String code, String runtime){
        Intent runningsend = new Intent();
        runningsend.setAction(RunningTrue);
        runningsend.putExtra("partnercode",code);
        runningsend.putExtra("runtime",runtime);
        BCcontext.sendBroadcast(runningsend);
    }
    private void RunFalse(Context BCcontext, String code, String runtime){
        Intent runningsend = new Intent();
        runningsend.setAction(RunningFalse);
        runningsend.putExtra("partnercode",code);
        runningsend.putExtra("runtime",runtime);
        BCcontext.sendBroadcast(runningsend);
    }

    private void sendImplicitBroadcast(Context ctxt, Intent i)
    {
        PackageManager pm=ctxt.getPackageManager();
        List<ResolveInfo> matches=pm.queryBroadcastReceivers(i, 0);
        for (ResolveInfo resolveInfo : matches) {
            Intent explicit=new Intent(i);
            ComponentName cn= new ComponentName(resolveInfo.activityInfo.applicationInfo.packageName, resolveInfo.activityInfo.name);
            explicit.setComponent(cn);
            ctxt.sendBroadcast(explicit);
        }
    }
}

