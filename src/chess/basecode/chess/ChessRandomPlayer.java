package chess.basecode.chess;

import java.util.List;

import chess.basecode.bgame.Utils;
import chess.basecode.bgame.control.Player;
import chess.basecode.bgame.model.Board;
import chess.basecode.bgame.model.GameError;
import chess.basecode.bgame.model.GameMove;
import chess.basecode.bgame.model.GameRules;
import chess.basecode.bgame.model.Piece;

public class ChessRandomPlayer extends Player { //TODO Rename to ChessDummyPlayer

	private static final long serialVersionUID = 1L;

	@Override
	public GameMove requestMove(Piece piece, Board board, List<Piece> allPieces, GameRules rules) {
		List<GameMove> moves = rules.validMoves(board, allPieces, piece);
		
		if(moves.isEmpty()){
			throw new GameError("Player is stuck and can't move");
		}
		
		return moves.get(Utils.randomInt(moves.size()));

	}

	/**
	 * Creates the actual move to be returned by the player. Separating this
	 * method from {@link #requestMove(Piece, Board, List, GameRules)} allows us
	 * to reuse it for other similar games by overriding this method.
	 * 
	 * <p>
	 * Crea el movimiento concreto que sera devuelto por el jugador. Se separa
	 * este metodo de {@link #requestMove(Piece, Board, List, GameRules)} para
	 * permitir la reutilizacion de esta clase en otros juegos similares,
	 * sobrescribiendo este metodo.
	 * 
	 * @param row
	 *            row number.
	 * @param col
	 *            column number..
	 * @param p
	 *            Piece to place at ({@code row},{@code col}).
	 * @return
	 */
	protected GameMove createMove(int row, int col, int rowDes, int colDes, Piece piece) {
		return new ChessMove(row, col, rowDes, colDes, piece);
	}
}
