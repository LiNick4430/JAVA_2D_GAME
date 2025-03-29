package object;

import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.image.BufferedImage;

import main.GamePanel;
import main.UtilityTool;

public class SuperObject {
	
	GamePanel gp;
	
	public BufferedImage image, image2, image3;
	public String name;
	public boolean collision = false;
	public int worldX, worldY;
	
	public Rectangle solidArea = new Rectangle(0,0,48,48); // 產生碰撞區域 
	public int solidAreaDefaultX = 0;	// 儲存碰撞區域的預設 X 座標，用於在需要時恢復原始位置。
	public int solidAreaDefaultY = 0;	// 儲存碰撞區域的預設 Y 座標，用於在需要時恢復原始位置。
	UtilityTool uTool = new UtilityTool();
	
	public void draw(Graphics2D g2, GamePanel gp) {
		
		int screenX = worldX - gp.player.worldX + gp.player.screenX;
		int screenY = worldY - gp.player.worldY + gp.player.screenY;
		
		// 讓 只在畫面XY 附近的 世界XY 的 繪圖 用於減輕效能 
		if(worldX + gp.tileSize > gp.player.worldX - gp.player.screenX &&
		   worldX - gp.tileSize < gp.player.worldX + gp.player.screenX &&
		   worldY + gp.tileSize > gp.player.worldY - gp.player.screenY &&
		   worldY - gp.tileSize < gp.player.worldY + gp.player.screenY) {
			
			g2.drawImage(image, screenX, screenY, null);	// 畫上 Object
		}
		
	}
}
