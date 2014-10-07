package com.versapp.requests;

import android.app.Activity;
import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.TextView;

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
        adapter = new GridViewAdapter(getApplicationContext(), requests);
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
                    reqs.add(new GroupInvitationRequest((GroupChat)c));
                }

                return reqs;
            }

            @Override
            protected void onPostExecute(ArrayList<Request> result) {
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

        private GridViewAdapter(Context context, ArrayList<Request> requests) {
            this.context = context;
            this.requests = requests;
            inflater = (LayoutInflater) context.getSystemService(LAYOUT_INFLATER_SERVICE);
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

            convertView = inflater.inflate(R.layout.request_list_item, null, false);

            final TextView titleLabel = (TextView) convertView.findViewById(R.id.request_title_text_view);
            final TextView messageLabel = (TextView) convertView.findViewById(R.id.request_message_text_view);
            final TextView acceptBtn = (TextView) convertView.findViewById(R.id.request_accept_btn);
            final TextView denyBtn = (TextView) convertView.findViewById(R.id.request_deny_btn);

            titleLabel.setText(currentRequest.getTitle());
            messageLabel.setText(currentRequest.getMessage());

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

            return convertView;
        }
    }

}
