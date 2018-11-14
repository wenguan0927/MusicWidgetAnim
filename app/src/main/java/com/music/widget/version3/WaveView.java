package com.music.widget.version3;

import java.util.ArrayList;
import java.util.List;

import android.animation.Animator;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.animation.Animator.AnimatorListener;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Bitmap;
import android.graphics.BitmapShader;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Shader;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.LinearInterpolator;
import android.widget.RemoteViews.RemoteView;

/**
 * ClassName: WaveView <br/>
 * Function: TODO ADD FUNCTION. <br/>
 * Date:2016-1-21
 * 
 * @author wenguan.chen
 * @version 0.1
 * @since MT 1.0
 */
@RemoteView
public class WaveView extends View {
    
    private final String TAG = "WaveView";
    /**
     * +------------------------+
     * |<--wave length->        |______
     * |   /\          |   /\   |  |
     * |  /  \         |  /  \  | amplitude
     * | /    \        | /    \ |  |
     * |/      \       |/      \|__|____
     * |        \      /        |  |
     * |         \    /         |  |
     * |          \  /          |  |
     * |           \/           | water level
     * |                        |  |
     * |                        |  |
     * +------------------------+__|____
     */
    private final float DEFAULT_AMPLITUDE_RATIO = 0.03f;
    private final float DEFAULT_WATER_LEVEL_RATIO = 0.5f;
    private final float DEFAULT_WAVE_LENGTH_RATIO = 1.0f;
    private final float DEFAULT_WAVE_SHIFT_RATIO = 0.0f;

    private final int DEFAULT_BEHIND_WAVE_COLOR_ALPHA = 127;
    private final int DEFAULT_FRONT_WAVE_COLOR_ALPHA = 70;

    public final int DEFAULT_BEHIND_WAVE_COLOR = Color.argb(
            DEFAULT_BEHIND_WAVE_COLOR_ALPHA, 255, 255, 255);
    public final int DEFAULT_FRONT_WAVE_COLOR = Color.argb(
            DEFAULT_FRONT_WAVE_COLOR_ALPHA, 255, 255, 255);

    // shader containing repeated waves
    private BitmapShader mWaveShader;
    // shader matrix
    private Matrix mShaderMatrix;
    // paint to draw wave
    private Paint mViewPaint;
    // paint to draw border
    private float mRadius;
    private int mViewWidth;
    private int mViewHeight;
    private float mCenterX;
    private float mCenterY;

    private float mDefaultAmplitude;
    private float mDefaultWaterLevel;
    private double mDefaultAngularFrequency;

    private float mAmplitudeRatio = DEFAULT_AMPLITUDE_RATIO;
    private float mWaveLengthRatio = DEFAULT_WAVE_LENGTH_RATIO;
    private float mWaterLevelRatio = 0f;
    private float mWaveShiftRatio = DEFAULT_WAVE_SHIFT_RATIO;

    private int mBehindWaveColor = DEFAULT_BEHIND_WAVE_COLOR;
    private int mFrontWaveColor = DEFAULT_FRONT_WAVE_COLOR;

    private int mColor = Color.BLACK;
    private boolean mIsPlaying = false;
    private boolean mIsShowWave = false;
    private List<Animator> mAnimators;
    private boolean mIsPlayingAnim = false;
    private AnimatorSet mAnimatorSet;
    private Bitmap mWaveAnimBmp = null;
    private ObjectAnimator mWaterLevelAnim;
    private ObjectAnimator mWaterShiftAnim;
    private final int TYPE_RISE = 1;
    private final int TYPE_FALL = 2;
    private int mType = TYPE_RISE;
    
    private boolean mIsScreenOn = true;
    private boolean mIsLauncherActive = true;

    private final String LAUNCHER_RESUME = "com.meitu.mobile.widget.WIDGET_START";
    private final String LAUNCHER_STOP = "com.meitu.mobile.widget.WIDGET_END";
    private Context mContext;
    
    public WaveView(Context context) {
        this(context, null);
    }

