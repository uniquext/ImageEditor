package com.uniquext.android.imageeditor.widget.characters;

import android.content.Context;
import android.graphics.PointF;
import android.graphics.Rect;
import android.support.annotation.Nullable;
import android.support.v7.widget.AppCompatTextView;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;

import com.uniquext.android.imageeditor.R;


/**
 * @author penghaitao
 * @version 1.0
 * @date 2018/7/21 19:51
 * @description
 */
public class ImageTextView extends RelativeLayout {

    //  region 文本框默认信息
    private static final String DEFAULT_HINT_TEXT = "点击输入文字";
    /**
     * 默认文本大小
     */
    private static final int DEFAULT_TEXT_SIZE_SP = 18;
    /**
     * 默认文本颜色
     */
    private static final int DEFAULT_TEXT_COLOR = 0xFFFFFFFF;
    /**
     * 默认锚点大小
     */
    private static final int DEFAULT_ANCHOR_WIDTH_DP = 30;
    /**
     * 文本框默认宽度
     */
    private static final int DEFAULT_FRAME_WIDTH_DP = 250;
    /**
     * 文本框最大宽度
     */
    private static final float MAX_FRAME_WIDTH_DP = 524f;
    /**
     * 文本框最小宽度
     */
    private static final float MIN_FRAME_WIDTH_DP = 110f;
    /**
     * 文本框默认高度
     */
    private static final int DEFAULT_FRAME_HEIGHT_DP = 68;
    //  endregion

    //  region 动作类型
    /**
     * 平移
     */
    private static final int TRANSLATE = 1;
    /**
     * 旋转缩放
     */
    private static final int SCALE_ROTATE = 2;
    //  endregion

    //  region 平移变量
    /**
     * 当前操作类型
     */
    private int action = 0;
    //  endregion

    //  region 旋转变量
    /**
     * X轴偏移量
     */
    private float offsetX;
    /**
     * Y轴偏移量
     */
    private float offsetY;
    /**
     * 旋转度数
     */
    private float mRotateValue = 0;
    //  endregion

    //  region 缩放变量
    /**
     * 初始弧度
     */
    private float mInitRotateRadian = 0;
    /**
     * 当前弧度
     */
    private float mCurrentRotateRadian = 0;
    /**
     * 缩放比
     */
    private float mScaleValue = 1f;
    //  endregion

    // region 单点触控
    /**
     * 初始缩放标尺
     */
    private float mInitScaleDistance = 0;
    /**
     * 当前缩放标尺
     */
    private float mCurrentScaleDistance = 0;
    /**
     * 初始点
     */
    private PointF mPointerInit = new PointF();
    //  endregion

    //  region 多点触控
    /**
     * 中心点
     */
    private PointF mPointerCenter = new PointF();
    /**
     * 当前点
     */
    private volatile PointF mPointerCurrent = new PointF();
    //  endregion
    /**
     * 主要点
     */
    private PointF mPointerMain = new PointF();
    /**
     * 辅助点
     */
    private PointF mPointerSecondary = new PointF();
    /**
     * 当前触摸点数
     */
    private int mPointerCount = 0;

    private AppCompatTextView mInputView;
    private View mFrameView;
    private View mAnchorView;
    private View mAddView;
    private View mDeleteView;

    private boolean mIsChecked = false;

    private int mUnitMargin = 0;
    private Rect mDrawableRect = new Rect();
    private DisplayMetrics mDisplayMetrics;

    private OnItemClickListener mOnItemClickListener;

    public ImageTextView(Context context) {
        this(context, null);
    }

    public ImageTextView(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public ImageTextView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        mDisplayMetrics = getContext().getResources().getDisplayMetrics();

        initFrame();
        initInputView();
        initAnchorView();
        initAddView();
        initDeleteView();

        mAnchorView.setOnTouchListener(new AnchorScaleRotateListener());
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        initDrawableRect();
        mPointerCenter.set((getLeft() + getRight()) * 0.5f, (getTop() + getBottom()) * 0.5f);
    }

