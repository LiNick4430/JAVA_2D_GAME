package main;

import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

import entity.Entity;
import main.GamePanel.GameState;

public class KeyHandler implements KeyListener{
	
	GamePanel gp;	
	public boolean upPressed, downPressed, leftPressed, rightPressed, enterPressed, shotKeyPressed;
	// DEBUG
	boolean checkDrawTime = false;
	
	
	public KeyHandler(GamePanel gp) {
		this.gp = gp;
	}
	

	@Override
	public void keyTyped(KeyEvent e) { // 當輸入一個 Unicode 字元時調用。
	}

	@Override
	public void keyPressed(KeyEvent e) {  // 當按鍵被按下時調用。

		int code = e.getKeyCode();
		
		switch (gp.gameState) {
			
			case TITLE:				// TITLE STATE
				titleState(code);
				break;			
			case PLAY:				// PLAY STATE
				playState(code);
				break;
			case PAUSE:				// PAUSE STATE
				pauseState(code);
				break;
			case DIALOGUE:			// DIALOGUE STATE
				dialogueState(code);
				break;
			case CHARACTER:			// CHARACTER STATE
				characterState(code);
				break;
			case OPTIONS:			// OPTIONS STATE
				optionsState(code);
				break;
			case GAMEOVER:			// GAME OVER STATE
				gameoOverState(code);
				break;
			case TRADE:				// TRADE STATE
				tradeState(code);
				break;
				
			default:
				System.err.println("Unknown game state: " + gp.gameState);
				break;

		}
	}
	
	public void titleState(int code) {
		
		if (code == KeyEvent.VK_W || code == KeyEvent.VK_UP) {
			gp.ui.commandNum--;
			if (gp.ui.commandNum < 0) {
				gp.ui.commandNum = 2;
			}
		}
		if (code == KeyEvent.VK_S || code == KeyEvent.VK_DOWN) {
			gp.ui.commandNum++;
			if (gp.ui.commandNum > 2) {
				gp.ui.commandNum = 0;
			}
		}
		if (code == KeyEvent.VK_ENTER) {
			switch (gp.ui.commandNum) {
				case 0:
					gp.restart();
					gp.gameState = GameState.PLAY;
					gp.playMusic(0);
					break;
				case 1:
					// add later
					break;
				case 2:
					System.exit(0);
					break;
					
			}
		}
	}
	
	public void playState(int code) {
		
		if (code == KeyEvent.VK_W || code == KeyEvent.VK_UP) {
			upPressed = true;
		}
		if (code == KeyEvent.VK_S || code == KeyEvent.VK_DOWN) {
			downPressed = true;
		}
		if (code == KeyEvent.VK_A || code == KeyEvent.VK_LEFT) {
			leftPressed = true;
		}
		if (code == KeyEvent.VK_D || code == KeyEvent.VK_RIGHT) {
			rightPressed = true;
		}
		if (code == KeyEvent.VK_ENTER) {
			enterPressed = true;
		}
		if (code == KeyEvent.VK_F) {
			shotKeyPressed = true;
		}	
		
		// GAME STATE
		if (code == KeyEvent.VK_P) {
			gp.gameState = GameState.PAUSE;
		}
		
		// CHARACTER STATE
		if (code == KeyEvent.VK_C) {
			gp.gameState = GameState.CHARACTER;
		}
		
		// OPTION STATE
		if (code == KeyEvent.VK_ESCAPE) {
			gp.gameState = GameState.OPTIONS;
		}
		
		// DEBUG
		if (code == KeyEvent.VK_T) {
			if (checkDrawTime == false) {
				checkDrawTime = true;
			} else if (checkDrawTime == true) {
				checkDrawTime = false;
			}
		}
		if (code == KeyEvent.VK_R) {
			switch (gp.currentMap) {
				case 0: gp.tileM.loadMap("maps/WorldV3.txt", 0); break;
				case 1: gp.tileM.loadMap("maps/interior01.txt", 1); break;
			}
		}
	}
	
