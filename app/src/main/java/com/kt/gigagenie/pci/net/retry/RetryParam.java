package com.kt.gigagenie.pci.net.retry;

/**
 * Created by LeeBaeng on 2019-01-28.
 */

public abstract class RetryParam {
    RetryAPI owner;

    public RetryParam( RetryAPI _owner ) {
        owner = _owner;
    }

    public void setOwner( RetryAPI _owner ) { owner = _owner; }

    public String toString(){
        if(owner != null) return "RETRY_PARAM|"+ owner.toString();
        return "RETRY_PARAM";
    }

    public void destory(){
        dispose();
        owner = null;
    }
    protected abstract void dispose();

}
