import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.OutputStream;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.HashMap;
import java.util.Iterator;

// Ŭ���̾�Ʈ�� �հ� �������� ������ ��, ä���� �ϰ� �Ǹ� �� ���� �ܸ��Ⱑ ���� ��ȭ�� ��
// ���̷�Ʈ�� ��ȭ�� �����°�ó�� �������� �ᱹ�� �߰��� ������ ����, �������� ���� ������ ������ ���ִ� ��
// �켱�� �������� ������ ������ ���� ���� ��ü������ �ѷ��ִ� ��

public class ChatServer {
	
	// �������� ������ ������ �� , �ƾ�巹��, ��Ʈ��
	HashMap<String, DataOutputStream> clients;
	// ���� ����(���񽺸� �����ϱ� ���� ����)
	ServerSocket serverSocket=null;
	
	// ������ -> ��ü ���� ������ �� �߰��� �� �ְԲ� ��
	public ChatServer() {
		clients = new HashMap<String, DataOutputStream>();
	}
	// main->��ü�� ����
	public static void main(String[] args) {
		new ChatServer().start();
	}
	
	// start �޼ҵ� ȣ��
	void start() {
		int port=3000; // 0~ 65355 �߿��� ����ִ� ��Ʈ, ���񽺸� ���� ��Ʈ ��ȣ
		// ServerSocket(���񽺸� �����ϱ� ���� ����), Socket(�׳� ����, Ŭ���̾�Ʈ�� �����ϱ� ���� ����)
		Socket socket=null; // Ŭ���̾�Ʈ�� ������ �ϱ� ���� ����
		try {
			// ���� ���� ����
			serverSocket = new ServerSocket(port);
			System.out.println("���� �����"); 
			while(true) {
				socket = serverSocket.accept(); // ���� ��� ����
				 								// �׷��� Ŭ���̾�Ʈ�� ������ �ϰ� �Ǹ�, �� �� ������ �����Ǹ鼭 ����ó���� ��
				// Ŭ���̾�Ʈ�� ip �ּҸ� �����ͼ� �ؿ� ����� ������
				InetAddress ip = socket.getInetAddress();
				System.out.println(ip + " connected");
				// ���� ����ڰ� ������ ������ ����ڸ��� ������ �����带 ����
				// ����� : 100�� --> ������ : 100��~, ������ �þ�� �׸�ŭ ������ �������� ��
				// Ŭ���̾�Ʈ�� �����ϸ� ���ο� �����带 ����
				// ������.start() = > run()
				new MultiThread(socket).start();
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	// ���� Ŭ����(�����带 ��� ����)
	class MultiThread extends Thread {
		Socket socket=null;
		String mac=null;
		String msg=null;
		DataInputStream input;
		DataOutputStream output;
		// ���� Ŭ������ ������
		public MultiThread(Socket socket) {
			this.socket = socket;
			try { // ������ ������� ���� ��Ʈ�� ����
				input = new DataInputStream(socket.getInputStream());
				output = new DataOutputStream(socket.getOutputStream());
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		public void run() {
			try {
				// ���� �޽���, Ŭ���̾�Ʈ�� �޽����� ����, Ŭ���̾�Ʈ�� ������ �Ѿ�ͼ�
				// �� �ִٰ� ������ �ϸ�, �� ��巹���� ����ؼ� ���� ����, �ڽ��� �� ��巹���� ��������
				mac = input.readUTF(); // �ѱ� ó���� ���� uft��
				System.out.println("Mac Address:"+mac); // �� ��巹���� ������ ǥ���� �ΰ�
				// Ŭ���̾�Ʈ�� ������ �ؽøʿ��ٰ� ����(mac address�� key��)
				// mac address�� Ű ���̴ϱ� �ߺ��� �ȵ�
				clients.put(mac,output); // mac address, outputstream
				sendMsg(mac+ " ����");
				while(input != null) {
					try {
						// ä���� �ϸ� �� ������ ��� �Ѿ��
						String temp=input.readUTF(); // ���� �޽���
						// �޽����� �����ϰ� �Ǹ� �� ������, �߰� ����
						// �츮�� �ڵ������� ��ȭ�� �ϸ� �ڵ��� ���� ���� �Դٰ��� �ϴ°� �ƴ���?
						// �������� ���� ���ٰ� ���������� ���ĸ� �ѷ��ִ� ����! -> ���̷�Ʈ�� �Դٰ��� �ϴ°� �ƴϴ� -> chatting
						// �������� �� ��Ҵٰ� �ѷ��ִ� ���
						sendMsg(temp); // 22:50
						System.out.println(temp);
					} catch(Exception e) {
						sendMsg("No Message"); // �������� �� �޽���
						e.printStackTrace();
					}
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		} // end of run()
		
		void sendMsg(String msg) { // sengMsg �޼ҵ� : Ŭ���̾�Ʈ������ ������ ����
			// Ŭ���̾�Ʈ�� key ���� (mac address�� ����)
			Iterator<String> it = clients.keySet().iterator();
			// while���� ���鼭 ������ ��� ��������� �޽����� ���� ��
			// 1:1 �� �Ϸ��� ������� �ɾ���� ��
			while(it.hasNext()) { // mac address���� �����ͼ� ���� ��Ұ� ������
				try {
					// Ŭ���̾�Ʈ�� OutputStream�� ����
					OutputStream dos=clients.get(it.next());
					DataOutputStream output = new DataOutputStream(dos);
					// Ŭ���̾�Ʈ�� �޽��� ����
					output.writeUTF(msg);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}
		
		
	}
}
