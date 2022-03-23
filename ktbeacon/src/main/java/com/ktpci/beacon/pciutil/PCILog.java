package com.ktpci.beacon.pciutil;

import android.content.Context;
import android.os.Build;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static android.util.Log.println;

public class PCILog {

    @NonNull
    private static final Locale LOCALE = Locale.getDefault();


    /* Instantiation not allowed */
    private PCILog() {
    }

    /**
     * Log Level : VERBOSE(2)
     */
    public static void v(String format, Object... args) {
        if (format == null) return;
        println(Log.VERBOSE, getTag(), String.format(LOCALE, format, args));
    }

    public static void v(String message) {
        if (message == null) return;
        println(Log.VERBOSE, getTag(), message);
    }

    public static void v(Throwable error) {
        if (error == null) return;
        println(Log.VERBOSE, error.getClass().getSimpleName(), stackTraceOf(error));
    }

    /**
     * Log Level : DEBUG(3)
     */
    public static void d(String format, Object... args) {
        if (format == null) return;
        println(Log.DEBUG, getTag(), String.format(LOCALE, format, args));
    }

    public static void d(String message) {
        if (message == null) return;
        println(Log.DEBUG, getTag(), message);
    }

    public static void d(Throwable error) {
        if (error == null) return;
        println(Log.DEBUG, error.getClass().getSimpleName(), stackTraceOf(error));
    }

    /**
     * Log Level : INFO(4)
     */
    public static void i(String format, Object... args) {
        if (format == null) return;
        println(Log.INFO, getTag(), String.format(LOCALE, format, args));
    }

    public static void i(String message) {
        if (message == null) return;
        println(Log.INFO, getTag(), message);
    }

    public static void i(Throwable error) {
        if (error == null) return;
        println(Log.INFO, error.getClass().getSimpleName(), stackTraceOf(error));
    }

    /**
     * Log Level : WARN(5)
     */
    public static void w(String format, Object... args) {
        if (format == null) return;
        println(Log.WARN, getTag(), String.format(LOCALE, format, args));
    }

    public static void w(String message) {
        if (message == null) return;
        println(Log.WARN, getTag(), message);
    }

    public static void w(Throwable error) {
        if (error == null) return;
        println(Log.WARN, error.getClass().getSimpleName(), stackTraceOf(error));
    }

    /**
     * Log Level : ERROR(6)
     */
    public static void e(String format, Object... args) {
        if (format == null) return;
        println(Log.ERROR, getTag(), String.format(LOCALE, format, args));
    }

    public static void e(String message) {
        if (message == null) return;
        println(Log.ERROR, getTag(), message);
    }

    public static void e(Throwable error) {
        if (error == null) return;
        println(Log.ERROR, error.getClass().getSimpleName(), stackTraceOf(error));
    }

    /**
     * Log Level : ASSERT(7)
     */
    public static void wtf(String format, Object... args) {
        if (format == null) return;
        println(Log.ASSERT, getTag(), String.format(LOCALE, format, args));
    }

    public static void wtf(String message) {
        if (message == null) return;
        println(Log.ASSERT, getTag(), message);
    }

    public static void wtf(Throwable error) {
        if (error == null) return;
        println(Log.ASSERT, error.getClass().getSimpleName(), stackTraceOf(error));
    }



    /* Throwable StackTrace */
    private static String stackTraceOf(Throwable error) {
        final StringWriter stringWriter = new StringWriter();
        final PrintWriter printWriter = new PrintWriter(stringWriter);
        error.printStackTrace(printWriter);
        return stringWriter.toString();
    }

    /* Tag Detection */
    private static final int MAX_TAG_LENGTH = 23;
    private static final int CALL_STACK_INDEX = 3;
    private static final Pattern ANONYMOUS_CLASS = Pattern.compile("(\\$\\d+)+$");

    @NonNull
    private static String getTag() {
        StackTraceElement[] stackTrace = new Throwable().getStackTrace();
        StringBuilder tag = new StringBuilder("PCI_");

        if (stackTrace.length <= CALL_STACK_INDEX) tag.append("Anonymous");
        else tag.append(createStackElementTag(stackTrace[CALL_STACK_INDEX]));

        if (tag.length() <= MAX_TAG_LENGTH || Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) return tag.toString();
        else return tag.substring(0, MAX_TAG_LENGTH);
    }

    @NonNull
    private static String createStackElementTag(@NonNull StackTraceElement element) {
        String tag = element.getClassName();
        Matcher matcher = ANONYMOUS_CLASS.matcher(tag);

        if (matcher.find()) tag = matcher.replaceAll("");
        return tag.substring(tag.lastIndexOf('.') + 1);
    }
}
