package pkg;

import java.awt.image.BufferedImage;

public class Dot{

	BufferedImage img;
	public int orientation = 0, x, y, index = 0;;
	boolean special = false, head = false, tail = false;
	
	Dot() {
	}

	Dot(int oldDirection, int direction, int x, int y) {
		this.x = x;
		this.y = y;
		switch ((oldDirection)*10+direction) {
		case 41:
		case 5:  {index = 0; break;}
		case 44:
		case 55: {index = 1; break;}
		case 51:
		case 4:  {index = 2; break;}
		case 15:
		case 40:  {index = 3; break;}
		case 14:
		case 50:  {index = 4; break;}
		case 0:
		case 11: {index = 5; break;}
		}
		this.orientation = direction;
		this.img = Game.dots[index];
	}
	
	public void toggle() {
		special = !special;
		if (special) {
			img = Game.dots[11];
		}
		else img = Game.dots[6];
	}
	
	Dot(boolean special, int x, int y) {
		this.x = x;
		this.y = y;
		this.special = !special;
		this.toggle();
	}
	
	
}
