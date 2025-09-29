package com.manpreet.androidassignments;

import android.Manifest;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.GridLayout;
import android.widget.ImageButton;
import android.widget.RadioButton;
import android.widget.Toast;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContract;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.widget.SwitchCompat;
import androidx.appcompat.widget.Toolbar;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.app.NavUtils;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.android.material.snackbar.Snackbar;

import java.io.IOException;


public class ListItemsActivity extends AppCompatActivity {
    String DEBUG_MESSAGE_KEY = "LIST_ITEMS_DEBUG";
    private  ImageButton imageButton;
    private CheckBox checkBox;
    private GridLayout layout;
    private static final int CAMERA_REQUEST_CODE = 20;
    private static final int IMAGE_CAPTURE_CODE = 30;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_list_items);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        imageButton = findViewById(R.id.imageButton);
        imageButton.setOnClickListener(this::onImageButtonClick);
        layout = findViewById(R.id.main);
        SwitchCompat switchButton = findViewById(R.id.switch1);
        switchButton.setOnCheckedChangeListener(this::setOnCheckedChanged);
        checkBox = findViewById(R.id.checkBox);
        checkBox.setOnCheckedChangeListener(this::onCheckChanged);
        if(getSupportActionBar() != null){
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }
        Button navigateToMainActivityButton = findViewById(R.id.navigate_to_main_activity);
        navigateToMainActivityButton.setOnClickListener(this::onNavigateToMainActivityClick);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
    }
    @Override
    protected void onResume(){
        super.onResume();
        Log.d(DEBUG_MESSAGE_KEY,"In ListItemsActivity and Executing onResume() Callback");
    }
    @Override
    protected void onStart(){
        super.onStart();
        Log.d(DEBUG_MESSAGE_KEY,"In ListItemsActivity and Executing onStart() Callback");
    }
    @Override
    protected void onPause(){
        super.onPause();
        Log.d(DEBUG_MESSAGE_KEY,"In ListItemsActivity and Executing onPause() Callback");
    }
    @Override
    protected void onStop(){
        super.onStop();
        Log.d(DEBUG_MESSAGE_KEY,"In ListItemsActivity and Executing onStop() Callback");
    }
    @Override
    protected void onDestroy(){
        super.onDestroy();
        Log.d(DEBUG_MESSAGE_KEY,"In ListItemsActivity and Executing onDestroy() Callback");
    }
    @Override
    protected void onSaveInstanceState(Bundle savedInstanceState){
        super.onSaveInstanceState(savedInstanceState);
        Log.d(DEBUG_MESSAGE_KEY,"In ListItemsActivity and Executing onSaveInstanceState() Callback");
    }
    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState){
        super.onRestoreInstanceState(savedInstanceState);
        Log.d(DEBUG_MESSAGE_KEY,"In ListItemsActivity and Executing onRestoreInstanceState() Callback");
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if(item.getItemId() == android.R.id.home){
            NavUtils.navigateUpFromSameTask(this);
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantedResults){
        super.onRequestPermissionsResult(requestCode,permissions,grantedResults);
        if(requestCode == CAMERA_REQUEST_CODE){
            if(grantedResults.length > 0 && grantedResults[0] == PackageManager.PERMISSION_GRANTED){
                openCamera();
            }else{
                Toast.makeText(this,"Camera Permission Denied",Toast.LENGTH_LONG).show();
            }
        }
    }
    @Override
    public void onActivityResult(int requestCode, int responseCode, Intent data){
        super.onActivityResult(requestCode,responseCode,data);
        if(requestCode == IMAGE_CAPTURE_CODE){
            if(data != null && data.getExtras() != null){
                Bitmap imageBitmap = (Bitmap) data.getExtras().get("data");
                print("Received Image Bitmap: " + imageBitmap);
                imageButton.setImageBitmap(imageBitmap);
            }else{
                Toast.makeText(this, "Failed to capture image", Toast.LENGTH_LONG).show();
            }
        }
    }
    private void onNavigateToMainActivityClick(View view){
        Intent intent = new Intent(this,MainActivity.class);
        startActivity(intent);
    }
    private void openCamera(){
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        startActivityForResult(intent, IMAGE_CAPTURE_CODE);
    }
    private void onImageButtonClick(View view){
        if(ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED){
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.CAMERA}, CAMERA_REQUEST_CODE);
        }else{
            openCamera();
        }
    }
    private void setOnCheckedChanged(CompoundButton button, boolean isChecked){
        if(isChecked){
            Toast.makeText(this, "Switch is On", Toast.LENGTH_SHORT).show();
        }else{
            Toast.makeText(this, "Switch is Off", Toast.LENGTH_LONG).show();
        }
    }
    private void onCheckChanged(CompoundButton button, boolean isChecked){
        AlertDialog.Builder builder = new AlertDialog.Builder(ListItemsActivity.this);
        builder.setTitle(getString(R.string.alert_dialog_title))
                .setMessage(getString(R.string.alert_dialog_description))
                .setPositiveButton(getString(R.string.ok), (dialogInterface, i) -> {
                    Intent intent = new Intent();
                    intent.putExtra("Response", "Here is my response");
                    setResult(ListItemsActivity.RESULT_OK, intent);
                    finish();
                })
                .setNegativeButton(getString(R.string.cancel),((dialogInterface, i) -> {
                    dialogInterface.cancel();
                }))
                .show();
    }
    private void print(String s){
        Snackbar.make(layout, s,Snackbar.LENGTH_LONG).show();
    }
}