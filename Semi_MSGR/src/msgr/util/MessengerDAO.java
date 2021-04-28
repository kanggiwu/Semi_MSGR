package msgr.util;

import java.util.List;
import java.util.Map;

import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;

import msgr.map.MessengerMap;

public class MessengerDAO {

	private static MessengerDAO msgrDAO = null;

	private MessengerDAO() {

	}

	public static MessengerDAO getInstance() {

		if (msgrDAO == null) {
			msgrDAO = new MessengerDAO();
		}
		return msgrDAO;
	}

	SqlSessionFactory factory = null;

	/**
	 * alt + shift + j
	 * 
	 * 로그인 메서드 /확인
	 * 
	 * SELECT mem_id_vc, mem_nick_vc FROM MSGR_MEMBER WHERE mem_id_vc = #{mem_id_vc}
	 * AND mem_pw_vc = #{mem_pw_vc}
	 * 
	 * @param pMap - 사용자가 입력한 아이디, 비밀번호를 저장해둔 Map
	 * @return tempList - 아이디, 비밀번호 일치 시 아이디, 닉네임을 리스트로 리턴
	 */
	public List<Map<String, Object>> signIn(Map<String, Object> pMap) {
		factory = MyBatisCommonFactory.getInstance();
		SqlSession					sqlSession	= factory.openSession();
		List<Map<String, Object>>	tempList	= sqlSession.selectList("MsgrMapper.signIn", pMap);

		sqlSession.close();

		return tempList;
	}

	/**
	 * 회원가입의 아이디 중복확인 메서드 /확인
	 * 
	 * SELECT mem_id_vc FROM msgr_member WHERE mem_id_vc = #{mem_id_vc}
	 * 
	 * @param pMap - 사용자가 입력한 아이디를 저장해둔 Map
	 * @return tempList - 사용자가 입력한 아이디와 일치하는 아이디를 리스트로 리턴
	 */
	public List<Map<String, Object>> idCheck(Map<String, Object> pMap) {
		factory = MyBatisCommonFactory.getInstance();
		SqlSession					sqlSession	= factory.openSession();
		List<Map<String, Object>>	tempList	= sqlSession.selectList("MsgrMapper.idCheck", pMap);

		sqlSession.close();
		return tempList;
	}

	/**
	 * 회원가입 메서드 /확인
	 * 
	 * INSERT INTO msgr_member (mem_id_vc, mem_pw_vc, mem_nick_vc) VALUES
	 * (#{mem_id_vc}, #{mem_pw_vc}, #{mem_nick_vc})
	 * 
	 * @param pMap - 사용자가 입력한 아이디, 비밀번호, 닉네임을 저장해둔 Map
	 * @return accept - (1) : 회원가입 성공 (-1) : 회원가입 실패
	 */
	public int signUp(Map<String, Object> pMap) {
		factory = MyBatisCommonFactory.getInstance();
		SqlSession	sqlSession	= factory.openSession();
		int			idCheck		= 0;

		try {
			idCheck = sqlSession.insert("MsgrMapper.signUp", pMap);

		}
		catch (Exception e) {
			System.out.println(e.getMessage());
			idCheck = -1;
		}
		sqlSession.commit();

		sqlSession.close();
		return idCheck;
	}

	/**
	 * 톡방리스트 불러오기 메서드 /확인
	 * 
	 * SELECT room.room_name_vc, room.room_no_nu FROM MSGR_ROOM
	 * room,MSGR_ROOM_IN_LIST rlist WHERE room.room_no_nu = rlist.room_no_nu AND
	 * rlist.mem_id_vc = #{mem_id_vc} ORDER BY room.room_no_nu ASC
	 * 
	 * @param pMap - 사용자가 입력한 아이디를 저장해둔 Map
	 * @return tempList - 사용자가 입력한 아이디와 일치하는 톡방 이름, 톡방 번호를 리스트로 리턴
	 */
	public List<Map<String, Object>> getTalkRoomList(Map<String, Object> pMap) {
		factory = MyBatisCommonFactory.getInstance();
		SqlSession					sqlSession	= factory.openSession();
		List<Map<String, Object>>	tempList	= sqlSession.selectList("MsgrMapper.getTalkRoomList", pMap);

		sqlSession.close();

		return tempList;
	}

