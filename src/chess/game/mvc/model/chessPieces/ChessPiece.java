package chess.game.mvc.model.chessPieces;

import chess.game.mvc.model.chessPieces.chessPiecesImp.*;
import chess.game.mvc.model.genericGameFiles.GameError;
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

	@Override
	public String toString() {
		String ret = "";

		if(this.isWhite) {
			ret += "W";
		} else {
			ret += "B";
		}

		if(this instanceof Rook) {
			ret += "R";
		} else if(this instanceof Knight) {
			ret += "N";
		} else if(this instanceof Bishop) {
			ret += "B";
		} else if(this instanceof King) {
			ret += "K";
		} else if(this instanceof Queen) {
			ret += "Q";
		} else if(this instanceof Pawn) {
			ret += "P";
		}

		return ret;
	}
}