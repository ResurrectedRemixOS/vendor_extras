/*
 * SPDX-FileCopyrightText: The Resurrected Remix Project
 * SPDX-License-Identifier: Apache-2.0
 */
package org.resurrection.livingcrimson;

import android.app.Activity;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Switch;
import android.widget.TextView;

/** Living crimson options. */
public class SettingsActivity extends Activity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        SharedPreferences prefs = getSharedPreferences(LivingCrimsonService.PREFS, MODE_PRIVATE);
        LinearLayout list = new LinearLayout(this);
        list.setOrientation(LinearLayout.VERTICAL);
        int pad = Math.round(24 * getResources().getDisplayMetrics().density);
        list.setPadding(pad, pad, pad, pad);
        TextView title = new TextView(this);
        title.setText(R.string.color);
        title.setTextSize(14);
        list.addView(title);
        RadioGroup colors = new RadioGroup(this);
        int[] labels = {R.string.color_crimson, R.string.color_accent, R.string.color_album};
        int mode = LivingCrimsonService.colorMode(prefs);
        for (int i = 0; i < labels.length; i++) {
            RadioButton button = new RadioButton(this);
            button.setId(i + 1);
            button.setText(labels[i]);
            button.setTextSize(17);
            colors.addView(button);
            if (i == mode) button.setChecked(true);
        }
        colors.setOnCheckedChangeListener((group, id) ->
                prefs.edit().putInt(LivingCrimsonService.KEY_COLOR, id - 1).apply());
        list.addView(colors);
        add(list, prefs, LivingCrimsonService.KEY_MOTION, R.string.motion, true);
        add(list, prefs, LivingCrimsonService.KEY_LIVELY, R.string.motion_lively, false);
        add(list, prefs, LivingCrimsonService.KEY_BLOOM, R.string.bloom, true);
        add(list, prefs, LivingCrimsonService.KEY_CHARGING, R.string.charging, true);
        add(list, prefs, LivingCrimsonService.KEY_AOD, R.string.aod, true);
        setContentView(list);
    }

    private void add(LinearLayout list, SharedPreferences prefs, String key, int label,
            boolean def) {
        Switch view = new Switch(this);
        view.setText(label);
        view.setTextSize(17);
        int pad = Math.round(14 * getResources().getDisplayMetrics().density);
        view.setPadding(0, pad, 0, pad);
        view.setChecked(prefs.getBoolean(key, def));
        view.setOnCheckedChangeListener((b, checked) -> prefs.edit().putBoolean(key, checked).apply());
        list.addView(view);
    }
}
