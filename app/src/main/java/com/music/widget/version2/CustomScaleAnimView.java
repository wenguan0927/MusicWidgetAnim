
package com.music.widget.version2;

import java.util.HashMap;

import android.content.Context;
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
 * ClassName: CustomScaleAnimView <br/>
 * Function: TODO ADD FUNCTION. <br/>
 * Date:2014-7-18
 * 
 * @author wenguan.chen
 * @version 0.1
 * @since MT 1.0
 */
@RemoteView
public class CustomScaleAnimView extends View {

    private final String TAG = "CustomScaleAnimView";

    private Paint mPaint;

    private Xfermode mXfermode;

    private Bitmap mAlbumTailorBmp;

    private Bitmap mAnimTailorBmp;

    private Bitmap mAnimMosaicBmp;

    private float mAlbumWidth = 525;
    private float mAlbumHeight = 525;

    private Bitmap mDefAlbumBmp;

    private Bitmap mCurrentBmp;

    private Bitmap mAnimBmp;

    private Bitmap mAlbumBmp;

    private boolean mShouldInvalidate;

    private long mAudioId = -1;

    private DrawFilter mDrawFilter;

    private Bitmap mBmpForAnim;

    private float mStartPointX = 58f;
    private float mStartPointY = 0;

    private float mPathHeight = 519.6f;

    private float mPathWidth = 450f;

    private float mXOffset = 134.5f - (float)((mAlbumWidth - mPathWidth) / 2);

    private float mYOffset = (float)((mAlbumHeight - mPathHeight) / 2);

    private Path mAnimPath01;

    private Path mAnimPath02;

    private Path mAnimPath03;

    private Path mAnimPath04;

    private Path mAnimPath05;

    private Path mAnimPath06;

    private Path mAnimPath07;

    private Path mAnimPath08;

    private Path mAnimPath09;

    private Path mAnimPath10;

    private Path mAnimPath11;

    private Path mAnimPath12;

    private Path mAlbumPath;

    private Path mDefaultAlbumPath;

    private HashMap<String, Path> mPathMap = new HashMap<String, Path>();

    private int mSequenceIndex = 0;

    private int mTempIndex = -1;

    public static final int WAIT_INIT_EVENT = 20140729;

    private int[] mSequence = {
            0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11
    };

    private Canvas mCanvas;

