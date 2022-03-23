package com.gnifrix.ui.layout;

import android.content.ContextWrapper;
import android.view.KeyEvent;

/**
 * Created by LeeBaeng on 2018-09-04.
 */

public abstract class GniLayout {
    protected ContextWrapper context = null;
    public GniLayout(ContextWrapper context){
        this.context = context;
    }

    public void destory(){
        dispose();
        context = null;
    }


    public abstract void init();
    public abstract void start();
    public abstract void pause();
    public abstract void stop();
    public abstract void dispose();
    public abstract void onKey(int keyCode, KeyEvent event);

    public ContextWrapper getContext() { return context; }
    public void setContext( ContextWrapper _context ) { context = _context; }
}
