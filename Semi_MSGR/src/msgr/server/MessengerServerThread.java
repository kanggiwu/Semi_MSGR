package msgr.server;

import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.List;
import java.util.Map;
import java.util.StringTokenizer;
import java.util.Vector;

import msgr.map.MessengerMap;
import msgr.util.MessengerDAO;

public class MessengerServerThread extends Thread {

	MessagerServer				msgrServer		= null;
	Socket						client			= null;
	ObjectOutputStream			oos				= null;
	ObjectInputStream			ois				= null;
	String						id				= "";
	String						nickname		= "";
	MessengerDAO				msgrDAO			= null;
	MessengerMap				pMap			= null;
	List<MessengerServerThread>	buddyList		= null;
	List<MessengerTalkRoom>		talkRoomList	= null;

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
		buddyList = new Vector<>();
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
				// 100 # id
				case Protocol.SIGNIN: {// 로그인

					MessengerTalkRoom msgrTalkRoom = null;

					id = token.nextToken();
					msgrServer.textArea_log.append(msg + "님이 로그인\n");// 클라이언트에서 받은 메시지 로그창에 출력

					// 친구목록 & 톡방 목록 출력

					pMap.getMap().put("mem_id_vc", id);// 클라이언트가 받아온 id를 map 넣는다.
					List<Map<String, Object>> roomList = msgrDAO.getTalkRoomList(pMap.getMap());// id를 파라미터로 넘겨준 뒤 마이바티스를 통해 해당하는 id가 참여한
																								// 톡방리스트를 받아온다
					// 톡방이름, 톡방번호, 톡방 종류

					System.out.println("for루프 전");

					for (Map<String, Object> map : roomList) {
						System.out.println(map);
					}

					for (Map<String, Object> map : roomList) {// 받아온 톡방리스트 별로 톡방 객체를 생성한 뒤 톡방List에 넣어준다.
						msgrTalkRoom = new MessengerTalkRoom();
						System.out.println("톡룸 인스턴스화");
						String room_name = (String) map.get("ROOM_NAME_VC");
						System.out.println("변수하나넣고");
						// 해당 부분에서 멈춰버려서 오브젝트를 스트링으로 바꾸고 파스인트를 하니까 됨
						// int room_no = (int) map.get("ROOM_NO_NU"));
						int room_no = Integer.parseInt(map.get("ROOM_NO_NU").toString());
						System.out.println("변수둘넣고");
						int is_private = Integer.parseInt(map.get("IS_PRIVATE_YN").toString());
						System.out.println("변수셋넣고");

						msgrTalkRoom.setMsgrTalkRoom(room_name, room_no, is_private);
						talkRoomList.add(msgrTalkRoom);// 톡방리스트에 만들어준 톡방을 넣어준다.
					}
					System.out.println("for루프 빠져나옴");

					// 클라이언트스레드에게 프로토콜, 방 개수, 톡방이름, 톡방 번호, 톡방 종류에 대해 송신한다.
					// 100 # 톡방개수 # 톡방이름 # 톡방 번호 # 톡방종류 #이름& 번호& 종류 톡방개수만큼 반복
					String response = Protocol.SIGNIN + Protocol.SEPERATOR + talkRoomList.size();

					System.out.println("2번째 for루프 전");

					for (MessengerTalkRoom index : talkRoomList) {
						response += Protocol.SEPERATOR;
						response += index.getTalkTitle();
						response += Protocol.SEPERATOR;
						response += index.getTalk_no();
						response += Protocol.SEPERATOR;
						response += index.getIs_private();
					}

					send(response);

					pMap.getMap().put("mem_id_vc", id);
					List<Map<String, Object>> buddyList = msgrDAO.getBuddyList(pMap.getMap());
					
					oos.writeObject(buddyList);
					
					System.out.println(response);
				}
					break;

