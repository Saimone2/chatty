package com.saimone.chatty;

import android.content.res.ColorStateList;
import android.content.res.Configuration;
import android.os.Bundle;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.RelativeLayout;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.firebase.firestore.Query;
import com.saimone.chatty.adapter.SearchUserRecyclerAdapter;
import com.saimone.chatty.model.UserModel;
import com.saimone.chatty.utils.FirebaseUtil;

public class SearchUserActivity extends AppCompatActivity {
    EditText searchInput;
    ImageButton searchButton;
    ImageButton backButton;
    RecyclerView recyclerView;
    RelativeLayout toolbar;
    SearchUserRecyclerAdapter adapter;
    int nightModeFlags;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_user);

        nightModeFlags = getResources().getConfiguration().uiMode & Configuration.UI_MODE_NIGHT_MASK;

        searchInput = findViewById(R.id.search_username_input);
        searchButton = findViewById(R.id.search_user_btn);
        backButton = findViewById(R.id.back_button);
        recyclerView = findViewById(R.id.search_user_recycler_view);
        toolbar = findViewById(R.id.toolbar);

        backButton.setOnClickListener(view -> getOnBackPressedDispatcher().onBackPressed());

        searchInput.requestFocus();

        if (nightModeFlags == Configuration.UI_MODE_NIGHT_YES) {
            searchButton.setBackgroundTintList(ColorStateList.valueOf(ContextCompat.getColor(this, R.color.chatty_dark_blue)));
            searchButton.setImageTintList(ColorStateList.valueOf(ContextCompat.getColor(this, R.color.chatty_blue)));
            toolbar.setBackgroundColor(ContextCompat.getColor(this, R.color.chatty_dark_blue));
            searchInput.setBackgroundTintList(ColorStateList.valueOf(ContextCompat.getColor(this, R.color.charcoal_gray)));
        } else {
            searchButton.setBackgroundTintList(ColorStateList.valueOf(ContextCompat.getColor(this, R.color.chatty_blue)));
            searchButton.setImageTintList(ColorStateList.valueOf(ContextCompat.getColor(this, R.color.smoke_white)));
            toolbar.setBackgroundColor(ContextCompat.getColor(this, R.color.chatty_blue));
            searchInput.setBackgroundTintList(ColorStateList.valueOf(ContextCompat.getColor(this, R.color.white)));
        }

        searchButton.setOnClickListener(view -> {
            String searchText = searchInput.getText().toString();
            if (searchText.isEmpty() || searchText.length() < 3 || searchText.length() > 30) {
                searchInput.setError("Invalid Username");
            } else {
                setupSearchRecyclerView(searchText);
            }
        });
    }

    private void setupSearchRecyclerView(String searchText) {
        Query query = FirebaseUtil.allUserCollectionReference()
                .whereGreaterThanOrEqualTo("username", searchText)
                .whereLessThanOrEqualTo("username", searchText + '\uf8ff');

        FirestoreRecyclerOptions<UserModel> options = new FirestoreRecyclerOptions.Builder<UserModel>()
                .setQuery(query, UserModel.class)
                .build();

        adapter = new SearchUserRecyclerAdapter(options, getApplicationContext());
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(adapter);
        adapter.startListening();
    }

    @Override
    protected void onStart() {
        super.onStart();
        if (adapter != null) {
            adapter.startListening();
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (adapter != null) {
            adapter.stopListening();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (adapter != null) {
            adapter.startListening();
        }
    }
}