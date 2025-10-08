package com.manpreet.androidassignments;

import android.content.Context;
import android.os.Bundle;
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
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
    }

    public void sendMessage(View view){
        String message = messageInput.getText().toString().trim();
        if(message.isEmpty()){
            Toast.makeText(this, getString(R.string.empty_message),Toast.LENGTH_SHORT).show();
            return;
        }
        chatMessages.add(message);
        chatAdapter.notifyDataSetChanged();
        messageInput.setText("");
    }
}