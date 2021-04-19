package msgr.server;

import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;

public class TestClinet extends JFrame implements Runnable, ActionListener {

	Socket				socket					= null;
	ObjectOutputStream	oos						= null;						// 말 하고 싶을 때
	ObjectInputStream	ois						= null;						// 듣기 할 때
	JButton				jbtn_logout				= new JButton("로그아웃");
	JButton				change_nickname			= new JButton("닉네임변경");
	JButton				mem_delete				= new JButton("회원탈퇴");
	JButton				room_create_buddy		= new JButton("친구톡방 생성");
	JButton				room_create_opentalk	= new JButton("오픈톡방 생성");
	JButton				room_list				= new JButton("방리스트 출력");
	JButton				room_in					= new JButton("방 입장");
	JButton				room_in_mem				= new JButton("톡방 참가 인원");
	JButton				room_out				= new JButton("톡방 나가기");
	JButton				room_delete				= new JButton("방 삭제");
	JButton				buddy_add				= new JButton("친구 추가");
	JButton				buddy_list				= new JButton("친구리스트 출력");
	JButton				buddy_delete			= new JButton("친구 삭제");
	JButton				message					= new JButton("메시지 전송");
	JButton				emoticon				= new JButton("이모티콘");
	JButton				attachment				= new JButton("파일 첨부");
	JButton				sendChat				= new JButton("대화내용저장");
	JPanel				jp						= new JPanel();

	public void initDisplay() {
		jbtn_logout.addActionListener(this);
		change_nickname.addActionListener(this);
		mem_delete.addActionListener(this);
		room_create_buddy.addActionListener(this);
		room_create_opentalk.addActionListener(this);
		room_list.addActionListener(this);
		room_in.addActionListener(this);
		room_in_mem.addActionListener(this);
		room_out.addActionListener(this);
		room_delete.addActionListener(this);
		buddy_add.addActionListener(this);
		buddy_list.addActionListener(this);
		buddy_delete.addActionListener(this);
		message.addActionListener(this);
		emoticon.addActionListener(this);
		attachment.addActionListener(this);

		this.setLayout(new GridLayout(17, 1, 3, 3));
		this.add(jbtn_logout);
		this.add(change_nickname);
		this.add(mem_delete);
		this.add(room_create_buddy);
		this.add(room_create_opentalk);
		this.add(room_list);
		this.add(room_in);
		this.add(room_in_mem);
		this.add(room_out);
		this.add(room_delete);
		this.add(buddy_add);
		this.add(buddy_list);
		this.add(buddy_delete);
		this.add(message);
		this.add(emoticon);
		this.add(attachment);
		this.add(sendChat);

		this.add("Center", jp);
		this.setSize(400, 500);
		this.setVisible(true);
	}

	public static void main(String[] args) {
		TestClinet tc = new TestClinet();
		tc.initDisplay();
		Thread thread = new Thread(tc);
		thread.start();

	}

	@Override
	public void run() {
		try {
			// 서버측의 ip주소 작성하기
			socket = new Socket("127.0.0.1", 21430);
			// TS ServerSocket감지 -> client = server.accept(); //클라이언트 소켓에 대한 정보 갖음.
			// 홀수 소켓에 대한 처리
			oos = new ObjectOutputStream(socket.getOutputStream());
			// 짝수 소켓에 대한 처리
			ois = new ObjectInputStream(socket.getInputStream());
			// initDisplay에서 닉네임이 결정된 후 init메소드가 호출되므로
			// 서버에게 내가 입장한 사실을 알린다.(말하기)
			// TomatoServerThread의 생성자가 듣기
			// 서버에 말을 한 후 들을 준비를 한다. - 대기 탄다 - 듣기 - 프로토콜을 비교해야 한다.
			// 프로토콜 설계하기 - ERD 그린다. - 데이터 클래스 설계 - List, Map 단위테스트까지도 너가 해줄래
		}
		catch (Exception e) {
			// 예외가 발생했을 때 직접적인 원인되는 클래스명 출력하기
			e.printStackTrace();
		}

	}

	@Override
	public void actionPerformed(ActionEvent e) {
		Object obj = e.getSource();
		
		if(jbtn_logout == obj) {
			System.out.println(socket.isConnected());
			try {
				oos.writeObject(Protocol.LOGOUT);
			}
			
			catch (IOException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
		}else if(change_nickname		==obj) {
			try {
				oos.writeObject(Protocol.CHANGE_NICKNAME);
			}
			catch (IOException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
		}else if(mem_delete			    ==obj) {
			try {
				oos.writeObject(Protocol.MEM_DELETE);
			}
			catch (IOException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
		}else if(room_create_buddy	    ==obj) {
			try {
				oos.writeObject(Protocol.ROOM_CREATE_BUDDY);
			}
			catch (IOException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
		}else if(room_create_opentalk   ==obj) {
			try {
				oos.writeObject(Protocol.ROOM_CREATE_OPENTALK);
			}
			catch (IOException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
		}else if(room_list			    ==obj) {
			try {
				oos.writeObject(Protocol.ROOM_LIST);
			}
			catch (IOException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
		}else if(room_in				==obj) {
			try {
				oos.writeObject(Protocol.ROOM_IN);
			}
			catch (IOException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
		}else if(room_in_mem			==obj) {
			try {
				oos.writeObject(Protocol.ROOM_IN_MEM);
			}
			catch (IOException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
		}else if(room_out			    ==obj) {
			try {
				oos.writeObject(Protocol.ROOM_OUT);
			}
			catch (IOException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
		}else if(room_delete			==obj) {
			try {
				oos.writeObject(Protocol.ROOM_DELETE);
			}
			catch (IOException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
		}else if(buddy_add			    ==obj) {
			try {
				oos.writeObject(Protocol.BUDDY_ADD);
			}
			catch (IOException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
		}else if(buddy_list			    ==obj) {
			try {
				oos.writeObject(Protocol.BUDDY_LIST);
			}
			catch (IOException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
		}else if(buddy_delete		    ==obj) {
			try {
				oos.writeObject(Protocol.BUDDY_DELETE);
			}
			catch (IOException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
		}else if(message				==obj) {
			try {
				oos.writeObject(Protocol.STORECHAT);
			}
			catch (IOException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
		}else if(emoticon			    ==obj) {
			try {
				oos.writeObject(Protocol.EMOTICON);
			}
			catch (IOException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
		}else if(attachment			    ==obj) {
			try {
				oos.writeObject(Protocol.ATTACHMENT);
			}
			catch (IOException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
		}
		
	}

}
