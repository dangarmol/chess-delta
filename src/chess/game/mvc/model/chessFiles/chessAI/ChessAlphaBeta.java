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

public class ChessAlphaBeta implements AIAlgorithm {

	private static final long serialVersionUID = 556625192621284461L;

	private int level; //How hard it is to win against the AI. The higher this number, the more intelligent it becomes.
	private BasicBoardEvaluator evaluator;
	private AIStatistics aiStats;
	private GameRules rules;
	private List<Piece> pieces;
	private int minID;
	private int maxID;
	private ChessAINode bestNode;
	private ChessPiece maxPiece;
	private ChessPiece minPiece;
	private long nodesExplored;
	
	public ChessAlphaBeta() {
		this.level = ChessStatic.DEFAULT_MINMAX_LEVEL;
		this.evaluator = new BasicBoardEvaluator();
		this.aiStats = new AIStatistics(ChessStatic.DEFAULT_MINMAX_LEVEL, "AlphaBeta");
	}
	
	public ChessAlphaBeta(int level) {
		this.level = level;
		this.evaluator = new BasicBoardEvaluator();
		this.aiStats = new AIStatistics(level, "AlphaBeta");
	}
	
	@Override
	public GameMove getMove(Piece p, Board board, List<Piece> playersPieces, List<Piece> pieceTypes, GameRules rules) {
		this.rules = rules;
		this.pieces = playersPieces;
		this.bestNode = new ChessAINode();
		this.maxID = (((ChessPiece) p).getWhite() ? ChessStatic.WHITE_ID : ChessStatic.BLACK_ID);
		this.minID = ((maxID == ChessStatic.WHITE_ID) ? ChessStatic.BLACK_ID : ChessStatic.WHITE_ID);
		this.maxPiece = (ChessPiece) this.pieces.get(this.maxID);
		this.minPiece = (ChessPiece) this.pieces.get(this.minID);
		this.nodesExplored = 0;
		this.aiStats.setColour(((ChessPiece) p).getWhite());
		return minMax(board, ChessStatic.STARTING_DEPTH, -1000, +1000).getMove();
	}
	
	
	//TODO Make sure that ELO sites allow computers to play
	//Also make matrix of different alpha-beta and Minmax win-loss and plot average thinking times
	private ChessAINode minMax(Board board, int depth, double alpha, double beta) {
		this.aiStats.increaseMoves();
		
		final long startTime = System.currentTimeMillis();
		double moveRating = max((ChessBoard) board, depth, alpha, beta);
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
		System.out.println("Rating: " + this.bestNode.getRating());*/
		
		return this.bestNode;
	}
	
	/**
	 * TODO Add to report!
	 * Alpha is changed in max if the value is higher than Alpha.
	 * Beta is changed in min if the value is lower than Beta.
	 * 
	 * Alpha means don't search further if you find a lower value.
	 * Beta means don't search further if you find a higher value.
	 * Alpha is the minimum guaranteed value for max.
	 * Beta is the max guaranteed value for min.
	 */
	private double min(Board board, int depth, double alpha, double beta) {
		Pair<State, Piece> gameState = this.rules.updateState(board, pieces, this.minPiece);
		
		this.nodesExplored++;
		
		if(gameState.getFirst().equals(State.Won)) {
			if(gameState.getSecond().equals(this.minPiece)) {
				return 1000;
			} else {
				return -1000;
			}
		} else if(gameState.getFirst().equals(State.Draw)) {
			return 0;
		} else if(depth == this.level) {
			return this.evaluator.getRating((ChessBoard) board, (ChessPiece) this.pieces.get(this.minID), this.maxPiece);
		} else if(this.bestNode.getRating() < 1000 && this.bestNode.getRating() > -1000) {
			List<GameMove> validMoves = this.rules.validMoves(board, this.pieces, this.pieces.get(this.minID));
			double lowestInBranch = Double.MAX_VALUE;
			for(GameMove move : validMoves) {
				ChessBoard testBoard = (ChessBoard) board.copy();
				((ChessMove) move).executeAICheckedMove(testBoard);
				
				if(depth == ChessStatic.STARTING_DEPTH) { //ONLY saves the movements in the case when the depth is 0, moves from a higher depth are not relevant, only the rating is
					double currentNodeRating = max(testBoard, depth + 1, alpha, beta);
					if (currentNodeRating < beta) {
						beta = currentNodeRating;
						lowestInBranch = currentNodeRating;
						this.bestNode.changeNode((ChessMove) move, currentNodeRating); //Should this be here?
					}
				} else {
					double currentNodeRating = max(testBoard, depth + 1, alpha, beta);
					if (currentNodeRating < beta) {
						beta = currentNodeRating;
						lowestInBranch = currentNodeRating;
					}
				}
				if(alpha >= beta) {
					return lowestInBranch;
				}
			}
			return lowestInBranch;
		} else {
			return this.bestNode.getRating();
		}
	}
	
	private double max(Board board, int depth, double alpha, double beta) {
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
		} else if(this.bestNode.getRating() < 1000 && this.bestNode.getRating() > -1000) {
			List<GameMove> validMoves = this.rules.validMoves(board, this.pieces, this.pieces.get(this.maxID));

			/**
			 * TODO Add to report: IN JAVA, Double.MIN_VALUE IS JUST THE MINIMUM ABSOLUTE VALUE, NOT NEGATIVE INFINITY.
			 * It should be expressed like so: -Double.MAX_VALUE
			 * https://stackoverflow.com/questions/3884793/why-is-double-min-value-in-not-negative
			 */
			double highestInBranch = -Double.MAX_VALUE;
			for(GameMove move : validMoves) {
				ChessBoard testBoard = (ChessBoard) board.copy();
				((ChessMove) move).executeAICheckedMove(testBoard);
				
				if(depth == ChessStatic.STARTING_DEPTH) { //ONLY saves the movements in the case when the depth is 0, moves from a higher depth are not relevant, only the rating is
					double currentNodeRating = min(testBoard, depth + 1, alpha, beta);
					if(currentNodeRating > alpha) {
						alpha = currentNodeRating;
						highestInBranch = currentNodeRating;
						this.bestNode.changeNode((ChessMove) move, currentNodeRating);
					}
				} else {
					double currentNodeRating = min(testBoard, depth + 1, alpha, beta);
					if (currentNodeRating > alpha) {
						alpha = currentNodeRating;
						highestInBranch = currentNodeRating;
					}
				}
				if(alpha >= beta) {
					return highestInBranch;
				}
			}
			return highestInBranch;
		} else {
			return this.bestNode.getRating();
		}
	}
}