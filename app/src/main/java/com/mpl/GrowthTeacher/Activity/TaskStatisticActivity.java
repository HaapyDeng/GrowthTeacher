package com.mpl.GrowthTeacher.Activity;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Handler;
import android.os.Message;
import android.support.v4.widget.DrawerLayout;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.format.DateFormat;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.DatePicker;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.mpl.GrowthTeacher.Adapter.CategoryListViewAdapter;
import com.mpl.GrowthTeacher.Adapter.ComprehensiveEvaluationAdapter;
import com.mpl.GrowthTeacher.Adapter.TaskStatisticAdapter;
import com.mpl.GrowthTeacher.Bean.CategoryListItem;
import com.mpl.GrowthTeacher.Bean.ComprehensiveEvaluationItem;
import com.mpl.GrowthTeacher.Bean.TaskStatisticItem;
import com.mpl.GrowthTeacher.R;
import com.mpl.GrowthTeacher.Tools.MyRadioGroup;
import com.mpl.GrowthTeacher.Tools.NetworkUtils;
import com.mpl.GrowthTeacher.View.LoadMoreListView;
import com.mpl.GrowthTeacher.View.LoadingDialog;
import com.paging.listview.PagingListView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import cz.msebera.android.httpclient.Header;

/**
 * 小助手 - 任务统计 - 列表
 */
public class TaskStatisticActivity extends AppCompatActivity implements View.OnClickListener, SwipeRefreshLayout.OnRefreshListener, AdapterView.OnItemClickListener {
    private TextView tv_choose;
    private LinearLayout back;
    private LoadMoreListView listview;
    private LoadingDialog loadingDialog;
    private String currentPage = "1";
    private int totalPage;
    private String totalCount;
    private List<TaskStatisticItem> mDatas = new ArrayList<TaskStatisticItem>();
    private TaskStatisticAdapter taskStatisticAdapter;

    private SwipeRefreshLayout mSwipeLayout;
    private boolean isRefresh = false;//是否刷新中

