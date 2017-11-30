package chess.game.mvc.model.chessFiles;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import chess.game.mvc.model.chessPieces.ChessPiece;
import chess.game.mvc.model.chessPieces.ChessPieceID;
import chess.game.mvc.model.chessPieces.chessPiecesImp.Bishop;
import chess.game.mvc.model.chessPieces.chessPiecesImp.King;
import chess.game.mvc.model.chessPieces.chessPiecesImp.Knight;
import chess.game.mvc.model.chessPieces.chessPiecesImp.Pawn;
import chess.game.mvc.model.chessPieces.chessPiecesImp.Queen;
import chess.game.mvc.model.chessPieces.chessPiecesImp.Rook;
import chess.game.mvc.model.genericGameFiles.Board;
import chess.game.mvc.model.genericGameFiles.FiniteRectBoard;
import chess.game.mvc.model.genericGameFiles.Game;
import chess.game.mvc.model.genericGameFiles.GameMove;
import chess.game.mvc.model.genericGameFiles.GameRules;
import chess.game.mvc.model.genericGameFiles.Pair;
import chess.game.mvc.model.genericGameFiles.Piece;
import chess.game.mvc.model.genericGameFiles.Game.State;
import chess.game.mvc.model.genericGameFiles.GameError;

public class ChessRules implements GameRules {
	
	// This object is returned by gameOver to indicate that the game is not
	// over. Just to avoid creating it multiple times, etc.
	protected final Pair<State, Piece> gameInPlayResult = new Pair<State, Piece>(State.InPlay, null);
	
	public ChessRules() {}

	@Override
	public String gameDesc() {
		return "Chess";
	}
	
	@Override
	public Board createBoard(List<Piece> pieces, List<Piece> pieceTypes) {
		Board board = new ChessBoard();		
		
		//Sets the positions for every piece on their starting positions.
		board.setPosition(0, 0, pieceTypes.get(ChessPieceID.BLACK_ROOK_A));
		board.setPosition(0, 1, pieceTypes.get(ChessPieceID.BLACK_KNIGHT));
		board.setPosition(0, 2, pieceTypes.get(ChessPieceID.BLACK_BISHOP));
		board.setPosition(0, 3, pieceTypes.get(ChessPieceID.BLACK_QUEEN));
		board.setPosition(0, 4, pieceTypes.get(ChessPieceID.BLACK_KING));
		board.setPosition(0, 5, pieceTypes.get(ChessPieceID.BLACK_BISHOP));
		board.setPosition(0, 6, pieceTypes.get(ChessPieceID.BLACK_KNIGHT));
		board.setPosition(0, 7, pieceTypes.get(ChessPieceID.BLACK_ROOK_H));
		
		board.setPosition(7, 0, pieceTypes.get(ChessPieceID.WHITE_ROOK_A));
		board.setPosition(7, 1, pieceTypes.get(ChessPieceID.WHITE_KNIGHT));
		board.setPosition(7, 2, pieceTypes.get(ChessPieceID.WHITE_BISHOP));
		board.setPosition(7, 3, pieceTypes.get(ChessPieceID.WHITE_QUEEN));
		board.setPosition(7, 4, pieceTypes.get(ChessPieceID.WHITE_KING));
		board.setPosition(7, 5, pieceTypes.get(ChessPieceID.WHITE_BISHOP));
		board.setPosition(7, 6, pieceTypes.get(ChessPieceID.WHITE_KNIGHT));
		board.setPosition(7, 7, pieceTypes.get(ChessPieceID.WHITE_ROOK_H));
		
		board.setPosition(1, 0, pieceTypes.get(ChessPieceID.BLACK_PAWN_A));
		board.setPosition(6, 0, pieceTypes.get(ChessPieceID.WHITE_PAWN_A));
		board.setPosition(1, 1, pieceTypes.get(ChessPieceID.BLACK_PAWN_B));
		board.setPosition(6, 1, pieceTypes.get(ChessPieceID.WHITE_PAWN_B));
		board.setPosition(1, 2, pieceTypes.get(ChessPieceID.BLACK_PAWN_C));
		board.setPosition(6, 2, pieceTypes.get(ChessPieceID.WHITE_PAWN_C));
		board.setPosition(1, 3, pieceTypes.get(ChessPieceID.BLACK_PAWN_D));
		board.setPosition(6, 3, pieceTypes.get(ChessPieceID.WHITE_PAWN_D));
		board.setPosition(1, 4, pieceTypes.get(ChessPieceID.BLACK_PAWN_E));
		board.setPosition(6, 4, pieceTypes.get(ChessPieceID.WHITE_PAWN_E));
		board.setPosition(1, 5, pieceTypes.get(ChessPieceID.BLACK_PAWN_F));
		board.setPosition(6, 5, pieceTypes.get(ChessPieceID.WHITE_PAWN_F));
		board.setPosition(1, 6, pieceTypes.get(ChessPieceID.BLACK_PAWN_G));
		board.setPosition(6, 6, pieceTypes.get(ChessPieceID.WHITE_PAWN_G));
		board.setPosition(1, 7, pieceTypes.get(ChessPieceID.BLACK_PAWN_H));
		board.setPosition(6, 7, pieceTypes.get(ChessPieceID.WHITE_PAWN_H));
		
		return board;
	}
	
