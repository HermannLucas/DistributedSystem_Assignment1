package assignment;

import java.util.Set;
import java.util.TreeSet;

import net.jini.core.entry.Entry;

// Single object containing s tree set of all the users connected
public class ConnectedUsers implements Entry {
	// Variables
	public Set<String> users;
	
	// Empty constructor (for templates only)
	public ConnectedUsers() {		
	}
}
