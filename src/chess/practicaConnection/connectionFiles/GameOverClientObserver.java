package chess.practicaConnection.connectionFiles;

import java.util.List;

import chess.basecode.bgame.model.Board;
import chess.basecode.bgame.model.Game.State;
import chess.basecode.bgame.model.GameObserver;
import chess.basecode.bgame.model.Piece;

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
