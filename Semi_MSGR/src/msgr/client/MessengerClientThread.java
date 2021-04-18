package msgr.client;

import java.awt.Color;
import java.awt.event.AdjustmentEvent;
import java.awt.event.AdjustmentListener;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.StringTokenizer;
import java.util.Vector;

import javax.swing.ImageIcon;
import javax.swing.JOptionPane;
import javax.swing.JScrollBar;
import javax.swing.text.MutableAttributeSet;
import javax.swing.text.SimpleAttributeSet;
import javax.swing.text.StyleConstants;

public class MessengerClientThread extends Thread {
	MessengerClientThread msgrClientThread = null;
	String path = "C:\\Java\\dev_javaB\\dev_java\\src\\image\\";
	String g_roomTitle = null;
	int    protocol    = null;
	public MessengerClientThread(MessengerClient msgrClient) {
		this.msgrClientThread = msgrClientThread;
	}

	public SimpleAttributeSet makeAttribute(String fcolor) {
		SimpleAttributeSet sas = new SimpleAttributeSet();
		sas.addAttribute(StyleConstants.ColorConstants.Foreground, new Color(Integer.parseInt(fcolor)));
		return sas;
	}

	public void run() {
		String	msg		= null;
		boolean	isStop	= false;

		while (!isStop) {// 무한루프 방지코드를 꼭 추가하자 - 변수처리하자, 조건식을 활용하자

			try {
				// 100|나초보
				msg = (String) msgrClientThread.ois.readObject();
				JOptionPane.showMessageDialog(msgrClientThread, msg + "\n");
				StringTokenizer	st			= null;
				int				protocol	= 0;

				if (msg != null) {
					st = new StringTokenizer(msg, Protocol.seperator);
					protocol = Integer.parseInt(st.nextToken());
				}

				// JOptionPane.showMessageDialog(msgrClientThread, "프로토콜:"+protocol);
				switch (protocol) {
				case Protocol.LOGIN: {}
					break;
				case Protocol.LOGOUT: {}
					break;
				case Protocol.CHANGE_NICKNAME: {}
					break;
				case Protocol.MEM_DELETE: {}	
					break;
				case Protocol.ROOM_CREATE_FRIEND: {}
					break;
				case Protocol.ROOM_CREATE_OPENTALK: {}
					break;
				case Protocol.ROOM_LIST: {}
					break;
				case Protocol.ROOM_IN: {}
					break;
				case Protocol.ROOM_IN_MEM: {}
					break;
				case Protocol.ROOM_OUT: {}
					break;
				case Protocol.ROOM_DELETE: {}
					break;
				case Protocol.MESSAGE: {}
					break;
				case Protocol.FRIEND_ADD: {}
					break;
				case Protocol.FRIEND_LIST: {}
					break;
				case Protocol.FRIEND_DELETE: {}
					break;
				case Protocol.MESSAGE: {}
					break;
				case Protocol.EMOTICON: {}
					break;
				case Protocol.ATTACHMENT: {}
					break;
				case Protocol.FLAG_OPENROOM: {}
					break;
				case Protocol.FLAG_FRIENDROOM: {}
				break;
			catch (Exception e) {
				e.printStackTrace();
			}
			}
		}
