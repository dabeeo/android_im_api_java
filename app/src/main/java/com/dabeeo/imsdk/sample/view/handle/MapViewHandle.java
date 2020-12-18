package com.dabeeo.imsdk.sample.view.handle;

import android.animation.ObjectAnimator;
import android.content.Context;
import android.util.DisplayMetrics;
import android.util.IntProperty;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AccelerateInterpolator;

import com.dabeeo.imsdk.navigation.PathRequest;

public class MapViewHandle implements View.OnTouchListener {

    public enum BarState {
        Top, Middle, Bottom
    }

    private Context context;
    private View handle;
    private ViewGroup container;
    private BarState barState;

    private int heightScreen;
    private int heightExpand;
    private int heightMid;
    private int heightCollapse;

    private ViewGroup.LayoutParams params;
    private ObjectAnimator animator;
    private float preY;

    public MapViewHandle(Context context, View handle, ViewGroup container) {
        this.context = context;
        this.handle = handle;
        this.container = container;

        init();
    }

    private void init() {
        handle.setOnTouchListener(this);

        params = container.getLayoutParams();

        DisplayMetrics metrics = new DisplayMetrics();
        context.getDisplay().getRealMetrics(metrics);

        int statusBarHeight = 0;
        int resourceId = context.getResources().getIdentifier("status_bar_height", "dimen", "android");
        if (resourceId > 0) {
            statusBarHeight = context.getResources().getDimensionPixelSize(resourceId);
        }

        int bottomBarHeight = 0;
        int resourceIdBottom = context.getResources().getIdentifier("navigation_bar_height", "dimen", "android");
        if (resourceIdBottom > 0) {
            bottomBarHeight = context.getResources().getDimensionPixelSize(resourceIdBottom);
        }

        heightScreen = metrics.heightPixels;
        heightExpand = heightScreen - statusBarHeight;
        heightMid = (heightScreen / 2) - handle.getLayoutParams().height;
        heightCollapse = handle.getLayoutParams().height;

        animator = ObjectAnimator.ofInt(container, new IntProperty<ViewGroup>("height") {
            @Override
            public void setValue(ViewGroup v, int value) {
                v.getLayoutParams().height = value;
                v.setLayoutParams(v.getLayoutParams());
            }

            @Override
            public Integer get(ViewGroup v) {
                return v.getLayoutParams().height;
            }
        }, container.getLayoutParams().height, 0);
        animator.setDuration(200);
        animator.setInterpolator(new AccelerateInterpolator());

        moveBar(BarState.Top);
    }

    public BarState getCurrentState() {
        return barState;
    }

    @Override
    public boolean onTouch(View v, MotionEvent event) {
        boolean result = gestureDetector.onTouchEvent(event);

        if (event.getAction() == MotionEvent.ACTION_UP && !result) {
            float ratio = (float) params.height / heightScreen;

            if (ratio >= 0.7f) {
                moveBar(BarState.Top);
            } else if (ratio <= 0.3f) {
                moveBar(BarState.Bottom);
            } else {
                moveBar(BarState.Middle);
            }
        }

        return true;
    }

    public void moveBar(BarState state) {
        barState = state;

        int targetValue = 0;

        switch (state) {
            case Top:
                targetValue = heightExpand;
                break;
            case Middle:
                targetValue = heightMid;
                break;
            case Bottom:
                targetValue = heightCollapse;
                break;
        }

        animator.setIntValues(container.getLayoutParams().height, targetValue);
        animator.setupStartValues();
        animator.start();
    }

    private GestureDetector gestureDetector = new GestureDetector(
            context,
            new GestureDetector.OnGestureListener() {
                @Override
                public boolean onDown(MotionEvent e) {
                    preY = e.getRawY();

                    return true;
                }

                @Override
                public void onShowPress(MotionEvent e) {

                }

                @Override
                public boolean onSingleTapUp(MotionEvent e) {
                    switch (barState) {
                        case Top:
                        case Bottom:
                            moveBar(BarState.Middle);
                            break;
                        case Middle:
                            moveBar(BarState.Bottom);
                            break;
                    }

                    return true;
                }

                @Override
                public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY) {
                    float rawY = e2.getRawY();
                    int diff = (int) (preY - rawY);
                    params.height = params.height + diff;

                    container.setLayoutParams(params);
                    preY = rawY;

                    return true;
                }

                @Override
                public void onLongPress(MotionEvent e) {

                }

                @Override
                public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
                    float diff = e2.getY() - e1.getY();
                    if (params.height >= heightMid) {
                        if (diff < 0) {
                            moveBar(BarState.Top);
                        } else {
                            moveBar(BarState.Middle);
                        }
                    } else {
                        if (diff < 0) {
                            moveBar(BarState.Middle);
                        } else {
                            moveBar(BarState.Bottom);
                        }
                    }

                    return true;
                }
            });
}
