package entity.player;

import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.image.BufferedImage;
import java.util.ArrayList;

import entity.Entity;
import entity.object.OBJ_Fireball;
import entity.object.OBJ_Key;
import entity.object.OBJ_Shield_Wood;
import entity.object.OBJ_Sword_Normal;
import main.GamePanel;
import main.KeyHandler;

public class Player extends Entity {
	
	GamePanel gp;
	KeyHandler keyH;
	
	public final int screenX;	// 畫面 X
	public final int screenY;	// 畫面 Y
	int standCounter = 0;		// 計數 無輸入的 禎數 
	public boolean attackCanceled = false;	
	
	public Player(GamePanel gp, KeyHandler keyH) {
		
		super(gp);
		this.gp = gp;
		
		this.keyH = keyH;
		
		screenX = gp.screenWidth/2 - (gp.tileSize/2);	// 畫面X 在 畫面X軸距離一半, 因為繪圖是依照圖的左上角 還要再往回 半格的距離 
		screenY = gp.screenHeight/2 - (gp.tileSize/2);	// 畫面Y 在 畫面Y軸距離一半, 因為繪圖是依照圖的左上角 還要再往回 半格的距離
		
//		solidArea = new Rectangle (8, 16, 32, 32);
		solidArea = new Rectangle ();	// 建立一個 矩形
		solidArea.x = 8;					// 矩形的X  X0 是這個Entity 的左上角 用originalTileSize(16*16) 當基準
		solidArea.y = 16;					// 矩形的Y  Y0 是這個Entity 的左上角
		solidAreaDefaultX = solidArea.x;	// 儲存碰撞區域的預設 X 座標，用於在需要時恢復原始位置。
		solidAreaDefaultY = solidArea.y;	// 儲存碰撞區域的預設 Y 座標，用於在需要時恢復原始位置。
		solidArea.width = 32;				// 矩形的長度(X軸) originalTileSize*scale(3) - (x*2)(左右各一份) = originalTileSize*2
		solidArea.height = 32;				// 矩形的高度(Y軸) originalTileSize*scale(3) - (y)(上面一份) = originalTileSize*2
		
		setDefaultValues();
		getPlayerImage();
		getPlayerAttackImage();
		setItems();
	}
	// set default values 設定預設數值
	public void setDefaultValues() {
		
		worldX = gp.tileSize * 23;
		worldY = gp.tileSize * 21;
		speed = 4;
		direction = "down";
		
		// PLAYER STAUTS
		level = 1;
		maxLife = 6;
		life = maxLife;
		maxMana = 4;
		mana = maxMana;
		strength = 1;			// The more strength he has, the more damage he gives.
		dexterity = 1;			// The more dexterity he has, the less damage he receives.
		exp = 0;
		nextLevelExp = 5;
		coin = 0;
		currentWeapon = new OBJ_Sword_Normal(gp);
		currentShield = new OBJ_Shield_Wood(gp);
		projectile = new OBJ_Fireball(gp);
		attack = getAttack();	// The total attack value is decided by strength and weapon.
		defense = getDefense();	// The total defense value is decided by dexterity and shield.
	}
	// Set Default Positions
	public void setDefaultPositions() {
		
		worldX = gp.tileSize * 23;
		worldY = gp.tileSize * 21;
		direction = "down";
	}
	// Restore Life And Mana
	public void restoreLifeAndMana() {
		
		life = maxLife;
		mana = maxMana;
		invincible = false;
	}
	
	// 設置 初始道具
	public void setItems() {
		
		inventory.clear();
		inventory.add(currentWeapon);
		inventory.add(currentShield);
		inventory.add(new OBJ_Key(gp));
		}
	// 獲得 ATTACK 同時檢查 攻擊範圍(attackArea)
	public int getAttack() {
		attackArea = currentWeapon.attackArea;
		return attack = strength * currentWeapon.attackValue;
	}
	// 獲得 DEFENSE
	public int getDefense() {
		return defense = dexterity * currentShield.defenseValue;
	}
	
