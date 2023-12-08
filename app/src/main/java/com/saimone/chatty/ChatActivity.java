package com.saimone.chatty;

import android.os.Bundle;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import com.saimone.chatty.model.UserModel;
import com.saimone.chatty.utils.AndroidUtil;

public class ChatActivity extends AppCompatActivity {
    UserModel otherUser;
    EditText messageInput;
    ImageButton sendMessageBtn;
    ImageButton backBtn;
    TextView otherUsername;
    RecyclerView recyclerView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);

        messageInput = findViewById(R.id.chat_message_input);
        sendMessageBtn = findViewById(R.id.message_send_btn);
        backBtn = findViewById(R.id.back_button);
        otherUsername = findViewById(R.id.other_username);
        recyclerView = findViewById(R.id.chat_recycler_view);

        otherUser = AndroidUtil.getUserModelFromIntent(getIntent());
        otherUsername.setText(otherUser.getUsername());

        backBtn.setOnClickListener(view -> getOnBackPressedDispatcher().onBackPressed());


    }
}