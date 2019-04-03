package com.example.joongwon.socket;

import android.content.Context;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;


import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.OutputStream;
import java.net.Socket;


public class Chat1 extends AppCompatActivity {
    TextView txtMessage;
    Button btnConnect,btnSend;
    EditText editIp, editPort, editMessage;
    Handler msgHandler; // android.os.Handler
    SocketClient client;
    ReceiveThread receive;
    SendThread send;
    Socket socket;
    // LinkedList<SocketClient> threadList;
    Context context;
    String mac;

    // @SuppressLint("HandlerLeak")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.chat1);

        // 현재 액티비티 정보를 컨텍스트에 백업
        // 이따 와이파이 상태 체크할때 컨텍스트 정보 필요
        context = this; // 컨텍스트 정보 설정

        editIp =  findViewById(R.id.editIp);
        editPort =  findViewById(R.id.editPort);
        editMessage =  findViewById(R.id.editMessage);
        btnConnect =  findViewById(R.id.btnConnect);
        btnSend =  findViewById(R.id.btnSend);
        txtMessage =  findViewById(R.id.txtMessage);

        // 핸들러 작성 -> 네트워크 통신을 할 떄는 안드로이드에 제약이 있음
        // 네트워크 작업은 반드시 백그라운드 스레드를 써야 함
        // 지금처럼 스레드를 직접 구현해도 되고
        msgHandler = new Handler() {
            // Handler : 화면 고치는 일을 전담한다
            // 메시지를 수신했을 때 어떻게 할 것인가
            // 백그라운드 스레드에서 받은 메시지를 처리
            @Override
            public void handleMessage(Message msg) {
                if (msg.what == 1111) { // 값은 임의의 값, 필요없지만 여러군데에서 호출할 때 필요
                    // 채팅 서버로부터 수신한 메시지를 텍스튜에 추가
                    txtMessage.append(msg.obj.toString() + "\n");
                }
            }
        };
        // 서버에 접속하는 버튼
        btnConnect.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                client = new SocketClient(editIp.getText().toString()
                        , editPort.getText().toString());  // 서버의 IP와 PORT번호 전달
                client.start();
            }
        });
        // 메시지 전송 버튼
        btnSend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // 사용자가 입력한 메시지
                String message = editMessage.getText().toString();
                if(message != null && message.equals("")){ // null이 아니고 빈 값도 아니면
                    send = new SendThread(socket); // 전송용 스레드를 만들어서 호출할것이다
                    send.start();
                    editMessage.setText(""); // 입력 대화상자 : 빈 값으로 초기화
                }
            }
        });
    }
    // 1. 내부 클래스
    // 서버의 IP와 PORT번호 전달
    class SocketClient extends  Thread {
        boolean threadAlive; // 스레드의 동작 여부
        // 백키를 누르면 온풋으로 빠져나갔다가 온디스트로이로 액티비티가 종료됨
        // 스레드 돌릴 때 조심 -> 앱은 종료되는데 스레드가 죽지 않는 경우가 있음
        String ip;
        String port;

        OutputStream outputStream = null;
        //BufferedReader br = null;
        DataOutputStream output = null;

        public SocketClient(String ip, String port) {
            threadAlive = true; // true 이면 스레드 돌리고 false 이면 스레드 die
            this.ip = ip;
            this.port = port;
        }

        public void run() { // 스레드 start 요청이 되면 run 메소드 실행
            try {
                // 채팅 서버에 접속, 소켓을 만듬
                socket = new Socket(ip, Integer.parseInt(port));
                // 서버에 메시지를 전달하기 위한 스트림 생성
                output = new DataOutputStream(socket.getOutputStream());
                // 메시지 수신용 스레드 생성
                receive = new ReceiveThread(socket);
                receive.start();
                // 와이파이 정보 관리자 객체로부터 폰의 mac address를 가져와서
                // 채팅서버에 전달
                // 식별자로 mac address를 보내기로 했기 때문에 WIFI_SERVICE가 있음
                WifiManager mng = (WifiManager) context.getSystemService(WIFI_SERVICE);
                WifiInfo info = mng.getConnectionInfo();
                mac = info.getMacAddress(); // 맥 어드레스가 넘어감
                output.writeUTF(mac); //이 때
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }// end of SocketClient

    // 2. 내부 클래스
    // 서버에서 도착한 메시지를 받아서 핸들러한테 요청을 하면 핸들러가 화면에 표시하는 수신용 스레드
    // 메시지 수신용
    class ReceiveThread extends  Thread {
        Socket socket = null;
        DataInputStream input = null;
        // 생성자 소켓
        public ReceiveThread(Socket socket){
            this.socket=socket;
            try{
                // 채팅 서버로부터 메시지를 받기 위한 스트림 생성
                input = new DataInputStream(socket.getInputStream());
            }catch (Exception e){
                e.printStackTrace();
            }
        }
        public void run(){
            try{
                while (input != null){ //input 스트림이 존재하면
                    // 채팅 서버로부터 받은 메시지를 읽어들임
                    String msg=input.readUTF();
                    if(msg != null){
                        // 핸들러에게 전달할 메시지 객체
                        Message hdmsg=msgHandler.obtainMessage();
                        hdmsg.what=1111; // 메시지의 식별자 : what
                        hdmsg.obj=msg; // 메시지의 본문 : obj
                        // 핸들러에게 메시지 전달(화면 변경 요청)
                        msgHandler.sendMessage(hdmsg);
                    }
                }
            }catch (Exception e){
                e.printStackTrace();
            }
        }
    } // end of ReceiveThread

    // 3.내부 클래스 -> 폰에서 작성한 메시지를 채팅서버로 보내주는 역할을 하는 스레드
    class SendThread extends  Thread {
        Socket socket;
        String sendmsg = editMessage.getText().toString(); // 사용자가 입력한 문장
        DataOutputStream output;
        public SendThread(Socket socket){
            this.socket = socket;
            try{
                //채팅 서버로 메시지를 보내기 위한 스트림 생성
                output = new DataOutputStream(socket.getOutputStream());
            }catch (Exception e){
                e.printStackTrace();
            }
        }
        public void run(){
            try{
                if(output != null){ // 스트림이 존재하고
                    if(sendmsg != null){ // 메시지가 널이 아니면
                        // 채팅서버에 메시지 전달
                        output.writeUTF(mac +":"+sendmsg);
                    }
                }

            }catch (Exception e){
                e.printStackTrace();
            }
        }

    }
}

























