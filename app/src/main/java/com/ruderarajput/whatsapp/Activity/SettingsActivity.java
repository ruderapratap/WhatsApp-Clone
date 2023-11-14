package com.ruderarajput.whatsapp.Activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.ruderarajput.whatsapp.R;
import com.ruderarajput.whatsapp.databinding.ActivitySettingsBinding;
import com.squareup.picasso.Picasso;

public class SettingsActivity extends AppCompatActivity {

    private ActivitySettingsBinding binding;
    private FirebaseDatabase database;
    private FirebaseUser firebaseUser;
    private  DatabaseReference usersRef;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding=ActivitySettingsBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        getSupportActionBar().hide();
        firebaseUser= FirebaseAuth.getInstance().getCurrentUser();
        database = FirebaseDatabase.getInstance();
         usersRef = database.getReference("users");

        AlertDialog.Builder builder = new AlertDialog.Builder(SettingsActivity.this);
         binding.prfileChanging.setOnClickListener(new View.OnClickListener() {
             @Override
             public void onClick(View view) {
                 builder.setTitle("Upadate Profile");
                 builder.setMessage("update your photo and status and name.");
                 builder.setPositiveButton("Update", new DialogInterface.OnClickListener() {
                     public void onClick(DialogInterface dialog, int which) {
                       Intent intent=new Intent(SettingsActivity.this,SetupProfileActivity.class);
                       startActivity(intent);
                       finish();
                     }
                 });
                 builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                     public void onClick(DialogInterface dialog, int which) {
                         Intent intent=new Intent(SettingsActivity.this,MainActivity.class);
                         startActivity(intent);
                         finish();
                     }
                 });
                 AlertDialog dialog = builder.create();
                 dialog.show();
             }
         });

        binding.backarowSetting.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent=new Intent(SettingsActivity.this,MainActivity.class);
                startActivity(intent);
                finish();
            }
        });
        if (firebaseUser != null) {
            String uid = firebaseUser.getUid();

            DatabaseReference userRef = usersRef.child(uid);
            userRef.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    if (dataSnapshot.exists()) {
                        String profileImage= dataSnapshot.child("profileImage").getValue(String.class);
                        String name = dataSnapshot.child("name").getValue(String.class);
                        String about=dataSnapshot.child("about").getValue(String.class);
                        binding.tvUsername.setText(name);
                       if (!about.isEmpty() && profileImage!=null) {
                            binding.aboutSave.setText(about);
                            Picasso.get().load(profileImage).placeholder(R.drawable.profile_icon).into(binding.profileSaveImage);
                        } else if(about.isEmpty() && profileImage!=null){
                            binding.aboutSave.setText("Hey there! I am using WhatsApp.");
                            Picasso.get().load(profileImage).placeholder(R.drawable.profile_icon).into(binding.profileSaveImage);
                        }
                    }
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {
                }
            });
        }

        }
    }