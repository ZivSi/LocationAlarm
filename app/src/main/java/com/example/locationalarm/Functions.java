package com.example.locationalarm;

import android.app.ActivityManager;
import android.content.Context;
import android.os.Build;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.RequiresApi;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.textfield.TextInputEditText;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
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
     * When searching in the search bar, update the recylcer view
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

                for (ItemData item : fixedData) { // check all fields in the item
                    if (item.getName().toLowerCase().contains(String.valueOf(s).toLowerCase()) ||
                            item.getAddress().toLowerCase().contains(String.valueOf(s).toLowerCase()) ||
                            item.getLatitude().toLowerCase().contains(String.valueOf(s).toLowerCase()) ||
                            item.getLongitude().toLowerCase().contains(String.valueOf(s).toLowerCase())) {
                        if (!dataArrayList.contains(item)) {
                            dataArrayList.add(item);
                        }
                    }
                    else {
                        if (dataArrayList.contains(item)) {
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
    static void showTextIfEmpty(TextView noLocationsTextView) {
        if (MainActivity.data.size() == 0) {
            noLocationsTextView.setVisibility(View.VISIBLE);
        } else {
            noLocationsTextView.setVisibility(View.GONE);
        }
    }

    static void putTestingData() {
        MainActivity.data.put("My School", new ItemData("Name", "Nofei Prat", "2.23423", "12.4324", "300"));

        MainActivity.data.put("My School2", new ItemData("Name2", "Jerusalem", "64.33", "23.3454", "4000"));

        MainActivity.data.put("My School3", new ItemData("Name3", "Springfield", "31.4", "33.43244", "5000"));

        MainActivity.data.put("My School4", new ItemData("Name4", "UK", "36.345", "32.434", "1200"));

    }

    /**
     * set data ready for saving to file
     * @param data Map of data to save to file
     * @return data as organized string
     */
    static String dataAsString(HashMap<String, ItemData> data) {
        StringBuilder temp = new StringBuilder();

        /*
        temp =
        Location:LocationZM֎Nofey PratZM֎34.325ZM֎35.324ZM֎500
        Location2:Location2ZM֎Nofey PratZM֎34.325ZM֎35.324ZM֎500
        Location3:Location3ZM֎Nofey PratZM֎34.325ZM֎35.324ZM֎500
         */
        for (String key : data.keySet()) {
            ItemData properties = data.get(key);
            temp.append(key).append(":").append(properties.toString());
            temp.append("\n");
        }

        return temp.toString();
    }

    /**
     * Save the map to the file
     */
    static void SaveData(Context context, HashMap<String, ItemData> data) {
        File externalFile = new File(context.getExternalFilesDir(MainActivity.DIR_PATH), MainActivity.FILE_NAME);
        FileOutputStream fos;

        try {
            fos = new FileOutputStream(externalFile);
            fos.write(dataAsString(data).getBytes());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    static HashMap<String, ItemData> stringToMap(String dataAsString) {
        HashMap<String, ItemData> temp = new HashMap<>();

        for (String line : dataAsString.split("\n")) { // Location:LocationZM֎Nofey PratZM֎34.325ZM֎35.324ZM֎500
            if (line.equals("") || line.equals("\n")) {
                continue;
            }
            String key = line.split(":")[0]; // Location
            String data = line.split(":")[1]; // LocationZM֎Nofey PratZM֎34.325ZM֎35.324ZM֎500

            String[] dataSplitted = data.split(MainActivity.SPLITTER);

            temp.put(key, new ItemData(dataSplitted[0], dataSplitted[1], dataSplitted[2], dataSplitted[3], dataSplitted[4]));
        }

        return temp;
    }

    /**
     * Load the data from the file
     */
    static HashMap<String, ItemData> LoadData(Context context) {
        HashMap<String, ItemData> temp;

        FileReader fr;
        File externalFile = new File(context.getExternalFilesDir(MainActivity.DIR_PATH), MainActivity.FILE_NAME);
        StringBuilder content = new StringBuilder();

        try {
            fr = new FileReader(externalFile);
            BufferedReader br = new BufferedReader(fr);

            String line = br.readLine();

            while (line != null) {
                content.append(line + "\n");
                line = br.readLine();
            }

        } catch (IOException e) {
            e.printStackTrace();
        }

        temp = stringToMap(content.toString());

        return temp;
    }

    /**
     * Create text file if not exists
     */
    static void createFileIfNotExists(Context context) {
        try {
            OutputStreamWriter outputStreamWriter = new OutputStreamWriter(context.openFileOutput(MainActivity.FILE_NAME, Context.MODE_PRIVATE));
            outputStreamWriter.append("");
            outputStreamWriter.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    // before opening a popup menu use this function to make
    // the icons on the popup menu appear
    static void showIconsForPopupMenu(PopupMenu popup, Context context) {
        try {
            Field[] fields = popup.getClass().getDeclaredFields();
            for (Field field : fields) {
                if ("mPopup".equals(field.getName())) {
                    field.setAccessible(true);
                    Object menuPopupHelper = field.get(popup);
                    Class<?> classPopupHelper = Class.forName(menuPopupHelper.getClass().getName());
                    Method setForceIcons = classPopupHelper.getMethod("setForceShowIcon", boolean.class);
                    setForceIcons.invoke(menuPopupHelper, true);
                    break;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    static boolean startLocationAlarm(String longitude, String latitude, int radius, Context context) {
        LocationFinder locationFinder = new LocationFinder(longitude, latitude, context);
        int dist = locationFinder.getDistanceFromUserToDestination();
        if (dist <= radius) {
            Toast.makeText(context, "You are in the radius", Toast.LENGTH_SHORT).show();
            return true;
        }
        Toast.makeText(context, "out of radius", Toast.LENGTH_SHORT).show();
        return false;
    }

    /**
     * @param service specific service to check
     * @return if service is running or not
     */
    static boolean isServiceRunning(Context context, AppService service) {
        ActivityManager manager = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);

        for (ActivityManager.RunningServiceInfo runnnigService : manager.getRunningServices(Integer.MAX_VALUE)) {
            if (service.getClass().getName().equals(runnnigService.service.getClassName())) {
                return true;
            }
        }
        return false;
    }
}
