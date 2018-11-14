
package com.music.widget.version2;

import java.util.HashMap;
import java.util.Random;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Bitmap.Config;

/**
 * ClassName: PrismFlickerTool <br/>
 * Function: TODO ADD FUNCTION. <br/>
 * Date:2014-7-18
 * 
 * @author wenguan.chen
 * @version 0.1
 * @since MT 1.0
 */
class PrismFlickerTool {

    Random mRandom;

    private Paint mTrianglePaint;

    private Path mPathOne;

    private Path mPathTwo;

    private HashMap<String, TriangleNode> mTriangleNodeMap = new HashMap<String, TriangleNode>();

    private int mColor;

    private int mAlpha;

    public static PrismFlickerTool mInstance;

    private float mViewWidth = 720;

    private Bitmap mTriangleBmp;

    private Bitmap mTriangleBmpOne;

    private Bitmap mTriangleBmpTwo;

    /**
     * getInstance: TODO<br/>
     * 
     * @author wenguan.chen
     * @return Sprinkle
     * @since MT 1.0
     */
    public static PrismFlickerTool getInstance() {
        if (mInstance == null) {
            mInstance = new PrismFlickerTool();
        }
        return mInstance;
    }

    public PrismFlickerTool() {
        mRandom = new Random();
        mTrianglePaint = new Paint();
        mTrianglePaint.setColor(Color.argb(75, 224, 255, 255));
        mTrianglePaint.setAlpha(160);
        mTrianglePaint.setDither(true);
        mTrianglePaint.setAntiAlias(true);
        initBitmap();
        initNodeData();
    }

    /**
     * initBitmap: TODO<br/>
     * 
     * @author wenguan.chen
     * @since MT 1.0
     */
    private void initBitmap() {
        if (mPathOne == null) {
            mPathOne = new Path();
            mPathOne.moveTo(0, 0);
            mPathOne.lineTo(36.5f, 64f);
            mPathOne.lineTo(73.5f, 0);
            mPathOne.close();
        }

        if (mPathTwo == null) {
            mPathTwo = new Path();
            mPathTwo.moveTo(37f, 0);
            mPathTwo.lineTo(74f, 64f);
            mPathTwo.lineTo(0, 64f);
            mPathTwo.close();
        }

        if (mTriangleBmp == null || mTriangleBmp.isRecycled()) {
            mTriangleBmp = Bitmap.createBitmap(75, 65, Config.ARGB_8888);
        }

        if (mTriangleBmpOne == null || mTriangleBmpOne.isRecycled()) {
            mTriangleBmpOne = getPathBmp(mPathOne, mTrianglePaint);
        }

        if (mTriangleBmpTwo == null || mTriangleBmpTwo.isRecycled()) {
            mTriangleBmpTwo = getPathBmp(mPathTwo, mTrianglePaint);
        }

    }

    /**
     * setColor: TODO<br/>
     * 
     * @author wenguan.chen
     * @param pColor int
     * @since MT 1.0
     */
    public void setColor(int pColor) {
        mColor = pColor;
        mTrianglePaint.setColor(mColor);
        initBitmap();
    }

    /**
     * setAlpha: TODO<br/>
     * 
     * @author wenguan.chen
     * @param pAlpha int
     * @since MT 1.0
     */
    public void setAlpha(int pAlpha) {
        mAlpha = pAlpha;
        mTrianglePaint.setAlpha(mAlpha);
        initBitmap();
    }

    /**
     * setViewWidth: TODO<br/>
     * 
     * @author wenguan.chen
     * @param pWidth float
     * @since MT 1.0
     */
    public void setViewWidth(float pWidth) {
        mViewWidth = pWidth;
        initNodeData();
    }

    /**
     * initNodeData: TODO<br/>
     * 
     * @author wenguan.chen
     * @since MT 1.0
     */
    public void initNodeData() {
        mTriangleNodeMap.clear();
        TriangleNode node = null;
        float Xoffset = (float)(360 - mViewWidth / 2);
        for (int i = 0; i < 54; i++) {
            if (i >= 0 && i < 7) {
                node = new TriangleNode(i, (float)(210f + 37.5f * i - Xoffset),
                        64.95f);
                if (i % 2 == 0) {
                    node.setType(0);
                } else {
                    node.setType(1);
                }
            } else if (i >= 7 && i < 16) {
                node = new TriangleNode(i,
                        (float)(172f + 37.5f * (i - 7) - Xoffset), 129.9f);
                if (i % 2 == 0) {
                    node.setType(1);
                } else {
                    node.setType(0);
                }
            } else if (i >= 16 && i < 27) {
                node = new TriangleNode(i,
                        (float)(134.5f + 37.5f * (i - 16) - Xoffset), 194.85f);
                if (i % 2 == 0) {
                    node.setType(0);
                } else {
                    node.setType(1);
                }
            } else if (i >= 27 && i < 38) {
                node = new TriangleNode(i,
                        (float)(134.5f + 37.5f * (i - 27) - Xoffset), 259.8f);
                if (i % 2 == 0) {
                    node.setType(0);
                } else {
                    node.setType(1);
                }
            } else if (i >= 38 && i < 47) {
                node = new TriangleNode(i,
                        (float)(172f + 37.5f * (i - 38) - Xoffset), 324.75f);
                if (i % 2 == 0) {
                    node.setType(1);
                } else {
                    node.setType(0);
                }
            } else if (i >= 47 && i < 54) {
                node = new TriangleNode(i,
                        (float)(209.5f + 37.5f * (i - 47) - Xoffset), 389.7f);
                if (i % 2 == 0) {
                    node.setType(0);
                } else {
                    node.setType(1);
                }
            }

            mTriangleNodeMap.put(String.valueOf(i), node);
        }
    }

