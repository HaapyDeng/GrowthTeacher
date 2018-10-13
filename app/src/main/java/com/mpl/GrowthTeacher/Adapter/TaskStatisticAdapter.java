package com.mpl.GrowthTeacher.Adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.mpl.GrowthTeacher.Bean.TaskStatisticItem;
import com.mpl.GrowthTeacher.R;
import com.mpl.GrowthTeacher.Tools.DownImage;
import com.mpl.GrowthTeacher.View.CircleImageView;

import java.util.List;


public class TaskStatisticAdapter extends BaseAdapter {
    private LayoutInflater mInflater;
    private List<TaskStatisticItem> mDatas;
    private Context mContext;
    private OnClickMyTextView mOnClickMyTextView;


    /**
     * 25      * 自定义接口，用于回调按钮点击事件到Activity
     * 28
     */
    //接口回调
    public interface OnClickMyTextView {
        public void myTextViewClick(View view, int position);
    }

    public void setOnClickMyTextView(OnClickMyTextView onClickMyTextView) {
        mOnClickMyTextView = onClickMyTextView;
    }

    public TaskStatisticAdapter(Context context, List<TaskStatisticItem> datas) {
        mContext = context;
        mInflater = LayoutInflater.from(context);
        mDatas = datas;
    }

    @Override
    public int getCount() {
        return mDatas.size();
    }

    @Override
    public Object getItem(int position) {
        return mDatas.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @SuppressLint("NewApi")
    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        ViewHolder holder = null;
//        if (convertView == null) {
        convertView = mInflater.inflate(R.layout.task_statistic_item, parent, false); //加载布局
        holder = new ViewHolder();
        holder.img_head = convertView.findViewById(R.id.img_head);
        holder.tv_name = convertView.findViewById(R.id.tv_name);
        holder.tv_rate = convertView.findViewById(R.id.tv_rate);
        holder.iv_catgray_img = convertView.findViewById(R.id.iv_catgray_img);
        holder.tv_catgray_name = convertView.findViewById(R.id.tv_catgray_name);
        holder.iv_lab_img = convertView.findViewById(R.id.iv_lab_img);
        holder.tv_lable_name = convertView.findViewById(R.id.tv_lable_name);
        holder.tv_end_time = convertView.findViewById(R.id.tv_end_time);
        holder.tv_tongji = convertView.findViewById(R.id.tv_tongji);
        holder.tv_info = convertView.findViewById(R.id.tv_info);

//        } else {   //else里面说明，convertView已经被复用了，说明convertView中已经设置过tag了，即holder
//            holder = (ViewHolder) convertView.getTag();
//        }
        TaskStatisticItem taskStatisticItem = mDatas.get(position);
        if (taskStatisticItem.getImage().length() == 0) {
//            Drawable drawable0 = mContext.getResources().getDrawable(R.mipmap.achieve_default);
//            holder.img_head.setImageDrawable(drawable0);
        } else {
            DownImage downImage = new DownImage(taskStatisticItem.getImage());
            final ViewHolder finalHolder = holder;
            holder.img_head = convertView.findViewById(R.id.img_head);
            downImage.loadImage(new DownImage.ImageCallBack() {

                @Override
                public void getDrawable(Drawable drawable) {
                    finalHolder.img_head.setImageDrawable(drawable);
                }
            });
        }
        holder.tv_name.setText(taskStatisticItem.getName());
        holder.tv_rate.setText(taskStatisticItem.getComplete_rate());
        if (taskStatisticItem.getTask_type().equals("1")) {
            Drawable drawable = mContext.getResources().getDrawable(R.mipmap.label_txt);
            holder.iv_catgray_img.setBackground(drawable);
        } else if (taskStatisticItem.getTask_type().equals("2")) {
            Drawable drawable2 = mContext.getResources().getDrawable(R.mipmap.label_img);
            holder.iv_catgray_img.setBackground(drawable2);
        } else if (taskStatisticItem.getTask_type().equals("3")) {
            Drawable drawable3 = mContext.getResources().getDrawable(R.mipmap.label_video);
            holder.iv_catgray_img.setBackground(drawable3);
        } else if (taskStatisticItem.getTask_type().equals("4")) {
            Drawable drawable4 = mContext.getResources().getDrawable(R.mipmap.little_pingjia_img);
            holder.iv_catgray_img.setBackground(drawable4);
        } else if (taskStatisticItem.getTask_type().equals("5")) {
            Drawable drawable5 = mContext.getResources().getDrawable(R.mipmap.label_labels);
            holder.iv_catgray_img.setBackground(drawable5);
        }
        holder.tv_catgray_name.setText(taskStatisticItem.getCategory_name());
        holder.tv_lable_name.setText(taskStatisticItem.getLabel_name());
        holder.tv_end_time.setText("距离截止还有" + taskStatisticItem.getCountdown() + "天");
        holder.tv_tongji.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mOnClickMyTextView.myTextViewClick(view, position);
            }
        });
        holder.tv_info.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mOnClickMyTextView.myTextViewClick(view, position);
            }
        });
        return convertView;
    }


    private class ViewHolder {
        CircleImageView img_head;
        TextView tv_name;
        TextView tv_rate;
        ImageView iv_catgray_img;
        TextView tv_catgray_name;
        ImageView iv_lab_img;
        TextView tv_lable_name;
        TextView tv_end_time;
        TextView tv_tongji;
        TextView tv_info;
    }
}
