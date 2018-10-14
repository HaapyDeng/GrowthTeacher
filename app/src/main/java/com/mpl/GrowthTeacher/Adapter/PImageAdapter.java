package com.mpl.GrowthTeacher.Adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

import com.mpl.GrowthTeacher.R;
import com.mpl.GrowthTeacher.Tools.DownImage;
import com.mpl.GrowthTeacher.View.SquareLayout;

import java.util.List;

public class PImageAdapter extends BaseAdapter {
    private List<String> data;
    private Context context;

    public PImageAdapter(Context context, List<String> image) {
        this.context = context;
        this.data = image;

    }

    @Override
    public int getCount() {
        return data.size();
    }

    @Override
    public Object getItem(int i) {

        return data.get(i);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup viewGroup) {
        final Holder holder;
        if (convertView == null) {
            holder = new Holder();
//            AbsListView.LayoutParams param = new AbsListView.LayoutParams(
//                    android.view.ViewGroup.LayoutParams.FILL_PARENT,
//                    120);//传入自己需要的宽高
//            convertView.setLayoutParams(param);
            convertView = LayoutInflater.from(context).inflate(R.layout.image_grid_view, null);
            holder.iv = (SquareLayout) convertView.findViewById(R.id.iv_image);
            DownImage downImage = null;
            downImage = new DownImage(data.get(position));
            downImage.loadImage(new DownImage.ImageCallBack() {

                @SuppressLint("NewApi")
                @Override
                public void getDrawable(Drawable drawable) {
                    (holder.iv).setBackground(drawable);
                }
            });
        }
        return convertView;
    }

    class Holder {
        SquareLayout iv;
    }
}
