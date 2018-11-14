
package com.music.widget.version1;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.DrawFilter;
import android.graphics.Paint;
import android.graphics.PaintFlagsDrawFilter;
import android.graphics.Path;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Xfermode;
import android.graphics.Bitmap.Config;
import android.os.Handler;
import android.os.Message;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.widget.RemoteViews.RemoteView;

import com.music.widget.anim.R;

/**
 * ClassName: WaterAnimView <br/>
 * Function: TODO ADD FUNCTION. <br/>
 * Date:2014-1-6
 * 
 * @author wenguan.chen
 * @version 0.1
 * @since MT 1.0
 */
@RemoteView
public class WaterAnimView extends View {
    private static final String TAG = "WaterAnimView";

    private int mAlbumWidth = 515;
    private int mAlbumHeight = 515;

    private float mStartPointX = 202f;
    private float mStartPointY = 322f;

    private DrawFilter mDrawFilter;

    private Bitmap mPurpleBmp;
    private Bitmap mYellowBmp;
    private Bitmap mAnimBmp;
    private Bitmap mMaskBmp;

    private int mPurpleX = 0;
    private int mPurpleY = 0;
    private int mYellowX = 0;
    private int mYellowY = 0;

    private int mCountNum = 0;

    private int mPurpleBmpWidth;

    private int mPurpleBmpHeight;

    private int mMaskWidth;

    private int mMaskHeight;

    private long mAudioId = -1;

    private static final int WATER_DOWN_UPDATE = 1989;

    private static final int WATER_RISE_UPDATE = 2014;

    private static final int WATER_END_ANIM = 2012;

    private boolean mIsEndAnim = true;

    private boolean mIsWaterRiseUpdate = false;

    private boolean mIsWaterDownUpdate = false;

    private Path mClipPath;

    private Xfermode mXfermode;

    private Bitmap mTailorBmp;

    private Paint mPaint;

    private boolean mIsScreenOn = true;

    private boolean mIsLauncherActive = true;

    private static final String LAUNCHER_RESUME = "com.meitu.mobile.widget.WIDGET_START";
    private static final String LAUNCHER_STOP = "com.meitu.mobile.widget.WIDGET_END";

    public static native void nativeWaterWave(Bitmap src1, int srcW1,
            int srcH1, Bitmap src2, int srcW2, int srcH2, Bitmap mask,
            Bitmap des, int x1, int y1, int x2, int y2, int width, int height);

    private Context mContext;
    /**
     * @param context
     */
    public WaterAnimView(Context context) {
        super(context);
    }

