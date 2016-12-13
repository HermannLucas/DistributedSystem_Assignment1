package assignment;

import java.awt.BorderLayout;
import java.awt.Container;
import java.awt.FlowLayout;
import java.util.UUID;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JTabbedPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;

// Class displaying the window gathering all the chats in tabs
public class ChatsWindow extends JFrame {
	
	private JPanel inputPanel;
	protected JTextArea textPart;
	protected JTextArea msgInput;
	
	public ChatsWindow (String me,UUID chatId) {
		
		setTitle ("Chat");
		
		Chat chat = new Chat(this, me, chatId);
		
		Container inTab = getContentPane();
		inTab.setLayout(new BorderLayout ());
		
		textPart = new JTextArea();
		textPart.setEditable(false);
		textPart.setLayout(new FlowLayout ());
		
		inTab.add(textPart, "North");
		
		inputPanel = new JPanel();
		inputPanel.setLayout(new FlowLayout ());
		
		msgInput = new JTextArea (2,125);
		msgInput.setText("");
		
		inputPanel.add(msgInput, "Center");
		
		JButton sendBut = new JButton ();
		sendBut.setText("Send");
		sendBut.addActionListener (new java.awt.event.ActionListener () {
			public void actionPerformed (java.awt.event.ActionEvent evt) {
				chat.sendMessage(msgInput.getText());
			}
		}  );
		
		JButton saveBut = new JButton ();
		saveBut.setText("Save");
		saveBut.addActionListener (new java.awt.event.ActionListener () {
			public void actionPerformed (java.awt.event.ActionEvent evt) {
				chat.saveText(textPart.getSelectedText());
			}
		}  );
		
		inputPanel.add(sendBut, "East");
		inputPanel.add(saveBut, "East");
		inTab.add(inputPanel, "South");
		this.pack();
	}
}
