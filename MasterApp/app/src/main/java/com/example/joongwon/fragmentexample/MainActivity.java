package com.example.joongwon.fragmentexample;

import android.content.Intent;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
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
                // Intent : 다른 화면이나 메인 -> sub, sub -> main 으로 올 때
                // 화면이동을 하게 해줌
                // 첫 번째 인자 : 현재 Activity
                // 두 번째 인자 : 넘어갈 Activity
                // MainActivity 에서 버튼을 클릭했을 때 Fragment1 로 넘어가게 해줌
                //Intent intent = new Intent(MainActivity.this, Fragment1.class);
               // startActivity(intent);
                // 팝업 창 띄우기
                // MainActivity 에서 나는 이 메시지를 팝업으로 띄우겠다 , 얼마나 토스트 메세지를 띄우게 할지 시간초를 설정해주는 것
                Toast.makeText(getApplicationContext(),"로그인 수신 완료",Toast.LENGTH_SHORT).show();
            }
        });

        // Button 을 눌렀을 때 부분
        btn2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Intent : 다른 화면이나 메인 -> sub, sub -> main 으로 올 때
                // 화면이동을 하게 해줌
                // 첫 번째 인자 : 현재 Activity
                // 두 번째 인자 : 넘어갈 Activity
                // MainActivity 에서 버튼을 클릭했을 때 Fragment2 로 넘어가게 해줌
               // Intent intent = new Intent(MainActivity.this, Fragment2.class);
               // startActivity(intent);
                // 팝업 창 띄우기
                // MainActivity 에서 나는 이 메시지를 팝업으로 띄우겠다 , 얼마나 토스트 메세지를 띄우게 할지 시간초를 설정해주는 것
                Toast.makeText(getApplicationContext(),"권한 승인 완료",Toast.LENGTH_SHORT).show();
            }
        });

        // Button 을 눌렀을 때 부분
        btn3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Intent : 다른 화면이나 메인 -> sub, sub -> main 으로 올 때
                // 화면이동을 하게 해줌
                // 첫 번째 인자 : 현재 Activity
                // 두 번째 인자 : 넘어갈 Activity
                // MainActivity 에서 버튼을 클릭했을 때 Fragment3 로 넘어가게 해줌
               // Intent intent = new Intent(MainActivity.this, Fragment3.class);
               // startActivity(intent);
                // 팝업 창 띄우기
                // MainActivity 에서 나는 이 메시지를 팝업으로 띄우겠다 , 얼마나 토스트 메세지를 띄우게 할지 시간초를 설정해주는 것
                Toast.makeText(getApplicationContext(),"OTP 수신 완료",Toast.LENGTH_SHORT).show();
            }
        });

        // Button 을 눌렀을 때 부분
        btn4.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Intent : 다른 화면이나 메인 -> sub, sub -> main 으로 올 때
                // 화면이동을 하게 해줌
                // 첫 번째 인자 : 현재 Activity
                // 두 번째 인자 : 넘어갈 Activity
                // MainActivity 에서 버튼을 클릭했을 때 Fragment3 로 넘어가게 해줌
                // Intent intent = new Intent(MainActivity.this, Fragment3.class);
                // startActivity(intent);
                // 팝업 창 띄우기
                // MainActivity 에서 나는 이 메시지를 팝업으로 띄우겠다 , 얼마나 토스트 메세지를 띄우게 할지 시간초를 설정해주는 것
                Toast.makeText(getApplicationContext(),"OTP 검증 완료",Toast.LENGTH_SHORT).show();
            }
        });




        /*
        btn1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
                Fragment1 fragment1 = new Fragment1();
                transaction.replace(R.id.frame, fragment1);
                transaction.commit(); // 저장, 새로고침 같은 개념
            }
        });

        btn2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
                Fragment2 fragment2 = new Fragment2();
                transaction.replace(R.id.frame, fragment2);
                transaction.commit(); // 저장, 새로고침 같은 개념
            }
        });

        btn3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
                Fragment3 fragment3 = new Fragment3();
                transaction.replace(R.id.frame, fragment3);
                transaction.commit(); // 저장, 새로고침 같은 개념
            }
        });*/


    }
}
