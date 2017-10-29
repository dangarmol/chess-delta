package chess.practicaAtaxx.ataxx;

import java.util.ArrayList;
import java.util.Scanner;

import chess.basecode.bgame.control.ConsolePlayer;
import chess.basecode.bgame.control.Controller;
import chess.basecode.bgame.control.Player;
import chess.basecode.bgame.model.GameError;
import chess.basecode.bgame.model.GameMove;
import chess.basecode.bgame.model.GameObserver;
import chess.basecode.bgame.model.GameRules;
import chess.basecode.bgame.model.Observable;
import chess.basecode.bgame.views.GenericConsoleView;
import chess.basecode.connectn.ConnectNFactory;

public class AtaxxFactory extends ConnectNFactory {
	
	private static final long serialVersionUID = 1L;
	private int dim;
	private int obs;
	
	
	public AtaxxFactory(int dim, int obsNum) {
		super(dim); 
		
		if (dim % 2 == 0 || dim < 5) {
			throw new GameError("Dimension must be odd and greater than five");
		} else {
			this.dim = dim;
			this.obs = obsNum;
		}
	}

	//Default
	public AtaxxFactory(){
		this(5, 0);
	}
	
	@Override
	public GameRules gameRules() {
		return new AtaxxRules(dim,this.obs);
	}
	
	@Override
	public Player createConsolePlayer() {
		ArrayList<GameMove> possibleMoves = new ArrayList<GameMove>();
		possibleMoves.add(new AtaxxMove());
		return new ConsolePlayer(new Scanner(System.in), possibleMoves);
	}
	
	@Override
	public void createConsoleView(Observable<GameObserver> g, Controller c) {
		new GenericConsoleView(g, c);
	}
	
	@Override
	public Player createRandomPlayer() {
		return new AtaxxRandomPlayer();
	}

}
