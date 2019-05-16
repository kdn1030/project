package com.example.joongwon.m_a;

import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ListView;
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
import java.util.ArrayList;

/* 회원 목록*/

public class MemberList extends AppCompatActivity {

    ListView listView;
    ArrayList<PermissionListItem> items = new ArrayList<>();
    PermissionAdapter adapter;

    private String TAG_JSON = "webnautes", mJsonString;

    ArrayList<String> dbid = new ArrayList<>();
    ArrayList<String> dbname = new ArrayList<>();
    ArrayList<String> dbbirthday = new ArrayList<>();
    ArrayList<String> dbpermission = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_member_list); // 화면 디자인을 xml 로 정의해놓은 파일을 불러오는 것
        findViewById();
        adapter = new PermissionAdapter(); // adapter 초기화

        PermissionLIst permissionLIst = new PermissionLIst();
        permissionLIst.execute("12");
    }

    class PermissionAdapter extends BaseAdapter {
        // BaseAdapter -> Alt+Enter -> implement methods
        // 모든 필요한 method 가 아래와 같이 나타남
        @Override
        public int getCount() {
            return items.size();
        }

        public void addItem(PermissionListItem item) {
            items.add(item);
        }

        @Override
        public Object getItem(int position) {
            return items.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {

            PermissionListItemView view = new PermissionListItemView(getApplicationContext());
            PermissionListItem item = items.get(position);

            // Master 에서 회원목록을 볼 수 있게 함
            // 아이디 이름 생일 권한
            view.setText1(item.getId());
            view.setText2(item.getName());
            view.setText3(item.getBirthday());
            view.setText4(item.getPermission());
            return view;
        }
    }

    private void findViewById() {
        listView = findViewById(R.id.listView);
    }

    // 권한 승인
    class PermissionLIst extends AsyncTask<String, Void, String> {

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
                for (int i = 0; i < dbid.size(); i++) {
                    if(dbpermission.get(i).equals("1")){
                        // Master 가 Guest 의 권한 요청을 승인 하면 권한 여부가 "승인"으로 나옴
                        adapter.addItem(new PermissionListItem(dbid.get(i), dbname.get(i), dbbirthday.get(i), "승인"));
                        listView.setAdapter(adapter);
                    }else{
                        // Master 가 Guest 의 권한 요청을 취소 하면 권한 여부가 "승인"으로 나옴
                        adapter.addItem(new PermissionListItem(dbid.get(i), dbname.get(i), dbbirthday.get(i), "미승인"));
                        listView.setAdapter(adapter);
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
            String postParameters = "name=" + strings[0];
            try { // 서버 연결
                URL url = new URL("https://yoosongmi95.cafe24.com/user_list.php");
                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                conn.setReadTimeout(3000);
                conn.setConnectTimeout(3000);
                conn.setRequestMethod("POST");
                conn.setDoInput(true);
                conn.connect();

                // 안드로이드 -> 서버 파라미터 값 전달
                OutputStream outputStream = conn.getOutputStream();
                outputStream.write(postParameters.getBytes("UTF-8"));
                outputStream.flush();
                outputStream.close();

                // 서버 -> 안드로이드 파라미터 값 전달
                InputStream is;
                BufferedReader in;
                String data;

                is = conn.getInputStream();
                in = new BufferedReader(new InputStreamReader(is), 8 * 1024);

                String line;
                StringBuffer buffer = new StringBuffer();

                while ((line = in.readLine()) != null) {
                    buffer.append(line + "\n");
                }

                data = buffer.toString().trim();
                // Log.e("d", data);
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
                    dbid.add(item.getString("userID"));
                    dbname.add(item.getString("userName"));
                    dbbirthday.add(item.getString("userBirthday"));
                    dbpermission.add(item.getString("permission"));
                }
                // Log.e("완료되고", "0");
            } catch (JSONException e) {
            }
        }
    }
}
