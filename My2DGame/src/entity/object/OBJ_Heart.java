package entity.object;

import entity.Entity;
import main.GamePanel;

public class OBJ_Heart extends Entity{
	
	GamePanel gp;
	
	public OBJ_Heart(GamePanel gp) {
		super(gp);
		this.gp = gp;

		type = Type.PICKUP_ONLY;
		name = "Heart";
		value = 2;
		image = setup("objects/heart_full");
		image2 = setup("objects/heart_half");
		image3 = setup("objects/heart_blank");
		down1 = setup("objects/heart_full");		
	}
	
	@Override
	public boolean use(Entity entity) {
		
		gp.playSE(2);
		gp.ui.addMessage("Life +" + value);
		entity.life += value;
		return true;
	}
}
