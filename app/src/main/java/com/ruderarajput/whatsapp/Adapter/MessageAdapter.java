package com.ruderarajput.whatsapp.Adapter;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.ImageView;
import android.widget.PopupWindow;
import android.widget.ProgressBar;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.github.pgreze.reactions.ReactionPopup;
import com.github.pgreze.reactions.ReactionsConfig;
import com.github.pgreze.reactions.ReactionsConfigBuilder;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.FirebaseDatabase;
import com.ruderarajput.whatsapp.Activity.VideoPlayerActivity;
import com.ruderarajput.whatsapp.Model.MessagesModel;
import com.ruderarajput.whatsapp.R;
import com.ruderarajput.whatsapp.databinding.SampleReciverBinding;
import com.ruderarajput.whatsapp.databinding.SampleSenderBinding;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

public class MessageAdapter extends RecyclerView.Adapter {

    Context context;
    ArrayList<MessagesModel> messages;

    final int ITEM_SENT = 1;
    final int ITEM_RECEIVE = 2;

    String senderRoom;
    String receiverRoom;
    String Uid;


    public MessageAdapter(Context context, ArrayList<MessagesModel> messages, String senderRoom, String receiverRoom) {
        this.context = context;
        this.messages = messages;
        this.senderRoom = senderRoom;
        this.receiverRoom = receiverRoom;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        if(viewType == ITEM_SENT) {
            View view = LayoutInflater.from(context).inflate(R.layout.sample_sender, parent, false);
            return new SentViewHolder(view);
        } else {
            View view = LayoutInflater.from(context).inflate(R.layout.sample_reciver, parent, false);
            return new ReceiverViewHolder(view);
        }
    }

    @Override
    public int getItemViewType(int position) {
        MessagesModel message = messages.get(position);
        if (FirebaseAuth.getInstance().getUid().equals(message.getSenderId())) {
            return ITEM_SENT;
        } else {
            return ITEM_RECEIVE;
        }
    }



    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        MessagesModel messageModel = messages.get(position);


        int[] reactions = {
                R.drawable.heart_emoji,
                R.drawable.heart_eyes,
                R.drawable.like,
                R.drawable.thinking,
                R.drawable.cool,
                R.drawable.weep,
                R.drawable.weep2,
                R.drawable.fire,
        };

        ReactionsConfig config = new ReactionsConfigBuilder(context)
                .withReactions(reactions)
                .build();

        ReactionPopup popup = new ReactionPopup(context, config, pos -> {
            if (pos >= 0 && pos < reactions.length) {
                if (holder instanceof SentViewHolder) {
                    SentViewHolder viewHolder = (SentViewHolder) holder;
                    viewHolder.binding.setEmoji.setImageResource(reactions[pos]);
                    viewHolder.binding.setEmoji.setVisibility(View.VISIBLE);
                } else if (holder instanceof ReceiverViewHolder) {
                    ReceiverViewHolder viewHolder = (ReceiverViewHolder) holder;
                    viewHolder.binding.setEmoji.setImageResource(reactions[pos]);
                    viewHolder.binding.setEmoji.setVisibility(View.VISIBLE);
                }
                messageModel.setFeeling(pos);

                FirebaseDatabase.getInstance().getReference()
                        .child("chats")
                        .child(senderRoom)
                        .child("messages")
                        .child(messageModel.getMessageId())
                        .setValue(messageModel);

                FirebaseDatabase.getInstance().getReference()
                        .child("chats")
                        .child(receiverRoom)
                        .child("messages")
                        .child(messageModel.getMessageId())
                        .setValue(messageModel);
            }
            return true;
        });

