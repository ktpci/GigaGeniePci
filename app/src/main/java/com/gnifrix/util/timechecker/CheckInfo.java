package com.gnifrix.util.timechecker;

import com.gnifrix.debug.GLog;

/**
 * Created by LeeBaeng on 2019-01-30.
 */

public class CheckInfo {
    public static final int STATE_READY     = 1;
    public static final int STATE_RUNNING   = 2;
    public static final int STATE_FINISHED  = 3;

    private int state = STATE_READY;
    private long startTime = -1;
    private long endTime = -1;
    private long leadTime = -1;
    private String id;

    public CheckInfo(String id){
        this.id = id;
    }

    public void start(long _startTime ) {
        startTime = _startTime;
        state = STATE_RUNNING;
    }

    public void end(long _endTime ) {
        if(state == STATE_RUNNING){
            endTime = _endTime;
            leadTime = endTime-startTime;
            state = STATE_FINISHED;
        }else{
            GLog.printInfo("CheckInfo","Can't finish timeCheck!! " + id + " is not running state. (state : " + state + ")");
        }
    }

    public int getState() { return state; }
    public long getStartTime() { return startTime; }
    public long getEndTime() { return endTime; }
    public long getLeadTime() { return leadTime; }
    public String getId() { return id; }
    public void setId(String _id ) { id = _id; }

}
