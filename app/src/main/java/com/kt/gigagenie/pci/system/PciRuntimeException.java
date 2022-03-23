package com.kt.gigagenie.pci.system;

/**
 * Created by leebaeng on 19/03/2019.
 */

public class PciRuntimeException extends RuntimeException {
    public PciRuntimeException() {
        super();
    }

    public PciRuntimeException(String detailMessage) {
        super(detailMessage);
    }

    public PciRuntimeException(String detailMessage, Throwable throwable) {
        super(detailMessage, throwable);
    }

    public PciRuntimeException(Throwable throwable) {
        super(throwable);
    }
}
