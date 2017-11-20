package chess.game.mvc.view.chessViews;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;
import javax.swing.ImageIcon;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.SwingUtilities;

import chess.game.mvc.model.chessPieces.ChessPiece;
import chess.game.mvc.model.chessPieces.chessPiecesImp.Bishop;
import chess.game.mvc.model.chessPieces.chessPiecesImp.King;
import chess.game.mvc.model.chessPieces.chessPiecesImp.Knight;
import chess.game.mvc.model.chessPieces.chessPiecesImp.Pawn;
import chess.game.mvc.model.chessPieces.chessPiecesImp.Queen;
import chess.game.mvc.model.chessPieces.chessPiecesImp.Rook;
import chess.game.mvc.model.genericGameFiles.Board;
import chess.game.mvc.model.genericGameFiles.GameError;
import chess.game.mvc.model.genericGameFiles.Piece;

@SuppressWarnings("serial")
public abstract class ChessBoardComponent extends JComponent { //Draws the board and pieces

	private int _CELL_HEIGHT = 50;
	private int _CELL_WIDTH = 50;

	//Java properties file where you can store the current path
	//Also convert the whole project into a runnable JAR file
	
	//Path must end in either "/" on Linux/MacOS or "\" on Windows
	//public static final String piecesPath = "/Users/Daniel/Google Drive/University and School/2017-2018 Hertfordshire/TFG/Chess Repository Workspace/Chess TFG/img/";
	public static final String piecesPath = "img/";
	
	private BufferedImage whitePawn;
	private BufferedImage whiteRook;
	private BufferedImage whiteKnight;
	private BufferedImage whiteBishop;
	private BufferedImage whiteQueen;
	private BufferedImage whiteKing;
	private BufferedImage blackPawn;
	private BufferedImage blackRook;
	private BufferedImage blackKnight;
	private BufferedImage blackBishop;
	private BufferedImage blackQueen;
	private BufferedImage blackKing;
	
	private int rows;
	private int cols;
	private Board board;

	/**
	 * Default constructor
	 */
	public ChessBoardComponent() {
		loadImages();
		createGUI();
	}
	
	/**
	 * Constructor with parameters
	 * @param rows
	 * @param cols
	 */
	public ChessBoardComponent(int rows, int cols) {
		this.rows = rows;
		this.cols = cols;
		loadImages();
		createGUI();
	}
	
	private void loadImages() {
		try {
			this.whitePawn = ImageIO.read(new File(piecesPath + "WhitePawn.png"));
			this.whiteRook = ImageIO.read(new File(piecesPath + "WhiteRook.png"));
			this.whiteKnight = ImageIO.read(new File(piecesPath + "WhiteKnight.png"));
			this.whiteBishop = ImageIO.read(new File(piecesPath + "WhiteBishop.png"));
			this.whiteQueen = ImageIO.read(new File(piecesPath + "WhiteQueen.png"));
			this.whiteKing = ImageIO.read(new File(piecesPath + "WhiteKing.png"));
			
			this.blackPawn = ImageIO.read(new File(piecesPath + "BlackPawn.png"));
			this.blackRook = ImageIO.read(new File(piecesPath + "BlackRook.png"));
			this.blackKnight = ImageIO.read(new File(piecesPath + "BlackKnight.png"));
			this.blackBishop = ImageIO.read(new File(piecesPath + "BlackBishop.png"));
			this.blackQueen = ImageIO.read(new File(piecesPath + "BlackQueen.png"));
			this.blackKing = ImageIO.read(new File(piecesPath + "BlackKing.png"));
		} catch (IOException e) {
			e.printStackTrace();
			//"Chess piece files not found!! Check the path and try again!" TODO
		}
	}

