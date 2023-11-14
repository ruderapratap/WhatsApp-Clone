package com.ruderarajput.whatsapp.Fragments;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.ruderarajput.whatsapp.Activity.AllUserActivity;
import com.ruderarajput.whatsapp.Adapter.UserAdapter;
import com.ruderarajput.whatsapp.Model.User;
import com.ruderarajput.whatsapp.databinding.FragmentChatsBinding;
import java.util.ArrayList;

public class ChatsFragment extends Fragment {
    private FragmentChatsBinding binding;
    private UserAdapter userAdapter;
    private ArrayList<User> users;
    private FirebaseDatabase database;
    private DatabaseReference databaseReference;
    private RecyclerView.LayoutManager layoutManager;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentChatsBinding.inflate(inflater, container, false);
        View view = binding.getRoot();

        database = FirebaseDatabase.getInstance();
        users = new ArrayList<>();

        initializeRecyclerView();
        fetchAllUsers();

        binding.alluserBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent=new Intent(getContext(), AllUserActivity.class);
                startActivity(intent);
            }
        });

        return view;
    }

    private void initializeRecyclerView() {
        binding.recyclerView.setNestedScrollingEnabled(false);
        binding.recyclerView.setHasFixedSize(false);
        layoutManager = new LinearLayoutManager(getContext());
        userAdapter = new UserAdapter(getContext(), users);
        binding.recyclerView.setLayoutManager(layoutManager);
        binding.recyclerView.setAdapter(userAdapter);
    }

    private void fetchAllUsers() {
        databaseReference = database.getReference().child("users");
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                users.clear();
                for (DataSnapshot userSnapshot : snapshot.getChildren()) {
                    User user = userSnapshot.getValue(User.class);
                    users.add(user);
                }
                userAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                // Handle database error
            }
        });
    }
}
