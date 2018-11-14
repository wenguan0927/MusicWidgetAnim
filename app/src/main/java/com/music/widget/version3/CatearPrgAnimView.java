package com.music.widget.version3;

import android.animation.Animator;
import android.animation.ObjectAnimator;
import android.animation.Animator.AnimatorListener;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.view.animation.AccelerateInterpolator;
import android.widget.RemoteViews.RemoteView;

/**
 * ClassName: CatearPrgAnimView <br/>
 * Function: TODO ADD FUNCTION. <br/>
 * Date:2016-1-27
 * 
 * @author wenguan.chen
 * @version 0.1
 * @since MT 1.0
 */
@RemoteView
public class CatearPrgAnimView extends View {

    private static final String TAG = "CatearPrgAnimView";

    private int mViewWidth;
    private int mViewHeight;
    private int mCurveWidth;
    private int mCurveHeight;
    private int mCurveSideLength;
    private Paint mPaint;
    private float mAnimRation;
    private int mProgressRatio;
    private final int mPaintStrokeWidth = 5;
    private int mCurrentColor = Color.WHITE;
    private int mNewColor = Color.rgb(65, 208, 120);
    private Path mBgPath;
    private Path mAnimPrgPath;
    private int mArcLeft;
    private int mArcTop;
    private int mArcRight;
    private int mArcBottom;
    private int mProgressLength;
    private int mArcRatio;
    private long mAudioId = -1;
    private ObjectAnimator mObjAnim;
    private boolean mIsPlayingAnim = false;

    public CatearPrgAnimView(Context context, AttributeSet attrs) {
        super(context, attrs, 0);
        init();
    }

    public CatearPrgAnimView(Context context) {
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
        
        if (mObjAnim == null) {
            mObjAnim = ObjectAnimator.ofFloat(this, "animRatio", 0f, 1f);
            mObjAnim.setDuration(700);
            mObjAnim.setInterpolator(new AccelerateInterpolator());
            mObjAnim.addListener(mAnimListener);
        }
    }
    
    private AnimatorListener mAnimListener = new AnimatorListener() {

        @Override
        public void onAnimationStart(Animator arg0) {
            mIsPlayingAnim = true;
        }

        @Override
        public void onAnimationRepeat(Animator arg0) {}

        @Override
        public void onAnimationEnd(Animator arg0) {
            onAnimfinish();
        }

        @Override
        public void onAnimationCancel(Animator arg0) {
            onAnimfinish();
        }
    };
    
    /**
     * onAnimfinish: TODO<br/>
     * 
     * @author wenguan.chen
     * @since MT 1.0
     */
    private void onAnimfinish() {
        mIsPlayingAnim = false;
        mCurrentColor = mNewColor;
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
        mBgPath.moveTo(mArcLeft + mPaintStrokeWidth / 2, mArcTop
                - mPaintStrokeWidth / 2);
        mBgPath.lineTo(mArcLeft + mPaintStrokeWidth / 2, mArcBottom);
        mBgPath.close();

        if (mAnimPrgPath == null) {
            mAnimPrgPath = new Path();
        }

        mProgressLength = (int)(Math.PI * mCurveSideLength / 4 + (mArcBottom
                - mArcTop + mPaintStrokeWidth / 2));
        mArcRatio = (int)((Math.PI * mCurveSideLength * 100 / 4) / mProgressLength);
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        // draw background line path
        mPaint.setColor(mCurrentColor);
        mPaint.setStyle(Paint.Style.STROKE);
        mPaint.setStrokeWidth(mPaintStrokeWidth);
        canvas.drawArc(mArcLeft - mCurveSideLength, mArcTop, mArcRight,
                mCurveSideLength * 2 + mArcTop, -45, -45, false, mPaint);

        canvas.drawPath(mBgPath, mPaint);

        // draw current progress
        mPaint.setColor(mNewColor);
        if (mProgressRatio <= mArcRatio) {
            canvas.drawArc(mArcLeft - mCurveSideLength, mArcTop, mArcRight,
                    mCurveSideLength * 2 + mArcTop, -45, -45 * mProgressRatio
                            / mArcRatio, false, mPaint);
        } else {
            canvas.drawArc(mArcLeft - mCurveSideLength, mArcTop, mArcRight,
                    mCurveSideLength * 2 + mArcTop, -45, -45, false, mPaint);

            mAnimPrgPath.reset();
            mAnimPrgPath.moveTo(mArcLeft + mPaintStrokeWidth / 2, mArcTop
                    - mPaintStrokeWidth / 2);
            mAnimPrgPath.lineTo(mArcLeft + mPaintStrokeWidth / 2, mArcTop
                    + (mArcBottom - mArcTop) * (mProgressRatio - mArcRatio)
                    / (100 - mArcRatio));
            mAnimPrgPath.close();

            canvas.drawPath(mAnimPrgPath, mPaint);
        }
    }

    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
        init();
    }

    @Override
    protected void onDetachedFromWindow() {
        if(mObjAnim.isStarted() || mObjAnim.isRunning()){
            mObjAnim.end();
        }
        clearAnimation();
        super.onDetachedFromWindow();
    }

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
        this.mNewColor = pValue;
    }

    /**
     * getAnimRatio: TODO<br/>
     * 
     * @author wenguan.chen
     * @return float
     * @since MT 1.0
     * @hide
     */
    public float getAnimRatio() {
        return mAnimRation;
    }

    /**
     * setAnimRatio: according to <code>animRatio</code>.<br/>
     * 
     * @author wenguan.chen
     * @param transRatio Should be 0 ~ 1. Default to be 0. <br/>
     * @since MT 1.0
     * @hide
     */
    public void setAnimRatio(float transRatio) {
        if (mAnimRation != transRatio) {
            mAnimRation = transRatio;
            mProgressRatio = (int)(mAnimRation * 100);
            invalidate();
        }
    }

    /**
     * setAudioId: TODO<br/>
     * 
     * @author wenguan.chen
     * @param pAudioId long
     * @since MT 1.0
     * @hide
     */
    //@android.view.RemotableViewMethod
    public void setAudioId(long pAudioId) {
        Log.v(TAG, "------->>setAudioId(" + pAudioId + ")");
        if (mAudioId != pAudioId) {
            mAudioId = pAudioId;
            if(mIsPlayingAnim){
                mObjAnim.end();
            }
            mProgressRatio = 0;
            mObjAnim.start();
        }
    }
}
