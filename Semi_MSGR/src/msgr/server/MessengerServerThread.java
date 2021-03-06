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

	MessagerServer		msgrServer	= null;
	Socket				client		= null;
	ObjectOutputStream	oos			= null;
	ObjectInputStream	ois			= null;
	String				id			= "";
	String				nickname	= "";
	MessengerDAO		msgrDAO		= null;
	MessengerMap		pMap		= null;
	List<String>		myBuddyList	= null;

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
		String	msg		= "";
		boolean	isStop	= false;

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
					(((((송신))))) 프로토콜(String) | 참여한 친구톡방리스트 | 참여한 오픈톡방| 전체 오픈톡방| 친구리스트*/
				case Protocol.SIGNIN: {// 로그인.======================================================================>완료
					String response = null;
					id = token.nextToken();
					nickname = token.nextToken();

					// 참여한 톡방 리스트 불러오기
					// 친구 톡방 불러오기
					pMap.getMap().put("mem_id_vc", id);
					List<Map<String, Object>> joinBuddyRoomList = msgrDAO.getJoinBuddyTalkList(pMap.getMap());
					// 오픈 톡방 불러오기
					pMap.getMap().put("mem_id_vc", id);
					List<Map<String, Object>> joinOpenTalkList = msgrDAO.getJoinOpenTalkList(pMap.getMap());

					// 전체 톡방 불러오기
					pMap.getMap().put("mem_id_vc", id);
					List<Map<String, Object>> allOpenTalkList = msgrDAO.getAllOpenTalkList(pMap.getMap());

					// 친구 목록 DB에서 받아오기
					pMap.getMap().put("mem_id_vc", id);
					List<Map<String, Object>> buddyList = msgrDAO.getBuddyList(pMap.getMap());

					// 내 친구 리스트 id 저장
					myBuddyList = new ArrayList<String>();

					for (Map<String, Object> map : buddyList) {
						myBuddyList.add(String.valueOf(map.get("BUDDY_ID_VC")));
					}
					// 클라이언트 스레드에 메시지 전송
					response = Protocol.SIGNIN + Protocol.SEPERATOR;
					send(response);// 로그인 프로토콜 전송
					send(joinBuddyRoomList);// 입장 중인 친구톡방
					send(joinOpenTalkList);// 입장 중인 오픈톡방
					send(allOpenTalkList);// 모든 오픈톡방
					send(buddyList);// 친구 리스트 전송

				}
					break;

				// 120 # id
				case Protocol.SIGNOUT: {// 로그아웃============================================================================>완료
					msgrServer.globalList.remove(this);// 접속자 목록에서 삭제

					String response = Protocol.SIGNOUT + Protocol.SEPERATOR + id;
					send(response);
					isStop = true;
					// 현재 접속자에 대한 정보를 클라이언트가 알 필요는 없으니까, 메세지를 보낼 필요가 없다.?
				}
					break;

				// 130 # id # aftername
				case Protocol.CHANGE_NICKNAME: {// 닉네임변경=======================================================>완료
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
				case Protocol.MEM_DELETE: {// 회원탈퇴=========================================>완료

					int result = 0;
					// DB에 해당하는 회원 정보 삭제
					pMap.getMap().put("mem_id_vc", id);
					result = msgrDAO.deleteMember(pMap.getMap());

					// 회원탈퇴한 회원 정보를 송신
					// 클라이언트 스레드에서 해당하는 정보를 받으면 창을 끄고 로그인화면을 띄워줌
					String response = Protocol.MEM_DELETE + Protocol.SEPERATOR + result;
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
				case Protocol.ROOM_CREATE_BUDDY: {// 친구톡 생성 + 친구들 해당 톡방에 참여=================>완료
					int	checkDao	= -3;

					// 마지막 톡방 번호를 받아오고
					int	lastTalk_no	= -1;
					lastTalk_no = msgrDAO.getLastRoomNum();
					// 방이름 생성한 사람 먼저
					String						room_name			= nickname + "님";
					// 선택한 친구 정보 받아오고
					List<Map<String, Object>>	selectedBuddyList	= (List) ois.readObject();

					// 받아온 친구들 닉네임 방제목에 붙이기
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
					pMap.getMap().put("join_chat_no_nu", 1);
					checkDao = msgrDAO.joinChatMember(pMap.getMap());
					msgrServer.textArea_log.append("친구톡방 본인 참여 성공 여부 : " + checkDao + "\n");

					// 친구들 참여
					for (Map<String, Object> buddy : selectedBuddyList) {
						// 마지막 톡방번호는 이미 209번에서 put 돼서 필요없음
//						pMap.getMap().put("room_no_nu", lastTalk_no);
						pMap.getMap().put("mem_id_vc", buddy.get("buddy_id_vc"));
						pMap.getMap().put("join_chat_no_nu", 1);

						checkDao = msgrDAO.joinChatMember(pMap.getMap());
						msgrServer.textArea_log.append("친구톡방 참여 성공 여부 : " + checkDao + "\n");
					}

					// 나한테 전송
					pMap.getMap().put("mem_id_vc", id);
					List<Map<String, Object>>		joinBuddyRoomList	= msgrDAO.getJoinBuddyTalkList(pMap.getMap());
					List<Map<String, Object>>		joinOpenTalkList	= msgrDAO.getJoinOpenTalkList(pMap.getMap());
					List<List<Map<String, Object>>>	joinRoomList		= new Vector<List<Map<String, Object>>>();
					joinRoomList.add(joinBuddyRoomList);
					joinRoomList.add(joinOpenTalkList);
					String response = Protocol.ROOM_CREATE_BUDDY + Protocol.SEPERATOR;
					send(response);
					send(joinRoomList);

					// 로그인한 친구들 톡방목록 갱신
					for (MessengerServerThread currentUser : msgrServer.globalList) {

						for (Map<String, Object> selectedBuddy : selectedBuddyList) {

							if (selectedBuddy.get("buddy_id_vc").equals(currentUser.id)) {
								pMap.getMap().put("mem_id_vc", selectedBuddy.get("buddy_id_vc"));
								List<Map<String, Object>>		b_joinBuddyRoomList	= msgrDAO.getJoinBuddyTalkList(pMap.getMap());
								List<Map<String, Object>>		b_joinOpenTalkList	= msgrDAO.getJoinOpenTalkList(pMap.getMap());
								List<List<Map<String, Object>>>	b_joinRoomList		= new Vector<List<Map<String, Object>>>();
								b_joinRoomList.add(b_joinBuddyRoomList);
								b_joinRoomList.add(b_joinOpenTalkList);
								String response_buddy = Protocol.ROOM_LIST + Protocol.SEPERATOR;
								currentUser.send(response_buddy);
								currentUser.send(b_joinRoomList);
							}
						}
					}

				}
					break;

				// 201 # 닉네임(쓰진 않는듯)
				case Protocol.ROOM_CREATE_OPENTALK: {// 오픈톡 생성

					int	checkDao	= -3;
					int	lastTalk_no	= -1;
					lastTalk_no = msgrDAO.getLastRoomNum();
					String room_name = nickname + "님의 오픈톡방";
					// 방을 만들고
					pMap.getMap().put("room_no_nu", ++lastTalk_no);
					pMap.getMap().put("room_name_vc", room_name);
					checkDao = msgrDAO.createOpenTalkRoom(pMap.getMap());
					msgrServer.textArea_log.append("본인의 오픈톡방 생성 성공 여부 : " + checkDao + "\n");

					pMap.getMap().put("mem_id_vc", id);
					pMap.getMap().put("join_chat_no_nu", 1);

					checkDao = msgrDAO.joinChatMember(pMap.getMap());
					msgrServer.textArea_log.append("본인의 오픈톡방 참여 성공 여부 : " + checkDao + "\n");

					// 참여한 친구&오픈톡방 목록
					pMap.getMap().put("mem_id_vc", id);
					List<Map<String, Object>>		joinBuddyRoomList	= msgrDAO.getJoinBuddyTalkList(pMap.getMap());
					List<Map<String, Object>>		joinOpenTalkList	= msgrDAO.getJoinOpenTalkList(pMap.getMap());
					List<List<Map<String, Object>>>	joinRoomList		= new Vector<List<Map<String, Object>>>();
					joinRoomList.add(joinBuddyRoomList);
					joinRoomList.add(joinOpenTalkList);

					// 모든 오픈톡방 목록
					List<Map<String, Object>>	openTalkList	= msgrDAO.getAllOpenTalkList(pMap.getMap());

					String						response		= Protocol.ROOM_CREATE_OPENTALK + Protocol.SEPERATOR;

					// 나 자신에게 생성된 오픈 톡방을 출력
					send(response);
					send(joinRoomList);
					send(openTalkList);
					response = Protocol.OPEN_ROOM_LIST + Protocol.SEPERATOR;
					// 접속한모든 사람들에게 생성된 오픈 톡방을 출력
					broadCasting(response);
					broadCasting(openTalkList);

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

					response = Integer.toString(Protocol.ROOM_LIST);
					send(response);// 톡방 리스트 출력 프로토콜 전송
					send(roomList);// 톡방 리스트 전송

				}
					break;
				/*
				 * (수신) 211 # 톡방번호 # 톡방이름
				 * (송신) 211 # 톡방번호 # 톡방이름 
				 */
				case Protocol.JOIN_OPENROOM: { // 오픈톡방 참가 ===========================>완료
					String	response	= null;
					int		room_no		= Integer.parseInt(token.nextToken());
					msgrServer.textArea_log.append("방번호" + room_no + "\n");// 클라이언트에서 받은 메시지 로그창에 출력
					String room_name = token.nextToken();
					msgrServer.textArea_log.append("방이름" + room_name + "\n");// 클라이언트에서 받은 메시지 로그창에 출력
					int	last_chatNum	= -1;
					int	checkDAO		= -3;

					// 마지막 방 번호 가져오기
					pMap.getMap().put("room_no_nu", room_no);
					last_chatNum = msgrDAO.getLastChatNum(pMap.getMap());
					msgrServer.textArea_log.append("마지막 대화번호 가져오기 성공\n");// 클라이언트에서 받은 메시지 로그창에 출력

					// 해당 방에 넣어주기
					pMap.getMap().put("room_no_nu", room_no);
					pMap.getMap().put("mem_id_vc", id);
					pMap.getMap().put("join_chat_no_nu", ++last_chatNum);
					checkDAO = msgrDAO.joinChatMember(pMap.getMap());
					msgrServer.textArea_log.append("해당 방에 회원 넣어주기 성공\n");// 클라이언트에서 받은 메시지 로그창에 출력

					System.out.println("방 입장 DAO 체크" + checkDAO);

					response = Protocol.JOIN_OPENROOM
												+ Protocol.SEPERATOR
												+ room_no
												+ Protocol.SEPERATOR
												+ room_name;
					send(response);

					// 클라이언트에서 해당 참여톡방에 추가

				}
					break;
				/*	(수신) 212 # 톡방번호 # 톡방 이름
				 *	(송신) 212 # 톡방번호 # 톡방 이름 | 참가한 후 채팅내용
				 */
				case Protocol.ROOM_IN: {// 톡방 입장(이전 대화내용 출력)===========================>수정중
					msgrServer.textArea_log.append(msg + "," + id + "톡방에 입장\n");// 클라이언트에서 받은 메시지 로그창에 출력
					msgrServer.textArea_log.setCaretPosition(msgrServer.textArea_log.getDocument().getLength());
					String	response	= null;
					int		room_no		= Integer.parseInt(token.nextToken());
					// 톡방이름은 타이틀에 띄워주기 위해 필요
					String	room_name	= token.nextToken();

					// 참여한 톡 번호 가져오기
					pMap.getMap().put("room_no_nu", room_no);
					pMap.getMap().put("mem_id_vc", id);

					int joinChatNum = msgrDAO.getJoinChatNum(pMap.getMap());
					msgrServer.textArea_log.append("참여한 톡방 채팅번호: " + joinChatNum);

					// 참여한 톡 번호 이후로 대화내용 가져오기
					pMap.getMap().put("room_no_nu", room_no);
					pMap.getMap().put("join_chat_no_nu", joinChatNum);
					List<Map<String, Object>> chatList = msgrDAO.getAllChatList(pMap.getMap());

					for (Map<String, Object> map : chatList) {
						msgrServer.textArea_log.append("톡내용 불러오기");
						msgrServer.textArea_log.append("톡내용" + map.get("MEM_NICK_VC") + map.get("CHAT_VC"));
					}

					response = Protocol.ROOM_IN
												+ Protocol.SEPERATOR
												+ room_no
												+ Protocol.SEPERATOR
												+ room_name;
					send(response);
					send(chatList);
				}

					break;
				// ois 220 # 방번호
				// oos 220 & 참여톡방리스트
				case Protocol.ROOM_DELETE: {// 톡방 삭제
					// 방번호
					int room_no = Integer.parseInt(token.nextToken());

					// 현재 사용자가 들어가있는 방번호의 톡방 삭제
					pMap.getMap().put("room_no_nu", room_no);
					pMap.getMap().put("mem_id_vc", id);
					int cheakDao = msgrDAO.deleteTalkRoom(pMap.getMap());

					msgrServer.textArea_log.append("톡방삭제 성공 여부 " + cheakDao);

					/* 톡방을 삭제한 이후 리스트를 새로고침할 쿼리. 프로시저로 합쳤어도 좋았을 듯 */
					// 참여한 친구톡방 리스트 가져오기
					pMap.getMap().put("mem_id_vc", id);
					List<Map<String, Object>> joinBuddyRoomList = msgrDAO.getJoinBuddyTalkList(pMap.getMap());

					// 참여한 오픈톡방 리스트 가져오기
					pMap.getMap().put("mem_id_vc", id);
					List<Map<String, Object>>		joinOpenTalkList	= msgrDAO.getJoinOpenTalkList(pMap.getMap());

					// 참여한 톡방 리스트에 참여한 친구톡방 & 오픈톡방 리스트 넣어주기
					List<List<Map<String, Object>>>	joinRoomList		= new Vector<List<Map<String, Object>>>();
					joinRoomList.add(joinBuddyRoomList);
					joinRoomList.add(joinOpenTalkList);

					// 220 # 삭제성공여부
					String response = Protocol.ROOM_DELETE + Protocol.SEPERATOR + cheakDao;

					send(response);
					send(joinRoomList);
				}
					break;

				// 300 # 친구아이디
				case Protocol.BUDDY_ADD: {// 친구추가

					String	response	= null;
					String	buddyId		= token.nextToken();

					pMap.getMap().put("mem_id_vc", id);
					pMap.getMap().put("buddy_id_vc", buddyId);
					int result = msgrDAO.makeBuddy(pMap.getMap());

					response = Protocol.BUDDY_ADD + Protocol.SEPERATOR + result;

					// 300 # 성공여부
					send(response);

					if (result == -1) {

						pMap.getMap().put("mem_id_vc", id);
						List<Map<String, Object>> tempList = msgrDAO.getBuddyList(pMap.getMap());
						send(tempList);

						buddyCastingTest(tempList, response);
					}

					// 친구 추가 시 해당하는 친구가 있는지 확인하고 , 있을 경우, 해당 아이디가 있다는 것을 클라이언트 스레드에게 보내준다.
					// 클라이언트 스레드에서 해당하는 친구가 있음을 출력하는 optionPane을 출력해준다.

				}
					break;
				case Protocol.BUDDY_LIST: {// 친구목록 출력
					msgrServer.textArea_log.append(msg + "\n");// 클라이언트에서 받은 메시지 로그창에 출력
					msgrServer.textArea_log.setCaretPosition(msgrServer.textArea_log.getDocument().getLength());// 로그창 맨// 아래로// 스크롤

				}
					break;
				// 320 # 친구아이디 # 친구닉네임
				case Protocol.BUDDY_DELETE: {// 친구 삭제
					String	response	= null;
					String	buddyId		= token.nextToken();
					token.nextToken();

					pMap.getMap().put("mem_id_vc", id);
					pMap.getMap().put("buddy_id_vc", buddyId);
					int result = msgrDAO.deleteBuddy(pMap.getMap());
					msgrServer.textArea_log.append("친구 삭제 결과 : " + result + "\n");

					response = Protocol.BUDDY_DELETE + Protocol.SEPERATOR + result;
					send(response);

					if (result == -1) {
						pMap.getMap().put("mem_id_vc", id);
						List<Map<String, Object>> tempList = msgrDAO.getBuddyList(pMap.getMap());
						send(tempList);
					}
				}
					break;
				// 400 # 방번호 # id # nickname # 메시지
				case Protocol.SENDCHAT: {// 메시지 전송
					int		room_no		= Integer.parseInt(token.nextToken());
					String	id			= token.nextToken();
					String	nickname	= token.nextToken();
					String	chat		= token.nextToken();

					String	response	= Protocol.SENDCHAT + Protocol.SEPERATOR + room_no + Protocol.SEPERATOR + nickname
												+ Protocol.SEPERATOR + chat;

					pMap.getMap().put("room_no_nu", room_no);
					int chat_no = msgrDAO.getLastChatNum(pMap.getMap());

					msgrServer.textArea_log.append(chat_no + "\n");

					pMap.getMap().put("room_no_nu", room_no);
					pMap.getMap().put("chat_no_nu", ++chat_no);
					pMap.getMap().put("mem_id_vc", id);
					pMap.getMap().put("chat_vc", chat);
					int insertChatResult = msgrDAO.insertChat(pMap.getMap());
					msgrServer.textArea_log.append("클라이언트에서 전송한 채팅 저장 성공 여부 " + insertChatResult + "\n");

					pMap.getMap().put("room_no_nu", room_no);
					List<Map<String, Object>> tempList = msgrDAO.getTalkRoomUserList(pMap.getMap());

					// 방에 참여한 사람들에게만 메시지 전송
					broadCasting(tempList, response);
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

	private void broadCasting(Object response) {

		for (MessengerServerThread currentUser : msgrServer.globalList) {
			currentUser.send(response);
		}
	}

	// 접속한 유저들 중에 해당 채팅방에 입장 중이면 채팅 전송
	private void broadCasting(List<Map<String, Object>> tempList, String response) {

		for (MessengerServerThread currentUser : msgrServer.globalList) {
			// 테스트1, 2, 3

			for (Map<String, Object> userIndex : tempList) {

				if (currentUser.id.equals(userIndex.get("MEM_ID_VC"))) {
					currentUser.send(response);
					break;
				}
			}
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

	private void buddyCastingTest(List<Map<String, Object>> tempList, String response) {// 접속한 친구들한테만 브로드캐스팅
		// 접속한 사람들 중

		for (MessengerServerThread currentUser : msgrServer.globalList) {

			// 내 친구 리스트에서
			for (Map<String, Object> buddyMap : tempList) {

				// 현재 접속 중인 유저와 일치하는 아이디가 있으면 send
				if (buddyMap.get("BUDDY_ID_VC").equals(currentUser.id)) {
					pMap.getMap().put("mem_id_vc", buddyMap.get("BUDDY_ID_VC"));
					List<Map<String, Object>> b_buddyList = msgrDAO.getBuddyList(pMap.getMap());
					currentUser.send(response);
					currentUser.send(b_buddyList);
				}
			}
		}
	}// end of buddyCasting()
}
