package msgr.client;

import java.awt.BorderLayout;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;

public class RoomListView extends JPanel implements MouseListener {
	MessengerClientView	msgrClient		= null;
	String[]		roomName		= { "0번 방", "1번 방", "2번 방", "3번 방", "4번 방", "5번 방", "6번 방", "7번 방", "8번 방", "9번 방"
	};
	JList<String>	roomList		= new JList<>(roomName);
	JScrollPane		scrollPane_list	= new JScrollPane(roomList, JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED,
								JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);

	public RoomListView() {

	}

	public RoomListView(MessengerClientView msgrClient) {
		this.msgrClient = msgrClient;
		initDisplay();
	}

	public void initDisplay() {
		Font font = new Font("맑은 고딕", Font.PLAIN, 15);
		roomList.addMouseListener(this);
		roomList.setFont(font);
		this.setLayout(new BorderLayout());
		this.add("Center", scrollPane_list);
		this.setSize(500, 400);
		this.setVisible(true);
	}

	public void message_process(String msg, String imgChoice) {

	}

	@Override
	public void mouseClicked(MouseEvent e) {
		System.out.println(roomList.getSelectedValue());

		if (e.getClickCount() == 2) {

			if (!"".equals(roomList.getSelectedValue())) {
				System.out.println("어떤 방이든 더블클릭됨");
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
