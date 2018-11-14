
package com.music.widget.version2;

import java.util.Random;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.os.Handler;
import android.os.Message;
import android.util.AttributeSet;
import android.util.Log;
//import android.view.RemotableViewMethod;
import android.view.View;
import android.widget.RemoteViews.RemoteView;

/**
 * ClassName: CustomFireflyView <br/>
 * Function: TODO ADD FUNCTION. <br/>
 * Date:2014-8-5
 * 
 * @author wenguan.chen
 * @version 0.1
 * @since MT 1.0
 */
@RemoteView
public class CustomFireflyView extends View {

    private static final String TAG = "CustomFireflyView";

    private static final int FIREFLY_REFRESH = 20140905;

    private boolean mIsPlaying = false;
    private boolean mIsScreenOn = true;
    private boolean mIsLauncherActive = true;

    private static final String LAUNCHER_RESUME = "com.meitu.mobile.widget.WIDGET_START";
    private static final String LAUNCHER_STOP = "com.meitu.mobile.widget.WIDGET_END";

    private Context mContext;

    private int mXOffset = 0;

    private boolean mIsSwitch = false;

    private long mAudioId = -1;

    private boolean mSwitching = false;

    public CustomFireflyView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        mContext = context;
    }

    public CustomFireflyView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public CustomFireflyView(Context context) {
        this(context, null);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        int widthSize = MeasureSpec.getSize(widthMeasureSpec);
        mXOffset = 360 - widthSize / 2;
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
        drawFirefly(canvas);
    }

    /**
     * drawFirefly: TODO<br/>
     * 
     * @author wenguan.chen
     * @param pCanvas Canvas
     * @since MT 1.0
     */
    private void drawFirefly(Canvas pCanvas) {
        if (mIsPlaying && !mSwitching) {
            Firefly.FireflyNode[] sprinkle = Firefly.getInstance().getNodes();
            for (int i = 0; i < sprinkle.length; i++) {
                sprinkle[i].sparkle();
                sprinkle[i].draw(pCanvas, mXOffset);
            }
        }
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

    Handler myHandler = new Handler() {
        public void handleMessage(Message msg) {
            switch (msg.what) {
            case FIREFLY_REFRESH:
                mSwitching = false;
                invalidate();
                refreshView(false);
                break;
            default:
                break;
            }
        }
    };

    /**
     * refreshView: TODO<br/>
     * 
     * @author wenguan.chen
     * @since MT 1.0
     */
    private void refreshView(boolean pIsSwtich) {
        myHandler.removeCallbacksAndMessages(null);
        if (mIsPlaying && mIsScreenOn && mIsLauncherActive) {
            if (pIsSwtich) {
                mSwitching = true;
                invalidate();
                myHandler.sendEmptyMessageDelayed(FIREFLY_REFRESH, 1000);
            } else {
                myHandler.sendEmptyMessageDelayed(FIREFLY_REFRESH, 50);
            }
        }
        if (!mIsPlaying) {
            invalidate();
        }
    }

    /**
     * ClassName: Firefly <br/>
     * Function: TODO ADD FUNCTION. <br/>
     * Date:2014-8-5
     * 
     * @author wenguan.chen
     * @version 0.1
     * @since MT 1.0
     */
    public static class Firefly {
        private static final int N_PARTICLES = 350;
        private static final int BRIGHT = 150;
        private static final int DELTA = 25;
        private static final int SIZE = 3;
        private static final double SPREAD = 50;
        private Random mRandom;
        private FireflyNode[] mNodes;
        private static final int WIDTH = 720;
        private int mBanding = 358;
        private Paint mFireflyPaint;
        private static Firefly mInstance;

        public static Firefly getInstance() {
            if (mInstance == null) {
                mInstance = new Firefly();
            }
            return mInstance;
        }

        public Firefly() {
            mRandom = new Random();
            mFireflyPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
            mFireflyPaint.setColor(Color.WHITE);
            createStars();
        }

        /**
         * createStars: TODO<br/>
         * 
         * @author wenguan.chen
         * @since MT 1.0
         */
        private void createStars() {
            mNodes = new FireflyNode[N_PARTICLES];
            for (int i = 0; i < N_PARTICLES; i++) {
                mNodes[i] = new FireflyNode();
                // Randomize the intial brightness
                mNodes[i].mAlpha = mRandom.nextInt(Firefly.BRIGHT);
                mNodes[i].mAlphaDelta = (mRandom.nextInt(3) <= 1) ? -Firefly.DELTA
                        : Firefly.DELTA;
            }
        }

        /**
         * getNodes: TODO<br/>
         * 
         * @author wenguan.chen
         * @return FireflyNode[]
         * @since MT 1.0
         * @hide
         */
        public FireflyNode[] getNodes() {
            return mNodes;
        }

        /**
         * ClassName: Node <br/>
         * Function: TODO ADD FUNCTION. <br/>
         * Date:2014-8-5
         * 
         * @author wenguan.chen
         * @version 0.1
         * @since MT 1.0
         */
        public class FireflyNode {
            public int mXCoordinate = 0;
            public int mYCoordinate = 0;
            public int mRadius = 0;
            public int mAlpha = 0;
            public int mAlphaDelta = DELTA;

            public FireflyNode(int x, int y, int s, int b) {
                this.mXCoordinate = x;
                this.mYCoordinate = y;
                this.mRadius = s;
                this.mAlpha = b;
            }

            public FireflyNode() {
                randomize();
            }

            /**
             * randomize: TODO<br/>
             * 
             * @author wenguan.chen
             * @since MT 1.0
             * @hide
             */
            public void randomize() {
                mAlpha = 0;
                mAlphaDelta = DELTA;
                mXCoordinate = mRandom.nextInt(WIDTH);
                double g = mRandom.nextGaussian();
                mYCoordinate = mBanding + (int)(SPREAD * g);
                mRadius = mRandom.nextInt(SIZE);
            }

            /**
             * sparkle: TODO<br/>
             * 
             * @author wenguan.chen
             * @since MT 1.0
             * @hide
             */
            public void sparkle() {
                mAlpha += mAlphaDelta;
                if (mAlpha > BRIGHT) {
                    mAlphaDelta = -DELTA;
                } else if (mAlpha < 0) {
                    randomize();
                }
            }

            /**
             * draw: TODO<br/>
             * 
             * @author wenguan.chen
             * @param pCanvas Canvas
             * @since MT 1.0
             * @hide
             */
            public void draw(Canvas pCanvas, int pXOffset) {
                if (isInLimitArea(mXCoordinate, mYCoordinate, pXOffset)) {
                    mFireflyPaint.setAlpha(mAlpha);
                    pCanvas.drawCircle(mXCoordinate, mYCoordinate, mRadius,
                            mFireflyPaint);
                }
            }

            /**
             * isInLimitArea: TODO<br/>
             * 
             * @author wenguan.chen
             * @param pXCoord int
             * @param pYCoord int
             * @return boolean
             * @since MT 1.0
             */
            private boolean isInLimitArea(int pXCoord, int pYCoord, int pXOffset) {
                if (pYCoord > 389 && pYCoord < 519) {
                    if (pXCoord < 360) {
                        int XOnline = (int)((float)(102.264f + (float)pYCoord) / 1.7274f)
                                - pXOffset;
                        if (pXCoord > XOnline) {
                            return true;
                        } else {
                            return false;
                        }
                    } else if (pXCoord >= 360) {
                        int XOnline = (int)((float)(1144.7702f - (float)pYCoord) / 1.7366f)
                                - pXOffset;
                        if (pXCoord < XOnline) {
                            return true;
                        } else {
                            return false;
                        }
                    }
                } else if (pYCoord < 389) {
                    if (pXCoord < 360) {
                        int XOnline = (int)((float)(622.1375f - (float)pYCoord) / 1.7281f)
                                - pXOffset;
                        if (pXCoord > XOnline) {
                            return true;
                        } else {
                            return false;
                        }
                    } else if (pXCoord >= 360) {
                        int XOnline = (int)((float)(623.52f + (float)pYCoord) / 1.732f)
                                - pXOffset;
                        if (pXCoord < XOnline) {
                            return true;
                        } else {
                            return false;
                        }
                    }
                }
                return false;
            }
        }
    }

}
