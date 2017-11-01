package chess.game.mvc.model.chessFiles;

import chess.game.mvc.controller.Controller;
import chess.game.mvc.controller.Player;
import chess.game.mvc.model.genericGameFiles.GameObserver;
import chess.game.mvc.model.genericGameFiles.Observable;
import chess.game.mvc.model.genericGameFiles.Piece;
import chess.game.mvc.view.FiniteRectBoardSwingView;

@SuppressWarnings("serial")
public class ChessSwingView extends FiniteRectBoardSwingView {

	private ChessSwingPlayer player;
	private boolean activeBoard;
	private int row;
	private int col;
	private int rowD;
	private int colD;
	boolean firstClick = true;
	
	/**
	 * Constructor
	 * @param g
	 * @param c
	 * @param localPiece
	 * @param randPlayer
	 * @param aiPlayer
	 */
	public ChessSwingView(Observable<GameObserver> g, Controller c, Piece localPiece, Player randPlayer,
			Player aiPlayer) {
		super(g, c, localPiece, randPlayer, aiPlayer);
		player = new ChessSwingPlayer();
		activeBoard = true;
	}
	
	/**
	 * @return the value of firstClick
	 */
	public boolean getFirstClick() {
		return firstClick;
	}
	
	/**
	 * Performs a move with the value of the click position
	 */
	@Override
	protected void handleMouseClick(int row, int col, int mouseButton) {
		if (activeBoard && firstClick && mouseButton == 1)
		{
			this.row = row;
			this.col = col;
			firstClick = false;
			addMsg("Selected piece: (" + this.row + "," + this.col + ")");
			addMsg("Click on the destination...");
		}
		else if (activeBoard && !firstClick)
		{
			this.rowD = row;
			this.colD = col;
			firstClick = true;
			if(mouseButton == 1)
			{
				addMsg("Destination for the piece: (" + this.rowD + "," + this.colD + ")");
				player.setMoveValue(this.row, this.col, this.rowD, this.colD);
				decideMakeManualMove(this.player);
			}
		}
	}
	
	/**
	 * Activates the board for the view
	 */
	@Override
	protected void activateBoard() {
		activeBoard = true;
		addMsg("Click on one of your pieces and then click \non an empty box");
	}

	/**
	 * Deactivates the board for the view
	 */
	@Override
	protected void deActivateBoard() {
		activeBoard = false;
	}
}
