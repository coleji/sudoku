import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;


import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.JToggleButton;
/*
 * Puzzle Mode:
 * 1) => Entry
 * 2) Solve
 * 3) New
 * 4) Hint
 * 
 * Entry Mode:
 * 1) => Puzzle
 * 2) Solve
 * 3) New
 * 4) Check
 */



public class SudokuWindow implements ActionListener, KeyListener {
	
	private JFrame frame = new JFrame("Sudoku");	
	private int chosenNumber = 1;										// Variable that holds what number to fill a clicked cell with
	private JButton[][] numbers = new JButton[9][9];					// 9x9 grid of puzzle squares
	private JToggleButton[] numberChoosers = new JToggleButton[11];		// row of 9 buttons at the bottom which determine chosenNumber
	private JTextField bottomText = new JTextField("");					// Status Text (bottom)
	private boolean entryMode = false;									// true for entry mode, false for normal mode
	private boolean[][] lockedNumbers = new boolean[9][9];				// true for numbers with values at start of solve; false for empty squares
		
	private JButton modeButton = new JButton("> Entry");
	private JButton solveButton = new JButton("Solve");
	private JButton newPuzzleButton = new JButton("New");

	
	public SudokuWindow() {
		
		frame.addKeyListener(this);
		
		JPanel mainPanel = new JPanel(new BorderLayout());					// Contents of Frame
		JPanel midPanel = new JPanel(new GridLayout(9,9));					// Puzzle Buttons
		JPanel bottomPanel = new JPanel(new GridLayout(2,1));				// Contains toggle buttons and bottom text thingy
		JPanel buttonChooserPanel = new JPanel(new GridLayout(1,11));		// Contains toggle buttons
		JPanel topPanel = new JPanel(new GridLayout(1,3));					// Contains top buttons
		
		mainPanel.addKeyListener(this);
		midPanel.addKeyListener(this);
		bottomPanel.addKeyListener(this);
		buttonChooserPanel.addKeyListener(this);
		topPanel.addKeyListener(this);
		
		
		//Buttons at top		
		modeButton.addActionListener(this);
		modeButton.setActionCommand("mode");
		modeButton.addKeyListener(this);
		topPanel.add(modeButton);

		
		solveButton.addActionListener(this);
		solveButton.setActionCommand("solve");
		solveButton.addKeyListener(this);
		topPanel.add(solveButton);
		
		
		newPuzzleButton.addActionListener(this);
		newPuzzleButton.setActionCommand("new");
		newPuzzleButton.addKeyListener(this);
		topPanel.add(newPuzzleButton);
		
		mainPanel.add(topPanel, BorderLayout.NORTH);
		mainPanel.add(midPanel, BorderLayout.CENTER);
		mainPanel.add(bottomPanel, BorderLayout.SOUTH);
		
		// Erase Button
		numberChoosers[0] = new JToggleButton("_");
		numberChoosers[0].setBorder(BorderFactory.createMatteBorder(5, 1, 1, 1, Color.BLACK));
		numberChoosers[0].addActionListener(this);
		numberChoosers[0].setActionCommand("erase");
		numberChoosers[0].addKeyListener(this);
		buttonChooserPanel.add(numberChoosers[0]);
		
		// All the other toggle buttons
		for (int i=1; i<10; i++) {
			numberChoosers[i] = new JToggleButton(Integer.toString(i));
			numberChoosers[i].setBorder(BorderFactory.createMatteBorder(5, 1, 1, 1, Color.BLACK));
			numberChoosers[i].addActionListener(this);
			numberChoosers[i].setActionCommand("choose"+i);
			buttonChooserPanel.add(numberChoosers[i]);
			numberChoosers[i].addKeyListener(this);
		}
		
		// Debug/Check Button
		// In game mode, this will activate "debug mode" and display the safety of an entered number
		// In entry mode, this will check for puzzle integrity after each new input when selected
		numberChoosers[10] = new JToggleButton("?");
		numberChoosers[10].setBorder(BorderFactory.createMatteBorder(5, 1, 1, 1, Color.BLACK));
		numberChoosers[10].addActionListener(this);
		numberChoosers[10].setActionCommand("debug");
		numberChoosers[10].addKeyListener(this);
		buttonChooserPanel.add(numberChoosers[10]);
		
		bottomPanel.add(buttonChooserPanel);
		bottomPanel.add(bottomText);
		bottomText.setEditable(false);
		bottomText.addKeyListener(this);
		

		// Create Puzzle Buttons *****************
		for (int r = 0; r<9; r++){
			for (int c = 0; c<9; c++){
				
				numbers[r][c] = new JButton();
				numbers[r][c].setPreferredSize(new Dimension(35,30));
				numbers[r][c].setHorizontalAlignment(JButton.CENTER);
				numbers[r][c].addActionListener(this);
				numbers[r][c].setActionCommand(""+r+c);
				numbers[r][c].addKeyListener(this);
			
				// Creates the thick borders which denote the 3x3 sections
				if ((c == 2 || c == 5) && !(r == 2 || r == 5))
					numbers[r][c].setBorder(BorderFactory.createMatteBorder(1,1,1,3,Color.BLACK));
				else if((r == 2 || r == 5) && !(c == 2 || c == 5))
					numbers[r][c].setBorder(BorderFactory.createMatteBorder(1,1,3,1,Color.BLACK));
				else if ((r == 2 || r == 5) && (c == 2 || c == 5))
					numbers[r][c].setBorder(BorderFactory.createMatteBorder(1,1,3,3,Color.BLACK));
				else
					numbers[r][c].setBorder(BorderFactory.createMatteBorder(1,1,1,1,Color.BLACK));
				
				numbers[r][c].setText("");
				midPanel.add(numbers[r][c]);
			}
		}
		// *******************************
		
		// Start with "1" selected
		numberChoosers[1].setSelected(true);
		
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setContentPane(mainPanel);
		frame.pack();
		frame.setVisible(true);
		
		// Create a puzzle on load
		generatePuzzle();
		
		
	}
	
