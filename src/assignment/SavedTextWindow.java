package assignment;

import java.awt.BorderLayout;
import java.awt.Container;
import java.awt.FlowLayout;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JTextArea;

public class SavedTextWindow extends JFrame {

	private JPanel panel;
	private JTextArea textPart;
	
	public SavedTextWindow() {
		setTitle ("Saved messages");
		
		Container inTab = getContentPane();
		inTab.setLayout(new BorderLayout ());
		
		textPart = new JTextArea();
		textPart.setEditable(false);
		textPart.setLayout(new FlowLayout ());
		
		inTab.add(textPart, "North");
		this.pack();
	}
	
	public void addMessage (SavedText savedTxt) {
		textPart.append("You saved : " + savedTxt.content + "\n");
	}
}
