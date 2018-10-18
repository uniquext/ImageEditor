package com.uniquext.android.imageeditor.widget;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.PointF;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.RectF;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.support.annotation.Nullable;
import android.support.v7.widget.AppCompatImageView;
import android.util.AttributeSet;
import android.view.MotionEvent;

/**
 * @author penghaitao
 * @version 1.0
 * @date 2018/7/24 16:18
 * @description
 */
public class TrimView extends AppCompatImageView {

    /**
     * 自由裁剪
     */
    public static final float FREE_RATIO = 0f;

    //  region 裁剪框基线坐标相关
    /**
     * 裁剪辅助行数
     */
    private static final int ROW_COUNT = 3;
    /**
     * 裁剪辅助列数
     */
    private static final int COLUMN_COUNT = 3;
    /**
     * 裁剪框基线颜色值
     */
    private static final int CLIP_FRAME_LINE_COLOR = 0xFFFFFFFF;
    /**
     * 裁剪框外部蒙版颜色值
     */
    private static final int CLIP_FRAME_OUT_COLOR = 0xB2000000;
    /**
     * 缩放锚点单位长度
     */
    private static final float UNIT_ANCHOR_LENGTH = 10f;
    //  endregion

    //  region 颜色值
    /**
     * 最小可裁剪宽度
     */
    private static final float MIN_GRID_WIDTH = 200f;
    /**
     * 辅助线宽度
     */
    private static final int GRID_LINE_WIDTH_PX = 1;
    //  endregion

    //  region 宽度设点
    /**
     * 表格边框宽度
     */
    private static final int FRAME_LINE_WIDTH_PX = 2;
    /**
     * 锚点宽度
     */
    private static final int ANCHOR_LINE_WIDTH_PX = 4;
    /**
     * 无效操作
     */
    private static final int MOTION_ACTION_NONE = 0x0000;
    /**
     * 左锚点
     */
    private static final int MOTION_ACTION_LEFT = 0x0001;
    /**
     * 上锚点
     */
    private static final int MOTION_ACTION_TOP = 0x0002;
    //  endregion

    //  region 操作事件
    /**
     * 右锚点
     */
    private static final int MOTION_ACTION_RIGHT = 0x0004;
    /**
     * 下锚点
     */
    private static final int MOTION_ACTION_BOTTOM = 0x0008;
    /**
     * 左上
     */
    private static final int MOTION_ACTION_LEFT_TOP = 0x0003;
    /**
     * 右上
     */
    private static final int MOTION_ACTION_RIGHT_TOP = 0x0006;
    /**
     * 左下
     */
    private static final int MOTION_ACTION_LEFT_BOTTOM = 0x0009;
    /**
     * 右下
     */
    private static final int MOTION_ACTION_RIGHT_BOTTOM = 0x000C;
    /**
     * 移动操作
     */
    private static final int MOTION_ACTION_MOVE = 0x000F;
    /**
     * 表格边线坐标数组大小
     */
    private int FRAME_LINE_COUNT = 2 * 2 * 4;
    /**
     * 锚点线坐标数组大小
     */
    private int ANCHOR_LINE_COUNT = (ROW_COUNT + COLUMN_COUNT) * 2 * 4;
    /**
     * 辅助线坐标数组大小
     */
    private int GRID_LINE_COUNT = (ROW_COUNT - 1) * (COLUMN_COUNT - 1) * 4;
    //  endregion
    /**
     * 矩阵原始宽高比
     */
    private float mMatrixRatio = 0;
    /**
     * 图像裁剪比
     */
    private float mClipRatio = FREE_RATIO;
    /**
     * 触摸事件
     */
    private int mMotionActions = MOTION_ACTION_NONE;
    /**
     * 图像矩阵缩放比
     */
    private float mMatrixScale = 1f;
    /**
     * 锚点单位长
     * 单位dp
     */
    private float mUnitAnchorLength = 0f;

    /**
     * 原始图
     */
    private Bitmap mSourceBitmap;

    /**
     * 裁剪矩阵
     */
    private RectF mClipRect = new RectF();
    /**
     * 图像矩阵
     */
    private RectF mDrawableRectF = new RectF();
    /**
     * 最后移动点
     */
    private PointF mLastMotionPoint = new PointF();

