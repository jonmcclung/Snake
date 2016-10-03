package pkg;

import java.awt.BorderLayout;
import java.awt.Canvas;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.FontFormatException;
import java.awt.GraphicsEnvironment;
import java.awt.Toolkit;
import java.io.IOException;
import java.util.Random;

import javax.imageio.ImageIO;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JLayeredPane;
import javax.swing.JPanel;

import net.miginfocom.swing.MigLayout;

public class Main extends Canvas implements Runnable {
	
	static JFrame frame;
	static JLabel timeLabel;
	static JLabel pointLabel;
	public static JLayeredPane pane;
	public static Game game;
	static Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
	static final int xCenter = (int)screenSize.getWidth()/2;
	static final int yCenter = (int)screenSize.getHeight()/2;
	public static int time = 0, animationCount = 0, moveCount = 0, fps = 60, framesPerMove = 15;
	public static Random rand = new Random();
	public boolean running = false;
	public static Color dark = Color.decode("0x596569"), light = Color.decode("0x28c5ff");
	public static Font font;
	public PopupWindow popup;
	public static JPanel gamePanel, bar;
	boolean playFlag = false;
	public static boolean wonFlag = false;
	public Reset reset;
	
	public void reStarter() {
		playFlag = false;
		String message = "Would you like to quit this game and start another?";
		popup = new PopupWindow(message, 375, 270, 130, new String[] {"Easy", "Medium", "Hard", "Extreme", "Cancel"}, new int[] {3,4,5,6,0});
	}

	
	public void gameOver() {
		playFlag = false;
		String message = "Game Over!\nPoints: " + Game.points+"\nTime: "+time;
		popup = new PopupWindow(message, 375, 270, 110, new String[] {"Easy", "Medium", "Hard", "Extreme", "Exit"}, new int[] {3,4,5,6,2});
	}
	
	Main() {
	frame = new JFrame("Snake"); frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE); frame.setResizable(false); frame.setLocation(xCenter-70, yCenter-50); frame.setVisible(true); pane = new JLayeredPane();
	try {
		font = Font.createFont(Font.TRUETYPE_FONT, this.getClass().getResourceAsStream("res/ebrimaBold.ttf"));
	     GraphicsEnvironment ge = GraphicsEnvironment.getLocalGraphicsEnvironment();
	     ge.registerFont(Font.createFont(Font.TRUETYPE_FONT, this.getClass().getResourceAsStream("res/ebrimaBold.ttf")));
	} catch (IOException|FontFormatException e) {
	     System.out.println("failure");
	}
	gamePanel = new JPanel();
	gamePanel.setLayout(new BorderLayout());
	pane.setLayer(gamePanel, new Integer(0));
	pane.add(gamePanel);
	timeLabel = labelMaker(24f);
	pointLabel = labelMaker(24f);
	game = new Game();
	frame.setLocation(Main.xCenter-240, Main.yCenter-240);
	MigLayout mig = new MigLayout("ins 0,wrap 3","[33%][33%][33%]","");
	bar = new JPanel(mig);
	bar.setBackground(dark);
	gamePanel.add(bar, BorderLayout.SOUTH);
	bar.add(pointLabel, "gapright 4, gapleft 4");
	bar.add(timeLabel, "push");
	reset = new Reset();
	bar.add(reset, "w 30!,h 30!, right");
	bar.setSize(new Dimension((int)game.getWidth(), 30));
	gamePanel.add(game, BorderLayout.CENTER);
	gamePanel.setBounds(0, 0, (int) game.getWidth(), (int) (game.getHeight() + bar.getHeight()));
	pane.setPreferredSize(new Dimension(gamePanel.getWidth(), gamePanel.getHeight()));
	String message = "This is Snake!\n"
			+ "Eat the dots, don't run into yourself or the wall.\n"
			+ "Have fun!";
	popup = new PopupWindow(message, 375, 270, 130, new String[] {"Easy", "Medium", "Hard", "Extreme"}, new int[] {3,4,5,6});
	frame.add(pane);
	frame.pack();		
}
	
private JLabel labelMaker(float size) {
	JLabel l = new JLabel();
	l.setBackground(dark);
	l.setForeground(light);
	l.setOpaque(true);
	l.setFont(font.deriveFont(size));
	return l;
}
public static void main(String[] args)
{
	new Main().start();
}

public synchronized void start() {
	running = true;
	new Thread(this).start();
}

public synchronized void stop() {
	running = false;
}

