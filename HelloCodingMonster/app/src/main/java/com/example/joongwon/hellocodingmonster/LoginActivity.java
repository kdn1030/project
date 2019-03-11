package com.example.joongwon.hellocodingmonster;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;

public class LoginActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        EditText idText = (EditText) findViewByld(R.id.idText);
        EditText passwordText = (EditText) findViewByld(R.id.passwordText);
        Button loginButton = (Button) findViewByld(R.id.loginButton);
        TextView registerButton = (TexView) findViewByld(R.id.registerButton);

        registerButton.setOnClickListner(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                Intent registerIntent = new Intent(LoginActivity.this, RegisterActivity.class);
                LoginActivity.this.startActivity(Intent);
            }
        });

        }
    }

