package com.mpl.GrowthTeacher.Activity;


import android.annotation.SuppressLint;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.Toast;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.mpl.GrowthTeacher.Adapter.ChengJiuJinXingZhongListViewAdapter;
import com.mpl.GrowthTeacher.Bean.ChengJiuJinXingZhongItem;
import com.mpl.GrowthTeacher.R;
import com.mpl.GrowthTeacher.Tools.NetworkUtils;

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
public class ChengJiuYiWanChengFragment extends Fragment {
    private LinearLayout ll_empty;
    private ListView listView;
    private List<ChengJiuJinXingZhongItem> mDatas;
    private ChengJiuJinXingZhongListViewAdapter chengJiuJinXingZhongListViewAdapter;
    private CharSequence choose_start_time = "0", choose_end_time = "0";
    private String categoryid = "0";
    private String lableid = "0";
    private String studentId;

    public ChengJiuYiWanChengFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View root = inflater.inflate(R.layout.fragment_cheng_jiu_yi_wan_cheng, container, false);
        Bundle bundle = getArguments();
        studentId = bundle.getString("studentId");
        listView = root.findViewById(R.id.listview);
        ll_empty = root.findViewById(R.id.ll_empty);
        SharedPreferences sp = getActivity().getSharedPreferences("parameters", MODE_PRIVATE);
        choose_start_time = sp.getString("start", "");
        choose_end_time = sp.getString("end", "");
        categoryid = sp.getString("cid", "");
        lableid = sp.getString("lid", "");

        getCpmpletAchieve(choose_start_time, choose_end_time, categoryid, lableid);
        return root;
    }

    private void getCpmpletAchieve(CharSequence choose_start_time, CharSequence choose_end_time, String categoryid, String lableid) {
        if (!NetworkUtils.checkNetWork(getActivity())) {
            Toast.makeText(getActivity(), R.string.no_network, Toast.LENGTH_LONG).show();
            return;
        }
        SharedPreferences sharedPreferences = this.getActivity().getSharedPreferences("myinfo", MODE_PRIVATE);
        String token = sharedPreferences.getString("token", "");
        String url = getResources().getString(R.string.local_url) + "/v1/achievement/default/statistical/" + "1/" + studentId + "/" + choose_start_time + "/" + choose_end_time + "/" + categoryid + "/" + lableid;
        Log.d("url==>>", url);
        AsyncHttpClient client = new AsyncHttpClient();
        client.addHeader("X-Api-Token", token);
        client.get(url, new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                super.onSuccess(statusCode, headers, response);
                Log.d("response==>>>", response.toString());
                try {
                    int code = response.getInt("code");
                    if (code == 0) {
                        JSONObject data = response.getJSONObject("data");
                        JSONArray list = data.getJSONArray("list");
                        if (list.length() == 0) {
                            Message message = new Message();
                            message.what = 1;
                            handler.sendMessage(message);
                        } else {
                            mDatas = new ArrayList<ChengJiuJinXingZhongItem>();
                            for (int i = 0; i < list.length(); i++) {
                                JSONObject object = list.getJSONObject(i);
                                String id = object.getString("id");
                                String name = object.getString("name");
                                String type = object.getString("type");
                                String image = object.getString("image");
                                String category_name = object.getString("category_name");
                                String label_name = object.getString("label_name");
                                String task_star = object.getString("task_star");
                                String status = object.getString("status");
                                String classroom_id = object.getString("classroom_id");
                                String star = object.getString("star");
                                ChengJiuJinXingZhongItem chengJiuJinXingZhongItem = new ChengJiuJinXingZhongItem(id, name, type, image, category_name, label_name, task_star, status, classroom_id, star);
                                mDatas.add(chengJiuJinXingZhongItem);
                            }
                            chengJiuJinXingZhongListViewAdapter = new ChengJiuJinXingZhongListViewAdapter(getActivity(), mDatas);
                            listView.setAdapter(chengJiuJinXingZhongListViewAdapter);
                        }
                    } else {
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
                Toast.makeText(getActivity(), R.string.no_network, Toast.LENGTH_LONG).show();
                return;
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                super.onFailure(statusCode, headers, responseString, throwable);
                Toast.makeText(getActivity(), R.string.no_network, Toast.LENGTH_LONG).show();
                return;
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONArray errorResponse) {
                super.onFailure(statusCode, headers, throwable, errorResponse);
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
                    ll_empty.setVisibility(View.VISIBLE);
                    break;
            }
        }
    };
}
