package com.ktpci.beacon.pciutil;

import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.PowerManager;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.work.WorkInfo;
import androidx.work.WorkManager;

import com.google.common.util.concurrent.ListenableFuture;

import java.util.List;
import java.util.concurrent.ExecutionException;

public class PCIPackageUtil {

    @Nullable
    public static boolean isPackageNewlyReplaced(@NonNull Context context, @NonNull String savedVersion) {
        String currVersion = getVersionName(context);
        return (currVersion == null) || !currVersion.equals(savedVersion);
    }

    @Nullable
    public static String getVersionName(@NonNull Context var0) {
        try {
            PackageInfo var1 = var0.getApplicationContext().getPackageManager().getPackageInfo(var0.getPackageName(), 0);
            return var1.versionName;
        } catch (PackageManager.NameNotFoundException var2) {
            return null;
        } catch (RuntimeException var3) {
            return null;
        }
    }

    public static boolean isWorkScheduled(Context sContext,String tag) {
        WorkManager instance = WorkManager.getInstance(sContext);
        ListenableFuture<List<WorkInfo>> statuses = instance.getWorkInfosByTag(tag);
        try {
            boolean running = false;
            List<WorkInfo> workInfoList = statuses.get();
            for (WorkInfo workInfo : workInfoList) {
                WorkInfo.State state = workInfo.getState();
                running = state == WorkInfo.State.RUNNING | state == WorkInfo.State.ENQUEUED;
            }
            return running;
        } catch (ExecutionException e) {
            e.printStackTrace();
            return false;
        } catch (InterruptedException e) {
            e.printStackTrace();
            return false;
        }
    }


    public static boolean isScreenState(Context pContext){
        PowerManager pm = (PowerManager) pContext.getSystemService(Context.POWER_SERVICE);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT_WATCH) {
            return pm.isInteractive();
        }else {
            return false;
        }
    }

    public static boolean isIdleState(Context pContext){
        PowerManager pm = (PowerManager) pContext.getSystemService(Context.POWER_SERVICE);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            return pm.isDeviceIdleMode();
        }else {
            return false;
        }
    }
    public static boolean isSaveState(Context pContext){
        PowerManager pm = (PowerManager) pContext.getSystemService(Context.POWER_SERVICE);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            return pm.isPowerSaveMode();
        }else {
            return false;
        }
    }


}