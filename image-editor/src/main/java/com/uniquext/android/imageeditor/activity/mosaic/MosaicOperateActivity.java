package com.uniquext.android.imageeditor.activity.mosaic;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.AppCompatImageView;
import android.widget.SeekBar;
import android.widget.TextView;

import com.uniquext.android.imageeditor.R;
import com.uniquext.android.imageeditor.core.AbstractMVPActivity;
import com.uniquext.android.imageeditor.helper.DrawableManager;
import com.uniquext.android.imageeditor.widget.MosaicView;

import java.util.Locale;


/**
 * @author penghaitao
 * @version 1.0
 * @date 2018/7/24 14:39
 * @description
 */
public class MosaicOperateActivity extends AbstractMVPActivity<MosaicPresenter> implements MosaicContract.View {
    /**
     * 显示图
     */
    private MosaicView mMosaicView;
    /**
     * 画笔宽度
     */
    private SeekBar mSeekWidth;
    /**
     * 模糊度
     */
    private SeekBar mSeekMosaic;
    /**
     * 撤销
     */
    private AppCompatImageView mIvRevoke;
    /**
     * 模糊值
     */
    private TextView mTvMosaicRate;
    /**
     * 取消
     */
    private AppCompatImageView mIvCancel;
    /**
     * 确认
     */
    private AppCompatImageView mIvConfirm;


    @Override
    protected MosaicPresenter getPresenter() {
        return new MosaicPresenter(this);
    }

    @Override
    protected int getLayoutId() {
        return R.layout.activity_mosaic;
    }

    @Override
    protected void initView() {
        mMosaicView = findViewById(R.id.view_image_mosaic);
        mSeekWidth = findViewById(R.id.seek_bar_brush);
        mSeekMosaic = findViewById(R.id.seek_bar_rate);
        mTvMosaicRate = findViewById(R.id.tv_rate);
        mIvRevoke = findViewById(R.id.iv_revoke);
        mIvCancel = findViewById(R.id.iv_cancel);
        mIvConfirm = findViewById(R.id.iv_confirm);
    }

    @Override
    protected void initData(@Nullable Bundle savedInstanceState) {
        mPresenter.start();
    }

    @Override
    protected void initEvent() {
        mIvRevoke.setOnClickListener(v -> mMosaicView.revoke());
        mIvCancel.setOnClickListener(v -> mPresenter.cancel());
        mIvConfirm.setOnClickListener(v -> mPresenter.confirm(mMosaicView.getMosaicBitmap()));

        mMosaicView.setOnMosaicChangeListener(count -> mPresenter.setRevoke(count));

        mSeekWidth.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {

            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                setBrushWidth(seekBar.getProgress() / 100f);
            }
        });
        mSeekMosaic.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                mTvMosaicRate.setText(String.format(Locale.CHINA, "%d%%", progress));
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                setRate(seekBar.getProgress());
            }
        });
    }

    @Override
    public void init() {
        mMosaicView.setImageBitmap(DrawableManager.getInstance().getDrawableBitmap());
        mSeekMosaic.setProgress(50);
        mSeekWidth.setProgress(50);
        mTvMosaicRate.setText(String.format(Locale.CHINA, "%d%%", mSeekMosaic.getProgress()));
        setRate(mSeekMosaic.getProgress());
        setBrushWidth(mSeekWidth.getProgress() / 100f);
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
    public void showRevoke() {
        mIvRevoke.setImageResource(R.mipmap.icon_revoke_able);
    }

    @Override
    public void showUnRevoke() {
        mIvRevoke.setImageResource(R.mipmap.icon_revoke_unable);
    }


    @Override
    public void setRate(float progress) {
        mMosaicView.setRate(progress);
    }

    @Override
    public void setBrushWidth(float progress) {
        mMosaicView.setBrushWidth(8 + 2 * progress * (42 - 8));
    }
}
