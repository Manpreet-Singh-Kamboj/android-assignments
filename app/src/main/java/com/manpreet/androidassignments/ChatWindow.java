package com.manpreet.androidassignments;

import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import java.util.ArrayList;

public class ChatWindow extends AppCompatActivity {
    ListView listView;
    EditText messageInput;
    Button sendButton;
    ArrayList<String> chatMessages;
    ChatAdapter chatAdapter;
    SQLiteDatabase db;
    private static final String ACTIVITY_NAME = "ChatWindowActivity";
    ContentValues values;
    FrameLayout detailContainer;
    Cursor cursor;

    class ChatAdapter extends ArrayAdapter<String> {
        public ChatAdapter(Context context) {
            super(context, 0);
        }

        @Override
        public int getCount(){
            return chatMessages.size();
        }

        @Override
        public String getItem(int pos){
            return chatMessages.get(pos);
        }

        @Override
        public long getItemId(int position){
            cursor.moveToPosition(position);
            return cursor.getLong(cursor.getColumnIndexOrThrow(ChatDatabaseHelper.KEY_ID));
        }

        @Override
        public View getView(int pos, View contentView, ViewGroup parent){
            LayoutInflater inflater = ChatWindow.this.getLayoutInflater();
            View result = null;
            if(pos % 2 == 0){
                result = inflater.inflate(R.layout.chat_row_incoming,null);
                TextView messageText = result.findViewById(R.id.text_message);
                messageText.setText(getItem(pos));
            }else{
                result = inflater.inflate(R.layout.chat_row_outgoing,null);
                TextView messageText = result.findViewById(R.id.text_message);
                messageText.setText(getItem(pos));
            }
            return result;
        }
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_chat_window);
        Toolbar toolbar = findViewById(R.id.toolbar);
        toolbar.setTitle(getString(R.string.chat_window_title));
        toolbar.setTitleTextColor(getColor(R.color.white));
        setSupportActionBar(toolbar);
        if(getSupportActionBar() != null){
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }
        listView = findViewById(R.id.chat_list);
        messageInput = findViewById(R.id.message_input);
        sendButton = findViewById(R.id.send_button);
        detailContainer = findViewById(R.id.detail_container);
        sendButton.setOnClickListener(this::sendMessage);
        chatMessages = new ArrayList<>();
        chatAdapter = new ChatAdapter(this);
        listView.setAdapter(chatAdapter);
        listView.setOnItemClickListener((parent, view, position, id) -> {
            String message = chatMessages.get(position);
            if (detailContainer != null) {
                MessageFragment fragment = new MessageFragment();
                Bundle bundle = new Bundle();
                bundle.putString("message", message);
                bundle.putLong("id", id);
                bundle.putBoolean("isTablet", true);
                fragment.setArguments(bundle);

                getSupportFragmentManager()
                        .beginTransaction()
                        .replace(R.id.detail_container, fragment)
                        .commit();

            } else {
                Intent intent = new Intent(ChatWindow.this, MessageDetails.class);
                intent.putExtra("message", message);
                intent.putExtra("id", id);
                startActivityForResult(intent,1001);
            }
        });

        ChatDatabaseHelper dbHelper = new ChatDatabaseHelper(this);
        try{
            db = dbHelper.getWritableDatabase();
            cursor = db.rawQuery("SELECT id, message from messages", null);
            if (cursor.moveToFirst()) {
                do {
                    String msg = cursor.getString(cursor.getColumnIndexOrThrow(ChatDatabaseHelper.KEY_MESSAGE));
                    Log.i(ACTIVITY_NAME, "SQL MESSAGE: " + msg);
                    chatMessages.add(msg);
                } while (cursor.moveToNext());
            } else {
                Log.i(ACTIVITY_NAME, "No messages found in DB.");
                Toast.makeText(this,"No Messages found in DB.",Toast.LENGTH_SHORT).show();
            }
            Log.i(ACTIVITY_NAME,"Cursor’s  column count = " + cursor.getColumnCount());
            for (int i = 0; i < cursor.getColumnCount(); i++) {
                Log.i(ACTIVITY_NAME, "Column " + i + " name = " + cursor.getColumnName(i));
            }
        }catch (Exception e){
            Log.e("ERROR_RETRIEVING_MESSAGES","Error occurred while retrieving messages... " + e);
        }
        values = new ContentValues();
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
    }

    @Override
    public void onDestroy(){
        super.onDestroy();
        if (db != null && db.isOpen()) {
            db.close();
        }
    }

    public void deleteMessage(long id) {
        db.delete(ChatDatabaseHelper.TABLE_NAME,
                ChatDatabaseHelper.KEY_ID + "=?",
                new String[]{String.valueOf(id)});
        for (int i = 0; i < chatMessages.size(); i++) {
            if (chatAdapter.getItemId(i) == id) {
                chatMessages.remove(i);
                chatAdapter.notifyDataSetChanged();
                break;
            }
        }
        if (detailContainer != null) {
            getSupportFragmentManager().beginTransaction().remove(
                    getSupportFragmentManager().findFragmentById(R.id.detail_container)
            ).commit();
        }
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == 1001 && resultCode == 1002) {
            if (data != null && data.hasExtra("id")) {
                long deleteId = data.getLongExtra("id", -1);
                db.delete(ChatDatabaseHelper.TABLE_NAME,
                        ChatDatabaseHelper.KEY_ID + "=?",
                        new String[]{String.valueOf(deleteId)});
                for (int i = 0; i < chatMessages.size(); i++) {
                    if (chatAdapter.getItemId(i) == deleteId) {
                        chatMessages.remove(i);
                        break;
                    }
                }

                chatAdapter.notifyDataSetChanged();
                Toast.makeText(this, "Message deleted", Toast.LENGTH_SHORT).show();
            }
        }
    }


    public void sendMessage(View view){
        String message = messageInput.getText().toString().trim();
        if(message.isEmpty()){
            Toast.makeText(this, getString(R.string.empty_message),Toast.LENGTH_SHORT).show();
            return;
        }
        chatMessages.add(message);
        values.put("message",message);
        db.insert("messages",null,values);
        cursor = db.rawQuery("SELECT id, message FROM messages", null);
        chatAdapter.notifyDataSetChanged();
        messageInput.setText("");
    }
}