package msgr.util;

import java.util.ArrayList;
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

	/** alt + shift + j
	 * 로그인 메서드 
	 *  SELECT mem_id_vc, mem_nick_vc
		FROM msgr_member
		WHERE mem_id_vc = #{mem_id_vc} AND mem_pw_vc = #{mem_pw_vc}
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
	 * 회원가입의 아이디 중복확인 메서드 
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
	 * 회원가입 메서드 
	 * INSERT INTO msgr_member (mem_id_vc, mem_pw_vc, mem_nick_vc) 
	 * values (#{mem_id_vc}, #{mem_pw_vc}, #{mem_nick_vc})
	 * 
	 * @param pMap - 사용자가 입력한 아이디, 비밀번호, 닉네임을 저장해둔 Map
	 * @return accept - (1) : 회원가입 성공 
	 * 				    (-1) : 회원가입 실패
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
	 * 톡방리스트 불러오기 메서드 
	 * SELECT room.room_name_vc, room.room_no_nu
		FROM MSGR_ROOM room, MSGR_ROOM_IN_LIST rlist
		WHERE room.room_no_nu = rlist.room_no_nu AND rlist.mem_id_vc = #{mem_id_vc}
		ORDER BY room.room_no_nu ASC
	 * 
	 * @param pMap
	 * @return
	 */
	public List<Map<String, Object>> getTalkRoomList(Map<String, Object> pMap) {
		factory = MyBatisCommonFactory.getInstance();
		SqlSession					sqlSession	= factory.openSession();
		List<Map<String, Object>>	tempList	= sqlSession.selectList("MsgrMapper.getTalkRoomList", pMap);

		sqlSession.close();

		return tempList;
	}
	
	/**
	 * 친구목록 불러오기 메서드
	 * SELECT buddy_id_vc FROM MSGR_BUDDY 
	 * WHERE mem_id_vc = #{mem_id_vd} ORDER BY mem_id_vc
	 * 
	 * @param pMap
	 * @return
	 */
	public List<Map<String, Object>> getBuddyList(Map<String, Object> pMap){
		factory = MyBatisCommonFactory.getInstance();
		SqlSession					sqlSession	= factory.openSession();
		List<Map<String, Object>>	tempList	= sqlSession.selectList("MsgrMapper.getBuddyList", pMap);
	
		sqlSession.close();

		return tempList;	
	}
	
	/**
	 * 톡방 추가하기 메소드
	 * INSERT INTO MSGR_ROOM(room_no_nu, room_name_vc, is_private_yn) 
	 * VALUES (#{room_no_nu), #{room_name_vc}, 1)
	 * @param pMap
	 * @return
	 */
	public List<Map<String, Object>> createTalkRoom(Map<String, Object> pMap){
		factory = MyBatisCommonFactory.getInstance();
		SqlSession					sqlSession	= factory.openSession();
		List<Map<String, Object>>	tempList	= sqlSession.selectList("MsgrMapper.createTalkRoom", pMap);
	
		sqlSession.close();

		return tempList;
	}
	
	/**
	 * 톡방 삭제 메소드
	 * DELETE FROM MSGR_ROOM 
	 * WHERE room_no_nu = #{room_no_nu}
	 * @param pMap
	 * @return
	 */
	public List<Map<String, Object>> deleteTalkRoom(Map<String, Object> pMap){
		factory = MyBatisCommonFactory.getInstance();
		SqlSession					sqlSession	= factory.openSession();
		List<Map<String, Object>>	tempList	= sqlSession.selectList("MsgrMapper.deleteTalkRoom", pMap);
	
		sqlSession.close();

		return tempList;
	}
	
	
	/**
	 * 마지막 대화번호 가져오기 메소드
	 * SELECT chat_no_nu FROM(SELECT chat_no_nu) 
	 * FROM MSGR_CHAT ORDER BY chat_no_nu DESC) WHERE ROWNUM =1
	 * @param pMap
	 * @return
	 */
	public List<Map<String, Object>> getLastChatNum(Map<String, Object> pMap){
		factory = MyBatisCommonFactory.getInstance();
		SqlSession					sqlSession	= factory.openSession();
		List<Map<String, Object>>	tempList	= sqlSession.selectList("MsgrMapper.getLastChatNum", pMap);
	
		sqlSession.close();

		return tempList;
	}
	
	/**
	 * 톡방 참가한 이후의 대화내용 가져오기 메소드
	 * SELECT chat.chat_vc FROM MSGR_CHAT chat, MSGR_ROOM_IN_LIST rlist 
		WHERE chat.chat_no_nu > (SELECT join_chat_no_nu FROM MSGR_ROOM_IN_LIST WHERE mem_id_vc=#{mem_id_vc})
		AND rlist.mem_id_vc = #{rlist.mem_id_vc}
		AND chat.roon_no_nu = #{chat.room_no_nu}
	 * @param pMap
	 * @return
	 */
	public List<Map<String, Object>> getChatAfterJoin(Map<String, Object> pMap){
		factory = MyBatisCommonFactory.getInstance();
		SqlSession					sqlSession	= factory.openSession();
		List<Map<String, Object>>	tempList	= sqlSession.selectList("MsgrMapper.getChatAfterJoin", pMap);
	
		sqlSession.close();

		return tempList;
	}
	
	/**
	 * 회원탈퇴 메소드
	 * {
		 CALL
		  BEGIN
		  	UPDATE MSGR_CHAT SET mem_id_vc = null WHERE mem_id_vc = #{mem_id_vd};
		  	DELETE FROM MSGR_ROOM_IN_LIST WHERE mem_id_vc = #{mem_id_vc};
		  	DELETE FROM MSGR_BUDDY WHERE mem_id_vc = #{mem_id_vc};
		  	DELETE FROM MSGR__MEMBER WEHRE mem_id_vc = #{mem_id_vc};
		  END
		}
	 * @param pMap
	 * @return
	 */
	public List<Map<String, Object>> deleteMember(Map<String, Object> pMap){
		factory = MyBatisCommonFactory.getInstance();
		SqlSession					sqlSession	= factory.openSession();
		List<Map<String, Object>>	tempList	= sqlSession.selectList("MsgrMapper.deleteMember", pMap);
		
		sqlSession.close();
		
		return tempList;
	}
	
	/**
	 * 친구삭제 메소드
	 * {
		 CALL
		  BEGIN
		  	DELETE FROM MSGR_BUDDY WHERE buddy_id_vc = #{buddy_id_vc} AND mem_id_vc = #{mem_id_vc};
		  	DELETE FROM MSGR_BUDDY WHERE buddy_id_vc = #{buddy_id_vc} AND mem_id_vc = #{mem_id_vc};
		  END
		}
	 * @param pMap
	 * @return
	 */
	public List<Map<String, Object>> deleteBuddy(Map<String, Object> pMap){
		factory = MyBatisCommonFactory.getInstance();
		SqlSession					sqlSession	= factory.openSession();
		List<Map<String, Object>>	tempList	= sqlSession.selectList("MsgrMapper.deleteBuddy", pMap);
		
		sqlSession.close();
		
		return tempList;
	}
	
	/**
	 * 닉네임 변경 메소드
	 * 
		UPDATE MSGR_MEMBER SET mem_nick_vc = #{mem_nick_vc} WHERE mem_id_vc = #{mem_id_vc}
	
	 * @param pMap
	 * @return
	 */
	public List<Map<String, Object>> changeNickname(Map<String, Object> pMap){
		factory = MyBatisCommonFactory.getInstance();
		SqlSession					sqlSession	= factory.openSession();
		List<Map<String, Object>>	tempList	= sqlSession.selectList("MsgrMapper.changeNickname", pMap);
		
		sqlSession.close();
		
		return tempList;
	}
	
	/**
	 * 친구추가 메소드
	 * {
		 CALL
		  BEGIN
		   INSERT INTO MSGR_BUDDY(buddy_seq_nu, mem_id_vc, buddy_id_vc) VALUES (buddy_seq_nu.NEXTVAL, #{msgr_mem_id), #{msgr_buddy_id};
		   CREATE SEQUENCE buddy_seq_nu START WITH 1 INCREMENT BY MAXVALUE 1000 CYCLE NOCACHE
		 END
		}
	 * @param pMap
	 * @return
	 */
	public List<Map<String, Object>> makeBuddys(Map<String, Object> pMap){
		factory = MyBatisCommonFactory.getInstance();
		SqlSession					sqlSession	= factory.openSession();
		List<Map<String, Object>>	tempList	= sqlSession.selectList("MsgrMapper.makeBuddys", pMap);
		
		sqlSession.close();
		
		return tempList;
	}
	
	/* 로그인 프로시저 메서드
	public Map<String, Object> loginProcedure(String id, String password, Map<String, Object> pMap) {
		factory = MyBatisCommonFactory.getInstance();
		SqlSession sqlSession = factory.openSession();

		pMap.put("id", id);
		pMap.put("password", password);
		sqlSession.selectOne("model.MemberMapper.mapProcedureTest", pMap);

		sqlSession.close();

		return pMap;
	}
	*/
	/*public static void main(String[] args) {
		MessengerDAO				dao		= new MessengerDAO();
		MessengerMap				msgrMap	= MessengerMap.getInstance();
		List<Map<String, Object>>	list	= new ArrayList<Map<String, Object>>();
		msgrMap.getMap().put("mem_id_vc", "test1");
		list = dao.getRoomList(msgrMap.getMap());

		for (Map<String, Object> map : list) {
			System.out.println(map);
		}

	}
	*/
}
