package com.uniquext.android.imageeditor.activity.characters.adapter;

import android.content.Context;
import android.graphics.Color;
import android.support.annotation.ColorInt;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.uniquext.android.imageeditor.R;
import com.uniquext.android.imageeditor.widget.ColorSelectorView;

import java.util.List;

/**
 * @author penghaitao
 * @version 1.0
 * @date 2018/8/15 18:15
 * @description
 */
public class ColorSelectorAdapter extends RecyclerView.Adapter<ColorSelectorHolder> {

    private Context mContext;
    @ColorInt
    private int mCurrentColor;
    private List<String> mColors;
    private OnColorChangeListener mOnColorChangeListener;

    public ColorSelectorAdapter(Context context, List<String> colors, String checked) {
        this.mContext = context;
        this.mColors = colors;
        this.mCurrentColor = Color.parseColor(checked);
    }

    @NonNull
    @Override
    public ColorSelectorHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(mContext);
        View view = layoutInflater.inflate(R.layout.item_color_selector, parent, false);
        return new ColorSelectorHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ColorSelectorHolder holder, int position) {
        ColorSelectorView colorSelectorView = holder.itemView.findViewById(R.id.color_selector);
        colorSelectorView.setColor(Color.parseColor(mColors.get(position)));
        colorSelectorView.setSelected(mCurrentColor == colorSelectorView.getColor());
        colorSelectorView.setTag(colorSelectorView.getColor());
        colorSelectorView.setOnClickListener(v -> {
            mCurrentColor = (int) v.getTag();
            if (mOnColorChangeListener != null) {
                mOnColorChangeListener.onColorChanged(mCurrentColor);
            }
            notifyDataSetChanged();
        });
    }

    @Override
    public int getItemCount() {
        return mColors.size();
    }

    public void setOnColorChangeListener(OnColorChangeListener onColorChangeListener) {
        this.mOnColorChangeListener = onColorChangeListener;
    }

    public void setCurrentColor(int color) {
        this.mCurrentColor = color;
    }

    public interface OnColorChangeListener {
        void onColorChanged(@ColorInt int color);
    }
}