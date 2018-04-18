package chess.game.mvc.model.chessFiles;

import java.util.HashMap;

import chess.game.mvc.model.chessPieces.ChessPiece;
import chess.game.mvc.model.chessPieces.chessPiecesImp.Bishop;
import chess.game.mvc.model.chessPieces.chessPiecesImp.King;
import chess.game.mvc.model.chessPieces.chessPiecesImp.Knight;
import chess.game.mvc.model.chessPieces.chessPiecesImp.Pawn;
import chess.game.mvc.model.chessPieces.chessPiecesImp.Queen;
import chess.game.mvc.model.chessPieces.chessPiecesImp.Rook;
import chess.game.mvc.model.genericGameFiles.BasicBoard;
import chess.game.mvc.model.genericGameFiles.Board;
import chess.game.mvc.model.genericGameFiles.GameError;
import chess.game.mvc.model.genericGameFiles.Piece;

/**
 * An implementation of a chess board.
 */
public class ChessBoard extends BasicBoard {

	private static final long serialVersionUID = 1L;
	
	/**
	 * The internal representation of the board. Simply a matrix of objects of
	 * type {@link Piece}.
	 * 
	 * <p>
	 * Representacion interna del tablero: una matriz de objetos de tipo
	 * {@link Piece}.
	 */
	protected Piece[][] board;

	/**
	 * Number of columns in the board.
	 * <p>
	 * Numero de columnas del tablero.
	 */
	protected int cols;

	/**
	 * Number of rows in the board.
	 * <p>
	 * Numero de filas del tablero.
	 */
	protected int rows;

	/**
	 * Number of moves without moving a pawn or capturing a piece.
	 */
	protected int movesWithoutAction;
	
	protected boolean repetitionsRule;
	
	/**
	 * Keeps track of the last positions of the board to avoid 3 repeated positions in a match.
	 */
	protected HashMap<String, Integer> lastPositions;
	
	/**
	 * This constructor constructs a finite rectangular board of a given
	 * dimension.
	 * 
	 * <p>
	 * Construye un tablero rectangular finito de una dimension determinada.
	 * 
	 * @param rows
	 *            Number of rows.
	 *            <p>
	 *            Numero de filas.
	 * @param cols
	 *            Number of columns.
	 *            <p>
	 *            Numero de columnas.
	 */
	@SuppressWarnings({ "unchecked", "rawtypes" })
	public ChessBoard() {
		this.rows = ChessStatic.BOARD_DIMS;
		this.cols = ChessStatic.BOARD_DIMS;
		this.lastPositions = new HashMap();
		this.repetitionsRule = false;
		this.board = new Piece[rows][cols];
		this.movesWithoutAction = 0;
	}
	
	@SuppressWarnings({ "unchecked", "rawtypes" })
	public ChessBoard(HashMap<String, Integer> positions, boolean repetitions, int noAction) {
		this.rows = ChessStatic.BOARD_DIMS;
		this.cols = ChessStatic.BOARD_DIMS;
		this.lastPositions = new HashMap();
		this.lastPositions.putAll(positions);
		this.repetitionsRule = repetitions;
		this.board = new Piece[rows][cols];
		this.movesWithoutAction = noAction;
	}

	@Override
	public void setPosition(int row, int col, Piece p) {
		if (row < 0 || row >= rows || col < 0 || col >= cols) {
			throw new GameError("Trying to access an invalid position (" + row + "," + col + ")");
		}

		board[row][col] = p;
	}

	@Override
	public ChessPiece getPosition(int row, int col) { //May be worth overriding this for chess pieces.
		if (row < 0 || row >= rows || col < 0 || col >= cols) {
			throw new GameError("Trying to access an invalid position (" + row + "," + col + ")");
		}
		return (ChessPiece) board[row][col];
	}

	@Override
	public int getRows() {
		return rows;
	}

	@Override
	public int getCols() {
		return cols;
	}

