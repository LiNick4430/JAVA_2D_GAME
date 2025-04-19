package entity.object;

import entity.Entity;
import main.GamePanel;

public class OBJ_Key extends Entity {
	
	public OBJ_Key(GamePanel gp) {
		super(gp);

		name = "Key";
		down1 = setup("objects/key");
		description = "[" +name+ "]\nIt open the door.";
		price = 100;
	}
}
