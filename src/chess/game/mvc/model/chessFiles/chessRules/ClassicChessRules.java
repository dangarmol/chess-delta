package chess.game.mvc.model.chessFiles.chessRules;

import java.util.ArrayList;
import java.util.List;

import chess.game.mvc.model.chessFiles.ChessBoard;
import chess.game.mvc.model.chessFiles.ChessMove;
import chess.game.mvc.model.chessFiles.ChessStatic;
import chess.game.mvc.model.chessPieces.ChessPiece;
import chess.game.mvc.model.chessPieces.ChessPieceID;
import chess.game.mvc.model.chessPieces.chessPiecesImp.Bishop;
import chess.game.mvc.model.chessPieces.chessPiecesImp.King;
import chess.game.mvc.model.chessPieces.chessPiecesImp.Knight;
import chess.game.mvc.model.chessPieces.chessPiecesImp.Pawn;
import chess.game.mvc.model.chessPieces.chessPiecesImp.Queen;
import chess.game.mvc.model.chessPieces.chessPiecesImp.Rook;
import chess.game.mvc.model.genericGameFiles.Board;
import chess.game.mvc.model.genericGameFiles.GameMove;
import chess.game.mvc.model.genericGameFiles.GameRules;
import chess.game.mvc.model.genericGameFiles.Pair;
import chess.game.mvc.model.genericGameFiles.Piece;
import chess.game.mvc.model.genericGameFiles.Game.State;
import chess.game.mvc.model.genericGameFiles.GameError;

public class ClassicChessRules implements GameRules {

    // This object is returned by gameOver to indicate that the game is not
    // over. Just to avoid creating it multiple times, etc.
    protected final Pair<State, Piece> gameInPlayResult = new Pair<State, Piece>(State.InPlay, null);

    public ClassicChessRules() {}

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

        setStartingAttributes(board);
        ((ChessBoard) board).addCurrentPosition();

