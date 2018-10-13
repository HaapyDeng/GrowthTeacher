package com.mpl.GrowthTeacher.Activity;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.widget.SwipeRefreshLayout;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.mpl.GrowthTeacher.Adapter.ComprehensiveEvaluationAdapter;
import com.mpl.GrowthTeacher.Bean.ChooseItemBean;
import com.mpl.GrowthTeacher.Bean.ComprehensiveEvaluationItem;
import com.mpl.GrowthTeacher.R;
import com.mpl.GrowthTeacher.Tools.NetworkUtils;
import com.mpl.GrowthTeacher.View.LoadMoreListView;
import com.mpl.GrowthTeacher.View.LoadingDialog;
import com.paging.listview.PagingListView;
import com.yangchangfu.pickview_lib.Item;
import com.yangchangfu.pickview_lib.PickView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import cz.msebera.android.httpclient.Header;

public class ComprehensiveEvaluationActivity extends Activity implements View.OnClickListener, SwipeRefreshLayout.OnRefreshListener, AdapterView.OnItemClickListener {
    private TextView tv_choose, tv_total;
    private LinearLayout back;
    private LoadMoreListView listview;
    private LoadingDialog loadingDialog;
    private String currentPage = "1";
    private int totalPage;
    private String totalCount;
    private List<ComprehensiveEvaluationItem> mDatas = new ArrayList<ComprehensiveEvaluationItem>();
    private ComprehensiveEvaluationAdapter comprehensiveEvaluationAdapter;

    private SwipeRefreshLayout mSwipeLayout;
    private boolean isRefresh = false;//是否刷新中

