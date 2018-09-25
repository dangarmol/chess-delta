package chess.game.mvc.model.chessFiles.chessAI.chessBoardEvaluators;

import chess.game.mvc.model.chessFiles.ChessBoard;
import chess.game.mvc.model.chessFiles.ChessStatic;
import chess.game.mvc.model.chessPieces.ChessPiece;
import chess.game.mvc.model.chessPieces.chessPiecesImp.Bishop;
import chess.game.mvc.model.chessPieces.chessPiecesImp.King;
import chess.game.mvc.model.chessPieces.chessPiecesImp.Knight;
import chess.game.mvc.model.chessPieces.chessPiecesImp.Pawn;
import chess.game.mvc.model.chessPieces.chessPiecesImp.Queen;
import chess.game.mvc.model.chessPieces.chessPiecesImp.Rook;
import chess.game.mvc.model.genericGameFiles.GameError;

public class BasicChessBoardEvaluator implements ChessBoardEvaluator {
	
	public BasicChessBoardEvaluator() {}
	
	public double getRating(ChessBoard board, ChessPiece currentPiece, ChessPiece maxPiece) {
		if(board.checkMovesRulesLimit()) {
			return 0;
		}
		
		double cumulativeRating = 0;
		
		boolean whiteInCheck = isWhiteInCheck(board);
		boolean blackInCheck;
		
		if(!whiteInCheck) {
			blackInCheck = isBlackInCheck(board);
		} else {
			blackInCheck = false; //It's not possible to have both players in check at the same time.
		}
		
		if(whiteInCheck) { //If there is a player in check
			if(currentPiece.getWhite()) {
				cumulativeRating += 10;
			} else {
				cumulativeRating -= 10;
			}
		} else if(blackInCheck) {
			if(!currentPiece.getWhite()) {
				cumulativeRating += 10;
			} else {
				cumulativeRating -= 10;
			}
		}
		
		cumulativeRating += rateBoardByPieces(board, maxPiece.getWhite()); //Adds the max player board rating by pieces.
		
		return cumulativeRating;
	}
	
	/*
	 * HEURISTICS:
	 * Pawn is worth exponentially more more the closer to promoting it is, e.g 2^(rowNumber - 2) --> 5, 6, 7, 8, 13, 28, 45 (Last row means there is a queen)
	 * Proportionally, pieces are worth: Queen = 45, Rook = 25, Bishop = 15, Knight = 15, Pawn = 5
	 * Those are only base values, since they can be higher if they're in a good position at the board.
	 */
	private double rateBoardByPieces(ChessBoard board, boolean isMaxWhite) {
		double rating = 0;
		for(int rowX = ChessStatic.MIN_DIM; rowX <= ChessStatic.MAX_DIM; rowX++) {
			for(int colY = ChessStatic.MIN_DIM; colY <= ChessStatic.MAX_DIM; colY++) {
				if(board.getPosition(rowX, colY) != null) { //If there's a piece
					if(((ChessPiece) board.getPosition(rowX, colY)).getWhite() == isMaxWhite) { //If the colour matches the max player...
						if(board.getPosition(rowX, colY) instanceof Pawn) {
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
						
						//Pieces further away from the sides of the board are usually worth slightly more.
						if(colY == 3 || colY == 4) rating += 3;
						else if(colY == 2 || colY == 5) rating += 2;
						else if(colY == 1 || colY == 6) rating += 1;
					} else { //If the colour matches the min player...
						if(board.getPosition(rowX, colY) instanceof Pawn) { //If the colour matches the min player...
							rating -= ratePawn(rowX, colY, ((ChessPiece) board.getPosition(rowX, colY)).getWhite());
						} else if(board.getPosition(rowX, colY) instanceof Rook) {
							rating -= rateRook(rowX, colY, ((ChessPiece) board.getPosition(rowX, colY)).getWhite());
						} else if(board.getPosition(rowX, colY) instanceof Knight) {
							rating -= rateKnight(rowX, colY, ((ChessPiece) board.getPosition(rowX, colY)).getWhite());
						} else if(board.getPosition(rowX, colY) instanceof Bishop) {
							rating -= rateBishop(rowX, colY, ((ChessPiece) board.getPosition(rowX, colY)).getWhite());
						} else if(board.getPosition(rowX, colY) instanceof Queen) {
							rating -= rateQueen(rowX, colY, ((ChessPiece) board.getPosition(rowX, colY)).getWhite());
						} else if(board.getPosition(rowX, colY) instanceof King) {
							continue; //The King doesn't add any value to the board, since his value would be infinite.
						}
						
						//Pieces further away from the sides of the board are usually worth slightly more.
						if(colY == 3 || colY == 4) rating -= 3;
						else if(colY == 2 || colY == 5) rating -= 2;
						else if(colY == 1 || colY == 6) rating -= 1;
					}
				}
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
			if(rowX == ChessStatic.MAX_DIM) { //If it's white and is on the bottom row it's worth less because it's usually trapped.
				rating -= 2;
			} else if(rowX == ChessStatic.MAX_DIM - 1) { //On this row, rooks can be even more constrained, especially at the beginning of a match.
				rating -= 4;
			}
		} else {
			if(rowX == ChessStatic.MIN_DIM) { //The opposite is true for black pieces
				rating -= 2;
			} else if(rowX == ChessStatic.MIN_DIM + 1) { //Same as above.
				rating -= 4;
			}
		}
		return rating;
	}
	
	private double rateKnight(int rowX, int colY, boolean isWhite) {
		double rating = 15;
		if(isWhite) {
			if(rowX == ChessStatic.MAX_DIM) //If it's white and is on the bottom row it's worth less because it's usually trapped.
				rating -= 3;
		} else {
			if(rowX == ChessStatic.MIN_DIM) //The opposite is true for black pieces
				rating -= 3;
		}
		return rating;
	}
	
	private double rateBishop(int rowX, int colY, boolean isWhite) {
		double rating = 15;
		if(isWhite) {
			if(rowX == ChessStatic.MAX_DIM) //If it's white and is on the bottom row it's worth less because it's usually trapped.
				rating -= 3;
		} else {
			if(rowX == ChessStatic.MIN_DIM) //The opposite is true for black pieces
				rating -= 3;
		}
		return rating;
	}
	
	private double rateQueen(int rowX, int colY, boolean isWhite) {
		double rating = 45;
		if(isWhite) {
			if(rowX == ChessStatic.MAX_DIM) //If it's white and is on the bottom row it's worth less because it's usually trapped.
				rating -= 5;
		} else {
			if(rowX == ChessStatic.MIN_DIM) //The opposite is true for black pieces
				rating -= 5;
		}
		return rating;
	}
	
	/**
	 * Returns if the king is in check for the white player!!
	 * @return @true if it is, @false otherwise.
	 */
	private boolean isWhiteInCheck(ChessBoard board) {
		if(board.isKingInCheck(ChessStatic.WHITE))
			return true;
		else
			return false;
	}
	
	/**
	 * Returns if the king is in check for the white player!!
	 * @return @true if it is, @false otherwise.
	 */
	private boolean isBlackInCheck(ChessBoard board) {
		if(board.isKingInCheck(ChessStatic.BLACK))
			return true;
		else
			return false;
	}
}