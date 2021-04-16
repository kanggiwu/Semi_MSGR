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

	/**
	 * 로그인 메서드 SELECT id, nickname FROM chat WHERE id = #{id} AND password =
	 * #{password}
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
	 * 회원가입 아이디 중복확인 메서드 SELECT id FROM chat WHERE id = #{id}
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
	 * 회원가입 메서드 INSERT INTO chat (id, password, nickname) values (#{id},
	 * #{password}, #{nickname})
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

	public List<Map<String, Object>> getRoomList(Map<String, Object> pMap) {
		factory = MyBatisCommonFactory.getInstance();
		SqlSession					sqlSession	= factory.openSession();
		List<Map<String, Object>>	tempList	= sqlSession.selectList("MsgrMapper.getRoomList", pMap);

		sqlSession.close();

		return tempList;
	}

	// 로그인 프로시저 메서드
	public Map<String, Object> loginProcedure(String id, String password, Map<String, Object> pMap) {
		factory = MyBatisCommonFactory.getInstance();
		SqlSession sqlSession = factory.openSession();

		pMap.put("id", id);
		pMap.put("password", password);
		sqlSession.selectOne("model.MemberMapper.mapProcedureTest", pMap);

		sqlSession.close();

		return pMap;
	}

	public static void main(String[] args) {
		MessengerDAO				dao		= new MessengerDAO();
		MessengerMap				msgrMap	= MessengerMap.getInstance();
		List<Map<String, Object>>	list	= new ArrayList<Map<String, Object>>();
		msgrMap.getMap().put("mem_id_vc", "test1");
		list = dao.getRoomList(msgrMap.getMap());

		for (Map<String, Object> map : list) {
			System.out.println(map);
		}

	}
}
