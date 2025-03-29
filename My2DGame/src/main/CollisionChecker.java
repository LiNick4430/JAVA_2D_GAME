package main;

import entity.Entity;

public class CollisionChecker {
	
	GamePanel gp;

	public CollisionChecker(GamePanel gp) {
		this.gp = gp;
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
				tileNum1 = gp.tileM.mapTileNum[entityLeftCol][entityTopRow];
				tileNum2 = gp.tileM.mapTileNum[entityRightCol][entityTopRow];
				// 檢測 Num1 或者 Num2 是否 碰撞 
				if(gp.tileM.tile[tileNum1].collision == true || gp.tileM.tile[tileNum2].collision == true) {
					entity.collisionOn = true;
				}
				break;
			case "down":
				entityBottomRow = (entityBottomWorldY + entity.speed) / gp.tileSize;
				tileNum1 = gp.tileM.mapTileNum[entityLeftCol][entityBottomRow];
				tileNum2 = gp.tileM.mapTileNum[entityRightCol][entityBottomRow];
				if(gp.tileM.tile[tileNum1].collision == true || gp.tileM.tile[tileNum2].collision == true) {
					entity.collisionOn = true;
				}
				break;
			case "left":
				entityLeftCol = (entityLeftWorldX - entity.speed) / gp.tileSize;
				tileNum1 = gp.tileM.mapTileNum[entityLeftCol][entityTopRow];
				tileNum2 = gp.tileM.mapTileNum[entityLeftCol][entityBottomRow];
				if(gp.tileM.tile[tileNum1].collision == true || gp.tileM.tile[tileNum2].collision == true) {
					entity.collisionOn = true;
				}
				break;
			case "right":
				entityRightCol = (entityRightWorldX + entity.speed) / gp.tileSize;
				tileNum1 = gp.tileM.mapTileNum[entityRightCol][entityTopRow];
				tileNum2 = gp.tileM.mapTileNum[entityRightCol][entityBottomRow];
				if(gp.tileM.tile[tileNum1].collision == true || gp.tileM.tile[tileNum2].collision == true) {
					entity.collisionOn = true;
				}
				break;
		}				
	}
	
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
						if (entity.solidArea.intersects(gp.obj[i].solidArea)) { // 當下一動的位置 會和 Object 的位置 發生碰撞
							// System.out.println("up! collision!"); 			   測試運行用
							if (gp.obj[i].collision == true)  {			// 當 Object 的 collision 為 true
								entity.collisionOn = true; 				// collisionOn 回傳 true  無法移動
							}
							if (player == true) { 						// 當 Object 碰觸到 東西
								index = i;								// 回傳 Object index
							}
						} 
						break;
					case "down":
						entity.solidArea.y += entity.speed;
						if (entity.solidArea.intersects(gp.obj[i].solidArea)) {
							// System.out.println("down! collision!"); 測試運行用
							if (gp.obj[i].collision == true)  {
								entity.collisionOn = true;
							}
							if (player == true) {
								index = i;
							}
						}
						break;
					case "left":
						entity.solidArea.x -= entity.speed;
						if (entity.solidArea.intersects(gp.obj[i].solidArea)) {
							// System.out.println("left! collision!"); 測試運行用
							if (gp.obj[i].collision == true)  {
								entity.collisionOn = true;
							}
							if (player == true) {
								index = i;
							}
						}
						break;
					case "right":
						entity.solidArea.x += entity.speed;
						if (entity.solidArea.intersects(gp.obj[i].solidArea)) {
							// System.out.println("right! collision!"); 測試運行用
							if (gp.obj[i].collision == true)  {
								entity.collisionOn = true;
							}
							if (player == true) {
								index = i;
							}
						}
						break;
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
	
	// 檢查 Player 和 Entity(NPC or Monster) 之間的碰撞
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
						if (entity.solidArea.intersects(target[i].solidArea)) { // 當下一動的位置 會和 Object 的位置 發生碰撞
							// System.out.println("up! collision!"); 			   測試運行用
							entity.collisionOn = true; 				// collisionOn 回傳 true  無法移動
							index = i;								// 回傳 Object index	
						} 
						break;
					case "down":
						entity.solidArea.y += entity.speed;
						if (entity.solidArea.intersects(target[i].solidArea)) {
							entity.collisionOn = true;
							index = i;
						}
						break;
					case "left":
						entity.solidArea.x -= entity.speed;
						if (entity.solidArea.intersects(target[i].solidArea)) {
							entity.collisionOn = true;
							index = i;
						}
						break;
					case "right":
						entity.solidArea.x += entity.speed;
						if (entity.solidArea.intersects(target[i].solidArea)) {
							entity.collisionOn = true;
							index = i;
						}
						break;
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
	public void checkPlayer(Entity entity) {
		
		// Get entity's solid area position 
		entity.solidArea.x = entity.worldX + entity.solidArea.x;
		entity.solidArea.y = entity.worldY + entity.solidArea.y;
		// Get the Player solid area position
		gp.player.solidArea.x = gp.player.worldX + gp.player.solidArea.x;
		gp.player.solidArea.y = gp.player.worldY + gp.player.solidArea.y;

		switch(entity.direction) {
			case "up":
				entity.solidArea.y -= entity.speed;						// 下一動 會移動到的位置
				if (entity.solidArea.intersects(gp.player.solidArea)) { // 當下一動的位置 會和 Object 的位置 發生碰撞
					// System.out.println("up! collision!"); 			   測試運行用
					entity.collisionOn = true; 				// collisionOn 回傳 true  無法移動
				} 
				break;
			case "down":
				entity.solidArea.y += entity.speed;
				if (entity.solidArea.intersects(gp.player.solidArea)) {
					entity.collisionOn = true;
				}
				break;
			case "left":
				entity.solidArea.x -= entity.speed;
				if (entity.solidArea.intersects(gp.player.solidArea)) {
					entity.collisionOn = true;
				}
				break;
			case "right":
				entity.solidArea.x += entity.speed;
				if (entity.solidArea.intersects(gp.player.solidArea)) {
					entity.collisionOn = true;
				}
				break;
		}
		// 將 solidArea 回復成原本的數值
		entity.solidArea.x = entity.solidAreaDefaultX;
		entity.solidArea.y = entity.solidAreaDefaultY;
		gp.player.solidArea.x = gp.player.solidAreaDefaultX;
		gp.player.solidArea.y = gp.player.solidAreaDefaultY;
	}
}
