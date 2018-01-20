package chess.game.mvc.model.chessFiles;

import java.util.List;

import chess.game.mvc.model.genericGameFiles.AIAlgorithm;
import chess.game.mvc.model.genericGameFiles.Board;
import chess.game.mvc.model.genericGameFiles.GameMove;
import chess.game.mvc.model.genericGameFiles.GameRules;
import chess.game.mvc.model.genericGameFiles.Piece;

public class ChessAI implements AIAlgorithm {

	private static final long serialVersionUID = 556625192621284461L;

	
	
	@Override
	public GameMove getMove(Piece p, Board board, List<Piece> playersPieces, List<Piece> pieceTypes, GameRules rules) {
		// TODO Auto-generated method stub
		return null;
	}
}