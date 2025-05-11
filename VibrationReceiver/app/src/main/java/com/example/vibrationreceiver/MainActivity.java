package com.example.vibrationreceiver;

import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.VibrationEffect;
import android.os.Vibrator;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.Timer;
import java.util.TimerTask;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class MainActivity extends AppCompatActivity {

    private TextView statusText;
    private final OkHttpClient client = new OkHttpClient();
    private Vibrator vibrator;
    private Timer timer;
    private final Handler handler = new Handler(Looper.getMainLooper());
    
    // 服务器URL，需要替换为实际的服务器地址
    private static final String SERVER_URL = "http://car.lyjsshop.com/vibration_server.php?action=receive";
    // 轮询间隔（毫秒）
    private static final long POLLING_INTERVAL = 3000;
    // 上次接收到的震动ID
    private int lastVibrationId = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        statusText = findViewById(R.id.statusText);
        vibrator = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
        
        // 启动轮询服务器
        startPollingServer();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        // 停止轮询
        if (timer != null) {
            timer.cancel();
            timer = null;
        }
    }

    private void startPollingServer() {
        timer = new Timer();
        timer.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                checkForNewVibrations();
            }
        }, 0, POLLING_INTERVAL);
    }

    private void checkForNewVibrations() {
        Request request = new Request.Builder()
                .url(SERVER_URL)
                .build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                updateStatus("连接服务器失败: " + e.getMessage());
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                try {
                    String responseData = response.body().string();
                    JSONObject jsonResponse = new JSONObject(responseData);
                    
                    if (jsonResponse.getString("status").equals("success")) {
                        JSONObject data = jsonResponse.getJSONObject("data");
                        int vibrationId = data.getInt("id");
                        String pattern = data.getString("pattern");
                        
                        // 只有当收到新的震动ID时才执行震动
                        if (vibrationId > lastVibrationId) {
                            lastVibrationId = vibrationId;
                            executeVibration(pattern);
                            updateStatus("收到新震动: " + pattern);
                        }
                    }
                } catch (JSONException e) {
                    updateStatus("解析响应失败: " + e.getMessage());
                }
            }
        });
    }

    private void executeVibration(String pattern) {
        handler.post(new Runnable() {
            @Override
            public void run() {
                // 根据不同的震动模式执行不同的震动效果
                switch (pattern) {
                    case "single":
                        // 单次震动 (500毫秒)
                        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
                            vibrator.vibrate(VibrationEffect.createOneShot(500, VibrationEffect.DEFAULT_AMPLITUDE));
                        } else {
                            vibrator.vibrate(500);
                        }
                        break;
                        
                    case "double":
                        // 两次震动 (短-短)
                        long[] doublePattern = {0, 300, 200, 300};
                        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
                            vibrator.vibrate(VibrationEffect.createWaveform(doublePattern, -1));
                        } else {
                            vibrator.vibrate(doublePattern, -1);
                        }
                        break;
                        
                    case "long_short":
                        // 长短震动 (长-短)
                        long[] longShortPattern = {0, 800, 200, 300};
                        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
                            vibrator.vibrate(VibrationEffect.createWaveform(longShortPattern, -1));
                        } else {
                            vibrator.vibrate(longShortPattern, -1);
                        }
                        break;
                }
            }
        });
    }

    private void updateStatus(String message) {
        handler.post(new Runnable() {
            @Override
            public void run() {
                statusText.setText(message);
            }
        });
    }
}