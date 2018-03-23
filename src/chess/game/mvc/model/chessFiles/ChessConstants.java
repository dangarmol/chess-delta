package chess.game.mvc.model.chessFiles;

//TODO Possibly change name to ChessStatic
public class ChessConstants {
	//CHESSBOARD
	public static final int MIN_DIM = 0;
	public static final int MAX_DIM = 7;
	public static final int WHITE_ID = 0;
	public static final int BLACK_ID = 1;
	public static final int UNKNOWN = -1;
	public static final int POSITIVE = 1;
	public static final int NEGATIVE = -1;
	public static final int NEUTRAL = 0;
	public static final int NW = 0;
	public static final int N = 1;
	public static final int NE = 2;
	public static final int E = 3;
	public static final int SE = 4;
	public static final int S = 5;
	public static final int SW = 6;
	public static final int W = 7;
	public static final int ITER_LIMIT = 10;
	public static final boolean WHITE = true;
	public static final boolean BLACK = false;
	public static final int STARTING_MINMAX_DEPTH = 0;
	public static final int DEFAULT_MINMAX_LEVEL = 4;
	public static final int BOARD_DIMS = 8;
	
	public static int movesWithoutAction = 0; //TODO Should be an attribute at the ChessBoard class for 50 move rule, as well as the 3 move rule.
	
	//VIEW
	public static int rowClicked = -1;
	public static int colClicked = -1;
	public static boolean firstClick = true;
}