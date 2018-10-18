package com.uniquext.android.imageeditor.activity.characters;

import android.graphics.Bitmap;

import com.uniquext.android.imageeditor.activity.controller.MainControllerContract;
import com.uniquext.android.imageeditor.core.BaseContract;

/**
 * @author penghaitao
 * @version 1.0
 * @date 2018/7/30 10:35
 * @description
 */
public interface CharactersContract {
    /**
     * 视图接口
     */
    interface View extends BaseContract.BaseView<MainControllerContract.Presenter> {
        /**
         * 初始化颜色选择器
         */
        void initColorSelector();

        /**
         * 展示透明度操作条
         */
        void showSizeBar();

        /**
         * 展示颜色操作条
         */
        void showColorBar();

        /**
         * 设置下划线的相对量
         *
         * @param sourceView 参考View
         */
        void setUnderLineHorizontalBias(android.view.View sourceView);
    }

    /**
     * 操作接口
     */
    interface Presenter extends BaseContract.BasePresenter {
        /**
         * 选择透明度
         */
        void switchSize();

        /**
         * 选择颜色
         */
        void switchColor();

        /**
         * 取消
         */
        void cancel();

        /**
         * 确认
         *
         * @param bitmap bitmap
         */
        void confirm(Bitmap bitmap);
    }
}
