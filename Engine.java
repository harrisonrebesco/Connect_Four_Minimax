/* COMP2230 - ASSIGNMENT 1
 * Author: Harrison Rebesco 
 * Student Number: c3237487  
 * Date: 22/10/19
 * Description: Engine that plays the two player game connect four, utilizing minimax algorithm.
 */

import java.util.*;
import java.io.*;

public class Engine 
{
	//used for board creation/manipulation
	private int rows = 6;
	private int columns = 7;
	
	//used for turns/board evaluation  
	private int player = 1;
	private int ai = 2;
	Random rand = new Random(); 
	
	//store board state
	private int board[][];
	
	//PRE: N/A 
	//POST: this generates an empty board sized with the appropriate number of rows and columns 
	//USED: called in Interface at start up 
	public void generateBoard()
	{
		//create 2 dimensional array to represent board with fixed rows and columns 
		board = new int[rows][columns];
		
		//set all positions to 0
		for (int r = 0; r < rows; r++) 
		{
            for (int c = 0; c < columns; c++) 
                board[r][c] = 0; //set current position to 0
        }
	}
	
	//PRE: p = string of current positions sent by coordinator  
	//POST: updates the board, adding any new moves made 
	//USED: called by Interface by the "position" case in switch statement 
	public void updateBoard(String p)
	{
		int token; //used to represent player or ai  
		int columnList[] = new int [2]; //stores the last two positions played 
		
		//use mod to see which token to start off with 
		if ((p.length() % 2) == 0) 
			token = 2; //mod == 0 --> ai 
		else 
			token = 1; //mod == 1 --> player 
	
		//traverse though positions swapping token values to get correct value if necessary 
		for (int i = 0; i < p.length()-2; i ++)
		{
			if (token == 1)
				token = 2;
			else 
				token = 1;
		}
	
		//CASE 1: theres only 1 move to place 
		if (p.length() == 1)
		{
			int column = Integer.parseInt(p); //parse to int 
			int row = getAvailableRow(column); 
			makeMove(row, column, token); //place move 
		}
		
		//CASE 2: more than 1 move to place 
		else 
		{
			int positions = Integer.parseInt(p.substring(Math.max(p.length() - 2, 0))); //get last two indicies from string 

			//get last two values from position list 
			for (int i = 2; i > 0; i--) 
			{
				columnList[i-1] = (int) positions % 10; //use mod 10 to isolate last value from list & add to list 
				positions /= 10; //divide by 10 to get new "last" value from list 
			}
			
			//place moves 
			for (int i = 0; i < 2; i++)
			{
				int row = getAvailableRow(columnList[i]); //get row 
				makeMove(row, columnList[i], token); //place token 
				
				//swap token value 
				if (token == 1) 
					token = 2;
				else 
					token = 1;
			}
		}
	}
	
	//PRE: b = board, r = row, c = column, t = token 
	//POST: places a token in a specified location 
	//USED: used in minimax() when placing tokens in a cloned instance of current board state 
	public void makeMove(int r, int c, int t)
	{
		board[r][c] = t; //set location = token 
	}
	
	//PRE: b = board, r = row, c = column, t = token 
	//POST: removes a token in a specified location 
	//USED: used in minimax() when placing tokens in a cloned instance of current board state 
	public void unmakeMove(int r, int c)
	{
		board[r][c] = 0; //set location = token 
	}
	
	
	//PRE: b = board, c = column 
	//POST: returns the next available row on the board, in column specified. returns true if there is an available row, false if column is full 
	//USED: used in minimax() to determine legal positions to place tokens 
	public int getAvailableRow(int c)
	{
		//start at the lowest row, work up to the top row to find the available row 
		for (int r = 0; r < rows; r++)
		{
			if (board[r][c] == 0) //if the value of the board = 0 --> row is available 
				 return r; //return the current row 
		}
		return -1; //return -1 to indicate column is full 
	}
	
	//PRE: b = board
	//POST: returns a list of all possible moves on board specified 
	//USED: used in isGameOver() to see if there are any moves remaining, and used in minimax() to generate a list of valid moves 
	public ArrayList<Integer> getPossibleMoves()
	{
		//move list will store all possible moves 
		ArrayList<Integer> moveList = new ArrayList<Integer>();
		
		//traverse all columns to see if column is available 
		for (int c = 0; c < columns; c++)
		{
			if (isColumnAvailable(c))
				moveList.add(c); //if column is available add position to moveList 
		}
		return moveList;
	}
	
