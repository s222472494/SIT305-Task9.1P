package com.example.a101plostandfoundmapapp;


import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;

public class ListItemsActivity extends AppCompatActivity {

    ListView listView;
    ArrayList<String> advertList;
    ArrayList<Integer> advertIds;
    ArrayAdapter<String> adapter;
    DatabaseHelper db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list_items);

        listView = findViewById(R.id.listViewAdverts); // Make sure this ID matches your XML
        advertList = new ArrayList<>();
        advertIds = new ArrayList<>();
        db = new DatabaseHelper(this);

        loadAdverts();

        listView.setOnItemClickListener((adapterView, view, i, l) -> {
            if (!advertIds.isEmpty()) {
                int id = advertIds.get(i);
                Intent intent = new Intent(ListItemsActivity.this, RemoveItemActivity.class);
                intent.putExtra("ITEM_ID", id);
                startActivity(intent);
            } else {
                Toast.makeText(this, "Nothing to show", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void loadAdverts() {
        advertList.clear();
        advertIds.clear();
        Cursor cursor = db.getAllAdverts();

        if (cursor.getCount() == 0) {
            advertList.add("No items found.");
            // Don't add any ID to advertIds
            adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, advertList);
            listView.setAdapter(adapter);
            listView.setOnItemClickListener(null); // Disable clicks
            return;
        }

        while (cursor.moveToNext()) {
            int id = cursor.getInt(0);
            String type = cursor.getString(1);
            String name = cursor.getString(2);
            String desc = cursor.getString(4);

            advertList.add(type + ": " + name + " - " + desc);
            advertIds.add(id);
        }

        adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, advertList);
        listView.setAdapter(adapter);

        // Ensure click listener is active
        listView.setOnItemClickListener((adapterView, view, i, l) -> {
            int itemId = advertIds.get(i);
            Intent intent = new Intent(ListItemsActivity.this, RemoveItemActivity.class);
            intent.putExtra("ITEM_ID", itemId); // Consistent key name
            startActivity(intent);
        });

    }


    @Override
    protected void onResume() {
        super.onResume();
        loadAdverts(); // Refresh list after returning from RemoveItemActivity
    }
}
