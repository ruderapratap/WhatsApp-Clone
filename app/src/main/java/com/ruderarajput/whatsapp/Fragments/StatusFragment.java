package com.ruderarajput.whatsapp.Fragments;

import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnCompleteListener;
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
import com.ruderarajput.whatsapp.Adapter.StatusAdapter;
import com.ruderarajput.whatsapp.Model.Status;
import com.ruderarajput.whatsapp.Model.User;
import com.ruderarajput.whatsapp.Model.UserStatus;
import com.ruderarajput.whatsapp.databinding.FragmentStatusBinding;
import com.ruderarajput.whatsapp.utils.MySharedPreferences;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

public class StatusFragment extends Fragment {
    private static final String PREFS_NAME = "UserPrefs";
    private static final String STATUS_SEEN_KEY = "isStatusSeen";

    private FragmentStatusBinding binding;
    private StatusAdapter adapter;
    private ArrayList<UserStatus> userStatuses;
    private FirebaseDatabase database;
    private ProgressDialog dialog;
    private User user;
    private MySharedPreferences sharedPreferences;
    private Map<String, Boolean> seenStatusesMap; // Store seen statuses locally

    private DatabaseReference seenStatusRef; // Reference for tracking seen statuses

    public StatusFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        database = FirebaseDatabase.getInstance();
        userStatuses = new ArrayList<>();
        //sharedPreferences = (MySharedPreferences) PreferenceManager.getDefaultSharedPreferences(getContext());
        sharedPreferences = new MySharedPreferences(requireContext());
        seenStatusesMap = new HashMap<>();

        database.getReference().child("users").child(FirebaseAuth.getInstance().getUid())
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        user = snapshot.getValue(User.class);
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                    }
                });

        dialog = new ProgressDialog(getContext());
        dialog.setMessage("Uploading Status...");
        dialog.setCancelable(false);

        seenStatusRef = database.getReference().child("seenStatuses").child(FirebaseAuth.getInstance().getUid());
        seenStatusRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    seenStatusesMap = (Map<String, Boolean>) snapshot.getValue();
                    updateSeenStatuses();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }
        });
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentStatusBinding.inflate(inflater, container, false);
        RecyclerView statusRecview = binding.statusRecview;
        LinearLayoutManager layoutManager = new LinearLayoutManager(getContext());
        statusRecview.setLayoutManager(layoutManager);

        adapter = new StatusAdapter(getContext(), userStatuses);
        statusRecview.setAdapter(adapter);

        database.getReference().child("stories").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    userStatuses.clear();
                    for (DataSnapshot storySnapshot : snapshot.getChildren()) {
                        UserStatus userStatus = new UserStatus();
                        userStatus.setName(storySnapshot.child("name").getValue(String.class));
                        userStatus.setProfileImage(storySnapshot.child("profileImage").getValue(String.class));
                        userStatus.setLastUpdated(storySnapshot.child("lastUpdated").getValue(long.class));
                        userStatus.setSeen(false);

                        ArrayList<Status> statuses = new ArrayList<>();

                        for (DataSnapshot statusSnapshot : storySnapshot.child("statuses").getChildren()) {
                            Status sampleStatus = statusSnapshot.getValue(Status.class);
                            statuses.add(sampleStatus);
                        }
                        userStatus.setStatuses(statuses);
                        userStatuses.add(userStatus);
                    }
                    adapter.notifyDataSetChanged();

                    updateSeenStatuses();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }
        });

        binding.updateStatus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                uploadStatus();
            }
        });

        return binding.getRoot();
    }

    private void uploadStatus() {
        Intent intent = new Intent();
        intent.setAction(Intent.ACTION_GET_CONTENT);
        intent.setType("image/*");
        startActivityForResult(intent, 75);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (data != null) {
            dialog.show();
            FirebaseStorage storage = FirebaseStorage.getInstance();
            Date date = new Date();
            StorageReference reference = storage.getReference().child("status")
                    .child(date.getTime() + "");

            reference.putFile(data.getData())
                    .addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {
                            if (task.isSuccessful()) {
                                reference.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                    @Override
                                    public void onSuccess(Uri uri) {
                                        UserStatus userStatus = new UserStatus();
                                        userStatus.setName(user.getName());
                                        userStatus.setProfileImage(user.getProfileImage());
                                        userStatus.setLastUpdated(date.getTime());
                                        userStatus.setSeen(false);

                                        HashMap<String, Object> obj = new HashMap<>();
                                        obj.put("name", userStatus.getName());
                                        obj.put("profileImage", userStatus.getProfileImage());
                                        obj.put("lastUpdated", userStatus.getLastUpdated());

                                        String imageUrl = uri.toString();
                                        Status status = new Status(imageUrl, userStatus.getLastUpdated());

                                        DatabaseReference storyRef = database.getReference().child("stories")
                                                .child(FirebaseAuth.getInstance().getUid());

                                        storyRef.updateChildren(obj);

                                        String pushId = storyRef.child("statuses").push().getKey();
                                        storyRef.child("statuses").child(pushId).setValue(status);

                                        // Schedule automatic deletion after 24 hours
                                        scheduleDeletion(pushId, System.currentTimeMillis() + TimeUnit.MINUTES.toMillis(1));

                                        dialog.dismiss();
                                    }
                                });
                            }
                        }
                    });
        }
    }

    private void scheduleDeletion(String pushId, long deletionTime) {
        DatabaseReference deletionRef = database.getReference().child("deletionStatuses")
                .child(FirebaseAuth.getInstance().getUid()).child(pushId);

        deletionRef.setValue(deletionTime);
    }
    private void updateSeenStatuses() {
         // Mark the statuses as seen in the adapter
         if (seenStatusesMap != null) {
             for (UserStatus userStatus : userStatuses) {
                 if (seenStatusesMap.containsKey(userStatus.getName()) && seenStatusesMap.get(userStatus.getName())) {
                     userStatus.setSeen(true);
                 }
             }
         }

        // Notify the adapter of the data change
        adapter.notifyDataSetChanged();
    }

    @Override
    public void onResume() {
        super.onResume();

        // Save the seen status preference as true
        sharedPreferences.putBoolean(PREFS_NAME, STATUS_SEEN_KEY, true);
    }

    @Override
    public void onStop() {
        super.onStop();

        // Save the seen status preference as false
        sharedPreferences.putBoolean(PREFS_NAME, STATUS_SEEN_KEY, false);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}


