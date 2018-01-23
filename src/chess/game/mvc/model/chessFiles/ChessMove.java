package chess.game.mvc.model.chessFiles;

import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.List;

import chess.game.mvc.controller.Controller;
import chess.game.mvc.model.chessPieces.ChessPiece;
import chess.game.mvc.model.chessPieces.ChessPieceID;
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

/**
 * Although this class is completely full of comments, just don't touch any algorithm nor try to change anything unless you're sure
 * of what you're doing and it's really necessary.
 * @author Daniel
 */
public class ChessMove extends GameMove {

	private int row;
	private int col;
	private int rowDes;
	private int colDes;
	
	private boolean actionMove; //If it is a pawn move or a capture move, for the 50 moves rule.
	private boolean testMove; //If it is a test move and will be reversed/ignored.
	
	private ChessPiece chessPiece;
	private ChessBoard chessBoard;
	
	public ChessMove() {}
	
	public ChessMove(int row, int col,int rowDes, int colDes, Piece p) {
		super(p);
		this.row = row;
		this.col = col;
		this.rowDes = rowDes;
		this.colDes = colDes;
		this.chessPiece = (ChessPiece) p;
		this.actionMove = false;
		this.testMove = false;
	}
	
	private static final long serialVersionUID = 273689252496184587L;
	
	//Checks if the move can be made and, if so, moves the piece to the specified position.
	public void execute(Board board, List<Piece> playersPieces, List<Piece> chessPieces) {
		//Changed from this.getPiece() instanceof Rook
		//this.getPiece() now returns the player that made the move.
		//Check that the player moves his own piece
		
		this.chessBoard = (ChessBoard) board;
		this.actionMove = false;
		
		ChessBoard originalBoard = this.chessBoard.copyChessBoard();
		
		if(this.chessBoard.getChessPosition(this.row, this.col) == null)
			throw new GameError("You need to select one of your pieces to perform the move!");
		
		if(!checkTurn(this.chessBoard)) {//Checks that the player is trying to move his own piece.
			String color = "";
			if(this.getPiece().getWhite())
				color = "WHITE";
			else
				color = "BLACK";
			throw new GameError("You can only move your own pieces. It's the turn for " + color + ". Try again."); //Works properly
		}
		
		if(this.row == this.rowDes && this.col == this.colDes)
			throw new GameError("You cannot move a piece to the same position it was before. Try again.");
			
		if(this.chessBoard.getPosition(this.row, this.col) instanceof Pawn) {
			executePawnMove(this.chessBoard);
			this.actionMove = true; //This is used for the 50 moves rule
		} else if (this.chessBoard.getPosition(this.row, this.col) instanceof Rook) {
			executeRookMove(this.chessBoard);
		} else if (this.chessBoard.getPosition(this.row, this.col) instanceof Knight) {
			executeKnightMove(this.chessBoard);
		} else if (this.chessBoard.getPosition(this.row, this.col) instanceof Bishop) {
			executeBishopMove(this.chessBoard);
		} else if (this.chessBoard.getPosition(this.row, this.col) instanceof Queen) {
			executeQueenMove(this.chessBoard);
		} else if (this.chessBoard.getPosition(this.row, this.col) instanceof King) {
			executeKingMove(this.chessBoard);
		} else {
			throw new GameError("Piece type not recognised! This should be unreachable.");
		}
		
		if(isKingInCheck(this.chessBoard, this.getPiece().getWhite())) { //If the King is in Check
			revertBoard(this.chessBoard, originalBoard);
			if(isKingInCheck(originalBoard, this.getPiece().getWhite())) { //If the King was in Check before the move.
				throw new GameError("You cannot perform that move, your King is in Check!!!");
			} else { //If the King would be in check after the move.
				throw new GameError("You cannot perform that move, your King would be in Check after that!!!");
			}
		}
		
		//If the code reaches this point, the move has been successfully executed.
		
		disableEnPassant(!this.getPiece().getWhite()); //Disable En passant for every pawn of the colour that is going
		//to move next turn, since it has been more than a move ago that he moved his pieces.
		
		if(actionMove && !testMove) {
			ChessConstants.movesWithoutAction = 0;
		} else {
			ChessConstants.movesWithoutAction++;
		}
	}
	
	//Used by ChessRules to get the list of valid moves.
	public boolean isMoveLegal(ChessBoard testBoard) {
		try { //TODO Check Pawn promotion here!! (?)
			if (this.colDes > ChessConstants.MAX_DIM || this.rowDes > ChessConstants.MAX_DIM ||
					this.colDes < ChessConstants.MIN_DIM || this.rowDes < ChessConstants.MIN_DIM) //First checks the range of the move.
				return false;
			
			this.testMove = true;
			this.executeFakeMove(testBoard);
			this.testMove = false;
		} catch(Exception e) {
			return false;
		}
		return true;
	}
	
	//Used by the function above to test if a move is valid or not.
	//Similar to execute, but faster to run.
	private void executeFakeMove(ChessBoard testBoard) {
		this.chessBoard = testBoard.copyChessBoard();
		
		if(this.chessBoard.getChessPosition(this.row, this.col) == null)
			throw new GameError("You need to select one of your pieces to perform the move!");
		
		if(!checkTurn(this.chessBoard)) {//Checks that the player is trying to move his own piece.
			String color = "";
			if(this.getPiece().getWhite())
				color = "WHITE";
			else
				color = "BLACK";
			throw new GameError("You can only move your own pieces. It's the turn for " + color + ". Try again."); //Works properly
		}
		
		if(this.row == this.rowDes && this.col == this.colDes)
			throw new GameError("You cannot move a piece to the same position it was before. Try again.");
			
		if(this.chessBoard.getPosition(this.row, this.col) instanceof Pawn) {
			executePawnMove(this.chessBoard);
		} else if (this.chessBoard.getPosition(this.row, this.col) instanceof Rook) {
			executeRookMove(this.chessBoard);
		} else if (this.chessBoard.getPosition(this.row, this.col) instanceof Knight) {
			executeKnightMove(this.chessBoard);
		} else if (this.chessBoard.getPosition(this.row, this.col) instanceof Bishop) {
			executeBishopMove(this.chessBoard);
		} else if (this.chessBoard.getPosition(this.row, this.col) instanceof Queen) {
			executeQueenMove(this.chessBoard);
		} else if (this.chessBoard.getPosition(this.row, this.col) instanceof King) {
			executeKingMove(this.chessBoard);
		} else {
			throw new GameError("Piece type not recognised! This should be unreachable.");
		}
		
		if(isKingInCheck(this.chessBoard, this.getPiece().getWhite())) { //If the King is in Check
			throw new GameError("You cannot perform that move, your King would be in Check after that!!!");
		}
	}
	
