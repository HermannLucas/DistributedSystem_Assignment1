package assignment;

import java.util.List;
import java.util.UUID;

import Notifications.MsgNotification;
import Ressources.SpaceUtils;
import net.jini.core.entry.Entry;
import net.jini.core.event.*;
import net.jini.core.lease.Lease;
import net.jini.space.JavaSpace;

import net.jini.export.Exporter;
import net.jini.jeri.BasicILFactory;
import net.jini.jeri.BasicJeriExporter;
import net.jini.jeri.tcp.TcpServerEndpoint;

import java.rmi.RMISecurityManager;
import java.rmi.Remote;
import java.rmi.RemoteException;

public class MessageHandler implements RemoteEventListener {

	private Chat vador;
	private JavaSpace space;
	private RemoteEventListener theStub;
	
	public MessageHandler (Chat assoChat) {
		vador = assoChat;
		
		// Set up the security manager
		if (System.getSecurityManager() == null) {
			System.setSecurityManager(new RMISecurityManager());
		}
		// Connecting to the space
		space = SpaceUtils.getSpace();
		if (space == null) {
			System.err.println("Failed to find the javaspace");
			System.exit(1);
		}
		
	}
	
	// Listener for the chatroom associated to this message handler
	public void listen () {
		// Creating an event for new messages sent to the chatroom
		// Creating the exporter
		Exporter myDefaultExporter = new BasicJeriExporter(TcpServerEndpoint.getInstance(0), new BasicILFactory(), false, true);
		
		try  {
			// Registering the remote object and collect the reference to the 'stub'
			theStub = (RemoteEventListener) myDefaultExporter.export((Remote) this);
			
			// Make the listener for our precise chatroom
			MsgNotification notifTemplate = new MsgNotification();
			notifTemplate.chatroomId = vador.chatroomId;
			notifTemplate.target = vador.username;
			space.notify(notifTemplate, null, this.theStub, 10*60*1000, null);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	// This method is called when a new message arrive
	// it collects the new notification
	// calls the method collecting the message
	// and pass the new message to the updateText method from the parent
	public void notify (RemoteEvent ev) {
		MsgNotification newNotifTemplate = new MsgNotification();
		newNotifTemplate.chatroomId = vador.chatroomId;
		newNotifTemplate.target = vador.username;
		
		try {
			MsgNotification newNotif = (MsgNotification)space.take(newNotifTemplate, null, 10*60*1000);
			vador.updateText(getMessage(newNotif.msgId));
		} catch (Exception e) {
			e.printStackTrace();			
		}
	}
	
	// This method require a message Id and read the Message on the space associated to this id
	private Message getMessage(UUID msgId) {
		Message msg = new Message();
		Message newMsgTemplate = new Message();
		newMsgTemplate.id = msgId;
		
		try {
			msg = (Message)space.read(newMsgTemplate, null, 10*60*1000);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return msg;
	}
	
	

	// Writes a new message on the space
	// then calls the method creating a notification for each user of this chatroom 
	public void sendMessage (Message msg) {
		
		// Start by writing the message on the space
		try {
			space.write(msg, null, 10*60*1000);
			
			// Then if the write succeed call the method to create the notifications
			sendNotifications(getUsers(), msg.id);	
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	// Create a notification for each targeted users
	private void sendNotifications (List<String> targets, UUID msgId) {
		targets.forEach((user) -> {
			MsgNotification notif = new MsgNotification(msgId, vador.chatroomId, user);
			try {
				space.write(notif, null, 10*60*1000);
			} catch (Exception e) {
				e.printStackTrace();
			}
		});
	}

	// Collect the List of users connected on the chatroom
	private List<String> getUsers () {
		ChatRoom chatTemplate = new ChatRoom();
		chatTemplate.id = vador.chatroomId;
		ChatRoom chat = new ChatRoom();
		
		try {
			chat = (ChatRoom)space.read(chatTemplate, null, 10*60*1000);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return chat.users;
	}
	
	// sending the text to be saved
	protected void saveText(SavedText txt) {
		try {
			space.write(txt,null,20*60*1000);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
}