	/**
	 * 친구리스트 불러오기 메서드 /확인
	 * 
	 * SELECT buddy_id_vc FROM MSGR_BUDDY WHERE mem_id_vc = #{mem_id_vd} ORDER BY
	 * mem_id_vc
	 * 
	 * @param pMap - 사용자가 입력한 아이디를 저장해둔 Map
	 * @return tempList - 사용자가 입력한 아이디의 친구들을 리스트로 리턴
	 */
	public List<Map<String, Object>> getBuddyList(Map<String, Object> pMap) {
		factory = MyBatisCommonFactory.getInstance();
		SqlSession					sqlSession	= factory.openSession();
		List<Map<String, Object>>	tempList	= sqlSession.selectList("MsgrMapper.getBuddyList", pMap);

		sqlSession.close();

		return tempList;
	}

	/**
	 * 마지막 톡방 번호 불러오기 메서드 /확인
	 * 
	 * SELECT room_no_nu FROM MSGR_ROOM WHERE ROWNUM = 1 ORDER BY room_no_nu DESC
	 * 
	 * @return - 마지막 톡방 번호를 리턴
	 */
	public int getLastRoomNum() {
		factory = MyBatisCommonFactory.getInstance();
		SqlSession	sqlSession	= factory.openSession();
		int			tempInt		= 0;

		try {
			tempInt = sqlSession.selectOne("MsgrMapper.getLastRoomNum");
		}
		catch (Exception e) {
			tempInt = 0;
		}

		sqlSession.close();

		return tempInt;
	}

	/**
	 * 오픈톡방 추가하기 메서드 /확인
	 * 
	 * INSERT INTO MSGR_ROOM(room_no_nu, room_name_vc, is_private_yn) VALUES
	 * (#{room_no_nu), #{room_name_vc}, 0)
	 * 
	 * @param pMap - 사용자가 입력한 톡방 번호, 톡방 이름을 저장한 Map
	 * @return accept - (1) : 오픈톡방 추가 성공 (-1) : 오픈톡방 추가 실패
	 */
	public int createOpenTalkRoom(Map<String, Object> pMap) {
		factory = MyBatisCommonFactory.getInstance();
		SqlSession	sqlSession			= factory.openSession();
		int			createOpenTalkCheck	= 0;

		try {
			createOpenTalkCheck = sqlSession.insert("MsgrMapper.createOpenTalkRoom", pMap);
		}
		catch (Exception e) {
			System.out.println(e.getMessage()); // console 창에 에러이벤트에 대한 메시지를 가져와라.
			createOpenTalkCheck = -1;
		}

		sqlSession.commit();

		sqlSession.close();
		return createOpenTalkCheck;
	}

	/**
	 * 친구톡방 추가하기 메서드 /확인
	 * 
	 * INSERT INTO MSGR_ROOM(room_no_nu, room_name_vc, is_private_yn) VALUES
	 * (#{room_no_nu), #{room_name_vc}, 1)
	 * 
	 * @param pMap - 사용자가 입력한 톡방번호, 톡방이름을 저장한 Map
	 * @return accept - (1) : 친구톡방 추가 성공 (-1) : 친구톡방 추가 실패
	 */
	public int createBuddyTalkRoom(Map<String, Object> pMap) {
		factory = MyBatisCommonFactory.getInstance();
		SqlSession	sqlSession				= factory.openSession();
		int			createBuddyTalkCheck	= 0;

		try {
			createBuddyTalkCheck = sqlSession.insert("MsgrMapper.createBuddyTalkRoom", pMap);
		}
		catch (Exception e) {
			System.out.println(e.getMessage());
			createBuddyTalkCheck = -1;
		}
		sqlSession.commit();

		sqlSession.close();

		return createBuddyTalkCheck;
	}

	/**
	 * 톡방 삭제 메서드 /확인
	 * 
	 * {CALL DECLARE BEGIN UPDATE MSGR_CHAT SET mem_id_vc = null WHERE mem_id_vc =
	 * #{mem_id_vc}; DELETE FROM MSGR_ROOM_IN_LIST WHERE room_no_nu = #{room_no_nu}
	 * and mem_id_vc = #{mem_id_vc}; END }
	 * 
	 * @param pMap - 사용자가 입력한 톡방 번호를 저장한 Map
	 * @return accept - (1) : 톡방 삭제 성공 (-1) : 톡방 삭제 실패
	 */
	public int deleteTalkRoom(Map<String, Object> pMap) {
		factory = MyBatisCommonFactory.getInstance();
		SqlSession	sqlSession			= factory.openSession();
		int			deleteTalkRoomCheck	= 0;

		try {
			deleteTalkRoomCheck = sqlSession.delete("MsgrMapper.deleteTalkRoom", pMap);
		}
		catch (Exception e) {
			System.out.println(e.getMessage());
			deleteTalkRoomCheck = -1;
		}
		sqlSession.commit();
		sqlSession.close();

		return deleteTalkRoomCheck;
	}

