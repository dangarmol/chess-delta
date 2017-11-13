package chess.game.mvc.model.chessFiles;

import chess.game.mvc.model.chessPieces.ChessPiece;
import chess.game.mvc.model.genericGameFiles.FiniteRectBoard;
import chess.game.mvc.model.genericGameFiles.GameError;

public class ChessBoard extends FiniteRectBoard {

	public ChessBoard() {
		super(8, 8);
	}
	
	//Returns the ChessPiece at the selected position if the current board is a ChessBoard
	public ChessPiece getChessPosition(int row, int col) {
		if (row < 0 || row >= rows || col < 0 || col >= cols) {
			throw new GameError("Trying to access an invalid position (" + row + "," + col + ")");
		}
		return (ChessPiece) board[row][col];
	}
}