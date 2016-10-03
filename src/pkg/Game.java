package pkg;

import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.event.ActionEvent;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.ArrayList;

import javax.imageio.ImageIO;
import javax.swing.AbstractAction;
import javax.swing.JComponent;
import javax.swing.JPanel;
import javax.swing.KeyStroke;

public class Game extends JPanel {

	static BufferedImage spriteSheet = null, bg = null;
	static BufferedImage[] dots = new BufferedImage[12];
	public static int points = 0, direction = 0, oldDirection = 0, scale = 25, spriteWidth = 15, snakeLength = 3, acceleration = 0, accelerationThreshold = 5;
	public static Dot dot = null;
	public static String spath, bgpath;

	public static ArrayList<Dot> snake;
	public boolean won = false, GameOver = false;
	public static boolean ready = false, shouldRender = false, moveFlag = false;
	
	Game() {

		setSize(new Dimension(spriteWidth*scale, spriteWidth*scale));
		setBackground(Main.dark);
		Main.pointLabel.setText("Points: "+points);		try {
			spriteSheet = ImageIO.read(this.getClass().getResourceAsStream("res/spriteSheet.png"));
		} catch (IOException e) {
			System.out.println("failure to load spriteSheet");
			e.printStackTrace();
		}
		try {
			bg = ImageIO.read(this.getClass().getResourceAsStream("res/bg.png"));
		} catch (IOException e) {
			System.out.println("failure to load bg");
			e.printStackTrace();
		}
		for (int i = 0; i<12; i++) {
			dots[i] = spriteSheet.getSubimage((i%3)*spriteWidth, (i/3)*spriteWidth, spriteWidth, spriteWidth);
		}
		snake = new ArrayList<Dot>(3);
		class KeyAction extends AbstractAction {
			String key;

			KeyAction(String key) {this.key = key;}
			@Override
			public void actionPerformed(ActionEvent e) {
				if (ready && !moveFlag) {
					oldDirection = direction;
					switch(key) {
					case "UP": direction = 0; break;
					case "DOWN": direction = 1; break;
					case "LEFT": direction = 4; break;
					case "RIGHT": direction = 5; break;
				}
				moveFlag = true;
				if (oldDirection-direction == 1 || oldDirection-direction == -1) GameOver = true;
				}
			}	
		}
		String [] keys= {"UP","DOWN","LEFT","RIGHT"};
		for (int i=0;i<keys.length;i++) {
			String s = keys[i];
			getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(KeyStroke.getKeyStroke(s), s);
			getActionMap().put(s, new KeyAction(s));
		}
	}
	
	public void newGame() {
		setSize(new Dimension(spriteWidth*scale, spriteWidth*scale));
		snake.clear();
		snakeLength = 3;
		try {
			spriteSheet = ImageIO.read(this.getClass().getResourceAsStream(new String("res/"+spath+".png")));
		} catch (IOException e) {
			System.out.println("failure to load spriteSheet");
			e.printStackTrace();
		}
		for (int i = 0; i<12; i++) {
			dots[i] = spriteSheet.getSubimage((i%3)*spriteWidth, (i/3)*spriteWidth, spriteWidth, spriteWidth);
		}
		Main.time = 0;
		Main.timeLabel.setText("Time: 0");
		dot = null;
		points = 0;
		Main.pointLabel.setText("Points: "+points);
		direction = 0; oldDirection = 0;
		for (int i = 0; i<snakeLength; i++) {
			int j = scale-5;
			snake.add(0, new Dot(oldDirection, direction, j, j-i));
			}
		makeDot();
	}
	
	public void move() {

	switch (direction) {
	case 0: {
		snake.set(0, new Dot(oldDirection, direction, snake.get(0).x, snake.get(0).y));
		snake.add(0, new Dot(oldDirection, direction, snake.get(0).x, snake.get(0).y-1));
		break;
		
	}
	case 1: {
		snake.set(0, new Dot(oldDirection, direction, snake.get(0).x, snake.get(0).y));
		snake.add(0, new Dot(oldDirection, direction, snake.get(0).x, snake.get(0).y+1));
		break;
		
	}
	case 4: {
		snake.set(0, new Dot(oldDirection, direction, snake.get(0).x, snake.get(0).y));
		snake.add(0, new Dot(oldDirection, direction, snake.get(0).x-1, snake.get(0).y));
		break;
		
	}
	case 5: {
		snake.set(0, new Dot(oldDirection, direction, snake.get(0).x, snake.get(0).y));
		snake.add(0, new Dot(oldDirection, direction, snake.get(0).x+1, snake.get(0).y));
		break;
	}
	}
	if (snake.size() > snakeLength) snake.remove(snake.size()-1);
	for (int i = 0; i<snake.size(); i++) {
		for (int j = 0; j<snake.size(); j++) {
			if (j != i) {
				if (snake.get(j).x == snake.get(i).x && snake.get(j).y == snake.get(i).y ) GameOver = true;
			}
			}
		}
	if (snake.get(0).x<0||snake.get(0).x>=scale||snake.get(0).y<0||snake.get(0).y>=scale) GameOver = true;
	if (snake.get(0).x == dot.x && snake.get(0).y == dot.y) makeDot();
	oldDirection = direction;
	moveFlag = false;
	}
	
