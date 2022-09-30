package com.example.locationalarm;

import android.content.Context;
import android.graphics.Color;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.Arrays;
import java.util.HashMap;

public class RecyclerAddapter extends RecyclerView.Adapter<RecyclerAddapter.ViewHolders> {
    @NonNull
    @Override
    public RecyclerAddapter.ViewHolders onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return null;
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerAddapter.ViewHolders holder, int position) {

    }

    @Override
    public int getItemCount() {
        return 0;
    }

    public class ViewHolders extends RecyclerView.ViewHolder {
        public ViewHolders(@NonNull ViewGroup parent) {
            super(parent);
        }
    }
}
