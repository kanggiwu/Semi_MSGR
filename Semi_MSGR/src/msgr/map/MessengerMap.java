package msgr.map;

import java.util.HashMap;
import java.util.Map;

public class MessengerMap {

	private static MessengerMap	msgrMap	= null;
	private Map<String, Object>	map		= null;

	private MessengerMap() {
		columnInit();
	}

	// 싱글턴 방식으로 MessengerMap 인스턴스화
	public static MessengerMap getInstance() {

		if (msgrMap == null) {
			msgrMap = new MessengerMap();
		}
		return msgrMap;
	}

	// 모든 테이블의 컬럼을 미리 put해놓는다.
	private void columnInit() {
		map = new HashMap<String, Object>();
		/* 모든 테이블의 컬럼 미리 put 해놓기 */
		map.put("mem_id_vc", "");
		map.put("mem_pw_vc", "");
		map.put("mem_nick_vc", "");
		map.put("buddy_seq_nu", 0);
		map.put("buddy_id_vc", "");
		map.put("room_no_nu", 0);
		map.put("chat_no_nu", 0);
		map.put("chat_vc", "");
		map.put("room_name_vc", "");
		map.put("is_private_yn", 0);
		map.put("join_chat_no_nu", 0);

		/* 모든 테이블의 컬럼 미리 put 해놓기 */
	}

	public Map<String, Object> getMap() {
		return map;
	}
}