	public void actionPerformed(ActionEvent e) {
		
		// Hit a toggle
		if (e.getActionCommand().contains("choose"))
			switchChosen(e.getActionCommand().subSequence(6,7 ).toString());
		
		// Hit erase (single)
		else if (e.getActionCommand().equals("erase"))
			switchChosen("erase");
		
		// Hit a puzzle square
		else if (e.getActionCommand().length() == 2)
			fillSquare(e.getActionCommand());
		
		// Hit New
		else if (e.getActionCommand().equals("new")){
			clearAll();
			if (entryMode == false)
				generatePuzzle();
		}
		
		// Hit Solve
		else if (e.getActionCommand().equals("solve"))
			solvePuzzle();
		
		// Hit Mode change
		else if (e.getActionCommand().equals("mode"))
			switchModes();
		
	}
	
	// When you click a puzzle square
	private void fillSquare(String s) {
		
		
		Integer sInt = new Integer(s);
		int r = sInt/10;
		int c = sInt % 10;
		if (lockedNumbers[r][c] == false){
			// Set to chosenNumber, unless == 0 (then clear the square)
			if (chosenNumber > 0)
				numbers[r][c].setText(""+chosenNumber);
			else if (chosenNumber == 0)
				numbers[r][c].setText("");
			
			// Check for conflicts/win
			if (checkConflicts() == false)
				bottomText.setText("Conflict!");
			else if (checkFilled() == true)
				bottomText.setText("Win!!");
			else bottomText.setText("");
			
			if (numberChoosers[10].isSelected())
					debugMeth(s);
			
		} else
			bottomText.setText("Locked!");
		
		if (entryMode == true && numberChoosers[10].isSelected())
			checkGrid();
	}

	// When you click a toggle button
	private void switchChosen(String s){
		
		for (int i=0; i<10; i++)
			numberChoosers[i].setSelected(false);
		
		if (s.equals("erase")){
			chosenNumber = 0;
			numberChoosers[0].setSelected(true);
		} 
		
		else {
		chosenNumber = new Integer(s);					
		numberChoosers[chosenNumber].setSelected(true);		
		}
		
	}
	
	// When you push Clear all
	private void clearAll() {
		
		for (int r=0; r<9; r++){
			for (int c=0; c<9; c++){
				numbers[r][c].setText("");
				numbers[r][c].setBackground(null);
				lockedNumbers[r][c] = false;
			}
		}
		
		bottomText.setText("");
		
	}
	
