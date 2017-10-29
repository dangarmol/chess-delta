package chess.basecode.viewClasses;

import java.awt.Color;

import chess.basecode.bgame.control.Controller;
import chess.basecode.bgame.control.Player;
import chess.basecode.bgame.model.GameObserver;
import chess.basecode.bgame.model.Observable;
import chess.basecode.bgame.model.Piece;

@SuppressWarnings("serial")
public abstract class FiniteRectBoardSwingView extends SwingView { //Crea el tablero
	/**
	 * Constructor
	 * @param g
	 * @param c
	 * @param localPiece
	 * @param randPlayer
	 * @param aiPlayer
	 */
	public FiniteRectBoardSwingView(Observable<GameObserver> g, Controller c, Piece localPiece, Player randPlayer, Player aiPlayer) {
		super(g, c, localPiece, randPlayer, aiPlayer);
	}
	
	private BoardComponent boardComponent;

	/**
	 * Starts the board GUI
	 */
	@Override
	protected void startBoardGUI() {
		boardComponent = new BoardComponent() {
				@Override
				protected void mouseClicked(int row, int col, int mouseButton) {
					handleMouseClick(row, col ,mouseButton);
				}
				
				@Override
				protected Color getPieceColor(Piece p) {
					return getPieceColorFromSwing(p);
				}; // get the color from the colours table, and if not available (e.g., for obstacles) set it to have a color
				
				@Override
				protected boolean isPlayerPiece(Piece p) {
					return rootPaneCheckingEnabled;
				}; // return true if p is a player piece, false if not (e.g, an obstacle)
		};
		setBoardArea(boardComponent); // install the board in the view
	};
	
	/**
	 * Redraws the board
	 */
	@Override
	protected void redrawBoard() {
		boardComponent.redraw(getBoard());
	} // ask boardComponent to redraw the board
	
	protected abstract void handleMouseClick(int row, int col, int mouseButton);
}