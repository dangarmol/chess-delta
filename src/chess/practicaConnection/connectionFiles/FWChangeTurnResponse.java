package chess.practicaConnection.connectionFiles;

import chess.basecode.bgame.model.Board;
import chess.basecode.bgame.model.GameObserver;
import chess.basecode.bgame.model.Piece;

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