import java.awt.*;
import java.awt.event.*;
import java.util.ArrayList;

import javax.swing.*;

//etch a sketch drawing
//could be done: an undo button, trail overlaps in order of newest over oldest, 
//different color block options

public class EtchSketch extends JPanel implements ActionListener, MouseListener

{
	private final int WIDTH = 1500, HEIGHT = 700; //starting screen size
	private final int INITIAL_X = 100, INITIAL_Y = 100; //starting icon location
	private int currentWidth; //used to ensure icon doesn't leave frame
	private int currentHeight;//used to ensure icon doesn't leave frame
	
	
	private ImageIcon up, down, right, left, trailH, trailV, currentImage;
	private int x, y; //current top-left coordinates of image 
	private final int JUMP = 10; //increment for movement
	private int orientation; // 0 = up, 1 = right, 2= down, 3 = left //clockwise
	
	//for trail generation.  There are two different arrays because the image is
	//different depending on if it is horizontal or vertical
	private ArrayList<Point> trailArrayH; 
	private ArrayList<Point> trailArrayV;
	private int a, b; //trail locations
	
	private JButton clear;
	private JLabel coordinatesLabel;
	
	
	public EtchSketch()
	{
		
		//initial location for icon
		x = INITIAL_X;
		y = INITIAL_Y;
		
		//icons
		up = new ImageIcon ("up.gif");
		down = new ImageIcon ("down.gif");
		left = new ImageIcon ("left.gif");
		right = new ImageIcon ("right.gif");
		trailH = new ImageIcon ("trailleftright.gif");
		trailV = new ImageIcon ("trailupdown.gif");
		currentImage = right;
		orientation = 1;
		
		//trail arrays
		trailArrayH = new ArrayList<Point>();
		trailArrayV = new ArrayList<Point>();
		
		//clear button
		clear = new JButton("Clear");
		clear.addActionListener(this);
		add(clear);
		
		//add keyboard listener
		addKeyListener (new myListener());
		
		//mouse listener
		addMouseListener(this);
		
		//coordinates label
		coordinatesLabel = new JLabel();
		coordinatesLabel.setBackground(Color.white);
		coordinatesLabel.setForeground(Color.white);
		add(coordinatesLabel);
		
		//set up panel
		setBackground(Color.black);
		setPreferredSize(new Dimension (WIDTH, HEIGHT));
		
		setFocusable(true); //panel will get the keyboard focus when user clicks on it
	}
	
	
//*******************paint the frame**************
	
	public void paintComponent(Graphics page)
	{
		super.paintComponent(page);
		currentImage.paintIcon (this, page, x, y);
		setCoordinates();
	
	//paint the trail:
		//horizontal images
		for (int index = 0; index<(trailArrayH.size()); index++) 
		{
			Point temp = trailArrayH.get(index).getLocation();
			a = (int) temp.getX();
			b = (int) temp.getY();
			trailH.paintIcon (this, page, a, b);
		}
		//vertical images
		for (int index = 0; index<(trailArrayV.size()); index++) 
		{
			Point temp = trailArrayV.get(index).getLocation();
			a = (int) temp.getX();
			b = (int) temp.getY();
			trailV.paintIcon (this, page, a, b);
		}
	}
	

//*************listen to keyboard input***********************************

	private class myListener implements KeyListener
	{

