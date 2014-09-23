package com.versapp.confessions;

import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.versapp.R;

/**
 * Created by william on 23/09/14.
 */
public class ComposeConfessionColorFragment extends Fragment {

    String color;

    public static ComposeConfessionColorFragment newInstance(String color) {

        ComposeConfessionColorFragment pageFragment = new ComposeConfessionColorFragment();
        Bundle bundle = new Bundle();
        bundle.putString("color", color);
        pageFragment.setArguments(bundle);
        return pageFragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        ViewGroup rootView = (ViewGroup) inflater.inflate(
                R.layout.fragment_compose_confession_color, container, false);

        color = getArguments().getString("color");

        View view = (View) rootView.findViewById(R.id.compose_confession_color_view);
        view.setBackgroundColor(Color.parseColor(color));

        return rootView;
    }

}
