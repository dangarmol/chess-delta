package chess.connection.connectionFiles;

import java.util.List;

import chess.game.mvc.model.genericGameFiles.Board;
import chess.game.mvc.model.genericGameFiles.GameObserver;
import chess.game.mvc.model.genericGameFiles.Piece;

public class FWGameStartResponse implements Response {

	private static final long serialVersionUID = 1L;
	private Board board;
	private String gameDesc;
	private List<Piece> pieces;
	private Piece turn;
	public FWGameStartResponse(Board board, String gameDesc, List<Piece> pieces, Piece turn) {
		this.board = board;
		this.gameDesc = gameDesc;
		this.pieces = pieces;
		this.turn = turn;
	}
	@Override
	public void run(GameObserver o) { o.onGameStart(board, gameDesc, pieces, turn); }
}
