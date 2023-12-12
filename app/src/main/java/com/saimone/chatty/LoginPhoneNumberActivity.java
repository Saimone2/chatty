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

import com.hbb20.CountryCodePicker;

public class LoginPhoneNumberActivity extends AppCompatActivity {
    CountryCodePicker countryCodePicker;
    EditText phoneInput;
    Button sendPinCodeBtn;
    ProgressBar progressBar;
    RelativeLayout round2;
    RelativeLayout round3;
    TextView enterTextView;
    int nightModeFlags;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login_phone_number);

        nightModeFlags = getResources().getConfiguration().uiMode & Configuration.UI_MODE_NIGHT_MASK;

        countryCodePicker = findViewById(R.id.login_country_code);
        phoneInput = findViewById(R.id.login_mobile_number);
        sendPinCodeBtn = findViewById(R.id.send_pin_code_btn);
        progressBar = findViewById(R.id.login_progress_bar);
        round2 = findViewById(R.id.round2);
        round3 = findViewById(R.id.round3);
        enterTextView = findViewById(R.id.enter_text_view);

        progressBar.setVisibility(View.GONE);

        if (nightModeFlags == Configuration.UI_MODE_NIGHT_YES) {
            phoneInput.setBackgroundTintList(ColorStateList.valueOf(ContextCompat.getColor(this, R.color.charcoal_gray)));
            round2.setBackgroundTintList(ColorStateList.valueOf(ContextCompat.getColor(this, R.color.charcoal_gray)));
            round3.setBackgroundTintList(ColorStateList.valueOf(ContextCompat.getColor(this, R.color.charcoal_gray)));
            enterTextView.setTextColor(ContextCompat.getColor(this, R.color.chatty_blue));
            countryCodePicker.setBackgroundTintList(ColorStateList.valueOf(ContextCompat.getColor(this, R.color.charcoal_gray)));
            countryCodePicker.setDialogTextColor(ContextCompat.getColor(this, R.color.smoke_white));
            countryCodePicker.setContentColor(ContextCompat.getColor(this, R.color.light_gray));
        } else {
            phoneInput.setBackgroundTintList(ColorStateList.valueOf(ContextCompat.getColor(this, R.color.white)));
            round2.setBackgroundTintList(ColorStateList.valueOf(ContextCompat.getColor(this, R.color.light_gray)));
            round3.setBackgroundTintList(ColorStateList.valueOf(ContextCompat.getColor(this, R.color.light_gray)));
            enterTextView.setTextColor(ContextCompat.getColor(this, R.color.charcoal_gray));
            countryCodePicker.setBackgroundTintList(ColorStateList.valueOf(ContextCompat.getColor(this, R.color.white)));
        }

        countryCodePicker.registerCarrierNumberEditText(phoneInput);
        sendPinCodeBtn.setOnClickListener((v) -> {
            if (!countryCodePicker.isValidFullNumber()) {
                phoneInput.setError("Phone number not valid");
                return;
            }

            Intent intent = new Intent(LoginPhoneNumberActivity.this, LoginPinCodeActivity.class);
            intent.putExtra("phone", countryCodePicker.getFullNumberWithPlus());
            startActivity(intent);
        });
    }
}