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

import com.mpl.GrowthTeacher.Bean.ComprehensiveEvaluationItem;
import com.mpl.GrowthTeacher.Bean.TaskItem;
import com.mpl.GrowthTeacher.R;
import com.mpl.GrowthTeacher.Tools.DelteableInterface;
import com.mpl.GrowthTeacher.Tools.DownImage;
import com.mpl.GrowthTeacher.View.CircleImageView;

import java.util.Iterator;
import java.util.List;

public class TaskFragmentAdapter extends BaseAdapter implements DelteableInterface {
    private LayoutInflater mInflater;
    private List<TaskItem> mDatas;
    private Context mContext;

    public TaskFragmentAdapter(Context context, List<TaskItem> datas) {
        mContext = context;
        mInflater = LayoutInflater.from(context);
        mDatas = datas;
    }

    @Override
    public int getCount() {
        return mDatas.size();
    }

    @Override
    public TaskItem getItem(int position) {
        return mDatas.get(position % mDatas.size());
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
        convertView = mInflater.inflate(R.layout.task_fragment_item, parent, false); //加载布局
        holder = new ViewHolder();
        holder.iv_image = convertView.findViewById(R.id.iv_image);
        holder.tv_title = convertView.findViewById(R.id.tv_title);
        holder.tv_username = convertView.findViewById(R.id.tv_username);
        holder.tv_g_class = convertView.findViewById(R.id.tv_g_class);
        holder.iv_choose = convertView.findViewById(R.id.iv_choose);
        holder.tv_type_image = convertView.findViewById(R.id.tv_type_image);
        holder.tv_type_name = convertView.findViewById(R.id.tv_type_name);
        holder.tv_lable_image = convertView.findViewById(R.id.tv_lable_image);
        holder.tv_lable_name = convertView.findViewById(R.id.tv_lable_name);
        holder.tv_state = convertView.findViewById(R.id.tv_state);
        convertView.setTag(holder);
//        } else {   //else里面说明，convertView已经被复用了，说明convertView中已经设置过tag了，即holder
//            holder = (ViewHolder) convertView.getTag();
//        }
        TaskItem taskItem = mDatas.get(position);
        if (taskItem.getImage().length() == 0) {
//            Drawable drawable0 = mContext.getResources().getDrawable(R.mipmap.achieve_default);
//            holder.iv_image.setImageDrawable(drawable0);
        } else {
            DownImage downImage = new DownImage(taskItem.getImage());
            final ViewHolder finalHolder = holder;
            finalHolder.iv_image = convertView.findViewById(R.id.iv_image);
            downImage.loadImage(new DownImage.ImageCallBack() {
                @Override
                public void getDrawable(Drawable drawable) {
                    finalHolder.iv_image.setImageDrawable(drawable);
                }
            });
        }
        holder.tv_title.setText(taskItem.getName());
        holder.tv_username.setText(taskItem.getUsername());
        holder.tv_g_class.setText(taskItem.getGrade() + taskItem.getClassroom_name());
        String type = taskItem.getType();
        if (type.equals("1")) {
            Drawable drawable = mContext.getResources().getDrawable(R.mipmap.label_txt_grey);
            holder.tv_type_image.setBackground(drawable);
            holder.tv_type_name.setText("文字");
        } else if (type.equals("2")) {
            Drawable drawable2 = mContext.getResources().getDrawable(R.mipmap.label_img_grey);
            holder.tv_type_image.setBackground(drawable2);
            holder.tv_type_name.setText("图文");
        } else if (type.equals("3")) {
            Drawable drawable3 = mContext.getResources().getDrawable(R.mipmap.label_video_grey);
            holder.tv_type_image.setBackground(drawable3);
            holder.tv_type_name.setText("视频");
        } else if (type.equals("4")) {
            Drawable drawable4 = mContext.getResources().getDrawable(R.mipmap.little_pingjia_img);
            holder.tv_type_image.setBackground(drawable4);
            holder.tv_type_name.setText("问卷");
        } else if (type.equals("5")) {
            Drawable drawable5 = mContext.getResources().getDrawable(R.mipmap.label_labels_grey);
            holder.tv_type_image.setBackground(drawable5);
            holder.tv_type_name.setText("系统");
        }
        holder.tv_lable_name.setText(taskItem.getLabel_name());
        String role = taskItem.getRole();
        if (role.equals("teacher")) {
            holder.tv_state.setText("待老师完成");
            holder.tv_state.setTextColor(mContext.getResources().getColor(R.color.text3));
        } else if (role.equals("student")) {
            holder.tv_state.setText(taskItem.getUpdated_at());
        } else {
            holder.tv_state.setText(taskItem.getUpdated_at());
        }
        if (canChoose) {
            holder.iv_choose.setVisibility(View.VISIBLE);
        } else {
            holder.iv_choose.setVisibility(View.GONE);
        }
        final TaskItem bean = getItem(position);
        if (bean.choosed) {
            holder.iv_choose.setImageResource(R.drawable.choose_scope_shape);
        } else {
            holder.iv_choose.setImageResource(R.drawable.not_choose_scope_shape);
        }
        return convertView;
    }

    @Override
    public void choose(int position) {
        TaskItem taskItem = getItem(position);
        taskItem.choosed = !taskItem.choosed;
        notifyDataSetChanged();
    }

    @Override
    public void delete() {
        Iterator<TaskItem> iterator = mDatas.iterator();
        while (iterator.hasNext()) {
            TaskItem taskItem = iterator.next();
            if (taskItem.choosed) {
                iterator.remove();
            }
        }
        notifyDataSetChanged();

    }

    private boolean canChoose = false;

    public void setCanChoose(Boolean canChoose) {
        this.canChoose = canChoose;
        notifyDataSetChanged();
    }

    private class ViewHolder {
        CircleImageView iv_image;
        TextView tv_title;
        TextView tv_username;
        TextView tv_g_class;
        ImageView iv_choose;
        ImageView tv_type_image;
        TextView tv_type_name;
        ImageView tv_lable_image;
        TextView tv_lable_name;
        TextView tv_state;
    }
}
