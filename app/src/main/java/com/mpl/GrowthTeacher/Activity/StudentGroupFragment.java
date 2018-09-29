package com.mpl.GrowthTeacher.Activity;


import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;

import com.mpl.GrowthTeacher.R;

/**
 * A simple {@link Fragment} subclass.
 */
public class StudentGroupFragment extends Fragment implements View.OnClickListener {

    private ImageView ib_message;

    public static StudentGroupFragment newInstance(String name) {
        StudentGroupFragment fragment = new StudentGroupFragment();
        Bundle args = new Bundle();
        args.putString("name", name);
        fragment.setArguments(args);
        return fragment;
    }

    public StudentGroupFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View root = inflater.inflate(R.layout.fragment_student_group, container, false);
        ib_message = root.findViewById(R.id.ib_message);
        ib_message.setOnClickListener(this);
        return root;
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.ib_message:
                Intent intent = new Intent(getActivity(), MessageActivity.class);
                startActivity(intent);
                break;
        }
    }
}
