package com.gnifrix.system;

/**
 * Created by LeeBaeng on 2019-01-17.
 */

public abstract class GRunnable implements Runnable {
    String name;

    public void GRunnable(){}

    public void GRunnable(String _name){
        name = _name;
    }

    public abstract void run();

    public void setName(String _name){ name = _name;}
    public String getName(){ return name; }
    public String toString(){ return name; }
}
