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
	MessengerChatView	msgrChatView	= null;
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
					List<Map<String, Object>>	tempList	= new Vector<>();
					Map<String, Object>			tempMap		= null;
					int							roomNum		= Integer.parseInt(token.nextToken());

					for (int i = 0; i < roomNum; i++) {
						tempMap = new HashMap<>();
						tempMap.put("talkTitle", token.nextToken());
						// 이건 int로 파싱해야 될 수도 있음
						tempMap.put("talkNo", token.nextToken());
						// 이건 int로 파싱해야 될 수도 있음
						// 또는 삭제
						tempMap.put("isPrivate", token.nextToken());
						tempList.add(tempMap);
					}

					for (Map<String, Object> map : tempList) {
						System.out.println(map);
					}

					List<Map<String, Object>> buddyList = (List) msgrClientView.ois.readObject();

					for (Map<String, Object> map : buddyList) {
						System.out.println(map);
					}

				}
					break;
				case Protocol.SIGNOUT: {
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
					this.msgrChatView = msgrClientView.roomListView.msgrChatView;
					// 21.04.21. 21:27 유성열 수정
					// 400 # nickname # 안녕하세요
					String	nickname	= token.nextToken();
					String	message		= token.nextToken();

					System.out.println(msgrChatView);
					msgrChatView.chatArea.append(nickname + "] " + message + "\n");

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
