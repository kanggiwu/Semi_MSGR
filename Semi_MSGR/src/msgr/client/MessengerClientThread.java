package msgr.client;

import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.HashMap;
import java.util.Map;
import java.util.StringTokenizer;

import javax.swing.JOptionPane;

import msgr.server.Protocol;

public class MessengerClientThread extends Thread {
	MessengerClientView	msgrClientView	= null;
	Socket				socket			= null;
	ObjectOutputStream	oos				= null;
	ObjectInputStream	ois				= null;

	public MessengerClientThread(MessengerClientView msgrClientView) {
		this.msgrClientView = msgrClientView;
		this.socket = msgrClientView.socket;
	}

	public void run() {
		String	msg		= null;
		boolean	isStop	= false;

		while (!isStop) {// 무한루프 방지코드를 꼭 추가하자 - 변수처리하자, 조건식을 활용하자

			try {
				// 100|나초보
				msg = (String) msgrClientView.ois.readObject();
				JOptionPane.showMessageDialog(msgrClientView, msg + "\n");
				StringTokenizer	st			= null;
				int				protocol	= 0;

				if (msg != null) {
					st = new StringTokenizer(msg, Protocol.SEPERATOR);
					protocol = Integer.parseInt(st.nextToken());
				}

				// JOptionPane.showMessageDialog(msgrClientView, "프로토콜:"+protocol);
				switch (protocol) {
				case Protocol.LOGIN: {
					System.out.println(st.nextToken());
				}
					break;
				case Protocol.LOGOUT: {
				}
					break;
				case Protocol.CHANGE_NICKNAME: {
				}
					break;
				case Protocol.MEM_DELETE: {
				}
					break;
				case Protocol.ROOM_CREATE_BUDDY: {
				}
					break;
				case Protocol.ROOM_CREATE_OPENTALK: {
				}
					break;
				case Protocol.ROOM_LIST: {
				}
					break;
				case Protocol.ROOM_IN: {
				}
					break;
				case Protocol.ROOM_IN_MEM: {
				}
					break;
				case Protocol.ROOM_OUT: {
				}
					break;
				case Protocol.ROOM_DELETE: {
				}
					break;
				case Protocol.BUDDY_ADD: {
				}
					break;
				case Protocol.BUDDY_LIST: {
				}
					break;
				case Protocol.BUDDY_DELETE: {
				}
					break;
				case Protocol.SENDCHAT: {
				}
					break;
				case Protocol.EMOTICON: {
				}
					break;
				case Protocol.ATTACHMENT: {
				}
					break;
				case Protocol.FLAG_OPEN_ROOM: {
				}
					break;
				case Protocol.FLAG_BUDDY_ROOM: {
				}
					break;
				}
			}
			catch (Exception e) {
				e.printStackTrace();
			}
		}
	}
}
