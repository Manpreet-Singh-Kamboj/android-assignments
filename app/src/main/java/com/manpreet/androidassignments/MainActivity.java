package com.manpreet.androidassignments;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.app.NavUtils;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.android.material.snackbar.Snackbar;

public class MainActivity extends AppCompatActivity {
    String DEBUG_MESSAGE_KEY = "MAIN_DEBUG";
    private ConstraintLayout layout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        Button button = findViewById(R.id.button);
        button.setOnClickListener(this::onButtonClick);
        layout = findViewById(R.id.main);
        if(getSupportActionBar() != null){
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
    }
    @Override
    protected void onResume(){
        super.onResume();
        Log.d(DEBUG_MESSAGE_KEY,"In MainActivity and Executing onResume() Callback");
    }
    @Override
    protected void onStart(){
        super.onStart();
        Log.d(DEBUG_MESSAGE_KEY,"In MainActivity and Executing onStart() Callback");
    }
    @Override
    protected void onPause(){
        super.onPause();
        Log.d(DEBUG_MESSAGE_KEY,"In MainActivity and Executing onPause() Callback");
    }
    @Override
    protected void onStop(){
        super.onStop();
        Log.d(DEBUG_MESSAGE_KEY,"In MainActivity and Executing onStop() Callback");
    }
    @Override
    protected void onDestroy(){
        super.onDestroy();
        Log.d(DEBUG_MESSAGE_KEY,"In MainActivity and Executing onDestroy() Callback");
    }
    @Override
    protected void onSaveInstanceState(Bundle savedInstanceState){
        super.onSaveInstanceState(savedInstanceState);
        Log.d(DEBUG_MESSAGE_KEY,"In MainActivity and Executing onSaveInstanceState() Callback");
    }
    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState){
        super.onRestoreInstanceState(savedInstanceState);
        Log.d(DEBUG_MESSAGE_KEY,"In MainActivity and Executing onRestoreInstanceState() Callback");
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item){
        if(item.getItemId() == android.R.id.home){
            NavUtils.navigateUpFromSameTask(this);
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
    @Override
    public void onActivityResult(int requestCode, int responseCode, Intent data){
        super.onActivityResult(requestCode,responseCode,data);
        if(requestCode == 10){
            Log.d(DEBUG_MESSAGE_KEY, "Returned to MainActivity.onActivityResult");
        }
        if(data.getStringExtra("Response") != null){
            Toast.makeText(this, "ListItemsActivity passed: " + data.getStringExtra("Response"), Toast.LENGTH_LONG).show();
        }
    }
    public void onButtonClick(View view){
        Intent intent = new Intent(this,ListItemsActivity.class);
        startActivityForResult(intent,10);
    }
}