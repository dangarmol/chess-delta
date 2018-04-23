package chess.connection.connectionFiles;

import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.SwingUtilities;

import chess.game.mvc.controller.Controller;
import chess.game.mvc.controller.commands.Command;
import chess.game.mvc.model.genericGameFiles.Board;
import chess.game.mvc.model.genericGameFiles.Game;
import chess.game.mvc.model.genericGameFiles.GameError;
import chess.game.mvc.model.genericGameFiles.GameFactory;
import chess.game.mvc.model.genericGameFiles.GameObserver;
import chess.game.mvc.model.genericGameFiles.Piece;
import chess.game.mvc.model.genericGameFiles.Player;
import chess.game.mvc.model.genericGameFiles.Game.State;

public class GameServer extends Controller implements GameObserver {
	
	private int port;
	private int numPlayers;
	private int numOfConnectedPlayers;
	private GameFactory gameFactory;
	private List<Connection> clients;
	volatile private ServerSocket server;
	volatile private boolean stopped;
	volatile private boolean gameOver;
	private boolean firstTime;
	
	private JPanel msgPanel;
	private JTextArea msgArea;
	
	public GameServer(GameFactory gameFactory, List<Piece> pieces, List<Piece> pieceTypes, int port) {
		super(new Game(gameFactory.gameRules()), pieces, pieceTypes);
		this.port = port;
		this.numPlayers = pieces.size();
		this.pieces = pieces;
		this.pieceTypes = pieceTypes;
		this.gameFactory = gameFactory;
		this.firstTime = true;
		this.numOfConnectedPlayers = 0;
		this.clients = new ArrayList<Connection>();
		game.addObserver(this);
	}

	@Override
	public synchronized void makeMove(Player player) {
		try { super.makeMove(player); } catch (GameError e) { }
	}
	@Override
	public synchronized void stop() {
		try { super.stop(); } catch (GameError e) { }
	}
	@Override
	public synchronized void restart() {
		try { super.restart(); } catch (GameError e) { }
	}
	@Override
	public void start() {
		controlGUI();
		log("Trying to start the server...");
		startServer();
	}
	
	private void controlGUI() {
		try {
			SwingUtilities.invokeAndWait(new Runnable() {
				@Override
				public void run() { constructGUI(); }
			});
		} catch (InvocationTargetException | InterruptedException e) {
			throw new GameError("Something went wrong while constructing the GUI");
		}
	}
	
	private void stopGame() {
		if(game.getState() == State.InPlay){
			log("Game will be stopped...");
			stop();
			log("Game stopped.");
		}
		gameOver = true;
		log("Disconnecting all clients");
		for(Connection c: clients){
			try {
				c.stop();
			} catch (IOException e) {
				e.printStackTrace();
			}						
		}
		this.clients.clear();
		this.numOfConnectedPlayers = 0;
	}
	