    // TODO: 2018/7/31 需要重新计算
    @Override
    public boolean onTouchEvent(MotionEvent event) {
        mPointerCount = event.getPointerCount();
        confirmCoordinate(event);
        switch (event.getAction() & MotionEvent.ACTION_MASK) {
            case MotionEvent.ACTION_DOWN:
                action = TRANSLATE;
                mPointerInit.set(mPointerCurrent);
                break;
            case MotionEvent.ACTION_POINTER_DOWN:
                action = SCALE_ROTATE;
                mInitRotateRadian = computeCurrentRotateRadian();
                mInitScaleDistance = computeCurrentScaleDistance();
                break;
            case MotionEvent.ACTION_MOVE:
                if (!mIsChecked) {
                    break;
                }
                if (action == TRANSLATE) {
                    translation();
                } else if (action == SCALE_ROTATE) {
                    scaleAction();
                    rotateAction();
                }
                break;
            case MotionEvent.ACTION_POINTER_UP:
            case MotionEvent.ACTION_UP:
            default:
                if (action == TRANSLATE) {
                    float deltaX = mPointerCurrent.x - mPointerInit.x;
                    float deltaY = mPointerCurrent.y - mPointerInit.y;
                    setOffset(deltaX, deltaY);

                    Rect text = new Rect();
                    getHitRect(text);
                    text.inset(mUnitMargin, mUnitMargin);
                    if (!text.intersect(mDrawableRect)) {
                        setOffset(-offsetX, -offsetY);
                        setTranslationX(0);
                        setTranslationY(0);
                    } else if (Math.abs(deltaX) < 10 && Math.abs(deltaY) < 10 && mOnItemClickListener != null) {
                        mOnItemClickListener.onClickInputView(this);
                    }
                }
                invalidate();
                action = 0;
                break;
        }
        return true;
    }

    /**
     * 图片矩阵
     */
    private void initDrawableRect() {
        View parent = (View) getParent();
        mDrawableRect.left = 0;
        mDrawableRect.top = 0;
        mDrawableRect.right = parent.getWidth();
        mDrawableRect.bottom = parent.getHeight();
    }

    /**
     * 初始化文本框大小
     */
    private void initFrame() {
        int width = (int) (mDisplayMetrics.density * (DEFAULT_FRAME_WIDTH_DP + DEFAULT_ANCHOR_WIDTH_DP));
        int height = (int) (mDisplayMetrics.density * (DEFAULT_FRAME_HEIGHT_DP + DEFAULT_ANCHOR_WIDTH_DP));
        ViewGroup.LayoutParams layoutParams = new ViewGroup.LayoutParams(width, height);
        setLayoutParams(layoutParams);
    }

    //  region 初始化控件

    /**
     * 添加文本控件
     */
    private void initInputView() {
        mInputView = new AppCompatTextView(getContext());
        mInputView.setGravity(Gravity.CENTER);
        mInputView.setTextColor(DEFAULT_TEXT_COLOR);
        mInputView.setText(DEFAULT_HINT_TEXT);
        mInputView.setTextSize(TypedValue.COMPLEX_UNIT_SP, DEFAULT_TEXT_SIZE_SP);
        mUnitMargin = (int) (mDisplayMetrics.density * DEFAULT_ANCHOR_WIDTH_DP * 0.5f);
        int width = (int) (mDisplayMetrics.density * DEFAULT_FRAME_WIDTH_DP);
        int height = (int) (mDisplayMetrics.density * DEFAULT_FRAME_HEIGHT_DP);
        LayoutParams layoutParams = new LayoutParams(width, height);
        layoutParams.addRule(CENTER_IN_PARENT);
        layoutParams.setMargins(mUnitMargin, mUnitMargin, mUnitMargin, mUnitMargin);
        addView(mInputView, layoutParams);

        mFrameView = new View(getContext());
        mFrameView.setBackgroundResource(R.drawable.characters_frame);
        addView(mFrameView, layoutParams);
    }

    /**
     * 添加旋转控件
     */
    private void initAnchorView() {
        mAnchorView = new View(getContext());
        mAnchorView.setBackgroundResource(R.mipmap.icon_text_rotate);
        float density = getContext().getResources().getDisplayMetrics().density;
        LayoutParams layoutParams = new LayoutParams(
                (int) density * DEFAULT_ANCHOR_WIDTH_DP, (int) density * DEFAULT_ANCHOR_WIDTH_DP);
        layoutParams.addRule(ALIGN_PARENT_BOTTOM);
        layoutParams.addRule(ALIGN_PARENT_END);
        addView(mAnchorView, layoutParams);
    }

