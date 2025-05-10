package entity.monster;

import java.util.Random;

import entity.Entity;
import entity.object.OBJ_Coin_Bronze;
import entity.object.OBJ_Heart;
import entity.object.OBJ_ManaCrystal;
import entity.object.OBJ_Rock;
import main.GamePanel;

public class MON_GreenSlime extends Entity{
	
	GamePanel gp;
	
	public MON_GreenSlime(GamePanel gp) {
		super(gp);
		this.gp = gp;
		
		// 基本資料
		type = Type.MONSTER;
		name = "Green Slime";
		defaultSpeed = 1;
		speed = defaultSpeed;
		maxLife = 4;
		life = maxLife;
		attack = 5;
		defense = 0;
		exp = 2;
		projectile = new OBJ_Rock(gp);
		// solidArea
		setSolidArea(3, 18, 42, 30);
		
		getImage();
	}
	
	public void getImage() {
		
		up1 = setup("monster/greenslime_down_1");
		up2 = setup("monster/greenslime_down_2");
		down1 = setup("monster/greenslime_down_1");
		down2 = setup("monster/greenslime_down_2");
		left1 = setup("monster/greenslime_down_1");
		left2 = setup("monster/greenslime_down_2");
		right1 = setup("monster/greenslime_down_1");
		right2 = setup("monster/greenslime_down_2");
	}
	
	public void update() {
		super.update();
		
		int xDistance = Math.abs(worldX - gp.player.worldX);
		int yDistance = Math.abs(worldY - gp.player.worldY);
		int tileDistance = (xDistance + yDistance)/gp.tileSize;
		
		if (onPath == false && tileDistance < 5) {
			
			int i = new Random().nextInt(100)+1;
			if (i > 50) {
				onPath = true;
			}
		}
		
		if (onPath == true && tileDistance > 20) {
			onPath = false;
		}
		
	}
	
	public void setAction() {
		
		// 行動行為
		if (onPath == true) {

			// 跟隨玩家
			int goalCol = (gp.player.worldX + gp.player.solidArea.x)/gp.tileSize;
			int goalRow = (gp.player.worldY + gp.player.solidArea.y)/gp.tileSize;
			
			searchPath(goalCol, goalRow);
			
			// 攻擊行為
			int i = new Random().nextInt(200)+1;
			if (i > 197 && projectile.alive == false && shotAvailableCounter == 30) {
				// SET DEFAULT COORDINATES, DESCRIPTION AND USER
				projectile.set(worldX, worldY, direction, true, this);	

				// CHECK VACANCY				
				for (int ii = 0; ii < gp.projectileList[gp.currentMap].length; ii++) {
					if (gp.projectileList[gp.currentMap][ii] == null) {
						gp.projectileList[gp.currentMap][ii] = projectile;
						break;
					}
				}
				
				
				// RESET shotAvailableCounter
				shotAvailableCounter = 0;
			}
			
		} else {
			actionLockCounter ++;
			
			if (actionLockCounter == 120) {
				Random random = new Random();
				int i = random.nextInt(100)+1; // pick up a number (1~100)
				
				if (i <= 25) {
					direction = "up";
				} else if (i <= 50) {
					direction = "down";
				} else if (i <= 75) {
					direction = "left";
				} else if (i <= 100) {
					direction = "right";
				}
				
				actionLockCounter = 0;
			}
		}

		
	}
	
	@Override
	public void damageReaction() {
		
		actionLockCounter = 0;
//		direction = gp.player.direction;
		onPath = true;
	}
	
	@Override
	public void checkDrop() {
		
		// CAST A DIE
		int i = new Random().nextInt(100)+1;
		
		// SET THE MONESTER DROP
		if (i <50) {
			dropItem(new OBJ_Coin_Bronze(gp));
		}else if (i < 75) {
			dropItem(new OBJ_Heart(gp));
		}else {
			dropItem(new OBJ_ManaCrystal(gp));
		}
	}

}
