package com.example.joongwon.g_a;

// 블루투스
import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
// 블루투스

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Date;
import java.util.Timer;
import java.util.TimerTask;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;

// 블루투스
import app.akexorcist.bluetotohspp.library.BluetoothSPP;
import app.akexorcist.bluetotohspp.library.BluetoothState;
import app.akexorcist.bluetotohspp.library.DeviceList;
// 블루투스
public class MainActivity extends AppCompatActivity implements View.OnClickListener {
    Button otpButton, signupButton, loginButton, permissionButton, button;
    boolean login = false, otp = false, done = false;

    // SharedPreferences :  간단한 데이터들을 저장하는 용도
    // 파일 형태로 Data 를 저장하며 해당 앱이 삭제 되기 전까지 유지 됨
    // 앱이 삭제 되면 사용했던 SharedPreferences 도 삭제 됨
    SharedPreferences sharedPreferences;
    SharedPreferences.Editor editor;

    TextView state, textView, textView2, textView3;
    EditText editText;
    String id, otp_number, re;

    private String TAG_JSON = "webnautes", mJsonString;
    int success = 0, time = 30;

    ////////////////////////////////////////////////////////////////////////////////////

    /* OTP 를 만들고 검증하는 class 를 생성하고, 상수를 정의하기*/

    private static final long DISTANCE = 30000; // 30 sec 마다 OTP 번호 생성하기

    // HMAC -> " The keyed-Hash Message Authentication Code "
    // 해시를 통한 MAC 인데 Key 를 사용하는 것 -> 정보의 무결성을 검증하는 방법
    // HMAC_SHA1_ALGORITHM 사용
    private static final String ALGORITHM = "HmacSHA1";
    private static final byte[] SECRET_KEY = "define your secret key here".getBytes();

    /**/
    private BluetoothSPP bt;
    ////////////////////////////////////////////////////////////////////////////////////

    @Override
    protected void onResume() {
        super.onResume();
        login = sharedPreferences.getBoolean("login", false);
        id = sharedPreferences.getString("id", "");

        // 로그인에 성공하면 상단 중간에 TextMessage -> "로그인 성공!"
        if (login == true) {
            state.setText("로그인 성공!");

            User_OTP_Permission user_otp_permission = new User_OTP_Permission();
            user_otp_permission.execute(id);

            // 로그인을 하지 않은 상태라면 상단 중간에 TextMessage -> "로그인이 필요합니다."
            // ToastMessage : "로그인이 필요합니다." 중앙에 띄움
        } else {
            state.setText("로그인이 필요합니다.");
            Toast toast1 = Toast.makeText(getApplicationContext(), "로그인이 필요합니다.", Toast.LENGTH_SHORT);
            toast1.setGravity(Gravity.CENTER, 0, 0);
            toast1.show();
        }
    }

