package com.example.instagram;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.instagram.Adapter.CommentAdapter;
import com.example.instagram.Model.Comment;
import com.example.instagram.Model.User;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class CommentActivity extends AppCompatActivity {

    private RecyclerView recyclerView;        //加了recyclerView
    private CommentAdapter commentAdapter;
    private List<Comment> commentList;
    private EditText addComment;
    private CircleImageView imageProfile;
    private TextView post;

    private String postId;
    private String authorId;

    FirebaseUser fUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_comment);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);                    //set, 应用栏
        getSupportActionBar().setTitle("Comments");      //左箭头标有返回功能
        getSupportActionBar().setDisplayHomeAsUpEnabled(true); //此特定方法调用将操作栏配置为显示“向上”按钮（通常由向左的箭头图标表示）。“向上”按钮提供了一个导航选项，旨在提供分层导航——一种在应用程序定义的任务堆栈或逻辑父结构中向上导航的方式
        toolbar.setNavigationOnClickListener(new View.OnClickListener() { //按到toolbar就关闭
            @Override
            public void onClick(View v) {
               finish();
            }
        });

        Intent intent = getIntent();
        postId = intent.getStringExtra("postId");
        authorId = intent.getStringExtra("authorId");

        recyclerView = findViewById(R.id.recycler_view);                           //初始化了recyclerView
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        commentList = new ArrayList<>();
        commentAdapter = new CommentAdapter(this, commentList, postId);

        recyclerView.setAdapter(commentAdapter);

        addComment = findViewById(R.id.add_comment);          //编辑处
        imageProfile = findViewById(R.id.image_profile);
        post = findViewById(R.id.post);                       //按钮

        fUser = FirebaseAuth.getInstance().getCurrentUser();

        getUserImage(); //设置自己的头像

        post.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(TextUtils.isEmpty(addComment.getText().toString())){
                    Toast.makeText(CommentActivity.this, "No comment added!", Toast.LENGTH_SHORT).show();
                }else {
                    putComment();
                }
            }
        });

        getComment();  //检索数据库的评论对象, 利用adapter更新视图
    }

    private void getComment() {

        FirebaseDatabase.getInstance().getReference().child("Comments").child(postId).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                commentList.clear();

                for (DataSnapshot snapshot : dataSnapshot.getChildren()){
                    Comment comment = snapshot.getValue(Comment.class);
                    commentList.add(comment);
                }

                commentAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }

    private void putComment() { //添加评论到数据库

        HashMap<String, Object> map = new HashMap<>();

        DatabaseReference ref = FirebaseDatabase.getInstance().getReference().child("Comments").child(postId);

        String id = ref.push().getKey(); //拿到Comment的id

        map.put("id", id);
        map.put("comment", addComment.getText().toString());
        map.put("publisher", fUser.getUid());

        addComment.setText("");

       ref.child(id).setValue(map).addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if(task.isSuccessful()){
                            Toast.makeText(CommentActivity.this, "Comment added", Toast.LENGTH_SHORT).show();
                        }else {
                            Toast.makeText(CommentActivity.this, task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    }
                });

    }

    private void getUserImage() {

        FirebaseDatabase.getInstance().getReference().child("Users").child(fUser.getUid()).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                User user = dataSnapshot.getValue(User.class);
                if(user.getImageurl().equals("default")){
                    imageProfile.setImageResource(R.mipmap.ic_launcher);
                }else {
                    Picasso.get().load(user.getImageurl()).into(imageProfile);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }
}