	//Returns the position of the king from the current player as an 2 digits integer.
	//Left digit represents row and right digit represents col. King at (2, 5), would be returned as "25".
	//Returns -1 if the king hasn't been found. An exception should be thrown if this happens.
	private int findKing(ChessBoard board, boolean isWhite) {
		for(int rowX = ChessConstants.MIN_DIM; rowX <= ChessConstants.MAX_DIM; rowX++) {
			for(int colY = ChessConstants.MIN_DIM; colY <= ChessConstants.MAX_DIM; colY++) {
				if(board.getChessPosition(rowX, colY) != null) {
					if(board.getChessPosition(rowX, colY) instanceof King && (board.getChessPosition(rowX, colY).getWhite() == isWhite)) {
						return rowX * 10 + colY;
					}
				}
			}
		}
		return ChessConstants.UNKNOWN;
	}

	//Checks if the King for the colour passed by parameter is threatened by any piece.
	public boolean isKingInCheck(ChessBoard board, boolean isWhite) {
		int kingLocation = findKing(board, isWhite);
		if(kingLocation == ChessConstants.UNKNOWN) {
			throw new GameError("Internal error. King not found, this should never happen.");
		}
		int kingRow = kingLocation / 10;
		int kingCol = kingLocation % 10;
		
		//King can't be threatened by the other King. Since a King cannot move to a vulnerable position.
		//True means King is in check, False mean King is not in check.
		return checkHorizVertThreat(board, kingRow, kingCol) || checkDiagonalThreat(board, kingRow, kingCol) ||
				checkKnightThreat(board, kingRow, kingCol) || checkPawnThreat(board, kingRow, kingCol) ||
				checkKingsCollision(board, kingRow, kingCol);
	}
	
	//Checks if the King would move to a position threatened by the enemy King.
	private boolean checkKingsCollision(ChessBoard board, int kingRow, int kingCol) {
		for(int rowX = kingRow - 1; rowX <= kingRow + 1; rowX++) {
			for(int colY = kingCol - 1; colY <= kingCol + 1; colY++) {
				if(rowX >= ChessConstants.MIN_DIM && rowX <= ChessConstants.MAX_DIM &&
						colY >= ChessConstants.MIN_DIM && colY <= ChessConstants.MAX_DIM) { //Can't check outside the board!
					if(rowX != kingRow || colY != kingCol) { //To avoid checking the position where the current king is.
						if(board.getChessPosition(rowX, colY) != null) {
							if(board.getChessPosition(rowX, colY) instanceof King) {
								return true;
							}
						}
					}
				}
			}
		}
		return false;
	}
	
	//Checks if the king is threatened from any row or column, horizontally or vertically by either a Queen or a Rook.
	private boolean checkHorizVertThreat(ChessBoard board, int kingRow, int kingCol) {
		for(int rowX = kingRow + 1; rowX <= ChessConstants.MAX_DIM; rowX++) { //South Direction
			if(board.getChessPosition(rowX, kingCol) != null) { //If the position is empty, the loop keeps running.
				if(checkHorizVertAttacker(board, rowX, kingCol, board.getChessPosition(kingRow, kingCol).getWhite()))
					return true; //If it's not empty and this function above finds an attacker on the explored position, it returns true and breaks every loop.
				else
					break; //If it's not empty and it finds a piece that isn't a threat or is an ally, it breaks the current loop and starts the next one.
			}
		}
		
		for(int rowX = kingRow - 1; rowX >= ChessConstants.MIN_DIM; rowX--) { //North Direction
			if(board.getChessPosition(rowX, kingCol) != null) { //If the position is empty, the loop keeps running.
				if(checkHorizVertAttacker(board, rowX, kingCol, board.getChessPosition(kingRow, kingCol).getWhite()))
					return true; //If it's not empty and this function above finds an attacker on the explored position, it returns true and breaks every loop.
				else
					break; //If it's not empty and it finds a piece that isn't a threat or is an ally, it breaks the current loop and starts the next one.
			}
		}
		
		for(int colY = kingCol + 1; colY <= ChessConstants.MAX_DIM; colY++) { //East Direction
			if(board.getChessPosition(kingRow, colY) != null) { //If the position is empty, the loop keeps running.
				if(checkHorizVertAttacker(board, kingRow, colY, board.getChessPosition(kingRow, kingCol).getWhite()))
					return true; //If it's not empty and this function above finds an attacker on the explored position, it returns true and breaks every loop.
				else
					break; //If it's not empty and it finds a piece that isn't a threat or is an ally, it breaks the current loop and starts the next one.
			}
		}
		
		for(int colY = kingCol - 1; colY >= ChessConstants.MIN_DIM; colY--) { //West Direction
			if(board.getChessPosition(kingRow, colY) != null) { //If the position is empty, the loop keeps running.
				if(checkHorizVertAttacker(board, kingRow, colY, board.getChessPosition(kingRow, kingCol).getWhite()))
					return true; //If it's not empty and this function above finds an attacker on the explored position, it returns true and breaks every loop.
				else
					break; //If it's not empty and it finds a piece that isn't a threat or is an ally, it breaks the current loop and returns false, since none of the loops found a threat.
			}
		}
		return false;
	}
	
	private boolean checkHorizVertAttacker(ChessBoard board, int rowX, int colY, boolean isWhiteKing) {
		return (board.getChessPosition(rowX, colY) instanceof Queen || board.getChessPosition(rowX, colY) instanceof Rook) && //Checks that there's either a Queen or a Rook
				(!board.getChessPosition(rowX, colY).getWhite() == isWhiteKing); //And that the king is not the same color as the piece.
	}
	
