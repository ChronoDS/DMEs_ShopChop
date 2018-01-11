package com.example.danie.dmes_shopchop;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.TextView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class CategoriesActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_categories);
        final TextView textView = findViewById(R.id.categoriesTextView);
        textView.setText("");
        DatabaseReference databaseRef = FirebaseAccess.getDatabaseReference();
        databaseRef.child("sweats").getRef().addListenerForSingleValueEvent(new ValueEventListener(){
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                Map<String, Object> hashMap = (HashMap<String,Object>) dataSnapshot.getValue();
                Set<String> keys = hashMap.keySet();
                for (String key: keys) {
                    textView.append(key + ": "+ hashMap.get(key) + "\n");
                }
            }

            @Override
            public void onCancelled(DatabaseError error) {
                textView.setText(error.getMessage());
            }
        });
    }
}
