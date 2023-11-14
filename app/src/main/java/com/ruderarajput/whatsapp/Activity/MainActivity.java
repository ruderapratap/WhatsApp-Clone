package com.ruderarajput.whatsapp.Activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.PopupMenu;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.ruderarajput.whatsapp.Adapter.FragementsAdapter;
import com.ruderarajput.whatsapp.R;
import com.ruderarajput.whatsapp.databinding.ActivityMainBinding;

import java.util.HashMap;
import java.util.Map;

public class MainActivity extends AppCompatActivity {
    ActivityMainBinding binding;
    FirebaseAuth auth;

    FirebaseDatabase database;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        getSupportActionBar().hide();

        auth = FirebaseAuth.getInstance();
        database = FirebaseDatabase.getInstance();

        binding.viewPager.setAdapter(new FragementsAdapter(getSupportFragmentManager()));
        binding.tablayout.setupWithViewPager(binding.viewPager);


        binding.menuId.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Create a PopupMenu
                PopupMenu popupMenu = new PopupMenu(MainActivity.this, binding.menuId);
                popupMenu.getMenuInflater().inflate(R.menu.menu, popupMenu.getMenu());

                // Set a click listener for menu items
                popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick(android.view.MenuItem item) {
                        switch (item.getItemId()) {
                            case R.id.new_groupchat: {
                                Toast.makeText(MainActivity.this, "New Group", Toast.LENGTH_SHORT).show();
                                break;
                            }
                            case R.id.new_broadcast: {
                                Toast.makeText(MainActivity.this, "New Broadcast", Toast.LENGTH_SHORT).show();
                                break;
                            }
                            case R.id.whatsapp_web: {
                                Toast.makeText(MainActivity.this, "whatsapp web", Toast.LENGTH_SHORT).show();
                                break;
                            }
                            case R.id.starred_message: {
                                Toast.makeText(MainActivity.this, "starred message", Toast.LENGTH_SHORT).show();
                                break;
                            }
                            case R.id.settings: {
                                Intent intent = new Intent(MainActivity.this, SettingsActivity.class);
                                startActivity(intent);
                                break;
                            }
                            case R.id.logout: {
                                auth.signOut();
                                Intent intent = new Intent(MainActivity.this, SignInActivity.class);
                                startActivity(intent);
                                break;
                            }
                            default:
                                return false;
                        }
                        return true;
                    }
                });

                popupMenu.show();
            }
        });

    }

   /* public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu, menu);
        return super.onCreateOptionsMenu(menu);*/


    private long lastOnlineTime = 0;

    @Override
    protected void onResume() {
        super.onResume();
        String currentId = FirebaseAuth.getInstance().getUid();
        DatabaseReference presenceRef = FirebaseDatabase.getInstance().getReference().child("presence").child(currentId);

        // Get the last online time from the database
        presenceRef.child("lastOnline").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    lastOnlineTime = snapshot.getValue(Long.class);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                // Handle error
            }
        });

        // Set the current online status
        presenceRef.setValue("Online");
    }

    @Override
    protected void onPause() {
        super.onPause();
        String currentId = FirebaseAuth.getInstance().getUid();
        DatabaseReference presenceRef = FirebaseDatabase.getInstance().getReference().child("presence").child(currentId);

        // Set the last online time as the current timestamp if the user was previously online
        if (lastOnlineTime > 0) {
            Map<String, Object> timestamp = new HashMap<>();
            timestamp.put("lastOnline", lastOnlineTime);

            presenceRef.setValue(timestamp);
        } else {
            presenceRef.setValue("Offline");
        }
    }
}