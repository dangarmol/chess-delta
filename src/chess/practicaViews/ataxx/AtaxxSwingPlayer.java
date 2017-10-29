package chess.practicaViews.ataxx;

import java.util.List;

import chess.basecode.bgame.control.Player;
import chess.basecode.bgame.model.Board;
import chess.basecode.bgame.model.GameMove;
import chess.basecode.bgame.model.GameRules;
import chess.basecode.bgame.model.Piece;
import chess.practicaAtaxx.ataxx.AtaxxMove;


@SuppressWarnings("serial")
public class AtaxxSwingPlayer extends Player {

	private int row;
	private int col;
	private int rowD;
	private int colD;

	/**
	 * Constructor
	 */
	public AtaxxSwingPlayer() {	}

	/**
	 * Returns a game move
	 */
	@Override
	public GameMove requestMove(Piece p, Board board, List<Piece> pieces, GameRules rules) {
		return createMove(row, col, rowD, colD, p);
	}

	/**
	 * Sets the value of the move
	 * @param row
	 * @param col
	 * @param rowD
	 * @param colD
	 */
	public void setMoveValue(int row, int col, int rowD, int colD) {
		this.row = row;
		this.col = col;
		this.rowD = rowD;
		this.colD = colD;
	}
	
	/**
	 * Creates and returns a new move for the game
	 * @param row initial row
	 * @param col initial col
	 * @param rowD destination 
	 * @param colD destination
	 * @param p piece
	 * @return the move
	 */
	protected GameMove createMove(int row, int col, int rowD, int colD, Piece p) {
		return new AtaxxMove(row, col, rowD, colD, p);
	}
}