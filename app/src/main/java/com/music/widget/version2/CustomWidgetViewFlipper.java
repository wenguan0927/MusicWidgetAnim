
package com.music.widget.version2;

import android.content.Context;
import android.graphics.Color;
import android.util.AttributeSet;
import android.util.Log;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.AnimationSet;
import android.view.animation.TranslateAnimation;
import android.widget.LinearLayout;
import android.widget.RemoteViews.RemoteView;
import android.widget.TextView;
import android.widget.ViewFlipper;
import android.text.TextUtils;

/**
 * ClassName: MusicWidgetViewFlipper <br/>
 * Function: use for music appwidget special<br/>
 * Simple {@link ViewFlipper} that will animate between two or more views that
 * have been added to it. Only one child is shown at a time. If requested, can
 * automatically flip between each child at a regular interval. Date:2013-12-3
 * 
 * @author wenguan.chen
 * @version 0.1
 * @since MT 1.0
 * @hide
 */
@RemoteView
public class CustomWidgetViewFlipper extends ViewFlipper {
    private static final String TAG = "MusicWidgetViewFlipper";

    public static final String LEFTTORIGHT = "left_to_right";

    public static final String RIGHTTOLEFT = "right_to_left";

    public static final String DEFAULT = "default";

    private AnimationSet mAnimSetSlideRightIn;

    private AnimationSet mAnimSetSlideRightOut;

    private AnimationSet mAnimSetSlideLeftIn;

    private AnimationSet mAnimSetSlideLeftOut;

    private AnimationSet mAnimSetFadeIn;

    private AnimationSet mAnimSetFadeOut;

    private TextView mTitle01OneTxt;

    private TextView mTitle02OneTxt;

    private TextView mTitle01TwoTxt;

    private TextView mTitle02TwoTxt;
    
    private int mBigTitleColor = Color.WHITE;

    private int mSecondTitleColor = Color.argb(179, 255, 255, 255);

    private Context mContext;
    /**
     * @param pContext
     */
    public CustomWidgetViewFlipper(Context pContext) {
        this(pContext, null);
    }

    /**
     * @param pContext
     * @param pAttrs
     */
    public CustomWidgetViewFlipper(Context pContext, AttributeSet pAttrs) {
        super(pContext, pAttrs);
        mContext = pContext;
        initViewPage();
    }

