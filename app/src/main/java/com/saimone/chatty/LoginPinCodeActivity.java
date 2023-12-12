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

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import com.google.firebase.FirebaseException;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthOptions;
import com.google.firebase.auth.PhoneAuthProvider;
import com.saimone.chatty.utils.AndroidUtil;

import java.util.Objects;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.TimeUnit;

public class LoginPinCodeActivity extends AppCompatActivity {
    String phoneNumber;
    Long timeoutSeconds = 60L;
    String verificationCode;
    PhoneAuthProvider.ForceResendingToken resendingToken;
    EditText loginPinCode;
    Button nextBtn;
    ProgressBar progressBar;
    TextView resendPinCode;
    RelativeLayout round1;
    RelativeLayout round3;
    FirebaseAuth auth = FirebaseAuth.getInstance();
    TextView enterTextView;
    int nightModeFlags;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login_pin_code);

        nightModeFlags = getResources().getConfiguration().uiMode & Configuration.UI_MODE_NIGHT_MASK;

        loginPinCode = findViewById(R.id.login_pin_code);
        nextBtn = findViewById(R.id.login_next_btn);
        progressBar = findViewById(R.id.login_progress_bar);
        resendPinCode = findViewById(R.id.resend_pin_code);
        round1 = findViewById(R.id.round1);
        round3 = findViewById(R.id.round3);
        enterTextView = findViewById(R.id.enter_text_view);

        phoneNumber = Objects.requireNonNull(getIntent().getExtras()).getString("phone");

        sendPinCode(phoneNumber, false);

        nextBtn.setOnClickListener(view -> {
            String enteredPinCode = loginPinCode.getText().toString();
            PhoneAuthCredential credential = PhoneAuthProvider.getCredential(verificationCode, enteredPinCode);
            signIn(credential);
            setInProgress(true);
        });

        if (nightModeFlags == Configuration.UI_MODE_NIGHT_YES) {
            round1.setBackgroundTintList(ColorStateList.valueOf(ContextCompat.getColor(this, R.color.charcoal_gray)));
            round3.setBackgroundTintList(ColorStateList.valueOf(ContextCompat.getColor(this, R.color.charcoal_gray)));
            loginPinCode.setBackgroundTintList(ColorStateList.valueOf(ContextCompat.getColor(this, R.color.charcoal_gray)));
            enterTextView.setTextColor(ContextCompat.getColor(this, R.color.chatty_blue));
        } else {
            round1.setBackgroundTintList(ColorStateList.valueOf(ContextCompat.getColor(this, R.color.light_gray)));
            round3.setBackgroundTintList(ColorStateList.valueOf(ContextCompat.getColor(this, R.color.light_gray)));
            loginPinCode.setBackgroundTintList(ColorStateList.valueOf(ContextCompat.getColor(this, R.color.white)));
        }

        resendPinCode.setOnClickListener(view -> sendPinCode(phoneNumber, true));
    }

    void sendPinCode(String phoneNumber, boolean isResend) {
        startResendTimer();
        setInProgress(true);
        PhoneAuthOptions.Builder builder =
                PhoneAuthOptions.newBuilder(auth)
                        .setPhoneNumber(phoneNumber)
                        .setTimeout(timeoutSeconds, TimeUnit.SECONDS)
                        .setActivity(this)
                        .setCallbacks(new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
                            @Override
                            public void onVerificationCompleted(@NonNull PhoneAuthCredential phoneAuthCredential) {
                                signIn(phoneAuthCredential);
                                setInProgress(false);
                            }

                            @Override
                            public void onVerificationFailed(@NonNull FirebaseException e) {
                                AndroidUtil.showToast(getApplicationContext(), "Pin code verification failed");
                                setInProgress(false);
                            }

                            @Override
                            public void onCodeSent(@NonNull String s, @NonNull PhoneAuthProvider.ForceResendingToken forceResendingToken) {
                                super.onCodeSent(s, forceResendingToken);
                                verificationCode = s;
                                resendingToken = forceResendingToken;
                                AndroidUtil.showToast(getApplicationContext(), "Pin code send successfully");
                                setInProgress(false);
                            }
                        });

        if (isResend) {
            PhoneAuthProvider.verifyPhoneNumber(builder.setForceResendingToken(resendingToken).build());
        } else {
            PhoneAuthProvider.verifyPhoneNumber(builder.build());
        }
    }

    private void startResendTimer() {
        resendPinCode.setEnabled(false);
        Timer timer = new Timer();
        timer.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                timeoutSeconds--;
                resendPinCode.setText(getString(R.string.resend_pin_code_seconds, timeoutSeconds));
                if (timeoutSeconds <= 0) {
                    timeoutSeconds = 60L;
                    timer.cancel();
                    runOnUiThread(() -> {
                        resendPinCode.setText(R.string.resend_pin_code);
                        resendPinCode.setEnabled(true);
                    });
                }
            }
        }, 0, 1000);
    }

    void setInProgress(boolean inProgress) {
        if (inProgress) {
            progressBar.setVisibility(View.VISIBLE);
            nextBtn.setVisibility(View.GONE);
        } else {
            progressBar.setVisibility(View.GONE);
            nextBtn.setVisibility(View.VISIBLE);
        }
    }

    private void signIn(PhoneAuthCredential phoneAuthCredential) {
        setInProgress(true);
        auth.signInWithCredential(phoneAuthCredential).addOnCompleteListener(task -> {
            setInProgress(false);
            if (task.isSuccessful()) {
                Intent intent = new Intent(LoginPinCodeActivity.this, LoginProfileActivity.class);
                intent.putExtra("phone", phoneNumber);
                startActivity(intent);
            } else {
                AndroidUtil.showToast(getApplicationContext(), "Pin code verification failed");
            }
        });
    }
}