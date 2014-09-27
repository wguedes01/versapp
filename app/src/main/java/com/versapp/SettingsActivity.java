package com.versapp;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Point;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Adapter;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.TextView;
import android.widget.Toast;

import com.versapp.connection.ConnectionManager;
import com.versapp.friends.FriendListActivity;
import com.versapp.friends.FriendsManager;

import java.util.ArrayList;


public class SettingsActivity extends Activity {

    private GridView grid;
    private Adapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        ArrayList<SettingsButton> buttons = new ArrayList<SettingsButton>();
        buttons.add(new SettingsButton("Home", new HomeOnClickListener()));
        buttons.add(new SettingsButton("Account", null));
        buttons.add(new SettingsButton("My Thoughts", null));
        buttons.add(new SettingsButton("Friends", new FriendsOnClickListener()));
        buttons.add(new SettingsButton("Support", null));
        buttons.add(new SettingsButton("Logout", new LogoutOnClickListener()));

        grid = (GridView) findViewById(R.id.activity_settings_main_grid);
        adapter = new GridAdapter(this, buttons);
        grid.setAdapter((android.widget.ListAdapter) adapter);


    }

    private class GridAdapter extends BaseAdapter {

        private Context context;
        private ArrayList<SettingsButton> buttons;

        private GridAdapter(Context context, ArrayList<SettingsButton> buttons) {
            this.context = context;
            this.buttons = buttons;
        }

        @Override
        public int getCount() {
            return buttons.size();
        }

        @Override
        public Object getItem(int position) {
            return buttons.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {

            Display display = getWindowManager().getDefaultDisplay();

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

            LayoutInflater inflater = (LayoutInflater) context.getSystemService(LAYOUT_INFLATER_SERVICE);

            convertView = inflater.inflate(R.layout.settings_btn_item, parent, false);

            GridView.LayoutParams params = (GridView.LayoutParams) convertView.getLayoutParams();
            params.height = width/2;
            convertView.setLayoutParams(params);

            TextView btnTitle = (TextView) convertView.findViewById(R.id.settings_btn_item_title);
            btnTitle.setText(buttons.get(position).getTitle());

            convertView.setOnClickListener(buttons.get(position).getOnClickListener());

            return convertView;
        }
    }

    private class SettingsButton {

        private String title;
        private View.OnClickListener onClickListener;

        private SettingsButton(String title, View.OnClickListener onClickListener) {
            this.title = title;
            this.onClickListener = onClickListener;
        }

        public String getTitle() {
            return title;
        }

        public void setTitle(String title) {
            this.title = title;
        }

        public View.OnClickListener getOnClickListener() {
            return onClickListener;
        }

        public void setOnClickListener(View.OnClickListener onClickListener) {
            this.onClickListener = onClickListener;
        }
    }

    private class FriendsOnClickListener implements View.OnClickListener {

        @Override
        public void onClick(View v) {

            AlertDialog.Builder dialog = new AlertDialog.Builder(SettingsActivity.this);

            String[] options = { "Add", "View" };

            dialog.setItems(options, new Dialog.OnClickListener() {

                @Override
                public void onClick(DialogInterface dialog, int which) {

                    switch (which) {
                        case 0:

                            final EditText friendPhoneEdit = (EditText) new EditText(getApplicationContext());

                            AlertDialog.Builder addFriendDialog = new AlertDialog.Builder(SettingsActivity.this);
                            addFriendDialog.setView(friendPhoneEdit);
                            addFriendDialog.setTitle("Add a friend");
                            addFriendDialog.setPositiveButton("Add", new DialogInterface.OnClickListener() {

                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    final String phone = friendPhoneEdit.getText().toString();

                                    new AsyncTask<Void, Void, Void>(){

                                        @Override
                                        protected void onPreExecute() {
                                            Toast.makeText(getApplicationContext(), "Request Sent", Toast.LENGTH_SHORT).show();
                                            super.onPreExecute();
                                        }

                                        @Override
                                        protected Void doInBackground(Void... params) {

                                            FriendsManager.getInstance().sendRequest(getApplicationContext(), phone + "@" + ConnectionManager.SERVER_IP_ADDRESS);

                                            return null;
                                        }
                                    }.execute();
                                }
                            });
                            addFriendDialog.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {

                                @Override
                                public void onClick(DialogInterface dialog, int which) {

                                }
                            });

                            addFriendDialog.show();

                            break;
                        case 1:

                            Intent intent = new Intent(getApplicationContext(), FriendListActivity.class);
                            intent.putExtra(FriendListActivity.LIST_MODE_INTENT_EXTRA, FriendListActivity.OPTS_MODE);
                            startActivity(intent);

                            break;
                    }

                }

            }).setCancelable(true).show();

        }
    }

    private class HomeOnClickListener implements View.OnClickListener {

        @Override
        public void onClick(View v) {
            finish();
        }
    }

    private class LogoutOnClickListener implements View.OnClickListener {

        @Override
        public void onClick(View v) {

            ConnectionManager.getInstance(getApplicationContext()).logout();
            Intent intent = new  Intent(getApplicationContext(), LoginActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK|Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);

        }
    }

}
