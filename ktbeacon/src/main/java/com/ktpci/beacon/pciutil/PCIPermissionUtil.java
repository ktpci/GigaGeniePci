package com.ktpci.beacon.pciutil;

import android.bluetooth.BluetoothAdapter;
import android.content.Context;

import androidx.annotation.NonNull;


public class PCIPermissionUtil {

    /** 위치 권한 획득 by dalkommjk | 2020-03-23 */
    public static boolean bluetoothenable(@NonNull Context context) {
        BluetoothAdapter mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        if (mBluetoothAdapter.enable()) {
            //mBluetoothAdapter.enable();
            return true;
        }
        else {
            //mBluetoothAdapter.disable();
            return false;
        }
    }

}

