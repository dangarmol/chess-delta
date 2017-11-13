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
		
		ChessBoard chessBoard = (ChessBoard) board;
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
				throw new GameError("Piece type not recognised!");
			}
		} else {
			throw new GameError("You can only move your own pieces!!!");
		}
	}
	
	/**
	 * Shows whether the current player is moving his own pieces.
	 * @param board
	 * @return
	 */
	public boolean checkTurn(ChessBoard board) { //TODO Check this function
		//this.getPiece() returns the piece to which the move belongs!
		return (this.getPiece().getId() == "White" && (board.getChessPosition(this.row, this.col)).getWhite()) ||
				(this.getPiece().getId() == "Black" && !(board.getChessPosition(this.row, this.col)).getWhite());
	}
	
	public void executePawnMove(ChessBoard board, List<Piece> pieces) {
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
				(this.colDes == this.col + 2 || this.colDes == this.col - 2))) {
				if(board.getPosition(this.rowDes, this.colDes) == null) {
					board.setPosition(this.rowDes, this.colDes, board.getChessPosition(this.row, this.col));
					deleteMovedPiece(this.row, this.col, board);
				} else if((board.getChessPosition(this.rowDes, this.colDes).getWhite() && (this.getPiece().getId() == "White")) ||
						((!board.getChessPosition(this.rowDes, this.colDes).getWhite() && (this.getPiece().getId() == "Black")))) { //TODO Check this
					if(board.getChessPosition(this.rowDes, this.colDes) instanceof King) {
						throw new GameError("Checkmate? This point should be unreachable.");
					}
					throw new GameError("Destination position already occupied by an ally piece!");
				} else {
					board.setPosition(this.rowDes, this.colDes, board.getChessPosition(this.row, this.col));
					deleteMovedPiece(this.row, this.col, board);
				}
			} else {
				throw new GameError("Invalid movement, try again.");
			}
	}
	
	public void executeBishopMove(ChessBoard board, List<Piece> pieces) {
	
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
	public GameMove fromString(Piece p, String str) {
		// TODO Remove
		return null;
	}
}
