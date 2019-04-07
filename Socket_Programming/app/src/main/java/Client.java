import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.Scanner;

// 1 대 1 소켓 통신 클라이언트
public class Client {

    private Socket mSocket;

    public  Client(String ip,int port){

            // new 하는 순간 요청을 보내는 것
            // 서버에 요청 보내기
        try {
            mSocket = new Socket(ip, port);
        } catch (IOException e) {
            e.printStackTrace();
        }
        System.out.println(ip+" 연결됨");

        new ReceiverThread(mSocket).start();
        new SenderThread(mSocket).start();

    }
    public static void main(String[] args) {
        // localhost -> 내 IP로 연결됨
        // new Client("localhost",5555);
        new Client("192.168.219.155",3000);
    }
    public  static  class ReceiverThread extends Thread{
        private final Socket mSocket;

        private BufferedReader mIn;

        public ReceiverThread(Socket socket){
            mSocket = socket;
            try {
                mIn = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        @Override
        public void run() {
            try{
                while(true){
                    System.out.println("클라이언트 : " +mIn.readLine());
                }
            } catch (Exception e){
                e.printStackTrace();
            }finally {
                try {
                    mSocket.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

   public   static class SenderThread extends Thread{
        private  Socket mSocket;
        private PrintWriter mOut;

        public SenderThread(Socket socket){
            mSocket = socket;
            try{
                mOut = new PrintWriter(socket.getOutputStream());
            } catch(IOException e){
                e.printStackTrace();
            }
        }
        // 사용자가 키보드에 입력을 하는 걸 기다렸다가 입력을 하면쏘는거
        @Override
        public void run() {
            try{
            Scanner scanner = new Scanner(System.in);
                while(true){
                    mOut.println(scanner.next()); // 키보드 입력받은걸 집어넣고
                    mOut.flush(); // 이렇게 하면 쏘는거
                }
            } catch (Exception e){
                e.printStackTrace();
            }finally {
                try {
                    mSocket.close();
                } catch (IOException e){
                    e.printStackTrace();
                }
            }
        }
    }
}
