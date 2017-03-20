package m127.golad.bot;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.Point;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;

import m127.golad.ui.CellPane;
import m127.golad.ui.JOptionPaneMessenger;
import m127.golad.ui.UIUtil;

import golad.bot.Bot;
import golad.lib.Board;
import golad.lib.CellState;
import golad.lib.Move;
import golad.lib.MoveFactory;
import golad.lib.MutableBoard;
import golad.lib.impl.DefaultBoard;

public class GameUI extends JPanel implements Bot,MouseListener {
	/**
	 * 
	 */
	private static final long serialVersionUID = -6425215926837976060L;
	private Thread waiting;
	private MoveFactory fac;
	private Move move;
	private Point t,s;
	private MutableBoard b;
	
	private JPanel header=new JPanel(new GridLayout(1,0,5,5));
		private JTextArea oname,oversion,oauthor,ocolor;
	private JPanel gridPane;
		private CellPane[][] cells; 
	public GameUI(String opponentName, String opponentVersion, String opponentAuthor, CellState color, int w, int h) {
		super(new BorderLayout(5,5));
		this.add(header, BorderLayout.NORTH);
		header.add(UIUtil.wrapBorder(oname=new JTextArea(opponentName),"Opponent"));
		oname.setEditable(false);
		oname.setBackground(getBackground());
		header.add(UIUtil.wrapBorder(oversion=new JTextArea(opponentVersion),"Opponent Version"));
		oversion.setEditable(false);
		oversion.setBackground(getBackground());
		header.add(UIUtil.wrapBorder(oauthor=new JTextArea(opponentAuthor),"Opponent Author"));
		oauthor.setEditable(false);
		oauthor.setBackground(getBackground());
		header.add(UIUtil.wrapBorder(ocolor=new JTextArea(color.name()),"Your Color"));
		ocolor.setEditable(false);
		switch(color) {
		case RED:
			ocolor.setBackground(Color.RED);
			break;
		case BLUE:
			ocolor.setBackground(Color.BLUE);
			ocolor.setForeground(Color.WHITE);
			break;
		default:
			break;
		}
		
		this.add(new JScrollPane(gridPane=new JPanel(new GridLayout(h,w,5,5))), BorderLayout.CENTER);
		cells=new CellPane[w][h];
		for(int i=0;i<w;i++) for(int j=0;j<h;j++) {
			cells[i][j]=new CellPane(CellState.DEAD);
			gridPane.add(cells[i][j]);
			cells[i][j].addMouseListener(this);
		}
		gridPane.setPreferredSize(new Dimension(40*w,40*h));
		JFrame f=new JFrame("Human vs. "+opponentName);
		f.setContentPane(this);
		f.pack();
		f.setSize(Math.max(f.getWidth(), 600), Math.max(f.getHeight(), 600));
	}
	
	@Override
	public Move getNextMove(MoveFactory factory, Move lastOpponentMove) {
		fac=factory;
		setBoard(b=new DefaultBoard(factory.getBoard()));
		if(this.getTopLevelAncestor().isVisible()) this.requestFocus();
		else this.getTopLevelAncestor().setVisible(true);
		waiting=Thread.currentThread();
		try {
			waiting.join();
		} catch (InterruptedException e) {}
		this.getTopLevelAncestor().setVisible(false);
		Move ret=move;
		move=null;
		fac=null;
		b=null;
		return ret;
	}
	
	private void setBoard(Board b) {
		for(int i=0;i<b.getWidth();i++) for(int j=0;j<b.getHeight();j++) {
			cells[i][j].setState(b, i, j);
		}
	}
	
	@Override
	public void endGame(Boolean win) {
		getTopLevelAncestor().setVisible(false);
		new JOptionPaneMessenger("You ("+ocolor.getText()+")"+(win==null?"have drawn":win?"won":"lost")+".", "Game End").start();
	}

	@Override
	public void mouseClicked(MouseEvent e) {
		int x,y=-1;
		L:for(x=0;x<cells.length;x++) for(y=0;y<cells[x].length;y++) {
			if(e.getSource()==cells[x][y]) break L;
		}
		//System.err.printf("Clicked %s;%s%n",x,y);
		if(x==cells.length) return;
		if(y==-1) return;
		if(t==null) {
			if(fac.getBoard().getCellStateAt(x, y).alive) try{
				move=fac.createKillMove(x, y);
				waiting.interrupt();
			} catch(IndexOutOfBoundsException|IllegalArgumentException ex) {
				ex.printStackTrace();
				setBoard(b=new DefaultBoard(fac.getBoard()));
			} else {
				t=new Point(x,y);
				b.setCellStateAt(x, y, fac.getPlayerColor());
				setBoard(b);
			}
		} else if(s==null) {
			if(fac.getBoard().getCellStateAt(x, y).alive) {
				s=new Point(x,y);
				b.setCellStateAt(x, y, CellState.DEAD);
				setBoard(b);
			}
		} else if(fac.getBoard().getCellStateAt(x, y).alive) try{
			move=fac.createBirthMove(t.x, t.y, new int[]{s.x,x}, new int[]{s.y,y});
			s=null;
			t=null;
			waiting.interrupt();
		} catch(IndexOutOfBoundsException|IllegalArgumentException ex) {
			ex.printStackTrace();
			setBoard(b=new DefaultBoard(fac.getBoard()));
		}
	}

	@Override
	public void mouseEntered(MouseEvent arg0) {}

	@Override
	public void mouseExited(MouseEvent arg0) {}

	@Override
	public void mousePressed(MouseEvent arg0) {}

	@Override
	public void mouseReleased(MouseEvent arg0) {}

}
