package chess.basecode.chess;

import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

import chess.basecode.bgame.control.AIPlayer;
import chess.basecode.bgame.control.ConsolePlayer;
import chess.basecode.bgame.control.Controller;
import chess.basecode.bgame.control.DummyAIPlayer;
import chess.basecode.bgame.control.GameFactory;
import chess.basecode.bgame.control.Player;
import chess.basecode.bgame.model.AIAlgorithm;
import chess.basecode.bgame.model.GameError;
import chess.basecode.bgame.model.GameMove;
import chess.basecode.bgame.model.GameObserver;
import chess.basecode.bgame.model.GameRules;
import chess.basecode.bgame.model.Observable;
import chess.basecode.bgame.model.Piece;
import chess.basecode.bgame.model.chessPieces.ChessPieceID;
import chess.basecode.bgame.model.chessPieces.chessPiecesImp.Bishop;
import chess.basecode.bgame.model.chessPieces.chessPiecesImp.King;
import chess.basecode.bgame.model.chessPieces.chessPiecesImp.Knight;
import chess.basecode.bgame.model.chessPieces.chessPiecesImp.Pawn;
import chess.basecode.bgame.model.chessPieces.chessPiecesImp.Queen;
import chess.basecode.bgame.model.chessPieces.chessPiecesImp.Rook;
import chess.basecode.bgame.views.GenericConsoleView;

public class ChessFactory implements GameFactory /*extends ConnectNFactory*/ {
	
	private static final long serialVersionUID = 1L;
	private int dim;
	private int obs;
	
	
	public ChessFactory(int dim, int obsNum) {
		this.dim = dim;
		this.obs = obsNum;
	}

	//Default
	public ChessFactory(){
		this(8, 0);
	}
	
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
	public void createConsoleView(Observable<GameObserver> g, Controller c) {
		new GenericConsoleView(g, c);
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
	 * By default, we have two players
	 * <p>
	 * Por defecto, hay dos jugadores
	 */
	@Override
	public List<Piece> createDefaultPieces() {
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
