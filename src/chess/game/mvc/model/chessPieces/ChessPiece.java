package chess.game.mvc.model.chessPieces;

import chess.game.mvc.model.genericGameFiles.Piece;

public class ChessPiece extends Piece {
	
	private static final long serialVersionUID = 1L;
	
	private boolean isWhite;
	
	public ChessPiece() {
		super();
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

	public ChessPiece copyPiece() { //Not going to be used
		return new ChessPiece(this.getId(), this.isWhite);
	}
}