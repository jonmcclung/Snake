package pkg;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.image.BufferedImage;
import java.io.IOException;

import javax.imageio.ImageIO;
import javax.swing.JLayeredPane;
import javax.swing.JPanel;
import javax.swing.JTextPane;
import javax.swing.text.DefaultStyledDocument;
import javax.swing.text.SimpleAttributeSet;
import javax.swing.text.StyleConstants;
import javax.swing.text.StyledDocument;

import net.miginfocom.swing.MigLayout;

public class PopupWindow extends JPanel{
	public static Font font;
	public static int xOffset, yOffset, width, textHeight;
	public Button[] button;
	public int [] commands;
	public int bw = 90, bh = 35;
	
	PopupWindow (String msg, int width, int height, int textHeight, String[] buttonText, int [] commands) {
		Game.ready = false;
		Game.shouldRender = false;
		Main.gamePanel.setBounds(0, 0, 15*25, 15*25 + Main.bar.getHeight());
		Main.pane.setPreferredSize(new Dimension(Main.gamePanel.getWidth(), Main.gamePanel.getHeight()));
		Main.frame.pack();
		button = new Button[buttonText.length];
		this.commands = commands;
		PopupWindow.width = width;
		PopupWindow.textHeight = textHeight;
		xOffset = (int) ((Main.pane.getPreferredSize().getWidth()-width)/2);
		yOffset = (int) ((Main.pane.getPreferredSize().getHeight()-height)/2);
		setBackground(Main.dark);
		font = Main.font;
		switch(buttonText.length) {
		case 1: {
			setLayout(new MigLayout("ins 0","","nogrid"));
			break;
		}
		case 2: {
			setLayout(new MigLayout("ins 0","[50%|50%]","nogrid"));
			break;
		}
		case 3: {
			setLayout(new MigLayout("ins 0","[33%|33%|33%]","nogrid"));
			break;
		}
		case 4: {
			setLayout(new MigLayout("ins 0, wrap 2","[50%|50%]",""));break;}
		case 5: {
			if (Game.accelerationThreshold == 3)
			setLayout(new MigLayout("ins 0, wrap 2","[50%|50%]",""));
			else
				setLayout(new MigLayout("ins 0, wrap 3","[33%|33%|33%]",""));
			break;
		}
		}
	        
		JTextPane text = new JTextPane();
		StyledDocument doc = new DefaultStyledDocument();
		text.setDocument(doc);
		
		text.setFont(font.deriveFont(24f));
		text.setEditable(false);
		text.setBackground(Main.dark);
		text.setForeground(Main.light);
		text.setText(msg);
		add(text, new String("wrap, span, w 100%, h "+textHeight+"!"));
		String dimensions = "center, gapleft 16, gaptop 15";
		if (Game.accelerationThreshold == 3) dimensions = "center";
		for (int i = 0; i < buttonText.length; i++) {
			if (i < 6) {
				button[i] = new Button(buttonText[i], commands[i]);
				add(button[i], dimensions);
			}
		}
		
		SimpleAttributeSet center = new SimpleAttributeSet();
		StyleConstants.setAlignment(center, StyleConstants.ALIGN_CENTER);
		doc.setParagraphAttributes(0, doc.getLength(), center, false);
		setBounds(xOffset, yOffset, width, height);
		Main.pane.setLayer(this, new Integer(1));
		Main.pane.add(this);
	}
	
	
}
