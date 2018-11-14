
package com.music.widget.version2;

import java.util.HashMap;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Canvas;
import android.os.Handler;
import android.util.AttributeSet;
import android.util.Log;
//import android.view.RemotableViewMethod;
import android.view.View;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.Animation.AnimationListener;
import android.widget.RemoteViews.RemoteView;

/**
 * ClassName: CustomPrismFlickerView <br/>
 * Function: TODO ADD FUNCTION. <br/>
 * Date:2014-7-18
 * 
 * @author wenguan.chen
 * @version 0.1
 * @since MT 1.0
 */
@RemoteView
class CustomPrismFlickerView extends View implements AnimationListener {

    private static final String TAG = "CustomPrismFlickerView";
    private boolean mIsPlaying = false;
    private boolean mIsScreenOn = true;
    private boolean mIsLauncherActive = true;

    private static final String LAUNCHER_RESUME = "com.meitu.mobile.widget.WIDGET_START";
    private static final String LAUNCHER_STOP = "com.meitu.mobile.widget.WIDGET_END";

    private int mNum = 5;

    private int[] mRandom = PrismFlickerTool.getInstance().getRandomArray(mNum);

    private Context mContext;

    private AlphaAnimation mAlphaAnim;

    private long mAudioId = -1;

    private boolean mIsSwitch = false;

    private Handler mHandler = new Handler();

    private boolean mSwitching = false;

    public CustomPrismFlickerView(Context pContext, AttributeSet pAttrs,
            int pDefStyle) {
        super(pContext, pAttrs, pDefStyle);
        mContext = pContext;
        initData();
    }

    public CustomPrismFlickerView(Context pContext, AttributeSet pAttrs) {
        this(pContext, pAttrs, 0);
    }

    public CustomPrismFlickerView(Context pContext) {
        this(pContext, null);
    }

    /**
     * initData: TODO<br/>
     * 
     * @author wenguan.chen
     * @since MT 1.0
     */
    private void initData() {
        if (mAlphaAnim == null) {
            mAlphaAnim = new AlphaAnimation(1, 0);
            mAlphaAnim.setDuration(1000 + (int)Math.random() * 1000);
            mAlphaAnim.setAnimationListener(this);
        }
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        int widthSize = MeasureSpec.getSize(widthMeasureSpec);
        int heightSize = MeasureSpec.getSize(heightMeasureSpec);
        PrismFlickerTool.getInstance().setViewWidth(widthSize);
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
        refreshTriangleView();
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

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        drawTriangle(canvas);
    }

    /**
     * drawTriangle: TODO<br/>
     * 
     * @author wenguan.chen
     * @param pCanvas Canvas
     * @since MT 1.0
     */
    private void drawTriangle(Canvas pCanvas) {
        if (mIsPlaying && !mSwitching) {
            HashMap<String, PrismFlickerTool.TriangleNode> mNodeMap = PrismFlickerTool
                    .getInstance().getTriangleNodes();
            for (int i = 0; i < mRandom.length; i++) {
                PrismFlickerTool.TriangleNode node = mNodeMap.get(String.valueOf(mRandom[i]));
                node.drawTriangle(pCanvas);
            }
        }
    }

    /**
     * seColor: TODO<br/>
     * 
     * @author wenguan.chen
     * @param pColor int
     * @since MT 1.0
     * @hide
     */
    //@RemotableViewMethod
    public void setColor(int pColor) {
        PrismFlickerTool.getInstance().setColor(pColor);
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
        PrismFlickerTool.getInstance().setAlpha(pAlpha);
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
                    refreshTriangleView();
                }
            }, 1000);
        } else {
            refreshTriangleView();
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
     * setShowNum: TODO<br/>
     * 
     * @author wenguan.chen
     * @param pNum int
     * @since MT 1.0
     * @hide
     */
    //@RemotableViewMethod
    public void setShowNum(int pNum) {
        Log.v(TAG, "------->>setShowNum(" + pNum + ")");
        mNum = pNum;
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
        refreshTriangleView();
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
        refreshTriangleView();
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
     * refreshView: TODO<br/>
     * 
     * @author wenguan.chen
     * @since MT 1.0
     */
    private void refreshTriangleView() {
        initData();
        if (mIsPlaying && mIsScreenOn && mIsLauncherActive) {
            mRandom = PrismFlickerTool.getInstance().getRandomArray(mNum);
            invalidate();
            mAlphaAnim.setDuration(1000 + (int)Math.random() * 1000);
            startAnimation(mAlphaAnim);
        } else {
            mAlphaAnim.cancel();
            invalidate();
        }
    }

    @Override
    public void onAnimationEnd(Animation arg0) {
        refreshTriangleView();
    }

    @Override
    public void onAnimationRepeat(Animation arg0) {}

    @Override
    public void onAnimationStart(Animation arg0) {}

}
