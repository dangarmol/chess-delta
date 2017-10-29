package chess.practicaViews.ttt;

import java.lang.reflect.InvocationTargetException;

import javax.swing.SwingUtilities;

import chess.basecode.bgame.control.Controller;
import chess.basecode.bgame.control.Player;
import chess.basecode.bgame.model.GameError;
import chess.basecode.bgame.model.GameObserver;
import chess.basecode.bgame.model.Observable;
import chess.basecode.bgame.model.Piece;
import chess.basecode.ttt.TicTacToeFactory;

@SuppressWarnings("serial")
public class TicTacToeFactoryExtended extends TicTacToeFactory {
	
	/**
	 * Constructor
	 */
	public TicTacToeFactoryExtended() {
		super();
	}
	
	/**
	 * Creates the Swing View
	 */
	@Override
	public void createSwingView(final Observable<GameObserver> g, final Controller c, final Piece viewPiece,
			final Player random, final Player ai) {
			try {
				SwingUtilities.invokeAndWait(new Runnable() {
					@Override
					public void run()
					{
						new TicTacToeSwingView(g, c, viewPiece, random, ai);
					}
				});
			} catch (InvocationTargetException | InterruptedException e) {
				throw new GameError(e.getMessage());
			}
	}
}