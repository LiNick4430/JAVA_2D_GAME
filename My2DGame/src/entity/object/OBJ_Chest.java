package entity.object;

import entity.Entity;
import main.GamePanel;
import main.GamePanel.GameState;

public class OBJ_Chest extends Entity {

	GamePanel gp;
	Entity loot;
	boolean opened = false;
	
	public OBJ_Chest(GamePanel gp, Entity loot) {		
		super(gp);
		this.gp = gp;
		this.loot = loot;
		
		type = Type.OBSTACLE;
		name = "Chest";
		image = setup("objects/chest");
		image2 = setup("objects/chest_opened");	
		down1 = image;
		collision = true;
		
		setSolidArea(4, 16, 40, 32);
	}

	@Override
	public void interact() {
		
		gp.gameState = GameState.DIALOGUE;
		
		if (opened == false) {
			gp.playSE(3);
			
			StringBuilder sb = new StringBuilder();
			sb.append("You are open the chest and find a " + loot.name + "!");
			
			if (gp.player.canObtainItem(loot) == false) {
				sb.append("\n..But you cannot carry any more!");
			}else {
				sb.append("\nYou obtain the " + loot.name + "!");		
				opened = true;
				down1 = image2;
			}
			gp.ui.currentDialogue = sb.toString();
		} else {
			gp.ui.currentDialogue = "It's empty.";
		}
	}
	
	
}
