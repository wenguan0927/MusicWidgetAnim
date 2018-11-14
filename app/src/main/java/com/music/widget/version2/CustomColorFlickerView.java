
package com.music.widget.version2;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.LinearGradient;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Shader.TileMode;
import android.os.Handler;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.view.View.MeasureSpec;
import android.view.animation.AlphaAnimation;
//import android.view.RemotableViewMethod;
import android.view.animation.Animation;
import android.view.animation.Animation.AnimationListener;
import android.widget.RemoteViews.RemoteView;

/**
 * ClassName: CustomColorFlickerView <br/>
 * Function: TODO ADD FUNCTION. <br/>
 * Date:2014-8-5
 * 
 * @author wenguan.chen
 * @version 0.1
 * @since MT 1.0
 */
@RemoteView
public class CustomColorFlickerView extends View implements AnimationListener {
    private static final String TAG = "CustomColorFlickerView";
    private boolean mIsPlaying = false;
    private boolean mIsScreenOn = true;
    private boolean mIsLauncherActive = true;

    public static final int COLOR_TYPE_YELLOW = 1;
    public static final int COLOR_TYPE_GREEN = 0;
    private static final String LAUNCHER_RESUME = "com.meitu.mobile.widget.WIDGET_START";
    private static final String LAUNCHER_STOP = "com.meitu.mobile.widget.WIDGET_END";

    private Context mContext;

    private AlphaAnimation mAlphaAnim;

    private int mAlpha = 70;

    private int mColor = Color.rgb(255, 204, 0);

    public static int COLOR_YELLOW = Color.rgb(255, 204, 0);

    public static int COLOR_GREEN = Color.rgb(0, 255, 186);

    private Path mNormalMaskPath;

    private Path mDefaultAlbumMaskPath;

    private LinearGradient mNormalColorGradient;

    private LinearGradient mDefaultAlbumColorGradient;

    private Paint mPaint;

    private Handler mHandler = new Handler();

    private float Xoffset;

    private long mAudioId = -1;

    private boolean mIsSwitch = false;

    private boolean mSwitching = false;

    private boolean mIsDefaultAlbum = true;

    public CustomColorFlickerView(Context context, AttributeSet attrs,
            int defStyle) {
        super(context, attrs, defStyle);
        mContext = context;
        initData();
    }

    public CustomColorFlickerView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public CustomColorFlickerView(Context context) {
        this(context, null);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        int widthSize = MeasureSpec.getSize(widthMeasureSpec);
        initPath(widthSize);
    }

    /**
     * initData: TODO<br/>
     * 
     * @author wenguan.chen
     * @since MT 1.0
     */
    private void initData() {
        if (mPaint == null) {
            mPaint = new Paint();
            mPaint.setAntiAlias(true);
            mPaint.setDither(true);
            mPaint.setColor(Color.WHITE);
        }

        if (mAlphaAnim == null) {
            mAlphaAnim = new AlphaAnimation(1.0f, 0.7f);
            mAlphaAnim.setDuration(1500);
            mAlphaAnim.setAnimationListener(this);
        }
    }

    /**
     * initPath: TODO<br/>
     * 
     * @author wenguan.chen
     * @param pViewWidth int
     * @since MT 1.0
     */
    private void initPath(int pViewWidth) {
        Xoffset = (float)(360 - pViewWidth / 2);
        if (mNormalMaskPath == null) {
            mNormalMaskPath = new Path();
            mNormalMaskPath.moveTo(172f - Xoffset, 194.85f);
            mNormalMaskPath.lineTo(209.5f - Xoffset, 259.8f);
            mNormalMaskPath.lineTo(134.5f - Xoffset, 389.7f);
            mNormalMaskPath.lineTo(285f - Xoffset, 389.7f);
            mNormalMaskPath.lineTo(360f - Xoffset, 519.6f);
            mNormalMaskPath.lineTo(435f - Xoffset, 389.7f);
            mNormalMaskPath.lineTo(585f - Xoffset, 389.7f);
            mNormalMaskPath.lineTo(510f - Xoffset, 259.8f);
            mNormalMaskPath.lineTo(547f - Xoffset, 194.85f);
            mNormalMaskPath.close();
        }

        if (mDefaultAlbumMaskPath == null) {
            mDefaultAlbumMaskPath = new Path();
            mDefaultAlbumMaskPath.moveTo(360f - Xoffset, 0);
            mDefaultAlbumMaskPath.lineTo(134.5f - Xoffset, 389.7f);
            mDefaultAlbumMaskPath.lineTo(285f - Xoffset, 389.7f);
            mDefaultAlbumMaskPath.lineTo(360f - Xoffset, 519.6f);
            mDefaultAlbumMaskPath.lineTo(435f - Xoffset, 389.7f);
            mDefaultAlbumMaskPath.lineTo(585f - Xoffset, 389.7f);
            mDefaultAlbumMaskPath.close();
        }

        if (mNormalColorGradient == null) {
            mNormalColorGradient = new LinearGradient(360f - Xoffset, 519.6f,
                    360f - Xoffset, 194.85f, mColor, Color.TRANSPARENT,
                    TileMode.MIRROR);
        }

        if (mDefaultAlbumColorGradient == null) {
            mDefaultAlbumColorGradient = new LinearGradient(360f - Xoffset,
                    519.6f, 360f - Xoffset, 0f, Color.WHITE, Color.TRANSPARENT,
                    TileMode.MIRROR);
        }
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        drawColorArea(canvas);
    }

