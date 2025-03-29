package tile;

import java.awt.Graphics2D;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import javax.imageio.ImageIO;

import main.GamePanel;
import main.UtilityTool;

public class TileManager { 	// 圖塊 管理
	
	GamePanel gp;
	public Tile[] tile;			// 存放 匯入圖片 的 對應種類
	public int mapTileNum[][];		// 用於存放 maps 中 每一格 num 對應 每一格 的 圖片
	
	
	public TileManager(GamePanel gp) {
		
		this.gp = gp;
		
		tile = new Tile[50];										// 建立 一個 陣列 存放 最大圖片種類
		// mapTileNum = new int[gp.maxScreenCol][gp.maxScreenRow];	// 建立 一個 二維陣列 用於存放 圖片號碼 對應 畫面COL最大值 畫面ROW最大值 
		mapTileNum = new int[gp.maxWorldCol][gp.maxWorldRow];		// 建立 一個 世界
		
		getTileImage();
		loadMap("maps/WorldV2.txt");
	}
	
	// 匯入圖片
	public void getTileImage() {
		
		// PLACEHOLDER
		setup(0, "grass00", false);
		setup(1, "grass00", false);
		setup(2, "grass00", false);
		setup(3, "grass00", false);
		setup(4, "grass00", false);
		setup(5, "grass00", false);
		setup(6, "grass00", false);
		setup(7, "grass00", false);
		setup(8, "grass00", false);
		setup(9, "grass00", false);
		// PLACEHOLDER
		
		setup(10, "grass00", false);
		setup(11, "grass01", false);
		setup(12, "water00", true);
		setup(13, "water01", true);
		setup(14, "water02", true);
		setup(15, "water03", true);
		setup(16, "water04", true);
		setup(17, "water05", true);
		setup(18, "water06", true);
		setup(19, "water07", true);
		setup(20, "water08", true);
		setup(21, "water09", true);
		setup(22, "water10", true);
		setup(23, "water11", true);
		setup(24, "water12", true);
		setup(25, "water13", true);
		setup(26, "road00", false);
		setup(27, "road01", false);
		setup(28, "road02", false);
		setup(29, "road03", false);
		setup(30, "road04", false);
		setup(31, "road05", false);
		setup(32, "road06", false);
		setup(33, "road07", false);
		setup(34, "road08", false);
		setup(35, "road09", false);
		setup(36, "road10", false);
		setup(37, "road11", false);
		setup(38, "road12", false);
		setup(39, "earth", false);
		setup(40, "wall", true);
		setup(41, "tree", true);
		
		/* 舊版匯入方法 被整合進去 setup()
		try {			 
			// 建立一個 Tile , 陣列[0] 用於存放 grass, (預設)collision = false;
			tile[0] = new Tile(); 
			tile[0].image = ImageIO.read(getClass().getClassLoader().getResourceAsStream("tiles/grass.png"));
			// 建立一個 Tile , 陣列[1] 用於存放 wall, collision = true;  
			tile[1] = new Tile(); 
			tile[1].image = ImageIO.read(getClass().getClassLoader().getResourceAsStream("tiles/wall.png"));
			tile[1].collision = true;
			// 建立一個 Tile , 陣列[2] 用於存放 water
			tile[2] = new Tile();
			tile[2].image = ImageIO.read(getClass().getClassLoader().getResourceAsStream("tiles/water.png"));
			tile[2].collision = true;
			// 建立一個 Tile , 陣列[3] 用於存放 water
			tile[3] = new Tile();
			tile[3].image = ImageIO.read(getClass().getClassLoader().getResourceAsStream("tiles/earth.png"));
			// 建立一個 Tile , 陣列[4] 用於存放 water
			tile[4] = new Tile();
			tile[4].image = ImageIO.read(getClass().getClassLoader().getResourceAsStream("tiles/tree.png"));
			tile[4].collision = true;
			// 建立一個 Tile , 陣列[5] 用於存放 water
			tile[5] = new Tile();
			tile[5].image = ImageIO.read(getClass().getClassLoader().getResourceAsStream("tiles/sand.png"));						
		}catch(IOException e) {
			e.printStackTrace();
		}
		*/
	}
	
