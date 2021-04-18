package msgr.server;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.List;
import java.util.Vector;

import javax.swing.JFrame;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;

public class MessagerServer extends JFrame implements Runnable{
	ServerSocket server = null;
	Socket client = null;
	JTextArea textArea_log = new JTextArea();
	JScrollPane scrollPane = new JScrollPane(textArea_log,JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED,
			JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
	MessengerServerThread msgrServerThread = null;
	List<MessengerServerThread> globalList = null;// 현재 접속중인 사람들 리스트
	List<MessengerTalkRoom> talkRoomList = null;// 방에 참여한 사람들 리스트
	
	
	public void initDisplay() {
		//윈도우 창 닫으면 서버와 클라이언트를 종료하고 창을 종료한다.
		this.addWindowListener(new WindowAdapter() {
			public void windowClosing(WindowEvent we) {

				try {
					server.close();
					client.close();
					System.exit(0);
				}
				catch (IOException e) {
					e.printStackTrace();
				}
			}
		});
		this.setTitle("Server Log Window");
		textArea_log.setEditable(false);
		this.add(textArea_log);
		this.setSize(500,500);
		this.setVisible(true);
	}
	
	public static void main(String[] args) {
		MessagerServer messagerServer = new MessagerServer();
		messagerServer.initDisplay();
		new Thread(messagerServer).start();// 서버 시작
		
	}
	@Override
	public void run() {
		globalList = new Vector<>();// 접속한 클라이언트를 관리할 리스트 초기화
		boolean isStop = false;
		
		try {
			server = new ServerSocket(21430);// 포트번호 준비
			textArea_log.append("서버가 준비되었습니다.");
			
			while(!isStop) {
				client = server.accept();// 클라이언트 스레드를 기다림..
				textArea_log.append(client.toString()+"\n");//포트번호 출력 테스트
				msgrServerThread = new MessengerServerThread(this);
				msgrServerThread.start();
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

}
