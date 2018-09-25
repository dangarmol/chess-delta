package chess.game.mvc.model.chessFiles.chessAI.chessBoardEvaluators;

import chess.game.mvc.model.chessFiles.ChessBoard;
import chess.game.mvc.model.chessPieces.ChessPiece;

public interface ChessBoardEvaluator {
	
	/**
	 * This function should return a rating for the current state of the board.
	 * @param board
	 * @param currentPiece
	 * @param maxPiece
	 * @return
	 */
	public abstract double getRating(ChessBoard board, ChessPiece currentPiece, ChessPiece maxPiece);
}