 package chess.game.mvc.controller;

import java.util.List;

import chess.game.mvc.model.genericGameFiles.AIAlgorithm;
import chess.game.mvc.model.genericGameFiles.Board;
import chess.game.mvc.model.genericGameFiles.GameMove;
import chess.game.mvc.model.genericGameFiles.GameRules;
import chess.game.mvc.model.genericGameFiles.Piece;

/**
 * Class that implements an AI player that simply delegates the requests to an
 * {@link AIAlgorithm}.
 * 
 * <p>
 * Clase que implementa un jugador con IA que delega la peticion a un
 * {@link AIAlgorithm}..
 */
public class AIPlayer extends Player {

	private static final long serialVersionUID = 1L;
	private AIAlgorithm alg;

	/**
	 * Constructs an AI player.
	 *
	 * <p>
	 * Construye un jugador AI.
	 * 
	 * @param alg
	 *            The {@link AIAlgorithm} to be used by this player.
	 *            
	 *            <p>
	 *            El {@link AIAlgorithm} que usa este jugador. 
	 */
	public AIPlayer(AIAlgorithm alg) {
		this.alg = alg;
	}

	@Override
	public GameMove requestMove(Piece p, Board board, List<Piece> playersPieces, List<Piece> pieceTypes, GameRules rules) {
		return alg.getMove(p, board, playersPieces, pieceTypes, rules);
	}

}