	public void pauseState(int code) {
		
		if (code == KeyEvent.VK_P) {
			gp.gameState = GameState.PLAY;
		}
	}
	
	public void dialogueState(int code) {
		
		if (code == KeyEvent.VK_ENTER) {
			gp.gameState = GameState.PLAY;
		}
	}
	
	public void characterState(int code) {
		
		if (code == KeyEvent.VK_C || code == KeyEvent.VK_ESCAPE) {
			gp.ui.playerSlotCol = 0;
			gp.ui.playerSlotRow = 0;
			gp.gameState = GameState.PLAY;
		}
		if (code == KeyEvent.VK_ENTER) {
			gp.player.selectItem();
		}
		
		playInventory(code);
		
	}
	
	public void optionsState(int code) {
		
		if (code == KeyEvent.VK_ESCAPE) {
			gp.gameState = GameState.PLAY;
			gp.ui.subState = 0;
			gp.config.saveConfig();
		}
		if (code == KeyEvent.VK_ENTER) {
			enterPressed = true;
		}
		
		int maxCommandNum = 0;
		switch (gp.ui.subState) {
			case 0:
				maxCommandNum = 5;
				break;
			case 3:
				maxCommandNum = 1;
				break;
		}
		
		if (code == KeyEvent.VK_W || code == KeyEvent.VK_UP) {
			gp.ui.commandNum--;
			gp.playSE(9);
			if (gp.ui.commandNum < 0) {
				gp.ui.commandNum = maxCommandNum;
			}
		}
		if (code == KeyEvent.VK_S || code == KeyEvent.VK_DOWN) {
			gp.ui.commandNum++;
			gp.playSE(9);
			if (gp.ui.commandNum > maxCommandNum) {
				gp.ui.commandNum = 0;
			}
		}
		if (code == KeyEvent.VK_A || code == KeyEvent.VK_LEFT) {
			if (gp.ui.subState == 0) {
				if (gp.ui.commandNum == 1 && gp.music.volumeScale > 0) {
					gp.music.volumeScale--;
					gp.music.checkVolume();
					gp.playSE(9);
				}
				if (gp.ui.commandNum == 2 && gp.se.volumeScale > 0) {
					gp.se.volumeScale--;
					gp.playSE(9);
				}
			}
		}
		if (code == KeyEvent.VK_D || code == KeyEvent.VK_RIGHT) {
			if (gp.ui.subState == 0) {
				if (gp.ui.commandNum == 1 && gp.music.volumeScale < 5) {
					gp.music.volumeScale++;
					gp.music.checkVolume();
					gp.playSE(9);
				}
				if (gp.ui.commandNum == 2 && gp.se.volumeScale < 5) {
					gp.se.volumeScale++;
					gp.playSE(9);
				}
			}
		}
		
	}
	
	public void gameoOverState(int code) {
		if (code == KeyEvent.VK_W || code == KeyEvent.VK_UP) {
			gp.ui.commandNum--;
			gp.playSE(9);
			if (gp.ui.commandNum < 0) {
				gp.ui.commandNum = 1;
			}
		}
		if (code == KeyEvent.VK_S || code == KeyEvent.VK_DOWN) {
			gp.ui.commandNum++;
			gp.playSE(9);
			if (gp.ui.commandNum > 1) {
				gp.ui.commandNum = 0;
			}
		}
		if (code == KeyEvent.VK_ENTER) {
			if (gp.ui.commandNum == 0) {
				gp.gameState = GameState.PLAY;
				gp.retry();
				gp.playMusic(0);
			}else if (gp.ui.commandNum == 1) {
				gp.gameState = GameState.PLAY;
				gp.restart();
			}
		}
	}
	
