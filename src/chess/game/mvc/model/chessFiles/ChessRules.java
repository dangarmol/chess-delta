package chess.game.mvc.model.chessFiles;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import chess.game.mvc.model.chessPieces.ChessPieceID;
import chess.game.mvc.model.genericGameFiles.Board;
import chess.game.mvc.model.genericGameFiles.FiniteRectBoard;
import chess.game.mvc.model.genericGameFiles.Game;
import chess.game.mvc.model.genericGameFiles.GameMove;
import chess.game.mvc.model.genericGameFiles.GameRules;
import chess.game.mvc.model.genericGameFiles.Pair;
import chess.game.mvc.model.genericGameFiles.Piece;
import chess.game.mvc.model.genericGameFiles.Game.State;

public class ChessRules implements GameRules { //Should it be abstract?
	
	// This object is returned by gameOver to indicate that the game is not
	// over. Just to avoid creating it multiple times, etc.
	
	protected final Pair<State, Piece> gameInPlayResult = new Pair<State, Piece>(State.InPlay, null);
	
	public ChessRules() {}

	@Override
	public String gameDesc() {
		return "Chess";
	}
	
	@Override
	public Board createBoard(List<Piece> pieces) { //pieces.get(0) = White, (1) = Black
		Board board = new FiniteRectBoard(8, 8);		
		
		//Sets the positions for every piece on their starting positions.
		board.setPosition(0, 0, pieces.get(ChessPieceID.BLACK_ROOK));
		board.setPosition(0, 1, pieces.get(ChessPieceID.BLACK_KNIGHT));
		board.setPosition(0, 2, pieces.get(ChessPieceID.BLACK_BISHOP));
		board.setPosition(0, 3, pieces.get(ChessPieceID.BLACK_QUEEN));
		board.setPosition(0, 4, pieces.get(ChessPieceID.BLACK_KING));
		board.setPosition(0, 5, pieces.get(ChessPieceID.BLACK_BISHOP));
		board.setPosition(0, 6, pieces.get(ChessPieceID.BLACK_KNIGHT));
		board.setPosition(0, 7, pieces.get(ChessPieceID.BLACK_ROOK));
		
		board.setPosition(7, 0, pieces.get(ChessPieceID.WHITE_ROOK));
		board.setPosition(7, 1, pieces.get(ChessPieceID.WHITE_KNIGHT));
		board.setPosition(7, 2, pieces.get(ChessPieceID.WHITE_BISHOP));
		board.setPosition(7, 3, pieces.get(ChessPieceID.WHITE_QUEEN));
		board.setPosition(7, 4, pieces.get(ChessPieceID.WHITE_KING));
		board.setPosition(7, 5, pieces.get(ChessPieceID.WHITE_BISHOP));
		board.setPosition(7, 6, pieces.get(ChessPieceID.WHITE_KNIGHT));
		board.setPosition(7, 7, pieces.get(ChessPieceID.WHITE_ROOK));
		
		for(int i = 0; i < 8; i++) {
			board.setPosition(1, i, pieces.get(ChessPieceID.BLACK_PAWN));
			board.setPosition(6, i, pieces.get(ChessPieceID.WHITE_PAWN));
		}
		
		return board;
	}
	
	@Override
	public Pair<State, Piece> updateState(Board board, List<Piece> pieces, Piece lastPlayer) {
		
		//TODO Change Piece to boolean
		
		Pair<State, Piece> gameState;
		boolean noValidMovesAnyPiece = false;
		int playingPiecesCount = 0; //Counter for the active players (still have pieces on board)
		
		Piece currentPlayer = nextPlayer(board, pieces, lastPlayer);
		gameState = gameInPlayResult; //gameInPlayResult = IF GAME IS NOT FINISHED, STILL IN PLAY
		
		if(currentPlayer != null) //If at least one piece can move (any player)
		{
			for(int i = 0; i < pieces.size(); i++)
			{
				if(board.getPieceCount(pieces.get(i)) > 0)
				{
					playingPiecesCount++;
				}
			}
			
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
			gameState = checkWinnerFullBoard(board, pieces);
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
	public List<GameMove> validMoves(Board board, List<Piece> playersPieces, Piece turn) {
		List<GameMove> moves = new ArrayList<GameMove>(); //Every single possible move on the board is saved here
		for (int row = 0; row < board.getRows(); row++) {
			for (int col = 0; col < board.getCols(); col++) {
				//A tile is selected
				if(board.getPosition(row, col) != null){ //If it is not empty, it checks where it can move
					if(board.getPosition(row, col).getId() == turn.getId()){ //Checks if it has the correct ID
						for(int rowDes = Math.max(row - 2, 0); rowDes <= Math.min(row + 2, board.getRows() - 1); rowDes++){
							for(int colDes = Math.max(col - 2, 0); colDes <= Math.min(col + 2, board.getCols() - 1); colDes++){
								if (board.getPosition(rowDes, colDes) == null){ //If destination is empty...
									moves.add(new ChessMove(row, col, rowDes, colDes, turn)); //...it saves the move
								}
							}
						}
					}
				}
			}
		}
		return moves;
	}

	@Override
	public Piece nextPlayer(Board board, List<Piece> playersPieces, Piece lastPlayer){		
		boolean noMoves = false;	
		boolean noPieces = false;		
		int nextPlayerIndex = 1 + playersPieces.indexOf(lastPlayer); //Getting the current player index (even if it is greater than 4 it works later with %)
		Piece nextPlayer;
		boolean infiniteLoop = false;
		int loopCount = 0;
		
		do{
			//Get next player's piece
			nextPlayer = playersPieces.get((nextPlayerIndex) % playersPieces.size());
			if(board.getPieceCount(nextPlayer) > 0) //Check if the next player has got at least one piece
			{
				noPieces = false;
				if(validMoves(board, playersPieces, nextPlayer).isEmpty()) //Check if he can move any of his pieces
				{
					noMoves = true;
				}
				else
				{
					noMoves = false;
				}
			}
			else
			{
				noPieces = true;
			}
			nextPlayerIndex++;
			if(loopCount > playersPieces.size())
			{
				infiniteLoop = true;
				nextPlayer = null;
				//If no player can move, this can get stuck in some rare cases.
				//This "if" avoids the execution entering an infinite loop!
			}
			loopCount++;
		} while((noPieces || noMoves) && !infiniteLoop);		
		
		return nextPlayer;
	}

	@Override
	public Piece initialPlayer(Board board, List<Piece> pieces) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public double evaluate(Board board, List<Piece> pieces, Piece turn, Piece p) {
		// TODO Check ConnectNRules
		return 0;
	}

	@Override
	public int minPlayers() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public int maxPlayers() {
		// TODO Auto-generated method stub
		return 0;
	}
}