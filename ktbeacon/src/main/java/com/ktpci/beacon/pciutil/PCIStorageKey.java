package com.ktpci.beacon.pciutil;

import androidx.annotation.NonNull;
import androidx.annotation.StringDef;

import java.lang.annotation.Retention;

import static java.lang.annotation.RetentionPolicy.SOURCE;

public class PCIStorageKey {

    @Retention(SOURCE)
    @StringDef({
            UUID,
            STATE,
//            AVOID_APP_PERMISSION_MONITOR,
//            BITSOUND_JOB_TRIGGER_TIME,
//            UPLOAD_APP_LIST_TRIGGER_TIME,
            REGIDATE,
            SECRETADID,
            RUNNINGABLE,
            APP_VERSION,
            WMSTATE
    })
    @interface StorageKey {}

    @NonNull public static final String UUID = "uuid";
    @NonNull public static final String STATE = "state";
    @NonNull public static final String REGIDATE = "regidate";
    @NonNull public static final String SECRETADID = "secretadid";
    @NonNull public static final String RUNNINGABLE = "runningable";
    @NonNull public static final String WMSTATE = "wmstate";
//    @NonNull public static final String AVOID_APP_PERMISSION_MONITOR = "avoid_app_permission_monitor";
//    @NonNull public static final String BITSOUND_JOB_TRIGGER_TIME = "bitsound_job_trigger_time";
//    @NonNull public static final String UPLOAD_APP_LIST_TRIGGER_TIME = "upload_app_list_trigger_time";
    @NonNull public static final String APP_VERSION = "app_version";
}