	// 獲得 行走 圖片
	public void getPlayerImage() {
		// 放大 到固定大小 gp.tileSize gp.tileSize 
		up1 = setup("player/boy_up_1");
		up2 = setup("player/boy_up_2");
		down1 = setup("player/boy_down_1");
		down2 = setup("player/boy_down_2");
		left1 = setup("player/boy_left_1");
		left2 = setup("player/boy_left_2");
		right1 = setup("player/boy_right_1");
		right2 = setup("player/boy_right_2");
	}
	// 根據不同武器 獲得 攻擊圖片
	public void getPlayerAttackImage() {
		
		if (currentWeapon.type == type_sword) {
			attackUp1 = setup("player/boy_attack_up_1", gp.tileSize, gp.tileSize*2);
			attackUp2 = setup("player/boy_attack_up_2", gp.tileSize, gp.tileSize*2);
			attackDown1 = setup("player/boy_attack_down_1", gp.tileSize, gp.tileSize*2);
			attackDown2 = setup("player/boy_attack_down_2", gp.tileSize, gp.tileSize*2);
			attackLeft1 = setup("player/boy_attack_left_1", gp.tileSize*2, gp.tileSize);
			attackLeft2 = setup("player/boy_attack_left_2", gp.tileSize*2, gp.tileSize);
			attackRight1 = setup("player/boy_attack_right_1", gp.tileSize*2, gp.tileSize);
			attackRight2 = setup("player/boy_attack_right_2", gp.tileSize*2, gp.tileSize);
		}else if (currentWeapon.type == type_axe) {
			attackUp1 = setup("player/boy_axe_up_1", gp.tileSize, gp.tileSize*2);
			attackUp2 = setup("player/boy_axe_up_2", gp.tileSize, gp.tileSize*2);
			attackDown1 = setup("player/boy_axe_down_1", gp.tileSize, gp.tileSize*2);
			attackDown2 = setup("player/boy_axe_down_2", gp.tileSize, gp.tileSize*2);
			attackLeft1 = setup("player/boy_axe_left_1", gp.tileSize*2, gp.tileSize);
			attackLeft2 = setup("player/boy_axe_left_2", gp.tileSize*2, gp.tileSize);
			attackRight1 = setup("player/boy_axe_right_1", gp.tileSize*2, gp.tileSize);
			attackRight2 = setup("player/boy_axe_right_2", gp.tileSize*2, gp.tileSize);
		}
		
	}
	