    /**
     * 添加新增控件
     */
    private void initAddView() {
        mAddView = new View(getContext());
        mAddView.setBackgroundResource(R.mipmap.icon_text_add);
        float density = getContext().getResources().getDisplayMetrics().density;
        LayoutParams layoutParams = new LayoutParams(
                (int) density * DEFAULT_ANCHOR_WIDTH_DP, (int) density * DEFAULT_ANCHOR_WIDTH_DP);
        layoutParams.addRule(ALIGN_PARENT_BOTTOM);
        layoutParams.addRule(ALIGN_PARENT_START);
        addView(mAddView, layoutParams);
        mAddView.setOnClickListener(v -> {
            if (mOnItemClickListener != null) {
                mOnItemClickListener.onClickAddView();
            }
        });
    }

    /**
     * 添加删除控件
     */
    private void initDeleteView() {
        mDeleteView = new View(getContext());
        mDeleteView.setBackgroundResource(R.mipmap.icon_text_delete);
        float density = getContext().getResources().getDisplayMetrics().density;
        LayoutParams layoutParams = new LayoutParams(
                (int) density * DEFAULT_ANCHOR_WIDTH_DP, (int) density * DEFAULT_ANCHOR_WIDTH_DP);
        layoutParams.addRule(ALIGN_PARENT_TOP);
        layoutParams.addRule(ALIGN_PARENT_END);
        addView(mDeleteView, layoutParams);
        mDeleteView.setOnClickListener(v -> {
            if (mOnItemClickListener != null) {
                mOnItemClickListener.onClickDeleteView(this);
            }
        });
    }
    //  endregion

    /**
     * 确定坐标
     *
     * @param event 触摸事件
     */
    private void confirmCoordinate(MotionEvent event) {
        if (mPointerCount == 2) {
            mPointerMain.set(event.getX(0), event.getY(0));
            mPointerSecondary.set(event.getX(1), event.getY(1));
        } else {
            mPointerCurrent.set(event.getRawX(), event.getRawY());
        }
    }

    //  region 多点触控缩放

    /**
     * 缩放
     */
    private void scaleAction() {
        mCurrentScaleDistance = computeCurrentScaleDistance();
        if (canScale()) {
            mScaleValue = mScaleValue * mCurrentScaleDistance / mInitScaleDistance;
            setScaleX(mScaleValue);
            setScaleY(mScaleValue);
        }

//        v1.setScaleX(1f);
//        v1.setScaleY(1f);
//        v2.setScaleX(1f);
//        v2.setScaleY(1f);
//        v3.setScaleX(1f);
//        v3.setScaleY(1f);
    }

    private boolean canScale() {
        float tempScale = mScaleValue * mCurrentScaleDistance / mInitScaleDistance;
        return tempScale <= MAX_FRAME_WIDTH_DP / DEFAULT_FRAME_WIDTH_DP
                && tempScale >= MIN_FRAME_WIDTH_DP / DEFAULT_FRAME_WIDTH_DP;
    }

    /**
     * 计算当前缩放标尺
     *
     * @return ScaleDistance
     */
    private float computeCurrentScaleDistance() {
        float deltaX = mPointerSecondary.x - mPointerMain.x;
        float deltaY = mPointerSecondary.y - mPointerMain.y;
        return (float) Math.sqrt(deltaX * deltaX + deltaY * deltaY);
    }
    //  endregion

    //  region 多点触控旋转

    /**
     * 计算当前弧度
     *
     * @return RotateRadian
     */
    private float computeCurrentRotateRadian() {
        // TODO: 2018/7/30 弧度计算不能使用坐标点 ，应该使用余弦定理
        float deltaX = mPointerSecondary.x - mPointerMain.x;
        float deltaY = mPointerSecondary.y - mPointerMain.y;
        return (float) Math.atan2(deltaY, deltaX);
    }

    /**
     * 旋转
     */
    private void rotateAction() {
        mCurrentRotateRadian = computeCurrentRotateRadian();
        mRotateValue = (mRotateValue + (float) Math.toDegrees(mCurrentRotateRadian - mInitRotateRadian)) % 360;
        setRotation(mRotateValue);
    }
    //  endregion