	@Override
	public Pair<State, Piece> updateState(Board board, List<Piece> pieces, Piece lastPlayer) {
		//TODO This is the next function to modify.
		
		Pair<State, Piece> gameState;
		boolean noValidMovesAnyPiece = false;
		int playingPiecesCount = 0; //Counter for the active players (still have pieces on board)
		
		Piece currentPlayer = nextPlayer(board, pieces, lastPlayer);
		gameState = gameInPlayResult; //gameInPlayResult = IF GAME IS NOT FINISHED, STILL IN PLAY
		
		if(currentPlayer != null) //If next player can move
		{
			
			if(playingPiecesCount == 1) //If only one type of pieces remain (even though the board is not full)
			{
				gameState = new Pair<State, Piece>(State.Won, currentPlayer);
			}
			
			if(lastPlayer == currentPlayer)
			{
				//stuckCounter++; //This is only used in an extreme case where obstacles block most pieces except
				//for one that gets stuck moving alternatively between 2 positions, it is a rare case, but it
				//happened while testing the game (it is not really probable and can be removed in most cases)
				//IT WILL END THE GAME AFTER 10 REPEATS
			}
			else if(lastPlayer != currentPlayer)
			{
				//stuckCounter = 0;
			}
		}
		else
		{
			noValidMovesAnyPiece = true; //Noone can move (an extreme case where noone can move but board is not full yet)
		}
		
		if(board.isFull() || noValidMovesAnyPiece /*|| stuckCounter >= 10*/)
		{
			//If board is full, noone can move, or a piece got stuck, it checks the winner of the game.
			//gameState = checkWinnerFullBoard(board, pieces);
		}
		
		return gameState;
	}
	
	private Pair<State, Piece> checkWinnerFullBoard(Board board, List<Piece> pieces)
	{
		Pair<State, Piece> winner = null;
		int currentCounter, highest = 0, winnerIndex = 0;
		boolean thereIsWinner = true; //There is a winner, if 2 players end up even, the game finishes as a Draw.

		for(int index = 0; index < pieces.size(); index++)
		{
			currentCounter = board.getPieceCount(pieces.get(index));
			if (currentCounter > highest)
			{
				thereIsWinner = true;
				highest = currentCounter;
				winnerIndex = index;
			}
			else if (currentCounter == highest)
			{
				thereIsWinner = false;
				winner = new Pair<State, Piece>(State.Draw, null);
				//In this case, two players have the greatest amount of pieces.
			}
		}
		
		if(thereIsWinner)
		{
			winner = new Pair<State, Piece>(State.Won, pieces.get(winnerIndex));
		}
		
		return winner;
	}
	
