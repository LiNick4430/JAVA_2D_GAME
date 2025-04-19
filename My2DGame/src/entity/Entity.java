package entity;

import java.awt.AlphaComposite;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.ArrayList;

import javax.imageio.ImageIO;

import main.GamePanel;
import main.UtilityTool;

// 實體(Entity) 儲存變數 用在 player, monster, NPC classes, OBJ 等 任何 會 繪畫的 非背景 物件
public abstract class Entity {
	
	GamePanel gp;	
	//	Image
	public BufferedImage up1, up2, down1, down2, left1, left2, right1, right2; 	// 行走動畫 繪畫於 世界座標 上
	public BufferedImage attackUp1, attackUp2, attackDown1, attackDown2, 
						attackLeft1, attackLeft2, attackRight1, attackRight2; 	// 攻擊動畫 繪畫於 世界座標 上
	public BufferedImage image, image2, image3;									// 圖片 繪畫於 UI 上
	// solidArea
	public Rectangle solidArea = new Rectangle(0, 0, 48, 48);	// 用於表示一個矩形區域。  它通常用於表示物件的邊界框或碰撞區域。
	public Rectangle attackArea = new Rectangle(0, 0, 0, 0);	// 預設 攻擊行為 的碰撞區域
	public int solidAreaDefaultX, solidAreaDefaultY;			// 儲存碰撞區域的預設 X,Y 座標，用於在需要時恢復原始位置。
	public boolean collision = false;							// Entity 預設碰撞	
	// dialogues
	public String[] dialogues = new String[20];						// 儲存 對話 陣列	
	// STATE
	public int worldX, worldY;				// 預設 世界位置 X, Y
	public String direction = "down";		// 方向 用於switch case 的對應
	public int spriteNum = 1;				// 決定 圖片序號
	int dialogueIndex = 0;					// 對話 陣列 的 序號
	public boolean collisionOn = false;		// 是否 與其他 Entity/Tile 產生碰撞
	public boolean invincible = false;		// 無敵禎 用於當受到損傷後 產生的短暫無法受傷時間判斷
	public boolean attacking = false;		// 是否 進行 攻擊行動
	public boolean alive = true;			// 是否 生存
	public boolean dying = false;			// 是否 死亡
	boolean hpBarOn = false;				// 是否顯示 生命條	
	// Counter
	public int spriteCounter = 0;			// 用於計算 update 計數器
	public int attackCounter = 0;			// 攻擊行動 計數器
	public int actionLockCounter = 0;		// 行動邏輯 計數器
	public int invincibleCounter = 0;		// 無敵禎 計數器
	public int shotAvailableCounter = 0;	// 射擊 計數器
	int dyingCounter = 0;					// 死亡 計數器
	int hpBarCounter = 0;					// 血條 計數器
	// CHARCTER ATTIBUTES
	public String name;		// Entity 名稱
	public int speed;		// 預設 速度
	public int maxLife;		// 最大生命值
	public int life ;		// 現有生命值
	public int maxMana;		
	public int mana ;
	public int ammo;
	public int level;
	public int strength;
	public int dexterity;
	public int attack;
	public int defense;
	public int exp;
	public int nextLevelExp;
	public int coin;
	public Entity currentWeapon;
	public Entity currentShield;
	public Projectile projectile;	
	// ITEM ATTIBUTES
	public ArrayList<Entity> inventory = new ArrayList<>();
	public final int maxInventorySize = 20;
	public int value;
	public int attackValue;
	public int defenseValue;
	public String description = "";
	public int useCost;	
	public int price;
	// TYPE
	public int type;		
	public final int type_player = 0;
	public final int type_npc = 1;
	public final int type_monster = 2;
	public final int type_sword = 3;
	public final int type_axe = 4;
	public final int type_shied = 5;
	public final int type_consumable = 6;
	public final int type_pickupOnly = 7;
	
	
	public Entity(GamePanel gp) {
		this.gp = gp;
	}
	
	public void setAction() {}			// 預設 空 setAction(行動邏輯)
	public void damageReaction() {}
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
	public void use(Entity entity) {}
	public void checkDrop() {}
	public void dropItem(Entity dropprdItem) {
		
		for (int i = 0; i < gp.obj[gp.currentMap].length; i++) {
			if (gp.obj[gp.currentMap][i] == null) {
				gp.obj[gp.currentMap][i] = dropprdItem;
				gp.obj[gp.currentMap][i].worldX = worldX;	// THE DEAD MONSTER WORLDX
				gp.obj[gp.currentMap][i].worldY = worldY;	// THE DEAD MONSTER WORLDY
				break;
			}
		}
	}
	