	//PRE: b = board, c = column 
	//POST: returns true if column is not full, false if column is full 
	//USED: used in getPossibleMoves() to check that column is not full 
	public boolean isColumnAvailable(int c)
	{
		return board[rows-1][c] == 0; //check the top row to see that position = 0 (if position is not 0 then the column is full and not available)
	}
	
	//PRE: b = board
	//POST: returns true if player wins, if ai wins, or if there are no moves remaining. returns false otherwise
	//USED: used in minimax() as one of the terminal conditions when exploring possible moves 
	public boolean isGameOver()
	{
		//     check if player wins 	check if ai wins 	 check that there are still moves available 
		return isWinningMove(player) || isWinningMove(ai) || getPossibleMoves().size() == 0;
	}
	
	//PRE: b = board, t = token 
	//POST: returns true if there are 4 matching tokens in a row, adhering to connect 4 rules, otherwise returns false.
	//USED: used in isGameOver() to check if player, or ai has won the game. used in minimax() to weight winning/losing the game 
	public boolean isWinningMove(int t)
	{
		//CASE 1: check for a horizontal winning move  
		
		for (int c = 0; c < columns-3; c++) //traverse all columns 
		{
			for (int r = 0; r < rows; r++) //traverse all rows 
			{
				if ((board[r][c] == t) && (board[r][c+1] == t) && (board[r][c+2] == t) && (board[r][c+3] == t)) //check for 4 consecutive tokens in a horizontal line 
					return true; 
			}
		}
		
		//CASE 2: check for a vertical winning move 
		
		for (int c = 0; c < columns; c++) //traverse all columns 
		{
			for (int r = 0; r < rows-3; r++) //traverse all rows 
			{
				if ((board[r][c] == t) && (board[r+1][c] == t) && (board[r+2][c] == t) && (board[r+3][c] == t)) //check for 4 consecutive tokens in a vertical line 
					return true; 
			}
		}
		
		//CASE 3: check for a positive sloped diagonal winning move 
		
		for (int c = 0; c < columns-3; c++) //traverse all columns 
		{
			for (int r = 0; r < rows-3; r++) //traverse all rows 
			{
				if ((board[r][c] == t) && (board[r+1][c+1] == t) && (board[r+2][c+2] == t) && (board[r+3][c+3] == t)) //check for 4 consecutive tokens in a positive sloped diagonal line 
					return true; 
			}
		}
		
		//CASE 4: check for a negative sloped diagonal winning move 
		
		for (int c = 0; c < columns-3; c++) //traverse all columns 
		{
			for (int r = 3; r < rows; r++) //traverse all rows 
			{
				if ((board[r][c] == t) && (board[r-1][c+1] == t) && (board[r-2][c+2] == t) && (board[r-3][c+3] == t)) //check for 4 consecutive tokens in a negatively sloped diagonal line 
					return true; 
			}
		}

		return false;
	}
	
	//PRE: l = line, t = token 
	//POST: evaluates a line segment of current board state and returns a value depending on tokens within that line 
	//USED: used in evaluateBoard(), to traverse the board and score accordingly 
	public int evaluateLine(int l[], int t)
	{
		int offence = 0; //represents the offencive score 
		int defence = 0; //represents the defencive score 
		int currentCount = 0; //represents current players token count 
		int emptyCount = 0; //represents empty token count 
		int opponentCount = 0; //represents opponents tokens count 
		int opponentToken = 0; //set opponent token to zero by default 
		
		//STEP 1: find who the opponent is based on the token given 
		
		if (t == player) //if token matches player --> opponent is ai
			opponentToken = ai; 
		else //if token matches ai --> opponet is player 
			opponentToken = player;
		
		//STEP 2: traverse the line and count the various tokens
		
		for (int i = 0; i < 4; i++)
		{
			if (l[i] == t) //line segment contains matching token --> increment current player token count 
				currentCount++; 
			else if (l[i] == 0) //line segment is empty --> increment empty count 
				emptyCount++; 
			else if (l[i] == opponentToken) //line segment contains opponents token 
				opponentCount++;
		}
		
		//STEP 3: assign score based on line segment contents 
		
		//OFFENSIVE SCORING: 
		if (currentCount == 3 && emptyCount == 1) //current player can get 3 in a row with a potential win --> assign moderate preference 
			offence = 9; 
		else if (currentCount == 2 && emptyCount == 2) //current player can get 2 in a row with a potential win --> assign low preference 
			offence = 4;
		
		//DEFENSIVE SCORING: 
		if (opponentCount == 3 && emptyCount == 1) //opponent can get 3 in a row with a potential win --> assign moderate preference 
			defence = 12; 
		else if (opponentCount == 2 && emptyCount == 2)
			defence = 5;
		
		return offence - defence; //will be positive value if offencive move, negative if defencive move 
	}
	
