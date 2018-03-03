package chess.game.mvc.model.chessFiles.chessAI;

import java.util.List;

import chess.game.mvc.model.chessFiles.ChessBoard;
import chess.game.mvc.model.chessFiles.ChessConstants;
import chess.game.mvc.model.chessFiles.ChessMove;
import chess.game.mvc.model.chessPieces.ChessPiece;
import chess.game.mvc.model.chessPieces.chessPiecesImp.Bishop;
import chess.game.mvc.model.chessPieces.chessPiecesImp.King;
import chess.game.mvc.model.chessPieces.chessPiecesImp.Knight;
import chess.game.mvc.model.chessPieces.chessPiecesImp.Pawn;
import chess.game.mvc.model.chessPieces.chessPiecesImp.Queen;
import chess.game.mvc.model.chessPieces.chessPiecesImp.Rook;
import chess.game.mvc.model.genericGameFiles.GameError;
import chess.game.mvc.model.genericGameFiles.GameMove;

public class ChessBoardEvaluator {
	
	public boolean whiteKingInCheck;
	
	public ChessBoardEvaluator() {}
	
	/**
	 * TODO:
	 * Look up killer heuristic
	 * Move that makes the opponent always lose.
	 * Alpha 1000 Beta -1000
	 * Once you find a winning move, it stops iterating/searching.
	 */
	
	/* HEURISTICS:
	 * If the player causes the enemy to be in check, the rating is increased.
	 * A Checkmate returns an infinite or minus infinite value.
	 * A Stalemate returns a neutral (zero) rating.
	 * The more possible moves, the better the rating.
	 * Finally, the rating is also increased by the pieces the player has and the position where they are.
	 */
	public double getRating(ChessBoard board, ChessPiece currentPiece, ChessPiece maxPiece, List<GameMove> validMoves) {
		//TODO Test this function!
		double cumulativeRating = 0;
		
		boolean whiteInCheck = isWhiteInCheck(board);
		boolean blackInCheck;
		
		if(!whiteInCheck) {
			blackInCheck = isBlackInCheck(board);
		} else {
			blackInCheck = false; //It's not possible to have both players in check at the same time.
		}
		
		if(whiteInCheck || blackInCheck) { //If the current piece performed a check move.
			if(currentPiece.getWhite() == maxPiece.getWhite()) {
				//If the player that is in check is the current player...
				cumulativeRating += 50;
			} else {
				//If it's the opponent...
				cumulativeRating -= 50;
			}
		}
		
		if(isCheck && validMoves.isEmpty()) { //There is a checkmate
			//First check if someone won. If someone did, return Double max or min value, depending on whether it's a
			//win for the Max player or the opposite.
			if(currentPiece.getWhite() == maxPiece.getWhite()) {
				//If the player that won is the max player...
				return Double.MAX_VALUE; //TODO Don't use infinity for winning. Use 1000 or some other high value.
			} else {
				//Otherwise...
				return -Double.MAX_VALUE; //TODO Add bugfix: This was previously Double.MIN_VALUE!!!
			}
		} else if(validMoves.isEmpty()) {
			//There is a stalemate.
			return 0;
		}
		
		if(currentPiece.getWhite() == maxPiece.getWhite()) {
			cumulativeRating += validMoves.size();
		} else {
			cumulativeRating -= validMoves.size();
		}
		
		cumulativeRating += rateBoardByPieces(board, maxPiece.getWhite()); //Adds the max player board rating by pieces.
		cumulativeRating -= rateBoardByPieces(board, !maxPiece.getWhite()); //Subtracts the min player board rating by pieces.
		
		return cumulativeRating;
	}
	
