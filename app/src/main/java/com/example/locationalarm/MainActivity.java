package com.example.locationalarm;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.material.textfield.TextInputEditText;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Objects;


public class MainActivity extends AppCompatActivity {
    static File file = new File("res/raw/data.properties");
    static final String FILE_PATH = file.getAbsolutePath();

    RecyclerView recyclerView;
    RecyclerAdapter adapter;

    static HashMap<String, ItemData> data = new HashMap<>();
    ArrayList<ItemData> dataArrayList = new ArrayList<>();
    ArrayList<ItemData> fixedData = new ArrayList<>();

    TextInputEditText searchBox;
    TextView noLocationsTextView;

    ImageView settingsButton;

    Animation settings_animation;

    Thread openSettingsThread;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Objects.requireNonNull(getSupportActionBar()).hide(); // Prevent null pointer exception

        openSettingsThread = new Thread(() -> {
            startActivity(new Intent(MainActivity.this, Settings.class));
        });

        initViews();

        data = Functions.LoadData(FILE_PATH);

        data = new HashMap<>();

        Functions.putTestingData();

        // Create an array from the data in the map
        dataArrayList = Functions.dataAsArray(data);
        fixedData = new ArrayList<>(dataArrayList); // Array that will never change, only when removing/adding item to map

        Functions.showTextIfEmpty(fixedData, noLocationsTextView); // Show textview if there are no items in recyclerview

        // Set adapter and create recyclerview object
        // Get ass array because java cannot pass by reference
        ArrayList<Object> items = Functions.initRecyclerView(this, adapter, dataArrayList, recyclerView);

        recyclerView = (RecyclerView) items.get(0);
        adapter = (RecyclerAdapter) items.get(1);
        Functions.initSearchRecycler(searchBox, dataArrayList, fixedData, adapter);
    }

    @Override
    protected void onResume() {
        super.onResume();

        // Save data to file
        if (data.size() > 0) {
            Functions.SaveData(data, FILE_PATH);

            dataArrayList = Functions.dataAsArray(data);
            fixedData = new ArrayList<>(dataArrayList);
            adapter.updateData(dataArrayList);
        }
    }

    /**
     * Init to all id's and views to create objects
     */
    private void initViews() {
        searchBox = findViewById(R.id.searchBox);
        recyclerView = findViewById(R.id.recyclerView);
        noLocationsTextView = findViewById(R.id.noLocationsTextView);
        settingsButton = findViewById(R.id.settingsButton);

        settings_animation = AnimationUtils.loadAnimation(this, R.anim.settings_animation);

        settings_animation.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {
            }

            @Override
            public void onAnimationEnd(Animation animation) {
                // Open settings
                openSettingsThread.start();
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });
    }


    public void OpenEditLayout(View view) {
        startActivity(new Intent(this, EditLayout.class));
    }


    public void OpenSettings(View view) {
        // Animate
        settingsButton.startAnimation(settings_animation);
    }
}