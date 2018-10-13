package com.mpl.GrowthTeacher.Activity;


import android.content.SharedPreferences;
import android.os.Bundle;
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
import com.mpl.GrowthTeacher.Adapter.StudentTaskAdapter;
import com.mpl.GrowthTeacher.Bean.StudentTaskItem;
import com.mpl.GrowthTeacher.R;
import com.mpl.GrowthTeacher.Tools.NetworkUtils;
import com.mpl.GrowthTeacher.View.LoadingDialog;

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
public class TaskCompletBlankFragment extends Fragment {
    private String task_id, cid;
    private ListView listview;
    private LinearLayout ll_empty;
    private LoadingDialog loadingDialog;
    List<StudentTaskItem> mdata = new ArrayList<>();
    StudentTaskAdapter studentTaskAdapter;

    public TaskCompletBlankFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        Bundle bundle = getArguments();
        task_id = bundle.getString("task_id");
        cid = bundle.getString("cid");
        View root = inflater.inflate(R.layout.fragment_task_complet_blank, container, false);
        listview = root.findViewById(R.id.listview);
        if (mdata.size() > 0) {
            mdata.clear();
        }
        initData(task_id, "1", cid);
        return root;

    }

    private void initData(String task_id, String s, String cid) {
        loadingDialog = new LoadingDialog(getActivity(), "加载中...", R.drawable.ic_dialog_loading);
        loadingDialog.show();
        if (NetworkUtils.checkNetWork(getActivity()) == false) {
            loadingDialog.dismiss();
            Toast.makeText(getActivity(), R.string.no_network, Toast.LENGTH_LONG).show();
            return;
        }
        SharedPreferences sharedPreferences = getActivity().getSharedPreferences("myinfo", MODE_PRIVATE);
        String token = sharedPreferences.getString("token", "");
        String url = getResources().getString(R.string.local_url) + "/v1/task/user/" + task_id + "/" + s + "/" + cid;
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
                        JSONArray list = data.getJSONArray("list");
                        if (list.length() == 0) {
                            View view = View.inflate(getActivity(), R.layout.empty_view, null);
                            listview.setEmptyView(view);
                        } else {
                            for (int i = 0; i < list.length(); i++) {
                                JSONObject object = list.getJSONObject(i);
                                String username = object.getString("username");
                                String classroom_id = object.getString("classroom_id");
                                String status = object.getString("status");
                                String updated_at = object.getString("updated_at");
                                String gender = object.getString("gender");
                                String grade = object.getString("grade");
                                StudentTaskItem studentTaskItem = new StudentTaskItem(username, classroom_id, status, updated_at, gender, grade);
                                mdata.add(studentTaskItem);
                            }
                            studentTaskAdapter = new StudentTaskAdapter(getActivity(), mdata);
                            listview.setAdapter(studentTaskAdapter);
                        }

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
}
