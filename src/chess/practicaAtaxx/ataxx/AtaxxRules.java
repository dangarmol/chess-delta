package chess.practicaAtaxx.ataxx;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import chess.basecode.bgame.model.Board;
import chess.basecode.bgame.model.FiniteRectBoard;
import chess.basecode.bgame.model.GameError;
import chess.basecode.bgame.model.GameMove;
import chess.basecode.bgame.model.Pair;
import chess.basecode.bgame.model.Piece;
import chess.basecode.bgame.model.Game.State;
import chess.basecode.connectn.ConnectNRules;

public class AtaxxRules extends ConnectNRules {

	private int dim;
	private int obs;
	private static int stuckCounter = 0; //Used in an extreme case (game could enter an infinite loop otherwise)
	//This part can be removed, it is just a remote case check.
	
	public AtaxxRules(int dim, int numObs) {
		super(dim); 
		
		if (dim % 2 == 0 || dim < 5)
		{
			throw new GameError("Dimension must be odd and greater than five"); //Check dimensions before proceeding
		} else {
			this.dim = dim;
			this.obs = numObs;
		}
	}

	@Override
	public String gameDesc() {
		return "Ataxx " + dim + "x" + dim;
	}
	
	@Override
	public Board createBoard(List<Piece> pieces) {
		Board board = new FiniteRectBoard(dim, dim);		
		int tamano = pieces.size();
		
		if(tamano >= 2){  
		// Pieces are allocated on opposing corners alternatively.
			board.setPosition(0, 0, pieces.get(0));
			board.setPosition(this.dim - 1, this.dim - 1, pieces.get(0));
			board.setPosition(this.dim - 1, 0, pieces.get(1));
			board.setPosition(0, this.dim - 1, pieces.get(1));	
		}
		
		// Player 3 pieces are added on the half of the board side horizontally.
		if(tamano >= 3){		
			board.setPosition(0, (this.dim / 2), pieces.get(2));
			board.setPosition(this.dim - 1, (this.dim / 2), pieces.get(2));			
		}
		// Player 4 pieces are added on the half of the board side vertically.
		if(tamano >= 4){
			board.setPosition(this.dim / 2, 0, pieces.get(3));
			board.setPosition(this.dim / 2, this.dim - 1, pieces.get(3));
		}	
		
		for (int i = 0; i < pieces.size(); i++)
		{
			board.setPieceCount(pieces.get(i), 2);
		}
		
		if(this.obs > 0)
		{
			createWalls(board, pieces);
		}
		
		return board;
	}
	
	private void createWalls(Board board, List<Piece> pieces)
	{
		int wallRow, wallCol; //Wall auxiliar location
		int wallCount = 0; //Wall counter
		Piece wallPiece = new Piece("*"); //Appearance for the walls
		Random randWall = new Random(); //Random generator for wall coordinates
		
		while(this.obs < (dim * dim - pieces.size()) && wallCount < this.obs)
		{
			//Generating coordinates...
			wallRow = randWall.nextInt(this.dim);
			wallCol = randWall.nextInt(this.dim);
			if(board.getPosition(wallRow, wallCol) == null) //Check and proceed if the position is empty
			{
				board.setPosition(wallRow, wallCol, wallPiece);
				wallCount++;
			}
		}
	}
	
	@Override
	public Pair<State, Piece> updateState(Board board, List<Piece> pieces, Piece lastPlayer) {
		
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
				stuckCounter++; //This is only used in an extreme case where obstacles block most pieces except
				//for one that gets stuck moving alternatively between 2 positions, it is a rare case, but it
				//happened while testing the game (it is not really probable and can be removed in most cases)
				//IT WILL END THE GAME AFTER 10 REPEATS
			}
			else if(lastPlayer != currentPlayer)
			{
				stuckCounter = 0;
			}
		}
		else
		{
			noValidMovesAnyPiece = true; //Noone can move (an extreme case where noone can move but board is not full yet)
		}
		
		if(board.isFull() || noValidMovesAnyPiece || stuckCounter >= 10)
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
									moves.add(new AtaxxMove(row, col, rowDes, colDes, turn)); //...it saves the move
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
}