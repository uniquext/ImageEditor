package com.uniquext.android.imageeditor.activity.characters;

import android.graphics.Bitmap;

import com.uniquext.android.imageeditor.helper.DrawableManager;

/**
 * @author penghaitao
 * @version 1.0
 * @date 2018/7/30 10:35
 * @description
 */
public class CharactersPresenter implements CharactersContract.Presenter {


    /**
     * Contract View
     */
    private CharactersContract.View mContractView;

    CharactersPresenter(CharactersContract.View view) {
        this.mContractView = view;
    }

    @Override
    public void start() {
        mContractView.init();
        mContractView.initColorSelector();
    }

    @Override
    public void recycle() {
        mContractView = null;
    }

    @Override
    public void switchSize() {
        mContractView.showSizeBar();
    }

    @Override
    public void switchColor() {
        mContractView.showColorBar();
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
