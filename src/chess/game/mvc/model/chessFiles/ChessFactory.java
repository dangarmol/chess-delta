package chess.game.mvc.model.chessFiles;

import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

import chess.game.mvc.controller.Controller;
import chess.game.mvc.model.chessFiles.chessAI.ChessAlphaBeta;
import chess.game.mvc.model.chessFiles.chessAI.ChessMinMax;
import chess.game.mvc.model.chessPieces.ChessPiece;
import chess.game.mvc.model.chessPieces.ChessPieceID;
import chess.game.mvc.model.chessPieces.chessPiecesImp.Bishop;
import chess.game.mvc.model.chessPieces.chessPiecesImp.King;
import chess.game.mvc.model.chessPieces.chessPiecesImp.Knight;
import chess.game.mvc.model.chessPieces.chessPiecesImp.Pawn;
import chess.game.mvc.model.chessPieces.chessPiecesImp.Queen;
import chess.game.mvc.model.chessPieces.chessPiecesImp.Rook;
import chess.game.mvc.model.genericGameFiles.AIAlgorithm;
import chess.game.mvc.model.genericGameFiles.AIPlayer;
import chess.game.mvc.model.genericGameFiles.ConsolePlayer;
import chess.game.mvc.model.genericGameFiles.DummyAIPlayer;
import chess.game.mvc.model.genericGameFiles.GameFactory;
import chess.game.mvc.model.genericGameFiles.GameMove;
import chess.game.mvc.model.genericGameFiles.GameObserver;
import chess.game.mvc.model.genericGameFiles.GameRules;
import chess.game.mvc.model.genericGameFiles.Observable;
import chess.game.mvc.model.genericGameFiles.Piece;
import chess.game.mvc.model.genericGameFiles.Player;

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
		return new ChessDummyPlayer();
	}

	@Override
	public List<Player> createAIPlayers(List<AIAlgorithm> algList) {
		List<Player> aiPlayers = new ArrayList<Player>();
		for(AIAlgorithm alg : algList) {
			if (alg != null) {
				aiPlayers.add(new AIPlayer(alg));
			} else {
				aiPlayers.add(new DummyAIPlayer(createRandomPlayer(), 1000));
			}
		}
		return aiPlayers;
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
		
		pieces.add(ChessPieceID.WHITE_PAWN_A, new Pawn(true));
		pieces.add(ChessPieceID.WHITE_PAWN_B, new Pawn(true));
		pieces.add(ChessPieceID.WHITE_PAWN_C, new Pawn(true));
		pieces.add(ChessPieceID.WHITE_PAWN_D, new Pawn(true));
		pieces.add(ChessPieceID.WHITE_PAWN_E, new Pawn(true));
		pieces.add(ChessPieceID.WHITE_PAWN_F, new Pawn(true));
		pieces.add(ChessPieceID.WHITE_PAWN_G, new Pawn(true));
		pieces.add(ChessPieceID.WHITE_PAWN_H, new Pawn(true));
		pieces.add(ChessPieceID.BLACK_PAWN_A, new Pawn(false));
		pieces.add(ChessPieceID.BLACK_PAWN_B, new Pawn(false));
		pieces.add(ChessPieceID.BLACK_PAWN_C, new Pawn(false));
		pieces.add(ChessPieceID.BLACK_PAWN_D, new Pawn(false));
		pieces.add(ChessPieceID.BLACK_PAWN_E, new Pawn(false));
		pieces.add(ChessPieceID.BLACK_PAWN_F, new Pawn(false));
		pieces.add(ChessPieceID.BLACK_PAWN_G, new Pawn(false));
		pieces.add(ChessPieceID.BLACK_PAWN_H, new Pawn(false));
		
		pieces.add(ChessPieceID.WHITE_ROOK_A, new Rook(true));
		pieces.add(ChessPieceID.WHITE_ROOK_H, new Rook(true));
		pieces.add(ChessPieceID.WHITE_ROOK_PAWN, new Rook(true, false));
		
		pieces.add(ChessPieceID.BLACK_ROOK_A, new Rook(false));		
		pieces.add(ChessPieceID.BLACK_ROOK_H, new Rook(false));		
		pieces.add(ChessPieceID.BLACK_ROOK_PAWN, new Rook(false, false));
		
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
			Player random, List<Player> ai) {
		throw new UnsupportedOperationException("There is no swing view");
	}

	public List<AIAlgorithm> createAIPlayerList() {
		List<AIAlgorithm> aiAlgs = new ArrayList<AIAlgorithm>();
		
		//This is important
		aiAlgs.add(new ChessMinMax(2));
		aiAlgs.add(new ChessMinMax(3));
		aiAlgs.add(new ChessMinMax(4));
		aiAlgs.add(new ChessMinMax(5));
		aiAlgs.add(new ChessAlphaBeta(2));
		aiAlgs.add(new ChessAlphaBeta(3));
		aiAlgs.add(new ChessAlphaBeta(4));
		aiAlgs.add(new ChessAlphaBeta(5));
		aiAlgs.add(new ChessAlphaBeta(6));
		aiAlgs.add(new ChessAlphaBeta(7));
		
		return aiAlgs;
	}
}
