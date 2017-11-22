package chess.game.mvc.controller;

import java.util.List;

import chess.game.mvc.Utils;
import chess.game.mvc.model.genericGameFiles.Board;
import chess.game.mvc.model.genericGameFiles.GameMove;
import chess.game.mvc.model.genericGameFiles.GameRules;
import chess.game.mvc.model.genericGameFiles.Piece;

/**
 * Class that implements an AI player by encapsulating a {@link Player} given in
 * the construction. It simply waits for some predefined time (to pretend that
 * it is thinking!) and then delegate the request to the encapsulated player. We
 * use it just to have support for automatic players right from the beginning.
 * 
 * <p>
 * Clase que implementa un jugador con IA encapsulando un objeto {@link Player}
 * dado en la constructora. En este caso, simplemente espera un tiempo
 * predefinido (simulando que esta pensando) y después delega la peticion al
 * jugador encapsulado. Lo utilizamos solo para dar soporte desde el principio
 * al uso de jugadores automaticos.
 */
public class DummyAIPlayer extends Player {

	private static final long serialVersionUID = 1L;

	/**
	 * The player to which we delegate request.
	 * 
	 * <p>
	 * Jugador en el que se delega la peticion.
	 */
	private Player player;

	/**
	 * The delay, in milliseconds, to wait before delegating a request to
	 * {@link #player}.
	 * 
	 * <p>
	 * Tiempo (en milisegundos) de espera antes de delegar una peticion al
	 * jugador {@link #player}.
	 */
	private int delay;

	/**
	 * Constructs a "dummy" AI player.
	 *
	 * <p>
	 * Construye un jugador AI ficticio.
	 * 
	 * @param p
	 *            The player to which requests are delegated.
	 *            <p>
	 *            Jugador sobre el que se delegan las peticiones.
	 * @param delay
	 *            The amount of milliseconds to wait before delegating the
	 *            request.
	 *            <p>
	 *            Tiempo en milisegundos que se va a esperar antes de delegar
	 *            las peticiones.
	 */
	public DummyAIPlayer(Player p, int delay) {
		this.player = p;
		this.delay = delay;
	}

	@Override
	public GameMove requestMove(Piece piece, Board board, List<Piece> playersPieces, List<Piece> pieceTypes, GameRules rules) {
		// if the thread is interrupted it will come back from sleep immediatly,
		// with the interruption flag on
		Utils.sleep(delay);
		if (Thread.interrupted()) {
			return null;
		} else {
			return player.requestMove(piece, board, playersPieces, pieceTypes, rules);
		}
	}

}
