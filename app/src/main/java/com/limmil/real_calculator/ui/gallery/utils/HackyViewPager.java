package com.limmil.real_calculator.ui.gallery.utils;

import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;

import androidx.viewpager.widget.ViewPager;

/**
 * There are some ViewGroups (ones that utilize onInterceptTouchEvent)
 * that throw exceptions
 * when a PhotoView is placed within them,
 * most notably ViewPager and DrawerLayout.
*/
public class HackyViewPager extends ViewPager {
    public HackyViewPager(Context context) {
        super(context);
    }

    public HackyViewPager(Context context, AttributeSet attrs) {
        super(context, attrs);
    }
    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        try {
            return super.onInterceptTouchEvent(ev);
        }
        catch (IllegalArgumentException e) {
            e.printStackTrace();
        }
        catch (ArrayIndexOutOfBoundsException e) {
        }
        return false;
    }
}
