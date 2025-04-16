package main;

public class EventHandler {
	
	GamePanel gp;
	EventRect eventRect[][][];
	// 用於判斷事件 需要距離一定距離 才可以再次觸發
	int previousEventX, previousEventY;
	boolean canTouchEvent = true;
	
	public EventHandler(GamePanel gp) {
		this.gp = gp;
		
		// 初始化了一個二維 EventRect 陣列，用於儲存遊戲世界中的事件區域
		eventRect = new EventRect[gp.maxMap][gp.maxWorldCol][gp.maxWorldRow];
		
		int map = 0;
		int col = 0;
		int row = 0;
		while (map < gp.maxMap && col < gp.maxWorldCol && row < gp.maxWorldRow) {
			
			eventRect[map][col][row] = new EventRect();
			eventRect[map][col][row].x = 23;
			eventRect[map][col][row].y = 23;
			eventRect[map][col][row].width = 2;
			eventRect[map][col][row].height = 2;
			eventRect[map][col][row].eventRectDefaultX = eventRect[map][col][row].x;
			eventRect[map][col][row].eventRectDefaultY = eventRect[map][col][row].y;
			
			col++;
			if (col == gp.maxWorldCol) {
				col = 0;
				row++;
				
				if (row == gp.maxWorldRow) {
					row = 0;
					map++;
				}
			}
		}
		
	}
	
	public void checkEvent() {
		// check if the player character is more than 1 tile away from the last event
		int xDistanse = Math.abs(gp.player.worldX - previousEventX);
		int yDistanse = Math.abs(gp.player.worldY - previousEventY);
		int distanse = Math.max(xDistanse, yDistanse);
		if (distanse > gp.tileSize) {
			canTouchEvent = true;
		}
		// 因為會加入很多事件, 縮成一事件一行 
		if (canTouchEvent == true) {		
			// 坑洞陷阱事件
			if (hit(0, 27, 16, "right") == true) { damagePit(gp.dialogueState); }
			// 泉水回復事件
			else if (hit(0, 23, 12, "up") == true) { healingPool(gp.dialogueState); }
			// 移動至其他地圖
			else if (hit(0, 10, 39, "any") == true) { teleport(1, 12, 13); }
			else if (hit(1, 12, 13, "any") == true) { teleport(0, 10, 39); }
		}

	}
	
	public boolean hit(int map, int col, int row, String reqDirection) {
		
		boolean hit = false;
		
		if (map == gp.currentMap) {
			// get play's current solidArea position 
			gp.player.solidArea.x = gp.player.worldX + gp.player.solidArea.x;
			gp.player.solidArea.y = gp.player.worldY + gp.player.solidArea.y;
			// get eventRect's solidArea position 
			eventRect[map][col][row].x = col * gp.tileSize + eventRect[map][col][row].x;
			eventRect[map][col][row].y = row * gp.tileSize + eventRect[map][col][row].y;
			// checking if player's solidArea is colliding with eventRect's solidArea
			if (gp.player.solidArea.intersects(eventRect[map][col][row]) && eventRect[map][col][row].eventDone == false ) { // 是否碰撞 與 是否觸發過
				if (gp.player.direction.contentEquals(reqDirection) || reqDirection.contentEquals("any")) {	// 是否方向一致
					hit = true;
					
					previousEventX = gp.player.worldX;
					previousEventY = gp.player.worldY;
				}
			}
			// after checking the collision reset the solidArea x and y
			gp.player.solidArea.x = gp.player.solidAreaDefaultX;
			gp.player.solidArea.y = gp.player.solidAreaDefaultY;
			eventRect[map][col][row].x = eventRect[map][col][row].eventRectDefaultX;
			eventRect[map][col][row].y = eventRect[map][col][row].eventRectDefaultY;
		}

		return hit;
	}
	
	// 坑洞陷阱事件
	public void damagePit(int gameState) {
		
		gp.gameState = gameState;
		gp.playSE(6);
		gp.ui.currentDialogue = "You fall into a pit!";
		gp.player.life -= 1;
// 		eventRect[col][row].eventDone = true;	// 使其只會觸發一次
		canTouchEvent = false;
	}
	// 泉水回復事件
	public void healingPool(int gameState) {
		
		if (gp.keyH.enterPressed == true) {
			gp.gameState = gameState;
			gp.player.attackCanceled = true;
			gp.playSE(2);
			gp.ui.currentDialogue = "You drink the water.\nYour life and mana have been recovered.";
			gp.player.life = gp.player.maxLife;
			gp.player.mana = gp.player.maxMana;

			gp.aSetter.setMonster();
			/*
			// 當 怪物被殺掉(null) 將會重新配置
			for (int i = 0; i < gp.monster[1].length; i++) {
				if (gp.monster[gp.currentMap][i] == null && i < gp.aSetter.monsterData[currentMap].length) {
					gp.aSetter.replenishMonster(i);
				}
			}
			*/
		}
	}
	// 移動至其他地圖 
	public void teleport(int map, int col, int row) {
		
		gp.currentMap = map;
		gp.player.worldX = gp.tileSize * col;
		gp.player.worldY = gp.tileSize * row;
		
		previousEventX = gp.player.worldX;
		previousEventY = gp.player.worldY;
		
		canTouchEvent =false;
		gp.playSE(13);
	}
}
