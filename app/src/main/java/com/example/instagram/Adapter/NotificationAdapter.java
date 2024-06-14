package com.example.instagram.Adapter;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.FragmentActivity;
import androidx.recyclerview.widget.RecyclerView;

import com.example.instagram.Fragments.PostDetailFragment;
import com.example.instagram.Fragments.ProfileFragment;
import com.example.instagram.Model.Notification;
import com.example.instagram.Model.Post;
import com.example.instagram.Model.User;
import com.example.instagram.R;
import com.google.firebase.Firebase;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.List;

public class NotificationAdapter extends RecyclerView.Adapter<NotificationAdapter.ViewHolder>{

    private Context mContext;
    private List<Notification> mNotification;

    public NotificationAdapter(Context mContext, List<Notification> mNotification) {
        this.mContext = mContext;
        this.mNotification = mNotification;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.notification_item, parent, false);

        return new NotificationAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {

        Notification notification = mNotification.get(position);

        getUser(holder.imageProfile, holder.username, notification.getUserid()); //根据notification的userid设置头像和名字
        holder.comment.setText(notification.getText());

        if(notification.isIsPost()){
            holder.postImage.setVisibility(View.VISIBLE);
            getPostImage(holder.postImage, notification.getPostid());           //用notification的postid从数据库检索到的post设置post图片
        }else {
            holder.postImage.setVisibility(View.GONE);
        }

        holder.itemView.setOnClickListener(new View.OnClickListener() {         //点击整个holder进入detail
            @Override
            public void onClick(View v) {
                if(notification.isIsPost()){                                      //如果不是isPost就打开profile
                    mContext.getSharedPreferences("PREFS", Context.MODE_PRIVATE)
                            .edit().putString("postid", notification.getPostid()).apply();

                    ((FragmentActivity)mContext).getSupportFragmentManager()
                            .beginTransaction().replace(R.id.fragment_container, new PostDetailFragment()).commit();  //进入detail要塞个PREFS
                }else {
                    mContext.getSharedPreferences("PROFILE", Context.MODE_PRIVATE)
                            .edit().putString("profileId", notification.getUserid()).apply();

                    ((FragmentActivity)mContext).getSupportFragmentManager()
                            .beginTransaction().replace(R.id.fragment_container, new ProfileFragment()).commit();  //进入profile要塞个PROFILE
                }
            }
        });

    }

    @Override
    public int getItemCount() {
        return mNotification.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder{

        public ImageView imageProfile; //头像
        public ImageView postImage;    //post图片
        public TextView username;      //名字文字
        public TextView comment;       //评论文字

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            imageProfile = itemView.findViewById(R.id.image_profile);
            postImage = itemView.findViewById(R.id.post_image);
            username = itemView.findViewById(R.id.username);
            comment = itemView.findViewById(R.id.comment);
        }
    }

    private void getPostImage(ImageView imageView, String postId){
        FirebaseDatabase.getInstance().getReference().child("Posts").child(postId).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                Post post = dataSnapshot.getValue(Post.class);
                Picasso.get().load(post.getImageurl()).placeholder(R.mipmap.ic_launcher).into(imageView);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void getUser(ImageView imageView, TextView textView, String userId){
        FirebaseDatabase.getInstance().getReference().child("Users").child(userId).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                User user = dataSnapshot.getValue(User.class);
                if(user.getImageurl().equals("default")){
                    imageView.setImageResource(R.mipmap.ic_launcher);
                }else {
                    Picasso.get().load(user.getImageurl()).into(imageView);
                }
                textView.setText(user.getUsername());
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

}
