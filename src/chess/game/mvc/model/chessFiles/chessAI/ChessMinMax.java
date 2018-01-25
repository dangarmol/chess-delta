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
import chess.game.mvc.model.genericGameFiles.Piece;

public class ChessMinMax implements AIAlgorithm {

	private static final long serialVersionUID = 556625192621284461L;

	private int level; //How hard it is to win against the AI. The higher this number, the more intelligent it becomes.
	private ChessBoardEvaluator evaluator;
	private GameRules rules;
	private List<Piece> pieces;
	private List<Piece> pieceTypes;
	private int minID;
	private int maxID;
	
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
			this.pieceTypes = pieceTypes;
			this.maxID = (((ChessPiece) p).getWhite() ? ChessConstants.WHITE_ID : ChessConstants.BLACK_ID); //TODO Check this.
			this.minID = ((maxID == ChessConstants.WHITE_ID) ? ChessConstants.BLACK_ID : ChessConstants.WHITE_ID);
			return minMax(p, board, ChessConstants.STARTING_MINMAX_DEPTH).getMove();
		} catch (Exception e) {
			return null;
		}
	}
	
	private ChessMinMaxNode minMax(Piece p, Board board, int depth) {
		//TODO This needs to return a move, not a rating. Probably adding moves on the last part of the penultimate line of the functions.
		return null;
	}
	
	private double min(ChessBoard board, int depth) {
		List<GameMove> validMoves = this.rules.validMoves(board, this.pieces, this.pieces.get(this.minID));
		if(depth == this.level || validMoves.isEmpty()) { //If it's empty means that game is over!
			return this.evaluator.getRating(board, this.pieces.get(this.minID));
		} else {
			double lowest = Double.MAX_VALUE;
			for(GameMove move : validMoves) {
				ChessBoard testBoard = board.copyChessBoard();
				((ChessMove) move).execute(testBoard, this.pieces, this.pieceTypes);
				/*A different execute function should be created on the ChessMove class. However, this should
				never cause problems, since all the executed moves are from the list of checked moves*/
				
				lowest = Math.min(max(board, depth + 1), lowest);
			}
			return lowest;
		}
	}
	
	private double max(ChessBoard board, int depth) {
		List<GameMove> validMoves = this.rules.validMoves(board, this.pieces, this.pieces.get(this.maxID));
		if(depth == this.level || validMoves.isEmpty()) { //If it's empty means that game is over!
			return this.evaluator.getRating(board, this.pieces.get(this.maxID));
		} else {
			double highest = Double.MIN_VALUE;
			for(GameMove move : validMoves) {
				ChessBoard testBoard = board.copyChessBoard();
				((ChessMove) move).execute(testBoard, this.pieces, this.pieceTypes);
				/*A different execute function should be created on the ChessMove class. However, this should
				never cause problems, since all the executed moves are from the list of checked moves*/
				
				highest = Math.max(min(board, depth + 1), highest);
			}
			return highest;
		}
	}
}