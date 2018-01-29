package chess.game.mvc.model.chessFiles.chessAI;

import java.util.List;

import chess.game.mvc.model.chessFiles.ChessBoard;
import chess.game.mvc.model.chessFiles.ChessConstants;
import chess.game.mvc.model.chessFiles.ChessMove;
import chess.game.mvc.model.chessPieces.ChessPiece;
import chess.game.mvc.model.genericGameFiles.GameMove;

public class ChessBoardEvaluator {
	
	public boolean whiteKingInCheck;
	
	public ChessBoardEvaluator() {}
	
	/* HEURISTICS:
	 * The more possible moves, the better the rating.
	 * Is this a NegMax implementation?
	 */
	public double getRating(ChessBoard board, ChessPiece currentPiece, ChessPiece maxPiece, List<GameMove> validMoves) {
		double cumulativeRating = 0;
		
		if(this.isCheckMate(board, currentPiece.getWhite(), validMoves.isEmpty())) {
			//First check if someone won. If someone did, return Double max or min value, depending on whether it's a
			//win for the Max player or the opposite.
			if(currentPiece.getWhite() == maxPiece.getWhite()) {
				//If the player that won is the same as the one that is being maxed...
				return Double.MAX_VALUE;
			} else {
				//Otherwise...
				return Double.MIN_VALUE;
			}
		} else if(validMoves.isEmpty()) {
			//There is a stalemate.
			return 0;
		}
		
		cumulativeRating += this.rateBoardByPieces(board, maxPiece.getWhite()); //Adds the max player board rating by pieces.
		cumulativeRating -= this.rateBoardByPieces(board, !maxPiece.getWhite()); //Subtracts the min player board rating by pieces.
		
		return cumulativeRating;
	}
	
	/*
	 * HEURISTICS:
	 * Pawn is worth exponentially more more the closer to promoting it is, e.g 2^(rowNumber - 2) --> 5, 6, 7, 8, 13, 28, 45 (Last row means there is a queen)
	 * Proportionally, pieces are worth: Queen = 45, Rook = 25, Bishop = 15, Knight = 15, Pawn = 5
	 */
	private double rateBoardByPieces(ChessBoard board, boolean isWhite) {
		double rating = 0;
		
		return rating;
	}
	
	/**
	 * Checks if the game is over for the current player!!
	 * If white just made a checkmate move and this method is called as isGameOver(board, false, true), it should return true.
	 * @return true if game is won BY CHECKMATE, NOT stalemate, false otherwise.
	 */
	private boolean isCheckMate(ChessBoard board, boolean isWhiteTurn, boolean emptyMoves) { //TODO This function needs to be checked!
		if(emptyMoves && new ChessMove().isKingInCheck(board, isWhiteTurn)) //TODO This should be changed to avoid creating new ChessMove.
			return true;
		else
			return false;
	}
}