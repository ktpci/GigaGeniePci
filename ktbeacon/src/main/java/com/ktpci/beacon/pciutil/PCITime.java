package com.ktpci.beacon.pciutil;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class PCITime {

    public static String currentDate(String DateFormat){     // String
        String today = null;
        long now = System.currentTimeMillis(); // current Time
        Date date = new Date(now);
        SimpleDateFormat sdf = new SimpleDateFormat(DateFormat); // yyyyMMdd HH mm ss SSS
        today = sdf.format(date);

        return today;
    }

    public static String postDate(String DateFormat){
        String today = null;
        long now = System.currentTimeMillis(); // current Time
        Date date = new Date(now);
        SimpleDateFormat sdf = new SimpleDateFormat(DateFormat); // yyyyMMdd HH mm ss SSS
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
//        cal.add(Calendar.HOUR, +24);
        cal.add(Calendar.HOUR, +2);
        today = sdf.format(cal.getTime());

        return today;
    }

    public static String preDate(String DateFormat){
        String predate = null;
        long now = System.currentTimeMillis(); // current Time
        Date date = new Date(now);
        date = new Date(date.getTime()+(1000*60*60*24*-1));
        SimpleDateFormat sdf = new SimpleDateFormat(DateFormat); // yyyyMMdd HH mm ss SSS
        predate = sdf.format(date);

        return predate;
    }


    public static int currentTime(String timeFormat){   // int
        int scheduleTime = 0;
        long now = System.currentTimeMillis(); // current Time
        Date date = new Date(now);
        SimpleDateFormat sdf = new SimpleDateFormat(timeFormat); // HHmm -> 0~24
        scheduleTime = Integer.parseInt(sdf.format(date));

        return scheduleTime;
    }

    public static boolean compareDate(String RegiDate, String CurrDate){

        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmmss"); // yyyyMMdd HH mm ss SSS
        Date dayOne = null;
        Date dayTwo = null;
        try{
            dayOne = sdf.parse(RegiDate);
            dayTwo = sdf.parse(CurrDate);
        }catch (ParseException e){

        }
        int compare = dayOne.compareTo(dayTwo);

        if(compare >= 0){
            return false;
        }else{
            return true;
        }
    }
}
