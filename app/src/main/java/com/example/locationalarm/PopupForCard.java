package com.example.locationalarm;

import android.content.Context;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.PopupMenu;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;

public class PopupForCard extends AppCompatActivity {

    String out = "";
    ArrayList<ItemData> dataList;
    int position;
    public ArrayList<ItemData> showPopup(View v, Context context, ArrayList<ItemData> data, int pos) {
        PopupMenu popup = new PopupMenu(context, v);
        popup.getMenuInflater().inflate(R.menu.card_popup_menu, popup.getMenu());
        dataList = data;
        position = pos;
        popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem menuItem) {
                out = menuItem.getTitle().toString();
                dataList = popupSwitch(dataList, position, out);
                return true;
            }
        });
        popup.show();
        return dataList;
    }

    /**
     * takes the input from the popup menu and does what it needs
     * @param data  the array list of the itemData
     * @param pos   the position of the item in the recycler view
     */
    public ArrayList<ItemData> popupSwitch(ArrayList<ItemData> data, int pos, String out){
        if (out != null) {
            if (out.equals("delete")) {
                data.remove(pos);
            } else if (out.equals("start")) {
                // todo: move to new activity
            } else if (out.equals("edit")) {
                // todo: move to the edit activity
            }
        }
        return data;
    }
}

