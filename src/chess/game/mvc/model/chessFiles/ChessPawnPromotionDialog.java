package chess.game.mvc.model.chessFiles;

import java.awt.FlowLayout;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;
import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JLabel;
import chess.game.mvc.model.chessPieces.ChessPieceID;
import chess.game.mvc.view.chessViews.ChessBoardComponent;
import javax.swing.JDialog;

public class ChessPawnPromotionDialog extends JDialog {

	private static final long serialVersionUID = 1L;

	Integer chosenPiece;
	
	private BufferedImage rookImg;
	private BufferedImage knightImg;
	private BufferedImage bishopImg;
	private BufferedImage queenImg;
	
	public ChessPawnPromotionDialog() {}
	
	//Based on ColorChooser class.
	public ChessPawnPromotionDialog(String title, boolean isWhite) {
		super();
		chosenPiece = -1; //Represents that it hasn't been chosen yet.
		loadPieces(isWhite);
                this.setModal(true);
		this.setTitle(title);
		this.getContentPane().setLayout(new FlowLayout());
		
		JLabel rookLabel = new JLabel(new ImageIcon(rookImg));
		rookLabel.addMouseListener(new MouseAdapter() {
		    @Override
		    public void mouseClicked(MouseEvent e) {
		    		if(isWhite)
		    			chosenPiece = ChessPieceID.WHITE_ROOK_PAWN;
		    		else
		    			chosenPiece = ChessPieceID.BLACK_ROOK_PAWN;
		    		closeDialog();            
		    }
		});
		
		JLabel knightLabel = new JLabel(new ImageIcon(knightImg));
		knightLabel.addMouseListener(new MouseAdapter() {
		    @Override
		    public void mouseClicked(MouseEvent e) {
		    		if(isWhite)
		    			chosenPiece = ChessPieceID.WHITE_KNIGHT;
		    		else
		    			chosenPiece = ChessPieceID.BLACK_KNIGHT;
		    		closeDialog();            
		    }
		});
		
		JLabel bishopLabel = new JLabel(new ImageIcon(bishopImg));
		bishopLabel.addMouseListener(new MouseAdapter() {
		    @Override
		    public void mouseClicked(MouseEvent e) {
		    		if(isWhite)
		    			chosenPiece = ChessPieceID.WHITE_BISHOP;
		    		else
		    			chosenPiece = ChessPieceID.BLACK_BISHOP;
		    		closeDialog();            
		    }
		});
		
		JLabel queenLabel = new JLabel(new ImageIcon(queenImg));
		queenLabel.addMouseListener(new MouseAdapter() {
		    @Override
		    public void mouseClicked(MouseEvent e) {
		    		if(isWhite)
		    			chosenPiece = ChessPieceID.WHITE_QUEEN;
		    		else
		    			chosenPiece = ChessPieceID.BLACK_QUEEN;
		    		closeDialog();            
		    }
		});
		
		this.getContentPane().add(rookLabel);
		this.getContentPane().add(knightLabel);
		this.getContentPane().add(bishopLabel);
		this.getContentPane().add(queenLabel);
		
		this.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
		this.pack();
		
		//For the window to start at the center of the screen.
		this.setLocationRelativeTo(null);
		
		this.setVisible(true);
	}

	private void loadPieces(boolean isWhite) {
		try {
			if(isWhite) {
				this.rookImg = ImageIO.read(new File(ChessBoardComponent.piecesPath + "WhiteRook.png"));
				this.knightImg = ImageIO.read(new File(ChessBoardComponent.piecesPath + "WhiteKnight.png"));
				this.bishopImg = ImageIO.read(new File(ChessBoardComponent.piecesPath + "WhiteBishop.png"));
				this.queenImg = ImageIO.read(new File(ChessBoardComponent.piecesPath + "WhiteQueen.png"));
			} else {
				this.rookImg = ImageIO.read(new File(ChessBoardComponent.piecesPath + "BlackRook.png"));
				this.knightImg = ImageIO.read(new File(ChessBoardComponent.piecesPath + "BlackKnight.png"));
				this.bishopImg = ImageIO.read(new File(ChessBoardComponent.piecesPath + "BlackBishop.png"));
				this.queenImg = ImageIO.read(new File(ChessBoardComponent.piecesPath + "BlackQueen.png"));
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	private void closeDialog() {
		this.setVisible(false);
		this.dispose();
	}

	public int getChosenPiece() {
		return chosenPiece;
	}
}