package Notifications;

import java.util.UUID;

import net.jini.core.entry.Entry;

public class ChatNotification implements Entry {
	// Variables
	public UUID chatId;
	public String target;

	// Empty constructor (for templates only)
	public ChatNotification () {
	}

	// Full constructor (all arguments)
	public ChatNotification (UUID chatroomId, String user) {
		chatId = chatroomId;
		target = user;
	}
}
