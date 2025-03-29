package entity;

// import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.image.BufferedImage;

import main.GamePanel;
import main.KeyHandler;

public class Player extends Entity {
	
	KeyHandler keyH;
	
	public final int screenX;	// 畫面 X
	public final int screenY;	// 畫面 Y
	int standCounter = 0;				// 計數 無輸入的 禎數 
	
	public Player(GamePanel gp, KeyHandler keyH) {
		
		super(gp);
		
		this.keyH = keyH;
		
		screenX = gp.screenWidth/2 - (gp.tileSize/2);	// 畫面X 在 畫面X軸距離一半, 因為繪圖是依照圖的左上角 還要再往回 半格的距離 
		screenY = gp.screenHeight/2 - (gp.tileSize/2);	// 畫面Y 在 畫面Y軸距離一半, 因為繪圖是依照圖的左上角 還要再往回 半格的距離
		
//		solidArea = new Rectangle (8, 16, 32, 32);
		solidArea = new Rectangle ();	// 建立一個 矩形
		solidArea.x = (gp.originalTileSize / 2);		// 矩形的X  X0 是這個Entity 的左上角 用originalTileSize(16*16) 當基準
		solidArea.y = gp.originalTileSize;				// 矩形的Y  Y0 是這個Entity 的左上角
		solidAreaDefaultX = solidArea.x;				// 儲存碰撞區域的預設 X 座標，用於在需要時恢復原始位置。
		solidAreaDefaultY = solidArea.y;				// 儲存碰撞區域的預設 Y 座標，用於在需要時恢復原始位置。
		solidArea.width = gp.originalTileSize * 2;		// 矩形的長度(X軸) originalTileSize*scale(3) - (x*2)(左右各一份) = originalTileSize*2
		solidArea.height = gp.originalTileSize * 2 ;	// 矩形的高度(Y軸) originalTileSize*scale(3) - (y)(上面一份) = originalTileSize*2
		
		setDefaultValues();
		getPlayerImage();
	}
	// set default values 設定預設數值
	public void setDefaultValues() {
		
		worldX = gp.tileSize * 23;
		worldY = gp.tileSize * 21;
		speed = 4;
		direction = "down";
		
		// PLAYER STAUTS
		maxLife = 6;
		life = maxLife;
	}
	// 匯入 圖片
	public void getPlayerImage() {
		
		up1 = setup("player/boy_up_1");
		up2 = setup("player/boy_up_2");
		down1 = setup("player/boy_down_1");
		down2 = setup("player/boy_down_2");
		left1 = setup("player/boy_left_1");
		left2 = setup("player/boy_left_2");
		right1 = setup("player/boy_right_1");
		right2 = setup("player/boy_right_2");

	}
	
	@Override
	public void update() {
		
		// 當有鍵盤輸入的時候
		if(keyH.upPressed == true || keyH.downPressed == true ||
				keyH.leftPressed == true || keyH.rightPressed == true) {
			
			if(keyH.upPressed == true) {
				direction = "up"; 					// direction 用於和 draw()中 switch case 判定 有對應的方向							 
			}else if (keyH.downPressed == true) {
				direction = "down";				
			}else if (keyH.leftPressed == true) {
				direction = "left";
			}else if (keyH.rightPressed == true) {
				direction = "right";				
			}
			
			// CHECK TILE COLLISION
			collisionOn = false; 			// 初始化  
			gp.cChecker.checkTile(this);	// 進行檢測
						
			// CHECK OBJECT COLLISION
			int objIndex = gp.cChecker.checkObject(this, true);
			pickUpObject(objIndex);
			
			// CHECK NPC COLLISION
			int npcIndex = gp.cChecker.checkEntity(this, gp.npc);
			interactNPC(npcIndex);
			
			// IF COLLISION IS FALSE, PLAYER CAN MOVE
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
				} else if (spriteNum == 2) {
					spriteNum = 1;
				}
				spriteCounter = 0;
			}
		}
		// 當沒有鍵盤輸入的時候
		else {
			standCounter++;
			if (standCounter == 20) {
				spriteNum = 1;
				standCounter = 0;
			}
		}		
	}
	
	// 遇到 / 撿起 Object 的行動
	public void pickUpObject(int i) {
		
		if (i != 999) {
			
			String objectName = gp.obj[i].name; 	// 取得 object 的 name
			
		}
	}
	
	//
	public void interactNPC(int i) {
		
		if (i != 999) {
			if (gp.keyH.enterPressed == true) {
				gp.gameState = gp.dialogueState;
				gp.npc[i].speak();
			}
		}
		gp.keyH.enterPressed = false;
	}
	
	@Override
	public void draw(Graphics2D g2) {
		
		// super.draw(g2);
		
/*		原本 產生 白色矩形
		g2.setColor(Color.white);		
		g2.fillRect(x, y, gp.tileSize, gp.tileSize); //繪製一個填充的矩形 矩形左上角的 x座標 y座標 矩形長度 寬度
*/
		
		BufferedImage image = null;  // 建立變數 預設 null 防止圖片匯入失敗
		
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
		// 使用 Graphics2D 物件 g2 在指定的座標和尺寸繪製一個圖像 (物件, x座標, y座標, 圖像寬度(省略 被整合setup), 圖像高度(省略 被整合setup), 圖像觀察者(null表示不需要) )
		g2.drawImage(image, screenX, screenY, null);
		
		// 顯示 碰撞區域 的 紅色外框
		// g2.setColor(Color.red);
		// g2.drawRect(screenX + solidArea.x, screenY + solidArea.y, solidArea.width, solidArea.height);
	}
	
	
}
