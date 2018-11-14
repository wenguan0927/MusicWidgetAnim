
package com.music.widget.version2;

import java.util.ArrayList;
import java.util.List;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.LinearGradient;
import android.graphics.Paint;
import android.graphics.Shader.TileMode;
import android.os.Handler;
import android.os.Message;
import android.util.AttributeSet;
import android.util.Log;
//import android.view.RemotableViewMethod;
import android.view.View;
import android.widget.RemoteViews.RemoteView;

/**
 * ClassName: CustomShootingStarView <br/>
 * Function: TODO ADD FUNCTION. <br/>
 * Date:2014-8-2
 * 
 * @author wenguan.chen
 * @version 0.1
 * @since MT 1.0
 */
@RemoteView
public class CustomShootingStarView extends View {
    private static final String TAG = "CustomShootingStarView";
    private boolean mIsPlaying = false;
    private boolean mIsScreenOn = true;
    private boolean mIsLauncherActive = true;

    private static final String LAUNCHER_RESUME = "com.meitu.mobile.widget.WIDGET_START";
    private static final String LAUNCHER_STOP = "com.meitu.mobile.widget.WIDGET_END";

    private Context mContext;

    private Paint mPaint;

    private Paint mAnimPaint;

    private int mLineColor = Color.argb(102, 255, 255, 255);

    private int mShootStarColor = Color.WHITE;

    private List<StarLine> mStarLineList = new ArrayList<StarLine>();

    private final float mStarLine01StartX = 97f;
    private final float mStarLine01EndX = 360f;

    private final float mStarLine02StartX = 621.8f;
    private final float mStarLine02EndX = 360f;

    private final float mStarLine03StartX = 360f;
    private final float mStarLine03EndX = 97f;

    private final float mStarLine04StartX = 59.5f;
    private final float mStarLine04EndX = 585f;

    private final float mStarLine05StartX = 660f;
    private final float mStarLine05EndX = 134.5f;

    private final float mHorizontalLineStart = 100f;
    private final float mHorizontalLineEnd = 610f;
    private final float mFadeOutStartOffset = 100;
    private final float mFadeOutEndOffset = 100;

    private static final int ANIM_LINE_ONE = 20140804;

    private static final int ANIM_LINE_TWO = 20140805;

    private static final int ANIM_LINE_THREE = 20140806;

    private static final int ANIM_LINE_FOUR = 20140807;

    private static final int ANIM_LINE_FIVE = 20140808;

    private static final int REFRESH_ANIM = 2014861653;

    private int mCurrentLine = ANIM_LINE_ONE;

    private float mOffset = 0;

    private StarLine mAnimStarLineDraw;

    private float mXOffset = 0;

    private float mTranslateX = 0;

    private float mTranslateY = 0;

    private StarLine mAnimLineOne;

    private StarLine mAnimLineTwo;

    private StarLine mAnimLineThree;

    private StarLine mAnimLineFour;

    private StarLine mAnimLineFive;

    private long mAudioId = -1;

    private boolean mIsSwitch = false;

    private boolean mSwitching = false;

    public CustomShootingStarView(Context context, AttributeSet attrs,
            int defStyle) {
        super(context, attrs, defStyle);
        mContext = context;
        initData();
    }

    public CustomShootingStarView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public CustomShootingStarView(Context context) {
        this(context, null);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        int widthSize = MeasureSpec.getSize(widthMeasureSpec);
        mXOffset = (float)(360 - widthSize / 2);
        initStarLine();
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
            mPaint.setColor(Color.rgb(248, 248, 255));
            mPaint.setStrokeWidth(3);
            mPaint.setDither(true);
            mPaint.setAntiAlias(true);
        }

        if (mAnimPaint == null) {
            mAnimPaint = new Paint();
            mAnimPaint.setColor(Color.rgb(248, 248, 255));
            mAnimPaint.setDither(true);
            mAnimPaint.setAntiAlias(true);
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
        refreshView(false);
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        myHandler.removeCallbacksAndMessages(null);
        mContext.unregisterReceiver(mReceiver);
    };

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        initLineView(canvas);

