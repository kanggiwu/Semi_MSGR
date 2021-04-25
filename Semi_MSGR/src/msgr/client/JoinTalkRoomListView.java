package msgr.client;

import java.awt.BorderLayout;
import java.awt.Font;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Vector;

import javax.swing.DefaultListModel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;

import msgr.server.Protocol;

public class JoinTalkRoomListView extends JPanel implements MouseListener {
	MessengerClientView			msgrClientView	= null;
	List<MessengerChatView>		chatList		= new Vector<MessengerChatView>();
	List<Map<String, Object>>	joinRoom_info	= null;																		// room_no_nu,room_name_vc
	List<Map<String, Object>>	openRoom_info	= null;																		// room_no_nu,room_name_vc

	JList<Object>				talkRoomList	= new JList<>();
	DefaultListModel<Object>	dlm				= new DefaultListModel<>();
	JScrollPane					scrollPane_list	= new JScrollPane(talkRoomList, JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED,
								JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
	MessengerChatView			msgrChatView	= null;
	Font						font			= new Font("맑은 고딕", Font.PLAIN, 15);

	public JoinTalkRoomListView() {

	}

	public JoinTalkRoomListView(MessengerClientView msgrClient) {
		this.msgrClientView = msgrClient;
		initDisplay();
	}

	public void initDisplay() {
		talkRoomList.setFont(font);
		talkRoomList.addMouseListener(this);
		this.setLayout(new BorderLayout());
		this.setSize(500, 400);
		this.setVisible(true);
		this.add("Center", scrollPane_list);
	}

	public void getRoomList(List<Map<String, Object>> pList) {
		joinRoom_info = pList;

		if (dlm.size() > 0) {
			dlm.clear();
		}

		for (Map<String, Object> map : pList) {
			dlm.addElement(map.get("ROOM_NAME_VC"));
		}
		talkRoomList.setModel(dlm);

	}

	public void message_process(String msg, String imgChoice) {

	}

	@Override
	public void mouseClicked(MouseEvent e) {
		System.out.println(talkRoomList.getSelectedValue());

		if (e.getClickCount() == 2) {
			String room_name = talkRoomList.getSelectedValue().toString();
			try {
				msgrClientView.oos.writeObject(Protocol.ROOM_IN+Protocol.SEPERATOR+room_name);
			}
			catch (IOException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}

			if (!"".equals(talkRoomList.getSelectedValue())) {
				msgrChatView = new MessengerChatView(this);
				chatList.add(msgrChatView);
				msgrChatView.initDisplay();
			}
		}
	}

	@Override
	public void mouseEntered(MouseEvent arg0) {
		// TODO Auto-generated method stub

	}

	@Override
	public void mouseExited(MouseEvent arg0) {
		// TODO Auto-generated method stub

	}

	@Override
	public void mousePressed(MouseEvent arg0) {
		// TODO Auto-generated method stub

	}

	@Override
	public void mouseReleased(MouseEvent arg0) {
		// TODO Auto-generated method stub

	}

}
