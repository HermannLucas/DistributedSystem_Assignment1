package assignment;

import java.util.UUID;

import net.jini.core.entry.Entry;

// Message sent with informations regarding the sender and the amount of people who hasn't read it yet
public class Message implements Entry {
	// Variables
	public String sender;
	public UUID id;
	public String content;
	
	// Empty constructor (for templates only)
	public Message () {
	}
}