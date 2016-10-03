package pkg;

import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
import java.io.IOException;

import javax.imageio.ImageIO;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;

public class Button extends JComponent implements MouseListener{
	
	public int command, width = 90, height = 35;
	JPanel textPanel;
	JLabel message;
	public boolean selected = false;
	BufferedImage Img, selectedImg;
	String msg;
	boolean pressed = false;
	
	Button (String msg, int command) {
		setPreferredSize(new Dimension(width, height));
		try {
			Img = ImageIO.read(this.getClass().getResourceAsStream("res/button.png"));
		}
		catch (IOException e){
			e.printStackTrace();
		}
		try {
			selectedImg = ImageIO.read(this.getClass().getResourceAsStream("res/selected.png"));
		}
		catch (IOException e){
			e.printStackTrace();
		}
		this.msg = msg;
		this.command = command;
		setBackground(Main.dark);
		addMouseListener(this);
		//mouse = new Mouse(this);
	}
	
	public void paintComponent(Graphics g) {
		//super.paintComponent(g);
	    Graphics2D g2 = (Graphics2D) g;
		g2.setColor(getBackground());
		g2.fill(new Rectangle2D.Double(0, 0, 130, 50));	
		if (selected) {
			g2.setColor(Main.dark);
			g.drawImage(selectedImg, 0, 0, null);
		}
		else {
			g2.setColor(Main.light);
			g.drawImage(Img, 0, 0, null);
		}
		RenderingHints rh = new RenderingHints(
                RenderingHints.KEY_ANTIALIASING,
                RenderingHints.VALUE_ANTIALIAS_ON);

        rh.put(RenderingHints.KEY_RENDERING,
               RenderingHints.VALUE_RENDER_QUALITY);		

        g2.setRenderingHints(rh);
        if (msg.length() > 5) {
        	g2.setFont(Main.font.deriveFont(18f));
    	    g2.drawString(msg, (int)((width/2)-(msg.length()*4.5)), (height/2)+8);
        }
        else {
    	    g2.setFont(Main.font.deriveFont(24f));
    	    g2.drawString(msg, ((width/2)-(msg.length()*7))-4, (height/2)+8);
        }

	}

	@Override
	public void mouseClicked(MouseEvent arg0) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void mouseEntered(MouseEvent arg0) {

		setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
		selected = true;
		repaint();
		
	}

	@Override
	public void mouseExited(MouseEvent arg0) {
		setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
		selected = false;
		repaint();
		
	}

	@Override
	public void mousePressed(MouseEvent arg0) {
		pressed = true;
		
	}

	@Override
	public void mouseReleased(MouseEvent arg0) {
		// TODO Auto-generated method stub
		
	}
}
