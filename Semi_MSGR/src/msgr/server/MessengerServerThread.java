package msgr.server;

import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.StringTokenizer;
import java.util.Vector;

import msgr.map.MessengerMap;
import msgr.util.MessengerDAO;

public class MessengerServerThread extends Thread {

	MessagerServer			msgrServer		= null;
	Socket					client			= null;
	ObjectOutputStream		oos				= null;
	ObjectInputStream		ois				= null;
	String					id				= "";
	String					nickname		= "";
	MessengerDAO			msgrDAO			= null;
	MessengerMap			pMap			= null;
	List<MessengerTalkRoom>	talkRoomList	= null;
	List<String>			myBuddyList		= null;

	// 깃허브 연습
	public MessengerServerThread(MessagerServer msgrServer) {
		this.msgrServer = msgrServer;
		this.client = msgrServer.client;

		try {
			oos = new ObjectOutputStream(client.getOutputStream());
			ois = new ObjectInputStream(client.getInputStream());
			msgrServer.globalList.add(this);
		}
		catch (Exception e) {
			e.getStackTrace();
		}
	}

	public void run() {
		String msg = "";
		talkRoomList = new Vector<>();
		boolean isStop = false;

		try {
			pMap = MessengerMap.getInstance();
			msgrDAO = MessengerDAO.getInstance();

			//////////////////// while문 시작////////////////////
			run_start: while (!isStop) {
				msg = (String) ois.readObject();// 클라이언트에서 보낸 메시지 받기
				msgrServer.textArea_log.append(msg + "\n");// 클라이언트에서 받은 메시지 로그창에 출력
				msgrServer.textArea_log.setCaretPosition(msgrServer.textArea_log.getDocument().getLength());// 로그창 맨 아래로
																											// 스크롤
				StringTokenizer	token		= null;	// 토큰 선언
				int				protocol	= -1;	// 프로토콜 선언

				if (msg != null) {
					token = new StringTokenizer(msg, Protocol.SEPERATOR);
					protocol = Integer.parseInt(token.nextToken()); // 프로토콜 초기화
				}

				// 클라이언트에서 로그인 시, 친구리스트 출력 & 톡방리스트 출력 요청
				switch (protocol) {
				/*	(((((수신))))) 100 # id # nickname
					(((((송신))))) 프로토콜(String) | 톡방리스트 | 친구리스트*/
				case Protocol.SIGNIN: {// 로그인.======================================================================>완료
					String response = null;
					id = token.nextToken();
					nickname = token.nextToken();

					// 클라이언트에서 받은 메시지 로그창에 출력
					msgrServer.textArea_log.append(msg + "님이 로그인\n");

					// 톡방 목록을 DB에서 받아오기
					pMap.getMap().put("mem_id_vc", id);
					List<Map<String, Object>> roomList = msgrDAO.getTalkRoomList(pMap.getMap());// id를 파라미터로 넘겨준 뒤 마이바티스를 통해 해당하는 id가 참여한
																								// 톡방리스트를 받아온다
					// 받아온 톡방리스트 별로 톡방 객체를 생성한 뒤 톡방List에 넣어준다.
					setTalkRoomList(roomList);

					// 친구 목록 DB에서 받아오기
					pMap.getMap().put("mem_id_vc", id);
					List<Map<String, Object>> buddyList = msgrDAO.getBuddyList(pMap.getMap());

					// 내 친구 리스트 id 저장
					myBuddyList = new ArrayList<String>();

					for (Map<String, Object> map : buddyList) {
						myBuddyList.add(String.valueOf(map.get("BUDDY_ID_VC")));
					}
					// 클라이언트 스레드에 메시지 전송
					response = Integer.toString(Protocol.SIGNIN);
					send(response);// 로그인 프로토콜 전송
					send(roomList);// 톡방 리스트 전송
					send(buddyList);// 친구 리스트 전송

				}
					break;

				// 120 # id
				case Protocol.SIGNOUT: {// 로그아웃============================================================================>완료
					msgrServer.textArea_log.append(msg + "님이 로그아웃\n");// 클라이언트에서 받은 메시지 로그창에 출력

					// 로그 맨 아래로 스크롤
					msgrServer.textArea_log.setCaretPosition(msgrServer.textArea_log.getDocument().getLength());
					msgrServer.globalList.remove(this);// 접속자 목록에서 삭제
					String response = Protocol.SIGNOUT + Protocol.SEPERATOR + id;
					System.out.println(id);
					send(response);
					isStop = true;
					// 현재 접속자에 대한 정보를 클라이언트가 알 필요는 없으니까, 메세지를 보낼 필요가 없다.?
				}
					break;

				// 130 # id # aftername
				case Protocol.CHANGE_NICKNAME: {// 닉네임변경=======================================================>완료
					msgrServer.textArea_log.append(msg + "\n");// 클라이언트에서 받은 메시지 로그창에 출력
					msgrServer.textArea_log.setCaretPosition(msgrServer.textArea_log.getDocument().getLength());
					String response = null;

					id = token.nextToken(); // id는 버린다. 로그용
					String aftername = token.nextToken();

					// 닉네임 변경 여부 받기
					pMap.getMap().put("mem_id_vc", id);
					pMap.getMap().put("mem_nick_vc", aftername);
					int result = msgrDAO.changeNickname(pMap.getMap());

					if (result == 1) {
						nickname = aftername;
					}
					System.out.println(result);
					response = Protocol.CHANGE_NICKNAME + Protocol.SEPERATOR + id + Protocol.SEPERATOR + nickname;
					// 접속해 있는 친구들에게 브로드 캐스팅
					buddyCasting(response);
					/* 사용자 본인에게 닉네임 변경 결과 전송
					 * 바뀌었을 경우 바뀐 닉네임 else 원래 닉네임
					 * 을 정의서에 추가해주기~
					 * 
					 */
					send(response);
				}
					break;

				/*	(((((수신))))) 140
				(((((송신))))) 140 # 
				*/
				case Protocol.MEM_DELETE: {// 회원탈퇴
											// =========================================================================>완료
					msgrServer.textArea_log.append(msg + id + "님이 회원탈퇴\n");// 클라이언트에서 받은 메시지 로그창에 출력
					msgrServer.textArea_log.setCaretPosition(msgrServer.textArea_log.getDocument().getLength());

					// DB에 해당하는 회원 정보 삭제
					pMap.getMap().put("mem_id_vc", id);
					msgrDAO.deleteMember(pMap.getMap());

					// 회원탈퇴한 회원 정보를 송신
					// 클라이언트 스레드에서 해당하는 정보를 받으면 창을 끄고 로그인화면을 띄워줌
					String response = Integer.toString(Protocol.MEM_DELETE);
					send(response);

					/*	회원탈퇴하는 경우, 친구목록에서 사라지고, 대화창에서도 사라진다. 
					 *	회원탈퇴를 했다. 	-> 친구들 스레드에서 친구리스트에서 동일한 아이디들 삭제
					 * 						-> 친구들한테 해당 아이디 알려줘서(buddyCasting) DTM에서 삭제
					 * 						-> 
					*/

					// 친구들의 친구리스트에서 회원탈퇴한 아이디 삭제
					removeBuddy(id);

					// 친구리스트 갱신
					// 클라이언트 쪽에서 해당하는 아이디랑 같은 경우 DTM에서 삭제해준다.
					response = null;
					response = Protocol.BUDDY_LIST_UPDATE + id;

					buddyCasting(response);
					msgrServer.globalList.remove(this);

				}
					break;
				/*	(((((수신))))) 200 | 친구톡을 생성할 친구리스트(id,nickname)
				(((((송신))))) 200 */
				case Protocol.ROOM_CREATE_BUDDY: {// 친구톡 생성
					msgrServer.textArea_log.setCaretPosition(msgrServer.textArea_log.getDocument().getLength());
					int	checkDao	= -3;

					// 마지막 톡방 번호를 받아오고
					int	lastTalk_no	= -1;
					lastTalk_no = msgrDAO.getLastRoomNum();

					String						room_name			= nickname + "님";
					List<Map<String, Object>>	selectedBuddyList	= (List) ois.readObject();

					for (Map<String, Object> index : selectedBuddyList) {
						System.out.println(index.get("mem_nick_vc"));
					}

					for (Map<String, Object> buddy : selectedBuddyList) {
						room_name += ", " + buddy.get("mem_nick_vc")
													+ "님";
					}
					room_name += "의 방";
					msgrServer.textArea_log.append("방이름 : " + room_name + "\n");
					// 방을 만들고
					pMap.getMap().put("room_no_nu", ++lastTalk_no);
					pMap.getMap().put("room_name_vc", room_name);
					checkDao = msgrDAO.createBuddyTalkRoom(pMap.getMap());
					msgrServer.textArea_log.append("친구톡방 생성 성공 여부 : " + checkDao + "\n");
					// 해당하는 방에 참여자들을 추가해준다.

					// 자기 자신 참여
					pMap.getMap().put("mem_id_vc", id);
					pMap.getMap().put("chat_no_nu", 1);
					checkDao = msgrDAO.JoinChatMember(pMap.getMap());

					// 친구들 참여
					for (Map<String, Object> buddy : selectedBuddyList) {
						// 마지막 톡방번호는 이미 209번에서 put 돼서 필요없음
//						pMap.getMap().put("room_no_nu", lastTalk_no);
						pMap.getMap().put("mem_id_vc", buddy.get("buddy_id_vc"));
						pMap.getMap().put("chat_no_nu", 1);

						checkDao = msgrDAO.JoinChatMember(pMap.getMap());
						msgrServer.textArea_log.append("친구톡방 참여 성공 여부 : " + checkDao + "\n");
					}

					// 나한테 전송
					pMap.getMap().put("mem_id_vc", id);
					List<Map<String, Object>>	roomList	= msgrDAO.getTalkRoomList(pMap.getMap());
					String						response	= Protocol.ROOM_CREATE_BUDDY + Protocol.SEPERATOR;
					send(response);
					send(roomList);

					// 로그인한 친구들 톡방목록 갱신
					for (MessengerServerThread currentUser : msgrServer.globalList) {

						for (Map<String, Object> selectedBuddy : selectedBuddyList) {

							if (selectedBuddy.get("buddy_id_vc").equals(currentUser.id)) {
								pMap.getMap().put("mem_id_vc", selectedBuddy.get("buddy_id_vc"));
								List<Map<String, Object>>	roomList_buddy	= msgrDAO.getTalkRoomList(pMap.getMap());
								String						response_buddy	= Protocol.ROOM_LIST + Protocol.SEPERATOR;
								currentUser.send(response_buddy);
								currentUser.send(roomList_buddy);
							}
						}
					}

					// 톡방리스트에 톡방 추가
					MessengerTalkRoom msgrTalkRoom = new MessengerTalkRoom();
					msgrTalkRoom.setMsgrTalkRoom(room_name, lastTalk_no, 1);
					talkRoomList.add(msgrTalkRoom);

				}
					break;

				// 201 # 닉네임(쓰진 않는듯)
				case Protocol.ROOM_CREATE_OPENTALK: {// 오픈톡 생성
					msgrServer.textArea_log.setCaretPosition(msgrServer.textArea_log.getDocument().getLength());

					int	checkDao	= -3;
					int	lastTalk_no	= -1;
					lastTalk_no = msgrDAO.getLastRoomNum();
					String room_name = nickname + "님의 오픈톡방";
					// 방을 만들고
					pMap.getMap().put("room_no_nu", ++lastTalk_no);
					pMap.getMap().put("room_name_vc", room_name);
					checkDao = msgrDAO.createOpenTalkRoom(pMap.getMap());
					msgrServer.textArea_log.append("오픈톡방 생성 성공 여부 : " + checkDao + "\n");

					pMap.getMap().put("mem_id_vc", id);
					pMap.getMap().put("chat_no_nu", 0);

					checkDao = msgrDAO.JoinChatMember(pMap.getMap());
					msgrServer.textArea_log.append("오픈톡방 참여 성공 여부 : " + checkDao + "\n");

					pMap.getMap().put("mem_id_vc", id);
					List<Map<String, Object>>	roomList	= msgrDAO.getTalkRoomList(pMap.getMap());

					String						response	= Protocol.ROOM_CREATE_OPENTALK + Protocol.SEPERATOR;
					send(response);
					send(roomList);

				}
					break;
				case Protocol.ROOM_LIST: {// 톡방 리스트 출력
					msgrServer.textArea_log.append(msg + "\n");// 클라이언트에서 받은 메시지 로그창에 출력
					msgrServer.textArea_log.setCaretPosition(msgrServer.textArea_log.getDocument().getLength());
					String response = null;

					// 톡방 목록을 DB에서 받아오기
					pMap.getMap().put("mem_id_vc", id);
					List<Map<String, Object>> roomList = msgrDAO.getTalkRoomList(pMap.getMap());// id를 파라미터로 넘겨준 뒤 마이바티스를 통해 해당하는 id가 참여한
																								// 톡방리스트를 받아온다
					// 받아온 톡방리스트 별로 톡방 객체를 생성한 뒤 톡방List에 넣어준다.
					setTalkRoomList(roomList);

					response = Integer.toString(Protocol.ROOM_LIST);
					send(response);// 톡방 리스트 출력 프로토콜 전송
					send(roomList);// 톡방 리스트 전송

				}
					break;
				/*
				 * (수신) 211 # 톡방번호 # 톡방이름
				 * (송신) 
				 */
				case Protocol.ROOM_PARTICIPATE: { // 톡방 참가
					msgrServer.textArea_log.append(msg + "\n");// 클라이언트에서 받은 메시지 로그창에 출력
					msgrServer.textArea_log.setCaretPosition(msgrServer.textArea_log.getDocument().getLength());

					int	checkDAO	= -3;
					int	talk_no		= Integer.parseInt(token.nextToken());

					pMap.getMap().put("room_no_nu", talk_no);
					checkDAO = msgrDAO.getLastChatNum(pMap.getMap());

					System.out.println("마지막 대화번호 가져오기 DAO 체크" + checkDAO);

				}
					break;
				/*	(수신) 212 # 톡방번호
				 *	(송신) 212 # 톡방 이름 | 참가한 후 채팅내용
				 */
				case Protocol.ROOM_IN: {// 톡방 입장
					msgrServer.textArea_log.append(msg + "\n");// 클라이언트에서 받은 메시지 로그창에 출력
					msgrServer.textArea_log.setCaretPosition(msgrServer.textArea_log.getDocument().getLength());
					String	response	= null;
					String	talkTitle	= null;
					int		room_no		= Integer.parseInt(token.nextToken());
					//

					// 톡방 참가한 이후의 대화내용 가져오기
					pMap.getMap().put("room_no_nu", room_no);
					List<Map<String, Object>> chatList = msgrDAO.getChatAfterJoin(pMap.getMap());

					// 톡방 번호가 같은 톡방의 제목을 받아옴
					for (MessengerTalkRoom map : talkRoomList) {
						if (map.getTalk_no() == room_no)
							talkTitle = map.getTalkTitle();
					}

					response = Protocol.ROOM_IN
												+ Protocol.SEPERATOR
												+ talkTitle;
					send(response);
					send(chatList);
				}

					break;
				case Protocol.ROOM_IN_MEM: {// 톡방 참가 인원
					msgrServer.textArea_log.append(msg + "\n");// 클라이언트에서 받은 메시지 로그창에 출력
					msgrServer.textArea_log.setCaretPosition(msgrServer.textArea_log.getDocument().getLength());

				}
					break;
				case Protocol.ROOM_OUT: {// 톡방 닫기
					msgrServer.textArea_log.append(msg + "\n");// 클라이언트에서 받은 메시지 로그창에 출력
					msgrServer.textArea_log.setCaretPosition(msgrServer.textArea_log.getDocument().getLength());

				}
					break;
				case Protocol.ROOM_DELETE: {// 톡방 나가기
					msgrServer.textArea_log.append(msg + "\n");// 클라이언트에서 받은 메시지 로그창에 출력
					msgrServer.textArea_log.setCaretPosition(msgrServer.textArea_log.getDocument().getLength());

				}
					break;

				// 300 # 친구아이디
				case Protocol.BUDDY_ADD: {// 친구추가

					String buddyId = token.nextToken();

					pMap.getMap().put("mem_id_vc", id);
					pMap.getMap().put("buddy_id_vc", buddyId);
//					int result = msgrDAO.makeBuddyProcedure(pMap.getMap());
//					System.out.println(result);

					// 친구 추가 시 해당하는 친구가 있는지 확인하고 , 있을 경우, 해당 아이디가 있다는 것을 클라이언트 스레드에게 보내준다.
					// 클라이언트 스레드에서 해당하는 친구가 있음을 출력하는 optionPane을 출력해준다.

				}
					break;
				case Protocol.BUDDY_LIST: {// 친구목록 출력
					msgrServer.textArea_log.append(msg + "\n");// 클라이언트에서 받은 메시지 로그창에 출력
					msgrServer.textArea_log.setCaretPosition(msgrServer.textArea_log.getDocument().getLength());// 로그창 맨// 아래로// 스크롤

				}
					break;
				case Protocol.BUDDY_DELETE: {// 친구 삭제
					msgrServer.textArea_log.append(msg + "\n");// 클라이언트에서 받은 메시지 로그창에 출력
					msgrServer.textArea_log.setCaretPosition(msgrServer.textArea_log.getDocument().getLength());// 로그창 맨
																												// 아래로
																												// 스크롤

				}
					break;
				// 400 # nickname # 메시지
				case Protocol.SENDCHAT: {// 메시지 전송
					String	nickname	= token.nextToken();
//					String	talkRoomFlag	= token.nextToken();
					String	chat		= token.nextToken();

					String	response	= Protocol.SENDCHAT + Protocol.SEPERATOR + nickname + Protocol.SEPERATOR + chat;

					// 테스트용) 모든 스레드에게 브로드캐스팅 하는 걸로

					send(response);
					buddyCasting(response);

					// 방에 참여한 사람들에게만 메시지 전송
					// broadCasting
				}
					break;
				case Protocol.EMOTICON: {// 이모티콘 전송
					msgrServer.textArea_log.append(msg + "\n");// 클라이언트에서 받은 메시지 로그창에 출력
					msgrServer.textArea_log.setCaretPosition(msgrServer.textArea_log.getDocument().getLength());// 로그창 맨
																												// 아래로
																												// 스크롤

				}
					break;
				case Protocol.ATTACHMENT: {// 파일 전송
					msgrServer.textArea_log.append(msg + "\n");// 클라이언트에서 받은 메시지 로그창에 출력
					msgrServer.textArea_log.setCaretPosition(msgrServer.textArea_log.getDocument().getLength());// 로그창 맨
																												// 아래로
																												// 스크롤

				}
					break run_start;
				}// ========================== end of switch
			} // ====================== end of while
		}
		catch (Exception e) {
			e.getStackTrace();
		}
	}// ======================== end of run

