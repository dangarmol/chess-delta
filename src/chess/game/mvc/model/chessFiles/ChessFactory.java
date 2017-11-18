package chess.game.mvc.model.chessFiles;

import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

import chess.game.mvc.controller.AIPlayer;
import chess.game.mvc.controller.ConsolePlayer;
import chess.game.mvc.controller.Controller;
import chess.game.mvc.controller.DummyAIPlayer;
import chess.game.mvc.controller.GameFactory;
import chess.game.mvc.controller.Player;
import chess.game.mvc.model.chessPieces.ChessPiece;
import chess.game.mvc.model.chessPieces.ChessPieceID;
import chess.game.mvc.model.chessPieces.chessPiecesImp.Bishop;
import chess.game.mvc.model.chessPieces.chessPiecesImp.King;
import chess.game.mvc.model.chessPieces.chessPiecesImp.Knight;
import chess.game.mvc.model.chessPieces.chessPiecesImp.Pawn;
import chess.game.mvc.model.chessPieces.chessPiecesImp.Queen;
import chess.game.mvc.model.chessPieces.chessPiecesImp.Rook;
import chess.game.mvc.model.genericGameFiles.AIAlgorithm;
import chess.game.mvc.model.genericGameFiles.GameMove;
import chess.game.mvc.model.genericGameFiles.GameObserver;
import chess.game.mvc.model.genericGameFiles.GameRules;
import chess.game.mvc.model.genericGameFiles.Observable;
import chess.game.mvc.model.genericGameFiles.Piece;

public class ChessFactory implements GameFactory {
	
	private static final long serialVersionUID = 1L;

	//Default
	public ChessFactory() {}
	
	@Override
	public GameRules gameRules() {
		return new ChessRules();
	}
	
	@Override
	public Player createConsolePlayer() {
		ArrayList<GameMove> possibleMoves = new ArrayList<GameMove>();
		possibleMoves.add(new ChessMove());
		return new ConsolePlayer(new Scanner(System.in), possibleMoves);
	}
	
	
	@Override
	public Player createRandomPlayer() {
		return new ChessRandomPlayer();
	}

	@Override
	public Player createAIPlayer(AIAlgorithm alg) {
		if ( alg != null ) {
			return new AIPlayer(alg);
		} else {
			return new DummyAIPlayer(createRandomPlayer(), 1000);
		}
	}

	/**
	 * By default, we have two players. Each one of them is represented
	 * by his colour in this game. One is black and one is white.
	 */
	@Override
	public List<Piece> createDefaultPieces() {
		List<Piece> pieces = new ArrayList<Piece>();
		
		//This is important
		pieces.add(new ChessPiece("White", true));
		pieces.add(new ChessPiece("Black", false));
		
		return pieces;
	}
	
	/**
	 * Creates the rest of piece types.
	 */
	@Override
	public List<Piece> createPieceTypes() {
		List<Piece> pieces = new ArrayList<Piece>();
		
		pieces.add(ChessPieceID.WHITE_PAWN, new Pawn(true));
		pieces.add(ChessPieceID.BLACK_PAWN, new Pawn(false));
		
		pieces.add(ChessPieceID.WHITE_ROOK, new Rook(true));
		pieces.add(ChessPieceID.BLACK_ROOK, new Rook(false));
		
		pieces.add(ChessPieceID.WHITE_KNIGHT, new Knight(true));
		pieces.add(ChessPieceID.BLACK_KNIGHT, new Knight(false));
		
		pieces.add(ChessPieceID.WHITE_BISHOP, new Bishop(true));
		pieces.add(ChessPieceID.BLACK_BISHOP, new Bishop(false));
		
		pieces.add(ChessPieceID.WHITE_QUEEN, new Queen(true));
		pieces.add(ChessPieceID.BLACK_QUEEN, new Queen(false));
		
		pieces.add(ChessPieceID.WHITE_KING, new King(true));
		pieces.add(ChessPieceID.BLACK_KING, new King(false));
		
		return pieces;
	}

	@Override
	public void createSwingView(final Observable<GameObserver> g, final Controller c, final Piece viewPiece,
			Player random, Player ai) {
		throw new UnsupportedOperationException("There is no swing view");
	}
}
