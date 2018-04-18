package chess.game.mvc.model.chessFiles.chessAI;

import java.util.List;

import chess.game.mvc.Utils;
import chess.game.mvc.model.chessFiles.ChessBoard;
import chess.game.mvc.model.chessFiles.ChessStatic;
import chess.game.mvc.model.chessFiles.ChessMove;
import chess.game.mvc.model.chessPieces.ChessPiece;
import chess.game.mvc.model.genericGameFiles.AIAlgorithm;
import chess.game.mvc.model.genericGameFiles.Board;
import chess.game.mvc.model.genericGameFiles.GameMove;
import chess.game.mvc.model.genericGameFiles.GameRules;
import chess.game.mvc.model.genericGameFiles.Pair;
import chess.game.mvc.model.genericGameFiles.Piece;
import chess.game.mvc.model.genericGameFiles.Game.State;
import chess.game.mvc.model.genericGameFiles.GameError;

public class ChessMinMax implements AIAlgorithm {

	private static final long serialVersionUID = 556625192621284461L;

	private int level; //How hard it is to win against the AI. The higher this number, the more intelligent it becomes.
	private BasicChessBoardEvaluator evaluator;
	private AIStatistics aiStats; //Instance of the class to create the statistics
	private GameRules rules; //Rules of chess
	private List<Piece> pieces;
	private int minID;
	private int maxID;
	private ChessAINode bestNode;
	private ChessPiece maxPiece;
	private ChessPiece minPiece;
	private long nodesExplored;
	
	public ChessMinMax() {
		this.level = ChessStatic.DEFAULT_MINMAX_LEVEL;
		this.evaluator = new BasicChessBoardEvaluator();
		this.aiStats = new AIStatistics(ChessStatic.DEFAULT_MINMAX_LEVEL, "MinMax");
	}
	
	public ChessMinMax(int level) {
		this.level = level;
		this.evaluator = new BasicChessBoardEvaluator();
		this.aiStats = new AIStatistics(level, "MinMax");
	}
	
	@Override
	public GameMove getMove(Piece p, Board board, List<Piece> playersPieces, List<Piece> pieceTypes, GameRules rules) {
		this.rules = rules;
		this.pieces = playersPieces;
		this.bestNode = new ChessAINode();
		this.maxID = (((ChessPiece) p).getWhite() ? ChessStatic.WHITE_ID : ChessStatic.BLACK_ID); //Takes the max piece ID
		this.minID = ((maxID == ChessStatic.WHITE_ID) ? ChessStatic.BLACK_ID : ChessStatic.WHITE_ID); //Takes the min piece ID
		this.maxPiece = (ChessPiece) this.pieces.get(this.maxID);
		this.minPiece = (ChessPiece) this.pieces.get(this.minID);
		this.nodesExplored = 0;
		this.aiStats.setColour(((ChessPiece) p).getWhite());
		return minMax(board, ChessStatic.STARTING_DEPTH).getMove(); //Calls minMax for a move!
	}
	
	/**
	 * Initialises the statistics and calls max for a move. This is always called by the max piece, therefore, the call is always started on max()
	 * @param board: Current board
	 * @param depth: Maximum depth to be explored
	 * @return The best node
	 */
	private ChessAINode minMax(Board board, int depth) {
		this.aiStats.increaseMoves();
		
		final long startTime = System.currentTimeMillis();
		double moveRating = max((ChessBoard) board, depth);
		final long thinkingTime = System.currentTimeMillis() - startTime;
		
		this.aiStats.addNodesExplored(this.nodesExplored);
		this.aiStats.addRating(moveRating);
		this.aiStats.addThinkingTime(thinkingTime);
		
		System.out.println(this.aiStats.getStats());
		
		if(this.bestNode.getMove() == null || this.bestNode == null) {
			List<GameMove> validMoves = this.rules.validMoves(board, this.pieces, this.pieces.get(this.maxID));
			this.bestNode.changeNode((ChessMove) validMoves.get(Utils.randomInt(validMoves.size())), -1000);
		}
		
		/*ChessBoard newBoard = (ChessBoard) board.copy();
		this.bestNode.getMove().executeAICheckedMove(newBoard);
		System.out.println(newBoard);
		System.out.println("Rating: " + this.bestNode.getRating());*/ //For debugging purposes
		
		return this.bestNode;
	}
	
