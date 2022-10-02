package com.example.locationalarm;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.SearchView;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        // 1. Read from file
        // 2. Add to map
        try {
            data = LoadData(FILE);
        } catch (IOException ignored) {
        }

        // 3. Add to array from map
        dataArrayList = dataAsArray(data);

        // for testing:
        dataArrayList.add(new ItemData("test", "addr", "38.55", "36.88", "100", "ring1"));
        dataArrayList.add(new ItemData("test2", "ad2dr", "38.554444444", "36.4444488", "134340", "ring1"));
        dataArrayList.add(new ItemData("tesat2", "ad2dr", "38.554444444", "36.4488", "134", "ring1"));
        // 4. Set adapter
        initRecyclerView(dataArrayList, this);
        initSearchRecycler();
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
     * @param data    array of data to be displayed
     * @param context context of the activity
     */
    private void initRecyclerView(ArrayList<ItemData> data, Context context) {
        RecyclerView recyclerView = findViewById(R.id.recyclerView);
        RecyclerAdapter adapter = new RecyclerAdapter(data, context);
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(context));
    }


    /**
     * when search bar is used, update the recycler view to show only the items that match the search
     */
    private void initSearchRecycler() {
        SearchView searchView = (SearchView) findViewById(R.id.searchView);
        // set onclick listener
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) { // change text on search
                ArrayList<ItemData> filterdList = new ArrayList<>();

                for (ItemData entry : dataArrayList) {// find matching items
                    if (entry.getName().toLowerCase().contains(newText.toLowerCase())) {
                        filterdList.add(entry);
                    }
                }
                initRecyclerView(filterdList, getApplicationContext()); // update recycler view
                return false;
            }
        });
    }


}