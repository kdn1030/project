package com.example.joongwon.guestapp;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;

public class Fragment2 extends AppCompatActivity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // setContentView 로 fragment2 와 연결시켜주기
        setContentView(R.layout.fragment2);
    }
}
