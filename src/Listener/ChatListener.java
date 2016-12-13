package Listener;

import java.rmi.RMISecurityManager;

import Notifications.ChatNotification;
import Notifications.ChatNotification;
import Ressources.SpaceUtils;
import assignment.ChatApp;
import net.jini.core.event.RemoteEvent;
import net.jini.core.event.RemoteEventListener;
import net.jini.core.lease.Lease;
import net.jini.export.Exporter;
import net.jini.jeri.BasicILFactory;
import net.jini.jeri.BasicJeriExporter;
import net.jini.jeri.tcp.TcpServerEndpoint;
import net.jini.space.JavaSpace;

public class ChatListener implements RemoteEventListener {
	
	private ChatApp vador;
	private JavaSpace space;
	private RemoteEventListener theStub;
	
	public ChatListener (ChatApp parent) {
		vador = parent;
		
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
		
		listen();
	}
	
	// Listener for chatroom notification
	private void listen () {
		// Creating an event for new user notification creation
		// Creating the exporter
		Exporter myDefaultExporter = new BasicJeriExporter(TcpServerEndpoint.getInstance(0), new BasicILFactory(), false, true);

		try  {
			// Registering the remote object and collect the reference to the 'stub'
			theStub = (RemoteEventListener) myDefaultExporter.export(this);

			// Make a template to listen to any notification toward us
			ChatNotification notifTemplate = new ChatNotification();
			notifTemplate.target = vador.username;
			space.notify(notifTemplate, null, this.theStub, 10*60*1000, null);
		} catch (Exception e) {
			e.printStackTrace();
		}	
	}
	
	// This method is called when the event is triggered
	// it collects the new notification
	public void notify (RemoteEvent ev) {
		
		ChatNotification newNotifTemplate = new ChatNotification();
		newNotifTemplate.target = vador.username;
		try {
			ChatNotification newNotif = (ChatNotification)space.take(newNotifTemplate, null, 10*60*1000);
			vador.connectToChat(newNotif.chatId);
		} catch (Exception e) {
			e.printStackTrace();			
		}
		
		listen();

	}

}
