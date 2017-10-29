package chess.practicaViews.connectN;

import java.util.List;

import chess.basecode.bgame.control.Player;
import chess.basecode.bgame.model.Board;
import chess.basecode.bgame.model.GameMove;
import chess.basecode.bgame.model.GameRules;
import chess.basecode.bgame.model.Piece;
import chess.basecode.connectn.ConnectNMove;

@SuppressWarnings("serial")
public class ConnectNSwingPlayer extends Player {

	private int row;
	private int col;

	/**
	 * Constructor
	 */
	public ConnectNSwingPlayer() {	}

	/**
	 * Returns a game move
	 */
	@Override
	public GameMove requestMove(Piece p, Board board, List<Piece> pieces, GameRules rules) {
		return createMove(row, col, p);
	}

	/**
	 * Sets the value of a move
	 * @param row
	 * @param col
	 */
	public void setMoveValue(int row, int col) {
		this.row = row;
		this.col = col;
	}
	
	/**
	 * Creates and returns a new move
	 * @param row
	 * @param col
	 * @param p
	 * @return
	 */
	protected GameMove createMove(int row, int col, Piece p) {
		return new ConnectNMove(row, col, p);
	}
}