	// monster 攻擊 玩家 計算傷害
	public void damagePlayer(int attack) {
		
		if (gp.player.invincible == false) {
			// we can give damage
			gp.playSE(6);
			
			int damage = attack - gp.player.defense;
			if (damage < 0) {
				damage = 0;
			}
			
			gp.player.life -= damage;
			gp.player.invincible = true;
		}
		
	}
	// GETTER Particle's Color, Size, Speed, MaxLife
	private Color getParticleColor() {
		Color color = null;
		return color;
	}
	private int getParticleSize() {
		int size = 0;		// PIXELS
		return size;
	}
	private int getParticleSpeed() {
		int speed = 0;
		return speed;
	}
	private int getParticleMaxLife() {
		int maxLife = 0;
		return maxLife;
	}
	// 生成 爆炸特效
	public void generateParticle(Entity generator, Entity target) {

		Color color = generator.getParticleColor();
		int size = generator.getParticleSize();
		int speed = generator.getParticleSpeed();
		int maxLife = generator.getParticleMaxLife();
		
		Particle p1 = new Particle(gp, target, color, size, speed, maxLife, -2, -1);
		Particle p2 = new Particle(gp, target, color, size, speed, maxLife, 2, -1);
		Particle p3 = new Particle(gp, target, color, size, speed, maxLife, -2, 1);
		Particle p4 = new Particle(gp, target, color, size, speed, maxLife, 2, 1);
		gp.particleList.add(p1);
		gp.particleList.add(p2);
		gp.particleList.add(p3);
		gp.particleList.add(p4);
		
	}
	// 進行更新
	public void update() {
		
		// 透過行動邏輯 
		setAction();
		
		// 碰撞邏輯檢測
		collisionOn = false; 						// 初始化  
		gp.cChecker.checkTile(this);				// 對 Tile, 獲得 entity.collisionOn 
		gp.cChecker.checkObject(this, false);		// 對 Object, 獲得 entity.collisionOn
		gp.cChecker.checkEntity(this, gp.npc);		// 對 NPC, 獲得 entity.collisionOn
		gp.cChecker.checkEntity(this, gp.monster);	// 對 monster, 獲得 entity.collisionOn
		gp.cChecker.checkEntity(this, gp.iTile);
		boolean contactPlayer =  gp.cChecker.checkPlayer(this); // 對 Player, 獲得 entity.collisionOn 與 contactPlayer
		
		// 如果是 type = type_monster 且 碰撞到玩家
		if (this.type == type_monster && contactPlayer == true) {
			damagePlayer(attack);
		}
		
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
		if(spriteCounter > 24) { 
			if(spriteNum == 1) {
				spriteNum = 2;
			} else if(spriteNum == 2) {
				spriteNum = 1;
			}
			spriteCounter = 0;
		}
		
		// This needs to be outside of key if statement!
		// 無敵禎的判定
		if (invincible == true) {
			invincibleCounter++;
			if (invincibleCounter > 40) {
				invincible = false;
				invincibleCounter = 0;
			}
		}
		// 計算 shotAvailableCounter 滿的時候 可以使用 
		if (shotAvailableCounter < 30) {
			shotAvailableCounter++;
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
			// Monster HP bar
			if (type == type_monster && hpBarOn == true) {
				
				double oneScale = (double)gp.tileSize/maxLife;
				double hpBarValue = oneScale * life;
					
				g2.setColor(new Color(35, 35, 35));
				g2.fillRect(screenX - 1 , screenY - 16, gp.tileSize +2 , 12);
					
				g2.setColor(new Color(255, 0, 20));
				g2.fillRect(screenX, screenY - 15, (int)hpBarValue, 10);
				
				hpBarCounter++;
				
				if (hpBarCounter > 600) {
					hpBarCounter = 0;
					hpBarOn = false;
				}
				
			}
			
			// 當受到損傷 產生無敵禎時 使 Entity 變成半透明
			if (invincible == true) {
				hpBarOn = true;
				hpBarCounter = 0;
				changeAlpha(g2,0.4f);			
			}
			
			// 死亡動畫
			if (dying == true) {
				hpBarOn = false;
				solidArea.width = 0;
				solidArea.height = 0;
				dyingAnimation(g2);				
			}
			
			g2.drawImage(image, screenX, screenY, null);
			// Reset alpha
			changeAlpha(g2,1f);
		}
	}
	
	// 死亡動畫
	public void dyingAnimation(Graphics2D g2) {
		
		int sec = 5;
		dyingCounter++;
		
		if (dyingCounter <= sec*8) {
		    if (dyingCounter % sec*2 < sec) {
		        changeAlpha(g2, 0f);
		    } else {
		        changeAlpha(g2, 1f);
		    }
		}else if (dyingCounter > sec*8) {
			alive = false;
		}
	}
	
	// 改變 圖片 透明度
	public void changeAlpha(Graphics2D g2, float alphaValue) {
		g2.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, alphaValue));
	}	
	
	// 圖片 讀取 並 同時放大 成 width * height
	private BufferedImage setupImage(String imagePath, int width, int height) {
		UtilityTool uTool = new UtilityTool();
		BufferedImage image = null;
		
		try {
			image = ImageIO.read(getClass().getClassLoader().getResourceAsStream(imagePath + ".png"));
			image = uTool.scaleImage(image, width, height); 
			
		} catch (IOException e) {
			e.printStackTrace(); 	
		}
		return image; // 輸出堆疊追蹤
	}
	// 圖片 讀取 並 放大到 特定大小
	public BufferedImage setup(String imagePath, int width, int height) {
		return setupImage(imagePath, width, height); 
	}
	// 圖片 讀取 並 放大到　預設大小 (gp.tileSize*gp.tileSize)　
	public BufferedImage setup(String imagePath) {
		return setupImage(imagePath, gp.tileSize, gp.tileSize); 
	}
}