	protected void copyTo(ChessBoard newboard) {

		// ask the super class to copy its stuff first.
		super.copyTo(newboard);

		// copy the actual board
		newboard.board = new Piece[rows][cols];
		for (int i = 0; i < rows; i++)
			for (int j = 0; j < cols; j++)
				newboard.board[i][j] = board[i][j];
		newboard.cols = cols;
		newboard.rows = rows;
	}

	@Override
	public Board copy() {
		ChessBoard newboard = new ChessBoard(this.lastPositions, this.repetitionsRule, this.movesWithoutAction);
		copyTo(newboard);
		return newboard;
	}

	/**
	 * Generates a string that represents the board. The symbols used to print
	 * the board are the first characters of the piece identifier.
	 * 
	 * <p>
	 * Genera un string que representa el tablero. El simbolo utilizado para
	 * cada ficha es el primer caracter de su id.
	 * 
	 * @return A string representation of the board.
	 */
	@Override
	public String toString() {
		//Modified this function to make each board string unique by using both ID characters.
		StringBuilder render = new StringBuilder();

		int height = getRows();
		int width = getCols();

		for (int r = 0; r < height; ++r) {

			render.append("  +");
			for (int c = 0; c < width; ++c)
				render.append("----+");
			render.append("\n");
			render.append("" + (r % 10) + " |");

			for (int c = 0; c < width; ++c) {
				if (getPosition(r, c) == null) {
					render.append("    |");
				} else {
					render.append(" " + getPosition(r, c).toString() + " |");
				}
			} // for columns
			render.append("\n");
		} // for rows

		render.append("  +");
		for (int c = 0; c < width; ++c)
			render.append("----+");
		render.append("\n");
		render.append("   ");
		for (int c = 0; c < width; ++c)
			render.append(" " + (c % 10) + "   ");
		render.append("\n");

		return render.toString();
	}
	
	//Returns the position of the king from the current player as an 2 digits integer.
	//Left digit represents row and right digit represents col. King at (2, 5), would be returned as "25".
	//Returns @ChessStatic.UNKNOWN if the king hasn't been found. An exception should be thrown if this happens.
	private int findKing(boolean isWhite) {
		for(int rowX = ChessStatic.MIN_DIM; rowX <= ChessStatic.MAX_DIM; rowX++) {
			for(int colY = ChessStatic.MIN_DIM; colY <= ChessStatic.MAX_DIM; colY++) {
				if(this.getPosition(rowX, colY) != null) {
					if(this.getPosition(rowX, colY) instanceof King && (((ChessPiece) this.getPosition(rowX, colY)).getWhite() == isWhite)) {
						return rowX * 10 + colY;
					}
				}
			}
		}
		return ChessStatic.UNKNOWN;
	}

	//Checks if the King for the colour passed by parameter is threatened by any piece.
	public boolean isKingInCheck(boolean isWhite) {
		int kingLocation = findKing(isWhite);
		if(kingLocation == ChessStatic.UNKNOWN) {
			throw new GameError("Internal error. King not found, this should never happen.");
		}
		int kingRow = kingLocation / 10;
		int kingCol = kingLocation % 10;
		
		//King can't be threatened by the other King. Since a King cannot move to a vulnerable position.
		//True means King is in check, False mean King is not in check.
		return checkHorizVertThreat(kingRow, kingCol) || checkDiagonalThreat(kingRow, kingCol) ||
				checkKnightThreat(kingRow, kingCol) || checkPawnThreat(kingRow, kingCol) ||
				checkKingsCollision(kingRow, kingCol);
	}
	
