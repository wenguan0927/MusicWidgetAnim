package com.music.widget.version3;

import android.animation.Animator;
import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.animation.Animator.AnimatorListener;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.DrawFilter;
import android.graphics.Paint;
import android.graphics.PaintFlagsDrawFilter;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Xfermode;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.LinearInterpolator;
import android.widget.RemoteViews.RemoteView;

import com.music.widget.anim.R;

/**
 * ClassName: CircleAlbumSwitchView <br/>
 * Function: TODO ADD FUNCTION. <br/>
 * Date:2016-1-25
 * 
 * @author wenguan.chen
 * @version 0.1
 * Note：传入专辑图片的宽高不要超过布局的宽高
 */
@RemoteView
public class CircleAlbumSwitchView extends View {

    private final String TAG = "CircleAlbumSwitchView";
    private boolean mIsScreenOn = true;
    private boolean mIsLauncherActive = true;

    private final String LAUNCHER_RESUME = "com.meitu.mobile.widget.WIDGET_START";
    private final String LAUNCHER_STOP = "com.meitu.mobile.widget.WIDGET_END";

    private Xfermode mXfermode;

    private Paint mPaint;
    private DrawFilter mDrawFilter;
    private Bitmap mDefAlbumBmp;
    private Bitmap mCurrentBmp;
    private Bitmap mAlbumBmp;

    private final int DEFAULT_LENGTH = 750;
    private int mViewWidth = DEFAULT_LENGTH;
    private int mViewHeight = DEFAULT_LENGTH;
    private int mAlbumWidth = DEFAULT_LENGTH;
    private int mAlbumHeight = DEFAULT_LENGTH;
    private int mAlbumSideLength = DEFAULT_LENGTH;
    private float mRadius = DEFAULT_LENGTH / 2;
    private long mAudioId = -1;
    private boolean mShouldInvalidate;
    private float mHorizonTransRatio = -1f;
    private ObjectAnimator mHorizonTransObjAnim;
    private ObjectAnimator mRotateObjAnim;
    private Bitmap mAnimTailorBmp;
    private Bitmap mAnimTempAlbum;
    private boolean mIsPlaying = false;
    private Canvas mCanvas;
    private boolean mIsPlayingRotateAnim = false;
    private boolean mIsPlayingTransAnim = false;
    private Context mContext;

    public CircleAlbumSwitchView(Context context, AttributeSet attrs) {
        super(context, attrs, 0);
        mContext = context;
        init();
    }

    public CircleAlbumSwitchView(Context context) {
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

        if (mXfermode == null) {
            mXfermode = new PorterDuffXfermode(PorterDuff.Mode.SRC_IN);
        }

        if (mDrawFilter == null) {
            mDrawFilter = new PaintFlagsDrawFilter(0, Paint.ANTI_ALIAS_FLAG
                    | Paint.FILTER_BITMAP_FLAG);
        }

        if (mHorizonTransObjAnim == null) {
            mHorizonTransObjAnim = ObjectAnimator.ofFloat(this,
                    "horizonTransRatio", 0f, 1f);
            mHorizonTransObjAnim.setDuration(1100);
            mHorizonTransObjAnim.setInterpolator(new AccelerateInterpolator());
            mHorizonTransObjAnim.addListener(mTransAnimListener);
        }

        if (mRotateObjAnim == null) {
            mRotateObjAnim = ObjectAnimator.ofFloat(this, "rotation", 0f, 360f);
            mRotateObjAnim.setRepeatCount(ValueAnimator.INFINITE);
            mRotateObjAnim.setDuration(10000);
            mRotateObjAnim.setInterpolator(new LinearInterpolator());
            mRotateObjAnim.addListener(mRotateAnimListener);
        }

        if (mCanvas == null) {
            mCanvas = new Canvas();
        }
    }