	public void makeDot() {
		if (dot != null) {
			if (dot.special) {snakeLength += 3; points += 3;}
			else {snakeLength++; points++;}
			Main.pointLabel.setText("Points: "+points);
		}
		boolean special = false;
		int r = Main.rand.nextInt(20);
		if (r == 0) special = true;
		boolean valid = true;
		do {
			valid = true;
			r = Main.rand.nextInt(scale*scale);
			for (int i = 0; i<snake.size(); i++) {
				if (snake.get(i).x == r%scale && snake.get(i).y == r/scale) valid = false;
				}
		}
		while (valid == false);
		dot = new Dot(special, r%scale, r/scale);
		System.out.println(dot.x+" "+dot.y);
		acceleration++;
		if (acceleration >= accelerationThreshold) {
			acceleration = 0;
			Main.framesPerMove--;
		}
	}
	
	public void render() {
		repaint();
	}
	
	public void paintComponent(Graphics g) {
		g.drawImage(bg, 0, 0, null);
		if (dot != null)
		g.drawImage(dot.img, dot.x*spriteWidth, dot.y*spriteWidth, null);
		for (int i = 0; i < snake.size(); i++) {
			int pixels = (Main.animationCount*(spriteWidth)/Main.framesPerMove);
				if (i == 0) {
					if (pixels != 0) {
					switch(snake.get(0).orientation) {
					case 0: {g.drawImage(dots[7].getSubimage(0, 0, spriteWidth, pixels),
							snake.get(0).x*spriteWidth, snake.get(1).y*spriteWidth-pixels, null);
					break;}
					case 1: {g.drawImage(dots[8].getSubimage(0, spriteWidth-pixels, spriteWidth,
							pixels), snake.get(0).x*spriteWidth, snake.get(0).y*spriteWidth, null);
					break;}
					case 4: {g.drawImage(dots[9].getSubimage(0, 0, pixels, spriteWidth),
							snake.get(0).x*spriteWidth+(spriteWidth-pixels), snake.get(0).y*spriteWidth, null); 
					break;}
					case 5: {g.drawImage(dots[10].getSubimage(spriteWidth-pixels, 0, pixels, spriteWidth),
							snake.get(0).x*spriteWidth, snake.get(0).y*spriteWidth, null);
					break;}
					}
					}
				}
				else if (i == snake.size()-1) {
					Dot last = snake.get(snake.size()-1);
					if (snake.size() == snakeLength) {
					if (pixels != spriteWidth)
					switch(last.orientation) {
					case 0: {g.drawImage(dots[8].getSubimage(0, pixels, spriteWidth, spriteWidth-pixels),
							last.x*spriteWidth, last.y*spriteWidth, null);
					break;}
					case 1: {g.drawImage(dots[7].getSubimage(0, 0, spriteWidth, spriteWidth-pixels),
							last.x*spriteWidth, last.y*spriteWidth+pixels, null);
					break;}
					case 4: {g.drawImage(dots[10].getSubimage(pixels, 0, spriteWidth-pixels, spriteWidth),
							last.x*spriteWidth, last.y*spriteWidth, null); 
					break;}
					case 5: {g.drawImage(dots[9].getSubimage(0, 0, spriteWidth-pixels, spriteWidth),
							last.x*spriteWidth+pixels, last.y*spriteWidth, null); 
					break;}
					}
					}
					else {
						switch(last.orientation) {
						case 0: {g.drawImage(dots[8],
								last.x*spriteWidth, last.y*spriteWidth, null);
						break;}
						case 1: {g.drawImage(dots[7],
								last.x*spriteWidth, last.y*spriteWidth, null);
						break;}
						case 4: {g.drawImage(dots[10],
								last.x*spriteWidth, last.y*spriteWidth, null); 
						break;}
						case 5: {g.drawImage(dots[9],
								last.x*spriteWidth, last.y*spriteWidth, null); 
						break;}
						}
						
					}
				}
				else g.drawImage(snake.get(i).img, snake.get(i).x*spriteWidth, snake.get(i).y*spriteWidth, null);		
	}
	}
}
