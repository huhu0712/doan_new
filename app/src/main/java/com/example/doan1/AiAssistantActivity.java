package com.example.doan1;

import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.bottomnavigation.BottomNavigationView;

import java.util.ArrayList;
import java.util.List;

public class AiAssistantActivity extends AppCompatActivity {

    private RecyclerView rvChat;
    private ChatAdapter adapter;
    private List<ChatMessage> chatMessages;
    private EditText etMessage;
    private ImageButton btnSend;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ai_assistant);

        Toolbar toolbar = findViewById(R.id.toolbarAi);
        toolbar.setNavigationOnClickListener(v -> finish());

        rvChat = findViewById(R.id.rvChat);
        etMessage = findViewById(R.id.etMessage);
        btnSend = findViewById(R.id.btnSendMessage);

        chatMessages = new ArrayList<>();
        adapter = new ChatAdapter(chatMessages);
        rvChat.setLayoutManager(new LinearLayoutManager(this));
        rvChat.setAdapter(adapter);

        // Chào mừng từ AI
        chatMessages.add(new ChatMessage("Xin chào! Tôi là trợ lý du lịch AI. Tôi có thể giúp gì cho bạn?", false));

        btnSend.setOnClickListener(v -> {
            String msg = etMessage.getText().toString().trim();
            if (!msg.isEmpty()) {
                sendMessage(msg);
            }
        });

        // Cấu hình Bottom Navigation
        BottomNavigationView bottomNav = findViewById(R.id.bottomNavigationAi);
        bottomNav.setSelectedItemId(R.id.nav_ai_assistant);
        bottomNav.setOnItemSelectedListener(item -> {
            int id = item.getItemId();
            if (id == R.id.nav_home) {
                finish(); // Quay lại MainActivity
                return true;
            } else if (id == R.id.nav_login) {
                startActivity(new android.content.Intent(this, LoginActivity.class));
                finish();
                return true;
            }
            return true;
        });
    }

    private void sendMessage(String message) {
        // Tin nhắn của người dùng
        chatMessages.add(new ChatMessage(message, true));
        adapter.notifyItemInserted(chatMessages.size() - 1);
        rvChat.scrollToPosition(chatMessages.size() - 1);
        etMessage.setText("");

        // Giả lập AI trả lời sau 1 giây
        new Handler().postDelayed(() -> {
            String response = getAiResponse(message);
            chatMessages.add(new ChatMessage(response, false));
            adapter.notifyItemInserted(chatMessages.size() - 1);
            rvChat.scrollToPosition(chatMessages.size() - 1);
        }, 1000);
    }

    private String getAiResponse(String userMsg) {
        userMsg = userMsg.toLowerCase();
        if (userMsg.contains("khách sạn")) return "Tôi có thể tìm giúp bạn các khách sạn tốt nhất tại Hà Nội, Đà Nẵng hoặc Phú Quốc. Bạn muốn đi đâu?";
        if (userMsg.contains("vé máy bay")) return "Hiện tại đang có nhiều chương trình khuyến mãi vé máy bay giá rẻ đi Đà Lạt và Nha Trang đấy!";
        if (userMsg.contains("du lịch")) return "Việt Nam có rất nhiều điểm đến tuyệt vời như Vịnh Hạ Long, Hội An, hay Phong Nha Kẻ Bàng.";
        if (userMsg.contains("xin chào") || userMsg.contains("hi")) return "Chào bạn! Chúc bạn một ngày tốt lành. Bạn cần tư vấn du lịch gì không?";
        return "Xin lỗi, tôi chưa hiểu ý bạn lắm. Bạn có thể hỏi về khách sạn, vé máy bay hoặc các địa điểm du lịch tại Việt Nam nhé!";
    }
}
