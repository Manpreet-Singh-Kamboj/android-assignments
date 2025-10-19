package com.manpreet.androidassignments.Assignment2;

import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.Bundle;

import com.google.android.material.snackbar.Snackbar;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import com.manpreet.androidassignments.databinding.ActivityTestToolbarBinding;

import com.manpreet.androidassignments.R;

public class TestToolbar extends AppCompatActivity {
    private SharedPreferences sharedPreferences;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ActivityTestToolbarBinding binding = ActivityTestToolbarBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        sharedPreferences = getSharedPreferences(getString(R.string.shared_preferences_file_name),Context.MODE_PRIVATE);
        setSupportActionBar(binding.toolbar);
        binding.fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Version 1.0 by Manpreet Singh", Snackbar.LENGTH_LONG)
                        .setAnchorView(R.id.fab)
                        .setAction("Action", null).show();
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu){
        super.onCreateOptionsMenu(menu);
        getMenuInflater().inflate(R.menu.toolbar_menu,menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if(id == R.id.action_one){
            Log.d("Toolbar", "Option 1 Selected");
            String message = sharedPreferences.getString("new_message","No new message...");
            Snackbar
                    .make(findViewById(R.id.toolbar),message,Snackbar.LENGTH_LONG)
                    .show();
            return true;
        } else if (id == R.id.action_two) {
            Log.d("Toolbar", "Option 2 Selected");
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder
                    .setTitle("Do you want to go back?")
                    .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            finish();
                        }
                    })
                    .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {

                        }
                    })
                    .show();
            return true;
        }else if (id == R.id.action_three){
            Log.d("Toolbar", "Option 3 Selected");
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            View dialogView = getLayoutInflater().inflate(R.layout.layout_custom_dialog,null);
            EditText messageInput = dialogView.findViewById(R.id.new_message);
            builder
                    .setView(dialogView)
                    .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            String message = messageInput.getText().toString();
                            Log.i("Input",messageInput.toString());
                            SharedPreferences.Editor editor = sharedPreferences.edit();
                            editor.putString("new_message", message);
                            editor.apply();
                        }
                    })
                    .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {

                        }
                    })
                    .show();
            return true;
        }else if(id == R.id.about){
            Toast.makeText(this, "Version 1.0, by Manpreet Singh", Toast.LENGTH_LONG).show();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}