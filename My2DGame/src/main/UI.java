package main;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Font;
import java.awt.FontFormatException;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.font.FontRenderContext;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;

import entity.Entity;
import entity.object.OBJ_Heart;
import entity.object.OBJ_ManaCrystal;

public class UI {

	GamePanel gp;
	Graphics2D g2;
	Font maruMonica, purisaB;
	BufferedImage heart_full, heart_half, heart_blank, crystal_full, crystal_blank;		// 特定 OBJ 名稱 
	
	public boolean messageOn = false;
	ArrayList<String> message = new ArrayList<>();
	ArrayList<Integer> messageCounter = new ArrayList<>();

	public boolean gameFinished = false;
	
	public String currentDialogue = "";		// 存放文字用
	public int commandNum = 0;				// 控制選擇位置
	public int slotCol = 0;					// 道具欄位使用
	public int slotRow = 0;					// 道具欄位使用
	public int subState = 0;				// 子頁面使用

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
		Entity heart = new OBJ_Heart(gp);
		heart_full = heart.image;
		heart_half = heart.image2;
		heart_blank = heart.image3;
		Entity crystal = new OBJ_ManaCrystal(gp);
		crystal_full = crystal.image;
		crystal_blank = crystal.image2;
	}
	
	public void addMessage(String text) {

		message.add(text);
		messageCounter.add(0);
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
		// PLAY STATE
		if (gp.gameState == gp.playState) {
			drawPlayerLifeAndMana();
			drawMessage();
		}
		// PAUSE STATE
		if (gp.gameState == gp.pauseState) {
			drawPlayerLifeAndMana();
			drawPauseScreen();
		}
		// DIALOGUE STATE
		if (gp.gameState == gp.dialogueState) {
			drawPlayerLifeAndMana();
			drawDialogueScreen();
		}
		// CHARACTER STATE
		if (gp.gameState == gp.characterState) {
			drawPlayerLifeAndMana();
			drawCharacterScreen();
			drawInventory();
		}
		// OPTIONS STATE
		if (gp.gameState == gp.optionsState) {
			drawOptionsScreen();
		}
		// GAME OVER STATE
		if (gp.gameState == gp.gameOverState) {
			drawGameOverScreen();
		}
	}
	
	public void drawPlayerLifeAndMana() {

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
		
		// DRAW CURRENT LIFE
		x = gp.tileSize/2;	// RESET
		y = gp.tileSize/2;	// RESET
		
		for(int i = 0; i < fullHearts; i++) {
		    g2.drawImage(heart_full, x, y, null);
		    x += gp.tileSize;
		}
		for(int i = 0; i < halfHearts; i++) {
		    g2.drawImage(heart_half, x, y, null);
		    x += gp.tileSize;
		}
		
		// DRAW MAX MANA
		x = (gp.tileSize/2)-5;			// RESET
		y = (int)(gp.tileSize*1.5);	// RESET
		maxCount = 0;				// RESET
		
		while (maxCount < gp.player.maxMana) {
			g2.drawImage(crystal_blank, x, y, null);
			maxCount++;
			x += 35;
		}
		
		// DRAW CURRENT MANA
		x = (gp.tileSize/2)-5;		// RESET
		y = (int)(gp.tileSize*1.5);	// RESET
		maxCount = 0;				// RESET
		
		while (maxCount < gp.player.mana) {
			g2.drawImage(crystal_full, x, y, null);
			maxCount++;
			x += 35;
		}
	}
	
	public void drawMessage() {
		
		int messageX = gp.tileSize;
		int messageY = gp.tileSize*4;
		g2.setFont(g2.getFont().deriveFont(Font.BOLD,32F));
		
		for (int i = 0; i < message.size(); i++) {
			
			if (message.get(i) != null) {
				
				g2.setColor(Color.black);
				g2.drawString(message.get(i), messageX+2, messageY+2);
				g2.setColor(Color.white);
				g2.drawString(message.get(i), messageX, messageY);
				
				int counter = messageCounter.get(i) + 1; 	// message counter++
				messageCounter.set(i, counter);				// set the counter to the array
				messageY += 50;
				
				if (messageCounter.get(i) > 180) {
					message.remove(i);
					messageCounter.remove(i);
				}
			}
		}
		
	}
	public void drawTitleScreen() {
		
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
	
	public void drawCharacterScreen() {
		
		// CREATE A FRAME
		final int frameX = gp.tileSize*2;
		final int frameY = gp.tileSize;
		final int frameWidth = gp.tileSize*5;
		final int frameHeight = gp.tileSize*10;
		drawSubWindow(frameX, frameY, frameWidth, frameHeight);
		
		// TEXT
		g2.setColor(Color.white);
		g2.setFont(g2.getFont().deriveFont(32F));
		
		int textX = frameX + 20;
		int textY = frameY + gp.tileSize;
		final int lineHeight = 35;
		
		// NAMES
		final String[] names = {
		        "level",			// 0
		        "Life",				// 1
		        "Mana",				// 2
		        "Strength",			// 3
		        "Dexterity",		// 4
		        "Attack",			// 5
		        "Defence",			// 6
		        "Exp",				// 7
		        "Next Level ",		// 8
		        "Coin",				// 9
		        "Weapon",			// 10
		        "shield"			// 11
		};
		for (int i = 0; i < names.length; i++) {
			g2.drawString(names[i], textX, textY);			
			if (i == 9) {
				textY += lineHeight+10;
			}else if (i == 10) {
				textY += lineHeight+15;
			}else {
				textY += lineHeight;
			}
		}
		
		// Reset textY
		textY = frameY + gp.tileSize;
		// 向右 對齊
		int tailX = (frameX + frameWidth) - 30;
		
		// VALUES
		String[] values = { 
				String.valueOf(gp.player.level),
				String.valueOf(gp.player.life) + "/" + String.valueOf(gp.player.maxLife) ,
				String.valueOf(gp.player.mana) + "/" + String.valueOf(gp.player.maxMana) ,
				String.valueOf(gp.player.strength),
				String.valueOf(gp.player.dexterity),
				String.valueOf(gp.player.attack),
				String.valueOf(gp.player.defense),
				String.valueOf(gp.player.exp),
				String.valueOf(gp.player.nextLevelExp),
				String.valueOf(gp.player.coin)
		};
		for (int i = 0; i < values.length; i++) {
			textX = getXforAlignToRightText(values[i], tailX);
			g2.drawString(values[i], textX, textY);
			textY += lineHeight;
		}
		g2.drawImage(gp.player.currentWeapon.down1, tailX - gp.tileSize, textY - 24, null);
		textY += gp.tileSize;
		g2.drawImage(gp.player.currentShield.down1, tailX - gp.tileSize, textY - 24, null);
	}
	
	public void drawInventory() {
		
		// CREATE A FRAME
		final int frameX = gp.tileSize*12;
		final int frameY = gp.tileSize;
		final int frameWidth = gp.tileSize*6;
		final int frameHeight = gp.tileSize*5;
		drawSubWindow(frameX, frameY, frameWidth, frameHeight);
		
		// SLOT
		final int slotXstart = frameX +20;
		final int slotYstart = frameY +20;
		int slotX = slotXstart;
		int slotY = slotYstart;
		int slotSize = gp.tileSize +3;
		
		// DRAW PLAYER'S ITEMS
		for (int i = 0; i < gp.player.inventory.size(); i++) {
			
			// EQUIP CURSOR
			if (gp.player.inventory.get(i) == gp.player.currentWeapon || 
					gp.player.inventory.get(i) == gp.player.currentShield) {
				g2.setColor(new Color(240, 190, 90));
				g2.fillRoundRect(slotX, slotY, gp.tileSize, gp.tileSize, 10, 10);
			}
			
			g2.drawImage(gp.player.inventory.get(i).down1, slotX, slotY, null);
			
			slotX += slotSize;
			
			if (i % 5 == 4) {
				slotX = slotXstart;
				slotY += slotSize;
			}
		}

		// CURSOR
		int cursorX = slotXstart + (slotSize * slotCol);
		int cursorY = slotYstart + (slotSize * slotRow);
		int cursorWidth = gp.tileSize;
		int cursorHeight = gp.tileSize;		
		// DRAW CURSOR
		g2.setColor(Color.white);
		g2.setStroke(new BasicStroke(3));
		g2.drawRoundRect(cursorX, cursorY, cursorWidth, cursorHeight, 10, 10);
		
		// DESCRIPTION FRAME
		int dFrameX = frameX;
		int dFrameY = frameY + frameHeight;
		int dFrameWidth = frameWidth;
		int dFrameHeight = gp.tileSize*3;
		
		
		// DRAW DESCRIPTION TEXT
		int textX = dFrameX +20;
		int textY = dFrameY + gp.tileSize;
		g2.setFont(g2.getFont().deriveFont(28F));
		
		int itemIndex = getItemIndexOnSolt();
		if (itemIndex < gp.player.inventory.size()) {
			
			drawSubWindow(dFrameX, dFrameY, dFrameWidth, dFrameHeight);
			
			for (String line : gp.player.inventory.get(itemIndex).description.split("\n")) {
				
				g2.drawString(line, textX, textY);
				textY += 32;
			}
						
		}
	}
	
	public void drawGameOverScreen() {
		
		g2.setColor(new Color(0, 0, 0, 150));
		g2.fillRect(0, 0, gp.screenWidth, gp.screenHeight);
		
		int x;
		int y; 
		String text;
		g2.setFont(g2.getFont().deriveFont(Font.BOLD, 110f));
		
		text = "Game Over";
		// SHADOW
		g2.setColor(Color.black);
		x = getXforCenteredText(text);
		y = gp.tileSize * 4;
		g2.drawString(text, x, y);
		// MAIN
		g2.setColor(Color.white);
		g2.drawString(text, x-4, y-4);
		
		// Retry
		g2.setFont(g2.getFont().deriveFont(50f));
		text = "Retry";
		x = getXforCenteredText(text);
		y += gp.tileSize * 4;
		g2.drawString(text, x, y);
		if (commandNum == 0) {
			g2.drawString(">", x-40, y);
		}
		// Back to title screen
		text = "Quit";
		x = getXforCenteredText(text);
		y += 55;
		g2.drawString(text, x, y);
		if (commandNum == 1) {
			g2.drawString(">", x-25, y);
		}
	}
	
	public void drawOptionsScreen() {

		g2.setColor(Color.white);
		g2.setFont(g2.getFont().deriveFont(32F));
		
		// SUB WINDOW
		int frameX = gp.tileSize*6;
		int frameY = gp.tileSize;
		int frameWidth = gp.tileSize*8;
		int frameHeight = gp.tileSize*10;
		drawSubWindow(frameX, frameY, frameWidth, frameHeight);
		
		switch (subState) {
			case 0: options_top(frameX, frameY); break;
			case 1: options_fullScreenNotification(frameX, frameY); break;
			case 2: options_control(frameX, frameY); break;
			case 3: options_endGameConfirmation(frameX, frameY); break;
		}
		
		gp.keyH.enterPressed = false;
		
	}
	
	public void options_top(int frameX, int frameY) {
		
		int textX, textY;
		
		// TITLE
		String text = "Option";
		textX = getXforCenteredText(text);
		textY = frameY + gp.tileSize;
		g2.drawString(text, textX, textY);
		
		// FULL SCREEN　ON/OFF
		textX = frameX + gp.tileSize;
		textY += gp.tileSize*2;
		g2.drawString("Full Screen", textX, textY);
		if (commandNum == 0) {
			g2.drawString(">", textX-25, textY);
			if (gp.keyH.enterPressed == true) {
				if (gp.fullScreenOn == false) {
					gp.fullScreenOn = true;
				}else if (gp.fullScreenOn == true) {
					gp.fullScreenOn = false;
				}
				subState = 1;
			}
		}
		
		// MUSIC
		textY += gp.tileSize;
		g2.drawString("Music", textX, textY);
		if (commandNum == 1) {
			g2.drawString(">", textX-25, textY);
		}
		
		// SE
		textY += gp.tileSize;
		g2.drawString("SE", textX, textY);
		if (commandNum == 2) {
			g2.drawString(">", textX-25, textY);
		}
		
		// CONTROL
		textY += gp.tileSize;
		g2.drawString("Control", textX, textY);
		if (commandNum == 3) {
			g2.drawString(">", textX-25, textY);
			if (gp.keyH.enterPressed == true) {
				subState = 2;
				commandNum = 0;
			}
		}
		
		// END GAME
		textY += gp.tileSize;
		g2.drawString("End Game", textX, textY);
		if (commandNum == 4) {
			g2.drawString(">", textX-25, textY);
			if (gp.keyH.enterPressed == true) {
				subState = 3;
				commandNum = 0;
			}
		}
		
		// BACK
		textY += gp.tileSize*2;
		g2.drawString("Back ", textX, textY);
		if (commandNum == 5) {
			g2.drawString(">", textX-25, textY);
			if (gp.keyH.enterPressed == true) {
				gp.gameState = gp.playState;
				commandNum = 0;
				gp.config.saveConfig();
			}
		}
		
		// FULL SCREEN CHECK BOX
		textX = frameX + (int)(gp.tileSize*4.5);
		textY = frameY + gp.tileSize*2 + 24;
		g2.setStroke(new BasicStroke(3));
		g2.drawRect(textX, textY, 24, 24);
		if (gp.fullScreenOn == true) {
			g2.fillRect(textX, textY, 24, 24);
		}
		
		// MUSIC VOLUME
		textY += gp.tileSize; 
		g2.drawRect(textX, textY, 120, 24);	// 120/5
		int volumeWidth = 24 * gp.music.volumeScale;
		g2.fillRect(textX, textY, volumeWidth, 24);
		
		// SE VOLUME
		textY += gp.tileSize; 
		g2.drawRect(textX, textY, 120, 24);
		volumeWidth = 24 * gp.se.volumeScale;
		g2.fillRect(textX, textY, volumeWidth, 24);
		
	}
	
	public void options_fullScreenNotification(int frameX, int frameY) {
		
		int textX = frameX + gp.tileSize;
		int textY = frameY + gp.tileSize*3;
		
		currentDialogue = "The change will take \neffect after restarting \nthe game.";
		for (String line : currentDialogue.split("\n")) {
			g2.drawString(line, textX, textY);
			textY += 40;
		}
		
		// BACK
		textY = frameY + gp.tileSize*9;
		g2.drawString("Back", textX, textY);
		if (commandNum == 0) {
			g2.drawString(">", textX-25, textY);
			if (gp.keyH.enterPressed == true) {
				subState = 0;
				commandNum = 0;
			}
		}
		
	}
	public void options_control(int frameX, int frameY) {
		
		int textX, textY;
		
		// TITLE
		String text = "Control";
		textX = getXforCenteredText(text);
		textY = frameY + gp.tileSize;
		g2.drawString(text, textX, textY);
		
		textX = frameX + gp.tileSize;
		textY += gp.tileSize;
		g2.drawString("Move", textX, textY);				textY += gp.tileSize*2;
		g2.drawString("Confirm/Attack", textX, textY);		textY += gp.tileSize;
		g2.drawString("Shoot/Cast", textX, textY);			textY += gp.tileSize;
		g2.drawString("Character Screen", textX, textY);	textY += gp.tileSize;
		g2.drawString("Pause", textX, textY);				textY += gp.tileSize;
		g2.drawString("Options", textX, textY);				
		
		textX = frameX + gp.tileSize*6;
		textY = frameY + gp.tileSize*2;
		
		g2.drawString("WASD", textX, textY);				textY += gp.tileSize;
		g2.drawString("↑←↓→", textX-10, textY);				textY += gp.tileSize;
		g2.drawString("ENTER", textX, textY);				textY += gp.tileSize;
		g2.drawString("F", textX, textY);					textY += gp.tileSize;
		g2.drawString("C", textX, textY);					textY += gp.tileSize;
		g2.drawString("P", textX, textY);					textY += gp.tileSize;
		g2.drawString("Esc", textX, textY);					
		
		// Back
		textX = frameX + gp.tileSize;
		textY = frameY + gp.tileSize*9;
		g2.drawString("Back", textX, textY);
		if (commandNum == 0) {
			g2.drawString(">", textX-25, textY);
			if (gp.keyH.enterPressed == true) {
				subState = 0;
				commandNum = 3;
			}
		}
	}
	
	public void options_endGameConfirmation(int frameX, int frameY) {
		
		int textX = frameX + gp.tileSize;
		int textY = frameY + gp.tileSize*3;
		
		currentDialogue = "Quit the game and \nreturn to the title screen?";
		for (String line : currentDialogue.split("\n")) {
			g2.drawString(line, textX, textY);
			textY += 40;
		}
		
		// YES
		String text = "Yes";
		textX = getXforCenteredText(text);
		textY += gp.tileSize*3;
		g2.drawString(text, textX, textY);
		if (commandNum == 0) {
			g2.drawString(">", textX-25, textY);
			if (gp.keyH.enterPressed == true) {
				subState = 0;
				gp.gameState = gp.titleState;
				gp.config.saveConfig();
			}
		}	
		// NO
		text = "No";
		textX = getXforCenteredText(text);
		textY += gp.tileSize;
		g2.drawString(text, textX, textY);
		if (commandNum == 1) {
			g2.drawString(">", textX-25, textY);
			if (gp.keyH.enterPressed == true) {
				subState = 0;
				commandNum = 4;
			}
		}
	}
	
	public int getItemIndexOnSolt() {
		int itemIndex = slotCol + (slotRow*5);
		return itemIndex;
	}
	
	// 繪製子視窗的邊緣外框
	public void drawSubWindow(int x, int y, int width, int height) {
		// 錯誤處理
		if (width <=0 || height <=0) {
			throw new IllegalArgumentException("寬度和高度必須大於 0。");
        }
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
	// 計算給定文本在螢幕上水平置中的 X 座標
	public int getXforCenteredText(String text) {
		// 錯誤處理
		if (text == null) {
            return 0; // 或者拋出異常
        }
		FontRenderContext frc = g2.getFontRenderContext();
        Rectangle2D bounds = g2.getFont().getStringBounds(text, frc);
        int textWidth = (int) bounds.getWidth();
		return gp.screenWidth/2 - textWidth/2;
		
	}
	// 計算給定文本在指定 tailX 位置右對齊的 X 座標
	public int getXforAlignToRightText(String text, int tailX) {
		// 錯誤處理
		if (text == null) {
            return 0; // 或者拋出異常
        }
		FontRenderContext frc = g2.getFontRenderContext();
        Rectangle2D bounds = g2.getFont().getStringBounds(text, frc);
        int textWidth = (int) bounds.getWidth();
		return tailX - textWidth;
		
	}
}
	
