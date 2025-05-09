package entity.object;

import entity.Entity;
import main.GamePanel;

public class OBJ_Shield_Blue extends Entity{

	public OBJ_Shield_Blue(GamePanel gp) {
		super(gp);
		
		type = Type.SHIED;
		name = "Blue Shield";
		down1 = setup("objects/shield_blue");
		defenseValue = 2;
		description = "[" +name+ "]\nA shiny blue shield.";
		price = 250;
	}
}
