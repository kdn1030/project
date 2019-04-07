
package com.example.joongwon.socket;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.support.annotation.WorkerThread;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

public class ChatActivity extends AppCompatActivity implements View.OnClickListener,  ReceiverThread.OnReceiveListener {

    private TextView mMessageTextView;
    private EditText mMessageEditText;
    private SenderThread mThread1;

    private  Socket mSocket = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_chat);

        mMessageTextView = (TextView) findViewById(R.id.message_text);
        mMessageEditText = (EditText) findViewById(R.id.message_edit);
        findViewById(R.id.send_button).setOnClickListener(this);

        new Thread(new Runnable() {
            @Override

            public void run() {
                try {
                    // 일단은 테스트 용으로 본인의 아이피를 입력해서 진행
                    mSocket = new Socket("192.168.219.155", 3004);
                    Log.d("ip", "ip + port ");
                    // 두번째 파라메터 로는 본인의 닉네임을 적어줌
                    mThread1 = new SenderThread(mSocket, "정중원2");
                    ReceiverThread thread2 = new ReceiverThread(mSocket);

                    thread2.setOnReceiveListener(ChatActivity.this);

                    mThread1.start();
                    thread2.start();

                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }

    @Override // 보내는거
    public void onClick(View v) {
        mThread1.sendMessage(mMessageEditText.getText().toString());
        Log.e("callback", "온 클릭");
    }

    @Override // 받는 콜백
    @WorkerThread
    public void onReceive(final String message) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                // 메세지 갱신
                mMessageTextView.append("\n" + message);
                Log.e("메세지 갱신", message);
            }
        });
    }

    @Override
    protected void onDestroy() {
        try {
            mSocket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        super.onDestroy();
    }
}
//밖에다두면 내부클래스가 아님. 다른 클래스임
// chat activity 안 에 클래스가 3개

// Thread가 액티비티에서 사용하는 자식같은느낌의 클래스
class ReceiverThread extends Thread {

    // 출력부분에 콜백 쓸 것임
    // 인터페이스 만들기
    interface  OnReceiveListener {
        void onReceive(String message);
    }
    OnReceiveListener mListener;

    public void setOnReceiveListener(OnReceiveListener listener){
        mListener = listener;
    } // 여기까지 interface 세트

    Socket socket;

    public ReceiverThread(Socket socket) {
        this.socket = socket;
    }
    @Override
    public void run() {
        try {
            BufferedReader reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            Log.d("receiver","run: ");
            while (true) {
                String str = reader.readLine();

                // 출력 -> Text View 에 뿌리게 하기
                // 출력이 되는 주체는 액티비티 쪽 ,작은애가 위에있는애한테 뭔가 하고싶음->걔를접근하려면 콜백을 써야함
                // System.out.println(str);
                if(mListener != null){
                    mListener.onReceive(str);
                }
            }
        } catch (Exception e) {
            Log.e("chat", "run: " + e.getMessage());
        }
    }
}


/**

 * 메시지의 발신을 담당하는 스레드

 */



class SenderThread extends Thread {

    Socket socket;
    String name;
    private PrintWriter mWriter;

    public SenderThread(Socket socket, String name) {
        this.socket = socket;
        this.name = name;
    }

    public  void close(){
        try {
            socket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void sendMessage(final String message){
        new Thread(new Runnable() {
            @Override
            public void run() {
                mWriter.println(message);
                mWriter.flush();
            }
        }).start();
    }

    @Override
    public void run() {
        try {
            // EditText에 입력한게 입력장치
            // BufferedReader 쓸 필요 x
            // BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
            // 스레드 시자작하면
            mWriter = new PrintWriter(socket.getOutputStream());

            // 제일 먼저 서버로 대화명을 송신
            mWriter.println(name);
            mWriter.flush();
        }
        catch (Exception e) {
            System.out.println(e.getMessage());
        }

    }

}






