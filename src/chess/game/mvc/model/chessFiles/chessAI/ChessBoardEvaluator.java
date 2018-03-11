package chess.game.mvc.model.chessFiles.chessAI;

import chess.game.mvc.model.chessFiles.ChessBoard;
import chess.game.mvc.model.chessPieces.ChessPiece;

public interface ChessBoardEvaluator {
	
	/**
	 * This function should return a rating for the current state of the board from the perspective
	 * of the max player.
	 * @param board
	 * @param currentPiece
	 * @param maxPiece
	 * @return
	 */
	public abstract double getRating(ChessBoard board, ChessPiece currentPiece, ChessPiece maxPiece);
}