	//PRE: b = board, t = token 
	//POST: evaluates the board state and returns a score based on the value calculated by evaluateLine function 
	//USED: used in minimax() to appraise potential moves 
	public int evaluateBoard(int t)
	{
		int score = 0;
		int centerCount = 0; //counts tokens that match value provided in center 
		int centerArray[] = new int[rows]; //array containing all values in the center column 
		
		//STEP 1: evaluate tokens in center column (having tokens in center column is generally advantageous in connect 4)

		//traverse rows and add relevant values to the center column array 
		for (int r = 0; r < rows; r++)  
			centerArray[r] = board[r][3]; //3 is the center column in the board used 
		
		//traverse center column array and count all matching tokens 
		for (int i = 0; i < centerArray.length; i++)
		{
			if (centerArray[i] == t) //if tokens match --> increment count 
				centerCount++; 
		}
		score += centerCount * 2; //update score based on center count modified by arbitrary value 
			
		//STEP 2: evaluate all horizontal lines 
		
		for (int r = 0; r < rows; r++) //traverse rows 
		{
			int horizontalArray[] = new int[columns]; //array containing all values in horizontal line 
			
			for (int c = 0; c < columns; c++) //traverse columns 
				horizontalArray[c] = board[r][c]; //add corresponding value 
			for (int c = 0; c < columns-3; c++) //traverse columns again (columns-3 used to stay within array bounds)
			{
				int horizontalLine[] = new int[4]; //array containing 4 values in horizional line 
				
				for (int i = 0; i < 4; i++) 
					horizontalLine[i] = horizontalArray[c+i]; //add 4 values to horizionalLine array 
				score += evaluateLine(horizontalLine, t); //evaluate the horizontal line & add value to score 
			}
		}
		
		//STEP 3: evaluate all vertical lines 
		
		for (int c = 0; c < columns; c++) //traverse columns 
		{
			int verticalArray[] = new int[rows]; //array containing all values in vertical line 
			
			for (int r = 0; r < rows; r++) //traverse rows 
				verticalArray[r] = board[r][c]; //add corresponding value 
			for (int r = 0; r < rows-3; r++) //traverse rows again (rows-3 used to stay within array bounds)
			{
				int verticalLine[] = new int[4]; //create array that will contain 4 vertical values 
			
				for (int i = 0; i < 4; i++)
					verticalLine[i] = verticalArray[r+i]; //add values in vertical line to array 
				score += evaluateLine(verticalLine, t); //evaluate the vertical line & add value to score 
			}
		}
		
		//STEP 4: evaluate all diagonal lines 
		
		for (int r = 0; r < rows-3; r++) //traverse rows (row-3 used to stay within bounds)
		{
			for (int c = 0; c < columns-3; c++) //traverse columns (columns-3 used to stay within bounds)
			{
				int positiveLine[] = new int[4]; //create array that will contain 4 positively sloped diagonal values 
				int negativeLine[] = new int[4]; //create array that will contain 4 negatively sloped diagonal values 
				
				for (int i = 0; i < 4; i++) 
				{
					positiveLine[i] = board[r+i][c+i]; //add values in positive slope to array 
					negativeLine[i] = board[r-i+3][c+i]; //add values in negative slope to array 
				}
				score += evaluateLine(positiveLine, t); //evaluate the positive slope & add to score 
				score += evaluateLine(negativeLine, t); //evaluate the negative slope & add to score 
			}
		}
		return score;
	}
	