	public void setup(int index, String imageName, boolean collision) {
		
		UtilityTool uTool = new UtilityTool(); 
		try {
			tile[index] = new Tile();
			tile[index].image = ImageIO.read(getClass().getClassLoader().getResourceAsStream("tiles/"+ imageName +".png"));
			tile[index].image = uTool.scaleImage(tile[index].image, gp.tileSize, gp.tileSize);
			tile[index].collision = collision;
			
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	public void loadMap(String filePath) {	// 匯入 maps 檔案中的 圖片號碼 並放入 mapTileNum
		
		try {
			InputStream is = getClass().getClassLoader().getResourceAsStream(filePath);	// 在路徑上 讀取 指定檔案
			BufferedReader br = new BufferedReader(new InputStreamReader(is));			
									// 創建一個 BufferedReader 物件，它使用 InputStreamReader 物件作為輸入源。
									// BufferedReader 提供 readLine() 方法，可以逐行讀取文字檔案。
			
			int col = 0;
			int row = 0;
			
			while(col < gp.maxWorldCol && row < gp.maxWorldRow) {
				
				String line = br.readLine();		// 逐行 讀取文字檔案
				
				while(col < gp.maxWorldCol) {
					
					String numbers[] = line.split(" ");			// 將 line 中 按照 空格(" ") 分割後 逐一放入 number[]
					
					int num = Integer.parseInt(numbers[col]); 	// 將字串陣列 numbers 中索引為 col 的元素轉換為整數，並將其儲存在整數變數 num 中。
					
					mapTileNum[col][row] = num;
					col++;
				}
				if(col == gp.maxWorldCol) {
					col = 0;
					row++;
				}
			}
			br.close(); 	// 關閉 BufferedReader 物件 br
						
		}catch(Exception e) {
			
		}
	}
	public void draw(Graphics2D g2) {
		
		// 1 tileSize = 48 往右 1 格要 + 48 往右 2 格要 + 96
		// g2.drawImage(tile[1].image, 0, 0, gp.tileSize, gp.tileSize, null);
		
		int worldCol = 0;
		int worldRow = 0;
//		int x = 0;
//		int y = 0;
		
		while(worldCol < gp.maxWorldCol && worldRow < gp.maxWorldRow) {
			
			int tileNum = mapTileNum[worldCol][worldRow];		// 從 mapTileNum 中 讀取每一格的 tileNum
			
			// 世界X 和 畫面X 的相對關係
			int worldX = worldCol * gp.tileSize;
			int worldY = worldRow * gp.tileSize;
			
			int screenX = worldX - gp.player.worldX + gp.player.screenX;
			int screenY = worldY - gp.player.worldY + gp.player.screenY;
			
			// 讓 只在畫面XY 附近的 世界XY 的 繪圖 用於減輕效能 
			if(worldX + gp.tileSize > gp.player.worldX - gp.player.screenX &&
			   worldX - gp.tileSize < gp.player.worldX + gp.player.screenX &&
			   worldY + gp.tileSize > gp.player.worldY - gp.player.screenY &&
			   worldY - gp.tileSize < gp.player.worldY + gp.player.screenY) {
				
				// g2.drawImage(tile[tileNum].image, screenX, screenY, gp.tileSize, gp.tileSize, null);	scale 功能 被整合進 setup() 中 UtilityTool.scaleImage
				g2.drawImage(tile[tileNum].image, screenX, screenY,  null);
			}
			
//			g2.drawImage(tile[tileNum].image, screenX, screenY, gp.tileSize, gp.tileSize, null);	// 依照 tileNum 依序畫上 每一格 圖片
			worldCol++;				// col 貼完後 換下一格
//			x += gp.tileSize;		// x 往右移動 tileSize 距離
			
			if(worldCol == gp.maxWorldCol) { // 當 col 貼完以後
				worldCol = 0;			// 初始化
//				x = 0;					// 初始化
				worldRow++;				// 此 row 換下一個 row
//				y += gp.tileSize;		// y 往下移動 tileSize 距離
			}
		}
		
	}

}
