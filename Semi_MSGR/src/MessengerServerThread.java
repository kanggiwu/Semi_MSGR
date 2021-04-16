import java.util.StringTokenizer;

public class MessengerServerThread extends Thread {
	MessagerServer msgrServer = null;
	public MessengerServerThread(MessagerServer msgrServer) {
		this.msgrServer = msgrServer;
	}

	public void run() {
		String msg = null;
		boolean isStop = false;
		try {
			run_start: while(!isStop) {
				msgrServer.textArea_log.append(msg + "\n");
				msgrServer.textArea_log.setCaretPosition(msgrServer.textArea_log.getDocument().getLength());
				StringTokenizer token = null;
				int protocol = 0; 
				if(msg != null) {
					token = new StringTokenizer(msg, "#");
					protocol = Integer.parseInt(token.nextToken());
				}
			}
		} catch (Exception e) {
			// TODO: handle exception
		}
	}
	
	
}
