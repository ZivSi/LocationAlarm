package com.example.locationalarm;

import static android.service.controls.ControlsProviderService.TAG;

import android.content.Context;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.textfield.TextInputEditText;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

public class Functions {
    /*
turn a hashmap of string and itemdata into an arraylist of itemdata
*/
    static ArrayList<ItemData> dataAsArray(HashMap<String, ItemData> data) {
        ArrayList<ItemData> temp = new ArrayList<>();

        for (String key : data.keySet()) {
            temp.add(data.get(key));
        }

        return temp;
    }

    /**
     * When searching in the search bar, update the recylcerview
     */
    static RecyclerAdapter initSearchRecycler(TextInputEditText searchBox,
                                   ArrayList<ItemData> dataArrayList,
                                   ArrayList<ItemData> fixedData,
                                   RecyclerAdapter adapter) {
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

        return adapter;
    }

    /**
     * Create object of the recyclerview using context of MainActivity
     */
    static ArrayList<Object> initRecyclerView(Context context, RecyclerAdapter adapter,
                                 ArrayList<ItemData> dataArrayList,
                                 RecyclerView recyclerView) {
        adapter = new RecyclerAdapter(dataArrayList, context);
        recyclerView.setLayoutManager(new LinearLayoutManager(context));
        recyclerView.setAdapter(adapter);

        return new ArrayList<>(Arrays.asList(recyclerView, adapter));
    }

    /**
     * Show textView that tells there are no locations saved yet
     */
    static void showTextIfEmpty(ArrayList<ItemData> fixedData, TextView noLocationsTextView) {
        if (fixedData.size() == 0) {
            noLocationsTextView.setVisibility(View.VISIBLE);
        }
    }

    static void putTestingData() {
        MainActivity.data.put("Name", new ItemData("Name", "Nofei Prat",
                "2.23423", "12.4324", "300"));

        MainActivity.data.put("Name2", new ItemData("Name2", "Jerusalem",
                "64.33", "23.3454", "4000"));

        MainActivity.data.put("Name3", new ItemData("Name3", "Springfield",
                "31.4", "33.43244", "5000"));

        MainActivity.data.put("Name4", new ItemData("Name4", "UK",
                "36.345", "32.434", "1200"));

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
    static HashMap<String, ItemData> LoadData(String file_name) {
        HashMap<String, ItemData> map_from_file = new HashMap<>();

        try {
            Properties properties = new Properties();

            properties.load(new FileInputStream(file_name));

            for (String key : properties.stringPropertyNames()) {
                map_from_file.put(key, (ItemData) properties.get(key));
            }
        } catch (IOException ignored) {
            ignored.printStackTrace();
        }

        return map_from_file;
    }
}
