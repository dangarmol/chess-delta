package chess.connection.connectionFiles;

import chess.game.mvc.model.genericGameFiles.Board;
import chess.game.mvc.model.genericGameFiles.GameObserver;
import chess.game.mvc.model.genericGameFiles.Piece;

public class FWMoveEndResponse implements Response {

	private static final long serialVersionUID = 1L;
	private Board board;
	private Piece turn;
	private boolean success;
	FWMoveEndResponse(Board board, Piece turn, boolean success){
		this.board = board;
		this.turn = turn;
		this.success = success;
	}
	@Override
	public void run(GameObserver o) {
		o.onMoveEnd(board, turn, success);
	}

}
