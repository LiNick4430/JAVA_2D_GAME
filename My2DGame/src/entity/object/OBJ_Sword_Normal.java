package entity.object;

import entity.Entity;
import main.GamePanel;

public class OBJ_Sword_Normal extends Entity{

	public OBJ_Sword_Normal(GamePanel gp) {
		super(gp);

		type = Type.SWORD;
		name = "Normal Sword";
		down1 = setup("objects/sword_normal");
		attackValue = 1;
		attackArea.width = 36;
		attackArea.height = 36;
		description = "[" +name+ "]\nAn old sword.";
		price = 20;
		knockBackPower = 2;
	}

}