    private DrawerLayout drawerLayout;
    private TextView tv_start_time, tv_end_time, tv_clear, tv_ok;
    private RadioButton rb_youeryuan, rb_xiaoxue, rb_chuzhong, rb_gaozhong;
    private MyRadioGroup radioGroup;
    private ListView lv_category, lv_label;
    private int school_scope = 0;
    private CharSequence choose_start_time = "0", choose_end_time = "0";
    private CategoryListItem categoryListItem;
    private List<CategoryListItem> listCategoryListItem;
    private List<CategoryListItem> listLableListItem;
    private String categoryid = "0";
    private String lableid = "0";
    private String studentId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_task_statistic);
        //初始化参数
        SharedPreferences sp3 = getSharedPreferences("taskparameters", MODE_PRIVATE);
        SharedPreferences.Editor editor3 = sp3.edit();
        editor3.putString("start", "0");
        editor3.putString("end", "0");
        editor3.putString("cid", "0");
        editor3.putString("lid", "0");
        editor3.commit();

        drawerLayout = (DrawerLayout) findViewById(R.id.activity_na);
        drawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED); //关闭手势滑动
        radioGroup = (MyRadioGroup) findViewById(R.id.radioGroupID);
        //设置监听
        radioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup radioGroup, int checkedId) {
                switch (checkedId) {
                    case R.id.tv_youeryuan:
                        school_scope = 1;
                        rb_youeryuan.setTextColor(getResources().getColor(R.color.white));
                        rb_xiaoxue.setTextColor(getResources().getColor(R.color.tab_checked));
                        rb_chuzhong.setTextColor(getResources().getColor(R.color.tab_checked));
                        break;
                    case R.id.tv_xiaoxue:
                        school_scope = 2;
                        rb_xiaoxue.setTextColor(getResources().getColor(R.color.white));
                        rb_youeryuan.setTextColor(getResources().getColor(R.color.tab_checked));
                        rb_chuzhong.setTextColor(getResources().getColor(R.color.tab_checked));
                        break;
                    case R.id.tv_chuzhong:
                        school_scope = 3;
                        rb_chuzhong.setTextColor(getResources().getColor(R.color.white));
                        rb_youeryuan.setTextColor(getResources().getColor(R.color.tab_checked));
                        rb_xiaoxue.setTextColor(getResources().getColor(R.color.tab_checked));
                        break;

                }
                Log.d("school_scope==>>", "" + school_scope);
            }
        });
        rb_youeryuan = findViewById(R.id.tv_youeryuan);
        rb_xiaoxue = findViewById(R.id.tv_xiaoxue);
        rb_chuzhong = findViewById(R.id.tv_chuzhong);
        rb_gaozhong = findViewById(R.id.tv_gaozhong);

        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");// HH:mm:ss
        //获取当前时间
        Date date = new Date(System.currentTimeMillis());
        tv_start_time = findViewById(R.id.tv_start_time);
        tv_start_time.setText(simpleDateFormat.format(date));
        tv_start_time.setOnClickListener(this);

        tv_end_time = findViewById(R.id.tv_end_time);
        tv_end_time.setOnClickListener(this);
        tv_end_time.setText(simpleDateFormat.format(date));

        tv_clear = findViewById(R.id.tv_clear);
        tv_clear.setOnClickListener(this);
        tv_ok = findViewById(R.id.tv_ok);
        tv_ok.setOnClickListener(this);
        lv_category = findViewById(R.id.lv_category);
        lv_category.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                categoryid = listCategoryListItem.get(position).getId();
                String name = listCategoryListItem.get(position).getName();
                Log.d("Categoryid==>>>>", categoryid);
                doGetLableList(categoryid);
            }
        });
        lv_label = findViewById(R.id.lv_label);
        lv_label.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                lableid = listLableListItem.get(position).getId();
                Log.d("lableid==>>>>", lableid);
            }
        });


        back = findViewById(R.id.back);
        back.setOnClickListener(this);

        tv_choose = findViewById(R.id.tv_choose);
        tv_choose.setOnClickListener(this);

        listview = findViewById(R.id.listview);
        initData(currentPage, choose_start_time, choose_end_time, categoryid, lableid);
        listview.setOnLoadMoreListener(new LoadMoreListView.OnLoadMoreListener() {
            @Override
            public void onloadMore() {
                int i = Integer.parseInt(currentPage);
                Log.d("i==>>", "" + i);
                if (i < totalPage) {
                    initMoreData("" + (i + 1), choose_start_time, choose_end_time, categoryid, lableid);
                } else {
                    listview.setLoadCompleted();
                }
            }
        });

        //设置SwipeRefreshLayout
        mSwipeLayout = (SwipeRefreshLayout) findViewById(R.id.swipeLayout);
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
    }


    private void initData(String page, CharSequence choose_start_time, CharSequence choose_end_time, String categoryid, String lableid) {
        loadingDialog = new LoadingDialog(this, "加载中...", R.drawable.ic_dialog_loading);
        loadingDialog.show();
        if (NetworkUtils.checkNetWork(this) == false) {
            loadingDialog.dismiss();
            Toast.makeText(this, R.string.no_network, Toast.LENGTH_LONG).show();
            return;
        }
        SharedPreferences sharedPreferences = this.getSharedPreferences("myinfo", MODE_PRIVATE);
        String token = sharedPreferences.getString("token", "");
        String url = getResources().getString(R.string.local_url) + "/v1/task/" + choose_start_time + "/" + choose_end_time + "/" + categoryid + "/" + lableid + "?page=" + page;
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
                        JSONArray list = data.getJSONArray("list");
                        totalCount = data.getString("totalCount");
                        totalPage = data.getInt("totalPage");
                        currentPage = data.getString("currentPage");
                        Log.d("mDatas.length==>>", "" + mDatas.size());
                        if (list.length() == 0) {
                            LayoutInflater layoutInflater = LayoutInflater.from(TaskStatisticActivity.this);
                            View view = layoutInflater.inflate(R.layout.empty_view, null);
                            listview.setEmptyView(view);
                        } else {
                            for (int i = 0; i < list.length(); i++) {
                                JSONObject object = list.getJSONObject(i);
                                String id = object.getString("id");
                                String task_type = object.getString("task_type");
                                String image = object.getString("image");
                                String name = object.getString("name");
                                String category_name = object.getString("category_name");
                                String label_name = object.getString("label_name");
                                String complete_rate = object.getString("complete_rate");
                                int countdown = object.getInt("countdown");
                                TaskStatisticItem taskStatisticItem = new TaskStatisticItem(id, task_type, image, name, category_name, label_name, complete_rate, countdown);
                                mDatas.add(taskStatisticItem);
                            }
                            taskStatisticAdapter = new TaskStatisticAdapter(TaskStatisticActivity.this, mDatas);
                            taskStatisticAdapter.setOnClickMyTextView(new TaskStatisticAdapter.OnClickMyTextView() {
                                @Override
                                public void myTextViewClick(View view, int position) {
                                    switch (view.getId()) {
                                        case R.id.tv_info:
                                            Log.d("111", "" + position);
                                            String id = mDatas.get(position).getId();
                                            Intent intent = new Intent(TaskStatisticActivity.this, TaskShowInfoActivity.class);
                                            Bundle bundle = new Bundle();
                                            bundle.putString("id", id);
                                            intent.putExtras(bundle);
                                            startActivity(intent);
                                            break;
                                        case R.id.tv_tongji:
                                            Log.d("222", "" + position);
                                            String id2 = mDatas.get(position).getId();
                                            String name = mDatas.get(position).getName();
                                            Intent intent2 = new Intent(TaskStatisticActivity.this, TaskCompletStatActivity.class);
                                            Bundle bundle2 = new Bundle();
                                            bundle2.putString("id", id2);
                                            bundle2.putString("name", name);
                                            intent2.putExtras(bundle2);
                                            startActivity(intent2);
                                            break;
                                    }
                                }
                            });
                            listview.setAdapter(taskStatisticAdapter);
                        }
                    } else {
                        loadingDialog.dismiss();
                        Toast.makeText(TaskStatisticActivity.this, response.getString("message"), Toast.LENGTH_LONG).show();
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
                Toast.makeText(TaskStatisticActivity.this, R.string.no_network, Toast.LENGTH_LONG).show();
                return;
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                super.onFailure(statusCode, headers, responseString, throwable);
                loadingDialog.dismiss();
                Toast.makeText(TaskStatisticActivity.this, R.string.no_network, Toast.LENGTH_LONG).show();
                return;
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONArray errorResponse) {
                super.onFailure(statusCode, headers, throwable, errorResponse);
                loadingDialog.dismiss();
                Toast.makeText(TaskStatisticActivity.this, R.string.no_network, Toast.LENGTH_LONG).show();
                return;
            }
        });
    }

    private void initMoreData(String page, CharSequence choose_start_time, CharSequence choose_end_time, String categoryid, String lableid) {
        loadingDialog = new LoadingDialog(this, "加载中...", R.drawable.ic_dialog_loading);
        loadingDialog.show();
        if (NetworkUtils.checkNetWork(this) == false) {
            loadingDialog.dismiss();
            Toast.makeText(this, R.string.no_network, Toast.LENGTH_LONG).show();
            return;
        }
        SharedPreferences sharedPreferences = this.getSharedPreferences("myinfo", MODE_PRIVATE);
        String token = sharedPreferences.getString("token", "");
        String url = getResources().getString(R.string.local_url) + "/v1/task/" + choose_start_time + "/" + choose_end_time + "/" + categoryid + "/" + lableid + "?page=" + page;
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
                        JSONArray list = data.getJSONArray("list");
                        totalCount = data.getString("totalCount");
                        totalPage = data.getInt("totalPage");
                        currentPage = data.getString("currentPage");
                        Log.d("mDatas.length==>>", "" + mDatas.size());
                        if (list.length() == 0) {
                            LayoutInflater layoutInflater = LayoutInflater.from(TaskStatisticActivity.this);
                            View view = layoutInflater.inflate(R.layout.empty_view, null);
                            listview.setEmptyView(view);
                        } else {
                            for (int i = 0; i < list.length(); i++) {
                                JSONObject object = list.getJSONObject(i);
                                String id = object.getString("id");
                                String task_type = object.getString("task_type");
                                String image = object.getString("image");
                                String name = object.getString("name");
                                String category_name = object.getString("category_name");
                                String label_name = object.getString("label_name");
                                String complete_rate = object.getString("complete_rate");
                                int countdown = object.getInt("countdown");
                                TaskStatisticItem taskStatisticItem = new TaskStatisticItem(id, task_type, image, name, category_name, label_name, complete_rate, countdown);
                                mDatas.add(taskStatisticItem);
                            }
                            taskStatisticAdapter.notifyDataSetChanged();
                            listview.setLoadCompleted();

                        }
                    } else {
                        loadingDialog.dismiss();
                        Toast.makeText(TaskStatisticActivity.this, response.getString("message"), Toast.LENGTH_LONG).show();
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
                Toast.makeText(TaskStatisticActivity.this, R.string.no_network, Toast.LENGTH_LONG).show();
                return;
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                super.onFailure(statusCode, headers, responseString, throwable);
                loadingDialog.dismiss();
                Toast.makeText(TaskStatisticActivity.this, R.string.no_network, Toast.LENGTH_LONG).show();
                return;
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONArray errorResponse) {
                super.onFailure(statusCode, headers, throwable, errorResponse);
                loadingDialog.dismiss();
                Toast.makeText(TaskStatisticActivity.this, R.string.no_network, Toast.LENGTH_LONG).show();
                return;
            }
        });
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
                    if (mDatas.size() > 0) {
                        mDatas.clear();
                    }
                    initData(currentPage, choose_start_time, choose_end_time, categoryid, lableid);
                    taskStatisticAdapter.notifyDataSetChanged();
                    isRefresh = false;
                }
            }, 3000);
        }
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.back:
                finish();
                break;
            case R.id.tv_choose://筛选
                drawerLayout.openDrawer(Gravity.RIGHT);
                getCategoryList();
                break;
            case R.id.tv_start_time:
                final Calendar c;
                c = Calendar.getInstance();
                DatePickerDialog dialog = new DatePickerDialog(TaskStatisticActivity.this, new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                        c.set(year, monthOfYear, dayOfMonth);
                        Log.d("data>>>", "" + DateFormat.format("yyy-MM-dd", c));
                        tv_start_time.setText(DateFormat.format("yyy-MM-dd", c));
                        choose_start_time = DateFormat.format("yyy-MM-dd", c);
                    }
                }, c.get(Calendar.YEAR), c.get(Calendar.MONTH), c.get(Calendar.DAY_OF_MONTH));
                dialog.show();
                break;
            case R.id.tv_end_time:
                final Calendar c2;
                c2 = Calendar.getInstance();
                DatePickerDialog dialog2 = new DatePickerDialog(TaskStatisticActivity.this, new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                        c2.set(year, monthOfYear, dayOfMonth);
                        Log.d("data>>>", "" + DateFormat.format("yyy-MM-dd", c2));
                        tv_end_time.setText(DateFormat.format("yyy-MM-dd", c2));
                        choose_end_time = DateFormat.format("yyy-MM-dd", c2);
                    }
                }, c2.get(Calendar.YEAR), c2.get(Calendar.MONTH), c2.get(Calendar.DAY_OF_MONTH));
                dialog2.show();
                break;
            case R.id.tv_clear:
                //清空选择按钮
                if (rb_xiaoxue.isChecked()) {
                    rb_xiaoxue.setTextColor(getResources().getColor(R.color.tab_checked));
                }
                if (rb_youeryuan.isChecked()) {
                    rb_youeryuan.setTextColor(getResources().getColor(R.color.tab_checked));
                }
                if (rb_chuzhong.isChecked()) {
                    rb_chuzhong.setTextColor(getResources().getColor(R.color.tab_checked));
                }
                rb_xiaoxue.setChecked(false);
                rb_youeryuan.setChecked(false);
                rb_chuzhong.setChecked(false);
                school_scope = 0;
                //清空时间
                SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");// HH:mm:ss
                //获取当前时间
                Date date = new Date(System.currentTimeMillis());
                tv_start_time.setText(simpleDateFormat.format(date));
                tv_end_time.setText(simpleDateFormat.format(date));
                choose_start_time = "0";
                choose_end_time = "0";
                //清空选择分类和标签
                listLableListItem = new ArrayList<>();
                String id = ("");
                String name = ("");
                categoryListItem = new CategoryListItem(id, name);
                listLableListItem.add(categoryListItem);
                CategoryListViewAdapter categoryListViewAdapter = new CategoryListViewAdapter(TaskStatisticActivity.this, listLableListItem);
                lv_label.setAdapter(categoryListViewAdapter);
                lableid = "0";
                categoryid = "0";
                SharedPreferences sp2 = getSharedPreferences("parameters", MODE_PRIVATE);
                SharedPreferences.Editor editor2 = sp2.edit();
                editor2.putString("start", "0");
                editor2.putString("end", "0");
                editor2.putString("cid", "0");
                editor2.putString("lid", "0");
                editor2.commit();
                break;
            case R.id.tv_ok:
                Log.d("Parameters==>>", "start:" + choose_start_time + "/" + "end:" + choose_end_time + "/" + "cid:" + categoryid + "/" + "lid:" + lableid);
                SharedPreferences sp = getSharedPreferences("parameters", MODE_PRIVATE);
                SharedPreferences.Editor editor = sp.edit();
                editor.putString("start", String.valueOf(choose_start_time));
                editor.putString("end", String.valueOf(choose_end_time));
                editor.putString("cid", categoryid);
                editor.putString("lid", lableid);
                editor.commit();
                mDatas.clear();
                initData("1", choose_start_time, choose_end_time, categoryid, lableid);
                taskStatisticAdapter.notifyDataSetChanged();
                drawerLayout.closeDrawers();
                break;
        }
    }

    @Override
    public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
        Log.d("333", "333");


    }


    private void getCategoryList() {
        if (NetworkUtils.checkNetWork(TaskStatisticActivity.this) == false) {
            Toast.makeText(TaskStatisticActivity.this, R.string.no_network, Toast.LENGTH_LONG).show();
            return;
        }
        SharedPreferences sharedPreferences = TaskStatisticActivity.this.getSharedPreferences("myinfo", MODE_PRIVATE);
        String token = sharedPreferences.getString("token", "");
        String url = getResources().getString(R.string.local_url) + "/v1/achievement/scope/";
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
                        JSONObject data = response.getJSONObject("data");
                        JSONArray list = data.getJSONArray("category");
                        listCategoryListItem = new ArrayList<>();
                        if (list.length() <= 0) {

                        } else {
                            for (int i = 0; i < list.length(); i++) {
                                JSONObject object = list.getJSONObject(i);
                                String id = object.getString("id");
                                String name = object.getString("name");
                                categoryListItem = new CategoryListItem(id, name);
                                listCategoryListItem.add(categoryListItem);
                            }
                            CategoryListViewAdapter categoryListViewAdapter = new CategoryListViewAdapter(TaskStatisticActivity.this, listCategoryListItem);
                            lv_category.setAdapter(categoryListViewAdapter);
                        }
                    } else {
                        Toast.makeText(TaskStatisticActivity.this, response.getString("message"), Toast.LENGTH_LONG).show();
                        return;
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                super.onFailure(statusCode, headers, throwable, errorResponse);
                Toast.makeText(TaskStatisticActivity.this, R.string.no_network, Toast.LENGTH_LONG).show();
                return;
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                super.onFailure(statusCode, headers, responseString, throwable);
                Toast.makeText(TaskStatisticActivity.this, R.string.no_network, Toast.LENGTH_LONG).show();
                return;
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONArray errorResponse) {
                super.onFailure(statusCode, headers, throwable, errorResponse);
                Toast.makeText(TaskStatisticActivity.this, R.string.no_network, Toast.LENGTH_LONG).show();
                return;
            }
        });
    }

    private void doGetLableList(String id) {
        if (NetworkUtils.checkNetWork(TaskStatisticActivity.this) == false) {
            Toast.makeText(TaskStatisticActivity.this, R.string.no_network, Toast.LENGTH_LONG).show();
            return;
        }
        SharedPreferences sharedPreferences = TaskStatisticActivity.this.getSharedPreferences("myinfo", MODE_PRIVATE);
        String token = sharedPreferences.getString("token", "");
        String url = getResources().getString(R.string.local_url) + "/v1/achievement/label/" + id;
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
                        JSONObject data = response.getJSONObject("data");
                        JSONArray list = data.getJSONArray("list");
                        listLableListItem = new ArrayList<>();
                        if (list.length() <= 0) {
                            String id = ("");
                            String name = ("");
                            categoryListItem = new CategoryListItem(id, name);
                            listLableListItem.add(categoryListItem);
                            CategoryListViewAdapter categoryListViewAdapter = new CategoryListViewAdapter(TaskStatisticActivity.this, listLableListItem);
                            lv_label.setAdapter(categoryListViewAdapter);
                        } else {
                            for (int i = 0; i < list.length(); i++) {
                                JSONObject object = list.getJSONObject(i);
                                String id = object.getString("id");
                                String name = object.getString("name");
                                categoryListItem = new CategoryListItem(id, name);
                                listLableListItem.add(categoryListItem);
                            }
                            CategoryListViewAdapter categoryListViewAdapter = new CategoryListViewAdapter(TaskStatisticActivity.this, listLableListItem);
                            lv_label.setAdapter(categoryListViewAdapter);
                        }
                    } else {
                        Toast.makeText(TaskStatisticActivity.this, response.getString("message"), Toast.LENGTH_LONG).show();
                        return;
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                super.onFailure(statusCode, headers, throwable, errorResponse);
                Toast.makeText(TaskStatisticActivity.this, R.string.no_network, Toast.LENGTH_LONG).show();
                return;
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                super.onFailure(statusCode, headers, responseString, throwable);
                Toast.makeText(TaskStatisticActivity.this, R.string.no_network, Toast.LENGTH_LONG).show();
                return;
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONArray errorResponse) {
                super.onFailure(statusCode, headers, throwable, errorResponse);
                Toast.makeText(TaskStatisticActivity.this, R.string.no_network, Toast.LENGTH_LONG).show();
                return;
            }
        });
    }

}
