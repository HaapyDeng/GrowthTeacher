package com.mpl.GrowthTeacher.Activity;


import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.mpl.GrowthTeacher.R;
import com.mpl.GrowthTeacher.Tools.NetworkUtils;
import com.mpl.GrowthTeacher.View.CircleImageView;
import com.mpl.GrowthTeacher.View.LoadingDialog;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import cz.msebera.android.httpclient.Header;

import static android.content.Context.MODE_PRIVATE;

/**
 * A simple {@link Fragment} subclass.
 */
public class MyFragment extends Fragment implements View.OnClickListener {
    private CircleImageView head_img;
    private TextView tv_teacher_name, tv_classname, tv_school;
    private ImageView iv_gender, ib_message;
    private LinearLayout own_class, ll_school, ll_changepsd, ll_set;
    private LoadingDialog loadingDialog;
    private String schoolName, name, class_name = "", class_grade = "";
    private int gender;


    public static MyFragment newInstance(String name) {
        MyFragment fragment = new MyFragment();
        Bundle args = new Bundle();
        args.putString("name", name);
        fragment.setArguments(args);
        return fragment;
    }

    public MyFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_my, container, false);
        head_img = root.findViewById(R.id.head_img);
        tv_teacher_name = root.findViewById(R.id.tv_teacher_name);
        tv_classname = root.findViewById(R.id.tv_classname);
        tv_school = root.findViewById(R.id.tv_school);
        iv_gender = root.findViewById(R.id.iv_gender);
        ib_message = root.findViewById(R.id.ib_message);
        ib_message.setOnClickListener(this);
        own_class = root.findViewById(R.id.own_class);
        own_class.setOnClickListener(this);
        ll_school = root.findViewById(R.id.ll_school);
        ll_school.setOnClickListener(this);
        ll_changepsd = root.findViewById(R.id.ll_changepsd);
        ll_changepsd.setOnClickListener(this);
        ll_set = root.findViewById(R.id.ll_set);
        ll_set.setOnClickListener(this);
        initData();
        return root;
    }

    private void initData() {
        loadingDialog = new LoadingDialog(getContext(), "加载中...", R.drawable.ic_dialog_loading);
        loadingDialog.show();
        if (NetworkUtils.checkNetWork(getActivity()) == false) {
            loadingDialog.dismiss();
            Toast.makeText(getActivity(), R.string.no_network, Toast.LENGTH_LONG).show();
            return;
        }
        SharedPreferences sharedPreferences = getActivity().getSharedPreferences("myinfo", MODE_PRIVATE);
        String token = sharedPreferences.getString("token", "");
        String url = getResources().getString(R.string.local_url) + "/user";
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
                        schoolName = data.getString("school_name");
                        name = data.getString("name");
                        gender = data.getInt("gender");
                        JSONArray classroom = data.getJSONArray("classroom");
                        if (classroom.length() > 0) {
                            JSONObject object = classroom.getJSONObject(0);
                            class_grade = object.getString("grade");
                            class_name = object.getString("name");
                        }
                        Message message = new Message();
                        message.what = 1;
                        handler.sendMessage(message);
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

    private Handler handler = new Handler() {
        @SuppressLint("NewApi")
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case 1:
                    if (gender == 1) {
                        head_img.setImageDrawable(getResources().getDrawable(R.mipmap.head_techer_man));
                        iv_gender.setBackground(getResources().getDrawable(R.mipmap.little_man));
                    } else {
                        head_img.setImageDrawable(getResources().getDrawable(R.mipmap.head_teacher_woman));
                        iv_gender.setBackground(getResources().getDrawable(R.mipmap.little_woman));
                    }
                    tv_teacher_name.setText(name);
                    tv_classname.setText(class_grade + "·" + class_name);
                    tv_school.setText(schoolName);
                    break;
            }
        }
    };

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.ib_message:
                Intent intent = new Intent(getActivity(), MessageActivity.class);
                startActivity(intent);
                break;
            case R.id.ll_changepsd:
                Intent intent1 = new Intent(getActivity(), ChangePsdActivity.class);
                startActivity(intent1);
                break;
            case R.id.ll_set:
                Intent intent2 = new Intent(getActivity(), SettingActivity.class);
                startActivity(intent2);
                break;
        }

    }
}
