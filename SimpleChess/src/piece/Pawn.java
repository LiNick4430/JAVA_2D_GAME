package piece;

import main.GamePanel;
import main.Type;

public class Pawn extends Piece{

	public Pawn(int color, int col, int row) {
		super(color, col, row);

		type = Type.PAWN;
		
		if (color == GamePanel.WHITE) {
			image = getImage("w-pawn");
		}else {
			image = getImage("b-pawn");
		}
		
	}

	@Override
	public boolean canMove(int targetCol, int targetRow) {
		
		if (isWithinBoard(targetCol, targetRow) && isSameSquare(targetCol, targetRow) == false) {
			
			// Difine the move value based on its color
			int moveValue;
			if (color == GamePanel.WHITE) {
				moveValue = -1;
			}else {
				moveValue = 1;
			}
			
			// Check the hitting piece
			hittingP = getHittingP(targetCol, targetRow);
			
			// 1 square movement
			if (targetCol == preCol && targetRow == preRow + moveValue && hittingP == null) {
				return true;
			}
			
			// 2 square movement
			if (targetCol == preCol && targetRow == preRow + moveValue * 2 && hittingP == null && moved == false &&
					pieceIsOnStraightLine(targetCol, targetRow) == false) {
				return true;
			}
			
			// Diagonal movement & Capture (if piece is on a square diagonally in front of it)
			if (Math.abs(targetCol - preCol) == 1 && targetRow == preRow + moveValue && hittingP != null &&
					hittingP.color != color) {
				return true;
			}
			
			// 吃過路兵(En Passant)
			if (Math.abs(targetCol - preCol) == 1 && targetRow == preRow + moveValue) {
				for (Piece piece : GamePanel.simPieces) {
					if (piece.col == targetCol && piece.row == preRow && piece.twoStepped == true) {
						hittingP = piece;
						return true;
					}
				}
			}
		}
		return false;
	}
}
