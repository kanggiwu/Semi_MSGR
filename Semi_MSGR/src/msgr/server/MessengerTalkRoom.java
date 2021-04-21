package msgr.server;

import java.util.List;

public class MessengerTalkRoom {
	private List<MessengerServerThread> roomInList = null;
	private int talk_no = -1;
	private int is_private = -1;
	private int currentClientNum = -1;
	private String talkTitle = null;
	
	public void setMsgrTalkRoom(List<MessengerServerThread> roomInList
								, int talk_no,  int is_private, String talkTitle) {
		this.roomInList = roomInList;
		this.talk_no = talk_no;
		this.is_private = is_private;
		this.talkTitle = talkTitle;
	}
	public void setMsgrTalkRoom(String talkTitle, int talk_no,  int is_private) {
		this.talkTitle = talkTitle;
		this.talk_no = talk_no;
		this.is_private = is_private;
	}
	public int getTalk_no() {
		return talk_no;
	}
	public int getIs_private() {
		return is_private;
	}
	public String getTalkTitle() {
		return talkTitle;
	}
	
	
	
	
	
	
	
	
	
	
	
}
