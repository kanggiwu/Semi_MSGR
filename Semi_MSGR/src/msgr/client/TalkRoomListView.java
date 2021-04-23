package msgr.client;

import java.awt.BorderLayout;
import java.awt.Font;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.util.List;
import java.util.Map;

import javax.swing.DefaultListModel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;

public class TalkRoomListView extends JPanel implements MouseListener {
	MessengerClientView			msgrClientView	= null;
	String[]					talkRoomName	= new String[50];
	JList<Object>				talkRoomList	= null;
	DefaultListModel<Object>	dlm				= new DefaultListModel<>();
	JScrollPane					scrollPane_list	= null;
	MessengerChatView			msgrChatView	= null;
	Font						font			= new Font("맑은 고딕", Font.PLAIN, 15);

	public TalkRoomListView() {

	}

	public TalkRoomListView(MessengerClientView msgrClient) {
		this.msgrClientView = msgrClient;
		initDisplay();
	}

	public void initDisplay() {
		this.setLayout(new BorderLayout());
		this.setSize(500, 400);
		this.setVisible(true);
	}

	public void getRoomList(List<Map<String, Object>> pList) {
		talkRoomList = new JList<Object>();

		for (Map<String, Object> map : pList) {
			dlm.addElement(map.get("ROOM_NAME_VC"));
		}
		talkRoomList.setModel(dlm);
		scrollPane_list = new JScrollPane(talkRoomList, JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED, JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
		talkRoomList.addMouseListener(this);
		talkRoomList.setFont(font);
		this.add("Center", scrollPane_list);
	}

	public void message_process(String msg, String imgChoice) {

	}

	@Override
	public void mouseClicked(MouseEvent e) {
		System.out.println(talkRoomList.getSelectedValue());

		if (e.getClickCount() == 2) {

			if (!"".equals(talkRoomList.getSelectedValue())) {
				msgrChatView = new MessengerChatView(this);
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
