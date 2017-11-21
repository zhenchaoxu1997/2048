package com.crayxu;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.event.KeyEvent;
import java.awt.image.BufferedImage;
import java.util.Random;

/**
 *
 *@Author: Cray Xu	
 *@Date: Nov 20, 2017 10:04:34 PM
 *
 */

public class Gameboard {
	
	private static final int ROWS = 4;
	private static final int COLUMNS = 4;
	
	private final int startingTiles = 2;
	private Tile[][] board;
	private boolean dead;
	private boolean won;
	private BufferedImage gameBoard;
	private BufferedImage finalBoard;
	private int x;
	private int y;

	
	private static int SPACING = 10;
	public static int BOARD_WIDTH = (COLUMNS + 1) * SPACING + COLUMNS * Tile.WIDTH;
	public static int BOARD_HEIGHT = (ROWS + 1) * SPACING + ROWS * Tile.HEIGHT;
	
	private boolean hasStarted;
	
	public Gameboard(int x, int y){
		this.x = x;
		this.y = y;
		board = new Tile[ROWS][COLUMNS];
		gameBoard = new BufferedImage(BOARD_WIDTH, BOARD_HEIGHT, BufferedImage.TYPE_INT_RGB);
		finalBoard = new BufferedImage(BOARD_WIDTH, BOARD_HEIGHT, BufferedImage.TYPE_INT_RGB);  
		
		createBoardImage();
		start();
	}

	private void createBoardImage() {
		Graphics2D g = (Graphics2D) gameBoard.getGraphics();
		g.setColor(Color.darkGray);
		g.fillRect(0, 0, BOARD_WIDTH, BOARD_HEIGHT);
		g.setColor(Color.lightGray);
		
		for(int row = 0; row < ROWS; row++){
			for(int column=0; column < COLUMNS; column++){
				int x = SPACING + SPACING * column + Tile.WIDTH * column;
				int y = SPACING + SPACING * row + Tile.HEIGHT * row;
				g.fillRoundRect(x, y, Tile.WIDTH, Tile.HEIGHT, Tile.ARC_WIDTH, Tile.ARC_HEIGHT);
			}
		}	
	}
	
	private void start(){
		for(int i = 0; i < startingTiles; i++){
			spawnRandom();
		}
	}
	
	private void spawnRandom() {
		Random random = new Random();
		boolean notValid = true;
		
		while(notValid){
			int location = random.nextInt(ROWS*COLUMNS);
			int row = location / ROWS;
			int column = location % COLUMNS;
			
			Tile current = board[row][column];
			if(current == null){
				int value = random.nextInt(10) < 9 ? 2 : 4;
				Tile tile = new Tile(value, getTileX(column), getTileY(row));
				board[row][column]= tile;
				notValid = false;
			}
		}
		
	}

	private int getTileX(int column) {
		return SPACING + column* Tile.WIDTH + column*SPACING;
	}

	private int getTileY(int row) {
		return SPACING + row* Tile.HEIGHT + row*SPACING;
	}

	public void render(Graphics2D g){
		Graphics2D g2d = (Graphics2D)finalBoard.getGraphics();
		g2d.drawImage(gameBoard, 0, 0, null);
		
		for(int row=0; row< ROWS; row++){
			for(int column=0; column < COLUMNS; column++){
				Tile current = board[row][column];
				if(current == null) continue;
				current.render(g2d);
			}
		}
		
		//draw tiles
		
		g.drawImage(finalBoard, x, y, null);
		g2d.dispose();
	}
	
	public void update(){
		checkKeys();
		
		for(int row=0; row<ROWS; row++){
			for(int column=0; column<COLUMNS; column++){
				Tile current = board[row][column];
				if(current == null) continue;
				current.update();
				//reset position
				if(current.getValue() == 2048){
					won = true;
				}
			}
		}
	}
	

