package com.mpl.GrowthTeacher.Activity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.mpl.GrowthTeacher.Adapter.TaskStatisticAdapter;
import com.mpl.GrowthTeacher.Bean.TaskStatisticItem;
import com.mpl.GrowthTeacher.R;
import com.mpl.GrowthTeacher.Tools.NetworkUtils;
import com.mpl.GrowthTeacher.View.LoadingDialog;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import cz.msebera.android.httpclient.Header;

public class TaskCompletStatActivity extends AppCompatActivity implements View.OnClickListener {
    private String id, name;
    private LinearLayout back;
    private TextView tv_task_name, tv_task_rate, tv_complet, tv_total, tv_ctext, tv_notctext, tv_choose_text, tv_choose;
    private LinearLayout fragment;
    private ProgressBar progressBar;
    private TaskCompletBlankFragment fragment1;
    private TaskNotCompletFragment fragment2;
    private android.app.FragmentManager fm;//管理器
    private LoadingDialog loadingDialog;
    private String all = "全部";

    private String task_rate, complet, total;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_task_complet_stat);
        Intent intent = getIntent();
        Bundle bundle = intent.getExtras();
        id = bundle.getString("id");
        name = bundle.getString("name");

        back = findViewById(R.id.back);
        back.setOnClickListener(this);

        progressBar = findViewById(R.id.progressBar);

        tv_choose = findViewById(R.id.tv_choose);
        tv_choose.setOnClickListener(this);

        tv_task_name = findViewById(R.id.tv_task_name);
        tv_task_rate = findViewById(R.id.tv_task_rate);
        tv_complet = findViewById(R.id.tv_complet);
        tv_total = findViewById(R.id.tv_total);
        tv_ctext = findViewById(R.id.tv_ctext);
        tv_ctext.setOnClickListener(this);
        tv_notctext = findViewById(R.id.tv_notctext);
        tv_notctext.setOnClickListener(this);
        tv_choose_text = findViewById(R.id.tv_choose_text);
        SharedPreferences sp = getSharedPreferences("taskcompletpragram", MODE_PRIVATE);
        SharedPreferences.Editor editor = sp.edit();
        editor.putString("cid", "0");
        editor.commit();
        selectFragment(0);
        initData(id, "0", "0");
    }

    private void initData(String id, String type, String cid) {
        loadingDialog = new LoadingDialog(this, "加载中...", R.drawable.ic_dialog_loading);
        loadingDialog.show();
        if (NetworkUtils.checkNetWork(this) == false) {
            loadingDialog.dismiss();
            Toast.makeText(this, R.string.no_network, Toast.LENGTH_LONG).show();
            return;
        }
        SharedPreferences sharedPreferences = this.getSharedPreferences("myinfo", MODE_PRIVATE);
        String token = sharedPreferences.getString("token", "");
        String url = getResources().getString(R.string.local_url) + "/v1/task/user/" + id + "/" + type + "/" + cid;
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
                        complet = data.getString("completeNumber");
                        total = data.getString("joinNumber");
                        Message message = new Message();
                        message.what = 1;
                        handler.sendMessage(message);
                    } else {
                        loadingDialog.dismiss();
                        Toast.makeText(TaskCompletStatActivity.this, response.getString("message"), Toast.LENGTH_LONG).show();
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
                Toast.makeText(TaskCompletStatActivity.this, R.string.no_network, Toast.LENGTH_LONG).show();
                return;
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                super.onFailure(statusCode, headers, responseString, throwable);
                loadingDialog.dismiss();
                Toast.makeText(TaskCompletStatActivity.this, R.string.no_network, Toast.LENGTH_LONG).show();
                return;
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONArray errorResponse) {
                super.onFailure(statusCode, headers, throwable, errorResponse);
                loadingDialog.dismiss();
                Toast.makeText(TaskCompletStatActivity.this, R.string.no_network, Toast.LENGTH_LONG).show();
                return;
            }
        });
    }

    private Handler handler = new Handler() {
        @SuppressLint("NewApi")
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case 1:
                    tv_task_name.setText(name);
                    tv_complet.setText(complet);
                    tv_total.setText("/" + total);
                    int c = Integer.parseInt(complet);
                    int t = Integer.parseInt(total);
                    int n = t - c;
                    if (c == 0) {
                        tv_task_rate.setText("0%");
                        progressBar.setMax(100);
                        progressBar.setProgress(0);
                    } else {
                        float pressent = (float) c / t * 100;
                        tv_task_rate.setText("" + pressent + "%");
                        progressBar.setMax(t);
                        progressBar.setProgress(c);
                    }
                    tv_ctext.setText("已完成" + "(" + c + ")");
                    tv_notctext.setText("未完成" + "(" + n + ")");
                    tv_choose_text.setText(all);
                    break;
            }
        }
    };
    // 切换Fragment

    private void selectFragment(int i) {

        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();

        hideFragment(transaction);

        changeView(i);// 设置选项颜色

        switch (i) {

            case 0:

                if (fragment1 == null) {

                    fragment1 = new TaskCompletBlankFragment();

                    transaction.add(R.id.fragment, fragment1);

                }
//                Bundle bundle = new Bundle();
//                bundle.putString("studentId", studentId);
//                fragment1.setArguments(bundle);

                transaction.show(fragment1);

                break;


            case 1:

                if (fragment2 == null) {

                    fragment2 = new TaskNotCompletFragment();

                    transaction.add(R.id.fragment, fragment2);

                }
//                Bundle bundle2 = new Bundle();
//                bundle2.putString("studentId", studentId);
//                fragment2.setArguments(bundle2);

                transaction.show(fragment2);

                break;

        }

        transaction.commit();

    }
    // 隐藏fragment

    private void hideFragment(FragmentTransaction transaction) {

        if (fragment1 != null) {

            transaction.hide(fragment1);

        }

        if (fragment2 != null) {

            transaction.hide(fragment2);

        }


    }
    //改变字体和背景色状态

    @SuppressLint("NewApi")

    private void changeView(int i) {

        if (i == 0) {

            //设置背景色及字体颜色

            tv_ctext.setBackgroundColor(getResources().getColor(R.color.getstarinfo));

            tv_ctext.setTextColor(getResources().getColor(R.color.text));

            tv_notctext.setBackground(getDrawable(R.drawable.textview_border));

            tv_notctext.setTextColor(getResources().getColor(R.color.getstarinfo));

        } else if (i == 1) {

            tv_notctext.setBackgroundColor(getResources().getColor(R.color.getstarinfo));

            tv_notctext.setTextColor(getResources().getColor(R.color.text));

            tv_ctext.setBackground(getDrawable(R.drawable.textview_border));

            tv_ctext.setTextColor(getResources().getColor(R.color.getstarinfo));

        }

    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.back:
                finish();
                break;
            case R.id.tv_ctext:
                selectFragment(0);
                break;
            case R.id.tv_notctext:
                selectFragment(1);
                break;
            case R.id.tv_choose:
                break;
        }
    }
}
