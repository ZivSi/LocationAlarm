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
import android.widget.SearchView;
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

    HashMap<String, ItemData> data = new HashMap<>();
    ArrayList<ItemData> dataArrayList = new ArrayList<>();
    ArrayList<ItemData> fixedData = new ArrayList<>();

    TextInputEditText searchBox;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        initViews();


        // Read from file and add to tha map
        try {
            data = LoadData(FILE);
        } catch (IOException ignored) {
        }

        // Create an array from the data in the map
        dataArrayList = dataAsArray(data);

        // for testing:
        dataArrayList.add(new ItemData("test", "Israel, Nofey Prat", "38.55", "36.88", "100", "ring1"));
        dataArrayList.add(new ItemData("test2", "USA", "38.554444444", "36.4444488", "134340", "ring1"));
        dataArrayList.add(new ItemData("test3", "England", "38.554444444", "36.4488", "134", "ring1"));

        // * fixedData will never change - used to filter
        fixedData = new ArrayList<>(dataArrayList);

        // Set adapter and create recyclerview object
        initRecyclerView(this);
        initSearchRecycler();
    }

    private void initViews() {
        searchBox = findViewById(R.id.searchBox);
        recyclerView = findViewById(R.id.recyclerView);
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
    }


    /**
     * @param context context of the activity
     */
    private void initRecyclerView(Context context) {
        adapter = new RecyclerAdapter(dataArrayList, context);
        recyclerView.setLayoutManager(new LinearLayoutManager(context));
        recyclerView.setAdapter(adapter);
    }

}