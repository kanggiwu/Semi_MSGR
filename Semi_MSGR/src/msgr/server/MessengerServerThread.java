package msgr.server;

import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.StringTokenizer;
import java.util.Vector;

import msgr.util.MessengerDAO;

public class MessengerServerThread extends Thread {

	MessagerServer msgrServer = null;
	Socket client = null;
	ObjectOutputStream oos = null;
	ObjectInputStream ois = null;
	String id = null;
	String nickname = null;
	MessengerDAO msgrDAO = null;

	public MessengerServerThread(MessagerServer msgrServer) {
		this.msgrServer = msgrServer;
	}

	public void run() {
		String	msg		= null;
		boolean	isStop	= false;

		try {

			msgrDAO = MessengerDAO.getInstance();
			run_start:
			while(!isStop) {
				msg = (String) ois.readObject();//클라이언트에서 보낸 메시지 받기				
				msgrServer.textArea_log.append(msg + "\n");//클라이언트에서 받은 메시지 로그창에 출력
				msgrServer.textArea_log.setCaretPosition(msgrServer.textArea_log.getDocument().getLength());//로그창 맨 아래로 스크롤
				StringTokenizer token = null;//토큰 선언
				int protocol = -1; //프로토콜 선언
				if(msg != null) {

					token = new StringTokenizer(msg, Protocol.SEPERATOR);
					protocol = Integer.parseInt(token.nextToken()); // 프로토콜 초기화
				}

				//클라이언트에서 로그인 시, 친구리스트 출력 & 톡방리스트 출력 요청
				switch (protocol) {
					case Protocol.LOGIN:{//로그인
						 //프로토콜#id#pw
						String id = token.nextToken();
						String pw = token.nextToken();
						Map<String, Object> pMap = new HashMap<>();
						
						
						//msgrDAO.signIn(id,pw);
						
						
					}break;
					case Protocol.LOGOUT:{//로그아웃
						msgrServer.globalList.remove(this);// 접속자 목록에서 삭제 
						//현재 접속자에 대한 정보를 클라이언트가 알 필요는 없으니까, 메세지를 보낼 필요가 없다.?
					}break;
					case Protocol.CHANGE_NICKNAME:{//닉네임변경
						//프로토콜# 기존 아이디 # 이후 아이디
						String nickname = token.nextToken();
						String aftername = token.nextToken();
						this.nickname = aftername;
						//접속해 있고, 친구목록에 있는 모든 클라이언트에게 브로드 캐스팅
						
						
						
					}break;
					case Protocol.MEM_DELETE:{//회원탈퇴
						
					}break;
					case Protocol.ROOM_CREATE_BUDDY:{//친구톡 생성
						
					}break;
					case Protocol.ROOM_CREATE_OPENTALK:{//오픈톡 생성
						
					}break;
					case Protocol.ROOM_LIST:{//톡방 리스트 출력
						List<String> talkRoomList = new Vector<>();
						//talkRoomList.msgrDAO.getTalkRoomList();
					}break;
					case Protocol.ROOM_IN:{//톡방 참가
						
					}break;
					case Protocol.ROOM_IN_MEM:{//톡방 참가 인원
						
					}break;
					case Protocol.ROOM_OUT:{//톡방 닫기
						
					}break;
					case Protocol.ROOM_DELETE:{//톡방 나가기
						
					}break;
					case Protocol.BUDDY_ADD:{//친구추가
						
					}break;
					case Protocol.BUDDY_LIST:{//친구목록 출력
						List<String> bubbyList = new Vector<>();
						//bubbyList.getBuddyList();
					}break;
					case Protocol.BUDDY_DELETE:{//친구 삭제
						
					}break;
					case Protocol.MESSAGE:{//메시지 전송
						//프로토롤#닉네임#메세지내용
						String nickname = token.nextToken();
						String talkRoomFlag = token.nextToken();
						String chat = token.nextToken();
						//방에 참여한 사람들에게만 메시지 전송
						//broadCating
					}break;
					case Protocol.EMOTICON:{//이모티콘 전송
						
					}break;
					case Protocol.ATTACHMENT:{//파일 전송
						
					}
					break run_start;
				}//========================== end of switch
			}//====================== end of while
		} catch (Exception e) {
			e.getStackTrace();
		}

	}//======================== end of run
}
