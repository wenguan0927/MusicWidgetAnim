
package com.music.widget.version1;

import android.content.Context;
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
import android.view.View;
import android.widget.RemoteViews.RemoteView;
import android.os.Handler;
import android.os.Message;
import android.util.AttributeSet;

import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;

import com.music.widget.anim.R;

/**
 * ClassName: MeituAppWidgetRotateImageView <br/>
 * Function: custom view to support remoteView in appwidget for image rotate
 * animation. <br/>
 * Date:2013-11-9
 * 
 * @author wenguan.chen
 * @version 0.1
 * @since MT 1.0
 */
@RemoteView
public class MusicWidgetRotateImageView extends View {
    private final String TAG="MusicWidgetRotateImageView";

    private int mAlbumWidth = 515;
    private int mAlbumHeight = 515;

    private float mStartPointX = 74f;
    private float mStartPointY = 101f;

    private DrawFilter mDrawFilter;

    // rotate angle array
    private int[] mSequence = {
            0, -5, -10, -9, -6, -1, 6, 15, 26, 39, 52, 63, 72, 79, 84, 87, 89,
            90
    };

    private int mSequenceIndex = 0;

    public static final int WAIT_INIT_EVENT = 1989;

    private Paint mPaint;

    private Path mRotatePath;

    private Xfermode mXfermode;

    private Bitmap mCurrentBmp;

    private long mAudioId = -1;

    private boolean mShouldRotate = false;

    private Bitmap mMosaicBmp;

    private Bitmap mRotateBmp;

    private Bitmap mTailorBmp;

    private Bitmap mAnimBmp;

    private Bitmap mTempBmp;

    private Bitmap mDefAlbumBmp;

    private int mTempIndex = -1;

    private Canvas mCanvas;

    /**
     * @param context
     */
    public MusicWidgetRotateImageView(Context context) {
        super(context);
    }

    /**
     * @param context
     * @param attrs
     */
    public MusicWidgetRotateImageView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    /**
     * @param context
     * @param attrs
     * @param defStyle
     */
    public MusicWidgetRotateImageView(Context context, AttributeSet attrs,
            int defStyle) {
        super(context, attrs, defStyle);
        initData();
    }

    /**
     * initData: init data<br/>
     * 
     * @author wenguan.chen
     * @since MT 1.0
     */
    private void initData() {
        if (mPaint == null) {
            mPaint = new Paint();
            mPaint.setColor(Color.WHITE);
            mPaint.setDither(true);
            mPaint.setAntiAlias(true);
        }

        if (mDrawFilter == null) {
            mDrawFilter = new PaintFlagsDrawFilter(0, Paint.ANTI_ALIAS_FLAG
                    | Paint.FILTER_BITMAP_FLAG);
        }

        if (mRotatePath == null) {
            mRotatePath = new Path();
            mRotatePath.moveTo(20, 0);
            mRotatePath.lineTo(257f, 469f);
            mRotatePath.lineTo(494, 0);
            mRotatePath.close();
        }

        if (mXfermode == null) {
            mXfermode = new PorterDuffXfermode(PorterDuff.Mode.SRC_IN);
        }

        if(mCanvas == null){
            mCanvas = new Canvas();
        }

        initBmp();
    }

