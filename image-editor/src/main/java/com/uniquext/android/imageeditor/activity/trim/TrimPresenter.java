package com.uniquext.android.imageeditor.activity.trim;

import android.graphics.Bitmap;
import android.support.annotation.IntDef;

import com.uniquext.android.imageeditor.helper.DrawableManager;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 * @author penghaitao
 * @version 1.0
 * @date 2018/7/27 11:03
 * @description
 */
public class TrimPresenter implements TrimContract.Presenter {
    /**
     * 自由缩放
     */
    public static final int RATE_FREE = 0;
    /**
     * 1:1
     */
    public static final int RATE_1 = 1;
    /**
     * 4:3
     */
    public static final int RATE_2 = 2;
    /**
     * 16:9
     */
    public static final int RATE_3 = 3;

    /**
     * Contract View
     */
    private TrimContract.View mContractView;

    TrimPresenter(TrimContract.View view) {
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
    public void setRate(int rate) {
        switch (rate) {
            case RATE_FREE:
                mContractView.showFreeRate();
                break;
            case RATE_1:
                mContractView.showRate1();
                break;
            case RATE_2:
                mContractView.showRate2();
                break;
            case RATE_3:
                mContractView.showRate3();
                break;
            default:
                break;
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

    /**
     * 裁剪宽高比类型
     */
    @Retention(RetentionPolicy.SOURCE)
    @IntDef({RATE_FREE, RATE_1, RATE_2, RATE_3})
    @interface Rate {

    }
}
