package main;

import java.awt.Rectangle;

import entity.Entity;

public class CollisionChecker {
	
	GamePanel gp;

	public CollisionChecker(GamePanel gp) {
		this.gp = gp;
	}
	
	// 封裝了計算實體下一位置的碰撞區域，並檢查是否與目標區域相交的邏輯。 接受要檢查碰撞的 Entity 和目標的 Rectangle 作為參數。
	private boolean checkCollision(Entity entity, Rectangle targetArea) {
		
		// 根據實體的 direction 和 speed 計算實體移動後的碰撞區域 (entityNextArea)。
        Rectangle entityNextArea = new Rectangle(
                entity.worldX + entity.solidArea.x,
                entity.worldY + entity.solidArea.y,
                entity.solidArea.width,
                entity.solidArea.height
        		);

        switch (entity.direction) {
            case "up":
                entityNextArea.y -= entity.speed;
                break;
            case "down":
                entityNextArea.y += entity.speed;
                break;
            case "left":
                entityNextArea.x -= entity.speed;
                break;
            case "right":
                entityNextArea.x += entity.speed;
                break;
        }
        
        // 返回 entityNextArea 是否與 targetArea 相交的布林值。
        return entityNextArea.intersects(targetArea);
    }
	
	// 檢查 Entity 和 Tile 之間的碰撞
	public void checkTile(Entity entity) {
		
		// 碰撞 邊緣的 X軸 Y軸
		int entityLeftWorldX = entity.worldX + entity.solidArea.x;
		int entityRightWorldX = entity.worldX + entity.solidArea.x + entity.solidArea.width;		
		int entityTopWorldY = entity.worldY + entity.solidArea.y;
		int entityBottomWorldY = entity.worldY + entity.solidArea.y + entity.solidArea.height;
		// 碰撞邊緣的 COL ROW
		int entityLeftCol = entityLeftWorldX / gp.tileSize;
		int entityRightCol = entityRightWorldX / gp.tileSize;		
		int entityTopRow = entityTopWorldY / gp.tileSize;
		int entityBottomRow = entityBottomWorldY / gp.tileSize;
		// 碰撞 的 tile Num
		int tileNum1, tileNum2;
		
		switch(entity.direction) {
			case "up":
				entityTopRow = (entityTopWorldY - entity.speed) / gp.tileSize;	// Y 移動後 的 ROW
				tileNum1 = gp.tileM.mapTileNum[gp.currentMap][entityLeftCol][entityTopRow];
				tileNum2 = gp.tileM.mapTileNum[gp.currentMap][entityRightCol][entityTopRow];
				// 檢測 Num1 或者 Num2 是否 碰撞 
				if(gp.tileM.tile[tileNum1].collision == true || gp.tileM.tile[tileNum2].collision == true) {
					entity.collisionOn = true;
				}
				break;
			case "down":
				entityBottomRow = (entityBottomWorldY + entity.speed) / gp.tileSize;
				tileNum1 = gp.tileM.mapTileNum[gp.currentMap][entityLeftCol][entityBottomRow];
				tileNum2 = gp.tileM.mapTileNum[gp.currentMap][entityRightCol][entityBottomRow];
				if(gp.tileM.tile[tileNum1].collision == true || gp.tileM.tile[tileNum2].collision == true) {
					entity.collisionOn = true;
				}
				break;
			case "left":
				entityLeftCol = (entityLeftWorldX - entity.speed) / gp.tileSize;
				tileNum1 = gp.tileM.mapTileNum[gp.currentMap][entityLeftCol][entityTopRow];
				tileNum2 = gp.tileM.mapTileNum[gp.currentMap][entityLeftCol][entityBottomRow];
				if(gp.tileM.tile[tileNum1].collision == true || gp.tileM.tile[tileNum2].collision == true) {
					entity.collisionOn = true;
				}
				break;
			case "right":
				entityRightCol = (entityRightWorldX + entity.speed) / gp.tileSize;
				tileNum1 = gp.tileM.mapTileNum[gp.currentMap][entityRightCol][entityTopRow];
				tileNum2 = gp.tileM.mapTileNum[gp.currentMap][entityRightCol][entityBottomRow];
				if(gp.tileM.tile[tileNum1].collision == true || gp.tileM.tile[tileNum2].collision == true) {
					entity.collisionOn = true;
				}
				break;
		}
	}
	
