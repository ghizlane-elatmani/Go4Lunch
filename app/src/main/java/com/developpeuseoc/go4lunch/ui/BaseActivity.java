package com.developpeuseoc.go4lunch.ui;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

import com.developpeuseoc.go4lunch.R;

public abstract class BaseActivity extends AppCompatActivity {

    // Lifecycle

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.setContentView(this.getFragmentLayout());
    }

    public abstract int getFragmentLayout();

}