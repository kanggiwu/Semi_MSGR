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
import java.util.StringTokenizer;
import java.util.Vector;

import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPopupMenu;
import javax.swing.JTabbedPane;

import msgr.map.MessengerMap;
//import messenger.util.Room;
import msgr.server.Protocol;

public class MessengerClientView extends JFrame implements ActionListener {

	// 생성자에서 로그인뷰의 정보를 받아와야 하므로 전역변수 선언
	// 탭과 탭에 들어갈 패널들(친구목록뷰, 톡방목록뷰)
	SignInView					signInView				= null;
	private JTabbedPane			tabbedPane				= new JTabbedPane(JTabbedPane.LEFT);
	BuddyListView				buddyListView			= null;
	JoinTalkRoomListView		joinTalkRoomListView	= null;
	OpenTalkRoomListView		openTalkRoomListView	= null;
	List<Map<String, Object>>	joinOpenTalkRoom_info	= null;
	List<Map<String, Object>>	joinBuddyTalkRoom_info	= null;
	List<Map<String, Object>>	allOpenTalk_info		= null;

	// 서버와 연결할 소켓, 스트림, 아이피, 포트
	Socket						socket					= null;
	ObjectOutputStream			oos						= null;
	ObjectInputStream			ois						= null;
	private String				ip						= "121.139.85.156";
	private int					port					= 21430;

	// 사용자 아이디, 닉네임
	private String				id						= "";
	private String				nickname				= "";

	// 클라이언트 메뉴 구성
	private JMenuBar			menuBar					= null;
	private JMenu				menu_myPage				= null;
	private String[]			myPageName				= { "닉네임 변경", "로그아웃", "회원탈퇴" };
	private JMenuItem[]			menuItem_myPage			= null;
	private JMenu				menu_talkRoom			= null;
	private String[]			talkRoomName			= { "오픈톡", "친구톡", "친구추가" };
	private JMenuItem[]			menuItem_talkRoom		= null;

	// 우클릭 했을 때 뜨는 팝업메뉴
	private JPopupMenu			popupMenu_buddy			= null;
	private JMenuItem			menuItem_deleteBuddy	= null;

	private JPopupMenu			popupMenu_joinRoom		= null;
	private JMenuItem			menuItem_deleteRoom		= null;

