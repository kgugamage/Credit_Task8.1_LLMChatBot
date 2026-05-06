package com.example.llmchatbot;

import android.os.Bundle;
import android.os.Handler;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

public class ChatActivity extends AppCompatActivity {

    TextView welcomeText;
    RecyclerView chatRecyclerView;
    EditText messageInput;
    Button sendButton;

    ArrayList<Message> messages;
    MessageAdapter adapter;
    ChatDatabaseHelper databaseHelper;

    String username;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);

        welcomeText = findViewById(R.id.welcomeText);
        chatRecyclerView = findViewById(R.id.chatRecyclerView);
        messageInput = findViewById(R.id.messageInput);
        sendButton = findViewById(R.id.sendButton);

        username = getIntent().getStringExtra("username");
        welcomeText.setText("Welcome " + username + "!");

        databaseHelper = new ChatDatabaseHelper(this);
        messages = databaseHelper.getAllMessages();

        adapter = new MessageAdapter(messages);
        chatRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        chatRecyclerView.setAdapter(adapter);

        scrollToBottom();

        sendButton.setOnClickListener(v -> sendMessage());
    }

    private void sendMessage() {
        String userMessage = messageInput.getText().toString().trim();

        if (userMessage.isEmpty()) {
            return;
        }

        String time = getCurrentTime();

        Message message = new Message(userMessage, "user", time);
        messages.add(message);
        databaseHelper.addMessage(userMessage, "user", time);

        adapter.notifyItemInserted(messages.size() - 1);
        scrollToBottom();

        messageInput.setText("");

        getBotReply(userMessage);
    }

    private void getBotReply(String userMessage) {
        new Handler().postDelayed(() -> {
            String botReply = createSimpleBotReply(userMessage);
            String time = getCurrentTime();

            Message botMessage = new Message(botReply, "bot", time);
            messages.add(botMessage);
            databaseHelper.addMessage(botReply, "bot", time);

            adapter.notifyItemInserted(messages.size() - 1);
            scrollToBottom();

        }, 800);
    }

    private String createSimpleBotReply(String message) {
        String originalMessage = message;
        message = message.toLowerCase();

        if (message.contains("hello") || message.contains("hi")) {
            return "Hello! How can I help you today?";
        } else if (message.contains("name")) {
            return "I am your AI chatbot.";
        } else if (message.contains("help")) {
            return "Sure, I can help. Please ask me a question.";
        } else if (message.contains("android")) {
            return "Android apps are built using activities, layouts, and Java or Kotlin code.";
        } else if (message.contains("time")) {
            return "The current message time is shown under each chat bubble.";
        } else {
            return "This is a chatbot response to: " + originalMessage;
        }
    }

    private String getCurrentTime() {
        return new SimpleDateFormat("hh:mm a", Locale.getDefault()).format(new Date());
    }

    private void scrollToBottom() {
        if (messages.size() > 0) {
            chatRecyclerView.scrollToPosition(messages.size() - 1);
        }
    }
}
