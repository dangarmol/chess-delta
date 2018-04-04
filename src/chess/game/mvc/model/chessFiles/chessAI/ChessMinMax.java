package chess.game.mvc.model.chessFiles.chessAI;

import java.util.List;

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

public class ChessMinMax implements AIAlgorithm {

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
	
	public ChessMinMax() {
		this.level = ChessStatic.DEFAULT_MINMAX_LEVEL;
		this.evaluator = new BasicBoardEvaluator();
		this.aiStats = new AIStatistics(ChessStatic.DEFAULT_MINMAX_LEVEL, "MinMax");
	}
	
	public ChessMinMax(int level) {
		this.level = level;
		this.evaluator = new BasicBoardEvaluator();
		this.aiStats = new AIStatistics(level, "MinMax");
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
		return minMax(board, ChessStatic.STARTING_DEPTH).getMove();
	}
	
	
	//TODO Make sure that ELO sites allow computers to play
	//Also make matrix of different alpha-beta and Minmax win-loss and plot average thinking times
	private ChessAINode minMax(Board board, int depth) {
		this.aiStats.increaseMoves();
		
		final long startTime = System.currentTimeMillis();
		double moveRating = max((ChessBoard) board, depth);
		final long thinkingTime = System.currentTimeMillis() - startTime;
		
		this.aiStats.addNodesExplored(this.nodesExplored);
		this.aiStats.addRating(moveRating);
		this.aiStats.addThinkingTime(thinkingTime);
		
		System.out.println(this.aiStats.getStats());
		
		ChessBoard newBoard = (ChessBoard) board.copy();
		this.bestNode.getMove().executeCheckedMove(newBoard);
		System.out.println(newBoard);
		System.out.println("Rating: " + this.bestNode.getRating());
		
		return this.bestNode;
	}
	
	private double min(Board board, int depth) {
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
		} else {
			List<GameMove> validMoves = this.rules.validMoves(board, this.pieces, this.pieces.get(this.minID));
			double lowestInBranch = Double.MAX_VALUE;
			for(GameMove move : validMoves) {
				ChessBoard testBoard = (ChessBoard) board.copy();
				((ChessMove) move).executeCheckedMove(testBoard);
				
				if(depth == ChessStatic.STARTING_DEPTH) { //ONLY saves the movements in the case when the depth is 0, moves from a higher depth are not relevant, only the rating is
					double currentNodeRating = max(testBoard, depth + 1);
					if(currentNodeRating <= lowestInBranch) { //Could be equal in case of loss or win if implemented as infinite rating.
						lowestInBranch = currentNodeRating;
						this.bestNode.changeNode((ChessMove) move, currentNodeRating);
					}
				} else {
					lowestInBranch = Math.min(max(testBoard, depth + 1), lowestInBranch);
				}
			}
			return lowestInBranch;
		}
	}
	
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

			/**
			 * APPARENTLY IN JAVA, Double.MIN_VALUE IS JUST THE MINIMUM ABSOLUTE VALUE, NOT NEGATIVE INFINITY.
			 * It should be expressed like so: -Double.MAX_VALUE
			 * https://stackoverflow.com/questions/3884793/why-is-double-min-value-in-not-negative
			 */
			double highestInBranch = -Double.MAX_VALUE;
			for(GameMove move : validMoves) {
				ChessBoard testBoard = (ChessBoard) board.copy();
				((ChessMove) move).executeCheckedMove(testBoard);
				
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