	public void tradeState(int code) {
		if (code == KeyEvent.VK_ENTER) {
			enterPressed = true;
		}
		
		if (gp.ui.subState == 0) {
			if (code == KeyEvent.VK_W || code == KeyEvent.VK_UP) {
				gp.ui.commandNum--;
				gp.playSE(9);
				if (gp.ui.commandNum < 0) {
					gp.ui.commandNum = 2;
				}
			}
			if (code == KeyEvent.VK_S || code == KeyEvent.VK_DOWN) {
				gp.ui.commandNum++;
				gp.playSE(9);
				if (gp.ui.commandNum > 2) {
					gp.ui.commandNum = 0;
				}
			}
		}
		if (gp.ui.subState == 1) {
			npcInventory(code);
			if (code == KeyEvent.VK_ESCAPE) {
				gp.ui.npcSlotCol = 0;
				gp.ui.npcSlotRow = 0;
				gp.ui.subState = 0;
			}			
		}
		if (gp.ui.subState == 2) {
			playInventory(code);
			if (code == KeyEvent.VK_ESCAPE) {
				gp.ui.playerSlotCol = 0;
				gp.ui.playerSlotRow = 0;
				gp.ui.subState = 0;
			}			
		}
		
	}
	
	public void playInventory(int code) {
		
		 inventoryMove(code, gp.player);
	}
	
	public void npcInventory(int code) {
		
		inventoryMove(code, gp.ui.npc);
	}
	
	private void inventoryMove(int code, Entity entity) {
		
		// 臨時儲存 的行 (newSlotRow) 和列 (newSlotCol) 索引
		int slotCol = 0;
		int slotRow = 0;		
		if (entity == gp.player) {
			slotCol = gp.ui.playerSlotCol;
			slotRow = gp.ui.playerSlotRow;
		}else if (entity == gp.ui.npc) {
			slotCol = gp.ui.npcSlotCol;
			slotRow = gp.ui.npcSlotRow;
		}
		// 新 的行 (newSlotRow) 和列 (newSlotCol) 索引
		int newSlotCol = slotCol;
		int newSlotRow = slotRow; 

		// 計算移動後 新的 行 (newSlotRow) 和列 (newSlotCol) 索引
		if (code == KeyEvent.VK_W || code == KeyEvent.VK_UP) {
			if (newSlotRow != 0) {
				newSlotRow--;
				gp.playSE(9);
			}		
		}
		if (code == KeyEvent.VK_S || code == KeyEvent.VK_DOWN) {
			if (newSlotRow != 3) {
				newSlotRow++;
				gp.playSE(9);
			}
		}
		if (code == KeyEvent.VK_A || code == KeyEvent.VK_LEFT) {
			if (newSlotCol != 0) {
				newSlotCol--;
				gp.playSE(9);
			}
		}
		if (code == KeyEvent.VK_D || code == KeyEvent.VK_RIGHT) {
			if (newSlotCol != 4) {
				newSlotCol++;
				gp.playSE(9);
			}
		}
		
		// 更新對應的 UI 變數
		if (entity == gp.player) {
			gp.ui.playerSlotCol = newSlotCol;
			gp.ui.playerSlotRow = newSlotRow;
		}else if (entity == gp.ui.npc) {
			gp.ui.npcSlotCol = newSlotCol;
			gp.ui.npcSlotRow = newSlotRow;
		}
	}

	@Override
	public void keyReleased(KeyEvent e) { // 當按鍵被釋放時調用。
		
		int code = e.getKeyCode();
		if (code == KeyEvent.VK_W || code == KeyEvent.VK_UP) {
			upPressed = false;
		}
		if (code == KeyEvent.VK_S || code == KeyEvent.VK_DOWN) {
			downPressed = false;
		}
		if (code == KeyEvent.VK_A || code == KeyEvent.VK_LEFT) {
			leftPressed = false;
		}
		if (code == KeyEvent.VK_D || code == KeyEvent.VK_RIGHT) {
			rightPressed = false;
		}		
		if (code == KeyEvent.VK_F) {
			shotKeyPressed = false;
		}
	}
	
}
