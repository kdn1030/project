package com.example.joongwon.m_a;

import android.content.DialogInterface;
import android.os.AsyncTask;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Adapter;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
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

/* 권한 승인 */

public class PermissionActivity extends AppCompatActivity{
    ListView listView;

    // 각각의 Guest 정보가 들어갈 수 있도록 items 를 만듦
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
        setContentView(R.layout.activity_permission);
        findViewById();
        adapter = new PermissionAdapter(); // adapter 초기화
        PermissionLIst permissionLIst = new PermissionLIst();
        permissionLIst.execute("12");
    }

    // Adapter 에 추가된 데이터를 저장하기 위한
    class PermissionAdapter extends BaseAdapter {
        // BaseAdapter -> Alt+Enter -> implement methods
        // 모든 필요한 method 가 아래와 같이 나타남
        @Override
        public int getCount() {
            // 현재 Guest 의 수를 반환
            return items.size();
        }

        public void addItem(PermissionListItem item) {
            // PermissionListItem 추가
            items.add(item);
        }

        @Override
        public Object getItem(int position) {
            // 특정한 Guest 를 반환할 수 있도록 함
            return items.get(position);
        }

        @Override
        public long getItemId(int position) {
            // 그대로 position 반환하면 됨
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            // Guest 에 대한 View 를 보여줌
            PermissionListItemView view = new PermissionListItemView(getApplicationContext());
            PermissionListItem item = items.get(position);

            // item 에 있는 Guest 의 Id,Name,Birthday,Permission 가져옴
            view.setText1(item.getId());
            view.setText2(item.getName());
            view.setText3(item.getBirthday());
            view.setText4(item.getPermission());

            // 해당 view 반환
            return view;
        }
    }

    // Master 가 Guest 의 권한요청을 승인하는 부분(승인을 해도 되고 해주지 않아도 됨)
    // 승인 했을 경우 Master 가 임의로 Guest 의 권한을 취소할 수 있음
    private AdapterView.OnItemClickListener itemClickListener = new AdapterView.OnItemClickListener() {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            final int index = (int) parent.getItemIdAtPosition(position);
            if (dbpermission.get(index).equals("0")) {
                AlertDialog.Builder builder = new AlertDialog.Builder(PermissionActivity.this);
                builder.setTitle("권한 승인");
                builder.setMessage(dbname.get(index) + " 님의 권한을 승인하시겠습니까?");
                builder.setPositiveButton("확인", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                        Permission permission = new Permission();
                        permission.execute(dbid.get(index), "1");
                    }
                });
                builder.setNegativeButton("취소", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });
                AlertDialog alertDialog = builder.create();
                alertDialog.show();
            } else {
                AlertDialog.Builder builder = new AlertDialog.Builder(PermissionActivity.this);
                builder.setTitle("권한 취소");
                builder.setMessage(dbname.get(index) + " 님의 권한을 취소하시겠습니까?");
                builder.setPositiveButton("확인", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                        Permission permission = new Permission();
                        permission.execute(dbid.get(index), "0");
                    }
                });
                builder.setNegativeButton("취소", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });
                AlertDialog alertDialog = builder.create();
                alertDialog.show();
            }
        }
    };

    private void findViewById() {
        listView = findViewById(R.id.listView);
        listView.setOnItemClickListener(itemClickListener);
    }

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

        String message = "",per = "";
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
                if(per.equals("1")){ // 권한 승인 시
                    Toast toast1 = Toast.makeText(getApplicationContext(), "권한 부여함", Toast.LENGTH_SHORT);
                    toast1.setGravity(Gravity.CENTER, 0, 0);
                    toast1.show();
                    PermissionLIst permissionLIst = new PermissionLIst();
                    permissionLIst.execute("12");
                }else{ // 권한 취소 시
                    Toast toast1 = Toast.makeText(getApplicationContext(), "권한 삭제함", Toast.LENGTH_SHORT);
                    toast1.setGravity(Gravity.CENTER, 0, 0);
                    toast1.show();
                    PermissionLIst permissionLIst = new PermissionLIst();
                    permissionLIst.execute("12");
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
            per = strings[1];
            String postParameters = "id=" + strings[0] + "&permission=" + strings[1];

            try { // 서버 연결
                URL url = new URL("https://yoosongmi95.cafe24.com/permission.php");
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
                InputStream is;
                BufferedReader in;
                //String data = "";

                is = conn.getInputStream();
                in = new BufferedReader(new InputStreamReader(is), 8 * 1024);
                String line = null;
                StringBuffer buffer = new StringBuffer();
                while ((line = in.readLine()) != null) {
                    buffer.append(line + "\n");
                }
                //data = buffer.toString().trim();
//                Log.e("da", data);

                ok = true;
            } catch (Exception e) {
                error = true;
                message = "네트워크 연결 실패\n3G/4G WIFI 연결을 확인해주세요.";
                return null;
            }
            return null;
        }
    }

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
        boolean ok = false, error = false;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            items.clear();
            dbpermission.clear();
            dbbirthday.clear();
            dbid.clear();
            dbname.clear();
        }

        @Override
        protected void onPostExecute(String s) {
            // 메인 스레드에서 실행
            super.onPostExecute(s);
            if (ok == true) {

                for (int i = 0; i < dbid.size(); i++) {
                    if (dbpermission.get(i).equals("1")) {
                        // Master 가 Guest 의 권한 요청을 승인 하면 권한 여부가 "승인"으로 나옴
                        adapter.addItem(new PermissionListItem(dbid.get(i), dbname.get(i), dbbirthday.get(i), "승인"));
                        listView.setAdapter(adapter);
                    } else {
                        // Master 가 Guest 의 권한 요청을 취소 하면 권한 여부가 "미승인"으로 나옴
                        adapter.addItem(new PermissionListItem(dbid.get(i), dbname.get(i), dbbirthday.get(i), "미승인"));
                        listView.setAdapter(adapter);
                    }
                }
                adapter.notifyDataSetChanged();
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
                URL url = new URL("https://yoosongmi95.cafe24.com/permission_list.php");
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