	//Checks if the king is threatened from any diagonal by either a Queen or a Bishop.
	private boolean checkDiagonalThreat(ChessBoard board, int kingRow, int kingCol) {
		int rowOffSet = ChessConstants.NEGATIVE, colOffSet = ChessConstants.NEGATIVE;
		int multiplier = 1;
		int rowX, colY;
		rowX = kingRow + rowOffSet * multiplier;
		colY = kingCol + colOffSet * multiplier;
		while(rowX >= ChessConstants.MIN_DIM && colY >= ChessConstants.MIN_DIM) { //NorthWest Direction
			if(board.getChessPosition(rowX, colY) != null) { //If the position is empty, the loop keeps running.
				if(checkDiagonalAttacker(board, rowX, colY, board.getChessPosition(kingRow, kingCol).getWhite()))
					return true; //If it's not empty and this function above finds an attacker on the explored position, it returns true and breaks every loop.
				else
					break; //If it's not empty and it finds a piece that isn't a threat or is an ally, it breaks the current loop and starts the next one.
			}
			multiplier++;
			rowX = kingRow + rowOffSet * multiplier;
			colY = kingCol + colOffSet * multiplier;
		}
		
		multiplier = 1; rowOffSet = ChessConstants.NEGATIVE; colOffSet = ChessConstants.POSITIVE;
		rowX = kingRow + rowOffSet * multiplier;
		colY = kingCol + colOffSet * multiplier;
		while(rowX >= ChessConstants.MIN_DIM && colY <= ChessConstants.MAX_DIM) { //NorthEast Direction
			if(board.getChessPosition(rowX, colY) != null) { //If the position is empty, the loop keeps running.
				if(checkDiagonalAttacker(board, rowX, colY, board.getChessPosition(kingRow, kingCol).getWhite()))
					return true; //If it's not empty and this function above finds an attacker on the explored position, it returns true and breaks every loop.
				else
					break; //If it's not empty and it finds a piece that isn't a threat or is an ally, it breaks the current loop and starts the next one.
			}
			multiplier++;
			rowX = kingRow + rowOffSet * multiplier;
			colY = kingCol + colOffSet * multiplier;
		}
		
		multiplier = 1; rowOffSet = ChessConstants.POSITIVE; colOffSet = ChessConstants.POSITIVE;
		rowX = kingRow + rowOffSet * multiplier;
		colY = kingCol + colOffSet * multiplier;
		while(rowX <= ChessConstants.MAX_DIM && colY <= ChessConstants.MAX_DIM) { //SouthEast Direction
			if(board.getChessPosition(rowX, colY) != null) { //If the position is empty, the loop keeps running.
				if(checkDiagonalAttacker(board, rowX, colY, board.getChessPosition(kingRow, kingCol).getWhite()))
					return true; //If it's not empty and this function above finds an attacker on the explored position, it returns true and breaks every loop.
				else
					break; //If it's not empty and it finds a piece that isn't a threat or is an ally, it breaks the current loop and starts the next one.
			}
			multiplier++;
			rowX = kingRow + rowOffSet * multiplier;
			colY = kingCol + colOffSet * multiplier;
		}
		
		multiplier = 1; rowOffSet = ChessConstants.POSITIVE; colOffSet = ChessConstants.NEGATIVE;
		rowX = kingRow + rowOffSet * multiplier;
		colY = kingCol + colOffSet * multiplier;
		while(rowX <= ChessConstants.MAX_DIM && colY >= ChessConstants.MIN_DIM) { //SouthWest Direction
			rowX = kingRow + rowOffSet * multiplier;
			colY = kingCol + colOffSet * multiplier;
			if(board.getChessPosition(rowX, colY) != null) { //If the position is empty, the loop keeps running.
				if(checkDiagonalAttacker(board, rowX, colY, board.getChessPosition(kingRow, kingCol).getWhite()))
					return true; //If it's not empty and this function above finds an attacker on the explored position, it returns true and breaks every loop.
				else
					break; //If it's not empty and it finds a piece that isn't a threat or is an ally, it breaks the current loop and returns false, since none of the loops found a threat.
			}
			multiplier++;
			rowX = kingRow + rowOffSet * multiplier;
			colY = kingCol + colOffSet * multiplier;
		}
		return false;
	}
	
	private boolean checkDiagonalAttacker(ChessBoard board, int rowX, int colY, boolean isWhiteKing) {
		return (board.getChessPosition(rowX, colY) instanceof Queen || board.getChessPosition(rowX, colY) instanceof Bishop) && //Checks that there's either a Queen or a Bishop
				(!board.getChessPosition(rowX, colY).getWhite() == isWhiteKing); //And that the king is not the same color as the piece.
	}

	private boolean checkKnightThreat(ChessBoard board, int kingRow, int kingCol) {
		boolean isWhiteKing = board.getChessPosition(kingRow, kingCol).getWhite();
		
		if(kingRow - 2 >= ChessConstants.MIN_DIM) { //Check if going 2 rows up is within the board range.
			if(kingCol - 1 >= ChessConstants.MIN_DIM) { //Check if going 1 column left is within the board range
				if(board.getChessPosition(kingRow - 2, kingCol - 1) != null &&
						board.getChessPosition(kingRow - 2, kingCol - 1) instanceof Knight &&
						board.getChessPosition(kingRow - 2, kingCol - 1).getWhite() != isWhiteKing) {
					return true; //If the explored position is not empty, and has a enemy Knight
				}
			}
			
			if(kingCol + 1 <= ChessConstants.MAX_DIM) { //Check if going 1 column right is within the board range
				if(board.getChessPosition(kingRow - 2, kingCol + 1) != null &&
						board.getChessPosition(kingRow - 2, kingCol + 1) instanceof Knight &&
						board.getChessPosition(kingRow - 2, kingCol + 1).getWhite() != isWhiteKing) {
					return true; //If the explored position is not empty, and has a enemy Knight
				}
			}
		}
		
		if(kingRow + 2 <= ChessConstants.MAX_DIM) { //Check if going 2 rows down is within the board range.
			if(kingCol - 1 >= ChessConstants.MIN_DIM) { //Check if going 1 column left is within the board range
				if(board.getChessPosition(kingRow + 2, kingCol - 1) != null &&
						board.getChessPosition(kingRow + 2, kingCol - 1) instanceof Knight &&
						board.getChessPosition(kingRow + 2, kingCol - 1).getWhite() != isWhiteKing) {
					return true; //If the explored position is not empty, and has a enemy Knight
				}
			}
			
			if(kingCol + 1 <= ChessConstants.MAX_DIM) { //Check if going 1 column right is within the board range
				if(board.getChessPosition(kingRow + 2, kingCol + 1) != null &&
						board.getChessPosition(kingRow + 2, kingCol + 1) instanceof Knight &&
						board.getChessPosition(kingRow + 2, kingCol + 1).getWhite() != isWhiteKing) {
					return true; //If the explored position is not empty, and has a enemy Knight
				}
			}
		}
		
		if(kingCol - 2 >= ChessConstants.MIN_DIM) { //Check if going 2 columns left is within the board range.
			if(kingRow - 1 >= ChessConstants.MIN_DIM) { //Check if going 1 row up is within the board range
				if(board.getChessPosition(kingRow - 1, kingCol - 2) != null &&
						board.getChessPosition(kingRow - 1, kingCol - 2) instanceof Knight &&
						board.getChessPosition(kingRow - 1, kingCol - 2).getWhite() != isWhiteKing) {
					return true; //If the explored position is not empty, and has a enemy Knight
				}
			}
			
			if(kingRow + 1 <= ChessConstants.MAX_DIM) { //Check if going 1 row down is within the board range
				if(board.getChessPosition(kingRow + 1, kingCol - 2) != null &&
						board.getChessPosition(kingRow + 1, kingCol - 2) instanceof Knight &&
						board.getChessPosition(kingRow + 1, kingCol - 2).getWhite() != isWhiteKing) {
					return true; //If the explored position is not empty, and has a enemy Knight
				}
			}
		}
		
		if(kingCol + 2 <= ChessConstants.MAX_DIM) { //Check if going 2 columns right is within the board range.
			if(kingRow - 1 >= ChessConstants.MIN_DIM) { //Check if going 1 row up is within the board range
				if(board.getChessPosition(kingRow - 1, kingCol + 2) != null &&
						board.getChessPosition(kingRow - 1, kingCol + 2) instanceof Knight &&
						board.getChessPosition(kingRow - 1, kingCol + 2).getWhite() != isWhiteKing) {
					return true; //If the explored position is not empty, and has a enemy Knight
				}
			}
			
			if(kingRow + 1 <= ChessConstants.MAX_DIM) { //Check if going 1 row down is within the board range
				if(board.getChessPosition(kingRow + 1, kingCol + 2) != null &&
						board.getChessPosition(kingRow + 1, kingCol + 2) instanceof Knight &&
						board.getChessPosition(kingRow + 1, kingCol + 2).getWhite() != isWhiteKing) {
					return true; //If the explored position is not empty, and has a enemy Knight
				}
			}
		}
		
		return false; //If none of the above conditions were met at some point
	}

