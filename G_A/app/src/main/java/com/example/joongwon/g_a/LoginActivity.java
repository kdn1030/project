package com.example.joongwon.g_a;

import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;

public class LoginActivity extends AppCompatActivity implements View.OnClickListener {
    Button okButton;
    EditText idEdit, pwEdit;
    String id, pw;
    SharedPreferences sharedPreferences;
    SharedPreferences.Editor editor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState); // Always call the superclass first
        setContentView(R.layout.activity_login);
        findViewById();
        sharedPreferences = getSharedPreferences("guest", MODE_PRIVATE);
        editor = sharedPreferences.edit();
    }

    private void findViewById() {
        okButton = findViewById(R.id.okButton);
        idEdit = findViewById(R.id.idEdit);
        pwEdit = findViewById(R.id.pwEdit);
        okButton.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.okButton:
                if(idEdit.getText().toString().length() > 0 || pwEdit.getText().toString().length() > 0){
                    id = idEdit.getText().toString();
                    pw = pwEdit.getText().toString();
                    Login login = new Login();
                    login.execute(id, pw);
                }else{
                    Toast toast = Toast.makeText(LoginActivity.this, "입력하지 않은 사항이 있습니다.", Toast.LENGTH_LONG);
                    toast.setGravity(Gravity.CENTER, 0, 0);
                    toast.show();
                }
                break;
        }
    }

    // 로그인
    class Login extends AsyncTask<String, Void, String> {

        // AsyncTask -> 간단하게 새로운 스레드를 생성해서 작업할 수 있음

        //   ㅁ(메인 스레드) 일을 하다가
        //    |
        //    v  ㅡ> ㅁ(일하는 스레드) 사용자가 다른일을 해야 하는 경우
        //    |      |
        //    v      v (doInBackground 시점)
        // (서로 자기 일을 함) -> 총 일이 2개
        //    |       |
        //    v       v (doInBackground 시점) -> 일이 다 끝나서 return 을 해주면
        //   ㅁ <ㅡㅡㅡ
        //   위 시점에서 다시 만나는 순간이 옴 -> onPostExecute

        String message = "";

        // ok, error -> 처음에 false 로 초기화
        boolean ok = false, error = false;

        @Override
        protected void onPreExecute() {
            // 메인 스레드에서 실행
            super.onPreExecute();
        }

        @Override
        protected void onPostExecute(String s) {
            // 메인 스레드에서 실행
            super.onPostExecute(s);

            if (ok == true) {
                editor.putBoolean("login", true);
                editor.putString("id",id);
                editor.apply();
                Toast toast1 = Toast.makeText(getApplicationContext(), "로그인 성공", Toast.LENGTH_SHORT);
                toast1.setGravity(Gravity.CENTER, 0, 0);
                toast1.show();
                finish();
            } else {
                editor.putBoolean("login", false);
                editor.apply();
                Toast toast1 = Toast.makeText(getApplicationContext(), "로그인 실패", Toast.LENGTH_SHORT);
                toast1.setGravity(Gravity.CENTER, 0, 0);
                toast1.show();
            }
            if (error == true) {
                Toast toast1 = Toast.makeText(getApplicationContext(), message, Toast.LENGTH_SHORT);
                toast1.setGravity(Gravity.CENTER, 0, 0);
                toast1.show();
                finish();
            }
        }

        @Override
        protected String doInBackground(String... strings) {
            // 새로 만든 스레드에 있는 영역
            String postParameters = "id=" + strings[0] + "&pw=" + strings[1];

            try { // 서버 연결
                URL url = new URL("https://yoosongmi95.cafe24.com/login.php");
                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                conn.setReadTimeout(3000);
                conn.setConnectTimeout(3000);
                conn.setRequestMethod("POST");
                conn.setDoInput(true);
                conn.connect();

                //안드로이드 -> 서버 파라미터 값 전달
                OutputStream outputStream = conn.getOutputStream();
                outputStream.write(postParameters.getBytes("UTF-8"));
                outputStream.flush();
                outputStream.close();

                //서버 -> 안드로이드 파라미터 값 전달
                InputStream is = null;
                BufferedReader in = null;
                String data = "";

                is = conn.getInputStream();
                in = new BufferedReader(new InputStreamReader(is), 8 * 1024);
                String line = null;
                StringBuffer buffer = new StringBuffer();
                while ((line = in.readLine()) != null) {
                    buffer.append(line + "\n");
                }
                data = buffer.toString().trim();
//                Log.e("da",data);
                if (data.equals("2")) { // 중복값 없음
                    ok = false;
                } else { //중복값 있음
                    ok = true;
                }

            } catch (Exception e) {
                error = true;
                message = "네트워크 연결 실패\n3G/4G WIFI 연결을 확인해주세요.";
                return null;
            }
            return null;
        }
    }
}


