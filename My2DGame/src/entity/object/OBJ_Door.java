package entity.object;

import entity.Entity;
import main.GamePanel;
import main.GamePanel.GameState;

public class OBJ_Door extends Entity {
	
	GamePanel gp;
	
	public OBJ_Door(GamePanel gp) {
		super(gp);
		this.gp = gp;
		
		type = Type.OBSTACLE;
		name = "Door";
		down1 = setup("objects/door");
		
		collision = true;
		
		setSolidArea(0, 16, 48, 32);		
	}

	@Override
	public void interact() {
		
		gp.gameState = GameState.DIALOGUE;
		gp.ui.currentDialogue = "You need a key to open this";
	}

}
