package com.uniquext.android.imageeditor.activity.mosaic;

import android.graphics.Bitmap;

import com.uniquext.android.imageeditor.helper.DrawableManager;

/**
 * @author penghaitao
 * @version 1.0
 * @date 2018/7/27 11:03
 * @description
 */
public class MosaicPresenter implements MosaicContract.Presenter {

    /**
     * Contract View
     */
    private MosaicContract.View mContractView;

    MosaicPresenter(MosaicContract.View view) {
        mContractView = view;
    }


    @Override
    public void setRevoke(int count) {
        if (count > 0) {
            mContractView.showRevoke();
        } else {
            mContractView.showUnRevoke();
        }
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

    @Override
    public void start() {
        mContractView.init();
    }

    @Override
    public void recycle() {
        mContractView = null;
    }
}
