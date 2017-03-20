package m127.golad;

import java.util.Arrays;

import golad.lib.Board;
import golad.lib.CellState;
import golad.lib.Move;
import golad.lib.Move.Type;
import golad.lib.MutableBoard;
import golad.lib.impl.DefaultAbstractMoveFactory;
import golad.lib.impl.DefaultBoard;
import golad.lib.impl.DelegateBoard;

public class MoveFactoryImpl extends DefaultAbstractMoveFactory {
	private static class MMove implements Move {
		private final Type type;
		private final int x,y;
		private final int[] sx,sy;

		private final Board original,post;

		public MMove(Board original, CellState player, Type type, int x, int y, int[] sx, int[] sy) {
			this.type = type;
			this.x = x;
			this.y = y;
			this.sx = Arrays.copyOf(sx,sx.length);
			this.sy = Arrays.copyOf(sy, sy.length);
			this.original=new DelegateBoard(new DefaultBoard(original));
			MutableBoard b=new DefaultBoard(this.original);
			switch(type) {
			case BIRTH:
				b.setCellStateAt(x, y, player);
				for(int i=0;i<sx.length;i++) {
					b.setCellStateAt(sx[i], sy[i], CellState.DEAD);
				}
				break;
			case KILL:
				b.setCellStateAt(x, y, CellState.DEAD);
				break;
			}
			post=new DelegateBoard(b);
		}

		@Override
		public Type getMoveType() {
			return type;
		}

		@Override
		public int getTargetX() {
			return x;
		}

		@Override
		public int getTargetY() {
			return y;
		}

		@Override
		public int getSacrificeX(int index) throws IndexOutOfBoundsException {
			return sx[index];
		}

		@Override
		public int getSacrificeY(int index) throws IndexOutOfBoundsException {
			return sy[index];
		}

		@Override
		public Board getOriginalState() {
			return original;
		}

		@Override
		public Board getNewState() {
			return post;
		}

	}

	public MoveFactoryImpl(Board board, CellState player)
			throws IllegalArgumentException, NullPointerException {
		super(board, player);
	}

	public static boolean isValid(Move move, Board board, CellState player) {
		try{
			move.getSacrificeX(move.getMoveType().sacrifices);
			return false;
		} catch(IndexOutOfBoundsException ex) {}
		try{
			move.getSacrificeY(move.getMoveType().sacrifices);
			return false;
		} catch(IndexOutOfBoundsException ex) {}
		if(move.getMoveType().sacrifices>0) {
			try{
				move.getSacrificeX(move.getMoveType().sacrifices-1);
			} catch(IndexOutOfBoundsException ex) {
				return false;
			}
			try{
				move.getSacrificeY(move.getMoveType().sacrifices-1);
			} catch(IndexOutOfBoundsException ex) {
				return false;
			}
		}
		try{
			switch(move.getMoveType()) {
			case BIRTH:
				if(board.getCellStateAt(move.getTargetX(), move.getTargetY()).alive) return false;
				break;
			case KILL:
				if(!board.getCellStateAt(move.getTargetX(), move.getTargetY()).alive) return false;
				break;
			default:
				throw new IllegalArgumentException("Unknown type");
			}
			for(int i=0;i<move.getMoveType().sacrifices;i++) {
				if(player!=board.getCellStateAt(move.getSacrificeX(i), move.getSacrificeY(i))) return false;
			}
		} catch(IndexOutOfBoundsException ex) {
			return false;
		}
		return true;
	}

	@Override
	public Move createTestMove(Board board, CellState player, Type type, int x,
			int y, int[] sx, int[] sy) throws IllegalArgumentException,
			NullPointerException, IndexOutOfBoundsException {
		if(board==null || player==null || type==null || sx==null || sy==null) throw new NullPointerException();
		if(sx.length!=type.sacrifices || sy.length!=type.sacrifices) throw new IllegalArgumentException("Invalid sacrifice count");
		Move ret=new MMove(board, player, type, x, y, sx, sy);
		if(!isValid(ret,board,player)) throw new IllegalArgumentException("Invalid Move");
		return ret;
	}

}
