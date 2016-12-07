package Notifications;

import java.util.UUID;

import net.jini.core.entry.Entry;

// This object will be used to let know a single user that a new message arrived
public class MsgNotification implements Entry {
		// Variables
		public UUID msgId;
		public UUID chatroomId;
		public String target;
		
		// Empty constructor (for templates only)
		public MsgNotification () {
		}
		
		// Full constructor (all arguments)
		public MsgNotification (UUID msg, UUID chatroom, String user) {
			msgId = msg;
			chatroomId = chatroom;
			target = user;
		}
}