    /**
     * getNodes: TODO<br/>
     * 
     * @author wenguan.chen
     * @return HashMap<String, TriangleNode>
     * @since MT 1.0
     */
    public HashMap<String, TriangleNode> getTriangleNodes() {
        return mTriangleNodeMap;
    }

    /**
     * createRandomArray: TODO<br/>
     * 
     * @author wenguan.chen
     * @param pSize int
     * @return int[]
     * @since MT 1.0
     */
    public int[] getRandomArray(int pSize) {
        int[] list = new int[pSize];
        int count = 0;
        int num = 0;
        int i;
        for (i = 0; i < pSize; i++) {
            list[i] = -1;
        }
        do {
            num = (int)(Math.random() * 54);
            for (i = 0; i < pSize; i++) {
                if (list[i] == num) {
                    break;
                }
            }
            if (i >= list.length) {
                list[count] = num;
                count++;
            }
        } while (count < pSize);

        return list;
    }

    /**
     * getPathBmp: TODO<br/>
     * 
     * @author wenguan.chen
     * @param pPath Path
     * @return Bitmap
     * @since MT 1.0
     */
    private Bitmap getPathBmp(Path pPath, Paint pPaint) {
        Bitmap bmp = Bitmap.createBitmap(75, 65, Config.ARGB_8888);
        Canvas canvas = new Canvas();
        canvas.setBitmap(bmp);
        canvas.drawPath(pPath, pPaint);
        return bmp;
    }

    /**
     * ClassName: TriangleNode <br/>
     * Function: TODO ADD FUNCTION. <br/>
     * Date:2014-7-18
     * 
     * @author wenguan.chen
     * @version 0.1
     * @since MT 1.0
     */
    public class TriangleNode {
        private int mIndex;

        private float mXCoordinate;

        private float mYCoordinate;

        private int mAlpha;

        private int mColor = Color.rgb(224, 255, 255);

        private int mType = 0;

        public TriangleNode(int pIndex, float pX, float pY) {
            this.mIndex = pIndex;
            this.mXCoordinate = pX;
            this.mYCoordinate = pY;
        }

        /**
         * copy: TODO<br/>
         * 
         * @author wenguan.chen
         * @return TriangleNode
         * @since MT 1.0
         */
        public TriangleNode copy() {
            TriangleNode node = new TriangleNode(mIndex, mXCoordinate,
                    mYCoordinate);
            node.setType(mType);
            return node;
        }

        /**
         * draw: TODO<br/>
         * 
         * @author wenguan.chen
         * @param pCanvas Canvas
         * @since MT 1.0
         */
        public void drawTriangle(Canvas pCanvas) {
            if (mType == 0) {
                pCanvas.drawBitmap(mTriangleBmpTwo, mXCoordinate, mYCoordinate,
                        mTrianglePaint);
            } else if (mType == 1) {
                pCanvas.drawBitmap(mTriangleBmpOne, mXCoordinate, mYCoordinate,
                        mTrianglePaint);
            }

        }

        /**
         * getIndex: TODO<br/>
         * 
         * @author wenguan.chen
         * @return int
         * @since MT 1.0
         */
        public int getIndex() {
            return mIndex;
        }

        /**
         * setIndex: TODO<br/>
         * 
         * @author wenguan.chen
         * @param pIndex int
         * @since MT 1.0
         */
        public void setIndex(int pIndex) {
            this.mIndex = pIndex;
        }

        /**
         * getXCoordinate: TODO<br/>
         * 
         * @author wenguan.chen
         * @return float
         * @since MT 1.0
         */
        public float getXCoordinate() {
            return mXCoordinate;
        }

        /**
         * setXCoordinate: TODO<br/>
         * 
         * @author wenguan.chen
         * @param pXCoordinate float
         * @since MT 1.0
         */
        public void setXCoordinate(float pXCoordinate) {
            this.mXCoordinate = pXCoordinate;
        }

        /**
         * getYCoordinate: TODO<br/>
         * 
         * @author wenguan.chen
         * @return float
         * @since MT 1.0
         */
        public float getYCoordinate() {
            return mYCoordinate;
        }

        /**
         * setYCoordinate: TODO<br/>
         * 
         * @author wenguan.chen
         * @param pYCoordinate float
         * @since MT 1.0
         */
        public void setYCoordinate(float pYCoordinate) {
            this.mYCoordinate = pYCoordinate;
        }

        /**
         * getAlpha: TODO<br/>
         * 
         * @author wenguan.chen
         * @return int
         * @since MT 1.0
         */
        public int getAlpha() {
            return mAlpha;
        }

        /**
         * setAlpha: TODO<br/>
         * 
         * @author wenguan.chen
         * @param pAlpha int
         * @since MT 1.0
         */
        public void setAlpha(int pAlpha) {
            this.mAlpha = pAlpha;
        }

        /**
         * getColor: TODO<br/>
         * 
         * @author wenguan.chen
         * @return int
         * @since MT 1.0
         */
        public int getColor() {
            return mColor;
        }

        /**
         * setColor: TODO<br/>
         * 
         * @author wenguan.chen
         * @param pColor int
         * @since MT 1.0
         */
        public void setColor(int pColor) {
            this.mColor = pColor;
        }

        /**
         * getType: TODO<br/>
         * 
         * @author wenguan.chen
         * @return int
         * @since MT 1.0
         */
        public int getType() {
            return mType;
        }

        /**
         * setType: TODO<br/>
         * 
         * @author wenguan.chen
         * @param pType int
         * @since MT 1.0
         */
        public void setType(int pType) {
            this.mType = pType;
        }

    }
}