	@Override
	public List<GameMove> validMoves(Board board, List<Piece> playersPieces, Piece turn) { //TODO This needs to be created.
		ChessBoard originalBoard = (ChessBoard) board;
		boolean isWhiteTurn = ((ChessPiece) turn).getWhite();
		ChessBoard testBoard = originalBoard.copyChessBoard();
		List<GameMove> legalMoves = new ArrayList<GameMove>(); //Every single possible move on the board is saved here
		for (int rowX = ChessConstants.MIN_DIM; rowX <= ChessConstants.MAX_DIM; rowX++) { //Exploring rows and cols.
			for (int colY = ChessConstants.MIN_DIM; colY <= ChessConstants.MAX_DIM; colY++) {
				if(originalBoard.getChessPosition(rowX, colY) != null && //If the position is not empty...
					originalBoard.getChessPosition(rowX, colY).getWhite() == isWhiteTurn){ //...and it is from the current turn.
					if(originalBoard.getChessPosition(rowX, colY) instanceof Pawn) {
						this.addPawnMoves(testBoard, originalBoard, legalMoves, rowX, colY, turn);
					} else if(originalBoard.getChessPosition(rowX, colY) instanceof Rook) {
						this.addHorizVertMoves(testBoard, originalBoard, legalMoves, rowX, colY, turn);
					} else if(originalBoard.getChessPosition(rowX, colY) instanceof Knight) {
						this.addKnightMoves(testBoard, originalBoard, legalMoves, rowX, colY, turn);
					} else if(originalBoard.getChessPosition(rowX, colY) instanceof Bishop) {
						this.addDiagonalMoves(testBoard, originalBoard, legalMoves, rowX, colY, turn);
					} else if(originalBoard.getChessPosition(rowX, colY) instanceof Queen) {
						this.addHorizVertMoves(testBoard, originalBoard, legalMoves, rowX, colY, turn);
						this.addDiagonalMoves(testBoard, originalBoard, legalMoves, rowX, colY, turn);
					} else if(originalBoard.getChessPosition(rowX, colY) instanceof King) {
						this.addKingMoves(testBoard, originalBoard, legalMoves, rowX, colY, turn);
					} else {
						throw new GameError("Internal error, piece type not found!");
					}
				}
			}
		}
		return legalMoves;
	}
	
	//TODO This should work, check anyway. Also check if isMoveLegal works as it should.
	private void checkAndAdd(ChessMove testMove, ChessBoard testBoard, ChessBoard originalBoard, List<GameMove> legalMoves) {
		if(testMove.isMoveLegal(testBoard)) {
			//System.out.println("Move: " + testMove);
			legalMoves.add(testMove);
			//System.out.println("List: " + legalMoves);
		}
	}
	
	private void addHorizVertMoves(ChessBoard testBoard, ChessBoard originalBoard, List<GameMove> legalMoves, int rowX, int colY, Piece turn) {
		int rowDes = rowX - 1, colDes = colY;
		while(rowDes >= ChessConstants.MIN_DIM) { //North direction
			this.checkAndAdd(new ChessMove(rowX, colY, rowDes, colDes, turn), testBoard, originalBoard, legalMoves);
			if(originalBoard.getChessPosition(rowDes, colDes) != null) break;
			rowDes--;
		}
		rowDes = rowX + 1; colDes = colY;
		while(rowDes <= ChessConstants.MAX_DIM) { //South direction
			this.checkAndAdd(new ChessMove(rowX, colY, rowDes, colDes, turn), testBoard, originalBoard, legalMoves);
			if(originalBoard.getChessPosition(rowDes, colDes) != null) break;
			rowDes++;
		}
		rowDes = rowX; colDes = colY + 1;
		while(colDes <= ChessConstants.MAX_DIM) { //East direction
			this.checkAndAdd(new ChessMove(rowX, colY, rowDes, colDes, turn), testBoard, originalBoard, legalMoves);
			if(originalBoard.getChessPosition(rowDes, colDes) != null) break;
			colDes++;
		}
		rowDes = rowX; colDes = colY - 1;
		while(colDes >= ChessConstants.MIN_DIM) { //West direction
			this.checkAndAdd(new ChessMove(rowX, colY, rowDes, colDes, turn), testBoard, originalBoard, legalMoves);
			if(originalBoard.getChessPosition(rowDes, colDes) != null) break;
			colDes--;
		}
	}
	
