package msgr.client;

import java.awt.BorderLayout;
import java.awt.Font;
import java.awt.MenuItem;
import java.awt.PopupMenu;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.util.List;
import java.util.Map;
import java.util.Vector;

import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.table.DefaultTableModel;

import msgr.server.Protocol;

public class JoinTalkRoomListView extends JPanel implements MouseListener {
	MessengerClientView		msgrClientView	= null;
	List<MessengerChatView>	chatList		= new Vector<MessengerChatView>();

	String[][]				data			= new String[0][2];
	String[]				columnName		= { "톡방이름", "톡방번호" };
	DefaultTableModel		dtm				= new DefaultTableModel(data, columnName) {									// 테이블 내에서 수정 금지
												public boolean isCellEditable(int row, int col) {
													return false;
												}
											};
	JTable					talkRoomTable	= new JTable(dtm);
	JScrollPane				scrollPane_list	= new JScrollPane(talkRoomTable, JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED,
								JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
	MessengerChatView		msgrChatView	= null;
	Font					font			= new Font("맑은 고딕", Font.PLAIN, 15);

	public JoinTalkRoomListView() {

	}

	public JoinTalkRoomListView(MessengerClientView msgrClient) {
		this.msgrClientView = msgrClient;
		initDisplay();
	}

	public void initDisplay() {
		talkRoomTable.setFont(font);
		talkRoomTable.addMouseListener(this);
		this.setLayout(new BorderLayout());
		this.setSize(500, 400);
		this.setVisible(true);
		this.add("Center", scrollPane_list);

	}

	public void getRoomList(List<List<Map<String, Object>>> buddyList) {

		Vector<Object> row = null;
		talkRoomTable.getColumnModel().getColumn(1).setMinWidth(0);
		talkRoomTable.getColumnModel().getColumn(1).setMaxWidth(0);

		while (dtm.getRowCount() > 0) {
			dtm.removeRow(0);
		}

		for (List<Map<String, Object>> index : buddyList) {

			for (Map<String, Object> innderIndex : index) {
				row = new Vector<Object>();
				row.add(0, innderIndex.get("ROOM_NAME_VC"));
				row.add(1, innderIndex.get("ROOM_NO_NU"));
				dtm.addRow(row);
			}
		}
	}

	public void message_process(String msg, String imgChoice) {

	}

	@Override
	public void mouseClicked(MouseEvent e) {

		if (e.getClickCount() == 2) {
			String room_name = String.valueOf(dtm.getValueAt(talkRoomTable.getSelectedRow(), 0));
			System.out.println(room_name);
			String room_no = String.valueOf(dtm.getValueAt(talkRoomTable.getSelectedRow(), 1));
			System.out.println(room_no);

			String request = Protocol.ROOM_IN + Protocol.SEPERATOR + room_no + Protocol.SEPERATOR + room_name;
			msgrClientView.send(request);
			}

//			if (!"".equals(talkRoomTable.getSelectedValue())) {
//				msgrChatView = new MessengerChatView(this);
//				chatList.add(msgrChatView);
//				msgrChatView.initDisplay();
//			}
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
	public void mousePressed(MouseEvent e) {

	}

	@Override
	public void mouseReleased(MouseEvent e) {
		Object obj = e.getSource();
		if (obj == talkRoomTable) {

			if (e.isPopupTrigger()) {
				msgrClientView.getPopupMenu_joinRoom().show(this, e.getX(), e.getY());
			}

		}
	}
}
