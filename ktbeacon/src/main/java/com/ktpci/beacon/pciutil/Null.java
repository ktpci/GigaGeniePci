package com.ktpci.beacon.pciutil;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

public class Null{
    public Null() {
    }

    @NonNull
    public static <T> T safe(@Nullable T var0, @NonNull T var1) {
        return var0 == null ? var1 : var0;
    }
}