	private void addDiagonalMoves(ChessBoard testBoard, ChessBoard originalBoard, List<GameMove> legalMoves, int rowX, int colY, Piece turn) {
		int rowDes = rowX - 1, colDes = colY - 1;
		while(rowDes >= ChessConstants.MIN_DIM && colDes >= ChessConstants.MIN_DIM) { //NorthWest direction
			this.checkAndAdd(new ChessMove(rowX, colY, rowDes, colDes, turn), testBoard, originalBoard, legalMoves);
			if(originalBoard.getChessPosition(rowDes, colDes) != null) break;
			rowDes--;
			colDes--;
		}
		
		rowDes = rowX - 1; colDes = colY + 1;
		while(rowDes >= ChessConstants.MIN_DIM && colDes <= ChessConstants.MAX_DIM) { //NorthEast direction
			this.checkAndAdd(new ChessMove(rowX, colY, rowDes, colDes, turn), testBoard, originalBoard, legalMoves);
			if(originalBoard.getChessPosition(rowDes, colDes) != null) break;
			rowDes--;
			colDes++;
		}
		
		rowDes = rowX + 1; colDes = colY + 1;
		while(colDes <= ChessConstants.MAX_DIM && rowDes <= ChessConstants.MAX_DIM) { //SouthEast direction
			this.checkAndAdd(new ChessMove(rowX, colY, rowDes, colDes, turn), testBoard, originalBoard, legalMoves);
			if(originalBoard.getChessPosition(rowDes, colDes) != null) break;
			colDes++;
			rowDes++;
		}
		
		rowDes = rowX + 1; colDes = colY - 1;
		while(colDes >= ChessConstants.MIN_DIM && rowDes <= ChessConstants.MAX_DIM) { //SouthWest direction
			this.checkAndAdd(new ChessMove(rowX, colY, rowDes, colDes, turn), testBoard, originalBoard, legalMoves);
			if(originalBoard.getChessPosition(rowDes, colDes) != null) break;
			colDes--;
			rowDes++;
		}
	}
	
	private void addKnightMoves(ChessBoard testBoard, ChessBoard originalBoard, List<GameMove> legalMoves, int rowX, int colY, Piece turn) {
		this.checkAndAdd(new ChessMove(rowX, colY, rowX - 2, colY - 1, turn), testBoard, originalBoard, legalMoves); //2 up 1 left
		this.checkAndAdd(new ChessMove(rowX, colY, rowX - 2, colY + 1, turn), testBoard, originalBoard, legalMoves); //2 up 1 right
		this.checkAndAdd(new ChessMove(rowX, colY, rowX + 2, colY - 1, turn), testBoard, originalBoard, legalMoves); //2 down 1 left
		this.checkAndAdd(new ChessMove(rowX, colY, rowX + 2, colY + 1, turn), testBoard, originalBoard, legalMoves); //2 down 1 right
		this.checkAndAdd(new ChessMove(rowX, colY, rowX - 1, colY - 2, turn), testBoard, originalBoard, legalMoves); //2 left 1 up
		this.checkAndAdd(new ChessMove(rowX, colY, rowX + 1, colY - 2, turn), testBoard, originalBoard, legalMoves); //2 left 1 down
		this.checkAndAdd(new ChessMove(rowX, colY, rowX - 1, colY + 2, turn), testBoard, originalBoard, legalMoves); //2 right 1 up
		this.checkAndAdd(new ChessMove(rowX, colY, rowX + 1, colY + 2, turn), testBoard, originalBoard, legalMoves); //2 right 1 down
	}
	
