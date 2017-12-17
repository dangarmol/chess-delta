package chess.game.mvc.model.chessFiles;

import java.util.List;

import chess.game.mvc.Utils;
import chess.game.mvc.controller.Player;
import chess.game.mvc.model.genericGameFiles.Board;
import chess.game.mvc.model.genericGameFiles.GameError;
import chess.game.mvc.model.genericGameFiles.GameMove;
import chess.game.mvc.model.genericGameFiles.GameRules;
import chess.game.mvc.model.genericGameFiles.Pair;
import chess.game.mvc.model.genericGameFiles.Piece;
import chess.game.mvc.model.genericGameFiles.Game.State;

public class ChessDummyPlayer extends Player {

	private static final long serialVersionUID = 1L;

	//TODO This should also check if the match is finished. Sometimes when 2 AI players play against each other and one checkmates the other, this throws and exception. Find out why. It doesn't happen with Human vs Computer.
	@Override
	public GameMove requestMove(Piece piece, Board board, List<Piece> playersPieces, List<Piece> pieceTypes, GameRules rules) {
		List<GameMove> moves = rules.validMoves(board, playersPieces, piece);
		Pair<State, Piece> gameState = ((ChessRules) rules).updateState(board, playersPieces, piece);
		if(gameState.getFirst() != State.InPlay) { //TODO Check this. It might not be what needs to be checked.
			//throw new GameError("The player is stuck and can't move"); //TODO This needs to be changed.
			return null;
		}
		
		Utils.sleep(50); //Delay is in ms.
		if (Thread.interrupted()) {
			//This should never happen.
			return null;
		} else {
			return moves.get(Utils.randomInt(moves.size()));
		}
		
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
