package Notifications;

import net.jini.core.entry.Entry;

public class UsersNotification implements Entry {
	
	// Variables
	public String who;
	public Boolean connected;
	
	// Empty constructor (for templates)
	public UsersNotification () {
	}
	
	public UsersNotification (String me, Boolean status) {
		who = me;
		connected = status;
	}

}
