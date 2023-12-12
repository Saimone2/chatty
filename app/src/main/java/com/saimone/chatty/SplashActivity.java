package com.saimone.chatty;

import static com.saimone.chatty.utils.FirebaseUtil.isLoggedIn;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
import android.os.Handler;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import com.saimone.chatty.model.UserModel;
import com.saimone.chatty.utils.AndroidUtil;
import com.saimone.chatty.utils.FirebaseUtil;

import java.util.Objects;

@SuppressLint("CustomSplashScreen")
public class SplashActivity extends AppCompatActivity {
    RelativeLayout layout;
    ImageView icon;
    int nightModeFlags;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        nightModeFlags = getResources().getConfiguration().uiMode & Configuration.UI_MODE_NIGHT_MASK;

        icon = findViewById(R.id.image_view_icon);
        layout = findViewById(R.id.splash_layout);

        if (nightModeFlags == Configuration.UI_MODE_NIGHT_YES) {
            layout.setBackgroundColor(ContextCompat.getColor(this, R.color.chatty_dark_blue));
            icon.setImageResource(R.drawable.icon_dark);
        } else {
            layout.setBackgroundColor(ContextCompat.getColor(this, R.color.chatty_blue));
            icon.setImageResource(R.drawable.icon_light);
        }

        if (FirebaseUtil.isLoggedIn() && getIntent().getExtras() != null) {
            String userId = getIntent().getExtras().getString("userId");
            FirebaseUtil.allUserCollectionReference().document(Objects.requireNonNull(userId)).get()
                    .addOnCompleteListener(task -> {
                        if (task.isSuccessful()) {
                            UserModel model = task.getResult().toObject(UserModel.class);

                            Intent mainIntent = new Intent(this, MainActivity.class);
                            mainIntent.setFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
                            startActivity(mainIntent);

                            Intent intent = new Intent(this, ChatActivity.class);
                            AndroidUtil.passUserModelAsIntent(intent, Objects.requireNonNull(model));
                            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                            startActivity(intent);
                            finish();
                        }
                    });
        } else {
            new Handler().postDelayed(() -> {
                if (isLoggedIn()) {
                    startActivity(new Intent(SplashActivity.this, MainActivity.class));
                } else {
                    startActivity(new Intent(SplashActivity.this, LoginPhoneNumberActivity.class));
                }
                finish();
            }, 1000);
        }
    }
}