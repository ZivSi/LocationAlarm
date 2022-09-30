package com.example.locationalarm;

import androidx.appcompat.app.AppCompatActivity;

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

    Intent intent = new Intent(MainActivity.this, EditLayout.class);
    Properties properties;
    HashMap<String, ItemData> data = new HashMap<>();
    ArrayList<ItemData> dataArrayList = new ArrayList<>();

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

        // 1. Read from file
        // 2. Add to map
        try {
            data = LoadData(FILE);
        } catch (IOException ignored) {
        }

        // 3. Add to array from map
        dataArrayList = dataAsArray(data);

        // TODO: Add to recyclerView
    }

    private ArrayList<ItemData> dataAsArray(HashMap<String, ItemData> data) {
        ArrayList<ItemData> temp = new ArrayList<>();

        for (String key : data.keySet()) {
            temp.add(data.get(key));
        }

        return temp;
    }

    private void SaveData(HashMap<String, ItemData> data, String file_name) throws IOException {
        properties = new Properties();

        for (Map.Entry<String, ItemData> entry : data.entrySet()) {
            properties.put(entry.getKey(), entry.getValue());
        }

        properties.store(new FileOutputStream(file_name), null);
    }

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
        startActivity(intent);
        finish();
    }
}