	private boolean checkConflicts() {  // Return false if there is a conflict  (I know it's backwards but it's too late to change it now....)
		
		boolean returnVal = true;			// Assume no conflicts; this will be the boolean returned
		
		for (int r=0; r<9; r++) {
			for (int c=0; c<9; c++) {
				if (numbers[r][c].getText() != ""){
					int value = new Integer(numbers[r][c].getText());		// value we are checking against
					
					// Check for conflicts in each row
					for (int cTest=0; cTest<9; cTest++){			
						if (!(numbers[r][cTest].getText().equals(""))){
							int checkValue = new Integer(numbers[r][cTest].getText());
							returnVal = (checkValue == value && cTest != c) ? false : returnVal;	
						}
					}
					
					// Check for conflicts in each column
					for (int rTest=0; rTest<9; rTest++){			
						if (!(numbers[rTest][c].getText().equals(""))){
							int checkValue = new Integer(numbers[rTest][c].getText());
							returnVal = (checkValue == value && rTest != r) ? false : returnVal;
						}
					}
				}
				
			}
			
			
		}
		
		// Check for conflicts in each 3x3 section
		for (int r=1; r<9; r+=3){
			for (int c=1; c<9; c+=3){   // each (r,c) is the middle square in each 3x3 section
				int[] section = new int[9]; // filled with the values of the 9 squares in the 3x3 section
				int i=0;
				for (int rIn=r-1; rIn<=r+1; rIn++){
					for (int cIn=c-1; cIn<=c+1; cIn++){
						if (!(numbers[rIn][cIn].getText().equals("")))
							section[i] = new Integer(numbers[rIn][cIn].getText());
						else
							section[i] = 0;			// if square is empty
						i++;
					}
				}
				
				// Look for non-zero duplicates in section[]
				for (int j=0; j<section.length-1; j++){
					for (int k=j+1;k<section.length; k++){
						returnVal = (section[j] == section[k] && section[j] != 0) ? false : returnVal;
					}
				}
				
			}
		}
		
		return returnVal;
		
	}
	
	private boolean checkFilled() {  // Return true if the entire grid is filled
		
		boolean filled = true;
		
		for (int r=0; r<9; r++){
			for (int c=0; c<9; c++){
				filled = (numbers[r][c].getText() == "") ? false : filled;
			}
		}
		return filled;
	}
	
