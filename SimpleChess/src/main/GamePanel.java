package main;

import java.awt.AlphaComposite;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.util.ArrayList;

import javax.swing.JPanel;

import piece.Bishop;
import piece.King;
import piece.Knight;
import piece.Pawn;
import piece.Piece;
import piece.Queen;
import piece.Rook;

public class GamePanel extends JPanel implements Runnable {

	public static final int WIDTH = 1100;	// 寬度 1100
	public static final int HEIGHT = 800;	// 高度 800
	final int FPS = 60;						// FPS 60
	Thread gameThread;						// 遊戲循環的執行緒
	Board board = new Board();				// 繪製棋盤
	Mouse mouse = new Mouse();				// 滑鼠輸入
	
	// PIECES
	public static ArrayList<Piece> pieces = new ArrayList<>();			// 儲存所有 棋子列表 
	public static ArrayList<Piece> simPieces = new ArrayList<>();		// 模擬移動 棋子列表
	ArrayList<Piece> promoPieces = new ArrayList<>();					// 用於兵升變時顯示可選擇的棋子
	Piece activeP, checkingP;					// activeP 儲存目前被選中的棋子，checkingP 儲存正在將軍的棋子
	public static Piece castlingP;				// 處理城堡移動的特殊棋子
	
	// COLOR
	public static final int WHITE = 1;			// 白色 常數
	public static final int BLACK = 0;			// 黑色 常數
	int currentColor = WHITE;					// 預設 白方
	
	// BOOLEAN
	boolean canMove;			// 標記目前選中的棋子是否可以移動到滑鼠懸停的位置
	boolean validSquare;		// 標記模擬的移動是否合法
	boolean promotion;			// 標記目前是否處於兵升變的階段
	boolean gameover;			// 標記遊戲是否結束（將軍絕殺）
	boolean stalemate;			// 標記遊戲是否平局（逼和）
	
	// 建構子
	public GamePanel() {
		setPreferredSize(new Dimension(WIDTH, HEIGHT));
		setBackground(Color.black);
		addMouseMotionListener(mouse);		// 滑鼠移動監聽器	移動（無論按鈕是否被按下）或者在按鈕被按下並拖曳時，這個監聽器會接收到相關的事件
		addMouseListener(mouse);			// 滑鼠監聽器 		進行點擊、按下或釋放按鈕等操作時，這個監聽器會接收到相關的事件。
		
		setPieces();
//		testPromotion();
//		testIllegal();
		copyPieces(pieces, simPieces);	// 將 pieces(儲存所有) 複製到 simPieces(模擬移動用的)
	}
	
	// 創建並啟動了一個新的執行緒來運行 run() 方法，開始遊戲循環。
	public void launchGame() {
		gameThread = new Thread(this);
		gameThread.start();
	}
	// 標準的西洋棋開局棋子位置
	public void setPieces() {
		
		// WHITE
		pieces.add(new Pawn(WHITE, 0, 6));
		pieces.add(new Pawn(WHITE, 1, 6));
		pieces.add(new Pawn(WHITE, 2, 6));
		pieces.add(new Pawn(WHITE, 3, 6));
		pieces.add(new Pawn(WHITE, 4, 6));
		pieces.add(new Pawn(WHITE, 5, 6));
		pieces.add(new Pawn(WHITE, 6, 6));
		pieces.add(new Pawn(WHITE, 7, 6));		
		pieces.add(new Rook(WHITE, 0, 7));
		pieces.add(new Rook(WHITE, 7, 7));
		pieces.add(new Knight(WHITE, 1, 7));
		pieces.add(new Knight(WHITE, 6, 7));
		pieces.add(new Bishop(WHITE, 2, 7));
		pieces.add(new Bishop(WHITE, 5, 7));
		pieces.add(new Queen(WHITE, 3, 7));
		pieces.add(new King(WHITE, 4, 7));
		
		// BLACK
		pieces.add(new Pawn(BLACK, 0, 1));
		pieces.add(new Pawn(BLACK, 1, 1));
		pieces.add(new Pawn(BLACK, 2, 1));
		pieces.add(new Pawn(BLACK, 3, 1));
		pieces.add(new Pawn(BLACK, 4, 1));
		pieces.add(new Pawn(BLACK, 5, 1));
		pieces.add(new Pawn(BLACK, 6, 1));
		pieces.add(new Pawn(BLACK, 7, 1));		
		pieces.add(new Rook(BLACK, 0, 0));
		pieces.add(new Rook(BLACK, 7, 0));
		pieces.add(new Knight(BLACK, 1, 0));
		pieces.add(new Knight(BLACK, 6, 0));
		pieces.add(new Bishop(BLACK, 2, 0));
		pieces.add(new Bishop(BLACK, 5, 0));
		pieces.add(new Queen(BLACK, 3, 0));
		pieces.add(new King(BLACK, 4, 0));		
	}
	// 測試特定環境棋子
	public void testPromotion() {
		pieces.add(new Pawn(WHITE, 0, 4));
		pieces.add(new Pawn(BLACK, 7, 4));
	}
	public void testIllegal() {
		pieces.add(new Pawn(WHITE, 7, 6));
		pieces.add(new King(WHITE, 3, 7));
		pieces.add(new King(BLACK, 0, 3));
		pieces.add(new Bishop(BLACK, 1, 4));
		pieces.add(new Queen(BLACK, 4, 5));
	}
	// 一個實用工具方法，用於將一個 Piece 列表的內容複製到另一個列表。這在模擬移動時非常有用，因為它允許在不影響原始棋子列表的情況下進行更改。
	private void copyPieces(ArrayList<Piece> source, ArrayList<Piece> target) {
		
		target.clear();
		for (int i = 0; i < source.size(); i++) {
			target.add(source.get(i));
		}
	}
	
