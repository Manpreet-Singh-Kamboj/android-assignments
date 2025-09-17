package com.manpreet.androidassignments;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class LoginActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_login);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        Button loginButton = findViewById(R.id.login_button);
        loginButton.setOnClickListener(this::onLoginClick);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
    }

    private void onLoginClick(View view){
        Intent intent = new Intent(this,ListItemsActivity.class);
        EditText loginInput = findViewById(R.id.email);
        EditText passwordInput = findViewById(R.id.password);
        if(loginInput.getText().length() == 0 || passwordInput.getText().length() == 0){
            Toast inputValidation = Toast.makeText(this,"Email or Password cannot be empty", Toast.LENGTH_LONG);
            inputValidation.show();
            return;
        }
        startActivity(intent);
    }
}