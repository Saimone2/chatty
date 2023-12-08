package com.saimone.chatty;

import static com.saimone.chatty.utils.FirebaseUtil.allUserCollectionReference;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.widget.EditText;
import android.widget.ImageButton;

import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.firebase.firestore.Query;
import com.saimone.chatty.adapter.SearchUserRecyclerAdapter;
import com.saimone.chatty.model.UserModel;

public class SearchUserActivity extends AppCompatActivity {
    EditText searchInput;
    ImageButton searchButton;
    ImageButton backButton;
    RecyclerView recyclerView;
    SearchUserRecyclerAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_user);

        searchInput = findViewById(R.id.search_username_input);
        searchButton = findViewById(R.id.search_user_btn);
        backButton = findViewById(R.id.back_button);
        recyclerView = findViewById(R.id.search_user_recycler_view);

        backButton.setOnClickListener(view -> getOnBackPressedDispatcher().onBackPressed());

        searchInput.requestFocus();

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
        Query query = allUserCollectionReference()
                .whereGreaterThanOrEqualTo("username", searchText);

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