	// 생성자에서 SignInView에서 받은 아이디, 닉네임을 저장함
	public MessengerClientView(SignInView signInView) {
		this.signInView = signInView;
		this.id = signInView.getId();
		this.nickname = signInView.getNickname();

		try {
			initDisplay();
		}
		catch (Exception e) {
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

			String request = Protocol.SIGNIN + Protocol.SEPERATOR + id + Protocol.SEPERATOR + nickname;
			send(request);
			MessengerClientThread msgrClientThread = new MessengerClientThread(this);
			msgrClientThread.start();
		}
		catch (Exception e) {
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
		// 친구톡방목록 패널 인스턴스화
		joinTalkRoomListView = new JoinTalkRoomListView(this);
		// 오픈톡방목록 패널 인스턴스화
		openTalkRoomListView = new OpenTalkRoomListView(this);

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

		/////////////////////// 친구목록 팝업메뉴 구성 시작 ///////////////////////
		menuItem_deleteBuddy = new JMenuItem("친구삭제");
		menuItem_deleteBuddy.addActionListener(this);
		popupMenu_buddy = new JPopupMenu();
		popupMenu_buddy.add(menuItem_deleteBuddy);
		/////////////////////// 친구목록 팝업메뉴 구성 끝 ///////////////////////

		/////////////////////// 참여톡방 목록 팝업메뉴 구성 시작////////////////////
		menuItem_deleteRoom = new JMenuItem("톡방삭제");
		menuItem_deleteRoom.addActionListener(this);
		popupMenu_joinRoom = new JPopupMenu();
		popupMenu_joinRoom.add(menuItem_deleteRoom);
		/////////////////////// 참여톡방 목록 팝업메뉴 구성 끝 ///////////////////////

		/////////////////////// 탭 구성 시작 ///////////////////////
		tabbedPane.addTab("친구목록", buddyListView);
		tabbedPane.addTab("참여톡방", joinTalkRoomListView);
		tabbedPane.addTab("오픈톡방", openTalkRoomListView);
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
		String	command		= e.getActionCommand();
		String	aftername	= "";

		/////////////////////// 마이페이지 메뉴아이템 시작 ///////////////////////
		if ("닉네임 변경".equals(command)) {

			// 텍스트필드 빈칸인데 확인 누르면 계속 창 띄워주기
			while ("".equals(aftername)) {
				aftername = JOptionPane.showInputDialog(this, "변경할 닉네임을 입력하세요", "닉네임 변경", JOptionPane.YES_NO_OPTION);
			}

			// 취소버튼 눌렀을 때 닫기
			if (aftername == null) {
				System.out.println("취소버튼");
			}

			// 닉네임을 제대로 입력하고 확인을 눌렀을 때
			else {
				String request = Protocol.CHANGE_NICKNAME
											+ Protocol.SEPERATOR
											+ id
											+ Protocol.SEPERATOR
											+ aftername;
				send(request);
			}
		}

		else if ("로그아웃".equals(command)) {
			String request = Protocol.SIGNOUT + Protocol.SEPERATOR + id;
			send(request);
		}

		else if ("회원탈퇴".equals(command)) {
			String request = Protocol.MEM_DELETE + Protocol.SEPERATOR + id;
			send(request);
		}
		/////////////////////// 마이페이지 메뉴아이템 끝 ///////////////////////

		/////////////////////// 톡방 메뉴아이템 시작 ///////////////////////
		else if ("오픈톡".equals(command)) {

			int result = JOptionPane.showConfirmDialog(this, "오픈톡방을 생성하시겠습니까?", "오픈톡방 생성", JOptionPane.YES_NO_OPTION,
										JOptionPane.INFORMATION_MESSAGE);

			if (result == 0) {
				String request = Protocol.ROOM_CREATE_OPENTALK + Protocol.SEPERATOR + nickname;
				send(request);
			}
		}

		else if ("친구톡".equals(command)) {
			List<Object> tempList = buddyListView.buddyList.getSelectedValuesList();

			for (Object index : tempList) {
				System.out.println(index);
			}

			List<Map<String, Object>>	buddyList	= new Vector<>();
			StringTokenizer				st			= null;
			Map<String, Object>			map			= null;

			for (int i = 0; i < tempList.size(); i++) {
				st = new StringTokenizer(String.valueOf(tempList.get(i)), Protocol.SEPERATOR);
				map = new HashMap<String, Object>();
				map.put("buddy_id_vc", st.nextToken());
				map.put("mem_nick_vc", st.nextToken());
				buddyList.add(map);
			}

			for (Map<String, Object> index : buddyList) {
				System.out.println(index);
			}

			String request = Protocol.ROOM_CREATE_BUDDY + Protocol.SEPERATOR;

			if (tempList.size() > 0) {
				send(request);
				send(buddyList);
			}
			else {
				JOptionPane.showMessageDialog(this, "친구를 선택해주세요.", "알림", JOptionPane.INFORMATION_MESSAGE);
			}
		}

		else if ("친구추가".equals(command)) {

			String buddyId = "";

			while ("".equals(buddyId)) {
				buddyId = JOptionPane.showInputDialog(this, "친구의 아이디를 입력해주세요.", "친구 추가", JOptionPane.PLAIN_MESSAGE);
			}

			// 취소버튼 눌렀을 때 닫기
			if (buddyId == null) {
				System.out.println("취소버튼");
			}

			else {
				String request = Protocol.BUDDY_ADD + Protocol.SEPERATOR + buddyId;
				System.out.println(request);
				send(request);
			}
		}

		/////////////////////// 톡방 메뉴아이템 끝 ///////////////////////
		if ("친구삭제".equals(command)) {
			String buddyId = "";

			if (buddyListView.buddyList.getSelectedValue() != null) {
				buddyId = String.valueOf(buddyListView.buddyList.getSelectedValue());
				String request = Protocol.BUDDY_DELETE + Protocol.SEPERATOR + buddyId;
				send(request);
			}
			else {
				JOptionPane.showMessageDialog(this, "삭제할 친구를 선택해주세요", "친구삭제", JOptionPane.ERROR_MESSAGE);
			}
		}

		if ("톡방삭제".equals(command)) {
			System.out.println("톡방삭제 메뉴아이템" + joinTalkRoomListView.talkRoomTable.getSelectedRowCount());

			if (joinTalkRoomListView.talkRoomTable.getSelectedRowCount() == 1) {
				int		row		= joinTalkRoomListView.talkRoomTable.getSelectedRow();
				int		room_no	= Integer.parseInt(joinTalkRoomListView.dtm.getValueAt(row, 1).toString());
				String	request	= Protocol.ROOM_DELETE + Protocol.SEPERATOR + room_no;
				send(request);
			}
			else {
				JOptionPane.showMessageDialog(this, "톡방 하나 선택해주세요", "톡방삭제", JOptionPane.ERROR_MESSAGE);

			}

		}
	}// end of ActionPerformed()

	// 프로토콜 송신용 메서드
	public void send(Object request) {

		try {
			oos.writeObject(request);
		}
		catch (IOException ioe) {
			System.out.println(ioe.getMessage() + " 요청 실패");
		}
	}

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

	public JPopupMenu getPopupMenu_buddy() {
		return popupMenu_buddy;
	}

	public JPopupMenu getPopupMenu_joinRoom() {
		return popupMenu_joinRoom;
	}

}