    //  region 平移

    /**
     * 平移
     */
    private void translation() {
        setTranslationX(offsetX + mPointerCurrent.x - mPointerInit.x);
        setTranslationY(offsetY + mPointerCurrent.y - mPointerInit.y);
    }

    /**
     * 设置偏移量
     *
     * @param x
     * @param y
     */
    private void setOffset(float x, float y) {
        offsetX = offsetX + x;
        offsetY = offsetY + y;
        mPointerCenter.offset(x, y);
    }
    //  endregion

    /**
     * 设置文本颜色
     *
     * @param color 颜色
     */
    public void setTextColor(int color) {
        mInputView.setTextColor(color);
    }

    /**
     * 设置文本透明度
     *
     * @param alpha 透明度
     */
    public void setTextAlpha(float alpha) {
        mInputView.setAlpha(alpha);
    }

    public AppCompatTextView getTextView() {
        return mInputView;
    }

    /**
     * 输入文本内容
     *
     * @param charSequence 内容
     */
    public void setText(CharSequence charSequence) {
        mInputView.setText(charSequence);
    }

    /**
     * 设置文本大小
     *
     * @param size 大小
     */
    public void setTextSize(float size) {
        mInputView.setTextSize(TypedValue.COMPLEX_UNIT_PX, size);
    }

    public void setOnItemClickListener(OnItemClickListener listener) {
        this.mOnItemClickListener = listener;
    }

    /**
     * 隐藏锚点
     *
     * @param isChecked 是否被选中
     */
    public void setChecked(boolean isChecked) {
        mIsChecked = isChecked;
        mFrameView.setVisibility(isChecked ? VISIBLE : GONE);
        mAnchorView.setVisibility(isChecked ? VISIBLE : GONE);
        mAddView.setVisibility(isChecked ? VISIBLE : GONE);
        mDeleteView.setVisibility(isChecked ? VISIBLE : GONE);
    }

    public void setTextHide() {
        mInputView.setVisibility(!DEFAULT_HINT_TEXT.equals(mInputView.getText().toString()) ? VISIBLE : GONE);
    }

    /**
     * 锚点缩放旋转事件监听
     */
    private class AnchorScaleRotateListener implements OnTouchListener {
        // TODO: 2018/7/30 均有待重新计算
        @Override
        public boolean onTouch(View v, MotionEvent event) {
            mPointerMain.set(event.getRawX(), event.getRawY());
            switch (event.getAction()) {
                case MotionEvent.ACTION_DOWN:
                    mPointerSecondary.set(mPointerCenter);
                    mInitRotateRadian = computeCurrentRotateRadian();
                    mInitScaleDistance = computeCurrentScaleDistance();
                    break;
                case MotionEvent.ACTION_MOVE:
                    mCurrentScaleDistance = computeCurrentScaleDistance();
                    if (canScale()) {
                        setScaleX(mScaleValue * mCurrentScaleDistance / mInitScaleDistance);
                        setScaleY(mScaleValue * mCurrentScaleDistance / mInitScaleDistance);
                    }
                    mCurrentRotateRadian = computeCurrentRotateRadian();
                    setRotation(mRotateValue + (float) Math.toDegrees(mCurrentRotateRadian - mInitRotateRadian));


                    break;
                case MotionEvent.ACTION_UP:
                default:
                    if (canScale()) {
                        mScaleValue = mScaleValue * mCurrentScaleDistance / mInitScaleDistance;
                    }
                    mRotateValue += (float) Math.toDegrees(mCurrentRotateRadian - mInitRotateRadian);
                    mRotateValue %= 360;

//                    Log.e("#### mPointerCenter", mPointerCenter.x + " #" + mPointerCenter.y);
//                    Log.e("#### Pivot", getPivotX() + " #" + getPivotY());

//                    mAddView.setScaleX(1f / mScaleValue);
//                    mAddView.setScaleY(1f / mScaleValue);
//                    mDeleteView.setScaleX(1f / mScaleValue);
//                    mDeleteView.setScaleY(1f / mScaleValue);
//                    mAnchorView.setScaleX(1f / mScaleValue);
//                    mAnchorView.setScaleY(1f / mScaleValue);

                    postInvalidate();
                    break;
            }
            return true;
        }
    }
}