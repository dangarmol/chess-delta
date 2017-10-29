package chess.practicaViews.connectN;

import chess.basecode.bgame.control.Controller;
import chess.basecode.bgame.control.Player;
import chess.basecode.bgame.model.GameObserver;
import chess.basecode.bgame.model.Observable;
import chess.basecode.bgame.model.Piece;

@SuppressWarnings("serial")
public class ConnectNSwingView extends chess.basecode.viewClasses.FiniteRectBoardSwingView {

	private ConnectNSwingPlayer player;
	private boolean activeBoard;
	
	/**
	 * Constructor
	 * @param g
	 * @param c
	 * @param localPiece
	 * @param randPlayer
	 * @param aiPlayer
	 */
	public ConnectNSwingView(Observable<GameObserver> g, Controller c, Piece localPiece, Player randPlayer,
			Player aiPlayer) {
		super(g, c, localPiece, randPlayer, aiPlayer); //Llama a constructor de SwingView mediante FiniteRectBoardSwingView
		player = new ConnectNSwingPlayer(); //Crea un nuevo jugador
		activeBoard = true;
	}

	/**
	 * Activates the board for a view
	 */
	@Override
	protected void activateBoard() {
		activeBoard = true;
		addMsg("Click on an empty cell");
	}

	/**
	 * Deactivates the board for a view
	 */
	@Override
	protected void deActivateBoard() {
		activeBoard = false;
	}
	
	/**
	 * Performs an action with the mouse click
	 */
	@Override
	protected void handleMouseClick(int row, int col, int mouseButton) {
		if (activeBoard)
		{
			player.setMoveValue(row, col);
			decideMakeManualMove(player);
			addMsg("Piece added on (" + row + "," + col + ")");
		}
	}

	/**
	 * Not used here
	 */
	@Override
	protected void resetCounter() {
		//Not used
	}
}
