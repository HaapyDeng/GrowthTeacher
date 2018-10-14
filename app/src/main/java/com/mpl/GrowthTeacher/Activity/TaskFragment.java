package com.mpl.GrowthTeacher.Activity;


import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;
import com.mpl.GrowthTeacher.Adapter.TaskFragmentAdapter;
import com.mpl.GrowthTeacher.Bean.TaskItem;
import com.mpl.GrowthTeacher.R;
import com.mpl.GrowthTeacher.Tools.NetworkUtils;
import com.mpl.GrowthTeacher.View.LoadMoreListView;
import com.mpl.GrowthTeacher.View.LoadingDialog;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import cz.msebera.android.httpclient.Header;

import static android.content.Context.MODE_PRIVATE;

/**
 * A simple {@link Fragment} subclass.
 */
public class TaskFragment extends Fragment implements View.OnClickListener, SwipeRefreshLayout.OnRefreshListener, AdapterView.OnItemClickListener {
    private ImageView ib_message;
    private LoadingDialog loadingDialog;
    private TextView tv_check_more, tv_cancel, tv_commit;
    private LoadMoreListView listview;
    private String currentPage = "1";
    private int totalPage;

    private SwipeRefreshLayout mSwipeLayout;
    private boolean isRefresh = false;//是否刷新中

    private List<TaskItem> mdatas = new ArrayList<>();
    private TaskFragmentAdapter taskFragmentAdapter;

    private int moreFlag = 0; //是否是多条审核表示
    private int deletFlag = 0;//是否是多条审核提交成功

    // 声明PopupWindow
    PopupWindow popupWindow;
    // 声明PopupWindow对应的视图
    View popupView;

    int chooseCount = 0;//多条审核选择数目

    ArrayList<String> idList;
    String[] arrayid;


    public TaskFragment() {
    }

    public static TaskFragment newInstance(String name) {
        TaskFragment fragment = new TaskFragment();
        Bundle args = new Bundle();
        args.putString("name", name);
        fragment.setArguments(args);
        return fragment;
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View root = inflater.inflate(R.layout.fragment_task, container, false);
        ib_message = root.findViewById(R.id.ib_message);
        ib_message.setOnClickListener(this);
        listview = root.findViewById(R.id.listview);
        tv_check_more = root.findViewById(R.id.tv_check_more);
        tv_check_more.setOnClickListener(this);
        tv_cancel = root.findViewById(R.id.tv_cancel);
        tv_cancel.setOnClickListener(this);
        tv_commit = root.findViewById(R.id.tv_commit);
        tv_commit.setOnClickListener(this);
        if (mdatas.size() > 0) {
            mdatas.clear();
        }
        doGetTask(currentPage);
        listview.setOnLoadMoreListener(new LoadMoreListView.OnLoadMoreListener() {
            @Override
            public void onloadMore() {
                int i = Integer.parseInt(currentPage);
                Log.d("i==>>", "" + i);
                if (i < totalPage) {
                    doGetTask("" + (i + 1));
                } else {
                    listview.setLoadCompleted();
                }
            }
        });
        listview.setOnItemClickListener(this);

        //设置SwipeRefreshLayout
        mSwipeLayout = (SwipeRefreshLayout) root.findViewById(R.id.swipeLayout);
        //设置进度条的颜色主题，最多能设置四种 加载颜色是循环播放的，只要没有完成刷新就会一直循环
        mSwipeLayout.setColorSchemeColors(Color.RED,
                Color.RED,
                Color.RED,
                Color.RED);
        // 设置手指在屏幕下拉多少距离会触发下拉刷新
        mSwipeLayout.setDistanceToTriggerSync(300);
        // 设定下拉圆圈的背景
        mSwipeLayout.setProgressBackgroundColorSchemeColor(Color.WHITE);
        mSwipeLayout.setTag("下拉刷新");
        // 设置圆圈的大小
        mSwipeLayout.setSize(SwipeRefreshLayout.LARGE);

        //设置下拉刷新的监听
        mSwipeLayout.setOnRefreshListener(this);
        return root;
    }

