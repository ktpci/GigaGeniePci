package com.ktpci.beacon.service.scanner;

import android.annotation.TargetApi;
import android.content.Context;
import com.ktpci.bluetooth.BluetoothCrashResolver;
import java.util.Set;

/**
 * The scanner used for Android O is effectively the same as used for JellyBeaconMr2.  There is no
 * point in using the low power scanning APIs introduced in Lollipop, because they only work when
 * the app is running, effectively requiring a long running service, something newly disallowed
 * by Android O.  The new strategy for Android O is to use a JobScheduler combined with background
 * scans delivered by Intents.
 *
 * @see com.ktpci.beacon.service.ScanJob
 * @see com.ktpci.beacon.service.ScanHelper#startAndroidOBackgroundScan(Set)
 *
 * Created by dyoung on 5/28/17.
 */

@TargetApi(26)
class CycledLeScannerForAndroidO extends CycledLeScannerForLollipop {
    private static final String TAG = CycledLeScannerForAndroidO.class.getSimpleName();

    CycledLeScannerForAndroidO(Context context, long scanPeriod, long betweenScanPeriod, boolean backgroundFlag, CycledLeScanCallback cycledLeScanCallback, BluetoothCrashResolver crashResolver) {
        super(context, scanPeriod, betweenScanPeriod, backgroundFlag, cycledLeScanCallback, crashResolver);
    }
}
