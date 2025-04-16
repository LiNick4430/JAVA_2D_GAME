package main;

import java.io.IOException;
import java.net.URL;

import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import javax.sound.sampled.FloatControl;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.UnsupportedAudioFileException;

public class Sound {

	Clip clip;						// Clip 物件可以將音訊資料載入到記憶體中，然後進行播放、停止、暫停、循環等操作。
	URL soundURL[] = new URL[30];
	FloatControl fc;
	int volumeScale;
	float volume;
	
	public Sound() {
		
		final String[] soundFileNames = {
		        "BlueBoyAdventure",	// 0
		        "coin",				// 1
		        "powerup",			// 2
		        "unlock",			// 3
		        "fanfare",			// 4
		        "hitmonster",		// 5
		        "receivedamage",	// 6
		        "swingweapon",		// 7
		        "levelup",			// 8
		        "cursor",			// 9
		        "burning",			// 10
		        "cuttree",			// 11
		        "gameover",			// 12
		        "stairs"			// 13
		    };

		    loadSoundURLs(soundFileNames);
	}
	
	private void loadSoundURLs(String[] soundFileNames) {
	    for (int i = 0; i < soundFileNames.length; i++) {
	        URL url = getClass().getClassLoader().getResource("sound/" + soundFileNames[i] + ".wav");
	        if (url == null) {
                System.err.println("找不到音效檔案：sound/" + soundFileNames[i] + ".wav");
            }
	        soundURL[i] = url;
	    }
	}
	
	public void setFile(int i) {
        try {
            if (soundURL[i] != null) {
                AudioInputStream ais = AudioSystem.getAudioInputStream(soundURL[i]);
                clip = AudioSystem.getClip();
                clip.open(ais);
                fc = (FloatControl)clip.getControl(FloatControl.Type.MASTER_GAIN);
                checkVolume();
            } else {
                System.err.println("音效檔案 URL 為 null，索引：" + i);
            }
        } catch (UnsupportedAudioFileException e) {
            System.err.println("不支援的音訊檔案格式：" + e.getMessage());
            e.printStackTrace();
        } catch (IOException e) {
            System.err.println("讀取音訊檔案時發生 I/O 錯誤：" + e.getMessage());
            e.printStackTrace();
        } catch (LineUnavailableException e) {
            System.err.println("無法取得音訊線路：" + e.getMessage());
            e.printStackTrace();
        } catch (Exception e) {
            System.err.println("發生未知錯誤：" + e.getMessage());
            e.printStackTrace();
        }
    }
	
	public void play() {
		
		if (clip != null) {
            clip.start();
        }
	}

	public void loop() {
		
		if (clip != null) {
            clip.loop(Clip.LOOP_CONTINUOUSLY);
        }
	}

	public void stop() {
		
		if (clip != null) {
            clip.stop();
        }
	}
	
	public void checkVolume() {
		
		switch (volumeScale) {
			case 0: volume = -80f; break;
			case 1: volume = -20f; break;
			case 2: volume = -12f; break;
			case 3: volume = -5f; break;
			case 4: volume = 1f; break;
			case 5: volume = 6f; break;

		}
		fc.setValue(volume);
	}
}
