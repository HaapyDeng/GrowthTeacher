package com.mpl.GrowthTeacher.Adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.mpl.GrowthTeacher.Bean.ComprehensiveEvaluationItem;
import com.mpl.GrowthTeacher.Bean.StudentTaskItem;
import com.mpl.GrowthTeacher.R;
import com.mpl.GrowthTeacher.View.CircleImageView;

import java.util.List;

public class StudentTaskAdapter extends BaseAdapter {
    private LayoutInflater mInflater;
    private List<StudentTaskItem> mDatas;
    private Context mContext;

    public StudentTaskAdapter(Context context, List<StudentTaskItem> datas) {
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
//        if (convertView == null) {
        convertView = mInflater.inflate(R.layout.student_task_item, parent, false); //加载布局
        holder = new ViewHolder();

        holder.iv_head = (CircleImageView) convertView.findViewById(R.id.iv_head);
        holder.tv_name = (TextView) convertView.findViewById(R.id.tv_name);
        holder.tv_classname = (TextView) convertView.findViewById(R.id.tv_classname);
        holder.tv_time = (TextView) convertView.findViewById(R.id.tv_time);
        convertView.setTag(holder);
//        } else {   //else里面说明，convertView已经被复用了，说明convertView中已经设置过tag了，即holder
//            holder = (ViewHolder) convertView.getTag();
//        }
        StudentTaskItem studentTaskItem = mDatas.get(position);
        String gender = studentTaskItem.getGender();
        if (gender.equals("1")) {
            holder.iv_head.setImageDrawable(mContext.getResources().getDrawable(R.mipmap.head_student_boy));
        } else {
            holder.iv_head.setImageDrawable(mContext.getResources().getDrawable(R.mipmap.head_studen_girl));
        }
        holder.tv_name.setText(studentTaskItem.getUsername());
        holder.tv_classname.setText(studentTaskItem.getGrade() + "·" + studentTaskItem.getClassroom_name());
        if (studentTaskItem.getStatus().equals("5")) {
            holder.tv_time.setText(studentTaskItem.getUpdated_at());
        } else if (studentTaskItem.getStatus().equals("0")) {
            holder.tv_time.setText("待学生重做");
        } else if (studentTaskItem.getStatus().equals("1")) {
            holder.tv_time.setText("待家长审核");
        } else if (studentTaskItem.getStatus().equals("2")) {
            holder.tv_time.setText("待家长审核");
        } else if (studentTaskItem.getStatus().equals("3")) {
            holder.tv_time.setText("待老师审核");
        } else if (studentTaskItem.getStatus().equals("4")) {
            holder.tv_time.setText("待评星");
        }


        return convertView;
    }

    private class ViewHolder {
        CircleImageView iv_head;
        TextView tv_name;
        TextView tv_classname;
        TextView tv_time;
    }
}
