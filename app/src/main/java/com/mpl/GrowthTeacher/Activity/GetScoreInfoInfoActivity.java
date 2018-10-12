package com.mpl.GrowthTeacher.Activity;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.mpl.GrowthTeacher.Adapter.GetScoreInfoInfoListViewAdapter;
import com.mpl.GrowthTeacher.Bean.GetStarInfoInfoItem;
import com.mpl.GrowthTeacher.R;
import com.mpl.GrowthTeacher.Tools.NetworkUtils;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import cz.msebera.android.httpclient.Header;

public class GetScoreInfoInfoActivity extends Activity implements View.OnClickListener {

    private String categoryid;
    private String studentId;
    private ListView listview;
    private List<GetStarInfoInfoItem> mDatas;
    private LinearLayout back;
    private TextView title;
    private String categoryname;
    private GetScoreInfoInfoListViewAdapter getScoreInfoInfoListViewAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_get_score_info_info);
        Intent intent = getIntent();
        Bundle bundle = intent.getExtras();
        categoryid = bundle.getString("categoryid");
        categoryname = bundle.getString("categoryname");
        studentId = bundle.getString("studentId");
        back = findViewById(R.id.back);
        back.setOnClickListener(this);
        title = findViewById(R.id.title);
        title.setText(categoryname);
        listview = findViewById(R.id.listview);
        initDate(categoryid, studentId);
    }

    private void initDate(String categoryid, String studentId) {
        if (!NetworkUtils.checkNetWork(GetScoreInfoInfoActivity.this)) {
            Toast.makeText(GetScoreInfoInfoActivity.this, R.string.no_network, Toast.LENGTH_LONG).show();
            return;
        }
        SharedPreferences sharedPreferences = getSharedPreferences("myinfo", MODE_PRIVATE);
        String token = sharedPreferences.getString("token", "");
        String url = getResources().getString(R.string.local_url) + "/v1/achievement/statistical/detail/" + categoryid + "/" + "2/" + studentId;
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
                        JSONArray list = data.getJSONArray("list");
                        mDatas = new ArrayList<GetStarInfoInfoItem>();
                        for (int i = 0; i < list.length(); i++) {
                            JSONObject object = list.getJSONObject(i);
                            String id = object.getString("id");
                            String classroom_id = object.getString("classroom_id");
                            String category_name = object.getString("category_name");
                            String label_name = object.getString("label_name");
                            String name = object.getString("name");
                            String image = object.getString("image");
                            String complete_name = object.getString("complete_name");
                            String type = object.getString("type");
                            String grade = object.getString("grade");
                            String star = object.getString("star");
                            String task_star = object.getString("task_star");
                            String point = object.getString("total_point");
                            String total_point = object.getString("total_point");
                            String updated_at = object.getString("updated_at");
                            GetStarInfoInfoItem getStarInfoInfoItem = new GetStarInfoInfoItem(id, classroom_id, category_name, label_name, name, image, complete_name,
                                    type, grade, star, task_star, point, total_point, updated_at);
                            mDatas.add(getStarInfoInfoItem);
                        }
                        getScoreInfoInfoListViewAdapter = new GetScoreInfoInfoListViewAdapter(GetScoreInfoInfoActivity.this, mDatas);
                        listview.setAdapter(getScoreInfoInfoListViewAdapter);
                    } else {
                        Toast.makeText(GetScoreInfoInfoActivity.this, response.getString("message"), Toast.LENGTH_LONG).show();
                        return;
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                super.onFailure(statusCode, headers, throwable, errorResponse);
                Toast.makeText(GetScoreInfoInfoActivity.this, R.string.no_network, Toast.LENGTH_LONG).show();
                return;
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                super.onFailure(statusCode, headers, responseString, throwable);
                Toast.makeText(GetScoreInfoInfoActivity.this, R.string.no_network, Toast.LENGTH_LONG).show();
                return;
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONArray errorResponse) {
                super.onFailure(statusCode, headers, throwable, errorResponse);
                Toast.makeText(GetScoreInfoInfoActivity.this, R.string.no_network, Toast.LENGTH_LONG).show();
                return;
            }
        });
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.back:
                finish();
                break;
        }
    }
}
