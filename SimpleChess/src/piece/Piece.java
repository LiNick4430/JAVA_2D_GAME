package piece;

import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.IOException;

import javax.imageio.ImageIO;

import main.Board;
import main.GamePanel;
import main.Type;

public class Piece {

	public Type type;
	public BufferedImage image;
	public int x, y;
	public int col, row, preCol, preRow;			// preCol 和 preRow 儲存了棋子移動前的位置，這對於某些特殊規則（如兵的兩步移動和吃過路兵）可能很有用。
	public int color;
	public Piece hittingP;							// 當棋子在模擬移動時遇到另一個棋子時，這個屬性會指向被遇到的棋子。
	public boolean moved, twoStepped;				// moved 標記棋子是否已經移動過（對於王和車的城堡移動很重要）
													// twoStepped 標記兵是否在上次移動時走了兩步（對於吃過路兵規則很重要）。
	// 建構子
	public Piece (int color, int col, int row) {
		
		this.color = color;
		this.col = col;
		this.row = row;
		x = getX(col);
		y = getY(row);
		preCol = col;
		preRow = row;
	}
	// 匯入圖片
	public BufferedImage getImage(String imagePath) {
		
		BufferedImage image = null;
		
		try {
			image = ImageIO.read(getClass().getClassLoader().getResourceAsStream("piece/" + imagePath + ".png"));
		} catch (IOException e) {
			e.printStackTrace();
		}	
		return image;
	}	
	// getX(int col) 和 getY(int row) 將棋盤上的欄列索引轉換為螢幕上的 x 和 y 座標。
	public int getX(int col) {
		return col * Board.SQUARE_SIZE;
	}
	public int getY(int row) {
		return row * Board.SQUARE_SIZE;
	}
	// getCol(int x) 和 getRow(int y) 執行相反的操作，將螢幕上的 x 和 y 座標轉換回棋盤上的欄列索引。它們考慮了棋子圖片的中心點來精確定位到棋盤格子。
	public int getCol(int x) {
		return (x + Board.HALF_SQUARE_SIZE) / Board.SQUARE_SIZE;
	}
	public int getRow(int y) {
		return (y + Board.HALF_SQUARE_SIZE) / Board.SQUARE_SIZE;
	}
	// 這個方法在 GamePanel.simPieces 列表中尋找當前棋子的索引。
	public int getIndex() {
		for (int index = 0; index < GamePanel.simPieces.size(); index++) {
			if (GamePanel.simPieces.get(index) == this) {
				return index;
			}			
		}
		return 0;
	}
	// 在棋子成功移動後 更新位置
	public void updatePosition() {
		
		// To check En Passant
		if (type == Type.PAWN) {
			if (Math.abs(row - preRow) == 2) {
				twoStepped = true;
			}
		}
		
		x = getX(col);
		y = getY(row);
		preCol = getCol(x);
		preRow = getRow(y);
		moved = true;		
	}
	// 將棋子的位置重置回移動前的位置（preCol 和 preRow），並相應地更新螢幕座標 x 和 y
	public void resetPosition() {

		col = preCol;
		row = preRow;
		x = getX(col);
		y = getY(row);

	}
	// 這是一個抽象方法,子類別（如 Pawn, Rook 等）都需要覆寫這個方法，以實現該棋子特定的移動規則。
	public boolean canMove(int targetCol, int targetRow) {
		return false;
	}
	// 查給定的目標欄列是否在棋盤的有效範圍內（0-7）
	public boolean isWithinBoard(int targetCol, int targetRow) {
		if (targetCol >= 0&& targetCol <= 7 && targetRow >= 0 && targetRow <= 7 ) {
			return true;
		}else {
			return false;
		}
	}
	// 這個方法檢查在 GamePanel.simPieces 列表中，目標欄列是否被另一個 不同於自身 的棋子佔據。如果是，則返回該棋子的 Piece 物件，並將其儲存在 hittingP 屬性中；否則返回 null
	public Piece getHittingP(int targetCol, int targetRow) {
		for (Piece piece : GamePanel.simPieces) {
			if (piece.col == targetCol && piece.row == targetRow && piece != this) {
				return piece;
			}
		}
		return null;
	}
	// 檢查目標欄列是否與棋子的原始位置（preCol, preRow）相同。
	public boolean isSameSquare(int targetCol, int targetRow) {
		if (targetCol == preCol && targetRow == preRow) {
			return true;
		}
		return false;
	}
	// 這個方法判斷目標格子是否是一個有效的移動目標
	public boolean isValidSquare(int targetCol, int targetRow) {
		
		hittingP = getHittingP(targetCol, targetRow);	// 檢查 目標格子 是否有棋子
		
		if (hittingP == null) {	// This square is VACANT
			return true;
		} else { // This square is OCCUPIED
			if (hittingP.color != this.color) {	// If this color is different, it can be captured 
				 return true;
			} else {	// 目標格子的棋子顏色相同，則不能移動到該格子，返回 false 並重置 hittingP 為 null。
				hittingP = null;
			}
		}
		return false;
	}
	// 這個方法檢查在當前棋子和目標格子之間的直線上（水平或垂直）是否有其他棋子阻擋
	public boolean pieceIsOnStraightLine(int targetCol, int targetRow) {
		
		// When this piece is moving to the left
		for (int c = preCol-1 ; c > targetCol; c--) {
			for (Piece piece : GamePanel.simPieces) {
				if (piece.col == c && piece.row == targetRow) {
					hittingP = piece;
					return true;
				}
			}
		}
		
		// When this piece is moving to the right 
		for (int c = preCol+1 ; c < targetCol; c++) {
			for (Piece piece : GamePanel.simPieces) {
				if (piece.col == c && piece.row == targetRow) {
					hittingP = piece;
					return true;
				}
			}
		}
		
		// When this piece is moving to the up 
		for (int c = preRow-1 ; c > targetRow; c--) {
			for (Piece piece : GamePanel.simPieces) {
				if (piece.col == targetCol && piece.row == c) {
					hittingP = piece;
					return true;
				}
			}
		}
		
		// When this piece is moving to the down 
		for (int c = preRow+1 ; c < targetRow; c++) {
			for (Piece piece : GamePanel.simPieces) {
				if (piece.col == targetCol && piece.row == c) {
					hittingP = piece;
					return true;
				}
			}
		}
		return false;
	}
	// 這個方法檢查在當前棋子和目標格子之間的 對角線上 是否有其他棋子阻擋
	public boolean pieceIsOnDiagonalLine(int targetCol, int targetRow) {
		
		if (targetRow < preRow) {
			// up left
			for (int c = preCol-1 ; c > targetCol; c--) {
				int diff = Math.abs(c - preCol);
				for (Piece piece : GamePanel.simPieces) {
					if (piece.col == c && piece.row == preRow - diff) {
						hittingP = piece;
						return true;
					}
				}
			}
			// up right
			for (int c = preCol+1 ; c < targetCol; c++) {
				int diff = Math.abs(c - preCol);
				for (Piece piece : GamePanel.simPieces) {
					if (piece.col == c && piece.row == preRow - diff) {
						hittingP = piece;
						return true;
					}
				}
			}
		}
		
		else if (targetRow > preRow) {
			// down left
			for (int c = preCol-1 ; c > targetCol; c--) {
				int diff = Math.abs(c - preCol);
				for (Piece piece : GamePanel.simPieces) {
					if (piece.col == c && piece.row == preRow - diff) {
						hittingP = piece;
						return true;
					}
				}
			}
			// down right
			for (int c = preCol+1 ; c < targetCol; c++) {
				int diff = Math.abs(c - preCol);
				for (Piece piece : GamePanel.simPieces) {
					if (piece.col == c && piece.row == preRow - diff) {
						hittingP = piece;
						return true;
					}
				}
			}
		}

		return false;
	}
	// 製棋子的圖像
	public void draw(Graphics2D g2) {
		
		g2.drawImage(image, x, y, Board.SQUARE_SIZE, Board.SQUARE_SIZE, null);
	}
	
}
