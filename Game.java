package com.crayxu;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.image.BufferedImage;

import javax.swing.JPanel;

/**
 *
 *@Author: Cray Xu	
 *@Date: Nov 20, 2017 1:21:21 AM
 *
 */

public class Game extends JPanel implements KeyListener, Runnable{

	private static final long serialVersionUID = 1L;
	public static final int WIDTH = 400;
	public static final int HEIGHT = 500;
	public static final Font main = new Font("Super Mario 256", Font.PLAIN, 30);
	private Thread game;
	private boolean running;
	private BufferedImage image = new BufferedImage(WIDTH, HEIGHT, BufferedImage.TYPE_INT_RGB);
	private Gameboard board;
	
	private long startTime;
	private long elapsed;
	private boolean set;
	
	public Game(){
		setFocusable(true);
		setPreferredSize(new Dimension(WIDTH, HEIGHT));
		addKeyListener(this);
		board = new Gameboard(WIDTH / 2 - Gameboard.BOARD_WIDTH / 2, HEIGHT - Gameboard.BOARD_HEIGHT - 10);
	}
	
	private void update(){
		board.update();
		Keyboard.update();
	}
	
	private void render(){
		Graphics2D g = (Graphics2D) image.getGraphics();
		g.setColor(Color.white);
		g.fillRect(0, 0, WIDTH, HEIGHT);
		board.render(g);
		//render board
		g.dispose();
		
		Graphics2D g2d = (Graphics2D) getGraphics();
		g2d.drawImage(image, 0, 0, null);
		g2d.dispose();
		
	}

	public void run() {
		
		int fps=0;
		int updates=0;
		long fpsTimer = System.currentTimeMillis();
		double nsPerUpdate = 1000000000.0/60;
		
		//last update time in nanoseconds
		double then = System.nanoTime();
		double unprocessed=0;
		
		while(running){
			
			boolean shouldRender=false;
			double now = System.nanoTime();
			unprocessed += (now-then) / nsPerUpdate;
			then = now;
			
		//update queue
		while(unprocessed >= 1){
			updates++;
			update();
			unprocessed--;
			shouldRender=true;
		}
		
		//render
		if(shouldRender){
			fps++;
			render();
			shouldRender=false;
		}
		else{
			try{
				Thread.sleep(1);
			}catch(Exception e){
				e.printStackTrace();
			}
		}
	}		
			//FPS Timer
			if(System.currentTimeMillis() - fpsTimer > 1000){
				System.out.printf("%d fps %updates", fps, updates);
				System.out.println();
				fps=0;
				updates=0;
				fpsTimer += 1000;
			}
		}
		
	public synchronized void start(){
		if(running)return;
		running = true;
		game= new Thread(this, "game");
		game.start();
	}
	
	public synchronized void stop(){
		if(!running)return;
		running = false;
		System.exit(0);
	}

	
	public void keyPressed(KeyEvent e) {
		Keyboard.keyPressed(e);
		
	}

	
	public void keyReleased(KeyEvent e) {
		Keyboard.KeyReleased(e);
		
	}
	
	public void keyTyped(KeyEvent e) {
			
			
	}
	
	

}
