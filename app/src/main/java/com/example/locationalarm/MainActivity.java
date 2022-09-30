package com.example.locationalarm;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

public class MainActivity extends AppCompatActivity {
    static final String FILE = "data.properties";

    //Intent intent = new Intent(MainActivity.this, EditLayout.class);
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
        } catch (IOException ignored) {}

        // 3. Add to array from map
        dataArrayList = dataAsArray(data);

        // for testing:
        dataArrayList.add(new ItemData("test", "addr", "38.55", "36.88", "100", "ring1"));
        dataArrayList.add(new ItemData("test2", "ad2dr", "38.554444444", "36.4444488", "134340", "ring1"));
        dataArrayList.add(new ItemData("tesat2", "ad2dr", "38.554444444", "36.4488", "134", "ring1"));
        // 4. Set adapter
        recyclerView = findViewById(R.id.recyclerView);
        adapter = new RecyclerAdapter(dataArrayList, this);
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
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
        //startActivity(intent);
        finish();
    }
}