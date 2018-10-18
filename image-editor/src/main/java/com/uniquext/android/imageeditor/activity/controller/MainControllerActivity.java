package com.uniquext.android.imageeditor.activity.controller;

import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.AppCompatImageView;
import android.widget.TextView;

import com.uniquext.android.imageeditor.R;
import com.uniquext.android.imageeditor.core.AbstractMVPActivity;
import com.uniquext.android.imageeditor.helper.DrawableManager;

import static com.uniquext.android.imageeditor.activity.controller.MainControllerPresenter.OPERATE_CHARACTERS;
import static com.uniquext.android.imageeditor.activity.controller.MainControllerPresenter.OPERATE_MOSAIC;
import static com.uniquext.android.imageeditor.activity.controller.MainControllerPresenter.OPERATE_ROTATE;
import static com.uniquext.android.imageeditor.activity.controller.MainControllerPresenter.OPERATE_TRIM;
import static com.uniquext.android.imageeditor.helper.DrawableManager.EXIT;
import static com.uniquext.android.imageeditor.helper.DrawableManager.SAVE;

/**
 * @author penghaitao
 * @version 1.0
 * @date 2018/7/27 10:46
 * @description 主控制界面
 */
public class MainControllerActivity extends AbstractMVPActivity<MainControllerPresenter>
        implements MainControllerContract.View {

    /**
     * 返回键
     */
    private TextView mTvBack;
    /**
     * 保存
     */
    private TextView mTvSave;
    /**
     * 撤销
     */
    private AppCompatImageView mIvRevoke;
    /**
     * 前进
     */
    private AppCompatImageView mIvForward;
    /**
     * 资源图片
     */
    private AppCompatImageView mIvSourceDrawable;
    /**
     * 旋转
     */
    private TextView mTvRotate;
    /**
     * 文字
     */
    private TextView mTvCharacters;
    /**
     * 模糊
     */
    private TextView mTvMosaic;
    /**
     * 裁剪
     */
    private TextView mTvTrim;

    @Override
    protected int getLayoutId() {
        return R.layout.activity_controller;
    }

    @Override
    protected MainControllerPresenter getPresenter() {
        return new MainControllerPresenter(this);
    }

    @Override
    protected void initView() {
        this.mTvBack = findViewById(R.id.tv_back);
        this.mTvSave = findViewById(R.id.tv_save);
        this.mIvRevoke = findViewById(R.id.iv_revoke);
        this.mIvForward = findViewById(R.id.iv_forward);
        this.mIvSourceDrawable = findViewById(R.id.iv_source_drawable);
        this.mTvRotate = findViewById(R.id.tv_rotate);
        this.mTvCharacters = findViewById(R.id.tv_characters);
        this.mTvMosaic = findViewById(R.id.tv_mosaic);
        this.mTvTrim = findViewById(R.id.tv_trim);
    }

    @Override
    protected void initData(@Nullable Bundle savedInstanceState) {
        mPresenter.start();
    }

    @Override
    protected void initEvent() {
        mTvBack.setOnClickListener(v -> new AlertDialog.Builder(this)
                .setMessage("确定要放弃当前修改吗？")
                .setNegativeButton("取消", null)
                .setPositiveButton("确定", (dialog, which) -> mPresenter.exit()).show());
        mIvRevoke.setOnClickListener(v -> mPresenter.revoke());
        mIvForward.setOnClickListener(v -> mPresenter.forward());
        mTvSave.setOnClickListener(v -> new AlertDialog.Builder(this)
                .setMessage("保存后将不可撤销，确定要继续吗？")
                .setNegativeButton("取消", null)
                .setPositiveButton("确定", (dialog, which) -> mPresenter.save()).show());

        mTvRotate.setOnClickListener(v -> mPresenter.rotate());
        mTvCharacters.setOnClickListener(v -> mPresenter.characters());
        mTvMosaic.setOnClickListener(v -> mPresenter.mosaic());
        mTvTrim.setOnClickListener(v -> mPresenter.trim());
    }

    @Override
    protected void onResume() {
        super.onResume();
        mPresenter.resume();
    }

    @Override
    public void init() {
        mIvSourceDrawable.setImageBitmap(DrawableManager.getInstance().getDrawableBitmap());
    }

    @Override
    public Context getContext() {
        return this;
    }

    @Override
    public void detach() {
        finish();
    }

    @Override
    public void refreshStackFlag() {
        if (DrawableManager.getInstance().revoke()) {
            mIvRevoke.setImageResource(R.mipmap.icon_revoke_able);
        } else {
            mIvRevoke.setImageResource(R.mipmap.icon_revoke_unable);
        }
        if (DrawableManager.getInstance().forward()) {
            mIvForward.setImageResource(R.mipmap.icon_forward_able);
        } else {
            mIvForward.setImageResource(R.mipmap.icon_forward_unable);
        }
    }

    @Override
    public void showRevoke() {
        mIvSourceDrawable.setImageBitmap(DrawableManager.getInstance().revokeAction());
    }

    @Override
    public void showForward() {
        mIvSourceDrawable.setImageBitmap(DrawableManager.getInstance().forwardAction());
    }

    @Override
    public void showRotate() {
        startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(OPERATE_ROTATE)));
    }

    @Override
    public void showCharacters() {
        startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(OPERATE_CHARACTERS)));
    }

    @Override
    public void showMosaic() {
        startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(OPERATE_MOSAIC)));
    }

    @Override
    public void showTrim() {
        startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(OPERATE_TRIM)));
    }

    @Override
    public void exit() {
        setResult(EXIT);
    }

    @Override
    public void save() {
        setResult(SAVE);
    }

}
