/*
 * SPDX-FileCopyrightText: The Resurrected Remix Project
 * SPDX-License-Identifier: Apache-2.0
 */
package org.resurrection.livingcrimson;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RadialGradient;
import android.graphics.Shader;
import android.os.BatteryManager;
import android.os.Handler;
import android.os.Looper;
import android.os.SystemClock;
import android.service.wallpaper.WallpaperService;
import android.view.SurfaceHolder;

/**
 * The RR crimson glow as a live wallpaper. It draws only while visible, at about 30 frames a
 * second, and stops when the screen is off or an app covers it.
 */
public class LivingCrimsonService extends WallpaperService {
    static final String PREFS = "living_crimson";
    static final String KEY_ACCENT = "accent";
    static final String KEY_MOTION = "motion";
    static final String KEY_LIVELY = "lively";
    static final String KEY_BLOOM = "bloom";
    static final String KEY_CHARGING = "charging";

    @Override
    public Engine onCreateEngine() {
        return new GlowEngine();
    }

    private class GlowEngine extends Engine {
        private final Handler mHandler = new Handler(Looper.getMainLooper());
        private final Paint mPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        private final long mStart = SystemClock.elapsedRealtime();
        private boolean mVisible;
        private boolean mCharging;
        private int mLevel;
        private long mBloomAt = -1;
        private int mWidth;
        private int mHeight;
        private final Runnable mFrame = this::draw;

        private final BroadcastReceiver mReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                if (Intent.ACTION_USER_PRESENT.equals(intent.getAction())) {
                    mBloomAt = SystemClock.elapsedRealtime();
                } else if (Intent.ACTION_BATTERY_CHANGED.equals(intent.getAction())) {
                    mCharging = intent.getIntExtra(BatteryManager.EXTRA_PLUGGED, 0) != 0;
                    mLevel = intent.getIntExtra(BatteryManager.EXTRA_LEVEL, 0);
                }
                schedule(0);
            }
        };

        @Override
        public void onCreate(SurfaceHolder holder) {
            super.onCreate(holder);
            IntentFilter filter = new IntentFilter(Intent.ACTION_USER_PRESENT);
            filter.addAction(Intent.ACTION_BATTERY_CHANGED);
            registerReceiver(mReceiver, filter, Context.RECEIVER_EXPORTED);
        }

        @Override
        public void onDestroy() {
            super.onDestroy();
            unregisterReceiver(mReceiver);
            mHandler.removeCallbacks(mFrame);
        }

        @Override
        public void onVisibilityChanged(boolean visible) {
            mVisible = visible;
            if (visible) schedule(0);
            else mHandler.removeCallbacks(mFrame);
        }

        @Override
        public void onSurfaceChanged(SurfaceHolder holder, int format, int width, int height) {
            mWidth = width;
            mHeight = height;
            schedule(0);
        }

        @Override
        public void onSurfaceDestroyed(SurfaceHolder holder) {
            mVisible = false;
            mHandler.removeCallbacks(mFrame);
        }

        private void schedule(long delay) {
            mHandler.removeCallbacks(mFrame);
            if (mVisible) mHandler.postDelayed(mFrame, delay);
        }

        private void draw() {
            SharedPreferences prefs = getSharedPreferences(PREFS, MODE_PRIVATE);
            boolean motion = prefs.getBoolean(KEY_MOTION, true);
            boolean lively = prefs.getBoolean(KEY_LIVELY, false);
            int color = prefs.getBoolean(KEY_ACCENT, false)
                    ? getColor(android.R.color.system_accent1_500) : 0xFFC4002F;
            long now = SystemClock.elapsedRealtime();
            float t = motion ? (now - mStart) / (lively ? 2500f : 6000f) : 0f;
            float bloom = 0f;
            if (prefs.getBoolean(KEY_BLOOM, true) && mBloomAt > 0) {
                float b = (now - mBloomAt) / 1400f;
                bloom = b < 1f ? (float) Math.sin(Math.PI * b) : 0f;
                if (b >= 1f) mBloomAt = -1;
            }
            // With the lock screen charging glow on, the glow does the charging effect.
            boolean glow = true;
            try {
                glow = android.provider.Settings.System.getInt(getContentResolver(),
                        "charging_glow", 1) == 1;
            } catch (RuntimeException e) {
                // Not readable here; assume the glow handles charging.
            }
            float charge = prefs.getBoolean(KEY_CHARGING, true) && mCharging && !glow ? 1f : 0f;
            SurfaceHolder holder = getSurfaceHolder();
            Canvas canvas = null;
            try {
                canvas = holder.lockHardwareCanvas();
                if (canvas != null) paint(canvas, color, t, bloom, charge);
            } catch (RuntimeException e) {
                // Surface went away.
            } finally {
                if (canvas != null) holder.unlockCanvasAndPost(canvas);
            }
            if (motion || mBloomAt > 0) schedule(33);
        }

        private void paint(Canvas canvas, int color, float t, float bloom, float charge) {
            int w = mWidth;
            int h = mHeight;
            canvas.drawColor(0xFF0A0405);
            float drift = (float) Math.sin(t * 2 * Math.PI);
            float drift2 = (float) Math.cos(t * 2 * Math.PI * 0.73);
            float lift = 0.12f + 0.28f * bloom + charge * 0.18f * (0.5f + mLevel / 200f);
            float bright = 0.85f + 0.15f * bloom + 0.2f * charge;
            glow(canvas, w * (0.5f + 0.08f * drift), h * (1.08f - lift), w * 0.95f, color, bright);
            int light = blend(color, 0xFFFF2A55, 0.5f);
            glow(canvas, w * (0.68f - 0.18f * drift2), h * (1.0f - lift * 0.6f), w * 0.55f, light,
                    0.6f * bright);
        }

        private void glow(Canvas canvas, float cx, float cy, float r, int color, float strength) {
            int core = Color.argb((int) (255 * Math.min(1f, strength)), Color.red(color),
                    Color.green(color), Color.blue(color));
            mPaint.setShader(new RadialGradient(cx, cy, r,
                    new int[] {core, Color.argb(Color.alpha(core) / 3, Color.red(color),
                            Color.green(color), Color.blue(color)), Color.TRANSPARENT},
                    new float[] {0f, 0.5f, 1f}, Shader.TileMode.CLAMP));
            canvas.drawCircle(cx, cy, r, mPaint);
        }

        private int blend(int a, int b, float f) {
            return Color.rgb((int) (Color.red(a) * (1 - f) + Color.red(b) * f),
                    (int) (Color.green(a) * (1 - f) + Color.green(b) * f),
                    (int) (Color.blue(a) * (1 - f) + Color.blue(b) * f));
        }
    }
}
