package com.kt.gigagenie.pci.net.retry;

/**
 * Created by LeeBaeng on 2019-01-25.
 */
public class RetryAPI {
    String apiCode;
    RetryParam param;

    public RetryAPI(String _apiCode, RetryParam _param){
        apiCode = _apiCode;
        param = _param;
    }

    public void destory(){
        if(param != null) param.destory();
        apiCode = null;
        param = null;
    }

    public String getApiCode() { return apiCode; }
    public RetryParam getParam() { return param; }
    public void setApiCode( String _apiCode ) { apiCode = _apiCode; }
    public void setParam( RetryParam _param ) { param = _param; }


}
