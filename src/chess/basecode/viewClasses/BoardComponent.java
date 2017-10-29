package chess.basecode.viewClasses;

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

import chess.basecode.bgame.model.Board;
import chess.basecode.bgame.model.Piece;

@SuppressWarnings("serial")
public abstract class BoardComponent extends JComponent { //Draws the board and pieces

	private int _CELL_HEIGHT = 50;
	private int _CELL_WIDTH = 50;

	private int rows;
	private int cols;
	private Board board;

	/**
	 * Default constructor
	 */
	public BoardComponent() {
		createGUI();
	}
	
	/**
	 * Constructor with parameters
	 * @param rows
	 * @param cols
	 */
	public BoardComponent(int rows, int cols) {
		this.rows = rows;
		this.cols = cols;
		createGUI();
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
				//System.out.println("Mouse Button "+e.getButton()+" Clicked at " + "(" + e.getX() + ","
				//		+ e.getY() + ")");
				int col = (e.getX() / _CELL_WIDTH); 
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
				BoardComponent.this.mouseClicked(row, col, mouseButton);
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
		g.setColor(Color.black);
		g.fillRect(0, 0, this.getWidth() - this.cols, this.getHeight() - this.rows);
		_CELL_WIDTH = this.getWidth() / board.getCols();
		_CELL_HEIGHT = this.getHeight() / board.getRows();

		for (int i = 0; i < board.getCols(); i++)
			for (int j = 0; j < board.getRows(); j++)
				drawCell(i, j, g);
	}

	/**
	 * Draws one cell and the pieces into them
	 * @param row
	 * @param col
	 * @param g
	 */
	private void drawCell(int row, int col, Graphics g) {
		//TODO Implement "instanceof" piece to paint.
		int x = col * _CELL_WIDTH;
		int y = row * _CELL_HEIGHT;
		
		//Selects the colour for each tile depending on position
		if(row % 2 == 0 && col % 2 == 1 || row % 2 == 1 && col % 2 == 0) {
			g.setColor(new Color(204, 102, 0)); //Dark tiles
		} else {
			g.setColor(new Color(255, 221, 153)); //Light tiles
		}
		
		//g.setColor(new Color(0,161,32)); //Background color
		//g.fillRect(x + 2, y + 2, _CELL_WIDTH - 4, _CELL_HEIGHT - 4);
		g.fillRect(x, y, _CELL_WIDTH, _CELL_HEIGHT);

		//EXPERIMENTAL PART
		
		try {
	         BufferedImage img = ImageIO.read(new File("/Users/Daniel/Downloads/DarkKing.png"));
	         g.drawImage(img, x, y, _CELL_WIDTH, _CELL_HEIGHT, this); //Draws a piece
	      } catch (IOException e) {
	         e.printStackTrace();
	      }
		
		//EXPERIMENTAL PART END
		
		if(board.getPosition(row, col) != null) {
			if(this.getPieceColor(getPiece(row, col, board)) != null) { //If tile isn't empty
				g.setColor(getPieceColor(getPiece(row, col, board))); //Fills the oval with the piece color, the color is obtained from the hashmap
				g.fillOval(x + 4, y + 4, _CELL_WIDTH - 8, _CELL_HEIGHT - 8);
				
				if(getPieceColor(getPiece(row, col, board)) == Color.black) {
					g.setColor(Color.white);
					g.drawOval(x + 8, y + 8, _CELL_WIDTH - 16, _CELL_HEIGHT - 16);
					
					g.setColor(Color.white); //Oval or shape color
					g.drawOval(x + 4, y + 4, _CELL_WIDTH - 8, _CELL_HEIGHT - 8);
				} else {
					g.setColor(Color.black);
					g.drawOval(x + 8, y + 8, _CELL_WIDTH - 16, _CELL_HEIGHT - 16);
					
					g.setColor(Color.black); //Oval or shape color
					g.drawOval(x + 4, y + 4, _CELL_WIDTH - 8, _CELL_HEIGHT - 8);
				}
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
