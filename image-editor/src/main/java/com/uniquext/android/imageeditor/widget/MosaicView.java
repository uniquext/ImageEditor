package com.uniquext.android.imageeditor.widget;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PointF;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.RectF;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.renderscript.Allocation;
import android.renderscript.RenderScript;
import android.renderscript.ScriptIntrinsicBlur;
import android.support.annotation.Nullable;
import android.support.v7.widget.AppCompatImageView;
import android.util.AttributeSet;
import android.view.MotionEvent;

import com.uniquext.android.imageeditor.util.ImageUtils;

import java.util.Iterator;
import java.util.Stack;

import static android.graphics.Canvas.ALL_SAVE_FLAG;

/**
 * @author penghaitao
 * @version 1.0
 * @date 2018/7/17 17:42
 * @description 马赛克
 */
public class MosaicView extends AppCompatImageView {
    /**
     * 轨迹点颜色值
     */
    private static final int BRUSH_BORDER_COLOR = 0xFFFFFFFF;
    /**
     * 画笔宽度
     */
    private float mBrushWidth = 1f;
    /**
     * 模糊度
     */
    private float mRate = 1f;

    /**
     * 显示区域
     */
    private RectF mDrawableRectF = new RectF();
    /**
     * 最后一个触摸点
     */
    private PointF mLastPointer = new PointF();
    /**
     * 当前路径
     */
    private DrawablePath mCurrentPath = null;
    /**
     * 总路径
     */
    private Stack<DrawablePath> mDrawablePathStack = new Stack<>();
    /**
     * 原始资源图
     */
    private Bitmap mSourceBitmap = null;
    /**
     * 模糊度是否有变化
     * 复用bitmap
     */
    private boolean mIsRateChanged = false;
    /**
     * 马赛克改变监听
     */
    private OnMosaicChangeListener mMosaicChangeListener = null;
    /**
     * 轨迹点
     */
    private Paint mBrushPaint = new Paint();
    /**
     * 是否隐藏轨迹点
     */
    private boolean mIsHiddenBrush = false;

    public MosaicView(Context context) {
        this(context, null);
    }

