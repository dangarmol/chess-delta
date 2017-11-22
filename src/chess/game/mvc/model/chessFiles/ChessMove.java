package chess.game.mvc.model.chessFiles;

import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.List;

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
 *
 */
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
	
	public void execute(Board board, List<Piece> playersPieces) { //TODO This needs to be fixed.
		throw new GameError("executeChessMove() should have been called instead.");
	}
	
	//Checks if the move can be made and, if so, moves the piece to the specified position.
	public void executeChessMove(Board board, List<Piece> playersPieces, List<Piece> chessPieces) {
		//Changed from this.getPiece() instanceof Rook
		//this.getPiece() now returns the player that made the move.
		//Check that the player moves his own piece
		
		this.chessBoard = (ChessBoard) board;
		
		if(!checkTurn(chessBoard)) {//Checks that the player is trying to move his own piece.
			String color = "";
			if(this.getPiece().getWhite())
				color = "WHITE";
			else
				color = "BLACK";
			throw new GameError("You can only move your own pieces. It's the turn for " + color + ". Try again. (Error Code 001)"); //Works properly
		}
		
		if(this.row == this.rowDes && this.col == this.colDes)
			throw new GameError("You cannot move a piece to the same position. Try again. (Error Code 002)");
			
		if(chessBoard.getPosition(this.row, this.col) instanceof Pawn) {
			executePawnMove(chessBoard, chessPieces);
		} else if (chessBoard.getPosition(this.row, this.col) instanceof Rook) {
			executeRookMove(chessBoard, chessPieces);
		} else if (chessBoard.getPosition(this.row, this.col) instanceof Knight) {
			executeKnightMove(chessBoard, chessPieces);
		} else if (chessBoard.getPosition(this.row, this.col) instanceof Bishop) {
			executeBishopMove(chessBoard, chessPieces);
		} else if (chessBoard.getPosition(this.row, this.col) instanceof Queen) {
			executeQueenMove(chessBoard, chessPieces);
		} else if (chessBoard.getPosition(this.row, this.col) instanceof King) {
			executeKingMove(chessBoard, chessPieces);
		} else {
			throw new GameError("Piece type not recognised! This should be unreachable. (Error Code 003)");
		}
		
		disableEnPassant(true, chessPieces);
	}
	
	//Returns the position of the king from the current player as an 2 digits integer.
	//Left digit represents row and right digit represents col. King at (2, 5), would be returned as "25".
	//Returns -1 if the king hasn't been found. An exception should be thrown if this happens.
	private int findKing(ChessBoard board, boolean isWhite) {
		for(int rowX = 0; rowX < 8; rowX++) {
			for(int colY = 0; colY < 8; colY++) {
				if(board.getChessPosition(rowX, colY) != null) {
					if(board.getChessPosition(rowX, colY) instanceof King && (board.getChessPosition(rowX, colY).getWhite() == isWhite)) {
						return rowX * 10 + colY;
					}
				}
			}
		}
		return -1;
	}
	
	public boolean isKingInCheck(ChessBoard board, boolean isWhite) { //TODO This need to be added to the conditions for the moves.
		int kingLocation = findKing(board, isWhite);
		if(kingLocation == -1) {
			throw new GameError("King not found, this should never happen.");
		}
		int kingRow = kingLocation / 10;
		int kingCol = kingLocation % 10;
		//King can't be threatened by the other King. Since a King cannot move to a vulnerable position.
		return checkHorizVertThreat(board, kingRow, kingCol) || checkDiagonalThreat(board, kingRow, kingCol) ||
				checkKnightThreat(board, kingRow, kingCol) || checkPawnThreat(board, kingRow, kingCol);
	}
	
	//Checks if the king is threatened from any row or column, horizontally or vertically by either a Queen or a Rook.
	private boolean checkHorizVertThreat(ChessBoard board, int kingRow, int kingCol) {
		for(int rowX = kingRow; rowX <= 7; rowX++) { //South Direction
			if(board.getChessPosition(rowX, kingCol) != null) { //If the position is empty, the loop keeps running.
				if(checkHorizVertAttacker(board, rowX, kingCol, board.getChessPosition(kingRow, kingCol).getWhite()))
					return true; //If it's not empty and this function above finds an attacker on the explored position, it returns true and breaks every loop.
				else
					break; //If it's not empty and it finds a piece that isn't a threat or is an ally, it breaks the current loop and starts the next one.
			}
		}
		
		for(int rowX = kingRow; rowX >= 0; rowX--) { //North Direction
			if(board.getChessPosition(rowX, kingCol) != null) { //If the position is empty, the loop keeps running.
				if(checkHorizVertAttacker(board, rowX, kingCol, board.getChessPosition(kingRow, kingCol).getWhite()))
					return true; //If it's not empty and this function above finds an attacker on the explored position, it returns true and breaks every loop.
				else
					break; //If it's not empty and it finds a piece that isn't a threat or is an ally, it breaks the current loop and starts the next one.
			}
		}
		
		for(int colY = kingCol; colY <= 7; colY++) { //East Direction
			if(board.getChessPosition(kingRow, colY) != null) { //If the position is empty, the loop keeps running.
				if(checkHorizVertAttacker(board, kingRow, colY, board.getChessPosition(kingRow, kingCol).getWhite()))
					return true; //If it's not empty and this function above finds an attacker on the explored position, it returns true and breaks every loop.
				else
					break; //If it's not empty and it finds a piece that isn't a threat or is an ally, it breaks the current loop and starts the next one.
			}
		}
		
		for(int colY = kingCol; colY >= 0; colY--) { //West Direction
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
		int rowOffSet = -1, colOffSet = -1;
		int multiplier = 1;
		int rowX, colY;
		do { //NorthWest Direction
			rowX = kingRow + rowOffSet * multiplier;
			colY = kingCol + colOffSet * multiplier;
			if(board.getChessPosition(rowX, colY) != null) { //If the position is empty, the loop keeps running.
				if(checkDiagonalAttacker(board, rowX, colY, board.getChessPosition(kingRow, kingCol).getWhite()))
					return true; //If it's not empty and this function above finds an attacker on the explored position, it returns true and breaks every loop.
				else
					break; //If it's not empty and it finds a piece that isn't a threat or is an ally, it breaks the current loop and starts the next one.
			}
			multiplier++;
		} while(rowX >= 0 && colY >= 0);
		
		multiplier = 1; rowOffSet = -1; colOffSet = 1;
		do { //NorthEast Direction
			rowX = kingRow + rowOffSet * multiplier;
			colY = kingCol + colOffSet * multiplier;
			if(board.getChessPosition(rowX, colY) != null) { //If the position is empty, the loop keeps running.
				if(checkDiagonalAttacker(board, rowX, colY, board.getChessPosition(kingRow, kingCol).getWhite()))
					return true; //If it's not empty and this function above finds an attacker on the explored position, it returns true and breaks every loop.
				else
					break; //If it's not empty and it finds a piece that isn't a threat or is an ally, it breaks the current loop and starts the next one.
			}
			multiplier++;
		} while(rowX >= 0 && colY <= 7);
		
		multiplier = 1; rowOffSet = 1; colOffSet = 1;
		do { //SouthEast Direction
			rowX = kingRow + rowOffSet * multiplier;
			colY = kingCol + colOffSet * multiplier;
			if(board.getChessPosition(rowX, colY) != null) { //If the position is empty, the loop keeps running.
				if(checkDiagonalAttacker(board, rowX, colY, board.getChessPosition(kingRow, kingCol).getWhite()))
					return true; //If it's not empty and this function above finds an attacker on the explored position, it returns true and breaks every loop.
				else
					break; //If it's not empty and it finds a piece that isn't a threat or is an ally, it breaks the current loop and starts the next one.
			}
			multiplier++;
		} while(rowX <= 7 && colY <= 7);
		
		multiplier = 1; rowOffSet = 1; colOffSet = -1;
		do { //SouthWest Direction
			rowX = kingRow + rowOffSet * multiplier;
			colY = kingCol + colOffSet * multiplier;
			if(board.getChessPosition(rowX, colY) != null) { //If the position is empty, the loop keeps running.
				if(checkDiagonalAttacker(board, rowX, colY, board.getChessPosition(kingRow, kingCol).getWhite()))
					return true; //If it's not empty and this function above finds an attacker on the explored position, it returns true and breaks every loop.
				else
					break; //If it's not empty and it finds a piece that isn't a threat or is an ally, it breaks the current loop and returns false, since none of the loops found a threat.
			}
			multiplier++;
		} while(rowX <= 7 && colY >= 0);
		return false;
	}
	
	private boolean checkDiagonalAttacker(ChessBoard board, int rowX, int colY, boolean isWhiteKing) {
		return (board.getChessPosition(rowX, colY) instanceof Queen || board.getChessPosition(rowX, colY) instanceof Bishop) && //Checks that there's either a Queen or a Bishop
				(!board.getChessPosition(rowX, colY).getWhite() == isWhiteKing); //And that the king is not the same color as the piece.
	}

	private boolean checkKnightThreat(ChessBoard board, int kingRow, int kingCol) {
		boolean isWhiteKing = board.getChessPosition(kingRow, kingCol).getWhite();
		
		if(kingRow - 2 >= 0) { //Check if going 2 rows up is within the board range.
			if(kingCol - 1 >= 0) { //Check if going 1 column left is within the board range
				if(board.getChessPosition(kingRow - 2, kingCol - 1) != null &&
						board.getChessPosition(kingRow - 2, kingCol - 1) instanceof Knight &&
						board.getChessPosition(kingRow - 2, kingCol - 1).getWhite() != isWhiteKing) {
					return true; //If the explored position is not empty, and has a enemy Knight
				}
			}
			
			if(kingCol + 1 <= 7) { //Check if going 1 column right is within the board range
				if(board.getChessPosition(kingRow - 2, kingCol + 1) != null &&
						board.getChessPosition(kingRow - 2, kingCol + 1) instanceof Knight &&
						board.getChessPosition(kingRow - 2, kingCol + 1).getWhite() != isWhiteKing) {
					return true; //If the explored position is not empty, and has a enemy Knight
				}
			}
		}
		
		if(kingRow + 2 <= 7) { //Check if going 2 rows down is within the board range.
			if(kingCol - 1 >= 0) { //Check if going 1 column left is within the board range
				if(board.getChessPosition(kingRow + 2, kingCol - 1) != null &&
						board.getChessPosition(kingRow + 2, kingCol - 1) instanceof Knight &&
						board.getChessPosition(kingRow + 2, kingCol - 1).getWhite() != isWhiteKing) {
					return true; //If the explored position is not empty, and has a enemy Knight
				}
			}
			
			if(kingCol + 1 <= 7) { //Check if going 1 column right is within the board range
				if(board.getChessPosition(kingRow + 2, kingCol + 1) != null &&
						board.getChessPosition(kingRow + 2, kingCol + 1) instanceof Knight &&
						board.getChessPosition(kingRow + 2, kingCol + 1).getWhite() != isWhiteKing) {
					return true; //If the explored position is not empty, and has a enemy Knight
				}
			}
		}
		
		if(kingCol - 2 >= 0) { //Check if going 2 columns left is within the board range.
			if(kingRow - 1 >= 0) { //Check if going 1 row up is within the board range
				if(board.getChessPosition(kingRow - 1, kingCol - 2) != null &&
						board.getChessPosition(kingRow - 1, kingCol - 2) instanceof Knight &&
						board.getChessPosition(kingRow - 1, kingCol - 2).getWhite() != isWhiteKing) {
					return true; //If the explored position is not empty, and has a enemy Knight
				}
			}
			
			if(kingRow + 1 <= 7) { //Check if going 1 row down is within the board range
				if(board.getChessPosition(kingRow + 1, kingCol - 2) != null &&
						board.getChessPosition(kingRow + 1, kingCol - 2) instanceof Knight &&
						board.getChessPosition(kingRow + 1, kingCol - 2).getWhite() != isWhiteKing) {
					return true; //If the explored position is not empty, and has a enemy Knight
				}
			}
		}
		
		if(kingCol + 2 <= 7) { //Check if going 2 columns right is within the board range.
			if(kingRow - 1 >= 0) { //Check if going 1 row up is within the board range
				if(board.getChessPosition(kingRow - 1, kingCol + 2) != null &&
						board.getChessPosition(kingRow - 1, kingCol + 2) instanceof Knight &&
						board.getChessPosition(kingRow - 1, kingCol + 2).getWhite() != isWhiteKing) {
					return true; //If the explored position is not empty, and has a enemy Knight
				}
			}
			
			if(kingRow + 1 <= 7) { //Check if going 1 row down is within the board range
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
		if(board.getChessPosition(kingRow, kingCol).getWhite() && kingRow != 0) { //If the king is white and not on the top row (to avoid null pointers).
			if(((kingCol + 1 <= 7) && board.getChessPosition(kingRow - 1, kingCol + 1) != null) || //Checks the range and then checks if the positions are empty.
					(kingCol - 1 >= 0) && (board.getChessPosition(kingRow - 1, kingCol - 1) != null)) {
				if(!board.getChessPosition(kingRow - 1, kingCol + 1).getWhite() || //Checks if the pieces are black
						!board.getChessPosition(kingRow - 1, kingCol - 1).getWhite()) {
					if(board.getChessPosition(kingRow - 1, kingCol + 1) instanceof Pawn || //Checks if it's a pawn
							board.getChessPosition(kingRow - 1, kingCol - 1) instanceof Pawn) {
						return true; //All the conditions were met
					}
				}
			}
		} else if(!board.getChessPosition(kingRow, kingCol).getWhite() && kingRow != 7) { //If the king is black and not on the bottom row (to avoid null pointers).
			if(((kingCol + 1 <= 7) && board.getChessPosition(kingRow + 1, kingCol + 1) != null) || //Checks the range and then checks if the positions are empty.
					(kingCol - 1 >= 0) && (board.getChessPosition(kingRow + 1, kingCol - 1) != null)) {
				if(board.getChessPosition(kingRow + 1, kingCol + 1).getWhite() || //Checks if the pieces are white
						board.getChessPosition(kingRow + 1, kingCol - 1).getWhite()) {
					if(board.getChessPosition(kingRow + 1, kingCol + 1) instanceof Pawn || //Checks if it's a pawn
							board.getChessPosition(kingRow + 1, kingCol - 1) instanceof Pawn) {
						return true; //All the conditions were met
					}
				}
			}
		}
		return false;
	}
	
	//Simply executes a move that has been previously checked and is legal.
	public void executeCheckedMove(ChessBoard board) { 
		board.setPosition(this.rowDes, this.colDes, board.getChessPosition(this.row, this.col));
		deleteMovedPiece(this.row, this.col, board);
	}
	
	public void executeCaptureMove(ChessBoard board) {
		if((board.getChessPosition(this.rowDes, this.colDes).getWhite() && (this.getPiece().getWhite())) ||
				((!board.getChessPosition(this.rowDes, this.colDes).getWhite() && (!this.getPiece().getWhite())))) {
				//If the player tried to capture an ally piece.
			throw new GameError("Destination position already occupied by an ally piece! (Error Code 004)");
		} else { //If he's capturing an enemy piece.
			if(board.getChessPosition(this.rowDes, this.colDes) instanceof King) { //You can't capture the king!
				throw new GameError("Checkmate? This point should be unreachable. (Error Code 005)");
			} else {
				executeCheckedMove(board);
			}
		}
	}
	
	//Disable En Passant for every Pawn from the colour passed by parameter.
	public void disableEnPassant(boolean isWhite, List<Piece> chessPieces) {
		//TODO Check if this works
		if(isWhite) {
			((Pawn) chessPieces.get(ChessPieceID.WHITE_PAWN_A)).setPassant(false);
			((Pawn) chessPieces.get(ChessPieceID.WHITE_PAWN_B)).setPassant(false);
			((Pawn) chessPieces.get(ChessPieceID.WHITE_PAWN_C)).setPassant(false);
			((Pawn) chessPieces.get(ChessPieceID.WHITE_PAWN_D)).setPassant(false);
			((Pawn) chessPieces.get(ChessPieceID.WHITE_PAWN_E)).setPassant(false);
			((Pawn) chessPieces.get(ChessPieceID.WHITE_PAWN_F)).setPassant(false);
			((Pawn) chessPieces.get(ChessPieceID.WHITE_PAWN_G)).setPassant(false);
			((Pawn) chessPieces.get(ChessPieceID.WHITE_PAWN_H)).setPassant(false);
		} else {
			((Pawn) chessPieces.get(ChessPieceID.BLACK_PAWN_A)).setPassant(false);
			((Pawn) chessPieces.get(ChessPieceID.BLACK_PAWN_B)).setPassant(false);
			((Pawn) chessPieces.get(ChessPieceID.BLACK_PAWN_C)).setPassant(false);
			((Pawn) chessPieces.get(ChessPieceID.BLACK_PAWN_D)).setPassant(false);
			((Pawn) chessPieces.get(ChessPieceID.BLACK_PAWN_E)).setPassant(false);
			((Pawn) chessPieces.get(ChessPieceID.BLACK_PAWN_F)).setPassant(false);
			((Pawn) chessPieces.get(ChessPieceID.BLACK_PAWN_G)).setPassant(false);
			((Pawn) chessPieces.get(ChessPieceID.BLACK_PAWN_H)).setPassant(false);
		}
	}
	
	//Shows whether the current player is moving his own pieces or trying to move the opponent's ones.
	public boolean checkTurn(ChessBoard board) {
		//this.getPiece() returns the piece to which the move belongs!
		return ((this.getPiece().getWhite() && (board.getChessPosition(this.row, this.col)).getWhite()) ||
				(!this.getPiece().getWhite() && !(board.getChessPosition(this.row, this.col)).getWhite()));
	}
	
	//Due to the peculiar pattern of pawn movement, it is required to have two
	//different functions, since white pawns move upwards, and black ones move downwards.
	public void executePawnMove(ChessBoard board, List<Piece> pieces) {
		if(this.getPiece().getWhite())
			executeWhitePawnMove(board, pieces);
		else
			executeBlackPawnMove(board, pieces);
	}
	
	private void executeWhitePawnMove(ChessBoard board, List<Piece> pieces) {
		//Check if this pawn is trying to capture a piece. (Diagonal move)
		if(this.rowDes == this.row - 1 && (this.colDes == this.col + 1 || this.colDes == this.col - 1)) {
			if(board.getChessPosition(this.rowDes, this.colDes) != null) { //The destination position isn't empty
				executeCaptureMove(board); //This already checks the kind of piece that is in the destination position and if it can be captured.
				((Pawn) board.getChessPosition(this.rowDes, this.colDes)).setFirstMove(false);
			} else if((board.getChessPosition(this.row, this.col + 1) instanceof Pawn) && (this.colDes == this.col + 1) || //There is a pawn on the right
					(board.getChessPosition(this.row, this.col - 1) instanceof Pawn) && (this.colDes == this.col - 1)) { //or the left for En Passant capture
				//No need to check that destination is null, that's checked above.
				if(this.colDes == this.col + 1) { //Capture to the right
					if(!board.getChessPosition(this.row, this.col + 1).getWhite() && //Check that the pawn is black
							((Pawn) board.getChessPosition(this.row, this.col + 1)).getPassant()) { //and it can be captured En Passant for this turn.						
						executeCheckedMove(board);
						board.setPosition(this.row, this.col + 1, null); //Deletes the captured piece
					} else { //Trying to capture either your own pawn or a pawn that can't be captured En Passant
						throw new GameError("Invalid move, try again. (Error 001)");
					}
				} else if (this.colDes == this.col - 1) { //Capture to the left.
					if(!board.getChessPosition(this.row, this.col - 1).getWhite() && //Check that the pawn is black
							((Pawn) board.getChessPosition(this.row, this.col - 1)).getPassant()) { //and it can be captured En Passant for this turn.
						executeCheckedMove(board);
						board.setPosition(this.row, this.col - 1, null); //Deletes the captured piece
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
				((Pawn) board.getChessPosition(this.rowDes, this.colDes)).setFirstMove(false);
			} else {
				throw new GameError("Invalid move, try again. (Error 005)");
			}
		} else if(this.col == this.colDes && this.row - 2 == this.rowDes) { //Check if it's making an opening move (Double).
			//TODO Check En Passant.
			if(((Pawn) board.getChessPosition(this.row, this.col)).getFirstMove()) { //Checks if it's the pawn's first move.
				if(board.getChessPosition(this.rowDes, this.colDes) != null) {
					throw new GameError("Invalid move, the position is occupied, try again. (Error 006)");
				} else if (!checkPiecesInbetween(this.row, this.col, this.rowDes, this.colDes)) {
					throw new GameError("Invalid move, you can't skip through other pieces, try again. (Error 007)");
				} else {
					executeCheckedMove(board);
					((Pawn) board.getChessPosition(this.rowDes, this.colDes)).setPassant(true); //This pawn can be captured En Passant in the next move.
					((Pawn) board.getChessPosition(this.rowDes, this.colDes)).setFirstMove(false);
				}
			} else {
				throw new GameError("Cannot make a double move with this Pawn.");
			}
		} else { //The move is not valid.
			throw new GameError("Invalid move, try again. (Error 008)");
		}
		
		//You can only get here if everything went right during the execution of the move.
		if(checkPromotion(board)) {
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
		}
	}
	
	private void executeBlackPawnMove(ChessBoard board, List<Piece> pieces) {
		//Check if this pawn is trying to capture a piece. (Diagonal move)
		if(this.rowDes == this.row + 1 && (this.colDes == this.col + 1 || this.colDes == this.col - 1)) {
			if(board.getChessPosition(this.rowDes, this.colDes) != null) { //The destination position isn't empty
				executeCaptureMove(board); //This already checks the kind of piece that is in the destination position and if it can be captured.
				((Pawn) board.getChessPosition(this.rowDes, this.colDes)).setFirstMove(false);
			} else if((board.getChessPosition(this.row, this.col + 1) instanceof Pawn) && (this.colDes == this.col + 1) || //There is a pawn on the right
					(board.getChessPosition(this.row, this.col - 1) instanceof Pawn) && (this.colDes == this.col - 1)) { //or the left for En Passant capture
				//No need to check that destination is null, that's checked above.
				if(this.colDes == this.col + 1) { //Capture to the right
					if(board.getChessPosition(this.row, this.col + 1).getWhite() && //Check that the pawn is white
							((Pawn) board.getChessPosition(this.row, this.col + 1)).getPassant()) { //and it can be captured En Passant for this turn.						
						executeCheckedMove(board);
						board.setPosition(this.row, this.col + 1, null); //Deletes the captured piece
					} else { //Trying to capture either your own pawn or a pawn that can't be captured En Passant
						throw new GameError("Invalid move, try again. (Error 001)");
					}
				} else if (this.colDes == this.col - 1) { //Capture to the left.
					if(board.getChessPosition(this.row, this.col - 1).getWhite() && //Check that the pawn is white
							((Pawn) board.getChessPosition(this.row, this.col - 1)).getPassant()) { //and it can be captured En Passant for this turn.
						executeCheckedMove(board);
						board.setPosition(this.row, this.col - 1, null); //Deletes the captured piece
					} else { //Trying to capture own piece En Passant
						throw new GameError("Invalid move, try again. (Error 002)");
					}
				} else { //Unrecognised En Passant move (not left nor right?)
					throw new GameError("Invalid move, try again. (Error 003)");
				}
			} else { //Pawn invalid diagonal move.
				throw new GameError("Invalid move, try again. (Error 004)");
			}
		} else if(this.col == this.colDes && this.row + 1 == this.rowDes) { //Check if it's making a simple move
			if(board.getChessPosition(this.rowDes, this.colDes) == null) { //Working.
				executeCheckedMove(board);
				((Pawn) board.getChessPosition(this.rowDes, this.colDes)).setFirstMove(false);
			} else {
				throw new GameError("Invalid move, try again. (Error 005)");
			}
		} else if(this.col == this.colDes && this.row + 2 == this.rowDes) { //Check if it's making an opening move (Double).
			//TODO Check En Passant.
			if(((Pawn) board.getChessPosition(this.row, this.col)).getFirstMove()) { //Checks if it's the pawn's first move.
				if(board.getChessPosition(this.rowDes, this.colDes) != null) {
					throw new GameError("Invalid move, the position is occupied, try again. (Error 006)");
				} else if (!checkPiecesInbetween(this.row, this.col, this.rowDes, this.colDes)) {
					throw new GameError("Invalid move, you can't skip through other pieces, try again. (Error 007)");
				} else {
					executeCheckedMove(board);
					((Pawn) board.getChessPosition(this.rowDes, this.colDes)).setPassant(true); //This pawn can be captured En Passant in the next move.
					((Pawn) board.getChessPosition(this.rowDes, this.colDes)).setFirstMove(false);
				}
			} else {
				throw new GameError("Cannot make a double move with this Pawn.");
			}
		} else { //The move is not valid.
			throw new GameError("Invalid move, try again. (Error 008)");
		}
		
		//You can only get here if everything went right during the execution of the move.
		if(checkPromotion(board)) {
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
		}
	}
	
	private boolean checkPromotion(ChessBoard board) { //This function should only be called from within the Pawn movement function for it to work properly.
		return (board.getChessPosition(this.rowDes, this.colDes).getWhite() && this.rowDes == 0) || //If it's white and has reached the top
				(!board.getChessPosition(this.rowDes, this.colDes).getWhite() && this.rowDes == 7); //If it's black and has reached the bottom
	}
	
	private void executePromotion(ChessBoard board, ChessPiece p) { //The piece must be properly created in another function. Usually executeWhite/BlackPawnMove().
		board.setPosition(this.rowDes, this.colDes, p);
	}

	public void executeRookMove(ChessBoard board, List<Piece> pieces) {
		if(checkHorizVertMove()) { //Check that it's moving either horizontally or vertically.
			if(board.getChessPosition(this.rowDes, this.colDes) != null) { //Trying to capture a piece.
				if((board.getChessPosition(this.rowDes, this.colDes).getWhite() && this.getPiece().getWhite()) ||
						(!board.getChessPosition(this.rowDes, this.colDes).getWhite() && !this.getPiece().getWhite())) {
					//Check that you're not trying to capture your own pieces.
					throw new GameError("Invalid move, the destination is occupied by an ally piece, try again. (Error 006)");
				} else if (!checkPiecesInbetween(this.row, this.col, this.rowDes, this.colDes)) { //Check there are no pieces inbetween.
					throw new GameError("Invalid move, you can't skip through other pieces, try again. (Error 007)");
				} else { //Everything correct, we can execute the move.
					executeCaptureMove(board); 
					((Rook) board.getChessPosition(this.rowDes, this.colDes)).setCastle(false); //This rook can't Castle since it moved.
				}
			} else { //The destination position is empty.
				if (!checkPiecesInbetween(this.row, this.col, this.rowDes, this.colDes)) { //Check there are no pieces inbetween.
					throw new GameError("Invalid move, you can't skip through other pieces, try again. (Error 007)");
				} else { //We can proceed to the move.
					executeCheckedMove(board);
					((Rook) board.getChessPosition(this.rowDes, this.colDes)).setCastle(false); //This rook can't Castle since it moved.
				}
			}
		} else { //It is not a vertical or horizontal move.
			throw new GameError("Invalid move, try again. (Error 008)");
		}
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
	
	public void executeBishopMove(ChessBoard board, List<Piece> pieces) {
		if(checkDiagonalMove()) { //Checks that it's moving diagonally.
			if(board.getChessPosition(this.rowDes, this.colDes) != null) { //Trying to capture a piece.
				if((board.getChessPosition(this.rowDes, this.colDes).getWhite() && this.getPiece().getWhite()) ||
						(!board.getChessPosition(this.rowDes, this.colDes).getWhite() && !this.getPiece().getWhite())) {
					//Check that you're not trying to capture your own pieces.
					throw new GameError("Invalid move, the destination is occupied by an ally piece, try again. (Error 006)");
				} else if (!checkPiecesInbetween(this.row, this.col, this.rowDes, this.colDes)) { //Check there are no pieces inbetween.
					throw new GameError("Invalid move, you can't skip through other pieces, try again. (Error 007)");
				} else { //Everything correct, we can execute the move.
					executeCaptureMove(board);
				}
			} else { //The destination position is empty.
				if (!checkPiecesInbetween(this.row, this.col, this.rowDes, this.colDes)) { //Check there are no pieces inbetween.
					throw new GameError("Invalid move, you can't skip through other pieces, try again. (Error 007)");
				} else { //We can proceed to the move.
					executeCheckedMove(board);
				}
			}
		} else { //It is not a vertical or horizontal move.
			throw new GameError("Invalid move, try again. (Error 008)");
		}
	}

	public void executeQueenMove(ChessBoard board, List<Piece> pieces) {
		if(checkDiagonalMove() || checkHorizVertMove()) { //Checks that it's moving diagonally or horizontally but not in any other way.
			if(board.getChessPosition(this.rowDes, this.colDes) != null) { //Trying to capture a piece.
				if((board.getChessPosition(this.rowDes, this.colDes).getWhite() && this.getPiece().getWhite()) ||
						(!board.getChessPosition(this.rowDes, this.colDes).getWhite() && !this.getPiece().getWhite())) {
					//Check that you're not trying to capture your own pieces.
					throw new GameError("Invalid move, the destination is occupied by an ally piece, try again. (Error 006)");
				} else if (!checkPiecesInbetween(this.row, this.col, this.rowDes, this.colDes)) { //Check there are no pieces inbetween.
					throw new GameError("Invalid move, you can't skip through other pieces, try again. (Error 007)");
				} else { //Everything correct, we can execute the move.
					executeCaptureMove(board); 
				}
			} else { //The destination position is empty.
				if (!checkPiecesInbetween(this.row, this.col, this.rowDes, this.colDes)) { //Check there are no pieces inbetween.
					throw new GameError("Invalid move, you can't skip through other pieces, try again. (Error 007)");
				} else { //We can proceed to the move.
					executeCheckedMove(board);
				}
			}
		} else { //It is not a vertical or horizontal move.
			throw new GameError("Invalid move, try again. (Error 008)");
		}
	}
	
	public void executeKingMove(ChessBoard board, List<Piece> pieces) {
		if(Math.abs(this.row - this.rowDes) <= 1 && Math.abs(this.col - this.colDes) <= 1) {
			//Check that it's moving only 1 tile away.
			if(board.getChessPosition(this.rowDes, this.colDes) != null) { //Trying to capture a piece.
				if((board.getChessPosition(this.rowDes, this.colDes).getWhite() && this.getPiece().getWhite()) ||
						(!board.getChessPosition(this.rowDes, this.colDes).getWhite() && !this.getPiece().getWhite())) {
					//Check that you're not trying to capture your own pieces.
					throw new GameError("Invalid move, the destination is occupied by an ally piece, try again. (Error 006)");
				} else { //Everything correct, we can execute the move.
					executeCaptureMove(board); 
					((King) board.getChessPosition(this.rowDes, this.colDes)).setCastle(false); //This King can't Castle since it just moved.
				}
			} else { //The destination position is empty.
					executeCheckedMove(board);
					((King) board.getChessPosition(this.rowDes, this.colDes)).setCastle(false); //This King can't Castle since it just moved.
			}
		} else if(board.getChessPosition(this.row, this.col).getWhite() && ((this.row == 7 && this.col == 4) &&
				(this.rowDes == 7 && (this.colDes == 2 || this.colDes == 6)))) { //Trying to Castle white king.
			if(((King) board.getChessPosition(this.row, this.col)).getCastle()) { //Check if the King can castle.
				if(this.colDes == 6) { //Short castling
					if(board.getChessPosition(7, 7) != null && board.getChessPosition(7, 7) instanceof Rook) { //Check that there's a rook
						if(((Rook) board.getChessPosition(7, 7)).getCastle()) { //Check if the Rook can castle
							if(checkPiecesInbetween(this.row, this.col, 7, 7)) { //Check that there are no pieces inbetween
								if(!((King) board.getChessPosition(this.row, this.col)).getCheck()) { //Check that the king is not in check
									if(true) { //TODO Check that the king doesn't go through any check positions!
										executeCheckedMove(board); //Performs Castling...
										board.setPosition(7, 5, board.getChessPosition(7, 7));
										deleteMovedPiece(7, 7, board);
									} else {
										throw new GameError("Cannot perform Castling, the King would be in Check during the move! (Error 008)");
									}
								} else {
									throw new GameError("Cannot perform Castling, the King is in Check! (Error 008)");
								}
							} else {
								throw new GameError("Cannot perform Castling, there are pieces inbetween. (Error 008)");
							}
						} else {
							throw new GameError("Rook cannot perform Castling. (Error 008)");
						}
					} else {
						throw new GameError("Rook is missing for Castling. (Error 008)");
					}
				} else { //Long castling
					if(board.getChessPosition(7, 0) != null && board.getChessPosition(7, 0) instanceof Rook) { //Check that there's a rook
						if(((Rook) board.getChessPosition(7, 0)).getCastle()) { //Check if the Rook can castle
							if(checkPiecesInbetween(this.row, this.col, 7, 0)) { //Check that there are no pieces inbetween
								if(!((King) board.getChessPosition(this.row, this.col)).getCheck()) { //Check that the king is not in check
									if(true) { //TODO Check that the king doesn't go through any check positions!
										executeCheckedMove(board); //Performs Castling...
										board.setPosition(7, 3, board.getChessPosition(7, 0));
										deleteMovedPiece(7, 0, board);
									} else {
										throw new GameError("Cannot perform Castling, the King would be in Check during the move! (Error 008)");
									}
								} else {
									throw new GameError("Cannot perform Castling, the King is in Check! (Error 008)");
								}
							} else {
								throw new GameError("Cannot perform Castling, there are pieces inbetween. (Error 008)");
							}
						} else {
							throw new GameError("Rook cannot perform Castling. (Error 008)");
						}
					} else {
						throw new GameError("Rook is missing for Castling. (Error 008)");
					}
				}
			} else {
				throw new GameError("This King cannot Castle anymore. (Error 008)");
			}
		} else if(!board.getChessPosition(this.row, this.col).getWhite() && ((this.row == 0 && this.col == 4) &&
				(this.rowDes == 0 && (this.colDes == 2 || this.colDes == 6)))) { //Trying to Castle black king.
			if(((King) board.getChessPosition(this.row, this.col)).getCastle()) { //Check if the King can castle.
				if(this.colDes == 6) { //Short castling
					if(board.getChessPosition(0, 7) != null && board.getChessPosition(0, 7) instanceof Rook) { //Check that there's a rook
						if(((Rook) board.getChessPosition(0, 7)).getCastle()) { //Check if the Rook can castle
							if(checkPiecesInbetween(this.row, this.col, 0, 7)) { //Check that there are no pieces inbetween
								if(!((King) board.getChessPosition(this.row, this.col)).getCheck()) { //Check that the king is not in check
									if(true) { //TODO Check that the king doesn't go through any check positions!
										executeCheckedMove(board); //Performs Castling...
										board.setPosition(0, 5, board.getChessPosition(0, 7));
										deleteMovedPiece(0, 7, board);
									} else {
										throw new GameError("Cannot perform Castling, the King would be in Check during the move! (Error 008)");
									}
								} else {
									throw new GameError("Cannot perform Castling, the King is in Check! (Error 008)");
								}
							} else {
								throw new GameError("Cannot perform Castling, there are pieces inbetween. (Error 008)");
							}
						} else {
							throw new GameError("Rook cannot perform Castling. (Error 008)");
						}
					} else {
						throw new GameError("Rook is missing for Castling. (Error 008)");
					}
				} else { //Long castling
					if(board.getChessPosition(0, 0) != null && board.getChessPosition(0, 0) instanceof Rook) { //Check that there's a rook
						if(((Rook) board.getChessPosition(0, 0)).getCastle()) { //Check if the Rook can castle
							if(checkPiecesInbetween(this.row, this.col, 0, 0)) { //Check that there are no pieces inbetween
								if(!((King) board.getChessPosition(this.row, this.col)).getCheck()) { //Check that the king is not in check
									if(true) { //TODO Check that the king doesn't go through any check positions!
										executeCheckedMove(board); //Performs Castling...
										board.setPosition(0, 3, board.getChessPosition(0, 0));
										deleteMovedPiece(0, 0, board);
									} else {
										throw new GameError("Cannot perform Castling, the King would be in Check during the move! (Error 008)");
									}
								} else {
									throw new GameError("Cannot perform Castling, the King is in Check! (Error 008)");
								}
							} else {
								throw new GameError("Cannot perform Castling, there are pieces inbetween. (Error 008)");
							}
						} else {
							throw new GameError("Rook cannot perform Castling. (Error 008)");
						}
					} else {
						throw new GameError("Rook is missing for Castling. (Error 008)");
					}
				}
			} else {
				throw new GameError("This King cannot Castle anymore. (Error 008)");
			}
		} else { //Illegal move.
			throw new GameError("Invalid move, try again. (Error 008)");
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
	//
	//Returns true if the movement can be performed, returns false if it can't be performed!!!
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
		
		while(itCount < 10 && (rowIt != rowEnd || colIt != colEnd)) {
			rowIt += rowOffset;
			colIt += colOffset;
			
			if(rowIt == rowEnd && colIt == colEnd) break; //Destination position should not be checked so you can capture pieces.
			
			if(chessBoard.getPosition(rowIt, colIt) != null) //If it found a piece on the way.
				return false;
			
			itCount++;
		}
		
		if(itCount >= 10) 
			throw new GameError("Move direction bugged. This should never happen. (Error 017)");
		
		return true;
	} //If returned false, throw an exception from the suitable function.
	
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