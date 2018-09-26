package com.mpl.GrowthTeacher.Activity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.ImageFormat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;
import com.mpl.GrowthTeacher.R;
import com.mpl.GrowthTeacher.Tools.NetworkUtils;
import com.mpl.GrowthTeacher.View.LoadingDialog;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import cz.msebera.android.httpclient.Header;

public class ChangePsdActivity extends AppCompatActivity {
    private LinearLayout back;
    private EditText et_oldpassword, et_newpassword, et_newpasswordagain;
    private TextView tv_warming, tv_save;
    private String oldpassword, newpassword, newpasswordagain;
    private LoadingDialog loadingDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_change_psd);
        back = findViewById(R.id.back);
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
        SharedPreferences sharedPreferences = getSharedPreferences("myinfo", MODE_PRIVATE);
        final String token = sharedPreferences.getString("token", "");
        final String password = sharedPreferences.getString("password", "");
        et_oldpassword = findViewById(R.id.et_oldpassword);
        et_newpassword = findViewById(R.id.et_newpassword);
        et_newpasswordagain = findViewById(R.id.et_newpasswordagain);
        tv_warming = findViewById(R.id.tv_warming);
        tv_save = findViewById(R.id.tv_save);
        tv_save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                oldpassword = et_oldpassword.getText().toString().trim();
                newpassword = et_newpassword.getText().toString().trim();
                newpasswordagain = et_newpasswordagain.getText().toString();
                if (oldpassword.equals("")) {
                    tv_warming.setVisibility(View.VISIBLE);
                    tv_warming.setText("请输入密码");
                    return;
                } else {
                    tv_warming.setVisibility(View.INVISIBLE);
                }
                if (newpassword.equals("")) {
                    tv_warming.setVisibility(View.VISIBLE);
                    tv_warming.setText("请输入密码");
                    return;
                } else {
                    tv_warming.setVisibility(View.INVISIBLE);
                }
                if (newpasswordagain.equals("")) {
                    tv_warming.setVisibility(View.VISIBLE);
                    tv_warming.setText("请输入密码");
                    return;
                } else {
                    tv_warming.setVisibility(View.INVISIBLE);
                }
                if (!oldpassword.equals(password)) {
                    tv_warming.setVisibility(View.VISIBLE);
                    tv_warming.setText("请输入正确密码");
                    return;
                } else {
                    tv_warming.setVisibility(View.INVISIBLE);
                }
                if (!newpassword.equals(newpasswordagain)) {
                    tv_warming.setVisibility(View.VISIBLE);
                    tv_warming.setText("请输入一致的新密码");
                    return;
                } else {
                    tv_warming.setVisibility(View.INVISIBLE);
                }
                doSavePassword(token, oldpassword, newpassword);
            }
        });
    }

    private void doSavePassword(String token, String oldpassword, final String newpassword) {
        loadingDialog = new LoadingDialog(this, "加载中...", R.drawable.ic_dialog_loading);
        loadingDialog.show();
        if (NetworkUtils.checkNetWork(this) == false) {
            loadingDialog.dismiss();
            Toast.makeText(this, R.string.no_network, Toast.LENGTH_LONG).show();
            return;
        }
        String url = getResources().getString(R.string.local_url) + "/user/teacher/update";
        Log.d("url==>>", url);
        AsyncHttpClient client = new AsyncHttpClient();
        RequestParams params = new RequestParams();
        params.put("password", oldpassword);
        params.put("newPassword", newpassword);
        client.addHeader("X-Api-Token", token);
        client.post(url, params, new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                super.onSuccess(statusCode, headers, response);
                Log.d("response==>>", response.toString());
                try {
                    int code = response.getInt("code");
                    if (code == 0) {
                        loadingDialog.dismiss();
                        SharedPreferences sharedPreferences = getSharedPreferences("myinfo", MODE_PRIVATE);
                        SharedPreferences.Editor editor = sharedPreferences.edit();
                        editor.putString("password", newpassword);
                        editor.commit();
                        Intent intent = new Intent(ChangePsdActivity.this, MainActivity.class);
                        Bundle bundle = new Bundle();
                        bundle.putInt("flag", 2);
                        intent.putExtras(bundle);
                        startActivity(intent);
                        finish();
                        Toast.makeText(ChangePsdActivity.this, "保存成功", Toast.LENGTH_LONG).show();
                    } else {
                        loadingDialog.show();
                        Toast.makeText(ChangePsdActivity.this, response.getString("message"), Toast.LENGTH_LONG).show();
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
                Toast.makeText(ChangePsdActivity.this, R.string.no_network, Toast.LENGTH_LONG).show();
                return;
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                super.onFailure(statusCode, headers, responseString, throwable);
                loadingDialog.dismiss();
                Toast.makeText(ChangePsdActivity.this, R.string.no_network, Toast.LENGTH_LONG).show();
                return;
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONArray errorResponse) {
                super.onFailure(statusCode, headers, throwable, errorResponse);
                loadingDialog.dismiss();
                Toast.makeText(ChangePsdActivity.this, R.string.no_network, Toast.LENGTH_LONG).show();
                return;
            }
        });
    }
}
