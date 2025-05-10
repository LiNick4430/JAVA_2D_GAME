package entity.tile_interactive;

import entity.InteractiveTile;
import main.GamePanel;

public class IT_Trunk extends InteractiveTile{

	GamePanel gp;
	
	public IT_Trunk(GamePanel gp, int col, int row) {
		super(gp, col, row);
		this.gp = gp;
		
		this.worldX = gp.tileSize * col;
		this.worldY = gp.tileSize * row;
		
		down1 = setup("tiles_interactive/trunk");
		
		solidArea.width = 0;
		solidArea.height = 0;
	}
}
