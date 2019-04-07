package com.example.joongwon.chatting;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

// test ssource ajldljalfal

    // MainActivity 에서 Chat1 으로 이동할 수 있게 함
    public void onClick(View v){
        Intent intent = null;
        switch (v.getId()){
            case R.id.button1:
                intent = new Intent(this,Chat1.class);
// break;;;;;;
                break;
        }
        startActivity(intent);
    }

}
