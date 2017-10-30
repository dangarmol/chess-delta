package chess.game.mvc.model.chessPieces.chessPiecesImp;

import chess.game.mvc.model.chessPieces.ChessPiece;

public class Pawn extends ChessPiece {

	private static final long serialVersionUID = 1L;

	private boolean canPassant; //If this pawn can be captured "En Passant"
	
	public Pawn() {
		super();
		this.canPassant = false;
	}
	
	public Pawn(boolean isWhite) {
		super(isWhite);
		this.canPassant = false;
	}

	public Pawn(String id, boolean isWhite) {
		super(id, isWhite);
		this.canPassant = false;
	}
	
	public boolean getPassant() {
		return canPassant;
	}
	
	public void setPassant(boolean canPassant) {
		this.canPassant = canPassant;
	}
}