        return board;
    }

    //In the case of a game restart, the attributes do not return to their original, therefore this function is needed.
    private void setStartingAttributes(Board b) {
        if(b instanceof ChessBoard) {
            ChessBoard board = (ChessBoard) b;
            for(int rowX = ChessStatic.MIN_DIM; rowX <= ChessStatic.MAX_DIM; rowX++) {
                for(int colY = ChessStatic.MIN_DIM; colY <= ChessStatic.MAX_DIM; colY++) {
                    if(board.getPosition(rowX, colY) != null) { //If there's a piece
                        if(board.getPosition(rowX, colY) instanceof Rook) {
                            ((Rook) board.getPosition(rowX, colY)).setCastle(true);
                        } else if(board.getPosition(rowX, colY) instanceof Pawn) {
                            ((Pawn) board.getPosition(rowX, colY)).setPassant(false);
                            ((Pawn) board.getPosition(rowX, colY)).setFirstMove(true);
                        } else if(board.getPosition(rowX, colY) instanceof King) {
                            ((King) board.getPosition(rowX, colY)).setCastle(true);
                        }
                    }
                }
            }
        }
    }

    @Override
    public Pair<State, Piece> updateState(Board board, List<Piece> pieces, Piece lastPlayer) {
        Pair<State, Piece> gameState = this.gameInPlayResult; //gameInPlayResult = IF GAME IS NOT FINISHED, STILL IN PLAY

        if(nextPlayer(board, pieces, lastPlayer) == null) { //If next player can't move, someone has won or there's a Stalemate!
            //There must be a Checkmate or Stalemate, therefore we check the end of the game.
            gameState = checkWinnerEndGame(board, pieces, lastPlayer);
        } else if(((ChessBoard) board).checkMovesRulesLimit()) { //Check if no pawn has been moved or no piece has been captured in the last 50 moves
            gameState = new Pair<State, Piece>(State.Draw, null);
        }

        return gameState;
    }

    /**
     * This function must only be called if the game has ended for a player not being able to move. Not in any other case!!
     * This could only be called after either Checkmate or Stalemate!!
     */
    private Pair<State, Piece> checkWinnerEndGame(Board board, List<Piece> pieces, Piece lastPlayer) {
        ChessPiece lastChessPlayer = (ChessPiece) lastPlayer;

        //Creates an empty chess move to check if the enemy king is in check
        if(((ChessBoard) board).isKingInCheck(!lastChessPlayer.getWhite())) { //Checks if the King from the player that hasn't made the last move is in Check. (It would be Checkmate)
            //If there is a Checkmate
            if(lastChessPlayer.getWhite()) {
                //If the last one to move was White
                return new Pair<State, Piece>(State.Won, pieces.get(ChessStatic.WHITE_ID)); //White wins!
            } else {
                //If the last one to move was Black
                return new Pair<State, Piece>(State.Won, pieces.get(ChessStatic.BLACK_ID)); //Black wins!
            }
        } else {
            //If there is a Stalemate
            return new Pair<State, Piece>(State.Draw, null); //There is a Draw
        }
    }

    @Override
    public List<GameMove> validMoves(Board board, List<Piece> playersPieces, Piece turn) {
        Board originalBoard = board;
        boolean isWhiteTurn = ((ChessPiece) turn).getWhite();
        Board testBoard = originalBoard.copy();
        List<GameMove> legalMoves = new ArrayList<GameMove>(); //Every single possible move on the board is saved here
        for (int rowX = ChessStatic.MIN_DIM; rowX <= ChessStatic.MAX_DIM; rowX++) { //Exploring rows and cols.
            for (int colY = ChessStatic.MIN_DIM; colY <= ChessStatic.MAX_DIM; colY++) {
                if(originalBoard.getPosition(rowX, colY) != null && //If the position is not empty...
                        ((ChessPiece) originalBoard.getPosition(rowX, colY)).getWhite() == isWhiteTurn){ //...and it is from the current turn.
                    if(originalBoard.getPosition(rowX, colY) instanceof Pawn) {
                        this.addPawnMoves(testBoard, originalBoard, legalMoves, rowX, colY, turn);
                    } else if(originalBoard.getPosition(rowX, colY) instanceof Rook) {
                        this.addHorizVertMoves(testBoard, originalBoard, legalMoves, rowX, colY, turn);
                    } else if(originalBoard.getPosition(rowX, colY) instanceof Knight) {
                        this.addKnightMoves(testBoard, originalBoard, legalMoves, rowX, colY, turn);
                    } else if(originalBoard.getPosition(rowX, colY) instanceof Bishop) {
                        this.addDiagonalMoves(testBoard, originalBoard, legalMoves, rowX, colY, turn);
                    } else if(originalBoard.getPosition(rowX, colY) instanceof Queen) {
                        this.addHorizVertMoves(testBoard, originalBoard, legalMoves, rowX, colY, turn);
                        this.addDiagonalMoves(testBoard, originalBoard, legalMoves, rowX, colY, turn);
                    } else if(originalBoard.getPosition(rowX, colY) instanceof King) {
                        this.addKingMoves(testBoard, originalBoard, legalMoves, rowX, colY, turn);
                    } else {
                        throw new GameError("Internal error, piece type not found!");
                    }
                }
            }
        }
        return legalMoves;
    }

    private void checkAndAdd(ChessMove testMove, Board testBoard, Board originalBoard, List<GameMove> legalMoves) {
        if(testMove.isMoveLegal((ChessBoard) testBoard)) {
            testMove.revertBoard(testBoard, originalBoard);
            legalMoves.add(testMove);
        }
    }

    private void addHorizVertMoves(Board testBoard, Board originalBoard, List<GameMove> legalMoves, int rowX, int colY, Piece turn) {
        int rowDes = rowX - 1, colDes = colY;
        while(rowDes >= ChessStatic.MIN_DIM) { //North direction
            this.checkAndAdd(new ChessMove(rowX, colY, rowDes, colDes, turn), testBoard, originalBoard, legalMoves);
            if(originalBoard.getPosition(rowDes, colDes) != null) break;
            rowDes--;
        }
        rowDes = rowX + 1; colDes = colY;
        while(rowDes <= ChessStatic.MAX_DIM) { //South direction
            this.checkAndAdd(new ChessMove(rowX, colY, rowDes, colDes, turn), testBoard, originalBoard, legalMoves);
            if(originalBoard.getPosition(rowDes, colDes) != null) break;
            rowDes++;
        }
        rowDes = rowX; colDes = colY + 1;
        while(colDes <= ChessStatic.MAX_DIM) { //East direction
            this.checkAndAdd(new ChessMove(rowX, colY, rowDes, colDes, turn), testBoard, originalBoard, legalMoves);
            if(originalBoard.getPosition(rowDes, colDes) != null) break;
            colDes++;
        }
        rowDes = rowX; colDes = colY - 1;
        while(colDes >= ChessStatic.MIN_DIM) { //West direction
            this.checkAndAdd(new ChessMove(rowX, colY, rowDes, colDes, turn), testBoard, originalBoard, legalMoves);
            if(originalBoard.getPosition(rowDes, colDes) != null) break;
            colDes--;
        }
    }

    private void addDiagonalMoves(Board testBoard, Board originalBoard, List<GameMove> legalMoves, int rowX, int colY, Piece turn) {
        int rowDes = rowX - 1, colDes = colY - 1;
        while(rowDes >= ChessStatic.MIN_DIM && colDes >= ChessStatic.MIN_DIM) { //NorthWest direction
            this.checkAndAdd(new ChessMove(rowX, colY, rowDes, colDes, turn), testBoard, originalBoard, legalMoves);
            if(originalBoard.getPosition(rowDes, colDes) != null) break;
            rowDes--;
            colDes--;
        }

        rowDes = rowX - 1; colDes = colY + 1;
        while(rowDes >= ChessStatic.MIN_DIM && colDes <= ChessStatic.MAX_DIM) { //NorthEast direction
            this.checkAndAdd(new ChessMove(rowX, colY, rowDes, colDes, turn), testBoard, originalBoard, legalMoves);
            if(originalBoard.getPosition(rowDes, colDes) != null) break;
            rowDes--;
            colDes++;
        }

        rowDes = rowX + 1; colDes = colY + 1;
        while(colDes <= ChessStatic.MAX_DIM && rowDes <= ChessStatic.MAX_DIM) { //SouthEast direction
            this.checkAndAdd(new ChessMove(rowX, colY, rowDes, colDes, turn), testBoard, originalBoard, legalMoves);
            if(originalBoard.getPosition(rowDes, colDes) != null) break;
            colDes++;
            rowDes++;
        }

        rowDes = rowX + 1; colDes = colY - 1;
        while(colDes >= ChessStatic.MIN_DIM && rowDes <= ChessStatic.MAX_DIM) { //SouthWest direction
            this.checkAndAdd(new ChessMove(rowX, colY, rowDes, colDes, turn), testBoard, originalBoard, legalMoves);
            if(originalBoard.getPosition(rowDes, colDes) != null) break;
            colDes--;
            rowDes++;
        }
    }

    private void addKnightMoves(Board testBoard, Board originalBoard, List<GameMove> legalMoves, int rowX, int colY, Piece turn) {
        this.checkAndAdd(new ChessMove(rowX, colY, rowX - 2, colY - 1, turn), testBoard, originalBoard, legalMoves); //2 up 1 left
        this.checkAndAdd(new ChessMove(rowX, colY, rowX - 2, colY + 1, turn), testBoard, originalBoard, legalMoves); //2 up 1 right
        this.checkAndAdd(new ChessMove(rowX, colY, rowX + 2, colY - 1, turn), testBoard, originalBoard, legalMoves); //2 down 1 left
        this.checkAndAdd(new ChessMove(rowX, colY, rowX + 2, colY + 1, turn), testBoard, originalBoard, legalMoves); //2 down 1 right
        this.checkAndAdd(new ChessMove(rowX, colY, rowX - 1, colY - 2, turn), testBoard, originalBoard, legalMoves); //2 left 1 up
        this.checkAndAdd(new ChessMove(rowX, colY, rowX + 1, colY - 2, turn), testBoard, originalBoard, legalMoves); //2 left 1 down
        this.checkAndAdd(new ChessMove(rowX, colY, rowX - 1, colY + 2, turn), testBoard, originalBoard, legalMoves); //2 right 1 up
        this.checkAndAdd(new ChessMove(rowX, colY, rowX + 1, colY + 2, turn), testBoard, originalBoard, legalMoves); //2 right 1 down
    }

    private void addPawnMoves(Board testBoard, Board originalBoard, List<GameMove> legalMoves, int rowX, int colY, Piece turn) {
        if(((ChessPiece) originalBoard.getPosition(rowX, colY)).getWhite()) { //If it's a white Pawn it will explore some moves...
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

    private void addKingMoves(Board testBoard, Board originalBoard, List<GameMove> legalMoves, int rowX, int colY, Piece turn) {
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

        if(lastPlayer == playersPieces.get(ChessStatic.WHITE_ID))
            nextPlayer = playersPieces.get(ChessStatic.BLACK_ID);
        else if (lastPlayer == playersPieces.get(ChessStatic.BLACK_ID))
            nextPlayer = playersPieces.get(ChessStatic.WHITE_ID);
        else
            return null; //throw new GameError("Something went wrong while changing turns. Unrecognised player. This should be unreachable.");

        if(validMoves(board, playersPieces, nextPlayer).isEmpty()) //Check if he can move any of his pieces
            return null; //Returns null if next player can't move!

        return nextPlayer;
    }

    @Override
    public Piece initialPlayer(Board board, List<Piece> pieces) {
        return pieces.get(ChessStatic.WHITE_ID); //White always starts
    }

    @Override
    public double evaluate(Board board, List<Piece> pieces, Piece turn, Piece p) {
        //This function should never be called
		/*ChessBoardEvaluator evaluator = new ChessBoardEvaluator((ChessBoard) board, ((ChessPiece) turn).getWhite());
		return evaluator.getRating();*/
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