	//Checks if the king is threatened by a Pawn.
	private boolean checkPawnThreat(ChessBoard board, int kingRow, int kingCol) {
		if(board.getChessPosition(kingRow, kingCol).getWhite() && kingRow > ChessConstants.MIN_DIM + 1) { //If the king is white and not on the top 2 rows (since pawns can't be on the top row anyway).
			if(kingCol + 1 <= ChessConstants.MAX_DIM && board.getChessPosition(kingRow - 1, kingCol + 1) != null) { //Checking the right diagonal for a pawn.
				if(!board.getChessPosition(kingRow - 1, kingCol + 1).getWhite()) { //If there is a black piece on that position
					if(board.getChessPosition(kingRow - 1, kingCol + 1) instanceof Pawn) { //If that piece is a pawn
						return true; //All the conditions were met.
					}
				}
			}
			
			if(kingCol - 1 <= ChessConstants.MAX_DIM && board.getChessPosition(kingRow - 1, kingCol - 1) != null) { //Checking the left diagonal.
				if(!board.getChessPosition(kingRow - 1, kingCol - 1).getWhite()) { //If there is a black piece on that position
					if(board.getChessPosition(kingRow - 1, kingCol - 1) instanceof Pawn) { //If that piece is a pawn
						return true; //All the conditions were met.
					}
				}
			}
		} else if(!board.getChessPosition(kingRow, kingCol).getWhite() && kingRow < ChessConstants.MAX_DIM - 1) { //If the king is black and not on the bottom 2 rows (since pawns can't be on the bottom row anyway).
			if(kingCol + 1 <= ChessConstants.MAX_DIM && board.getChessPosition(kingRow + 1, kingCol + 1) != null) { //Checking the right diagonal for a pawn.
				if(board.getChessPosition(kingRow + 1, kingCol + 1).getWhite()) { //If there is a white piece on that position
					if(board.getChessPosition(kingRow + 1, kingCol + 1) instanceof Pawn) { //If that piece is a pawn
						return true; //All the conditions were met.
					}
				}
			}
			
			if(kingCol - 1 <= ChessConstants.MAX_DIM && board.getChessPosition(kingRow + 1, kingCol - 1) != null) { //Checking the left diagonal.
				if(board.getChessPosition(kingRow + 1, kingCol - 1).getWhite()) { //If there is a white piece on that position
					if(board.getChessPosition(kingRow + 1, kingCol - 1) instanceof Pawn) { //If that piece is a pawn
						return true; //All the conditions were met.
					}
				}
			}
		}
		
		return false;
	}
	
	//Simply executes a move that has been previously checked and is legal.
	private void executeCheckedMove(ChessBoard board) { 
		board.setPosition(this.rowDes, this.colDes, board.getChessPosition(this.row, this.col));
		deleteMovedPiece(this.row, this.col, board);
	}
	
	private void executeCastlingTestMove(ChessBoard board, int row, int col, int rowDes, int colDes) { 
		board.setPosition(rowDes, colDes, board.getChessPosition(row, col));
		deleteMovedPiece(row, col, board);
	}
	
	
	private void executeCaptureMove(ChessBoard board) {
		if((board.getChessPosition(this.rowDes, this.colDes).getWhite() && (this.getPiece().getWhite())) ||
				((!board.getChessPosition(this.rowDes, this.colDes).getWhite() && (!this.getPiece().getWhite())))) {
				//If the player tried to capture an ally piece.
			throw new GameError("Destination position already occupied by an ally piece!");
		} else { //If he's capturing an enemy piece.
			if(board.getChessPosition(this.rowDes, this.colDes) instanceof King) { //You can't capture the king!
				throw new GameError("Checkmate? This point should be unreachable.");
			} else {
				executeCheckedMove(board);
			}
		}
	}
	
	//Disable En Passant for every Pawn from the colour passed by parameter.
	private void disableEnPassant(boolean isWhite) {
		for(int rowX = ChessConstants.MIN_DIM + 3; rowX <= ChessConstants.MAX_DIM - 3; rowX++) { //Pawns that have the "En passant" attribute set, can only be in the 2 central rows
			for(int colY = ChessConstants.MIN_DIM; colY <= ChessConstants.MAX_DIM; colY++) {
				if(this.chessBoard.getChessPosition(rowX, colY) != null &&
						this.chessBoard.getChessPosition(rowX, colY) instanceof Pawn &&
						this.chessBoard.getChessPosition(rowX, colY).getWhite() == isWhite) {
					if(!this.testMove) ((Pawn) this.chessBoard.getChessPosition(rowX, colY)).setPassant(false);
				}
			}
		}
	}
	
	//Shows whether the current player is moving his own pieces or trying to move the opponent's ones.
	private boolean checkTurn(ChessBoard board) {
		//this.getPiece() returns the piece to which the move belongs!
		return ((this.getPiece().getWhite() && (board.getChessPosition(this.row, this.col)).getWhite()) ||
				(!this.getPiece().getWhite() && !(board.getChessPosition(this.row, this.col)).getWhite()));
	}
	
	//Due to the peculiar pattern of pawn movement, it is required to have two
	//different functions, since white pawns move upwards, and black ones move downwards.
	private void executePawnMove(ChessBoard board) {
		if(this.getPiece().getWhite())
			executeWhitePawnMove(board);
		else
			executeBlackPawnMove(board);
	}
	