    /**
     * @param context
     * @param attrs
     */
    public WaterAnimView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    /**
     * @param context
     * @param attrs
     * @param defStyle
     */
    public WaterAnimView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        mContext = context;
        initData();
    }

    /**
     * initData: init data<br/>
     * 
     * @author wenguan.chen
     * @since MT 1.0
     */
    private void initData() {
        initBmp();

        if (mDrawFilter == null) {
            mDrawFilter = new PaintFlagsDrawFilter(0, Paint.ANTI_ALIAS_FLAG
                    | Paint.FILTER_BITMAP_FLAG);
        }

        if (mPaint == null) {
            mPaint = new Paint();
            mPaint.setColor(Color.WHITE);
            mPaint.setDither(true);
            mPaint.setAntiAlias(true);
        }

        if (mClipPath == null) {
            mClipPath = new Path();
            mClipPath.moveTo(3.5f, 0);
            mClipPath.lineTo(129f, 249f);
            mClipPath.lineTo(254.5f, 0);
            mClipPath.close();
        }

        if (mXfermode == null) {
            mXfermode = new PorterDuffXfermode(PorterDuff.Mode.SRC_IN);
        }

    }

    /**
     * initBmp: TODO<br/>
     * 
     * @author wenguan.chen
     * @since MT 1.0
     */
    private void initBmp() {
        if (mMaskBmp == null || mMaskBmp.isRecycled()) {
            mMaskBmp = BitmapFactory.decodeResource(getResources(),
                    R.drawable.ic_water_mask);
            mMaskWidth = mMaskBmp.getWidth();
            mMaskHeight = mMaskBmp.getHeight();
        }
        if (mPurpleBmp == null || mPurpleBmp.isRecycled()) {
            mPurpleBmp = BitmapFactory.decodeResource(getResources(),
                    R.drawable.ic_water_purple);
            mPurpleBmpWidth = mPurpleBmp.getWidth();
            mPurpleBmpHeight = mPurpleBmp.getHeight();
        }
        if (mYellowBmp == null || mYellowBmp.isRecycled()) {
            mYellowBmp = BitmapFactory.decodeResource(getResources(),
                    R.drawable.ic_water_yellow);
        }
        if (mTailorBmp == null || mTailorBmp.isRecycled()) {
            mTailorBmp = Bitmap.createBitmap(250, 250, Config.ARGB_8888);
        }
        if (mAnimBmp == null || mAnimBmp.isRecycled()) {
            mAnimBmp = Bitmap.createBitmap(mMaskWidth, mMaskHeight,
                    Config.ARGB_8888);
        }
    }

    /**
     * recycleBmp: TODO<br/>
     * 
     * @author wenguan.chen
     * @since MT 1.0
     */
    private void recycleBmp() {
        if (mMaskBmp != null && !mMaskBmp.isRecycled()) {
            mMaskBmp.recycle();
            mMaskBmp = null;
        }
        if (mAnimBmp != null && !mAnimBmp.isRecycled()) {
            mAnimBmp.recycle();
            mAnimBmp = null;
        }
        if (mPurpleBmp != null && !mPurpleBmp.isRecycled()) {
            mPurpleBmp.recycle();
            mPurpleBmp = null;
        }
        if (mYellowBmp != null && !mYellowBmp.isRecycled()) {
            mYellowBmp.recycle();
            mYellowBmp = null;
        }
        if (mTailorBmp != null && !mTailorBmp.isRecycled()) {
            mTailorBmp.recycle();
            mTailorBmp = null;
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
        Log.v(TAG, "------->>setAudioId(),pAudioId:" + pAudioId + ";mAudioId:"
                + mAudioId);
        if (mAudioId != pAudioId) {
            mAudioId = pAudioId;
            beginSwitchAnim();
        }
    }

    /**
     * endAnim: TODO<br/>
     * 
     * @author wenguan.chen
     * @param pValue boolean
     * @since MT 1.0
     * @hide
     */
    //@android.view.RemotableViewMethod
    public void endAnim(boolean pValue) {
        Log.v(TAG, "------->>endAnim(),pValue:" + pValue);
        if (pValue) {
            myHandler.removeMessages(WATER_RISE_UPDATE);
            myHandler.removeMessages(WATER_DOWN_UPDATE);
            mAudioId = -1;
            myHandler.sendMessage(myHandler.obtainMessage(WATER_END_ANIM));
        }
    }

    /**
     * beginSwitchAnim: TODO<br/>
     * 
     * @author wenguan.chen
     * @since MT 1.0
     */
    private void beginSwitchAnim() {
        mIsEndAnim = false;
        myHandler.removeMessages(WATER_END_ANIM);
        mIsWaterDownUpdate = true;
        refreshAnim();
    }

    /**
     * setScreenState: TODO<br/>
     * 
     * @author wenguan.chen
     * @param pIsScreenOn boolean
     * @since MT 1.0
     */
    private void setScreenState(boolean pIsScreenOn) {
        Log.v(TAG, "------->>setScreenState(),pIsScreenOn:" + pIsScreenOn);
        mIsScreenOn = pIsScreenOn;
        refreshAnim();
    }

    /**
     * setLauncherState: TODO<br/>
     * 
     * @author wenguan.chen
     * @param pIsActive boolean
     * @since MT 1.0
     */
    private void setLauncherState(boolean pIsActive) {
        Log.v(TAG, "------->>setLauncherState(),pIsActive:" + pIsActive);
        mIsLauncherActive = pIsActive;
        refreshAnim();
    }

    /**
     * water anim control invalidate handler
     */
    Handler myHandler = new Handler() {
        public void handleMessage(Message msg) {
            switch (msg.what) {
            case WATER_RISE_UPDATE:
                mIsWaterRiseUpdate = true;
                mIsWaterDownUpdate = false;
                mCountNum += 2;
                mPurpleX += 4;
                mYellowX += 3;
                if (mCountNum % 2 == 0 && mPurpleY < 245) {
                    mPurpleY += 1;
                }
                if (mCountNum % 2 == 0 && mYellowY < 261) {
                    mYellowY += 1;
                }
                if (mCountNum >= 4096) {
                    mCountNum = 0;
                }
                if (mPurpleX >= 792) {
                    mPurpleX = 0;
                }
                if (mYellowX >= 792) {
                    mYellowX = 0;
                }
                invalidate();
                myHandler.removeMessages(WATER_RISE_UPDATE);
                myHandler.sendMessageDelayed(
                        myHandler.obtainMessage(WATER_RISE_UPDATE), 30);
                break;
            case WATER_DOWN_UPDATE:
                mIsWaterRiseUpdate = false;
                mIsWaterDownUpdate = true;
                mPurpleX += 4;
                mPurpleY -= 8;
                mYellowX += 3;
                mYellowY -= 8;
                if (mPurpleX >= 792) {
                    mPurpleX = 0;
                }
                if (mPurpleY <= 0 || mYellowY <= 0) {
                    mPurpleY = 0;
                    mYellowY = 0;
                    myHandler.removeMessages(WATER_RISE_UPDATE);
                    myHandler.sendMessageDelayed(
                            myHandler.obtainMessage(WATER_RISE_UPDATE), 30);
                } else {
                    invalidate();
                    myHandler.removeMessages(WATER_DOWN_UPDATE);
                    myHandler.sendMessageDelayed(
                            myHandler.obtainMessage(WATER_DOWN_UPDATE), 30);
                }
                break;
            case WATER_END_ANIM:
                mIsWaterRiseUpdate = false;
                mIsWaterDownUpdate = false;
                mPurpleX += 4;
                mPurpleY -= 8;
                mYellowX += 3;
                mYellowY -= 8;
                if (mPurpleX >= 792) {
                    mPurpleX = 0;
                }
                if (mYellowX >= 792) {
                    mYellowX = 0;
                }
                if (mPurpleY <= 0 || mYellowY <= 0) {
                    mPurpleY = 0;
                    mYellowY = 0;
                    mIsEndAnim = true;
                    invalidate();
                } else {
                    invalidate();
                    myHandler.removeMessages(WATER_END_ANIM);
                    myHandler.sendMessageDelayed(
                            myHandler.obtainMessage(WATER_END_ANIM), 30);
                }
                break;
            default:
                break;
            }
            super.handleMessage(msg);
        }
    };

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int widthMode = MeasureSpec.getMode(widthMeasureSpec);
        int widthSize = MeasureSpec.getSize(widthMeasureSpec);
        int heightMode = MeasureSpec.getMode(heightMeasureSpec);
        int heightSize = MeasureSpec.getSize(heightMeasureSpec);

        float scaleX = 1.0f;
        float scaleY = 1.0f;

        if (widthMode != MeasureSpec.UNSPECIFIED
                && widthSize < (mAlbumWidth + mStartPointX)) {
            scaleX = (float)widthSize / (float)(mAlbumWidth + mStartPointX);
        }

        if (heightMode != MeasureSpec.UNSPECIFIED
                && heightSize < (mAlbumWidth + mStartPointY)) {
            scaleY = (float)heightSize / (float)(mAlbumHeight + mStartPointY);
        }

        float scale = Math.min(scaleX, scaleY);

        setMeasuredDimension(
                resolveSizeAndState(
                        (int)((mAlbumWidth + mStartPointX * 2) * scale),
                        widthMeasureSpec, 0),
                resolveSizeAndState(
                        (int)((mAlbumHeight + mStartPointY * 2) * scale),
                        heightMeasureSpec, 0));
    }

    /**
     * refreshAnim: TODO<br/>
     * 
     * @author wenguan.chen
     * @since MT 1.0
     */
    private void refreshAnim() {
        myHandler.removeMessages(WATER_RISE_UPDATE);
        myHandler.removeMessages(WATER_DOWN_UPDATE);
        if (mIsScreenOn && mIsLauncherActive) {
            if (mIsWaterRiseUpdate) {
                myHandler.sendMessage(myHandler
                        .obtainMessage(WATER_RISE_UPDATE));
            }
            if (mIsWaterDownUpdate) {
                myHandler.sendMessage(myHandler
                        .obtainMessage(WATER_DOWN_UPDATE));
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

    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
        Log.v(TAG, "------->>onAttachedToWindow()");
        IntentFilter filter = new IntentFilter();
        filter.addAction(Intent.ACTION_SCREEN_OFF);
        filter.addAction(Intent.ACTION_SCREEN_ON);
        filter.addAction(LAUNCHER_RESUME);
        filter.addAction(LAUNCHER_STOP);
        mContext.registerReceiver(mReceiver, filter);
        initData();
        refreshAnim();
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        Log.v(TAG, "------->>onDetachedFromWindow()");
        mContext.unregisterReceiver(mReceiver);
        mAudioId = -1;
        myHandler.removeMessages(WATER_RISE_UPDATE);
        myHandler.removeMessages(WATER_DOWN_UPDATE);
        recycleBmp();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        if (!mIsEndAnim) {
            canvas.setDrawFilter(mDrawFilter); // eliminate the aliasing effect
            canvas.drawBitmap(tailorBmp(), mStartPointX, mStartPointY, null);
        }
    }

    /**
     * tailorBmp: TODO<br/>
     * 
     * @author wenguan.chen
     * @return Bitmap
     * @since MT 1.0
     */
    private Bitmap tailorBmp() {
        initData();
        mTailorBmp.eraseColor(Color.TRANSPARENT);
        Canvas canvas = new Canvas(mTailorBmp);
        canvas.setDrawFilter(mDrawFilter); // eliminate the aliasing effect
        canvas.save();
        canvas.drawPath(mClipPath, mPaint);// draw triangle area
        mPaint.setXfermode(mXfermode);
        canvas.drawBitmap(applyEffect(mPurpleX, mPurpleY, mYellowX, mYellowY),
                0, 0, mPaint);
        mPaint.setXfermode(null);
        canvas.restore();
        return mTailorBmp;
    }

    /**
     * applyEffect: TODO<br/>
     * 
     * @author wenguan.chen
     * @param pX1 int
     * @param pY1 int
     * @param pX2 int
     * @param pY2 int
     * @return Bitmap
     * @since MT 1.0
     */
    private Bitmap applyEffect(int pX1, int pY1, int pX2, int pY2) {
        nativeWaterWave(mPurpleBmp, mPurpleBmpWidth, mPurpleBmpHeight,
                mYellowBmp, mPurpleBmpWidth, mPurpleBmpHeight, mMaskBmp,
                mAnimBmp, pX1, pY1, pX2, pY2, mMaskWidth, mMaskHeight);
        return mAnimBmp;
    }

}
