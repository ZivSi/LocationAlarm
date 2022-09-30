package com.example.locationalarm;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

public class MainActivity extends AppCompatActivity {
    Intent intent = new Intent(MainActivity.this, EditLayout.class);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // TODO: Read data from properties file, add to map, add to array from map, and add to recyclerview
        /*
        1. Read from file
        2. Add to map
        3. Add to array from map
        4. Add to recyclerView
         */
    }

    public void OpenEditLayout(View view) {
        // When press 'add' button
        startActivity(intent);
        finish();
    }
}