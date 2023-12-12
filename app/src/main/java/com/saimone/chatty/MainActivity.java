package com.saimone.chatty;

import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.messaging.FirebaseMessaging;
import com.saimone.chatty.utils.FirebaseUtil;

public class MainActivity extends AppCompatActivity {
    BottomNavigationView bottomNavigationView;
    ImageButton searchButton;
    ImageView icon;
    ChatFragment chatFragment;
    ProfileFragment profileFragment;

    RelativeLayout mainToolbar;
    int nightModeFlags;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        nightModeFlags = getResources().getConfiguration().uiMode & Configuration.UI_MODE_NIGHT_MASK;

        chatFragment = new ChatFragment();
        profileFragment = new ProfileFragment();

        bottomNavigationView = findViewById(R.id.bottom_navigation);
        searchButton = findViewById(R.id.main_search_button);
        icon = findViewById(R.id.image_view_icon);
        mainToolbar = findViewById(R.id.main_toolbar);

        searchButton.setOnClickListener(view -> startActivity(new Intent(MainActivity.this, SearchUserActivity.class)));

        bottomNavigationView.setOnItemSelectedListener(item -> {
            if (item.getItemId() == R.id.menu_chat) {
                getSupportFragmentManager().beginTransaction().replace(R.id.main_frame_layout, chatFragment).commit();
            }
            if (item.getItemId() == R.id.menu_profile) {
                getSupportFragmentManager().beginTransaction().replace(R.id.main_frame_layout, profileFragment).commit();
            }
            return true;
        });

        if (nightModeFlags == Configuration.UI_MODE_NIGHT_YES) {
            icon.setImageResource(R.drawable.dark_icon);
            mainToolbar.setBackgroundColor(ContextCompat.getColor(this, R.color.chatty_dark_blue));
        } else {
            icon.setImageResource(R.drawable.light_icon);
            mainToolbar.setBackgroundColor(ContextCompat.getColor(this, R.color.chatty_blue));
        }

        bottomNavigationView.setSelectedItemId(R.id.menu_chat);

        getFCMToken();
    }

    private void getFCMToken() {
        FirebaseMessaging.getInstance().getToken()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        String token = task.getResult();
                        FirebaseUtil.currentUserDetails().update("fcmToken", token);
                    }
                });
    }
}