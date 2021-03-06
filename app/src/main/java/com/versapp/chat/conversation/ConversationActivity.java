package com.versapp.chat.conversation;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.BroadcastReceiver;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Bitmap;
import android.graphics.Point;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v4.util.LruCache;
import android.view.Display;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.analytics.HitBuilders;
import com.google.android.gms.analytics.Tracker;
import com.versapp.GCSManager;
import com.versapp.MainApplication;
import com.versapp.R;
import com.versapp.TutorialManager;
import com.versapp.chat.Chat;
import com.versapp.chat.ChatManager;
import com.versapp.chat.ChatMessageListener;
import com.versapp.chat.ConfessionChat;
import com.versapp.chat.GroupChat;
import com.versapp.chat.OneToOneChat;
import com.versapp.chat.Participant;
import com.versapp.confessions.Confession;
import com.versapp.connection.ConnectionListener;
import com.versapp.connection.ConnectionManager;
import com.versapp.connection.ConnectionService;
import com.versapp.database.ChatsDAO;
import com.versapp.database.MessagesDAO;
import com.versapp.util.ImageManager;

import java.io.IOException;
import java.util.ArrayList;

public class ConversationActivity extends Activity {

    public static final String CHAT_UUID_INTENT_EXTRA = "CHAT_UUID_INTENT_EXTRA";
    public static final String FROM_NOTIFICATION_INTENT_EXTRA = "FROM_NOTIFICATION_INTENT_EXTRA";
    private String chatUUID;

    private Chat currentChat;
    private ChatsDAO chatsDAO;

    BroadcastReceiver newMessageBR;

    private ArrayList<ChatMessage> messages;

    private ListView messagesListView;
    private ArrayAdapter adapter;
    private EditText messageEditText;
    private ImageButton sendMessageBtn;
    private ImageButton attachImageBtn;

    private TextView chatName;
    private ImageButton settingsBtn;

    private Bitmap imageAttachment;
    private LruCache<String, Bitmap> cache;

