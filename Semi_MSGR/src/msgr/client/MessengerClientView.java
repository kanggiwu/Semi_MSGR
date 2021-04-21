package msgr.client;

import java.awt.Color;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Vector;

import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPopupMenu;
import javax.swing.JTabbedPane;

//import messenger.util.Room;
import msgr.server.Protocol;

public class MessengerClientView extends JFrame implements ActionListener {

	// 생성자에서 로그인뷰의 정보를 받아와야 하므로 전역변수 선언
	// 탭과 탭에 들어갈 패널들(친구목록뷰, 톡방목록뷰)
	SignInView signInView = null;
	private JTabbedPane tabbedPane = new JTabbedPane(JTabbedPane.LEFT);
	BuddyListView buddyListView = null;
	TalkRoomListView roomListView = null;

	// 서버와 연결할 소켓, 스트림, 아이피, 포트
	Socket socket = null;
	ObjectOutputStream oos = null;
	ObjectInputStream ois = null;
	PrintWriter pw = null;
	BufferedReader br = null;
	private String ip = "127.0.0.1";
	private int port = 21430;

	// 사용자 아이디, 닉네임
	private String id = "";
	private String nickname = "";

	// 클라이언트 메뉴 구성
	private JMenuBar menuBar = null;
	private JMenu menu_myPage = null;
	private String[] myPageName = { "닉네임 변경", "로그아웃", "회원탈퇴" };
	private JMenuItem[] menuItem_myPage = null;
	private JMenu menu_talkRoom = null;
	private String[] talkRoomName = { "오픈톡", "친구톡", "친구추가" };
	private JMenuItem[] menuItem_talkRoom = null;

	// 우클릭 했을 때 뜨는 팝업메뉴
	private JPopupMenu popupMenu = null;
	private JMenuItem menuItem_deleteBuddy = null;

	// 생성자에서 SignInView에서 받은 아이디, 닉네임을 저장함
	public MessengerClientView(SignInView signInView) {
		this.signInView = signInView;
		this.id = signInView.getId();
		this.nickname = signInView.getNickname();

		try {
			initDisplay();
		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	/**
	 * 로그인이 성공하면 소켓서버와 연결하고 스트림을 만드는 메서드
	 */
	public void getConnection() {

		this.setTitle(nickname + "(" + id + ")님");

		try {
			socket = new Socket(ip, port);

			oos = new ObjectOutputStream(socket.getOutputStream());
			ois = new ObjectInputStream(socket.getInputStream());
			pw = new PrintWriter(socket.getOutputStream());
			br = new BufferedReader(new InputStreamReader(socket.getInputStream()));

			pw.println(Protocol.LOGIN + Protocol.SEPERATOR + id);
			pw.flush();

			// oos.writeObject(Protocol.LOGIN + Protocol.SEPERATOR + id);
			MessengerClientThread msgrClientThread = new MessengerClientThread(this);
			msgrClientThread.start();
		} catch (Exception e) {
			System.out.println(e.getMessage());
		}
	}

	/**
	 * MessengerClientView 화면 초기화
	 * 
	 * @throws Exception 예외가 없는데 뭘 던져야 하는 거지?
	 */
	private void initDisplay() throws Exception {
		// 친구목록 패널 인스턴스화
		buddyListView = new BuddyListView(this);
		// 톡방목록 패널 인스턴스화
		roomListView = new TalkRoomListView(this);

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

		/////////////////////// 팝업메뉴 구성 시작 ///////////////////////
		menuItem_deleteBuddy = new JMenuItem("친구삭제");
		menuItem_deleteBuddy.addActionListener(this);
		popupMenu = new JPopupMenu();
		popupMenu.add(menuItem_deleteBuddy);
		/////////////////////// 팝업메뉴 구성 끝 ///////////////////////

		/////////////////////// 탭 구성 시작 ///////////////////////
		tabbedPane.addTab("친구목록", buddyListView);
		tabbedPane.addTab("톡방목록", roomListView);
		this.getContentPane().setBackground(Color.ORANGE);
		tabbedPane.setFont(new Font("맑은 고딕", Font.PLAIN, 12));
		tabbedPane.setToolTipText("");
		/////////////////////// 탭 구성 끝 ///////////////////////

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
			String aftername = JOptionPane.showInputDialog(this, "변경할 닉네임을 입력하세요", "닉네임 변경", JOptionPane.YES_NO_OPTION);
			if ("".equals(aftername)) {
				System.out.println("취소버튼 누른 거 같음");
			} else {
				pw.println(Protocol.CHANGE_NICKNAME + Protocol.SEPERATOR + aftername);
				pw.flush();
			}
		}

		else if ("로그아웃".equals(command)) {
			pw.println(Protocol.LOGOUT + Protocol.SEPERATOR + id);
			pw.flush();
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

			pw.println(Protocol.BUDDY_ADD + Protocol.SEPERATOR + id);
			pw.flush();

			List<Map<String, Object>> list = new Vector<>();
			Map<String, Object> map = new HashMap<String, Object>();
			map.put("testKey", "testValue");
			list.add(map);
			try {
				oos.writeObject(list);
			} catch (IOException e1) {
				e1.printStackTrace();
			}
		}

		/////////////////////// 톡방 메뉴아이템 끝 ///////////////////////
		if ("친구삭제".equals(command)) {
			System.out.println("친구삭제 메뉴아이템");
		}
	}// end of ActionPerformed()

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getNickname() {
		return nickname;
	}

	public void setNickname(String nickname) {
		this.nickname = nickname;
	}

	public JPopupMenu getPopupMenu() {
		return popupMenu;
	}
}