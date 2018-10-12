package com.mpl.GrowthTeacher.Adapter;


import android.annotation.SuppressLint;
import android.content.Context;
import android.widget.TextView;

import com.mpl.GrowthTeacher.Bean.GetStarInfoInfoItem;
import com.mpl.GrowthTeacher.R;

import java.util.List;


public class GetScoreInfoInfoListViewAdapter extends ListViewAdapter<GetStarInfoInfoItem> {
    private Context context1;

    //MyAdapter需要一个Context，通过Context获得Layout.inflater，然后通过inflater加载item的布局
    public GetScoreInfoInfoListViewAdapter(Context context, List<GetStarInfoInfoItem> datas) {
        super(context, datas, R.layout.get_score_info_info_item);
        context1 = context;
    }

    @SuppressLint("NewApi")
    @Override
    public void convert(final ViewHolder holder, GetStarInfoInfoItem bean) {
        ((TextView) holder.getView(R.id.tv_title)).setText(bean.getName());

        ((TextView) holder.getView(R.id.tv_score)).setText(bean.getTotal_point() + "分");

        ((TextView) holder.getView(R.id.tv_name)).setText(bean.getComplete_name());

        ((TextView) holder.getView(R.id.tv_time)).setText(bean.getUpdated_at());

    }
}