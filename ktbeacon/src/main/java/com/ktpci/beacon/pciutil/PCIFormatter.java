package com.ktpci.beacon.pciutil;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class PCIFormatter {
    private static final int DAY_IN_MILLISECONDS = 1000 * 60 * 60 * 24;

    public static String joinYyyymmdd(int yyyy, int mm, int dd) {
        return yyyy + (mm < 10 ? "0" : "") + mm + (dd < 10 ? "0" : "") + dd;
    }

    public static String joinHhmiss(int hh, int mi, int ss) {
        return (hh < 10 ? "0" : "") + hh + (mi < 10 ? "0" : "") + mi
                + (ss < 10 ? "0" : "") + ss;
    }

    public static Date dateFromYyyymmdd(String yyyymmdd) {
        if (yyyymmdd == null || yyyymmdd.length() != 8) {
            return null;
        }

        int yyyy = Integer.parseInt(yyyymmdd.substring(0, 4));
        int mm = Integer.parseInt(yyyymmdd.substring(4, 6));
        int dd = Integer.parseInt(yyyymmdd.substring(6, 8));

        Calendar cal = Calendar.getInstance();
        cal.set(Calendar.YEAR, yyyy);
        cal.set(Calendar.MONTH, mm - 1);
        cal.set(Calendar.DAY_OF_MONTH, dd);
        return cal.getTime();
    }

    public static Date dateFromYyyymmddhhmiss(String yyyymmddhhmiss) {
        if (yyyymmddhhmiss == null || yyyymmddhhmiss.length() != 14) {
            return null;
        }

        int yyyy = Integer.parseInt(yyyymmddhhmiss.substring(0, 4));
        int mm = Integer.parseInt(yyyymmddhhmiss.substring(4, 6));
        int dd = Integer.parseInt(yyyymmddhhmiss.substring(6, 8));
        int hh = Integer.parseInt(yyyymmddhhmiss.substring(8, 10));
        int mi = Integer.parseInt(yyyymmddhhmiss.substring(10, 12));
        int ss = Integer.parseInt(yyyymmddhhmiss.substring(12, 14));

        Calendar cal = Calendar.getInstance();
        cal.set(Calendar.YEAR, yyyy);
        cal.set(Calendar.MONTH, mm - 1);
        cal.set(Calendar.DAY_OF_MONTH, dd);
        cal.set(Calendar.HOUR_OF_DAY, hh);
        cal.set(Calendar.MINUTE, mi);
        cal.set(Calendar.SECOND, ss);

        return cal.getTime();
    }

    public static String yyyymmFromDate(Date date) {
        if (date == null) {
            return null;
        }

        Calendar cal = Calendar.getInstance();
        cal.setTime(date);

        int yyyy = cal.get(Calendar.YEAR);
        int mm = cal.get(Calendar.MONTH) + 1;
        return String.format("%04d%02d", yyyy, mm);
    }

    public static String yyyymm() {
        return yyyymmFromDate(new Date());
    }

    public static String yyyymmdd() {
        return yyyymmddFromDate(new Date());
    }

    public static String yyyymmddFromDate(Date date) {
        if (date == null) {
            return null;
        }

        Calendar cal = Calendar.getInstance();
        cal.setTime(date);

        int yyyy = cal.get(Calendar.YEAR);
        int mm = cal.get(Calendar.MONTH) + 1;
        int dd = cal.get(Calendar.DAY_OF_MONTH);
        return joinYyyymmdd(yyyy, mm, dd);
    }

    public static String yyyymmddhhmissFromDate(Date date) {
        if (date == null) {
            return null;
        }

        Calendar cal = Calendar.getInstance();
        cal.setTime(date);

        int yyyy = cal.get(Calendar.YEAR);
        int mm = cal.get(Calendar.MONTH) + 1;
        int dd = cal.get(Calendar.DAY_OF_MONTH);
        int hh = cal.get(Calendar.HOUR_OF_DAY);
        int mi = cal.get(Calendar.MINUTE);
        int ss = cal.get(Calendar.SECOND);
        return joinYyyymmdd(yyyy, mm, dd) + joinHhmiss(hh, mi, ss);
    }


    public static int countDaysBetween(String startYmd, String endYmd) {
        return countDaysBetween(dateFromYyyymmdd(startYmd),
                dateFromYyyymmdd(endYmd));
    }

    public static int countDaysBetween(Date start, Date end) {
        long startTime = start.getTime();
        long endTime = end.getTime();

        Calendar cal = Calendar.getInstance();
        cal.setTimeInMillis(startTime);
        cal.set(Calendar.HOUR_OF_DAY, 0);
        cal.set(Calendar.MINUTE, 0);
        cal.set(Calendar.SECOND, 0);
        cal.set(Calendar.MILLISECOND, 0);
        startTime = cal.getTimeInMillis();

        cal.setTimeInMillis(endTime);
        cal.set(Calendar.HOUR_OF_DAY, 0);
        cal.set(Calendar.MINUTE, 0);
        cal.set(Calendar.SECOND, 0);
        cal.set(Calendar.MILLISECOND, 0);
        endTime = cal.getTimeInMillis();

        long day = (long) ((endTime - startTime) / DAY_IN_MILLISECONDS);
        //		System.out.println(String.format("end:%d - start:%d = %d(%d days)",
        //				endTime, startTime, endTime - startTime, day));

        return (int) day;
    }

    @Nullable
    public static String maskPhoneNumber(@Nullable String phoneNumber) {
        if (phoneNumber == null) return null;

        String mask = "%s-%s**-*%s";
        phoneNumber = phoneNumber.replace("+", "").replace("-", "").replace(" ", "");
        if (phoneNumber.startsWith("82")) phoneNumber = phoneNumber.replaceFirst("82", "0");

        if (phoneNumber.length() == 11) { //010xxxxyyyy
            phoneNumber = String.format(mask, phoneNumber.substring(0, 3), phoneNumber.substring(3, 5), phoneNumber.substring(8));
        } else if (phoneNumber.length() == 10) { //010xxxyyyy
            phoneNumber = String.format(mask, phoneNumber.substring(0, 3), phoneNumber.substring(3, 4), phoneNumber.substring(7));
        } else {
            phoneNumber = null;
        }

        return phoneNumber;
    }

    @Nullable
    public static String nullIfEmpty(@Nullable String string) {
        if (string == null) return null;
        else if (string.length() == 0) return null;
        else return string;
    }

    public static Date truncateHour(Date date) {
        DateFormat formatter = new SimpleDateFormat("dd/MM/yyyy");

        try {
            return formatter.parse(formatter.format(date));
        } catch (ParseException e) {
            return null;
        }
    }

    public static String formatDateForLog(Date date) {
        DateFormat dateFormat = new SimpleDateFormat("MM-dd HH:mm:ss");
        return dateFormat.format(date);
    }

    public static String formatDateForLog(long date) {
        return formatDateForLog(new Date(date));
    }

    public static String formatDayForLog(long date) {
        DateFormat dateFormat = new SimpleDateFormat("MM-dd HH 시");
        return dateFormat.format(date);
    }

    @NonNull
    public static String time(long unixtime) {
        return time(unixtime, true);
    }

    @NonNull
    public static String time(long unixtime, boolean summary) {
        try {
            if (summary) return new SimpleDateFormat("yyMMdd.HH:mm:ss", Locale.getDefault()).format(unixtime);
            else return new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSZ", Locale.getDefault()).format(unixtime);
        } catch (Throwable e) {
            return String.valueOf(unixtime);
        }
    }

    public static String newMac(String mac) {
        return mac.replace(":","");
    }
}