    View chatExplanationLabel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_conversation);

        Tracker tracker = ((MainApplication) getApplicationContext()).getTracker();
        tracker.setScreenName(getClass().getName());
        tracker.send(new HitBuilders.AppViewBuilder().build());


        chatUUID = getIntent().getStringExtra(CHAT_UUID_INTENT_EXTRA);

        chatsDAO = new ChatsDAO(getApplicationContext());

        //currentChat = ChatManager.getInstance().getChat(chatUUID);
        currentChat = chatsDAO.get(chatUUID);

        cache = new LruCache<String, Bitmap>(3);

        messages = new ArrayList<ChatMessage>();

        messagesListView = (ListView) findViewById(R.id.activity_conversation_main_list);
        messageEditText = (EditText) findViewById(R.id.activity_conversation_message_edit_text);
        sendMessageBtn = (ImageButton) findViewById(R.id.activity_conversation_send_msg_btn);

        adapter = new ConversationListArrayAdapter(getApplicationContext(), messages, cache);
        messagesListView.setAdapter(adapter);

        chatName = (TextView) findViewById(R.id.activity_conversation_chat_name);
        chatName.setText(currentChat.getName());

        settingsBtn = (ImageButton) findViewById(R.id.activity_conversation_opts_btn);

        newMessageBR = new MessageReceivedBR(chatUUID, adapter);

        attachImageBtn = (ImageButton) findViewById(R.id.attach_image_btn);

        sendMessageBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (ConnectionListener.reconnecting){

                    Toast.makeText(getApplicationContext(), "Loading conversation. Try again in 5 seconds.", Toast.LENGTH_LONG).show();

                    return;
                }

                if (!ConnectionService.getConnection().isAuthenticated()){

                    Toast.makeText(getApplicationContext(), "Failed to send message. Please check network connectivity.", Toast.LENGTH_LONG).show();

                    return;
                }

                if (messageEditText.getText().length() <= 0 && attachImageBtn == null){
                    Toast.makeText(getApplicationContext(), "Message may not be blank", Toast.LENGTH_SHORT).show();
                } else {

                    final String messageBody = messageEditText.getText().toString();
                    messageEditText.setText("");
                    attachImageBtn.setImageResource((android.R.drawable.ic_menu_camera));

                    new AsyncTask<Void, Void, Message>(){



                        @Override
                        protected Message doInBackground(Void... params) {


                            String imageUrl = null;
                            if (imageAttachment != null) {
                                try {
                                    imageUrl = GCSManager.getInstance(getApplicationContext()).upload(imageAttachment);
                                    imageAttachment = null;
                                } catch (IOException e) {
                                    e.printStackTrace();
                                }
                            }

                            final Message message = new Message(chatUUID, messageBody, imageUrl, Message.getCurrentEpochTime(), true);
                            ConversationManager.getInstance(getApplicationContext()).sendMessage(message, chatUUID + "@" + ConnectionManager.SERVER_IP_ADDRESS);

                            return message;
                        }

                        @Override
                        protected void onPostExecute(Message message) {

                            messages.add(new ChatMessage(message, true));
                            adapter.notifyDataSetChanged();

                            super.onPostExecute(message);
                        }
                    }.execute();

                }

            }
        });

        new AsyncTask<Void, Void, ArrayList<Message>>(){

            @Override
            protected ArrayList<Message> doInBackground(Void... params) {
                return new MessagesDAO(getApplicationContext()).getAll(chatUUID);
            }

            @Override
            protected void onPostExecute(ArrayList<Message> msgs) {

                if (msgs != null){

                    for (Message msg : msgs){
                        messages.add(new ChatMessage(msg));
                    }

                    adapter.notifyDataSetChanged();

                    if (msgs.size() > 0){
                        messagesListView.setSelection(msgs.size()-1);
                    }

                }

                super.onPostExecute(msgs);
            }
        }.execute();


        // Check if user has gone through tutorial for this type of chat.
        if (!TutorialManager.getInstance(getApplicationContext()).isChatExplained() && currentChat.getType().equals(OneToOneChat.TYPE)){

            chatExplanationLabel = findViewById(R.id.activity_conversation_chat_explanation);
            chatExplanationLabel.setVisibility(View.VISIBLE);

            chatExplanationLabel.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    chatExplanationLabel.setVisibility(View.GONE);
                }
            });

            TutorialManager.getInstance(getApplicationContext()).setChatExplained();
        } else {

        }

    }

    @Override
    protected void onPause() {
        ChatManager.getInstance().setChatClosed(getApplicationContext(), currentChat);
        LocalBroadcastManager.getInstance(getApplicationContext()).unregisterReceiver(newMessageBR);
        super.onPause();
    }

    @Override
    protected void onResume() {
        ChatManager.getInstance().setChatOpen(getApplicationContext(), currentChat);
        LocalBroadcastManager.getInstance(getApplicationContext()).registerReceiver(newMessageBR, new IntentFilter(ChatMessageListener.NEW_MESSAGE_ACTION));
        super.onResume();
    }

    public void attachImage(View view){

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Background Options");
        builder.setItems(new CharSequence[] { "Select Image from Camera", "Take a Picture" }, new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {
                if (which == 0) {
                    browsePictures();
                } else if (which == 1) {
                    takePicture();
                }
            }
        });
        builder.show();

    }

    private static final int LOAD_IMAGE_REQUEST_CODE = 1;
    private static final int CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE = 100;

    private void browsePictures(View v) {
        Intent intent = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        intent.putExtra(Intent.EXTRA_LOCAL_ONLY, true);
        startActivityForResult(intent, LOAD_IMAGE_REQUEST_CODE);
    }

    public void takePicture(View v) {
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        startActivityForResult(intent, CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE);
    }

    public void browsePictures() {
        Intent intent = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(intent, LOAD_IMAGE_REQUEST_CODE);
    }

    public void takePicture() {
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        startActivityForResult(intent, CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == Activity.RESULT_OK && requestCode == LOAD_IMAGE_REQUEST_CODE && data != null) {
            handlePictureSelectedIntent(data);
        } else if (requestCode == CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE && resultCode == Activity.RESULT_OK && data != null) {
            handlePictureTakenIntent(data);
        }
    }

    public void handlePictureTakenIntent(final Intent intent) {

        new AsyncTask<Void, Void, Bitmap>(){

            @Override
            protected Bitmap doInBackground(Void... params) {

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

                ImageManager imageManager = new ImageManager();
                String selectedImagePath = imageManager.getSelectedImagePath(intent, getApplicationContext());
                imageManager.setTargetHeight(width);
                imageManager.setTargetWidth(width);

                try {
                    // Hack this. Sometimes the Samsung system doesn't create the path to the image taken in time.
                    if (selectedImagePath != null) {
                        return cropBitmap(imageManager.getScaledBitmapImage(selectedImagePath));
                    } else {
                        return null;
                    }

                } catch (IOException e) {
                    e.printStackTrace();
                    return null;
                }
            }

            @Override
            protected void onPostExecute(Bitmap bitmap) {

                if (bitmap != null) {
                    setMessagePicture(bitmap);
                } else {
                    Toast.makeText(getApplicationContext(), "Unable to use selected image. Please choose an image stored on your phone.", Toast.LENGTH_SHORT).show();
                }


                super.onPostExecute(bitmap);
            }
        }.execute();

    }

    public void handlePictureSelectedIntent(final Intent intent) {


        new AsyncTask<Void, Void, Bitmap>(){

            @Override
            protected Bitmap doInBackground(Void... params) {

                ImageManager imageManager = new ImageManager();
                String selectedImagePath = imageManager.getSelectedImagePath(intent, getApplicationContext());
                imageManager.setTargetHeight(500);
                imageManager.setTargetWidth(500);

                try {
                    return cropBitmap(imageManager.getScaledBitmapImage(selectedImagePath));
                } catch (IOException e) {
                    e.printStackTrace();
                    return null;
                }

            }

            @Override
            protected void onPostExecute(Bitmap bitmap) {

                setMessagePicture(bitmap);

                super.onPostExecute(bitmap);
            }

        }.execute();

    }

    protected Bitmap cropBitmap(Bitmap image) {
        if (image.getWidth() >= image.getHeight()) {
            return Bitmap.createBitmap(image, (int) 0, 0, (int) image.getWidth(), image.getHeight());
        } else {
            double height = image.getWidth();
            double inset = 0.5 * (image.getHeight() - height);
            return Bitmap.createBitmap(image, 0, (int) inset, image.getWidth(), (int) height);
        }
    }

    protected void setMessagePicture(final Bitmap scaledImage) {
        attachImageBtn.setImageBitmap(scaledImage);
        imageAttachment = scaledImage;
    }

    public void openSettings(View view){

        final Chat chat = chatsDAO.get(chatUUID);

        if (chat.getType().equals(GroupChat.TYPE)){

            AlertDialog.Builder dialog = new AlertDialog.Builder(ConversationActivity.this);

            String[] options = { "Participants", "Leave", "Am I anonymous?"/*, "Block"*/ };

            dialog.setItems(options, new Dialog.OnClickListener() {

                @Override
                public void onClick(DialogInterface dialog, int which) {

                    switch (which) {
                        case 0:

                            AlertDialog.Builder participantsDialog = new AlertDialog.Builder(ConversationActivity.this);
                            String[] options = null;

                            if (((GroupChat) chat).getParticipants(getApplicationContext()).size() == 0){
                                options = new String[1];
                                options[0] = "No participants";

                            } else {

                                ArrayList<Participant> participants = ((GroupChat) chat).getParticipants(getApplicationContext());

                                options =  new String[participants.size()];

                                int i = 0;
                                for (Participant p : participants){
                                    options[i] = p.getUsername();
                                    i++;
                                }
                            }

                            participantsDialog.setItems(options, new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {

                                }
                            }).setCancelable(true).setTitle("Participants").show();


                            break;
                        case 1:
                            Toast.makeText(getApplicationContext(), "Leaving group..", Toast.LENGTH_SHORT).show();

                            new AsyncTask<Void, Void, Void>(){

                                @Override
                                protected Void doInBackground(Void... params) {
                                    ChatManager.getInstance().leaveChat(getApplicationContext(), chat);
                                    finish();
                                    return null;
                                }
                            }.execute();

                            break;
                        case 2:

                            AlertDialog.Builder builder = new AlertDialog.Builder(ConversationActivity.this);
                            builder.setTitle("Am I anonymous?");
                            builder.setMessage("People in the group only know you're one of the members. They don't know who is the sender of each message.");
                            builder.setCancelable(true);

                            AlertDialog amIAnonymousDialog = builder.create();
                            amIAnonymousDialog.show();

                            break;
                        case 3: //block
                            Toast.makeText(getApplicationContext(), "Blocking..", Toast.LENGTH_SHORT).show();
                            break;
                    }

                }

            }).setCancelable(true).show();

        } else {

            AlertDialog.Builder dialog = new AlertDialog.Builder(ConversationActivity.this);

            String[] options = { "Leave", "Am I anonymous?"/*, "Block"*/ };

            dialog.setItems(options, new Dialog.OnClickListener() {

                @Override
                public void onClick(DialogInterface dialog, int which) {

                    switch (which) {
                        case 0:

                            new AsyncTask<Void, Void, Void>(){

                                @Override
                                protected Void doInBackground(Void... params) {
                                    ChatManager.getInstance().leaveChat(getApplicationContext(), chat);
                                    finish();
                                    return null;
                                }
                            }.execute();

                            break;
                        case 1:

                            String message = "";
                            if (currentChat.getType().equals(OneToOneChat.TYPE)){
                                if (((OneToOneChat)currentChat).isOwner()){
                                    message = "You know who you're talking to but your friends doesn't know who you are.";
                                } else {
                                    message = "You're talking to one of your friends. Your friend knows who you are.";
                                }
                            } else if(currentChat.getType().equals(ConfessionChat.TYPE)) {

                                if (((ConfessionChat) currentChat).getDegree() == Confession.FRIEND_DEGREE){
                                    message = "You're talking to a friend. You both don't know who each other is.";
                                } else if (((ConfessionChat) currentChat).getDegree() == Confession.FRIEND_OF_FRIEND_DEGREE){
                                    message = "You're talking to a friend of a friend. You both don't know who each other is.";
                                }

                            }

                            AlertDialog.Builder builder = new AlertDialog.Builder(ConversationActivity.this);
                            builder.setTitle("Am I anonymous?");
                            builder.setMessage(message);
                            builder.setCancelable(true);

                            AlertDialog amIAnonymousDialog = builder.create();
                            amIAnonymousDialog.show();


                            break;
                        case 2: //block
                            Toast.makeText(getApplicationContext(), "Blocking..", Toast.LENGTH_SHORT).show();
                            break;
                    }

                }

            }).setCancelable(true).show();

        }

    }

    public void back(View view){
        super.onBackPressed();
    }

    public void closeExplanationLabel(View view){
        chatExplanationLabel.setVisibility(View.GONE);
        TutorialManager.getInstance(getApplicationContext()).setChatExplained();
    }

}
