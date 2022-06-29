import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.event.*;
import javax.swing.*;

import java.util.Arrays;
import java.util.Random;

import javax.swing.JPanel;

public class GamePanel extends JPanel implements ActionListener{

	static final int SCREEN_WIDTH = 600;
	static final int SCREEN_HEIGHT = 600;
	static final int UNIT_SIZE = 25;
	static final int GAME_UNITS = (SCREEN_WIDTH * SCREEN_HEIGHT) / UNIT_SIZE; 
	static final int DELAY = 75; // higher delay = slower the game
	final int x[] = new int[GAME_UNITS];
	final int y[] = new int[GAME_UNITS];
	int bodyParts = 6;
	int applesEaten;
	int appleX;
	int appleY;
	char direction = 'R'; // R=right, L=left, U=up, D=down
	boolean running = false;
	boolean paused = false;
	boolean restart = false;
	boolean first_run = true; // used to prevent re-creating Timer timer variable upon game restarts
	Timer timer;
	Random random;
	
	GamePanel(){
		random = new Random();
		this.setPreferredSize(new Dimension(SCREEN_WIDTH, SCREEN_HEIGHT));
		this.setBackground(Color.black);
		this.setFocusable(true);
		this.addKeyListener(new MyKeyAdapter());
		startGame();
	}
	public void startGame() {
		newApple();
		running = true;
		if(first_run) {
			timer = new Timer(DELAY, this); // passing "this" because GamePanel class extends ActionListener
			timer.start();
			first_run = false;
		}
		else {
			timer.restart();
		}

	}
	public void paintComponent(Graphics g) {
		super.paintComponent(g);
		draw(g);
	}
	public void draw(Graphics g) {
		
		if(paused && running) {
			gamePaused(g);
		}
		
		else if(running && !paused){
			/*
			for(int i=0;i<SCREEN_HEIGHT/UNIT_SIZE;i++) {
				g.drawLine(i*UNIT_SIZE, 0, i*UNIT_SIZE, SCREEN_HEIGHT);
				g.drawLine(0, i*UNIT_SIZE, SCREEN_WIDTH, i*UNIT_SIZE);
			}
			*/
			g.setColor(Color.red);
			g.fillOval(appleX, appleY, UNIT_SIZE, UNIT_SIZE);
			
			for(int i=0;i<bodyParts;i++) {
				if(i==0) { // head of snake
					g.setColor(Color.green);
					g.fillRect(x[i], y[i], UNIT_SIZE, UNIT_SIZE);
				}
				else {
					//g.setColor(new Color(45,180,0));
					g.setColor(new Color(random.nextInt(255), random.nextInt(255), random.nextInt(255))); // randomizes colors of each snake's body chunk.
					g.fillRect(x[i], y[i], UNIT_SIZE, UNIT_SIZE);
				}
			}
			// Score X text during the game
			g.setColor(Color.red);
			g.setFont(new Font("Ink Free", Font.BOLD, 40));
			FontMetrics metrics = getFontMetrics(g.getFont());
			String msg = "Score: " + applesEaten;
			g.drawString(msg, (SCREEN_WIDTH - metrics.stringWidth(msg))/2, g.getFont().getSize());
			// Press Space to pause text
			g.setColor(Color.red);
			g.setFont(new Font("Ink Free", Font.BOLD, 20));
			FontMetrics metrics2 = getFontMetrics(g.getFont());
			String msg2 = "Press Space to pause";
			g.drawString(msg2, (SCREEN_WIDTH - metrics2.stringWidth(msg2))/2, (SCREEN_HEIGHT - g.getFont().getSize()));
		
		}
		else {
			gameOver(g);
		}

	}
	public void newApple() {
		appleX = random.nextInt((int)SCREEN_WIDTH/UNIT_SIZE)*UNIT_SIZE;
		appleY = random.nextInt((int)SCREEN_HEIGHT/UNIT_SIZE)*UNIT_SIZE;
	}
	public void move() {
		for(int i = bodyParts;i>0;i--) {
			x[i] = x[i-1];
			y[i] = y[i-1];
		}
		
		switch(direction) {
		case 'U':
			y[0] = y[0] - UNIT_SIZE;
			break;
		case 'D':
			y[0] = y[0] + UNIT_SIZE;
			break;
		case 'R':
			x[0] = x[0] + UNIT_SIZE;
			break;
		case 'L':
			x[0] = x[0] - UNIT_SIZE;
			break;
		}
	}
	public void checkApple() {
		if((x[0] == appleX) && (y[0] == appleY)) {
			bodyParts++;
			applesEaten++;
			newApple();
		}
		
	}
	public void checkCollisions() {
		
		// Checks if head collides with body
		for(int i=bodyParts;i>0;i--) {
			if((x[0] == x[i]) && (y[0] == y[i])) {
				running = false;
				System.out.println("head collides with body");
			}
		}
		// Checks if head touches left border
		if(x[0] < 0) {
			running = false;
			System.out.println("head touches left border");
		}
		// Checks if head touches right border
		if(x[0] > SCREEN_WIDTH-UNIT_SIZE) {
			running = false;
			System.out.println("head touches right border");
		}
		// Checks if head touches top border
		if(y[0] < 0) {
			running = false;
			System.out.println("head touches top border");
		}
		// Checks if head touches bottom border
		if(y[0] > SCREEN_HEIGHT-UNIT_SIZE) {
			running = false;
			System.out.println("head touches bottom border");
		}
		
//		if (!running) {
//			timer.stop();
//		}
	}
	public void gameOver(Graphics g) {
		// Score
		g.setColor(Color.red);
		g.setFont(new Font("Ink Free", Font.BOLD, 40));
		FontMetrics metrics1 = getFontMetrics(g.getFont());
		String msg = "Score: " + applesEaten;
		g.drawString(msg, (SCREEN_WIDTH - metrics1.stringWidth(msg))/2, g.getFont().getSize());
	
		// Game Over text
		g.setColor(Color.red);
		g.setFont(new Font("Ink Free", Font.BOLD, 75));
		FontMetrics metrics2 = getFontMetrics(g.getFont());
		g.drawString("Game Over", (SCREEN_WIDTH - metrics2.stringWidth("Game Over"))/2, SCREEN_HEIGHT/2);
		
		// Press R to Restart text
		g.setColor(Color.red);
		g.setFont(new Font("Ink Free", Font.BOLD, 20));
		FontMetrics metrics3 = getFontMetrics(g.getFont());
		String msg3 = "Press R to restart the game";
		g.drawString(msg3, (SCREEN_WIDTH - metrics3.stringWidth(msg3))/2, (SCREEN_HEIGHT - g.getFont().getSize()));
	}
	
