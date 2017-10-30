package chess.game.mvc.model.chessPieces.chessPiecesImp;

import chess.game.mvc.model.chessPieces.ChessPiece;

public class King extends ChessPiece {

	private static final long serialVersionUID = 1L;

	private boolean canCastle;
	
	public King() {
		super();
		this.canCastle = true;
	}
	
	public King(boolean isWhite) {
		super(isWhite);
		this.canCastle = true;
	}

	public King(String id, boolean isWhite) {
		super(id, isWhite);
		this.canCastle = true;
	}
	
	public boolean getCastle() {
		return canCastle;
	}
	
	public void setCastle(boolean canCastle) {
		this.canCastle = canCastle;
	}
}