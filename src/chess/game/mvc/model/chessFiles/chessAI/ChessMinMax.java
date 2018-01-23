package chess.game.mvc.model.chessFiles.chessAI;

import java.util.List;

import chess.game.mvc.model.genericGameFiles.AIAlgorithm;
import chess.game.mvc.model.genericGameFiles.Board;
import chess.game.mvc.model.genericGameFiles.GameMove;
import chess.game.mvc.model.genericGameFiles.GameRules;
import chess.game.mvc.model.genericGameFiles.Pair;
import chess.game.mvc.model.genericGameFiles.Piece;

public class ChessMinMax implements AIAlgorithm {

	private static final long serialVersionUID = 556625192621284461L;

	private int level;
	
	public ChessMinMax(int level) {
		this.level = level;
	}
	
	@Override
	public GameMove getMove(Piece p, Board board, List<Piece> playersPieces, List<Piece> pieceTypes, GameRules rules) {
		try {
			return minMax(p, board, playersPieces, pieceTypes, rules, this.level).getMove();
		} catch (Exception e) {
			return null;
		}
	}
	
	private ChessMinMaxNode minMax(Piece p, Board board, List<Piece> playersPieces, List<Piece> pieceTypes, GameRules rules, int level) {
		
		return null;
	}
}