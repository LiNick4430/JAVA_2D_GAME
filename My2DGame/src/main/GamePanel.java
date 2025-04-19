package main;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GraphicsDevice;
import java.awt.GraphicsEnvironment;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

import javax.swing.JPanel;

import entity.Entity;
import entity.InteractiveTile;
import entity.player.Player;
import tile.TileManager;

//定義了一個名為 GamePanel(遊戲面板) 的類別，它繼承自 JPanel 類別, 並實作了 Runnable 介面
public class GamePanel extends JPanel implements Runnable { 
		
	// 基本 TileSize 設定
	public final int originalTileSize = 16; 	// 16x16 tile(圖塊) 原始圖塊的尺寸 常見於2D遊戲中
	final int scale = 3;  						// 縮放比例（scale）	
	public final int tileSize = originalTileSize * scale; 		// 實際顯示的圖塊大小 48x48 tile(圖塊)	
	// SCREEN SETTINGS 螢幕設定
	public final int maxScreenCol = 20; 						// 儲存螢幕水平方向可以顯示的圖塊數量       
	public final int maxScreenRow = 12; 						// 儲存螢幕垂直方向可以顯示的圖塊數量  
	public final int screenWidth = tileSize * maxScreenCol; 	// 960 pixels
	public final int screenHeight = tileSize * maxScreenRow; 	// 576 pixels   
	// WORLD SETTINGS
	public final int maxWorldCol = 50; 		// WorldWidth = tileSize * maxWorldCol
	public final int maxWorldRow = 50; 		// WorldHeight = tileSize * maxWorldRow;
	public final int maxMap = 10;			// MAP MAX
	public int currentMap = 0;				// NOW MAP
	// FOR FULL SCREEN
	int screenWidth2 = screenWidth;			// FULL SCREEN WIDTH
	int screenHeight2 = screenHeight;		// FULL SCREEN HEIGHT
	BufferedImage tempScreen;
	Graphics2D g2;
	public boolean fullScreenOn;
    // FPS 目標幀率
    final int FPS = 60;
    // SYSTEM
    TileManager tileM = new TileManager(this); 		// 匯入背景
    public KeyHandler keyH = new KeyHandler(this); 	// 用於處理鍵盤事件
    Sound music = new Sound();
    Sound se = new Sound();
    public CollisionChecker cChecker = new CollisionChecker(this);
    public AssetSetter aSetter = new AssetSetter(this);
    public UI ui = new UI(this);
    public EventHandler eHandler = new EventHandler(this);
    Config config = new Config(this);
    Thread gameThread;							// 創建和管理執行緒的類別 執行緒是程式中獨立的執行單元，可以並行執行。
    // ENTITY MAX 
    public final int maxobj = 20;
    public final int maxNpc = 10;
    public final int maxMonster = 20;
    public final int maxiTle = 50;
    // ENTITY 
    public Player player = new Player(this,keyH);  
    public Entity obj[][] = new Entity[maxMap][maxobj]; 				// [int] 的意思是 同一時間 可以顯示 object 物件, 同時顯示太多可能會導致卡頓
    public Entity npc[][] = new Entity[maxMap][maxNpc];
    public Entity monster[][] = new Entity[maxMap][maxMonster];
    public InteractiveTile iTile[][] = new InteractiveTile[maxMap][maxiTle];
    public ArrayList<Entity> projectileList = new ArrayList<>();	// 存放 Project tile
    public ArrayList<Entity> particleList = new ArrayList<>();
    ArrayList<Entity> entityList = new ArrayList<>();				// 用於繪畫 Entity 實體   
    // GAME STATE
    public int gameState;
    public final int titleState = 0;
    public final int playState = 1;
    public final int pauseState = 2;
    public final int dialogueState = 3;
    public final int characterState = 4;
    public final int optionsState = 5;
    public final int gameOverState = 6;
    public final int transitionState = 7;
    public final int tradeState = 8;
    
        
    public GamePanel() {
    	
    	this.setPreferredSize(new Dimension(screenWidth, screenHeight));
    	this.setBackground(Color.black);
    	this.setDoubleBuffered(true); // 啟用雙重緩衝（double buffering），以減少繪圖時可能出現的閃爍現象。
    	this.addKeyListener(keyH);
    	this.setFocusable(true); // 讓 gamePanel 物件獲得鍵盤焦點，以便它可以接收鍵盤事件。
    	
    }
    
