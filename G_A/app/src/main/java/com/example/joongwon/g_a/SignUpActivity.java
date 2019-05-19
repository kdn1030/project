package com.example.joongwon.g_a;

import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
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

public class SignUpActivity extends AppCompatActivity implements View.OnClickListener {

    private static final String TAG = "SignUpActivity";

    EditText birthdayEdit, idEdit, pwEdit, pwEdit2, nameEdit;
    Button okButton, idcheckButton;
    String birthday, id, pw, pw2, name;
    boolean idcheck = false, result = false;
    SharedPreferences sharedPreferences;
    SharedPreferences.Editor editor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.e(TAG, "onCreate in SignUpActivity");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);
        findViewById();
        sharedPreferences = getSharedPreferences("guest", MODE_PRIVATE);
        editor = sharedPreferences.edit();
    }

    private void findViewById() {
        birthdayEdit = findViewById(R.id.birthdayEdit);
        idEdit = findViewById(R.id.idEdit);
        pwEdit = findViewById(R.id.pwEdit);
        pwEdit2 = findViewById(R.id.pwEdit2);
        nameEdit = findViewById(R.id.nameEdit);
        okButton = findViewById(R.id.okButton);
        idcheckButton = findViewById(R.id.idcheckButton);
        okButton.setOnClickListener(this);
        idcheckButton.setOnClickListener(this);
    }

    private void sign() {
        if (result == true) {
            try {
                birthday = birthdayEdit.getText().toString();
                id = idEdit.getText().toString();
                pw = pwEdit.getText().toString();
                pw2 = pwEdit2.getText().toString();
                name = nameEdit.getText().toString();
            } catch (Exception e) {
                Toast toast = Toast.makeText(SignUpActivity.this, "입력하지 않은 사항이 있습니다.", Toast.LENGTH_LONG);
                toast.setGravity(Gravity.CENTER, 0, 0);
                toast.show();
            }
            if (birthday.length() >= 8) {
                if (id.length() >= 5) {
                    if (pw.equals(pw2)) {
                        if(name.length() >= 2) {
                            SingUp_Complete singUp_complete = new SingUp_Complete();
                            singUp_complete.execute(id, pw, name, birthday);
                        }else{
                            AlertDialog.Builder builder = new AlertDialog.Builder(SignUpActivity.this);
                            builder.setTitle("회원가입");
                            builder.setMessage("이름이 입력되지 않았습니다.");
                            AlertDialog alertDialog = builder.create();
                            alertDialog.show();
                        }
                    } else {
                        AlertDialog.Builder builder = new AlertDialog.Builder(SignUpActivity.this);
                        builder.setTitle("회원가입");
                        builder.setMessage("비밀번호를 다시 확인해주세요.");
                        AlertDialog alertDialog = builder.create();
                        alertDialog.show();
                    }
                } else {
                    AlertDialog.Builder builder = new AlertDialog.Builder(SignUpActivity.this);
                    builder.setTitle("회원가입");
                    builder.setMessage("ID는 5자리 이상 입력해주세요.");
                    AlertDialog alertDialog = builder.create();
                    alertDialog.show();
                }
            } else {
                AlertDialog.Builder builder = new AlertDialog.Builder(SignUpActivity.this);
                builder.setTitle("회원가입");
                builder.setMessage("생년월일은 8자리를 입력해주세요.");
                AlertDialog alertDialog = builder.create();
                alertDialog.show();
            }
        } else {
            AlertDialog.Builder builder = new AlertDialog.Builder(SignUpActivity.this);
            builder.setTitle("회원가입");
            builder.setMessage("아이디 중복확인이 필요합니다.");
            AlertDialog alertDialog = builder.create();
            alertDialog.show();
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.okButton: // 확인 버튼
                sign();
                break;

            case R.id.idcheckButton: // id 중복확인 버튼
                ID_Check id_check = new ID_Check();
                id_check.execute(id);
                break;
        }
    }

    // 회원가입
    class SingUp_Complete extends AsyncTask<String, Void, String> {

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
        String i;

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
                editor.putBoolean("login", false);
                editor.putString("id", i);
                editor.apply();
                Toast toast1 = Toast.makeText(getApplicationContext(), "회원가입이 완료되었습니다.", Toast.LENGTH_SHORT);
                toast1.setGravity(Gravity.CENTER, 0, 0);
                toast1.show();
                finish();
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
            i = strings[0];
            String postParameters = "id=" + strings[0] + "&pw=" + strings[1] + "&name=" + strings[2] + "&birthday=" + strings[3];

            try { // 서버 연결
                URL url = new URL("https://yoosongmi95.cafe24.com/signup.php");
                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                conn.setReadTimeout(3000);
                conn.setConnectTimeout(3000);
                conn.setRequestMethod("POST");
                conn.setDoInput(true);
                conn.connect();

                //안드로이드 -> 서버 파라미터 값 전달
                OutputStream outputStream = conn.getOutputStream();
                outputStream.write(postParameters.getBytes(
                        "UTF-8"));
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
//                Log.e("data",data);

                ok = true;


            } catch (Exception e) {
                e.printStackTrace();
                error = true;
                message = "네트워크 연결 실패\n3G/4G WIFI 연결을 확인해주세요.";
                return null;
            }
            return null;
        }
    }

    class ID_Check extends AsyncTask<String, Void, String> {

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
                if (idcheck == false) {
                    Toast toast1 = Toast.makeText(getApplicationContext(), "중복확인 성공", Toast.LENGTH_SHORT);
                    toast1.setGravity(Gravity.CENTER, 0, 0);
                    toast1.show();
                    result = true;
                } else {
                    Toast toast1 = Toast.makeText(getApplicationContext(), "중복된 ID 입니다.", Toast.LENGTH_SHORT);
                    toast1.setGravity(Gravity.CENTER, 0, 0);
                    toast1.show();
                    result = false;
                }
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
            String postParameters = "id=" + strings[0];

            try { // 서버 연결
                URL url = new URL("https://yoosongmi95.cafe24.com/id_check.php");
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

                    idcheck = false;
                } else { //중복값 있음
                    idcheck = true;
                }
                ok = true;
            } catch (Exception e) {
                error = true;
                message = "네트워크 연결 실패\n3G/4G WIFI 연결을 확인해주세요.";
                return null;
            }
            return null;
        }
    }
}