    Timer timer = new Timer();
    TimerTask timerTask = new TimerTask() {
        @Override
        public void run() {
            try {
                otp_number = String.format("%06d", create(new Date().getTime() / DISTANCE));
            } catch (Exception e) {
                e.printStackTrace();
            }
            MainActivity.this.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    textView.setText(otp_number);
                }
            });
        }
    };

    Timer timer2 = new Timer();
    TimerTask timerTask2 = new TimerTask() {
        @Override
        public void run() {
            // 30초마다 OTP 번호가 바뀜
            // 30,29,....1 -> 반복

            time = time - 1;
            if (time == 0) {
                time = 30;
            }
            MainActivity.this.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    // textView3 에 30초 숫자가 나오게 함
                    textView3.setText(String.valueOf(time));
                }
            });
        }
    };

    /////////////////////////////////////////////////////////////

    /* 비밀키와 시간에 따라 Hash 처리하여 패스워드를 생성하는 메소드*/

    private static long create(long time) throws Exception {
        byte[] data = new byte[8];

        long value = time;
        for (int i = 8; i-- > 0; value >>>= 8) {
            data[i] = (byte) value;
        }
        // Mac(Message Authentication Code)
        Mac mac = Mac.getInstance(ALGORITHM);
        mac.init(new SecretKeySpec(SECRET_KEY, ALGORITHM));

        byte[] hash = mac.doFinal(data);
        int offset = hash[20 - 1] & 0xF;

        long truncatedHash = 0;
        for (int i = 0; i < 4; ++i) {
            truncatedHash <<= 8;
            truncatedHash |= hash[offset + i] & 0xFF;
        }

        truncatedHash &= 0x7FFFFFFF;
        truncatedHash %= 1000000;

        return truncatedHash;
    }

    ///////////////////////////////////////////////////////////

    // Activity 가 종료될 때 onDestroy 함수 콜백 됨
    // Activity 종료 시 login -> false , 즉 logout 되게 함
    @Override
    protected void onDestroy() {
        editor.putBoolean("login", false);
        editor.remove("id");
        editor.apply();
        Log.e("?", "?");
        if (done == true) {
            timerTask.cancel(); // timerTask 작업 취소
            timerTask2.cancel(); // timerTask2 작업 취소
            done = false;
            otp_number = "";
            otp = false;
        }
        super.onDestroy();

        // 블루투스
        bt.stopService(); // 블루투스 중지
        // 블루투스

    }

    // Activity 의 상태 복원하기
    // Activity 가 Destroy 되었다가 다시 복원될 때 -> onCreate 함수
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        findViewById();
        sharedPreferences = getSharedPreferences("guest", MODE_PRIVATE);
        editor = sharedPreferences.edit();

        // 블루투스
        bt = new BluetoothSPP(this); //Initializing

        if (!bt.isBluetoothAvailable()) { //블루투스 사용 불가
            Toast.makeText(getApplicationContext()
                    , "Bluetooth is not available"
                    , Toast.LENGTH_SHORT).show();
            finish();
        }

        bt.setOnDataReceivedListener(new BluetoothSPP.OnDataReceivedListener() { //데이터 수신
            public void onDataReceived(byte[] data, String message) {
                Toast.makeText(MainActivity.this, message, Toast.LENGTH_SHORT).show();
            }
        });

        bt.setBluetoothConnectionListener(new BluetoothSPP.BluetoothConnectionListener() { //연결됐을 때
            public void onDeviceConnected(String name, String address) {
                Toast.makeText(getApplicationContext()
                        , "Connected to " + name + "\n" + address
                        , Toast.LENGTH_SHORT).show();
            }

            public void onDeviceDisconnected() { //연결해제
                Toast.makeText(getApplicationContext()
                        , "Connection lost", Toast.LENGTH_SHORT).show();
            }

            public void onDeviceConnectionFailed() { //연결실패
                Toast.makeText(getApplicationContext()
                        , "Unable to connect", Toast.LENGTH_SHORT).show();
            }
        });

        Button btnConnect = findViewById(R.id.btnConnect); //연결시도
        btnConnect.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                if (bt.getServiceState() == BluetoothState.STATE_CONNECTED) {
                    bt.disconnect();
                } else {
                    Intent intent = new Intent(getApplicationContext(), DeviceList.class);
                    startActivityForResult(intent, BluetoothState.REQUEST_CONNECT_DEVICE);
                }
            }
        });
        //
        findViewById();

        // 블루투스

    }

    // 블루투스
    public void onStart() {
        super.onStart();
        if (!bt.isBluetoothEnabled()) { //
            Intent intent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivityForResult(intent, BluetoothState.REQUEST_ENABLE_BT);
        } else {
            if (!bt.isServiceAvailable()) {
                bt.setupService();
                bt.startService(BluetoothState.DEVICE_OTHER); //DEVICE_ANDROID는 안드로이드 기기 끼리
                setup();
            }
        }
    }

    public void setup() {
        Button btnSend = findViewById(R.id.btnSend); //데이터 전송
        btnSend.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                bt.send("Text", true);
            }
        });
    }

    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == BluetoothState.REQUEST_CONNECT_DEVICE) {
            if (resultCode == Activity.RESULT_OK)
                bt.connect(data);
        } else if (requestCode == BluetoothState.REQUEST_ENABLE_BT) {
            if (resultCode == Activity.RESULT_OK) {
                bt.setupService();
                bt.startService(BluetoothState.DEVICE_OTHER);
                setup();
            } else {
                Toast.makeText(getApplicationContext()
                        , "Bluetooth was not enabled."
                        , Toast.LENGTH_SHORT).show();
                finish();
            }
        }
    }
    // 블루투스


    @Override
    public void onClick(View v) {
        switch (v.getId()) {

            case R.id.button: // OTP 검증 버튼
                if (login == true) {
                    if (editText.getText().toString().length() != 0) {
                        if (otp == false) { // OTP 요청을 하지 않고 OTP 검증 버튼을 눌렀을 경우
                            Toast toast1 = Toast.makeText(getApplicationContext(), "OTP 권한이 필요합니다.\n요청 후 관리자에게 문의하세요.", Toast.LENGTH_SHORT);
                            toast1.setGravity(Gravity.CENTER, 0, 0);
                            toast1.show();
                        } else {
                            if (editText.getText().toString().equals(otp_number)) {
                                OTP_Result otp_result = new OTP_Result();
                                otp_result.execute(id);
                            }else{ // OTP 번호를 올바르게 입력하지 않았을 경우
                                Toast toast1 = Toast.makeText(getApplicationContext(), "인증번호가 잘못되었습니다.", Toast.LENGTH_SHORT);
                                toast1.setGravity(Gravity.CENTER, 0, 0);
                                toast1.show();
                            }
                        }
                    } else { // Master 에서 OTP 권한을 승인하고 OTP 번호를 생성시켜줬는데, OTP 번호를 입력하지 않고 OTP 검증 버튼을 눌렀을 경우
                        Toast toast1 = Toast.makeText(getApplicationContext(), "인증번호가 입력되지 않았습니다.", Toast.LENGTH_SHORT);
                        toast1.setGravity(Gravity.CENTER, 0, 0);
                        toast1.show();
                    }
                } else { // 로그인을 하지 않은 상태에서 OTP 검증 버튼을 눌렀을 경우
                    Toast toast1 = Toast.makeText(getApplicationContext(), "로그인이 되지 않았습니다.", Toast.LENGTH_SHORT);
                    toast1.setGravity(Gravity.CENTER, 0, 0);
                    toast1.show();
                }
                break;

            case R.id.otpButton: // OTP 요청 버튼
                if (login == true) {
                    PermissionSuccess permissionSuccess = new PermissionSuccess();
                    permissionSuccess.execute(id);
                }
                break;

            case R.id.signupButton: // 회원가입 버튼
                Intent intent = new Intent(MainActivity.this, SignUpActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP | Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
                break;

            case R.id.loginButton: // 로그인 버튼
                if (login == false) {
                    Intent intent2 = new Intent(MainActivity.this, LoginActivity.class);
                    intent2.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP | Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    startActivity(intent2);
                }
                break;

            case R.id.permissionButton: // 권한 요청 버튼
                if (login == true) {
                    Permission permission = new Permission();
                    permission.execute(id, "1");
                } else {
                    Toast toast1 = Toast.makeText(getApplicationContext(), "로그인이 필요합니다.", Toast.LENGTH_SHORT);
                    toast1.setGravity(Gravity.CENTER, 0, 0);
                    toast1.show();
                }
                break;
        }
    }

    private void findViewById() {
        otpButton = findViewById(R.id.otpButton);
        signupButton = findViewById(R.id.signupButton);
        loginButton = findViewById(R.id.loginButton);
        permissionButton = findViewById(R.id.permissionButton);
        state = findViewById(R.id.state);
        textView = findViewById(R.id.textView);
        textView2 = findViewById(R.id.textView2);
        editText = findViewById(R.id.editText);
        button = findViewById(R.id.button);
        textView3 = findViewById(R.id.textView3);
        button.setOnClickListener(this);
        otpButton.setOnClickListener(this);
        signupButton.setOnClickListener(this);
        loginButton.setOnClickListener(this);
        permissionButton.setOnClickListener(this);
    }

    // 권한 요청
    class Permission extends AsyncTask<String, Void, String> {

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
                Toast toast1 = Toast.makeText(getApplicationContext(), "권한요청 성공\n관리자에게 문의해주세요.", Toast.LENGTH_SHORT);
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
            String postParameters = "id=" + strings[0] + "&request=" + strings[1];

            try { // 서버 연결
                URL url = new URL("https://yoosongmi95.cafe24.com/request.php");
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

                ok = true;
            } catch (Exception e) {
                error = true;
                message = "네트워크 연결 실패\n와이파이 연결을 확인해주세요.";
                return null;
            }
            return null;
        }
    }

    // 권한요청이 승인 된 경우 -> OTP 요청을 할 수 있다
    class PermissionSuccess extends AsyncTask<String, Void, String> {

        String message = "";
        boolean ok = false, error = false;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            if (ok == true) {
                if (success == 1) {
                    if(otp == false) { // OTP 가 요청이 안 된 경우
                        textView2.setVisibility(View.VISIBLE);
                        textView2.setText("OTP 번호를 요청중입니다.\n승인 시 OTP 번호가 생성됩니다.");
                    }
                } else { // 권한 승인이 되지 않은 경우
                    Toast toast1 = Toast.makeText(getApplicationContext(), "아이디 권한승인이 되지 않았습니다.\n관리자에게 문의해주세요.", Toast.LENGTH_LONG);
                    toast1.setGravity(Gravity.CENTER, 0, 0);
                    toast1.show();
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

            String postParameters = "id=" + strings[0];
            try { // 서버 연결
                URL url = new URL("https://yoosongmi95.cafe24.com/permission_success.php");
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
                //Log.e("d", data);
                mJsonString = data;
                showResult();

                ok = true;
            } catch (Exception e) {
                e.printStackTrace();
                error = true;
                message = "네트워크 연결 실패\n3G/4G WIFI 연결을 확인해주세요.";
                return null;
            }
            return null;
        }

        private void showResult() { // JSON 으로 만든 리스트 형태의 DB 를 가져옴
            try {
                JSONObject jsonObject = new JSONObject(mJsonString);
                JSONArray jsonArray = jsonObject.getJSONArray(TAG_JSON);

                for (int i = 0; i < jsonArray.length(); i++) {
                    JSONObject item = jsonArray.getJSONObject(i);
                    success = Integer.parseInt(item.getString("permission"));
                }
                //Log.e("완료되고", "0");
            } catch (JSONException e) {
            }
        }
    }

    class OTP_Result extends AsyncTask<String, Void, String> {

        String message = "";
        boolean ok = false, error = false;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();

        }

        // Master 가 OTP 권한에서 승인 해주면 OTP 번호 생성 됨
        // 생성된 OTP 번호를 바탕으로 알맞게 입력했을 경우
        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            if (ok == true) {
                AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
                builder.setTitle("OTP 인증");
                builder.setMessage("OTP 인증 성공\n관리자에게 문의해주세요.");
                AlertDialog alertDialog = builder.create();
                alertDialog.show();

                // Master 가 Guest 의 OTP 요청을 승인하기 전이므로 textView,textView2,textView2 안보이게 함
                textView.setVisibility(View.INVISIBLE);
                textView2.setVisibility(View.INVISIBLE);
                textView3.setVisibility(View.INVISIBLE);
                timerTask.cancel();
                timerTask2.cancel();
                done = false;
                otp = false;
                otp_number = "";
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

            String postParameters = "id=" + strings[0];
            try { // 서버 연결
                URL url = new URL("https://yoosongmi95.cafe24.com/otp_result.php");
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
//                Log.e("d", data);
                mJsonString = data;

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

    class User_OTP_Permission extends AsyncTask<String, Void, String> {

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
                if (re.equals("1")) {
                    if (done == false) { // OTP 번호 입력 해서 OTP 검증이 안 된 상태
                        textView2.setVisibility(View.VISIBLE); // OTP 인증번호
                        textView.setVisibility(View.VISIBLE); // OTP 번호(6자리 난수)
                        textView3.setVisibility(View.VISIBLE); // 시간 -> 30초
                        timer.schedule(timerTask, 0, 30000);
                        timer2.schedule(timerTask2, 0, 1000);
                        done = true;
                        otp = true;
                    }
                } else {
                    Toast toast1 = Toast.makeText(getApplicationContext(), "OTP 권한이 승인되지 않았습니다.\n관리자에게 문의해주세요.", Toast.LENGTH_LONG);
                    toast1.setGravity(Gravity.CENTER, 0, 0);
                    toast1.show();
                    if (done == true) { // OTP 번호 입력 해서 OTP 검증이 된 상태
                        timerTask.cancel();
                        timerTask2.cancel();
                        done = false;
                        otp_number = "";
                        textView.setVisibility(View.INVISIBLE);
                        textView2.setVisibility(View.INVISIBLE);
                        textView3.setVisibility(View.INVISIBLE);
                        otp = false; // OTP 번호 승인이 되면 곧 바로 미승인으로 바뀌게 함 -> 보안을 위해서
                    }
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
                URL url = new URL("https://yoosongmi95.cafe24.com/guest_otp_permission.php");
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
//                Log.e("d", data);
                mJsonString = data;
                showResult();

                ok = true;
            } catch (Exception e) {
                e.printStackTrace();
                error = true;
                message = "네트워크 연결 실패\n3G/4G WIFI 연결을 확인해주세요.";
                return null;
            }
            return null;
        }

        private void showResult() { // JSON 으로 만든 리스트형태의 DB 를 가져옴
            try {
                JSONObject jsonObject = new JSONObject(mJsonString);
                JSONArray jsonArray = jsonObject.getJSONArray(TAG_JSON);

                for (int i = 0; i < jsonArray.length(); i++) {
                    JSONObject item = jsonArray.getJSONObject(i);
                    re = item.getString("otp_permission");
                }

            } catch (JSONException e) {
            }
        }
    }
}

