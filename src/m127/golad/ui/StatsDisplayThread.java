package m127.golad.ui;

import golad.lib.CellState;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.Map;
import javax.swing.JOptionPane;

import m127.golad.GameThread;

public class StatsDisplayThread extends Thread {
	private final GameThread thread;

	public StatsDisplayThread(GameThread thread) {
		this.thread = thread;
	}

	public StatsDisplayThread(String name, GameThread thread) {
		super(name);
		this.thread = thread;
	}

	public StatsDisplayThread(ThreadGroup group, String name, GameThread thread) {
		super(group, name);
		this.thread = thread;
	}
	
	@Override
	public void run() {
		switch(thread.getState()) {
		case NEW:
			thread.start();
			//$FALL-THROUGH$
		case RUNNABLE:
		case BLOCKED:
		case TIMED_WAITING:
		case WAITING:
			try {
				thread.join();
			} catch (InterruptedException e) {}
			//$FALL-THROUGH$
		case TERMINATED:
			StringWriter stats=new StringWriter();
			PrintWriter p=new PrintWriter(stats);
			p.println("Results:");
			Map<CellState,Integer> m=thread.getWinMap();
			for(CellState c:CellState.values()) {
				p.printf("%s: %s%n", c.name(), m.get(c));
			}
			JOptionPane.showMessageDialog(null, stats.toString(), "Results", JOptionPane.PLAIN_MESSAGE);
			break;
		}
	}
}
