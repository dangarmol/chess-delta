package chess.practicaAtaxx.ataxx;

import java.util.List;

import chess.basecode.bgame.model.Board;
import chess.basecode.bgame.model.GameError;
import chess.basecode.bgame.model.GameMove;
import chess.basecode.bgame.model.Piece;

public class AtaxxMove extends GameMove {

	protected int row;
	protected int col;
	protected int rowDes;
	protected int colDes;
	
	/** Default constructor.
	 * */
	public AtaxxMove() {}
	
	public AtaxxMove(int row, int col,int rowDes, int colDes, Piece p) {
		super(p);
		this.row = row;
		this.col = col;
		this.rowDes = rowDes;
		this.colDes = colDes;
	}
	
	private static final long serialVersionUID = 273689252496184587L;
	
	//Checks if the move can be made and, if so, moves the piece to the specified position.
	public void execute(Board board, List<Piece> pieces) {
		//Check that the player moves his own piece
		if(!getPiece().equals(board.getPosition(row, col)))
		{
			throw new GameError("You can only move your own pieces!!!");			
		}
		else if (board.getPosition(row, col) != null && board.getPosition(rowDes, colDes) != null) //Player needs to choose an empty tile
		{
			throw new GameError("Position (" + rowDes + "," + colDes + ") is already occupied!!!");			
		}
		else
		{
			Piece currentPiece = board.getPosition(row, col);
			int moveRadius = getDistance(row, col, rowDes, colDes); //Check distance
			if(moveRadius == 0) //Can't choose the initial tile
			{
				throw new GameError("Destination coordinates cannot be the same as the initial ones!!!");
			}
			else if(moveRadius == 1) //If moved only one, it duplicates
			{
				board.setPosition(rowDes, colDes, getPiece()); //Copy the piece, not move
				//Counter increments
				addPieceToCounter(board, currentPiece); 
				//Transforms pieces around
				transformPiecesAround(board, pieces, rowDes, colDes);
			}
			else if(moveRadius == 2) //Long move (does not duplicate)
			{
				board.setPosition(rowDes, colDes, getPiece());
				//Copies and transforms pieces around.
				transformPiecesAround(board, pieces, rowDes, colDes);
				//Deletes the piece to "move" it
				deletePiece(row, col, board);
			}
			else if (moveRadius > 2)
			{
				throw new GameError("You can move a maximum of 2 tiles away, not further!!!");
			}
		}
	}
	
	//Deletes a piece after copying it (when making a long move)
	private void deletePiece(int row, int col, Board board)
	{
		board.setPosition(row, col, null);
	}
	
	//How long is the selected move?
	private int getDistance(int row, int col, int rowDes, int colDes){	
		int compareRow = Math.abs(this.row - this.rowDes);
		int compareCol = Math.abs(this.col - this.colDes);
		return  Math.max(compareRow, compareCol);		
	}
	
	private void transformPiecesAround(Board board, List<Piece> pieces, int rowDes, int colDes){
		Piece currentPiece = board.getPosition(rowDes, colDes);
		Piece auxPiece;
		
		for(int i = Math.max(rowDes - 1, 0); i <= Math.min(rowDes + 1, board.getRows() - 1); i++){
			for(int j = Math.max(colDes - 1, 0); j <= Math.min(colDes + 1, board.getCols() - 1); j++){ //Goes all around the current piece
				
				if(board.getPosition(i, j) != null){ //If it is not empty...					
					auxPiece = board.getPosition(i, j); //Copy the piece to compare
					if(!auxPiece.getId().equals("*")){ //If it is not a wall...
						if(!currentPiece.equals(auxPiece)){ //If it is not already yours...
							removePieceFromCounter(board, board.getPosition(i, j)); //Removes one from the enemy counter
							deletePiece(i, j, board); //Delete enemy piece
							
							addPieceToCounter(board, currentPiece); //Add one to the player counter
							board.setPosition(i, j, currentPiece); //Insert current piece
						}
					}					
				}
			}
		}
	}
	
	private void addPieceToCounter(Board board, Piece currentPiece){
		int counter = board.getPieceCount(currentPiece);
		board.setPieceCount(currentPiece, counter + 1);
	}
	
	private void removePieceFromCounter(Board board, Piece enemy){
		int counter = board.getPieceCount(enemy);
		board.setPieceCount(enemy, counter - 1);
	}

	//Creates a move from the string introduced by the user.
	public GameMove fromString(Piece p, String str) {
		String[] words = str.split(" ");
		if (words.length != 4) //2 first numbers = Origin, 2 last numbers = Destination
		{
			return null;
		}

		try {//A�adido el parseo del destino
			int row, col, rowDes, colDes;
			row = Integer.parseInt(words[0]);
			col = Integer.parseInt(words[1]);
			rowDes = Integer.parseInt(words[2]);
			colDes = Integer.parseInt(words[3]);
			
			return createMove(row, col, rowDes, colDes, p);
		} catch (NumberFormatException e) {
			return null;
		}
	}

	/** Creates a new instance of AtaxxMove with parameters row, col, p.
	 * */
	protected GameMove createMove(int row, int col, int rowDes, int colDes, Piece p) {
		return new AtaxxMove(row, col, rowDes, colDes, p);
	}

	/** Sends a help message to help the user.
	 * */
	public String help() {
		return "'Xrow Xcolumn Yrow Ycolumn', to move a piece from X to Y.";
	}
	
	/** Notifies the user if they are using the wrong instructions.
	 * */
	public String toString() {
		if (getPiece() == null) {
			return help();
		} else {
			return "Place a piece '" + getPiece() + "' at (" + row + "," + col + ")";
		}
	}

}