	private void generatePuzzle() {
		
		/**
		 * Puzzles are generated by a three-step process:
		 * 1) Seed two 3x3 sections (top-left, middle)
		 * 2) Fill in the rest of the grid using the solvePuzzle() method, then further randomize by interchanging rows & columns
		 * 3) Blank out a random square in the grid 300x by checking for safety, then sweep through and remove all safe squares.
		 * 
		 */
		
		// Seed upper-left section
		for (int r=0; r<3; r++){
			for (int c=0; c<3; c++){
				do{
					numbers[r][c].setText(new Integer ((int)(Math.random()*9+1)).toString());
					           
				} while (checkConflicts() == false);
			}
		}
		
		// Seed central section
		for (int r=3; r<6; r++){
			for (int c=3; c<6; c++){
				do{
					numbers[r][c].setText(new Integer ((int)(Math.random()*9+1)).toString());
					           
				} while (checkConflicts() == false);
			}
		}
		/*
		// Removed from method because it adds to the time to create a puzzle without significantly adding to the difficulty
		// Seed bottom-right section 
		for (int r=6; r<9; r++){
			for (int c=6; c<9; c++){
				do{
					numbers[r][c].setText(new Integer ((int)(Math.random()*9+1)).toString());
					           
				} while (checkConflicts() == false);
			}
		}
		*/
		
		// Fill the grid
		solvePuzzle();			
		
		// Randomizes grid **************
		
		for (int rCount=0; rCount<1000; rCount++){
			int r = (int)(Math.random()*9);
			if (r % 3 == 0){
				if (Math.random() < 0.5){
					for (int c=0; c<9; c++){
						String tempStr = numbers[r+1][c].getText();
						numbers[r+1][c].setText(numbers[r][c].getText());
						numbers[r][c].setText(tempStr);
					}	
				} else {
					for (int c=0; c<9; c++){
						String tempStr = numbers[r+2][c].getText();
						numbers[r+2][c].setText(numbers[r][c].getText());
						numbers[r][c].setText(tempStr);
					}
				}
			}
			if (r % 3 == 1){
				if (Math.random() < 0.5){
					for (int c=0; c<9; c++){
						String tempStr = numbers[r+1][c].getText();
						numbers[r+1][c].setText(numbers[r][c].getText());
						numbers[r][c].setText(tempStr);
					}	
				} else {
					for (int c=0; c<9; c++){
						String tempStr = numbers[r-1][c].getText();
						numbers[r-1][c].setText(numbers[r][c].getText());
						numbers[r][c].setText(tempStr);
					}
				}
			}
			if (r % 3 == 2){
				if (Math.random() < 0.5){
					for (int c=0; c<9; c++){
						String tempStr = numbers[r-1][c].getText();
						numbers[r-1][c].setText(numbers[r][c].getText());
						numbers[r][c].setText(tempStr);
					}	
				} else {
					for (int c=0; c<9; c++){
						String tempStr = numbers[r-2][c].getText();
						numbers[r-2][c].setText(numbers[r][c].getText());
						numbers[r][c].setText(tempStr);
					}
				}
			}
			
		}
		
		for (int cCount=0; cCount<1000; cCount++){
			int c = (int)(Math.random()*9);
			if (c % 3 == 0){
				if (Math.random() < 0.5){
					for (int r=0; r<9; r++){
						String tempStr = numbers[r][c+1].getText();
						numbers[r][c+1].setText(numbers[r][c].getText());
						numbers[r][c].setText(tempStr);
					}	
				} else {
					for (int r=0; r<9; r++){
						String tempStr = numbers[r][c+2].getText();
						numbers[r][c+2].setText(numbers[r][c].getText());
						numbers[r][c].setText(tempStr);
					}
				}
			}
			if (c % 3 == 1){
				if (Math.random() < 0.5){
					for (int r=0; r<9; r++){
						String tempStr = numbers[r][c+1].getText();
						numbers[r][c+1].setText(numbers[r][c].getText());
						numbers[r][c].setText(tempStr);
					}	
				} else {
					for (int r=0; r<9; r++){
						String tempStr = numbers[r][c-1].getText();
						numbers[r][c-1].setText(numbers[r][c].getText());
						numbers[r][c].setText(tempStr);
					}
				}
			}
			if (c % 3 == 2){
				if (Math.random() < 0.5){
					for (int r=0; r<9; r++){
						String tempStr = numbers[r][c-1].getText();
						numbers[r][c-1].setText(numbers[r][c].getText());
						numbers[r][c].setText(tempStr);
					}	
				} else {
					for (int r=0; r<9; r++){
						String tempStr = numbers[r][c-2].getText();
						numbers[r][c-2].setText(numbers[r][c].getText());
						numbers[r][c].setText(tempStr);
					}
				}
			}
			
		}
		// **************************************
		
		// Look at 300 random cells, empty them if the number is safe
		for (int i=0; i<300; i++){
			int r = (int)(Math.random()*9);
			int c = (int)(Math.random()*9);
			if (rowSafe(r,c) == true || colSafe(r,c) == true || secSafe(r,c) == true)  
				numbers[r][c].setText("");  
		}
		
		
		// Sweep through and remove any remaining safe numbers
		int count = 0;
		do {
			count = 0;
			for (int r=0; r<9; r++){
				for (int c=0; c<9; c++){
					if ((rowSafe(r,c) == true || colSafe(r,c) == true || secSafe(r,c) == true) && (numbers[r][c].getText().equals("") == false))  {
						numbers[r][c].setText("");
						count++;
					}
				}
			}
		
		} while (count > 0);
		
		
		// Log number of starting numbers
		// Purely for debug purposes.
		int cellCount = 0;
		for (int r=0; r<9; r++){
			for (int c=0; c<9; c++){
				if (numbers[r][c].getText().equals("") == false)
					cellCount++;
			}
		}
		bottomText.setText(cellCount+" numbers.");
		
		// Lock starting numbers
		for (int r=0; r<9; r++){
			for (int c=0; c<9; c++){
				lockedNumbers[r][c] = (numbers[r][c].getText().equals("") == false) ? true : false;
				if (numbers[r][c].getText().equals("") == false)
					numbers[r][c].setBackground(Color.LIGHT_GRAY);
			}
		}
	}
	
	// Returns true if the value of the argument square could not go anywhere else in that row.
	private boolean rowSafe(int r, int c) {  
		if (numbers[r][c].getText().equals(""))
			return true;
		else {
			int value = new Integer(numbers[r][c].getText());
			boolean returnVal = true;
			
			for (int cTest=0; cTest<9; cTest++){
				if (cTest != c && numbers[r][cTest].getText().equals("")){
					
					// Check for rowsafe by column
					boolean cSafe = false;
					for (int rTest=0; rTest<9; rTest++){
						if (numbers[rTest][cTest].getText().equals("")){} else
						cSafe = (numbers[rTest][cTest].getText().equals(""+value)) ? true : cSafe;						
					}
					
					
					// check for rowsafe by section
					boolean sSafe = false;
					int r0 = (r/3)*3;
					int c0 = (cTest/3)*3;
					
					for (int rTest1=r0; rTest1<r0+3; rTest1++){
						for (int cTest1 = c0; cTest1<c0+3; cTest1++){
							if (numbers[rTest1][cTest1].getText().equals("") || (rTest1 == r && cTest1 == c)){} else
								sSafe = (numbers[rTest1][cTest1].getText().equals(""+value)) ? true : sSafe;
						}
					}
					returnVal = (cSafe == false && sSafe == false) ? false : returnVal;
					
				}
				
				
			}
			
			
			
			return returnVal;
		}
		
		
	}
	
