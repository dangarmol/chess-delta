package chess.game.mvc.model.chessFiles;

import java.lang.reflect.InvocationTargetException;

import javax.swing.SwingUtilities;

import chess.game.mvc.controller.Controller;
import chess.game.mvc.controller.Player;
import chess.game.mvc.model.genericGameFiles.GameError;
import chess.game.mvc.model.genericGameFiles.GameObserver;
import chess.game.mvc.model.genericGameFiles.Observable;
import chess.game.mvc.model.genericGameFiles.Piece;

@SuppressWarnings("serial")
public class ChessFactoryExtended extends ChessFactory {

	/**
	 * Constructor
	 */
	public ChessFactoryExtended() {}
	
	/**
	 * Creates the Swing View
	 */
	@Override
	public void createSwingView(final Observable<GameObserver> g, final Controller c, final Piece viewPiece,
			final Player random, final Player ai) {
			try {
				SwingUtilities.invokeAndWait(new Runnable() {
					@Override
					public void run() {
						new ChessSwingView(g, c, viewPiece, random, ai);
					}
				});
			} catch (InvocationTargetException | InterruptedException e) {
				throw new GameError(e.getMessage());
			}
	}
}