    private List<Item> cates;
    private List<ChooseItemBean> chooseItemBeans;
    private PickView catePickView;
    private String cid = "0";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_comprehensive_evaluation);
        back = findViewById(R.id.back);
        back.setOnClickListener(this);

        tv_choose = findViewById(R.id.tv_choose);
        tv_choose.setOnClickListener(this);

        tv_total = findViewById(R.id.tv_total);


        listview = findViewById(R.id.listview);
        initData(currentPage, cid);
        listview.setOnLoadMoreListener(new LoadMoreListView.OnLoadMoreListener() {
            @Override
            public void onloadMore() {
                int i = Integer.parseInt(currentPage);
                Log.d("i==>>", "" + i);
                if (i < totalPage) {
                    initData("" + (Integer.getInteger(currentPage) + 1), cid);
                } else {
                    listview.setLoadCompleted();
                }
            }
        });
        listview.setOnItemClickListener(this);
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

    private void initData(String page, String classroom_id) {
        loadingDialog = new LoadingDialog(this, "加载中...", R.drawable.ic_dialog_loading);
        loadingDialog.show();
        if (NetworkUtils.checkNetWork(this) == false) {
            loadingDialog.dismiss();
            Toast.makeText(this, R.string.no_network, Toast.LENGTH_LONG).show();
            return;
        }
        SharedPreferences sharedPreferences = this.getSharedPreferences("myinfo", MODE_PRIVATE);
        String token = sharedPreferences.getString("token", "");
        String url = getResources().getString(R.string.local_url) + "/v1/achievement/statistical/student/" + classroom_id + "?page=" + page;
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
                        totalCount = data.getString("totalCount");
                        totalPage = data.getInt("totalPage");
                        currentPage = data.getString("currentPage");
                        JSONArray list = data.getJSONArray("list");
                        for (int i = 0; i < list.length(); i++) {
                            JSONObject object = list.getJSONObject(i);
                            String name = object.getString("name");
                            String gender = object.getString("gender");
                            String classroom_name = object.getString("classroom_name");
                            String id = object.getString("id");
                            String star = object.getString("star");
                            String grade = object.getString("grade");
                            int total_point = object.getInt("total_point");
                            ComprehensiveEvaluationItem comprehensiveEvaluationItem = new ComprehensiveEvaluationItem(name, gender, classroom_name, id, star, grade, total_point);
                            mDatas.add(comprehensiveEvaluationItem);
                        }
                        comprehensiveEvaluationAdapter = new ComprehensiveEvaluationAdapter(ComprehensiveEvaluationActivity.this, mDatas);
                        listview.setAdapter(comprehensiveEvaluationAdapter);

                        Message message = new Message();
                        message.what = 1;
                        handler.sendMessage(message);
                    } else {
                        loadingDialog.dismiss();
                        Toast.makeText(ComprehensiveEvaluationActivity.this, response.getString("message"), Toast.LENGTH_LONG).show();
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
                Toast.makeText(ComprehensiveEvaluationActivity.this, R.string.no_network, Toast.LENGTH_LONG).show();
                return;
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                super.onFailure(statusCode, headers, responseString, throwable);
                loadingDialog.dismiss();
                Toast.makeText(ComprehensiveEvaluationActivity.this, R.string.no_network, Toast.LENGTH_LONG).show();
                return;
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONArray errorResponse) {
                super.onFailure(statusCode, headers, throwable, errorResponse);
                loadingDialog.dismiss();
                Toast.makeText(ComprehensiveEvaluationActivity.this, R.string.no_network, Toast.LENGTH_LONG).show();
                return;
            }
        });
    }

    private Handler handler = new Handler(new Handler.Callback() {
        @Override
        public boolean handleMessage(Message msg) {
            switch (msg.what) {
                case 1:
                    tv_total.setText(totalCount + "人");
                    break;
            }
            return false;
        }
    });

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.back:
                finish();
                break;
            case R.id.tv_choose://筛选
                //加载筛选窗口数
                initCates();
                break;
        }
    }

    private void initCates() {
        loadingDialog = new LoadingDialog(this, "加载中...", R.drawable.ic_dialog_loading);
        loadingDialog.show();
        if (NetworkUtils.checkNetWork(this) == false) {
            loadingDialog.dismiss();
            Toast.makeText(this, R.string.no_network, Toast.LENGTH_LONG).show();
            return;
        }
        SharedPreferences sharedPreferences = this.getSharedPreferences("myinfo", MODE_PRIVATE);
        String token = sharedPreferences.getString("token", "");
        String url = getResources().getString(R.string.local_url) + "/v1/school/classroom";
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
                        List<Item> items = new ArrayList<>();
                        JSONArray data = response.getJSONArray("data");
                        for (int i = 0; i < data.length(); i++) {
                            Item item = new Item();
                            JSONObject object = data.getJSONObject(i);
                            item.name = object.getString("name");
                            List<Item> items1 = new ArrayList<>();
                            JSONArray grade = object.getJSONArray("item");
                            chooseItemBeans = new ArrayList<ChooseItemBean>();
                            for (int j = 0; j < grade.length(); j++) {
                                JSONObject object2 = grade.getJSONObject(j);
                                Item item1 = new Item();
                                item1.name = object2.getString("name");
                                String id = object2.getString("id");
                                ChooseItemBean bean = new ChooseItemBean(id, item1.name);
                                chooseItemBeans.add(bean);
                                items1.add(item1);
                            }
                            item.items = items1;
                            items.add(item);
                        }
                        ComprehensiveEvaluationActivity.this.cates = items;
                        catePickView = new PickView(ComprehensiveEvaluationActivity.this);
                        catePickView.setPickerView(cates, PickView.Style.DOUBLE);
                        catePickView.setShowSelectedTextView(false);
                        catePickView.show();
                        catePickView.setOnSelectListener(new PickView.OnSelectListener() {
                            @Override
                            public void OnSelectItemClick(View view, int[] selectedIndexs, String selectedText) {
                                Log.d("selectedText==>>", selectedText);
                                for (int i = 0; i < chooseItemBeans.size(); i++) {
                                    if ((selectedText.substring(selectedText.length() - 2)).equals(chooseItemBeans.get(i).getName())) {
                                        cid = chooseItemBeans.get(i).getId();
                                        Log.d("cid==>>", cid);
                                        if (mDatas.size() > 0) {
                                            mDatas.clear();
                                        }
                                        initData(currentPage, cid);
                                    }
                                }
                            }
                        });
                        catePickView.setOnStateChangeListener(new PickView.OnStateChangeListener() {
                            @Override
                            public void OnStateChange(View view, boolean state) {
                                Log.d("选择器2: ", (catePickView.isShow ? "打开" : "关闭"));
                                System.out.println("catePickView isshow = " + catePickView.isShow);
                            }
                        });
                    } else {
                        loadingDialog.dismiss();
                        Toast.makeText(ComprehensiveEvaluationActivity.this, response.getString("message"), Toast.LENGTH_LONG).show();
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
                Toast.makeText(ComprehensiveEvaluationActivity.this, R.string.no_network, Toast.LENGTH_LONG).show();
                return;
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                super.onFailure(statusCode, headers, responseString, throwable);
                loadingDialog.dismiss();
                Toast.makeText(ComprehensiveEvaluationActivity.this, R.string.no_network, Toast.LENGTH_LONG).show();
                return;
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONArray errorResponse) {
                super.onFailure(statusCode, headers, throwable, errorResponse);
                loadingDialog.dismiss();
                Toast.makeText(ComprehensiveEvaluationActivity.this, R.string.no_network, Toast.LENGTH_LONG).show();
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
                    initData(currentPage, cid);
                    comprehensiveEvaluationAdapter.notifyDataSetChanged();
                    isRefresh = false;
                }
            }, 3000);
        }
    }


    @Override
    public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
        String studentId = mDatas.get(i).getId();
        Intent intent = new Intent(this, ComprehensiveEvaItemDetailActivity.class);
        Bundle bundle = new Bundle();
        bundle.putString("studentId", studentId);
        intent.putExtras(bundle);
        startActivity(intent);
    }
}
