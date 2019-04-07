import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;

public class Server {
    // 서버 소켓 생성
    private ServerSocket mserverSocket;
    private Socket mSocket;


    public Server(){

        try {
             mserverSocket = new ServerSocket(3000);
            System.out.println("서버 시작!!!");
            //  위 문장을 호출하는 순간 스레드가 멈춰 있게 되고
            //  정확히는 serverSocket.accept(); 에서 멈춰 있음

            // 연결 요청이 들어오면 연결을 하면서 쭉 진행이 됨
            // accept 내부에서 스레드 스립이나 이런게 걸려있다고 보면됨
            // 아에 그냥 계속 멈춰있음, 누군가가 이 5555 포트를 통해서 연결요청이 들어올 때까지 멈춰 있음
            // 연결 요청이 들어오면 그 때 소켓이 연결이 됨
            mSocket = mserverSocket.accept();
            System.out.println("클라이언트와 연결 됨");

            new Client.ReceiverThread(mSocket).start();
            new Client.SenderThread(mSocket).start();


        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    public static void main(String[] args) {
        Server server = new Server();
    }

}
