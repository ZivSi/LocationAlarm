package com.example.locationalarm;

import static android.service.controls.ControlsProviderService.TAG;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.LinearInterpolator;
import android.view.animation.RotateAnimation;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.textfield.TextInputEditText;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

public class MainActivity extends AppCompatActivity {
    static File file = new File("res/raw/data.properties");
    static final String FILE_PATH = file.getAbsolutePath();

    Properties properties = new Properties();

    RecyclerView recyclerView;
    RecyclerAdapter adapter;

    static HashMap<String, ItemData> data = new HashMap<>();
    ArrayList<ItemData> dataArrayList = new ArrayList<>();
    ArrayList<ItemData> fixedData = new ArrayList<>();

    TextInputEditText searchBox;
    TextView noLocationsTextView;

    ImageView settingsButton;

    Animation settings_animation;

    Thread openSettingsThred;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        getSupportActionBar().hide();

        openSettingsThred = new Thread(() -> {
            startActivity(new Intent(MainActivity.this, Settings.class));
        });

        initViews();

        data = LoadData(FILE_PATH);

        data = new HashMap<>();

        putTestingData();

        // Create an array from the data in the map
        dataArrayList = dataAsArray(data);
        fixedData = new ArrayList<>(dataArrayList); // Array that will never change, only when removing/adding item to map

        showTextIfEmpty(); // Show textview if there are no items in recyclerview

        // Set adapter and create recyclerview object
        initRecyclerView(this);
        initSearchRecycler();
    }

    private void showTextIfEmpty() {
        if (fixedData.size() == 0) {
            noLocationsTextView.setVisibility(View.VISIBLE);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();

        // Save data to file
        if (data.size() > 0) {
            SaveData(data, FILE_PATH);

            Log.d("asdf", "entry: " + data.entrySet());
            Log.d("asdf", "key: " + data.keySet());

            dataArrayList = dataAsArray(data);
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
                openSettingsThred.start();
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });
    }

    /**
     * When searching in the search bar, update the recylcerview
     */
    private void initSearchRecycler() {
        // set onclick listener
        searchBox.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            @Override
            public void afterTextChanged(Editable s) {
                // S is new content
                // Filter data array

                dataArrayList.clear();

                for (ItemData item : fixedData) {
                    Log.d(TAG, "afterTextChanged: Checking item: " + item.getName());
                    if (item.getName().toLowerCase().contains(String.valueOf(s).toLowerCase()) ||
                            item.getAddress().toLowerCase().contains(String.valueOf(s).toLowerCase()) ||
                            item.getLatitude().toLowerCase().contains(String.valueOf(s).toLowerCase()) ||
                            item.getLongitude().toLowerCase().contains(String.valueOf(s).toLowerCase())) {

                        Log.d(TAG, "afterTextChanged: " + item.getName() + " Should be");
                        if (!dataArrayList.contains(item)) {
                            Log.d(TAG, "afterTextChanged: " + item.getName() + " Added");
                            dataArrayList.add(item);
                        }

                    } else {
                        Log.d(TAG, "afterTextChanged: " + item.getName() + " Should not be");

                        if (dataArrayList.contains(item)) {
                            Log.d(TAG, "afterTextChanged: " + item.getName() + " Removed");
                            dataArrayList.remove(item);
                        }
                    }
                }

                // TODO: make with animations
                adapter.updateData(dataArrayList);
            }
        });
    }


    /*
    turn a hashmap of string and itemdata into an arraylist of itemdata
    */
    private ArrayList<ItemData> dataAsArray(HashMap<String, ItemData> data) {
        ArrayList<ItemData> temp = new ArrayList<>();

        for (String key : data.keySet()) {
            temp.add(data.get(key));
        }

        return temp;
    }

    /*
    save a hashmap of string and itemdata to a file
     */
    static void SaveData(HashMap<String, ItemData> data, String file_name) {
        Properties properties = new Properties();

        try {
            // Empty file completly before storing
            properties.store(new FileOutputStream(file_name), null);

            for (Map.Entry<String, ItemData> entry : data.entrySet()) {
                properties.put(entry.getKey(), entry.getValue());
            }

            properties.store(new FileOutputStream(file_name), null);
        } catch (IOException ioException) {
        }
    }

    /*
    load a hashmap of string and itemdata from a file
     */
    private HashMap<String, ItemData> LoadData(String file_name) {
        HashMap<String, ItemData> map_from_file = new HashMap<>();

        try {
            properties = new Properties();

            properties.load(new FileInputStream(file_name));

            for (String key : properties.stringPropertyNames()) {
                map_from_file.put(key, (ItemData) properties.get(key));
            }
        } catch (IOException ignored) {
            ignored.printStackTrace();
        }

        return map_from_file;
    }

    public void OpenEditLayout(View view) {
        startActivity(new Intent(this, EditLayout.class));
    }


    /**
     * Create object of the recyclerview using context of MainActivity
     */
    private void initRecyclerView(Context context) {
        adapter = new RecyclerAdapter(dataArrayList, context);
        recyclerView.setLayoutManager(new LinearLayoutManager(context));
        recyclerView.setAdapter(adapter);
    }

    public void OpenSettings(View view) {
        // Animate
        settingsButton.startAnimation(settings_animation);
    }

    private void putTestingData() {
        MainActivity.data.put("Name", new ItemData("Name", "Nofei Prat",
                "2.23423", "12.4324", "300"));

        MainActivity.data.put("Name2", new ItemData("Name2", "Jerusalem",
                "64.33", "23.3454", "4000"));

        MainActivity.data.put("Name3", new ItemData("Name3", "Springfield",
                "31.4", "33.43244", "5000"));

        MainActivity.data.put("Name4", new ItemData("Name4", "UK",
                "36.345", "32.434", "1200"));

    }
}