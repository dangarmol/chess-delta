package chess.game.mvc.model.chessFiles.chessAI;

import java.util.List;

import chess.game.mvc.model.chessFiles.ChessBoard;
import chess.game.mvc.model.chessFiles.ChessConstants;
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
	private ChessBoardEvaluator evaluator;
	private GameRules rules;
	private List<Piece> pieces;
	private int minID;
	private int maxID;
	private ChessMinMaxNode bestNode;
	private ChessPiece maxPiece;
	private ChessPiece minPiece;
	
	public ChessMinMax() {
		this.level = ChessConstants.DEFAULT_MINMAX_LEVEL;
		this.evaluator = new ChessBoardEvaluator();
	}
	
	public ChessMinMax(int level) {
		this.level = level;
		this.evaluator = new ChessBoardEvaluator();
	}
	
	@Override
	public GameMove getMove(Piece p, Board board, List<Piece> playersPieces, List<Piece> pieceTypes, GameRules rules) {
		try {
			this.rules = rules;
			this.pieces = playersPieces;
			this.bestNode = new ChessMinMaxNode();
			this.maxID = (((ChessPiece) p).getWhite() ? ChessConstants.WHITE_ID : ChessConstants.BLACK_ID);
			this.minID = ((maxID == ChessConstants.WHITE_ID) ? ChessConstants.BLACK_ID : ChessConstants.WHITE_ID);
			this.maxPiece = (ChessPiece) this.pieces.get(this.maxID);
			this.minPiece = (ChessPiece) this.pieces.get(this.minID);
			return minMax(board, ChessConstants.STARTING_MINMAX_DEPTH).getMove();
		} catch (Exception e) {
			return null;
		}
	}
	
	private ChessMinMaxNode minMax(Board board, int depth) {
		if(max((ChessBoard) board, depth) == this.bestNode.getRating()) { //This might seem like a trivial check, but it assures that the "bestNode" is up to date.
			return this.bestNode;
		} else {
			return null; //This should never be reached
		}
	}
	
	/**
	 * TODO
	 * Alpha is changed in max if the value is higher than Alpha.
	 * Beta is changed in min if the value is lower than Beta.
	 * 
	 * Alpha means don't search further if you find a lower value.
	 * Beta means don't search further if you find a higher value.
	 * Alpha is the minimum guaranteed value for max.
	 * Beta is the max guaranteed value for min.
	 */
	private double min(Board board, int depth) {
		Pair<State, Piece> gameState = this.rules.updateState(board, pieces, this.minPiece); //TODO Test this. Should be max or min?
		if(gameState.getFirst().equals(State.Won)) { //TODO Test this
			if(gameState.getSecond().equals(this.minPiece)) { //TODO Test this
				return 1000;
			} else {
				return -1000;
			}
		} else if(gameState.getFirst().equals(State.Draw)) { //TODO Test this
			return 0;
		} else if(depth == this.level) { //If it's empty means that game is over! //TODO This may be wrong
			List<GameMove> validMoves = this.rules.validMoves(board, this.pieces, this.pieces.get(this.minID));
			return this.evaluator.getRating((ChessBoard) board, (ChessPiece) this.pieces.get(this.minID), this.maxPiece, validMoves);
		} else {
			List<GameMove> validMoves = this.rules.validMoves(board, this.pieces, this.pieces.get(this.minID));
			double lowestInBranch = Double.MAX_VALUE;
			for(GameMove move : validMoves) {
				Board testBoard = board.copy();
				((ChessMove) move).executeCheckedMove(testBoard);
				
				if(depth == ChessConstants.STARTING_MINMAX_DEPTH) { //ONLY saves the movements in the case when the depth is 0, moves from a higher depth are not relevant, only the rating is
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
		Pair<State, Piece> gameState = this.rules.updateState(board, pieces, this.maxPiece); //TODO Test this. Should be max or min?
		if(gameState.getFirst().equals(State.Won)) { //TODO Test this
			if(gameState.getSecond().equals(this.maxPiece)) { //TODO Test this
				return 1000;
			} else {
				return -1000;
			}
		} else if(gameState.getFirst().equals(State.Draw)) { //TODO Test this
			return 0;
		} else if(depth == this.level) { //If it's empty means that game is over! //TODO This may be wrong
			List<GameMove> validMoves = this.rules.validMoves(board, this.pieces, this.pieces.get(this.maxID));
			return this.evaluator.getRating((ChessBoard) board, (ChessPiece) this.pieces.get(this.maxID), this.maxPiece, validMoves);
		} else {
			List<GameMove> validMoves = this.rules.validMoves(board, this.pieces, this.pieces.get(this.maxID));
			//double highestInBranch = Double.MIN_VALUE;
			/**
			 * APPARENTLY IN JAVA, Double.MIN_VALUE IS JUST THE MINIMUM ABSOLUTE VALUE, NOT NEGATIVE INFINITY.
			 * It should be expressed like so: -Double.MAX_VALUE
			 * https://stackoverflow.com/questions/3884793/why-is-double-min-value-in-not-negative
			 */
			double highestInBranch = -Double.MAX_VALUE;
			for(GameMove move : validMoves) {
				Board testBoard = board.copy();
				((ChessMove) move).executeCheckedMove(testBoard);
				
				if(depth == ChessConstants.STARTING_MINMAX_DEPTH) { //ONLY saves the movements in the case when the depth is 0, moves from a higher depth are not relevant, only the rating is
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