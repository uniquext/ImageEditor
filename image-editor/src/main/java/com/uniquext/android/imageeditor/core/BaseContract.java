package com.uniquext.android.imageeditor.core;

import android.content.Context;

/**
 * @author penghaitao
 * @version 1.0
 * @date 2018/7/27 10:58
 * @description
 */
public interface BaseContract {
    /**
     * 视图接口
     *
     * @param <T> Presenter 泛型
     */
    interface BaseView<T extends BasePresenter> {
        /**
         * 初始化
         */
        void init();

        /**
         * 获取视图上下文
         *
         * @return 上下文
         */
        Context getContext();

        /**
         * 从界面移除
         */
        void detach();

    }

    /**
     * Presenter
     */
    interface BasePresenter {
        /**
         * 开始
         */
        void start();

        /**
         * 释放
         */
        void recycle();

    }

}
