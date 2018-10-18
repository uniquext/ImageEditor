package com.uniquext.android.imageeditor.activity.controller;

import com.uniquext.android.imageeditor.core.BaseContract;

/**
 * @author penghaitao
 * @version 1.0
 * @date 2018/7/26 17:19
 * @description 控制界面的契约管理
 */
public interface MainControllerContract {
    /**
     * 视图接口
     */
    interface View extends BaseContract.BaseView<Presenter> {

        /**
         * 刷新操作栈标记
         */
        void refreshStackFlag();

        /**
         * 回退
         */
        void showRevoke();

        /**
         * 前进
         */
        void showForward();

        /**
         * 旋转
         */
        void showRotate();

        /**
         * 文字
         */
        void showCharacters();

        /**
         * 模糊
         */
        void showMosaic();

        /**
         * 裁剪
         */
        void showTrim();

        /**
         * 退出
         */
        void exit();

        /**
         * 保存
         */
        void save();

    }

    /**
     * 操作者接口
     */
    interface Presenter extends BaseContract.BasePresenter {

        /**
         * 恢复
         */
        void resume();

        /**
         * 撤销
         */
        void revoke();

        /**
         * 前进
         */
        void forward();

        /**
         * 旋转
         */
        void rotate();

        /**
         * 文字
         */
        void characters();

        /**
         * 模糊
         */
        void mosaic();

        /**
         * 裁剪
         */
        void trim();

        /**
         * 退出
         */
        void exit();

        /**
         * 保存
         */
        void save();
    }
}
