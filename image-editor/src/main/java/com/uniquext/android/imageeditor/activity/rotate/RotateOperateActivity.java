package com.uniquext.android.imageeditor.activity.rotate;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.AppCompatImageView;

import com.uniquext.android.imageeditor.R;
import com.uniquext.android.imageeditor.core.AbstractMVPActivity;
import com.uniquext.android.imageeditor.helper.DrawableManager;
import com.uniquext.android.imageeditor.widget.RotateImageView;

/**
 * @author penghaitao
 * @version 1.0
 * @date 2018/7/23 15:53
 * @description
 */
public class RotateOperateActivity extends AbstractMVPActivity<RotatePresenter> implements RotateContract.View {

    /**
     * 显示图
     */
    private RotateImageView mRotateView;

    /**
     * 左转
     */
    private AppCompatImageView mIvRotateLeft;
    /**
     * 右转
     */
    private AppCompatImageView mIvRotateRight;

    /**
     * 取消
     */
    private AppCompatImageView mIvCancel;
    /**
     * 确认
     */
    private AppCompatImageView mIvConfirm;

    @Override
    protected RotatePresenter getPresenter() {
        return new RotatePresenter(this);
    }

    @Override
    protected int getLayoutId() {
        return R.layout.activity_rotate;
    }

    @Override
    protected void initView() {
        mRotateView = findViewById(R.id.view_image_rotate);
        mIvRotateLeft = findViewById(R.id.iv_rotate_left);
        mIvRotateRight = findViewById(R.id.iv_rotate_right);
        mIvCancel = findViewById(R.id.iv_cancel);
        mIvConfirm = findViewById(R.id.iv_confirm);
    }

    @Override
    protected void initData(@Nullable Bundle savedInstanceState) {
        mPresenter.start();
    }

    @Override
    protected void initEvent() {
        mIvRotateLeft.setOnClickListener(v -> mPresenter.left());
        mIvRotateRight.setOnClickListener(v -> mPresenter.right());
        mIvCancel.setOnClickListener(v -> mPresenter.cancel());
        mIvConfirm.setOnClickListener(v -> mPresenter.confirm(mRotateView.getImageBitmap()));
    }

    @Override
    public void init() {
        mRotateView.setImageBitmap(DrawableManager.getInstance().getDrawableBitmap());
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
    public void rotateLeft() {
        mRotateView.rotateLeft();
    }

    @Override
    public void rotateRight() {
        mRotateView.rotateRight();
    }
}
