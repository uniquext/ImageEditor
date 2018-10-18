package com.uniquext.android.imageeditor.activity.characters;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.constraint.ConstraintLayout;
import android.support.constraint.Group;
import android.support.v7.widget.AppCompatImageView;
import android.support.v7.widget.AppCompatTextView;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.SeekBar;

import com.uniquext.android.imageeditor.R;
import com.uniquext.android.imageeditor.activity.characters.adapter.ColorSelectorAdapter;
import com.uniquext.android.imageeditor.core.AbstractMVPActivity;
import com.uniquext.android.imageeditor.helper.DrawableManager;
import com.uniquext.android.imageeditor.widget.characters.CharactersLayout;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;

/**
 * @author penghaitao
 * @version 1.0
 * @date 2018/7/30 10:34
 * @description
 */
public class CharactersOperateActivity extends AbstractMVPActivity<CharactersPresenter>
        implements CharactersContract.View {
    /**
     * 图片文字视图
     */
    private CharactersLayout mCharactersLayout = null;

    /**
     * 透明度操作条
     */
    private Group mGroupTextBar;

    /**
     * 文字
     */
    private AppCompatTextView mTvSize;
    /**
     * 颜色
     */
    private AppCompatTextView mTvColor;
    /**
     * 下划线
     */
    private View mUnderLine = null;
    /**
     * 取消
     */
    private AppCompatImageView mIvCancel;
    /**
     * 确认
     */
    private AppCompatImageView mIvConfirm;

    /**
     * 透明度调整
     */
    private SeekBar mSeekAlpha = null;
    private AppCompatTextView mAlphaLabel;

    /**
     * 颜色选择器
     */
    private RecyclerView mRvColorSelector;
    private ColorSelectorAdapter mColorSelectorAdapter;

    @Override
    protected CharactersPresenter getPresenter() {
        return new CharactersPresenter(this);
    }

    @Override
    protected int getLayoutId() {
        return R.layout.activity_characters;
    }

    @Override
    protected void initView() {
        mCharactersLayout = findViewById(R.id.layout_content);

        mGroupTextBar = findViewById(R.id.group_size_bar);

        mSeekAlpha = findViewById(R.id.seek_bar_rate);
        mAlphaLabel = findViewById(R.id.label_end);
        mRvColorSelector = findViewById(R.id.rv_color_selector);

        mUnderLine = findViewById(R.id.view_checked_underline);
        mTvSize = findViewById(R.id.tv_text);
        mTvColor = findViewById(R.id.tv_color);

        mIvCancel = findViewById(R.id.iv_cancel);
        mIvConfirm = findViewById(R.id.iv_confirm);
    }

    @Override
    protected void initData(@Nullable Bundle savedInstanceState) {
        mPresenter.start();
    }

    @Override
    protected void initEvent() {
        mTvSize.setOnClickListener(v -> mPresenter.switchSize());
        mTvColor.setOnClickListener(v -> mPresenter.switchColor());

        mIvCancel.setOnClickListener(v -> mPresenter.cancel());
        mIvConfirm.setOnClickListener(v -> mPresenter.confirm(mCharactersLayout.buildImageDrawable()));

        mSeekAlpha.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                mCharactersLayout.setTextAlpha(progress / 100f);
                mAlphaLabel.setText(String.format(Locale.CHINA, "%d%%", progress));
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });

        mCharactersLayout.setOnItemCheckListener((color, alpha) -> {
            mColorSelectorAdapter.setCurrentColor(color);
            mColorSelectorAdapter.notifyDataSetChanged();
            mSeekAlpha.setProgress((int) (alpha * 100f));
            mAlphaLabel.setText(String.format(Locale.CHINA, "%d%%", mSeekAlpha.getProgress()));
        });
    }

    @Override
    public void init() {
        mSeekAlpha.setProgress(100);
        mCharactersLayout.setImageBitmap(DrawableManager.getInstance().getDrawableBitmap());
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
    public void initColorSelector() {
        List<String> colors = new ArrayList<>(Arrays.asList(getResources().getStringArray(R.array.colors)));
        mColorSelectorAdapter = new ColorSelectorAdapter(this, colors, colors.get(0));
        mColorSelectorAdapter.setOnColorChangeListener(color -> mCharactersLayout.setTextColor(color));
        mRvColorSelector.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));
        mRvColorSelector.setAdapter(mColorSelectorAdapter);
    }

    @Override
    public void showSizeBar() {
        setUnderLineHorizontalBias(mTvSize);
        mTvSize.setTextColor(getResources().getColor(R.color.text_theme_color));
        mTvColor.setTextColor(getResources().getColor(R.color.text_default_color));
        mGroupTextBar.setVisibility(View.VISIBLE);
        mRvColorSelector.setVisibility(View.GONE);
    }

    @Override
    public void showColorBar() {
        setUnderLineHorizontalBias(mTvColor);
        mTvSize.setTextColor(getResources().getColor(R.color.text_default_color));
        mTvColor.setTextColor(getResources().getColor(R.color.text_theme_color));
        mGroupTextBar.setVisibility(View.GONE);
        mRvColorSelector.setVisibility(View.VISIBLE);
    }

    @Override
    public void setUnderLineHorizontalBias(View sourceView) {
        ConstraintLayout.LayoutParams layoutParams = ((ConstraintLayout.LayoutParams) mUnderLine.getLayoutParams());
        layoutParams.horizontalBias = ((ConstraintLayout.LayoutParams) sourceView.getLayoutParams()).horizontalBias;
        mUnderLine.setLayoutParams(layoutParams);
    }

}
