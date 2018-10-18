package com.uniquext.android.imageeditor.widget.characters;

import android.app.Dialog;
import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.AppCompatEditText;
import android.support.v7.widget.AppCompatTextView;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;

import com.uniquext.android.imageeditor.R;

/**
 * @author penghaitao
 * @version 1.0
 * @date 2018/7/31 13:54
 * @description
 */
public class InputDialog extends Dialog {

    /**
     * 默认提示文本
     */
    private static final String DEFAULT_MESSAGE_TIP = "点击输入文字";

    /**
     * 默认文本大小
     */
    private static final int DEFAULT_TEXT_SIZE_SP = 18;
    /**
     * 最小文本大小
     */
    private static final int MIN_TEXT_SIZE_SP = 13;

    /**
     * 文本框
     */
    private AppCompatTextView mTextView;

    private AppCompatTextView mTvComplete;

    /**
     * 字数
     */
    private int mCount = 0;
    /**
     * 是否跳行
     */
    private boolean mFlag = false;
    /**
     * 输入完成监听
     */
    private OnInputCompleteListener mOnInputCompleteListener;

    public InputDialog(@NonNull Context context, AppCompatTextView textView) {
        super(context, R.style.InputDialog);
        View view = LayoutInflater.from(context).inflate(R.layout.dialog_input_characters, null);
        setContentView(view);
        mTextView = view.findViewById(R.id.tv_label);
        mTvComplete = view.findViewById(R.id.tv_complete);

        mTextView.setText(TextUtils.isEmpty(textView.getText()) ? DEFAULT_MESSAGE_TIP : textView.getText());
        mTextView.setTextSize(TypedValue.COMPLEX_UNIT_PX, textView.getTextSize());

        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE);

        AppCompatEditText editText = view.findViewById(R.id.ed_input);
        editText.setText(textView.getText());
        editText.setText(DEFAULT_MESSAGE_TIP.equals(textView.getText().toString()) ? "" : textView.getText());
        editText.setSelection(editText.length());
        editText.addTextChangedListener(new TextWatcher() {

            private CharSequence charSequence;

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                charSequence = mTextView.getText();
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if (TextUtils.isEmpty(s)) {
                    mTextView.setText(DEFAULT_MESSAGE_TIP);
                    mTvComplete.setTextColor(getContext().getResources().getColor(R.color.dialog_success_empty));
                } else {
                    mTextView.setText(s);
                    mTvComplete.setTextColor(getContext().getResources().getColor(R.color.text_theme_color));
                }
                int lineCount = mTextView.getLineCount();
                if (lineCount > 4) {
                    mTextView.setText(charSequence);
                    return;
                }
                if (!mFlag && lineCount == 4) {
                    mFlag = true;
                    mCount = mTextView.length();
                }
                if (mFlag && mCount <= mTextView.length()) {
                    mTextView.setTextSize(TypedValue.COMPLEX_UNIT_SP, MIN_TEXT_SIZE_SP);
                } else {
                    mFlag = false;
                    mTextView.setTextSize(TypedValue.COMPLEX_UNIT_SP, DEFAULT_TEXT_SIZE_SP);
                }
            }
        });
        mTvComplete.setOnClickListener(v -> {
            if (mOnInputCompleteListener != null) {
                mOnInputCompleteListener.onComplete(mTextView);
            }
            this.dismiss();
        });
    }

    @Override
    public void show() {
        super.show();
        WindowManager.LayoutParams layoutParams = getWindow().getAttributes();
        layoutParams.width = ViewGroup.LayoutParams.MATCH_PARENT;
        layoutParams.height = ViewGroup.LayoutParams.MATCH_PARENT;
        getWindow().getDecorView().setPadding(0, 0, 0, 0);
        getWindow().setAttributes(layoutParams);
    }

    public void setOnInputCompleteListener(OnInputCompleteListener listener) {
        this.mOnInputCompleteListener = listener;
    }

    /**
     * 输入监听接口
     */
    public interface OnInputCompleteListener {
        /**
         * 完成回调
         *
         * @param textView textView
         */
        void onComplete(AppCompatTextView textView);
    }
}
