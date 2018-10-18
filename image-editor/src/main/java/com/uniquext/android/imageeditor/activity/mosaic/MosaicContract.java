package com.uniquext.android.imageeditor.activity.mosaic;

import android.graphics.Bitmap;

import com.uniquext.android.imageeditor.core.BaseContract;

/**
 * @author penghaitao
 * @version 1.0
 * @date 2018/7/26 17:19
 * @description 控制界面的契约管理
 */
public interface MosaicContract {
    /**
     * 视图接口
     */
    interface View extends BaseContract.BaseView<Presenter> {
        /**
         * 允许撤销
         */
        void showRevoke();

        /**
         * 不允许撤销
         */
        void showUnRevoke();

        /**
         * 设置硬度
         *
         * @param progress 进度
         */
        void setRate(float progress);

        /**
         * 设置画笔宽度
         *
         * @param progress 进度
         */
        void setBrushWidth(float progress);
    }

    /**
     * 操作者接口
     */
    interface Presenter extends BaseContract.BasePresenter {

        /**
         * 设置是否允许撤销
         *
         * @param count 当前图层数
         */
        void setRevoke(int count);

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
