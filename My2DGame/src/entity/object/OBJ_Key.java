package entity.object;

import entity.Entity;
import main.GamePanel;
import main.GamePanel.GameState;

public class OBJ_Key extends Entity {
	
	GamePanel gp;
	
	public OBJ_Key(GamePanel gp) {
		super(gp);
		this.gp = gp;

		type = Type.CONSUMABLE;
		name = "Key";
		down1 = setup("objects/key");
		description = "[" +name+ "]\nIt open the door.";
		price = 100;
		stackable = true;
	}

	@Override
	public boolean use(Entity entity) {

		gp.gameState = GameState.DIALOGUE;
		
		int objectIndex = getDetected(entity, gp.obj, "Door");
		
		if (objectIndex != 999) {
			gp.ui.currentDialogue = "You use the " + name + " and open the door";
			gp.playSE(3);
			gp.obj[gp.currentMap][objectIndex] = null;
			return true;
		} else {
			gp.ui.currentDialogue = "What are you doing?";
			return false;
		}
	}
	
	
}
