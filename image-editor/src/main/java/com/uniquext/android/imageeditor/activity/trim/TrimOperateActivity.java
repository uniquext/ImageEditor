package com.uniquext.android.imageeditor.activity.trim;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.AppCompatImageView;
import android.support.v7.widget.AppCompatTextView;

import com.uniquext.android.imageeditor.R;
import com.uniquext.android.imageeditor.core.AbstractMVPActivity;
import com.uniquext.android.imageeditor.helper.DrawableManager;
import com.uniquext.android.imageeditor.widget.TrimView;

import static com.uniquext.android.imageeditor.widget.TrimView.FREE_RATIO;


/**
 * @author penghaitao
 * @version 1.0
 * @date 2018/7/26 9:25
 * @description
 */
public class TrimOperateActivity extends AbstractMVPActivity<TrimPresenter> implements TrimContract.View {

    /**
     * 显示图
     */
    private TrimView mTrimView;
    /**
     * 自由
     */
    private AppCompatImageView mIvFree;
    private AppCompatTextView mTvFree;
    /**
     * 1:1
     */
    private AppCompatImageView mIvRate1;
    private AppCompatTextView mTvRate1;
    /**
     * 4:3
     */
    private AppCompatImageView mIvRate2;
    private AppCompatTextView mTvRate2;
    /**
     * 16:9
     */
    private AppCompatImageView mIvRate3;
    private AppCompatTextView mTvRate3;

    /**
     * 取消
     */
    private AppCompatImageView mIvCancel;
    /**
     * 确认
     */
    private AppCompatImageView mIvConfirm;

    @Override
    protected TrimPresenter getPresenter() {
        return new TrimPresenter(this);
    }

    @Override
    protected int getLayoutId() {
        return R.layout.activity_trim;
    }

    @Override
    protected void initView() {
        mTrimView = findViewById(R.id.view_image_trim);
        mIvFree = findViewById(R.id.iv_rotate_rate_free);
        mTvFree = findViewById(R.id.tv_rotate_rate_free);
        mIvRate1 = findViewById(R.id.iv_rotate_rate_1);
        mTvRate1 = findViewById(R.id.tv_rotate_rate_1);
        mIvRate2 = findViewById(R.id.iv_rotate_rate_2);
        mTvRate2 = findViewById(R.id.tv_rotate_rate_2);
        mIvRate3 = findViewById(R.id.iv_rotate_rate_3);
        mTvRate3 = findViewById(R.id.tv_rotate_rate_3);
        mIvCancel = findViewById(R.id.iv_cancel);
        mIvConfirm = findViewById(R.id.iv_confirm);
    }

    @Override
    protected void initData(@Nullable Bundle savedInstanceState) {
        mPresenter.start();
    }

    @Override
    protected void initEvent() {
        mIvFree.setOnClickListener(v -> mPresenter.setRate(TrimPresenter.RATE_FREE));
        mIvRate1.setOnClickListener(v -> mPresenter.setRate(TrimPresenter.RATE_1));
        mIvRate2.setOnClickListener(v -> mPresenter.setRate(TrimPresenter.RATE_2));
        mIvRate3.setOnClickListener(v -> mPresenter.setRate(TrimPresenter.RATE_3));

        mIvCancel.setOnClickListener(v -> mPresenter.cancel());
        mIvConfirm.setOnClickListener(v -> mPresenter.confirm(mTrimView.clip()));
    }

    @Override
    public void init() {
        mTrimView.setImageBitmap(DrawableManager.getInstance().getDrawableBitmap());
    }

    @Override
    public Context getContext() {
        return this;
    }

    @Override
    public void detach() {
        onBackPressed();
    }

    @Override
    public void showFreeRate() {
        mTrimView.setFrameRatio(FREE_RATIO);
        mIvFree.setImageResource(R.mipmap.icon_rotate_rate_free_checked);
        mIvRate1.setImageResource(R.mipmap.icon_rotate_rate_1_normal);
        mIvRate2.setImageResource(R.mipmap.icon_rotate_rate_2_normal);
        mIvRate3.setImageResource(R.mipmap.icon_rotate_rate_3_normal);


        mTvFree.setTextColor(getResources().getColor(R.color.text_theme_color));
        mTvRate1.setTextColor(getResources().getColor(R.color.text_default_color));
        mTvRate2.setTextColor(getResources().getColor(R.color.text_default_color));
        mTvRate3.setTextColor(getResources().getColor(R.color.text_default_color));
    }

    @Override
    public void showRate1() {
        mTrimView.setFrameRatio(1f);
        mIvFree.setImageResource(R.mipmap.icon_rotate_rate_free_normal);
        mIvRate1.setImageResource(R.mipmap.icon_rotate_rate_1_checked);
        mIvRate2.setImageResource(R.mipmap.icon_rotate_rate_2_normal);
        mIvRate3.setImageResource(R.mipmap.icon_rotate_rate_3_normal);

        mTvFree.setTextColor(getResources().getColor(R.color.text_default_color));
        mTvRate1.setTextColor(getResources().getColor(R.color.text_theme_color));
        mTvRate2.setTextColor(getResources().getColor(R.color.text_default_color));
        mTvRate3.setTextColor(getResources().getColor(R.color.text_default_color));
    }

    @Override
    public void showRate2() {
        mTrimView.setFrameRatio(4f / 3f);
        mIvFree.setImageResource(R.mipmap.icon_rotate_rate_free_normal);
        mIvRate1.setImageResource(R.mipmap.icon_rotate_rate_1_normal);
        mIvRate2.setImageResource(R.mipmap.icon_rotate_rate_2_checked);
        mIvRate3.setImageResource(R.mipmap.icon_rotate_rate_3_normal);

        mTvFree.setTextColor(getResources().getColor(R.color.text_default_color));
        mTvRate1.setTextColor(getResources().getColor(R.color.text_default_color));
        mTvRate2.setTextColor(getResources().getColor(R.color.text_theme_color));
        mTvRate3.setTextColor(getResources().getColor(R.color.text_default_color));
    }

    @Override
    public void showRate3() {
        mTrimView.setFrameRatio(16f / 9f);
        mIvFree.setImageResource(R.mipmap.icon_rotate_rate_free_normal);
        mIvRate1.setImageResource(R.mipmap.icon_rotate_rate_1_normal);
        mIvRate2.setImageResource(R.mipmap.icon_rotate_rate_2_normal);
        mIvRate3.setImageResource(R.mipmap.icon_rotate_rate_3_checked);

        mTvFree.setTextColor(getResources().getColor(R.color.text_default_color));
        mTvRate1.setTextColor(getResources().getColor(R.color.text_default_color));
        mTvRate2.setTextColor(getResources().getColor(R.color.text_default_color));
        mTvRate3.setTextColor(getResources().getColor(R.color.text_theme_color));
    }
}