    private void doGetTask(String page) {
        loadingDialog = new LoadingDialog(getContext(), "加载中...", R.drawable.ic_dialog_loading);
        loadingDialog.show();
        if (NetworkUtils.checkNetWork(getActivity()) == false) {
            loadingDialog.dismiss();
            Toast.makeText(getActivity(), R.string.no_network, Toast.LENGTH_LONG).show();
            return;
        }
        SharedPreferences sharedPreferences = getActivity().getSharedPreferences("myinfo", MODE_PRIVATE);
        String token = sharedPreferences.getString("token", "");
        String url = getResources().getString(R.string.local_url) + "/v1/achievement" + "?page=" + page;
        Log.d("url==>>", url);
        AsyncHttpClient client = new AsyncHttpClient();
        client.addHeader("X-Api-Token", token);
        client.get(url, new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                super.onSuccess(statusCode, headers, response);
                Log.d("response==>>", response.toString());
                try {
                    int code = response.getInt("code");
                    if (code == 0) {
                        loadingDialog.dismiss();
                        JSONObject data = response.getJSONObject("data");
                        totalPage = data.getInt("totalPage");
                        JSONArray list = data.getJSONArray("list");
                        if (list.length() == 0) {
                            View view = LinearLayout.inflate(getActivity(), R.layout.empty_view, null);
                            listview.setEmptyView(view);
                        } else {
                            for (int i = 0; i < list.length(); i++) {
                                JSONObject object = list.getJSONObject(i);
                                String id = object.getString("id");
                                String name = object.getString("name");
                                String type = object.getString("type");
                                String write_by_type = object.getString("write_by_type");
                                String image = object.getString("image");
                                String category_name = object.getString("category_name");
                                String label_name = object.getString("label_name");
                                String status = object.getString("status");
                                String role = object.getString("role");
                                String updated_at = object.getString("updated_at");
                                String username = object.getString("username");
                                String classroom_id = object.getString("classroom_id");
                                String classroom_name = object.getString("classroom_name");
                                String task_relation_id = object.getString("task_relation_id");
                                String grade = object.getString("grade");
                                TaskItem taskItem = new TaskItem(id, name, type, write_by_type, image, category_name, label_name, status, role,
                                        updated_at, username, classroom_id, classroom_name, task_relation_id, grade);
                                mdatas.add(taskItem);
                            }
                            taskFragmentAdapter = new TaskFragmentAdapter(getActivity(), mdatas);
                            listview.setAdapter(taskFragmentAdapter);
                            listview.setLoadCompleted();
                        }

                    } else {
                        loadingDialog.dismiss();
                        Toast.makeText(getActivity(), response.getString("message"), Toast.LENGTH_LONG).show();
                        return;
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                super.onFailure(statusCode, headers, throwable, errorResponse);
                loadingDialog.dismiss();
                Toast.makeText(getActivity(), R.string.no_network, Toast.LENGTH_LONG).show();
                return;
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                super.onFailure(statusCode, headers, responseString, throwable);
                loadingDialog.dismiss();
                Toast.makeText(getActivity(), R.string.no_network, Toast.LENGTH_LONG).show();
                return;
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONArray errorResponse) {
                super.onFailure(statusCode, headers, throwable, errorResponse);
                loadingDialog.dismiss();
                Toast.makeText(getActivity(), R.string.no_network, Toast.LENGTH_LONG).show();
                return;
            }
        });
    }


    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.ib_message:
                Intent intent = new Intent(getActivity(), MessageActivity.class);
                startActivity(intent);
                break;
            case R.id.tv_check_more:
//                Intent intent1 = new Intent(getActivity(), CheckMoreTaskActivity.class);
//                startActivity(intent1);
                moreFlag = 1;
                taskFragmentAdapter.setCanChoose(true);
                tv_check_more.setVisibility(View.INVISIBLE);
                tv_cancel.setVisibility(View.VISIBLE);
                tv_commit.setVisibility(View.VISIBLE);
                ib_message.setVisibility(View.INVISIBLE);
                break;
            case R.id.tv_commit:
                moreFlag = 0;
                chooseCount = taskFragmentAdapter.chooseCount();
                idList = taskFragmentAdapter.chooseId();
                arrayid = new String[idList.size()];
                for (int i = 0; i < idList.size(); i++) {
                    arrayid[i] = (String) idList.get(i);
                }
                Log.d("arrayid==>>", arrayid.toString());
                if (chooseCount == 0) {
                    Toast.makeText(getActivity(), "请选择批量评审任务", Toast.LENGTH_SHORT).show();
                    moreFlag = 1;
                    taskFragmentAdapter.setCanChoose(true);
                    tv_check_more.setVisibility(View.INVISIBLE);
                    tv_cancel.setVisibility(View.VISIBLE);
                    tv_commit.setVisibility(View.VISIBLE);
                    ib_message.setVisibility(View.INVISIBLE);
                } else {
                    ShowPopuWindow();
                }