    //  region 画笔
    /**
     * 裁剪框矩阵交涉画笔
     */
    private Paint mRectFPaint = new Paint();
    /**
     * 辅助线画笔
     */
    private Paint mGridLinePaint = new Paint();
    /**
     * 边线画笔
     */
    private Paint mFrameLinePaint = new Paint();
    /**
     * 锚点画笔
     */
    private Paint mAnchorLinePaint = new Paint();
    //  endregion

    //  region 坐标
    /**
     * 辅助线坐标
     */
    private float[] mGridLines = new float[GRID_LINE_COUNT];
    /**
     * 边线坐标
     */
    private float[] mFrameLines = new float[FRAME_LINE_COUNT];
    /**
     * 锚点坐标
     */
    private float[] mAnchorLines = new float[ANCHOR_LINE_COUNT];
    //  endregion

    public TrimView(Context context) {
        this(context, null);
    }

    public TrimView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public TrimView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        mGridLinePaint.setColor(CLIP_FRAME_LINE_COLOR);
        mFrameLinePaint.setColor(CLIP_FRAME_LINE_COLOR);
        mAnchorLinePaint.setColor(CLIP_FRAME_LINE_COLOR);
        float density = getContext().getResources().getDisplayMetrics().density;
        mUnitAnchorLength = UNIT_ANCHOR_LENGTH * density;
        mGridLinePaint.setStrokeWidth(GRID_LINE_WIDTH_PX);
        mFrameLinePaint.setStrokeWidth(FRAME_LINE_WIDTH_PX * density);
        mAnchorLinePaint.setStrokeWidth(ANCHOR_LINE_WIDTH_PX * density);
        mRectFPaint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.DST_OUT));
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
    }

    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        super.onLayout(changed, left, top, right, bottom);
        if (getDrawable() != null) {
            computerMatrixScale();
        } else {
            mDrawableRectF.set(left, top, right, bottom);
        }
        initClipGrid();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        int layerId = canvas.saveLayer(0, 0, canvas.getWidth(), canvas.getHeight(), null, Canvas.ALL_SAVE_FLAG);
        canvas.drawColor(CLIP_FRAME_OUT_COLOR);
        canvas.drawRect(mClipRect, mRectFPaint);
        canvas.restoreToCount(layerId);
        canvas.drawLines(mGridLines, 0, mGridLines.length, mGridLinePaint);
        canvas.drawLines(mFrameLines, 0, mFrameLines.length, mFrameLinePaint);
        canvas.drawLines(mAnchorLines, 0, mAnchorLines.length, mAnchorLinePaint);
    }

    @SuppressLint("ClickableViewAccessibility")
    @Override
    public boolean onTouchEvent(MotionEvent event) {
        float coordinateX = event.getX();
        float coordinateY = event.getY();

        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                mLastMotionPoint.set(coordinateX, coordinateY);
                computerMotionAction(coordinateX, coordinateY);
                break;
            case MotionEvent.ACTION_MOVE:
                float offsetX = coordinateX - mLastMotionPoint.x;
                float offsetY = coordinateY - mLastMotionPoint.y;
                if (mMotionActions == MOTION_ACTION_MOVE) {
                    if (mClipRect.left + offsetX < mDrawableRectF.left
                            || mClipRect.right + offsetX > mDrawableRectF.right) {
                        offsetX = 0;
                    }
                    if (mClipRect.top + offsetY < mDrawableRectF.top
                            || mClipRect.bottom + offsetY > mDrawableRectF.bottom) {
                        offsetY = 0;
                    }
                    mClipRect.offset(offsetX, offsetY);
                } else {
                    scaleOnAxis(offsetX, offsetY);
                    scaleOnDiagonal(offsetX, offsetY);
                }
                mLastMotionPoint.set(coordinateX, coordinateY);
                break;
            case MotionEvent.ACTION_UP:
                autoReplace();
                break;
            default:
                break;
        }
        drawClipGrid();
        return true;
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

    /**
     * 设置裁剪缩放比
     *
     * @param ratio 比例
     */
    public void setFrameRatio(float ratio) {
        mClipRatio = ratio;
        if (mClipRatio == FREE_RATIO) {
            return;
        } else {
            mClipRect.intersect(mDrawableRectF);
            float width = mClipRect.width();
            float height = mClipRect.height();
            float centerX = mClipRect.centerX();
            float centerY = mClipRect.centerY();

            if (mMatrixRatio > mClipRatio) {
                mClipRect.left = centerX - height * mClipRatio * 0.5f;
                mClipRect.right = centerX + height * mClipRatio * 0.5f;
            } else {
                mClipRect.top = centerY - width / mClipRatio * 0.5f;
                mClipRect.bottom = centerY + width / mClipRatio * 0.5f;
            }
        }
        autoReplace();
        drawClipGrid();
    }

    /**
     * 裁剪
     *
     * @return 裁剪后的图像
     */
    public Bitmap clip() {
        int left = (int) ((mClipRect.left - mDrawableRectF.left) / mMatrixScale);
        int top = (int) ((mClipRect.top - mDrawableRectF.top) / mMatrixScale);
        int right = (int) ((mClipRect.right - mDrawableRectF.left) / mMatrixScale);
        int bottom = (int) ((mClipRect.bottom - mDrawableRectF.top) / mMatrixScale);
        if (right - left > 0 && bottom - top > 0) {
            return Bitmap.createBitmap(mSourceBitmap, left, top, right - left, bottom - top, null, false);
        } else {
            return mSourceBitmap;
        }
    }

    /**
     * 初始化定位
     */
    private void initClipGrid() {
        mClipRect.set(mDrawableRectF);
        float deltaWidth = Math.abs(mClipRect.width() - mClipRect.height()) * 0.5f;
        if (mClipRect.width() > mClipRect.height()) {
            mClipRect.inset(deltaWidth, 0);
        } else {
            mClipRect.inset(0, deltaWidth);
        }
        mClipRect.inset(ANCHOR_LINE_WIDTH_PX, ANCHOR_LINE_WIDTH_PX);
        drawClipGrid();
    }

    /**
     * 自动调整
     */
    private void autoReplace() {
        if (!mDrawableRectF.contains(mClipRect)) {
            autoScale();
            springBack();
        }
    }

    /**
     * 计算图像矩阵信息
     * 缩放比、长宽比、坐标系
     */
    private void computerMatrixScale() {
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

        mMatrixRatio = drawableWidth / drawableHeight;
        mMatrixScale = Math.min(matrixValues[Matrix.MSCALE_X], matrixValues[Matrix.MSCALE_Y]);
        mDrawableRectF.set(left, top, left + drawableWidth, top + drawableHeight);

    }

    /**
     * 绘制裁剪表格
     */
    private void drawClipGrid() {
        drawGridLines();
        drawFrameLines();
        drawAnchorLines();
        invalidate();
    }

    /**
     * 绘制中间的分割线
     */
    private void drawGridLines() {
        float unitRow = (mClipRect.right - mClipRect.left) / ROW_COUNT;
        float unitColumn = (mClipRect.bottom - mClipRect.top) / COLUMN_COUNT;
        // Column grid line
        for (int column = 1; column < COLUMN_COUNT; column++) {
            float pointX = column * unitColumn + mClipRect.top;
            mGridLines[4 * (column - 1) + 0] = mClipRect.left;
            mGridLines[4 * (column - 1) + 1] = pointX;
            mGridLines[4 * (column - 1) + 2] = mClipRect.right;
            mGridLines[4 * (column - 1) + 3] = pointX;
        }
        // Row grid line
        int pointOffset = (ROW_COUNT - 1) * 4;
        for (int row = 1; row < COLUMN_COUNT; row++) {
            float pointY = row * unitRow + mClipRect.left;
            mGridLines[4 * (row - 1) + pointOffset + 0] = pointY;
            mGridLines[4 * (row - 1) + pointOffset + 1] = mClipRect.top;
            mGridLines[4 * (row - 1) + pointOffset + 2] = pointY;
            mGridLines[4 * (row - 1) + pointOffset + 3] = mClipRect.bottom;
        }
    }

    /**
     * 绘制边框
     */
    private void drawFrameLines() {
        // Column frame line
        for (int column = 0; column < 2; column++) {
            float pointX = column == 0 ? mClipRect.left : mClipRect.right;
            mFrameLines[4 * column + 0] = pointX;
            mFrameLines[4 * column + 1] = mClipRect.top;
            mFrameLines[4 * column + 2] = pointX;
            mFrameLines[4 * column + 3] = mClipRect.bottom;
        }
        // Row frame line
        int pointOffset = 2 * 4;
        for (int row = 0; row < 2; row++) {
            float pointY = row == 0 ? mClipRect.top : mClipRect.bottom;
            mFrameLines[4 * row + pointOffset + 0] = mClipRect.left;
            mFrameLines[4 * row + pointOffset + 1] = pointY;
            mFrameLines[4 * row + pointOffset + 2] = mClipRect.right;
            mFrameLines[4 * row + pointOffset + 3] = pointY;
        }
    }

    /**
     * 绘制锚点
     */
    private void drawAnchorLines() {
        // Column frame line
        int pointOffset = 0;
        for (int column = 0; column < 2; column++) {
            float pointX = column == 0 ? mClipRect.left : mClipRect.right;
            for (int section = 0; section < COLUMN_COUNT; section++) {
                float top, bottom;
                if (section == 0) {
                    top = mClipRect.top - mAnchorLinePaint.getStrokeWidth() * 0.5f;
                    bottom = mClipRect.top + mUnitAnchorLength;
                } else if (section == 1) {
                    top = (mClipRect.top + mClipRect.bottom) * 0.5f - mUnitAnchorLength;
                    bottom = (mClipRect.top + mClipRect.bottom) * 0.5f + mUnitAnchorLength;
                } else {
                    top = mClipRect.bottom - mUnitAnchorLength;
                    bottom = mClipRect.bottom + mAnchorLinePaint.getStrokeWidth() * 0.5f;
                }
                mAnchorLines[4 * pointOffset + 0] = pointX;
                mAnchorLines[4 * pointOffset + 1] = top;
                mAnchorLines[4 * pointOffset + 2] = pointX;
                mAnchorLines[4 * pointOffset + 3] = bottom;
                pointOffset++;
            }
        }
        // Row frame line
        for (int row = 0; row < 2; row++) {
            float pointY = row == 0 ? mClipRect.top : mClipRect.bottom;
            for (int section = 0; section < COLUMN_COUNT; section++) {
                float left, right;
                if (section == 0) {
                    left = mClipRect.left - mAnchorLinePaint.getStrokeWidth() * 0.5f;
                    right = mClipRect.left + mUnitAnchorLength;
                } else if (section == 1) {
                    left = (mClipRect.left + mClipRect.right) * 0.5f - mUnitAnchorLength;
                    right = (mClipRect.left + mClipRect.right) * 0.5f + mUnitAnchorLength;
                } else {
                    left = mClipRect.right - mUnitAnchorLength;
                    right = mClipRect.right + mAnchorLinePaint.getStrokeWidth() * 0.5f;
                }
                mAnchorLines[4 * pointOffset + 0] = left;
                mAnchorLines[4 * pointOffset + 1] = pointY;
                mAnchorLines[4 * pointOffset + 2] = right;
                mAnchorLines[4 * pointOffset + 3] = pointY;
                pointOffset++;
            }
        }
    }

    /**
     * 计算动作类型
     *
     * @param x x坐标
     * @param y y坐标
     */
    private void computerMotionAction(float x, float y) {
        float deltaUnit = 2 * mUnitAnchorLength;
        RectF touch = new RectF(x, y, x, y);
        touch.inset(-deltaUnit, -deltaUnit);
        if (!RectF.intersects(mClipRect, touch)) {
            mMotionActions = MOTION_ACTION_NONE;
            return;
        }
        int verticalAction, horizontalAction;
        if (x > mClipRect.left - deltaUnit && x < mClipRect.left + deltaUnit) {
            verticalAction = MOTION_ACTION_LEFT;
        } else if (x > mClipRect.right - deltaUnit && x < mClipRect.right + deltaUnit) {
            verticalAction = MOTION_ACTION_RIGHT;
        } else {
            verticalAction = MOTION_ACTION_NONE;
        }
        if (y > mClipRect.top - deltaUnit && y < mClipRect.top + deltaUnit) {
            horizontalAction = MOTION_ACTION_TOP;
        } else if (y > mClipRect.bottom - deltaUnit && y < mClipRect.bottom + deltaUnit) {
            horizontalAction = MOTION_ACTION_BOTTOM;
        } else {
            horizontalAction = MOTION_ACTION_NONE;
        }
        mMotionActions = verticalAction | horizontalAction;
        if (mMotionActions == MOTION_ACTION_NONE) {
            mMotionActions = MOTION_ACTION_MOVE;
        }
    }

    /**
     * 在X/Y轴上缩放
     *
     * @param offsetX x偏移量
     * @param offsetY y偏移量
     */
    private void scaleOnAxis(float offsetX, float offsetY) {
        boolean subScale;
        boolean subScaleX = mClipRect.width() > MIN_GRID_WIDTH;
        boolean subScaleY = mClipRect.height() > MIN_GRID_WIDTH;
        boolean addScale = mClipRatio == FREE_RATIO || mDrawableRectF.contains(mClipRect);
        switch (mMotionActions) {
            case MOTION_ACTION_LEFT:
                subScale = subScaleX && (mClipRatio == FREE_RATIO || subScaleY);
                if (offsetX > 0 && subScale || offsetX < 0 && addScale) {
                    mClipRect.left += offsetX;
                    scaleClipGridHeight(mClipRatio == FREE_RATIO ? 0 : offsetX / mClipRatio);
                }
                break;
            case MOTION_ACTION_TOP:
                subScale = subScaleY && (mClipRatio == FREE_RATIO || subScaleX);
                if (offsetY > 0 && subScale || offsetY < 0 && addScale) {
                    mClipRect.top += offsetY;
                    scaleClipGridWidth(offsetY * mClipRatio);
                }
                break;
            case MOTION_ACTION_RIGHT:
                subScale = subScaleX && (mClipRatio == FREE_RATIO || subScaleY);
                if (offsetX < 0 && subScale || offsetX > 0 && addScale) {
                    mClipRect.right += offsetX;
                    scaleClipGridHeight(mClipRatio == FREE_RATIO ? 0 : -offsetX / mClipRatio);
                }
                break;
            case MOTION_ACTION_BOTTOM:
                subScale = subScaleY && (mClipRatio == FREE_RATIO || subScaleX);
                if (offsetY < 0 && subScale || offsetY > 0 && addScale) {
                    mClipRect.bottom += offsetY;
                    scaleClipGridWidth(-offsetY * mClipRatio);
                }
                break;
            default:
                break;
        }
    }

    /**
     * 在对角上缩放
     *
     * @param offsetX x偏移量
     * @param offsetY y偏移量
     */
    private void scaleOnDiagonal(float offsetX, float offsetY) {
        float deltaLeft = mDrawableRectF.left - mClipRect.left;
        float deltaTop = mDrawableRectF.top - mClipRect.top;
        float deltaRight = mDrawableRectF.right - mClipRect.right;
        float deltaBottom = mDrawableRectF.bottom - mClipRect.bottom;

        float subScaleX = mClipRect.width() - MIN_GRID_WIDTH;
        float subScaleY = mClipRect.height() - MIN_GRID_WIDTH;

        switch (mMotionActions) {
            case MOTION_ACTION_LEFT_TOP:
                offsetX = subScaleX < offsetX ? 0 : offsetX;
                offsetY = subScaleY < offsetY ? 0 : offsetY;
                if (mClipRatio == FREE_RATIO) {
                    mClipRect.left += deltaLeft > offsetX ? 0 : offsetX;
                    mClipRect.top += deltaTop > offsetY ? 0 : offsetY;
                } else if (offsetX * offsetY > 0) {
                    float minDelta = Math.min(Math.abs(offsetX), Math.abs(offsetY));
                    offsetY = offsetY > 0 ? minDelta : -minDelta;
                    offsetX = offsetX > 0 ? minDelta * mClipRatio : -minDelta * mClipRatio;
                    if (deltaLeft < offsetX && deltaTop < offsetY) {
                        mClipRect.left += offsetX;
                        mClipRect.top += offsetY;
                    }
                }
                break;
            case MOTION_ACTION_RIGHT_TOP:
                offsetX = subScaleX < -offsetX ? 0 : offsetX;
                offsetY = subScaleY < offsetY ? 0 : offsetY;
                if (mClipRatio == FREE_RATIO) {
                    mClipRect.right += deltaRight < offsetX ? 0 : offsetX;
                    mClipRect.top += deltaTop > offsetY ? 0 : offsetY;
                } else if (offsetX * offsetY < 0) {
                    float minDelta = Math.min(Math.abs(offsetX), Math.abs(offsetY));
                    offsetY = offsetY > 0 ? minDelta : -minDelta;
                    offsetX = offsetX > 0 ? minDelta * mClipRatio : -minDelta * mClipRatio;
                    if (deltaRight > offsetX && deltaTop < offsetY) {
                        mClipRect.right += offsetX;
                        mClipRect.top += offsetY;
                    }
                }
                break;
            case MOTION_ACTION_LEFT_BOTTOM:
                offsetX = subScaleX < offsetX ? 0 : offsetX;
                offsetY = subScaleY < -offsetY ? 0 : offsetY;
                if (mClipRatio == FREE_RATIO) {
                    mClipRect.left += deltaLeft > offsetX ? 0 : offsetX;
                    mClipRect.bottom += deltaBottom < offsetY ? 0 : offsetY;
                } else if (offsetX * offsetY < 0) {
                    float minDelta = Math.min(Math.abs(offsetX), Math.abs(offsetY));
                    offsetY = offsetY > 0 ? minDelta : -minDelta;
                    offsetX = offsetX > 0 ? minDelta * mClipRatio : -minDelta * mClipRatio;
                    if (deltaLeft < offsetX && deltaBottom > offsetY) {
                        mClipRect.left += offsetX;
                        mClipRect.bottom += offsetY;
                    }
                }
                break;
            case MOTION_ACTION_RIGHT_BOTTOM:
                offsetX = subScaleX < -offsetX ? 0 : offsetX;
                offsetY = subScaleY < -offsetY ? 0 : offsetY;
                if (mClipRatio == FREE_RATIO) {
                    mClipRect.right += deltaRight < offsetX ? 0 : offsetX;
                    mClipRect.bottom += deltaBottom < offsetY ? 0 : offsetY;
                } else if (offsetX * offsetY > 0) {
                    float minDelta = Math.min(Math.abs(offsetX), Math.abs(offsetY));
                    offsetY = offsetY > 0 ? minDelta : -minDelta;
                    offsetX = offsetX > 0 ? minDelta * mClipRatio : -minDelta * mClipRatio;
                    if (deltaRight > offsetX && deltaBottom > offsetY) {
                        mClipRect.right += offsetX;
                        mClipRect.bottom += offsetY;
                    }
                }
                break;
            default:
                break;
        }
    }

    /**
     * 缩放裁剪框X轴
     *
     * @param dx x偏移量
     */
    private void scaleClipGridWidth(float dx) {
        mClipRect.left += dx * 0.5f;
        mClipRect.right -= dx * 0.5f;
    }

    /**
     * 缩放裁剪框Y轴
     *
     * @param dy y偏移量
     */
    private void scaleClipGridHeight(float dy) {
        mClipRect.top += dy * 0.5f;
        mClipRect.bottom -= dy * 0.5f;
    }

    /**
     * 回弹
     */
    private void springBack() {
        float deltaLeft = mDrawableRectF.left - mClipRect.left;
        float deltaTop = mDrawableRectF.top - mClipRect.top;
        float deltaRight = mDrawableRectF.right - mClipRect.right;
        float deltaBottom = mDrawableRectF.bottom - mClipRect.bottom;
        if (deltaLeft > 0) {
            mClipRect.offset(deltaLeft, 0);
        } else if (deltaRight < 0) {
            mClipRect.offset(deltaRight, 0);
        }
        if (deltaTop > 0) {
            mClipRect.offset(0, deltaTop);
        } else if (deltaBottom < 0) {
            mClipRect.offset(0, deltaBottom);
        }
    }

    /**
     * 自动缩放
     */
    private void autoScale() {
        float deltaWidth = Math.max(0, mClipRect.width() - mDrawableRectF.width());
        float deltaHeight = Math.max(0, mClipRect.height() - mDrawableRectF.height());
        float deltaMax = Math.max(deltaWidth, deltaHeight);
        if (deltaMax > 0) {
            if (mClipRatio == FREE_RATIO) {
                mClipRect.inset(deltaWidth * 0.5f, deltaHeight * 0.5f);
            } else {
                mClipRect.inset(deltaMax * 0.5f, deltaMax * 0.5f);
            }
        }
    }
}
