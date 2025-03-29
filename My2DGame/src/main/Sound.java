package main;

import java.net.URL;

import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;

public class Sound {

	Clip clip;						// Clip 物件可以將音訊資料載入到記憶體中，然後進行播放、停止、暫停、循環等操作。
	URL soundURL[] = new URL[30];
	
	public Sound() {
		
		soundURL[0] = getClass().getClassLoader().getResource("sound/BlueBoyAdventure.wav");
		soundURL[1] = getClass().getClassLoader().getResource("sound/coin.wav");
		soundURL[2] = getClass().getClassLoader().getResource("sound/powerup.wav");
		soundURL[3] = getClass().getClassLoader().getResource("sound/unlock.wav");
		soundURL[4] = getClass().getClassLoader().getResource("sound/fanfare.wav");		
	}
	
	public void setFile(int i) {
		
		try {
			AudioInputStream ais = AudioSystem.getAudioInputStream(soundURL[i]); 	// 這行程式碼用於從指定的 URL 獲取音訊資料的輸入流。 例如 WAV、AU、AIFF 等
			clip = AudioSystem.getClip();		// 創建一個 Clip 物件
			clip.open(ais);						// 載入音訊檔案
			
		} catch (Exception e) {
			System.out.println("NO MUSIC");
		}
	}
	public void play() {
		
		clip.start();
	}
	public void loop() {
		
		clip.loop(clip.LOOP_CONTINUOUSLY);	// 無限循環播放
	}
	public void stop() {
		
		clip.stop();
	}
}
