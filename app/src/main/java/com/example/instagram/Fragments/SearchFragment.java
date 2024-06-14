package com.example.instagram.Fragments;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.instagram.Adapter.TagAdapter;
import com.example.instagram.Adapter.UserAdapter;
import com.example.instagram.Model.User;
import com.example.instagram.R;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.hendraanggrian.appcompat.socialview.widget.SocialAutoCompleteTextView;

import java.util.ArrayList;
import java.util.List;

public class SearchFragment extends Fragment {

    private RecyclerView recyclerView;
    private List<User> mUsers;
    private UserAdapter userAdapter;

    private RecyclerView recyclerViewTags;
    private List<String> mHashTags;
    private List<String> mHashTagsCount;
    private TagAdapter tagAdapter;

    private SocialAutoCompleteTextView search_bar;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_search, container, false);

        recyclerView = view.findViewById(R.id.recycler_view_users); //user的Recycler
        recyclerView.setHasFixedSize(true); //此设置意味着 `RecyclerView` 可以进行某些优化，因为它不需要在其适配器的内容每次更改时重新计算 RecyclerView 本身的大小（即其宽度和高度）
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        recyclerViewTags = view.findViewById(R.id.recycler_view_tags); //tag的Recycler
        recyclerViewTags.setHasFixedSize(true);
        recyclerViewTags.setLayoutManager(new LinearLayoutManager(getContext()));

        mHashTags = new ArrayList<>();
        mHashTagsCount = new ArrayList<>();
        tagAdapter = new TagAdapter(getContext(), mHashTags, mHashTagsCount);
        recyclerViewTags.setAdapter(tagAdapter);

        mUsers = new ArrayList<>();
        userAdapter = new UserAdapter(getContext(), mUsers, true);
        recyclerView.setAdapter(userAdapter);

        search_bar = view.findViewById(R.id.search_bar); //appbar中的toolbar中的SocialAutoCompleteTextView

        readUsers();
        readTags();

        search_bar.addTextChangedListener(new TextWatcher() { //只要往搜索框添加文字
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                searchUser(s.toString()); //过滤user
            } //本来就是传user数组, 一输入, user数组改变为过滤的数组, 通知adapter, 改变

            @Override
            public void afterTextChanged(Editable s) {
                filter(s.toString());   //过滤标签
            }
        });

        return view;
    }

    private void readTags() {

        FirebaseDatabase.getInstance().getReference().child("HashTags").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                mHashTags.clear();
                mHashTagsCount.clear();

                for (DataSnapshot snapshot : dataSnapshot.getChildren()){
                    mHashTags.add(snapshot.getKey()); //标签名
                    mHashTagsCount.add(snapshot.getChildrenCount() + ""  ); //等于标签后的上传者的id数
                }

                tagAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }

    private void readUsers() {

        DatabaseReference reference = FirebaseDatabase.getInstance().getReference().child("Users");
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if(TextUtils.isEmpty(search_bar.getText().toString())){
                    mUsers.clear(); //清空列表
                    for (DataSnapshot snapshot : dataSnapshot.getChildren()){
                        User user = snapshot.getValue(User.class);
                        mUsers.add(user); //添加所有user到列表
                    }

                    userAdapter.notifyDataSetChanged(); //这会通知 `UserAdapter`（`RecyclerView` 的适配器）底层数据已更改，提示 `RecyclerView` 刷新并显示当前用户列表
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }

    private void searchUser(String s){
        Query query = FirebaseDatabase.getInstance().getReference().child("Users")
                .orderByChild("username").startAt(s).endAt(s + "\uf8ff"); // 搜索以s为开头的, 并搜索s+("\uf8ff")最高可能的字符串

        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                mUsers.clear();
                for (DataSnapshot snapshot : dataSnapshot.getChildren()){ //将查询内容添加到列表
                    User user = snapshot.getValue(User.class);
                    mUsers.add(user);
                }
                userAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void filter(String text){
        List<String> mSearchTags = new ArrayList<>();
        List<String> mSearchTagsCount = new ArrayList<>();

        for(String s : mHashTags){ //拿到所有标签
            if(s.toLowerCase().contains(text.toLowerCase())){ //只要标签名含有搜索的字母
                mSearchTags.add(s); //直接加入搜素标签组
                mSearchTagsCount.add(mHashTagsCount.get(mHashTags.indexOf(s)));
            }
        }

        tagAdapter.filter(mSearchTags, mSearchTagsCount);  //用特定字符串过滤标签
    }
}