        if (mAnimStarLineDraw != null && !mSwitching) {
            canvas.translate(mTranslateX + mXOffset, mTranslateY);
            mAnimPaint.setShader(mAnimStarLineDraw.getLineGradient());
            canvas.drawLine(
                    mAnimStarLineDraw.getStarLineOne().getXCoordinate(),
                    mAnimStarLineDraw.getStarLineOne().getYCoordinate(),
                    mAnimStarLineDraw.getStarLineTwo().getXCoordinate(),
                    mAnimStarLineDraw.getStarLineTwo().getYCoordinate(),
                    mAnimPaint);
            canvas.translate(-(mTranslateX + mXOffset), -mTranslateY);
        }
    }

    private BroadcastReceiver mReceiver = new BroadcastReceiver() {

        @Override
        public void onReceive(Context pContext, Intent intent) {
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
        if (!mSwitching) {
            refreshView(mIsSwitch);
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
     * setScreenState: TODO<br/>
     * 
     * @author wenguan.chen
     * @param pIsScreenOn boolean
     * @since MT 1.0
     */
    private void setScreenState(boolean pIsScreenOn) {
        Log.v(TAG, "------->>setScreenState(" + pIsScreenOn + ")");
        mIsScreenOn = pIsScreenOn;
        refreshView(false);
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
        refreshView(false);
    }

    /**
     * refreshView: TODO<br/>
     * 
     * @author wenguan.chen
     * @param pIsSwitch boolean
     * @since MT 1.0
     */
    private void refreshView(boolean pIsSwitch) {
        myHandler.removeMessages(REFRESH_ANIM);
        if (mIsPlaying && mIsScreenOn && mIsLauncherActive) {
            if (pIsSwitch) {
                mSwitching = true;
                mAnimStarLineDraw = null;
                invalidate();
                mOffset = 0;
                mTranslateX = 0;
                mTranslateY = 0;
                mCurrentLine = ANIM_LINE_ONE;
                myHandler.sendEmptyMessageDelayed(REFRESH_ANIM, 1000);
            } else {
                swtichLine(mCurrentLine);
            }
        }
        if (!mIsPlaying) {
            mAnimStarLineDraw = null;
            invalidate();
        }
    }

    /**
     * swtichLine: TODO<br/>
     * 
     * @author wenguan.chen
     * @param pCase int
     * @since MT 1.0
     */
    private void swtichLine(int pCase) {
        switch (pCase) {
        case ANIM_LINE_ONE:
            mOffset += 10;
            mAnimStarLineDraw = getAnimLineOne();
            translateLineOne(mOffset);
            if (mStarLine01StartX + mOffset + 75f >= mStarLine01EndX) {
                mAnimStarLineDraw = null;
                invalidate();
                mOffset = 0;
                mTranslateX = 0;
                mTranslateY = 0;
                mCurrentLine = ANIM_LINE_TWO;
                myHandler.sendEmptyMessageDelayed(REFRESH_ANIM, 1000);
            } else {
                invalidate();
                myHandler.sendEmptyMessageDelayed(REFRESH_ANIM, 60);
            }
            break;
        case ANIM_LINE_TWO:
            mOffset += 10;
            mAnimStarLineDraw = getAnimLineTwo();
            translateLineTwo(mOffset);
            if (mStarLine02StartX - 75f - mOffset <= mStarLine02EndX) {
                mAnimStarLineDraw = null;
                invalidate();
                mOffset = 0;
                mTranslateX = 0;
                mTranslateY = 0;
                mCurrentLine = ANIM_LINE_THREE;
                myHandler.sendEmptyMessageDelayed(REFRESH_ANIM, 1000);
            } else {
                invalidate();
                myHandler.sendEmptyMessageDelayed(REFRESH_ANIM, 60);
            }
            break;
        case ANIM_LINE_THREE:
            mOffset += 10;
            mAnimStarLineDraw = getAnimLineThree();
            translateLineThree(mOffset);
            if (mStarLine03StartX - 75f - mOffset <= mStarLine03EndX) {
                mAnimStarLineDraw = null;
                invalidate();
                mOffset = 0;
                mTranslateX = 0;
                mTranslateY = 0;
                mCurrentLine = ANIM_LINE_FOUR;
                myHandler.sendEmptyMessageDelayed(REFRESH_ANIM, 1000);
            } else {
                invalidate();
                myHandler.sendEmptyMessageDelayed(REFRESH_ANIM, 60);
            }
            break;
        case ANIM_LINE_FOUR:
            mOffset += 20;
            mAnimStarLineDraw = getAnimLineFour();
            translateLineFour(mOffset);
            if (mStarLine04StartX + 150f + mOffset >= mStarLine04EndX) {
                mAnimStarLineDraw = null;
                invalidate();
                mOffset = 0;
                mTranslateX = 0;
                mTranslateY = 0;
                mCurrentLine = ANIM_LINE_FIVE;
                myHandler.sendEmptyMessageDelayed(REFRESH_ANIM, 1000);
            } else {
                invalidate();
                myHandler.sendEmptyMessageDelayed(REFRESH_ANIM, 60);
            }
            break;
        case ANIM_LINE_FIVE:
            mOffset += 20;
            mAnimStarLineDraw = getAnimLineFive();
            translateLineFive(mOffset);
            if (mStarLine05StartX - 150f - mOffset <= mStarLine05EndX) {
                mAnimStarLineDraw = null;
                invalidate();
                mOffset = 0;
                mTranslateX = 0;
                mTranslateY = 0;
                mCurrentLine = ANIM_LINE_ONE;
                myHandler.sendEmptyMessageDelayed(REFRESH_ANIM, 1000);
            } else {
                invalidate();
                myHandler.sendEmptyMessageDelayed(REFRESH_ANIM, 60);
            }
            break;
        default:
            break;
        }
    }

    Handler myHandler = new Handler() {
        public void handleMessage(Message msg) {
            switch (msg.what) {
            case REFRESH_ANIM:
                mSwitching = false;
                refreshView(false);
                break;
            default:
                break;
            }
        }
    };

    /**
     * initLineBmp: TODO<br/>
     * 
     * @author wenguan.chen
     * @param pCanvas Canvas
     * @since MT 1.0
     */
    private void initLineView(Canvas pCanvas) {
        if (mStarLineList.size() == 0) {
            return;
        }
        StarLine starLine = null;
        for (int i = 0; i < mStarLineList.size(); i++) {
            starLine = mStarLineList.get(i);
            mPaint.setShader(starLine.getLineGradient());
            pCanvas.drawLine(starLine.getStarLineOne().getXCoordinate(),
                    starLine.getStarLineOne().getYCoordinate(), starLine
                            .getStarLineTwo().getXCoordinate(), starLine
                            .getStarLineTwo().getYCoordinate(), mPaint);
        }
    }

    /**
     * initStarLine: TODO<br/>
     * 
     * @author wenguan.chen
     * @since MT 1.0
     */
    private void initStarLine() {
        mStarLineList.clear();
        LinePoint point01 = null;
        LinePoint point02 = null;
        StarLine starLine = null;
        LinearGradient gradient = null;
        // line left01
        point01 = new LinePoint(59.401f - mXOffset, 129.9f);
        point02 = new LinePoint(322.6f - mXOffset, 584.55f);
        gradient = new LinearGradient(point01.getXCoordinate(),
                point01.getYCoordinate(), point02.getXCoordinate(),
                point02.getYCoordinate(), new int[] {
                        Color.TRANSPARENT, mLineColor, Color.TRANSPARENT
                }, new float[] {
                        0, 0.5f, 1.0f
                }, TileMode.MIRROR);
        starLine = new StarLine(point01, point02, gradient);
        mStarLineList.add(starLine);

        // line left02
        point01 = new LinePoint(134.4f - mXOffset, 129.9f);
        point02 = new LinePoint(360f - mXOffset, 519.6f);
        gradient = new LinearGradient(96.8f - mXOffset, 64.95f,
                point02.getXCoordinate(), point02.getYCoordinate(), new int[] {
                        Color.TRANSPARENT, mLineColor, Color.TRANSPARENT
                }, new float[] {
                        0, 0.5f, 1.0f
                }, TileMode.MIRROR);
        starLine = new StarLine(point01, point02, gradient);
        mStarLineList.add(starLine);

        // line left03
        point01 = new LinePoint(171.8f - mXOffset, 64.95f);
        point02 = new LinePoint(435f - mXOffset, 519.6f);
        gradient = new LinearGradient(134.201f - mXOffset, 0f,
                point02.getXCoordinate(), point02.getYCoordinate(), new int[] {
                        Color.TRANSPARENT, mLineColor, Color.TRANSPARENT
                }, new float[] {
                        0, 0.5f, 1.0f
                }, TileMode.MIRROR);
        starLine = new StarLine(point01, point02, gradient);
        mStarLineList.add(starLine);

        // line left04
        point01 = new LinePoint(247f - mXOffset, 64.95f);
        point02 = new LinePoint(472.6f - mXOffset, 454.65f);
        gradient = new LinearGradient(209.201f - mXOffset, 0f, 510f - mXOffset,
                519.6f, new int[] {
                        Color.TRANSPARENT, mLineColor, Color.TRANSPARENT
                }, new float[] {
                        0, 0.5f, 1.0f
                }, TileMode.MIRROR);
        starLine = new StarLine(point01, point02, gradient);
        mStarLineList.add(starLine);

        // line left05
        point01 = new LinePoint(303.015f - mXOffset, 32.5f);
        point02 = new LinePoint(530.0619f - mXOffset, 424.7f);
        gradient = new LinearGradient(266.834f - mXOffset, -30f,
                point02.getXCoordinate(), point02.getYCoordinate(), new int[] {
                        Color.TRANSPARENT, mLineColor, Color.TRANSPARENT
                }, new float[] {
                        0, 0.5f, 1.0f
                }, TileMode.MIRROR);
        starLine = new StarLine(point01, point02, gradient);
        mStarLineList.add(starLine);

        // line left06
        point01 = new LinePoint(360f - mXOffset, 0f);
        point02 = new LinePoint(590f - mXOffset, 398.7f);
        gradient = new LinearGradient(point01.getXCoordinate(),
                point01.getYCoordinate(), point02.getXCoordinate(),
                point02.getYCoordinate(), new int[] {
                        Color.TRANSPARENT, mLineColor, Color.TRANSPARENT
                }, new float[] {
                        0, 0.5f, 1.0f
                }, TileMode.MIRROR);
        starLine = new StarLine(point01, point02, gradient);
        mStarLineList.add(starLine);

        // line left07
        point01 = new LinePoint(395.601f - mXOffset, -64.95f);
        point02 = new LinePoint(622.2f - mXOffset, 324.75f);
        gradient = new LinearGradient(point01.getXCoordinate(),
                point01.getYCoordinate(), 659.8f - mXOffset, 389.7f, new int[] {
                        Color.TRANSPARENT, mLineColor, Color.TRANSPARENT
                }, new float[] {
                        0, 0.5f, 1.0f
                }, TileMode.MIRROR);
        starLine = new StarLine(point01, point02, gradient);
        mStarLineList.add(starLine);

        // line right01
        point01 = new LinePoint(321.603f - mXOffset, -64.95f);
        point02 = new LinePoint(97.1985f - mXOffset, 324.75f);
        gradient = new LinearGradient(point01.getXCoordinate(),
                point01.getYCoordinate(), 59.798f - mXOffset, 389.7f,
                new int[] {
                        Color.TRANSPARENT, mLineColor, Color.TRANSPARENT
                }, new float[] {
                        0, 0.5f, 1.0f
                }, TileMode.MIRROR);

        starLine = new StarLine(point01, point02, gradient);
        mStarLineList.add(starLine);

        // line right02
        point01 = new LinePoint(134.798f - mXOffset, 389.7f);
        point02 = new LinePoint(359.202f - mXOffset, 0f);
        gradient = new LinearGradient(point01.getXCoordinate(),
                point01.getYCoordinate(), point02.getXCoordinate(),
                point02.getYCoordinate(), new int[] {
                        Color.TRANSPARENT, mLineColor, Color.TRANSPARENT
                }, new float[] {
                        0, 0.5f, 1.0f
                }, TileMode.MIRROR);
        starLine = new StarLine(point01, point02, gradient);
        mStarLineList.add(starLine);

        // line right03
        point01 = new LinePoint(172.397f - mXOffset, 454.65f);
        point02 = new LinePoint(415.487f - mXOffset, 32.5f);
        gradient = new LinearGradient(point01.getXCoordinate(),
                point01.getYCoordinate(), 434.202f - mXOffset, 0f, new int[] {
                        Color.TRANSPARENT, mLineColor, Color.TRANSPARENT
                }, new float[] {
                        0, 0.5f, 1.0f
                }, TileMode.MIRROR);
        starLine = new StarLine(point01, point02, gradient);
        mStarLineList.add(starLine);

        // line right04
        point01 = new LinePoint(209.997f - mXOffset, 519.6f);
        point02 = new LinePoint(471.801f - mXOffset, 64.95f);
        gradient = new LinearGradient(point01.getXCoordinate(),
                point01.getYCoordinate(), 509.202f - mXOffset, 0f, new int[] {
                        Color.TRANSPARENT, mLineColor, Color.TRANSPARENT
                }, new float[] {
                        0, 0.5f, 1.0f
                }, TileMode.MIRROR);
        starLine = new StarLine(point01, point02, gradient);
        mStarLineList.add(starLine);

        // line right05
        point01 = new LinePoint(284.997f - mXOffset, 519.6f);
        point02 = new LinePoint(509.4f - mXOffset, 129.9f);
        gradient = new LinearGradient(point01.getXCoordinate(),
                point01.getYCoordinate(), 546.801f - mXOffset, 64.95f,
                new int[] {
                        Color.TRANSPARENT, mLineColor, Color.TRANSPARENT
                }, new float[] {
                        0, 0.5f, 1.0f
                }, TileMode.MIRROR);
        starLine = new StarLine(point01, point02, gradient);
        mStarLineList.add(starLine);

        // line right06
        point01 = new LinePoint(359.997f - mXOffset, 519.6f);
        point02 = new LinePoint(565.916f - mXOffset, 162f);
        gradient = new LinearGradient(point01.getXCoordinate(),
                point01.getYCoordinate(), 621.801f - mXOffset, 64.95f,
                new int[] {
                        Color.TRANSPARENT, mLineColor, Color.TRANSPARENT
                }, new float[] {
                        0, 0.5f, 1.0f
                }, TileMode.MIRROR);
        starLine = new StarLine(point01, point02, gradient);
        mStarLineList.add(starLine);

        // line right07
        point01 = new LinePoint(397.596f - mXOffset, 584.55f);
        point02 = new LinePoint(622f - mXOffset, 194.85f);
        gradient = new LinearGradient(point01.getXCoordinate(),
                point01.getYCoordinate(), 659.4f - mXOffset, 129.9f, new int[] {
                        Color.TRANSPARENT, mLineColor, Color.TRANSPARENT
                }, new float[] {
                        0, 0.5f, 1.0f
                }, TileMode.MIRROR);
        starLine = new StarLine(point01, point02, gradient);
        mStarLineList.add(starLine);

        // horizontal line01
        point01 = new LinePoint(mHorizontalLineStart - mXOffset, 64.95f);
        point02 = new LinePoint(mHorizontalLineEnd - mXOffset, 64.95f);
        gradient = new LinearGradient(point01.getXCoordinate()
                - mFadeOutStartOffset, point01.getYCoordinate(),
                point02.getXCoordinate() + mFadeOutEndOffset,
                point02.getYCoordinate(), new int[] {
                        Color.TRANSPARENT, mLineColor, Color.TRANSPARENT
                }, new float[] {
                        0, 0.5f, 1.0f
                }, TileMode.MIRROR);
        starLine = new StarLine(point01, point02, gradient);
        mStarLineList.add(starLine);

        // horizontal line02
        point01 = new LinePoint(mHorizontalLineStart - mXOffset, 129.9f);
        point02 = new LinePoint(mHorizontalLineEnd - mXOffset, 129.9f);
        gradient = new LinearGradient(point01.getXCoordinate()
                - mFadeOutStartOffset, point01.getYCoordinate(),
                point02.getXCoordinate() + mFadeOutEndOffset,
                point02.getYCoordinate(), new int[] {
                        Color.TRANSPARENT, mLineColor, Color.TRANSPARENT
                }, new float[] {
                        0, 0.5f, 1.0f
                }, TileMode.MIRROR);
        starLine = new StarLine(point01, point02, gradient);
        mStarLineList.add(starLine);

        // horizontal line03
        point01 = new LinePoint(mHorizontalLineStart - mXOffset, 194.85f);
        point02 = new LinePoint(mHorizontalLineEnd - mXOffset, 194.85f);
        gradient = new LinearGradient(point01.getXCoordinate()
                - mFadeOutStartOffset, point01.getYCoordinate(),
                point02.getXCoordinate() + mFadeOutEndOffset,
                point02.getYCoordinate(), new int[] {
                        Color.TRANSPARENT, mLineColor, Color.TRANSPARENT
                }, new float[] {
                        0, 0.5f, 1.0f
                }, TileMode.MIRROR);
        starLine = new StarLine(point01, point02, gradient);
        mStarLineList.add(starLine);

        // horizontal line04
        point01 = new LinePoint(mHorizontalLineStart - mXOffset, 259.8f);
        point02 = new LinePoint(mHorizontalLineEnd - mXOffset, 259.8f);
        gradient = new LinearGradient(point01.getXCoordinate()
                - mFadeOutStartOffset, point01.getYCoordinate(),
                point02.getXCoordinate() + mFadeOutEndOffset,
                point02.getYCoordinate(), new int[] {
                        Color.TRANSPARENT, mLineColor, Color.TRANSPARENT
                }, new float[] {
                        0, 0.5f, 1.0f
                }, TileMode.MIRROR);
        starLine = new StarLine(point01, point02, gradient);
        mStarLineList.add(starLine);

        // horizontal line05
        point01 = new LinePoint(mHorizontalLineStart - mXOffset, 324.75f);
        point02 = new LinePoint(mHorizontalLineEnd - mXOffset, 324.75f);
        gradient = new LinearGradient(point01.getXCoordinate()
                - mFadeOutStartOffset, point01.getYCoordinate(),
                point02.getXCoordinate() + mFadeOutEndOffset,
                point02.getYCoordinate(), new int[] {
                        Color.TRANSPARENT, mLineColor, Color.TRANSPARENT
                }, new float[] {
                        0, 0.5f, 1.0f
                }, TileMode.MIRROR);
        starLine = new StarLine(point01, point02, gradient);
        mStarLineList.add(starLine);

        // horizontal line06
        point01 = new LinePoint(mHorizontalLineStart - mXOffset, 389.7f);
        point02 = new LinePoint(mHorizontalLineEnd - mXOffset, 389.7f);
        gradient = new LinearGradient(point01.getXCoordinate()
                - mFadeOutStartOffset, point01.getYCoordinate(),
                point02.getXCoordinate() + mFadeOutEndOffset,
                point02.getYCoordinate(), new int[] {
                        Color.TRANSPARENT, mLineColor, Color.TRANSPARENT
                }, new float[] {
                        0, 0.5f, 1.0f
                }, TileMode.MIRROR);
        starLine = new StarLine(point01, point02, gradient);
        mStarLineList.add(starLine);

        // horizontal line07
        point01 = new LinePoint(mHorizontalLineStart - mXOffset, 454.65f);
        point02 = new LinePoint(mHorizontalLineEnd - mXOffset, 454.65f);
        gradient = new LinearGradient(point01.getXCoordinate()
                - mFadeOutStartOffset, point01.getYCoordinate(),
                point02.getXCoordinate() + mFadeOutEndOffset,
                point02.getYCoordinate(), new int[] {
                        Color.TRANSPARENT, mLineColor, Color.TRANSPARENT
                }, new float[] {
                        0, 0.5f, 1.0f
                }, TileMode.MIRROR);
        starLine = new StarLine(point01, point02, gradient);
        mStarLineList.add(starLine);

    }

    /**
     * getAnimLineOne: TODO<br/>
     * 
     * @author wenguan.chen
     * @return StarLine
     * @since MT 1.0
     */
    private StarLine getAnimLineOne() {
        if (mAnimLineOne == null
                || (mAnimLineOne != null && mAnimLineOne.getXOffset() == 0 && mXOffset != 0)) {
            float point01XCoord = mStarLine01StartX - mXOffset;
            float point02XCoord = mStarLine01StartX + 75f - mXOffset;
            float point01YCoord = 1.7274f * (mStarLine01StartX) - 102.264f;
            float point02YCoord = 1.7274f * (mStarLine01StartX + 75f) - 102.264f;
            LinePoint point01 = new LinePoint(point01XCoord, point01YCoord);
            LinePoint point02 = new LinePoint(point02XCoord, point02YCoord);
            LinearGradient gradient = new LinearGradient(
                    point01.getXCoordinate(), point01.getYCoordinate(),
                    point02.getXCoordinate(), point02.getYCoordinate(),
                    Color.TRANSPARENT, mShootStarColor, TileMode.MIRROR);
            mAnimLineOne = new StarLine(point01, point02, gradient);
            mAnimLineOne.setXOffset(mXOffset);
        }
        return mAnimLineOne;
    }

    /**
     * translateLineOne: TODO<br/>
     * 
     * @author wenguan.chen
     * @param pOffset float
     * @since MT 1.0
     */
    private void translateLineOne(float pOffset) {
        float point01YCoord = 1.7274f * (mStarLine01StartX + pOffset) - 102.264f;

        mTranslateX = pOffset - mXOffset;
        mTranslateY = point01YCoord - (1.7274f * mStarLine01StartX - 102.264f);
    }

    /**
     * getAnimLineTwo: TODO<br/>
     * 
     * @author wenguan.chen
     * @return StarLine
     * @since MT 1.0
     */
    private StarLine getAnimLineTwo() {
        if (mAnimLineTwo == null
                || (mAnimLineTwo != null && mAnimLineTwo.getXOffset() == 0 && mXOffset != 0)) {
            float point01XCoord = mStarLine02StartX - mXOffset;
            float point02XCoord = mStarLine02StartX - 75f - mXOffset;
            float point01YCoord = 1144.7702f - 1.7366f * (mStarLine02StartX);
            float point02YCoord = 1144.7702f - 1.7366f * (mStarLine02StartX - 75f);
            LinePoint point01 = new LinePoint(point01XCoord, point01YCoord);
            LinePoint point02 = new LinePoint(point02XCoord, point02YCoord);
            LinearGradient gradient = new LinearGradient(
                    point01.getXCoordinate(), point01.getYCoordinate(),
                    point02.getXCoordinate(), point02.getYCoordinate(),
                    Color.TRANSPARENT, mShootStarColor, TileMode.MIRROR);
            mAnimLineTwo = new StarLine(point01, point02, gradient);
            mAnimLineTwo.setXOffset(mXOffset);
        }
        return mAnimLineTwo;
    }

    /**
     * translateLineTwo: TODO<br/>
     * 
     * @author wenguan.chen
     * @param pOffset float
     * @since MT 1.0
     */
    private void translateLineTwo(float pOffset) {
        float point01YCoord = 1144.7702f - 1.7366f * (mStarLine02StartX - pOffset);

        mTranslateX = -pOffset - mXOffset;
        mTranslateY = point01YCoord
                - (1144.7702f - 1.7366f * mStarLine02StartX);
    }

    /**
     * getAnimLineThree: TODO<br/>
     * 
     * @author wenguan.chen
     * @return StarLine
     * @since MT 1.0
     */
    private StarLine getAnimLineThree() {
        if (mAnimLineThree == null
                || (mAnimLineThree != null && mAnimLineThree.getXOffset() == 0 && mXOffset != 0)) {
            float point01XCoord = mStarLine03StartX - mXOffset;
            float point02XCoord = mStarLine03StartX - 75f - mXOffset;
            float point01YCoord = 1.7274f * (mStarLine03StartX) - 102.264f;
            float point02YCoord = 1.7274f * (mStarLine03StartX - 75f) - 102.264f;
            LinePoint point01 = new LinePoint(point01XCoord, point01YCoord);
            LinePoint point02 = new LinePoint(point02XCoord, point02YCoord);
            LinearGradient gradient = new LinearGradient(
                    point01.getXCoordinate(), point01.getYCoordinate(),
                    point02.getXCoordinate(), point02.getYCoordinate(),
                    Color.TRANSPARENT, mShootStarColor, TileMode.MIRROR);
            mAnimLineThree = new StarLine(point01, point02, gradient);
            mAnimLineThree.setXOffset(mXOffset);
        }
        return mAnimLineThree;
    }

    /**
     * translateLineThree: TODO<br/>
     * 
     * @author wenguan.chen
     * @param pOffset float
     * @since MT 1.0
     */
    private void translateLineThree(float pOffset) {
        float point01YCoord = 1.7274f * (mStarLine03StartX - pOffset) - 102.264f;

        mTranslateX = -pOffset - mXOffset;
        mTranslateY = point01YCoord - (1.7274f * mStarLine03StartX - 102.264f);
    }

    /**
     * getAnimLineFour: TODO<br/>
     * 
     * @author wenguan.chen
     * @return StarLine
     * @since MT 1.0
     */
    private StarLine getAnimLineFour() {
        if (mAnimLineFour == null
                || (mAnimLineFour != null && mAnimLineFour.getXOffset() == 0 && mXOffset != 0)) {
            float point01XCoord = mStarLine04StartX - mXOffset;
            float point02XCoord = mStarLine04StartX + 150f - mXOffset;
            float point01YCoord = 454.65f;
            float point02YCoord = 454.65f;
            LinePoint point01 = new LinePoint(point01XCoord, point01YCoord);
            LinePoint point02 = new LinePoint(point02XCoord, point02YCoord);
            LinearGradient gradient = new LinearGradient(
                    point01.getXCoordinate(), point01.getYCoordinate(),
                    point02.getXCoordinate(), point02.getYCoordinate(),
                    Color.TRANSPARENT, mShootStarColor, TileMode.MIRROR);
            mAnimLineFour = new StarLine(point01, point02, gradient);
            mAnimLineFour.setXOffset(mXOffset);
        }
        return mAnimLineFour;
    }

    /**
     * translateLineFour: TODO<br/>
     * 
     * @author wenguan.chen
     * @param pOffset float
     * @since MT 1.0
     */
    private void translateLineFour(float pOffset) {
        mTranslateX = pOffset - mXOffset;
        mTranslateY = 0;
    }

    /**
     * getAnimLineFive: TODO<br/>
     * 
     * @author wenguan.chen
     * @return StarLine
     * @since MT 1.0
     */
    private StarLine getAnimLineFive() {
        if (mAnimLineFive == null
                || (mAnimLineFive != null && mAnimLineFive.getXOffset() == 0 && mXOffset != 0)) {
            float point01XCoord = mStarLine05StartX - mXOffset;
            float point02XCoord = mStarLine05StartX - 150f - mXOffset;
            float point01YCoord = 64.95f;
            float point02YCoord = 64.95f;
            LinePoint point01 = new LinePoint(point01XCoord, point01YCoord);
            LinePoint point02 = new LinePoint(point02XCoord, point02YCoord);
            LinearGradient gradient = new LinearGradient(
                    point01.getXCoordinate(), point01.getYCoordinate(),
                    point02.getXCoordinate(), point02.getYCoordinate(),
                    Color.TRANSPARENT, mShootStarColor, TileMode.MIRROR);
            mAnimLineFive = new StarLine(point01, point02, gradient);
            mAnimLineFive.setXOffset(mXOffset);
        }
        return mAnimLineFive;
    }

    /**
     * translateLineFive: TODO<br/>
     * 
     * @author wenguan.chen
     * @param pOffset float
     * @since MT 1.0
     */
    private void translateLineFive(float pOffset) {
        mTranslateX = -pOffset - mXOffset;
        mTranslateY = 0;
    }

    /**
     * ClassName: LinePoint <br/>
     * Function: TODO ADD FUNCTION. <br/>
     * Date:2014-8-4
     * 
     * @author wenguan.chen
     * @version 0.1
     * @since MT 1.0
     */
    private class LinePoint {

        private float mXCoordinate;

        private float mYCoordinate;

        public LinePoint(float pXCor, float pYCor) {
            this.mXCoordinate = pXCor;
            this.mYCoordinate = pYCor;
        }

        /**
         * setXCoordinate: TODO<br/>
         * 
         * @author wenguan.chen
         * @param pXCor float
         * @since MT 1.0
         * @hide
         */
        public void setXCoordinate(float pXCor) {
            this.mXCoordinate = pXCor;
        }

        /**
         * setYCoordinate: TODO<br/>
         * 
         * @author wenguan.chen
         * @param pYCor float
         * @since MT 1.0
         * @hide
         */
        public void setYCoordinate(float pYCor) {
            this.mYCoordinate = pYCor;
        }

        /**
         * getXCoordinate: TODO<br/>
         * 
         * @author wenguan.chen
         * @return float
         * @since MT 1.0
         * @hide
         */
        public float getXCoordinate() {
            return mXCoordinate;
        }

        /**
         * getYCoordinate: TODO<br/>
         * 
         * @author wenguan.chen
         * @return float
         * @since MT 1.0
         * @hide
         */
        public float getYCoordinate() {
            return mYCoordinate;
        }
    }

    /**
     * ClassName: StarLine <br/>
     * Function: TODO ADD FUNCTION. <br/>
     * Date:2014-8-4
     * 
     * @author wenguan.chen
     * @version 0.1
     * @since MT 1.0
     */
    private class StarLine {
        private LinePoint mPointOne;

        private LinePoint mPointTwo;

        private LinearGradient mLineGradient;

        private float mXOffset = 0;

        public StarLine(LinePoint pPointOne, LinePoint pPointTwo,
                LinearGradient pGradient) {
            this.mPointOne = pPointOne;
            this.mPointTwo = pPointTwo;
            this.mLineGradient = pGradient;
        }

        /**
         * setXOffset: TODO<br/>
         * 
         * @author wenguan.chen
         * @param pOffset float
         * @since MT 1.0
         */
        public void setXOffset(float pOffset) {
            this.mXOffset = pOffset;
        }

        /**
         * getXOffset: TODO<br/>
         * 
         * @author wenguan.chen
         * @return float
         * @since MT 1.0
         */
        public float getXOffset() {
            return mXOffset;
        }

        /**
         * setLineGradient: TODO<br/>
         * 
         * @author wenguan.chen
         * @param pGradient LinearGradient
         * @since MT 1.0
         * @hide
         */
        public void setLineGradient(LinearGradient pGradient) {
            this.mLineGradient = pGradient;
        }

        /**
         * getLineGradient: TODO<br/>
         * 
         * @author wenguan.chen
         * @return LinearGradient
         * @since MT 1.0
         * @hide
         */
        public LinearGradient getLineGradient() {
            return this.mLineGradient;
        }

        /**
         * setStarLineOne: TODO<br/>
         * 
         * @author wenguan.chen
         * @param pPointOne LinePoint
         * @since MT 1.0
         * @hide
         */
        public void setStarLineOne(LinePoint pPointOne) {
            this.mPointOne = pPointOne;
        }

        /**
         * setStarLineTwo: TODO<br/>
         * 
         * @author wenguan.chen
         * @param pPointTwo LinePoint
         * @since MT 1.0
         * @hide
         */
        public void setStarLineTwo(LinePoint pPointTwo) {
            this.mPointTwo = pPointTwo;
        }

        /**
         * getStarLineOne: TODO<br/>
         * 
         * @author wenguan.chen
         * @return LinePoint
         * @since MT 1.0
         * @hide
         */
        public LinePoint getStarLineOne() {
            return this.mPointOne;
        }

        /**
         * getStarLineTwo: TODO<br/>
         * 
         * @author wenguan.chen
         * @return LinePoint
         * @since MT 1.0
         * @hide
         */
        public LinePoint getStarLineTwo() {
            return this.mPointTwo;
        }
    }

}
