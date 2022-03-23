package com.ktpci.beacon.pciutil;

import android.content.Context;
import android.content.SharedPreferences;
import android.text.TextUtils;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.ktpci.beacon.C;
import com.ktpci.beacon.pciutil.PCIStorageKey.StorageKey;

import java.util.Map;

public class PCIStorage {

    private static final String TAG = PCIStorage.class.getSimpleName();

    private static final int STORAGE_MODE = Context.MODE_PRIVATE;
//    private static final int STORAGE_WORLD_R_MODE = Context.MODE_WORLD_READABLE;
//    private static final int STORAGE_WORLD_W_MODE = Context.MODE_WORLD_WRITEABLE;
    private static final String STORAGE_NAME = "com.ktpci.beacon";

//    public static final boolean DEFAULTT_IS_AVOID = true;

    private PCIStorage() {}

    /* SharedPreference Method Mapping */
    public static boolean contains(@NonNull Context context, @StorageKey @NonNull String key) {
        final boolean contains = context.getApplicationContext().getSharedPreferences(STORAGE_NAME, STORAGE_MODE).contains(key);
//        final boolean contains = context.getApplicationContext().getSharedPreferences(STORAGE_NAME, STORAGE_WORLD_R_MODE + STORAGE_WORLD_W_MODE).contains(key);
        if (contains) PCILog.d(TAG, " O  : Storage[%s]", key);
        else PCILog.d(TAG, " X  : Storage[%s]", key);
        return contains;
    }

    public static Map<String, ?> getAll(@NonNull Context context) {
        PCILog.d(TAG, "ALL : Storage");
        return context.getApplicationContext().getSharedPreferences(STORAGE_NAME, STORAGE_MODE).getAll();
    }

    public static boolean getBoolean(@NonNull Context context, @StorageKey @NonNull String key, boolean fallback) {
        final boolean value = context.getApplicationContext().getSharedPreferences(STORAGE_NAME, STORAGE_MODE).getBoolean(key, fallback);
        PCILog.d(TAG, "GET : Storage[%s] <-- %s (fallback : %s)", key, value, fallback);
        return value;
    }

    @Nullable
    public static Boolean getBooleanNullable(@NonNull Context context, @StorageKey @NonNull String key) {
        if (PCIStorage.contains(context, key)) {
            final boolean value = context.getApplicationContext().getSharedPreferences(STORAGE_NAME, STORAGE_MODE).getBoolean(key, false);
            PCILog.d(TAG, "GET : Storage[%s] <-- %s (fallback : false(hard-coded))", key, value);
            return value;
        } else {
            return null;
        }
    }

    public static float getFloat(@NonNull Context context, @StorageKey @NonNull String key, float fallback) {
        final float value = context.getApplicationContext().getSharedPreferences(STORAGE_NAME, STORAGE_MODE).getFloat(key, fallback);
        PCILog.d(TAG, "GET : Storage[%s] <-- %s (fallback : %s)", key, value, fallback);
        return value;
    }

    public static int getInt(@NonNull Context context, @StorageKey @NonNull String key, int fallback) {
        final int value = context.getApplicationContext().getSharedPreferences(STORAGE_NAME, STORAGE_MODE).getInt(key, fallback);
        PCILog.d(TAG, "GET : Storage[%s] <-- %s (fallback : %s)", key, value, fallback);
        return value;
    }

    public static long getLong(@NonNull Context context, @StorageKey @NonNull String key, long fallback) {
        final long value = context.getApplicationContext().getSharedPreferences(STORAGE_NAME, STORAGE_MODE).getLong(key, fallback);
        PCILog.d(TAG, "GET : Storage[%s] <-- %s (fallback : %s)", key, value, fallback);
        return value;
    }

    @NonNull
    public static String getString(@NonNull Context context, @StorageKey @NonNull String key, @NonNull String fallback) {
        try {
            final String value = context.getApplicationContext().getSharedPreferences(STORAGE_NAME, STORAGE_MODE).getString(key, fallback);
//            final String value = context.getApplicationContext().getSharedPreferences(STORAGE_NAME, STORAGE_WORLD_R_MODE + STORAGE_WORLD_W_MODE).getString(key, fallback);
            PCILog.d(TAG, "GET : Storage[%s] <-- %s (fallback : %s)", key, value, fallback);
            return value;
        } catch (ClassCastException e) {
            PCILog.e(e);
            PCIStorage.remove(context, key);
            return fallback;
        }
    }

    @Nullable
    public static String getStringNullable(@NonNull Context context, @StorageKey @NonNull String key) {
        try {
            final String value = context.getApplicationContext().getSharedPreferences(STORAGE_NAME, STORAGE_MODE).getString(key, null);
//            final String value = context.getApplicationContext().getSharedPreferences(STORAGE_NAME, STORAGE_WORLD_R_MODE + STORAGE_WORLD_W_MODE).getString(key, null);
            PCILog.d(TAG, "GET : Storage[%s] <-- %s (fallback : null(hard-coded))", key, value);
            return value;
        } catch (ClassCastException e) {
            PCILog.e(e);
            PCIStorage.remove(context, key);
            return null;
        }
    }

    /* SharedPreferences.Editor Method Mapping */
    public static void clearAll(@NonNull Context context) {
        PCILog.d(TAG, "CLEAR-ALL : Storage");
        PCIStorage.clear(context);
    }

    public static void clear(@NonNull Context context) {
        PCILog.d(TAG, "CLR : Storage");
        context.getApplicationContext().getSharedPreferences(STORAGE_NAME, STORAGE_MODE).edit().clear().apply();
//        context.getApplicationContext().getSharedPreferences(STORAGE_NAME, STORAGE_WORLD_R_MODE + STORAGE_WORLD_W_MODE).edit().clear().apply();
    }

