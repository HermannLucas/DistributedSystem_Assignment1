package Notifications;

import net.jini.core.entry.Entry;

public class UserNotification implements Entry {
	
	// Variables
	public String who;
	public String target;
	public Boolean connected;
	
	// Empty constructor (for templates)
	public UserNotification () {
	}
	
	public UserNotification (String me, String reciepient, Boolean status) {
		who = me;
		target = reciepient;
		connected = status;
	}

}
