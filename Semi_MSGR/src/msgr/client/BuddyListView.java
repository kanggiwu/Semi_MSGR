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

import msgr.map.MessengerMap;
import msgr.util.MessengerDAO;

public class BuddyListView extends JPanel implements MouseListener {
	MessengerClientView			msgrClientView	= null;
	JList<Object>				buddyList		= null;
	DefaultListModel<Object>	dlm				= new DefaultListModel<>();
	JScrollPane					scrollPane_list	= null;
	Font						font			= new Font("맑은 고딕", Font.PLAIN, 15);

	public BuddyListView() {

	}

	public BuddyListView(MessengerClientView msgrClientView) {
		this.msgrClientView = msgrClientView;
		initDisplay();
	}

	public void getBuddyList(List<Map<String, Object>> pList) {
		buddyList = new JList<Object>();
		buddyList.setFont(font);
		buddyList.addMouseListener(this);

		for (Map<String, Object> map : pList) {
			dlm.addElement(map.get("BUDDY_ID_VC"));
		}
		buddyList.setModel(dlm);
		scrollPane_list = new JScrollPane(buddyList, JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED,
									JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
		this.add("Center", scrollPane_list);
	}

	public void initDisplay() {
		this.setLayout(new BorderLayout());
		this.setSize(500, 400);
		this.setVisible(true);
	}

	public void message_process(String msg, String imgChoice) {

	}

	@Override
	public void mouseClicked(MouseEvent e) {

		if (e.getClickCount() == 2) {

			if (!"".equals(buddyList.getSelectedValue())) {
				System.out.println(buddyList.getSelectedValue());
			}
		}
	}

	@Override
	public void mouseEntered(MouseEvent e) {
		// TODO Auto-generated method stub

	}

	@Override
	public void mouseExited(MouseEvent e) {
		// TODO Auto-generated method stub

	}

	@Override
	public void mousePressed(MouseEvent e) {
		// TODO Auto-generated method stub

	}

	@Override
	public void mouseReleased(MouseEvent e) {
		Object obj = e.getSource();

		if (obj == buddyList) {

			if (e.isPopupTrigger()) {
				msgrClientView.getPopupMenu().show(this, e.getX(), e.getY());
			}
		}
	}
}