	// 檢查 Entity 和 Object 之間的碰撞
	public int checkObject(Entity entity, boolean player) {
        int index = 999;
        /* Entity 原本的 碰撞區域
        Rectangle entityArea = new Rectangle(entity.worldX + entity.solidArea.x, 
        									entity.worldY + entity.solidArea.y, 
        									entity.solidArea.width, 
        									entity.solidArea.height); */
        
        for (int i = 0; i < gp.obj[gp.currentMap].length; i++) {
            if (gp.obj[gp.currentMap][i] != null) {
            	// Object 的 碰撞區域
                Rectangle objectArea = new Rectangle(gp.obj[gp.currentMap][i].worldX + gp.obj[gp.currentMap][i].solidArea.x, 
                									gp.obj[gp.currentMap][i].worldY + gp.obj[gp.currentMap][i].solidArea.y, 
                									gp.obj[gp.currentMap][i].solidArea.width, 
                									gp.obj[gp.currentMap][i].solidArea.height);

                if (checkCollision(entity, objectArea)) {
                    if (gp.obj[gp.currentMap][i].collision) {
                        entity.collisionOn = true;
                    }
                    if (player) {
                        index = i;
                    }
                }
            }
        }

        // Restore solid area 恢復了實體和所有物件的 solidArea 到其預設位置
        entity.solidArea.x = entity.solidAreaDefaultX;
        entity.solidArea.y = entity.solidAreaDefaultY;
        for (Entity obj : gp.obj[1]) {
            if (obj != null) {
                obj.solidArea.x = obj.solidAreaDefaultX;
                obj.solidArea.y = obj.solidAreaDefaultY;
            }
        }

        return index;
    }
	
	// NPC OR MONSTER
	public int checkEntity(Entity entity, Entity[][] target) {
		
		int index = 999;	// 預設值 用於 沒有撞到 Entity 的時候
		/* Entity 原本的 碰撞區域
		Rectangle entityArea = new Rectangle(entity.worldX + entity.solidArea.x, 
											entity.worldY + entity.solidArea.y, 
											entity.solidArea.width, 
											entity.solidArea.height); */

		
		for (int i = 0; i < target[gp.currentMap].length; i++) {
            if (target[gp.currentMap][i] != null && target[gp.currentMap][i] != entity) {
            	// target 的 碰撞區域
                Rectangle targetArea = new Rectangle(target[gp.currentMap][i].worldX + target[gp.currentMap][i].solidArea.x, 
                									target[gp.currentMap][i].worldY + target[gp.currentMap][i].solidArea.y, 
                									target[gp.currentMap][i].solidArea.width, 
                									target[gp.currentMap][i].solidArea.height);

                if (checkCollision(entity, targetArea)) {
                    entity.collisionOn = true;
                    index = i;
                }
            }
        }

        // Restore solid area 恢復了實體和所有目標實體的 solidArea 到其預設位置。
        entity.solidArea.x = entity.solidAreaDefaultX;
        entity.solidArea.y = entity.solidAreaDefaultY;
        for (Entity targetEntity : target[1]) {
            if (targetEntity != null) {
                targetEntity.solidArea.x = targetEntity.solidAreaDefaultX;
                targetEntity.solidArea.y = targetEntity.solidAreaDefaultY;
            }
        }

        return index;
    }
	
	// 檢查 Entity(NPC or Monster) 和 Player 之間的碰撞
	public boolean checkPlayer(Entity entity) {
		/* Entity 原本的 碰撞區域
        Rectangle entityArea = new Rectangle(entity.worldX + entity.solidArea.x, 
        									entity.worldY + entity.solidArea.y, 
        									entity.solidArea.width, 
        									entity.solidArea.height); */
        
        Rectangle playerArea = new Rectangle(gp.player.worldX + gp.player.solidArea.x, 
        									gp.player.worldY + gp.player.solidArea.y, 
        									gp.player.solidArea.width, 
        									gp.player.solidArea.height);

        boolean contactPlayer = checkCollision(entity, playerArea);	// 是否碰撞 回傳 布林值
        if (contactPlayer) {
            entity.collisionOn = true;
        }

        // Restore solid area 恢復了實體和玩家的 solidArea 到其預設位置。
        entity.solidArea.x = entity.solidAreaDefaultX;
        entity.solidArea.y = entity.solidAreaDefaultY;
        gp.player.solidArea.x = gp.player.solidAreaDefaultX;
        gp.player.solidArea.y = gp.player.solidAreaDefaultY;

        return contactPlayer;
    }
}


