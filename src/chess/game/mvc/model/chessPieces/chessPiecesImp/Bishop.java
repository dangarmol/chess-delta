package chess.game.mvc.model.chessPieces.chessPiecesImp;

import chess.game.mvc.model.chessPieces.ChessPiece;

public class Bishop extends ChessPiece {

	private static final long serialVersionUID = 1L;

	public Bishop() {
		super();
	}
	
	public Bishop(boolean isWhite) {
		super(isWhite);
	}

	public Bishop(String id, boolean isWhite) {
		super(id, isWhite);
	}
	
	@Override
	public Bishop copyPiece() {
		return new Bishop(this.getId(), this.getWhite());
	}
}
