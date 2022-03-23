package com.ktpci.beacon;

import android.annotation.SuppressLint;
import android.content.Context;

import androidx.annotation.IntDef;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.ktpci.beacon.pcireceiver.RunningReceiver;
import com.ktpci.beacon.pciredux.action.ActionAgreeTerms;
import com.ktpci.beacon.pciredux.action.ActionDowngradeState;
import com.ktpci.beacon.pciredux.action.ActionOptIn;
import com.ktpci.beacon.pciredux.action.ActionOptOut;
import com.ktpci.beacon.pciredux.core.Action;
import com.ktpci.beacon.pciredux.core.PCIStore;
import com.ktpci.beacon.pciredux.state.PCIState;
import com.ktpci.beacon.pciutil.Null;
import com.ktpci.beacon.pciutil.PCIChiper;
import com.ktpci.beacon.pciutil.PCILog;
import com.ktpci.beacon.pciutil.PCIPackageUtil;
import com.ktpci.beacon.pciutil.PCIStorage;
import com.ktpci.beacon.pciutil.PCIStorageKey;

import java.lang.annotation.Retention;
import java.util.Timer;
import java.util.TimerTask;

import static com.ktpci.beacon.pciutil.PCITime.currentDate;
import static com.ktpci.beacon.pciutil.PCITime.currentTime;
import static com.ktpci.beacon.pciutil.PCITime.preDate;
import static java.lang.annotation.RetentionPolicy.SOURCE;

public class PCI {
    @Retention(SOURCE)
    @IntDef({
            SUCCESS,
            INVALID_ARGUMENTS,
            NO_MATCHED_NOTIFIER_NAME,

    })
    private @interface PCICode {}
    public static final int SUCCESS = 0x00000000;
    public static final int INVALID_ARGUMENTS = 0x00002000;
    public static final int NO_MATCHED_NOTIFIER_NAME = 0x00002002;
    public static boolean runable = true;



    @SuppressLint("StaticFieldLeak")
    private static volatile PCI singleton;

    @NonNull
    private Context context;

    private PCI(@NonNull Context context) {
        this.context = context;
        //PCILog.installDatabase(context);

        final String savedVersion = PCIStorage.getString(context, PCIStorageKey.APP_VERSION, "None");
        if (PCIPackageUtil.isPackageNewlyReplaced(context, savedVersion)) {
            final String newVersion = Null.safe(PCIPackageUtil.getVersionName(context), savedVersion);
            PCIStorage.put(context, PCIStorageKey.APP_VERSION, newVersion);

        }
    }
    public static PCI with(@Nullable Context context) {
        if (context == null) throw new NullPointerException("Context is null");
        else if (singleton == null) {
            synchronized (PCI.class) {
                if (singleton == null) {
                    singleton = new PCI(context);
                }
            }
        }
        return singleton;
    }

    @SuppressLint("WrongConstant")
    @PCICode
    public int agreeTerms(@Nullable String adid, @Nullable String partnercode, @NonNull int pcimode) {
        PCILog.d("agreeTerms( %s, %s , %s )", adid, partnercode, pcimode);
        if (adid == null || adid.isEmpty() ||
                partnercode == null || partnercode.isEmpty())
            return INVALID_ARGUMENTS;

        /** Secret ADID 등록 */
        if(currentTime("HHmm") < 400) {
            String advertiseAdid = PCIChiper.Encrypt(adid, preDate("yyyyMMdd"));
            PCIStorage.put(context, PCIStorageKey.SECRETADID, advertiseAdid);
        }else{
            String advertiseAdid = PCIChiper.Encrypt(adid, currentDate("yyyyMMdd"));
            PCIStorage.put(context, PCIStorageKey.SECRETADID, advertiseAdid);
        }

        Action actionAgreeTerms = new ActionAgreeTerms(adid, partnercode, pcimode);
        PCIStore.getInstance(context).dispatch(actionAgreeTerms);


        return PCI.SUCCESS;
    }

    @PCICode
    public int disagreeTerms() {
        PCILog.d("disagreeTerms()");
        Action actiondowngrade = new ActionDowngradeState(PCIState.Type.DEFAULT);
        PCIStore.getInstance(context).dispatch(actiondowngrade);
        try {
            KtAdvertise.getInstance().finish();
        }catch (Exception e){
            PCILog.d("Already Beacon Advertising stop!!");
        }
//        Action actionDisagreeTerms = new ActionDisagreeTerms();
//        PCIStore.getInstance(context).dispatch(actionDisagreeTerms);
        return PCI.SUCCESS;
    }
    @PCICode
    public int optIn() {
        PCILog.d("optIn()");
        Action actionOptIn = new ActionOptIn();
        PCIStore.getInstance(context).dispatch(actionOptIn);
        return PCI.SUCCESS;
    }
    @PCICode
    public int optOut() {
        PCILog.d("optOut()");
        Action actionOptOut = new ActionOptOut();
        PCIStore.getInstance(context).dispatch(actionOptOut);
        return PCI.SUCCESS;
    }


    public void beaconStart(String AdId, String parterCode) {
        PCILog.d("beaconStart()");
        String cdate = currentDate("yyyyMMdd");
        String pdate = preDate("yyyyMMdd");
        int ctime = currentTime("HHmm");
        String tAdid = AdId;
        String pCode = parterCode;
        if(!KtAdvertise.getInstance().isStarted()) {
            try {
                if (ctime < 400) {
                    tAdid = PCIChiper.Encrypt(tAdid, pdate);
                } else {
                    tAdid = PCIChiper.Encrypt(tAdid, cdate);
                }
                KtAdvertise.getInstance().start(context, "start", tAdid, pCode);
            } catch (Exception e) {
                PCILog.d("Beacon Advertising error!!");
            }

            Timer timer = new Timer(true);
            TimerTask timerTask = new TimerTask() {
                @Override
                public void run() {
                    beaconStop();
                }
            };
            timer.schedule(timerTask, 3 * 60 * 1000);
        }else{
            PCILog.d("Already Beacon Advertising ... ");
        }
    }

    public void beaconStop() {
        try { KtAdvertise.getInstance().finish();
        }catch (Exception e){ PCILog.d("Beacon Advertising Stop error!!"); }
    }




}
