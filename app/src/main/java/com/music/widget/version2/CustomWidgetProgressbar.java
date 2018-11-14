
package com.music.widget.version2;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.drawable.Drawable;
import android.os.Handler;
import android.os.Message;
import android.util.AttributeSet;
import android.util.Log;
//import android.view.RemotableViewMethod;
import android.widget.ProgressBar;
import android.widget.RemoteViews.RemoteView;

import com.music.widget.anim.R;

/**
 * ClassName: CustomWidgetProgressbar <br/>
 * Function: TODO ADD FUNCTION. <br/>
 * Date:2014-9-1
 * 
 * @author wenguan.chen
 * @version 0.1
 * @since MT 1.0
 */
@RemoteView
public class CustomWidgetProgressbar extends ProgressBar {
    private static final String TAG = "MusicWidgetProgressbar";
    private boolean mIsPlaying = false;
    private boolean mIsScreenOn = true;
    private boolean mIsLauncherActive = true;

    private static final String LAUNCHER_RESUME = "com.meitu.mobile.widget.WIDGET_START";
    private static final String LAUNCHER_STOP = "com.meitu.mobile.widget.WIDGET_END";

    private Context mContext;

    public CustomWidgetProgressbar(Context context) {
        this(context, null);
    }

    public CustomWidgetProgressbar(Context context, AttributeSet attrs) {
        super(context, attrs, 0);
        mContext = context;
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
        refreshProgress();
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        mContext.unregisterReceiver(mReceiver);
        mRefreshProgress.removeCallbacksAndMessages(null);
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
     * setProgressColor: TODO<br/>
     * 
     * @author wenguan.chen
     * @param pValue int
     * @since MT 1.0
     * @hide
     */
    //@RemotableViewMethod
    public void setProgressColor(int pValue) {
        if (pValue == 0) {
            Drawable dwbGreen = getResources()
                    .getDrawable(R.drawable.music_layer_list_appwidget_progress_green);
            setProgressDrawable(dwbGreen);
        } else if (pValue == 1) {
            Drawable dwbYellow = getResources()
                    .getDrawable(R.drawable.music_layer_list_appwidget_progress_yellow);
            setProgressDrawable(dwbYellow);
        } else if (pValue == 3) {
            Drawable dwbYellow = getResources()
                    .getDrawable(R.drawable.music_layer_list_appwidget_progress_white);
            setProgressDrawable(dwbYellow);
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
    //@RemotableViewMethod
    public void setPlayingState(boolean pIsPlaying) {
        Log.v(TAG, "------->>setPlayingState(" + pIsPlaying + ")");
        mIsPlaying = pIsPlaying;
        refreshProgress();
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
        refreshProgress();
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
        refreshProgress();
    }

    private static final int REFRESH = 1989;

    /**
     * refreshProgress: TODO<br/>
     * 
     * @author wenguan.chen
     * @since MT 1.0
     */
    private void refreshProgress() {
        mRefreshProgress.removeMessages(REFRESH);
        if (mIsPlaying && mIsScreenOn && mIsLauncherActive) {
            if (getProgress() < getMax()) {
                mRefreshProgress.sendMessageDelayed(
                        mRefreshProgress.obtainMessage(REFRESH), 1000);
            } else if (getProgress() >= getMax()) {
                setProgress(0);
                mRefreshProgress.sendMessageDelayed(
                        mRefreshProgress.obtainMessage(REFRESH), 1000);
            }
        }
    }

    private Handler mRefreshProgress = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
            case REFRESH:
                int position = getProgress();
                position++;
                if (getProgress() <= getMax()) {
                    setProgress(position);
                }
                refreshProgress();
                break;
            default:
                break;
            }
        }
    };

}