	@Override
	public void update() {
		 
		if (attacking == true) {
			attacking();
		}
		// 當有鍵盤輸入的時候
		else if(keyH.upPressed == true || keyH.downPressed == true ||
				keyH.leftPressed == true || keyH.rightPressed == true || keyH.enterPressed == true) {
			
			if(keyH.upPressed == true) {
				direction = "up"; 					// direction 用於和 draw()中 switch case 判定 有對應的方向							 
			}else if (keyH.downPressed == true) {
				direction = "down";				
			}else if (keyH.leftPressed == true) {
				direction = "left";
			}else if (keyH.rightPressed == true) {
				direction = "right";				
			}
		
			// COLLISION
			collisionOn = false; 			// 初始化 
			
			// CHECK TILE COLLISION			 
			gp.cChecker.checkTile(this);	// 進行檢測
						
			// CHECK OBJECT COLLISION
			int objIndex = gp.cChecker.checkObject(this, true);
			pickUpObject(objIndex);
			
			// CHECK NPC COLLISION
			int npcIndex = gp.cChecker.checkEntity(this, gp.npc);
			interactNPC(npcIndex);
			
			// CHECK MONSTER COLLISION
			int monsterIndex = gp.cChecker.checkEntity(this, gp.monster);
			contactMonster(monsterIndex);
			
			// CHECK INTERACTIVE TILES COLLISION
			int iTileIndex = gp.cChecker.checkEntity(this, gp.iTile);
			
			// CHECK EVENT
			gp.eHandler.checkEvent();

			// IF COLLISION IS FALSE, PLAYER CAN MOVE
			if (collisionOn == false && keyH.enterPressed == false) {
				
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
			// 當按下 ENTER 且 沒有觸發 其他ENTER事件 的時候 進行攻擊行動
			if (keyH.enterPressed == true && attackCanceled == false) {
				gp.playSE(7);
				attacking = true;
				spriteCounter = 0;
			}
			
			// 當 ENTER 事件結束後 初始化
			/*	interactNPC() 
				gp.eHandler.checkEvent()
			*/
			gp.keyH.enterPressed = false;	// 初始化
			attackCanceled = false;			// 初始化
			
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
		// 不是攻擊行為 且 沒有鍵盤輸入
		else {
			standCounter++;
			if (standCounter == 20) {
				spriteNum = 1;
				standCounter = 0;
			}
		}
		
		if (gp.keyH.shotKeyPressed == true && projectile.alive == false 
				&& shotAvailableCounter == 30 && projectile.haveResource(this) == true) {
			// SET DEFAULT COORDINATES, DESCRIPTION AND USER
			projectile.set(worldX, worldY, direction, true, this);
			// SUBTRACK THE COS (MANA, AMMO ETC.)
			projectile.subtrackResource(this);
			// ADD IT TO THE LIST
			gp.projectileList.add(projectile);
			// RESET shotAvailableCounter
			shotAvailableCounter = 0;
			
			gp.playSE(10);
			
		}
		
		// This needs to be outside of key if statement!
		// 計算 無敵禎
		if (invincible == true) {
			invincibleCounter++;
			if (invincibleCounter > 60) {
				invincible = false;
				invincibleCounter = 0;
			}
		}
		// 計算 shotAvailableCounter 滿的時候 可以使用 
		if (shotAvailableCounter < 30) {
			shotAvailableCounter++;
		}
		// 使 life / mana 不會超過最大值
		if (life > maxLife) {
			life = maxLife;
		}		
		if (mana > maxMana) {
			mana = maxMana;
		}
		// Game Over
		if (life <= 0) {	
			gp.gameState = gp.gameOverState;
			gp.ui.commandNum = -1;
			gp.stopMusic();
			gp.playSE(12);
			
		}
	}
	
	// 攻擊行為
	public void attacking() {
		// attack animation
		attackCounter++;
		
		if (attackCounter <= 5) {
			spriteNum = 1;
		}else if (attackCounter <= 25) {
			spriteNum = 2;	
			attackingCollision(); 	// 進行攻擊行為 的 碰撞檢查		
		}else if (attackCounter > 25) {
			spriteNum = 1;
			attackCounter = 0;
			attacking = false;
		}	
	}
	// 攻擊行為 時 碰撞檢查 
	private void attackingCollision() {
		// save the current worldX, worldY, solidArea
		int currentWorldX = worldX;
		int currentWorldY = worldY;
		int solidAreaWidth = solidArea.width;
		int solidAreaHeight = solidArea.height;
		// Adjust Player's worldX/Y for the attackArea
		switch (direction) {
			case "up":
				worldY -= attackArea.height;
				break;
			case "down":
				worldY += attackArea.height;
				break;
			case "left":
				worldX -= attackArea.width;
				break;
			case "right":
				worldX += attackArea.width;
				break;
		}
		// attackArea become solidArea
		solidArea.width = attackArea.width;
		solidArea.height = attackArea.height;
		// check monster collision with the updated worldX, worldY and solidArea
		int monsterIndex = gp.cChecker.checkEntity(this, gp.monster);
		damageMonster(monsterIndex, attack);
		int iTileIndex = gp.cChecker.checkEntity(this, gp.iTile);
		damageInteractiveTiles(iTileIndex);
		// After checking collision, reset the original data;
		worldX = currentWorldX;
		worldY = currentWorldY;
		solidArea.width = solidAreaWidth;
		solidArea.height = solidAreaHeight;
	}	
	
	// 遇到 / 撿起 Object 的行動
	public void pickUpObject(int i) {
		
		if (i != 999) {
			
			// PICK ONLY ITEMS
			if (gp.obj[gp.currentMap][i].type == type_pickupOnly) {
				
				gp.obj[gp.currentMap][i].use(this);
				gp.obj[gp.currentMap][i] = null;
			}		
			// INVENTORY ITEMS
			else {
				String text;
				
				if (inventory.size() != maxInventorySize) {
					
					inventory.add(gp.obj[gp.currentMap][i]);
					gp.playSE(1);
					text = "Got a " + gp.obj[gp.currentMap][i].name + "!";		
				}else {
					text = "You cannot carry any more.";
				}
				gp.ui.addMessage(text);
				gp.obj[gp.currentMap][i] = null;
			}
		}
	}
	// 遇到 NPC 或 無(i=999) 時的行動
	public void interactNPC(int i) {
		
		if (gp.keyH.enterPressed == true) {
			if (i != 999) {
				attackCanceled = true;
				gp.gameState = gp.dialogueState;
				gp.npc[gp.currentMap][i].speak();
			}
		}		
	}
	// 撞到 Monster 被造成傷害 時
	public void contactMonster(int i) {
		
		if (i != 999) {			
			if (invincible == false && gp.monster[gp.currentMap][i].dying == false) {
				gp.playSE(6);
				
				int damage = gp.monster[gp.currentMap][i].attack - defense;
				if (damage < 0) {
					damage = 0;
				}
				
				life -= damage;
				invincible = true;
			}			
		}
	}
	// 攻擊 Monster 造成傷害 時
	public void damageMonster(int i, int attack) {
		
		if (i != 999) {
			// 當 Monster 不再 無敵禎 狀態
			if (gp.monster[gp.currentMap][i].invincible == false) {
				
				gp.playSE(5);
				
				int damage = attack - gp.monster[gp.currentMap][i].defense;
				if (damage < 0) {
					damage = 0;
				}
				
				gp.monster[gp.currentMap][i].life -= damage;
				gp.ui.addMessage(damage + " damage!");
				gp.monster[gp.currentMap][i].invincible = true;
				gp.monster[gp.currentMap][i].damageReaction();
				// 當 Monster 血量小於 0 
				if (gp.monster[gp.currentMap][i].life <= 0) {
					gp.monster[gp.currentMap][i].dying = true;
					gp.ui.addMessage("Killed the " + gp.monster[gp.currentMap][i].name + "!");
					gp.ui.addMessage("Exp + " + gp.monster[gp.currentMap][i].exp);
					exp += gp.monster[gp.currentMap][i].exp;
					checkLevelUp();
				}
			}
		}
	}
	// 攻擊到 InteractiveTiles 的時候
	public void damageInteractiveTiles(int i) {
		
		if (i != 999 && gp.iTile[gp.currentMap][i].destructible == true &&
				gp.iTile[gp.currentMap][i].isCorrectItem(this) == true && gp.iTile[gp.currentMap][i].invincible == false) {
			
			gp.iTile[gp.currentMap][i].playSE();
			gp.iTile[gp.currentMap][i].life--;
			gp.iTile[gp.currentMap][i].invincible = true;
			
			// GENERATE PARTICLE
			generateParticle(gp.iTile[gp.currentMap][i], gp.iTile[gp.currentMap][i]);
			
			if (gp.iTile[gp.currentMap][i].life == 0) {
				gp.iTile[gp.currentMap][i] = gp.iTile[gp.currentMap][i].getDestroyedForm();
			}
			
		}
	}
	
	// 提升 等級 的方法
	public void checkLevelUp() {
		
		if (exp >= nextLevelExp) {
			
			level++;
			nextLevelExp = nextLevelExp *2;
			maxLife += 2;
			strength++;
			dexterity++;
			attack = getAttack();
			defense = getDefense();
			
			gp.playSE(8);
			gp.gameState = gp.dialogueState;
			gp.ui.currentDialogue = "You are level " + level + " now!\n"
					+ "You feel stranger!";
		}
	}
	// 物品選擇 方法
	public void selectItem() {
		
		int itemIndex = gp.ui.getItemIndexOnSolt(gp.ui.playerSlotCol, gp.ui.playerSlotRow);
		// 檢查 itemIndex 是否小於背包 (inventory) 的大小。 確保玩家選擇的索引是有效的，防止 IndexOutOfBoundsException。
		if (itemIndex < inventory.size()) {
			Entity selectItem = inventory.get(itemIndex);
			
			if (selectItem.type == type_sword || selectItem.type == type_axe) {
				
				currentWeapon = selectItem;
				attack = getAttack();
				getPlayerAttackImage();
			}
			
			if (selectItem.type == type_shied) {	
				
				currentShield = selectItem;
				defense = getDefense();
			}
			
			if (selectItem.type == type_consumable) {
				
				selectItem.use(this);
				inventory.remove(itemIndex);	// 從背包 (inventory) 中移除已使用的消耗品。
			}
		}
	}
	
	@Override
	public void draw(Graphics2D g2) {
		
/*		原本 產生 白色矩形
		g2.setColor(Color.white);		
		g2.fillRect(x, y, gp.tileSize, gp.tileSize); //繪製一個填充的矩形 矩形左上角的 x座標 y座標 矩形長度 寬度
*/
		
		BufferedImage image = null;  // 建立變數 預設 null 防止圖片匯入失敗
		int tempScreenX = screenX; 
		int tempScreenY = screenY;
		
		switch(direction) {
			case "up":
				if (attacking == false) {
					if(spriteNum == 1) { 
						image = up1; 
					}else if(spriteNum == 2) { 
						image = up2; 
					}
				}else if (attacking == true) {
					tempScreenY = screenY - gp.tileSize;
					if(spriteNum == 1) {
						image = attackUp1;
					}else if(spriteNum == 2) {
						image = attackUp2;
					}
				}
				break;
			case "down":
				if (attacking == false) {
					if(spriteNum == 1) {
						image = down1;
					}else if(spriteNum == 2) {
						image = down2;
					}
				}else if (attacking == true) {
					if(spriteNum == 1) {
						image = attackDown1;
					}else if(spriteNum == 2) {
						image = attackDown2;
					}
				}
				break;
			case "left":
				if (attacking == false) {
					if(spriteNum == 1) {
						image = left1;
					}else if(spriteNum == 2) {
						image = left2;
					}
				}else if (attacking == true) {
					tempScreenX = screenX - gp.tileSize;
					if(spriteNum == 1) {
						image = attackLeft1;
					}else if(spriteNum == 2) {
						image = attackLeft2;
					}
				}
				break;
			case "right":
				if (attacking == false) {
					if(spriteNum == 1) {
						image = right1;
					}else if(spriteNum == 2) {
						image = right2;
					}
				}else if (attacking == true) {
					if(spriteNum == 1) {
						image = attackRight1;
					}else if(spriteNum == 2) {
						image = attackRight2;
					}
				}
				break;
		}
		
		// 當受到損傷 產生無敵禎時 會使PLAYER 變成半透明
		if (invincible == true) {
			changeAlpha(g2, 0.3f);
		}
		
		// 使用 Graphics2D 物件 g2 在指定的座標和尺寸繪製一個圖像 (物件, x座標, y座標, 圖像寬度(省略 被整合setup), 圖像高度(省略 被整合setup), 圖像觀察者(null表示不需要) )
		g2.drawImage(image, tempScreenX, tempScreenY, null);
		
		// Reset alpha
		changeAlpha(g2, 1f);
		
		// 顯示 碰撞區域 的 紅色外框
		// g2.setColor(Color.red);
		// g2.drawRect(screenX + solidArea.x, screenY + solidArea.y, solidArea.width, solidArea.height);
		
		// DEBUG
		// g2.setFont(new Font("Arial", Font.PLAIN, 26));
		// g2.setColor(Color.white);
		// g2.drawString("Invincible:" + invincibleCounter, 10, 400);
	}
	
	
}
