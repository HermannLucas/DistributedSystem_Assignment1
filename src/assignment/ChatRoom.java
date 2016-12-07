package assignment;

import java.util.List;
import java.util.UUID;

import net.jini.core.entry.Entry;

// Chatroom listing all the users connected to it and 
public class ChatRoom implements Entry {
	// Variables
	public UUID id;
	public List<String> users;
	
	// Empty constructor (for templates only)
	public ChatRoom () {
	}
	
	// Second constructor (all arguments)
	public ChatRoom (UUID title, List<String> present) {
		id = title;
		users = present;
	}
}
