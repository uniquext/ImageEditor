package com.uniquext.android.imageeditor.widget;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.PointF;
import android.support.annotation.ColorInt;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.View;

import com.uniquext.android.imageeditor.R;

/**
 * @author penghaitao
 * @version 1.0
 * @date 2018/7/30 15:58
 * @description 颜色选择器
 */
public class ColorSelectorView extends View {
    /**
     * 透明色
     */
    private static final int TRANSPARENT = 0;

    /**
     * 是否被选中
     */
    private boolean mIsSelected = false;
    /**
     * 选择器颜色
     */
    private int mColor = TRANSPARENT;
    /**
     * 半径
     */
    private float mRadius = 0;
    /**
     * 中心点
     */
    private PointF mPointerCenter = new PointF();
    /**
     * 画笔
     */
    private Paint mPaint = new Paint();

    public ColorSelectorView(Context context) {
        this(context, null);
    }

    public ColorSelectorView(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public ColorSelectorView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        TypedArray typedArray = getContext().obtainStyledAttributes(attrs, R.styleable.ColorSelectorView);
        mColor = typedArray.getColor(R.styleable.ColorSelectorView_color, TRANSPARENT);
        mIsSelected = typedArray.getBoolean(R.styleable.ColorSelectorView_selected, false);
        typedArray.recycle();
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        mRadius = Math.min(w, h) * 0.5f;
        mPointerCenter.set(w * 0.5f, h * 0.5f);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        // TODO: 2018/7/30 selected状态可以考虑使用互补色，并在构造函数阶段就计算赋值，包括是否应该描边
        if (mColor == 0xFFFFFFFF) {
            mPaint.setColor(0xFF000000);
            canvas.drawCircle(mPointerCenter.x, mPointerCenter.y, mRadius, mPaint);
            mPaint.setColor(mColor);
            canvas.drawCircle(mPointerCenter.x, mPointerCenter.y, mRadius - 1, mPaint);
        } else {
            mPaint.setColor(mColor);
            canvas.drawCircle(mPointerCenter.x, mPointerCenter.y, mRadius, mPaint);
        }
        if (mIsSelected) {
            if (mColor != 0xFFFFFFFF) {
                mPaint.setColor(0xFFFFFFFF);
            } else {
                mPaint.setColor(0xFF000000);
            }
            canvas.drawCircle(mPointerCenter.x, mPointerCenter.y, 6, mPaint);
        }
    }

    public int getColor() {
        return mColor;
    }

    /**
     * 设置颜色并刷新
     *
     * @param color 颜色值
     */
    public void setColor(@ColorInt int color) {
        mColor = color;
        invalidate();
    }

    @Override
    public boolean isSelected() {
        return mIsSelected;
    }

    @Override
    public void setSelected(boolean selected) {
        mIsSelected = selected;
        invalidate();
    }
}
