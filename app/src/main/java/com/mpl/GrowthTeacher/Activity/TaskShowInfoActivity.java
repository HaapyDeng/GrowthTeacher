package com.mpl.GrowthTeacher.Activity;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.mpl.GrowthTeacher.R;
import com.mpl.GrowthTeacher.Tools.NetworkUtils;
import com.mpl.GrowthTeacher.View.LoadingDialog;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import cz.msebera.android.httpclient.Header;

public class TaskShowInfoActivity extends Activity {
    private String id;
    private LinearLayout back;
    private TextView tv_start_t, tv_end_t, tv_score, tv_type, tv_name, tv_answer_name;
    private LoadingDialog loadingDialog;
    private String start, end, name;
    private JSONArray write_by;
    int type, write_by_type, star;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_task_show_info);
        Intent intent = getIntent();
        Bundle bundle = intent.getExtras();
        id = bundle.getString("id");
        back = findViewById(R.id.back);
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
        tv_start_t = findViewById(R.id.tv_start_t);
        tv_end_t = findViewById(R.id.tv_end_t);
        tv_score = findViewById(R.id.tv_score);
        tv_type = findViewById(R.id.tv_type);
        tv_name = findViewById(R.id.tv_name);
        tv_answer_name = findViewById(R.id.tv_answer_name);
        initData(id);
    }

    private void initData(String id) {
        loadingDialog = new LoadingDialog(this, "加载中...", R.drawable.ic_dialog_loading);
        loadingDialog.show();
        if (NetworkUtils.checkNetWork(this) == false) {
            loadingDialog.dismiss();
            Toast.makeText(this, R.string.no_network, Toast.LENGTH_LONG).show();
            return;
        }
        SharedPreferences sharedPreferences = this.getSharedPreferences("myinfo", MODE_PRIVATE);
        String token = sharedPreferences.getString("token", "");
        String url = getResources().getString(R.string.local_url) + "/v1/task/default/show/" + id;
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
                        start = data.getString("start");
                        end = data.getString("end");
                        type = data.getInt("type");
                        name = data.getString("name");
                        write_by_type = data.getInt("write_by_type");
                        write_by = data.getJSONArray("write_by");
                        star = data.getInt("star");
                        Message message = new Message();
                        message.what = 1;
                        handler.sendMessage(message);
                    } else {
                        loadingDialog.dismiss();
                        Toast.makeText(TaskShowInfoActivity.this, response.getString("message"), Toast.LENGTH_LONG).show();
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
                Toast.makeText(TaskShowInfoActivity.this, R.string.no_network, Toast.LENGTH_LONG).show();
                return;
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                super.onFailure(statusCode, headers, responseString, throwable);
                loadingDialog.dismiss();
                Toast.makeText(TaskShowInfoActivity.this, R.string.no_network, Toast.LENGTH_LONG).show();
                return;
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONArray errorResponse) {
                super.onFailure(statusCode, headers, throwable, errorResponse);
                loadingDialog.dismiss();
                Toast.makeText(TaskShowInfoActivity.this, R.string.no_network, Toast.LENGTH_LONG).show();
                return;
            }
        });
    }

    @SuppressLint("HandlerLeak")
    private Handler handler = new Handler() {
        @SuppressLint("NewApi")
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case 1:
                    tv_start_t.setText(start);
                    tv_end_t.setText(end);
                    switch (star) {
                        case 0:
                            tv_score.setText("零颗星");
                            break;
                        case 1:
                            tv_score.setText("一颗星");
                            break;
                        case 2:
                            tv_score.setText("二颗星");
                            break;
                        case 3:
                            tv_score.setText("三颗星");
                            break;
                        case 4:
                            tv_score.setText("四颗星");
                            break;
                        case 5:
                            tv_score.setText("五颗星");
                            break;
                    }
                    switch (write_by_type) {
                        case 1:
                            tv_type.setText("文字");
                            break;
                        case 2:
                            tv_type.setText("图文");
                            break;
                        case 3:
                            tv_type.setText("视频");
                            break;
                    }
                    tv_name.setText(name);
                    switch (write_by.length()) {
                        case 0:
                            break;
                        case 1:
                            tv_answer_name.setText("学生");
                            break;
                        case 2:
                            tv_answer_name.setText("学生、家长各填一份");
                            break;
                        case 3:
                            tv_answer_name.setText("学生、家长、老师各填一份");
                            break;
                    }
            }
        }
    };
}
