package m127.golad.ui;

import java.awt.Component;

import javax.swing.JOptionPane;

public class JOptionPaneMessenger extends Thread {
	private final Component parent;
	private final String message,title;
	private final int type;
	
	public JOptionPaneMessenger(Component parent, String message, String title, int type) {
		this.parent = parent;
		this.message = message;
		this.title = title;
		this.type = type;
	}
	
	public JOptionPaneMessenger(String message, String title, int type) {
		this(null,message,title,type);
	}
	
	public JOptionPaneMessenger(String message, String title) {
		this(message,title,JOptionPane.PLAIN_MESSAGE);
	}

	@Override
	public void run() {
		JOptionPane.showMessageDialog(parent, message, title, type);
	}
}