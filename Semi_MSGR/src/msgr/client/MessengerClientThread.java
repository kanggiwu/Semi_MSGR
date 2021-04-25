package msgr.client;

import java.awt.HeadlessException;
import java.io.IOException;
import java.net.Socket;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.StringTokenizer;
import java.util.Vector;

import javax.swing.JOptionPane;

import msgr.server.Protocol;

public class MessengerClientThread extends Thread {
	MessengerClientView	msgrClientView	= null;
	Socket				socket			= null;

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
				StringTokenizer	token		= null;
				int				protocol	= 0;

				if (msg != null) {
					token = new StringTokenizer(msg, Protocol.SEPERATOR);
					protocol = Integer.parseInt(token.nextToken());
				}

				// JOptionPane.showMessageDialog(msgrClientView, "프로토콜:"+protocol);
				switch (protocol) {
				case Protocol.SIGNIN: {
					List<Map<String, Object>>		joinBuddyRoomList	= (List) msgrClientView.ois.readObject();
					List<Map<String, Object>>		joinOpenTalkList	= (List) msgrClientView.ois.readObject();
					List<Map<String, Object>>		allOpenTalkList		= (List) msgrClientView.ois.readObject();
					List<Map<String, Object>>		buddyList			= (List) msgrClientView.ois.readObject();
					List<List<Map<String, Object>>>	joinRoomList		= new Vector<List<Map<String, Object>>>();
					joinRoomList.add(joinBuddyRoomList);
					joinRoomList.add(joinOpenTalkList);

					msgrClientView.allOpenTalk_info = allOpenTalkList;

					msgrClientView.joinTalkRoomListView.getRoomList(joinRoomList);
					msgrClientView.buddyListView.getBuddyList(buddyList);
					msgrClientView.openTalkRoomListView.getRoomList(allOpenTalkList);
				}
					break;
				case Protocol.SIGNOUT: {
					JOptionPane.showMessageDialog(msgrClientView, msg + "\n");
					msgrClientView.setVisible(false);
					msgrClientView.dispose();
					msgrClientView.signInView.setId("");
					msgrClientView.signInView.setNickname("");
					msgrClientView.signInView.setVisible(true);
					isStop = true;
				}
					break;
				case Protocol.CHANGE_NICKNAME: {
					JOptionPane.showMessageDialog(msgrClientView, msg + "\n");
					// 130 # mem_id_vc # nickname

					// 아이디는 바뀌지 않지만 혹시 몰라 바꿔 줌.
					msgrClientView.setId(token.nextToken());
					msgrClientView.setNickname(token.nextToken());
					msgrClientView.setTitle(msgrClientView.getNickname() + "(" + msgrClientView.getId() + ")님");

				}
					break;
				case Protocol.MEM_DELETE: {
					JOptionPane.showMessageDialog(msgrClientView, msg + "\n");
					msgrClientView.setVisible(false);
					msgrClientView.dispose();
					msgrClientView.signInView.setId("");
					msgrClientView.signInView.setNickname("");
					msgrClientView.signInView.setVisible(true);
					isStop = true;
				}
					break;
				case Protocol.ROOM_CREATE_BUDDY: {
					List<Map<String, Object>> tempList = (List<Map<String, Object>>) msgrClientView.ois.readObject();
//					msgrClientView.joinTalkRoomListView.getRoomList(tempList);
				}
					break;

				// 201 #
				case Protocol.ROOM_CREATE_OPENTALK: {
					List<Map<String, Object>> tempList = (List<Map<String, Object>>) msgrClientView.ois.readObject();
//					msgrClientView.joinTalkRoomListView.getRoomList(tempList);
				}
					break;
				case Protocol.ROOM_LIST: {
					List<Map<String, Object>> tempList = (List<Map<String, Object>>) msgrClientView.ois.readObject();
//					msgrClientView.joinTalkRoomListView.getRoomList(tempList);
				}
					break;

				case Protocol.JOIN_OPENROOM: {

				}
					break;

				case Protocol.ROOM_IN: {
					String						room_name	= (String) msgrClientView.ois.readObject();
					List<Map<String, Object>>	chatList	= (List) msgrClientView.ois.readObject();
					msgrClientView.joinTalkRoomListView.msgrChatView.setTitle(room_name);

					for (Map<String, Object> map : chatList) {
						msgrClientView.joinTalkRoomListView.msgrChatView.chatArea
													.append(map.get("mem_nick_vc") + ": " + map.get("chat_vc"));

					}

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
					// 21.04.24. 21:40 유성열 수정하는중....... 어케해야됨?
					// 400 # nickname # 안녕하세요
					String	nickname	= token.nextToken();
					String	message		= token.nextToken();

					msgrClientView.joinTalkRoomListView.msgrChatView.chatArea.append(nickname + "] " + message + "\n");

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
			catch (IOException ioe) {
				ioe.printStackTrace();
			}
			catch (ClassNotFoundException cnfe) {
				cnfe.printStackTrace();
			}
			catch (HeadlessException he) {
				he.printStackTrace();
			}
			catch (NumberFormatException nfe) {
				nfe.printStackTrace();
			}
		}
	}
}