    public MosaicView(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public MosaicView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        float density = getContext().getResources().getDisplayMetrics().density;
        mBrushPaint.setColor(BRUSH_BORDER_COLOR);
        mBrushPaint.setStrokeWidth(1 * density);
        mBrushPaint.setStyle(Paint.Style.STROKE);
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
    }

    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        super.onLayout(changed, left, top, right, bottom);
        if (getDrawable() != null) {
            initDrawableRectF();
        } else {
            mDrawableRectF.set(left, top, right, bottom);
        }
        mLastPointer.set(mDrawableRectF.centerX(), mDrawableRectF.centerY());
    }

    @Override
    public void setImageResource(int resId) {
        super.setImageResource(resId);
        mSourceBitmap = BitmapFactory.decodeResource(getContext().getResources(), resId);
    }

    @Override
    public void setImageBitmap(Bitmap bm) {
        super.setImageBitmap(bm);
        mSourceBitmap = bm;
    }

    @Override
    public void setImageDrawable(@Nullable Drawable drawable) {
        super.setImageDrawable(drawable);
        if (drawable != null && drawable instanceof BitmapDrawable) {
            mSourceBitmap = ((BitmapDrawable) drawable).getBitmap();
        }
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        canvas.clipRect(mDrawableRectF);
        for (DrawablePath drawablePath : mDrawablePathStack) {
            drawMosaicPath(canvas, drawablePath);
        }
        if (mCurrentPath != null) {
            drawMosaicPath(canvas, mCurrentPath);
        }
        if (!mIsHiddenBrush) {
            canvas.drawCircle(mLastPointer.x, mLastPointer.y, mBrushWidth * 0.5f, mBrushPaint);
        }
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        mLastPointer.set(event.getX(), event.getY());
        if (mDrawableRectF.contains(event.getX(), event.getY())) {
            switch (event.getAction()) {
                case MotionEvent.ACTION_DOWN:
                    mCurrentPath = new DrawablePath();
                    mCurrentPath.path.moveTo(event.getX(), event.getY());
                    break;
                case MotionEvent.ACTION_MOVE:
                    mCurrentPath.path.lineTo(event.getX(), event.getY());
                    postInvalidate();
                    break;
                case MotionEvent.ACTION_UP:
                    postInvalidate();
                    mDrawablePathStack.push(mCurrentPath);
                    mMosaicChangeListener.onChanged(mDrawablePathStack.size());
                    break;
                default:
                    break;
            }
        }
        return true;
    }

    @Override
    protected void onDetachedFromWindow() {
        Iterator<DrawablePath> iterator = mDrawablePathStack.iterator();
        while (iterator.hasNext()) {
            DrawablePath drawablePath = iterator.next();
            drawablePath.recycle();
            iterator.remove();
        }
        super.onDetachedFromWindow();
    }

    /**
     * 初始化显示矩阵
     */
    private void initDrawableRectF() {
        final float[] matrixValues = new float[9];
        getImageMatrix().getValues(matrixValues);
        //  缩放比
        float scaleX = matrixValues[Matrix.MSCALE_X];
        float scaleY = matrixValues[Matrix.MSCALE_Y];
        //  左上角坐标
        float left = matrixValues[Matrix.MTRANS_X];
        float top = matrixValues[Matrix.MTRANS_Y];
        //  drawable 显示宽高
        float drawableWidth = getDrawable().getIntrinsicWidth() * scaleX;
        float drawableHeight = getDrawable().getIntrinsicHeight() * scaleY;
        mDrawableRectF.set(left, top, left + drawableWidth, top + drawableHeight);
    }

    /**
     * 获取马赛克图层
     *
     * @return 马赛克图层
     */
    private Bitmap getMosaicLayer() {
        Bitmap bitmap =
                Bitmap.createBitmap(mSourceBitmap.getWidth(), mSourceBitmap.getHeight(), mSourceBitmap.getConfig());
        Canvas canvas = new Canvas(bitmap);
        canvas.drawBitmap(mSourceBitmap, new Matrix(), null);
        RenderScript rs = RenderScript.create(getContext());
        Allocation overlayAlloc = Allocation.createFromBitmap(rs, bitmap);
        ScriptIntrinsicBlur blur = ScriptIntrinsicBlur.create(rs, overlayAlloc.getElement());
        blur.setInput(overlayAlloc);
        //Radius out of range (0 < r <= 25).
        blur.setRadius(mRate);
        blur.forEach(overlayAlloc);
        overlayAlloc.copyTo(bitmap);
        return bitmap;
    }

    /**
     * 绘制马赛克路径图
     *
     * @param canvas       画布
     * @param drawablePath 画笔路径
     */
    private void drawMosaicPath(Canvas canvas, DrawablePath drawablePath) {
        int layerId = canvas.saveLayer(0, 0, canvas.getWidth(), canvas.getHeight(), null, ALL_SAVE_FLAG);
        canvas.drawPath(drawablePath.path, drawablePath.paint);
        drawablePath.paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC_IN));
        canvas.drawBitmap(drawablePath.mosaic, getImageMatrix(), drawablePath.paint);
        drawablePath.paint.setXfermode(null);
        canvas.restoreToCount(layerId);
    }

    /**
     * 撤销
     */
    public void revoke() {
        if (!mDrawablePathStack.isEmpty()) {
            mDrawablePathStack.peek().recycle();
            mDrawablePathStack.pop();
            mMosaicChangeListener.onChanged(mDrawablePathStack.size());
            invalidate();
        }
    }

    /**
     * 获取模糊图
     *
     * @return 局部模糊图
     */
    public Bitmap getMosaicBitmap() {
        mIsHiddenBrush = true;
        postInvalidate();
        Bitmap bitmap = ImageUtils.View2Bitmap(this);
        int left = (int) mDrawableRectF.left + 1;
        int top = (int) mDrawableRectF.top + 1;
        int right = (int) mDrawableRectF.right - 1;
        int bottom = (int) mDrawableRectF.bottom - 1;
        return Bitmap.createBitmap(bitmap, left, top, right - left, bottom - top, null, false);
    }

    /**
     * 设置模糊度
     *
     * @param rate 模糊值
     */
    public void setRate(float rate) {
        mIsRateChanged = true;
        this.mRate = rate * 0.25f;
        mRate = mRate <= 0 ? 1f : mRate;
        mRate = mRate > 25 ? 25f : mRate;
    }

    /**
     * 设置画笔粗细并更新画布
     *
     * @param width 宽度
     */
    public void setBrushWidth(float width) {
        this.mBrushWidth = width;
        postInvalidate();
    }

    public void setOnMosaicChangeListener(OnMosaicChangeListener listener) {
        this.mMosaicChangeListener = listener;
    }

    /**
     * 马赛克变动监听
     */
    public interface OnMosaicChangeListener {
        /**
         * 发生了改变
         *
         * @param count 图层总数
         */
        void onChanged(int count);
    }

    /**
     * 路径图
     */
    private class DrawablePath {
        /**
         * 路径
         */
        Path path = new Path();
        /**
         * 画笔
         */
        Paint paint = new Paint();
        /**
         * 马赛克图
         */
        Bitmap mosaic;

        DrawablePath() {
            paint.setStrokeCap(Paint.Cap.ROUND);
            paint.setStyle(Paint.Style.STROKE);
            paint.setStrokeWidth(mBrushWidth);

            if (mIsRateChanged || mDrawablePathStack.isEmpty()) {
                mosaic = getMosaicLayer();
                mIsRateChanged = false;
            } else {
                mosaic = mDrawablePathStack.peek().mosaic;
            }
        }

        /**
         * 释放资源
         */
        private void recycle() {
            path.reset();
            paint.reset();
            if (mosaic != null && mosaic.isRecycled()) {
                mosaic.recycle();
            }
        }

    }
}
