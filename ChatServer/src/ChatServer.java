import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.OutputStream;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.HashMap;
import java.util.Iterator;

// 클라이언트가 먼가 글을쓰면 서버로 옴, 채팅을 하게 되면 두 대의 단말기가 서로 대화를 함
// 다이렉트로 대화가 오가는것처럼 보이지만 결국은 중간에 서버가 있음, 서버한테 먼저 보내면 서버가 쏴주는 식
// 우선은 서버한테 보내고 서버가 받은 것을 전체적으로 뿌려주는 것

public class ChatServer {
	
	// 접속자의 정보를 저장할 맵 , 맥어드레스, 스트림
	HashMap<String, DataOutputStream> clients;
	// 서버 소켓(서비스를 제공하기 위한 소켓)
	ServerSocket serverSocket=null;
	
	// 생성자 -> 객체 만들어서 접속할 때 추가할 수 있게끔 함
	public ChatServer() {
		clients = new HashMap<String, DataOutputStream>();
	}
	// main->객체를 만듬
	public static void main(String[] args) {
		new ChatServer().start();
	}
	
	// start 메소드 호출
	void start() {
		int port=3000; // 0~ 65355 중에서 비어있는 포트, 서비스를 위한 포트 번호
		// ServerSocket(서비스를 제공하기 위한 소켓), Socket(그냥 소켓, 클라이언트와 연결하기 위한 소켓)
		Socket socket=null; // 클라이언트와 연결을 하기 위한 소켓
		try {
			// 서버 소켓 생성
			serverSocket = new ServerSocket(port);
			System.out.println("접속 대기중"); 
			while(true) {
				socket = serverSocket.accept(); // 접속 대기 상태
				 								// 그러다 클라이언트가 접속을 하게 되면, 그 때 소켓이 생성되면서 접속처리가 됨
				// 클라이언트의 ip 주소를 가져와서 밑에 출력을 시켜줌
				InetAddress ip = socket.getInetAddress();
				System.out.println(ip + " connected");
				// 여러 사용자가 들어오기 때문에 사용자마다 별도의 스레드를 만듬
				// 사용자 : 100명 --> 스레드 : 100개~, 유저가 늘어나면 그만큼 성능이 떨어지게 됨
				// 클라이언트가 접속하면 새로운 스레드를 만듬
				// 스레드.start() = > run()
				new MultiThread(socket).start();
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	// 내부 클래스(스레드를 상속 받은)
	class MultiThread extends Thread {
		Socket socket=null;
		String mac=null;
		String msg=null;
		DataInputStream input;
		DataOutputStream output;
		// 내부 클래스의 생성자
		public MultiThread(Socket socket) {
			this.socket = socket;
			try { // 데이터 입출력을 위한 스트림 생성
				input = new DataInputStream(socket.getInputStream());
				output = new DataOutputStream(socket.getOutputStream());
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		public void run() {
			try {
				// 수신 메시지, 클라이언트가 메시지를 쓰면, 클라이언트의 정보가 넘어와서
				// 좀 있다가 접속을 하면, 맥 어드레스를 계산해서 보낼 것임, 자신의 맥 어드레스를 보낼거임
				mac = input.readUTF(); // 한글 처리를 위해 uft로
				System.out.println("Mac Address:"+mac); // 맥 어드레스를 서버에 표시해 두고
				// 클라이언트의 정보를 해시맵에다가 저장(mac address를 key로)
				// mac address는 키 값이니까 중복이 안됨
				clients.put(mac,output); // mac address, outputstream
				sendMsg(mac+ " 접속");
				while(input != null) {
					try {
						// 채팅을 하면 이 쪽으로 계속 넘어옴
						String temp=input.readUTF(); // 수신 메시지
						// 메시지를 수신하게 되면 다 보내줌, 중계 역할
						// 우리가 핸드폰으로 통화를 하면 핸드폰 끼리 서로 왔다갔다 하는게 아니지?
						// 기지국을 먼저 갔다가 기지국에서 전파를 뿌려주는 거지! -> 다이렉트로 왔다갔다 하는게 아니다 -> chatting
						// 서버에서 다 모았다가 뿌려주는 방식
						sendMsg(temp); // 22:50
						System.out.println(temp);
					} catch(Exception e) {
						sendMsg("No Message"); // 실패했을 때 메시지
						e.printStackTrace();
					}
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		} // end of run()
		
		void sendMsg(String msg) { // sengMsg 메소드 : 클라이언트들한테 보내는 거임
			// 클라이언트의 key 집합 (mac address의 집합)
			Iterator<String> it = clients.keySet().iterator();
			// while문이 돌면서 접속한 모든 사용자한테 메시지가 가게 됨
			// 1:1 로 하려면 제약사항 걸어줘야 함
			while(it.hasNext()) { // mac address에서 가져와서 다음 요소가 있으면
				try {
					// 클라이언트의 OutputStream을 저장
					OutputStream dos=clients.get(it.next());
					DataOutputStream output = new DataOutputStream(dos);
					// 클라이언트에 메시지 전송
					output.writeUTF(msg);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}
		
		
	}
}
