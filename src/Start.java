package com.crayxu;

import javax.swing.JFrame;

/**
 *
 *@Author: Cray Xu	
 *@Date: Nov 20, 2017 1:16:43 AM
 *
 */

public class Start {
	
	public static void main(String[] args){
		Game game = new Game();
		
		JFrame window = new JFrame("2048");
		window.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		window.setResizable(false);
		window.add(game);
		window.pack();
		window.setLocationRelativeTo(null);
		window.setVisible(true);
		
		game.start();
		
		
	}

}
