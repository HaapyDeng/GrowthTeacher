package com.mpl.GrowthTeacher.Activity;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.WindowManager;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.RadioGroup;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;
import com.mpl.GrowthTeacher.Bean.ChooseItemBean;
import com.mpl.GrowthTeacher.R;
import com.mpl.GrowthTeacher.Tools.NetworkUtils;
import com.mpl.GrowthTeacher.View.LoadingDialog;
import com.yangchangfu.pickview_lib.Item;
import com.yangchangfu.pickview_lib.PickView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import cz.msebera.android.httpclient.Header;

public class ReviewWenZiActivity extends Activity implements View.OnClickListener {
    private String taskId;
    private LoadingDialog loadingDialog;
    private String id, task_relation_id, name, username, updated_at, grade, classroom, complete_name, complete_role, start, star, prompt, answer;
    private String label, content;
    private int type, task_star;
    private LinearLayout back, ll_open;
    private TextView tv_name, tv_g_class, tv_time, tv_labe, tv_content;

    // 声明PopupWindow
    PopupWindow popupWindow;
    // 声明PopupWindow对应的视图
    View popupView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_review_wen_zi);
        tv_name = findViewById(R.id.tv_name);
        tv_g_class = findViewById(R.id.tv_g_class);
        tv_time = findViewById(R.id.tv_time);
        tv_labe = findViewById(R.id.tv_labe);
        tv_content = findViewById(R.id.tv_content);
        ll_open = findViewById(R.id.ll_open);
        ll_open.setOnClickListener(this);
        back = findViewById(R.id.back);
        back.setOnClickListener(this);
        Intent intent = getIntent();
        Bundle bundle = intent.getExtras();
        taskId = bundle.getString("taskId");
        doGetTaskInfo(taskId);
    }

    private void doGetTaskInfo(String taskId) {
        loadingDialog = new LoadingDialog(this, "加载中...", R.drawable.ic_dialog_loading);
        loadingDialog.show();
        if (NetworkUtils.checkNetWork(this) == false) {
            loadingDialog.dismiss();
            Toast.makeText(this, R.string.no_network, Toast.LENGTH_LONG).show();
            return;
        }
        SharedPreferences sharedPreferences = this.getSharedPreferences("myinfo", MODE_PRIVATE);
        String token = sharedPreferences.getString("token", "");
        String url = getResources().getString(R.string.local_url) + "/v1/achievement/text/show/" + taskId;
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
                        id = data.getString("id");
                        username = data.getString("username");
                        grade = data.getString("grade");
                        classroom = data.getString("classroom");
                        updated_at = data.getString("updated_at");
                        JSONArray labelary = data.optJSONArray("label");
                        label = labelary.getString(0);
                        task_star = data.getInt("task_star");
                        content = response.getString("answer");
                        Message message = new Message();
                        message.what = 1;
                        handler.sendMessage(message);
                    } else {
                        loadingDialog.dismiss();
                        Toast.makeText(ReviewWenZiActivity.this, response.getString("message"), Toast.LENGTH_LONG).show();
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
                Toast.makeText(ReviewWenZiActivity.this, R.string.no_network, Toast.LENGTH_LONG).show();
                return;
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                super.onFailure(statusCode, headers, responseString, throwable);
                loadingDialog.dismiss();
                Toast.makeText(ReviewWenZiActivity.this, R.string.no_network, Toast.LENGTH_LONG).show();
                return;
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONArray errorResponse) {
                super.onFailure(statusCode, headers, throwable, errorResponse);
                loadingDialog.dismiss();
                Toast.makeText(ReviewWenZiActivity.this, R.string.no_network, Toast.LENGTH_LONG).show();
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
                    tv_name.setText(username);
                    tv_g_class.setText(grade + "·" + classroom);
                    tv_time.setText(updated_at);
                    tv_labe.setText(label);
                    tv_content.setText(content);
                    ShowPopuWindow();
                    break;
            }
        }
    };

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.back:
                finish();
                break;
            case R.id.ll_open:
                ll_open.setVisibility(View.INVISIBLE);
                ShowPopuWindow();
                break;
        }
    }

    private void ShowPopuWindow() {
        final int[] choose_star = {0};
        // 声明平移动画
        popupView = View.inflate(this, R.layout.review_one_item, null);
        // 参数2,3：指明popupwindow的宽度和高度
        popupWindow = new PopupWindow(popupView, WindowManager.LayoutParams.MATCH_PARENT,
                WindowManager.LayoutParams.WRAP_CONTENT);
        // 设置点击popupwindow外屏幕其它地方不消失
        popupWindow.setOutsideTouchable(false);
        // 设置popupWindow的显示位置，此处是在手机屏幕底部且水平居中的位置
        popupWindow.showAtLocation(findViewById(R.id.tv_content), Gravity.BOTTOM | Gravity.CENTER_HORIZONTAL, 0, 0);
        LinearLayout ll_close = popupView.findViewById(R.id.ll_close);
        ll_close.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                popupWindow.dismiss();
                ll_open.setVisibility(View.VISIBLE);
            }
        });
        RatingBar rb = popupView.findViewById(R.id.rb);
        rb.setNumStars(task_star);
        rb.setOnRatingBarChangeListener(new RatingBar.OnRatingBarChangeListener() {
            @Override
            public void onRatingChanged(RatingBar ratingBar, float v, boolean b) {
                choose_star[0] = ratingBar.getNumStars();
            }
        });
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
                    ck_1.setTextColor(getColor(R.color.white));
                } else {
                    ck_1.setBackground(getResources().getDrawable(R.drawable.textview_shape_grey));
                    ck_1.setTextColor(getColor(R.color.text2));
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
                    ck_2.setTextColor(getColor(R.color.white));
                } else {
                    ck_2.setBackground(getResources().getDrawable(R.drawable.textview_shape_grey));
                    ck_2.setTextColor(getColor(R.color.text2));
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
                    ck_3.setTextColor(getColor(R.color.white));
                } else {
                    ck_3.setBackground(getResources().getDrawable(R.drawable.textview_shape_grey));
                    ck_3.setTextColor(getColor(R.color.text2));
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
                    ck_4.setTextColor(getColor(R.color.white));
                } else {
                    ck_4.setBackground(getResources().getDrawable(R.drawable.textview_shape_grey));
                    ck_4.setTextColor(getColor(R.color.text2));
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
                    ck_5.setTextColor(getColor(R.color.white));
                } else {
                    ck_5.setBackground(getResources().getDrawable(R.drawable.textview_shape_grey));
                    ck_5.setTextColor(getColor(R.color.text2));
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
                        ck_1.setText("图文并茂很生动");
                        ck_1.setBackground(getResources().getDrawable(R.drawable.textview_shape_grey));
                        ck_1.setTextColor(getColor(R.color.text2));
                        ck_2.setText("我看好你哦加油");
                        ck_2.setBackground(getResources().getDrawable(R.drawable.textview_shape_grey));
                        ck_2.setTextColor(getColor(R.color.text2));
                        ck_3.setText("相互共同进步");
                        ck_3.setBackground(getResources().getDrawable(R.drawable.textview_shape_grey));
                        ck_3.setTextColor(getColor(R.color.text2));
                        ck_4.setText("多学多问问题");
                        ck_4.setBackground(getResources().getDrawable(R.drawable.textview_shape_grey));
                        ck_4.setTextColor(getColor(R.color.text2));
                        ck_5.setText("很棒很棒");
                        ck_5.setBackground(getResources().getDrawable(R.drawable.textview_shape_grey));
                        ck_5.setTextColor(getColor(R.color.text2));
                        break;
                    case R.id.rb_no:
                        et_content.setText("");
                        sb[0] = new StringBuilder("");
                        rbText[0] = 2;
                        ck_1.setText("文字有违规字眼");
                        ck_1.setBackground(getResources().getDrawable(R.drawable.textview_shape_grey));
                        ck_1.setTextColor(getColor(R.color.text2));
                        ck_2.setText("图片有违规内容");
                        ck_2.setBackground(getResources().getDrawable(R.drawable.textview_shape_grey));
                        ck_2.setTextColor(getColor(R.color.text2));
                        ck_3.setText("未按要求记录");
                        ck_3.setBackground(getResources().getDrawable(R.drawable.textview_shape_grey));
                        ck_3.setTextColor(getColor(R.color.text2));
                        ck_4.setText("记录内容过少");
                        ck_4.setBackground(getResources().getDrawable(R.drawable.textview_shape_grey));
                        ck_4.setTextColor(getColor(R.color.text2));
                        ck_5.setText("有错别字");
                        ck_5.setBackground(getResources().getDrawable(R.drawable.textview_shape_grey));
                        ck_5.setTextColor(getColor(R.color.text2));
                        break;
                }
            }
        });
        TextView tv_commit = popupView.findViewById(R.id.tv_commit);
        tv_commit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                contentText[0] = et_content.getText().toString().trim();
                if (contentText[0].equals("")) {
                    Toast.makeText(ReviewWenZiActivity.this, "请添加评审内容", Toast.LENGTH_SHORT).show();
                    return;
                }
                doCommit(rbText[0], choose_star, id, contentText[0]);

            }
        });
    }

    private void doCommit(int i, int[] choose_star, String id, String content) {
        loadingDialog = new LoadingDialog(ReviewWenZiActivity.this, "加载中...", R.drawable.ic_dialog_loading);
        loadingDialog.show();
        if (NetworkUtils.checkNetWork(ReviewWenZiActivity.this) == false) {
            loadingDialog.dismiss();
            Toast.makeText(ReviewWenZiActivity.this, R.string.no_network, Toast.LENGTH_LONG).show();
            return;
        }
        SharedPreferences sharedPreferences = getSharedPreferences("myinfo", MODE_PRIVATE);
        String token = sharedPreferences.getString("token", "");
        String url = getResources().getString(R.string.local_url) + "/v1/achievement/audit/update/" + id;
        Log.d("url==>>", url);
        AsyncHttpClient client = new AsyncHttpClient();
        RequestParams params = new RequestParams();
        params.put("status", i);
        params.put("star", choose_star);
        params.put("content", content);
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
                        finish();

                    } else {
                        loadingDialog.dismiss();
                        Toast.makeText(ReviewWenZiActivity.this, response.getString("message"), Toast.LENGTH_LONG).show();
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
                Toast.makeText(ReviewWenZiActivity.this, R.string.no_network, Toast.LENGTH_LONG).show();
                return;
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                super.onFailure(statusCode, headers, responseString, throwable);
                loadingDialog.dismiss();
                Toast.makeText(ReviewWenZiActivity.this, R.string.no_network, Toast.LENGTH_LONG).show();
                return;
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONArray errorResponse) {
                super.onFailure(statusCode, headers, throwable, errorResponse);
                loadingDialog.dismiss();
                Toast.makeText(ReviewWenZiActivity.this, R.string.no_network, Toast.LENGTH_LONG).show();
                return;
            }
        });
    }
}
