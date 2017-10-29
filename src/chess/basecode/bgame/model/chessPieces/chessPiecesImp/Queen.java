package chess.basecode.bgame.model.chessPieces.chessPiecesImp;

import chess.basecode.bgame.model.chessPieces.ChessPiece;

public class Queen extends ChessPiece {

	private static final long serialVersionUID = 1L;

	public Queen() {
		super();
	}
	
	public Queen(boolean isWhite) {
		super(isWhite);
	}

	public Queen(String id, boolean isWhite) {
		super(id, isWhite);
	}
}