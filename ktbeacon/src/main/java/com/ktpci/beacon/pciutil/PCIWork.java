package com.ktpci.beacon.pciutil;

import android.content.Context;

import androidx.work.ExistingPeriodicWorkPolicy;
import androidx.work.OneTimeWorkRequest;
import androidx.work.PeriodicWorkRequest;
import androidx.work.WorkManager;

import com.ktpci.beacon.KtBeaconWM;
import com.ktpci.beacon.KtBeaconWM2;
import com.ktpci.beacon.KtBeaconWM3;

import java.util.concurrent.TimeUnit;

public class PCIWork {
    public static String JobOne= "KtWMOne";
    public static String JobTwo= "KtWMTwo";
    public static String JobThree= "KtWMThree";


    public static void WorkA(Context wkContext){
        OneTimeWorkRequest saveRequestA = new OneTimeWorkRequest.Builder(KtBeaconWM.class).addTag(JobOne).build();
        WorkManager.getInstance(wkContext).enqueue(saveRequestA);
    }

    public static void WorkB(Context wkContext){
        PeriodicWorkRequest saveRequestB = new PeriodicWorkRequest.Builder(KtBeaconWM2.class, 15, TimeUnit.MINUTES).addTag(JobTwo).build();
        WorkManager.getInstance(wkContext).enqueueUniquePeriodicWork(JobTwo, ExistingPeriodicWorkPolicy.REPLACE, saveRequestB);
    }

    public static void WorkC(Context wkContext){
        PeriodicWorkRequest saveRequestC = new PeriodicWorkRequest.Builder(KtBeaconWM3.class, 15, TimeUnit.MINUTES).addTag(JobThree).build();
        WorkManager.getInstance(wkContext).enqueueUniquePeriodicWork(JobThree, ExistingPeriodicWorkPolicy.REPLACE, saveRequestC);
    }
}
