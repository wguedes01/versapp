package com.versapp.chat.conversation;

import android.app.Activity;
import android.app.AlertDialog;
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

import com.versapp.GCSManager;
import com.versapp.R;
import com.versapp.chat.ChatManager;
import com.versapp.chat.ChatMessageListener;
import com.versapp.connection.ConnectionManager;
import com.versapp.database.MessagesDAO;
import com.versapp.util.ImageManager;

import java.io.IOException;
import java.util.ArrayList;

public class ConversationActivity extends Activity {

    public static final String CHAT_UUID_INTENT_EXTRA = "CHAT_UUID_INTENT_EXTRA";
    private String chatUUID;

    BroadcastReceiver newMessageBR;

    private ArrayList<Message> messages;

    private ListView messagesListView;
    private ArrayAdapter adapter;
    private EditText messageEditText;
    private ImageButton sendMessageBtn;
    private ImageButton attachImageBtn;

    private TextView chatName;
    private ImageButton backBtn;

    private Bitmap imageAttachment;
    private LruCache<String, Bitmap> cache;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_conversation);

        cache = new LruCache<String, Bitmap>(3);

        messages = new ArrayList<Message>();

        chatUUID = getIntent().getStringExtra(CHAT_UUID_INTENT_EXTRA);

        messagesListView = (ListView) findViewById(R.id.activity_conversation_main_list);
        messageEditText = (EditText) findViewById(R.id.activity_conversation_message_edit_text);
        sendMessageBtn = (ImageButton) findViewById(R.id.activity_conversation_send_msg_btn);

        adapter = new ConversationListArrayAdapter(getApplicationContext(), messages, cache);
        messagesListView.setAdapter(adapter);

        chatName = (TextView) findViewById(R.id.activity_conversation_chat_name);
        chatName.setText(ChatManager.getInstance().getChat(chatUUID).getName());

        backBtn = (ImageButton) findViewById(R.id.activity_conversation_back_btn);
        backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        newMessageBR = new MessageReceivedBR(chatUUID, adapter);

        attachImageBtn = (ImageButton) findViewById(R.id.attach_image_btn);

        sendMessageBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (messageEditText.getText().length() <= 0 ){
                    Toast.makeText(getApplicationContext(), "Message may not be blank", Toast.LENGTH_SHORT).show();
                } else {

                    final String messageBody = messageEditText.getText().toString();
                    messageEditText.setText("");

                    new AsyncTask<Void, Void, Message>(){



                        @Override
                        protected Message doInBackground(Void... params) {


                            String imageUrl = null;
                            if (imageAttachment != null) {
                                try {
                                    imageUrl = GCSManager.getInstance(getApplicationContext()).upload(imageAttachment);
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

                            messages.add(message);
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
                messages.addAll(msgs);
                adapter.notifyDataSetChanged();
                super.onPostExecute(msgs);
            }
        }.execute();

    }

    @Override
    protected void onPause() {
        LocalBroadcastManager.getInstance(getApplicationContext()).unregisterReceiver(newMessageBR);
        super.onPause();
    }

    @Override
    protected void onResume() {
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

                return cropBitmap(imageManager.getScaledBitmapImage(selectedImagePath));

            }

            @Override
            protected void onPostExecute(Bitmap bitmap) {

                setMessagePicture(bitmap);

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

                return cropBitmap(imageManager.getScaledBitmapImage(selectedImagePath));

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


}