    public WaveView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public WaveView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        this.mContext = context;
        initData();
    }

    /**
     * init: TODO<br/>
     * 
     * @author wenguan.chen
     * @since MT 1.0
     */
    private void initData() {
        if (mShaderMatrix == null) {
            mShaderMatrix = new Matrix();
        }

        if (mViewPaint == null) {
            mViewPaint = new Paint();
            mViewPaint.setAntiAlias(true);
        }
        
        if(mAnimators == null){
            mAnimators = new ArrayList<Animator>();
            mWaterShiftAnim = ObjectAnimator.ofFloat(this,
                    "waveShiftRatio", 0f, 1f);
            mWaterShiftAnim.setRepeatCount(ValueAnimator.INFINITE);
            mWaterShiftAnim.setDuration(1500);
            mWaterShiftAnim.addListener(mShiftAnimListener);
            mWaterShiftAnim.setInterpolator(new LinearInterpolator());
            mAnimators.add(mWaterShiftAnim);

            mWaterLevelAnim = ObjectAnimator.ofFloat(this, "waterLevelRatio",
                    0f, 0.5f);
            mWaterLevelAnim.addListener(mLevelAnimListener);
            mWaterLevelAnim.setDuration(5000);
            mWaterLevelAnim.setInterpolator(new AccelerateInterpolator());
            mAnimators.add(mWaterLevelAnim);
        }
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
        refreshAnimState();
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
        refreshAnimState();
    }
    
    /**
     * refreshAnimState: TODO<br/>
     * 
     * @author wenguan.chen
     * @since MT 1.0
     */
    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    private void refreshAnimState() {
        if (mIsPlaying && mIsScreenOn && mIsLauncherActive) {
            if (mWaterShiftAnim.isPaused()) {
                mWaterShiftAnim.resume();
            } else if (!mIsPlayingAnim) {
                mWaterShiftAnim.start();
            }
        } else {
            if (mIsPlaying) {
                if (mIsPlayingAnim) {
                    mWaterShiftAnim.pause();
                }
            } else {
                if (mIsPlayingAnim) {
                    mWaterLevelAnim.cancel();
                }
            }
        }
    }
    
    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
        IntentFilter filter = new IntentFilter();
        filter.addAction(Intent.ACTION_SCREEN_OFF);
        filter.addAction(Intent.ACTION_SCREEN_ON);
        filter.addAction(LAUNCHER_RESUME);
        filter.addAction(LAUNCHER_STOP);
        mContext.registerReceiver(mReceiver, filter);
        initData();
        refreshAnimState();
    }

    @Override
    protected void onDetachedFromWindow() {
        mContext.unregisterReceiver(mReceiver);
        if (mIsPlayingAnim) {
            mWaterLevelAnim.cancel();
        }
        clearAnimation();
        if (mWaveAnimBmp != null && !mWaveAnimBmp.isRecycled()) {
            mWaveAnimBmp.recycle();
            mWaveAnimBmp = null;
        }
        super.onDetachedFromWindow();
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
            if (!mIsPlayingAnim) {
                mType = TYPE_RISE;
                playAnimatorSet(mAnimators);
            } else {
                if (mType == TYPE_FALL) {
                    mType = TYPE_RISE;
                    mWaterLevelAnim.reverse();
                }
            }
        } else {
            if (mIsPlayingAnim && mType == TYPE_RISE) {
                mType = TYPE_FALL;
                mWaterLevelAnim.reverse();
            }
        }
    }
    
    /**
     * playAnimatorSet: TODO<br/>
     * 
     * @author wenguan.chen
     * @param pAnimatorsEnd List<Animator> 
     * @since MT 1.0
     */
    private void playAnimatorSet(List<Animator> pAnimatorsEnd) {
        mAnimatorSet = new AnimatorSet();
        mAnimatorSet.playTogether(pAnimatorsEnd);
        mAnimatorSet.start();
    }
    
    private AnimatorListener mShiftAnimListener = new AnimatorListener() {

        @Override
        public void onAnimationStart(Animator arg0) {
            mIsShowWave = true;
            mIsPlayingAnim = true;
        }

        @Override
        public void onAnimationRepeat(Animator arg0) {
            
        }

        @Override
        public void onAnimationEnd(Animator arg0) {
            mIsPlayingAnim = false;
            invalidate();
        }

        @Override
        public void onAnimationCancel(Animator arg0) {
            mIsPlayingAnim = false;
            invalidate();
        }
    };
    
    private AnimatorListener mLevelAnimListener = new AnimatorListener() {

        @Override
        public void onAnimationStart(Animator arg0) {
        }

        @Override
        public void onAnimationRepeat(Animator arg0) {
            
        }

        @Override
        public void onAnimationEnd(Animator arg0) {
            if(mWaterLevelRatio == 0f){
                mIsShowWave = false;
                mWaterShiftAnim.end();
            }
            clearAnimation();
        }

        @Override
        public void onAnimationCancel(Animator arg0) {
            mWaterLevelRatio = 0f;
        }
    };
    
    /**
     * setColor: TODO<br/>
     * 
     * @author wenguan.chen
     * @param pColor int
     * @since MT 1.0
     * @hide
     */
    //@android.view.RemotableViewMethod
    public void setColor(int pColor) {
        if (mColor != pColor) {
            mColor = pColor;
            int red = (pColor & 0xff0000) >> 16;
            int green = (pColor & 0x00ff00) >> 8;
            int blue = (pColor & 0x0000ff);

            mBehindWaveColor = Color.argb(DEFAULT_BEHIND_WAVE_COLOR_ALPHA, red,
                    green, blue);
            mFrontWaveColor = Color.argb(DEFAULT_FRONT_WAVE_COLOR_ALPHA, red,
                    green, blue);

            // need to recreate shader when color changed
            mWaveShader = null;
            createShader();
            invalidate();
        }
    }
    
	/**
     * setBehindColor: TODO<br/>
     * 
     * @author wenguan.chen
     * @param pColor int
     * @since MT 1.0
     * @hide
     */
    //@android.view.RemotableViewMethod
    public void setBehindColor(int pColor) {
        if (mBehindWaveColor != pColor) {
            mBehindWaveColor = pColor;
            // need to recreate shader when color changed
            mWaveShader = null;
            createShader();
            invalidate();
        }
    }
	
    /**
     * setFrontColor: TODO<br/>
     * 
     * @author wenguan.chen
     * @param pColor int
     * @since MT 1.0
     * @hide
     */
    //@android.view.RemotableViewMethod
    public void setFrontColor(int pColor) {
        if (mFrontWaveColor != pColor) {
            mFrontWaveColor = pColor;
            // need to recreate shader when color changed
            mWaveShader = null;
            createShader();
            invalidate();
        }
    }
	
    /**
     * getWaveShiftRatio: TODO<br/>
     * 
     * @author wenguan.chen
     * @return float
     * @since MT 1.0
     * @hide
     */
    public float getWaveShiftRatio() {
        return mWaveShiftRatio;
    }

    /**
     * setWaveShiftRatio: Shift the wave horizontally according to
     * <code>waveShiftRatio</code>.<br/>
     * 
     * @author wenguan.chen
     * @param waveShiftRatio Should be 0 ~ 1. Default to be 0. <br/>
     *            Result of waveShiftRatio multiples width of WaveView is the
     *            length to shift.
     * @since MT 1.0
     * @hide
     */
    public void setWaveShiftRatio(float waveShiftRatio) {
        if (mWaveShiftRatio != waveShiftRatio) {
            mWaveShiftRatio = waveShiftRatio;
            invalidate();
        }
    }

    /**
     * getWaterLevelRatio: TODO<br/>
     * 
     * @author wenguan.chen
     * @return float
     * @since MT 1.0
     * @hide
     */
    public float getWaterLevelRatio() {
        return mWaterLevelRatio;
    }

    /**
     * setWaterLevelRatio: Set water level according to
     * <code>waterLevelRatio</code>.<br/>
     * 
     * @author wenguan.chen
     * @param waterLevelRatio Should be 0 ~ 1. Default to be 0.5. <br/>
     *            Ratio of water level to WaveView height.
     * @since MT 1.0
     * @hide
     */
    public void setWaterLevelRatio(float waterLevelRatio) {
        if (mWaterLevelRatio != waterLevelRatio) {
            mWaterLevelRatio = waterLevelRatio;
            invalidate();
        }
    }

    /**
     * getAmplitudeRatio: TODO<br/>
     * 
     * @author wenguan.chen
     * @return float
     * @since MT 1.0
     * @hide
     */
    public float getAmplitudeRatio() {
        return mAmplitudeRatio;
    }

    /**
     * setAmplitudeRatio: Set vertical size of wave according to
     * <code>amplitudeRatio</code><br/>
     * 
     * @author wenguan.chen
     * @param amplitudeRatio Default to be 0.05. Result of amplitudeRatio +
     *            waterLevelRatio should be less than 1. <br/>
     *            Ratio of amplitude to height of WaveView.
     * @since MT 1.0
     * @hide
     */
    public void setAmplitudeRatio(float amplitudeRatio) {
        if (mAmplitudeRatio != amplitudeRatio) {
            mAmplitudeRatio = amplitudeRatio;
            invalidate();
        }
    }

    /**
     * getWaveLengthRatio: TODO<br/>
     * 
     * @author wenguan.chen
     * @return float
     * @since MT 1.0
     * @hide
     */
    public float getWaveLengthRatio() {
        return mWaveLengthRatio;
    }

    /**
     * setWaveLengthRatio: Set horizontal size of wave according to
     * <code>waveLengthRatio</code><br/>
     * 
     * @author wenguan.chen
     * @param waveLengthRatio Default to be 1. <br/>
     *            Ratio of wave length to width of WaveView.
     * @since MT 1.0
     * @hide
     */
    public void setWaveLengthRatio(float waveLengthRatio) {
        mWaveLengthRatio = waveLengthRatio;
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        mViewWidth = getWidth();
        mViewHeight = getHeight();

        mCenterX = mViewWidth / 2f;
        mCenterY = mViewHeight / 2f;
        
        mRadius = (mViewWidth> mViewHeight? mViewWidth:mViewHeight) / 2f;
        mDefaultAngularFrequency = 2.0f * Math.PI / DEFAULT_WAVE_LENGTH_RATIO / mViewWidth;
        mDefaultAmplitude = mViewHeight * DEFAULT_AMPLITUDE_RATIO;
        mDefaultWaterLevel = mViewHeight * DEFAULT_WATER_LEVEL_RATIO;
        
        createShader();
    }

    /**
     * createShader: TODO<br/>
     * 
     * @author wenguan.chen
     * @since MT 1.0
     */
    private void createShader() {
        if (mViewHeight <= 0 || mViewWidth <= 0) {
            return;
        }
        if(mWaveAnimBmp == null || mWaveAnimBmp.isRecycled()){
            mWaveAnimBmp = Bitmap.createBitmap(mViewWidth, mViewHeight,
                    Bitmap.Config.ARGB_8888);
        }
        mWaveAnimBmp.eraseColor(Color.TRANSPARENT);
        Canvas canvas = new Canvas(mWaveAnimBmp);

        mViewPaint.reset();
        mViewPaint.setStrokeWidth(2);
        mViewPaint.setAntiAlias(true);

        // Draw default waves into the bitmap
        // y=Asin(ax+b)+c
        final int endX = mViewWidth + 1;
        final int endY = mViewHeight + 1;

        float[] waveY = new float[endX];

        mViewPaint.setColor(mBehindWaveColor);
        for (int beginX = 0; beginX < endX; beginX++) {
            double wx = beginX * mDefaultAngularFrequency;
            float beginY = (float) (mDefaultWaterLevel + mDefaultAmplitude * Math.sin(wx));
            canvas.drawLine(beginX, beginY, beginX, endY, mViewPaint);

            waveY[beginX] = beginY;
        }

        mViewPaint.setColor(mFrontWaveColor);
        final int wave2Shift = mViewWidth / 4;
        for (int beginX = 0; beginX < endX; beginX++) {
            canvas.drawLine(beginX, waveY[(beginX + wave2Shift) % endX], beginX, endY, mViewPaint);
        }

        mWaveShader = new BitmapShader(mWaveAnimBmp, Shader.TileMode.REPEAT, Shader.TileMode.CLAMP);
        mViewPaint.setShader(mWaveShader);
        
        mViewPaint.reset();
        mViewPaint.setAntiAlias(true);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        // modify paint shader according to mShowWave state
        if (mIsShowWave && mWaveShader != null) {
            // first call after mShowWave, assign it to our paint
            if (mViewPaint.getShader() == null) {
                mViewPaint.setShader(mWaveShader);
            }

            // sacle shader according to mWaveLengthRatio and mAmplitudeRatio
            // this decides the size(mWaveLengthRatio for width, mAmplitudeRatio for height) of waves
            mShaderMatrix.setScale(
                    mWaveLengthRatio / DEFAULT_WAVE_LENGTH_RATIO,
                    mAmplitudeRatio / DEFAULT_AMPLITUDE_RATIO, 0,
                    mDefaultWaterLevel);
            // translate shader according to mWaveShiftRatio and mWaterLevelRatio
            // this decides the start position(mWaveShiftRatio for x, mWaterLevelRatio for y) of waves
            mShaderMatrix.postTranslate(mWaveShiftRatio * mViewWidth,
                    (DEFAULT_WATER_LEVEL_RATIO - mWaterLevelRatio)
                            * mViewHeight);

            // assign matrix to invalidate the shader
            mWaveShader.setLocalMatrix(mShaderMatrix);

            canvas.drawCircle(mCenterX, mCenterY, mRadius, mViewPaint);
        } else {
            mViewPaint.setShader(null);
        }
    }
}