    // 初始化 遊戲
    public void setupGame() {
    	
    	aSetter.setObject(); 			// 初始化 Object
    	aSetter.setNPC();				// 初始化 NPC
    	aSetter.setMonster();			// 初始化 Monster
    	aSetter.setInteractiveTile();	// 初始化 InteractiveTile
    			
    	// playMusic(0);				// 撥放 背景音樂
    	// stopMusic(); // 關閉背景音樂
    	gameState = titleState;
    	
    	// 雙緩衝 
    	tempScreen = new BufferedImage(screenWidth, screenHeight, BufferedImage.TYPE_INT_ARGB);
    				// 創建了一個與遊戲視窗尺寸相同的、支援 Alpha 透明度的記憶體圖像，並將其引用賦值給 tempScreen 變數。這個 tempScreen 就是您的離屏緩衝區。
    	g2 = (Graphics2D)tempScreen.getGraphics();
    				// tempScreen.getGraphics(): BufferedImage 類別的 getGraphics() 方法返回一個 Graphics 物件，這個 Graphics 物件可以被用來在 tempScreen 這個 BufferedImage 上進行繪圖操作。
    				// (Graphics2D): 這是一個類型轉換（cast）。
    				// getGraphics() 方法實際上返回的是一個 Graphics 物件，但為了使用更強大的 2D 繪圖功能（例如抗鋸齒、變換、更精細的形狀繪製等），通常會將其強制轉換為 Graphics2D 物件。Graphics2D 是 Graphics 的子類別，提供了更豐富的繪圖 API。
    				// g2 = ...: 將轉換後的 Graphics2D 物件的引用賦值給名為 g2 的變數。這個 g2 變數將在您的 drawToTempScreen() 方法中用於在 tempScreen 上繪製所有的遊戲內容。
    	
    	if (fullScreenOn == true) {
    		setFullScreen();			// 全螢幕
		}			
    }
    // 重試 這輪 
    public void retry() {
		
    	player.setDefaultPositions();
    	player.restoreLifeAndMana();
    	aSetter.setNPC();				// 初始化 NPC
    	aSetter.setMonster();			// 初始化 Monster
	}
    // 重開 新的 一輪
    public void restart() {
		
    	player.setDefaultValues();
    	player.setDefaultPositions();
    	player.restoreLifeAndMana();
    	player.setItems();
    	aSetter.setObject(); 			// 初始化 Object
    	aSetter.setNPC();				// 初始化 NPC
    	aSetter.setMonster();			// 初始化 Monster
    	aSetter.setInteractiveTile();	// 初始化 InteractiveTile
	}
    
    // Java Swing 應用程式全螢幕功能
    public void setFullScreen() {
		
    	// GET LOCAL SCREEN DEVICE
    	GraphicsEnvironment ge = GraphicsEnvironment.getLocalGraphicsEnvironment();
    							// GraphicsEnvironment 類別代表本地圖形環境。
    							// getLocalGraphicsEnvironment() 是一個靜態方法，用於返回本地系統的 GraphicsEnvironment 物件。
    	GraphicsDevice gd = ge.getDefaultScreenDevice();
    							// GraphicsDevice 類別代表一個圖形設備，例如螢幕。
    							// getDefaultScreenDevice() 方法返回預設的螢幕設備。
    							// 如果您的系統有多個螢幕，您可以使用 ge.getScreenDevices() 來獲取所有螢幕設備的陣列。
    	gd.setFullScreenWindow(Main.window);				// 它將指定的 Window 物件 設定為全螢幕模式
    	
    	// GET FULL SCREEN WIDTH AND HEIGHT
    	screenWidth2 = Main.window.getWidth();
    	screenHeight2 = Main.window.getHeight();    	
	}
    
