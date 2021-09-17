package com.transsnet.transsdktest.view;

import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.Scroller;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.transsnet.transsdktest.R;
import com.transsnet.transsdktest.utils.ScreenUtils;

import java.lang.reflect.Field;

/**
 * Created by zr on 2018/10/17.
 */
public class SwipeRefreshLoadLayout extends SwipeRefreshLayout {
    private VerticalViewPager verticalViewPager;
    private ProgressBar progressBar;
    private Scroller mScroller;
    private int dy;
    private static int maxScrollY;
    private static int footerHeight;
    private View footerView;
    //todo loadenable
    private boolean loading;
    private OnRefreshLoadListener loadListener;
    private boolean mLoadMoreEnable = true;


    public SwipeRefreshLoadLayout(@NonNull Context context) {
        super(context);
        initLoadView(context);
    }

    public SwipeRefreshLoadLayout(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        initLoadView(context);
    }

    private void initLoadView(Context context) {
        if (!mLoadMoreEnable) {
            return;
        }
        footerView = LayoutInflater.from(context).inflate(R.layout.swipe_footer, this, false);
        addView(footerView);
        progressBar = findViewById(R.id.pb_foot);
        mScroller = new Scroller(context);
        maxScrollY = (int) ScreenUtils.INSTANCE.dpToPx(context, 200f);
        footerHeight = (int)ScreenUtils.INSTANCE.dpToPx(context, 200);
    }


    private void ensureView() {
        if (verticalViewPager == null) {
            int childCount = getChildCount();
            for (int i = 0; i < childCount; i++) {
                View view = getChildAt(i);
                if (view instanceof VerticalViewPager) {
                    verticalViewPager = (VerticalViewPager) view;
                }
            }
            gettarget();
        }
    }

    private void gettarget() {
        try {
            Field mTarget = SwipeRefreshLayout.class.getDeclaredField("mTarget");
            mTarget.setAccessible(true);
            mTarget.set(this, verticalViewPager);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private int x1;
    private int y1;

    @Override
    public void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        ensureView();
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        if (mLoadMoreEnable && footerView != null) {
            footerView.measure(MeasureSpec.makeMeasureSpec(getMeasuredWidth(), MeasureSpec.EXACTLY),
                    MeasureSpec.makeMeasureSpec(footerHeight, MeasureSpec.EXACTLY));
        }
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        if (!mLoadMoreEnable) {
            return super.onInterceptTouchEvent(ev);
        }
        switch (ev.getAction()) {
            case MotionEvent.ACTION_DOWN:
                x1 = (int) ev.getX();
                y1 = (int) ev.getY();
                break;
        }
        boolean intercept = super.onInterceptTouchEvent(ev);
        if (intercept) {
            return true;
        }
        if (verticalViewPager != null) {
            return !verticalViewPager.onRefreshTouchEvent(ev);
        }
        return false;
    }


    private void smoothScrollByScroller(int scrollY) {
        mScroller.startScroll(0, scrollY, 0, -scrollY, 300);
        invalidate();
    }


    @Override
    public void computeScroll() {
        if (mLoadMoreEnable && mScroller.computeScrollOffset()) {
            scrollTo(mScroller.getCurrX(), mScroller.getCurrY());
            invalidate();
        }
    }

    @Override
    public boolean onTouchEvent(MotionEvent ev) {
        if (!mLoadMoreEnable) {
            return super.onTouchEvent(ev);
        }
        Log.i("touch", "ontouch");
        switch (ev.getAction()) {
            case MotionEvent.ACTION_MOVE:
                int x2 = (int) ev.getX();
                int y2 = (int) ev.getY();
                dy = y2 - y1;
                int dx = x2 - x1;
                if (dy < 0 && Math.abs(dy) <= maxScrollY && !isRefreshing()) {
                    scrollTo(0, -dy);
                    loading =true;
                    return true;
                }
                break;
            case MotionEvent.ACTION_UP:
                if (loading && !isRefreshing() && loadListener != null) {
                    loading = false;
                    loadListener.onload();
                }
                int scrollY = getScrollY();
                smoothScrollByScroller(scrollY);
                break;
        }
        return super.onTouchEvent(ev);
    }

    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        if (!mLoadMoreEnable) {
            super.onLayout(changed, left, top, right, bottom);
            return;
        }
        ensureView();
        if (footerView != null) {
            footerView.layout(0, getMeasuredHeight(), getMeasuredWidth(), getMeasuredHeight() + footerView.getMeasuredHeight());
        }
        super.onLayout(changed, left, top, right, bottom);
    }

    public void completeLoadMore() {
        loading = false;
    }

    public interface OnRefreshLoadListener extends OnRefreshListener {
        void onload();
    }

    public void setLoadMoreEnable(boolean enable) {
        mLoadMoreEnable = enable;
    }

    /**
     * Set the listener to be notified when a refresh is triggered via the swipe
     * gesture.
     */
    public void setOnRefreshLoadListener(@Nullable OnRefreshLoadListener listener) {
        loadListener = listener;
        super.setOnRefreshListener(listener);
    }

}
