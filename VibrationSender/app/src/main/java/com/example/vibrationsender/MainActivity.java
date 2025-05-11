package com.example.vibrationsender;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.RadioGroup;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class MainActivity extends AppCompatActivity {

    private RadioGroup patternGroup;
    private Button sendButton;
    private final OkHttpClient client = new OkHttpClient();
    private static final MediaType JSON = MediaType.parse("application/json; charset=utf-8");
    
    // 服务器URL，需要替换为实际的服务器地址
    private static final String SERVER_URL = "http://your-server-url/vibration_server.php?action=send";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        patternGroup = findViewById(R.id.patternGroup);
        sendButton = findViewById(R.id.sendButton);

        sendButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendVibrationPattern();
            }
        });
    }

    private void sendVibrationPattern() {
        // 获取选中的震动模式
        String pattern;
        switch (patternGroup.getCheckedRadioButtonId()) {
            case R.id.patternSingle:
                pattern = "single";
                break;
            case R.id.patternDouble:
                pattern = "double";
                break;
            case R.id.patternLongShort:
                pattern = "long_short";
                break;
            default:
                Toast.makeText(this, "请选择一个震动模式", Toast.LENGTH_SHORT).show();
                return;
        }

        // 创建JSON请求体
        String json = "{\"pattern\":\"" + pattern + "\"}";
        RequestBody body = RequestBody.create(json, JSON);

        // 创建请求
        Request request = new Request.Builder()
                .url(SERVER_URL)
                .post(body)
                .build();

        // 发送请求
        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(MainActivity.this, "发送失败: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                final String responseData = response.body().string();
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(MainActivity.this, "发送成功", Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });
    }
}