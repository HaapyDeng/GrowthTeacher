package com.mpl.GrowthTeacher.Adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.mpl.GrowthTeacher.Bean.ComprehensiveEvaluationItem;
import com.mpl.GrowthTeacher.R;
import com.mpl.GrowthTeacher.View.CircleImageView;

import java.util.List;

public class ComprehensiveEvaluationAdapter extends BaseAdapter {
    private LayoutInflater mInflater;
    private List<ComprehensiveEvaluationItem> mDatas;
    private Context mContext;

    public ComprehensiveEvaluationAdapter(Context context, List<ComprehensiveEvaluationItem> datas) {
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
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder = null;
        if (convertView == null) {
            convertView = mInflater.inflate(R.layout.comprehensive_evaluation_item, parent, false); //加载布局
            holder = new ViewHolder();

            holder.iv_head = (CircleImageView) convertView.findViewById(R.id.iv_head);
            holder.tv_name = (TextView) convertView.findViewById(R.id.tv_name);
            holder.tv_classname = (TextView) convertView.findViewById(R.id.tv_classname);
            holder.tv_starcount = (TextView) convertView.findViewById(R.id.tv_starcount);
            holder.tv_score = (TextView) convertView.findViewById(R.id.tv_score);
            convertView.setTag(holder);
        } else {   //else里面说明，convertView已经被复用了，说明convertView中已经设置过tag了，即holder
            holder = (ViewHolder) convertView.getTag();
        }
        ComprehensiveEvaluationItem comprehensiveEvaluationItem = mDatas.get(position);
        String gender = comprehensiveEvaluationItem.getGender();
        if (gender.equals("1")) {
            holder.iv_head.setImageDrawable(mContext.getResources().getDrawable(R.mipmap.head_student_boy));
        } else {
            holder.iv_head.setImageDrawable(mContext.getResources().getDrawable(R.mipmap.head_studen_girl));
        }
        holder.tv_name.setText(comprehensiveEvaluationItem.getName());
        holder.tv_classname.setText(comprehensiveEvaluationItem.getGrade() + "·" + comprehensiveEvaluationItem.getClassroom_name());
        holder.tv_starcount.setText(comprehensiveEvaluationItem.getStar());
        holder.tv_score.setText(comprehensiveEvaluationItem.getTotal_point() + "分");
        return convertView;
    }

    private class ViewHolder {
        CircleImageView iv_head;
        TextView tv_name;
        TextView tv_classname;
        TextView tv_starcount;
        TextView tv_score;
    }
}
