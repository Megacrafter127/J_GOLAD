package m127.golad;

import golad.lib.CellState;
import golad.lib.MutableBoard;
import golad.lib.impl.DefaultBoard;

import java.util.Random;

public class GameGen {
	public static MutableBoard genBoard(Random random) {
		final int w=2+random.nextInt(100)+random.nextInt(100),h=w;
		final boolean center=(w*h)%2==1;
		MutableBoard ret=new DefaultBoard(w,h);
		final int cells=w*h/5;
		for(int i=0;i<cells;i++) {
			final int x=random.nextInt(w),y=random.nextInt(h);
			if(ret.getCellStateAt(x, y)!=CellState.DEAD || (center && x==w/2 && y==h/2)) {
				i--;
				continue;
			}
			ret.setCellStateAt(x, y, CellState.RED);
			ret.setCellStateAt(w-x-1, h-y-1, CellState.BLUE);
		}
		return ret;
	}
}
