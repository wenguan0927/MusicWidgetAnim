package com.music.widget.version3;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.widget.RemoteViews.RemoteView;

/**
 * ClassName: CatearProgressView <br/>
 * Function: TODO ADD FUNCTION. <br/>
 * Date:2016-1-26
 * 
 * @author wenguan.chen
 * @version 0.1
 * @since MT 1.0
 */
@RemoteView
public class CatearProgressView extends View {

    private static final String TAG = "CatearProgressView";
    private boolean mIsPlaying = false;
    private boolean mIsScreenOn = true;
    private boolean mIsLauncherActive = true;

    private final String LAUNCHER_RESUME = "com.meitu.mobile.widget.WIDGET_START";
    private final String LAUNCHER_STOP = "com.meitu.mobile.widget.WIDGET_END";

    private int mViewWidth;
    private int mViewHeight;
    private int mCurveWidth;
    private int mCurveHeight;
    private int mCurveSideLength;
    private Paint mPaint;
    private Context mContext;
    private int mProgress;
    private int mProgressRatio;
    private int mMax;
    private final float mPaintBgStrokeWidth = 5;
    private final float mPaintPrgStrokeWidth = 6f;
    private final float mPaintRectifyValue = 0.5f;
    private int mProgressBgColor = Color.WHITE;
    private int mProgressColor = Color.rgb(65, 208, 120);
    private Path mBgPath;
    private Path mAnimPrgPath;
    private int mArcLeft;
    private int mArcTop;
    private int mArcRight;
    private int mArcBottom;
    private int mProgressLength;
    private int mArcRatio;

    public CatearProgressView(Context context, AttributeSet attrs) {
        super(context, attrs, 0);
        this.mContext = context;
        init();
    }

    public CatearProgressView(Context context) {
        this(context, null);
    }
	
