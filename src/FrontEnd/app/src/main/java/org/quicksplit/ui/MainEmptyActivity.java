package org.quicksplit.ui;

import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

public class MainEmptyActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Intent activityIntent;

        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(MainEmptyActivity.this);
        String tokenAuth = preferences.getString("token", null);

        if (tokenAuth == null) {
            activityIntent = new Intent(this, LoginActivity.class);
        } else {
            activityIntent = new Intent(this, MainActivity.class);
        }

        startActivity(activityIntent);
        finish();
    }
}
