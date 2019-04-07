//package client_n_n;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

/**

 * 이번에는 다 대 다 연결을 지원하는 클라이언트를 만들도록 하겠습니다.

 *

 * 기존과 동일하게 메세지의 수신과 발신을 담당하는 스레드가 하나씩 필요합니다.

 *

 */

/**

 * 메시지의 수신을 담당하는 스레드 입니다.

 */

class ReceiverThread extends Thread {

    Socket socket;

    public ReceiverThread(Socket socket) {
        this.socket = socket;
        }
    @Override
    public void run() {
        try {
            BufferedReader reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            while (true) {
                String str = reader.readLine();
                if (str == null) {
                    break;
                }
                System.out.println(str);
            }
        }
        catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }
}


/**
 *
 * 메시지의 발신을 담당하는 스레드
 *
 */

class SenderThread extends Thread {
    Socket socket;
    String name;

    public SenderThread(Socket socket, String name) {
        this.socket = socket;
        this.name = name;
    }
    @Override
    public void run() {

        try {
            BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
            PrintWriter writer = new PrintWriter(socket.getOutputStream());

            // 제일 먼저 서버로 대화명을 송신합니다.

            writer.println(name);
            writer.flush();

            while (true) {
                String str = reader.readLine();
                if (str.equals("bye")) {
                    break;
                }
                writer.println(str);
                writer.flush();
            }
        }
        catch (Exception e) {
            System.out.println(e.getMessage());
        }
        finally {
            try {
                socket.close();
            } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }
    }
}
public class Client_n_n {
    public static void main(String[] args) {
        try {
            // 일단은 테스트 용으로 본인의 아이피를 입력해서 진행하겠습니다.
            Socket socket = new Socket("localhost", 3004);

            // 두번째 파라메터 로는 본인의 닉네임을 적어줍니다.
            Thread thread1 = new SenderThread(socket, "정중원");
            Thread thread2 = new ReceiverThread(socket);

            thread1.start();
            thread2.start();

        }

        catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }
}