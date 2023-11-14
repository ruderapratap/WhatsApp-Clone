package com.ruderarajput.whatsapp.Activity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.github.dhaval2404.imagepicker.ImagePicker;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.ruderarajput.whatsapp.Model.User;
import com.ruderarajput.whatsapp.databinding.ActivitySetupProfileBinding;

public class SetupProfileActivity extends AppCompatActivity {

    private static final String TAG="SetupProfileActivity";

    private ActivitySetupProfileBinding binding;
    String cameraPermission[];
    String storagePermission[];
    private FirebaseAuth auth;
    private FirebaseDatabase database;
    private FirebaseStorage storage;
    private Uri selectedImage;
    private ProgressDialog dialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding=ActivitySetupProfileBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());


        auth=FirebaseAuth.getInstance();
        database=FirebaseDatabase.getInstance();
        storage=FirebaseStorage.getInstance();
        getSupportActionBar().hide();

        dialog=new ProgressDialog(this);
        dialog.setMessage("Updating Profile...");
        dialog.setCancelable(false);

        binding.profileUserImg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ImagePicker.with(SetupProfileActivity.this).crop()                    //Crop image(Optional), Check Customization for more option
                        .compress(1024)            //Final image size will be less than 1 MB(Optional)
                        .maxResultSize(1080, 1080)    //Final image resolution will be less than 1080 x 1080(Optional)
                        .start();
            }
        });
        binding.setupProfileBtn.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                dialog.show();
                String name=binding.NameBox.getText().toString();
                String about=binding.aboutBox.getText().toString();
                if(selectedImage!=null && !name.isEmpty() && !about.isEmpty()) {
                    StorageReference reference = storage.getReference().child("Profiles").child(auth.getUid());
                    reference.putFile(selectedImage).addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {
                            if (task.isSuccessful()) {
                                reference.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                    @Override
                                    public void onSuccess(Uri uri) {
                                        String imageUrl = uri.toString();
                                        String uid = auth.getUid();
                                        String name = binding.NameBox.getText().toString();
                                        String about = binding.aboutBox.getText().toString();
                                        String phone = auth.getCurrentUser().getPhoneNumber();


                                        User user = new User(uid, name, about, imageUrl, phone);
                                        database.getReference()
                                                .child("users")
                                                .child(uid)
                                                .setValue(user)
                                                .addOnSuccessListener(new OnSuccessListener<Void>() {
                                                    @Override
                                                    public void onSuccess(Void unused) {
                                                        dialog.dismiss();
                                                        Intent intent = new Intent(SetupProfileActivity.this, MainActivity.class);
                                                        startActivity(intent);
                                                        finish();
                                                    }
                                                });
                                    }
                                });
                            }
                        }
                    });
                  }else if (selectedImage!=null && !name.isEmpty()){
                        StorageReference reference=storage.getReference().child("Profiles").child(auth.getUid());
                        reference.putFile(selectedImage).addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
                            @Override
                            public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {
                                if(task.isSuccessful()){
                                    reference.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                        @Override
                                        public void onSuccess(Uri uri) {
                                            String imageUrl=uri.toString();
                                            String uid=auth.getUid();
                                            String name=binding.NameBox.getText().toString();
                                            String phone=auth.getCurrentUser().getPhoneNumber();


                                            User user=new User(uid,name,"Hey there! I am using Whatsapp.",imageUrl,phone);
                                            database.getReference()
                                                    .child("users")
                                                    .child(uid)
                                                    .setValue(user)
                                                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                                                        @Override
                                                        public void onSuccess(Void unused) {
                                                            dialog.dismiss();
                                                            Intent intent=new Intent(SetupProfileActivity.this, MainActivity.class);
                                                            startActivity(intent);
                                                            finish();
                                                        }
                                                    });
                                        }
                                    });
                                }
                            }
                        });
                }else if(!name.isEmpty() && !about.isEmpty()) {
                    String uid = auth.getUid();
                    String phone = auth.getCurrentUser().getPhoneNumber();

                    User user = new User(uid, name, about,"No Image", phone);
                    database.getReference()
                            .child("users")
                            .child(uid)
                            .setValue(user)
                            .addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void unused) {
                                    dialog.dismiss();
                                    Intent intent = new Intent(SetupProfileActivity.this, MainActivity.class);
                                    startActivity(intent);
                                    finish();
                                }
                            });
                      }else if(!name.isEmpty()){
                        String uid=auth.getUid();
                        String phone=auth.getCurrentUser().getPhoneNumber();


                        User user=new User(uid,name,"Hey there! I am using Whatsapp.","No Image",phone);
                        database.getReference()
                                .child("users")
                                .child(uid)
                                .setValue(user)
                                .addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void unused) {
                                        dialog.dismiss();
                                        Intent intent=new Intent(SetupProfileActivity.this,MainActivity.class);
                                        startActivity(intent);
                                        finish();
                                    }
                                });
                }else{
                    dialog.dismiss();
                    Toast.makeText(SetupProfileActivity.this, "Please Fill Your Full Name.", Toast.LENGTH_SHORT).show();
                }
            }
        });

    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(data!=null){
            if(data.getData()!=null){
                binding.profileUserImg.setImageURI(data.getData());
                selectedImage=data.getData();
            }
        }
    }
    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }
}