	//PRE: depth will determine how many recursive calls are made 
	//POST: explores all possible nodes to search and returns a count 
	//USED: called by Interface when the "perft" case is used in switch statement 
	public long perft(int depth)
	{
		if (depth == 0)
			return 1; //add 1 to count, leaf node 
		else if (isGameOver())
			return 1; //return 0 as game is over 
		
		ArrayList<Integer> moveList = getPossibleMoves();
		int column = moveList.get(0);
		long total = 1;
		
		for (int i = 0; i < moveList.size(); i++)
		{
			int c = moveList.get(i);
			int row = getAvailableRow(c);
			makeMove(row, c, 3);
			total += perft(depth-1);
			unmakeMove(row, c); 
		}
		
		return total;
	}
	
	//PRE: b = board, depth indicates the depth of the tree, alpha & beta are used for alpha-beta pruning, minimax indicates whether its minimizing or maximizing for each "round" 
	//POST: implementation of the recursive minimax algorithm with alpha beta pruning, which returns the optimal move & score 
	//USED: called by Interface when the "go" case is used in switch statement 
	public int[] minimax(int depth, int alpha, int beta, boolean minimax)
	{
		ArrayList<Integer> moveList = getPossibleMoves(); //generate list containing all possible moves 
		boolean gameOver = isGameOver(); //check if game is over 
		
		//CASE 1: depth is 0 
		
		if (depth == 0) //if depth == 0 --> evaluate the board & return score 
			return new int[]{-1, evaluateBoard(ai)}; 
		
		//CASE 2: game is over 
		
		if (gameOver) //if game is over --> player has won, ai has won, or there are no moves remaining 
		{
			if (isWinningMove(ai)) 
				return new int[]{-1, Integer.MAX_VALUE}; //ai wins 
			else if (isWinningMove(player)) 
				return new int[]{-1, Integer.MIN_VALUE}; //player wins 
			else 
				return new int[]{-1, 0}; //draw 
		}
		
		//CASE 3: maximize board 
		
		if (minimax) //minimax == true --> maximize 
		{
			int maxScore = Integer.MIN_VALUE; //set maxScore to lowest possible value to begin with 
			int random = rand.nextInt(moveList.size());
			int column = moveList.get(random); //get random move, will make moves a bit more dynamic 
			
			for (int i = 0; i < moveList.size(); i++) //traverse moveList
			{
				int c = moveList.get(i); //get current move 
				int row = getAvailableRow(c); //find the next available row 
				makeMove(row, c, ai); //place a token in the available row and current column 
				int newScore = minimax(depth-1, alpha, beta, false)[1]; //recursively call minimax to minimize tempBoard with 1 less depth (this will return the second half of array)
				unmakeMove(row, c);
				
				//compare maxScore to newScore --> if newScore is higher than maxScore, newScore is the new maxScore
				if (newScore > maxScore)
				{
					column = c; //set column 
					maxScore = newScore; //set new max  
				}
				
				//alpha-beta pruning 
				if (maxScore > alpha)
					alpha = maxScore; //set new alpha 
				if (alpha >= beta)
					break; //prune branch if alpha is >= beta 
			}
			return new int[]{column, maxScore}; //return the column and score of minimax 
		}
		
		//CASE 4: minimize board 
		
		else //minimax == false --> minimize 
		{
			int minScore = Integer.MAX_VALUE; //set minScore to highest possible value to begin with 
			int random = rand.nextInt(moveList.size());
			int column = moveList.get(random); //get first possible move available 
			
			for (int i = 0; i < moveList.size(); i++) //traverse moveList 
			{
				int c = moveList.get(i); //get current move available 
				int row = getAvailableRow(c); //find the next available row 
				makeMove(row, c, player); //place a token in the available row and current column 
				int newScore = minimax(depth-1, alpha, beta, true)[1]; //recursively call minimax to minimize tempBoard with 1 less depth (this will return the second half of array)
				unmakeMove(row, c);
				
				//compare score to newScore --> if minScore is lower than newScore, newScore is the new minScore 
				if (newScore < minScore)
				{
					minScore = newScore; //set new min  
					column = c; //set column 
				}
				
				//alpha-beta pruning 
				if (minScore < beta)
					beta = minScore; //set new beta  
				if (alpha >= beta)
					break; //prune branch if alpha is >= beta
			}
			return new int[]{column, minScore};
		}
	}
}