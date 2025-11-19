package com.manpreet.androidassignments;

import android.content.Intent;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;


public class MessageFragment extends Fragment {
    private long messageId;
    private Boolean isTablet = false;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_message_details, container, false);

        TextView messageTextView = view.findViewById(R.id.message_text);
        TextView idTextView = view.findViewById(R.id.message_id);
        Button deleteButton = view.findViewById(R.id.delete_button);

        Bundle args = getArguments();
        if (args != null) {
            messageId = args.getLong("id");
            String message = args.getString("message");
            isTablet = args.getBoolean("isTablet");
            messageTextView.setText(message);
            idTextView.setText(String.valueOf(messageId));
        }

        deleteButton.setOnClickListener(v -> {
            if (isTablet) {
                if (getActivity() instanceof ChatWindow) {
                    ((ChatWindow) getActivity()).deleteMessage(messageId);
                }
            } else {
                // Phone: send result back and finish activity
                Intent resultIntent = new Intent();
                resultIntent.putExtra("id", messageId);
                getActivity().setResult(1002, resultIntent);
                getActivity().finish();
            }
        });

        return view;
    }
}