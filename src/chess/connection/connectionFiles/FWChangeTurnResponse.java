package chess.connection.connectionFiles;

import chess.game.mvc.model.genericGameFiles.Board;
import chess.game.mvc.model.genericGameFiles.GameObserver;
import chess.game.mvc.model.genericGameFiles.Piece;

public class FWChangeTurnResponse implements Response {

	private static final long serialVersionUID = 1L;
	private Board board;
	private Piece turn;
	
	public FWChangeTurnResponse(Board board, Piece turn){
		this.board = board;
		this.turn = turn;
	}
	@Override
	public void run(GameObserver o) {
		o.onChangeTurn(board, turn);
	}
}