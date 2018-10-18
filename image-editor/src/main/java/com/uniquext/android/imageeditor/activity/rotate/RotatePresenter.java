package com.uniquext.android.imageeditor.activity.rotate;

import android.graphics.Bitmap;

import com.uniquext.android.imageeditor.helper.DrawableManager;

/**
 * @author penghaitao
 * @version 1.0
 * @date 2018/7/27 11:03
 * @description
 */
public class RotatePresenter implements RotateContract.Presenter {

    /**
     * Contract View
     */
    private RotateContract.View mContractView;

    RotatePresenter(RotateContract.View view) {
        mContractView = view;
    }

    @Override
    public void start() {
        mContractView.init();
    }

    @Override
    public void recycle() {
        mContractView = null;
    }

    @Override
    public void left() {
        mContractView.rotateLeft();
    }

    @Override
    public void right() {
        mContractView.rotateRight();
    }

    @Override
    public void cancel() {
        mContractView.detach();
    }

    @Override
    public void confirm(Bitmap bitmap) {
        DrawableManager.getInstance().pushBitmap(bitmap);
        mContractView.detach();
    }
}
