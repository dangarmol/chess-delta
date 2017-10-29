package chess.basecode.bgame.control.commands;

import chess.basecode.bgame.control.Controller;

/**
 * A 'QUIT' command. It executes {@link Controller#stop()} of the corresponding
 * controller.
 *
 * <p>
 * Comando 'QUIT'. Ejecuta el metodo {@link Controller#stop()} del controlador
 * correspondiente.
 */
public class QuitCommand implements Command {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	@Override
	public void execute(Controller c) {
		c.stop();
	}

	@Override
	public String helpText() {
		return "QUIT: exit the game.";
	}

	@Override
	public Command parse(String cmd) {
		if (cmd.trim().equalsIgnoreCase("quit")) {
			return this;
		} else {
			return null;
		}
	}

}