				// 120 # id
				case Protocol.SIGNOUT: {// 로그아웃
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
				case Protocol.CHANGE_NICKNAME: {// 닉네임변경
//					msgrServer.textArea_log.append(msg + "\n");// 클라이언트에서 받은 메시지 로그창에 출력
//					msgrServer.textArea_log.setCaretPosition(msgrServer.textArea_log.getDocument().getLength());// 로그창 맨
//																												// 아래로
//																												// 스크롤
//
//					token.nextToken(); // id는 버린다. 로그용
//					String	aftername	= token.nextToken();
//					String	response	= "";
//
//					pMap.getMap().put("mem_id_vc", id);
//					pMap.getMap().put("mem_nick_vc", nickname);
//
//					// 리턴 타입 인티저로 바뀔 예정 1이면 업데이트 성공, else 실패
//					int result = msgrDAO.changeNickname(pMap.getMap());
//
//					if (result == 1) {
//						nickname = aftername;
//					}
//					response = Protocol.CHANGE_NICKNAME + Protocol.SEPERATOR + id + Protocol.SEPERATOR + nickname;
//					// 접속해 있는 친구들에게 브로드 캐스팅
//					buddyCasting(response);
//					/* 사용자 본인에게 닉네임 변경 결과 전송
//					 * 바뀌었을 경우 바뀐 닉네임 else 원래 닉네임
//					 * 을 정의서에 추가해주기~
//					 * 
//					 */
//					send(response);
				}
					break;

				// 140#
				case Protocol.MEM_DELETE: {// 회원탈퇴
					msgrServer.textArea_log.append(msg + "\n");// 클라이언트에서 받은 메시지 로그창에 출력
					msgrServer.textArea_log.setCaretPosition(msgrServer.textArea_log.getDocument().getLength());// 로그창 맨
																												// 아래로
																												// 스크롤
					msgrServer.globalList.remove(this);

					String response = Integer.toString(Protocol.MEM_DELETE) + id;
					pMap.getMap().put("mem_id_vc", id);
					msgrDAO.deleteMember(pMap.getMap());
					buddyCasting(response);

				}
					break;
				case Protocol.ROOM_CREATE_BUDDY: {// 친구톡 생성
					msgrServer.textArea_log.append(msg + "\n");// 클라이언트에서 받은 메시지 로그창에 출력
					msgrServer.textArea_log.setCaretPosition(msgrServer.textArea_log.getDocument().getLength());// 로그창 맨
																												// 아래로
																												// 스크롤
				}
					break;
				case Protocol.ROOM_CREATE_OPENTALK: {// 오픈톡 생성
					msgrServer.textArea_log.append(msg + "\n");// 클라이언트에서 받은 메시지 로그창에 출력
					msgrServer.textArea_log.setCaretPosition(msgrServer.textArea_log.getDocument().getLength());// 로그창 맨
																												// 아래로
																												// 스크롤
				}
					break;
				case Protocol.ROOM_LIST: {// 톡방 리스트 출력
					msgrServer.textArea_log.append(msg + "\n");// 클라이언트에서 받은 메시지 로그창에 출력
					msgrServer.textArea_log.setCaretPosition(msgrServer.textArea_log.getDocument().getLength());// 로그창 맨
																												// 아래로
																												// 스크롤
					List<String> talkRoomList = new Vector<>();
					// talkRoomList.msgrDAO.getTalkRoomList();
				}
					break;
				case Protocol.ROOM_IN: {// 톡방 참가
					msgrServer.textArea_log.append(msg + "\n");// 클라이언트에서 받은 메시지 로그창에 출력
					msgrServer.textArea_log.setCaretPosition(msgrServer.textArea_log.getDocument().getLength());// 로그창 맨
																												// 아래로
																												// 스크롤

				}
					break;
				case Protocol.ROOM_IN_MEM: {// 톡방 참가 인원
					msgrServer.textArea_log.append(msg + "\n");// 클라이언트에서 받은 메시지 로그창에 출력
					msgrServer.textArea_log.setCaretPosition(msgrServer.textArea_log.getDocument().getLength());// 로그창 맨
																												// 아래로
																												// 스크롤

				}
					break;
				case Protocol.ROOM_OUT: {// 톡방 닫기
					msgrServer.textArea_log.append(msg + "\n");// 클라이언트에서 받은 메시지 로그창에 출력
					msgrServer.textArea_log.setCaretPosition(msgrServer.textArea_log.getDocument().getLength());// 로그창 맨
																												// 아래로
																												// 스크롤

				}
					break;
				case Protocol.ROOM_DELETE: {// 톡방 나가기
					msgrServer.textArea_log.append(msg + "\n");// 클라이언트에서 받은 메시지 로그창에 출력
					msgrServer.textArea_log.setCaretPosition(msgrServer.textArea_log.getDocument().getLength());// 로그창 맨// 아래로// 스크롤

					
					
					
					
					
				}
					break;

				// 테스트용 300 # id
				case Protocol.BUDDY_ADD: {// 친구추가
					msgrServer.textArea_log.append(msg + "\n");// 클라이언트에서 받은 메시지 로그창에 출력
					msgrServer.textArea_log.setCaretPosition(msgrServer.textArea_log.getDocument().getLength());// 로그창 맨
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
				case Protocol.SENDCHAT: {// 메시지 전송
					msgrServer.textArea_log.append(msg + "\n");// 클라이언트에서 받은 메시지 로그창에 출력
					msgrServer.textArea_log.setCaretPosition(msgrServer.textArea_log.getDocument().getLength());// 로그창 맨
																												// 아래로
																												// 스크롤
					// 프로토롤#닉네임#메세지내용
					String	nickname		= token.nextToken();
					String	talkRoomFlag	= token.nextToken();
					String	chat			= token.nextToken();
					// 방에 참여한 사람들에게만 메시지 전송
					// broadCating
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

	private void send(String response) {

		try {
			oos.writeObject(response);
		}
		catch (Exception e) {
			e.printStackTrace();
		}
	}

	private void broadCasting(String response) {// 접속한 모든 사람들에게 브로드캐스팅
		// 오픈톡방 리스트 출력

		for (MessengerServerThread msgrServerThread : msgrServer.globalList) {
			msgrServerThread.send(response);
		}
	}

	private void removeBuddy(String response) {// 접속한 친구들한테만 브로드캐스팅

		for (int i = 0; i < msgrServer.globalList.size(); i++) {

			for (int j = 0; j < msgrServer.globalList.get(i).buddyList.size(); j++) {

				if (msgrServer.globalList.get(i).buddyList.get(j).id == this.id) {
					msgrServer.globalList.get(i).buddyList.remove(this);
				}
			}
		}

		for (MessengerServerThread globalThread : msgrServer.globalList) {

			for (MessengerServerThread bubbyThread : buddyList) {

				if (globalThread.id == bubbyThread.id) {
					buddyList.remove(this);
				}
			}
		}
	}// end of buddyCasting()

	private void buddyCasting(String response) {// 접속한 친구들한테만 브로드캐스팅
		// 친구리스트 출력

		// 접속 중인 사용자 수만큼 루프
		for (MessengerServerThread globalThread : msgrServer.globalList) {

			// 현재 스레드의 친구목록 수만큼 루프
			for (MessengerServerThread bubbyThread : buddyList) {

				// 접속 중인 사용자 아이디 == 친구 아이디 ? 접속중인 친구에게 전송
				if (globalThread.id == bubbyThread.id) {
					bubbyThread.send(response);
				}
			}
		}
	}// end of buddyCasting()
}
