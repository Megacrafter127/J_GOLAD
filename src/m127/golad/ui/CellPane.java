package m127.golad.ui;

import java.awt.Color;
import java.awt.Graphics;
import java.util.Collections;
import java.util.EnumMap;
import java.util.Map;

import golad.lib.Board;
import golad.lib.CellState;

import javax.swing.JComponent;

public class CellPane extends JComponent {
	/**
	 * 
	 */
	private static final long serialVersionUID = -6684176270608180180L;
	public static final Map<CellState,Color> STATE_COLORS;
	static{
		Map<CellState,Color> map=new EnumMap<>(CellState.class);
		map.put(CellState.DEAD, Color.DARK_GRAY);
		map.put(CellState.RED, Color.RED);
		map.put(CellState.BLUE, Color.BLUE);
		STATE_COLORS=Collections.unmodifiableMap(map);
	}
	
	private CellState currentState,futureState;
	public CellPane(CellState currentState, CellState futureState) {
		if(currentState==null) throw new NullPointerException();
		this.currentState=currentState;
		this.futureState=futureState;
	}
	
	public CellPane(CellState state) {
		this(state,null);
	}
	
	public CellPane(Board b, int x, int y) {
		this(b.getCellStateAt(x, y),b.iterate().getCellStateAt(x, y));
	}

	public CellState getCurrentState() {
		return currentState;
	}

	public void setCurrentState(CellState currentState) {
		if(currentState==null) throw new NullPointerException();
		this.currentState = currentState;
		repaint();
	}

	public CellState getFutureState() {
		return futureState;
	}

	public void setFutureState(CellState futureState) {
		this.futureState = futureState;
		repaint();
	}
	
	public void setState(Board b, int x, int y) {
		currentState=b.getCellStateAt(x, y);
		futureState=b.iterate().getCellStateAt(x, y);
		repaint();
	}

	@Override
	protected void paintComponent(Graphics g) {
		super.paintComponent(g);
		g.setColor(STATE_COLORS.get(currentState));
		g.fillRect(0, 0, getWidth(), getHeight());
		if(futureState!=null) {
			g.setColor(STATE_COLORS.get(futureState));
			g.fillRect(getWidth()*4/10, getHeight()*4/10, getWidth()/5, getHeight()/5);
		}
	}
	
	
}
