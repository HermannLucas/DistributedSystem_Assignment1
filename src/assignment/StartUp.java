package assignment;

import java.awt.*;
import javax.swing.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;

// First window to appear
// used to enter a username 
public class StartUp extends JFrame{

	private JPanel titlePanel, usernamePanel, incorrectPanel;
	private JTextField usernameIn;
	private JLabel usernameLbl, incorrectLbl;
	private JButton connectButton;
	private ChatApp vador;

	public StartUp (ChatApp father) {

		setTitle("User name input");

		vador = father;

		Container cont = getContentPane();
		cont.setLayout (new BorderLayout());

		titlePanel = new JPanel();
		titlePanel.setLayout(new FlowLayout());

		usernameLbl = new JLabel("Please enter your username");
		titlePanel.add(usernameLbl);

		cont.add(titlePanel, "North");

		usernamePanel = new JPanel();
		usernamePanel.setLayout (new FlowLayout());

		usernameIn = new JTextField(20);
		usernameIn.setText("");
		usernamePanel.add(usernameIn);

		connectButton = new JButton("Connect");
		connectButton.setEnabled(false);
		usernamePanel.add(connectButton);
		connectButton.addActionListener (new java.awt.event.ActionListener () {
			public void actionPerformed (java.awt.event.ActionEvent evt) {
				connect	(evt);
			}
		});

		cont.add(usernamePanel, "Center");

		incorrectPanel = new JPanel();
		incorrectPanel.setLayout(new FlowLayout());
		incorrectPanel.setVisible(false);

		incorrectLbl = new JLabel();
		incorrectPanel.add(incorrectLbl);

		cont.add(incorrectPanel, "South");
 
		// Enable the connect button when text is inputed
		usernameIn.getDocument().addDocumentListener(new DocumentListener() {
			public void changedUpdate(DocumentEvent e) {
				textEntered();
			}
			public void removeUpdate(DocumentEvent e) {
				textEntered();
			}
			public void insertUpdate(DocumentEvent e) {
				textEntered();
			}

			public void textEntered() {
				if (usernameIn.getText().equals("")){
					connectButton.setEnabled(false);
				}
				else {
					connectButton.setEnabled(true);
				}
			}
		});
		
		this.pack();

	}

	// Method called when the connect button is clicked
	// pass the username inputed to the ChatApp class to "connect" to the space and perform further tests
	private void connect (java.awt.event.ActionEvent evt) {
		if (usernameIn.getText().isEmpty()) {
			incorrectLbl.setText("Please enter a username.");
			incorrectPanel.setVisible(true);
		} else {
			vador.setUsername(usernameIn.getText());
		}
	}
	
	// Method called by the mChatApp class if the username is already used
	public void usedUsername () {
		incorrectLbl.setText("This username is already in use please try another one.");
		incorrectPanel.setVisible(true);

		usernameIn.setText("");		
	}

	public void terminate() {
		this.dispose();
	}
}