	private double min(Board board, int depth) {
		Pair<State, Piece> gameState = this.rules.updateState(board, pieces, this.minPiece); //Gets the state of the game
		
		this.nodesExplored++;
		
		if(gameState.getFirst().equals(State.Won)) { //If someone won the game
			if(gameState.getSecond().equals(this.minPiece)) { //If min player won the game
				return 1000;
			} else { //If max player won the game
				return -1000;
			}
		} else if(gameState.getFirst().equals(State.Draw)) { //If there is a draw
			return 0;
		} else if(depth == this.level) { //If the maximum depth has been reached, the board evaluation is simply returned
			return this.evaluator.getRating((ChessBoard) board, (ChessPiece) this.pieces.get(this.minID), this.maxPiece);
		} else { //In any other case, the game is still in play and the maximum depth has not been reached, therefore, it continues iterating
			List<GameMove> validMoves = this.rules.validMoves(board, this.pieces, this.pieces.get(this.minID)); //Get list of all valid possible moves
			double lowestInBranch = Double.MAX_VALUE;
			for(GameMove move : validMoves) { //For each valid move...
				ChessBoard testBoard = (ChessBoard) board.copy();
				((ChessMove) move).executeAICheckedMove(testBoard); //The move is executed
				
				if(depth == ChessStatic.STARTING_DEPTH) { //ONLY saves the movements in the case when the depth is 0, moves from a higher depth are not relevant, only the rating is
					double currentNodeRating = max(testBoard, depth + 1);
					if(currentNodeRating <= lowestInBranch) { //Could be equal in case of loss or win if implemented as infinite rating
						lowestInBranch = currentNodeRating; //This is only reached if the current node is the lowest (or equal), therefore it's changed
						this.bestNode.changeNode((ChessMove) move, currentNodeRating); //The best node so far is also changed
					}
				} else {
					//If the depth is not 0, there is no need to save the movement, only the rating of it, therefore it is calculated by taking the returned result of max() and comparing it to the lowest in the branch
					lowestInBranch = Math.min(max(testBoard, depth + 1), lowestInBranch);
				}
			}
			return lowestInBranch;
		}
	}
	
	/**
	 * Max is fairly similar to min, however, many things need to be inverted.
	 * @param board
	 * @param depth
	 * @return
	 */
	private double max(Board board, int depth) {
		Pair<State, Piece> gameState = this.rules.updateState(board, pieces, this.maxPiece);
		
		this.nodesExplored++;
		
		if(gameState.getFirst().equals(State.Won)) {
			if(gameState.getSecond().equals(this.maxPiece)) {
				return 1000;
			} else {
				return -1000;
			}
		} else if(gameState.getFirst().equals(State.Draw)) {
			return 0;
		} else if(depth == this.level) {
			return this.evaluator.getRating((ChessBoard) board, (ChessPiece) this.pieces.get(this.maxID), this.maxPiece);
		} else {
			List<GameMove> validMoves = this.rules.validMoves(board, this.pieces, this.pieces.get(this.maxID));

			double highestInBranch = -Double.MAX_VALUE;
			for(GameMove move : validMoves) {
				ChessBoard testBoard = (ChessBoard) board.copy();
				((ChessMove) move).executeAICheckedMove(testBoard);
				
				if(depth == ChessStatic.STARTING_DEPTH) { //ONLY saves the movements in the case when the depth is 0, moves from a higher depth are not relevant, only the rating is
					double currentNodeRating = min(testBoard, depth + 1);
					if(currentNodeRating >= highestInBranch) { //Could be equal in case of loss or win if implemented as infinite rating.
						highestInBranch = currentNodeRating;
						this.bestNode.changeNode((ChessMove) move, currentNodeRating);
					}
				} else {
					highestInBranch = Math.max(min(testBoard, depth + 1), highestInBranch);
				}
			}
			return highestInBranch;
		}
	}
}