package com.uniquext.android.imageeditor.activity.rotate;

import android.graphics.Bitmap;

import com.uniquext.android.imageeditor.core.BaseContract;

/**
 * @author penghaitao
 * @version 1.0
 * @date 2018/7/26 17:19
 * @description 控制界面的契约管理
 */
public interface RotateContract {
    /**
     * 视图接口
     */
    interface View extends BaseContract.BaseView<Presenter> {
        /**
         * 左旋
         */
        void rotateLeft();

        /**
         * 右旋
         */
        void rotateRight();

    }

    /**
     * 操作者接口
     */
    interface Presenter extends BaseContract.BasePresenter {

        /**
         * 左旋
         */
        void left();

        /**
         * 右旋
         */
        void right();

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
