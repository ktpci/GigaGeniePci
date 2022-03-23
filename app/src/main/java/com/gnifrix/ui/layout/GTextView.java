package com.gnifrix.ui.layout;

import android.content.Context;
import android.graphics.Canvas;
import android.util.AttributeSet;
import android.widget.TextView;

import com.gnifrix.debug.GLog;

/**
 * Created by LeeBaeng on 2018-09-20.
 */

public class GTextView extends TextView {
    public GTextView(Context context){
        super(context);
    }

    public GTextView(Context context, AttributeSet attrs){
        super(context, attrs);
    }

    public GTextView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }


    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        scrollTo(0, GLog.getScreenLogScrollPosition());
    }
}
