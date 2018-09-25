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
public class HelperFragment extends Fragment {

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
        return inflater.inflate(R.layout.fragment_helper, container, false);
    }


}