    // 啟動遊戲執行緒
    public void startGameThread() {
    	
    	gameThread = new Thread(this);
    	gameThread.start();
    }
    
    
/*	這段程式碼是一個簡單有效的遊戲迴圈，它使用固定幀率控制遊戲的更新和繪製速度。在大多數情況下，它都能夠提供流暢的遊戲體驗。但是，在需要更高精度時間控制的應用程式中，可能需要使用其他更複雜的遊戲迴圈模式。
  	優點：
	1. 實現了固定的FPS，遊戲體驗穩定。
	2. 實現了時間同步，不同硬件表現一致。
	3. 代碼簡單，易於理解。
	缺點：	
	1. Thread.sleep() 的精度可能不高，在某些情況下可能導致幀率不穩定。
	2. 如果更新和繪製所需的時間超過了時間間隔，遊戲可能會出現卡頓。
	
	@Override	
	public void run() {	
		
		double drawInterval = 1000000000/FPS; // = 0.16666 seconds	// 計算每次繪製之間的時間間隔
		double nextDrawTime = System.nanoTime() + drawInterval; 	// 計算下一次繪製的時間，即當前時間加上時間間隔。
 		// Game Loop
		while(gameThread != null) {				
			// 1. UPDATE： update information such as character positions
			update();			
			// 2. DRAW： draw the screen with the updated information
			repaint();
			
			try {
				double remainingTime = nextDrawTime  - System.nanoTime();	// 計算距離下一次繪製還剩餘的時間（以奈秒為單位）。
				remainingTime = remainingTime/1000000;						// 將剩餘時間從奈秒轉換為毫秒。
				
				if(remainingTime < 0) {										// 如果剩餘時間為負數，表示已經超過了下一次繪製的時間，將其設置為 0。
					remainingTime = 0;
				}
				
				Thread.sleep((long) remainingTime);							// 讓執行緒休眠剩餘的時間，以控制遊戲速度。
				
				nextDrawTime += drawInterval;								// 更新下一次繪製的時間。
				
			}  catch (InterruptedException e) { 
				e.printStackTrace();				
			}				
		}
		
	}
*/
    
    // 基於時間的遊戲迴圈，它可以控制遊戲的更新和繪製速度，使其與目標幀率保持一致。
/*	優點：
	1. 基於時間的遊戲迴圈，能夠較好的適應不同硬體。
	2. 能夠計算並顯示當前遊戲的FPS。
	缺點：	
	1. 相對於固定的Thread.sleep()的遊戲迴圈，此方法較為複雜。
	2. 在一些極端的硬體環境下，可能會產生FPS計算上的誤差。
*/
    @Override	
	public void run() {
		
		double drawInterval = 1000000000/FPS;	// 計算每次繪製之間的時間間隔（以奈秒為單位）
		double delta = 0;						// 用於累積經過的時間，以確定何時進行下一次更新和繪製
		long lastTime = System.nanoTime();		// 儲存上一次迴圈開始的時間
		long currentTime;						// 儲存當前迴圈開始的時間
		long timer = 0;							// 用於累積經過的時間，以計算 FPS
		int drawcount = 0;						// 用於計算每秒繪製的幀數
		
		while(gameThread != null) {
			
			currentTime = System.nanoTime(); 	// 獲取系統當前的高精度時間，以奈秒（nanoseconds）為單位
			
			delta += (currentTime - lastTime) / drawInterval;	// 計算經過的時間，並將其累積到 delta 變數中。
			timer += (currentTime - lastTime);	// 將經過的時間累積到 timer 變數中。
			lastTime = currentTime;				// 更新 lastTime 變數，以便在下一次迴圈中使用。
			
			if(delta >= 1) {					// 檢查 delta 是否大於或等於 1，表示已經過了足夠的時間進行一次更新和繪製。
				update();						// 調用 update() 方法，更新遊戲邏輯。
				drawToTempScreen();				// draw everything to the buffered image
				drawToScreen();					// draw the buffered image to the screen
				delta--;						// 減少 delta 變數的值。
				drawcount++;					// 增加繪製幀數計數器。
			}
			
			if(timer >= 1000000000) {			// 檢查 timer 是否大於或等於 1 秒（1000000000 奈秒）。
				// System.out.println("FPS：" + drawcount);		// 輸出當前 FPS。 (用於測試是否正常運行輸出)
				drawcount = 0;					// 重置繪製幀數計數器。
				timer = 0;						// 重置計時器。
			}
						
		}
		
	}
	
