package m127.golad;

import java.util.Random;

import golad.bot.Bot;
import golad.bot.BotFactory;
import golad.lib.CellState;
import golad.lib.Move;
import golad.lib.MoveFactory;
import golad.lib.MutableBoard;
import golad.lib.impl.DefaultBoard;

public class Game {
	private final BotFactory<?> redf,bluef;
	private final int w,h,initCells;
	
	public Game(BotFactory<?> redf, BotFactory<?> bluef, int w, int h, int initCells) {
		this.redf=redf;
		this.bluef=bluef;
		this.w=w;
		this.h=h;
		this.initCells=initCells;
	}
	
	public Game(BotFactory<?> redf, BotFactory<?> bluef, int w, int h) {
		this(redf,bluef,w,h,(int)(Math.random()*w*h/2));
	}
	
	private Bot initBot(BotFactory<?> botf, CellState color, BotFactory<?> opponent) {
		return botf.createBot(opponent.getName(), opponent.getVersion(), opponent.getAuthor(), w, h, initCells, color);
	}
	private Bot initRed() {
		return initBot(redf,CellState.RED,bluef);
	}
	
	private Bot initBlue() {
		return initBot(bluef,CellState.BLUE,redf);
	}
	
	public CellState game() {
		Bot red=initRed(),blue=initBlue();
		final MutableBoard board=new DefaultBoard(w,h);
		Random r=new Random();
		for(int i=0;i<initCells;i++) {
			final int x=r.nextInt(w),y=r.nextInt(h);
			if(board.getCellStateAt(x, y).alive) i--;
			else if(w%2==1 && h%2==1) {
				if(x==w/2 && y==h/2) i--;
			} else {
				board.setCellStateAt(x, y, CellState.RED);
				board.setCellStateAt(w-x-1, h-y-1, CellState.BLUE);
			}
		}
		Move lastMove=null;
		for(boolean b=false;board.getTotalCellCount(CellState.BLUE_STATES)>0 && board.getTotalCellCount(CellState.RED_STATES)>0;b=!b) {
			final MoveFactory f=new MoveFactoryImpl(board,b?CellState.BLUE:CellState.RED);
			final Bot bot=b?blue:red;
			try{
				final Move m=bot.getNextMove(f, lastMove);
				if(!MoveFactoryImpl.isValid(m,board,f.getPlayerColor())) {
					if(b) {
						red.endGame(Boolean.TRUE);
						blue.endGame(Boolean.FALSE);
						return CellState.RED;
					}
					blue.endGame(Boolean.TRUE);
					red.endGame(Boolean.FALSE);
					return CellState.BLUE;
				}
				for(int i=0;i<m.getMoveType().sacrifices;i++) board.setCellStateAt(m.getSacrificeX(i), m.getSacrificeY(i), CellState.DEAD);
				switch(m.getMoveType()) {
				case BIRTH:
					board.setCellStateAt(m.getTargetX(), m.getTargetY(), f.getPlayerColor());
					break;
				case KILL:
					board.setCellStateAt(m.getTargetX(), m.getTargetY(), CellState.DEAD);
					break;
				}
				board.iterateSelf();
			} catch(Exception e) {
				if(b) {
					red.endGame(Boolean.TRUE);
					blue.endGame(Boolean.FALSE);
					return CellState.RED;
				}
				blue.endGame(Boolean.TRUE);
				red.endGame(Boolean.FALSE);
				return CellState.BLUE;
			}
		}
		if(board.getTotalCellCount(CellState.BLUE_STATES)>0) {
			blue.endGame(Boolean.TRUE);
			red.endGame(Boolean.FALSE);
			return CellState.BLUE;
		} else if(board.getTotalCellCount(CellState.RED_STATES)>0) {
			red.endGame(Boolean.TRUE);
			blue.endGame(Boolean.FALSE);
			return CellState.RED;
		} else {
			red.endGame(null);
			blue.endGame(null);
			return CellState.DEAD;
		}
	}
}
