package msgr.client;

import java.awt.Color;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;

import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JTabbedPane;

//import messenger.util.Room;
import msgr.server.Protocol;

public class MessengerClientView extends JFrame implements ActionListener {
	SignInView			signInView			= null;
	JTabbedPane			tabbedPane			= new JTabbedPane(JTabbedPane.LEFT);
	BuddyListView		buddyListView		= null;
	RoomListView		roomListView		= null;
	Socket				mySocket			= null;
	ObjectInputStream	ois					= null;
	ObjectOutputStream	oos					= null;
	String				ip					= "127.0.0.1";
	int					port				= 21430;
	String				nickname			= null;

	JMenuBar			menuBar				= null;
	JMenu				menu_myPage			= null;
	String[]			myPageName			= { "닉네임 변경", "로그아웃", "회원탈퇴" };
	JMenuItem[]			menuItem_myPage		= null;
	JMenu				menu_talkRoom		= null;
	String[]			talkRoomName		= { "오픈톡", "친구톡", "친구추가" };
	JMenuItem[]			menuItem_talkRoom	= null;

	public MessengerClientView(SignInView signInView) {
		this.signInView = signInView;
		nickname = signInView.getNickname();

		try {
			initDisplay();
		}
		catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void getConnection() {
		this.setTitle(nickname + "님");

		try {
			mySocket = new Socket(ip, port);

			oos = new ObjectOutputStream(mySocket.getOutputStream());
			ois = new ObjectInputStream(mySocket.getInputStream());

			oos.writeObject(Protocol.LOGIN + Protocol.SEPERATOR + nickname);
//			MessengerClientThread msgrClientThread = new MessengerClientThread(this);
//			msgrClientThread.start();// TalkClientThread의 run호출됨.-콜백함수
		}
		catch (Exception e) {
			e.printStackTrace();
		}
	}

	private void initDisplay() throws Exception {
		// 친구목록 패널 인스턴스화
		buddyListView = new BuddyListView(this);
		// 톡방목록 패널 인스턴스화
		roomListView = new RoomListView(this);

		/////////////////////// 메뉴 구성 시작 ///////////////////////
		menuBar = new JMenuBar();
		menu_myPage = new JMenu("마이페이지");
		menuItem_myPage = new JMenuItem[myPageName.length];
		menu_talkRoom = new JMenu("톡방");
		menuItem_talkRoom = new JMenuItem[talkRoomName.length];

		for (int i = 0; i < myPageName.length; i++) {
			menuItem_myPage[i] = new JMenuItem(myPageName[i]);
			menu_myPage.add(menuItem_myPage[i]);
			menuItem_myPage[i].addActionListener(this);
		}

		for (int i = 0; i < talkRoomName.length; i++) {
			menuItem_talkRoom[i] = new JMenuItem(talkRoomName[i]);
			menu_talkRoom.add(menuItem_talkRoom[i]);
			menuItem_talkRoom[i].addActionListener(this);
		}

		menuBar.add(menu_myPage);
		menuBar.add(menu_talkRoom);
		/////////////////////// 메뉴 구성 끝 ///////////////////////

		tabbedPane.addTab("친구목록", buddyListView);
		tabbedPane.addTab("톡방목록", roomListView);
		this.getContentPane().setBackground(Color.ORANGE);
		tabbedPane.setFont(new Font("맑은 고딕", Font.PLAIN, 12));
		tabbedPane.setToolTipText("");

		this.setDefaultCloseOperation(EXIT_ON_CLOSE);
		this.setJMenuBar(menuBar);
		this.setResizable(false);
		this.add(tabbedPane);
		this.setSize(400, 600);
		this.setVisible(true);
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		String command = e.getActionCommand();

		/////////////////////// 마이페이지 메뉴아이템 시작 ///////////////////////
		if ("닉네임 변경".equals(command)) {

			try {
				oos.writeObject(Protocol.CHANGE_NICKNAME + Protocol.SEPERATOR + nickname);
			}
			catch (IOException e1) {
				e1.printStackTrace();
			}
		}

		else if ("로그아웃".equals(command)) {
			System.out.println("로그아웃 버튼");
			this.setVisible(false);
			this.dispose();
			signInView.setVisible(true);
		}

		else if ("회원탈퇴".equals(command)) {
			System.out.println("회원탈퇴 버튼");
		}
		/////////////////////// 마이페이지 메뉴아이템 끝 ///////////////////////

		/////////////////////// 톡방 메뉴아이템 시작 ///////////////////////
		else if ("오픈톡".equals(command)) {
			System.out.println("오픈톡 버튼");
		}

		else if ("친구톡".equals(command)) {
			System.out.println("친구톡 버튼");
		}

		else if ("친구추가".equals(command)) {
			System.out.println("친구추가 버튼");
		}
		/////////////////////// 톡방 메뉴아이템 끝 ///////////////////////
	}
}