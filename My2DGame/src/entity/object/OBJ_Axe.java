package entity.object;

import entity.Entity;
import main.GamePanel;

public class OBJ_Axe extends Entity{

	public OBJ_Axe(GamePanel gp) {
		super(gp);
		
		type = Type.AXE;
		name = "Woodcutter's Axe";
		down1 = setup("objects/axe");
		attackValue = 2;
		attackArea.width = 30;
		attackArea.height = 30;
		description = "[" +name+ "]\nA bit rusty but still \ncan cut some trees.";
		price = 75;
		knockBackPower = 10;
	} 
}
