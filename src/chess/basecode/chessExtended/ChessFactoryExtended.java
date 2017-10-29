package chess.basecode.chessExtended;

import java.lang.reflect.InvocationTargetException;

import javax.swing.SwingUtilities;

import chess.basecode.bgame.control.Controller;
import chess.basecode.bgame.control.Player;
import chess.basecode.bgame.model.GameError;
import chess.basecode.bgame.model.GameObserver;
import chess.basecode.bgame.model.Observable;
import chess.basecode.bgame.model.Piece;
import chess.basecode.chess.ChessFactory;

@SuppressWarnings("serial")
public class ChessFactoryExtended extends ChessFactory {

	/**
	 * Constructor
	 * @param dim Dimension
	 * @param numObs number of obstacles
	 */
	public ChessFactoryExtended(Integer dim, Integer numObs) {
		super(dim, numObs);
	}
	
	/**
	 * Default Ataxx Constructor
	 */
	public ChessFactoryExtended() {
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
						new ChessSwingView(g, c, viewPiece, random, ai);
					}
				});
			} catch (InvocationTargetException | InterruptedException e) {
				throw new GameError(e.getMessage());
			}
	}
}