    /**
     * initBitmap: TODO<br/>
     * 
     * @author wenguan.chen
     * @since MT 1.0
     */
    private void initBitmap() {
        if (mDefAlbumBmp == null || mDefAlbumBmp.isRecycled()) {
            mDefAlbumBmp = BitmapFactory.decodeResource(getResources(),
                    R.drawable.ic_appwidget_albumart_default);
            Bitmap resizeBmp = getAlbumTailorBmp(mDefAlbumBmp);
            if (mDefAlbumBmp != resizeBmp) {
                mDefAlbumBmp.recycle();
                mDefAlbumBmp = resizeBmp;
            }
        }

        if (mCurrentBmp == null || mCurrentBmp.isRecycled()) {
            mCurrentBmp = mDefAlbumBmp.copy(Config.ARGB_8888, true);
        }

        if (mAnimTailorBmp == null || mAnimTailorBmp.isRecycled()) {
            mAnimTailorBmp = Bitmap.createBitmap((int)mAlbumWidth,
                    mAlbumHeight, Config.ARGB_8888);
        }

        if (mAnimTempAlbum == null || mAnimTempAlbum.isRecycled()) {
            mAnimTempAlbum = Bitmap.createBitmap((int)mAlbumWidth,
                    mAlbumHeight, Config.ARGB_8888);
        }

    }

    /**
     * recycleBmp: TODO<br/>
     * 
     * @author wenguan.chen
     * @since MT 1.0
     */
    private void recycleBmp() {
        if (mDefAlbumBmp != null && !mDefAlbumBmp.isRecycled()) {
            mDefAlbumBmp.recycle();
            mDefAlbumBmp = null;
        }

        if (mAnimTailorBmp != null && !mAnimTailorBmp.isRecycled()) {
            mAnimTailorBmp.recycle();
            mAnimTailorBmp = null;
        }

        if (mAnimTempAlbum != null && !mAnimTempAlbum.isRecycled()) {
            mAnimTempAlbum.recycle();
            mAnimTempAlbum = null;
        }
    }

    /**
     * geHorizonTranstRatio: TODO<br/>
     * 
     * @author wenguan.chen
     * @return float
     * @since MT 1.0
     * @hide
     */
    public float getHorizonTransRatio() {
        return mHorizonTransRatio;
    }

