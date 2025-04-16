package main;

import javax.swing.JFrame;

public class Main {
	
	public static JFrame window;	

	public static void main(String[] args) {
		
		window = new JFrame();
		window.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);  // 當使用者點擊關閉（“x”）按鈕時，視窗就可以正確關閉。
		window.setResizable(false); 		// 禁止使用者調整視窗的大小
		window.setTitle("2D Adventure"); 	// 視窗標題
		
		
		GamePanel gamePanel = new GamePanel();
		window.add(gamePanel);
		
		gamePanel.config.loadConfig();
		if (gamePanel.fullScreenOn == true) {
			window.setUndecorated(true); 		// 無 標準作業系統 視窗裝飾,  須在 JFrame 變為 可顯示 之前調用（即在調用 setVisible(true) 之前）
		}
		
		window.pack(); // 調整視窗大小以適合組件 Adjust window size to fit components
		
		window.setLocationRelativeTo(null); // 將視窗置於螢幕的中央
		window.setVisible(true); 			// 讓視窗顯示在螢幕上
		
		gamePanel.setupGame();		 // 設定遊戲
		gamePanel.startGameThread(); // 啟動遊戲執行緒
	}

}
