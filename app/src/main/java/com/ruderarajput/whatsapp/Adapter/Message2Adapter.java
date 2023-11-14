package com.ruderarajput.whatsapp.Adapter;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.ruderarajput.whatsapp.Activity.ChatDetailActivity;
import com.ruderarajput.whatsapp.Model.MessagesModel;
import com.ruderarajput.whatsapp.Model.User;
import com.ruderarajput.whatsapp.R;
import com.ruderarajput.whatsapp.databinding.SampleMessageuserBinding;

import java.util.ArrayList;

public class Message2Adapter extends RecyclerView.Adapter<Message2Adapter.UsersViewHolder> {
    private Context context;
    private ArrayList<User> users;

    public Message2Adapter(Context context, ArrayList<User> users) {
        this.context = context;
        this.users = users;
    }

    @NonNull
    @Override
    public UsersViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.sample_messageuser,parent, false);
        return new UsersViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull UsersViewHolder holder, int position) {
        User user=users.get(position);

        holder.binding.userName.setText(user.getName());
        holder.binding.lastMessage.setText(user.getAbout());

        holder.binding.userName.setText(user.getName());
        Glide.with(context).load(user.getProfileImage())
                .placeholder(R.drawable.profile_icon)
                .into(holder.binding.profileImage);

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(context, ChatDetailActivity.class);
                intent.putExtra("name", user.getName());
                intent.putExtra("phoneNumber", user.getPhoneNumber());
                intent.putExtra("profileImage", user.getProfileImage());
                intent.putExtra("userId", user.getUid());
                context.startActivity(intent);
            }
        });
    }

    @Override
    public int getItemCount() {
        return users.size();
    }

    public static class UsersViewHolder extends RecyclerView.ViewHolder {
        SampleMessageuserBinding binding;

        public UsersViewHolder(@NonNull View itemView) {
            super(itemView);
            binding = SampleMessageuserBinding.bind(itemView);
        }
    }
}