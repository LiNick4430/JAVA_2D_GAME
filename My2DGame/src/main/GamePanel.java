package main;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;

import javax.swing.JPanel;

import entity.Entity;
import entity.Player;
import object.SuperObject;
import tile.TileManager;

//定義了一個名為 GamePanel(遊戲面板) 的類別，它繼承自 JPanel 類別
public class GamePanel extends JPanel implements Runnable { 
	
	// SCREEN SETTINGS 螢幕設定
	public final int originalTileSize = 16; 	// 16x16 tile(圖塊) 原始圖塊的尺寸 常見於2D遊戲中
	final int scale = 3;  						// 縮放比例（scale）
	
	public final int tileSize = originalTileSize * scale; 		// 實際顯示的圖塊大小 48x48 tile(圖塊)
	public final int maxScreenCol = 16; 						// 儲存螢幕水平方向可以顯示的圖塊數量       
	public final int maxScreenRow = 12; 						// 儲存螢幕垂直方向可以顯示的圖塊數量       
	public final int screenWidth = tileSize * maxScreenCol; 	// 768 pixels
	public final int screenHeight = tileSize * maxScreenRow; 	// 576 pixels
    
	// WORLD SETTINGS
	public final int maxWorldCol = 50; 		//	WorldWidth = tileSize * maxWorldCol
	public final int maxWorldRow = 50; 		//	 WorldHeight = tileSize * maxWorldRow;
	
    // FPS 目標幀率
    int FPS = 60;
    
    // SYSTEM
    TileManager tileM = new TileManager(this); 		// 匯入背景
    public KeyHandler keyH = new KeyHandler(this); 	// 用於處理鍵盤事件
    Sound music = new Sound();
    Sound se = new Sound();
    public CollisionChecker cChecker = new CollisionChecker(this);
    public AssetSetter aSetter = new AssetSetter(this);
    public UI ui = new UI(this);
    Thread gameThread;							// 創建和管理執行緒的類別 執行緒是程式中獨立的執行單元，可以並行執行。
    
    // ENTITY AND OBJECT
    public Player player = new Player(this,keyH);  
    public SuperObject obj[] = new SuperObject[10]; // 10 的意思是 同一時間 可以顯示 10個 object物件, 同時顯示太多可能會導致卡頓
    public Entity npc[] = new Entity[10];
        
    // GAME STATE
    public int gameState;
    public final int titleState = 0;
    public final int playState = 1;
    public final int pauseState = 2;
    public final int dialogueState = 3;
    
        
    public GamePanel() {
    	
    	this.setPreferredSize(new Dimension(screenWidth, screenHeight));
    	this.setBackground(Color.black);
    	this.setDoubleBuffered(true); // 啟用雙重緩衝（double buffering），以減少繪圖時可能出現的閃爍現象。
    	this.addKeyListener(keyH);
    	this.setFocusable(true); // 讓 gamePanel 物件獲得鍵盤焦點，以便它可以接收鍵盤事件。
    	
    }
    
    // 設定遊戲
    public void setupGame() {
    	
    	aSetter.setObject(); 	// 設定Object
    	aSetter.setNPC();		// 設定NPC
    	
    	// playMusic(0);
    	// stopMusic(); // 關閉背景音樂
    	gameState = titleState;
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
				repaint();						// 調用 repaint() 方法，觸發畫面重繪。	// swing程式中，repaint()方法，會觸發paintComponent()方法。
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
			for (int i = 0; i < npc.length; i++) {
				if(npc[i] != null) {
					npc[i].update();
				}
			}
			
		}else if (gameState == pauseState) {
			// NOTHING
		}
		
		
	}
	
	// run() 的 repaint()
	@Override
	public void paintComponent(Graphics g) { // Graphics g：是一個 Graphics 物件，它提供了繪製圖形的方法，例如繪製線條、矩形、圖像等。		
		super.paintComponent(g); 			// 調用父類的 paintComponent 方法		
		Graphics2D g2 = (Graphics2D)g; 		// 將 Graphics 物件轉換為 Graphics2D 物件

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
			tileM.draw(g2); // 背景 比 player 先 繪畫 
			
			// OBJECT
			for (int i = 0; i < obj.length; i++) {
				if(obj[i] != null) {
					obj[i].draw(g2,this);
				}
			}
			
			// NPC
			for (int i = 0; i < npc.length; i++) {
				if(npc[i] != null) {
					npc[i].draw(g2);
				}
			}
			
			// PLAYER
			player.draw(g2); // 原本上面程式碼 被整合到 player.draw(g2) 裡面
			
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
		g2.dispose(); // 關閉視窗並釋放資源
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
