package m127.golad;

import golad.lib.CellState;

import java.util.Collections;
import java.util.EnumMap;
import java.util.Map;

public class GameThread extends Thread {
	private final Game game;
	private final int times;
	private final Map<CellState,Integer> wins=new EnumMap<>(CellState.class);
	
	public GameThread(Game game, int times) {
		this.game = game;
		this.times = times;
	}
	
	public GameThread(Game game) {
		this(game,1);
	}
	
	public GameThread(String name, Game game, int times) {
		super(name);
		this.game = game;
		this.times = times;
	}
	
	public GameThread(String name, Game game) {
		this(name,game,1);
	}
	
	public GameThread(ThreadGroup group, String name, Game game, int times) {
		super(group, name);
		this.game = game;
		this.times = times;
	}
	
	public GameThread(ThreadGroup group, String name, Game game) {
		this(group,name,game,1);
	}
	
	@Override
	public void run() {
		for(int i=0;i<times;i++) {
			CellState win=game.game();
			Integer c=wins.get(win);
			if(c==null) c=1;
			else c++;
			wins.put(win, c);
		}
	}
	
	public Map<CellState,Integer> getWinMap() {
		return Collections.unmodifiableMap(wins);
	}
}