    public CustomScaleAnimView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        initData();
    }

    public CustomScaleAnimView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public CustomScaleAnimView(Context context) {
        this(context, null);
    }

    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
        initData();
        if (mAudioId == -1) {
            setAudioId(-2);// init View
            setAlbumArtBitmap(null);
        }
    }

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

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        myHandler.removeCallbacksAndMessages(null);
        recycleBmp();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        canvas.setDrawFilter(mDrawFilter); // eliminate the aliasing effect

        if ((mSequenceIndex < mSequence.length) && mAnimBmp != null
                && !mAnimBmp.isRecycled()) {
            canvas.drawBitmap(mAnimBmp, mStartPointX, mStartPointY, null);
            mSequenceIndex++;
            myHandler.removeMessages(WAIT_INIT_EVENT);
            myHandler.sendEmptyMessage(WAIT_INIT_EVENT);
        }

        if ((mSequenceIndex == mSequence.length) && mAnimBmp != null
                && !mAnimBmp.isRecycled()) {
            canvas.drawBitmap(mAnimBmp, mStartPointX, mStartPointY, null);
        }
    }

    /**
     * initData: TODO<br/>
     * 
     * @author wenguan.chen
     * @since MT 1.0
     */
    private void initData() {
        initAnimPath();
        initPath();

        if(mCanvas == null){
            mCanvas = new Canvas();
        }

        if (mPaint == null) {
            mPaint = new Paint();
            mPaint.setColor(Color.WHITE);
            mPaint.setDither(true);
            mPaint.setAntiAlias(true);
        }

        if (mXfermode == null) {
            mXfermode = new PorterDuffXfermode(PorterDuff.Mode.SRC_IN);
        }

        if (mAlbumTailorBmp == null || mAlbumTailorBmp.isRecycled()) {
            mAlbumTailorBmp = Bitmap.createBitmap((int)mAlbumWidth,
                    (int)mAlbumHeight, Config.ARGB_8888);
        }

        if (mAnimTailorBmp == null || mAnimTailorBmp.isRecycled()) {
            mAnimTailorBmp = Bitmap.createBitmap((int)mAlbumWidth,
                    (int)mAlbumHeight, Config.ARGB_8888);
        }

        if (mAnimMosaicBmp == null || mAnimMosaicBmp.isRecycled()) {
            mAnimMosaicBmp = Bitmap.createBitmap((int)mAlbumWidth,
                    (int)mAlbumHeight, Config.ARGB_8888);
        }

        if (mDefAlbumBmp == null || mDefAlbumBmp.isRecycled()) {
            mDefAlbumBmp = BitmapFactory
                    .decodeResource(
                            getResources(), R.drawable.ic_appwidget_albumart_default);
            Bitmap resizeBmp = getDefaultAlbumTailorBmp(mDefAlbumBmp);
            if (mDefAlbumBmp != resizeBmp) {
                mDefAlbumBmp.recycle();
                mDefAlbumBmp = resizeBmp;
            }
        }

        if (mDrawFilter == null) {
            mDrawFilter = new PaintFlagsDrawFilter(0, Paint.ANTI_ALIAS_FLAG
                    | Paint.FILTER_BITMAP_FLAG);
        }

    }

    /**
     * initPath: TODO<br/>
     * 
     * @author wenguan.chen
     * @since MT 1.0
     */
    private void initPath() {
        if (mAlbumPath == null) {
            mAlbumPath = new Path();
            mAlbumPath.moveTo(360f - mXOffset, 0);
            mAlbumPath.lineTo(322.5f - mXOffset, 64.95f);
            mAlbumPath.lineTo(247.5f - mXOffset, 64.95f);
            mAlbumPath.lineTo(172f - mXOffset, 194.85f);
            mAlbumPath.lineTo(209.5f - mXOffset, 259.8f);
            mAlbumPath.lineTo(134.5f - mXOffset, 389.7f);
            mAlbumPath.lineTo(285f - mXOffset, 389.7f);
            mAlbumPath.lineTo(360f - mXOffset, 519.6f);
            mAlbumPath.lineTo(435f - mXOffset, 389.7f);
            mAlbumPath.lineTo(585f - mXOffset, 389.7f);
            mAlbumPath.lineTo(510f - mXOffset, 259.8f);
            mAlbumPath.lineTo(547f - mXOffset, 194.85f);
            mAlbumPath.lineTo(472.5f - mXOffset, 64.95f);
            mAlbumPath.lineTo(397.5f - mXOffset, 64.95f);
            mAlbumPath.close();
        }

        if (mDefaultAlbumPath == null) {
            mDefaultAlbumPath = new Path();
            mDefaultAlbumPath.moveTo(247.5f - mXOffset, 64.95f);
            mDefaultAlbumPath.lineTo(172f - mXOffset, 194.85f);
            mDefaultAlbumPath.lineTo(360f - mXOffset, 519.6f);
            mDefaultAlbumPath.lineTo(547f - mXOffset, 194.85f);
            mDefaultAlbumPath.lineTo(472.5f - mXOffset, 64.95f);
            mDefaultAlbumPath.close();
        }
    }

    /**
     * initAnimPath: TODO<br/>
     * 
     * @author wenguan.chen
     * @since MT 1.0
     */
    private void initAnimPath() {
        if (mAnimPath01 == null) {
            mAnimPath01 = new Path();
            mAnimPath01.moveTo(360f - mXOffset, 0);
            mAnimPath01.lineTo(322.5f - mXOffset, 64.95f);
            mAnimPath01.lineTo(247.5f - mXOffset, 64.95f);
            mAnimPath01.lineTo(172f - mXOffset, 194.85f);
            mAnimPath01.lineTo(209.5f - mXOffset, 259.8f);

            mAnimPath01.lineTo(172f - mXOffset, 324.75f);
            mAnimPath01.lineTo(209.5f - mXOffset, 389.7f);

            mAnimPath01.lineTo(285f - mXOffset, 389.7f);
            mAnimPath01.lineTo(360f - mXOffset, 519.6f);
            mAnimPath01.lineTo(435f - mXOffset, 389.7f);
            mAnimPath01.lineTo(585f - mXOffset, 389.7f);
            mAnimPath01.lineTo(510f - mXOffset, 259.8f);
            mAnimPath01.lineTo(547f - mXOffset, 194.85f);
            mAnimPath01.lineTo(472.5f - mXOffset, 64.95f);
            mAnimPath01.lineTo(397.5f - mXOffset, 64.95f);
            mAnimPath01.close();
            mPathMap.put(String.valueOf(0), mAnimPath01);
        }

        if (mAnimPath02 == null) {
            mAnimPath02 = new Path();
            mAnimPath02.moveTo(360f - mXOffset, 0);
            mAnimPath02.lineTo(322.5f - mXOffset, 64.95f);
            mAnimPath02.lineTo(247.5f - mXOffset, 64.95f);
            mAnimPath02.lineTo(172f - mXOffset, 194.85f);
            mAnimPath02.lineTo(209.5f - mXOffset, 259.8f);

            mAnimPath02.lineTo(172f - mXOffset, 324.75f);
            mAnimPath02.lineTo(247.5f - mXOffset, 324.75f);
            mAnimPath02.lineTo(209.5f - mXOffset, 389.7f);

            mAnimPath02.lineTo(285f - mXOffset, 389.7f);
            mAnimPath02.lineTo(360f - mXOffset, 519.6f);
            mAnimPath02.lineTo(435f - mXOffset, 389.7f);
            mAnimPath02.lineTo(585f - mXOffset, 389.7f);
            mAnimPath02.lineTo(510f - mXOffset, 259.8f);
            mAnimPath02.lineTo(547f - mXOffset, 194.85f);
            mAnimPath02.lineTo(472.5f - mXOffset, 64.95f);
            mAnimPath02.lineTo(397.5f - mXOffset, 64.95f);
            mAnimPath02.close();
            mPathMap.put(String.valueOf(1), mAnimPath02);
        }

        if (mAnimPath03 == null) {
            mAnimPath03 = new Path();
            mAnimPath03.moveTo(360f - mXOffset, 0);
            mAnimPath03.lineTo(322.5f - mXOffset, 64.95f);
            mAnimPath03.lineTo(247.5f - mXOffset, 64.95f);
            mAnimPath03.lineTo(172f - mXOffset, 194.85f);

            mAnimPath03.lineTo(360f - mXOffset, 519.6f);
            mAnimPath03.lineTo(435f - mXOffset, 389.7f);
            mAnimPath03.lineTo(585f - mXOffset, 389.7f);
            mAnimPath03.lineTo(510f - mXOffset, 259.8f);
            mAnimPath03.lineTo(547f - mXOffset, 194.85f);
            mAnimPath03.lineTo(472.5f - mXOffset, 64.95f);
            mAnimPath03.lineTo(397.5f - mXOffset, 64.95f);
            mAnimPath03.close();
            mPathMap.put(String.valueOf(2), mAnimPath03);
        }

        if (mAnimPath04 == null) {
            mAnimPath04 = new Path();
            mAnimPath04.moveTo(360f - mXOffset, 0);
            mAnimPath04.lineTo(322.5f - mXOffset, 64.95f);
            mAnimPath04.lineTo(247.5f - mXOffset, 64.95f);

            mAnimPath04.lineTo(172f - mXOffset, 194.85f);
            mAnimPath04.lineTo(247.5f - mXOffset, 194.85f);

            mAnimPath04.lineTo(209.5f - mXOffset, 259.8f);
            mAnimPath04.lineTo(285f - mXOffset, 259.8f);

            mAnimPath04.lineTo(247.5f - mXOffset, 324.75f);
            mAnimPath04.lineTo(322.5f - mXOffset, 324.75f);

            mAnimPath04.lineTo(285f - mXOffset, 389.7f);
            mAnimPath04.lineTo(360f - mXOffset, 389.7f);

            mAnimPath04.lineTo(322.5f - mXOffset, 454.65f);
            mAnimPath04.lineTo(397.5f - mXOffset, 454.65f);

            mAnimPath04.lineTo(435f - mXOffset, 389.7f);
            mAnimPath04.lineTo(585f - mXOffset, 389.7f);
            mAnimPath04.lineTo(510f - mXOffset, 259.8f);
            mAnimPath04.lineTo(547f - mXOffset, 194.85f);
            mAnimPath04.lineTo(472.5f - mXOffset, 64.95f);
            mAnimPath04.lineTo(397.5f - mXOffset, 64.95f);
            mAnimPath04.close();
            mPathMap.put(String.valueOf(3), mAnimPath04);
        }

        if (mAnimPath05 == null) {
            mAnimPath05 = new Path();
            mAnimPath05.moveTo(360f - mXOffset, 0);
            mAnimPath05.lineTo(322.5f - mXOffset, 64.95f);
            mAnimPath05.lineTo(247.5f - mXOffset, 64.95f);

            mAnimPath05.lineTo(209.5f - mXOffset, 129.9f);
            mAnimPath05.lineTo(397.5f - mXOffset, 454.65f);

            mAnimPath05.lineTo(435f - mXOffset, 389.7f);
            mAnimPath05.lineTo(585f - mXOffset, 389.7f);
            mAnimPath05.lineTo(510f - mXOffset, 259.8f);
            mAnimPath05.lineTo(547f - mXOffset, 194.85f);
            mAnimPath05.lineTo(472.5f - mXOffset, 64.95f);
            mAnimPath05.lineTo(397.5f - mXOffset, 64.95f);
            mAnimPath05.close();
            mPathMap.put(String.valueOf(4), mAnimPath05);
        }

        if (mAnimPath06 == null) {
            mAnimPath06 = new Path();
            mAnimPath06.moveTo(360f - mXOffset, 0);
            mAnimPath06.lineTo(322.5f - mXOffset, 64.95f);
            mAnimPath06.lineTo(247.5f - mXOffset, 64.95f);

            mAnimPath06.lineTo(209.5f - mXOffset, 129.9f);
            mAnimPath06.lineTo(285f - mXOffset, 129.9f);

            mAnimPath06.lineTo(247.5f - mXOffset, 194.85f);
            mAnimPath06.lineTo(322.5f - mXOffset, 194.85f);

            mAnimPath06.lineTo(285f - mXOffset, 259.8f);
            mAnimPath06.lineTo(360f - mXOffset, 259.8f);

            mAnimPath06.lineTo(322.5f - mXOffset, 324.75f);
            mAnimPath06.lineTo(397.5f - mXOffset, 324.75f);

            mAnimPath06.lineTo(360f - mXOffset, 389.7f);
            mAnimPath06.lineTo(435f - mXOffset, 389.7f);

            mAnimPath06.lineTo(585f - mXOffset, 389.7f);
            mAnimPath06.lineTo(510f - mXOffset, 259.8f);
            mAnimPath06.lineTo(547f - mXOffset, 194.85f);
            mAnimPath06.lineTo(472.5f - mXOffset, 64.95f);
            mAnimPath06.lineTo(397.5f - mXOffset, 64.95f);
            mAnimPath06.close();
            mPathMap.put(String.valueOf(5), mAnimPath06);
        }

        if (mAnimPath07 == null) {
            mAnimPath07 = new Path();
            mAnimPath07.moveTo(360f - mXOffset, 0);
            mAnimPath07.lineTo(322.5f - mXOffset, 64.95f);
            mAnimPath07.lineTo(247.5f - mXOffset, 64.95f);

            mAnimPath07.lineTo(435f - mXOffset, 389.7f);
            mAnimPath07.lineTo(585f - mXOffset, 389.7f);
            mAnimPath07.lineTo(510f - mXOffset, 259.8f);
            mAnimPath07.lineTo(547f - mXOffset, 194.85f);
            mAnimPath07.lineTo(472.5f - mXOffset, 64.95f);
            mAnimPath07.lineTo(397.5f - mXOffset, 64.95f);
            mAnimPath07.close();
            mPathMap.put(String.valueOf(6), mAnimPath07);
        }

        if (mAnimPath08 == null) {
            mAnimPath08 = new Path();
            mAnimPath08.moveTo(360f - mXOffset, 0);

            mAnimPath08.lineTo(285f - mXOffset, 129.9f);
            mAnimPath08.lineTo(360f - mXOffset, 129.9f);

            mAnimPath08.lineTo(322.5f - mXOffset, 194.85f);
            mAnimPath08.lineTo(397.5f - mXOffset, 194.85f);

            mAnimPath08.lineTo(360f - mXOffset, 259.8f);
            mAnimPath08.lineTo(435f - mXOffset, 259.8f);

            mAnimPath08.lineTo(397.5f - mXOffset, 324.75f);
            mAnimPath08.lineTo(472.5f - mXOffset, 324.75f);

            mAnimPath08.lineTo(435f - mXOffset, 389.7f);
            mAnimPath08.lineTo(585f - mXOffset, 389.7f);
            mAnimPath08.lineTo(510f - mXOffset, 259.8f);
            mAnimPath08.lineTo(547f - mXOffset, 194.85f);
            mAnimPath08.lineTo(472.5f - mXOffset, 64.95f);
            mAnimPath08.lineTo(397.5f - mXOffset, 64.95f);
            mAnimPath08.close();
            mPathMap.put(String.valueOf(7), mAnimPath08);
        }

        if (mAnimPath09 == null) {
            mAnimPath09 = new Path();
            mAnimPath09.moveTo(360f - mXOffset, 0);
            mAnimPath09.lineTo(322.5f - mXOffset, 64.95f);

            mAnimPath09.lineTo(510f - mXOffset, 389.7f);

            mAnimPath09.lineTo(585f - mXOffset, 389.7f);
            mAnimPath09.lineTo(510f - mXOffset, 259.8f);
            mAnimPath09.lineTo(547f - mXOffset, 194.85f);
            mAnimPath09.lineTo(472.5f - mXOffset, 64.95f);
            mAnimPath09.lineTo(397.5f - mXOffset, 64.95f);
            mAnimPath09.close();
            mPathMap.put(String.valueOf(8), mAnimPath09);
        }

        if (mAnimPath10 == null) {
            mAnimPath10 = new Path();
            mAnimPath10.moveTo(360f - mXOffset, 0);
            mAnimPath10.lineTo(322.5f - mXOffset, 64.95f);
            mAnimPath10.lineTo(397.5f - mXOffset, 64.95f);

            mAnimPath10.lineTo(360f - mXOffset, 129.9f);
            mAnimPath10.lineTo(435f - mXOffset, 129.9f);

            mAnimPath10.lineTo(397.5f - mXOffset, 194.85f);
            mAnimPath10.lineTo(472.5f - mXOffset, 194.85f);

            mAnimPath10.lineTo(435f - mXOffset, 259.8f);
            mAnimPath10.lineTo(510f - mXOffset, 259.8f);

            mAnimPath10.lineTo(472.5f - mXOffset, 324.75f);
            mAnimPath10.lineTo(547f - mXOffset, 324.75f);

            mAnimPath10.lineTo(510f - mXOffset, 389.7f);
            mAnimPath10.lineTo(585f - mXOffset, 389.7f);

            mAnimPath10.lineTo(510f - mXOffset, 259.8f);
            mAnimPath10.lineTo(547f - mXOffset, 194.85f);
            mAnimPath10.lineTo(472.5f - mXOffset, 64.95f);
            mAnimPath10.lineTo(397.5f - mXOffset, 64.95f);
            mAnimPath10.close();
            mPathMap.put(String.valueOf(9), mAnimPath10);
        }

        if (mAnimPath11 == null) {
            mAnimPath11 = new Path();
            mAnimPath11.moveTo(397.5f - mXOffset, 64.95f);
            mAnimPath11.lineTo(510f - mXOffset, 259.8f);
            mAnimPath11.lineTo(547f - mXOffset, 194.85f);
            mAnimPath11.lineTo(472.5f - mXOffset, 64.95f);
            mAnimPath11.close();
            mPathMap.put(String.valueOf(10), mAnimPath11);
        }

        if (mAnimPath12 == null) {
            mAnimPath12 = new Path();
            mAnimPath12.moveTo(472.5f - mXOffset, 64.95f);
            mAnimPath12.lineTo(435f - mXOffset, 129.9f);
            mAnimPath12.lineTo(510f - mXOffset, 129.9f);
            mAnimPath12.lineTo(472.5f - mXOffset, 194.85f);
            mAnimPath12.lineTo(547f - mXOffset, 194.85f);
            mAnimPath12.close();
            mPathMap.put(String.valueOf(11), mAnimPath12);
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

        if (mAlbumTailorBmp != null && !mAlbumTailorBmp.isRecycled()) {
            mAlbumTailorBmp.recycle();
            mAlbumTailorBmp = null;
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
        initData();
        if (pBmp == null) {
            pBmp = mDefAlbumBmp;
        } else {
            Bitmap resizeBmp = Bitmap.createScaledBitmap(pBmp,
                    (int)mAlbumWidth, (int)mAlbumHeight, true);
            if (resizeBmp != pBmp) {
                pBmp.recycle();
                pBmp = resizeBmp;
            }
        }

        myHandler.removeMessages(WAIT_INIT_EVENT);
        mSequenceIndex = 0;
        mTempIndex = -1;

        if (mCurrentBmp == null || mCurrentBmp.isRecycled()) {
            mCurrentBmp = pBmp;
        }

        mBmpForAnim = mCurrentBmp.copy(Config.ARGB_8888, true);

        mAlbumBmp = getAlbumTailorBmp(pBmp, mAlbumPath);

        if (pBmp != mCurrentBmp) {
            if (mCurrentBmp != mDefAlbumBmp) {
                mCurrentBmp.recycle();
            }
            mCurrentBmp = pBmp;
        }
        myHandler.sendEmptyMessage(WAIT_INIT_EVENT);
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
     * getTailorBmp: TODO<br/>
     * 
     * @author wenguan.chen
     * @param pBmp Bitmap
     * @return Bitmap
     * @since MT 1.0
     */
    private Bitmap getAlbumTailorBmp(Bitmap pBmp, Path pPath) {
        mAlbumTailorBmp.eraseColor(Color.TRANSPARENT);
        mCanvas.setBitmap(mAlbumTailorBmp);
        mCanvas.save();
        mCanvas.drawPath(pPath, mPaint);// draw path area
        mPaint.setXfermode(mXfermode);
        mCanvas.drawBitmap(pBmp, 0, 0, mPaint);
        mPaint.setXfermode(null);
        mCanvas.restore();
        return mAlbumTailorBmp;
    }

    /**
     * getDefaultAlbumTailorBmp: TODO<br/>
     * 
     * @author wenguan.chen
     * @param pBmp Bitmap
     * @return Bitmap
     * @since MT 1.0
     */
    private Bitmap getDefaultAlbumTailorBmp(Bitmap pBmp) {
        Bitmap resizeBmp = Bitmap.createScaledBitmap(pBmp, (int)mAlbumWidth,
                (int)mAlbumHeight, true);
        Bitmap canvasBmp = Bitmap.createBitmap((int)mAlbumWidth,
                (int)mAlbumHeight, Config.ARGB_8888);
        mCanvas.setBitmap(canvasBmp);
        mCanvas.save();
        mCanvas.drawPath(mDefaultAlbumPath, mPaint);// draw path area
        mPaint.setXfermode(mXfermode);
        mCanvas.drawBitmap(resizeBmp, 0, 0, mPaint);
        mPaint.setXfermode(null);
        mCanvas.restore();
        resizeBmp.recycle();
        return canvasBmp;
    }

    /**
     * getAnimTailorBmp: TODO<br/>
     * 
     * @author wenguan.chen
     * @param pBmp Bitmap
     * @param pPath Path
     * @return Bitmap
     * @since MT 1.0
     */
    private Bitmap getAnimTailorBmp(Bitmap pBmp, Path pPath) {

        mAnimTailorBmp.eraseColor(Color.TRANSPARENT);
        if (pPath != null && pBmp != null && !pBmp.isRecycled()) {
            mCanvas.setBitmap(mAnimTailorBmp);
            mCanvas.save();
            mCanvas.drawPath(pPath, mPaint);// draw path area
            mPaint.setXfermode(mXfermode);
            mCanvas.drawBitmap(pBmp, 0, 0, mPaint);
            mPaint.setXfermode(null);
            mCanvas.restore();
        }

        mAnimMosaicBmp.eraseColor(Color.TRANSPARENT);
        mCanvas.setBitmap(mAnimMosaicBmp);
        mCanvas.save();
        mCanvas.drawBitmap(mAlbumBmp, 0, 0, null);
        mCanvas.drawBitmap(mAnimTailorBmp, 0, 0, null);
        mCanvas.restore();

        return mAnimMosaicBmp;
    }

    /**
     * image rotate invalidate handler
     */
    Handler myHandler = new Handler() {
        public void handleMessage(Message msg) {
            switch (msg.what) {
            case WAIT_INIT_EVENT:
                if (mSequenceIndex < mSequence.length) {
                    if (mBmpForAnim == null || mBmpForAnim.isRecycled()) {
                        return;
                    }
                    if (mTempIndex != mSequenceIndex) {
                        mAnimBmp = getAnimTailorBmp(mBmpForAnim,
                                mPathMap.get(String
                                        .valueOf(mSequence[mSequenceIndex])));
                        if (mAnimBmp != null && !mAnimBmp.isRecycled()) {
                            mTempIndex = mSequenceIndex;
                            invalidate();
                        }
                    }
                } else {
                    mAnimBmp = getAnimTailorBmp(null, null);
                    invalidate();
                    mBmpForAnim.recycle();
                    mBmpForAnim = null;
                }
                break;
            default:
                break;
            }
        }
    };

}
