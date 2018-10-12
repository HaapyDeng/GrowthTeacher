package com.mpl.GrowthTeacher.Activity;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.mpl.GrowthTeacher.Adapter.GetStarInfoListViewAdapter;
import com.mpl.GrowthTeacher.Bean.GetStarInfoItem;
import com.mpl.GrowthTeacher.R;
import com.mpl.GrowthTeacher.Tools.NetworkUtils;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import cz.msebera.android.httpclient.Header;

import static android.content.Context.MODE_PRIVATE;

public class GetStarInfoFragment extends Fragment implements AdapterView.OnItemClickListener {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    private String mParam1;
    private String mParam2;
    private ListView listView;
    private List<GetStarInfoItem> mDatas;
    private GetStarInfoListViewAdapter getStarInfoListViewAdapter;
    private TextView star_score;
    private LinearLayout ll_empty;
    private String studentId;


    public GetStarInfoFragment() {
        // Required empty public constructor
    }

    public static GetStarInfoFragment newInstance(String param1, String param2) {
        GetStarInfoFragment fragment = new GetStarInfoFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_get_star_info, container, false);
        Bundle bundle = getArguments();
        studentId = bundle.getString("studentId");
        listView = root.findViewById(R.id.lv);
        listView.setOnItemClickListener(this);
        ll_empty = root.findViewById(R.id.ll_empty);
        getStarInfoData();
        star_score = root.findViewById(R.id.star_score);
        SharedPreferences sharedPreferences = this.getActivity().getSharedPreferences("myinfo", MODE_PRIVATE);
        String one_star_point = sharedPreferences.getString("one_star_point", "");
        star_score.setText("= " + one_star_point + " 分");
        return root;
    }

    @Override
    public void onResume() {
        super.onResume();
        getStarInfoData();
    }

    private void getStarInfoData() {
        if (!NetworkUtils.checkNetWork(getActivity())) {
            Toast.makeText(getActivity(), R.string.no_network, Toast.LENGTH_LONG).show();
            return;
        }
        SharedPreferences sharedPreferences = this.getActivity().getSharedPreferences("myinfo", MODE_PRIVATE);
        String token = sharedPreferences.getString("token", "");
        String url = getResources().getString(R.string.local_url) + "/v1/achievement/statistical/category/" + "1/" + studentId;
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
                        if (list.length() == 0) {
                            Message message = new Message();
                            message.what = 1;
                            handler.sendMessage(message);
                        }
                        mDatas = new ArrayList<GetStarInfoItem>();
                        for (int i = 0; i < list.length(); i++) {
                            JSONObject object = list.getJSONObject(i);
                            String classroom_id = object.getString("classroom_id");
                            String category_name = object.getString("category_name");
                            String category_id = object.getString("category_id");
                            String grade = object.getString("grade");
                            String star = object.getString("star");
                            String task_star = object.getString("task_star");
                            String point = object.getString("point");
                            String total_point = object.getString("total_point");
                            GetStarInfoItem getStarInfoItem = new GetStarInfoItem(classroom_id, category_name, category_id, grade, star, task_star, point, total_point);
                            mDatas.add(getStarInfoItem);
                        }
                        getStarInfoListViewAdapter = new GetStarInfoListViewAdapter(getActivity(), mDatas);
                        listView.setAdapter(getStarInfoListViewAdapter);
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
                    listView.setVisibility(View.GONE);
                    ll_empty.setVisibility(View.VISIBLE);
                    break;
            }
        }
    };

    @Override
    public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
        String categoryid = mDatas.get(i).getCategory_id();
        String categoryname = mDatas.get(i).getCategory_name();
        Intent intent = new Intent(getActivity(), GetStarInfoInfoActivity.class);
        Bundle bundle = new Bundle();
        bundle.putString("categoryid", categoryid);
        bundle.putString("categoryname", categoryname);
        bundle.putString("studentId", studentId);
        intent.putExtras(bundle);
        startActivity(intent);
    }

}
