package com.uniquext.android.imageeditor.activity.controller;

import com.uniquext.android.imageeditor.helper.DrawableManager;

/**
 * @author penghaitao
 * @version 1.0
 * @date 2018/7/27 11:03
 * @description
 */
public class MainControllerPresenter implements MainControllerContract.Presenter {

    /**
     * 旋转
     */
    static final String OPERATE_ROTATE = "scheme://com.uniquext.image-editor/rotate";
    /**
     * 模糊
     */
    static final String OPERATE_MOSAIC = "scheme://com.uniquext.image-editor/mosaic";
    /**
     * 裁剪
     */
    static final String OPERATE_TRIM = "scheme://com.uniquext.image-editor/trim";
    /**
     * 文字
     */
    static final String OPERATE_CHARACTERS = "scheme://com.uniquext.image-editor/characters";
    /**
     * Contract View
     */
    private MainControllerContract.View mContractView;

    MainControllerPresenter(MainControllerContract.View view) {
        mContractView = view;
    }

    @Override
    public void start() {
        mContractView.init();
    }

    @Override
    public void resume() {
        mContractView.init();
        mContractView.refreshStackFlag();
    }

    @Override
    public void revoke() {
        if (DrawableManager.getInstance().revoke()) {
            mContractView.showRevoke();
            mContractView.refreshStackFlag();
        }
    }

    @Override
    public void forward() {
        if (DrawableManager.getInstance().forward()) {
            mContractView.showForward();
            mContractView.refreshStackFlag();
        }
    }

    @Override
    public void rotate() {
        mContractView.showRotate();
    }

    @Override
    public void characters() {
        mContractView.showCharacters();
    }

    @Override
    public void mosaic() {
        mContractView.showMosaic();
    }

    @Override
    public void trim() {
        mContractView.showTrim();
    }

    @Override
    public void exit() {
        DrawableManager.getInstance().recycle();
        mContractView.exit();
        mContractView.detach();
    }

    @Override
    public void save() {
        DrawableManager.getInstance().save();
        mContractView.save();
        mContractView.detach();
    }

    @Override
    public void recycle() {
        mContractView = null;
    }
}
