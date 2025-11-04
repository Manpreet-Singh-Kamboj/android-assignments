package com.manpreet.androidassignments;

import android.content.ContentValues;
import android.content.Context;
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
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
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

    class ChatAdapter extends ArrayAdapter<String> {
        public ChatAdapter(@NonNull Context context) {
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

        @NonNull
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
        sendButton.setOnClickListener(this::sendMessage);
        chatMessages = new ArrayList<>();
        chatAdapter = new ChatAdapter(this);
        listView.setAdapter(chatAdapter);
        ChatDatabaseHelper dbHelper = new ChatDatabaseHelper(this);
        try{
            db = dbHelper.getWritableDatabase();
            Cursor cursor = db.rawQuery("SELECT * from messages", null);
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
            cursor.close();
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
        db.close();
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
        chatAdapter.notifyDataSetChanged();
        messageInput.setText("");
    }
}