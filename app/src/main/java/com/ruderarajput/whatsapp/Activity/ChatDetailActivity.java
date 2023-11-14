package com.ruderarajput.whatsapp.Activity;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Rect;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.view.ViewTreeObserver;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.ruderarajput.whatsapp.Adapter.MessageAdapter;
import com.ruderarajput.whatsapp.Model.MessagesModel;
import com.ruderarajput.whatsapp.R;
import com.ruderarajput.whatsapp.databinding.ActivityChatDetailBinding;
import com.squareup.picasso.Picasso;
import com.vanniktech.emoji.EmojiPopup;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;


public class ChatDetailActivity extends AppCompatActivity {

    private final String TAG = "ChatDetailActivity";
    private ActivityChatDetailBinding binding;
    private MessageAdapter adapter;
    private ArrayList<MessagesModel> messages;
    private String senderRoom, receiverRoom;
    private FirebaseDatabase database;
    private ProgressDialog dialog;
    private String senderUid;
    private String receiverUid;
    private DatabaseReference databaseReference;
    private FirebaseStorage firebaseStorage;
    private ProgressDialog progressDialog;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityChatDetailBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        EmojiPopup popup = EmojiPopup.Builder.fromRootView(binding.rootView)
                .build(binding.etMsg);

        binding.emoji.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                popup.toggle(); // Toggle the emoji keyboard
            }
        });


        getSupportActionBar().hide();


        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Sending...");
        progressDialog.setCancelable(false);

        database = FirebaseDatabase.getInstance();
        dialog = new ProgressDialog(this);
        dialog.setMessage("Uploading image...");
        dialog.setCancelable(false);


        messages = new ArrayList<>();

        String name = getIntent().getStringExtra("name");
        String profile = getIntent().getStringExtra("profileImage");
        receiverUid = getIntent().getStringExtra("userId");
        senderUid = FirebaseAuth.getInstance().getUid();
        binding.userName.setText(name);
        Picasso.get()
                .load(profile)
                .placeholder(R.drawable.profile_icon)
                .into(binding.profileImage);

        senderRoom = senderUid + receiverUid;
        receiverRoom = receiverUid + senderUid;

        database.getReference().child("presence").child(receiverUid).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    String lastSeen = snapshot.getValue(String.class);
                    if (!lastSeen.isEmpty()) {
                        if (lastSeen.equals("Offline")) {
                            binding.lastSeen.setVisibility(View.GONE);
                        } else if (lastSeen.equals("lastOnline")) {
                            binding.lastSeen.setVisibility(View.VISIBLE);
                        } else {
                            binding.lastSeen.setText(lastSeen);
                            binding.lastSeen.setVisibility(View.VISIBLE);
                        }
                    }
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        firebaseStorage = FirebaseStorage.getInstance();
        adapter = new MessageAdapter(this, messages, senderRoom, receiverRoom);
        binding.recviewChating.setAdapter(adapter);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        binding.recviewChating.setLayoutManager(layoutManager);
        layoutManager.setStackFromEnd(false);
        binding.recviewChating.setNestedScrollingEnabled(false);
        binding.recviewChating.setHasFixedSize(false);


        binding.backLinear.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(ChatDetailActivity.this, MainActivity.class);
                startActivity(intent);
                finish();
            }
        });

        binding.audiocallChat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                databaseReference = database.getReference().child("users");
                databaseReference.child(receiverUid).addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if (snapshot.exists()) {
                            String phoneNumber = snapshot.child("phoneNumber").getValue(String.class);
                            callUser(phoneNumber);
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
            }
        });

        binding.recviewChating.postDelayed(new Runnable() {
            @Override
            public void run() {
                binding.recviewChating.scrollToPosition(adapter.getItemCount() - 1);
            }
        }, 100);

        database.getReference().child("chats")
                .child(senderRoom)
                .child("messages")
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        messages.clear();
                        for (DataSnapshot snapshot1 : snapshot.getChildren()) {
                            MessagesModel message = snapshot1.getValue(MessagesModel.class);
                            message.setMessageId(snapshot1.getKey());
                            messages.add(message);
                        }

                        adapter.notifyDataSetChanged();
                        binding.recviewChating.scrollToPosition(adapter.getItemCount() - 1);
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });


        binding.getRoot().getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                Rect r = new Rect();
                binding.getRoot().getWindowVisibleDisplayFrame(r);
                int screenHeight = binding.getRoot().getRootView().getHeight();
                int keypadHeight = screenHeight - r.bottom;

                if (keypadHeight > screenHeight * 0.15) {
                    binding.recviewChating.smoothScrollToPosition(adapter.getItemCount());
                } else {
                }
            }
        });

        final Handler hanlder = new Handler();
        binding.etMsg.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                database.getReference().child("presence").child(senderUid).setValue("typing...");
                hanlder.removeCallbacksAndMessages(null);
                hanlder.postDelayed(userStoppedTyping, 2000);

            }

            Runnable userStoppedTyping = new Runnable() {
                @Override
                public void run() {
                    database.getReference().child("presence").child(senderUid).setValue("Online");
                }
            };
        });

        binding.cameraSentPhoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent pickIntent = new Intent(Intent.ACTION_PICK);
                pickIntent.setType("image/* video/*");

                Intent chooserIntent = Intent.createChooser(pickIntent, "Select File");
                startActivityForResult(chooserIntent, 25);
            }
        });


        binding.sendBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String messageTxt = binding.etMsg.getText().toString();

                if (!messageTxt.isEmpty()) {

                    Date date = new Date();
                    MessagesModel message = new MessagesModel(messageTxt, senderUid, receiverUid, date.getTime());
                    binding.etMsg.setText("");

                    String randomKey = database.getReference().push().getKey();

                    HashMap<String, Object> lastMsgObj = new HashMap<>();
                    lastMsgObj.put("lastMsg", message.getMessage());
                    lastMsgObj.put("lastMsgTime", date.getTime());

                    database.getReference().child("chats").child(senderRoom).updateChildren(lastMsgObj);
                    database.getReference().child("chats").child(receiverRoom).updateChildren(lastMsgObj);

                    database.getReference().child("chats")
                            .child(senderRoom)
                            .child("messages")
                            .child(randomKey)
                            .setValue(message)
                            .addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    if (task.isSuccessful()) {
                                        database.getReference().child("chats")
                                                .child(receiverRoom)
                                                .child("messages")
                                                .child(randomKey)
                                                .setValue(message)
                                                .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                    @Override
                                                    public void onComplete(@NonNull Task<Void> task) {
                                                        if (task.isSuccessful()) {

                                                        } else {

                                                        }
                                                    }
                                                });
                                    } else {
                                    }
                                }
                            });
                }
            }
        });
    }

    private void callUser(String phoneNumber) {
        if (!phoneNumber.isEmpty()) {
            if (ContextCompat.checkSelfPermission(this, android.Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.CALL_PHONE}, 235);
            } else {
                String dial = "tel:" + phoneNumber;
                startActivity(new Intent(Intent.ACTION_CALL, Uri.parse(dial)));
            }
        } else {
            Toast.makeText(this, "Invalid Your Phone Number! Please Try Again.", Toast.LENGTH_LONG).show();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == 25) {
            if (resultCode == RESULT_OK && data != null && data.getData() != null) {
                Uri selectedFile = data.getData();
                String fileType = getContentResolver().getType(selectedFile);

                if (fileType != null && fileType.startsWith("image/")) {
                    Calendar calendar = Calendar.getInstance();
                    StorageReference reference = firebaseStorage.getReference().child("chats").child(calendar.getTimeInMillis() + "");
                    progressDialog.show();
                    reference.putFile(selectedFile).addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {
                            progressDialog.dismiss();
                            if (task.isSuccessful()) {
                                reference.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                    @Override
                                    public void onSuccess(Uri uri) {
                                        String imageUrl = uri.toString();

                                        String messageTxt = binding.etMsg.getText().toString();

                                        if (!imageUrl.isEmpty()) {

                                            Date date = new Date();
                                            MessagesModel message = new MessagesModel(messageTxt, senderUid, receiverUid, date.getTime());
                                            message.setMessage("photo");
                                            message.setImageUrl(imageUrl);
                                            binding.etMsg.setText("");

                                            String randomKey = database.getReference().push().getKey();

                                            HashMap<String, Object> lastMsgObj = new HashMap<>();
                                            lastMsgObj.put("lastMsg", message.getMessage());
                                            lastMsgObj.put("lastMsgTime", date.getTime());

                                            database.getReference().child("chats").child(senderRoom).updateChildren(lastMsgObj);
                                            database.getReference().child("chats").child(receiverRoom).updateChildren(lastMsgObj);

                                            database.getReference().child("chats")
                                                    .child(senderRoom)
                                                    .child("messages")
                                                    .child(randomKey)
                                                    .setValue(message)
                                                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                        @Override
                                                        public void onComplete(@NonNull Task<Void> task) {
                                                            if (task.isSuccessful()) {
                                                                database.getReference().child("chats")
                                                                        .child(receiverRoom)
                                                                        .child("messages")
                                                                        .child(randomKey)
                                                                        .setValue(message)
                                                                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                                            @Override
                                                                            public void onComplete(@NonNull Task<Void> task) {
                                                                                if (task.isSuccessful()) {

                                                                                } else {

                                                                                }
                                                                            }
                                                                        });
                                                            } else {
                                                            }
                                                        }
                                                    });
                                        }


                                    }
                                });
                            }
                        }
                    });
                } else if (fileType != null && fileType.startsWith("video/")) {
                    // Video File
                    Calendar calendar = Calendar.getInstance();
                    StorageReference reference = firebaseStorage.getReference().child("chats").child(calendar.getTimeInMillis() + "");
                    progressDialog.show();
                    reference.putFile(selectedFile).addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {
                            progressDialog.dismiss();
                            if (task.isSuccessful()) {
                                reference.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                    @Override
                                    public void onSuccess(Uri uri) {
                                        String videoUrl = uri.toString();

                                        String messageTxt = binding.etMsg.getText().toString();

                                        if (!videoUrl.isEmpty()) {
                                            Date date = new Date();
                                            MessagesModel message = new MessagesModel(messageTxt, senderUid, receiverUid, date.getTime());
                                            message.setMessage("video");
                                            message.setVideoUrl(videoUrl);
                                            binding.etMsg.setText("");

                                            String randomKey = database.getReference().push().getKey();

                                            HashMap<String, Object> lastMsgObj = new HashMap<>();
                                            lastMsgObj.put("lastMsg", message.getMessage());
                                            lastMsgObj.put("lastMsgTime", date.getTime());

                                            database.getReference().child("chats").child(senderRoom).updateChildren(lastMsgObj);
                                            database.getReference().child("chats").child(receiverRoom).updateChildren(lastMsgObj);

                                            database.getReference().child("chats")
                                                    .child(senderRoom)
                                                    .child("messages")
                                                    .child(randomKey)
                                                    .setValue(message)
                                                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                        @Override
                                                        public void onComplete(@NonNull Task<Void> task) {
                                                            if (task.isSuccessful()) {
                                                                database.getReference().child("chats")
                                                                        .child(receiverRoom)
                                                                        .child("messages")
                                                                        .child(randomKey)
                                                                        .setValue(message)
                                                                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                                            @Override
                                                                            public void onComplete(@NonNull Task<Void> task) {
                                                                                if (task.isSuccessful()) {

                                                                                } else {
                                                                                    // Failed to send video message
                                                                                }
                                                                            }
                                                                        });
                                                            } else {
                                                                // Failed to send video message
                                                            }
                                                        }
                                                    });
                                        }
                                    }
                                }).addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception e) {
                                        // Failed to get video download URL
                                    }
                                });
                            } else {
                                // Failed to upload video file
                            }
                        }
                    });

                }
            } else {
            }
        }
    }

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

