package chess.game.minmax;

import java.util.List;

import chess.game.mvc.model.genericGameFiles.AIAlgorithm;
import chess.game.mvc.model.genericGameFiles.Board;
import chess.game.mvc.model.genericGameFiles.GameError;
import chess.game.mvc.model.genericGameFiles.GameMove;
import chess.game.mvc.model.genericGameFiles.GameRules;
import chess.game.mvc.model.genericGameFiles.Pair;
import chess.game.mvc.model.genericGameFiles.Piece;
import chess.game.mvc.model.genericGameFiles.Game.State;

@SuppressWarnings("serial")
public class MinMax implements AIAlgorithm {

	final private static int _TREE_DEPTH = 5;

	private boolean useAblphaBeta;
	private int depth;

	public MinMax() {
		this(_TREE_DEPTH, true);
	}

	public MinMax(boolean useAblphaBeta) {
		this(_TREE_DEPTH, useAblphaBeta);
	}

	public MinMax(int depth) {
		this(depth, true);
	}

	public MinMax(int depth, boolean useAblphaBeta) {
		if (depth < 1) {
			throw new GameError("Invalid depth ('" + depth + "') for the MinMax algorithm, it should be at east 1");
		}
		this.depth = depth;
		this.useAblphaBeta = useAblphaBeta;
	}

	@Override
	public GameMove getMove(Piece p, Board board, List<Piece> playersPieces, List<Piece> pieceTypes, GameRules rules) {
		try {
			Pair<GameMove, Double> m = minmax(depth, -Double.MAX_VALUE, Double.MAX_VALUE, p, p, board, playersPieces, pieceTypes, rules);
			return m.getFirst();
		} catch (Exception e) {
			return null;
		}
	}

	private Pair<GameMove, Double> minmax(int d, Double alpha, Double beta, Piece p, Piece turn, Board board,
			List<Piece> playersPieces, List<Piece> pieceTypes, GameRules rules) throws InterruptedException {

		if (Thread.interrupted()) {
			throw new InterruptedException();
		}

		Pair<State, Piece> r = rules.updateState(board, playersPieces, turn);

		switch (r.getFirst()) {
		case Won:
			if (p.equals(r.getSecond())) {
				return new Pair<GameMove, Double>(null, 1.5 * (d + 1));
			} else {
				return new Pair<GameMove, Double>(null, -1.5 * (d + 1));
			}
		case Draw:
			return new Pair<GameMove, Double>(null, 0.0);
		default:
			break;
		}

		if (d < 1) {
			return new Pair<GameMove, Double>(null, rules.evaluate(board, playersPieces, turn, p));
		}

		List<GameMove> moves = rules.validMoves(board, playersPieces, turn);
		assert (moves.size() > 0);

		// max is p, min are all other players -- just trying to generalize to
		// more than two players, don't know if it makes sense!
		//
		if (p.equals(turn)) {
			return max(d, alpha, beta, p, turn, board, playersPieces, pieceTypes, rules, moves);
		} else {
			return min(d, alpha, beta, p, turn, board, playersPieces, pieceTypes, rules, moves);
		}
	}

	private Pair<GameMove, Double> max(int d, Double alpha, Double beta, Piece p, Piece turn, Board board,
			List<Piece> playersPieces, List<Piece> pieceTypes, GameRules rules, List<GameMove> moves) throws InterruptedException {

		GameMove selectedMove = null;

		for (GameMove m : moves) {
			Board b = board.copy();
			m.execute(b, playersPieces, pieceTypes);
			Piece nextTurn = rules.nextPlayer(b, playersPieces, turn);
			Pair<GameMove, Double> res = minmax(d - 1, alpha, beta, p, nextTurn, b, playersPieces, pieceTypes, rules);
			if (res.getSecond() > alpha) {
				alpha = res.getSecond();
				selectedMove = m;
			}
			if (useAblphaBeta && alpha >= beta) {
				return new Pair<GameMove, Double>(selectedMove, alpha);
			}
		}

		return new Pair<GameMove, Double>(selectedMove, alpha);
	}

	private Pair<GameMove, Double> min(int d, Double alpha, Double beta, Piece p, Piece turn, Board board,
			List<Piece> playersPieces, List<Piece> pieceTypes, GameRules rules, List<GameMove> moves) throws InterruptedException {

		GameMove selectedMove = null;

		for (GameMove m : moves) {
			Board b = board.copy();
			m.execute(b, playersPieces, pieceTypes);
			Piece nextTurn = rules.nextPlayer(b, playersPieces, turn);
			Pair<GameMove, Double> res = minmax(d - 1, alpha, beta, p, nextTurn, b, playersPieces, pieceTypes, rules);
			if (res.getSecond() < beta) {
				beta = res.getSecond();
				selectedMove = m;
			}
			if (useAblphaBeta && beta <= alpha) {
				return new Pair<GameMove, Double>(selectedMove, beta);
			}

		}

		return new Pair<GameMove, Double>(selectedMove, beta);
	}

}
