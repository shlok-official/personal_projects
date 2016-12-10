package tictactoe;
import javax.swing.*;
import java.util.*;
import java.io.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class tictactoe {
	public static int num = 0;				//number of players
	public static int ROWS = 0, COLS = 0; 	//number of rows and columns
	public static int rules = 0;			//number of winning pieces
	public static int counter = 1;			//count the pieces in one line
	public static int nextplayer = 0;		//who will do the next step from a saved game
	public static int[] s = new int[1];		//Array container of the coordinate
	public static String[][] board = new String[999][999];	//define the whole board
	public static String start;				//resume or new
	public static Scanner st = new Scanner(System.in);
	public static Scanner in = new Scanner(System.in);
	public static int moveCount;			//total step
	public static int boardsize;			//board size

	public static void main(String[] args) {
		while (true) {//to ask if players want to resume or new a game
			try {
				System.out.println("would like to resume a saved game?(enter 'Y' or 'N')");
				start = st.nextLine();
				if ("Y".equals(start) || "y".equals(start) || "n".equals(start) || "N".equals(start))
					break;
				System.out.println("Error! Please enter 'Y' or 'N'");
			} catch (Exception e) {
				System.out.println("Error! Please enter 'Y' or 'N'");
			}
		}
		
		if ("Y".equals(start) || "y".equals(start)) {//if player want to resume a game
			System.out.println("Enter the name of your file");
			resume();
			printBoard();// print last board
			do {		//for the round which may not start from the first player
				nextplayer++;
				s = getLocation(nextplayer, boardsize); 				//get the coordinate
				board[s[0] - 1][s[1] - 1] = symbol(nextplayer);			//put the piece on the board
				if (win(s[0] - 1, s[1] - 1, symbol(nextplayer)) == 1) {	//for winning
					printBoard();
					System.out.println("Player " + nextplayer + " win!");
					System.exit(0);
				}
				if (win(s[0] - 1, s[1] - 1, symbol(nextplayer)) == 2) {	//for draw
					printBoard();
					System.out.println("-----Game is Draw-----");
					System.exit(0);
				}
				printBoard();
			} while (nextplayer < num);
			do {//game from the first player
				for (int o = 1; o <= num; o++) {
					s = getLocation(o, boardsize);					//get the coordinate
					board[s[0] - 1][s[1] - 1] = symbol(o);			//put the piece on the board
					if (win(s[0] - 1, s[1] - 1, symbol(o)) == 1) {	//for winning
						printBoard();
						System.out.println("Player " + o + " win!");
						System.exit(0);
					}
					if (win(s[0] - 1, s[1] - 1, symbol(o)) == 2) {	//for draw
						printBoard();
						System.out.println("-----Game is Draw-----");
						System.exit(0);
					}
					printBoard();
				}
			} while (true);
		}

		if ("N".equals(start) || "n".equals(start)) {// new a game
			do { // judgement(rules,num,ROWS)
				num = players(); // player number
				ROWS = boardsize(); // board size
				COLS = ROWS;
				rules = rule(); // win sequence count
			} while (judgement(rules, num, ROWS) != 1);

			for (int x = 0; x < ROWS; x++) {// Initialize array
				for (int y = 0; y < COLS; y++) {
					board[y][x] = " ";
				}
			}
			printBoard();// print the empty board
			do {// win or tie, save quit and resume
				for (int o = 1; o <= num; o++) {
					s = getLocation(o, boardsize);
					board[s[0] - 1][s[1] - 1] = symbol(o);
					if (win(s[0] - 1, s[1] - 1, symbol(o)) == 1) {
						printBoard();
						System.out.println("Player " + o + " win!");
						System.exit(0);
					}
					if (win(s[0] - 1, s[1] - 1, symbol(o)) == 2) {
						printBoard();
						System.out.println("-----Game is Draw-----");
						System.exit(0);
					}
					printBoard();
				}
			} while (true);
		}
	}

	public static int[] getLocation(int player, int boardsize) {		//function for getting coordinates
		String[] arr = new String[1];		//array container
		int[] tmp = new int[2];				//value container of coordinates and symbol
		while (true) {
			try {
				System.out.println("It is player " + player + "'s turn !" + "\n" + "You can press S to save or Q to Save and Quit" + "\n"
						+ "Or " + "you can enter the numbers of row and column you want : ");
				in = new Scanner(System.in);
				String str0 = in.nextLine();
				String check = "^([0-9]*[1-9][0-9]*)\\s([0-9]*[1-9][0-9]*)$";
		        Pattern regex = Pattern.compile(check);
		        Matcher matcher = regex.matcher(str0);
				if (matcher.matches() == true){
					arr = str0.split(" ");
					int r = Integer.parseInt(arr[0]);
					int c = Integer.parseInt(arr[1]);
					if(r>=1 && r<=boardsize && c>=1 && c<=boardsize && board[r-1][c-1].equals(" ") == true){
						tmp[0] = r;
						tmp[1] = c;
						tmp[2] = player;
						break;
					}
					else
						System.out.println("Wrong coordinate ! Please enter again!");
				}
				else if(str0.matches("^[Qq]+$")){ //save and quit game
					save();
					System.out.println("Game has been saved. " + "Game over");
					System.exit(0);
				}
				else if(str0.matches("^[Ss]+$")){// save game
					save();
					System.out.println("Game has been saved. " + "Continue");
				}
				else
					System.out.println("Error! Please enter two integer separated by spaces representing coordinate(rows and columns) you want : ");
			} catch (Exception e) {
				System.out.println("Error! Please enter two integer separated by spaces representing coordinate(rows and columns) you want : ");
			}
		}
		moveCount++;
		return tmp;
	}

	public static void save() {//save the game to a txt
		System.out.println("Saving... Enter the name of your file");
		String filename = st.nextLine();
		try {
			File f = new File("c:\\"+filename+".txt");//get a empty text for empty board
			FileWriter fww = new FileWriter(f);
			fww.write("");
			fww.close();

			File locationfile = new File("c:\\"+filename+".txt");
			FileWriter fw = new FileWriter(locationfile, true);
			fw.write(num + " " + boardsize + " " + rules + " " + moveCount + "\r\n"); // write the game frame on first line
			fw.close();
		} catch (Exception e) {
			JOptionPane.showMessageDialog(null, "cannot create file");
			e.printStackTrace();
		}
		for (int row = 0; row < ROWS; row++)
			for (int col = 0; col < COLS; col++)
				if (board[row][col] != " ") {	//write the coordinate and symbol on next line
					try {
						File locationfile = new File("d:\\" + filename + ".txt");
						FileWriter fw = new FileWriter(locationfile, true);
						fw.write(row + " " + col + " " + board[row][col] + "\r\n");
						fw.close();
					} catch (Exception e) {
						JOptionPane.showMessageDialog(null, "cannot create file");
						e.printStackTrace();
					}
				}
	}

	public static void resume() {//resume a game from a txt
		int count = 0;//count the line of the txt
		while (true) {
			try {
				String filename = st.nextLine();
				File file = new File("d:\\" + filename + ".txt");
				if (file.isFile() && file.exists()) { //to judge if the file exists
					InputStreamReader read = new InputStreamReader(new FileInputStream(file), "GBK");
					BufferedReader bufferedReader = new BufferedReader(read);
					String lineTxt = null;
					while ((lineTxt = bufferedReader.readLine()) != null) {//read the file
						count++;
						if (count == 1) {//the first line is the game frame
							String[] ss = lineTxt.split(" ");
							num = Integer.parseInt(ss[0]);
							boardsize = Integer.parseInt(ss[1]);
							ROWS = boardsize;
							COLS = boardsize;
							rules = Integer.parseInt(ss[2]);
							moveCount = Integer.parseInt(ss[3]);
							nextplayer = moveCount % num;
							for (int x = 0; x < ROWS; x++) {// Initialize array
								for (int y = 0; y < COLS; y++) {
									board[y][x] = " ";
								}
							}
						}
						if (count > 1) {//other line is the coordinate and symbol of player
							String[] ss = lineTxt.split(" ");
							int num1 = Integer.parseInt(ss[0]);
							int num2 = Integer.parseInt(ss[1]);
							String playersymbol = ss[2];
							board[num1][num2] = playersymbol;
						}
					}
					read.close();
					break;
				} else {
					System.out.println("Can't find the file. Please Enter the name again");
				}
			} catch (Exception e) {
				System.out.println("Error!");
				e.printStackTrace();
			}
		}
	}

	public static int win(int ROW, int COL, String symbol) {
		//Win for Column
		int deltarow = 0;
		int deltacol = 0;
		if (COL < COLS - 1) {
			if (board[ROW][COL + 1].equals(symbol)) {
				counter++;
				deltarow = 0;
				deltacol = 1;
				next(ROW, COL + 1, deltarow, deltacol, counter, symbol);
			}
		}
		if (COL >= 1) {
			if (board[ROW][COL - 1].equals(symbol)) {
				counter++;
				deltarow = 0;
				deltacol = -1;
				next(ROW, COL - 1, deltarow, deltacol, counter, symbol);
			}
		}
		if (counter == rules)
			return 1;

		//Win for Rows
		counter = 1;//count the pieces
		deltarow = 0;
		deltacol = 0;
		if (ROW < ROWS - 1) {
			if (board[ROW + 1][COL].equals(symbol) && ROW >= 0 && COL >= 0) {
				counter++;
				deltarow = 1;
				deltacol = 0;
				next(ROW + 1, COL, deltarow, deltacol, counter, symbol);
			}
		}
		if (ROW >= 1) {
			if (board[ROW - 1][COL].equals(symbol) && ROW >= 1 && COL >= 0) {
				counter++;
				deltarow = -1;
				deltacol = 0;
				next(ROW - 1, COL, deltarow, deltacol, counter, symbol);
			}
		}
		if (counter == rules)
			return 1;

		//Win for diagonal
		counter = 1;//count the pieces
		deltarow = 0;
		deltacol = 0;
		if (ROW < ROWS - 1 && COL < COLS - 1) {
			if (board[ROW + 1][COL + 1].equals(symbol) && ROW >= 0 && COL >= 0) {
				counter++;
				deltarow = 1;
				deltacol = 1;
				next(ROW + 1, COL + 1, deltarow, deltacol, counter, symbol);
			}
		}
		if (ROW >= 1 && COL >= 1)
			if (board[ROW - 1][COL - 1].equals(symbol)) {
				counter++;
				deltarow = -1;
				deltacol = -1;
				next(ROW - 1, COL - 1, deltarow, deltacol, counter, symbol);
			}
		if (counter == rules)
			return 1;

		// Win for back-diagonal
		counter = 1;//count the pieces
		deltarow = 0;
		deltacol = 0;
		if (ROW >= 1 && COL < COLS - 1) {
			if (board[ROW - 1][COL + 1].equals(symbol) && COL >= 0) {
				counter++;
				deltarow = -1;
				deltacol = 1;
				next(ROW - 1, COL + 1, deltarow, deltacol, counter, symbol);
			}
		}
		if (COL >= 1 && ROW < ROWS - 1) {
			if (board[ROW + 1][COL - 1].equals(symbol) && ROW >= 0 && COL >= 1) {
				counter++;
				deltarow = 1;
				deltacol = -1;
				next(ROW + 1, COL - 1, deltarow, deltacol, counter, symbol);
			}
		}
		if (counter == rules)
			return 1;

		counter = 1;
		if (moveCount == (boardsize * boardsize))// draw
			return 2;

		return 0;
	}

	public static void next(int ROW, int COL, int deltarow, int deltacol, int count, String symbol) {//recursive function for counting pieces
		if (ROW + deltarow >= 0 && COL + deltacol >= 0)
			if (board[ROW][COL].equals(board[ROW + deltarow][COL + deltacol])) {
				counter++;
				ROW = ROW + deltarow;
				COL = COL + deltacol;
				next(ROW, COL, deltarow, deltacol, counter, symbol);
			}
	}

	public static int players() {//get number of players
		while (true) {
			try {
				in = new Scanner(System.in);
				System.out.println("Please enter the number of players (must between 2 and 26): ");
				String str1 = in.nextLine();
				if (str1.matches("^+?[1-9][0-9]*$") && Integer.parseInt(str1)>=2 && Integer.parseInt(str1)<=26 ){
					num = Integer.parseInt(str1);
					break;
				}
				System.out.println("Error! Please enter a integer between 2 and 26 !");
			} catch (Exception e) {
				System.out.println("Error! Please enter a integer ");
			}
		}
		return num;
	}

	public static int boardsize() {// get size of board
		while (true) {
			try {
				in = new Scanner(System.in);
				System.out.println("Please enter the number of rows(columns) (must between 3 and 999) : ");
				String str2 = in.nextLine();
				if (str2.matches("^+?[1-9][0-9]*$") && Integer.parseInt(str2)>=3 && Integer.parseInt(str2)<=999 ){
					boardsize = Integer.parseInt(str2);
					break;
				}
				System.out.println("Error! Please enter the number of rows(columns) (must between 3 and 999) : ");
			} catch (Exception e) {
				System.out.println("Error! Please enter a integer ! ");
			}
		}
		return boardsize;
	}

	public static int rule() {// get rules
		while (true) {
			try {
				in = new Scanner(System.in);
				System.out.println("Please enter the win sequence count(>=3) : ");
				String str3 = in.nextLine();
				if (str3.matches("^+?[1-9][0-9]*$") && Integer.parseInt(str3)>=3){
					rules = Integer.parseInt(str3);
					break;
				}
				System.out.println("Error! Please enter a integer greater than 3! ");
			} catch (Exception e) {
				System.out.println("Error! Please enter a integer! ");
			}
		}
		return rules;
	}

	public static void printBoard() {// print the board
		System.out.print("   ");// print orders of col on the first row
		for (int col = 0; col < COLS; col++) {
			if (col < 8)
				System.out.print(" " + (col + 1) + "  ");
			else if (col <= 99 && col >= 8)
				System.out.print(" " + (col + 1) + " ");
			else
				System.out.print((col + 1) + " ");
		}

		System.out.println();// first row end

		for (int row = 0; row < ROWS - 1; row++) {// start from the second row:
													// print row
													// orders,space,-,+ and |
			if (row <= 8) {// the orders of row
				System.out.print("  " + (row + 1));
			} else if (row > 8 && row < 99) {
				System.out.print(" " + (row + 1));
			} else
				System.out.print(row + 1);

			for (int col = 0; col < COLS - 1; ++col) {// the board of every
														// column on one row
				System.out.print(" " + board[row][col] + " |");
			}
			System.out.print(" " + board[row][COLS - 1] + " ");
			System.out.println();// end this row(has order)
			System.out.print("   ");
			for (int col = 0; col < ROWS - 1; ++col) {// the board of every
														// column on the '---'
          										// row
				System.out.print("---+");
			}
			System.out.print("---");
			System.out.println();// end this row(no order)
		}
		if (ROWS - 1 <= 8) {// the orders of row
			System.out.print("  " + ROWS);
		} else if (ROWS - 1 > 8 && ROWS - 1 < 99) {
			System.out.print(" " + (ROWS));
		} else
			System.out.print(ROWS);
		for (int col = 0; col < COLS - 1; ++col) {// the board of every Column
													// on LAST row
			System.out.print(" " + board[ROWS - 1][col] + " |");
		}
		System.out.print(" " + board[ROWS - 1][COLS - 1] + " ");
		System.out.println();
		System.out.print("   ");
		System.out.println();
	}

	public static String symbol(int player) {// symbol of players
		switch (player) { // only for the a board
		case 1:
			return "O";
		case 2:
			return "X";
		case 3:
			return "A";
		case 4:
			return "B";
		case 5:
			return "C";
		case 6:
			return "D";
		case 7:
			return "E";
		case 8:
			return "F";
		case 9:
			return "G";
		case 10:
			return "H";
		case 11:
			return "I";
		case 12:
			return "J";
		case 13:
			return "K";
		case 14:
			return "L";
		case 15:
			return "M";
		case 16:
			return "N";
		case 17:
			return "P";
		case 18:
			return "Q";
		case 19:
			return "R";
		case 20:
			return "S";
		case 21:
			return "T";
		case 22:
			return "U";
		case 23:
			return "V";
		case 24:
			return "W";
		case 25:
			return "Y";
		case 26:
			return "Z";
		}
		return null;
	}

	public static int judgement(int winnumber, int player, int sizeofboard) {//judge if winning is possible
		if (winnumber > sizeofboard || winnumber < 3) {
			System.out.println("Out of boardsize! Please enter again!");
			return -1;
		} else if (player * (winnumber - 1) + 1 > sizeofboard * sizeofboard) {
			System.out.println("Winning is impossible if we need " + rules + " marks in line on a " + boardsize + "X"
					+ boardsize + " board with " + num + " players. Please enter again!");
			return -1;
		} else
			return 1;
	}

}