package org.quicksplit.ui;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import org.quicksplit.R;

public class MapActivity extends AppCompatActivity {

    private static final String TAG = "MapActivity";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map);
    }
}
