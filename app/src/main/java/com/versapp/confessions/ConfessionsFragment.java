package com.versapp.confessions;

import android.graphics.Point;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.versapp.Logger;
import com.versapp.R;

import java.util.ArrayList;
import java.util.Arrays;

/**
 * Created by william on 20/09/14.
 */
public class ConfessionsFragment extends Fragment {

    private ArrayList<Confession> confessions;
    private ListView confessionsListView;
    private ConfessionListArrayAdapter adapter;
    private ImageButton favoriteBtn;
    private ImageButton startMessageBtn;
    private ImageView composeConfessionBtn;
    private TextView removeConfessionLabel;


    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View convertView = inflater.inflate(R.layout.fragment_confessions, container, false);

        confessions = new ArrayList<Confession>();

        confessionsListView = (ListView) convertView.findViewById(R.id.fragment_big_confessions_list_view);
        adapter = new ConfessionListArrayAdapter(getActivity(), confessions);
        confessionsListView.setAdapter(adapter);


        // Action btns
        favoriteBtn = (ImageButton) convertView.findViewById(R.id.big_confession_favorite_btn);
        startMessageBtn = (ImageButton) convertView.findViewById(R.id.big_confession_msg_btn);
        composeConfessionBtn = (ImageView) convertView.findViewById(R.id.big_confession_compose_new_confession_btn);
        removeConfessionLabel = (TextView) convertView.findViewById(R.id.big_confession_remove_confession_btn);

        // Due to differences among devices, we need to programatically adjust
        // size of a few elements.
        adjustLayoutElementsSize(convertView);








        new AsyncTask<Void, Void, Confession[]>() {
            @Override
            protected Confession[] doInBackground(Void... params) {

                Log.d(Logger.CONFESSIONS_DEBUG, "About to get confessions");
                Confession[] confessions = ConfessionManager.getInstance().downloadConfessions(getActivity());
                Log.d(Logger.CONFESSIONS_DEBUG, "Size: " + confessions.length);

                return confessions;

            }

            @Override
            protected void onPostExecute(Confession[] result) {

                confessions.addAll(Arrays.asList(result));
                adapter.notifyDataSetChanged();
                super.onPostExecute(result);
            }
        }.execute();






        return convertView;
    }


    private void adjustLayoutElementsSize(View convertView) {

        Display display = getActivity().getWindowManager().getDefaultDisplay();

        int width = 0;
        int height = 0;

        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.HONEYCOMB_MR2) {
            Point size = new Point();
            display.getSize(size);
            width = size.x;
            height = size.y;

        } else {
            width = display.getWidth();
            height = display.getHeight();
        }

        RelativeLayout.LayoutParams paramsList = (RelativeLayout.LayoutParams) confessionsListView.getLayoutParams();
        paramsList.height = height - (80 + width / 2);
        confessionsListView.setLayoutParams(paramsList);

        LinearLayout dashboard = (LinearLayout) convertView.findViewById(R.id.big_confession_dashboard);
        RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams) dashboard.getLayoutParams();
        params.width = width;
        params.height = width / 2;
        dashboard.setLayoutParams(params);

    }
}
