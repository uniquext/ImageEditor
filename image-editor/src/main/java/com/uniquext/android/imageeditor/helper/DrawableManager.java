package com.uniquext.android.imageeditor.helper;

import android.graphics.Bitmap;

import java.util.Iterator;
import java.util.LinkedList;

/**
 * @author penghaitao
 * @version 1.0
 * @date 2018/7/26 17:36
 * @description 图像管理
 */
public final class DrawableManager {
    /**
     * 退出
     */
    public static final int EXIT = -1;
    /**
     * 保存
     */
    public static final int SAVE = 1;
    /**
     * 可撤销20步，一共最多21张
     */
    private static final int MAX_DEEP = 21;
    /**
     * 当前下标
     */
    private int mCurrent = -1;
    /**
     * 图像集
     */
    private LinkedList<Bitmap> mDrawableBitmaps = new LinkedList<>();
    /**
     * 原始图片
     */
    private Bitmap mSourceBitmap = null;

    private DrawableManager() {
    }

    /**
     * 获取单例
     *
     * @return 单例
     */
    public static DrawableManager getInstance() {
        return SingleHolder.INSTANCE;
    }

    /**
     * 是否允许撤销
     *
     * @return 是否允许撤销
     */
    public boolean revoke() {
        return mCurrent > 0;
    }

    /**
     * 是否允许前进
     *
     * @return 是否允许前进
     */
    public boolean forward() {
        return mCurrent < mDrawableBitmaps.size() - 1;
    }

    /**
     * 撤销动作
     *
     * @return 撤销后的图像
     */
    public Bitmap revokeAction() {
        return mDrawableBitmaps.get(--mCurrent);
    }

    /**
     * 获取当前的图像
     *
     * @return bitmap
     */
    public Bitmap getDrawableBitmap() {
        return mCurrent == -1 ? null : mDrawableBitmaps.get(mCurrent);
    }

    /**
     * 前进动作
     *
     * @return 前进后的图像
     */
    public Bitmap forwardAction() {
        return mDrawableBitmaps.get(++mCurrent);
    }

    /**
     * 初始化
     *
     * @param bitmap bitmap
     */
    public void init(Bitmap bitmap) {
        recycle();
        mCurrent = 0;
        mSourceBitmap = bitmap;
        mDrawableBitmaps.add(bitmap);
    }

    /**
     * 压栈
     *
     * @param bitmap bitmap
     */
    public void pushBitmap(Bitmap bitmap) {
        for (int i = mDrawableBitmaps.size() - 1; i > mCurrent; --i) {
            mDrawableBitmaps.get(i).recycle();
            mDrawableBitmaps.remove(i);
        }
        this.mDrawableBitmaps.add(bitmap);
        if (mDrawableBitmaps.size() > MAX_DEEP) {
            mDrawableBitmaps.removeFirst();
        }
        mCurrent = mDrawableBitmaps.size() - 1;
    }

    /**
     * 保存当前bitmap
     * 并移除释放其他bitmap
     */
    public void save() {
        Iterator<Bitmap> iterator = mDrawableBitmaps.iterator();
        Bitmap current = getDrawableBitmap();
        while (iterator.hasNext()) {
            Bitmap bitmap = iterator.next();
            if (bitmap != null && bitmap != current) {
                bitmap.recycle();
                iterator.remove();
            }
        }
        mCurrent = 0;
    }

    /**
     * 释放所有资源
     */
    public void recycle() {
        for (Bitmap bitmap : mDrawableBitmaps) {
            if (bitmap != null && bitmap != mSourceBitmap) {
                bitmap.recycle();
            }
        }
        mDrawableBitmaps.clear();
        mCurrent = -1;
    }

    /**
     * 内部类单例
     */
    private static class SingleHolder {
        /**
         * 单例
         */
        private static final DrawableManager INSTANCE = new DrawableManager();
    }
}
