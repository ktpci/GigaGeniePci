package com.ktpci.beacon.startup;

import android.content.Context;

import com.ktpci.beacon.MonitorNotifier;

public interface BootstrapNotifier extends MonitorNotifier {
    public Context getApplicationContext();
}
