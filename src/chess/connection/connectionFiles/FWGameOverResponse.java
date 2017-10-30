package chess.connection.connectionFiles;

import chess.game.mvc.model.genericGameFiles.Board;
import chess.game.mvc.model.genericGameFiles.GameObserver;
import chess.game.mvc.model.genericGameFiles.Piece;
import chess.game.mvc.model.genericGameFiles.Game.State;

public class FWGameOverResponse implements Response {

	private static final long serialVersionUID = 1L;
	private Board board;
	private State state;
	private Piece winner;
	
	public FWGameOverResponse(Board board, State state, Piece winner) {
		this.board = board;
		this.state = state;
		this.winner = winner;
	}
	
	@Override
	public void run(GameObserver o) {
		o.onGameOver(board, state, winner);
	}
}