    public static void put(@NonNull Context context, @StorageKey @NonNull String key, boolean value) {
        PCILog.d(TAG, "PUT : Storage[%s] <-- %s", key, value);
        context.getApplicationContext().getSharedPreferences(STORAGE_NAME, STORAGE_MODE).edit().putBoolean(key, value).apply();
//        context.getApplicationContext().getSharedPreferences(STORAGE_NAME, STORAGE_WORLD_R_MODE + STORAGE_WORLD_W_MODE).edit().putBoolean(key, value).apply();
    }

    public static void put(@NonNull Context context, @StorageKey @NonNull String key, float value) {
        PCILog.d(TAG, "PUT : Storage[%s] <-- %s", key, value);
        context.getApplicationContext().getSharedPreferences(STORAGE_NAME, STORAGE_MODE).edit().putFloat(key, value).apply();
//        context.getApplicationContext().getSharedPreferences(STORAGE_NAME, STORAGE_WORLD_R_MODE + STORAGE_WORLD_W_MODE).edit().putFloat(key, value).apply();
    }

    public static void put(@NonNull Context context, @StorageKey @NonNull String key, int value) {
        PCILog.d(TAG, "PUT : Storage[%s] <-- %s", key, value);
        context.getApplicationContext().getSharedPreferences(STORAGE_NAME, STORAGE_MODE).edit().putInt(key, value).apply();
//        context.getApplicationContext().getSharedPreferences(STORAGE_NAME, STORAGE_WORLD_R_MODE + STORAGE_WORLD_W_MODE).edit().putInt(key, value).apply();
    }

    public static void put(@NonNull Context context, @StorageKey @NonNull String key, long value) {
        PCILog.d(TAG, "PUT : Storage[%s] <-- %s", key, value);
        context.getApplicationContext().getSharedPreferences(STORAGE_NAME, STORAGE_MODE).edit().putLong(key, value).apply();
//        context.getApplicationContext().getSharedPreferences(STORAGE_NAME, STORAGE_WORLD_R_MODE + STORAGE_WORLD_W_MODE).edit().putLong(key, value).apply();
    }

    public static void put(@NonNull Context context, @StorageKey @NonNull String key, @NonNull String value) {
        PCILog.d(TAG, "PUT : Storage[%s] <-- %s", key, value);
        context.getApplicationContext().getSharedPreferences(STORAGE_NAME, STORAGE_MODE).edit().putString(key, value).apply();
//        context.getApplicationContext().getSharedPreferences(STORAGE_NAME, STORAGE_WORLD_R_MODE + STORAGE_WORLD_W_MODE).edit().putString(key, value).apply();
    }

    public static void remove(@NonNull Context context, @StorageKey @NonNull String key) {
        PCILog.d(TAG, "DEL : Storage[%s]", key);
        context.getApplicationContext().getSharedPreferences(STORAGE_NAME, STORAGE_MODE).edit().remove(key).apply();
//        context.getApplicationContext().getSharedPreferences(STORAGE_NAME, STORAGE_WORLD_R_MODE + STORAGE_WORLD_W_MODE).edit().remove(key).apply();
    }

    /* Counter */
    public static void countInt(@NonNull Context context, @StorageKey @NonNull String key) {
        final SharedPreferences preferences = context.getApplicationContext().getSharedPreferences(STORAGE_NAME, STORAGE_MODE);
        final int count = preferences.getInt(key, 0);
        preferences.edit().putInt(key, count + 1).apply();
        PCILog.d(TAG, "CNT : Storage[%s] %s <-- %s", key, count, count + 1);
    }

    public static void countLong(@NonNull Context context, @StorageKey @NonNull String key) {
        final SharedPreferences preferences = context.getApplicationContext().getSharedPreferences(STORAGE_NAME, STORAGE_MODE);
        final long count = preferences.getLong(key, 0);
        preferences.edit().putLong(key, count + 1).apply();
        PCILog.d(TAG, "CNT : Storage[%s] %s <-- %s", key, count, count + 1);
    }

    /* Key Generator */
    @NonNull
    public static String key(@NonNull Object key1, @NonNull Object key2) {
        return key1 + "." + key2;
    }

    @NonNull
    public static String key(@NonNull Object key1, @NonNull Object key2, @NonNull Object key3) {
        return key1 + "." + key2 + "." + key3;
    }

    /* Gson Converter */
    @NonNull
    public static PCIGsonConverter loadGson(@NonNull Context context, @StorageKey @NonNull String key) {
        return new PCIGsonConverter(PCIStorage.getStringNullable(context, key));
    }

    public static <T> void saveGson(@NonNull Context context, @StorageKey @NonNull String key, @Nullable T model) {
        final String value = PCIGsonConverter.load(model).take();
        if (value == null) PCIStorage.remove(context, key);
        else PCIStorage.put(context, key, value);
    }

    public static class PCIGsonConverter implements C {

        @Nullable
        private final String json;

        private PCIGsonConverter(@Nullable String json) {
            this.json = json;
        }

        @Nullable
        public <T> T as(Class<T> clazz) {
            if (TextUtils.isEmpty(json)) return null;
            else try {
                return gson.fromJson(json, clazz);
            } catch (Throwable e) {
                // JsonSyntaxException
                return null;
            }
        }

        @Nullable
        public String take() {
            return json;
        }

        @NonNull
        public static PCIGsonConverter load(@Nullable String json) {
            return new PCIGsonConverter(json);
        }

        @NonNull
        public static <T> PCIGsonConverter load(@Nullable T model) {
            return new PCIGsonConverter(gson.toJson(model));
        }
    }
}