    /**
     * initBmp: TODO<br/>
     * 
     * @author wenguan.chen
     * @since MT 1.0
     */
    private void initBmp() {
        if (mDefAlbumBmp == null || mDefAlbumBmp.isRecycled()) {
            mDefAlbumBmp = BitmapFactory
                    .decodeResource(
                            getResources(),
                            R.drawable.ic_appwidget_albumart_default);
            Bitmap resizeBmp = Bitmap.createScaledBitmap(mDefAlbumBmp,
                    mAlbumWidth, mAlbumHeight, true);
            if (mDefAlbumBmp != resizeBmp) {
                mDefAlbumBmp.recycle();
                mDefAlbumBmp = resizeBmp;
            }
        }
        if (mRotateBmp == null || mRotateBmp.isRecycled()) {
            mRotateBmp = Bitmap.createBitmap(mAlbumWidth, mAlbumHeight,
                    Config.ARGB_8888);
        }
        if (mTailorBmp == null || mTailorBmp.isRecycled()) {
            mTailorBmp = Bitmap.createBitmap(mAlbumWidth, mAlbumHeight,
                    Config.ARGB_8888);
        }
        if (mTempBmp == null || mTempBmp.isRecycled()) {
            mTempBmp = Bitmap.createBitmap(mAlbumWidth, mAlbumHeight * 2,
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
        if (mDefAlbumBmp != null && !mDefAlbumBmp.isRecycled()) {
            mDefAlbumBmp.recycle();
            mDefAlbumBmp = null;
        }
        if (mRotateBmp != null && !mRotateBmp.isRecycled()) {
            mRotateBmp.recycle();
            mRotateBmp = null;
          
        }
        if (mTempBmp != null && !mTempBmp.isRecycled()) {
            mTempBmp.recycle();
            mTempBmp = null;
        }
    }

    /**
     * setAlbumArtBitmap: init rotate albumArt bitmap<br/>
     * 
     * @author wenguan.chen
     * @param pBmp Bitmap
     * @since MT 1.0
     * @hide
     */
    //@android.view.RemotableViewMethod
    public void setAlbumArtBitmap(Bitmap pBmp) {
        if (!mShouldRotate) {
            return;
        }
        initBmp();
        if (pBmp == null) {
            pBmp = mDefAlbumBmp;
        } else {
            Bitmap resizeBmp = Bitmap.createScaledBitmap(pBmp, mAlbumWidth,
                    mAlbumHeight, true);
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

        mMosaicBmp = mosaicBitmap(pBmp, mCurrentBmp);

        if (pBmp != mCurrentBmp) {
            if (mCurrentBmp != mDefAlbumBmp) {
                mCurrentBmp.recycle();
            }
            mCurrentBmp = pBmp;
        }

        Message msg = myHandler.obtainMessage(WAIT_INIT_EVENT);
        myHandler.sendMessage(msg);
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
        if (mAudioId == pAudioId) {
            mShouldRotate = false;
        } else {
            mAudioId = pAudioId;
            mShouldRotate = true;
        }
        if (mAudioId == 0) {
            mAudioId = -1;
        }
    }

    /**
     * image rotate invalidate handler
     */
    Handler myHandler = new Handler() {
        public void handleMessage(Message msg) {
            switch (msg.what) {
            case WAIT_INIT_EVENT:
                if (mSequenceIndex < mSequence.length) {
                    if (mMosaicBmp == null || mMosaicBmp.isRecycled()) {
                        return;
                    }
                    if (mTempIndex != mSequenceIndex) {
                        mAnimBmp = rotateBmp(mMosaicBmp,
                                mSequence[mSequenceIndex]);
                        if (mAnimBmp != null && !mAnimBmp.isRecycled()) {
                            mTempIndex = mSequenceIndex;
                            invalidate();
                        }
                    }
                    Message msg1 = myHandler.obtainMessage(WAIT_INIT_EVENT);
                    myHandler.removeMessages(WAIT_INIT_EVENT);
                    myHandler.sendMessageDelayed(msg1, 4);
                }
            default:
                break;
            }
        }
    };

    /**
     * setAlbumWidth: set album image bitmap width<br/>
     * 
     * @author wenguan.chen
     * @param pWidth int
     * @since MT 1.0
     * @hide
     */
    //@android.view.RemotableViewMethod
    public void setAlbumWidth(int pWidth) {
        mAlbumWidth = pWidth;
    }

    /**
     * setAlbumHeight: set album image height<br/>
     * 
     * @author wenguan.chen
     * @param pHeight int
     * @since MT 1.0
     * @hide
     */
    //@android.view.RemotableViewMethod
    public void setAlbumHeight(int pHeight) {
        mAlbumHeight = pHeight;
    }

    /**
     * setPointX: set draw bitmap start point in X coordinate<br/>
     * 
     * @author wenguan.chen
     * @param pPointX Float
     * @since MT 1.0
     * @hide
     */
    //@android.view.RemotableViewMethod
    public void setPointX(Float pPointX) {
        mStartPointX = pPointX;
    }

    /**
     * setPointY: set draw bitmap start point in Y coordinate<br/>
     * 
     * @author wenguan.chen
     * @param pPointY Float
     * @since MT 1.0
     * @hide
     */
    //@android.view.RemotableViewMethod
    public void setPointY(Float pPointY) {
        mStartPointY = pPointY;
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
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
        initData();
        if (mAudioId < 0) {
            setAudioId(0);// init View
            setAlbumArtBitmap(null);
        }
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        recycleBmp();
        myHandler.removeMessages(WAIT_INIT_EVENT);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        canvas.setDrawFilter(mDrawFilter); // eliminate the aliasing effect
        if ((mSequenceIndex < mSequence.length) && mAnimBmp != null
                && !mAnimBmp.isRecycled()) {
            canvas.drawBitmap(mAnimBmp, mStartPointX, mStartPointY, null);
            mSequenceIndex++;
        }
        if (mSequenceIndex == mSequence.length && mAnimBmp != null
                && !mAnimBmp.isRecycled()) {
            canvas.drawBitmap(mAnimBmp, mStartPointX, mStartPointY, null);
        }
    }

    /**
     * rotateBmp: TODO<br/>
     * 
     * @author wenguan.chen
     * @param pBmp Bitmap
     * @param pRotateValue int
     * @return Bitmap
     * @since MT 1.0
     */
    private Bitmap rotateBmp(Bitmap pBmp, int pRotateValue) {
        if (pBmp == null || pBmp.isRecycled()) {
            return null;
        }
        initData();
        mCanvas.setBitmap(mRotateBmp);
        mCanvas.setDrawFilter(mDrawFilter); // eliminate the aliasing effect
        mCanvas.save(Canvas.ALL_SAVE_FLAG);
        mCanvas.translate(0, -pBmp.getHeight() / 2);
        mCanvas.rotate(pRotateValue, 0, pBmp.getHeight() / 2);
        mCanvas.drawBitmap(pBmp, 0, 0, null);
        mCanvas.restore();

        mTailorBmp.eraseColor(Color.TRANSPARENT);
        mCanvas.setBitmap(mTailorBmp);
        mCanvas.save();
        mCanvas.drawPath(mRotatePath, mPaint);// draw triangle area
        mPaint.setXfermode(mXfermode);
        mCanvas.drawBitmap(mRotateBmp, 0, 0, mPaint);
        mPaint.setXfermode(null);
        mCanvas.restore();
        return mTailorBmp;
    }

    /**
     * mosaicBitmap: mosaic two bitmap to one for appwidget albumArt rotate<br/>
     * 
     * @author wenguan.chen
     * @param pFirstBmp Bitmap
     * @param pSecondBmp Bitmap
     * @return Bitmap
     * @since MT 1.0
     */
    private Bitmap mosaicBitmap(Bitmap pFirstBmp, Bitmap pSecondBmp) {
        if (pFirstBmp == null || pFirstBmp.isRecycled() || pSecondBmp == null
                || pSecondBmp.isRecycled()) {
            return null;
        }
        initBmp();
        mCanvas.setBitmap(mTempBmp);
        mCanvas.save(Canvas.ALL_SAVE_FLAG);
        mCanvas.rotate(270, (float)mAlbumWidth / 2, (float)mAlbumHeight / 2);
        mCanvas.drawBitmap(pFirstBmp, 0, 0, null);
        mCanvas.restore();
        mCanvas.save();
        mCanvas.drawBitmap(pSecondBmp, 0, mAlbumHeight, null);
        mCanvas.restore();
        return mTempBmp;
    }
}
