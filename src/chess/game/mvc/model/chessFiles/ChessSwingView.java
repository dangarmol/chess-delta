package chess.game.mvc.model.chessFiles;

import chess.game.mvc.controller.Controller;
import chess.game.mvc.controller.Player;
import chess.game.mvc.model.genericGameFiles.GameError;
import chess.game.mvc.model.genericGameFiles.GameObserver;
import chess.game.mvc.model.genericGameFiles.Observable;
import chess.game.mvc.model.genericGameFiles.Piece;
import chess.game.mvc.view.chessViews.ChessFRBoardSwingView;

@SuppressWarnings("serial")
public class ChessSwingView extends ChessFRBoardSwingView {

	private ChessSwingPlayer player;
	private boolean activeBoard;
	private int row;
	private int col;
	private int rowDes;
	private int colDes;
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
	
	//Returns a string with the needed position (a1 to h8)
	private String getFormattedPosition(int row, int col) {
		String aux = "";
		switch(col) {
		case 0:
			aux += "a";	break;
		case 1:
			aux += "b";	break;
		case 2:
			aux += "c";	break;
		case 3:
			aux += "d";	break;
		case 4:
			aux += "e";	break;
		case 5:
			aux += "f";	break;
		case 6:
			aux += "g";	break;
		case 7:
			aux += "h"; break;
		default:
			throw new GameError("Error writing formatted position.");
		}
		
		aux += (8 - row);
		
		return aux;
	}
	
	/**
	 * Performs a move with the value of the click position
	 */
	@Override
	protected void handleMouseClick(int clickedRow, int clickedCol, int mouseButton) {
		if (activeBoard && firstClick && mouseButton == 1)
		{
			this.row = clickedRow;
			this.col = clickedCol;
			firstClick = false;
			//addMsg("Selected piece (" + this.row + "," + this.col + ")");
			addMsg("Selected piece from " + getFormattedPosition(this.row, this.col) + ".");
			addMsg("Click on the destination...");
		}
		else if (activeBoard && !firstClick)
		{
			this.rowDes = clickedRow;
			this.colDes = clickedCol;
			firstClick = true;
			if(mouseButton == 1)
			{
				addMsg("Destination for the piece: " + getFormattedPosition(this.rowDes, this.colDes) + ".");
				player.setMoveValue(this.row, this.col, this.rowDes, this.colDes);
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
