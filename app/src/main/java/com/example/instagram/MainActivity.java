package com.example.instagram;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.fragment.app.Fragment;

import com.example.instagram.Fragments.HomeFragment;
import com.example.instagram.Fragments.NotificationFragment;
import com.example.instagram.Fragments.ProfileFragment;
import com.example.instagram.Fragments.SearchFragment;
import com.google.android.material.bottomnavigation.BottomNavigationView;

public class MainActivity extends AppCompatActivity {

    private BottomNavigationView bottomNavigationView;
    private Fragment selectorFragment;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

//        // Apply window insets for system bars
//        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
//            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
//            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
//            return insets;
//        });

        bottomNavigationView = findViewById(R.id.bottom_navigation);

        // Set the listener for item selection using the new setOnItemSelectedListener method
        bottomNavigationView.setOnItemSelectedListener(item -> {
            int itemId = item.getItemId();
            if (itemId == R.id.nav_home) {
                selectorFragment = new HomeFragment();
            } else if (itemId == R.id.nav_search) {
                selectorFragment = new SearchFragment();
            } else if (itemId == R.id.nav_add) {
                selectorFragment = null; // No fragment for the 'Add' action, starts an activity instead
                startActivity(new Intent(MainActivity.this, PostActivity.class));
                return false; // Returning false because we don't want this to be selected
            } else if (itemId == R.id.nav_heart) {
                selectorFragment = new NotificationFragment();
            } else if (itemId == R.id.nav_profile) {
                selectorFragment = new ProfileFragment();
            }

            if (selectorFragment != null) {
                getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, selectorFragment).commit();
            }

            return true; // Return true to display the item as the selected item
        });

        Bundle intent = getIntent().getExtras(); //拿到当前intent中的bundle
        if(intent != null){
            String profileId = intent.getString("publisherId");

            getSharedPreferences("PROFILE", MODE_PRIVATE).edit().putString("profileId", profileId).apply();
//            - 此行负责将 `profileId` 保存到共享首选项。
//            `getSharedPreferences(name, mode)` 用于检索由 `name`（本例中为 `"PROFILE"`）标识的共享首选项文件，
//            并在指定模式下运行（`MODE_PRIVATE` 表示数据只能由调用应用程序访问）。调用 `edit()` 为这些首选项创建编辑器，
//            然后 `putString(key, value)` 将 `profileId` 插入共享首选项，最后使用 `apply()` 异步保存更改。
            getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, new ProfileFragment()).commit();
            bottomNavigationView.setSelectedItemId(R.id.nav_profile);
        }else { //此行用于将容器（`R.id.fragment_container`）中的片段替换为 `ProfileFragment` 的新实例
            getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, new HomeFragment()).commit();
        }

        // Set the default selected item, usually done to show initial content
//        getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, new HomeFragment()).commit();
    }
}