	private void constructGUI() {
		JFrame window = new JFrame("Game Server");
		window.setPreferredSize(new Dimension (600, 300));
		window.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
		window.pack();
		
		msgArea = new JTextArea();
		msgArea.setEditable(false);
		JScrollPane sp = new JScrollPane (msgArea);
		sp.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
		
		msgPanel = new JPanel();
		JButton quitButton = new JButton("Stop Server");
		quitButton.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				int op = JOptionPane.showOptionDialog(new JFrame(), "Do you actually want to stop the server? ", "WARNING", JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE, null, null, null);
				if (op == 0) {
					try {
						stopGame();
					}
					catch (GameError err) {}
					System.exit(0);
				}
			}
			
		});
		
		msgPanel.setBorder(BorderFactory.createTitledBorder("Server log messages"));
		
		window.add(msgPanel);
		msgPanel.setLayout(new GridLayout (2, 1, 10, 10));
		msgPanel.add(sp);
		msgPanel.add(quitButton);
		window.setVisible(true);

		log("Server GUI successfully created.");
	}
	
	private void log(final String msg) {
		SwingUtilities.invokeLater(new Runnable() {
            public void run () {
                msgArea.append(msg + System.getProperty("line.separator"));
            }
        });
	}
	
	private void startServer() {
		try {
			log("Attempting to create socket on port " + this.port + "...");
			this.server = new ServerSocket(this.port);
			this.stopped = false;
			log("Socket created on port " + this.port + ". Server is running now.");
		} catch (IOException e) {
			log("Error creating socket: " + e.getMessage());
		}

		while (!stopped) {
			try {
				// 1. accept a connection into a socket s
				log("Waiting for a connection into socket... (Port " + this.port + ")");
				Socket s = server.accept();
				log("Connection request accepted.");
				log("Handling new connection...");
				// 3. call handleRequest(s) to handle the request
				handleRequest(s);
				log("Client connected.");
			} catch (IOException e) {
				if (!stopped) {
					log("Error while waiting for a connection: " + e.getMessage());
				} else {
					log("Server is stopped: " + e.getMessage());
				}
			}
		}
	}
	
	private void handleRequest(Socket s) {
		try {
			Connection c = new Connection(s);
			Object clientRequest = c.getObject();
			if (!(clientRequest instanceof String) && !((String) clientRequest).equalsIgnoreCase("Connect") ) {
				c.sendObject(new GameError("Invalid Request"));
				c.stop();
				return;
			}
			// 1. �
			log("Checking for free player slots...");
			if (numOfConnectedPlayers == this.numPlayers) {
				c.sendObject(new GameError("There are no free player slots."));
				return;
			}
			log("Found a free slot.");
			
			// 2.
			log("New player connected.");
			this.numOfConnectedPlayers++;
			log(this.numOfConnectedPlayers + " of " + this.numPlayers + " players connected.");
			this.clients.add(c);
			// 3. �
			log("Sending OK signal...");
			c.sendObject("Ok");
			log("OK sent.");
			log("Sending Game Factory...");
			c.sendObject(gameFactory);
			log("Game Factory sent.");
			log("Assigning a piece...");
			c.sendObject(pieces.get(numOfConnectedPlayers - 1));
			// 4. �
			log("Checking game start...");
			if (numOfConnectedPlayers == numPlayers) {
				log("Player slots are full, starting game...");
				if(this.firstTime){
					super.start();
					this.firstTime = false;
				}
				else{
					restart();
				}			
			}
			else {
				log("Waiting for more players before the game starts...");
			}
			// 5.
			log("Starting client listener...");
			startClientListener(c);
		} catch (IOException | ClassNotFoundException _e) {
			log("Error handling connection");
		}
	}
	
	private void startClientListener(final Connection c) {
		gameOver = false;
		final GameServer server = this;
		 
		 Thread t = new Thread() {
			 public void run () {
				 while (!stopped && !gameOver) {
					 try {
						 Command comando;
						 log("Waiting for a command...");
						 comando = (Command) c.getObject();
						 log("Executing command...");
						 comando.execute(server);
					  } 
					 catch (ClassNotFoundException | IOException e) {
						 if (!stopped && !gameOver) {
							 gameOver = true;
							 server.log("Some error has been produced, the game will now stop!");
							 server.stop();
						 }
					 }
				 }
				 
			 }
		 };
		 t.start();
	}
	
	void forwardNotification(Response r) {
		// call c.sendObject(r) for each client connection �c�
		for (Connection c : clients) {
			try {
				c.sendObject(r);
			} catch (IOException e) {
				log(e.getMessage());
				e.printStackTrace();
			}
		}
	}
	
	@Override
	public void onGameStart(Board board, String gameDesc, List<Piece> pieces, Piece turn) {
		forwardNotification(new FWGameStartResponse(board, gameDesc, pieces, turn));
	}
	@Override
	public void onGameOver(Board board, State state, Piece winner) {
		forwardNotification(new FWGameOverResponse(board, state, winner));
		stopGame();
	}
	@Override
	public void onMoveStart(Board board, Piece turn) {
		forwardNotification(new FWMoveStartResponse(board, turn));
	}
	@Override
	public void onMoveEnd(Board board, Piece turn, boolean success) {
		forwardNotification(new FWMoveEndResponse(board, turn, success));
	}
	@Override
	public void onChangeTurn(Board board, Piece turn) {
		forwardNotification(new FWChangeTurnResponse(board, turn));
	}
	@Override
	public void onError(String msg) {
		forwardNotification(new FWErrorResponse(msg));
	}
}
