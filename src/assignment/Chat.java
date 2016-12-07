package assignment;

import java.util.UUID;

public class Chat {
	
	protected String username;
	protected UUID chatroomId;
	private ChatsWindow vador;
	
	private MessageHandler luke;
	
	// Require that the chatroom is already created on the space
	public Chat (ChatsWindow parent,String me, UUID chatroom) {
		vador = parent;
		username = me;
		chatroomId = chatroom;
		
		// Create a new message handler for this chatroom
		// and make it listen for new messages
		luke = new MessageHandler (this);
		luke.listen();
	}
	 // NEED TO MODIFY THIS PART
	public void sendMessage(String txt) {
		
		Message msg = new Message();
		msg.sender = username;
		msg.id = UUID.randomUUID();
		msg.content = txt;
		luke.sendMessage(msg);
	}
	
	// Collect the text from a message object and update the graphical interface
	protected void updateText (Message msg) {
		
		vador.textPart.append(msg.sender + " sent : " + msg.content);
	}
}
