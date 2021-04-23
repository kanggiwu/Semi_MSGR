package msgr.client;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;

import msgr.server.Protocol;

public class MessengerChatView extends JDialog implements ActionListener {

	private JPanel		centerPanel			= new JPanel(new BorderLayout());
	private JPanel		southPanel			= new JPanel(new BorderLayout());
	JTextArea			chatArea			= new JTextArea();
	private JScrollPane	scrollPane_chat		= new JScrollPane(chatArea, JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED,
								JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
	private JTextField	messegeField		= new JTextField();
	private JButton		button_send			= new JButton("전송");
	private JButton		button_emoticon		= new JButton("이모티콘");
	TalkRoomListView	talkRoomListView	= null;

	public MessengerChatView(TalkRoomListView talkRoomListView) {
		this.talkRoomListView = talkRoomListView;
	}

	public void initDisplay() {
		Font font = new Font("맑은 고딕", Font.PLAIN, 15);
		chatArea.setFont(font);
		centerPanel.add("Center", scrollPane_chat);
		southPanel.add("Center", messegeField);
		southPanel.add("West", button_emoticon);
		southPanel.add("East", button_send);
		this.add("Center", centerPanel);
		chatArea.setEditable(false);
		chatArea.setBackground(Color.ORANGE);
		this.add("South", southPanel);
		messegeField.addActionListener(this);
		button_send.addActionListener(this);
		button_emoticon.addActionListener(this);
		this.setTitle("채팅방 이름");
		this.setDefaultCloseOperation(DISPOSE_ON_CLOSE);
		this.setSize(400, 600);
		this.setVisible(true);
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		Object obj = e.getSource();

		if (messegeField == obj || button_send == obj) {

			// 400 # nickname # 메시지
			try {
				talkRoomListView.msgrClientView.oos.writeObject(
											Protocol.SENDCHAT + Protocol.SEPERATOR + talkRoomListView.msgrClientView.getNickname()
																		+ Protocol.SEPERATOR + messegeField.getText());
			}
			catch (IOException ioe) {
				ioe.printStackTrace();
			}
			messegeField.setText("");
			messegeField.requestFocus();
		}
		else if (button_emoticon == obj) {
			Emoticon emoticon = new Emoticon(this);
		}
	}
}
