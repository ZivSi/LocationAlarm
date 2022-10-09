package com.example.locationalarm;

import android.content.Context;
import android.os.Build;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.RequiresApi;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.textfield.TextInputEditText;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;

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
    static void initSearchRecycler(TextInputEditText searchBox, ArrayList<ItemData> dataArrayList, ArrayList<ItemData> fixedData, RecyclerAdapter adapter) {
        // set onclick listener
        searchBox.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            @RequiresApi(api = Build.VERSION_CODES.R)
            @Override
            public void afterTextChanged(Editable s) {
                // S is new content
                // Filter data array

                dataArrayList.clear();

                for (ItemData item : fixedData) {
                    Log.d("Tag", "afterTextChanged: Checking item: " + item.getName());
                    if (item.getName().toLowerCase().contains(String.valueOf(s).toLowerCase()) || item.getAddress().toLowerCase().contains(String.valueOf(s).toLowerCase()) || item.getLatitude().toLowerCase().contains(String.valueOf(s).toLowerCase()) || item.getLongitude().toLowerCase().contains(String.valueOf(s).toLowerCase())) {

                        Log.d("Tag", "afterTextChanged: " + item.getName() + " Should be");
                        if (!dataArrayList.contains(item)) {
                            Log.d("Tag", "afterTextChanged: " + item.getName() + " Added");
                            dataArrayList.add(item);
                        }

                    } else {
                        Log.d("Tag", "afterTextChanged: " + item.getName() + " Should not be");

                        if (dataArrayList.contains(item)) {
                            Log.d("Tag", "afterTextChanged: " + item.getName() + " Removed");
                            dataArrayList.remove(item);
                        }
                    }
                }

                // TODO: make with animations
                adapter.updateData(dataArrayList);
            }
        });
    }

    /**
     * Create object of the recyclerview using context of MainActivity
     */
    static ArrayList<Object> initRecyclerView(Context context, ArrayList<ItemData> dataArrayList, RecyclerView recyclerView) {
        RecyclerAdapter adapter = new RecyclerAdapter(dataArrayList, context);
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
        MainActivity.data.put("My School", new ItemData("Name", "Nofei Prat", "2.23423", "12.4324", "300"));

        MainActivity.data.put("My School2", new ItemData("Name2", "Jerusalem", "64.33", "23.3454", "4000"));

        MainActivity.data.put("My School3", new ItemData("Name3", "Springfield", "31.4", "33.43244", "5000"));

        MainActivity.data.put("My School4", new ItemData("Name4", "UK", "36.345", "32.434", "1200"));

    }

    /**
     *
     * @param data Map of data to save to file
     * @return data as orgenized string
     */
    static String dataAsString(HashMap<String, ItemData> data) {
        String temp = "";

        /*
        temp =
        Location|LocationZM֎Nofey PratZM֎34.325ZM֎35.324ZM֎500
        Location2|Location2ZM֎Nofey PratZM֎34.325ZM֎35.324ZM֎500
        Location3|Location3ZM֎Nofey PratZM֎34.325ZM֎35.324ZM֎500
         */
        for (String key : data.keySet()) {
            ItemData properties = data.get(key);
            temp += key + ":" + properties.toString();
            temp += "\n";
        }

        // Remove last \n
        temp = temp.substring(0, temp.length() - 1);

        return temp;
    }

    /*
save a hashmap of string and itemdata to a file
 */
    static void SaveData(Context context, HashMap<String, ItemData> data) {
        try {
            OutputStreamWriter outputStreamWriter = new OutputStreamWriter(context.openFileOutput(MainActivity.FILE_NAME, Context.MODE_PRIVATE));
            outputStreamWriter.write(dataAsString(data));
            outputStreamWriter.close();

            print("Saved data as string: " + dataAsString(data));
        } catch (IOException e) {
            Log.e("Exception", "File write failed: " + e);
        }
    }

    static HashMap<String, ItemData> stringToMap(String dataAsString) {
        HashMap<String, ItemData> temp = new HashMap<>();

        /*
        dataAsString =
        Location|LocationZM֎Nofey PratZM֎34.325ZM֎35.324ZM֎500
        Location2|Location2ZM֎Nofey PratZM֎34.325ZM֎35.324ZM֎500
        Location3|Location3ZM֎Nofey PratZM֎34.325ZM֎35.324ZM֎500
         */

        for (String line : dataAsString.split("\n")) { // Location|LocationZM֎Nofey PratZM֎34.325ZM֎35.324ZM֎500
            if(line.equals("") || line.equals("\n")) {
                continue;
            }
            String key = line.split(":")[0]; // Location
            String data = line.split(":")[1]; // LocationZM֎Nofey PratZM֎34.325ZM֎35.324ZM֎500

            String[] dataSplitted = data.split(MainActivity.SPLITTER);

            temp.put(key, new ItemData(dataSplitted[0], dataSplitted[1], dataSplitted[2], dataSplitted[3], dataSplitted[4]));
        }

        return temp;
    }

    /*
    load a hashmap of string and itemdata from a file
    */
    static HashMap<String, ItemData> LoadData(Context context) {

        HashMap<String, ItemData> temp = new HashMap<>();

        try {
            InputStream inputStream = context.openFileInput(MainActivity.FILE_NAME);

            if (inputStream != null) {
                InputStreamReader inputStreamReader = new InputStreamReader(inputStream);
                BufferedReader bufferedReader = new BufferedReader(inputStreamReader);
                String receiveString = "";
                String stringBuilder = "";

                while ((receiveString = bufferedReader.readLine()) != null) {
                    stringBuilder += receiveString;
                }

                inputStream.close();

                temp = stringToMap(stringBuilder);
            }
        } catch (FileNotFoundException e) {
            print("File not found: " + e.toString());
        } catch (IOException e) {
            print("Can not read file: " + e.toString());
        }

        return temp;
    }

    /**
     * Create text file if not exists
     */
    static void createFileIfNotExists(Context context) {
        try {
            OutputStreamWriter outputStreamWriter = new OutputStreamWriter(context.openFileOutput(MainActivity.FILE_NAME, Context.MODE_PRIVATE));
            outputStreamWriter.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    static void print(String s) {
        Log.d("Tag", "Debug: " + s);
    }
}
