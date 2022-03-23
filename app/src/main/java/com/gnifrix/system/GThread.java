package com.gnifrix.system;

/**
 * Created by LeeBaeng on 2019-01-17.
 */

public class GThread extends Thread {
    public GThread(){
        super();
    }

    public GThread(String name){
        super();
        setName(name);
    }

    public GThread(GRunnable runnable){
        super(runnable);
    }

    public GThread(String name, GRunnable runnable){
        super(runnable);
        setName(name);
        runnable.setName(name);
    }

    public String toString(){
        return getName();
    }
}
