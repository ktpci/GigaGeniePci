package com.ktpci.beacon;

import android.bluetooth.BluetoothAdapter;
import android.content.Context;

import com.ktpci.beacon.pciutil.PCILog;

import java.util.Arrays;

public class KtAdvertise {
    private final String KTCode = "65535";
    private static KtAdvertise ktInstance = new KtAdvertise();
    BeaconParser beaconParser;
    BeaconTransmitter beaconTransmitter;
    BluetoothAdapter mBlutoothAdapter;

    private KtAdvertise() { }
    public static synchronized KtAdvertise getInstance(){
        if(ktInstance == null){
            ktInstance = new KtAdvertise(); }
        return ktInstance; }


    public void start(Context context, String YN, String adid, String partercode) {
        beaconParser = new BeaconParser().setBeaconLayout("m:2-3=beac,i:4-19,i:20-21,i:22-23,p:24-24,d:25-25");
        beaconTransmitter = new BeaconTransmitter(context, beaconParser);
        mBlutoothAdapter = BluetoothAdapter.getDefaultAdapter();
        String minorcode = partercode;
        if (mBlutoothAdapter.isEnabled()) {

            Beacon beacon = new Beacon.Builder()
                    .setId1(adid)
                    .setId2(KTCode)
                    .setId3(minorcode)
                    .setManufacturer(0x0118)
                    .setTxPower(-59)
                    .setDataFields(Arrays.asList(new Long[]{0l}))
                    .setBluetoothName("dalkomm")
                    .build();

            if (YN == "start") {
                beaconTransmitter.startAdvertising(beacon);
            } else {
                beaconTransmitter.stopAdvertising();
            }
        } else {
            PCILog.d("BLE State is disable !!!");
        }
    }
    public boolean isStarted(){

        try {
            boolean startResult = beaconTransmitter.isStarted();
            if (startResult == true) return true;
            else if (startResult == false) return false;
        }catch (Exception e) {
            PCILog.d(" Not yet Beacon Advertising ready!!");
        }
        return false;
    }

    public void finish(){
        beaconTransmitter.stopAdvertising();
    }


}