    /**
     * initViewPage:init two page to show text switch animation<br/>
     * 
     * @author wenguan.chen
     * @since MT 1.0
     */
    private void initViewPage() {
        LinearLayout.LayoutParams lParams = new LinearLayout.LayoutParams(
                LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT);
        LinearLayout lineLayoutOne = new LinearLayout(mContext);
        lineLayoutOne.setLayoutParams(lParams);
        lineLayoutOne.setOrientation(LinearLayout.VERTICAL);
        lineLayoutOne.setVisibility(View.VISIBLE);
        mTitle01OneTxt = new TextView(mContext);
        mTitle01OneTxt.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 25);
        mTitle01OneTxt.setGravity(Gravity.CENTER);
        mTitle01OneTxt.setTextColor(mBigTitleColor);
        mTitle01OneTxt.setSingleLine(true);
        mTitle01OneTxt.setEllipsize(TextUtils.TruncateAt.valueOf("END"));
        mTitle02OneTxt = new TextView(mContext);
        mTitle02OneTxt.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 15);
        mTitle02OneTxt.setTextColor(mSecondTitleColor);
        mTitle02OneTxt.setGravity(Gravity.CENTER);
        mTitle02OneTxt.setSingleLine(true);
        mTitle02OneTxt.setEllipsize(TextUtils.TruncateAt.valueOf("END"));
        lineLayoutOne.addView(mTitle01OneTxt, lParams);
        lineLayoutOne.addView(mTitle02OneTxt, lParams);
        addView(lineLayoutOne);

        LinearLayout lineLayoutTwo = new LinearLayout(mContext);
        lineLayoutTwo.setLayoutParams(lParams);
        lineLayoutTwo.setOrientation(LinearLayout.VERTICAL);
        lineLayoutTwo.setVisibility(View.VISIBLE);
        mTitle01TwoTxt = new TextView(mContext);
        mTitle01TwoTxt.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 25);
        mTitle01TwoTxt.setGravity(Gravity.CENTER);
        mTitle01TwoTxt.setTextColor(mBigTitleColor);
        mTitle01TwoTxt.setSingleLine(true);
        mTitle01TwoTxt.setEllipsize(TextUtils.TruncateAt.valueOf("END"));
        mTitle02TwoTxt = new TextView(mContext);
        mTitle02TwoTxt.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 15);
        mTitle02TwoTxt.setTextColor(mSecondTitleColor);
        mTitle02TwoTxt.setGravity(Gravity.CENTER);
        mTitle02TwoTxt.setSingleLine(true);
        mTitle02TwoTxt.setEllipsize(TextUtils.TruncateAt.valueOf("END"));
        lineLayoutTwo.addView(mTitle01TwoTxt, lParams);
        lineLayoutTwo.addView(mTitle02TwoTxt, lParams);
        addView(lineLayoutTwo);
    }

    /**
     * setShowContent: TODO<br/>
     * 
     * @author wenguan.chen
     * @param pStrContent String
     * @since MT 1.0
     * @hide
     */
    //@android.view.RemotableViewMethod
    public void setShowContent(String pStrContent) {
        Log.v(TAG, "------->>setShowContent(" + pStrContent + ")");
        String[] lArrContent = pStrContent.split(",");
        int lIntDisplayChild = getDisplayedChild();
        if (lIntDisplayChild == 0) {
            if (mTitle01OneTxt.getText().equals("")
                    && mTitle02OneTxt.getText().equals("")) {
                mTitle01OneTxt.setText(lArrContent[0]);
                mTitle02OneTxt.setText(lArrContent[1]);
            } else if (!(mTitle01OneTxt.getText().equals(lArrContent[0]) && mTitle02OneTxt
                    .getText().equals(lArrContent[1]))) {
                mTitle01TwoTxt.setText(lArrContent[0]);
                mTitle02TwoTxt.setText(lArrContent[1]);
                setDisplayedChild(1);
            }
        } else if (lIntDisplayChild == 1) {
            if (mTitle01TwoTxt.getText().equals("")
                    && mTitle02TwoTxt.getText().equals("")) {
                mTitle01TwoTxt.setText(lArrContent[0]);
                mTitle02TwoTxt.setText(lArrContent[1]);
            } else if (!(mTitle01TwoTxt.getText().equals(lArrContent[0]) && mTitle02TwoTxt
                    .getText().equals(lArrContent[1]))) {
                mTitle01OneTxt.setText(lArrContent[0]);
                mTitle02OneTxt.setText(lArrContent[1]);
                setDisplayedChild(0);
            }
        }
    }

    /**
     * setBigTitleColor: TODO<br/>
     * 
     * @author wenguan.chen
     * @param pColor int
     * @since MT 1.0
     * @hide
     */
    //@android.view.RemotableViewMethod
    public void setBigTitleColor(int pColor) {
        mTitle01OneTxt.setTextColor(pColor);
        mTitle01TwoTxt.setTextColor(pColor);
    }

    /**
     * setSmallTitleColor: TODO<br/>
     * 
     * @author wenguan.chen
     * @param pColor int
     * @since MT 1.0
     * @hide
     */
    //@android.view.RemotableViewMethod
    public void setSmallTitleColor(int pColor) {
        mTitle02OneTxt.setTextColor(pColor);
        mTitle02TwoTxt.setTextColor(pColor);
    }

    /**
     * switchAnimation: TODO<br/>
     * 
     * @author wenguan.chen
     * @param pStrDirection String
     * @since MT 1.0
     * @hide
     */
    //@android.view.RemotableViewMethod
    public void setAnimationMode(String pStrDirection) {
        Log.v(TAG, "------->>setAnimationMode(" + pStrDirection + ")");
        if (pStrDirection.equals(LEFTTORIGHT)) {
            setInAnimation(slideLeftIn());
            setOutAnimation(slideRightOut());
        } else if (pStrDirection.equals(RIGHTTOLEFT)) {
            setInAnimation(slideRightIn());
            setOutAnimation(slideLeftOut());
        } else if (pStrDirection.equals(DEFAULT)) {
            setInAnimation(fadeIn());
            setOutAnimation(fadeOut());
        }
    }

    /**
     * slideLeftIn: TODO<br/>
     * 
     * @author wenguan.chen
     * @return Animation
     * @since MT 1.0
     */
    private Animation slideLeftIn() {
        if (mAnimSetSlideLeftIn == null) {
            mAnimSetSlideLeftIn = new AnimationSet(false);
            Animation inFromLeft = new TranslateAnimation(
                    Animation.RELATIVE_TO_PARENT, -1.0f,
                    Animation.RELATIVE_TO_PARENT, 0.0f,
                    Animation.RELATIVE_TO_PARENT, 0.0f,
                    Animation.RELATIVE_TO_PARENT, 0.0f);
            inFromLeft.setDuration(700);
            inFromLeft.setInterpolator(new AccelerateDecelerateInterpolator());
            mAnimSetSlideLeftIn.addAnimation(inFromLeft);
            AlphaAnimation alphaAnim = new AlphaAnimation(0, 1);
            alphaAnim.setDuration(700);
            mAnimSetSlideLeftIn.addAnimation(alphaAnim);
        }
        return mAnimSetSlideLeftIn;
    }

    /**
     * slideRightOut: TODO<br/>
     * 
     * @author wenguan.chen
     * @return Animation
     * @since MT 1.0
     */
    private Animation slideRightOut() {
        if (mAnimSetSlideRightOut == null) {
            mAnimSetSlideRightOut = new AnimationSet(false);
            Animation outToRight = new TranslateAnimation(
                    Animation.RELATIVE_TO_PARENT, 0.0f,
                    Animation.RELATIVE_TO_PARENT, +1.0f,
                    Animation.RELATIVE_TO_PARENT, 0.0f,
                    Animation.RELATIVE_TO_PARENT, 0.0f);
            outToRight.setDuration(700);
            outToRight.setInterpolator(new AccelerateDecelerateInterpolator());
            mAnimSetSlideRightOut.addAnimation(outToRight);
            AlphaAnimation alphaAnim = new AlphaAnimation(1, 0);
            alphaAnim.setDuration(700);
            mAnimSetSlideRightOut.addAnimation(alphaAnim);
        }
        return mAnimSetSlideRightOut;
    }

    /**
     * slideLeftOut: TODO<br/>
     * 
     * @author wenguan.chen
     * @return Animation
     * @since MT 1.0
     */
    private Animation slideLeftOut() {
        if (mAnimSetSlideLeftOut == null) {
            mAnimSetSlideLeftOut = new AnimationSet(false);
            Animation outToLeft = new TranslateAnimation(
                    Animation.RELATIVE_TO_PARENT, 0.0f,
                    Animation.RELATIVE_TO_PARENT, -1.0f,
                    Animation.RELATIVE_TO_PARENT, 0.0f,
                    Animation.RELATIVE_TO_PARENT, 0.0f);
            outToLeft.setDuration(700);
            outToLeft.setInterpolator(new AccelerateDecelerateInterpolator());
            mAnimSetSlideLeftOut.addAnimation(outToLeft);
            AlphaAnimation alphaAnim = new AlphaAnimation(1, 0);
            alphaAnim.setDuration(700);
            mAnimSetSlideLeftOut.addAnimation(alphaAnim);
        }
        return mAnimSetSlideLeftOut;
    }

    /**
     * slideRightIn: TODO<br/>
     * 
     * @author wenguan.chen
     * @return Animation
     * @since MT 1.0
     */
    private Animation slideRightIn() {
        if (mAnimSetSlideRightIn == null) {
            mAnimSetSlideRightIn = new AnimationSet(false);
            Animation inFromRight = new TranslateAnimation(
                    Animation.RELATIVE_TO_PARENT, +1.0f,
                    Animation.RELATIVE_TO_PARENT, 0.0f,
                    Animation.RELATIVE_TO_PARENT, 0.0f,
                    Animation.RELATIVE_TO_PARENT, 0.0f);
            inFromRight.setDuration(700);
            inFromRight.setInterpolator(new AccelerateDecelerateInterpolator());
            mAnimSetSlideRightIn.addAnimation(inFromRight);
            AlphaAnimation alphaAnim = new AlphaAnimation(0, 1);
            alphaAnim.setDuration(700);
            mAnimSetSlideRightIn.addAnimation(alphaAnim);
        }
        return mAnimSetSlideRightIn;
    }

    /**
     * fadeIn: TODO<br/>
     * 
     * @author wenguan.chen
     * @return Animation
     * @since MT 1.0
     */
    private Animation fadeIn() {
        if (mAnimSetFadeIn == null) {
            mAnimSetFadeIn = new AnimationSet(false);
            AlphaAnimation alphaAnim = new AlphaAnimation(0, 1);
            alphaAnim.setDuration(700);
            mAnimSetFadeIn.addAnimation(alphaAnim);
        }
        return mAnimSetFadeIn;
    }

    /**
     * fadeOut: TODO<br/>
     * 
     * @author wenguan.chen
     * @return Animation
     * @since MT 1.0
     */
    private Animation fadeOut() {
        if (mAnimSetFadeOut == null) {
            mAnimSetFadeOut = new AnimationSet(false);
            AlphaAnimation alphaAnim = new AlphaAnimation(1, 0);
            alphaAnim.setDuration(700);
            mAnimSetFadeOut.addAnimation(alphaAnim);
        }
        return mAnimSetFadeOut;
    }
}
