package com.example.aifloatingassistant;

import android.app.Service;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.PixelFormat;
import android.os.IBinder;
import android.provider.Settings;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;
import org.json.JSONObject;
import java.net.HttpURLConnection;
import java.net.URL;

public class FloatingService extends Service {
    // Isi dengan URL backend HTTPS milikmu. Jangan simpan API key AI di APK.
    private static final String BACKEND_URL = "https://YOUR-SERVER.example/api/chat";
    private WindowManager wm;
    private View panel;
    private WindowManager.LayoutParams params;

    @Override public void onCreate() {
        super.onCreate();
        if (!Settings.canDrawOverlays(this)) { stopSelf(); return; }
        wm = (WindowManager) getSystemService(WINDOW_SERVICE);
        buildPanel();
    }

    private int dp(float value) {
        return (int)(value * getResources().getDisplayMetrics().density);
    }

    private void buildPanel() {
        LinearLayout box = new LinearLayout(this);
        box.setOrientation(LinearLayout.VERTICAL);
        box.setPadding(dp(12), dp(10), dp(12), dp(12));
        box.setBackgroundColor(Color.rgb(248, 250, 255));

        LinearLayout header = new LinearLayout(this);
        header.setGravity(Gravity.CENTER_VERTICAL);
        TextView title = new TextView(this);
        title.setText("AI Floating Assistant  ⋮⋮");
        title.setTextColor(Color.rgb(25, 55, 110));
        title.setTextSize(16);
        header.addView(title, new LinearLayout.LayoutParams(0, dp(42), 1));
        Button close = new Button(this);
        close.setText("×");
        header.addView(close, new LinearLayout.LayoutParams(dp(48), dp(42)));
        box.addView(header);

        TextView answer = new TextView(this);
        answer.setText("Hai! Tanyakan materi pelajaran atau minta penjelasan di sini.");
        answer.setTextColor(Color.DKGRAY);
        answer.setTextSize(14);
        answer.setPadding(dp(8), dp(8), dp(8), dp(8));
        ScrollView scroll = new ScrollView(this);
        scroll.addView(answer);
        box.addView(scroll, new LinearLayout.LayoutParams(-1, dp(150)));

        EditText question = new EditText(this);
        question.setHint("Tulis pertanyaan...");
        question.setTextSize(14);
        box.addView(question);

        Button send = new Button(this);
        send.setText("Tanya AI");
        box.addView(send);

        params = new WindowManager.LayoutParams(
            dp(310), WindowManager.LayoutParams.WRAP_CONTENT,
            WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY,
            WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL |
                WindowManager.LayoutParams.FLAG_LAYOUT_IN_SCREEN,
            PixelFormat.TRANSLUCENT
        );
        params.gravity = Gravity.TOP | Gravity.START;
        params.x = dp(18);
        params.y = dp(100);
        panel = box;
        wm.addView(panel, params);

        // Geser jendela melalui area judul.
        final int[] start = new int[2];
        final float[] touch = new float[2];
        title.setOnTouchListener((v, event) -> {
            if (event.getAction() == MotionEvent.ACTION_DOWN) {
                start[0] = params.x; start[1] = params.y;
                touch[0] = event.getRawX(); touch[1] = event.getRawY();
                return true;
            } else if (event.getAction() == MotionEvent.ACTION_MOVE) {
                params.x = start[0] + (int)(event.getRawX() - touch[0]);
                params.y = start[1] + (int)(event.getRawY() - touch[1]);
                wm.updateViewLayout(panel, params);
                return true;
            }
            return event.getAction() == MotionEvent.ACTION_UP;
        });

        close.setOnClickListener(v -> stopSelf());
        send.setOnClickListener(v -> {
            String text = question.getText().toString().trim();
            if (text.isEmpty()) return;
            send.setEnabled(false);
            answer.setText("AI sedang menyiapkan jawaban...");
            new Thread(() -> {
                String result = ask(text);
                answer.post(() -> {
                    answer.setText(result);
                    send.setEnabled(true);
                });
            }).start();
        });
    }

    private String ask(String prompt) {
        try {
            HttpURLConnection c = (HttpURLConnection)new URL(BACKEND_URL).openConnection();
            c.setRequestMethod("POST");
            c.setConnectTimeout(15000);
            c.setReadTimeout(30000);
            c.setDoOutput(true);
            c.setRequestProperty("Content-Type", "application/json; charset=utf-8");
            JSONObject body = new JSONObject();
            body.put("message", prompt);
            c.getOutputStream().write(body.toString().getBytes("UTF-8"));
            int code = c.getResponseCode();
            java.io.InputStream stream = code >= 200 && code < 300 ? c.getInputStream() : c.getErrorStream();
            java.util.Scanner s = new java.util.Scanner(stream).useDelimiter("\\A");
            String raw = s.hasNext() ? s.next() : "";
            c.disconnect();
            return new JSONObject(raw).optString("answer", "Jawaban tidak ditemukan.");
        } catch (Exception e) {
            return "AI belum tersambung. Backend HTTPS perlu disiapkan dan BACKEND_URL di FloatingService.java harus diisi.";
        }
    }

    @Override public void onDestroy() {
        if (wm != null && panel != null) {
            try { wm.removeView(panel); } catch (Exception ignored) {}
        }
        super.onDestroy();
    }
    @Override public IBinder onBind(Intent intent) { return null; }
}
