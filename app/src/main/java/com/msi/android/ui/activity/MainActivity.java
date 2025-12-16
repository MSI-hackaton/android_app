package com.msi.android.ui.activity;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import com.msi.android.App;
import com.msi.android.R;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        ((App) getApplication())
                .getAppComponent()
                .inject(this);

        setContentView(R.layout.activity_main);
    }
}
