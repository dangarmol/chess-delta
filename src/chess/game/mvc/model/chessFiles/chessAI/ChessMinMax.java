package chess.game.mvc.model.chessFiles.chessAI;

import java.util.List;

import chess.game.mvc.model.chessFiles.ChessBoard;
import chess.game.mvc.model.chessFiles.ChessConstants;
import chess.game.mvc.model.chessPieces.ChessPiece;
import chess.game.mvc.model.genericGameFiles.AIAlgorithm;
import chess.game.mvc.model.genericGameFiles.Board;
import chess.game.mvc.model.genericGameFiles.GameMove;
import chess.game.mvc.model.genericGameFiles.GameRules;
import chess.game.mvc.model.genericGameFiles.Piece;

public class ChessMinMax implements AIAlgorithm {

	private static final long serialVersionUID = 556625192621284461L;

	private int level;
	private ChessBoardEvaluator evaluator;
	private GameRules rules;
	private List<Piece> pieces;
	private int minID;
	private int maxID;
	
	public ChessMinMax(int level) {
		this.level = level;
		this.evaluator = new ChessBoardEvaluator();
	}
	
	@Override
	public GameMove getMove(Piece p, Board board, List<Piece> playersPieces, List<Piece> pieceTypes, GameRules rules) {
		try {
			this.rules = rules;
			this.pieces = playersPieces;
			this.maxID = (((ChessPiece) p).getWhite() ? ChessConstants.WHITE_ID : ChessConstants.BLACK_ID); //TODO Check this.
			this.minID = ((maxID == ChessConstants.WHITE_ID) ? ChessConstants.BLACK_ID : ChessConstants.WHITE_ID);
			return minMax(p, board, ChessConstants.STARTING_DEPTH).getMove();
		} catch (Exception e) {
			return null;
		}
	}
	
	private ChessMinMaxNode minMax(Piece p, Board board, int depth) {

		return null;
	}
	
	private double min(ChessBoard board, int depth) {
		//TODO Check if statement
		if(depth == level || this.rules.validMoves(board, this.pieces, this.pieces.get(minID)).isEmpty()) { //If it's empty means that game is over!
			return 1;
		}
		return 0;
	}
	
	private double max() {
		return 0;
	}
}