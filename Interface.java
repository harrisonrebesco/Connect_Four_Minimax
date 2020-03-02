/* COMP2230 - ASSIGNMENT 1
 * Author: Harrison Rebesco 
 * Student Number: c3237487  
 * Date: 22/10/19
 * Description: Interface class -- communicates with the Coordinator provided to manage the Engine class based on Coordinator output.
 */

import java.util.*;
import java.io.*;

public class Interface 
{
	public static void main(String args[])
	{
	    Scanner in = new Scanner(System.in);
		Engine engine = new Engine(); 
		boolean gameOver = false; //drives while loop 
		String name = "the-little-engine-that-could(c3237487)"; 
		engine.generateBoard(); 
		
		while (!gameOver)
		{
			String inputRead = in.nextLine(); //get next line (sent from controller)
			String[] inputArray = inputRead.split(" "); //split by paces & add to array 
			String inputSwitch = inputArray[0]; //get first entry of array (identifies which part of switch to jump to)
		
			switch(inputSwitch) 
			{
				//if name --> print engine name 
				case "name": 
					System.out.println(name);
					break;
			
				//if isready --> return readyok
				case "isready":
					System.out.println("readyok");
					break;

				//if go --> use minimax to find optimal move, place token if column is valid, return "bestmove column row"
				case "go":
					int column = engine.minimax(7, Integer.MIN_VALUE, Integer.MAX_VALUE, true)[0]; //use minimax to find best columns to place token 
					if (engine.isColumnAvailable(column)) 
					{
						int row = engine.getAvailableRow(column); //find the correct row to place token 
						System.out.println("bestmove" + " " + column + " " + row); //return the best column & row to place token 
					}
					break;
					
				//if position --> update board state to reflect correct positions 
				case "position":
					if (inputArray.length == 3) //check that it is a fresh board 
						engine.updateBoard(inputArray[2]); //update board by passing all moves 
					break;
					
				//if quit --> return quitting 
				case "quit":
					System.out.println("quitting"); 
					gameOver = false;
					break;
					
				//if perft --> use perft to count nodes at given depth 
				case "perft":
					int depth = Integer.parseInt(inputArray[1]); //get depth from string array 
					System.out.println("perft " + inputArray[1] + " " + engine.perft(depth)); //call perft with appropriate depth 
				 	break;
				
				//default case
				default:				
					break;
			}		
		}
	}
}