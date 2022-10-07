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
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.SearchView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.textfield.TextInputEditText;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

public class MainActivity extends AppCompatActivity {
    static final String FILE = "data.properties";

    Properties properties;

    RecyclerView recyclerView;
    RecyclerAdapter adapter;

    static HashMap<String, ItemData> data = new HashMap<>();
    ArrayList<ItemData> dataArrayList = new ArrayList<>();
    ArrayList<ItemData> fixedData = new ArrayList<>();

    TextInputEditText searchBox;
    TextView noLocationsTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        getSupportActionBar().hide();

        initViews();


        // Read from file and add to tha map
        try {
            data = LoadData(FILE);
        } catch (IOException ignored) {
        }

        // ! Testing - remove later
        data.put("My School", new ItemData("Name", "Nofei Prat",
                "2.23423", "12.4324", "300", "Hello Darkness"));

        data.put("My School2", new ItemData("Name2", "Jerusalem",
                "64.33", "23.3454", "4000", "Hello Darkness"));

        data.put("My School3", new ItemData("Name3", "Springfield",
                "31.4", "33.43244", "100", "Hello Darkness"));

        data.put("My School4", new ItemData("Name4", "UK",
                "36.345", "32.434", "1200", "Hello Darkness"));

        // Create an array from the data in the map
        dataArrayList = dataAsArray(data);
        // * fixedData will never change - used to filter
        fixedData = new ArrayList<>(dataArrayList);

        if (fixedData.size() == 0) {
            noLocationsTextView.setVisibility(View.VISIBLE);
        }

        // Set adapter and create recyclerview object
        initRecyclerView(this);
        initSearchRecycler();
    }

    @Override
    protected void onResume() {
        super.onResume();
        // save data to file
        if (data.size() > 0) {

            try {
                SaveData(data, FILE); // fixme: not saving data correctly for some reason - when exit it gets deleted
            } catch (IOException e) {
                e.printStackTrace();
            }
            Log.d("asdf", "entry: " + data.entrySet());
            Log.d("asdf", "key: " + data.keySet());
            dataArrayList = dataAsArray(data);
            fixedData = new ArrayList<>(dataArrayList);
            adapter.updateData(dataArrayList);
        }
    }

    private void initViews() {
        searchBox = findViewById(R.id.searchBox);
        recyclerView = findViewById(R.id.recyclerView);
        noLocationsTextView = findViewById(R.id.noLocationsTextView);
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

                int index = 0;

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

                    index++;
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
    static void SaveData(HashMap<String, ItemData> data, String file_name) throws IOException {
        Properties properties = new Properties();

        for (Map.Entry<String, ItemData> entry : data.entrySet()) {
            properties.put(entry.getKey(), entry.getValue());
        }

        properties.store(new FileOutputStream(file_name), null);
    }

    /*
    load a hashmap of string and itemdata from a file
     */
    private HashMap<String, ItemData> LoadData(String file_name) throws IOException {
        HashMap<String, ItemData> map_from_file = new HashMap<>();
        properties = new Properties();

        properties.load(new FileInputStream(file_name));

        for (String key : properties.stringPropertyNames()) {
            map_from_file.put(key, (ItemData) properties.get(key));
        }

        return map_from_file;
    }

    public void OpenEditLayout(View view) {
        // When press 'add' button
        startActivity(new Intent(this, EditLayout.class));
        // todo: update after returning from edit layout
    }


    /**
     * @param context context of the activity
     */
    private void initRecyclerView(Context context) {
        adapter = new RecyclerAdapter(dataArrayList, context);
        recyclerView.setLayoutManager(new LinearLayoutManager(context));
        recyclerView.setAdapter(adapter);
    }

    public void OpenSettings(View view) {
        startActivity(new Intent(this, Settings.class));
    }
}