	// run() 的 update()
	public void update() {
				
		if (gameState == playState) {
			// PLAYER
			player.update(); 							
			// NPC
			for (int i = 0; i < npc[currentMap].length; i++) {
				if(npc[currentMap][i] != null) {
					npc[currentMap][i].update();
				}
			}
			// Monster
			for (int i = 0; i < monster[currentMap].length; i++) {
				if(monster[currentMap][i] != null) {
					// 當 Monster 生存 且 尚未 死亡
					if(monster[currentMap][i].alive == true && monster[currentMap][i].dying == false) {
						monster[currentMap][i].update();
					}
					// 當 Monster 非 生存
					if (monster[currentMap][i].alive == false){ 
						monster[currentMap][i].checkDrop();
						monster[currentMap][i] = null;
					}
				}
			}
			// projectileList (EX：Fire ball)
			for (int i = 0; i < projectileList.size(); i++) {
				if(projectileList.get(i) != null) {
					
					if(projectileList.get(i).alive == true ) {
						projectileList.get(i).update();
					}
					
					if (projectileList.get(i).alive == false){ 
						projectileList.remove(i);
					}
				}
			}
			// InteractiveTiles
			for (int i = 0; i < iTile[currentMap].length; i++) {
				if (iTile[currentMap][i] != null) {
					iTile[currentMap][i].update();
				}
			}
			// particle
			for (int i = 0; i < particleList.size(); i++) {
				if(particleList.get(i) != null) {
					
					if(particleList.get(i).alive == true ) {
						particleList.get(i).update();
					}
					
					if (particleList.get(i).alive == false){ 
						particleList.remove(i);
					}
				}
			}
		}else if (gameState == pauseState) {
			// NOTHING
		}
		
		
	}
	
	// 在 緩衝圖像 上繪製所有遊戲內容
	public void drawToTempScreen() {
		
		// DEBUG
		long drawStart = 0;
		if (keyH.checkDrawTime == true) {			
			drawStart = System.nanoTime();
		}		
		
		// TITLE SCREEN
		if (gameState == titleState) {
			ui.draw(g2);
		}
		// OTHERS
		else {
			// TILE
			tileM.draw(g2); // 背景 比 ENTITIES 先行繪畫上
					
			for (int i = 0; i < iTile[currentMap].length; i++) {
				if (iTile[currentMap][i] != null) {
					iTile[currentMap][i].draw(g2);
				}
			}
				
			// ADD ENTITIES TO LIST 
			entityList.add(player);
					
			for (int i = 0; i < npc[currentMap].length; i++) {
				if (npc[currentMap][i] != null) {
					entityList.add(npc[currentMap][i]);
				}
			}
					
			for (int i = 0; i < obj[currentMap].length; i++) {
				if (obj[currentMap][i] != null) {
					entityList.add(obj[currentMap][i]);
				}
			}
					
			for (int i = 0; i < monster[currentMap].length; i++) {
				if (monster[currentMap][i] != null) {
					entityList.add(monster[currentMap][i]);
				}
			}
					
			for (int i = 0; i < projectileList.size(); i++) {
				if (projectileList.get(i) != null) {
					entityList.add(projectileList.get(i));
				}
			}
					
			for (int i = 0; i < particleList.size(); i++) {
				if (particleList.get(i) != null) {
					entityList.add(particleList.get(i));
				}
			}
			// SORT 依照 worldY 排出類似 景深 的效果
			Collections.sort(entityList, new Comparator<Entity>() {

				@Override
				public int compare(Entity e1, Entity e2) {

					int result = Integer.compare(e1.worldY, e2.worldY); 
					return result;
				}
				 
			});			
			// DRAW ENTITIES 
			for (int  i = 0; i < entityList.size() ;  i++) {
				entityList.get(i).draw(g2);
			}
					
			// EMPTY ENTITY LIST
			entityList.clear();
					
			// UI
			ui.draw(g2);
		}
						
		// DEBUG			
		if (keyH.checkDrawTime == true) {
			long drawEnd = System.nanoTime();
			long passed = drawEnd - drawStart;
			g2.setColor(Color.white);
			g2.drawString("Draw Time:" + passed, 10, 400);
			System.out.println("Draw Time:" + passed);
		}		
				
		// 越後面 繪畫的物件 將會在畫面的最上層	
	}
	
	// 將預先繪製好的 緩衝圖像 一次性地呈現到實際的螢幕上
	public void drawToScreen() {
		
		Graphics g = getGraphics();
		g.drawImage(tempScreen, 0, 0, screenWidth2, screenHeight2, null);
		g.dispose();
	}
	
	// play BGM
	public void playMusic(int i) {
		
		music.setFile(i);
		music.play();
		music.loop();
	}
	
	// stop BGM
	public void stopMusic() {
		
		music.stop();
	}
	
	// play SE
	public void playSE(int i) {
		
		se.setFile(i);
		se.play();
	}
}
