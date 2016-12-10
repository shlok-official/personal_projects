/**
 * Created by ShlokDixit on 09/12/16.
 */

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.Scanner;
public class game  implements Serializable {

    char[][] table = new char[3][3];
//    static final char EMPTY = ' ';
//    static final char PLAYER_X = 'x';
//    static final char PLAYER_O = 'o';

    public void initializeGame()
    {
        for (int i = 0; i < 3; i++)
            for (int p=0; p < 3; p++)
                table [i][p]= ' ';
    }

    public char changePlayer(char player) {
        return player == 'o' ? 'x' : 'o';
    }
    public void changeBoard(char player, int row, int column) {
        table[row][column] = player;

    }
    public void displayBoard() {

        System.out.println("  0  " + table[0][0] + "|" + table[0][1] + "|" + table[0][2]);
        System.out.println("    --+-+--");
        System.out.println("  1  " + table[1][0] + "|" + table[1][1] + "|" + table[1][2]);
        System.out.println("    --+-+--");
        System.out.println("  2  " + table[2][0] + "|" + table[2][1] + "|" + table[2][2]);
        System.out.println("     0 1 2 ");
    }
    public boolean checkIfWinner() {
        if (table[0][0] == table[1][0] && table[1][0] == table[2][0] && (table[0][0] == 'x' || table[0][0] == 'o')) {
            return true;
        } else if (table[0][1] == table[1][1] && table[1][1] == table[2][1] && (table[0][1] == 'x' || table[0][1] == 'o')) {
            return true;
        } else if (table[0][2] == table[1][2] && table[1][2] == table[2][2] && (table[0][2] == 'x' || table[0][2] == 'o')) {
            return true;
        } else if (table[0][0] == table[0][1] && table[0][1] == table[0][2] && (table[0][0] == 'x' || table[0][0] == 'o')) {
            return true;
        } else if (table[1][0] == table[1][1] && table[1][1] == table[1][2] && (table[1][0] == 'x' || table[1][0] == 'o')) {
            return true;
        } else if (table[2][0] == table[2][1] && table[2][1] == table[2][2] && (table[2][0] == 'x' || table[2][0] == 'o')) {
            return true;
        } else if (table[0][0] == table[1][1] && table[1][1] == table[2][2] && (table[0][0] == 'x' || table[0][0] == 'o')) {
            return true;
        } else if (table[2][0] == table[1][1] && table[1][1] == table[0][2] && (table[2][0] == 'x' || table[2][0] == 'o')) {
            return true;
        } else {
            return false;
        }
    }

    public boolean checkIfTie() {
        for (int i = 0; i < 3; i++) {
            for (int p = 0; p < 3; p++) {
                if (table[i][p] == ' ') {
                    return false;
                }
            }
        }

        return true;
    }
    public boolean checkIfLegal(int row, int column) {
        if ((row > 2 || column > 2) || (row < 0 || column < 0)) {
            return true;
        } else if (table[row][column] == 'x' || table[row][column] == 'o') {
            return true;
        }

        return false;
    }
    public void saveGame(game g){
        try {
            FileOutputStream fileOut = new FileOutputStream("gameSaved.ser");
            ObjectOutputStream out = new ObjectOutputStream(fileOut);
            out.writeObject(g);
            out.close();
            fileOut.close();
            System.out.printf("Serialized data is saved in gameSaved.ser");
        }catch(IOException i) {
            i.printStackTrace();
        }
    }

    public static void main(String args[]){
        char player = 'o';
        int row, column;
        Scanner k = new Scanner(System.in);
        game g = new game();
        g.initializeGame();
        g.displayBoard();
        System.out.println("Game ready !\n");

        while (true) {
            player = g.changePlayer(player);
            System.out.print("\n" + player + " ,choose your location (row, column):");
            row = k.nextInt();
            column = k.nextInt();

            if( row == '2' || column == '2' ){
                g.saveGame(g);
            }

            while (g.checkIfLegal(row, column)) {
                System.out.println("That slot is already taken or out of bounds, please try again (row, column).");
                g.displayBoard();
                row = k.nextInt();
                column = k.nextInt();
            }
            g.changeBoard(player, row, column);
            g.displayBoard();

            if (g.checkIfWinner()) {
                System.out.println("\nThe winner is " + player + " !");
                break;

            }
            if (g.checkIfTie()) {
                System.out.println("\nThe game is a tie !");
                break;
            }
        }



    }

}