    /**
     * init: TODO<br/>
     * 
     * @author wenguan.chen
     * @since MT 1.0
     */
    private void init() {
        if (mPaint == null) {
            mPaint = new Paint();
            mPaint.setColor(Color.WHITE);
            mPaint.setDither(true);
            mPaint.setAntiAlias(true);
        }

        if (mAnimPrgPath == null) {
            mAnimPrgPath = new Path();
        }
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        mViewWidth = getWidth();
        mViewHeight = getHeight();
        mCurveWidth = mViewWidth - getPaddingLeft() - getPaddingRight();
        mCurveHeight = mViewHeight - getPaddingTop() - getPaddingBottom();
        mCurveSideLength = mCurveWidth < mCurveHeight ? mCurveWidth
                : mCurveHeight;

        mArcLeft = getPaddingLeft();
        mArcTop = getPaddingTop();
        mArcRight = mCurveSideLength + getPaddingLeft();
        mArcBottom = mCurveSideLength + getPaddingTop();

        mBgPath = new Path();
        mBgPath.moveTo(mArcRight - mPaintBgStrokeWidth / 2, mArcTop
                - mPaintBgStrokeWidth / 2);
        mBgPath.lineTo(mArcRight - mPaintBgStrokeWidth / 2, mArcBottom);
        mBgPath.close();

        mProgressLength = (int)(Math.PI * mCurveSideLength / 2 + (mArcBottom
                - mArcTop + mPaintBgStrokeWidth / 2));
        mArcRatio = (int)((Math.PI * mCurveSideLength * 100 / 2) / mProgressLength);
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        // draw background line path
        mPaint.setColor(mProgressBgColor);
        mPaint.setStyle(Paint.Style.STROKE);
        mPaint.setStrokeWidth(mPaintBgStrokeWidth);
        canvas.drawArc(mArcLeft, mArcTop, mCurveSideLength * 2
                + getPaddingLeft(), mCurveSideLength * 2 + getPaddingTop(),
                -180, 90, false, mPaint);

        canvas.drawPath(mBgPath, mPaint);

        // draw current progress
        mPaint.setStrokeWidth(mPaintPrgStrokeWidth);
        mPaint.setColor(mProgressColor);
        if (mProgressRatio <= mArcRatio) {
            canvas.drawArc(mArcLeft - mPaintRectifyValue, mArcTop
                    - mPaintRectifyValue, mCurveSideLength * 2
                    + getPaddingLeft() + mPaintRectifyValue, mCurveSideLength
                    * 2 + getPaddingTop(), -180, 90 * mProgressRatio
                    / mArcRatio, false, mPaint);
        } else {
            canvas.drawArc(mArcLeft - mPaintRectifyValue, mArcTop
                    - mPaintRectifyValue, mCurveSideLength * 2
                    + getPaddingLeft() + mPaintRectifyValue, mCurveSideLength
                    * 2 + getPaddingTop(), -180, 90, false, mPaint);

            mAnimPrgPath.reset();
            mAnimPrgPath.moveTo(mArcRight - mPaintPrgStrokeWidth / 2
                    + mPaintRectifyValue, mArcTop - mPaintPrgStrokeWidth / 2
                    - mPaintRectifyValue);
            mAnimPrgPath.lineTo(mArcRight - mPaintPrgStrokeWidth / 2
                    + mPaintRectifyValue, mArcTop + (mArcBottom - mArcTop)
                    * (mProgressRatio - mArcRatio) / (100 - mArcRatio));
            mAnimPrgPath.close();

            canvas.drawPath(mAnimPrgPath, mPaint);
        }
    }

    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
        IntentFilter filter = new IntentFilter();
        filter.addAction(Intent.ACTION_SCREEN_OFF);
        filter.addAction(Intent.ACTION_SCREEN_ON);
        filter.addAction(LAUNCHER_RESUME);
        filter.addAction(LAUNCHER_STOP);
        mContext.registerReceiver(mReceiver, filter);
        refreshProgress();
    }

    @Override
    protected void onDetachedFromWindow() {
        removeCallbacks(mRefreshRunnable);
        mContext.unregisterReceiver(mReceiver);
        super.onDetachedFromWindow();
    }

    private BroadcastReceiver mReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (action.equals(Intent.ACTION_SCREEN_OFF)) {
                setScreenState(false);
            } else if (action.equals(Intent.ACTION_SCREEN_ON)) {
                setScreenState(true);
            } else if (action.equals(LAUNCHER_RESUME)) {
                setLauncherState(true);
            } else if (action.equals(LAUNCHER_STOP)) {
                setLauncherState(false);
            }
        }
    };

    /**
     * setProgressColor: TODO<br/>
     * 
     * @author wenguan.chen
     * @param pValue int
     * @since MT 1.0
     * @hide
     */
    //@android.view.RemotableViewMethod
    public void setProgressColor(int pValue) {
	    Log.v(TAG, "------->>setProgressColor(" + pValue + ")");
        this.mProgressColor = pValue;
    }

    /**
     * setProgress: TODO<br/>
     * 
     * @author wenguan.chen
     * @param pValue int
     * @since MT 1.0
     * @hide
     */
    //@android.view.RemotableViewMethod
    public void setProgress(int pValue) {
    	Log.v(TAG, "------->>setProgress(" + pValue + ")");
        this.mProgress = pValue;
    }

    /**
     * setMax: TODO<br/>
     * 
     * @author wenguan.chen
     * @param pValue int
     * @since MT 1.0
     * @hide
     */
    //@android.view.RemotableViewMethod
    public void setMax(int pValue) {
	    Log.v(TAG, "------->>setMax(" + pValue + ")");
        this.mMax = pValue;
    }

    /**
     * setPlayingState: TODO<br/>
     * 
     * @author wenguan.chen
     * @param pIsPlaying boolean
     * @since MT 1.0
     * @hide
     */
    //@android.view.RemotableViewMethod
    public void setPlayingState(boolean pIsPlaying) {
        Log.v(TAG, "------->>setPlayingState(" + pIsPlaying + ")");
        mIsPlaying = pIsPlaying;
        refreshProgress();
    }

    /**
     * setScreenState: TODO<br/>
     * 
     * @author wenguan.chen
     * @param pIsScreenOn boolean
     * @since MT 1.0
     */
    private void setScreenState(boolean pIsScreenOn) {
        Log.v(TAG, "------->>setScreenState(" + pIsScreenOn + ")");
        mIsScreenOn = pIsScreenOn;
        refreshProgress();
    }

    /**
     * setLauncherState: TODO<br/>
     * 
     * @author wenguan.chen
     * @param pIsActive boolean
     * @since MT 1.0
     * @hide
     */
    public void setLauncherState(boolean pIsActive) {
        Log.v(TAG, "------->>setLauncherState(" + pIsActive + ")");
        mIsLauncherActive = pIsActive;
        refreshProgress();
    }

    /**
     * refreshProgress: TODO<br/>
     * 
     * @author wenguan.chen
     * @since MT 1.0
     */
    private void refreshProgress() {
        init();
        removeCallbacks(mRefreshRunnable);
        if (mIsPlaying && mIsScreenOn && mIsLauncherActive) {
            if (mProgress < mMax) {
                postDelayed(mRefreshRunnable, 1000);
            } else if (mProgress >= mMax) {
                mProgress = 0;
                mProgressRatio = 0;
                postDelayed(mRefreshRunnable, 1000);
            }
        }
    }

    private Runnable mRefreshRunnable = new Runnable() {

        @Override
        public void run() {
            mProgress++;
            if (mProgress <= mMax && mMax != 0) {
                mProgressRatio = mProgress * 100 / mMax;
                invalidate();
            }
            refreshProgress();
        }

    };

}