	private void addPawnMoves(ChessBoard testBoard, ChessBoard originalBoard, List<GameMove> legalMoves, int rowX, int colY, Piece turn) {
		if(originalBoard.getChessPosition(rowX, colY).getWhite()) { //If it's a white Pawn it will explore some moves...
			this.checkAndAdd(new ChessMove(rowX, colY, rowX - 1, colY, turn), testBoard, originalBoard, legalMoves); //Simple move
			this.checkAndAdd(new ChessMove(rowX, colY, rowX - 2, colY, turn), testBoard, originalBoard, legalMoves); //Double move
			this.checkAndAdd(new ChessMove(rowX, colY, rowX - 1, colY - 1, turn), testBoard, originalBoard, legalMoves); //Diagonal move left
			this.checkAndAdd(new ChessMove(rowX, colY, rowX - 1, colY + 1, turn), testBoard, originalBoard, legalMoves); //Diagonal move right
		} else { //...if it's a black one it will explore others.
			this.checkAndAdd(new ChessMove(rowX, colY, rowX + 1, colY, turn), testBoard, originalBoard, legalMoves); //Simple move
			this.checkAndAdd(new ChessMove(rowX, colY, rowX + 2, colY, turn), testBoard, originalBoard, legalMoves); //Double move
			this.checkAndAdd(new ChessMove(rowX, colY, rowX + 1, colY - 1, turn), testBoard, originalBoard, legalMoves); //Diagonal move left
			this.checkAndAdd(new ChessMove(rowX, colY, rowX + 1, colY + 1, turn), testBoard, originalBoard, legalMoves); //Diagonal move right
		}
	}
	
	private void addKingMoves(ChessBoard testBoard, ChessBoard originalBoard, List<GameMove> legalMoves, int rowX, int colY, Piece turn) {
		this.checkAndAdd(new ChessMove(rowX, colY, rowX - 1, colY - 1, turn), testBoard, originalBoard, legalMoves); //NW move
		this.checkAndAdd(new ChessMove(rowX, colY, rowX - 1, colY, turn), testBoard, originalBoard, legalMoves); //N move
		this.checkAndAdd(new ChessMove(rowX, colY, rowX - 1, colY + 1, turn), testBoard, originalBoard, legalMoves); //NE move
		this.checkAndAdd(new ChessMove(rowX, colY, rowX, colY + 1, turn), testBoard, originalBoard, legalMoves); //E move
		this.checkAndAdd(new ChessMove(rowX, colY, rowX, colY - 1, turn), testBoard, originalBoard, legalMoves); //W move
		this.checkAndAdd(new ChessMove(rowX, colY, rowX + 1, colY - 1, turn), testBoard, originalBoard, legalMoves); //SW move
		this.checkAndAdd(new ChessMove(rowX, colY, rowX + 1, colY, turn), testBoard, originalBoard, legalMoves); //S move
		this.checkAndAdd(new ChessMove(rowX, colY, rowX + 1, colY + 1, turn), testBoard, originalBoard, legalMoves); //SE move
		
		this.checkAndAdd(new ChessMove(rowX, colY, rowX, colY + 2, turn), testBoard, originalBoard, legalMoves); //Short Castling
		this.checkAndAdd(new ChessMove(rowX, colY, rowX, colY - 2, turn), testBoard, originalBoard, legalMoves); //Long Castling
	}

	@Override
	public Piece nextPlayer(Board board, List<Piece> playersPieces, Piece lastPlayer){
		Piece nextPlayer;
		
		if(lastPlayer == playersPieces.get(ChessConstants.WHITE_ID))
			nextPlayer = playersPieces.get(ChessConstants.BLACK_ID);
		else if (lastPlayer == playersPieces.get(ChessConstants.BLACK_ID))
			nextPlayer = playersPieces.get(ChessConstants.WHITE_ID);
		else
			throw new GameError("Something went wrong while changing turns. Unrecognised player. This should be unreachable.");
		
		//System.out.println("Final List: " + validMoves(board, playersPieces, nextPlayer));
		//TODO This makes game crash. Changes turn to null wheneven King is in Check!
		if(validMoves(board, playersPieces, nextPlayer).isEmpty()) //Check if he can move any of his pieces
			return null; //Returns null if next player can't move!

		//int moveNum = validMoves(board, playersPieces, nextPlayer).size(); //TODO Check this.
		//System.out.println(moveNum + " possible moves for " + nextPlayer);
		return nextPlayer;
	}

	@Override
	public Piece initialPlayer(Board board, List<Piece> pieces) {
		return pieces.get(0); //White always starts
	}

	@Override
	public double evaluate(Board board, List<Piece> pieces, Piece turn, Piece p) {
		// TODO Check ConnectNRules
		return 0;
	}

	@Override
	public int minPlayers() {
		return 2;
	}

	@Override
	public int maxPlayers() {
		return 2;
	}

}