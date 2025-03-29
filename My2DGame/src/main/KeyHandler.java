package main;

import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

public class KeyHandler implements KeyListener{
	
	GamePanel gp;	
	public boolean upPressed, downPressed, leftPressed, rightPressed, enterPressed;
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
		
		// TITLE STATE
		if (gp.gameState == gp.titleState) {
			
			if (gp.ui.titleScreenState == 0) {

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
					/*
					if (gp.ui.commandNum == 0) {
						gp.ui.titleScreenState = 1;
					}else if (gp.ui.commandNum == 1) {
						// add later
					}else if (gp.ui.commandNum == 2) {
						System.exit(0);
					}
					*/
					switch (gp.ui.commandNum) {
						case 0:
							gp.ui.titleScreenState = 1;
							break;
						case 1:
							// add later
							break;
						case 2:
							System.exit(0);
							break;
							
					}
				}
				
			}else if (gp.ui.titleScreenState == 1) {

				if (code == KeyEvent.VK_W || code == KeyEvent.VK_UP) {
					gp.ui.commandNum--;
					if (gp.ui.commandNum < 0) {
						gp.ui.commandNum = 3;
					}
				}
				if (code == KeyEvent.VK_S || code == KeyEvent.VK_DOWN) {
					gp.ui.commandNum++;
					if (gp.ui.commandNum > 3) {
						gp.ui.commandNum = 0;
					}
				}
				if (code == KeyEvent.VK_ENTER) {
					/*
					if (gp.ui.commandNum == 0) {
						System.out.println("Do some fighter specific stuff!");
						gp.gameState = gp.playState;
						gp.playMusic(0);
					}else if (gp.ui.commandNum == 1) {
						System.out.println("Do some thief specific stuff!");
						gp.gameState = gp.playState;
						gp.playMusic(0);
					}else if (gp.ui.commandNum == 2) {
						System.out.println("Do some sorcerer specific stuff!");
						gp.gameState = gp.playState;
						gp.playMusic(0);
					}else if (gp.ui.commandNum == 3) {
						gp.ui.titleScreenState = 0;
						gp.ui.commandNum = 0;
					}
					*/
					switch (gp.ui.commandNum) {
						case 0:
							System.out.println("Do some fighter specific stuff!");
							gp.gameState = gp.playState;
							gp.playMusic(0);
							break;
						case 1:
							System.out.println("Do some thief specific stuff!");
							gp.gameState = gp.playState;
							gp.playMusic(0);
							break;
						case 2:
							System.out.println("Do some sorcerer specific stuff!");
							gp.gameState = gp.playState;
							gp.playMusic(0);
							break;
						case 3:
							gp.ui.titleScreenState = 0;
							gp.ui.commandNum = 0;
							break;	
					}
					
				}
			}
			
		}
		
		// PLAY STATE
		if (gp.gameState == gp.playState) {
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
			
			// GAME STATE
			if (code == KeyEvent.VK_P) {
				gp.gameState = gp.pauseState;
			}
			
			// DEBUG
			if (code == KeyEvent.VK_T) {
				if (checkDrawTime == false) {
					checkDrawTime = true;
				} else if (checkDrawTime == true) {
					checkDrawTime = false;
				}
			}
		}
		// PAUSE STATE
		else if (gp.gameState == gp.pauseState) {
			if (code == KeyEvent.VK_P) {
				gp.gameState = gp.playState;
			}
		}
		// DIALOGUE STATE
		else if (gp.gameState == gp.dialogueState) {
			if (code == KeyEvent.VK_ENTER) {
				gp.gameState = gp.playState;
			}
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
		
	}
	
}
