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
import chess.game.mvc.model.genericGameFiles.FiniteRectBoard;
import chess.game.mvc.model.genericGameFiles.GameError;
import chess.game.mvc.model.genericGameFiles.GameMove;
import chess.game.mvc.model.genericGameFiles.Piece;

public class ChessMove extends GameMove {

	protected int row;
	protected int col;
	protected int rowDes;
	protected int colDes;
	
	private ChessPiece chessPiece;
	private ChessBoard chessBoard;
	
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
		this.chessPiece = (ChessPiece) p;
	}
	
	private static final long serialVersionUID = 273689252496184587L;
	
	//Checks if the move can be made and, if so, moves the piece to the specified position.
	public void execute(Board board, List<Piece> pieces) {
		//Changed from this.getPiece() instanceof Rook
		//this.getPiece() now returns the player that made the move.
		//Check that the player moves his own piece
		
		this.chessBoard = (ChessBoard) board;
		//https://stackoverflow.com/questions/907360/explanation-of-classcastexception-in-java
		
		//board vs chessBoard issue?
		if(checkTurn(chessBoard)) { //Checks that the player is trying to move his own piece.
			if(chessBoard.getPosition(this.row, this.col) instanceof Pawn) {
				executePawnMove(chessBoard, pieces);
			} else if (chessBoard.getPosition(this.row, this.col) instanceof Rook) {
				executeRookMove(chessBoard, pieces);
			} else if (chessBoard.getPosition(this.row, this.col) instanceof Knight) {
				executeKnightMove(chessBoard, pieces);
			} else if (chessBoard.getPosition(this.row, this.col) instanceof Bishop) {
				executeBishopMove(chessBoard, pieces);
			} else if (chessBoard.getPosition(this.row, this.col) instanceof Queen) {
				executeQueenMove(chessBoard, pieces);
			} else if (chessBoard.getPosition(this.row, this.col) instanceof King) {
				executeKingMove(chessBoard, pieces);
			} else {
				throw new GameError("Piece type not recognised! This should be unreachable");
			}
		} else {
			throw new GameError("You can only move your own pieces!!!"); //Working
		}
	}
	
	/**
	 * Shows whether the current player is moving his own pieces.
	 * @param board
	 * @return
	 */
	public boolean checkTurn(ChessBoard board) { //TODO Check this function
		//this.getPiece() returns the piece to which the move belongs!
		return ((this.getPiece().getWhite() && (board.getChessPosition(this.row, this.col)).getWhite()) ||
				(!this.getPiece().getWhite() && !(board.getChessPosition(this.row, this.col)).getWhite()));
	}
	
	public void executePawnMove(ChessBoard board, List<Piece> pieces) {
		if(this.getPiece().getWhite())
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
	private void executeWhitePawnMove(ChessBoard board, List<Piece> pieces) {
		//Check if this pawn is trying to capture a piece. (Diagonal move)
		if(this.rowDes == this.row - 1 && (this.colDes == this.col + 1 || this.colDes == this.col - 1)) {
			
		} else if(this.col == this.colDes && this.row - 1 == this.rowDes) { //Check if it's making a simple move
			
		} else if(this.col == this.colDes && this.row - 2 == this.rowDes) { //Check if it's making an opening move (Double).
			//TODO Check En Passant.
			
		} else { //The move is not valid.
			throw new GameError("Invalid move, try again.");
		}
	}
	
	private void checkPromotion(ChessBoard board, List<Piece> pieces) {
		
	}
	
	private void executeBlackPawnMove(ChessBoard board, List<Piece> pieces) {
		//Check that player makes a valid move
				//if(captures something)
					//
				//else
					//
	}

	public void executeRookMove(ChessBoard board, List<Piece> pieces) {
		
	}
	
	public void executeKnightMove(ChessBoard board, List<Piece> pieces) {
		if(((this.colDes == this.col + 1 || this.colDes == this.col - 1) &&
			(this.rowDes == this.row + 2 || this.rowDes == this.row - 2)) ||
			((this.rowDes == this.row + 1 || this.rowDes == this.row - 1) &&
			(this.colDes == this.col + 2 || this.colDes == this.col - 2))) { //Check the movement is legal.
			if(board.getPosition(this.rowDes, this.colDes) == null) { //If the destination position is empty.
				board.setPosition(this.rowDes, this.colDes, board.getChessPosition(this.row, this.col));
				deleteMovedPiece(this.row, this.col, board);
			} else if((board.getChessPosition(this.rowDes, this.colDes).getWhite() && (this.getPiece().getWhite())) ||
					((!board.getChessPosition(this.rowDes, this.colDes).getWhite() && (!this.getPiece().getWhite())))) {
					//If the player tried to capture an ally piece.
				if(board.getChessPosition(this.rowDes, this.colDes) instanceof King) { //You can't capture the king!
					throw new GameError("Checkmate? This point should be unreachable.");
				}
				throw new GameError("Destination position already occupied by an ally piece!");
			} else { //If he's capturing an enemy piece.
				board.setPosition(this.rowDes, this.colDes, board.getChessPosition(this.row, this.col));
				deleteMovedPiece(this.row, this.col, board);
			}
		} else { //The movement is illegal.
				throw new GameError("Invalid movement, try again.");
		}
	}
	
	/*
	 * Returns the direction of the move, being the number returned the one matching the direction
	 * at the diagram underneath.
	 * 
	 * 		0 1 2
	 * 		7 * 3
	 * 		6 5 4
	 */
	private int checkDirection(int rowIni, int colIni, int rowEnd, int colEnd) {
		if(rowIni == rowEnd) { //It's either 3 or 7
			if(colIni > colEnd) {
				return 7;
			} else { //colIni < colEnd (colIni always != colEnd at this point!)
				return 3;
			}
		} else if (rowIni > rowEnd) { //It's either 0, 1 or 2
			if(colIni == colEnd) {
				return 1;
			} else if(colIni > colEnd) {
				return 0;
			} else { //colIni < colEnd
				return 2;
			}
		} else { //rowIni < rowEnd //It's either 4, 5 or 6
			if(colIni == colEnd) {
				return 5;
			} else if(colIni > colEnd) {
				return 6;
			} else { //colIni < colEnd
				return 4;
			}
		}
	}
	
	//Check if there are any pieces between the positions selected (for bishops, rooks and queens)
	//True means that the move can be performed, false means that there are pieces inbetween.
	private boolean checkPiecesInbetween(int rowIni, int colIni, int rowEnd, int colEnd) { //TODO Create this
		int direction = checkDirection(rowIni, colIni, rowEnd, colEnd);
		
		return false;
	} //If returned false, throw an exception from the suitable function.
	
	public void executeBishopMove(ChessBoard board, List<Piece> pieces) { //TODO Next to do.
		if(((this.colDes == this.col + 1 || this.colDes == this.col - 1) &&
			(this.rowDes == this.row + 2 || this.rowDes == this.row - 2)) ||
			((this.rowDes == this.row + 1 || this.rowDes == this.row - 1) &&
			(this.colDes == this.col + 2 || this.colDes == this.col - 2))) { //Check the movement is legal.
			if(board.getPosition(this.rowDes, this.colDes) == null) { //If the destination position is empty.
				board.setPosition(this.rowDes, this.colDes, board.getChessPosition(this.row, this.col));
				deleteMovedPiece(this.row, this.col, board);
			} else if((board.getChessPosition(this.rowDes, this.colDes).getWhite() && (this.getPiece().getWhite())) ||
					((!board.getChessPosition(this.rowDes, this.colDes).getWhite() && (!this.getPiece().getWhite())))) {
					//If the player tried to capture an ally piece.
				if(board.getChessPosition(this.rowDes, this.colDes) instanceof King) { //You can't capture the king!
					throw new GameError("Checkmate? This point should be unreachable.");
				}
				throw new GameError("Destination position already occupied by an ally piece!");
			} else { //If he's capturing an enemy piece.
				board.setPosition(this.rowDes, this.colDes, board.getChessPosition(this.row, this.col));
				deleteMovedPiece(this.row, this.col, board);
			}
		} else { //The movement is illegal.
				throw new GameError("Illegal movement, try again.");
		}
	}

	public void executeQueenMove(ChessBoard board, List<Piece> pieces) {
	
	}
	
	public void executeKingMove(ChessBoard board, List<Piece> pieces) {
	
	}
	
	//Deletes a piece (used after copying it to a new cell)
	private void deleteMovedPiece(int row, int col, Board board) {
		board.setPosition(row, col, null);
	}

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
	public ChessPiece getPiece() {
		return this.chessPiece;
	}

	@Override
	public GameMove fromString(Piece p, String str) {
		// TODO Remove
		return null;
	}
}
