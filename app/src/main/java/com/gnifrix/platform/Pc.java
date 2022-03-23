package com.gnifrix.platform;

/**
 * Platform-PC
 * Created by LeeBaeng on 2018-10-04.
 */

public class Pc extends Platform {
    @Override
    protected void init() {
        setPlatformType(PLATFORM_TYPE_PC);

    }

    @Override
    protected void dispose() {

    }
}
