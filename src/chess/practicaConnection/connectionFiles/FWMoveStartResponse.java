package chess.practicaConnection.connectionFiles;

import chess.basecode.bgame.model.Board;
import chess.basecode.bgame.model.GameObserver;
import chess.basecode.bgame.model.Piece;

public class FWMoveStartResponse implements Response {

	private static final long serialVersionUID = 1L;
	private Board board;
	private Piece turn;
	
	public FWMoveStartResponse(Board board, Piece turn){
		this.board = board;
		this.turn = turn;
	}
	@Override
	public void run(GameObserver o) {
		o.onMoveStart(board, turn);
	}

}
