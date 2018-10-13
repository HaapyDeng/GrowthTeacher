package com.mpl.GrowthTeacher.Activity;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.mpl.GrowthTeacher.R;

/**
 * A simple {@link Fragment} subclass.
 */
public class TaskNotCompletFragment extends Fragment {


    public TaskNotCompletFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_task_not_complet, container, false);
    }

}