                break;
            case R.id.tv_cancel:
                moreFlag = 0;
                taskFragmentAdapter.setCanChoose(false);
                tv_check_more.setVisibility(View.VISIBLE);
                tv_cancel.setVisibility(View.INVISIBLE);
                tv_commit.setVisibility(View.INVISIBLE);
                ib_message.setVisibility(View.VISIBLE);
                break;
        }
    }


    @Override
    public void onRefresh() {
        //检查是否处于刷新状态
        if (!isRefresh) {
            isRefresh = true;
            //模拟加载网络数据，这里设置4秒，正好能看到4色进度条
            new Handler().postDelayed(new Runnable() {
                public void run() {

                    //显示或隐藏刷新进度条
                    mSwipeLayout.setRefreshing(false);
                    //修改adapter的数据
                    if (mdatas.size() > 0) {
                        mdatas.clear();
                    }
                    doGetTask(currentPage);
                    taskFragmentAdapter.notifyDataSetChanged();
                    isRefresh = false;
                }
            }, 3000);
        }
    }

    private void ShowPopuWindow() {
        backgroundAlpha(0.7f);
        // 声明平移动画
        popupView = View.inflate(getActivity(), R.layout.review_more_item, null);
        // 参数2,3：指明popupwindow的宽度和高度
        popupWindow = new PopupWindow(popupView, WindowManager.LayoutParams.MATCH_PARENT,
                WindowManager.LayoutParams.WRAP_CONTENT);
        popupWindow.setBackgroundDrawable(new ColorDrawable(0x00ffffff));
        // 设置点击popupwindow外屏幕其它地方不消失
        popupWindow.setOutsideTouchable(false);
        // 设置popupWindow的显示位置，此处是在手机屏幕底部且水平居中的位置
        popupWindow.showAtLocation(getView(), Gravity.CENTER, 0, 0);
        TextView tv_pcancel = popupView.findViewById(R.id.tv_pcancel);
        tv_pcancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                popupWindow.dismiss();
            }
        });
        TextView tv_choose_count = popupView.findViewById(R.id.tv_choose_count);
        tv_choose_count.setText("为已选" + chooseCount + "条记录设定评审结果");
        final CheckBox ck_1, ck_2, ck_3, ck_4, ck_5;
        final StringBuilder[] sb = {new StringBuilder("")};
        final EditText et_content = popupView.findViewById(R.id.et_content);
        ck_1 = popupView.findViewById(R.id.ck_1);
        ck_1.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @SuppressLint("NewApi")
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean isChecked) {
                if (isChecked) {
                    et_content.setText(sb[0].append(ck_1.getText()));
                    ck_1.setBackground(getResources().getDrawable(R.drawable.textview_shape));
                    ck_1.setTextColor(getActivity().getColor(R.color.white));
                } else {
                    ck_1.setBackground(getResources().getDrawable(R.drawable.textview_shape_grey));
                    ck_1.setTextColor(getActivity().getColor(R.color.text2));
                }
            }
        });
        ck_2 = popupView.findViewById(R.id.ck_2);
        ck_2.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @SuppressLint("NewApi")
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean isChecked) {
                if (isChecked) {
                    et_content.setText(sb[0].append(ck_2.getText()));
                    ck_2.setBackground(getResources().getDrawable(R.drawable.textview_shape));
                    ck_2.setTextColor(getActivity().getColor(R.color.white));
                } else {
                    ck_2.setBackground(getResources().getDrawable(R.drawable.textview_shape_grey));
                    ck_2.setTextColor(getActivity().getColor(R.color.text2));
                }
            }
        });
        ck_3 = popupView.findViewById(R.id.ck_3);
        ck_3.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @SuppressLint("NewApi")
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean isChecked) {
                if (isChecked) {
                    et_content.setText(sb[0].append(ck_3.getText()));
                    ck_3.setBackground(getResources().getDrawable(R.drawable.textview_shape));
                    ck_3.setTextColor(getActivity().getColor(R.color.white));
                } else {
                    ck_3.setBackground(getResources().getDrawable(R.drawable.textview_shape_grey));
                    ck_3.setTextColor(getActivity().getColor(R.color.text2));
                }
            }
        });
        ck_4 = popupView.findViewById(R.id.ck_4);
        ck_4.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @SuppressLint("NewApi")
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean isChecked) {
                if (isChecked) {
                    et_content.setText(sb[0].append(ck_4.getText()));
                    ck_4.setBackground(getResources().getDrawable(R.drawable.textview_shape));
                    ck_4.setTextColor(getActivity().getColor(R.color.white));
                } else {
                    ck_4.setBackground(getResources().getDrawable(R.drawable.textview_shape_grey));
                    ck_4.setTextColor(getActivity().getColor(R.color.text2));
                }
            }
        });
        ck_5 = popupView.findViewById(R.id.ck_5);
        ck_5.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @SuppressLint("NewApi")
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean isChecked) {
                if (isChecked) {
                    et_content.setText(sb[0].append(ck_5.getText()));
                    ck_5.setBackground(getResources().getDrawable(R.drawable.textview_shape));
                    ck_5.setTextColor(getActivity().getColor(R.color.white));
                } else {
                    ck_5.setBackground(getResources().getDrawable(R.drawable.textview_shape_grey));
                    ck_5.setTextColor(getActivity().getColor(R.color.text2));
                }
            }
        });

        final String[] contentText = new String[1];
        final int[] rbText = {1};
        RadioGroup rg = popupView.findViewById(R.id.rg);
        rg.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @SuppressLint("NewApi")
            @Override
            public void onCheckedChanged(RadioGroup radioGroup, int i) {
                int id = radioGroup.getCheckedRadioButtonId();
                ck_1.setChecked(false);
                ck_2.setChecked(false);
                ck_3.setChecked(false);
                ck_4.setChecked(false);
                ck_5.setChecked(false);
                switch (id) {
                    case R.id.rb_ok:
                        et_content.setText("");
                        sb[0] = new StringBuilder("");
                        rbText[0] = 1;
                        ck_1.setText("不错继续加油哦");
                        ck_1.setBackground(getResources().getDrawable(R.drawable.textview_shape_grey));
                        ck_1.setTextColor(getActivity().getColor(R.color.text2));
                        ck_2.setText("很认真观察仔细");
                        ck_2.setBackground(getResources().getDrawable(R.drawable.textview_shape_grey));
                        ck_2.setTextColor(getActivity().getColor(R.color.text2));
                        ck_3.setText("逻辑清晰");
                        ck_3.setBackground(getResources().getDrawable(R.drawable.textview_shape_grey));
                        ck_3.setTextColor(getActivity().getColor(R.color.text2));
                        ck_4.setText("很棒！看好你");
                        ck_4.setBackground(getResources().getDrawable(R.drawable.textview_shape_grey));
                        ck_4.setTextColor(getActivity().getColor(R.color.text2));
                        ck_5.setText("再接再厉");
                        ck_5.setBackground(getResources().getDrawable(R.drawable.textview_shape_grey));
                        ck_5.setTextColor(getActivity().getColor(R.color.text2));
                        break;
                    case R.id.rb_no:
                        et_content.setText("");
                        sb[0] = new StringBuilder("");
                        rbText[0] = 2;
                        ck_1.setText("文字有违规字眼");
                        ck_1.setBackground(getResources().getDrawable(R.drawable.textview_shape_grey));
                        ck_1.setTextColor(getActivity().getColor(R.color.text2));
                        ck_2.setText("图片有违规内容");
                        ck_2.setBackground(getResources().getDrawable(R.drawable.textview_shape_grey));
                        ck_2.setTextColor(getActivity().getColor(R.color.text2));
                        ck_3.setText("很不认真");
                        ck_3.setBackground(getResources().getDrawable(R.drawable.textview_shape_grey));
                        ck_3.setTextColor(getActivity().getColor(R.color.text2));
                        ck_4.setText("记录内容过少");
                        ck_4.setBackground(getResources().getDrawable(R.drawable.textview_shape_grey));
                        ck_4.setTextColor(getActivity().getColor(R.color.text2));
                        ck_5.setText("再接再厉");
                        ck_5.setBackground(getResources().getDrawable(R.drawable.textview_shape_grey));
                        ck_5.setTextColor(getActivity().getColor(R.color.text2));
                        break;
                }
            }
        });
        TextView tv_ppcancel = popupView.findViewById(R.id.tv_pcancel);
        tv_ppcancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                popupWindow.dismiss();
                backgroundAlpha(1);
                moreFlag = 1;
                taskFragmentAdapter.setCanChoose(true);
                tv_check_more.setVisibility(View.INVISIBLE);
                tv_cancel.setVisibility(View.VISIBLE);
                tv_commit.setVisibility(View.VISIBLE);
                ib_message.setVisibility(View.INVISIBLE);
            }
        });
        TextView tv_commit = popupView.findViewById(R.id.tv_commit);
        tv_commit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                contentText[0] = et_content.getText().toString().trim();
                if (contentText[0].equals("")) {
                    Toast.makeText(getActivity(), "请添加评审内容", Toast.LENGTH_SHORT).show();
                    return;
                }
                Log.d("choose===>>>", rbText[0] + "/" + contentText[0]);
                Log.d("arrayid===>>", arrayid[0] + arrayid[1]);
                doCommit(arrayid, rbText[0], contentText[0]);

            }
        });
    }

    private void doCommit(String[] arrayid, int i, String s) {
        loadingDialog = new LoadingDialog(getContext(), "加载中...", R.drawable.ic_dialog_loading);
        loadingDialog.show();
        if (NetworkUtils.checkNetWork(getActivity()) == false) {
            loadingDialog.dismiss();
            Toast.makeText(getActivity(), R.string.no_network, Toast.LENGTH_LONG).show();
            return;
        }
        SharedPreferences sharedPreferences = getActivity().getSharedPreferences("myinfo", MODE_PRIVATE);
        String token = sharedPreferences.getString("token", "");
        String url = getResources().getString(R.string.local_url) + "/v1/achievement/audit/batch";
        Log.d("url==>>", url);
        AsyncHttpClient client = new AsyncHttpClient();
        RequestParams params = new RequestParams();
        params.put("id", arrayid);
        params.put("status", i);
        params.put("content", s);
        client.addHeader("X-Api-Token", token);
        client.post(url, new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                super.onSuccess(statusCode, headers, response);
                Log.d("response==>>", response.toString());
                try {
                    int code = response.getInt("code");
                    if (code == 0) {
                        loadingDialog.dismiss();
                        popupWindow.dismiss();
                        backgroundAlpha(1);
                        Toast.makeText(getActivity(), "批量评审完成", Toast.LENGTH_LONG).show();
                        deletFlag = 1;
                        moreFlag = 0;
                        taskFragmentAdapter.setCanChoose(false);
                        tv_check_more.setVisibility(View.VISIBLE);
                        tv_cancel.setVisibility(View.INVISIBLE);
                        tv_commit.setVisibility(View.INVISIBLE);
                        ib_message.setVisibility(View.VISIBLE);
                        if (deletFlag == 1) {
                            taskFragmentAdapter.delete();
                        }

                    } else {
                        loadingDialog.dismiss();
                        Toast.makeText(getActivity(), response.getString("message"), Toast.LENGTH_LONG).show();
                        return;
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                super.onFailure(statusCode, headers, throwable, errorResponse);
                loadingDialog.dismiss();
                Toast.makeText(getActivity(), R.string.no_network, Toast.LENGTH_LONG).show();
                return;
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                super.onFailure(statusCode, headers, responseString, throwable);
                loadingDialog.dismiss();
                Toast.makeText(getActivity(), R.string.no_network, Toast.LENGTH_LONG).show();
                return;
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONArray errorResponse) {
                super.onFailure(statusCode, headers, throwable, errorResponse);
                loadingDialog.dismiss();
                Toast.makeText(getActivity(), R.string.no_network, Toast.LENGTH_LONG).show();
                return;
            }
        });
    }

    /**
     * 设置添加屏幕的背景透明度
     *
     * @param bgAlpha
     */
    public void backgroundAlpha(float bgAlpha) {
        WindowManager.LayoutParams lp = getActivity().getWindow().getAttributes();
        lp.alpha = bgAlpha; //0.0-1.0
        getActivity().getWindow().setAttributes(lp);
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        if (moreFlag == 1) {
            taskFragmentAdapter.choose(position);
        } else {
            String taskId = mdatas.get(position).getId();
            Log.d("taskId==>", taskId);
            Bundle bundle = new Bundle();
            bundle.putString("taskId", taskId);
            String type = mdatas.get(position).getType();

            if (type.equals("1")) {
                Intent intent1 = new Intent(getActivity(), ReviewWenZiActivity.class);
                intent1.putExtras(bundle);
                startActivity(intent1);
            } else if (type.equals("2")) {
                Intent intent2 = new Intent(getActivity(), ReviewTuWenActivity.class);
                intent2.putExtras(bundle);
                startActivity(intent2);
            } else if (type.equals("3")) {
                Intent intent3 = new Intent(getActivity(), ReviewVideoActivity.class);
                intent3.putExtras(bundle);
                startActivity(intent3);
//            } else if (type.equals("4")) {
//                Intent intent4 = new Intent(getActivity(), ReviewPsqActivity.class);
//                intent4.putExtras(bundle);
//                startActivity(intent4);
//            } else if (type.equals("5")) {
//                Intent intent5 = new Intent(getActivity(), ReviewSystemActivity.class);
//                intent5.putExtras(bundle);
//                startActivity(intent5);
            }

        }

    }

}
