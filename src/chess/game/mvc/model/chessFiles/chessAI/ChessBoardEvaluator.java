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
	
	/* HEURISTICS:
	 * The more possible moves, the better the rating. //TODO Add amount of movements to heuristics
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
				if(board.getChessPosition(rowX, colY) != null) { //If there's a piece
					if(board.getChessPosition(rowX, colY) instanceof Pawn) { //If it's a white pawn
						rating += ratePawn(rowX, colY, board.getChessPosition(rowX, colY).getWhite());
					} else if(board.getChessPosition(rowX, colY) instanceof Rook) {
						rating += rateRook(rowX, colY);
					} else if(board.getChessPosition(rowX, colY) instanceof Knight) {
						rating += rateKnight(rowX, colY);
					} else if(board.getChessPosition(rowX, colY) instanceof Bishop) {
						rating += rateBishop(rowX, colY);
					} else if(board.getChessPosition(rowX, colY) instanceof Queen) {
						rating += rateQueen(rowX, colY);
					} else if(board.getChessPosition(rowX, colY) instanceof King) {
						continue; //The King doesn't add any value to the board, since his value would be infinity.
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
		
		//Pawns further away from the sides (closer to the middle of the board are worth slightly more)
		if(colY == 3 || colY == 4) rating += 3;
		else if(colY == 2 || colY == 5) rating += 2;
		else if(colY == 1 || colY == 6) rating += 1;
		return rating;
	}
	
	private double rateRook(int rowX, int colY) { //TODO
		double rating = 0;
		
		return rating;
	}
	
	private double rateKnight(int rowX, int colY) { //TODO
		double rating = 0;
		
		return rating;
	}
	
	private double rateBishop(int rowX, int colY) { //TODO
		double rating = 0;
		
		return rating;
	}
	
	private double rateQueen(int rowX, int colY) { //TODO
		double rating = 0;
		
		return rating;
	}
	
	/**
	 * Checks if the game is over for the current player!!
	 * If white just made a checkmate move and this method is called as isGameOver(board, false, true), it should return true.
	 * @return @true if game is won BY CHECKMATE, NOT stalemate, @false otherwise.
	 */
	private boolean isCheckMate(ChessBoard board, boolean isWhiteTurn, boolean emptyMoves) { //TODO This function needs to be tested!
		if(emptyMoves && new ChessMove().isKingInCheck(board, isWhiteTurn)) //TODO This should be changed later to avoid creating new ChessMove.
			return true;
		else
			return false;
	}
}