package com.uniquext.android.imageeditor.activity.trim;

import android.graphics.Bitmap;

import com.uniquext.android.imageeditor.core.BaseContract;

/**
 * @author penghaitao
 * @version 1.0
 * @date 2018/7/26 17:19
 * @description 控制界面的契约管理
 */
public interface TrimContract {
    /**
     * 视图接口
     */
    interface View extends BaseContract.BaseView<Presenter> {
        /**
         * 自由缩放
         */
        void showFreeRate();

        /**
         * 1:1
         */
        void showRate1();

        /**
         * 4:3
         */
        void showRate2();

        /**
         * 16:9
         */
        void showRate3();

    }

    /**
     * 操作者接口
     */
    interface Presenter extends BaseContract.BasePresenter {
        /**
         * 设置宽高比
         *
         * @param rate 比例类型
         */
        void setRate(@TrimPresenter.Rate int rate);

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
