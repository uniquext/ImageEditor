package com.uniquext.android.imageeditor.core;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;

/**
 * @param <T> 支持者
 * @author penghaitao
 * @version 1.0
 * @date 2018/7/27 10:57
 * @description
 */
public abstract class AbstractMVPActivity<T extends BaseContract.BasePresenter> extends AppCompatActivity {

    /**
     * 支持者
     */
    protected T mPresenter;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(getLayoutId());
        mPresenter = getPresenter();
        initView();
        initData(savedInstanceState);
        initEvent();
    }

    @Override
    protected void onDestroy() {
        mPresenter.recycle();
        mPresenter = null;
        super.onDestroy();
    }

    /**
     * 获取支持者
     *
     * @return Presenter
     */
    protected abstract T getPresenter();

    /**
     * 获取layout资源
     *
     * @return layoutId
     */
    protected abstract int getLayoutId();

    /**
     * 初始化View
     */
    protected abstract void initView();

    /**
     * 初始化Data
     *
     * @param savedInstanceState 实例状态
     */
    protected abstract void initData(@Nullable Bundle savedInstanceState);

    /**
     * 初始化事件监听
     */
    protected abstract void initEvent();
}
