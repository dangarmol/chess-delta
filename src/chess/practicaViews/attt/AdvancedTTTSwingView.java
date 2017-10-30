package chess.practicaViews.attt;

import chess.basecode.bgame.control.Controller;
import chess.basecode.bgame.control.Player;
import chess.basecode.bgame.model.GameObserver;
import chess.basecode.bgame.model.Observable;
import chess.basecode.bgame.model.Piece;
import chess.basecode.viewClasses.FiniteRectBoardSwingView;
import chess.practicaConnection.MainConnection;

@SuppressWarnings("serial")
public class AdvancedTTTSwingView extends FiniteRectBoardSwingView {

	private AdvancedTTTSwingPlayer player;
	private boolean activeBoard;
	private boolean startSwitching;
	private boolean firstClick = true;
	private int counter;
	private int row;
	private int col;
	private int rowD;
	private int colD;
	private boolean first = true;
	
	/**
	 * Constructor
	 * @param g
	 * @param c
	 * @param localPiece
	 * @param randPlayer
	 * @param aiPlayer
	 */
	public AdvancedTTTSwingView(Observable<GameObserver> g, Controller c, Piece localPiece, Player randPlayer,
			Player aiPlayer) {
		super(g, c, localPiece, randPlayer, aiPlayer);
		player = new AdvancedTTTSwingPlayer();
		activeBoard = true;
		startSwitching = false;
		this.counter = 0;
	}

	/**
	 * Chooses what to do with the info from the click
	 */
	@Override
	protected void handleMouseClick(int row, int col, int mouseButton) {
		if(activeBoard && mouseButton == 1)
		{
			if(!startSwitching)
			{
				player.setMoveValue(row, col);
				decideMakeManualMove(player);
				addMsg("Added piece on (" + row + "," + col + ")");
			}
			else
			{
				if(firstClick)
				{
					this.row = row;
					this.col = col;
					firstClick = false;
				}
				else
				{	
					this.rowD = row;
					this.colD = col;
					firstClick = true;
					player.setAdvancedMoveValue(this.rowD, this.colD, this.row, this.col);
					decideMakeManualMove(this.player);
				}
			}
			checkSwitcher();
		}
		else
		{
			firstClick = true;
		}
	}

	/**
	 * Activates the board on a view
	 */
	@Override
	protected void activateBoard()
	{
		activeBoard = true;
		if(!this.startSwitching)
		addMsg("Click on an empty position to play");
		else
		addMsg("Click on the piece you want to move, then on the destination");
	}

	/**
	 * Deactivates the board on a view
	 */
	@Override
	protected void deActivateBoard()
	{
		activeBoard = false;
	}
	
	/**
	 * Checks whether the moves should be simple or complex
	 */
	protected void checkSwitcher()
	{
		int max;
		if(MainConnection.isMultiviews())
		{
			max = 3;
		}
		else
		{
			max = 6;
		}
		this.counter++;
		if(this.counter >= max && first) {
			this.startSwitching = true;
			addMsg("6 pieces have been placed already");
			first = false;
		}
	}
	
	/**
	 * 
	 * @return Returns the state of the variable startSwitching
	 */
	public boolean getSwitching()
	{
		return this.startSwitching;
	}
}