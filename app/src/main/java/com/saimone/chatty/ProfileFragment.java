package com.saimone.chatty;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.github.dhaval2404.imagepicker.ImagePicker;
import com.saimone.chatty.model.UserModel;
import com.saimone.chatty.utils.AndroidUtil;
import com.saimone.chatty.utils.FirebaseUtil;

import java.util.Objects;

public class ProfileFragment extends Fragment {
    ImageView profilePic;
    EditText usernameInput;
    EditText phoneInput;
    Button updateProfileBtn;
    ProgressBar progressBar;
    TextView logoutText;
    UserModel currentUserModel;
    ActivityResultLauncher<Intent> imagePickLauncher;
    Uri selectedImageUri;

    public ProfileFragment() {

    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        imagePickLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    if (result.getResultCode() == Activity.RESULT_OK) {
                        Intent data = result.getData();
                        if (data != null && data.getData() != null) {
                            selectedImageUri = data.getData();
                            AndroidUtil.setProfilePic(getContext(), selectedImageUri, profilePic);
                        }
                    }
                }
        );
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_profile, container, false);

        profilePic = view.findViewById(R.id.profile_image_view);
        usernameInput = view.findViewById(R.id.profile_username);
        phoneInput = view.findViewById(R.id.profile_phone_number);
        updateProfileBtn = view.findViewById(R.id.profile_update_btn);
        progressBar = view.findViewById(R.id.profile_progress_bar);
        logoutText = view.findViewById(R.id.logout_text);

        getUserData();

        updateProfileBtn.setOnClickListener(view1 -> updateBtnClick());

        logoutText.setOnClickListener(view12 -> {
            FirebaseUtil.logout();
            Intent intent = new Intent(getContext(), SplashActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
        });

        profilePic.setOnClickListener(view13 -> ImagePicker
                .with(this)
                .cropSquare()
                .compress(512)
                .maxResultSize(512, 512)
                .createIntent(intent -> {
                    imagePickLauncher.launch(intent);
                    return null;
                }));

        return view;
    }

    void updateBtnClick() {
        String newUsername = usernameInput.getText().toString();
        if (newUsername.isEmpty() || newUsername.length() < 3) {
            usernameInput.setError("The username must be at least 3 characters long");
        } else if (newUsername.length() > 30) {
            usernameInput.setError("The username is too long");
        } else {
            currentUserModel.setUsername(newUsername);
            setInProgress(true);

            if (selectedImageUri != null) {
                FirebaseUtil.getCurrentProfilePicStorageReference().putFile(selectedImageUri)
                        .addOnCompleteListener(task -> {
                            if (task.isSuccessful()) {
                                updateToFirestore();
                            }
                        });
            } else {
                updateToFirestore();
            }
        }
    }

    void updateToFirestore() {
        FirebaseUtil.currentUserDetails().set(currentUserModel).addOnCompleteListener(task -> {
            setInProgress(false);
            if (task.isSuccessful()) {
                AndroidUtil.showToast(getContext(), "Successfully updated");
            } else {
                AndroidUtil.showToast(getContext(), "Update failed");
            }
        });
    }

    private void getUserData() {
        setInProgress(true);

        FirebaseUtil.getCurrentProfilePicStorageReference().getDownloadUrl()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        Uri uri = task.getResult();
                        AndroidUtil.setProfilePic(getContext(), uri, profilePic);
                    }
                });

        FirebaseUtil.currentUserDetails().get().addOnCompleteListener(task -> {
            setInProgress(false);
            currentUserModel = task.getResult().toObject(UserModel.class);
            usernameInput.setText(Objects.requireNonNull(currentUserModel).getUsername());
            phoneInput.setText(currentUserModel.getPhone());
        });
    }

    void setInProgress(boolean inProgress) {
        if (inProgress) {
            progressBar.setVisibility(View.VISIBLE);
            updateProfileBtn.setVisibility(View.GONE);
        } else {
            progressBar.setVisibility(View.GONE);
            updateProfileBtn.setVisibility(View.VISIBLE);
        }
    }
}