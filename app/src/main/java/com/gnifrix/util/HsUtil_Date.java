package com.gnifrix.util;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

/**
 * Created by LeeBaeng on 2018-09-10.
 */

public class HsUtil_Date {

    public static String getTimeString(Date date, String format){
        return new SimpleDateFormat(format).format(date);
    }

    public static String getTimeString(Date date){
        return getTimeString(date, "yyyy-MM-dd HH:mm:ss:SSS");
    }

    public static String getCurrentTimeStr(){
        return getTimeString(Calendar.getInstance().getTime());
    }

    public static String getCurrentTimeStr(String format){
        return getTimeString(Calendar.getInstance().getTime(), format);
    }

    public static Date getCurrentTime(){
        return Calendar.getInstance().getTime();
    }

    public static Date getDateObject(String str) throws ParseException{
        return getDateObject(str, "yyyy-MM-dd HH:mm:ss:SSS");
    }

    public static Date getDateObject(String str, String format) throws ParseException{
        return new SimpleDateFormat(format).parse(str);
    }
}