	/**
	 * 톡방 참여 메서드 /확인
	 * 
	 * INSERT INTO MSGR_ROOM_IN_LIST(room_no_nu, mem_id_vc, join_chat_no_nu)
	 * VALUES(#{room_no_nu}, #{mem_id_vc}, #{join_chat_no_nu})
	 * 
	 * @param - 사용자가 입력한 방번호, 아이디, 채팅 참여 번호를 저장한 Map
	 * @return accept - (1) : 톡방 참여 성공 (-1) : 톡방 참여 실패
	 */
	public int joinChatMember(Map<String, Object> pMap) {
		factory = MyBatisCommonFactory.getInstance();

		SqlSession	sqlSession			= factory.openSession();
		int			joinChatMemberCheck	= 0;

		try {
			joinChatMemberCheck = sqlSession.insert("MsgrMapper.joinChatMember", pMap);
		}
		catch (Exception e) {
			System.out.println(e.getMessage());
			joinChatMemberCheck = -1;
		}

		sqlSession.commit();
		sqlSession.close();

		return joinChatMemberCheck;

	}

	/**
	 * 대화내용 전송 메서드 /확인
	 * 
	 * INSERT INTO MSGR_CHAT(room_no_nu, chat_no_nu, mem_id_vc, chat_vc)
	 * VALUES(#{room_no_nu), #{chat_no_nu}, #{mem_id_vc}, #{chat_vc})
	 * 
	 * @param pMap - 사용자가 입력한 톡방 번호, 채팅 번호, 아이디, 대화내용을 저장한 Map
	 * @return accept - (1) : 대화내용 전송 성공 (-1) : 대화내용 전송 실패
	 */
	public int insertChat(Map<String, Object> pMap) {
		factory = MyBatisCommonFactory.getInstance();
		SqlSession	sqlSession		= factory.openSession();
		int			insertChatCheck	= 0;

		try {
			insertChatCheck = sqlSession.insert("MsgrMapper.insertChat", pMap);
		}
		catch (Exception e) {
			System.out.println(e.getMessage());
			insertChatCheck = -1;
		}

		sqlSession.commit();
		sqlSession.close();

		return insertChatCheck;
	}

	/**
	 * 마지막 대화 번호 가지고 오기 메서드 /확인
	 *
	 * SELECT chat_no_nu FROM(SELECT chat_no_nu FROM msgr_chat WHERE room_no_nu =
	 * #{room_no_nu} ORDER BY chat_no_nu DESC) WHERE ROWNUM = 1
	 * 
	 * @return tempList - 마지막 대화 번호를 리턴
	 */
	public int getLastChatNum(Map<String, Object> pMap) {
		factory = MyBatisCommonFactory.getInstance();
		SqlSession	sqlSession	= factory.openSession();

		int			tempInt		= -2;

		try {
			tempInt = sqlSession.selectOne("MsgrMapper.getLastChatNum", pMap);
		}
		catch (Exception e) {
			tempInt = 0;
		}

		sqlSession.close();

		return tempInt;
	}

	/**
	 * 톡방 참가한 이후의 대화내용 가져오기 메서드 /확인
	 * 
	 * SELECT mem.mem_nick_vc, chat.chat_vc FROM MSGR_MEMBER mem, MSGR_CHAT chat,
	 * MSGR_ROOM_IN_LIST rlist WHERE rlist.room_no_nu = chat.room_no_nu AND
	 * chat.chat_no_nu >= (SELECT join_chat_no_nu FROM MSGR_ROOM_IN_LIST WHERE
	 * mem_id_vc = #{mem_id_vc} AND room_no_nu = #{room_no_nu}) AND rlist.mem_id_vc
	 * = #{mem_id_vc} AND chat.room_no_nu = #{room_no_nu} AND mem.mem_id_vc =
	 * chat.mem_id_vc(+)
	 * 
	 * @param pMap- 사용자가 입력한 아이디, 톡방 번호가 저장된 Map
	 * @return tempList - 사용자가 입력한 아이디, 톡방 번호를 바탕으로 사용자가 톡방 참가한 이후의 닉네임과 대화 내용을 리스트로
	 *         리턴
	 */
	public List<Map<String, Object>> getChatAfterJoin(Map<String, Object> pMap) {
		factory = MyBatisCommonFactory.getInstance();
		SqlSession					sqlSession	= factory.openSession();
		List<Map<String, Object>>	tempList	= sqlSession.selectList("MsgrMapper.getChatAfterJoin", pMap);

		sqlSession.close();

		return tempList;
	}