	/**
	 * Function that creates the graphic interface
	 */
	private void createGUI() {

		addMouseListener(new MouseListener() {

			@Override
			public void mouseReleased(MouseEvent e) {
				System.out.println("Mouse Released: " + "(" + e.getX() + ","
						+ e.getY() + ")");
			}

			@Override
			public void mousePressed(MouseEvent e) {
				System.out.println("Mouse Pressed: " + "(" + e.getX() + ","
						+ e.getY() + ")");
			}

			@Override
			public void mouseExited(MouseEvent e) {
				System.out.println("Mouse Exited Component: " + "(" + e.getX()
						+ "," + e.getY() + ")");
			}

			@Override
			public void mouseEntered(MouseEvent e) {
				System.out.println("Mouse Entered Component: " + "(" + e.getX()
						+ "," + e.getY() + ")");
			}
			
			/**
			 * Gets the cell where the mouse was clicked
			 */
			@Override
			public void mouseClicked(MouseEvent e) {
				//TODO Check offset on mouse position click!
				
				//System.out.println("Mouse Button "+e.getButton()+" Clicked at " + "(" + e.getX() + ","
				//		+ e.getY() + ")");
				int col = (e.getX() / _CELL_WIDTH); //TODO This should be changed to fix the mouse offset on cells.
				int row = (e.getY() / _CELL_HEIGHT); 
				int mouseButton = 0; 
				if (SwingUtilities.isLeftMouseButton(e)) {
					mouseButton = 1; 
				} else if (SwingUtilities.isMiddleMouseButton(e)) {
					mouseButton = 2; 
				} else if (SwingUtilities.isRightMouseButton(e)) {
					mouseButton = 3;
				} else {
					return;
				}
				ChessBoardComponent.this.mouseClicked(row, col, mouseButton);
			}
		});
		
		this.setSize(new Dimension(rows * _CELL_HEIGHT, cols * _CELL_WIDTH));
		
		repaint();
	}
	
	/**
	 * Paints the background
	 */
	public void paintComponent(Graphics g) {
		super.paintComponent(g);

		if(board == null) {
			g.drawString("Game has not started yet, waiting for more clients to connect...", 300, 300);
			return;
		}
		g.setColor(Color.lightGray);
		g.fillRect(0, 0, this.getWidth(), this.getHeight());
		
		//Make cells slightly smaller to be able to fit column and row numbers and letters.
		_CELL_WIDTH = this.getWidth() / board.getCols() - 2;
		_CELL_HEIGHT = this.getHeight() / board.getRows() - 2;
		
		for (int i = 0; i < board.getCols(); i++)
			for (int j = 0; j < board.getRows(); j++)
				drawCell(i, j, g);
		
		g.setColor(Color.black);
		
		//Letters are 2 px wide.
		//y = 12 is for the letter not to be too close to the top of the screen.
		//16 is for the offset on drawCell(...), where the numbers will be.
		g.drawString("a", 16 + 0 * _CELL_WIDTH + (_CELL_WIDTH / 2), 12);
		g.drawString("b", 16 + 1 * _CELL_WIDTH + (_CELL_WIDTH / 2), 12);
		g.drawString("c", 16 + 2 * _CELL_WIDTH + (_CELL_WIDTH / 2), 12);
		g.drawString("d", 16 + 3 * _CELL_WIDTH + (_CELL_WIDTH / 2), 12);
		g.drawString("e", 16 + 4 * _CELL_WIDTH + (_CELL_WIDTH / 2), 12);
		g.drawString("f", 16 + 5 * _CELL_WIDTH + (_CELL_WIDTH / 2), 12);
		g.drawString("g", 16 + 6 * _CELL_WIDTH + (_CELL_WIDTH / 2), 12);
		g.drawString("h", 16 + 7 * _CELL_WIDTH + (_CELL_WIDTH / 2), 12);
		
		for (int row = 1; row <= 8; row++) {
			g.drawString(String.valueOf(9 - row), 6, 21 + (row - 1) * _CELL_HEIGHT + (_CELL_HEIGHT / 2));
		}
	}