	// Returns true if the value of the argument square could not go anywhere else in that column.
	private boolean colSafe(int r, int c) {  
		if (numbers[r][c].getText().equals(""))
			return true;
		else {
			int value = new Integer(numbers[r][c].getText());
			boolean returnVal = true;
			
			for (int rTest=0; rTest<9; rTest++){
				if (rTest != r && numbers[rTest][c].getText().equals("")){
					
					// Check for colsafe by row
					boolean rSafe = false;
					for (int cTest=0; cTest<9; cTest++){
						if (numbers[rTest][cTest].getText().equals("")){} else
						rSafe = (numbers[rTest][cTest].getText().equals(""+value)) ? true : rSafe;
					}
					
					
					
					// check for colsafe by section
					boolean sSafe = false;
					int r0 = (rTest/3)*3;
					int c0 = (c/3)*3;
					
					for (int rTest1=r0; rTest1<r0+3; rTest1++){
						for (int cTest1 = c0; cTest1<c0+3; cTest1++){
							if (numbers[rTest1][cTest1].getText().equals("") || (cTest1 == c && rTest1 == r)){} else
								sSafe = (numbers[rTest1][cTest1].getText().equals(""+value)) ? true : sSafe;
						}
					}
					
					returnVal = (rSafe == false && sSafe == false) ? false : returnVal;
					
				}
				
				
			}
			
			return returnVal;
		}
		
		
	}
	
	// Returns true if the value of the argument square could not go anywhere else in that 3x3 section.
	private boolean secSafe(int r, int c){ 
		
		if (numbers[r][c].getText().equals(""))
			return true;
		else {
			int value = new Integer(numbers[r][c].getText());
			boolean returnVal = true;
			int r0 = (r/3)*3;
			int c0 = (c/3)*3;
			
			for (int rTest=r0; rTest<r0 +3; rTest++){
				for (int cTest=c0; cTest<c0 +3; cTest++){
					if (numbers[rTest][cTest].getText().equals("")){
					
						// Check for secsafe by row
						boolean rSafe = false;
						for (int cTest1=0; cTest1<9; cTest1++){
							if (numbers[rTest][cTest1].getText().equals("") || (cTest1 == c && rTest == r)){} else
								rSafe = (numbers[rTest][cTest1].getText().equals(""+value)) ? true : rSafe;
						}
						
						// 	Check for secsafe by column
						boolean cSafe = false;
						for (int rTest1=0; rTest1<9; rTest1++){
							if (numbers[rTest1][cTest].getText().equals("") || (rTest1 == r && cTest == c)){} else
								cSafe = (numbers[rTest1][cTest].getText().equals(""+value)) ? true : cSafe;						
						}
						
						returnVal = (rSafe == false && cSafe == false) ? false : returnVal;
					}
				}
			}
			
			return returnVal;
		}

	}
	
	
	private void solvePuzzle() {
		
		/**
		 * Method for solving a grid:
		 * 1) populate a 9x9 2D array of boolean corresponding to the 9x9 puzzle grid;
		 *    enter "true" (hereafter, "locked") if the square had a value before the solving method was called, "false" ("unlocked") otherwise.
		 * 2) Start at the first "unlocked" number. Give it value 1. Check for conflicts;
		 *    if none, move to the next square, otherwise try the next value (eg 2).
		 * 3) If a square throws a conflict for every value 1-9, set value to null, go back to the last unlocked square,
		 *    increment its value by 1, and start again.
		 * 4) If the first unlocked square goes all the way to 9 and there are still no possible solutions,
		 *    terminate the algorithm and declare the puzzle unsolvable.
		 * 
		 */
		
		if (checkConflicts() == true){ // Only attempt to solve if there are no conflicts
			// 	Lock squares
			for (int r=0; r<9; r++){
				for (int c=0; c<9; c++){
					lockedNumbers[r][c] = (numbers[r][c].getText().equals("")) ? false : true;
				}
			}
			
			AttemptSolve:
				for (int r=0; r<9; r++){
					for (int c=0; c<9; c++){
						
						// 	Empty, unlocked squares start at "1", and increment every time they are recursively returned to
						int attempt = (numbers[r][c].getText().equals("")) ? 1 : new Integer(numbers[r][c].getText()) + 1;
						if (lockedNumbers[r][c] == false && attempt < 10){
							do {	// Try each number 1-9 in a particular square until there is no conflict
								numbers[r][c].setText(new Integer(attempt).toString());
								attempt++;
								
							} while (checkConflicts() == false && attempt < 10);
							
							// Return to a previous unlocked square and try again
							if (checkConflicts() == false){
								numbers[r][c].setText("");
								do {
									if (c > 0){
										c--;
									}  else {
										c = 8;
										r--;
									} 
								} while (lockedNumbers[r][c] == true);
								
								// 	Need to actually go back one spot "too far" because the loop will increment the chosen square by one at the end
								if (c > 0){
									c--;
								}  else {
									c = 8;
									r--;
								} 
						
							}
							
							// If you go back to a square that has exhausted all attempts, keep going back
						} else if (lockedNumbers[r][c] == false && attempt > 9){
							numbers[r][c].setText("");
							do {
								if (c > 0){
									c--;
								}  else {
									c = 8;
									r--;
								} 
								// If you go all the way back to the beginning, give up.
								if (r<0){ 
									bottomText.setText("Unsolvable!!");
									break AttemptSolve;
								}
								
							} while (lockedNumbers[r][c] == true);
							
							if (c > 0){
								c--;
							}  else {
								c = 8;
								r--;
							} 
							
						}
				
					}
					
				}

		}
	}
	
