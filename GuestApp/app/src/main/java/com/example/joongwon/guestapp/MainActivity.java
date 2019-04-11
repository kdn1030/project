package com.example.joongwon.guestapp;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {

    Button btn1,btn2,btn3,btn4;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        btn1 = (Button)findViewById(R.id.btn_1);
        btn2 = (Button)findViewById(R.id.btn_2);
        btn3 = (Button)findViewById(R.id.btn_3);
        btn4 = (Button)findViewById(R.id.btn_4);

        // Button 을 눌렀을 때 부분
        btn1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                // 팝업 창 띄우기
                // MainActivity 에서 나는 이 메시지를 팝업으로 띄우겠다 , 얼마나 토스트 메세지를 띄우게 할지 시간초를 설정해주는 것
                Toast.makeText(getApplicationContext(),"회원가입 완료",Toast.LENGTH_SHORT).show();
            }
        });

        // Button 을 눌렀을 때 부분
        btn2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                // 팝업 창 띄우기
                // MainActivity 에서 나는 이 메시지를 팝업으로 띄우겠다 , 얼마나 토스트 메세지를 띄우게 할지 시간초를 설정해주는 것
                Toast.makeText(getApplicationContext(),"로그인 완료",Toast.LENGTH_SHORT).show();
            }
        });

        // Button 을 눌렀을 때 부분
        btn3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                // 팝업 창 띄우기
                // MainActivity 에서 나는 이 메시지를 팝업으로 띄우겠다 , 얼마나 토스트 메세지를 띄우게 할지 시간초를 설정해주는 것
                Toast.makeText(getApplicationContext(),"권한 요청 완료",Toast.LENGTH_SHORT).show();
            }
        });

        // Button 을 눌렀을 때 부분
        btn4.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                // 팝업 창 띄우기
                // MainActivity 에서 나는 이 메시지를 팝업으로 띄우겠다 , 얼마나 토스트 메세지를 띄우게 할지 시간초를 설정해주는 것
                Toast.makeText(getApplicationContext(),"OTP 요청 완료",Toast.LENGTH_SHORT).show();
            }
        });

    }
}
