package com.mpl.GrowthTeacher.Adapter;


import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.drawable.Drawable;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.mpl.GrowthTeacher.Bean.ChengJiuJinXingZhongItem;
import com.mpl.GrowthTeacher.R;
import com.mpl.GrowthTeacher.Tools.DownImage;

import java.util.List;


public class ChengJiuJinXingZhongListViewAdapter extends ListViewAdapter<ChengJiuJinXingZhongItem> {
    private Context context1;

    //MyAdapter需要一个Context，通过Context获得Layout.inflater，然后通过inflater加载item的布局
    public ChengJiuJinXingZhongListViewAdapter(Context context, List<ChengJiuJinXingZhongItem> datas) {
        super(context, datas, R.layout.complet_achieve_item);
        context1 = context;
    }

    @SuppressLint("NewApi")
    @Override
    public void convert(final ViewHolder holder, ChengJiuJinXingZhongItem bean) {
        ((TextView) holder.getView(R.id.tv_title)).setText(bean.getName());
        ((TextView) holder.getView(R.id.tv_1)).setText(bean.getCategory_name());

        Log.d("qqqqqqqqqqq", "" + bean.getType());
        if (bean.getImage().length() == 0) {
            Drawable drawable0 = context1.getResources().getDrawable(R.mipmap.achieve_default);
            ((ImageView) holder.getView(R.id.head_img)).setImageDrawable(drawable0);
        } else {
            DownImage downImage = new DownImage(bean.getImage());
            downImage.loadImage(new DownImage.ImageCallBack() {

                @Override
                public void getDrawable(Drawable drawable) {
                    ((ImageView) holder.getView(R.id.head_img)).setImageDrawable(drawable);
                }
            });
        }
        if (bean.getStatus().equals("0")) {
            ((TextView) holder.getView(R.id.status)).setText("待学生重做");
        } else if (bean.getStatus().equals("1")) {
        } else if (bean.getStatus().equals("2")) {
            ((TextView) holder.getView(R.id.status)).setText("待家长审核");
        } else if (bean.getStatus().equals("3")) {
            ((TextView) holder.getView(R.id.status)).setText("待老师审核");
        } else if (bean.getStatus().equals("4")) {
            ((TextView) holder.getView(R.id.status)).setText("待评星");
        } else if (bean.getStatus().equals("5")) {
            ((TextView) holder.getView(R.id.status)).setBackground(mContext.getResources().getDrawable(R.mipmap.icon_selected));
        }
        if (bean.getType().equals("1")) {
            Drawable drawable = context1.getResources().getDrawable(R.mipmap.label_txt);
            ((ImageView) holder.getView(R.id.iv_1)).setBackground(drawable);
        } else if (bean.getType().equals("2")) {
            Drawable drawable2 = context1.getResources().getDrawable(R.mipmap.label_img);
            ((ImageView) holder.getView(R.id.iv_1)).setBackground(drawable2);
        } else if (bean.getType().equals("3")) {
            Drawable drawable3 = context1.getResources().getDrawable(R.mipmap.label_video);
            ((ImageView) holder.getView(R.id.iv_1)).setBackground(drawable3);
        } else if (bean.getType().equals("4")) {
            Drawable drawable4 = context1.getResources().getDrawable(R.mipmap.little_pingjia_img);
            ((ImageView) holder.getView(R.id.iv_1)).setBackground(drawable4);
        } else if (bean.getType().equals("5")) {
            Drawable drawable5 = context1.getResources().getDrawable(R.mipmap.label_labels);
            ((ImageView) holder.getView(R.id.iv_1)).setBackground(drawable5);
        }
        ((TextView) holder.getView(R.id.tv_2)).setText(bean.getLabel_name());
        if (bean.getStar().equals("5")) {
            ((ImageView) holder.getView(R.id.iv_star1)).setVisibility(View.VISIBLE);
            ((ImageView) holder.getView(R.id.iv_star1)).setBackground(context1.getResources().getDrawable(R.mipmap.star_small));
            ((ImageView) holder.getView(R.id.iv_star2)).setVisibility(View.VISIBLE);
            ((ImageView) holder.getView(R.id.iv_star2)).setBackground(context1.getResources().getDrawable(R.mipmap.star_small));
            ((ImageView) holder.getView(R.id.iv_star3)).setVisibility(View.VISIBLE);
            ((ImageView) holder.getView(R.id.iv_star3)).setBackground(context1.getResources().getDrawable(R.mipmap.star_small));
            ((ImageView) holder.getView(R.id.iv_star4)).setVisibility(View.VISIBLE);
            ((ImageView) holder.getView(R.id.iv_star4)).setBackground(context1.getResources().getDrawable(R.mipmap.star_small));
            ((ImageView) holder.getView(R.id.iv_star5)).setVisibility(View.VISIBLE);
            ((ImageView) holder.getView(R.id.iv_star5)).setBackground(context1.getResources().getDrawable(R.mipmap.star_small));
        } else if (bean.getStar().equals("4")) {
            ((ImageView) holder.getView(R.id.iv_star1)).setVisibility(View.VISIBLE);
            ((ImageView) holder.getView(R.id.iv_star1)).setBackground(context1.getResources().getDrawable(R.mipmap.star_small));
            ((ImageView) holder.getView(R.id.iv_star2)).setVisibility(View.VISIBLE);
            ((ImageView) holder.getView(R.id.iv_star2)).setBackground(context1.getResources().getDrawable(R.mipmap.star_small));
            ((ImageView) holder.getView(R.id.iv_star3)).setVisibility(View.VISIBLE);
            ((ImageView) holder.getView(R.id.iv_star3)).setBackground(context1.getResources().getDrawable(R.mipmap.star_small));
            ((ImageView) holder.getView(R.id.iv_star4)).setVisibility(View.VISIBLE);
            ((ImageView) holder.getView(R.id.iv_star4)).setBackground(context1.getResources().getDrawable(R.mipmap.star_small));
            ((ImageView) holder.getView(R.id.iv_star5)).setVisibility(View.VISIBLE);
        } else if (bean.getStar().equals("3")) {
            ((ImageView) holder.getView(R.id.iv_star1)).setVisibility(View.VISIBLE);
            ((ImageView) holder.getView(R.id.iv_star1)).setBackground(context1.getResources().getDrawable(R.mipmap.star_small));
            ((ImageView) holder.getView(R.id.iv_star2)).setVisibility(View.VISIBLE);
            ((ImageView) holder.getView(R.id.iv_star2)).setBackground(context1.getResources().getDrawable(R.mipmap.star_small));
            ((ImageView) holder.getView(R.id.iv_star3)).setVisibility(View.VISIBLE);
            ((ImageView) holder.getView(R.id.iv_star3)).setBackground(context1.getResources().getDrawable(R.mipmap.star_small));
            ((ImageView) holder.getView(R.id.iv_star4)).setVisibility(View.VISIBLE);
            ((ImageView) holder.getView(R.id.iv_star5)).setVisibility(View.VISIBLE);
        } else if (bean.getStar().equals("2")) {
            ((ImageView) holder.getView(R.id.iv_star1)).setVisibility(View.VISIBLE);
            ((ImageView) holder.getView(R.id.iv_star1)).setBackground(context1.getResources().getDrawable(R.mipmap.star_small));
            ((ImageView) holder.getView(R.id.iv_star2)).setVisibility(View.VISIBLE);
            ((ImageView) holder.getView(R.id.iv_star2)).setBackground(context1.getResources().getDrawable(R.mipmap.star_small));
            ((ImageView) holder.getView(R.id.iv_star3)).setVisibility(View.VISIBLE);
            ((ImageView) holder.getView(R.id.iv_star4)).setVisibility(View.VISIBLE);
            ((ImageView) holder.getView(R.id.iv_star5)).setVisibility(View.VISIBLE);
        } else if (bean.getStar().equals("1")) {
            ((ImageView) holder.getView(R.id.iv_star1)).setVisibility(View.VISIBLE);
            ((ImageView) holder.getView(R.id.iv_star1)).setBackground(context1.getResources().getDrawable(R.mipmap.star_small));
            ((ImageView) holder.getView(R.id.iv_star2)).setVisibility(View.VISIBLE);
            ((ImageView) holder.getView(R.id.iv_star3)).setVisibility(View.VISIBLE);
            ((ImageView) holder.getView(R.id.iv_star4)).setVisibility(View.VISIBLE);
            ((ImageView) holder.getView(R.id.iv_star5)).setVisibility(View.VISIBLE);
        } else {
            ((ImageView) holder.getView(R.id.iv_star1)).setVisibility(View.VISIBLE);
            ((ImageView) holder.getView(R.id.iv_star2)).setVisibility(View.VISIBLE);
            ((ImageView) holder.getView(R.id.iv_star3)).setVisibility(View.VISIBLE);
            ((ImageView) holder.getView(R.id.iv_star4)).setVisibility(View.VISIBLE);
            ((ImageView) holder.getView(R.id.iv_star5)).setVisibility(View.VISIBLE);
        }
//        ((TextView) holder.getView(R.id.titleTv)).setText(bean.getTitle());
//        ((TextView) holder.getView(R.id.descTv)).setText(bean.getDesc());
//        ((TextView) holder.getView(R.id.timeTv)).setText(bean.getTime());
//        ((TextView) holder.getView(R.id.phoneTv)).setText(bean.getPhone());

/*
        TextView tv = holder.getView(R.id.titleTv);
        tv.setText(...);

       ImageView view = getView(viewId);
       Imageloader.getInstance().loadImag(view.url);
*/
    }
}