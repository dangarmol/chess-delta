package chess.game.mvc.model.chessFiles;

import chess.game.mvc.model.chessPieces.ChessPiece;
import chess.game.mvc.model.genericGameFiles.BasicBoard;
import chess.game.mvc.model.genericGameFiles.Board;
import chess.game.mvc.model.genericGameFiles.GameError;
import chess.game.mvc.model.genericGameFiles.Piece;

/*public class ChessBoard extends FiniteRectBoard {

	private static final long serialVersionUID = -8595352893288103075L;

	public ChessBoard() {
		super(ChessConstants.BOARD_DIMS, ChessConstants.BOARD_DIMS);
	}
	
	//Returns the ChessPiece at the selected position if the current board is a ChessBoard
	public ChessPiece getChessPosition(int row, int col) {
		if (row < 0 || row >= rows || col < 0 || col >= cols) {
			throw new GameError("Trying to access an invalid position (" + row + "," + col + ")");
		}
		return (ChessPiece) board[row][col];
	}
	
	public ChessBoard copyChessBoard() {
		ChessBoard newboard = new ChessBoard();
		this.copyTo(newboard);
		return newboard;
	}
	
	
}*/

/**
 * An implementation of a finite rectangular board.
 * 
 * <p>
 * Implementacion de un tablero rectangular de dimensión finita.
 * 
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
	public ChessBoard() {
		this.rows = ChessConstants.BOARD_DIMS;
		this.cols = ChessConstants.BOARD_DIMS;
		board = new Piece[rows][cols];
	}

	@Override
	public void setPosition(int row, int col, Piece p) {
		if (row < 0 || row >= rows || col < 0 || col >= cols) {
			throw new GameError("Trying to access an invalid position (" + row + "," + col + ")");
		}

		board[row][col] = p;
	}

	@Override
	public Piece getPosition(int row, int col) { //May be worth overriding this for chess pieces.
		if (row < 0 || row >= rows || col < 0 || col >= cols) {
			throw new GameError("Trying to access an invalid position (" + row + "," + col + ")");
		}
		return board[row][col];
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
		ChessBoard newboard = new ChessBoard();
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
		StringBuilder render = new StringBuilder();

		int height = getRows();
		int width = getCols();

		for (int r = 0; r < height; ++r) {

			render.append("  +");
			for (int c = 0; c < width; ++c)
				render.append("---+");
			render.append("\n");
			render.append("" + (r % 10) + " |");

			for (int c = 0; c < width; ++c) {
				if (getPosition(r, c) == null) {
					render.append("   |");
				} else {
					render.append(" " + getPosition(r, c).toString().charAt(0) + " |");
				}
			} // for columns
			render.append("\n");
		} // for rows

		render.append("  +");
		for (int c = 0; c < width; ++c)
			render.append("---+");
		render.append("\n");
		render.append("   ");
		for (int c = 0; c < width; ++c)
			render.append(" " + (c % 10) + "  ");
		render.append("\n");

		return render.toString();
	}

	@Override
	public boolean isFull() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean isEmpty() {
		// TODO Auto-generated method stub
		return false;
	}

}