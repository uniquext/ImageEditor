package com.uniquext.android.imageeditor.widget.characters;

import android.view.View;

/**
 * @author penghaitao
 * @version 1.0
 * @date 2018/7/31 12:04
 * @description
 */
public interface OnItemClickListener {
    /**
     * 新增
     */
    void onClickAddView();

    /**
     * 删除
     *
     * @param view view
     */
    void onClickDeleteView(View view);

    /**
     * 输入
     *
     * @param view view
     */
    void onClickInputView(View view);
}
