package main;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

public class Config {

	GamePanel gp;
	private static final boolean DEFAULT_FULLSCREEN = false;
	private static final int DEFAULT_MUSIC_VOLUME = 3;
	private static final int DEFAULT_SE_VOLUME = 3;
	
	public Config(GamePanel gp) {
		this.gp = gp;
		
		
	}
	
	public void saveConfig() {
		
		try {
			BufferedWriter bw = new BufferedWriter(new FileWriter("config.txt"));
			
			// Full screen
			if (gp.fullScreenOn == true) {
				bw.write("On");
			}
			if (gp.fullScreenOn == false) {
				bw.write("Off");
			}
			bw.newLine();
			
			// Music volume
			bw.write(String.valueOf(gp.music.volumeScale));
			bw.newLine();
			
			// SE volume
			bw.write(String.valueOf(gp.se.volumeScale));
			bw.newLine();
			
			bw.close();			
			
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public void loadConfig() {
		
		try {
			BufferedReader br = new BufferedReader(new FileReader("config.txt"));
			
			try {
				String s = br.readLine();
				// Full screen
				if (s.equals("On")) {
					gp.fullScreenOn = true;
				}
				if (s.equals("Off")) {
					gp.fullScreenOn = false;
				}
				// Music volume
				s = br.readLine();
				gp.music.volumeScale = Integer.parseInt(s);
				
				// SE volume
				s = br.readLine();
				gp.se.volumeScale = Integer.parseInt(s);
				
				br.close();
				
			} catch (IOException e) {
				e.printStackTrace();
			}
			
		} catch (FileNotFoundException e) {
			gp.fullScreenOn = DEFAULT_FULLSCREEN;
			gp.music.volumeScale = DEFAULT_MUSIC_VOLUME;
			gp.se.volumeScale = DEFAULT_SE_VOLUME;
			
			e.printStackTrace();
		}
	}
}