	/*
	 * HEURISTICS:
	 * Pawn is worth exponentially more more the closer to promoting it is, e.g 2^(rowNumber - 2) --> 5, 6, 7, 8, 13, 28, 45 (Last row means there is a queen)
	 * Proportionally, pieces are worth: Queen = 45, Rook = 25, Bishop = 15, Knight = 15, Pawn = 5
	 * Those are only base values, since they can be higher if they're in a good position at the board.
	 */
	private double rateBoardByPieces(ChessBoard board, boolean isWhite) {
		double rating = 0;
		for(int rowX = ChessConstants.MIN_DIM; rowX <= ChessConstants.MAX_DIM; rowX++) {
			for(int colY = ChessConstants.MIN_DIM; colY <= ChessConstants.MAX_DIM; colY++) {
				if(board.getPosition(rowX, colY) != null) { //If there's a piece
					if(board.getPosition(rowX, colY) instanceof Pawn) { //If it's a white pawn
						rating += ratePawn(rowX, colY, ((ChessPiece) board.getPosition(rowX, colY)).getWhite());
					} else if(board.getPosition(rowX, colY) instanceof Rook) {
						rating += rateRook(rowX, colY, ((ChessPiece) board.getPosition(rowX, colY)).getWhite());
					} else if(board.getPosition(rowX, colY) instanceof Knight) {
						rating += rateKnight(rowX, colY, ((ChessPiece) board.getPosition(rowX, colY)).getWhite());
					} else if(board.getPosition(rowX, colY) instanceof Bishop) {
						rating += rateBishop(rowX, colY, ((ChessPiece) board.getPosition(rowX, colY)).getWhite());
					} else if(board.getPosition(rowX, colY) instanceof Queen) {
						rating += rateQueen(rowX, colY, ((ChessPiece) board.getPosition(rowX, colY)).getWhite());
					} else if(board.getPosition(rowX, colY) instanceof King) {
						continue; //The King doesn't add any value to the board, since his value would be infinite.
					}
				}
				//Pieces further away from the sides of the board are usually worth slightly more.
				if(colY == 3 || colY == 4) rating += 3;
				else if(colY == 2 || colY == 5) rating += 2;
				else if(colY == 1 || colY == 6) rating += 1;
			}
		}
		
		return rating;
	}
	
	private double ratePawn(int rowX, int colY, boolean isWhite) {
		double rating = 0;
		if(isWhite) { //If it's a white pawn...
			switch(rowX) {
				case 0: rating += 45; break;
				case 1: rating += 28; break;
				case 2: rating += 13; break;
				case 3: rating += 8; break;
				case 4: rating += 7; break;
				case 5: rating += 6; break;
				case 6: rating += 5; break;
				case 7: throw new GameError("A white pawn can't be on the bottom row!");
				default: throw new GameError("Unexpected row!");
			}
		} else { //If it's a black pawn...
			switch(rowX) {
				case 0: throw new GameError("A black pawn can't be on the top row!");
				case 1: rating += 5; break;
				case 2: rating += 6; break;
				case 3: rating += 7; break;
				case 4: rating += 8; break;
				case 5: rating += 13; break;
				case 6: rating += 28; break;
				case 7: rating += 45; break;
				default: throw new GameError("Unexpected row!");
			}
		}
		
		return rating;
	}
	
	private double rateRook(int rowX, int colY, boolean isWhite) {
		double rating = 25;
		if(isWhite) {
			if(rowX == ChessConstants.MAX_DIM) //If it's white and is on the bottom row it's worth less because it's usually trapped.
				rating -= 4;
		} else {
			if(rowX == ChessConstants.MIN_DIM) //The opposite is true for black pieces
				rating -= 4;
		}
		return rating;
	}
	
	private double rateKnight(int rowX, int colY, boolean isWhite) {
		double rating = 15;
		if(isWhite) {
			if(rowX == ChessConstants.MAX_DIM) //If it's white and is on the bottom row it's worth less because it's usually trapped.
				rating -= 3;
		} else {
			if(rowX == ChessConstants.MIN_DIM) //The opposite is true for black pieces
				rating -= 3;
		}
		return rating;
	}
	
	private double rateBishop(int rowX, int colY, boolean isWhite) {
		double rating = 15;
		if(isWhite) {
			if(rowX == ChessConstants.MAX_DIM) //If it's white and is on the bottom row it's worth less because it's usually trapped.
				rating -= 3;
		} else {
			if(rowX == ChessConstants.MIN_DIM) //The opposite is true for black pieces
				rating -= 3;
		}
		return rating;
	}
	
	private double rateQueen(int rowX, int colY, boolean isWhite) {
		double rating = 45;
		if(isWhite) {
			if(rowX == ChessConstants.MAX_DIM) //If it's white and is on the bottom row it's worth less because it's usually trapped.
				rating -= 5;
		} else {
			if(rowX == ChessConstants.MIN_DIM) //The opposite is true for black pieces
				rating -= 5;
		}
		return rating;
	}
	
	/**
	 * Checks if the game is over for the white player!!
	 * @return @true if game is won BY CHECKMATE, NOT stalemate, @false otherwise.
	 */
	private boolean isWhiteInCheck(ChessBoard board) {
		if(new ChessMove().isKingInCheck(board, ChessConstants.WHITE)) //TODO This should be changed later to avoid creating new ChessMove.
			return true;
		else
			return false;
	}
	
	/**
	 * Checks if the game is over for the black player!!
	 * @return @true if game is won BY CHECKMATE, NOT stalemate, @false otherwise.
	 */
	private boolean isBlackInCheck(ChessBoard board) {
		if(new ChessMove().isKingInCheck(board, ChessConstants.BLACK)) //TODO This should be changed later to avoid creating new ChessMove.
			return true;
		else
			return false;
	}
}