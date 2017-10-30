package chess.connection.connectionFiles;

import java.io.IOException;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

import chess.game.mvc.controller.Controller;
import chess.game.mvc.controller.GameFactory;
import chess.game.mvc.controller.Player;
import chess.game.mvc.controller.commands.Command;
import chess.game.mvc.controller.commands.PlayCommand;
import chess.game.mvc.controller.commands.QuitCommand;
import chess.game.mvc.controller.commands.RestartCommand;
import chess.game.mvc.model.genericGameFiles.Board;
import chess.game.mvc.model.genericGameFiles.GameError;
import chess.game.mvc.model.genericGameFiles.GameObserver;
import chess.game.mvc.model.genericGameFiles.Observable;
import chess.game.mvc.model.genericGameFiles.Piece;
import chess.game.mvc.model.genericGameFiles.Game.State;

public class GameClient extends Controller implements Observable<GameObserver> {
	private String host;
	private int port;
	private List<GameObserver> observers;
	private Piece localPiece;
	private GameFactory gameFactory;
	private Connection connectionToServer;
	private boolean gameOver;
	
	public GameClient(String host, int port) throws Exception {
		super(null, null);
		this.host = host;
		this.port = port;
		this.observers = new ArrayList<GameObserver>();
		connect();
	}
	private void connect() throws Exception {
		Socket socket = new Socket(host, port);
		connectionToServer = new Connection(socket);
		// 1. �
		this.connectionToServer.sendObject("Connect");
		Object response = this.connectionToServer.getObject();
		if (response instanceof Exception) {
			throw (Exception) response;
		}
		try {
			gameFactory = (GameFactory)this.connectionToServer.getObject();
			localPiece = (Piece)this.connectionToServer.getObject();
		} catch (Exception e) {
			throw new GameError("Unknown server response: " + e.getMessage());
		}
	}
	
	public GameFactory getGameFactory() {
		return this.gameFactory;
	}
	
	public Piece getPlayerPiece() {
		return this.localPiece;
	}
	
	@Override
	public void addObserver(GameObserver o) {
		this.observers.add(o);
	}
	
	@Override
	public void removeObserver(GameObserver o) {
		this.observers.remove(o);
	}

	private void forwardCommand(Command cmd) {
		// if the game is over do nothing, otherwise
		if (!gameOver) {
			// send the object cmd to the server
			try {
				connectionToServer.sendObject(cmd);
			} catch (IOException e) {
				e.printStackTrace();
			}
			
		}
	}
	
	@Override
	public void makeMove(Player p) {
		forwardCommand(new PlayCommand(p));
	}
	@Override
	public void stop() {
		forwardCommand(new QuitCommand());
	}
	@Override
	public void restart() {
		forwardCommand(new RestartCommand());
	}
	
	public void start() {
		GameOverClientObserver go = new GameOverClientObserver();
		this.observers.add(go);
		gameOver = false;
		while (!gameOver) {
			try {
				Response res = (Response)connectionToServer.getObject(); // read a response
				for (GameObserver o : observers) {
					// execute the response on the observer o
					res.run(o);
				}
			} catch (ClassNotFoundException | IOException e) {
			}
		}
	}
	
	public void onGameOver() {
		gameOver = true;
		try {
			connectionToServer.stop();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	private class GameOverClientObserver implements GameObserver {

		@Override
		public void onGameStart(Board board, String gameDesc, List<Piece> pieces, Piece turn) {
			
		}

		@Override
		public void onGameOver(Board board, State state, Piece winner) {
			gameOver = true;
		}

		@Override
		public void onMoveStart(Board board, Piece turn) {
			
		}

		@Override
		public void onMoveEnd(Board board, Piece turn, boolean success) {
			
		}

		@Override
		public void onChangeTurn(Board board, Piece turn) {
			new FWChangeTurnResponse(board, turn);
		}

		@Override
		public void onError(String msg) {
			
		}
	}
}
