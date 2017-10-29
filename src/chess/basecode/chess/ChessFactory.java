package chess.basecode.chess;

import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

import chess.basecode.bgame.control.ConsolePlayer;
import chess.basecode.bgame.control.Controller;
import chess.basecode.bgame.control.GameFactory;
import chess.basecode.bgame.control.Player;
import chess.basecode.bgame.model.AIAlgorithm;
import chess.basecode.bgame.model.GameError;
import chess.basecode.bgame.model.GameMove;
import chess.basecode.bgame.model.GameObserver;
import chess.basecode.bgame.model.GameRules;
import chess.basecode.bgame.model.Observable;
import chess.basecode.bgame.model.Piece;
import chess.basecode.bgame.views.GenericConsoleView;
import chess.basecode.connectn.ConnectNFactory;

public class ChessFactory implements GameFactory /*extends ConnectNFactory*/ {
	
	private static final long serialVersionUID = 1L;
	private int dim;
	private int obs;
	
	
	public ChessFactory(int dim, int obsNum) {
		/*super(dim); 
		
		if (dim % 2 == 0 || dim < 5) {
			throw new GameError("Dimension must be odd and greater than five");
		} else {
			this.dim = dim;
			this.obs = obsNum;
		}*/
	}

	//Default
	public ChessFactory(){
		this(5, 0);
	}
	
	@Override
	public GameRules gameRules() {
		return new ChessRules(dim,this.obs);
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
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<Piece> createDefaultPieces() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void createSwingView(Observable<GameObserver> game, Controller ctrl, Piece viewPiece, Player randPlayer,
			Player aiPlayer) {
		// TODO Auto-generated method stub
		
	}

}
