package com.example.joongwon.hellocodingmonster;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.EditText;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        EditText idText = (EditText) findViewByld(R.id.idText);
        EditText passwordText = (EditText) findViewByld(R.id.passwordText);
        TextView welcomeMessage = (TextView) findViewById(R.id.welcomMessage);
    }
}
