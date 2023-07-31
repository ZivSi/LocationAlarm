package com.example.locationalarm;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.textfield.TextInputEditText;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Objects;


public class MainActivity extends AppCompatActivity {
    static String COORDINATED_TAG = "COORDINATED_TAG";
    final static String DISTANCE_TAG = "DISTANCE_TAG";
    final static String FILE_NAME = "data.txt";
    final static String DIR_PATH = "FilesDir";
    final static String SPLITTER = "ZM֎";
    final static int MAX_NAME_LENGTH = 30;
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

        openSettingsThread = new Thread(() -> startActivity(new Intent(MainActivity.this, Settings.class)));

        initViews();

        // File exists?
        Functions.createFileIfNotExists(this);

        // Load data
        data = Functions.LoadData(this);

        // data.clear();
        // Functions.SaveData(this, data);

        dataArrayList = Functions.dataAsArray(data);
        fixedData = new ArrayList<>(dataArrayList); // ArrayList that will never change, only when removing/adding item to map

        Functions.showTextIfEmpty(noLocationsTextView);


        ArrayList<Object> items = Functions.initRecyclerView(this, dataArrayList, recyclerView);

        recyclerView = (RecyclerView) items.get(0);
        adapter = (RecyclerAdapter) items.get(1);
        Functions.initSearchRecycler(searchBox, dataArrayList, fixedData, adapter);
        LocationFinder.getLocationPermission(this);
        Functions.checkLocationActive(this);
    }

    @Override
    protected void onResume() {
        super.onResume();
        // After saving or deleting, update data from the map
        dataArrayList = Functions.dataAsArray(data);
        fixedData = new ArrayList<>(dataArrayList);
        adapter.updateData(dataArrayList);

        Functions.showTextIfEmpty(noLocationsTextView);
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

    // ! DEPRICATED function. use the one in app service.java
    public static void startLocationActiveMode(View view) {
        // TODO: check if service is already running and if so, don't start it again

        Thread tr = new Thread(() -> {
            while (Functions.isServiceRunning(view.getContext(), new AppService())) {
                try {
                    Thread.sleep(10000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        });

        tr.setDaemon(true);
        tr.start();
    }
}