package com.manpreet.androidassignments;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.android.material.snackbar.Snackbar;

public class LoginActivity extends AppCompatActivity {
    String DEBUG_MESSAGE_KEY = "LOGIN_DEBUG";
    private SharedPreferences sharedPreferences;
    private LinearLayout layout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_login);
        sharedPreferences = getSharedPreferences(getString(R.string.shared_preferences_file_name), Context.MODE_PRIVATE);
        loadUserData();
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        layout = findViewById(R.id.main);
        Button loginButton = findViewById(R.id.login_button);
        loginButton.setOnClickListener(this::onLoginClick);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
    }
    @Override
    protected void onResume(){
        super.onResume();
        Log.d(DEBUG_MESSAGE_KEY,"In LoginActivity and Executing onResume() Callback");
    }
    @Override
    protected void onStart(){
        super.onStart();
        Log.d(DEBUG_MESSAGE_KEY,"In LoginActivity and Executing onStart() Callback");
    }
    @Override
    protected void onPause(){
        super.onPause();
        Log.d(DEBUG_MESSAGE_KEY,"In LoginActivity and Executing onPause() Callback");
    }
    @Override
    protected void onStop(){
        super.onStop();
        Log.d(DEBUG_MESSAGE_KEY,"In LoginActivity and Executing onStop() Callback");
    }
    @Override
    protected void onDestroy(){
        super.onDestroy();
        Log.d(DEBUG_MESSAGE_KEY,"In LoginActivity and Executing onDestroy() Callback");
    }
    @Override
    protected void onSaveInstanceState(Bundle savedInstanceState){
        super.onSaveInstanceState(savedInstanceState);
        Log.d(DEBUG_MESSAGE_KEY,"In LoginActivity and Executing onSaveInstanceState() Callback");
    }
    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState){
        super.onRestoreInstanceState(savedInstanceState);
        Log.d(DEBUG_MESSAGE_KEY,"In LoginActivity and Executing onRestoreInstanceState() Callback");
    }
    private void loadUserData(){
        String email = sharedPreferences.getString(getString(R.string.shared_preferences_email_key),"");
        EditText loginInput = findViewById(R.id.email);
        loginInput.setText(email);
    }
    private void onLoginClick(View view){
        Intent intent = new Intent(this,MainActivity.class);
        EditText loginInput = findViewById(R.id.email);
        EditText passwordInput = findViewById(R.id.password);
        if(loginInput.getText().length() == 0 || passwordInput.getText().length() == 0){
            Toast.makeText(this,"Email or Password cannot be empty", Toast.LENGTH_LONG).show();
            return;
        }
        if(!Patterns.EMAIL_ADDRESS.matcher(loginInput.getText().toString()).matches()){
            Toast.makeText(this, "Please enter a valid email address", Toast.LENGTH_LONG).show();
            return;
        }
        SharedPreferences.Editor sharedPreferencesEditor = sharedPreferences.edit();
        sharedPreferencesEditor.putString(getString(R.string.shared_preferences_email_key), loginInput.getText().toString());
        sharedPreferencesEditor.apply();
        startActivity(intent);
    }
}