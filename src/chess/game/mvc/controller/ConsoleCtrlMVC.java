package chess.game.mvc.controller;

import java.util.List;
import java.util.Scanner;

import chess.game.mvc.controller.commands.Command;
import chess.game.mvc.controller.commands.CommandSet;
import chess.game.mvc.model.genericGameFiles.Game;
import chess.game.mvc.model.genericGameFiles.GameError;
import chess.game.mvc.model.genericGameFiles.Piece;
import chess.game.mvc.model.genericGameFiles.Player;
import chess.game.mvc.model.genericGameFiles.Game.State;

/**
 * A controller that uses MVC. The difference from {@link ConsoleCtrl} is that
 * it does not print any output related to the game (only user interaction
 * errors).
 * 
 * <p>
 * Controlador que usa MVC. Se diferencia de {@link ConsoleCtrl} en que no
 * escribe ningun resultado relacionado con el juego (solo los errores de
 * interaccion con el usuario).
 */
public class ConsoleCtrlMVC extends ConsoleCtrl {

	public ConsoleCtrlMVC(Game g, List<Piece> pieces, List<Piece> pieceTypes, List<Player> players, Scanner in) {
		super(g, pieces, pieceTypes, players, in);
	}

	@Override
	public void start() {

		// start the game
		game.start(this.pieces, this.pieceTypes);

		while (game.getState() == State.InPlay) {

			// get a line from the user
			System.out.println();
			System.out.print("Please type a command ('help' for usage info.): ");
			String line = in.nextLine().trim();

			// parse and execute the command
			Command cmd = CommandSet.parse(line);
			if (cmd != null) {
				try {
					cmd.execute(this);
				} catch (GameError e) {
				}
			} else {
				System.err.println("Unknown command: " + line);
				System.err.flush();
			}
		}

	}

}
