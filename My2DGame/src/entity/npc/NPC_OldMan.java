package entity.npc;

import java.awt.Rectangle;
import java.util.Random;

import entity.Entity;
import main.GamePanel;

public class NPC_OldMan extends Entity{
	
	GamePanel gp;
	
	public NPC_OldMan(GamePanel gp) {
		super(gp);
		
		this.gp = gp;
		
		direction = "down";
		speed = 1;
		
		solidArea = new Rectangle();
		solidArea.x = 8;
		solidArea.y = 16;
		solidAreaDefaultX = solidArea.x;
		solidAreaDefaultY = solidArea.y;
		solidArea.width = 32;
		solidArea.height = 32;
		
		getImage();
		setDialogue();
	}
	
	// 匯入 圖片
	public void getImage() {
		
		up1 = setup("npc/oldman_up_1");
		up2 = setup("npc/oldman_up_2");
		down1 = setup("npc/oldman_down_1");
		down2 = setup("npc/oldman_down_2");
		left1 = setup("npc/oldman_left_1");
		left2 = setup("npc/oldman_left_2");
		right1 = setup("npc/oldman_right_1");
		right2 = setup("npc/oldman_right_2");

	}
	
	public void setDialogue() {
		
		dialogues[0] = "Hello, lad.";
		dialogues[1] = "So you've come to this island to \nfind the treasure?";
		dialogues[2] = "I used to be a great wizard but now... \nI'm a bit too old for taking an adventure.";
		dialogues[3] = "Well, good luck on you.";
		
	}
	
	// 建立 NPC_OldMan 的行動邏輯 
	// 每 120 actionLockCounter 隨機向 4方向 移動
	@Override
	public void setAction() {
		
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
	
	@Override
	public void speak() {
		
		// Do this character specific stuff
		
		super.speak();
		
	}
}
