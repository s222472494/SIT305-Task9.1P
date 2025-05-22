package com.example.a101plostandfoundmapapp;

import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class RemoveItemActivity extends AppCompatActivity {

    TextView textDetails;
    Button btnRemove;
    DatabaseHelper db;
    int itemId = -1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_remove_item);

        textDetails = findViewById(R.id.textItemDetails);
        btnRemove = findViewById(R.id.btnRemove);
        db = new DatabaseHelper(this);

        Intent intent = getIntent();
        if (intent != null && intent.hasExtra("ITEM_ID")) {
            itemId = intent.getIntExtra("ITEM_ID", -1);
            loadItemDetails(itemId);
        }

        btnRemove.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (itemId != -1) {
                    boolean deleted = db.deleteAdvert(itemId);
                    if (deleted) {
                        Toast.makeText(RemoveItemActivity.this, "Item removed", Toast.LENGTH_SHORT).show();
                        finish(); // Go back to list
                    } else {
                        Toast.makeText(RemoveItemActivity.this, "Failed to remove", Toast.LENGTH_SHORT).show();
                    }
                }
            }
        });
    }

    private void loadItemDetails(int id) {
        Cursor cursor = db.getAdvertById(id);
        if (cursor != null && cursor.moveToFirst()) {
            String type = cursor.getString(1);
            String name = cursor.getString(2);
            String phone = cursor.getString(3);
            String desc = cursor.getString(4);
            String date = cursor.getString(5);
            String location = cursor.getString(6);

            String details = type + ": " + name + "\nPhone: " + phone +
                    "\n" + desc + "\nDate: " + date + "\nAt: " + location;

            textDetails.setText(details);
        }
    }
}