/* 儲存 尚未簡化前的 程式碼
	// 檢查 Entity 和 Object 之間的碰撞
	public int checkObject(Entity entity, boolean player) {
		
		int index = 999;	// 預設值 用於 沒有撞到 Object 的時候
		
		for (int i = 0; i < gp.obj.length; i++) {
			
			// 當撞到 Object 的時候
			if (gp.obj[i] != null) {
				
				// Get entity's solid area position 
				entity.solidArea.x = entity.worldX + entity.solidArea.x;
				entity.solidArea.y = entity.worldY + entity.solidArea.y;
				// Get the object's solid area position
				gp.obj[i].solidArea.x = gp.obj[i].worldX + gp.obj[i].solidArea.x;
				gp.obj[i].solidArea.y = gp.obj[i].worldY + gp.obj[i].solidArea.y;
	
				switch(entity.direction) {
					case "up":
						entity.solidArea.y -= entity.speed;						// 下一動 會移動到的位置
						break;
					case "down":
						entity.solidArea.y += entity.speed;
						break;
					case "left":
						entity.solidArea.x -= entity.speed;
						break;
					case "right":
						entity.solidArea.x += entity.speed;
						break;
				}
				
				if (entity.solidArea.intersects(gp.obj[i].solidArea)) { // 當下一動的位置 會和 Object 的位置 發生碰撞
					// System.out.println("up! collision!"); 			   測試運行用
					if (gp.obj[i].collision == true)  {			// 當 Object 的 collision 為 true
						entity.collisionOn = true; 				// collisionOn 回傳 true  無法移動
					}
					if (player == true) { 						// 當 PLAYER 碰觸到 Object
						index = i;								// 回傳 Object index
					}
				} 
				
				// 將 solidArea 回復成原本的數值
				entity.solidArea.x = entity.solidAreaDefaultX;
				entity.solidArea.y = entity.solidAreaDefaultY;
				gp.obj[i].solidArea.x = gp.obj[i].solidAreaDefaultX;
				gp.obj[i].solidArea.y = gp.obj[i].solidAreaDefaultY;
			}
		}
		
		return index;
	}

	// NPC OR MONSTER
	public int checkEntity(Entity entity, Entity[] target) {
		
		int index = 999;	// 預設值 用於 沒有撞到 Entity 的時候
		
		for (int i = 0; i < target.length; i++) {
			
			// 當撞到 Entity 的時候
			if (target[i] != null) {
				
				// Get entity's solid area position 
				entity.solidArea.x = entity.worldX + entity.solidArea.x;
				entity.solidArea.y = entity.worldY + entity.solidArea.y;
				// Get the target's solid area position
				target[i].solidArea.x = target[i].worldX + target[i].solidArea.x;
				target[i].solidArea.y = target[i].worldY + target[i].solidArea.y;

				switch(entity.direction) {
					case "up":
						entity.solidArea.y -= entity.speed;						// 下一動 會移動到的位置
						break;
					case "down":
						entity.solidArea.y += entity.speed;
						break;
					case "left":
						entity.solidArea.x -= entity.speed;
						break;
					case "right":
						entity.solidArea.x += entity.speed;
						break;
				}
				
				if (entity.solidArea.intersects(target[i].solidArea)) { 
					if (target[i] != entity) {
						entity.collisionOn = true; 				// collisionOn 回傳 true  無法移動
						index = i;	
					}												
				} 
				
				// 將 solidArea 回復成原本的數值
				entity.solidArea.x = entity.solidAreaDefaultX;
				entity.solidArea.y = entity.solidAreaDefaultY;
				target[i].solidArea.x = target[i].solidAreaDefaultX;
				target[i].solidArea.y = target[i].solidAreaDefaultY;
			}
		}
		
		return index;
	}
	
	// 檢查 Entity(NPC or Monster) 和 Player 之間的碰撞
	public boolean checkPlayer(Entity entity) {
		
		boolean contactPlayer = false;
		
		// Get entity's solid area position 
		entity.solidArea.x = entity.worldX + entity.solidArea.x;
		entity.solidArea.y = entity.worldY + entity.solidArea.y;
		// Get the Player solid area position
		gp.player.solidArea.x = gp.player.worldX + gp.player.solidArea.x;
		gp.player.solidArea.y = gp.player.worldY + gp.player.solidArea.y;

		switch(entity.direction) {
			case "up":
				entity.solidArea.y -= entity.speed;	
				break;
			case "down":
				entity.solidArea.y += entity.speed;
				break;
			case "left":
				entity.solidArea.x -= entity.speed;
				break;
			case "right":
				entity.solidArea.x += entity.speed;
				break;
		}
		
		if (entity.solidArea.intersects(gp.player.solidArea)) {
			entity.collisionOn = true; 				// collisionOn 回傳 true  無法移動
			contactPlayer = true;
		} 
		
		// 將 solidArea 回復成原本的數值
		entity.solidArea.x = entity.solidAreaDefaultX;
		entity.solidArea.y = entity.solidAreaDefaultY;
		gp.player.solidArea.x = gp.player.solidAreaDefaultX;
		gp.player.solidArea.y = gp.player.solidAreaDefaultY;
		
		return contactPlayer;
	}
*/
