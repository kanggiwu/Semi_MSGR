package msgr.client;

import java.awt.BorderLayout;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.util.List;
import java.util.Map;

import javax.swing.DefaultListModel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;

import msgr.map.MessengerMap;
import msgr.util.MessengerDAO;

public class TalkRoomListView extends JPanel implements MouseListener {
	MessengerClientView msgrClient = null;
	String[] talkRoomName = new String[50];
	JList<Object> talkRoomList = null;
	DefaultListModel<Object> dlm = new DefaultListModel<>();
	JScrollPane scrollPane_list = null;
	MessengerChatView msgrChatView = null;

	public TalkRoomListView() {

	}

	public TalkRoomListView(MessengerClientView msgrClient) {
		this.msgrClient = msgrClient;
		initDisplay();
	}

	public void getTalkRoomList() {

		MessengerDAO dao = MessengerDAO.getInstance();
		MessengerMap pMap = MessengerMap.getInstance();
		pMap.getMap().put("mem_id_vc", msgrClient.getId());
		List<Map<String, Object>> tempList = dao.getTalkRoomList(pMap.getMap());

		for (Map<String, Object> map : tempList) {
			dlm.addElement(map.get("ROOM_NAME_VC"));
		}
		talkRoomList.setModel(dlm);
	}

	public void initDisplay() {
		talkRoomList = new JList<Object>();
		getTalkRoomList();
		scrollPane_list = new JScrollPane(talkRoomList, JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED,
				JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
		Font font = new Font("맑은 고딕", Font.PLAIN, 15);
		talkRoomList.addMouseListener(this);
		talkRoomList.setFont(font);
		this.setLayout(new BorderLayout());
		this.add("Center", scrollPane_list);
		this.setSize(500, 400);
		this.setVisible(true);
	}

	public void message_process(String msg, String imgChoice) {

	}

	@Override
	public void mouseClicked(MouseEvent e) {
		System.out.println(talkRoomList.getSelectedValue());

		if (e.getClickCount() == 2) {

			if (!"".equals(talkRoomList.getSelectedValue())) {
				msgrChatView = new MessengerChatView();
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
