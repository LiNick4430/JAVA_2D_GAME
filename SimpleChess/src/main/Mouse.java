package main;

import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

public class Mouse extends MouseAdapter{
	
	public int x, y;
	public boolean pressed;
	
	@Override
	public void mousePressed(MouseEvent e) {
		pressed = true;
	}
	
	@Override
	public void mouseReleased(MouseEvent e) {
		pressed = false;
	}
	
	@Override		// 這個方法在滑鼠按鈕被按下並且滑鼠被拖曳時持續觸發。在這裡，它會更新 x 和 y 屬性，以記錄滑鼠拖曳時的最新位置。
	public void mouseDragged(MouseEvent e) {
		x = e.getX();
		y = e.getY();
	}
	
	@Override		// 這個方法在滑鼠在視窗中移動（但沒有按下任何按鈕）時持續觸發。在這裡，它也會更新 x 和 y 屬性，以記錄滑鼠移動到的最新位置。
	public void mouseMoved(MouseEvent e) {
		x = e.getX();
		y = e.getY();
	}
}
