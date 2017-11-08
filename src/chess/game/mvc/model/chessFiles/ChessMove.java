package chess.game.mvc.model.chessFiles;

import java.util.List;

import chess.game.mvc.model.chessPieces.ChessPiece;
import chess.game.mvc.model.chessPieces.chessPiecesImp.Bishop;
import chess.game.mvc.model.chessPieces.chessPiecesImp.King;
import chess.game.mvc.model.chessPieces.chessPiecesImp.Knight;
import chess.game.mvc.model.chessPieces.chessPiecesImp.Pawn;
import chess.game.mvc.model.chessPieces.chessPiecesImp.Queen;
import chess.game.mvc.model.chessPieces.chessPiecesImp.Rook;
import chess.game.mvc.model.genericGameFiles.Board;
import chess.game.mvc.model.genericGameFiles.GameError;
import chess.game.mvc.model.genericGameFiles.GameMove;
import chess.game.mvc.model.genericGameFiles.Piece;

public class ChessMove extends GameMove {

	protected int row;
	protected int col;
	protected int rowDes;
	protected int colDes;
	
	/** Default constructor.
	 *
	 **/
	public ChessMove() {}
	
	public ChessMove(int row, int col,int rowDes, int colDes, Piece p) {
		super(p);
		this.row = row;
		this.col = col;
		this.rowDes = rowDes;
		this.colDes = colDes;
	}
	
	private static final long serialVersionUID = 273689252496184587L;
	
	//Checks if the move can be made and, if so, moves the piece to the specified position.
	public void execute(Board board, List<Piece> pieces) {
		//Changed from this.getPiece() instanceof Rook
		//this.getPiece() now returns the player that made the move.
		//Check that the player moves his own piece
		if(checkTurn(board)) { //Checks that the player is trying to move his own piece.
			if(board.getPosition(this.row, this.col) instanceof Pawn) {
				executePawnMove(board, pieces);
			} else if (board.getPosition(this.row, this.col) instanceof Rook) {
				executeRookMove(board, pieces);
			} else if (board.getPosition(this.row, this.col) instanceof Knight) {
				executeKnightMove(board, pieces);
			} else if (board.getPosition(this.row, this.col) instanceof Bishop) {
				executeBishopMove(board, pieces);
			} else if (board.getPosition(this.row, this.col) instanceof Queen) {
				executeQueenMove(board, pieces);
			} else if (board.getPosition(this.row, this.col) instanceof King) {
				executeKingMove(board, pieces);
			} else {
				throw new GameError("Piece type not recognised!");
			}
		} else {
			throw new GameError("You can only move your own pieces!!!");
		}
		
		/*
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
				//addPieceToCounter(board, currentPiece); 
				//Transforms pieces around
				//transformPiecesAround(board, pieces, rowDes, colDes);
			}
			else if(moveRadius == 2) //Long move (does not duplicate)
			{
				board.setPosition(rowDes, colDes, getPiece());
				//Copies and transforms pieces around.
				//transformPiecesAround(board, pieces, rowDes, colDes);
				//Deletes the piece to "move" it
				deletePiece(row, col, board);
			}
			else if (moveRadius > 2)
			{
				throw new GameError("You can move a maximum of 2 tiles away, not further!!!");
			}
		}*/
	}
	
	/**
	 * Shows whether the current player is moving his own pieces.
	 * @param board
	 * @return
	 */
	public boolean checkTurn(Board board) {
		return (this.getPiece().getId() == "White" && ((ChessPiece) board.getPosition(this.row, this.col)).getWhite()) ||
				(this.getPiece().getId() == "Black" && !((ChessPiece) board.getPosition(this.row, this.col)).getWhite());
	}
	
	public void executePawnMove(Board board, List<Piece> pieces) {
		if(this.getPiece().getId() == "White")
			executeWhitePawnMove(board, pieces);
		else
			executeBlackPawnMove(board, pieces);
	}
	
	//TODO Check arguments
	/**
	 * Due to the peculiar pattern of pawn movement, it is required to have two
	 * different functions, since white pawns move upwards, and black ones move downwards.
	 * @param board
	 * @param pieces
	 */
	private void executeWhitePawnMove(Board board, List<Piece> pieces) {
		//Check if this pawn is trying to capture a piece. (Diagonal move)
		if(this.rowDes == this.row - 1 && (this.colDes == this.col + 1 || this.colDes == this.col - 1)) {
			
		} else if(this.col == this.colDes && this.row - 1 == this.rowDes) { //Check if it's making a simple move
			
		} else if(this.col == this.colDes && this.row - 2 == this.rowDes) { //Check if it's making an opening move (Double).
			//TODO Check En Passant.
			
		} else { //The move is not valid.
			throw new GameError("Invalid move, try again.");
		}
	}
	
	private void checkPromotion(Board board, List<Piece> pieces) {
		
	}
	
	private void executeBlackPawnMove(Board board, List<Piece> pieces) {
		//Check that player makes a valid move
				//if(captures something)
					//
				//else
					//
	}

	public void executeRookMove(Board board, List<Piece> pieces) {
		
	}
	
	public void executeKnightMove(Board board, List<Piece> pieces) {
		if(((this.colDes == this.col + 1 || this.colDes == this.col - 1) &&
			(this.rowDes == this.row + 2 || this.rowDes == this.row - 2)) ||
			((this.rowDes == this.row + 1 || this.rowDes == this.row - 1) &&
			(this.colDes == this.col + 2 || this.colDes == this.col - 2))) {
			board.setPosition(this.rowDes, this.colDes, this.getPiece());
			deleteMovedPiece(this.row, this.col, board);
		}
	}
	
	public void executeBishopMove(Board board, List<Piece> pieces) {
	
	}

	public void executeQueenMove(Board board, List<Piece> pieces) {
	
	}
	
	public void executeKingMove(Board board, List<Piece> pieces) {
	
	}
	
	//Deletes a piece (used after copying it to a new cell)
	private void deleteMovedPiece(int row, int col, Board board) {
		board.setPosition(row, col, null);
	}
	
	/*private void transformPiecesAround(Board board, List<Piece> pieces, int rowDes, int colDes){
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
	}*/
	
	/*private void addPieceToCounter(Board board, Piece currentPiece){
		int counter = board.getPieceCount(currentPiece);
		board.setPieceCount(currentPiece, counter + 1);
	}*/
	
	/*private void removePieceFromCounter(Board board, Piece enemy){
		int counter = board.getPieceCount(enemy);
		board.setPieceCount(enemy, counter - 1);
	}*/

	/** Creates a new instance of a ChessMove with parameters row, col, p.
	 * */
	protected GameMove createMove(int row, int col, int rowDes, int colDes, Piece p) {
		return new ChessMove(row, col, rowDes, colDes, p);
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

	@Override
	public GameMove fromString(Piece p, String str) {
		// TODO Remove
		return null;
	}

}
