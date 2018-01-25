package chess.game.mvc.model.chessFiles.chessAI;

import chess.game.mvc.model.chessFiles.ChessMove;

public class ChessMinMaxNode {
	
	private ChessMove move;
	private double rating;
	
	public ChessMinMaxNode() {}
	
	public ChessMinMaxNode(ChessMove cm) {
		this.move = cm;
	}
	
	public ChessMinMaxNode(ChessMove cm, double rating) {
		this.move = cm;
		this.rating = rating;
	}
	
	public ChessMove getMove() {
		return this.move;
	}
	
	public double getRating() {
		return this.rating;
	}
}
