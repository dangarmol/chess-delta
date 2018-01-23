package chess.game.mvc.model.chessFiles.chessAI;

import chess.game.mvc.model.chessFiles.ChessMove;

public class ChessMinMaxNode {
	
	private ChessMove move;
	private double quality;
	
	public ChessMinMaxNode() {}
	
	public ChessMinMaxNode(ChessMove cm) {
		this.move = cm;
	}
	
	public ChessMinMaxNode(ChessMove cm, double quality) {
		this.move = cm;
		this.quality = quality;
	}
	
	public ChessMove getMove() {
		return this.move;
	}
	
	public double getQuality() {
		return this.quality;
	}
}
