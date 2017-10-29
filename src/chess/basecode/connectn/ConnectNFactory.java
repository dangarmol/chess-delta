package chess.basecode.connectn;

import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

import chess.basecode.bgame.control.AIPlayer;
import chess.basecode.bgame.control.ConsolePlayer;
import chess.basecode.bgame.control.Controller;
import chess.basecode.bgame.control.DummyAIPlayer;
import chess.basecode.bgame.control.GameFactory;
import chess.basecode.bgame.control.Player;
import chess.basecode.bgame.model.AIAlgorithm;
import chess.basecode.bgame.model.GameError;
import chess.basecode.bgame.model.GameMove;
import chess.basecode.bgame.model.GameObserver;
import chess.basecode.bgame.model.GameRules;
import chess.basecode.bgame.model.Observable;
import chess.basecode.bgame.model.Piece;
import chess.basecode.bgame.views.GenericConsoleView;

/**
 * A factory for creating connect-N games. See {@link ConnectNRules} for the
 * description of the game.
 * 
 * 
 * <p>
 * Factoria para la creacion de juegos Connect-n. Vease {@link ConnectNRules}
 * para la descripcion del juego.
 */
public class ConnectNFactory implements GameFactory {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	protected int dim;

	public ConnectNFactory() {
		this(5);
	}

	public ConnectNFactory(int dim) {
		if (dim < 3) {
			throw new GameError("Dimension must be at least 3: " + dim);
		} else {
			this.dim = dim;
		}
	}

	@Override
	public GameRules gameRules() {
		return new ConnectNRules(dim);
	}

	@Override
	public Player createConsolePlayer() {
		ArrayList<GameMove> possibleMoves = new ArrayList<GameMove>();
		possibleMoves.add(new ConnectNMove());
		return new ConsolePlayer(new Scanner(System.in), possibleMoves);
	}

	@Override
	public Player createRandomPlayer() {
		return new ConnectNRandomPlayer();
	}

	@Override
	public Player createAIPlayer(AIAlgorithm alg) {
		if ( alg != null ) {
			return new AIPlayer(alg);
		} else {
			return new DummyAIPlayer(createRandomPlayer(), 1000);
		}
	}

	/**
	 * By default, we have two players, X and O.
	 * <p>
	 * Por defecto, hay dos jugadores, X y O.
	 */
	@Override
	public List<Piece> createDefaultPieces() {
		List<Piece> pieces = new ArrayList<Piece>();
		pieces.add(new Piece("X"));
		pieces.add(new Piece("O"));
		return pieces;
	}

	@Override
	public void createConsoleView(Observable<GameObserver> g, Controller c) {
		new GenericConsoleView(g, c);
	}

	@Override
	public void createSwingView(final Observable<GameObserver> g, final Controller c, final Piece viewPiece,
			Player random, Player ai) {
		throw new UnsupportedOperationException("There is no swing view");
	}

}
