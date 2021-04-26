package msgr.server;

public class Protocol {

	public static final int		SIGNIN					= 100;	// 로그인 (1)(해써)
	public static final int		SIGNOUT					= 120;	// 로그아웃 (1)(해써)
	public static final int		CHANGE_NICKNAME			= 130;	// 닉네임변경 (1)(해써)
	public static final int		MEM_DELETE				= 140;	// 회원탈퇴 (1)(해써)
	public static final int		ROOM_CREATE_BUDDY		= 200;	// 친구톡 생성
	public static final int		ROOM_CREATE_OPENTALK	= 201;	// 오픈톡 생성
	public static final int		ROOM_LIST				= 210;	// 톡방 리스트
	public static final int		JOIN_OPENROOM			= 211;	// 오픈톡방 참가
	public static final int		ROOM_IN					= 212;	// 톡방 입장
	public static final int		ROOM_DELETE				= 220;	// 톡방 삭제
	public static final int		BUDDY_ADD				= 300;	// 친구추가
	public static final int		BUDDY_LIST				= 310;	// 친구목록
	public static final int		BUDDY_LIST_UPDATE		= 311;	// 친구목록 갱신 (서버스레드->클라이언트스레드: 회원탈퇴 시 해당하는 아이디 삭제를 요청)
	public static final int		BUDDY_DELETE			= 320;	// 친구삭제
	public static final int		SENDCHAT				= 400;	// 메시지 전송
	public static final int		EMOTICON				= 401;	// 이모티콘 전송
	public static final int		ATTACHMENT				= 403;	// 파일전송
	public static final int		STORECHAT				= 410;	// 대화내용저장
	public static final int		FLAG_OPEN_ROOM			= 0;	// 오픈톡방 구분
	public static final int		FLAG_BUDDY_ROOM			= 1;	// 친구톡방 구분
	public static final String	SEPERATOR				= "#";

}
