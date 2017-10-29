package chess.practicaViews.attt;

import java.lang.reflect.InvocationTargetException;

import javax.swing.SwingUtilities;

import chess.basecode.attt.AdvancedTTTFactory;
import chess.basecode.bgame.control.Controller;
import chess.basecode.bgame.control.Player;
import chess.basecode.bgame.model.GameError;
import chess.basecode.bgame.model.GameObserver;
import chess.basecode.bgame.model.Observable;
import chess.basecode.bgame.model.Piece;

@SuppressWarnings("serial")
public class AdvancedTTTFactoryExtended extends AdvancedTTTFactory {
	
	/**
	 * Constructor
	 */
	public AdvancedTTTFactoryExtended() {
		super();
	}
	
	/**
	 * Creates the Swing View for the game
	 */
	@Override
	public void createSwingView(final Observable<GameObserver> g, final Controller c, final Piece viewPiece,
		final Player random, final Player ai) {
		try {
			SwingUtilities.invokeAndWait(new Runnable() {
				@Override
				public void run()
				{
					new AdvancedTTTSwingView(g, c, viewPiece, random, ai);
				}
			});
		} catch (InvocationTargetException | InterruptedException e) {
			throw new GameError(e.getMessage());
		}
	}
}