	private void executeWhitePawnMove(ChessBoard board) {
		//Check if this pawn is trying to capture a piece. (Diagonal move)
		if(this.rowDes == this.row - 1 && (this.colDes == this.col + 1 || this.colDes == this.col - 1)) {
			if(board.getChessPosition(this.rowDes, this.colDes) != null) { //The destination position isn't empty
				executeCaptureMove(board); //This already checks the kind of piece that is in the destination position and if it can be captured.
				if(!this.testMove) ((Pawn) board.getChessPosition(this.rowDes, this.colDes)).setFirstMove(false);
			} else if((board.getChessPosition(this.row, this.col + 1) instanceof Pawn) && (this.colDes == this.col + 1) || //There is a pawn on the right
					(board.getChessPosition(this.row, this.col - 1) instanceof Pawn) && (this.colDes == this.col - 1)) { //or the left for En Passant capture
				//No need to check that destination is null, that's checked above.
				if(this.colDes == this.col + 1) { //Capture to the right
					if(!board.getChessPosition(this.row, this.col + 1).getWhite() && //Check that the pawn is black
							((Pawn) board.getChessPosition(this.row, this.col + 1)).getPassant()) { //and it can be captured En Passant for this turn.						
						executeCheckedMove(board);
						if(!this.testMove) board.setPosition(this.row, this.col + 1, null); //Deletes the captured piece
					} else { //Trying to capture either your own pawn or a pawn that can't be captured En Passant
						throw new GameError("Invalid Pawn move, try again.");
					}
				} else if (this.colDes == this.col - 1) { //Capture to the left.
					if(!board.getChessPosition(this.row, this.col - 1).getWhite() && //Check that the pawn is black
							((Pawn) board.getChessPosition(this.row, this.col - 1)).getPassant()) { //and it can be captured En Passant for this turn.
						executeCheckedMove(board);
						if(!this.testMove) board.setPosition(this.row, this.col - 1, null); //Deletes the captured piece
					} else { //Trying to capture own piece En Passant
						throw new GameError("Invalid Pawn move, try again.");
					}
				} else { //Unrecognised En Passant move (not left nor right?)
					throw new GameError("Invalid Pawn move, try again.");
				}
			} else { //Pawn invalid diagonal move.
				throw new GameError("Invalid Pawn move, try again.");
			}
		} else if(this.col == this.colDes && this.row - 1 == this.rowDes) { //Check if it's making a simple move
			if(board.getChessPosition(this.rowDes, this.colDes) == null) { //Working.
				executeCheckedMove(board);
				if(!this.testMove) ((Pawn) board.getChessPosition(this.rowDes, this.colDes)).setFirstMove(false);
			} else {
				throw new GameError("Invalid Pawn move, try again.");
			}
		} else if(this.col == this.colDes && this.row - 2 == this.rowDes) { //Check if it's making an opening move (Double).
			if(((Pawn) board.getChessPosition(this.row, this.col)).getFirstMove()) { //Checks if it's the pawn's first move.
				if(board.getChessPosition(this.rowDes, this.colDes) != null) {
					throw new GameError("Invalid move, the destination position is occupied, try again.");
				} else if (!checkPiecesInbetween(this.row, this.col, this.rowDes, this.colDes)) {
					throw new GameError("Invalid move, you can't skip through other pieces, try again.");
				} else {
					executeCheckedMove(board);
					if(!this.testMove) ((Pawn) board.getChessPosition(this.rowDes, this.colDes)).setPassant(true); //This pawn can be captured En Passant in the next move.
					if(!this.testMove) ((Pawn) board.getChessPosition(this.rowDes, this.colDes)).setFirstMove(false);
				}
			} else {
				throw new GameError("This Pawn can't perform an opening move.");
			}
		} else { //The move is not valid.
			throw new GameError("Invalid Pawn move, try again.");
		}
		
		//You can only get here if everything went right during the execution of the move.
		if(!this.testMove && !isKingInCheck(board, this.getPiece().getWhite()) && checkPromotion(board)) {
			if(!Controller.isWhiteAI) {
				ChessPawnPromotionDialog dialog = new ChessPawnPromotionDialog("Select the piece you would like:", true);
				
				dialog.addWindowListener(new WindowAdapter() {
	                public void windowClosed(WindowEvent e) {
	                    ChessPiece newPiece;
	                    switch(dialog.getChosenPiece()) {
	                            case ChessPieceID.WHITE_ROOK_PAWN:
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
	            });
			} else {
				executePromotion(board, new Queen(true)); //Always promotes to Queen because of piece value maximisation heuristics
			}
		} else if(this.testMove) {
			executePromotion(board, new Queen(true)); //Always promotes to Queen because of piece value maximisation heuristics
		}
	}
	
	private void executeBlackPawnMove(ChessBoard board) {
		//Check if this pawn is trying to capture a piece. (Diagonal move)
		if(this.rowDes == this.row + 1 && (this.colDes == this.col + 1 || this.colDes == this.col - 1)) {
			if(board.getChessPosition(this.rowDes, this.colDes) != null) { //The destination position isn't empty
				executeCaptureMove(board); //This already checks the kind of piece that is in the destination position and if it can be captured.
				if(!this.testMove) ((Pawn) board.getChessPosition(this.rowDes, this.colDes)).setFirstMove(false);
			} else if((board.getChessPosition(this.row, this.col + 1) instanceof Pawn) && (this.colDes == this.col + 1) || //There is a pawn on the right
					(board.getChessPosition(this.row, this.col - 1) instanceof Pawn) && (this.colDes == this.col - 1)) { //or the left for En Passant capture
				//No need to check that destination is null, that's checked above.
				if(this.colDes == this.col + 1) { //Capture to the right
					if(board.getChessPosition(this.row, this.col + 1).getWhite() && //Check that the pawn is white
							((Pawn) board.getChessPosition(this.row, this.col + 1)).getPassant()) { //and it can be captured En Passant for this turn.						
						executeCheckedMove(board);
						board.setPosition(this.row, this.col + 1, null); //Deletes the captured piece
					} else { //Trying to capture either your own pawn or a pawn that can't be captured En Passant
						throw new GameError("Invalid Pawn move, try again.");
					}
				} else if (this.colDes == this.col - 1) { //Capture to the left.
					if(board.getChessPosition(this.row, this.col - 1).getWhite() && //Check that the pawn is white
							((Pawn) board.getChessPosition(this.row, this.col - 1)).getPassant()) { //and it can be captured En Passant for this turn.
						executeCheckedMove(board);
						board.setPosition(this.row, this.col - 1, null); //Deletes the captured piece
					} else { //Trying to capture own piece En Passant
						throw new GameError("Invalid Pawn move, try again.");
					}
				} else { //Unrecognised En Passant move (not left nor right?)
					throw new GameError("Invalid Pawn move, try again.");
				}
			} else { //Pawn invalid diagonal move.
				throw new GameError("Invalid Pawn move, try again.");
			}
		} else if(this.col == this.colDes && this.row + 1 == this.rowDes) { //Check if it's making a simple move
			if(board.getChessPosition(this.rowDes, this.colDes) == null) { //Working.
				executeCheckedMove(board);
				if(!this.testMove) ((Pawn) board.getChessPosition(this.rowDes, this.colDes)).setFirstMove(false);
			} else {
				throw new GameError("Invalid Pawn move, try again.");
			}
		} else if(this.col == this.colDes && this.row + 2 == this.rowDes) { //Check if it's making an opening move (Double).
			if(((Pawn) board.getChessPosition(this.row, this.col)).getFirstMove()) { //Checks if it's the pawn's first move.
				if(board.getChessPosition(this.rowDes, this.colDes) != null) {
					throw new GameError("Invalid move, the destination position is occupied, try again.");
				} else if (!checkPiecesInbetween(this.row, this.col, this.rowDes, this.colDes)) {
					throw new GameError("Invalid move, you can't skip through other pieces, try again.");
				} else {
					executeCheckedMove(board);
					if(!this.testMove) ((Pawn) board.getChessPosition(this.rowDes, this.colDes)).setPassant(true); //This pawn can be captured En Passant in the next move.
					if(!this.testMove) ((Pawn) board.getChessPosition(this.rowDes, this.colDes)).setFirstMove(false);
				}
			} else {
				throw new GameError("This Pawn can't perform an opening move.");
			}
		} else { //The move is not valid.
			throw new GameError("Invalid Pawn move, try again.");
		}
		
		//You can only get here if everything went right during the execution of the move.
		if(!this.testMove && !isKingInCheck(board, this.getPiece().getWhite()) && checkPromotion(board)) {
			if(!Controller.isBlackAI) {
				ChessPawnPromotionDialog dialog = new ChessPawnPromotionDialog("Select the piece you would like:", false);
				
				dialog.addWindowListener(new WindowAdapter() {
	                public void windowClosed(WindowEvent e)  {
	                    ChessPiece newPiece;
	                    switch(dialog.getChosenPiece()) {
	                            case ChessPieceID.BLACK_ROOK_PAWN:
	                                    newPiece = new Rook(false, false);
	                                    break;
	                            case ChessPieceID.BLACK_KNIGHT:
	                                    newPiece = new Knight(false);
	                                    break;
	                            case ChessPieceID.BLACK_BISHOP:
	                                    newPiece = new Bishop(false);
	                                    break;
	                            case ChessPieceID.BLACK_QUEEN:
	                                    newPiece = new Queen(false);
	                                    break;
	                            default:
	                                    throw new GameError("Invalid piece for promotion chosen.");
	                    }
	                    executePromotion(board, newPiece);
	                }
	            });
			} else {
				executePromotion(board, new Queen(false)); //Always promotes to Queen because of piece value maximisation heuristics
			}
		} else if(this.testMove) {
			executePromotion(board, new Queen(false)); //Always promotes to Queen because of piece value maximisation heuristics
		}
	}
	
	private boolean checkPromotion(ChessBoard board) { //This function should only be called from within the Pawn movement function for it to work properly.
		return (board.getChessPosition(this.rowDes, this.colDes).getWhite() && this.rowDes == ChessConstants.MIN_DIM) || //If it's white and has reached the top
				(!board.getChessPosition(this.rowDes, this.colDes).getWhite() && this.rowDes == ChessConstants.MAX_DIM); //If it's black and has reached the bottom
	}
	
	private void executePromotion(ChessBoard board, ChessPiece p) { //The piece must be properly created in another function. Usually executeWhite/BlackPawnMove().
		board.setPosition(this.rowDes, this.colDes, p);
	}

	private void executeRookMove(ChessBoard board) {
		if(checkHorizVertMove()) { //Check that it's moving either horizontally or vertically.
			if(board.getChessPosition(this.rowDes, this.colDes) != null) { //Trying to capture a piece.
				if((board.getChessPosition(this.rowDes, this.colDes).getWhite() && this.getPiece().getWhite()) ||
						(!board.getChessPosition(this.rowDes, this.colDes).getWhite() && !this.getPiece().getWhite())) {
					//Check that you're not trying to capture your own pieces.
					throw new GameError("Invalid move, the destination is occupied by an ally piece, try again.");
				} else if (!checkPiecesInbetween(this.row, this.col, this.rowDes, this.colDes)) { //Check there are no pieces inbetween.
					throw new GameError("Invalid move, you can't skip through other pieces, try again.");
				} else { //Everything correct, we can execute the move.
					executeCaptureMove(board);
					this.actionMove = true;
					if(!this.testMove) ((Rook) board.getChessPosition(this.rowDes, this.colDes)).setCastle(false); //This rook can't Castle since it moved.
				}
			} else { //The destination position is empty.
				if (!checkPiecesInbetween(this.row, this.col, this.rowDes, this.colDes)) { //Check there are no pieces inbetween.
					throw new GameError("Invalid move, you can't skip through other pieces, try again.");
				} else { //We can proceed to the move.
					executeCheckedMove(board);
					if(!this.testMove) ((Rook) board.getChessPosition(this.rowDes, this.colDes)).setCastle(false); //This rook can't Castle since it moved.
				}
			}
		} else { //It is not a vertical or horizontal move.
			throw new GameError("Invalid move, try again.");
		}
	}
	
	private void executeKnightMove(ChessBoard board) {
		if(((this.colDes == this.col + 1 || this.colDes == this.col - 1) &&
			(this.rowDes == this.row + 2 || this.rowDes == this.row - 2)) ||
			((this.rowDes == this.row + 1 || this.rowDes == this.row - 1) &&
			(this.colDes == this.col + 2 || this.colDes == this.col - 2))) { //Check if the movement is legal.
			if(board.getPosition(this.rowDes, this.colDes) == null) { //If the destination position is empty.
				executeCheckedMove(board);
			} else { //If the piece is trying to capture a piece.
				executeCaptureMove(board);
				this.actionMove = true;
			}
		} else { //The movement is illegal.
			throw new GameError("Invalid movement, try again.");
		}
	}
	
	private void executeBishopMove(ChessBoard board) {
		if(checkDiagonalMove()) { //Checks that it's moving diagonally.
			if(board.getChessPosition(this.rowDes, this.colDes) != null) { //Trying to capture a piece.
				if((board.getChessPosition(this.rowDes, this.colDes).getWhite() && this.getPiece().getWhite()) ||
						(!board.getChessPosition(this.rowDes, this.colDes).getWhite() && !this.getPiece().getWhite())) {
					//Check that you're not trying to capture your own pieces.
					throw new GameError("Invalid move, the destination position is occupied by an ally piece, try again.");
				} else if (!checkPiecesInbetween(this.row, this.col, this.rowDes, this.colDes)) { //Check there are no pieces inbetween.
					throw new GameError("Invalid move, you can't skip through other pieces, try again.");
				} else { //Everything correct, we can execute the move.
					executeCaptureMove(board);
					this.actionMove = true;
				}
			} else { //The destination position is empty.
				if (!checkPiecesInbetween(this.row, this.col, this.rowDes, this.colDes)) { //Check there are no pieces inbetween.
					throw new GameError("Invalid move, you can't skip through other pieces, try again.");
				} else { //We can proceed to the move.
					executeCheckedMove(board);
				}
			}
		} else { //It is not a vertical or horizontal move.
			throw new GameError("Invalid move, try again.");
		}
	}

	private void executeQueenMove(ChessBoard board) {
		if(checkDiagonalMove() || checkHorizVertMove()) { //Checks that it's moving diagonally or horizontally but not in any other way.
			if(board.getChessPosition(this.rowDes, this.colDes) != null) { //Trying to capture a piece.
				if((board.getChessPosition(this.rowDes, this.colDes).getWhite() && this.getPiece().getWhite()) ||
						(!board.getChessPosition(this.rowDes, this.colDes).getWhite() && !this.getPiece().getWhite())) {
					//Check that you're not trying to capture your own pieces.
					throw new GameError("Invalid move, the destination position is occupied by an ally piece, try again.");
				} else if (!checkPiecesInbetween(this.row, this.col, this.rowDes, this.colDes)) { //Check there are no pieces inbetween.
					throw new GameError("Invalid move, you can't skip through other pieces, try again.");
				} else { //Everything correct, we can execute the move.
					executeCaptureMove(board);
					this.actionMove = true;
				}
			} else { //The destination position is empty.
				if (!checkPiecesInbetween(this.row, this.col, this.rowDes, this.colDes)) { //Check there are no pieces inbetween.
					throw new GameError("Invalid move, you can't skip through other pieces, try again.");
				} else { //We can proceed to the move.
					executeCheckedMove(board);
				}
			}
		} else { //It is not a vertical or horizontal move.
			throw new GameError("Invalid move, try again.");
		}
	}
	
	private void executeKingMove(ChessBoard board) {
		if(Math.abs(this.row - this.rowDes) <= 1 && Math.abs(this.col - this.colDes) <= 1) {
			//Check that it's moving only 1 tile away.
			if(board.getChessPosition(this.rowDes, this.colDes) != null) { //Trying to capture a piece.
				if((board.getChessPosition(this.rowDes, this.colDes).getWhite() && this.getPiece().getWhite()) ||
						(!board.getChessPosition(this.rowDes, this.colDes).getWhite() && !this.getPiece().getWhite())) {
					//Check that you're not trying to capture your own pieces.
					throw new GameError("Invalid move, the destination position is occupied by an ally piece, try again.");
				} else { //Everything correct, we can execute the move.
					executeCaptureMove(board);
					this.actionMove = true;
					if(!this.testMove) ((King) board.getChessPosition(this.rowDes, this.colDes)).setCastle(false); //This King can't Castle since it just moved.
				}
			} else { //The destination position is empty.
					executeCheckedMove(board);
					if(!this.testMove) ((King) board.getChessPosition(this.rowDes, this.colDes)).setCastle(false); //This King can't Castle since it just moved.
			}
		} else if(board.getChessPosition(this.row, this.col).getWhite() && ((this.row == 7 && this.col == 4) &&
				(this.rowDes == 7 && (this.colDes == 2 || this.colDes == 6)))) { //Trying to Castle white king.
			if(((King) board.getChessPosition(this.row, this.col)).getCastle()) { //Check if the King can castle.
				if(this.colDes == 6) { //Short castling
					if(board.getChessPosition(7, 7) != null && board.getChessPosition(7, 7) instanceof Rook) { //Check that there's a rook
						if(((Rook) board.getChessPosition(7, 7)).getCastle()) { //Check if the Rook can castle
							if(checkPiecesInbetween(this.row, this.col, 7, 7)) { //Check that there are no pieces inbetween
								if(!((King) board.getChessPosition(this.row, this.col)).getCheck()) { //Check that the king is not in check
									ChessBoard testBoard = board.copyChessBoard(); //Duplicate the board
									boolean abortMovement = false;
									executeCastlingTestMove(testBoard, this.row, this.col, 7, 5); //Move to the first position it would go through
									if(isKingInCheck(testBoard, this.getPiece().getWhite()))//Check if it is in check at this point.
										abortMovement = true;
									executeCastlingTestMove(testBoard, 7, 5, 7, 6); //Move to the next position
									if(isKingInCheck(testBoard, this.getPiece().getWhite())) //Check if it is in check at this point.
										abortMovement = true;
									if(!abortMovement) { //If the king doesn't go through any danger positions
										executeCheckedMove(board); //Performs Castling...
										board.setPosition(7, 5, board.getChessPosition(7, 7));
										deleteMovedPiece(7, 7, board);
									} else {
										throw new GameError("Cannot perform Castling, the King would be in Check during the move!");
									}
								} else {
									throw new GameError("Cannot perform Castling, the King is in Check!");
								}
							} else {
								throw new GameError("Cannot perform Castling, there are pieces inbetween.");
							}
						} else {
							throw new GameError("Rook cannot perform Castling.");
						}
					} else {
						throw new GameError("Rook is missing for Castling.");
					}
				} else { //Long castling
					if(board.getChessPosition(7, 0) != null && board.getChessPosition(7, 0) instanceof Rook) { //Check that there's a rook
						if(((Rook) board.getChessPosition(7, 0)).getCastle()) { //Check if the Rook can castle
							if(checkPiecesInbetween(this.row, this.col, 7, 0)) { //Check that there are no pieces inbetween
								if(!((King) board.getChessPosition(this.row, this.col)).getCheck()) { //Check that the king is not in check
									ChessBoard testBoard = board.copyChessBoard(); //Duplicate the board
									boolean abortMovement = false;
									executeCastlingTestMove(testBoard, this.row, this.col, 7, 3); //Move to the first position it would go through
									if(isKingInCheck(testBoard, this.getPiece().getWhite()))//Check if it is in check at this point.
										abortMovement = true;
									executeCastlingTestMove(testBoard, 7, 3, 7, 2); //Move to the next position
									if(isKingInCheck(testBoard, this.getPiece().getWhite())) //Check if it is in check at this point.
										abortMovement = true;
									if(!abortMovement) { //If the king doesn't go through any danger positions
										executeCheckedMove(board); //Performs Castling...
										board.setPosition(7, 3, board.getChessPosition(7, 0));
										deleteMovedPiece(7, 0, board);
									} else {
										throw new GameError("Cannot perform Castling, the King would be in Check during the move!");
									}
								} else {
									throw new GameError("Cannot perform Castling, the King is in Check!");
								}
							} else {
								throw new GameError("Cannot perform Castling, there are pieces inbetween.");
							}
						} else {
							throw new GameError("Rook cannot perform Castling.");
						}
					} else {
						throw new GameError("Rook is missing for Castling.");
					}
				}
			} else {
				throw new GameError("This King cannot Castle anymore.");
			}
		} else if(!board.getChessPosition(this.row, this.col).getWhite() && ((this.row == 0 && this.col == 4) &&
				(this.rowDes == 0 && (this.colDes == 2 || this.colDes == 6)))) { //Trying to Castle black king.
			if(((King) board.getChessPosition(this.row, this.col)).getCastle()) { //Check if the King can castle.
				if(this.colDes == 6) { //Short castling
					if(board.getChessPosition(0, 7) != null && board.getChessPosition(0, 7) instanceof Rook) { //Check that there's a rook
						if(((Rook) board.getChessPosition(0, 7)).getCastle()) { //Check if the Rook can castle
							if(checkPiecesInbetween(this.row, this.col, 0, 7)) { //Check that there are no pieces inbetween
								if(!((King) board.getChessPosition(this.row, this.col)).getCheck()) { //Check that the king is not in check
									ChessBoard testBoard = board.copyChessBoard(); //Duplicate the board
									boolean abortMovement = false;
									executeCastlingTestMove(testBoard, this.row, this.col, 0, 5); //Move to the first position it would go through
									if(isKingInCheck(testBoard, this.getPiece().getWhite()))//Check if it is in check at this point.
										abortMovement = true;
									executeCastlingTestMove(testBoard, 0, 5, 0, 6); //Move to the next position
									if(isKingInCheck(testBoard, this.getPiece().getWhite())) //Check if it is in check at this point.
										abortMovement = true;
									if(!abortMovement) { //If the king doesn't go through any danger positions
										executeCheckedMove(board); //Performs Castling...
										board.setPosition(0, 5, board.getChessPosition(0, 7));
										deleteMovedPiece(0, 7, board);
									} else {
										throw new GameError("Cannot perform Castling, the King would be in Check during the move!");
									}
								} else {
									throw new GameError("Cannot perform Castling, the King is in Check!");
								}
							} else {
								throw new GameError("Cannot perform Castling, there are pieces inbetween.");
							}
						} else {
							throw new GameError("Rook cannot perform Castling.");
						}
					} else {
						throw new GameError("Rook is missing for Castling.");
					}
				} else { //Long castling
					if(board.getChessPosition(0, 0) != null && board.getChessPosition(0, 0) instanceof Rook) { //Check that there's a rook
						if(((Rook) board.getChessPosition(0, 0)).getCastle()) { //Check if the Rook can castle
							if(checkPiecesInbetween(this.row, this.col, 0, 0)) { //Check that there are no pieces inbetween
								if(!((King) board.getChessPosition(this.row, this.col)).getCheck()) { //Check that the king is not in check
									ChessBoard testBoard = board.copyChessBoard(); //Duplicate the board
									boolean abortMovement = false;
									executeCastlingTestMove(testBoard, this.row, this.col, 0, 3); //Move to the first position it would go through
									if(isKingInCheck(testBoard, this.getPiece().getWhite()))//Check if it is in check at this point.
										abortMovement = true;
									executeCastlingTestMove(testBoard, 0, 3, 0, 2); //Move to the next position
									if(isKingInCheck(testBoard, this.getPiece().getWhite())) //Check if it is in check at this point.
										abortMovement = true;
									if(!abortMovement) { //If the king doesn't go through any danger positions
										executeCheckedMove(board); //Performs Castling...
										board.setPosition(0, 3, board.getChessPosition(0, 0));
										deleteMovedPiece(0, 0, board);
									} else {
										throw new GameError("Cannot perform Castling, the King would be in Check during the move!");
									}
								} else {
									throw new GameError("Cannot perform Castling, the King is in Check!");
								}
							} else {
								throw new GameError("Cannot perform Castling, there are pieces inbetween.");
							}
						} else {
							throw new GameError("Rook cannot perform Castling.");
						}
					} else {
						throw new GameError("Rook is missing for Castling.");
					}
				}
			} else {
				throw new GameError("This King cannot Castle anymore.");
			}
		} else { //Illegal move.
			throw new GameError("Invalid move, try again.");
		}
	}
	
	//Checks if the movement is actually diagonal and not horizontal, vertical or anything else.
	private boolean checkDiagonalMove() {
		return (Math.abs(this.col - this.colDes) - Math.abs(this.row - this.rowDes)) == 0;
	}
	
	//Checks if the movement is actually horizontal or vertical and not diagonal or anything else.
	private boolean checkHorizVertMove() {
		return (this.col == this.colDes || this.row == this.rowDes);
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
		if(rowIni == rowEnd) { //It's either E or W
			if(colIni > colEnd) {
				return ChessConstants.W;
			} else { //colIni < colEnd (colIni always != colEnd at this point!) since both parameters can't be equal!
				return ChessConstants.E;
			}
		} else if (rowIni > rowEnd) { //It's either NW, N or NE
			if(colIni == colEnd) {
				return ChessConstants.N;
			} else if(colIni > colEnd) {
				return ChessConstants.NW;
			} else { //colIni < colEnd
				return ChessConstants.NE;
			}
		} else { //rowIni < rowEnd //It's either SW, S or SE
			if(colIni == colEnd) {
				return ChessConstants.S;
			} else if(colIni > colEnd) {
				return ChessConstants.SW;
			} else { //colIni < colEnd
				return ChessConstants.SE;
			}
		}
	}
	
	//Check if there are any pieces between the positions selected (for bishops, rooks and queens)
	//True means that the move can be performed, false means that there are pieces inbetween.
	//The directions are shown on at the checkDirection() function above.
	//
	//Returns true if the movement can be performed, returns false if it can't be performed!!!
	private boolean checkPiecesInbetween(int rowIni, int colIni, int rowEnd, int colEnd) {
		int rowOffset, colOffset;
		switch(checkDirection(rowIni, colIni, rowEnd, colEnd)) {
			case ChessConstants.NW:
				rowOffset = ChessConstants.NEGATIVE; colOffset = ChessConstants.NEGATIVE; break;
			case ChessConstants.N:
				rowOffset = ChessConstants.NEGATIVE; colOffset = ChessConstants.NEUTRAL; break;
			case ChessConstants.NE:
				rowOffset = ChessConstants.NEGATIVE; colOffset = ChessConstants.POSITIVE; break;
			case ChessConstants.E:
				rowOffset = ChessConstants.NEUTRAL; colOffset = ChessConstants.POSITIVE; break;
			case ChessConstants.SE:
				rowOffset = ChessConstants.POSITIVE; colOffset = ChessConstants.POSITIVE; break;
			case ChessConstants.S:
				rowOffset = ChessConstants.POSITIVE; colOffset = ChessConstants.NEUTRAL; break;
			case ChessConstants.SW:
				rowOffset = ChessConstants.POSITIVE; colOffset = ChessConstants.NEGATIVE; break;
			case ChessConstants.W:
				rowOffset = ChessConstants.NEUTRAL; colOffset = ChessConstants.NEGATIVE; break;
			default:
				throw new GameError("Internal error. Invalid move direction, this should be unreachable.");
		}
		
		int itCount = 0; //In case any error happens, this avoids an infinite loop.
		int rowIt = rowIni, colIt = colIni;
		
		while(itCount < ChessConstants.ITER_LIMIT && (rowIt != rowEnd || colIt != colEnd)) {
			rowIt += rowOffset;
			colIt += colOffset;
			
			if(rowIt == rowEnd && colIt == colEnd) break; //Destination position should not be checked so you can capture pieces.
			
			if(chessBoard.getPosition(rowIt, colIt) != null) //If it found a piece on the way.
				return false;
			
			itCount++;
		}
		
		if(itCount >= ChessConstants.ITER_LIMIT) 
			throw new GameError("Internal error. Move direction bugged. This should never happen.");
		
		return true;
	} //If returned false, throw an exception from the suitable function.
	
	//Deletes a piece (used after copying it to a new cell)
	private void deleteMovedPiece(int row, int col, Board board) {
		board.setPosition(row, col, null);
	}
	
	public void revertBoard(ChessBoard current, ChessBoard before) {
		for(int rowX = ChessConstants.MIN_DIM; rowX <= ChessConstants.MAX_DIM; rowX++) {
			for(int colY = ChessConstants.MIN_DIM; colY <= ChessConstants.MAX_DIM; colY++) {
				if(before.getChessPosition(rowX, colY) == null) { //If the cell was empty...
					if(current.getChessPosition(rowX, colY) != null) { //and the cell is occupied now...
						current.setPosition(rowX, colY, null); //it deletes the contents.
					} //else: It is still empty, nothing to do.
				} else { //If the cell wasn't empty...
					current.setPosition(rowX, colY, before.getChessPosition(rowX, colY)); //we just recover the old piece, whichever it was.
				}
			}
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
			return getPiece() + " player moves (" + row + "," + col + ") to ("  + rowDes + "," + colDes + ")";
		}
	}
	
	@Override
	public ChessPiece getPiece() {
		return this.chessPiece;
	}

	@Override
	public GameMove fromString(Piece p, String str) {
		//This is not needed in this game and should not be called.
		return null;
	}

	public int getRow() {
		return this.row;
	}
	
	public int getCol() {
		return this.col;
	}
	
	public int getRowDes() {
		return this.rowDes;
	}
	
	public int getColDes() {
		return this.colDes;
	}
}