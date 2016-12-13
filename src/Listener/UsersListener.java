package Listener;

import java.rmi.RMISecurityManager;
import java.rmi.Remote;
import java.rmi.RemoteException;

import Notifications.MsgNotification;
import Notifications.UserNotification;
import Ressources.SpaceUtils;
import assignment.ChatApp;
import assignment.Message;
import net.jini.core.event.RemoteEvent;
import net.jini.core.event.RemoteEventListener;
import net.jini.core.event.UnknownEventException;
import net.jini.core.lease.Lease;
import net.jini.export.Exporter;
import net.jini.jeri.BasicILFactory;
import net.jini.jeri.BasicJeriExporter;
import net.jini.jeri.tcp.TcpServerEndpoint;
import net.jini.space.JavaSpace;

public class UsersListener implements RemoteEventListener {

	private ChatApp vador;
	private JavaSpace space;
	private RemoteEventListener theStub;


	public UsersListener(ChatApp parent) {

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

		listen ();
	}

	// Listener for any user notifications
	private void listen() {
		// Creating an event for new user notification creation
		// Creating the exporter
		Exporter myDefaultExporter = new BasicJeriExporter(TcpServerEndpoint.getInstance(0), new BasicILFactory(), false, true);

		try  {
			// Registering the remote object and collect the reference to the 'stub'
			theStub = (RemoteEventListener) myDefaultExporter.export(this);

			// Make template with our username to listen to every notificqations comming to us
			UserNotification notifTemplate = new UserNotification();
			notifTemplate.target = vador.username;
			space.notify(notifTemplate, null, this.theStub, 10*60*1000, null);
		} catch (Exception e) {
			e.printStackTrace();
		}	
	}

	// This method is called when the event is triggered
	// it collects the new notification
	public void notify (RemoteEvent ev) {
		UserNotification newNotifTemplate = new UserNotification();
		newNotifTemplate.target = vador.username;
		try {
			UserNotification newNotif = (UserNotification)space.take(newNotifTemplate, null, 10*60*1000);

			String newUsr = newNotif.who;
			// Check if it's not related to us
//			if (newUsr != vador.username) {
				// Based on the boolean of the object call the connect or disconnect method of the ChatApp class
				if (newNotif.connected == true) {
					vador.userConnect(newUsr);
				} else {
					vador.userDisconnect(newUsr);
				}
//			}
		} catch (Exception e) {
			e.printStackTrace();			
		}
		
		listen();

	}

}
