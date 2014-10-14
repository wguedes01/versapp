package com.versapp.requests;

import android.app.Activity;
import android.content.Context;
import android.graphics.Point;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.versapp.R;
import com.versapp.chat.Chat;
import com.versapp.chat.ChatManager;
import com.versapp.chat.GroupChat;
import com.versapp.friends.Friend;
import com.versapp.friends.FriendsManager;

import java.util.ArrayList;

public class RequestsActivity extends Activity {

    private GridView grid;
    private BaseAdapter adapter;
    private ArrayList<Request> requests;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_requests);

        requests = new ArrayList<Request>();

        grid = (GridView) findViewById(R.id.activity_requests__grid);
        adapter = new GridViewAdapter(getApplicationContext(), requests, getWindowManager().getDefaultDisplay());
        grid.setAdapter(adapter);

        new AsyncTask<Void, Void, ArrayList<Request>>(){

            @Override
            protected ArrayList<Request> doInBackground(Void... params) {

                ArrayList<Request> reqs = new ArrayList<Request>();

                ArrayList<Friend> pendingFriends = FriendsManager.getInstance().getPendingFriends();

                for (Friend f : pendingFriends){
                    reqs.add(new FriendRequest(getApplicationContext(), f));
                }

                ArrayList<Chat> pendingChats = ChatManager.getInstance().getPendingChats();

                for(Chat c : pendingChats){
                    reqs.add(new GroupInvitationRequest(getApplicationContext(), (GroupChat)c));
                }

                return reqs;
            }

            @Override
            protected void onPostExecute(ArrayList<Request> result) {

                if (result.size() == 0){
                    Toast.makeText(getApplicationContext(), "You have no notifications", Toast.LENGTH_LONG).show();
                }

                requests.addAll(result);
                adapter.notifyDataSetChanged();
                super.onPostExecute(result);
            }

        }.execute();


    }

    private class GridViewAdapter extends BaseAdapter {

        private Context context;
        private ArrayList<Request> requests;
        private LayoutInflater inflater;

        Display display;
        int width = 0;
        int height = 0;

        private GridViewAdapter(Context context, ArrayList<Request> requests, Display display) {
            this.context = context;
            this.requests = requests;
            inflater = (LayoutInflater) context.getSystemService(LAYOUT_INFLATER_SERVICE);


            this.display = display;
            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.HONEYCOMB_MR2) {
                Point size = new Point();
                display.getSize(size);
                width = size.x;
                height = size.y;

            } else {
                width = display.getWidth();
                height = display.getHeight();
            }


        }

        @Override
        public int getCount() {

            if (requests == null){
                return 0;
            }

            return requests.size();
        }

        @Override
        public Object getItem(int position) {

            if (requests == null){
                return null;
            }

            return requests.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {

            final Request currentRequest = requests.get(position);

            convertView = inflater.inflate(R.layout.request_list_item, parent, false);

            final TextView titleLabel = (TextView) convertView.findViewById(R.id.request_title_text_view);
            final TextView messageLabel = (TextView) convertView.findViewById(R.id.request_message_text_view);
            final ImageButton acceptBtn = (ImageButton) convertView.findViewById(R.id.request_accept_btn);
            final ImageButton denyBtn = (ImageButton) convertView.findViewById(R.id.request_deny_btn);
            final ImageView icon = (ImageView) convertView.findViewById(R.id.request_list_item_icon);


            if (currentRequest.getTitle().length() <= 10){
                titleLabel.setTextSize(20);
            } else if(currentRequest.getTitle().length() > 10 && currentRequest.getTitle().length() <= 30) {
                titleLabel.setTextSize(16);
            } else {
                titleLabel.setTextSize(13);
            }
            titleLabel.setText(currentRequest.getTitle());

            messageLabel.setText(currentRequest.getMessage());

            if (currentRequest instanceof FriendRequest){
                icon.setImageResource(R.drawable.ic_friend_request);
            } else if(currentRequest instanceof GroupInvitationRequest){
                icon.setImageResource(R.drawable.ic_group_request);
            }

            acceptBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    new AsyncTask<Void, Void, Void>(){

                        @Override
                        protected Void doInBackground(Void... params) {

                            currentRequest.accept();

                            return null;
                        }
                    }.execute();




                    requests.remove(currentRequest);
                    adapter.notifyDataSetChanged();
                }
            });

            denyBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    new AsyncTask<Void, Void, Void>(){

                        @Override
                        protected Void doInBackground(Void... params) {

                            currentRequest.deny();

                            return null;
                        }
                    }.execute();

                    requests.remove(currentRequest);
                    adapter.notifyDataSetChanged();
                }
            });

            adjustTitleSize(convertView);

            return convertView;
        }



        private void adjustTitleSize(View tile){

            GridView.LayoutParams params = (GridView.LayoutParams) tile.getLayoutParams();
            params.height = width/2;
            tile.setLayoutParams(params);

        }

    }


}
