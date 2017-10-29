package chess.basecode.bgame.model.chessPieces;

import chess.basecode.bgame.model.Piece;

public class ChessPiece extends Piece {
	
	private static final long serialVersionUID = 1L;
	
	private boolean isWhite;
	
	public ChessPiece() {
		super();
		//TODO Add isWhite or not
	}
	
	public ChessPiece(boolean isWhite) {
		super();
		this.isWhite = isWhite;
	}
	
	public ChessPiece(String id, boolean isWhite) {
		super(id);
		this.isWhite = isWhite;
	}
	
	public void setWhite(boolean isWhite) {
		this.isWhite = isWhite;
	}
	
	public boolean getWhite() {
		return this.isWhite;
	}
}