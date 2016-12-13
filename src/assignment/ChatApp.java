package assignment;

import java.util.Iterator;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;
import java.util.TreeSet;
import java.util.UUID;

import Listener.ChatListener;
import Listener.UsersListener;
import Notifications.ChatNotification;
import Notifications.MsgNotification;
import Notifications.UserNotification;
import Ressources.SpaceUtils;
import net.jini.core.lease.Lease;
import net.jini.core.transaction.Transaction;
import net.jini.core.transaction.TransactionFactory;
import net.jini.core.transaction.server.TransactionManager;
import net.jini.space.JavaSpace;

// Class starting the application
public class ChatApp {

	public String username;

	private JavaSpace space;
	private TransactionManager mgr;
	private ConnectedUsers userList;
	
	private StartUp startUpWdw;
	private UserPanel usersWdw;
	private ChatsWindow chatWdw;

	// Setting up the required resources required
	// connecting to the space and setting up the security manager
	public ChatApp () {

		space = SpaceUtils.getSpace();
		if (space == null){
			System.err.println("Failed to find the javaspace");
		}

		mgr = SpaceUtils.getManager();
		if (mgr == null) {
			System.err.println("Failed to find the transaction manager");
			System.exit(1);
		}

		// Calling the username input window
		startUpWdw = new StartUp(this);
		startUpWdw.setVisible(true);
	}
	
	
	// Getting the username entered from the start up window 
	// first checking if there is any user connected on the space and if not creating a new empty list of users
	// reading the object on the space and checking if our username is already taken if not moving on with the connection
	// asks the user to input a new username
	protected void setUsername (String name) {
		ConnectedUsers userTemplate = new ConnectedUsers();
		
		try {
			userList = (ConnectedUsers) space.read(userTemplate, null, 1000);

			// If no one is connected we create a new user Set on the space
			if (userList == null) {
				userList = new ConnectedUsers();
				userList.users = new TreeSet<String>();
				space.write(userList, null, 5*60*1000);				
			}
			
			if (userList.users.contains(name)) {
				startUpWdw.usedUsername();
			} else {
				username = name;
				connect();
				startUpWdw.terminate();
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	// Add our name is the connectedUsersList
	private void connect() {
		
		// Create the transaction
		try {
			Transaction.Created trc = null;
			try {
				trc = TransactionFactory.create(mgr, 3000);
			} catch (Exception e) {
				System.out.println("Could not create the transaction " + e);
			}

			Transaction txn = trc.transaction;

			// Collect the UsersConnected object and add our username in
			ConnectedUsers userTemplate = new ConnectedUsers();
			try {
				try {
					userList = (ConnectedUsers) space.take( userTemplate, null, 1000);

					userList.users.add(username);
					space.write(userList, txn, 5*60*1000);
				} catch (Exception e) {
					System.out.println("Failed to read or write to space " + e);
					txn.abort();
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
			
			txn.commit();

			userList.users.forEach((usr) -> {
				if (username != usr) {
					try {
						space.write(new UserNotification(username, usr, true), null, 10*1000);
					} catch (Exception e) {
						e.printStackTrace();
					}
				}
			});
			new UsersListener(this);
			
			usersPan();
			collectSavedMessages();
			
			new ChatListener(this);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	// Remove our name is the connectedUsersList
	private void disconnect() {
		
		// Create the transaction
		try {
			Transaction.Created trc = null;
			try {
				trc = TransactionFactory.create(mgr, 3000);
			} catch (Exception e) {
				System.out.println("Could not create the transaction " + e);
			}

			Transaction txn = trc.transaction;

			// Collect the UsersConnected object and remove our username in
			ConnectedUsers userTemplate = new ConnectedUsers();
			try {
				try {
					userList = (ConnectedUsers) space.take( userTemplate, null, 1000);

					userList.users.remove(username);
					space.write(userList, txn, 5*60*1000);
				} catch (Exception e) {
					System.out.println("Failed to read or write to space " + e);
					txn.abort();
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
			
			txn.commit();
			
			userList.users.forEach((usr) -> {
				try {
					space.write(new UserNotification(username, usr, false), null, 10*1000);
				} catch (Exception e) {
					e.printStackTrace();
				}
			});
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	// Now that we are connected we can call the user panel and populate it with all the actual users connected
	private void usersPan () {

		usersWdw = new UserPanel(this);
		usersWdw.setVisible(true);
		
		userList.users.forEach((usr) -> {
			if (!username.equals(usr)) {
				usersWdw.userLiModel.addElement(usr);
			}
		});
//		for (Iterator<String> iter = userList.users.iterator(); iter.hasNext(); ) {
//			usersWdw.userLiModel.addElement(iter.next());
//		}
	}
	
	// Method updating the userList when new user connects
	public void userConnect (String name) {
		usersWdw.userConnect(name);
	}
	
	// Method updating userList when a user disconnects
	public void userDisconnect (String name) {
		usersWdw.userDisconnect(name);
	}
	
	// Method generating a new chartoom
	protected void createChat (List<String> users) {
		UUID chatId = UUID.randomUUID();
		users.add(username);
		ChatRoom chatroom = new ChatRoom(chatId, users);
		// Writing the new chatroom object in the space and sending a notification to all the concerned users
		try {
			space.write(chatroom, null, 5*60*1000);

			users.forEach((user) -> {
				try {
					space.write(new ChatNotification(chatId, user), null, 10*1000);
				} catch (Exception e) {
					e.printStackTrace();
				}
			});
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	// Method connecting to a new chatroom based on the chatroom id
	public void connectToChat(UUID chatroom) {
		
		// Collecting the list of users present to use it as a title
		ChatRoom meow = new ChatRoom();
		String title = username + " - Chat : "; 
		meow.id = chatroom;
		try {
			meow = (ChatRoom) space.read(meow, null, 10*1000);
			meow.users.forEach((usr) -> {
				title.concat(usr + ", ");
			});
//			title.substring(0, title.length() - 2);
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		chatWdw = new ChatsWindow(username, chatroom);
		chatWdw.setTitle(title);
		chatWdw.setVisible(true);
	}

	public static void main(java.lang.String[] args) {
		new ChatApp();
	}

	private void collectSavedMessages (){
		SavedText txtTemplate = new SavedText();
		txtTemplate.saver = username;

		try {
			if (space.readIfExists(txtTemplate, null, 5*60*1000) != null) {
				SavedTextWindow svTxtWdw = new SavedTextWindow ();
				svTxtWdw.setVisible(true);
				while (space.readIfExists(txtTemplate, null, 5*60*1000) != null) { 
					 svTxtWdw.addMessage((SavedText) space.take(txtTemplate, null, 10*1000));

				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}


	// Method to terminate the ChatApp remove the user from the space and stop the timer renewing it
	public void terminate() {
		try {
			disconnect();
			//remove me from the user set
		} catch (Exception e) {
			e.printStackTrace();
		}
		System.exit(0);    	
	}
}