	//Checks if the King would move to a position threatened by the enemy King.
	private boolean checkKingsCollision(int kingRow, int kingCol) {
		for(int rowX = kingRow - 1; rowX <= kingRow + 1; rowX++) {
			for(int colY = kingCol - 1; colY <= kingCol + 1; colY++) {
				if(rowX >= ChessStatic.MIN_DIM && rowX <= ChessStatic.MAX_DIM &&
						colY >= ChessStatic.MIN_DIM && colY <= ChessStatic.MAX_DIM) { //Can't check outside the board!
					if(rowX != kingRow || colY != kingCol) { //To avoid checking the position where the current king is.
						if(this.getPosition(rowX, colY) != null) {
							if(this.getPosition(rowX, colY) instanceof King) {
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
	private boolean checkHorizVertThreat(int kingRow, int kingCol) {
		for(int rowX = kingRow + 1; rowX <= ChessStatic.MAX_DIM; rowX++) { //South Direction
			if(this.getPosition(rowX, kingCol) != null) { //If the position is empty, the loop keeps running.
				if(checkHorizVertAttacker(rowX, kingCol, ((ChessPiece) this.getPosition(kingRow, kingCol)).getWhite()))
					return true; //If it's not empty and this function above finds an attacker on the explored position, it returns true and breaks every loop.
				else
					break; //If it's not empty and it finds a piece that isn't a threat or is an ally, it breaks the current loop and starts the next one.
			}
		}
		
		for(int rowX = kingRow - 1; rowX >= ChessStatic.MIN_DIM; rowX--) { //North Direction
			if(this.getPosition(rowX, kingCol) != null) { //If the position is empty, the loop keeps running.
				if(checkHorizVertAttacker(rowX, kingCol, ((ChessPiece) this.getPosition(kingRow, kingCol)).getWhite()))
					return true; //If it's not empty and this function above finds an attacker on the explored position, it returns true and breaks every loop.
				else
					break; //If it's not empty and it finds a piece that isn't a threat or is an ally, it breaks the current loop and starts the next one.
			}
		}
		
		for(int colY = kingCol + 1; colY <= ChessStatic.MAX_DIM; colY++) { //East Direction
			if(this.getPosition(kingRow, colY) != null) { //If the position is empty, the loop keeps running.
				if(checkHorizVertAttacker(kingRow, colY, ((ChessPiece) this.getPosition(kingRow, kingCol)).getWhite()))
					return true; //If it's not empty and this function above finds an attacker on the explored position, it returns true and breaks every loop.
				else
					break; //If it's not empty and it finds a piece that isn't a threat or is an ally, it breaks the current loop and starts the next one.
			}
		}
		
		for(int colY = kingCol - 1; colY >= ChessStatic.MIN_DIM; colY--) { //West Direction
			if(this.getPosition(kingRow, colY) != null) { //If the position is empty, the loop keeps running.
				if(checkHorizVertAttacker(kingRow, colY, ((ChessPiece) this.getPosition(kingRow, kingCol)).getWhite()))
					return true; //If it's not empty and this function above finds an attacker on the explored position, it returns true and breaks every loop.
				else
					break; //If it's not empty and it finds a piece that isn't a threat or is an ally, it breaks the current loop and returns false, since none of the loops found a threat.
			}
		}
		return false;
	}
	
	private boolean checkHorizVertAttacker(int rowX, int colY, boolean isWhiteKing) {
		return (this.getPosition(rowX, colY) instanceof Queen || this.getPosition(rowX, colY) instanceof Rook) && //Checks that there's either a Queen or a Rook
				(!((ChessPiece) this.getPosition(rowX, colY)).getWhite() == isWhiteKing); //And that the king is not the same color as the piece.
	}
	
	//Checks if the king is threatened from any diagonal by either a Queen or a Bishop.
	private boolean checkDiagonalThreat(int kingRow, int kingCol) {
		int rowOffSet = ChessStatic.NEGATIVE, colOffSet = ChessStatic.NEGATIVE;
		int multiplier = 1;
		int rowX, colY;
		rowX = kingRow + rowOffSet * multiplier;
		colY = kingCol + colOffSet * multiplier;
		while(rowX >= ChessStatic.MIN_DIM && colY >= ChessStatic.MIN_DIM) { //NorthWest Direction
			if(this.getPosition(rowX, colY) != null) { //If the position is empty, the loop keeps running.
				if(checkDiagonalAttacker(rowX, colY, ((ChessPiece) this.getPosition(kingRow, kingCol)).getWhite()))
					return true; //If it's not empty and this function above finds an attacker on the explored position, it returns true and breaks every loop.
				else
					break; //If it's not empty and it finds a piece that isn't a threat or is an ally, it breaks the current loop and starts the next one.
			}
			multiplier++;
			rowX = kingRow + rowOffSet * multiplier;
			colY = kingCol + colOffSet * multiplier;
		}
		
		multiplier = 1; rowOffSet = ChessStatic.NEGATIVE; colOffSet = ChessStatic.POSITIVE;
		rowX = kingRow + rowOffSet * multiplier;
		colY = kingCol + colOffSet * multiplier;
		while(rowX >= ChessStatic.MIN_DIM && colY <= ChessStatic.MAX_DIM) { //NorthEast Direction
			if(this.getPosition(rowX, colY) != null) { //If the position is empty, the loop keeps running.
				if(checkDiagonalAttacker(rowX, colY, ((ChessPiece) this.getPosition(kingRow, kingCol)).getWhite()))
					return true; //If it's not empty and this function above finds an attacker on the explored position, it returns true and breaks every loop.
				else
					break; //If it's not empty and it finds a piece that isn't a threat or is an ally, it breaks the current loop and starts the next one.
			}
			multiplier++;
			rowX = kingRow + rowOffSet * multiplier;
			colY = kingCol + colOffSet * multiplier;
		}
		
		multiplier = 1; rowOffSet = ChessStatic.POSITIVE; colOffSet = ChessStatic.POSITIVE;
		rowX = kingRow + rowOffSet * multiplier;
		colY = kingCol + colOffSet * multiplier;
		while(rowX <= ChessStatic.MAX_DIM && colY <= ChessStatic.MAX_DIM) { //SouthEast Direction
			if(this.getPosition(rowX, colY) != null) { //If the position is empty, the loop keeps running.
				if(checkDiagonalAttacker(rowX, colY, ((ChessPiece) this.getPosition(kingRow, kingCol)).getWhite()))
					return true; //If it's not empty and this function above finds an attacker on the explored position, it returns true and breaks every loop.
				else
					break; //If it's not empty and it finds a piece that isn't a threat or is an ally, it breaks the current loop and starts the next one.
			}
			multiplier++;
			rowX = kingRow + rowOffSet * multiplier;
			colY = kingCol + colOffSet * multiplier;
		}
		
		multiplier = 1; rowOffSet = ChessStatic.POSITIVE; colOffSet = ChessStatic.NEGATIVE;
		rowX = kingRow + rowOffSet * multiplier;
		colY = kingCol + colOffSet * multiplier;
		while(rowX <= ChessStatic.MAX_DIM && colY >= ChessStatic.MIN_DIM) { //SouthWest Direction
			rowX = kingRow + rowOffSet * multiplier;
			colY = kingCol + colOffSet * multiplier;
			if(this.getPosition(rowX, colY) != null) { //If the position is empty, the loop keeps running.
				if(checkDiagonalAttacker(rowX, colY, ((ChessPiece) this.getPosition(kingRow, kingCol)).getWhite()))
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
	
	private boolean checkDiagonalAttacker(int rowX, int colY, boolean isWhiteKing) {
		return (this.getPosition(rowX, colY) instanceof Queen || this.getPosition(rowX, colY) instanceof Bishop) && //Checks that there's either a Queen or a Bishop
				(!((ChessPiece) this.getPosition(rowX, colY)).getWhite() == isWhiteKing); //And that the king is not the same color as the piece.
	}

	private boolean checkKnightThreat(int kingRow, int kingCol) {
		boolean isWhiteKing = ((ChessPiece) this.getPosition(kingRow, kingCol)).getWhite();
		
		if(kingRow - 2 >= ChessStatic.MIN_DIM) { //Check if going 2 rows up is within the board range.
			if(kingCol - 1 >= ChessStatic.MIN_DIM) { //Check if going 1 column left is within the board range
				if(this.getPosition(kingRow - 2, kingCol - 1) != null &&
						this.getPosition(kingRow - 2, kingCol - 1) instanceof Knight &&
						((ChessPiece) this.getPosition(kingRow - 2, kingCol - 1)).getWhite() != isWhiteKing) {
					return true; //If the explored position is not empty, and has a enemy Knight
				}
			}
			
			if(kingCol + 1 <= ChessStatic.MAX_DIM) { //Check if going 1 column right is within the board range
				if(this.getPosition(kingRow - 2, kingCol + 1) != null &&
						this.getPosition(kingRow - 2, kingCol + 1) instanceof Knight &&
						((ChessPiece) this.getPosition(kingRow - 2, kingCol + 1)).getWhite() != isWhiteKing) {
					return true; //If the explored position is not empty, and has a enemy Knight
				}
			}
		}
		
		if(kingRow + 2 <= ChessStatic.MAX_DIM) { //Check if going 2 rows down is within the board range.
			if(kingCol - 1 >= ChessStatic.MIN_DIM) { //Check if going 1 column left is within the board range
				if(this.getPosition(kingRow + 2, kingCol - 1) != null &&
						this.getPosition(kingRow + 2, kingCol - 1) instanceof Knight &&
						((ChessPiece) this.getPosition(kingRow + 2, kingCol - 1)).getWhite() != isWhiteKing) {
					return true; //If the explored position is not empty, and has a enemy Knight
				}
			}
			
			if(kingCol + 1 <= ChessStatic.MAX_DIM) { //Check if going 1 column right is within the board range
				if(this.getPosition(kingRow + 2, kingCol + 1) != null &&
						this.getPosition(kingRow + 2, kingCol + 1) instanceof Knight &&
						((ChessPiece) this.getPosition(kingRow + 2, kingCol + 1)).getWhite() != isWhiteKing) {
					return true; //If the explored position is not empty, and has a enemy Knight
				}
			}
		}
		
		if(kingCol - 2 >= ChessStatic.MIN_DIM) { //Check if going 2 columns left is within the board range.
			if(kingRow - 1 >= ChessStatic.MIN_DIM) { //Check if going 1 row up is within the board range
				if(this.getPosition(kingRow - 1, kingCol - 2) != null &&
						this.getPosition(kingRow - 1, kingCol - 2) instanceof Knight &&
						((ChessPiece) this.getPosition(kingRow - 1, kingCol - 2)).getWhite() != isWhiteKing) {
					return true; //If the explored position is not empty, and has a enemy Knight
				}
			}
			
			if(kingRow + 1 <= ChessStatic.MAX_DIM) { //Check if going 1 row down is within the board range
				if(this.getPosition(kingRow + 1, kingCol - 2) != null &&
						this.getPosition(kingRow + 1, kingCol - 2) instanceof Knight &&
						((ChessPiece) this.getPosition(kingRow + 1, kingCol - 2)).getWhite() != isWhiteKing) {
					return true; //If the explored position is not empty, and has a enemy Knight
				}
			}
		}
		
		if(kingCol + 2 <= ChessStatic.MAX_DIM) { //Check if going 2 columns right is within the board range.
			if(kingRow - 1 >= ChessStatic.MIN_DIM) { //Check if going 1 row up is within the board range
				if(this.getPosition(kingRow - 1, kingCol + 2) != null &&
						this.getPosition(kingRow - 1, kingCol + 2) instanceof Knight &&
						((ChessPiece) this.getPosition(kingRow - 1, kingCol + 2)).getWhite() != isWhiteKing) {
					return true; //If the explored position is not empty, and has a enemy Knight
				}
			}
			
			if(kingRow + 1 <= ChessStatic.MAX_DIM) { //Check if going 1 row down is within the board range
				if(this.getPosition(kingRow + 1, kingCol + 2) != null &&
						this.getPosition(kingRow + 1, kingCol + 2) instanceof Knight &&
						((ChessPiece) this.getPosition(kingRow + 1, kingCol + 2)).getWhite() != isWhiteKing) {
					return true; //If the explored position is not empty, and has a enemy Knight
				}
			}
		}
		
		return false; //If none of the above conditions were met at some point
	}

	//Checks if the king is threatened by a Pawn.
	private boolean checkPawnThreat(int kingRow, int kingCol) {
		if(((ChessPiece) this.getPosition(kingRow, kingCol)).getWhite() && kingRow > ChessStatic.MIN_DIM + 1) { //If the king is white and not on the top 2 rows (since pawns can't be on the top row anyway).
			if(kingCol + 1 <= ChessStatic.MAX_DIM && this.getPosition(kingRow - 1, kingCol + 1) != null) { //Checking the right diagonal for a pawn.
				if(!((ChessPiece) this.getPosition(kingRow - 1, kingCol + 1)).getWhite()) { //If there is a black piece on that position
					if(this.getPosition(kingRow - 1, kingCol + 1) instanceof Pawn) { //If that piece is a pawn
						return true; //All the conditions were met.
					}
				}
			}
			
			if(kingCol - 1 <= ChessStatic.MAX_DIM && this.getPosition(kingRow - 1, kingCol - 1) != null) { //Checking the left diagonal.
				if(!((ChessPiece) this.getPosition(kingRow - 1, kingCol - 1)).getWhite()) { //If there is a black piece on that position
					if(this.getPosition(kingRow - 1, kingCol - 1) instanceof Pawn) { //If that piece is a pawn
						return true; //All the conditions were met.
					}
				}
			}
		} else if(!((ChessPiece) this.getPosition(kingRow, kingCol)).getWhite() && kingRow < ChessStatic.MAX_DIM - 1) { //If the king is black and not on the bottom 2 rows (since pawns can't be on the bottom row anyway).
			if(kingCol + 1 <= ChessStatic.MAX_DIM && this.getPosition(kingRow + 1, kingCol + 1) != null) { //Checking the right diagonal for a pawn.
				if(((ChessPiece) this.getPosition(kingRow + 1, kingCol + 1)).getWhite()) { //If there is a white piece on that position
					if(this.getPosition(kingRow + 1, kingCol + 1) instanceof Pawn) { //If that piece is a pawn
						return true; //All the conditions were met.
					}
				}
			}
			
			if(kingCol - 1 <= ChessStatic.MAX_DIM && this.getPosition(kingRow + 1, kingCol - 1) != null) { //Checking the left diagonal.
				if(((ChessPiece) this.getPosition(kingRow + 1, kingCol - 1)).getWhite()) { //If there is a white piece on that position
					if(this.getPosition(kingRow + 1, kingCol - 1) instanceof Pawn) { //If that piece is a pawn
						return true; //All the conditions were met.
					}
				}
			}
		}
		
		return false;
	}
	
	public void addCurrentPosition() {
		if(this.lastPositions.get(this.toString()) == null) {
			this.lastPositions.put(this.toString(), 1);
		} else {
			Integer repeats = this.lastPositions.get(this.toString());
			if(repeats + 1 == 3) { //If this is reached, it means that it will be incremented to 3 on this iteration.
				this.repetitionsRule = true;
			} else {
				this.lastPositions.put(this.toString(), repeats + 1);
			}
		}
	}
	
	public void resetBoardHistory() {
		this.repetitionsRule = false;
		this.lastPositions.clear();
	}
	
	public void increaseMovesWithoutAction() {
		this.movesWithoutAction++;
	}
	
	public void resetMovesRules() {
		this.movesWithoutAction = 0;
		resetBoardHistory();
	}
	
	/**
	 * @return @true if there have been more than 50 moves without a capture or pawn movement of 3 repeated boards. @false otherwise
	 */
	public boolean checkMovesRulesLimit() {
		return this.movesWithoutAction >= 50 || this.repetitionsRule;
	}
	
	@Override
	public boolean isFull() {
		return false;
	}

	@Override
	public boolean isEmpty() {
		return false;
	}

}