    /**
     * drawColorArea: TODO<br/>
     * 
     * @author wenguan.chen
     * @param pCanvas Canvas
     * @since MT 1.0
     */
    private void drawColorArea(Canvas pCanvas) {
        if (mIsDefaultAlbum) {
            if (mIsPlaying && !mSwitching) {
                mPaint.setShader(mDefaultAlbumColorGradient);
                pCanvas.drawPath(mDefaultAlbumMaskPath, mPaint);
            }
        } else {
            if (mIsPlaying && !mSwitching) {
                mPaint.setShader(mNormalColorGradient);
                pCanvas.drawPath(mNormalMaskPath, mPaint);
            }
        }
    }

    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
        initData();
        IntentFilter filter = new IntentFilter();
        filter.addAction(Intent.ACTION_SCREEN_OFF);
        filter.addAction(Intent.ACTION_SCREEN_ON);
        filter.addAction(LAUNCHER_RESUME);
        filter.addAction(LAUNCHER_STOP);
        mContext.registerReceiver(mReceiver, filter);
        refreshColorArea();
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        if (mAlphaAnim != null) {
            mAlphaAnim.cancel();
            mAlphaAnim = null;
        }
        mContext.unregisterReceiver(mReceiver);
    };

    /**
     * setColor: TODO<br/>
     * 
     * @author wenguan.chen
     * @param pColor int
     * @since MT 1.0
     * @hide
     */
    //@RemotableViewMethod
    public void setColor(int pColor) {
        if (pColor == COLOR_TYPE_YELLOW) {
            mColor = COLOR_YELLOW;
        } else if (pColor == COLOR_TYPE_GREEN) {
            mColor = COLOR_GREEN;
        }
        mNormalColorGradient = new LinearGradient(360f - Xoffset, 519.6f,
                360f - Xoffset, 194.85f, mColor, Color.TRANSPARENT,
                TileMode.MIRROR);
    }

    /**
     * setAlpha: TODO<br/>
     * 
     * @author wenguan.chen
     * @param pAlpha int
     * @since MT 1.0
     * @hide
     */
    //@RemotableViewMethod
    public void setAlpha(int pAlpha) {
        mAlpha = pAlpha;
    }

    /**
     * setPlayingState: TODO<br/>
     * 
     * @author wenguan.chen
     * @param pIsPlaying boolean
     * @since MT 1.0
     * @hide
     */
    //@RemotableViewMethod
    public void setPlayingState(boolean pIsPlaying) {
        Log.v(TAG, "------->>setPlayingState(" + pIsPlaying + ")");
        mIsPlaying = pIsPlaying;
        if (mIsSwitch) {
            mAlphaAnim.cancel();
            mSwitching = true;
            invalidate();
            mHandler.postDelayed(new Runnable() {

                @Override
                public void run() {
                    mSwitching = false;
                    invalidate();
                    refreshColorArea();
                }

            }, 1000);
        } else {
            invalidate();
            refreshColorArea();
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
    //@RemotableViewMethod
    public void setAudioId(long pAudioId) {
        Log.v(TAG, "------->>setAudioId(" + pAudioId + ")");
        if (mAudioId == pAudioId) {
            mIsSwitch = false;
        } else {
            mAudioId = pAudioId;
            mIsSwitch = true;
        }
        if (mAudioId == 0) {
            mAudioId = -1;
        }
    }

    /**
     * setIsDefaultAlbum: TODO<br/>
     * 
     * @author wenguan.chen
     * @param pIsDefault boolean
     * @since MT 1.0
     * @hide
     */
    //@RemotableViewMethod
    public void setIsDefaultAlbum(boolean pIsDefault) {
        mIsDefaultAlbum = pIsDefault;
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
        refreshColorArea();
    }

    /**
     * setLauncherState: TODO<br/>
     * 
     * @author wenguan.chen
     * @param pIsActive boolean
     * @since MT 1.0
     */
    private void setLauncherState(boolean pIsActive) {
        Log.v(TAG, "------->>setLauncherState(" + pIsActive + ")");
        mIsLauncherActive = pIsActive;
        refreshColorArea();
    }

    /**
     * refreshView: TODO<br/>
     * 
     * @author wenguan.chen
     * @since MT 1.0
     */
    private void refreshColorArea() {
        initData();
        if (mIsPlaying && mIsScreenOn && mIsLauncherActive) {
            startAnimation(mAlphaAnim);
        } else {
            mAlphaAnim.cancel();
        }
    }

    private BroadcastReceiver mReceiver = new BroadcastReceiver() {

        @Override
        public void onReceive(Context pContext, Intent pIntent) {
            String action = pIntent.getAction();
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

    @Override
    public void onAnimationEnd(Animation arg0) {
        refreshColorArea();
    }

    @Override
    public void onAnimationRepeat(Animation arg0) {}

    @Override
    public void onAnimationStart(Animation arg0) {}

}
