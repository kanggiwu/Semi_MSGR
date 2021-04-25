package msgr.server;

import java.util.List;

public class MessengerTalkRoom {
	private List<MessengerServerThread> roomInList = null;
	private int room_no = -1;
	private String roomTitle = null;
	private int is_private = -1;
	private int currentClientNum = -1;
	
	public void setMsgrTalkRoom(List<MessengerServerThread> roomInList
								, int room_no, String roomTitle,  int is_private) {
		this.roomInList = roomInList;
		this.room_no = room_no;
		this.is_private = is_private;
		this.roomTitle = roomTitle;
	}
	public void setMsgrTalkRoom(int room_no, String roomTitle,  int is_private) {
		this.roomTitle = roomTitle;
		this.room_no = room_no;
		this.is_private = is_private;
	}
	public int getRoom_no() {
		return room_no;
	}
	public int getIs_private() {
		return is_private;
	}
	public String getRoomTitle() {
		return roomTitle;
	}
	
	
	
	
	
	
	
	
	
	
	
}
