package Listener;

import java.rmi.RMISecurityManager;
import java.rmi.Remote;
import java.rmi.RemoteException;

import Notifications.MsgNotification;
import Notifications.UsersNotification;
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

			// Make an empty template to listen to any notification
			UsersNotification notifTemplate = new UsersNotification();
			space.notify(notifTemplate, null, this.theStub, 10*60*1000, null);
		} catch (Exception e) {
			e.printStackTrace();
		}	
	}

	// This method is called when the event is triggered
	// it collects the new notification
	public void notify (RemoteEvent ev) {
		UsersNotification newNotifTemplate = new UsersNotification();

		try {
			UsersNotification newNotif = (UsersNotification)space.read(newNotifTemplate, null, 10*60*1000);

			// Based on the boolean of the object call the connect or disconnect method of the ChatApp class
			if (newNotif.connected == true) {
				vador.userConnect(newNotif.who);
			} else {
				vador.userDisconnect(newNotif.who);
			}
		} catch (Exception e) {
			e.printStackTrace();			
		}
		
		listen();

	}

}
