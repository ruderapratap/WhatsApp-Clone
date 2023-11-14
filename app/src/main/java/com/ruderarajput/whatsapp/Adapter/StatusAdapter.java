package com.ruderarajput.whatsapp.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.FragmentActivity;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.firebase.database.FirebaseDatabase;
import com.ruderarajput.whatsapp.Model.Status;
import com.ruderarajput.whatsapp.Model.UserStatus;
import com.ruderarajput.whatsapp.R;
import com.ruderarajput.whatsapp.databinding.SampleGreenStatusBinding;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

import omari.hamza.storyview.StoryView;
import omari.hamza.storyview.callback.StoryClickListeners;
import omari.hamza.storyview.model.MyStory;

public class StatusAdapter extends RecyclerView.Adapter<StatusAdapter.StatusViewHolder> {
    private Context context;
    private ArrayList<UserStatus> userStatuses;

    public StatusAdapter(Context context, ArrayList<UserStatus> userStatuses) {
        this.context = context;
        this.userStatuses = userStatuses;
    }


    @NonNull
    @Override
    public StatusViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.sample_green_status, parent, false);
        return new StatusViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull StatusViewHolder holder, int position) {
        UserStatus userStatus = userStatuses.get(position);
        Status lastStatus = userStatus.getStatuses().get(userStatus.getStatuses().size() - 1);
        Glide.with(context).load(lastStatus.getImageUrl()).placeholder(R.drawable.profile_icon).into(holder.binding.image);
        holder.binding.circularStatusView2.setPortionsCount(userStatus.getStatuses().size());

        for (Status status : userStatus.getStatuses()) {
            holder.binding.statusName.setText(userStatus.getName());
            SimpleDateFormat sdf = new SimpleDateFormat("hh:mm a", Locale.getDefault());
            String formattedTimestamp = sdf.format(new Date(lastStatus.getTimestamp()));
            holder.binding.statusTime.setText(formattedTimestamp);
            Glide.with(context).load(status.getImageUrl()).placeholder(R.drawable.profile_icon).into(holder.binding.image);
        }


        int colorResId = userStatus.isSeen() ? R.color.grey : R.color.whatsappp_500;
        int color = ContextCompat.getColor(context, colorResId);
        holder.binding.circularStatusView2.setPortionsColor(color);


        SimpleDateFormat sdf = new SimpleDateFormat("hh:mm a", Locale.getDefault());
        String formattedTimestamp = sdf.format(new Date(lastStatus.getTimestamp()));

        holder.binding.circularStatusView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ArrayList<MyStory> myStories = new ArrayList<>();
                for (Status status : userStatus.getStatuses()) {
                    myStories.add(new MyStory(status.getImageUrl()));
                }
                new StoryView.Builder(((FragmentActivity) context).getSupportFragmentManager())
                        .setStoriesList(myStories)
                        .setStoryDuration(5000)
                        .setTitleText(userStatus.getName())
                        .setSubtitleText(formattedTimestamp)
                        .setTitleLogoUrl(userStatus.getProfileImage())
                        .setStoryClickListeners(new StoryClickListeners() {
                            @Override
                            public void onDescriptionClickListener(int position) {
                                // Your action
                            }

                            @Override
                            public void onTitleIconClickListener(int position) {
                                // Your action
                            }
                        })
                        .build()
                        .show();


                // Mark the status as seen
                userStatus.setSeen(true);

               FirebaseDatabase.getInstance().getReference("userStatuses")
                        .child(userStatus.getName())
                        .setValue(userStatus);
                notifyDataSetChanged();
            }
        });
    }

    @Override
    public int getItemCount() {
        return userStatuses.size();
    }

    public class StatusViewHolder extends RecyclerView.ViewHolder {
        private SampleGreenStatusBinding binding;

        public StatusViewHolder(@NonNull View itemView) {
            super(itemView);
            binding = SampleGreenStatusBinding.bind(itemView);
        }
    }
}

