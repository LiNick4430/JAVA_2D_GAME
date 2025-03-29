package entity;

import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.image.BufferedImage;
import java.io.IOException;

import javax.imageio.ImageIO;

import main.GamePanel;
import main.UtilityTool;

// 實體(Entity) 儲存變數 用在 player, monster, NPC classes
public class Entity {
	
	GamePanel gp;
	
	// set default position 設定預設位置
	public int worldX, worldY;	// 預設 世界位置 X, Y
	public int speed;
	// 匯入圖片 相關
	public BufferedImage up1, up2, down1, down2, left1, left2, right1, right2; 	// 匯入圖片 名稱
	// 圖片使用 相關
	public String direction;		// 方向 用於switch case 的對應
	public int spriteCounter = 0;	// 用於計算 update 次數
	public int spriteNum = 1;		// 決定使用 圖片序號
	// 碰撞區域 相關
	public Rectangle solidArea = new Rectangle(0, 0, 48, 48);	// 用於表示一個矩形區域。  它通常用於表示物件的邊界框或碰撞區域。
	public int solidAreaDefaultX, solidAreaDefaultY;			// 儲存碰撞區域的預設 X,Y 座標，用於在需要時恢復原始位置。
	public boolean collisionOn = false;							// 表示 是否成功碰撞
	// NPC 
	public int actionLockCounter = 0;		// 行動邏輯 計數器
	String[] dialogues = new String[20];	// 儲存 對話 陣列
	int dialogueIndex = 0;					// 對話 陣列 的 計數器
	// CHARCTER STAUS
	public int maxLife;		// 最大生命值
	public int life;		// 現有生命值
	
	public Entity(GamePanel gp) {
		this.gp = gp;
	}
	
	public void setAction() {}	// 預設 空 setAction(行動邏輯)
	
	public void speak() {
		
		if (dialogues[dialogueIndex] == null) {
			dialogueIndex = 0;
		}
		gp.ui.currentDialogue = dialogues[dialogueIndex];
		dialogueIndex++;
			
		switch (gp.player.direction) {
			case "up":
				direction = "down";
				break;
			case "down":
				direction = "up";
				break;
			case "left":
				direction = "right";				
				break;
			case "right":
				direction = "left";
				break;
		}
		
	}
	
	// 進行更新
	public void update() {
		// 透過行動邏輯 
		setAction();
		// 碰撞邏輯檢測
		collisionOn = false; 					// 初始化  
		gp.cChecker.checkTile(this);			// 對 Tile
		gp.cChecker.checkObject(this, false);	// 對 Object
		gp.cChecker.checkPlayer(this); 			// 對 Player
		// 當碰撞邏輯 false 進行移動
		if (collisionOn == false) {
			switch(direction) {
				case "up":
					worldY -= speed;
					break;
				case "down":
					worldY += speed;
					break;
				case "left":
					worldX -= speed;
					break;
				case "right":
					worldX += speed;
					break;
			}
		}
		// 當 update 次數 12 以上 會將圖片順序交換
		spriteCounter++; 	 
		if(spriteCounter > 12) { 
			if(spriteNum == 1) {
				spriteNum = 2;
			} else if(spriteNum == 2) {
				spriteNum = 1;
			}
			spriteCounter = 0;
		}
	}
	
	public void draw(Graphics2D g2) {
		
		BufferedImage image = null;  // 建立變數 預設 null 防止圖片匯入失敗
		
		int screenX = worldX - gp.player.worldX + gp.player.screenX;
		int screenY = worldY - gp.player.worldY + gp.player.screenY;
		
		// 讓 只在畫面XY 附近的 世界XY 的 繪圖 用於減輕效能 
		if(worldX + gp.tileSize > gp.player.worldX - gp.player.screenX &&
		   worldX - gp.tileSize < gp.player.worldX + gp.player.screenX &&
		   worldY + gp.tileSize > gp.player.worldY - gp.player.screenY &&
		   worldY - gp.tileSize < gp.player.worldY + gp.player.screenY) {
			
			switch(direction) {
				case "up":
					if(spriteNum == 1) {
						image = up1;
					}
					if(spriteNum == 2) {
						image = up2;
					}
					break;
				case "down":
					if(spriteNum == 1) {
						image = down1;
					}
					if(spriteNum == 2) {
						image = down2;
					}
					break;
				case "left":
					if(spriteNum == 1) {
						image = left1;
					}
					if(spriteNum == 2) {
						image = left2;
					}
					break;
				case "right":
					if(spriteNum == 1) {
						image = right1;
					}
					if(spriteNum == 2) {
						image = right2;
					}
					break;
			}			
			g2.drawImage(image, screenX, screenY, null);
		}
	}
	
	// 圖片 讀取 並同時放大
	public BufferedImage setup(String imagePath) {
		
		UtilityTool uTool = new UtilityTool();
		BufferedImage image = null;
		
		try {
			image = ImageIO.read(getClass().getClassLoader().getResourceAsStream(imagePath + ".png"));
			image = uTool.scaleImage(image, gp.tileSize, gp.tileSize); 
			
		} catch (IOException e) {
			e.printStackTrace();
		}
		return image; // 輸出堆疊追蹤
	}
	/* 舊版匯入方法 被整合進去 setup()
	try { // 嘗試圖片匯入 預防 目標檔案 找不到
		up1 = ImageIO.read(getClass().getClassLoader().getResourceAsStream("player/boy_up_1.png")); // 	當前類別的類別載入器（ClassLoader）來尋找資源。
		// 或者 up1 = ImageIO.read(getClass().getResourceAsStream("/player/boy_up_1.png"));  			 	絕對路徑，從類別路徑的根目錄開始尋找
	}catch(IOException e) {
		e.printStackTrace(); // 輸出堆疊追蹤
	}
	*/	
}
