package chess.basecode.attt;

import chess.basecode.bgame.control.Player;
import chess.basecode.bgame.model.GameRules;
import chess.basecode.ttt.TicTacToeFactory;

/**
 * A factory for Advanced Tic-Tac-Toe. See {@link AdvancedTTTRules} for the game
 * rules.
 * 
 * <p>
 * Factoria para el juego Tic-Tac-Toe avanzado. Vease {@link AdvancedTTTRules}
 * para ver las reglas del juego.
 */
public class AdvancedTTTFactory extends TicTacToeFactory {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	@Override
	public GameRules gameRules() {
		return new AdvancedTTTRules();
	}

	/*
	@Override
	public Player createConsolePlayer() {
		ArrayList<GameMove> possibleMoves = new ArrayList<GameMove>();
		possibleMoves.add(new AdvancedTTTMove());
		return new ConsolePlayer(new Scanner(System.in), possibleMoves);
	}
   */
	
	@Override
	public Player createRandomPlayer() {
		return new AdvancedTTTRandomPlayer();
	}

}
