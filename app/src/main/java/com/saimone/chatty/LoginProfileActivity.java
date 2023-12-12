package com.saimone.chatty;

import android.content.Intent;
import android.content.res.ColorStateList;
import android.content.res.Configuration;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import com.google.firebase.Timestamp;
import com.saimone.chatty.model.UserModel;
import com.saimone.chatty.utils.FirebaseUtil;

import java.util.Objects;

public class LoginProfileActivity extends AppCompatActivity {
    EditText usernameInput;
    TextView loginTextView;
    Button finishBtn;
    ProgressBar progressBar;
    String phoneNumber;
    UserModel userModel;
    RelativeLayout round1;
    RelativeLayout round2;
    int nightModeFlags;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login_profile);

        nightModeFlags = getResources().getConfiguration().uiMode & Configuration.UI_MODE_NIGHT_MASK;

        usernameInput = findViewById(R.id.login_username);
        loginTextView = findViewById(R.id.login_text_view);
        finishBtn = findViewById(R.id.login_finish_btn);
        progressBar = findViewById(R.id.login_progress_bar);
        round1 = findViewById(R.id.round1);
        round2 = findViewById(R.id.round2);

        phoneNumber = Objects.requireNonNull(getIntent().getExtras()).getString("phone");
        getUsername();

        finishBtn.setOnClickListener(view -> setUsername());

        if (nightModeFlags == Configuration.UI_MODE_NIGHT_YES) {
            round1.setBackgroundTintList(ColorStateList.valueOf(ContextCompat.getColor(this, R.color.charcoal_gray)));
            round2.setBackgroundTintList(ColorStateList.valueOf(ContextCompat.getColor(this, R.color.charcoal_gray)));
            loginTextView.setTextColor(ContextCompat.getColor(this, R.color.chatty_blue));
            usernameInput.setBackgroundTintList(ColorStateList.valueOf(ContextCompat.getColor(this, R.color.charcoal_gray)));
        } else {
            round1.setBackgroundTintList(ColorStateList.valueOf(ContextCompat.getColor(this, R.color.light_gray)));
            round2.setBackgroundTintList(ColorStateList.valueOf(ContextCompat.getColor(this, R.color.light_gray)));
            usernameInput.setBackgroundTintList(ColorStateList.valueOf(ContextCompat.getColor(this, R.color.white)));
        }
    }

    private void setUsername() {
        String username = usernameInput.getText().toString();
        if (username.isEmpty() || username.length() < 3) {
            usernameInput.setError("The username must be at least 3 characters long");
        } else if (username.length() > 30) {
            usernameInput.setError("The username is too long");
        } else {
            setInProgress(true);
            if (userModel != null) {
                userModel.setUsername(username);
            } else {
                userModel = new UserModel(phoneNumber, username, Timestamp.now(), FirebaseUtil.currentUserId());
            }

            FirebaseUtil.currentUserDetails().set(userModel).addOnCompleteListener(task -> {
                setInProgress(false);
                if (task.isSuccessful()) {
                    Intent intent = new Intent(LoginProfileActivity.this, MainActivity.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(intent);
                }
            });
        }
    }

    private void getUsername() {
        setInProgress(true);
        FirebaseUtil.currentUserDetails().get().addOnCompleteListener(task -> {
            setInProgress(false);
            if (task.isSuccessful()) {
                userModel = task.getResult().toObject(UserModel.class);
                if (userModel != null) {
                    loginTextView.setText(R.string.welcome);
                    usernameInput.setEnabled(false);
                    usernameInput.setText(userModel.getUsername());
                }
            }
        });
    }

    void setInProgress(boolean inProgress) {
        if (inProgress) {
            progressBar.setVisibility(View.VISIBLE);
            finishBtn.setVisibility(View.GONE);
        } else {
            progressBar.setVisibility(View.GONE);
            finishBtn.setVisibility(View.VISIBLE);
        }
    }
}