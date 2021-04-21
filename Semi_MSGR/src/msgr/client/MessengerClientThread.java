package msgr.client;

import java.io.IOException;
import java.net.Socket;
import java.util.List;
import java.util.StringTokenizer;

import javax.swing.JOptionPane;

import msgr.server.Protocol;

public class MessengerClientThread extends Thread {
	MessengerClientView msgrClientView = null;
	Socket socket = null;

	public MessengerClientThread(MessengerClientView msgrClientView) {
		this.msgrClientView = msgrClientView;
		this.socket = msgrClientView.socket;
	}

	public void run() {
		String msg = null;
		boolean isStop = false;

		while (!isStop) {// 무한루프 방지코드를 꼭 추가하자 - 변수처리하자, 조건식을 활용하자

			try {
				// 100|나초보
				msg = msgrClientView.br.readLine();
				JOptionPane.showMessageDialog(msgrClientView, msg + "\n");
				StringTokenizer st = null;
				int protocol = 0;

				if (msg != null) {
					st = new StringTokenizer(msg, Protocol.SEPERATOR);
					protocol = Integer.parseInt(st.nextToken());
				}

				// JOptionPane.showMessageDialog(msgrClientView, "프로토콜:"+protocol);
				switch (protocol) {
				case Protocol.LOGIN: {
					// ois 받는다 list list map 형태로 받아서
					// view 준비하시고 쏘세요
					// 참고 getBuddyList
					// 		getTalkRoomList
					  
					List<String> lt = (List<String>)msgrClientView.ois.readObject();
					System.out.println(lt);
				}
					break;
				case Protocol.LOGOUT: {
					msgrClientView.setVisible(false);
					msgrClientView.dispose();
					msgrClientView.signInView.setVisible(true);
					isStop = true;
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
			} catch (Exception e) {
				e.printStackTrace();
			} finally {
				try {
					socket.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
	}
}
