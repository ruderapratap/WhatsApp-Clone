package com.ruderarajput.whatsapp.Adapter;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.ImageView;
import android.widget.ProgressBar;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.ruderarajput.whatsapp.Activity.ChatDetailActivity;
import com.ruderarajput.whatsapp.Model.User;
import com.ruderarajput.whatsapp.R;
import com.ruderarajput.whatsapp.databinding.SampleShowUserBinding;

import java.util.ArrayList;


    public class UserAdapter extends RecyclerView.Adapter<UserAdapter.UsersViewHolder> {
        private Context context;
        private ArrayList<User> users;

        public UserAdapter(Context context, ArrayList<User> users) {
            this.context = context;
            this.users = users;
        }

        @NonNull
        @Override
        public UsersViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(context).inflate(R.layout.sample_show_user,parent, false);
            return new UsersViewHolder(view);
        }

    @Override
    public void onBindViewHolder(@NonNull UsersViewHolder holder, int position) {
        User user=users.get(position);
        UsersViewHolder viewHolder=(UsersViewHolder) holder;


        holder.binding.userName.setText(user.getName());
        holder.binding.lastMessage.setText(user.getAbout());

        holder.binding.userName.setText(user.getName());
        Glide.with(context).load(user.getProfileImage())
                .placeholder(R.drawable.profile_icon)
                .into(holder.binding.profileImage);

        viewHolder.binding.profileImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openFullScreenImage(user.getProfileImage());
            }
        });

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
        private void openFullScreenImage(String imageUrl) {
            Dialog dialog = new Dialog(context, android.R.style.Theme_Black_NoTitleBar_Fullscreen);
            dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
            dialog.setContentView(R.layout.photo_users);

            ImageView fullscreenImageView = dialog.findViewById(R.id.fullscreenImageView);
            ProgressBar progressBar = dialog.findViewById(R.id.progressBar);

            Glide.with(context)
                    .load(imageUrl)
                    .placeholder(R.drawable.profile_icon) // लोड होते समय प्लेसहोल्डर छवि
                    .into(fullscreenImageView);

            dialog.show();
        }
    @Override
    public int getItemCount() {
        return users.size();
    }

    public static class UsersViewHolder extends RecyclerView.ViewHolder {
        SampleShowUserBinding binding;

        public UsersViewHolder(@NonNull View itemView) {
            super(itemView);
            binding = SampleShowUserBinding.bind(itemView);
        }
    }
}


