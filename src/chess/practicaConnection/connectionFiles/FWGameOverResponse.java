package chess.practicaConnection.connectionFiles;

import chess.basecode.bgame.model.Board;
import chess.basecode.bgame.model.GameObserver;
import chess.basecode.bgame.model.Piece;
import chess.basecode.bgame.model.Game.State;

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
