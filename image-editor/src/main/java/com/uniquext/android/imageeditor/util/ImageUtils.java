package com.uniquext.android.imageeditor.util;

import android.graphics.Bitmap;
import android.view.View;

/**
 * @author penghaitao
 * @version 1.0
 * @date 2018/7/24 14:54
 * @description 图片工具类
 */
public class ImageUtils {
    /**
     * 将view生成图片
     *
     * @param view 控件
     * @return bitmap
     */
    public static Bitmap View2Bitmap(View view) {
        view.setDrawingCacheEnabled(true);
        view.buildDrawingCache();
        Bitmap bitmap = Bitmap.createBitmap(view.getDrawingCache());
        view.setDrawingCacheEnabled(false);
        return bitmap;
    }
}
