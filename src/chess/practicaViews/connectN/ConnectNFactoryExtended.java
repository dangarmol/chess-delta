package chess.practicaViews.connectN;

import java.lang.reflect.InvocationTargetException;

import javax.swing.SwingUtilities;

import chess.basecode.bgame.control.Controller;
import chess.basecode.bgame.control.Player;
import chess.basecode.bgame.model.GameError;
import chess.basecode.bgame.model.GameObserver;
import chess.basecode.bgame.model.Observable;
import chess.basecode.bgame.model.Piece;
import chess.basecode.connectn.ConnectNFactory;

@SuppressWarnings("serial")
public class ConnectNFactoryExtended extends ConnectNFactory {

	/**
	 * Constructor
	 * @param dimRows
	 */
	public ConnectNFactoryExtended(Integer dimRows) {
		super(dimRows);
	}
	
	/**
	 * Constructor
	 */
	public ConnectNFactoryExtended() {
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
						new ConnectNSwingView(g, c, viewPiece, random, ai);
					}
				});
			} catch (InvocationTargetException | InterruptedException e) {
				throw new GameError(e.getMessage());
			}
	}
}