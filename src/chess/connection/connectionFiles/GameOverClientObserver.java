package chess.connection.connectionFiles;

import java.util.List;

import chess.game.mvc.model.genericGameFiles.Board;
import chess.game.mvc.model.genericGameFiles.GameObserver;
import chess.game.mvc.model.genericGameFiles.Piece;
import chess.game.mvc.model.genericGameFiles.Game.State;

public class GameOverClientObserver implements GameObserver {

	private GameClient gameClient;
	
	public GameOverClientObserver (GameClient gc) {
		this.gameClient = gc;
	}
	
	@Override
	public void onGameStart(Board board, String gameDesc, List<Piece> pieces, Piece turn) { }

	@Override
	public void onGameOver(Board board, State state, Piece winner) {
		gameClient.onGameOver();
	}

	@Override
	public void onMoveStart(Board board, Piece turn) { }

	@Override
	public void onMoveEnd(Board board, Piece turn, boolean success) { }

	@Override
	public void onChangeTurn(Board board, Piece turn) { }

	@Override
	public void onError(String msg) { }

}
