package msgr.client;

import java.awt.BorderLayout;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

public class BuddyListView extends JPanel implements ActionListener, ListSelectionListener {
	MessengerClientView	msgrClient		= null;
	String[]		friendName		= { "강지우", "유성열", "이민주", "임동혁" };
	JList<String>	friendList		= null;
	JScrollPane		scrollPane_list	= new JScrollPane(friendList, JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED,
								JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);

	public BuddyListView() {

	}

	public BuddyListView(MessengerClientView msgrClient) {
		friendList = new JList<String>();
		this.msgrClient = msgrClient;
		initDisplay();
	}

	public void initDisplay() {
		Font font = new Font("맑은 고딕", Font.PLAIN, 15);
		friendList.setFont(font);
		friendList.addListSelectionListener(this);
		this.setLayout(new BorderLayout());
		this.add("Center", scrollPane_list);
		this.setSize(500, 400);
		this.setVisible(true);
	}

	public void message_process(String msg, String imgChoice) {

	}

	@Override
	public void actionPerformed(ActionEvent ae) {

//		Object obj = ae.getSource();
	}

	@Override
	public void valueChanged(ListSelectionEvent lse) {
		Object obj = lse.getLastIndex();

		System.out.println(obj);
	}
}
