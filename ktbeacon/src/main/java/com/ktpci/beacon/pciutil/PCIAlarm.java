package com.ktpci.beacon.pciutil;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Build;

import com.ktpci.beacon.pcireceiver.RunningReceiver;
import com.ktpci.beacon.pcireceiver.UpdateReceiver;
import com.ktpci.beacon.pcireceiver.WakeReceiver;

import java.util.Calendar;

import static android.os.SystemClock.sleep;
import static com.ktpci.beacon.pciutil.PCITime.currentTime;
import static java.lang.System.currentTimeMillis;

public class PCIAlarm {
    public static void WakeTime(Context context) {
        AlarmManager mAlarmMgr = null;
        PendingIntent mAlarmIntent = null;

        int delay = 5 * 60 * 1000; // 1시간 40분 6000 * 1000 ms

        Intent alarmIntent = new Intent(context, WakeReceiver.class);
        mAlarmIntent = PendingIntent.getBroadcast(context, 1004, alarmIntent, 0);

        mAlarmMgr = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        //mAlarmMgr.setRepeating(AlarmManager.RTC_WAKEUP, currentTimeMillis() + delay, delay, mAlarmIntent);

        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
                mAlarmMgr.setExact(AlarmManager.RTC_WAKEUP, currentTimeMillis() + delay, mAlarmIntent);
            } else {
                mAlarmMgr.set(AlarmManager.RTC_WAKEUP, currentTimeMillis() + delay, mAlarmIntent);
            }
        } else {
            //mAlarmMgr.setAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, currentTimeMillis() + delay, mAlarmIntent);
            //API 23 up
            Calendar restart = Calendar.getInstance();
            restart.setTimeInMillis(currentTimeMillis());
            restart.add(Calendar.MINUTE, 5); // 100 min [ 1h 40m ]
            AlarmManager.AlarmClockInfo ac = new AlarmManager.AlarmClockInfo(restart.getTimeInMillis(), mAlarmIntent);
            mAlarmMgr.setAlarmClock(ac, mAlarmIntent);
        }
    }
    public static void NightTime(Context context) {
        int delay;
        AlarmManager mAlarmMgr = null;
        PendingIntent mAlarmIntent = null;
        Intent alarmIntent = new Intent(context, WakeReceiver.class);
        mAlarmIntent = PendingIntent.getBroadcast(context, 1004, alarmIntent, 0);
        mAlarmMgr = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);

        if(0< currentTime("HHmm") && currentTime("HHmm") <= 0700){
            delay = (7 * 60 * 60 * 1000) - (currentTime("HH") * 60 * 60 * 1000) - (currentTime("mm") * 60 * 1000);
            PCILog.d("NightMode -> Alarm after " + delay + "ms  (AM 7:00)");
            if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
                    mAlarmMgr.setExact(AlarmManager.RTC_WAKEUP, currentTimeMillis() + delay, mAlarmIntent);
                } else {
                    mAlarmMgr.set(AlarmManager.RTC_WAKEUP, currentTimeMillis() + delay, mAlarmIntent);
                }
            } else {
                Calendar restart = Calendar.getInstance();
                //restart.setTimeInMillis(currentTimeMillis());
                restart.set(Calendar.HOUR_OF_DAY, 7);
                restart.set(Calendar.MINUTE, 0);
                restart.set(Calendar.SECOND, 0);
                long intervalTime = 24 * 60 * 60 * 1000;
                long selectTime = restart.getTimeInMillis();
                sleep(1 * 1000);
                long currentTime = currentTimeMillis();
                if(currentTime > selectTime){ selectTime += intervalTime; }
                AlarmManager.AlarmClockInfo ac = new AlarmManager.AlarmClockInfo(selectTime, mAlarmIntent);
                mAlarmMgr.setAlarmClock(ac, mAlarmIntent);
            }
        }else {
            delay = 5 * 60 * 1000;
            if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
                    mAlarmMgr.setExact(AlarmManager.RTC_WAKEUP, currentTimeMillis() + delay, mAlarmIntent);
                } else {
                    mAlarmMgr.set(AlarmManager.RTC_WAKEUP, currentTimeMillis() + delay, mAlarmIntent);
                }
            } else {
                Calendar restart = Calendar.getInstance();
                restart.setTimeInMillis(currentTimeMillis());
                restart.add(Calendar.MINUTE, 5); // 100 min [ 1h 40m ]
                AlarmManager.AlarmClockInfo ac = new AlarmManager.AlarmClockInfo(restart.getTimeInMillis(), mAlarmIntent);
                mAlarmMgr.setAlarmClock(ac, mAlarmIntent);
            }
        }

    }
    public static void UpdateTime(Context context) {
        int delay = 0;
        AlarmManager mAlarmMgr = null;
        PendingIntent mAlarmIntent = null;
        Intent alarmIntent = new Intent(context, UpdateReceiver.class);
        mAlarmIntent = PendingIntent.getBroadcast(context, 1005, alarmIntent, 0);
        mAlarmMgr = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);

        if(0< currentTime("HHmm") && currentTime("HHmm") <= 400) {
            delay = (4 * 60 * 60 * 1000) - (currentTime("HH") * 60 * 60 * 1000) - (currentTime("mm") * 60 * 1000);
        }else{
            delay = (24 * 60 * 60 * 1000)-((currentTime("HH") * 60 * 60 * 1000) + (currentTime("mm") * 60 * 1000) - (4 * 60 * 60 * 1000)) ;
        }
        PCILog.d("UpdatTime -> Alarm after " + delay + "ms  (AM 4:00)");
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
                mAlarmMgr.setExact(AlarmManager.RTC_WAKEUP, currentTimeMillis() + delay, mAlarmIntent);
            } else {
                    mAlarmMgr.set(AlarmManager.RTC_WAKEUP, currentTimeMillis() + delay, mAlarmIntent);
            }
        } else {
            Calendar restart = Calendar.getInstance();
            //restart.setTimeInMillis(currentTimeMillis());
            restart.set(Calendar.HOUR_OF_DAY, 4);
            restart.set(Calendar.MINUTE, 0);
            restart.set(Calendar.SECOND, 0);
            long intervalTime = 24 * 60 * 60 * 1000;
            long selectTime = restart.getTimeInMillis();
            sleep(1 * 1000);
            long currentTime = currentTimeMillis();
            if(currentTime > selectTime){
                selectTime += intervalTime;
            }

//            restart.add(Calendar.HOUR_OF_DAY, 4);
//            restart.add(Calendar.MINUTE, 0);
//            restart.add(Calendar.SECOND, 0);
//            AlarmManager.AlarmClockInfo ac = new AlarmManager.AlarmClockInfo(restart.getTimeInMillis(), mAlarmIntent);
            AlarmManager.AlarmClockInfo ac = new AlarmManager.AlarmClockInfo(selectTime, mAlarmIntent);
            mAlarmMgr.setAlarmClock(ac, mAlarmIntent);
        }
    }
    public static void RunningTime(Context context) {
        AlarmManager mAlarmMgr = null;
        PendingIntent mAlarmIntent = null;
        int delay = 24 * 60 * 60 * 1000;

        Intent alarmIntent = new Intent(context, RunningReceiver.class);
        mAlarmIntent = PendingIntent.getBroadcast(context, 1006, alarmIntent, 0);

        mAlarmMgr = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        //mAlarmMgr.setRepeating(AlarmManager.RTC_WAKEUP, currentTimeMillis() + delay, delay, mAlarmIntent);

        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
                mAlarmMgr.setExact(AlarmManager.RTC_WAKEUP, currentTimeMillis() + delay, mAlarmIntent);
            } else {
                mAlarmMgr.set(AlarmManager.RTC_WAKEUP, currentTimeMillis() + delay, mAlarmIntent);
            }
        } else {
            //mAlarmMgr.setAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, currentTimeMillis() + delay, mAlarmIntent);
            //API 23 up
            Calendar restart = Calendar.getInstance();
            restart.setTimeInMillis(currentTimeMillis());
            restart.add(Calendar.HOUR, 24);
//            restart.add(Calendar.MINUTE, 3);
            AlarmManager.AlarmClockInfo ac = new AlarmManager.AlarmClockInfo(restart.getTimeInMillis(), mAlarmIntent);
            mAlarmMgr.setAlarmClock(ac, mAlarmIntent);
        }
    }

    public static void RunningOneTime(Context context) {
        AlarmManager mAlarmMgr = null;
        PendingIntent mAlarmIntent = null;
        int delay = 1 * 1000; // 1시간 40분 6000 * 1000 ms

        Intent alarmIntent = new Intent(context, RunningReceiver.class);
        mAlarmIntent = PendingIntent.getBroadcast(context, 1007, alarmIntent, 0);

        mAlarmMgr = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        //mAlarmMgr.setRepeating(AlarmManager.RTC_WAKEUP, currentTimeMillis() + delay, delay, mAlarmIntent);

        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
                mAlarmMgr.setExact(AlarmManager.RTC_WAKEUP, currentTimeMillis() + delay, mAlarmIntent);
            } else {
                mAlarmMgr.set(AlarmManager.RTC_WAKEUP, currentTimeMillis() + delay, mAlarmIntent);
            }
        } else {
            Calendar restart = Calendar.getInstance();
            restart.setTimeInMillis(currentTimeMillis());
            restart.add(Calendar.SECOND, 1); // 100 min [ 1h 40m ]
            AlarmManager.AlarmClockInfo ac = new AlarmManager.AlarmClockInfo(restart.getTimeInMillis(), mAlarmIntent);
            mAlarmMgr.setAlarmClock(ac, mAlarmIntent);
        }
    }

    public static void RunningCancel(Context context) {
        AlarmManager mAlarmMgr = null;
        PendingIntent mAlarmIntent = null;
        mAlarmMgr = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        Intent alarmIntent = new Intent(context, RunningReceiver.class);
        mAlarmIntent = PendingIntent.getBroadcast(context, 1006, alarmIntent, 0);
        if (mAlarmIntent != null) {
            mAlarmMgr.cancel(mAlarmIntent);
            mAlarmIntent.cancel();
        }else{
            PCILog.d("Fail -> Running Alarm Cancel ");
        }

    }

    public static void WakeCancel(Context context) {
        AlarmManager mAlarmMgr = null;
        PendingIntent mAlarmIntent = null;
        mAlarmMgr = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        Intent alarmIntent = new Intent(context, WakeReceiver.class);
        mAlarmIntent = PendingIntent.getBroadcast(context, 1004, alarmIntent, 0);
        if (mAlarmIntent != null) {
            mAlarmMgr.cancel(mAlarmIntent);
            mAlarmIntent.cancel();
        }else{
            PCILog.d("Fail -> Wake Alarm Cancel ");
        }
    }
    public static void UpdateCancel(Context context) {
        AlarmManager mAlarmMgr = null;
        PendingIntent mAlarmIntent = null;
        mAlarmMgr = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        Intent alarmIntent = new Intent(context, UpdateReceiver.class);
        mAlarmIntent = PendingIntent.getBroadcast(context, 1005, alarmIntent, 0);
        if (mAlarmIntent != null) {
            mAlarmMgr.cancel(mAlarmIntent);
            mAlarmIntent.cancel();
        }else{
            PCILog.d("Fail -> Update Alarm Cancel ");
        }
    }

}