@Override
public void run() {
	long lastTime = System.nanoTime();
	double nsPerTick = 1000000000D/(double)fps;
	
	long lastTimer = System.currentTimeMillis();
	double delta = 0;	

	timeLabel.setText("Time: "+time);
	
	while (running)
	{
		long now = System.nanoTime();
		delta += (now - lastTime)/nsPerTick;
		lastTime = now;
		boolean shouldRender = false;
		
		while (delta >= 1) {
			delta -= 1;
			shouldRender = true;
		}
		if (shouldRender) {
			reset.ready = false;
			if (reset.pressed) {
				reset.pressed = false;
				Game.shouldRender = false;
				Game.ready = false;
				reStarter(); 
				}
			if (game.GameOver) {
				Game.shouldRender = false;
				Game.ready = false;
				game.GameOver = false;
				gameOver();
			}
			for (int j = 0; j < popup.button.length; j++) {
				if (popup.button[j].pressed) {
					popup.button[j].pressed = false;
					switch(popup.button[j].command) {
					case 0: {if (!playFlag) {play(); playFlag = true; break;}}
					case 2: {System.exit(0); break;}
					case 3: {if (!playFlag) {Game.scale = 30; framesPerMove = 18; Game.accelerationThreshold = 6; Game.spath = "snakeEasy"; newGame();} break;}
					case 4: {if (!playFlag) {Game.scale = 25; framesPerMove = 15; Game.accelerationThreshold = 5; Game.spath = "spriteSheet"; newGame();} break;}
					case 5: {if (!playFlag) {Game.scale = 20; framesPerMove = 12; Game.accelerationThreshold = 4; Game.spath = "snakeHard"; newGame();} break;}
					case 6: {if (!playFlag) {Game.scale = 15; framesPerMove = 9;  Game.accelerationThreshold = 3; Game.spath = "snakeExtreme"; newGame();} break;}
					}
				}
			}

			if (Game.shouldRender) {
			animationCount++;
			moveCount++;
			if (animationCount>framesPerMove) {
				animationCount=0;
			}
			if (moveCount>framesPerMove) {
				game.move();
				moveCount=0;
			}
			game.render();
			reset.ready = true;
			}
		}
		if (System.currentTimeMillis() - lastTimer >= 1000) {
			if (Game.ready) {
				time++;
				timeLabel.setText("Time: "+time);
				lastTimer += 1000;
			}
			else lastTimer = System.currentTimeMillis();
		}
		}
	}

public void newGame() {
	game.newGame();
	bar.removeAll();
	bar.setSize(new Dimension((int)game.getWidth(), 30));
	if (Game.accelerationThreshold == 3) {

		bar.add(pointLabel, "gapright 2, gapleft 2, w 90!");
		bar.add(timeLabel, "w 90!");
	timeLabel.setFont(font.deriveFont(16f));
	pointLabel.setFont(font.deriveFont(16f));
	}
	else if (Game.accelerationThreshold == 4) {
		bar.add(pointLabel, "gapright 2, gapleft 2, w 120!");
		bar.add(timeLabel, "w 120!");
		timeLabel.setFont(font.deriveFont(19f));
		pointLabel.setFont(font.deriveFont(19f));
	}
	else if (Game.accelerationThreshold == 5) {
		bar.add(pointLabel, "gapright 2, gapleft 2, w 120!");
		bar.add(timeLabel, "w 120!");
		timeLabel.setFont(font.deriveFont(21f));
		pointLabel.setFont(font.deriveFont(21f));
	}
	else {
		bar.add(pointLabel, "gapright 2, gapleft 2, w 120!");
		bar.add(timeLabel, "w 120!");
		timeLabel.setFont(font.deriveFont(24f));
		pointLabel.setFont(font.deriveFont(24f));
	}

	bar.add(reset, "w 30!,h 30!, right");
	play(); playFlag = true;
}

public void play() {
	game.setSize(new Dimension(Game.spriteWidth*Game.scale, Game.spriteWidth*Game.scale));
	gamePanel.setBounds(0, 0, (int) game.getWidth(), (int) (game.getHeight() + bar.getHeight()));
	pane.setPreferredSize(new Dimension(gamePanel.getWidth(), gamePanel.getHeight()));
	popup.removeAll();
	frame.remove(popup);
	pane.removeAll();
	pane.setLayer(gamePanel, new Integer(0));
	pane.add(gamePanel);
	reset.ready = true;
	Game.ready = true;
	Game.shouldRender = true;
	frame.revalidate();
	frame.pack();
	frame.repaint();
	game.requestFocusInWindow();
}
}