        if (holder instanceof SentViewHolder) {
            SentViewHolder viewHolder = (SentViewHolder) holder;


            if (messageModel.getMessage().equals("photo")) {
                viewHolder.binding.videoSentTime.setVisibility(View.GONE);
                 viewHolder.binding.videoViewCard.setVisibility(View.GONE);
                viewHolder.binding.videoViewCard2.setVisibility(View.GONE);
                viewHolder.binding.videoView.setVisibility(View.GONE);
                viewHolder.binding.message.setVisibility(View.GONE);
                viewHolder.binding.timestamp.setVisibility(View.GONE);
                viewHolder.binding.videoThumbnail.setVisibility(View.GONE);
                viewHolder.binding.playButton.setVisibility(View.GONE);
                SimpleDateFormat sdf = new SimpleDateFormat(" hh:mm a", Locale.getDefault());
                String formattedTimestamp = sdf.format(new Date(messageModel.getTimestamp()));
                viewHolder.binding.imageSentTime.setText(formattedTimestamp);
                viewHolder.binding.imageSentTime.setVisibility(View.VISIBLE);
                viewHolder.binding.imageViewCard.setVisibility(View.VISIBLE);
                viewHolder.binding.image.setVisibility(View.VISIBLE);
                Glide.with(context).load(messageModel.getImageUrl()).placeholder(R.drawable.img_1).into(viewHolder.binding.image);
                viewHolder.binding.image.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        openFullScreenImage(messageModel.getImageUrl());
                    }
                });

            }else if (messageModel.getMessage().equals("video")) {
                viewHolder.binding.imageSentTime.setVisibility(View.GONE);
                viewHolder.binding.image.setVisibility(View.GONE);
                viewHolder.binding.message.setVisibility(View.GONE);
                viewHolder.binding.timestamp.setVisibility(View.GONE);
                viewHolder.binding.imageViewCard.setVisibility(View.GONE);
                SimpleDateFormat sdf = new SimpleDateFormat(" hh:mm a", Locale.getDefault());
                String formattedTimestamp = sdf.format(new Date(messageModel.getTimestamp()));
                viewHolder.binding.videoSentTime.setText(formattedTimestamp);
                viewHolder.binding.videoSentTime.setVisibility(View.VISIBLE);
                viewHolder.binding.videoViewCard.setVisibility(View.VISIBLE);
                viewHolder.binding.videoViewCard2.setVisibility(View.VISIBLE);
                viewHolder.binding.playButton.setVisibility(View.VISIBLE);
                viewHolder.binding.videoThumbnail.setVisibility(View.VISIBLE);
                viewHolder.binding.videoView.setVisibility(View.VISIBLE);
                Glide.with(context).load(messageModel.getVideoUrl()).placeholder(R.drawable.img_2).into(viewHolder.binding.videoThumbnail);

                viewHolder.binding.playButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        // Play the video
                        Intent intent = new Intent(context, VideoPlayerActivity.class);
                        String videoUrl = messageModel.getVideoUrl();
                        intent.setData(Uri.parse(videoUrl));
                        context.startActivity(intent);

                    }
                });

            } else {
                viewHolder.binding.imageSentTime.setVisibility(View.GONE);
                viewHolder.binding.videoSentTime.setVisibility(View.GONE);
                viewHolder.binding.imageViewCard.setVisibility(View.GONE);
                viewHolder.binding.videoViewCard.setVisibility(View.GONE);
                viewHolder.binding.videoViewCard2.setVisibility(View.GONE);
                viewHolder.binding.videoView.setVisibility(View.GONE);
                viewHolder.binding.image.setVisibility(View.GONE);
                viewHolder.binding.videoThumbnail.setVisibility(View.GONE);
                viewHolder.binding.playButton.setVisibility(View.GONE);
                SimpleDateFormat sdf = new SimpleDateFormat(" hh:mm a", Locale.getDefault());
                String formattedTimestamp = sdf.format(new Date(messageModel.getTimestamp()));
                viewHolder.binding.timestamp.setText(formattedTimestamp);
                viewHolder.binding.timestamp.setVisibility(View.VISIBLE);
                viewHolder.binding.message.setVisibility(View.VISIBLE);
                viewHolder.binding.message.setText(messageModel.getMessage());
            }
            if (messageModel.getFeeling() >= 0) {
                viewHolder.binding.setEmoji.setImageResource(reactions[messageModel.getFeeling()]);
                viewHolder.binding.setEmoji.setVisibility(View.VISIBLE);
            } else {
                viewHolder.binding.setEmoji.setVisibility(View.GONE);
            }


            viewHolder.binding.relative.setOnLongClickListener(view -> {
                        view.setSelected(!view.isSelected());
                        popup.onTouch(view, MotionEvent.obtain(0, 0, MotionEvent.ACTION_DOWN, 0, 0, 0));
                        return true;
            });
            popup.setOnDismissListener(new PopupWindow.OnDismissListener() {
                @Override
                public void onDismiss() {
                    viewHolder.binding.relative.setSelected(false);
                }
            });

        } else if (holder instanceof ReceiverViewHolder) {
            ReceiverViewHolder viewHolder = (ReceiverViewHolder) holder;

            if(messageModel.getMessage().equals("photo")) {
                viewHolder.binding.videoSentTime.setVisibility(View.GONE);
                viewHolder.binding.videoViewCard.setVisibility(View.GONE);
                viewHolder.binding.videoViewCard2.setVisibility(View.GONE);
                viewHolder.binding.videoView.setVisibility(View.GONE);
                viewHolder.binding.videoThumbnail.setVisibility(View.GONE);
                viewHolder.binding.playButton.setVisibility(View.GONE);
                viewHolder.binding.timestamp.setVisibility(View.GONE);
                viewHolder.binding.message.setVisibility(View.GONE);
                SimpleDateFormat sdf = new SimpleDateFormat(" hh:mm a", Locale.getDefault());
                String formattedTimestamp = sdf.format(new Date(messageModel.getTimestamp()));
                viewHolder.binding.imageSentTime.setText(formattedTimestamp);
                viewHolder.binding.imageSentTime.setVisibility(View.VISIBLE);
                viewHolder.binding.imageViewCard.setVisibility(View.VISIBLE);
                viewHolder.binding.image.setVisibility(View.VISIBLE);
                Glide.with(context).load(messageModel.getImageUrl()).placeholder(R.drawable.img_1).into(viewHolder.binding.image);
                viewHolder.binding.image.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        openFullScreenImage(messageModel.getImageUrl());
                    }
                });
            } else if (messageModel.getMessage().equals("video")) {
                viewHolder.binding.imageSentTime.setVisibility(View.GONE);
                viewHolder.binding.imageViewCard.setVisibility(View.GONE);
                viewHolder.binding.image.setVisibility(View.GONE);
                viewHolder.binding.message.setVisibility(View.GONE);
                viewHolder.binding.timestamp.setVisibility(View.GONE);
                SimpleDateFormat sdf = new SimpleDateFormat(" hh:mm a", Locale.getDefault());
                String formattedTimestamp = sdf.format(new Date(messageModel.getTimestamp()));
                viewHolder.binding.videoSentTime.setText(formattedTimestamp);
                viewHolder.binding.videoSentTime.setVisibility(View.VISIBLE);
                viewHolder.binding.videoViewCard.setVisibility(View.VISIBLE);
                viewHolder.binding.videoViewCard2.setVisibility(View.VISIBLE);
                viewHolder.binding.playButton.setVisibility(View.VISIBLE);
                viewHolder.binding.videoThumbnail.setVisibility(View.VISIBLE);
                viewHolder.binding.videoView.setVisibility(View.VISIBLE);
                Glide.with(context).load(messageModel.getVideoUrl()).placeholder(R.drawable.img_1).into(viewHolder.binding.videoThumbnail);

                viewHolder.binding.playButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        // Play the video
                        Intent intent = new Intent(context, VideoPlayerActivity.class);
                        String videoUrl = messageModel.getVideoUrl();
                        intent.setData(Uri.parse(videoUrl));
                        context.startActivity(intent);

                    }
                });

            } else {
                viewHolder.binding.imageSentTime.setVisibility(View.GONE);
                viewHolder.binding.videoSentTime.setVisibility(View.GONE);
                viewHolder.binding.imageViewCard.setVisibility(View.GONE);
                viewHolder.binding.videoViewCard.setVisibility(View.GONE);
                viewHolder.binding.videoViewCard2.setVisibility(View.GONE);
                viewHolder.binding.videoView.setVisibility(View.GONE);
                viewHolder.binding.image.setVisibility(View.GONE);
                viewHolder.binding.videoThumbnail.setVisibility(View.GONE);
                viewHolder.binding.playButton.setVisibility(View.GONE);
                SimpleDateFormat sdf = new SimpleDateFormat(" hh:mm a", Locale.getDefault());
                String formattedTimestamp = sdf.format(new Date(messageModel.getTimestamp()));
                viewHolder.binding.timestamp.setText(formattedTimestamp);
                viewHolder.binding.timestamp.setVisibility(View.VISIBLE);
                viewHolder.binding.message.setVisibility(View.VISIBLE);
                viewHolder.binding.message.setText(messageModel.getMessage());
            }
            if (messageModel.getFeeling() >= 0) {
                viewHolder.binding.setEmoji.setImageResource(reactions[messageModel.getFeeling()]);
                viewHolder.binding.setEmoji.setVisibility(View.VISIBLE);
            } else {
                viewHolder.binding.setEmoji.setVisibility(View.GONE);
            }


            viewHolder.binding.relative.setOnLongClickListener(view -> {
                view.setSelected(!view.isSelected());
                popup.onTouch(view, MotionEvent.obtain(0, 0, MotionEvent.ACTION_DOWN, 0, 0, 0));
                return true;
            });
            popup.setOnDismissListener(new PopupWindow.OnDismissListener() {
                @Override
                public void onDismiss() {
                    viewHolder.binding.relative.setSelected(false);
                }
            });
        }
    }
    private void openFullScreenImage(String imageUrl) {
        Dialog dialog = new Dialog(context, android.R.style.Theme_Black_NoTitleBar_Fullscreen);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.full_screen_image);

        ImageView fullscreenImageView = dialog.findViewById(R.id.fullscreenImageView);
        ProgressBar progressBar = dialog.findViewById(R.id.progressBar);

        // छवि लोड करने के लिए Glide प्रयोग करें
        Glide.with(context)
                .load(imageUrl)
                .placeholder(R.drawable.img_1) // लोड होते समय प्लेसहोल्डर छवि
                .into(fullscreenImageView);

        dialog.show();
    }

    @Override
    public int getItemCount() {
        return messages.size();
    }

    public class SentViewHolder extends RecyclerView.ViewHolder {

        SampleSenderBinding binding;
        public SentViewHolder(@NonNull View itemView) {
            super(itemView);
            binding = SampleSenderBinding.bind(itemView);
        }
    }

    public class ReceiverViewHolder extends RecyclerView.ViewHolder {

        SampleReciverBinding binding;

        public ReceiverViewHolder(@NonNull View itemView) {
            super(itemView);
            binding = SampleReciverBinding.bind(itemView);
        }
    }

}