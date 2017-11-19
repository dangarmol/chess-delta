package chess.game.mvc.model.chessFiles;

import java.util.List;

import javax.swing.JFrame;

import chess.game.mvc.model.chessPieces.ChessPiece;
import chess.game.mvc.model.chessPieces.ChessPieceID;
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
import chess.game.mvc.view.genericViews.ColorChooser;

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
		
		if(this.row == this.rowDes && this.col == this.colDes)
			throw new GameError("You cannot move to the same position you were in!!! (Error 014)"); //Works
		
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
				throw new GameError("Piece type not recognised! This should be unreachable. (Error 013)");
			}
		} else {
			throw new GameError("You can only move your own pieces!!! (Error 012)"); //Works properly
		}
		
		//throw new GameError("Movement worked."); //TODO Delete this!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!
		
		//board = this.chessBoard; //TODO Is this necessary? ASK.
	}
	
	//Simply executes a move that has been previously checked and is legal.
	public void executeCheckedMove(ChessBoard board) { 
		board.setPosition(this.rowDes, this.colDes, board.getChessPosition(this.row, this.col));
		deleteMovedPiece(this.row, this.col, board);
	}
	
	public void executeCaptureMove(ChessBoard board) {
		try {
			if((board.getChessPosition(this.rowDes, this.colDes).getWhite() && (this.getPiece().getWhite())) ||
					((!board.getChessPosition(this.rowDes, this.colDes).getWhite() && (!this.getPiece().getWhite())))) {
					//If the player tried to capture an ally piece.
				throw new GameError("Destination position already occupied by an ally piece! (Error 011)");
			} else { //If he's capturing an enemy piece.
				if(board.getChessPosition(this.rowDes, this.colDes) instanceof King) { //You can't capture the king!
					throw new GameError("Checkmate? This point should be unreachable. (Error 010)");
				} else {
					executeCheckedMove(board);
				}
			}
		} catch (Exception e) {
			throw new GameError("This method has probably been called in the wrong place. This should never be reached. (Error 009)");
		}
	}
	
	public void disableEnPassant(boolean white) {
		//TODO Disable En Passant for every Pawn from the colour passed by parameter.
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
	
	//TODO Check arguments
		/**
		 * Due to the peculiar pattern of pawn movement, it is required to have two
		 * different functions, since white pawns move upwards, and black ones move downwards.
		 * @param board
		 * @param pieces
		 */
	public void executePawnMove(ChessBoard board, List<Piece> pieces) {
		if(this.getPiece().getWhite())
			executeWhitePawnMove(board, pieces);
		else
			executeBlackPawnMove(board, pieces);
	}
	
	private void executeWhitePawnMove(ChessBoard board, List<Piece> pieces) { //TODO Found some bugs that need solving in diagonal moves.
		//Check if this pawn is trying to capture a piece. (Diagonal move)
		if(this.rowDes == this.row - 1 && (this.colDes == this.col + 1 || this.colDes == this.col - 1)) {
			if(board.getChessPosition(this.rowDes, this.colDes) != null) { //The destination position isn't empty
				executeCaptureMove(board); //This already checks the kind of piece that is in the destination position
				//and if it can be captured.
			} else if((board.getChessPosition(this.row, this.col + 1) instanceof Pawn) && (this.colDes == this.col + 1) || //There is a pawn on the right
					(board.getChessPosition(this.row, this.col - 1) instanceof Pawn) && (this.colDes == this.col - 1)) { //or the left for En Passant capture
				//No need to check that destination is null, that's checked above.
				if(this.colDes == this.col + 1) { //Capture to the right
					if(!board.getChessPosition(this.row, this.col + 1).getWhite() && //Check that the pawn is black
							((Pawn) board.getChessPosition(this.row + 1, this.col)).getPassant()) { //and it can be captured En Passant for this turn.						
						executeCheckedMove(board);
						board.setPosition(this.row, this.col + 1, null);
					} else { //Trying to capture either your own pawn or a pawn that can't be captured En Passant
						throw new GameError("Invalid move, try again. (Error 001)");
					}
				} else if (this.colDes == this.col - 1) { //Capture to the left.
					if(!board.getChessPosition(this.row, this.col - 1).getWhite() && //Check that the pawn is black
							((Pawn) board.getChessPosition(this.row, this.col - 1)).getPassant()) { //and it can be captured En Passant for this turn.
						executeCheckedMove(board);
						deleteMovedPiece(this.row - 1, this.col, board);
						board.setPosition(this.row, this.col + 1, null);
					} else { //Trying to capture own piece En Passant
						throw new GameError("Invalid move, try again. (Error 002)");
					}
				} else { //Unrecognised En Passant move (not left nor right?)
					throw new GameError("Invalid move, try again. (Error 003)");
				}
			} else { //Pawn invalid diagonal move.
				throw new GameError("Invalid move, try again. (Error 004)");
			}
		} else if(this.col == this.colDes && this.row - 1 == this.rowDes) { //Check if it's making a simple move
			if(board.getChessPosition(this.rowDes, this.colDes) == null) { //Working.
				executeCheckedMove(board);
			} else {
				throw new GameError("Invalid move, try again. (Error 005)");
			}
		} else if(this.col == this.colDes && this.row - 2 == this.rowDes) { //Check if it's making an opening move (Double).
			//TODO Check En Passant.
			if(board.getChessPosition(this.rowDes, this.colDes) != null) {
				throw new GameError("Invalid move, the position is occupied, try again. (Error 006)");
			} else if (!checkPiecesInbetween(this.row, this.col, this.rowDes, this.colDes)) {
				throw new GameError("Invalid move, you can't skip through other pieces, try again. (Error 007)");
			} else {
				executeCheckedMove(board);

				((Pawn) board.getChessPosition(this.rowDes, this.colDes)).setPassant(true); //This pawn can be captured En Passant in the next move.
			}
		} else { //The move is not valid.
			throw new GameError("Invalid move, try again. (Error 008)");
		}
		
		//You can only get here if everything went right during the execution of the move.
		if(checkPromotion(board)) {
			int chosenPiece = -1;
			ChessPiece newPiece;
			ChessPawnPromotionDialog dialog = new ChessPawnPromotionDialog("Select the piece you would like:", true);
			
			//https://stackoverflow.com/questions/5472868/how-to-pause-program-until-a-button-press
			//TODO This needs to sleep or something similar until the piece is chosen.
			chosenPiece = dialog.getChosenPiece();
			
			switch(chosenPiece) {
				case ChessPieceID.WHITE_ROOK:
					newPiece = new Rook(true, false);
					break;
				case ChessPieceID.WHITE_KNIGHT:
					newPiece = new Knight(true);
					break;
				case ChessPieceID.WHITE_BISHOP:
					newPiece = new Bishop(true);
					break;
				case ChessPieceID.WHITE_QUEEN:
					newPiece = new Queen(true);
					break;
				default:
					throw new GameError("Invalid piece for promotion chosen.");
			}
			executePromotion(board, newPiece);
		}
	}
	
	private void executeBlackPawnMove(ChessBoard board, List<Piece> pieces) {
		
	}
	
	private boolean checkPromotion(ChessBoard board) { //TODO Check this function. It might not work as intended.
													  //Maybe should check the piece and not the position? Maybe ChessBoard?
		return (board.getChessPosition(this.rowDes, this.colDes).getWhite() && this.rowDes == 0) || //If it's white and has reached the top
				(!board.getChessPosition(this.rowDes, this.colDes).getWhite() && this.rowDes == 7); //If it's black and has reached the bottom
	}
	
	private void executePromotion(ChessBoard board, ChessPiece p) { //The piece must be properly created in another function. Usually executeWhite/BlackPawnMove().
		board.setPosition(this.rowDes, this.colDes, p);
	}

	public void executeRookMove(ChessBoard board, List<Piece> pieces) {
		
	}
	
	public void executeKnightMove(ChessBoard board, List<Piece> pieces) {
		if(((this.colDes == this.col + 1 || this.colDes == this.col - 1) &&
			(this.rowDes == this.row + 2 || this.rowDes == this.row - 2)) ||
			((this.rowDes == this.row + 1 || this.rowDes == this.row - 1) &&
			(this.colDes == this.col + 2 || this.colDes == this.col - 2))) { //Check if the movement is legal.
			if(board.getPosition(this.rowDes, this.colDes) == null) { //If the destination position is empty.
				executeCheckedMove(board);
			} else { //If the piece is trying to capture a piece.
				executeCaptureMove(board);
			}
		} else { //The movement is illegal.
			throw new GameError("Invalid movement, try again. (Error 015)");
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
			} else { //colIni < colEnd (colIni always != colEnd at this point!) since both parameters can't be equal!
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
	//The directions are shown on at the checkDirection() function above.
	private boolean checkPiecesInbetween(int rowIni, int colIni, int rowEnd, int colEnd) {
		int rowOffset, colOffset;
		switch(checkDirection(rowIni, colIni, rowEnd, colEnd)) {
			case 0:
				rowOffset = -1; colOffset = -1; break;
			case 1:
				rowOffset = -1; colOffset = 0; break;
			case 2:
				rowOffset = -1; colOffset = 1; break;
			case 3:
				rowOffset = 0; colOffset = 1; break;
			case 4:
				rowOffset = 1; colOffset = 1; break;
			case 5:
				rowOffset = 1; colOffset = 0; break;
			case 6:
				rowOffset = 1; colOffset = -1; break;
			case 7:
				rowOffset = 0; colOffset = -1; break;
			default:
				throw new GameError("Invalid move direction, this should be unreachable. (Error 016)");
		}
		
		int itCount = 0; //In case any error happens, this avoids an infinite loop.
		int rowIt = rowIni, colIt = colIni;
		
		while(itCount < 10 && (rowIt != rowEnd && colIt != colEnd)) {
			rowIt += rowOffset;
			colIt += colOffset;
			
			if(rowIt == rowEnd && colIt == colEnd) break; //Destination position should not be checked so you can capture pieces.
			
			if(chessBoard.getPosition(row, col) != null) //If it found a piece on the way.
				return false;
			
			itCount++;
		}
		
		if(itCount >= 10) 
			throw new GameError("Move direction bugged. This should never happen. (Error 017)");
		
		return true;
	} //If returned false, throw an exception from the suitable function.
	
	public void executeBishopMove(ChessBoard board, List<Piece> pieces) { //TODO Next to do.

	}

	public void executeQueenMove(ChessBoard board, List<Piece> pieces) {
	
	}
	
	public void executeKingMove(ChessBoard board, List<Piece> pieces) {
	
	}
	
	//Deletes a piece (used after copying it to a new cell)
	private void deleteMovedPiece(int row, int col, Board board) { //Working properly.
		if(board.getPosition(row, col) == null) {
			throw new GameError("Tried to delete an empty cell, there might be an error in the code. (Error 018)");
		} else {
			board.setPosition(row, col, null);
		}
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
