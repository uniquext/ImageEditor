package com.uniquext.android.imageeditor.widget.characters;

import android.content.Context;
import android.graphics.Bitmap;
import android.support.annotation.ColorInt;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.uniquext.android.imageeditor.util.ImageUtils;

import java.util.LinkedList;

/**
 * @author penghaitao
 * @version 1.0
 * @date 2018/7/26 12:32
 * @description
 */
public class CharactersLayout extends RelativeLayout {

    /**
     * 文本框默认宽度
     */
    private static final int DEFAULT_FRAME_WIDTH_DP = 250;
    /**
     * 默认锚点大小
     */
    private static final int DEFAULT_ANCHOR_WIDTH_DP = 30;


    /**
     * 显示图像
     */
    private ImageView imageView;
    /**
     * 父控件大小
     */
    private float[] mParentSize = new float[2];
    /**
     * 显示图大小
     */
    private float[] mDrawableSize = new float[2];
    /**
     * 文本框堆栈
     */
    private LinkedList<ImageTextView> mImageTextLink = new LinkedList<>();
    /**
     * 当前所选文本框
     */
    private ImageTextView mCurrentText;

    private OnItemCheckListener mOnItemCheckListener;

    private float mCurrentAlpha = 1.0f;

    public CharactersLayout(Context context) {
        this(context, null);
    }

    public CharactersLayout(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public CharactersLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

        imageView = new ImageView(getContext());
        imageView.setAdjustViewBounds(true);
        //  image view 永远填充到最大，迫使layout缩放
        addView(imageView, new LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
        imageView.post(() -> addCharactersView(true));
    }

    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        super.onLayout(changed, left, top, right, bottom);
        mParentSize[0] = ((ViewGroup) getParent()).getWidth();
        mParentSize[1] = ((ViewGroup) getParent()).getHeight();
        float scale = Math.min(mParentSize[0] / mDrawableSize[0], mParentSize[1] / mDrawableSize[1]);
        LayoutParams layoutParams = (LayoutParams) imageView.getLayoutParams();
        layoutParams.width = (int) (mDrawableSize[0] * scale);
        layoutParams.height = (int) (mDrawableSize[1] * scale);
        imageView.setLayoutParams(layoutParams);
    }

    /**
     * 添加文本框
     *
     * @param isInit 是否为初始化
     */
    private void addCharactersView(boolean isInit) {
        if (isInit) {
            ViewGroup.LayoutParams layoutParams = getLayoutParams();
            layoutParams.width = imageView.getWidth();
            layoutParams.height = imageView.getHeight();
            setLayoutParams(layoutParams);
        }
        final ImageTextView imageTextView = new ImageTextView(getContext());
        float density = getContext().getResources().getDisplayMetrics().density;
        float width = density * (DEFAULT_FRAME_WIDTH_DP + DEFAULT_ANCHOR_WIDTH_DP);
        if (imageView.getWidth() < width) {
            float scale = imageView.getWidth() / width;
            imageTextView.setScaleX(scale);
            imageTextView.setScaleY(scale);
            imageTextView.setTranslationX((imageView.getWidth() - width) * 0.5f);
        }
        if (!isInit) {
            imageTextView.setTextAlpha(mCurrentAlpha);
            imageTextView.setText(mCurrentText.getTextView().getText());
            imageTextView.setTextColor(mCurrentText.getTextView().getCurrentTextColor());
        }
        mImageTextLink.addLast(imageTextView);
        addView(imageTextView);
        RelativeLayout.LayoutParams layoutParams = (RelativeLayout.LayoutParams) imageTextView.getLayoutParams();
        layoutParams.addRule(CENTER_IN_PARENT);
        imageTextView.setLayoutParams(layoutParams);
        mCurrentText = imageTextView;
        imageTextView.setChecked(true);
        imageTextView.setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onClickAddView() {
                mCurrentText.setChecked(false);
                addCharactersView(false);
            }

            @Override
            public void onClickDeleteView(View view) {
                if (mImageTextLink.size() > 1) {
                    mImageTextLink.remove(view);
                    removeView(view);
                    mCurrentText = mImageTextLink.getLast();
                    mCurrentText.setChecked(true);
                } else {
                    Toast.makeText(getContext(), "至少保留一个文本框", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onClickInputView(View view) {
                if (mCurrentText == view) {
                    InputDialog inputDialog = new InputDialog(getContext(), mCurrentText.getTextView());
                    inputDialog.setOnInputCompleteListener(textView -> {
                        mCurrentText.setText(textView.getText());
                        mCurrentText.setTextSize(textView.getTextSize());
                    });
                    inputDialog.show();
                } else {
                    mCurrentText.setChecked(false);
                    mCurrentText = (ImageTextView) view;
                    mCurrentText.setChecked(true);
                    mOnItemCheckListener.onChecked(mCurrentText.getTextView().getCurrentTextColor(), mCurrentText.getTextView().getAlpha());
                }
            }
        });
    }

    /**
     * 设置显示图
     *
     * @param bitmap bitmap
     */
    public void setImageBitmap(Bitmap bitmap) {
        imageView.setImageBitmap(bitmap);
        mDrawableSize[0] = bitmap.getWidth();
        mDrawableSize[1] = bitmap.getHeight();
    }

    /**
     * 设置文字颜色
     * 只改变当前的文本框
     *
     * @param color 颜色值
     */
    public void setTextColor(int color) {
        mCurrentText.setTextColor(color);
    }

    /**
     * 设置透明度
     *
     * @param alpha 透明度
     */
    public void setTextAlpha(float alpha) {
        mCurrentAlpha = alpha;
        mCurrentText.setTextAlpha(alpha);
    }

    /**
     * 生成图片
     *
     * @return 图片
     */
    public Bitmap buildImageDrawable() {
        for (int i = 0; i < mImageTextLink.size(); ++i) {
            mImageTextLink.get(i).setChecked(false);
            mImageTextLink.get(i).setTextHide();
        }
        return ImageUtils.View2Bitmap(this);
    }

    public void setOnItemCheckListener(OnItemCheckListener onItemCheckListener) {
        this.mOnItemCheckListener = onItemCheckListener;
    }

    public interface OnItemCheckListener {
        void onChecked(@ColorInt int color, float alpha);
    }

}
