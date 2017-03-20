package m127.golad.bot;

import golad.bot.BotFactory;
import golad.lib.CellState;

public class GameUIFactory implements BotFactory<GameUI> {

	@Override
	public GameUI createBot(String opponentName, String opponentVersion,
			String opponentAuthor, int boardWidth, int boardHeight,
			int initialCellCount, CellState ownColor) {
		return new GameUI(opponentName, opponentVersion, opponentAuthor, ownColor, boardWidth, boardHeight);
	}

	@Override
	public String getName() {
		return "Human";
	}

	@Override
	public String getAuthor() {
		return "Megacrafter127";
	}

	@Override
	public String getVersion() {
		return "1.0";
	}
}
