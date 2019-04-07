//package server_n_n;


import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**

 * 다 대 다 연결을 지원하는 서버입니다.

 *

 * 기존과는 다르게 서버에서는 오직 클라이언트로부터 받은 문자열을 모든 클라이언트에 전달해주는 기능만을 가지고 있습니다.

 *

 */

/**

 * 연결을 요청하는 클라이언트와 관련된 처리를 하는 스레드 입니다. 

 */

class EachClientThread extends Thread {

    // 여러 스레드가 안전하게 공유할 수 있는 동기화된 리스트로 만듭니다.
    // 서버에 연결된 모든 클라이언트로의 출구가 이 리스트에 보관됩니다.
    static List<PrintWriter> list = Collections.synchronizedList(new ArrayList<PrintWriter>());

    Socket socket;
    PrintWriter writer;

    public EachClientThread(Socket socket) {
        this.socket = socket;
        try {
            writer = new PrintWriter(socket.getOutputStream());
            list.add(writer);
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }
    @Override
    public void run() {
        String name = null;
        try {
            BufferedReader reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));

            // 수신된 첫번째 문자열을 닉네임으로 지정합니다.
            name = reader.readLine();
            sendAll("#" + name + "님이 들어오셨습니다");
            System.out.println(name + "입장 전체인원 : " +list.size());
            while(true) {
                String str = reader.readLine();
                if(str == null) {
                    break;
                }
                // 수신된 메시지 앞에 대화명을 붙여서 모든 클라이언트로 보냅니다.
                sendAll(name + ">" + str);
            }
        }

        catch (Exception e) {
            System.out.println(e.getMessage());
        }
        finally {
            list.remove(writer);

            // 해당 사용자가 채팅을 종료했다는 메시지를 모든 클라이언트로 보냅니다.
            sendAll("#" + name + "님이 나가셨습니다");
            System.out.println(name + "퇴장 전체인원 : " + list.size());
            try {
                socket.close();
            } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }
    }
    private void sendAll(String str) {
        for (PrintWriter writer : list) {
            // 서버에 연결된 모든 클라이언트로 똑같은 메세지를 보냅니다.
            // for문 돌려서 다 쏴준다
            writer.println(str);
            writer.flush();
        }
    }
}

public class Server_n_n {
    public static void main(String[] args) {
        ServerSocket serverSocket = null;

        try {
            serverSocket = new ServerSocket(3004);
            // 클라이언트가 접속할 때마다 별도의 소켓을 생성하고,
            // 메시지의 수발신 처리를 위한 스레드를 만들고 바로 실행합니다.


            // while문을 돌면서 들어오는 연결을 전부 소켓으로 받음
            // 그거마다 스레드를 생성, 클라이언트 개수만큼 스레드 생성
            while(true) {
                Socket socket = serverSocket.accept();
                Thread thread = new EachClientThread(socket);
                thread.start();
            }
        }

        catch(Exception e) {
            System.out.println(e.getMessage());
        }
    }

}