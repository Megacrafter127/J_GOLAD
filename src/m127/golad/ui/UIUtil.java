package m127.golad.ui;

import java.awt.BorderLayout;
import java.awt.Component;

import javax.swing.BorderFactory;
import javax.swing.JPanel;

public class UIUtil {
	public static JPanel wrapBorder(Component c,String label) {
		JPanel ret=new JPanel(new BorderLayout(5,5));
		ret.add(c, BorderLayout.CENTER);
		ret.setBorder(BorderFactory.createTitledBorder(BorderFactory.createEtchedBorder(), label));
		return ret;
	}
	
	public static JPanel wrapDuo(Component c, Component r, String label) {
		JPanel ret=wrapBorder(c,label);
		ret.add(r, BorderLayout.EAST);
		return ret;
	}
}
