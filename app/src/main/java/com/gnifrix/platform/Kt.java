package com.gnifrix.platform;

/**
 * Platform-KT
 * Created by LeeBaeng on 2018-10-04.
 */

public class Kt extends Platform {
    @Override
    protected void init() {
        setPlatformType(PLATFORM_TYPE_STB);
    }

    @Override
    protected void dispose() {

    }
}
