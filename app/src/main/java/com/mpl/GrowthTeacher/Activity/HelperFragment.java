package com.mpl.GrowthTeacher.Activity;


import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.mpl.GrowthTeacher.R;

/**
 * A simple {@link Fragment} subclass.
 */
public class HelperFragment extends Fragment implements View.OnClickListener {
    private ImageView ib_message;
    private TextView tv_zhonghe, tv_studentchengjiu, tv_tasktongji;

    public static HelperFragment newInstance(String name) {
        HelperFragment fragment = new HelperFragment();
        Bundle args = new Bundle();
        args.putString("name", name);
        fragment.setArguments(args);
        return fragment;
    }

    public HelperFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View root = inflater.inflate(R.layout.fragment_helper, container, false);
        ib_message = root.findViewById(R.id.ib_message);
        ib_message.setOnClickListener(this);
        tv_zhonghe = root.findViewById(R.id.tv_zhonghe);
        tv_zhonghe.setOnClickListener(this);
        tv_studentchengjiu = root.findViewById(R.id.tv_studentchengjiu);
        tv_studentchengjiu.setOnClickListener(this);
        tv_tasktongji = root.findViewById(R.id.tv_tasktongji);
        tv_tasktongji.setOnClickListener(this);
        return root;
    }


    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.ib_message:
                Intent intent = new Intent(getActivity(), MessageActivity.class);
                startActivity(intent);
                break;
            case R.id.tv_zhonghe:
                Intent intent1 = new Intent(getActivity(), ComprehensiveEvaluationActivity.class);
                startActivity(intent1);
                break;
            case R.id.tv_studentchengjiu:
                Intent intent2 = new Intent(getActivity(), StudentAchievementActivity.class);
                startActivity(intent2);
                break;
            case R.id.tv_tasktongji:
                Intent intent3 = new
                        Intent(getActivity(), TaskStatisticActivity.class);
                startActivity(intent3);
                break;
        }
    }
}
