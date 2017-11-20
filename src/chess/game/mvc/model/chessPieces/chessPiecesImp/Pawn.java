package chess.game.mvc.model.chessPieces.chessPiecesImp;

import chess.game.mvc.model.chessPieces.ChessPiece;

public class Pawn extends ChessPiece {

	private static final long serialVersionUID = 1L;

	private boolean canPassant; //If this pawn can be captured "En Passant"
	private boolean firstMove;
	
	public Pawn() {
		super();
		this.canPassant = false;
		this.firstMove = true;
	}
	
	public Pawn(boolean isWhite) {
		super(isWhite);
		this.canPassant = false;
		this.firstMove = true;
	}

	public Pawn(String id, boolean isWhite) {
		super(id, isWhite);
		this.canPassant = false;
		this.firstMove = true;
	}
	
	public boolean getPassant() {
		return canPassant;
	}
	
	public void setPassant(boolean canPassant) {
		this.canPassant = canPassant;
	}
	
	public boolean getFirstMove() {
		return firstMove;
	}
	
	public void setFirstMove(boolean firstMove) {
		this.firstMove = firstMove;
	}
}