		public void keyPressed(KeyEvent event) 
		{
			makeTrail(); //document where the icon is  before moving it
			
			switch (event.getKeyCode())
			{
			//MOVE UP
			case KeyEvent.VK_UP: 
				if(boundaryCheck(KeyEvent.VK_UP)) //only move if within frame
				{
					x = smoothAdjust(KeyEvent.VK_UP, x);
					currentImage = up;
					y -= JUMP;
					orientation = 0; 
				}
				break;
				
			//MOVE RIGHT
			case KeyEvent.VK_RIGHT:
				if(boundaryCheck(KeyEvent.VK_RIGHT)) //only move if within frame
				{
					y = smoothAdjust(KeyEvent.VK_RIGHT, y);
					currentImage = right;
					x += JUMP;
					orientation = 1;
				}
				break;
				
			//MOVE DOWN
			case KeyEvent.VK_DOWN:
				if(boundaryCheck(KeyEvent.VK_DOWN)) //only move if within frame
				{
					x = smoothAdjust(KeyEvent.VK_DOWN, x);
					currentImage = down;
					y += JUMP;
					orientation = 2;
				}
				break;
			
			//MOVE LEFT
			case KeyEvent.VK_LEFT:
				if(boundaryCheck(KeyEvent.VK_LEFT))
				{
					y = smoothAdjust(KeyEvent.VK_LEFT, y);
					currentImage = left;
					x -= JUMP;
					orientation = 3;
				}
				break;
				
			//CLEAR
			case KeyEvent.VK_ENTER:
				clear();
				break;
			}
			repaint(); //calls paintComponent method
		}
		//unused KeyListener interface methods
		public void keyReleased(KeyEvent arg0) {}
		public void keyTyped(KeyEvent arg0) {}
	}
	
//	**** returns false if the the icon is in danger of leaving frame 
	
	private boolean boundaryCheck(int keycode)
	{
		boolean result = false;
		currentWidth = (int) getBounds().getWidth();
		currentHeight = (int) getBounds().getHeight();
		if (keycode == KeyEvent.VK_UP && y>8)
				return true;
		if (keycode == KeyEvent.VK_LEFT && x>8)
			return true;
		if (keycode == KeyEvent.VK_DOWN && y < (currentHeight-30))
			return true;
		if (keycode == KeyEvent.VK_RIGHT && x < (currentWidth-30))
			return true;
		return result;
	}
	
//***********smoothly adjust the movement*********************
		private int smoothAdjust(int keycode, int coordinate)
		{
			int result = coordinate;
			if (orientation ==1 || orientation == 3 )//facing right or left
			{
				if (keycode == KeyEvent.VK_UP || keycode == KeyEvent.VK_DOWN)
				{
				x -= 8;
				result = x;
				}
			}
			if (orientation == 0 || orientation == 2) //facing up or down
			{
				if(keycode == KeyEvent.VK_LEFT || keycode == KeyEvent.VK_RIGHT)
				{
					y -= 8;
					result = y;
				}
			}
			return result;
		}
		
//*************record the trail travelled by the icon ******
		private void makeTrail()
		{
			switch(orientation)
			{
			case 0: //current position is up
				trailArrayV.add(new Point(x, y));

				break;
			case 1: //current position is right
				trailArrayH.add(new Point (x,y));
				break;
			case 2: //current position is down
				trailArrayV.add(new Point (x, y));

				break;
			case 3: //current position is left
				trailArrayH.add(new Point (x, y));
				break;
			}
		}
		
//*******set the label text for coordinates
	private void setCoordinates()
	{
		coordinatesLabel.setText(x + ", " + y);
	}
	
//********** listen for the clear button****************
	public void actionPerformed(ActionEvent arg0) //for clear button
	{
		clear();
		this.requestFocusInWindow(); //after the clear button is pushed, return focus to keyboard
	}
	
///**** clear ********	
	private void clear()
	{
		trailArrayH.removeAll(trailArrayH);
		trailArrayV.removeAll(trailArrayV);
		x = INITIAL_X;
		y = INITIAL_Y;
		currentImage = right;
		orientation = 1;
		repaint();
	}
	
	
//	**** Mouse Listener****
	//moves the image to mouse click
	public void mouseClicked(MouseEvent arg0) 
	{
		Point temp = new Point(); 
		temp = arg0.getPoint();
		x= (int) temp.getX();
		y = (int) temp.getY();
		repaint();
		this.requestFocusInWindow(); //return focus to keyboard
	}

	public void mouseEntered(MouseEvent arg0) {}
	public void mouseExited(MouseEvent arg0) {}
	public void mousePressed(MouseEvent arg0) {}
	public void mouseReleased(MouseEvent arg0) {}

	
// **** MAIN ***	
	public static void main (String[] args)
	{
		JFrame frame = new JFrame("Etch a Sketch!");
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.add(new EtchSketch());

		frame.pack();
		frame.setVisible(true);
			
	}



	
}