	private void removeBuddy(String id) {

		for (MessengerServerThread msgrSeverThread : msgrServer.globalList) {

			for (String buddyId : msgrSeverThread.myBuddyList) {

				if (buddyId == id) {
					msgrSeverThread.myBuddyList.remove(id);
				}
			}
		}
	}

	private void send(Object response) {

		try {
			oos.writeObject(response);
		}
		catch (Exception e) {
			e.printStackTrace();
		}
	}

	// 접속한 모든 사람들에게 브로드캐스팅
	private void broadCasting(Object response) {

		// 오픈톡방 리스트 출력
		for (MessengerServerThread msgrServerThread : msgrServer.globalList) {
			msgrServerThread.send(response);
		}
	}

	private void buddyCasting(Object response) {// 접속한 친구들한테만 브로드캐스팅
		// 접속한 사람들 중

		for (MessengerServerThread msgrServerThread : msgrServer.globalList) {

			// 그 사람들의 친구리스트에서
			for (String buddyId : msgrServerThread.myBuddyList) {

				// 내가 있는 경우
				if (this.id == buddyId) {
					// 전송
					send(response);
				}
			}
		}

	}// end of buddyCasting()

	public void setTalkRoomList(List<Map<String, Object>> roomList) {
		this.talkRoomList = new Vector<>();
		MessengerTalkRoom msgrTalkRoom = null;

		for (Map<String, Object> map : roomList) {
			msgrTalkRoom = new MessengerTalkRoom();
			String	room_name	= (String) map.get("ROOM_NAME_VC");
			// 해당 부분에서 멈춰버려서 오브젝트를 스트링으로 바꾸고 파스인트를 하니까 됨
			// int room_no = (int) map.get("ROOM_NO_NU"));
			int		room_no		= Integer.parseInt(map.get("ROOM_NO_NU").toString());
			int		is_private	= Integer.parseInt(map.get("IS_PRIVATE_YN").toString());

			msgrTalkRoom.setMsgrTalkRoom(room_name, room_no, is_private);
			talkRoomList.add(msgrTalkRoom);// 톡방리스트에 만들어준 톡방을 넣어준다.
		}
	}
}
