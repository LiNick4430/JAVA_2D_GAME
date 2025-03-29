package main;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Font;
import java.awt.FontFormatException;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.InputStream;

import object.OBJ_Heart;
import object.SuperObject;

public class UI {

	GamePanel gp;
	Graphics2D g2;
	Font maruMonica, purisaB;
	BufferedImage heart_full, heart_half, heart_blank;
	
	public boolean messageOn = false;
	public String message = "";
	int messageCount = 0;

	public boolean gameFinished = false;
	// DIALOGUE SCREEN
	public String currentDialogue = "";
	// TITLE SCREEN
	public int commandNum = 0;
	public int titleScreenState = 0; // 0: the first screen, 1: the second screen

	public UI(GamePanel gp) {		
		this.gp = gp;
			
		try {
			InputStream is = getClass().getClassLoader().getResourceAsStream("font/x12y16pxMaruMonica.ttf");
			maruMonica = Font.createFont(Font.TRUETYPE_FONT, is);
			is = getClass().getClassLoader().getResourceAsStream("font/Purisa Bold.ttf");
			purisaB = Font.createFont(Font.TRUETYPE_FONT, is);
		} catch (FontFormatException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		/*
		1. FontFormatException：
			這個異常表示嘗試創建的字型格式不正確。
			例如，如果字型檔案損壞或不是有效的 TrueType 字型檔案，就會拋出這個異常。
		2. IOException：	
			這個異常表示在讀取字型檔案時發生了 I/O（輸入/輸出）錯誤。
			例如，如果字型檔案不存在或無法讀取，就會拋出這個異常。
		*/
		
		// CREATE HUD OBJECT
		SuperObject heart = new OBJ_Heart(gp);
		heart_full = heart.image;
		heart_half = heart.image2;
		heart_blank = heart.image3;
	}
	
	public void showMessage(String text) {
		
		message = text;
		messageOn = true;
	}
	
	public void draw(Graphics2D g2) {
		
		this.g2 = g2;
		
		g2.setFont(maruMonica);
		// g2.setFont(purisaB);
		g2.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
		// setRenderingHint() 方法用於設定 Graphics2D 物件的渲染提示
		// RenderingHints.KEY_TEXT_ANTIALIASING 設定文字抗鋸齒的渲染提示, RenderingHints.VALUE_TEXT_ANTIALIAS_ON 啟用文字抗鋸齒
		g2.setColor(Color.white);
		// TITLE STATE
		if (gp.gameState == gp.titleState) {
			drawTitleScreen();
		}
		// PLAT STATE
		if (gp.gameState == gp.playState) {
			drawPlayerLife();
		}
		// PAUSE STATE
		if (gp.gameState == gp.pauseState) {
			drawPlayerLife();
			drawPauseScreen();
		}
		// DIALOGUE STATE
		if (gp.gameState == gp.dialogueState) {
			drawPlayerLife();
			drawDialogueScreen();
		}
	}
	
	public void drawPlayerLife() {

		int x = gp.tileSize/2;
		int y = gp.tileSize/2;
		int maxCount = 0;
		int fullHearts = gp.player.life/2;
		int halfHearts = gp.player.life%2;
		
		// DRAW MAX LIFE
		while (maxCount < gp.player.maxLife/2) {
			g2.drawImage(heart_blank, x, y, null);
			maxCount++;
			x += gp.tileSize;
		}
		
		// RESET
		x = gp.tileSize/2;
		y = gp.tileSize/2;

		// DRAW CURRENT LIFE
		for(int i = 0; i < fullHearts; i++) {
		    g2.drawImage(heart_full, x, y, null);
		    x += gp.tileSize;
		}
		for(int i = 0; i < halfHearts; i++) {
		    g2.drawImage(heart_half, x, y, null);
		    x += gp.tileSize;
		}
	}
	
	public void drawTitleScreen() {
		
		if (titleScreenState == 0) {
			// BACKGROUND
			g2.setColor(new Color(0, 0, 0));
			g2.fillRect(0, 0, gp.screenWidth, gp.screenHeight);
			
			// TITLE NAME 
			g2.setFont(g2.getFont().deriveFont(Font.BOLD,96F));
			String text = "Blue Boy Adventure";		
			int x = getXforCenteredText(text);
			int y = gp.tileSize*3;
			
			// SHADOW
			g2.setColor(Color.gray);
			g2.drawString(text, x+5, y+5);
			// MAIN COLOR
			g2.setColor(Color.white);
			g2.drawString(text, x, y);
			
			// BLUE BOY IMAGE
			x = gp.screenWidth/2 - (gp.tileSize*2)/2;
			y += gp.tileSize*2;
			g2.drawImage(gp.player.down1, x, y, gp.tileSize*2, gp.tileSize*2, null);
			
			// MENU
			g2.setFont(g2.getFont().deriveFont(Font.BOLD,48F));
			
			text = "NEW GAME";		
			x = getXforCenteredText(text);
			y += gp.tileSize*3.5;
			g2.drawString(text, x, y);
			if (commandNum == 0) {
				g2.drawString(">", x-gp.tileSize, y);
			}
			text = "LOAD GAME";		
			x = getXforCenteredText(text);
			y += gp.tileSize;
			g2.drawString(text, x, y);
			if (commandNum == 1) {
				g2.drawString(">", x-gp.tileSize, y);
			}
			text = "QUIT";		
			x = getXforCenteredText(text);
			y += gp.tileSize;
			g2.drawString(text, x, y);
			if (commandNum == 2) {
				g2.drawString(">", x-gp.tileSize, y);
			}
			
		}else if (titleScreenState == 1) {
			
			// CLASS SELECTION SCREEN
			g2.setColor(Color.white);
			g2.setFont(g2.getFont().deriveFont(42F));
			
			String text = "Select your class!";
			int x = getXforCenteredText(text);
			int y = gp.tileSize*3;
			g2.drawString(text, x, y);
			
			text = "Fighter";
			x = getXforCenteredText(text);
			y += gp.tileSize*3;
			g2.drawString(text, x, y);
			if (commandNum == 0) {
				g2.drawString(">", x-gp.tileSize, y);
			}
			text = "Thref";
			x = getXforCenteredText(text);
			y += gp.tileSize;
			g2.drawString(text, x, y);
			if (commandNum == 1) {
				g2.drawString(">", x-gp.tileSize, y);
			}
			text = "Sorcerer";
			x = getXforCenteredText(text);
			y += gp.tileSize;
			g2.drawString(text, x, y);
			if (commandNum == 2) {
				g2.drawString(">", x-gp.tileSize, y);
			}
			text = "Back";
			x = getXforCenteredText(text);
			y += gp.tileSize*2;
			g2.drawString(text, x, y);
			if (commandNum == 3) {
				g2.drawString(">", x-gp.tileSize, y);
			}
		}

	}
	
	public void drawPauseScreen() {
		
		g2.setFont(g2.getFont().deriveFont(Font.PLAIN,80F));
		String text = "PAUSED";		
		int x = getXforCenteredText(text);
		int y = gp.screenHeight/2;
		
		g2.drawString(text, x, y);
		
	}
	
	public void drawDialogueScreen() {
		
		// WINDOW
		int x = gp.tileSize*2;
		int y = gp.tileSize/2;
		int width = gp.screenWidth - (gp.tileSize*4);
		int height = gp.tileSize*4;
		
		drawSubWindow(x, y, width, height);
		
		g2.setFont(g2.getFont().deriveFont(Font.PLAIN, 28F));
		x += gp.tileSize/2;
		y += gp.tileSize;
		
		for (String line : currentDialogue.split("\n")) {
			g2.drawString(line, x, y);
			y += 40;
		}
	}
	
	public void drawSubWindow(int x, int y, int width, int height) {
		// 建立底色背景
		Color c = new Color(0, 0, 0, 210);	// RGB color (R, G, B, Alpha(透明度) )
		g2.setColor(c);
		g2.fillRoundRect(x, y, width, height, 35, 35);
		// 建立邊緣外框
		c = new Color(255, 255, 255);
		g2.setColor(c);
		// setStroke() 方法用於設定 Graphics2D 物件的筆觸樣式。
		g2.setStroke(new BasicStroke(5));	// 當不帶任何參數呼叫 BasicStroke 的建構子時，它會建立一個預設的筆觸樣式。 預設的筆觸樣式是一條粗細為 1 像素的實線。
		g2.drawRoundRect(x+5, y+5, width-10, height-10, 25, 25);
	}
	
	public int getXforCenteredText(String text) {
		
		int lengh = (int)g2.getFontMetrics().getStringBounds(text, g2).getWidth();
		int x = gp.screenWidth/2 - lengh/2;
		return x;
		
	}
}
