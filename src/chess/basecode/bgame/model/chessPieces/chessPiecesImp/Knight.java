package chess.basecode.bgame.model.chessPieces.chessPiecesImp;

import chess.basecode.bgame.model.chessPieces.ChessPiece;

public class Knight extends ChessPiece {

	private static final long serialVersionUID = 1L;

	public Knight() {
		super();
	}
	
	public Knight(boolean isWhite) {
		super(isWhite);
	}

	public Knight(String id, boolean isWhite) {
		super(id, isWhite);
	}
}