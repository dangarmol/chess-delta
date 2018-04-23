package chess.game.mvc.view.chessViews;

import java.awt.Color;
import java.util.List;

import chess.game.mvc.controller.Controller;
import chess.game.mvc.model.genericGameFiles.GameObserver;
import chess.game.mvc.model.genericGameFiles.Observable;
import chess.game.mvc.model.genericGameFiles.Piece;
import chess.game.mvc.model.genericGameFiles.Player;

@SuppressWarnings("serial")
public abstract class ChessFRBoardSwingView extends ChessWindowSwingView { //Crea el tablero
	/**
	 * Constructor
	 * @param g
	 * @param c
	 * @param localPiece
	 * @param randPlayer
	 * @param aiPlayer
	 */
	public ChessFRBoardSwingView(Observable<GameObserver> g, Controller c, Piece localPiece, Player randPlayer, List<Player> aiPlayer) {
		super(g, c, localPiece, randPlayer, aiPlayer);
	}
	
	private ChessBoardComponent boardComponent;

	/**
	 * Starts the board GUI
	 */
	@Override
	protected void startBoardGUI() {
		boardComponent = new ChessBoardComponent() {
				@Override
				protected void mouseClicked(int row, int col, int mouseButton) {
					handleMouseClick(row, col, mouseButton);
				}
				
				@SuppressWarnings("unused")
				protected void mouseOver() {
					
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