	private void switchModes(){
		if (entryMode == false){
			entryMode = true;
			clearAll();
			modeButton.setText("> Game");
		} else {
			entryMode = false;
			clearAll();
			generatePuzzle();
			modeButton.setText("> Entry");
		}
	}

	// Only run during "Entry" mode when the debug button is pressed;
	// determines puzzle integrity by attempting to solve it and then erasing the solution (if it found one)
	private void checkGrid() {
		if (checkConflicts() == true){
		
			// Lock squares
			for (int r=0; r<9; r++){
				for (int c=0; c<9; c++){
					lockedNumbers[r][c] = (numbers[r][c].getText().equals("")) ? false : true;
				}
			}
		
			// Attempt to Solve
			solvePuzzle();
		
			if (checkFilled() == true)	
				bottomText.setText("Puzzle is solvable.");
			else
				bottomText.setText("Unsolvable!!");
			
			// Empty the unlocked squares
			for(int r=0; r<9; r++){
				for (int c=0; c<9; c++){
					if (lockedNumbers[r][c] == false)
						numbers[r][c].setText("");
				}
			}
			
			// Unlock all the squares
			for(int r=0; r<9; r++){
				for (int c=0; c<9; c++){
					lockedNumbers[r][c] = false;
				}
			}
		}
			
		
		
		
	}
	
	// Only used during "Game" mode when debug button is pressed;
	// Returns the safety of a square after every input.
	// Purely for debug purposes.
	private void debugMeth(String rc){
		if (entryMode == false){
			int r = new Integer(rc) / 10;
			int c = new Integer(rc) % 10;
		
			String rows = (rowSafe(r,c)) ? "Safe" : "Unsafe";
			String cols = (colSafe(r,c)) ? "Safe" : "Unsafe";
			String secs = (secSafe(r,c)) ? "Safe" : "Unsafe";
		
			bottomText.setText(bottomText.getText()+"  "+"Row: "+rows+"     Col: "+cols+"      Sec: "+secs);
		}
	}
	
	
	public static void main(String[] args) {
		
		@SuppressWarnings("unused")
		SudokuWindow window = new SudokuWindow();
	
	}

	// Allows you to change numberChosen by keystrokes.
	public void keyPressed(KeyEvent keyPressed) {
		
		char key = keyPressed.getKeyChar();
		
		if (key == '0' || key == '1' || key == '2' || key == '3' || key == '4' ||
		key == '5' || key == '6' || key == '7' || key == '8' || key == '9')
			switchChosen(new Character(key).toString());
		
		if (key == '`')
			if (numberChoosers[10].isSelected())
				numberChoosers[10].setSelected(false);
			else
				numberChoosers[10].setSelected(true);
		
	}

	// Unused
	public void keyReleased(KeyEvent key) {}
	public void keyTyped(KeyEvent key) {}
	
	

}
