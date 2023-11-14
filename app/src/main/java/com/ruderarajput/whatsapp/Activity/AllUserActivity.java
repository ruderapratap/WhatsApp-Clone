package com.ruderarajput.whatsapp.Activity;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.os.Build;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.ruderarajput.whatsapp.Adapter.UserAdapter;
import com.ruderarajput.whatsapp.Model.User;
import com.ruderarajput.whatsapp.databinding.ActivityAllUserBinding;

import java.util.ArrayList;
import java.util.HashSet;

public class AllUserActivity extends AppCompatActivity {
    private ActivityAllUserBinding binding;
    private UserAdapter userAdapter;
    private ArrayList<User> users;
    private FirebaseDatabase database;
    private DatabaseReference databaseReference;
    private RecyclerView.LayoutManager layoutManager;
    private HashSet<String> uniqueUsers;

    private static final int PERMISSIONS_REQUEST_READ_CONTACTS = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityAllUserBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        database = FirebaseDatabase.getInstance();
        users = new ArrayList<>();
        uniqueUsers = new HashSet<>();

        initializeRecyclerView();
        getPermissions();

        getSupportActionBar().hide();



        binding.searchBarIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                binding.showSearch.setVisibility(View.VISIBLE);
                binding.toolbar.setVisibility(View.GONE);
            }
        });
        binding.searchBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                binding.showSearch.setVisibility(View.GONE);
                binding.toolbar.setVisibility(View.VISIBLE);
            }
        });


        binding.backArrowBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(AllUserActivity.this, MainActivity.class);
                startActivity(intent);
                finish();
            }
        });
    }

    private void getContactList() {
        Cursor phones = getContentResolver().query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI, null, null, null, null);
        if (phones != null) {
            while (phones.moveToNext()) {
                @SuppressLint("Range") String name = phones.getString(phones.getColumnIndex(ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME));
                @SuppressLint("Range") String phoneNumber = phones.getString(phones.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));

                // Check if the user is already added to the set
                if (!uniqueUsers.contains(phoneNumber)) {
                    uniqueUsers.add(phoneNumber);
                    isUserRegistered(phoneNumber);
                }
            }
            phones.close();
        }
    }

    private void isUserRegistered(String phoneNumber) {
        databaseReference = database.getReference().child("users");
        databaseReference.orderByChild("phoneNumber").equalTo(phoneNumber)
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if (snapshot.exists()) {
                            for (DataSnapshot userSnapshot : snapshot.getChildren()) {
                                User user = userSnapshot.getValue(User.class);
                                users.add(user);
                            }
                            userAdapter.notifyDataSetChanged();
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
    }

    private void initializeRecyclerView() {
        binding.recviewAllusers.setNestedScrollingEnabled(false);
        binding.recviewAllusers.setHasFixedSize(false);
        layoutManager = new LinearLayoutManager(this);
        userAdapter = new UserAdapter(this, users);
        binding.recviewAllusers.setLayoutManager(layoutManager);
        binding.recviewAllusers.setAdapter(userAdapter);
    }

    private void getPermissions() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.READ_CONTACTS) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_CONTACTS}, PERMISSIONS_REQUEST_READ_CONTACTS);
            } else {
                getContactList();
            }
        } else {
            getContactList();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == PERMISSIONS_REQUEST_READ_CONTACTS) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                getContactList();
            } else {
                Toast.makeText(this, "Permission denied to access contacts", Toast.LENGTH_SHORT).show();
            }
        }
    }
}