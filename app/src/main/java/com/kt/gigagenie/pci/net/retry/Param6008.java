package com.kt.gigagenie.pci.net.retry;

/**
 * Created by LeeBaeng on 2019-01-28.
 */

public class Param6008 extends RetryParam {
    boolean isNeedResponseToMc;
    String reqPkgName;

    public Param6008(RetryAPI owner, boolean _isNeedResponseToMc, String _reqPkgName){
        super(owner);
        isNeedResponseToMc = _isNeedResponseToMc;
        reqPkgName = _reqPkgName;
    }


    public String toString(){
        return super.toString() + "|param1=" + isNeedResponseToMc + ", param2=" + reqPkgName;
    }

    @Override
    protected void dispose() {
        reqPkgName = null;
    }


    public void setIsNeedResponseToMc( boolean _isNeedResponseToMc ) { isNeedResponseToMc = _isNeedResponseToMc; }
    public void setReqPkgName( String _reqPkgName ) { reqPkgName = _reqPkgName; }
    public boolean getIsNeedResponseToMc() { return isNeedResponseToMc; }
    public String getReqPkgName() { return reqPkgName; }
}
