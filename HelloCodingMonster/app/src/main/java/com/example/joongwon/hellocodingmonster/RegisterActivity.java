package com.example.joongwon.hellocodingmonster;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.EditText;

public class RegisterActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        EditText idText = (EditText) findViewByld(R.id.idText);
        EditText passwordText = (EditText) findViewByld(R.id.passwordText);
        EditText nameText = (EditText) findViewByld(R.id.nameText);
        EditText ageText = (EditText) findViewByld(R.id.ageText);

        Button registerButton = (Button) findViewByld(R.id.registerButton);

    }
}