    /**
     * setHorizonTransRatio: Shift the wave horizontally according to
     * <code>waveShiftRatio</code>.<br/>
     * 
     * @author wenguan.chen
     * @param transRatio Should be 0 ~ 1. Default to be 0. <br/>
     *            Result of waveShiftRatio multiples width of WaveView is the
     *            length to shift.
     * @since MT 1.0
     * @hide
     */
    public void setHorizonTransRatio(float transRatio) {
        if (mHorizonTransRatio != transRatio) {
            mHorizonTransRatio = transRatio;
            invalidate();
        }
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
        if (mIsPlaying) {
            if (!mIsPlayingRotateAnim && !mIsPlayingTransAnim) {
                mRotateObjAnim.start();
            }
        } else {
            if (mIsPlayingRotateAnim) {
                mRotateObjAnim.end();
            }
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
        if (mAudioId == pAudioId) {
            mShouldInvalidate = false;
        } else {
            mAudioId = pAudioId;
            mShouldInvalidate = true;
        }
    }

    /**
     * setAlbumArtBitmap: TODO<br/>
     * 
     * @author wenguan.chen
     * @param pBmp Bitmap
     * @since MT 1.0
     * @hide
     */
    //@android.view.RemotableViewMethod
    public void setAlbumArtBitmap(Bitmap pBmp) {
        Log.v(TAG, "------->>setAlbumArtBitmap(),mShouldRotate:"
                + mShouldInvalidate);
        if (!mShouldInvalidate) {
            return;
        }
        init();
        initBitmap();
        if (pBmp == null) {
            pBmp = mDefAlbumBmp;
        } else {
            Bitmap resizeBmp = getAlbumTailorBmp(pBmp);
            if (resizeBmp != pBmp) {
                pBmp.recycle();
                pBmp = resizeBmp;
            }
        }

        if (mHorizonTransObjAnim.isStarted()
                || mHorizonTransObjAnim.isRunning()) {
            mHorizonTransObjAnim.cancel();
        }

        if (mIsPlayingRotateAnim) {
            mRotateObjAnim.end();
        }

        if (mCurrentBmp == null || mCurrentBmp.isRecycled()) {
            mCurrentBmp = pBmp;
        }

        mAlbumBmp = pBmp;

        mHorizonTransObjAnim.start();
    }

    private AnimatorListener mTransAnimListener = new AnimatorListener() {

        @Override
        public void onAnimationStart(Animator arg0) {
            mIsPlayingTransAnim = true;
        }

        @Override
        public void onAnimationRepeat(Animator arg0) {}

        @Override
        public void onAnimationEnd(Animator arg0) {
            onAnimEnd();
        }

        @Override
        public void onAnimationCancel(Animator arg0) {
            onAnimEnd();
        }
    };

    private AnimatorListener mRotateAnimListener = new AnimatorListener() {

        @Override
        public void onAnimationStart(Animator arg0) {
            mIsPlayingRotateAnim = true;
        }

        @Override
        public void onAnimationRepeat(Animator arg0) {}

        @Override
        public void onAnimationEnd(Animator arg0) {
            mIsPlayingRotateAnim = false;
        }

        @Override
        public void onAnimationCancel(Animator arg0) {
            mIsPlayingRotateAnim = false;
        }
    };

    /**
     * onAnimEnd: TODO<br/>
     * 
     * @author wenguan.chen
     * @since MT 1.0
     */
    private void onAnimEnd() {
        mIsPlayingTransAnim = false;
        mHorizonTransRatio = -1;
        clearAnimation();
        if (mCurrentBmp != mDefAlbumBmp && mCurrentBmp != mAlbumBmp) {
            mCurrentBmp.recycle();
        }
        mCurrentBmp = mAlbumBmp;
        invalidate();
        if (mIsPlaying && !mIsPlayingRotateAnim) {
            mRotateObjAnim.start();
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
        init();
		if (mIsPlaying) {
            if (!mIsPlayingRotateAnim && !mIsPlayingTransAnim) {
                mRotateObjAnim.start();
            }
        } 
    }

    @Override
    protected void onDetachedFromWindow() {
        mContext.unregisterReceiver(mReceiver);
        if (mIsPlayingTransAnim) {
            mHorizonTransObjAnim.end();
        }
        if (mIsPlayingRotateAnim) {
            mRotateObjAnim.end();
        }
        recycleBmp();
        clearAnimation();
        super.onDetachedFromWindow();
    }

    /**
     * getDefaultAlbumTailorBmp: TODO<br/>
     * 
     * @author wenguan.chen
     * @param pBmp Bitmap
     * @return Bitmap
     * @since MT 1.0
     */
    private Bitmap getAlbumTailorBmp(Bitmap pBmp) {
        Bitmap resizeBmp = Bitmap.createScaledBitmap(pBmp, mAlbumSideLength,
                mAlbumSideLength, true);
        Bitmap canvasBmp = Bitmap.createBitmap(mAlbumSideLength,
                mAlbumSideLength, Config.ARGB_8888);
        canvasBmp.eraseColor(Color.TRANSPARENT);
        mCanvas.setBitmap(canvasBmp);
        mCanvas.save();
        mCanvas.drawCircle(mRadius, mRadius, mRadius, mPaint);// draw path area
        mPaint.setXfermode(mXfermode);
        mCanvas.drawBitmap(resizeBmp, 0, 0, mPaint);
        mPaint.setXfermode(null);
        mCanvas.restore();
        resizeBmp.recycle();
        return canvasBmp;
    }

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        mViewWidth = getWidth();
        mViewHeight = getHeight();
        mAlbumWidth = mViewWidth - getPaddingLeft() - getPaddingRight();
        mAlbumHeight = mViewHeight - getPaddingTop() - getPaddingBottom();
        mAlbumSideLength = mAlbumWidth < mAlbumHeight ? mAlbumWidth
                : mAlbumHeight;
        mRadius = mAlbumSideLength / 2f;

        initBitmap();
        if (mAudioId == -1) {
            setAudioId(-2);// init View
            setAlbumArtBitmap(null);
        }
        refreshRotateAnim();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        canvas.setDrawFilter(mDrawFilter); // eliminate the aliasing effect
        if (mHorizonTransRatio > 0 && mHorizonTransRatio <= 1.0f) {// draw switch anim
            Bitmap tailorBmp = getAnimTailorBmp(mHorizonTransRatio);
            if (tailorBmp != null && !tailorBmp.isRecycled()) {
                canvas.drawBitmap(tailorBmp, mViewWidth / 2f - mRadius,
                        mViewHeight / 2f - mRadius, null);
            }
        } else {// draw mCurrentBmp
            if (mCurrentBmp != null && !mCurrentBmp.isRecycled()) {
                canvas.drawBitmap(mCurrentBmp, mViewWidth / 2f - mRadius,
                        mViewHeight / 2f - mRadius, null);
            }
        }
    }

    /**
     * getAnimTailorBmp: unnecessary create any new object during album bitmap
     * tailor<br/>
     * 
     * @author wenguan.chen
     * @param pRatio float
     * @return Bitmap
     * @since MT 1.0
     */
    private Bitmap getAnimTailorBmp(float pRatio) {
        if (mAnimTempAlbum != null && !mAnimTempAlbum.isRecycled()
                && mCurrentBmp != null && !mCurrentBmp.isRecycled()
                && mAlbumBmp != null && !mAlbumBmp.isRecycled()
                && mAnimTailorBmp != null && !mAnimTailorBmp.isRecycled()) {
            // draw current rotate album
            mAnimTempAlbum.eraseColor(Color.TRANSPARENT);
            mCanvas.setBitmap(mAnimTempAlbum);
            mCanvas.save();
            mCanvas.translate(mAlbumSideLength * pRatio / 13, -mAlbumSideLength
                * pRatio / 13);
            mCanvas.rotate(360f * pRatio, mRadius, mRadius);
            mCanvas.drawBitmap(mCurrentBmp, 0, 0, null);
            mCanvas.restore();
            // draw new rotate album
            mCanvas.save();
            mCanvas.translate(-mAlbumSideLength * (1f - pRatio), 0);
            mCanvas.rotate(-360f * (1f - pRatio), mRadius, mRadius);
            mCanvas.drawBitmap(mAlbumBmp, 0, 0, null);
            mCanvas.restore();

            // tailor bitmap in center circle area
            mAnimTailorBmp.eraseColor(Color.TRANSPARENT);
            mCanvas.setBitmap(mAnimTailorBmp);
            mCanvas.save();
            mCanvas.drawCircle(mRadius, mRadius, mRadius, mPaint);
            mPaint.setXfermode(mXfermode);
            mCanvas.drawBitmap(mAnimTempAlbum, 0, 0, mPaint);
            mPaint.setXfermode(null);
            mCanvas.restore();
            return mAnimTailorBmp;
        }
        return null;
    }

    private BroadcastReceiver mReceiver = new BroadcastReceiver() {
        @RequiresApi(api = Build.VERSION_CODES.KITKAT)
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
     * setScreenState: TODO<br/>
     * 
     * @author wenguan.chen
     * @param pIsScreenOn boolean
     * @since MT 1.0
     */
    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    private void setScreenState(boolean pIsScreenOn) {
        Log.v(TAG, "------->>setScreenState(" + pIsScreenOn + ")");
        mIsScreenOn = pIsScreenOn;
        refreshRotateAnim();
    }

    /**
     * setLauncherState: TODO<br/>
     * 
     * @author wenguan.chen
     * @param pIsActive boolean
     * @since MT 1.0
     * @hide
     */
    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    public void setLauncherState(boolean pIsActive) {
        Log.v(TAG, "------->>setLauncherState(" + pIsActive + ")");
        mIsLauncherActive = pIsActive;
        refreshRotateAnim();
    }

    /**
     * refreshRotateAnim: TODO<br/>
     * 
     * @author wenguan.chen
     * @since MT 1.0
     */
    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    private void refreshRotateAnim() {
        if (mIsPlaying && mIsScreenOn && mIsLauncherActive) {
            if (mRotateObjAnim.isPaused()) {
                mRotateObjAnim.resume();
            } else if (!mIsPlayingRotateAnim) {
                mRotateObjAnim.start();
            }
        } else {
            if (mIsPlaying) {
                if (mIsPlayingRotateAnim) {
                    mRotateObjAnim.pause();
                }
            } else {
                mRotateObjAnim.end();
            }
        }
    }

}
