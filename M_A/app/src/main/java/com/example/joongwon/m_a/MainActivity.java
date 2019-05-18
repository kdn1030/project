package com.example.joongwon.m_a;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import java.util.Date;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;

/* Main 화면*/

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    private static final String TAG = "MainActivity";

    Button memberlistButton, permissionButton, otpButton, otpcheckButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        findViewById();

        Log.e(TAG, "onCreate in MainActivity");
    }

    private void findViewById() {
        memberlistButton = findViewById(R.id.memberlistButton); // 회원 목록
        permissionButton = findViewById(R.id.permissionButton); // 권한 승인
        otpButton = findViewById(R.id.otpButton); // OTP 권한
        otpcheckButton = findViewById(R.id.otpcheckButton); // OTP 검증

        memberlistButton.setOnClickListener(this); // 회원 목록
        permissionButton.setOnClickListener(this); // 권한 승인
        otpButton.setOnClickListener(this); // OTP 권한
        otpcheckButton.setOnClickListener(this); // OTP 검증
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            // 회원 목록
            case R.id.memberlistButton:
                Log.e(TAG, "MemberList class will be started.");
                Intent intent = new Intent(MainActivity.this, MemberList.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP | Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
                break;

            // 권한 승인
            case R.id.permissionButton:
                Log.e(TAG, "PermissionActivity class will be started.");
                Intent intent2 = new Intent(MainActivity.this, PermissionActivity.class);
                intent2.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP | Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent2);
                break;

            // OTP 권한
            case R.id.otpButton:
                Log.e(TAG, "OTPActivity class will be started.");
                Intent intent3 = new Intent(MainActivity.this, OTPActivity.class);
                intent3.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP | Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent3);
                break;

            // OTP 검증
            case R.id.otpcheckButton:
                Log.e(TAG, "OTP_Result class will be started.");
                Intent intent4= new Intent(MainActivity.this, OTP_Result.class);
                intent4.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP | Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent4);
                break;

            default:
                Log.e(TAG, "invaild id, id = " + v.getId());
                break;
        }
    }
}