	private void checkKeys() {
		if(Keyboard.typed(KeyEvent.VK_LEFT)){
			moveTiles(Direction.LEFT);
			if(!hasStarted) hasStarted = true;
		}
		if(Keyboard.typed(KeyEvent.VK_RIGHT)){
			moveTiles(Direction.RIGHT);
			if(!hasStarted) hasStarted = true;
		}
		if(Keyboard.typed(KeyEvent.VK_UP)){
			moveTiles(Direction.UP);
			if(!hasStarted) hasStarted = true;
		}
		if(Keyboard.typed(KeyEvent.VK_DOWN)){
			moveTiles(Direction.DOWN);
			if(!hasStarted) hasStarted = true;
		}	
	}

	private void moveTiles(Direction dir) {
		boolean canMove = false;
		int horizontalDirection = 0;
		int verticalDirection = 0;
		
		if(dir==Direction.LEFT){
			horizontalDirection = -1;
			for(int row=0; row<ROWS; row++){
				for(int column=0; column<COLUMNS; column++){
						if(!canMove){
							canMove = move(row, column, horizontalDirection, verticalDirection, dir);
						}
						else move(row, column, horizontalDirection, verticalDirection, dir);
				}
			}
		}
		else if(dir==Direction.RIGHT){
			horizontalDirection = -1;
			for(int row=0; row<ROWS; row++){
				for(int column=0; column<COLUMNS; column++){
						if(!canMove){
							canMove = move(row, column, horizontalDirection, verticalDirection, dir);
						}
						else move(row, column, horizontalDirection, verticalDirection, dir);
				}
			}
		}
		else if(dir==Direction.UP){
			horizontalDirection = -1;
			for(int row=0; row<ROWS; row++){
				for(int column=COLUMNS -1 ; column>=0; column--){
						if(!canMove){
							canMove = move(row, column, horizontalDirection, verticalDirection, dir);
						}
						else move(row, column, horizontalDirection, verticalDirection, dir);
				}
			}
		}
		else if(dir==Direction.DOWN){
			verticalDirection = 1;
			for(int row=ROWS-1; row>=0; row--){
				for(int column=0; column<COLUMNS; column++){
						if(!canMove){
							canMove = move(row, column, horizontalDirection, verticalDirection, dir);
						}
						else move(row, column, horizontalDirection, verticalDirection, dir);
				}
			}
		}
		else{
			System.out.println(dir + "is not a valid direction");
		}
		
		for(int row=0; row < ROWS; row++){
			for(int column=0; column < COLUMNS; column++){
				Tile current = board[row][column];
				if(current == null) continue;
				current.setCanCombine(true);
			}
		}
		
		if(canMove){
			spawnRandom();
			//check dead
		}
	}

	private boolean move(int row, int column, int horizontalDirection, int verticalDirection, Direction dir) {
		boolean canMove = false;
		
		Tile current = board[row][column];
		if(current == null) return false;
		boolean move = true;
		int newColumn = column;
		int newRow = row;
		while(move){
			newColumn += horizontalDirection;
			newRow += verticalDirection;
			if(checkOutOfBounds(dir, newRow, newColumn)) break;
			if(board[newRow][newColumn] == null){
				board[newRow][newColumn] = current;
				board[newRow - verticalDirection][newColumn - horizontalDirection] = null;
				board[newRow][newColumn].setSlideTo(new Point(newRow, newColumn));
				
				
			}
			else if(board[newRow][newColumn].getValue() == current.getValue() && board[newRow][newColumn].canCombine()){
				board[newRow][newColumn].setCanCombine(false);
				board[newRow][newColumn].setValue(board[newRow][newColumn].getValue()*2);
				canMove = true;
				board[newRow - verticalDirection][newColumn - horizontalDirection] = null;
				board[newRow][newColumn].setSlideTo(new Point(newRow, newColumn));
//				board[newRow][newColumn].setCanCombineAnimation(true);
//				add score
			}
			else{
				move = false;
			}
		}
		
		return canMove;
	}

	private boolean checkOutOfBounds(Direction dir, int row, int column) {
		if(dir == Direction.LEFT){
			return column < 0;
		}
		else if(dir == Direction.RIGHT){
			return column > COLUMNS -1;
		}
		else if(dir == Direction.UP){
			return row < 0;
		}
		else if(dir == Direction.DOWN){
			return row > ROWS -1;
		}
		return false;
	}
}
