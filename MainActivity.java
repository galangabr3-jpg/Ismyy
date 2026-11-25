package com.example.aifloatingassistant;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.Settings;
import android.view.Gravity;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

public class MainActivity extends Activity {
    private static final int OVERLAY_REQUEST = 501;

    @Override public void onCreate(Bundle saved) {
        super.onCreate(saved);
        LinearLayout layout = new LinearLayout(this);
        layout.setOrientation(LinearLayout.VERTICAL);
        layout.setPadding(36, 48, 36, 36);
        layout.setGravity(Gravity.CENTER_HORIZONTAL);

        TextView title = new TextView(this);
        title.setText("AI Floating Assistant");
        title.setTextSize(25);
        title.setPadding(0, 0, 0, 18);
        layout.addView(title);

        TextView info = new TextView(this);
        info.setText("Asisten belajar AI dengan jendela melayang. Izinkan tampil di atas aplikasi lain, lalu mulai floating window.");
        info.setTextSize(16);
        info.setPadding(0, 0, 0, 24);
        layout.addView(info);

        Button permission = new Button(this);
        permission.setText("Izinkan Floating Window");
        permission.setOnClickListener(v -> {
            if (!Settings.canDrawOverlays(this)) {
                Intent i = new Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION,
                    Uri.parse("package:" + getPackageName()));
                startActivityForResult(i, OVERLAY_REQUEST);
            } else startFloating();
        });
        layout.addView(permission);

        Button start = new Button(this);
        start.setText("Mulai Floating AI");
        start.setOnClickListener(v -> {
            if (Settings.canDrawOverlays(this)) startFloating();
            else {
                Intent i = new Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION,
                    Uri.parse("package:" + getPackageName()));
                startActivityForResult(i, OVERLAY_REQUEST);
            }
        });
        layout.addView(start);

        Button stop = new Button(this);
        stop.setText("Tutup Floating Window");
        stop.setOnClickListener(v -> stopService(new Intent(this, FloatingService.class)));
        layout.addView(stop);

        setContentView(layout);
    }

    private void startFloating() {
        startService(new Intent(this, FloatingService.class));
        finish();
    }

    @Override protected void onActivityResult(int request, int result, Intent data) {
        super.onActivityResult(request, result, data);
        if (request == OVERLAY_REQUEST && Settings.canDrawOverlays(this)) startFloating();
    }
}