	public void gamePaused(Graphics g) {
		// Score text
		g.setColor(Color.red);
		g.setFont(new Font("Ink Free", Font.BOLD, 40));
		FontMetrics metrics1 = getFontMetrics(g.getFont());
		String msg1 = "Score: " + applesEaten;
		g.drawString(msg1, (SCREEN_WIDTH - metrics1.stringWidth(msg1))/2, g.getFont().getSize());
		
		// Press Space to resume text
		g.setColor(Color.red);
		g.setFont(new Font("Ink Free", Font.BOLD, 20));
		FontMetrics metrics2 = getFontMetrics(g.getFont());
		String msg2 = "Press Space to resume";
		g.drawString(msg2, (SCREEN_WIDTH - metrics2.stringWidth(msg2))/2, (SCREEN_HEIGHT - g.getFont().getSize()));
		
		// Game is paused text
		g.setColor(Color.red);
		g.setFont(new Font("Ink Free", Font.BOLD, 75));
		FontMetrics metrics3 = getFontMetrics(g.getFont());
		String msg3 = "Game is paused";
		g.drawString(msg3, (SCREEN_WIDTH - metrics3.stringWidth(msg3))/2, SCREEN_HEIGHT/2);
	}
	public void restartGame() {

		this.restart = false; // resetting variable for next use
		this.running = false;
		this.paused = false;
		this.bodyParts = 6;
		this.applesEaten = 0;
		this.direction = 'R'; // R=right, L=left, U=up, D=down
		Arrays.fill(x, 0);
		Arrays.fill(y, 0);
//		this.x[0] = 0;
//		this.y[0] = 0;
		startGame();
	}
	@Override
	public void actionPerformed(ActionEvent e) {

		if(restart){
			restartGame();
		}
		else if(running && !paused) {
			move();
			checkApple();
			checkCollisions();
		}
		repaint();
		
	}
	
	public class MyKeyAdapter extends KeyAdapter{
		@Override
		public void keyPressed(KeyEvent e) {
			switch(e.getKeyCode()) {
			case KeyEvent.VK_LEFT:
				if(direction != 'R') {
					direction = 'L';
				}
				break;
			case KeyEvent.VK_RIGHT:
				if(direction != 'L') {
					direction = 'R';
				}
				break;
			case KeyEvent.VK_UP:
				if(direction != 'D') {
					direction = 'U';
				}
				break;
			case KeyEvent.VK_DOWN:
				if(direction != 'U') {
					direction = 'D';
				}
				break;
			
			case KeyEvent.VK_SPACE:
				paused = !paused;
				break;
			case KeyEvent.VK_R:
				restart = true;
				break;
			}
		}
	}
	
}