	/**
	 * Draws one cell and the pieces into them
	 * @param row
	 * @param col
	 * @param g
	 */
	private void drawCell(int row, int col, Graphics g) {
		int x = col * _CELL_WIDTH + 19;
		int y = row * _CELL_HEIGHT + 16;
		//IMPORTANT: 19 and 16 express the offset from the left side and top side respectively
		//x and y are coordinates in the space, not rows/cols!!
		
		//Selects the colour for each tile depending on position
		if(row % 2 == 0 && col % 2 == 1 || row % 2 == 1 && col % 2 == 0) {
			g.setColor(new Color(204, 102, 0)); //Dark tiles
		} else {
			g.setColor(new Color(255, 217, 179)); //Light tiles
		}

		g.fillRect(x, y, _CELL_WIDTH, _CELL_HEIGHT);

		//Checks if there's a piece on the selected position. If there is, draws it depending on the type and colour.
		if(board.getPosition(row, col) != null) {
			if(this.getPiece(row, col, board) instanceof Pawn) {
				if (((ChessPiece) this.getPiece(row, col, board)).getWhite()) {
					g.drawImage(this.whitePawn, x, y, _CELL_WIDTH, _CELL_HEIGHT, this);
				} else {
					g.drawImage(this.blackPawn, x, y, _CELL_WIDTH, _CELL_HEIGHT, this);
				}
			} else if (this.getPiece(row, col, board) instanceof Rook) {
				if (((ChessPiece) this.getPiece(row, col, board)).getWhite()) {
					g.drawImage(this.whiteRook, x, y, _CELL_WIDTH, _CELL_HEIGHT, this);
				} else {
					g.drawImage(this.blackRook, x, y, _CELL_WIDTH, _CELL_HEIGHT, this);
				}
			} else if (this.getPiece(row, col, board) instanceof Knight) {
				if (((ChessPiece) this.getPiece(row, col, board)).getWhite()) {
					g.drawImage(this.whiteKnight, x, y, _CELL_WIDTH, _CELL_HEIGHT, this);
				} else {
					g.drawImage(this.blackKnight, x, y, _CELL_WIDTH, _CELL_HEIGHT, this);
				}
			} else if (this.getPiece(row, col, board) instanceof Bishop) {
				if (((ChessPiece) this.getPiece(row, col, board)).getWhite()) {
					g.drawImage(this.whiteBishop, x, y, _CELL_WIDTH, _CELL_HEIGHT, this);
				} else {
					g.drawImage(this.blackBishop, x, y, _CELL_WIDTH, _CELL_HEIGHT, this);
				}
			} else if (this.getPiece(row, col, board) instanceof Queen) {
				if (((ChessPiece) this.getPiece(row, col, board)).getWhite()) {
					g.drawImage(this.whiteQueen, x, y, _CELL_WIDTH, _CELL_HEIGHT, this);
				} else {
					g.drawImage(this.blackQueen, x, y, _CELL_WIDTH, _CELL_HEIGHT, this);
				}
			} else if (this.getPiece(row, col, board) instanceof King) {
				if (((ChessPiece) this.getPiece(row, col, board)).getWhite()) {
					g.drawImage(this.whiteKing, x, y, _CELL_WIDTH, _CELL_HEIGHT, this);
				} else {
					g.drawImage(this.blackKing, x, y, _CELL_WIDTH, _CELL_HEIGHT, this);
				}
			} else {
				throw new GameError("Piece type not recognised!");
			}
		}
	}
	
	/**
	 * Returns the piece on a position
	 * @param row
	 * @param col
	 * @param board
	 * @return
	 */
	private Piece getPiece(int row, int col, Board board) {
		return board.getPosition(row, col);
	}

	/**
	 * Redraws the board
	 * @param b
	 */
	public void redraw(Board b) {
		this.board = b;
		repaint();
	}

	protected abstract void mouseClicked(int row, int col, int mouseButton);

	protected abstract boolean isPlayerPiece(Piece p);

	protected abstract Color getPieceColor(Piece p);
}
