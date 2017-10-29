package chess.practicaViews.ttt;

import java.util.List;

import chess.basecode.bgame.control.Player;
import chess.basecode.bgame.model.Board;
import chess.basecode.bgame.model.GameMove;
import chess.basecode.bgame.model.GameRules;
import chess.basecode.bgame.model.Piece;
import chess.basecode.connectn.ConnectNMove;

@SuppressWarnings("serial")
public class TicTacToeSwingPlayer extends Player {

	private int row;
	private int col;
	
	/**
	 * Returns a valid move
	 */
	@Override
	public GameMove requestMove(Piece p, Board board, List<Piece> pieces, GameRules rules) {
		return createMove(row, col, p);
	}
	
	/**
	 * Returns a new game move
	 * @param row
	 * @param col
	 * @param p
	 * @return
	 */
	private GameMove createMove(int row, int col, Piece p) {
		return new ConnectNMove(row, col, p);
	}
	
	/**
	 * Sets the value for the move
	 * @param row
	 * @param col
	 */
	public void setMoveValue(int row, int col) {
		this.row = row;
		this.col = col;
	}
}