	@Override
	public void run() {
		
		// Game Loop
		double drawInterval = 1000000000/FPS;
		double delta = 0;
		long lastTime = System.nanoTime();
		long currentTime;
		
		while (gameThread != null) {
			
			currentTime = System.nanoTime();
			
			delta += (currentTime - lastTime) / drawInterval;
			lastTime = currentTime;
			
			if (delta > 1) {
				update();
				repaint();
				delta--;
			}
		}
	}
	
	private void update() {
		
		if (promotion == true) {
			promoting();	// promoting() 方法處理兵升變的選擇
		}
		else if(gameover == false && stalemate == false) { // 如果 遊戲 尚未結束 或 平局	
			
			//// MOUSE BUTTON PRESSED ////
			if (mouse.pressed) {
				if (activeP == null) {
					// If the activeP is null, check if you can pick up a piece
					for (Piece piece : simPieces) {
						// If the mouse is on an ally piece, pick up as the activeP
						if (piece.color == currentColor &&
								piece.col == mouse.x/Board.SQUARE_SIZE &&
								piece.row == mouse.y/Board.SQUARE_SIZE) {
							
							activeP = piece;
						}
					}
				}else {	
					// If the player is holding a piece, simulate the move
					simulate();				
				}
			}
			
			//// MOUSE BUTTON RELEASED ////
			if (mouse.pressed == false) {
				// 如果之前有選中棋子
				if (activeP != null) {
					
					if (validSquare) {		// 如果 validSquare 為 true (移動合法且不導致己方國王被將軍)
						
						// MOVE CONFIREMD						
						// Update the piece list in case a piece has been captured and removed during the simulation 
						copyPieces(simPieces, pieces);
						activeP.updatePosition();
						// 如果發生城堡移動 (castlingP != null)，也更新城堡棋子的位置。
						if (castlingP != null) {
							castlingP.updatePosition();
						}
						
						if (isKingInCheck() && isCheckmate()) {
							gameover = true;
						}
						else if (isStalemate() && isKingInCheck()) {
							stalemate = true;
						}
						else {	// The game is still going on 
							if (canPromote()) {
								promotion = true;
							}else {
								changePlayer();
							}
						}
					} else {
						// The move is not valid so reset everything
						// 如果 validSquare 為 false (移動不合法)，則撤銷模擬的移動
						copyPieces(pieces, simPieces);
						activeP.resetPosition();
						activeP = null;
					}
				}	
			}	
		}
	}
	// 模擬移動
	public void simulate() {
		
		canMove = false;
		validSquare = false;
		
		// Reset the piece list in every loop
		// This is basically for restoring the removed piece during the simulation
		// 將主要的 pieces 列表複製回 simPieces 列表，以確保在每次模擬時都從原始狀態開始。
		copyPieces(pieces, simPieces);
		
		// Reset the castling piece's position
		// 重置城堡棋子 (castlingP) 的位置（如果有的話）
		if (castlingP != null) {
			castlingP.col = castlingP.preCol;
			castlingP.x = castlingP.getX(castlingP.col);
			castlingP = null;
		}
		
		// If a piece is being held, update its position
		// 更新被拖動棋子 (activeP) 的螢幕位置和棋盤上的行列位置
		activeP.x = mouse.x - Board.HALF_SQUARE_SIZE;
		activeP.y = mouse.y - Board.HALF_SQUARE_SIZE;
		activeP.col = activeP.getCol(activeP.x);
		activeP.row = activeP.getRow(activeP.y);
		
		// Check if the piece is hovering over a reachable square
		if (activeP.canMove(activeP.col, activeP.row)) {
			
			canMove = true;
			
			// If hitting a piece, remove if from the list
			// 移動會捕獲到一個棋子 (activeP.hittingP != null)，則將該棋子從 simPieces 列表中移除。
			if (activeP.hittingP != null) {
				simPieces.remove(activeP.hittingP.getIndex());
			}
			// 處理城堡的特殊移動
			checkCastling();
			// 檢查模擬的移動是否會導致己方國王被將軍 (isIllegal(activeP) == false) 並且對方在移動後是否可以立即捕獲己方國王 (opponetCanCaptureKing() == false)
			// 如果都不是，則設定 validSquare 為 true。
			if (isIllegal(activeP) == false && opponetCanCaptureKing() == false) {
				validSquare = true;
			}
		}
	}
	// 檢查是否導致己方國王被將軍
	private boolean isIllegal(Piece king) {
		
		if (king.type == Type.KING) {
			// 遍歷 simPieces 列表中的所有敵方棋子，並檢查它們是否可以移動到國王目前的位置
			for (Piece piece : simPieces) {
				if (piece != king && piece.color != king.color && piece.canMove(king.col, king.row)) {
					return true;
				}
			}
		}
		return false;
	}
	// 檢查對方是否可以立即捕獲己方國王
	private boolean opponetCanCaptureKing() {

		Piece king = getKing(false);

		for (Piece piece : simPieces) {
			if (piece.color != king.color && piece.canMove(king.col, king.row)) {
				return true;
			}
		}	
		return false;
	}
	// 檢查己方國王是否被將軍
	private boolean isKingInCheck() {
		// 輪到的玩家的國王
		Piece king = getKing(true);
		// 是否被對方棋子將軍。如果被將軍，則將將軍的棋子儲存在 checkingP 中
		if (activeP.canMove(king.col, king.row)) {
			checkingP = activeP;
			return true;
		}else {
			checkingP = null;
		}
		return false;
	}
	// 獲取指定顏色的國王
	private Piece getKing(boolean oppnent) {
		Piece king = null;
		
		for (Piece piece : simPieces) {
			if (oppnent) {
				if (piece.type == Type.KING && piece.color != currentColor) {
					king = piece;
				}
			}else {
				if (piece.type == Type.KING && piece.color == currentColor) {
					king = piece;
				}
			}
		}
		return king;
	}
	// 檢查是否將軍絕殺
	private boolean isCheckmate() {
		
		Piece king = getKing(true);
		
		if (kingCanMove(king)) {
			return false;
		}else {
			// But you still have a chance!!!
			// Check if you can block the attack with your piece
			
			// Check the position of the checking piece and the king in check
			int colDiff = Math.abs(checkingP.col - king.col);
			int rowDiff = Math.abs(checkingP.row - king.row);
			
			if (colDiff == 0) {
				// The checking piece is attacking vertically
				if (checkingP.row < king.row) {
					// The checking piece is above the king
					for (int row = checkingP.row; row < king.row; row++) {
						for (Piece piece : simPieces) {
							if (piece != king && piece.color != currentColor && piece.canMove(checkingP.col, row)) {
								return false;
							}
						}
					}
				}
				if (checkingP.row > king.row) {
					// The checking piece is below the king
					for (int row = checkingP.row; row > king.row; row--) {
						for (Piece piece : simPieces) {
							if (piece != king && piece.color != currentColor && piece.canMove(checkingP.col, row)) {
								return false;
							}
						}
					}
				}
			}else if (rowDiff == 0) {
				// The checking piece is attacking horizontally
				if (checkingP.col < king.col) {
					// The checking piece is to the left
					for (int col = checkingP.col; col < king.col; col++) {
						for (Piece piece : simPieces) {
							if (piece != king && piece.color != currentColor && piece.canMove(col, checkingP.row)) {
								return false;
							}
						}
					}
				}
				if (checkingP.col > king.col) {
					// The checking piece is to the right
					for (int col = checkingP.col; col > king.col; col--) {
						for (Piece piece : simPieces) {
							if (piece != king && piece.color != currentColor && piece.canMove(col, checkingP.row)) {
								return false;
							}
						}
					}
				}
			}else if (colDiff == rowDiff) {
				// The checking piece is attacking diagonally
				if (checkingP.row < king.row) {
					// The checking piece is above the king
					if (checkingP.col < king.col) {
						// The checking piece is in the upper left
						for(int col = checkingP.col, row = checkingP.row; col < king.col; col++, row++) {
							for (Piece piece : simPieces) {
								if (piece != king && piece.color != currentColor && piece.canMove(col, row)) {
									return false;
								}
							}
						}
					}
					if (checkingP.col < king.col) {
						// The checking piece is in the upper right
						for(int col = checkingP.col, row = checkingP.row; col > king.col; col--, row++) {
							for (Piece piece : simPieces) {
								if (piece != king && piece.color != currentColor && piece.canMove(col, row)) {
									return false;
								}
							}
						}
					}
				}
				if (checkingP.row > king.row) {
					// The checking piece is below the king
					if (checkingP.col < king.col) {
						// The checking piece is in the lower left
						for(int col = checkingP.col, row = checkingP.row; col < king.col; col++, row--) {
							for (Piece piece : simPieces) {
								if (piece != king && piece.color != currentColor && piece.canMove(col, row)) {
									return false;
								}
							}
						}
					}
					if (checkingP.col < king.col) {
						// The checking piece is in the lower right
						for(int col = checkingP.col, row = checkingP.row; col > king.col; col--, row--) {
							for (Piece piece : simPieces) {
								if (piece != king && piece.color != currentColor && piece.canMove(col, row)) {
									return false;
								}
							}
						}
					}
				}
			}else {
				// The checking piece is knight
			}
		}
		return true;
	}
	// 檢查國王是否有任何合法的移動
	private boolean kingCanMove(Piece king) {

		// Simulate if there is any square where the king can move to 
		if ( isValidMove(king, -1, -1) ) { return true; }
		if ( isValidMove(king, 0, -1) ) { return true; }
		if ( isValidMove(king, 1, -1) ) { return true; }
		if ( isValidMove(king, -1, 0) ) { return true; }
		if ( isValidMove(king, 1, 0) ) { return true; }
		if ( isValidMove(king, 0, 1) ) { return true; }
		if ( isValidMove(king, 1, 1) ) { return true; }
		
		return false;
	}
	// 檢查單一國王移動是否合法
	private boolean isValidMove(Piece king, int colPlus, int rowPlus) {

		boolean isValidMove = false;
		
		// Update the king's position for second
		king.col += colPlus;
		king.row += rowPlus;
		
		if (king.canMove(king.col, king.row)) {
			
			if (king.hittingP != null) {
				simPieces.remove(king.hittingP.getIndex());
			}
			if (isIllegal(king) == false) {
				isValidMove = true;
			}
		}
		// Reset the king's position and restore the removed piece 
		king.resetPosition();
		copyPieces(pieces, simPieces);

		return isValidMove;	
	}
	// 檢查是否逼和
	private boolean isStalemate() {
		
		int count = 0;
		// Count the number of pieces
		for (Piece piece : simPieces) {
			if (piece.color != currentColor) {
				count++;
			}
		}
		
		// If only one piece (the king) is left
		if (count == 1) {
			if (kingCanMove(getKing(true)) == false) {
				return true;
			}
		}	
		return false;
	}
	// 處理城堡移動後的棋子位置更新
	private void checkCastling() {
		
		if (castlingP != null) {
			if (castlingP.col == 0) {
				castlingP.col += 3;
			}else if (castlingP.col == 7){
				castlingP.col -= 2;
			}
			castlingP.x = castlingP.getX(castlingP.col);
		}
	}
	// 切換玩家
	private void changePlayer() {
		
		if (currentColor == WHITE) {
			currentColor = BLACK;
			// Reset black's two stepped status
			for (Piece piece : pieces) {
				if (piece.color == BLACK) {
					piece.twoStepped = false;
				}
			}
		}else {
			currentColor = WHITE;
			// Reset white's two stepped status
			for (Piece piece : pieces) {
				if (piece.color == WHITE) {
					piece.twoStepped = false;
				}
			}
		}
		activeP = null;		// 取消選中任何棋子
	}
	// 是否可以兵升變
	private boolean canPromote() {
		
		if (activeP.type == Type.PAWN) {
			if ((currentColor == WHITE && activeP.row == 0) || (currentColor == BLACK && activeP.row == 7)) {
				promoPieces.clear();
				promoPieces.add(new Rook(currentColor, 9, 2));
				promoPieces.add(new Knight(currentColor, 9, 3));
				promoPieces.add(new Bishop(currentColor, 9, 4));
				promoPieces.add(new Queen(currentColor, 9, 5));
				return true;
			}
		}
		return false;
	}
	// 處理兵升變的選擇
	private void promoting() {
		
		if (mouse.pressed) {
			for (Piece piece : promoPieces) {
				if (piece.col == mouse.x/Board.SQUARE_SIZE && piece.row == mouse.y/Board.SQUARE_SIZE) {
					switch (piece.type) {
						case ROOK: simPieces.add(new Rook(currentColor, activeP.col, activeP.row)); break;
						case KNIGHT: simPieces.add(new Knight(currentColor, activeP.col, activeP.row)); break;
						case BISHOP: simPieces.add(new Bishop(currentColor, activeP.col, activeP.row)); break;
						case QUEEN: simPieces.add(new Queen(currentColor, activeP.col, activeP.row)); break;
						default: break;
					}
					simPieces.remove(activeP.getIndex());
					copyPieces(simPieces, pieces);
					activeP = null;
					promotion = false;
					changePlayer();
				}
			}
		}
	}
	// 繪製遊戲畫面
	@Override
	public void paintComponent(Graphics g) {
		super.paintComponent(g); 
		
		Graphics2D g2 = (Graphics2D)g;
		
		// BOARD
		board.draw(g2);
		
		// PIECES
		for (Piece p : simPieces) {
			p.draw(g2);
		}
		
		if (activeP != null) {
			if (canMove) {
				if (isIllegal(activeP) || opponetCanCaptureKing()) {
					g2.setColor(Color.gray);
					g2.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 0.7f));
					g2.fillRect(activeP.col*Board.SQUARE_SIZE, activeP.row*Board.SQUARE_SIZE,
							Board.SQUARE_SIZE, Board.SQUARE_SIZE);
					g2.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 1f));
				}else {
					g2.setColor(Color.white);
					g2.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 0.7f));
					g2.fillRect(activeP.col*Board.SQUARE_SIZE, activeP.row*Board.SQUARE_SIZE,
							Board.SQUARE_SIZE, Board.SQUARE_SIZE);
					g2.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 1f));
				}	
			}
			
			// Draw the active piece in the end so it won't be hidden by the board or colored square
			activeP.draw(g2);
		}
		
		// STATUS MESSAGES
		g2.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
		g2.setFont(new Font("Book Antiqua", Font.PLAIN, 40));
		g2.setColor(Color.white);
		// 處理 兵 升變
		if (promotion) {
			g2.drawString("Promote to:", 840, 150);
			for (Piece piece : promoPieces) {
				g2.drawImage(piece.image, piece.getX(piece.col), piece.getY(piece.row), 
						Board.SQUARE_SIZE, Board.SQUARE_SIZE, null);
			}
		}else {
			if (currentColor == WHITE) {
				g2.drawString("White's turn", 840, 550);
				if (checkingP != null && checkingP.color == BLACK) {
					g2.setColor(Color.red);
					g2.drawString("The king", 840, 650);
					g2.drawString("is in check!", 840, 700);
				}
			}else {
				g2.drawString("Black's turn", 840, 250);
				if (checkingP != null && checkingP.color == WHITE) {
					g2.setColor(Color.red);
					g2.drawString("The king", 840, 100);
					g2.drawString("is in check!", 840, 150);
				}
			}
		}
		// 繪製勝利訊息
		if (gameover) {
			String text = "";
			if (currentColor == WHITE) {
				text = "White Wins";
			} else {
				text = "Black Wins";
			}
			g2.setFont(new Font("Arial", Font.PLAIN, 90));
			g2.setColor(Color.green);
			g2.drawString(text, 200, 420);			
		}
		// 繪製平局訊息
		if (stalemate) {
			g2.setFont(new Font("Arial", Font.PLAIN, 90));
			g2.setColor(Color.lightGray);
			g2.drawString("Stalemate", 200, 420);
		}
	}
}
