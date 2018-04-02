package chess.game;

import java.util.ArrayList;
import java.util.List;
import chess.game.mvc.controller.Controller;
import chess.game.mvc.controller.GameFactory;
import chess.game.mvc.model.chessFiles.ChessFactoryExtended;
import chess.game.mvc.model.chessFiles.chessAI.ChessMinMax;
import chess.game.mvc.model.genericGameFiles.AIAlgorithm;
import chess.game.mvc.model.genericGameFiles.Game;
import chess.game.mvc.model.genericGameFiles.Piece;

public class Main {
    
    /**
     * Player modes (manual, random, etc.)
     * <p>
     * Modos de juego.
     */
    enum PlayerMode {
        MANUAL("m", "Manual"), RANDOM("r", "Dummy AI"), AI("a", "Intelligent AI");
        
        private String id;
        private String desc;
        
        PlayerMode(String id, String desc) {
            this.id = id;
            this.desc = desc;
        }
        
        public String getId() {
            return id;
        }
        
        public String getDesc() {
            return desc;
        }
        
        @Override
        public String toString() {
            return id;
        }
    }
    
    /**
     * Default player mode to use.
     * <p>
     * Modo de juego por defecto.
     */
    final private static PlayerMode DEFAULT_PLAYERMODE = PlayerMode.MANUAL;
    
    /**
     * This field includes a game factory that is constructed after parsing the
     * command-line arguments. Depending on the game selected with the -g option
     * (by default {@link #DEFAULT_GAME}).
     *
     * <p>
     * Este atributo incluye una factoria de juego que se crea despues de
     * extraer los argumentos de la linea de ordenes. Depende del juego
     * seleccionado con la opcion -g (por defecto, {@link #DEFAULT_GAME}).
     */
    private static GameFactory gameFactory;
    
    /**
     * List of pieces provided with the -p option, or taken from
     * {@link GameFactory#createDefaultPieces()} if this option was not
     * provided.
     *
     * <p>
     * Lista de fichas proporcionadas con la opcion -p, u obtenidas de
     * {@link GameFactory#createDefaultPieces()} si no hay opcion -p.
     */
    private static List<Piece> pieces;
    
    /**
     * List of piece types, which is different to the list of "pieces".
     * "Piece" is only used for the turn and number of players (each one
     * represents a player), not for the actual type of the piece
     * unless every piece is treated equally in the game.
     */
    private static List<Piece> pieceTypes;
    
    /**
     * A list of players. The i-th player corresponds to the i-th piece in the
     * list {@link #pieces}. They correspond to what is provided in the -p
     * option (or using the default value {@link #DEFAULT_PLAYERMODE}).
     *
     * <p>
     * Lista de jugadores. El jugador i-esimo corresponde con la ficha i-esima
     * de la lista {@link #pieces}. Esta lista contiene lo que se proporciona en
     * la opcion -p (o el valor por defecto {@link #DEFAULT_PLAYERMODE}).
     */
    private static List<PlayerMode> playerModes;
    
    /**
     * The view to use. Depending on the selected view using the -v option or
     * the default value {@link #DEFAULT_VIEW} if this option was not provided.
     *
     * <p>
     * Vista a utilizar. Dependiendo de la vista seleccionada con la opcion -v o
     * el valor por defecto {@link #DEFAULT_VIEW} si el argumento -v no se
     * proporciona.
     */
    //private static ViewInfo view;
    
    /**
     * {@code true} if the option -m was provided, to use a separate view for
     * each piece, and {@code false} otherwise.
     *
     * <p>
     * {@code true} si se incluye la opcion -m, para utilizar una vista separada
     * por cada ficha, o {@code false} en caso contrario.
     */
    private static boolean multiviews;
    
    /**
     * The algorithm to be used by the automatic player.
     *
     * <p>
     * Algoritmo a utilizar por el jugador automatico.
     */
    private static AIAlgorithm aiPlayerAlg;
    
    /**
     * Starts a game. Should be called after {@link #parseArgs(String[])} so
     * some fields are set to their appropriate values.
     * 
     * <p>
     * Inicia un juego. Debe llamarse despues de {@link #parseArgs(String[])}
     * para que los atributos tengan los valores correctos.
     * 
     */
    public static void startGame() {
	    	gameFactory = new ChessFactoryExtended();
        Game g = new Game(gameFactory.gameRules());
        Controller c = null;
        pieces = gameFactory.createDefaultPieces();
        pieceTypes = gameFactory.createPieceTypes();
        aiPlayerAlg = new ChessMinMax();
        
        playerModes = new ArrayList<PlayerMode>();
        for (int i = 0; i < pieces.size(); i++) {
            playerModes.add(DEFAULT_PLAYERMODE);
        }

        c = new Controller(g, pieces, pieceTypes);
        
        if(!isMultiviews()) {
            gameFactory.createSwingView(g, c, null, gameFactory.createRandomPlayer(), gameFactory.createAIPlayer(aiPlayerAlg));
        } else {
            for (Piece p : pieces) {
                gameFactory.createSwingView(g, c, p, gameFactory.createRandomPlayer(), gameFactory.createAIPlayer(aiPlayerAlg));
            }
        }
	        
	    	c.start();
    }
    
    public static boolean isMultiviews() {
        return multiviews;
    }
    
    public static void setMultiviews(boolean isMultiviews) {
        multiviews = isMultiviews;
    }
    
    /**
     * The main method. It calls {@link #parseArgs(String[])} and then
     * {@link #startGame()}.
     * 
     * <p>
     * Metodo main. Llama a {@link #parseArgs(String[])} y a continuacion inicia
     * un juego con {@link #startGame()}.
     * 
     * @param args
     *            Command-line arguments.
     * 
     */
    public static void main(String[] args) {
        startGame(); //TODO Create Menu before this
    }
}
