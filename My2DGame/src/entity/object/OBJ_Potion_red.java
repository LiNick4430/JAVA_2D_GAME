package entity.object;

import entity.Entity;
import main.GamePanel;
import main.GamePanel.GameState;

public class OBJ_Potion_red extends Entity{
	
	GamePanel gp;
	
	
	public OBJ_Potion_red(GamePanel gp) {
		super(gp);
		
		this.gp = gp;
		
		type =  Type.CONSUMABLE;
		name = "Red Potion";
		value = 5;
		down1 = setup("objects/potion_red");	
		description = "[" +name+ "]\nHeals your life by" + value + ".";
		price = 25;
		stackable = true;
	}
	
	@Override
	public boolean use(Entity entity) {
		
		gp.gameState = GameState.DIALOGUE;
		gp.ui.currentDialogue = "You drink the " + name + "!\n" 
				+ "Your life has been recovered by " + value + ".";
		entity.life += value;
		gp.playSE(2);
		return true;
	}
}
