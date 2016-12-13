package assignment;

import java.awt.BorderLayout;
import java.awt.Container;
import java.awt.FlowLayout;
import java.util.List;

import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.text.Position;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;


// GUI listing the connected users
public class UserPanel extends JFrame {

	private ChatApp vador; ;
	private JPanel userPanel, buttPanel;
	private JLabel buttLbl;
	private JButton chatBut;
	private JList<String> userLi;
	
	protected DefaultListModel<String> userLiModel;

	public UserPanel (ChatApp parent) {
		
		vador = parent;

		setTitle ("Connected Users - " + vador.username);
		addWindowListener (new java.awt.event.WindowAdapter () {
			public void windowClosing (java.awt.event.WindowEvent evt) {
				vador.terminate();
			}
		}   );

		Container userCont = getContentPane();
		userCont.setLayout (new BorderLayout ());

		userPanel = new JPanel ();
		userPanel.setLayout(new FlowLayout());

		userLiModel = new DefaultListModel<String> ();

		userLi = new JList<String> (userLiModel);
		userLi.setLayout (new FlowLayout ());
		
		userPanel.add(userLi);
		
		userCont.add(userPanel, "Center");
		
		buttPanel = new JPanel ();
		buttPanel.setLayout(new FlowLayout());

		buttLbl = new JLabel("Select the user(s) you want to chat with");
		buttPanel.add(buttLbl);
		
		chatBut = new JButton ();
		chatBut.setText("Start chat");
		chatBut.addActionListener (new java.awt.event.ActionListener () {
			public void actionPerformed (java.awt.event.ActionEvent evt) {
				vador.createChat(userLi.getSelectedValuesList());
			}
		}  );
		buttPanel.add(chatBut);
		
		userCont.add(buttPanel, "South");
		this.pack();
	}
	
	// Method to remove a user from the connected user list
	public void userDisconnect (String usr) {
		int index = userLi.getNextMatch(usr, 0, Position.Bias.Forward );
		if (index != -1) {
			userLiModel.remove(index);
			this.pack();
		}
	}
	
	// Method to add a new user to the connected user list
	public void userConnect (String usr) {
		userLiModel.addElement(usr);
		this.pack();
	}

}
