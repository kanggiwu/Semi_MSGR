package msgr.server;

import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.StringTokenizer;

public class MessengerServerThread extends Thread {
	MessagerServer msgrServer = null;
	Socket client = null;
	ObjectOutputStream oos = null;
	ObjectInputStream ois = null;
	public MessengerServerThread(MessagerServer msgrServer) {
		this.msgrServer = msgrServer;
	}

	public void run() {
		String msg = null;
		boolean isStop = false;
		try {
			run_start: while(!isStop) {
				msg = (String) ois.readObject();//클라이언트에서 보낸 메시지 받기				
				msgrServer.textArea_log.append(msg + "\n");//클라이언트에서 받은 메시지 로그창에 출력
				msgrServer.textArea_log.setCaretPosition(msgrServer.textArea_log.getDocument().getLength());//로그창 맨 아래로 스크롤
				StringTokenizer token = null;//토큰 선언
				int protocol = -1; //프로토콜 선언
				if(msg != null) {
					token = new StringTokenizer(msg, Protocol.SEPERATOR);
					protocol = Integer.parseInt(token.nextToken()); // 프로토콜 초기화
				}
				switch (protocol) {
					case Protocol.LOGIN:{//로그인
					
					}break;
					case Protocol.LOGOUT:{//로그아웃
					
					}break;
					case Protocol.CHANGE_NICKNAME:{//닉네임변경
						
					}break;
					case Protocol.MEM_DELETE:{//회원탈퇴
						
					}break;
					case Protocol.ROOM_CREATE_BUDDY:{//친구톡 생성
						
					}break;
					case Protocol.ROOM_CREATE_OPENTALK:{//오픈톡 생성
						
					}break;
					case Protocol.ROOM_LIST:{//톡방 리스트 출력
						
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
						
					}break;
					case Protocol.BUDDY_DELETE:{//친구 삭제
						
					}break;
					case Protocol.MESSAGE:{//메시지 전송
						
					}break;
					case Protocol.EMOTICON:{//이모티콘 전송
						
					}break;
					case Protocol.ATTACHMENT:{//파일 전송
						
					}break;
				}
			}
		} catch (Exception e) {
			// TODO: handle exception
		}
	}
	
	
}