	/**
	 * 회원탈퇴 메서드 /확인
	 * 
	 * { CALL BEGIN UPDATE MSGR_CHAT SET mem_id_vc = null WHERE mem_id_vc =
	 * #{mem_id_vc}; DELETE FROM MSGR_ROOM_IN_LIST WHERE mem_id_vc = #{mem_id_vc};
	 * DELETE FROM MSGR_BUDDY WHERE mem_id_vc = #{mem_id_vc}; DELETE FROM
	 * MSGR__MEMBER WEHRE mem_id_vc = #{mem_id_vc}; END }
	 * 
	 * @param pMap - 사용자가 입력한 아이디를 저장한 Map
	 * @return accept - (-1) : 회원 탈퇴 성공 (1) : 회원 탈퇴 실패
	 * 
	 */
	public int deleteMember(Map<String, Object> pMap) {
		factory = MyBatisCommonFactory.getInstance();
		SqlSession	sqlSession			= factory.openSession();
		int			deleteMemberCheck	= 0;

		try {
			deleteMemberCheck = sqlSession.delete("MsgrMapper.deleteMember", pMap);
		}
		catch (Exception e) {
			System.out.println(e.getMessage());
			System.out.println("실패");
			deleteMemberCheck = 0;

		}
		sqlSession.commit();
		sqlSession.close();

		return deleteMemberCheck;
	}

	/**
	 * 친구 삭제 메서드 /확인
	 * 
	 * { CALL BEGIN DELETE FROM MSGR_BUDDY WHERE buddy_id_vc = #{buddy_id_vc} AND
	 * mem_id_vc = #{mem_id_vc}; DELETE FROM MSGR_BUDDY WHERE buddy_id_vc =
	 * #{buddy_id_vc} AND mem_id_vc = #{mem_id_vc}; END }
	 * 
	 * @param pMap - 사용자가 입력한 아이디와 친구 아이디를 저장한 Map
	 * @return accept - (-1) : 친구 삭제 성공 (1) : 친구 삭제 실패
	 */
	public int deleteBuddy(Map<String, Object> pMap) {
		factory = MyBatisCommonFactory.getInstance();
		SqlSession	sqlSession			= factory.openSession();
		// List<Map<String, Object>> tempList =
		// sqlSession.selectList("MsgrMapper.deleteBuddy", pMap);
		int			deleteBuddyCheck	= 0;

		try {
			deleteBuddyCheck = sqlSession.delete("MsgrMapper.deleteBuddy", pMap);
		}
		catch (Exception e) {
			System.out.println(e.getMessage());
			deleteBuddyCheck = -1;
		}
		sqlSession.commit();
		sqlSession.close();

		return deleteBuddyCheck;
	}

	/**
	 * 닉네임 변경 메서드 /확인
	 * 
	 * UPDATE MSGR_MEMBER SET mem_nick_vc = #{mem_nick_vc} WHERE mem_id_vc =
	 * #{mem_id_vc}
	 * 
	 * @param pMap - 사용자가 입력한 아이디와 닉네임을 저장한 Map
	 * @return accept - (1) : 닉네임 변경 성공 (-1) : 닉네임 변경 실패
	 */
	public int changeNickname(Map<String, Object> pMap) {
		factory = MyBatisCommonFactory.getInstance();
		SqlSession	sqlSession			= factory.openSession();
		int			changeNickNameCheck	= 0;

		try {
			changeNickNameCheck = sqlSession.update("MsgrMapper.changeNickname", pMap);
		}
		catch (Exception e) {
			System.out.println(e.getMessage());
			changeNickNameCheck = -1;
		}
		sqlSession.commit();
		sqlSession.close();

		return changeNickNameCheck;
	}

	/**
	 * 친구 추가 메서드 /확인
	 * 
	 * {CALL DECLARE BEGIN INSERT INTO MSGR_BUDDY(mem_id_vc, buddy_id_vc) VALUES
	 * (#{mem_id_vc}, #{buddy_id_vc}); INSERT INTO MSGR_BUDDY(mem_id_vc,
	 * buddy_id_vc) VALUES (#{buddy_id_vc}, #{mem_id_vc}); END }
	 * 
	 * @param pMap - 사용자가 입력한 아이디와 친구 아이디를 저장한 Map
	 * @return accept - (-1) : 친구 추가 전송 성공 (0) : 친구 추가 실패
	 */
	public int makeBuddy(Map<String, Object> pMap) {
		factory = MyBatisCommonFactory.getInstance();
		SqlSession	sqlSession		= factory.openSession();
		int			makeBuddysCheck	= 0;

		try {
			makeBuddysCheck = sqlSession.insert("MsgrMapper.makeBuddy", pMap);
		}
		catch (Exception e) {
			System.out.println(e.getMessage());
			makeBuddysCheck = 0;
		}
		sqlSession.commit();
		sqlSession.close();

		return makeBuddysCheck;
	}

