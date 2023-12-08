package com.saimone.chatty;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login_profile);

        usernameInput = findViewById(R.id.login_username);
        loginTextView = findViewById(R.id.login_text_view);
        finishBtn = findViewById(R.id.login_finish_btn);
        progressBar = findViewById(R.id.login_progress_bar);

        phoneNumber = Objects.requireNonNull(getIntent().getExtras()).getString("phone");
        getUsername();

        finishBtn.setOnClickListener(view -> setUsername());
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