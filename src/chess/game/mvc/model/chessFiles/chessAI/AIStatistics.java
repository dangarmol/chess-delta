package chess.game.mvc.model.chessFiles.chessAI;

import chess.game.mvc.model.chessFiles.ChessStatic;

public class AIStatistics {
	
	private int depth;
	private int totalMoves;
	private long totalThinkingTime;
	private long totalNodesExplored;
	private double totalRating;
	private long lastThinkingTime;
	private long lastNodesExplored;
	private double lastRating;
	private boolean isWhite;
	
	public AIStatistics() {
		this.depth = ChessStatic.DEFAULT_MINMAX_LEVEL;
		this.totalMoves = 0;
		this.totalThinkingTime = 0;
		this.totalNodesExplored = 0;
		this.totalRating = 0;
		this.isWhite = true; //TODO Maybe remove
	}
	
	public AIStatistics(int depth) {
		this.depth = depth;
		this.totalMoves = 0;
		this.totalThinkingTime = 0;
		this.totalNodesExplored = 0;
		this.totalRating = 0;
		this.isWhite = true; //TODO Maybe remove
	}
	
	public void increaseMoves() {
		this.totalMoves++;
	}
	
	public void setColour(boolean isWhite) {
		this.isWhite = isWhite;
	}
	
	public void addThinkingTime(long time) {
		this.totalThinkingTime += time;
		this.lastThinkingTime = time;
	}
	
	public void addNodesExplored(long nodes) {
		this.totalNodesExplored += nodes;
		this.lastNodesExplored = nodes;
	}
	
	public void addRating(double rating) {
		if(rating != Double.MAX_VALUE && rating != -Double.MAX_VALUE) {
			this.totalRating += rating;
			this.lastRating = rating;
		} else {
			if(rating == Double.MAX_VALUE) {
				this.lastRating = 1000;
				this.totalRating += this.lastRating;
			} else if(rating == -Double.MAX_VALUE) {
				this.lastRating = -1000;
				this.totalRating += this.lastRating;
			}
		}
	}
	
	public String getStats() {
		if(this.totalMoves == 0) this.totalMoves = 1; //To avoid dividing by zero.
		String stats = "";
		stats += "###\nMove number " + this.totalMoves + " for " + (this.isWhite ? "WHITE" : "BLACK") + " player.\n";
		stats += "AI has been thinking for " + this.lastThinkingTime + " ms this turn. (Depth: " + this.depth + " | ";
		stats += (this.totalThinkingTime / this.totalMoves) + " ms average. | " + this.totalThinkingTime + " ms total.)\n";
		stats += "Nodes explored for last move: " + this.lastNodesExplored + ". ";
		stats += "(" + (this.totalNodesExplored / this.totalMoves) + " nodes average. | " + this.totalNodesExplored + " nodes total.)\n";
		stats += "Last move rating: " + this.lastRating + " (Average: " + (this.totalRating / this.totalMoves) + ")\n";
		stats += "###";
		return stats;
	}
}