	/**
	 * 전체 오픈 톡방 불러오기 메서드 /확인
	 * 
	 * SELECT room_no_nu, room_name_vc FROM MSGR_ROOM WHERE is_private_yn = 0
	 * 
	 * @param pMap
	 * @return
	 */
	public List<Map<String, Object>> getAllOpenTalkList(Map<String, Object> pMap) {
		factory = MyBatisCommonFactory.getInstance();
		SqlSession					sqlSession	= factory.openSession();
		List<Map<String, Object>>	tempList	= sqlSession.selectList("MsgrMapper.getAllOpenTalkList", pMap);

		sqlSession.close();

		return tempList;
	}

	/**
	 * 참여한 오픈 톡방 불러오기 메서드 /확인
	 * 
	 * SELECT r.room_no_nu, r.room_name_vc FROM MSGR_ROOM r, MSGR_ROOM_IN_LIST rlist
	 * WHERE mem_id_vc = #{mem_id_vc} AND r.room_no_nu = rlist.room_no_nu AND
	 * is_private_yn =0
	 * 
	 * @param pMap
	 * @return
	 */
	public List<Map<String, Object>> getJoinOpenTalkList(Map<String, Object> pMap) {
		factory = MyBatisCommonFactory.getInstance();
		SqlSession					sqlSession	= factory.openSession();
		List<Map<String, Object>>	tempList	= sqlSession.selectList("MsgrMapper.getJoinOpenTalkList", pMap);

		sqlSession.close();

		return tempList;

	}

	/**
	 * 참여한 친구 톡방 불러오기 메서드 /확인
	 * 
	 * SELECT r.room_no_nu, r.room_name_vc FROM MSGR_ROOM r, MSGR_ROOM_IN_LIST rlist
	 * WHERE mem_id_vc = #{mem_id_vc} AND r.room_no_nu = rlist.room_no_nu AND
	 * is_private_yn =1
	 * 
	 * @param pMap
	 * @return
	 */
	public List<Map<String, Object>> getJoinBuddyTalkList(Map<String, Object> pMap) {
		factory = MyBatisCommonFactory.getInstance();
		SqlSession					sqlSession	= factory.openSession();
		List<Map<String, Object>>	tempList	= sqlSession.selectList("MsgrMapper.getJoinBuddyTalkList", pMap);

		sqlSession.close();

		return tempList;

	}

	public List<Map<String, Object>> getTalkRoomUserList(Map<String, Object> pMap) {
		factory = MyBatisCommonFactory.getInstance();
		SqlSession					sqlSession	= factory.openSession();
		List<Map<String, Object>>	tempList	= sqlSession.selectList("MsgrMapper.getTalkRoomUserList", pMap);
		sqlSession.close();

		return tempList;
	}

	public int getJoinChatNum(Map<String, Object> pMap) {
		factory = MyBatisCommonFactory.getInstance();
		SqlSession	sqlSession	= factory.openSession();
		int			temp		= sqlSession.selectOne("MsgrMapper.getJoinChatNum", pMap);
		sqlSession.close();
		return temp;
	}

	public List<Map<String, Object>> getAllChatList(Map<String, Object> pMap) {
		factory = MyBatisCommonFactory.getInstance();
		SqlSession					sqlSession	= factory.openSession();
		List<Map<String, Object>>	tempList	= sqlSession.selectList("MsgrMapper.getAllChatList", pMap);
		sqlSession.close();

		return tempList;
	}

	public static void main(String[] args) {
		MessengerDAO	dao	= new MessengerDAO();

		MessengerMap	map	= MessengerMap.getInstance();

//		map.getMap().put("mem_id_vc", "test4");
//		map.getMap().put("mem_id_vc", "test3");
		map.getMap().put("join_chat_no_nu", "1");
		map.getMap().put("room_no_nu", 1);

//		int test = dao.getJoinChatNum(map.getMap()); 
//		System.out.println(test);

//		System.out.println("sdafasdf");
		List<Map<String, Object>> list = dao.getAllChatList(map.getMap());

		for (Map<String, Object> map2 : list) {
			System.out.println(map2);
		}

	}
}
