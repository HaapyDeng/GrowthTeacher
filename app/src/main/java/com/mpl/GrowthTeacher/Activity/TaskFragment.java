package com.mpl.GrowthTeacher.Activity;


import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.mpl.GrowthTeacher.R;
import com.mpl.GrowthTeacher.Tools.NetworkUtils;
import com.mpl.GrowthTeacher.View.LoadMoreListView;
import com.mpl.GrowthTeacher.View.LoadingDialog;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import cz.msebera.android.httpclient.Header;

import static android.content.Context.MODE_PRIVATE;

/**
 * A simple {@link Fragment} subclass.
 */
public class TaskFragment extends Fragment implements View.OnClickListener, SwipeRefreshLayout.OnRefreshListener {
    private ImageView ib_message;
    private LoadingDialog loadingDialog;
    private TextView tv_check_more;
    private LoadMoreListView listview;
    private String currentPage = "1";
    private int totalPage;

    private SwipeRefreshLayout mSwipeLayout;
    private boolean isRefresh = false;//是否刷新中


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
                Intent intent1 = new Intent(getActivity(), CheckMoreTaskActivity.class);
                startActivity(intent1);
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
//                    if (mDatas.size() > 0) {
//                        mDatas.clear();
//                    }
                    doGetTask(currentPage);
//                    comprehensiveEvaluationAdapter.notifyDataSetChanged();
                    isRefresh = false;
                }
            }, 3000);
        }
    }
}
