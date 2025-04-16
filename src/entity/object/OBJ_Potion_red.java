package entity.object;

import entity.Entity;
import main.GamePanel;

public class OBJ_Potion_red extends Entity{
	
	GamePanel gp;
	
	
	public OBJ_Potion_red(GamePanel gp) {
		super(gp);
		
		this.gp = gp;
		
		type =  type_consumable;
		name = "Red Potion";
		value = 5;
		down1 = setup("objects/potion_red");	
		description = "[" +name+ "]\nHeals your life by" + value + ".";
	}
	
	@Override
	public void use(Entity entity) {
		
		gp.gameState = gp.dialogueState;
		gp.ui.currentDialogue = "You drink the " + name + "!\n" 
				+ "Your life has been recovered by " + value + ".";
		entity.life += value;
		gp.playSE(2);
	}
}
