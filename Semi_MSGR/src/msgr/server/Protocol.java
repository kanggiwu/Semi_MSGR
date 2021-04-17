package msgr.server;

public class Protocol {
	static final int LOGIN =100;//로그인
	static final int LOGOUT=120;//로그아웃
	static final int CHANGE_NICKNAME=130;//닉네임변경
	static final int MEM_DELETE=140;//회원탈퇴
	static final int ROOM_CREATE_BUDDY = 200;//친구톡 생성
	static final int ROOM_CREATE_OPENTALK =201 ;//오픈톡 생성
	static final int ROOM_LIST = 210;//톡방 리스트
	static final int ROOM_IN = 211;//톡방 참가
	static final int ROOM_IN_MEM =212 ;//톡방 참가 인원
	static final int ROOM_OUT = 213;//톡방 닫기
	static final int ROOM_DELETE=220 ;//톡방 나가기
	static final int BUDDY_ADD =300;//친구추가
	static final int BUDDY_LIST =310;//친구목록
	static final int BUDDY_DELETE =320;//친구삭제
	static final int MESSAGE =400 ;//메시지 전송
	static final int EMOTICON = 401;//이모티콘 전송
	static final int ATTACHMENT =403 ;//파일전송
	static final int FLAG_OPEN_ROOM =0;//오픈톡방 구분
	static final int FLAG_BUDDY_ROOM=1;//친구톡방 구분
	static final String SEPERATOR = "#";

}
