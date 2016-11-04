import java.util.Scanner;

/**
 * Name: Ashour Dankha
 * NetID: Adankh2
 * Class: CS 342 Software Design
 * University of Illinois at Chicago
 * Fall, 2016
 *
 * Driver class that prompts user to play the N-Puzzle game.
 *  If a user no longer wants to play, they can either press "s" to have
 *  the heuristic to solve for them, or "0" to quit the program.
 *
 *  Initially the user must either press 1 or 2.
 *  1 will randomize a board for them.
 *  2 will prompt the user once more for a board they had in mind
 *
 */
public class TilesDriver {

    public static void main (String[] args) {

        printAuthorInformation();
        Scanner sc = new Scanner(System.in);
        System.out.print("Enter your choice --> ");

        int loopCounter = 0;                // Counter used for total times looped in program.
        boolean userWantsOwnPuzzle = false; // Used to check if the user wants to create their own puzzle/board
        Board theBoard = new Board();       // Initialize a board to use throughout the program

        while(true){

            String userInput = sc.next();

            try{
                // This if condition is for when the user wants to just run the A* algorithm
                if(userInput.equals("s") && loopCounter > 0) {
                    userWantsToRunAlgorithm(theBoard);
                    System.exit(0);
                }
                else if(userInput.equals("0")) {            // User wants to terminate the program
                    System.out.println("Thank you for playing. Goodbye.");
                    System.exit(0);
                }

                if(loopCounter == 0){               //Start of the program. Prompts user how they want to play.

                    if(userInput.equals("1")){      // Randomizes a board for the user
                        theBoard = new Board(); loopCounter++;
                        System.out.println("This is the board you will start with: ");
                        printPuzzleUI(theBoard, loopCounter);   //Prints the board and heuristic value for the user.
                    }
                    else if (userInput.equals("2")){ // Prompts user for a board
                        System.out.println("Some boards such as 728045163 are impossible." + "\nOthers such as 245386107 are possible." +
                                           "\nEnter a string of 9 digits (including 0) for the board --> ");
                        userWantsOwnPuzzle = true;
                    }
                    else
                        throw new NumberFormatException(" Please enter either 1 to random or 2 to enter your own puzzle to play.");
                }

                // If we are here, then we are executing the actual game part of the puzzle.
                else if(loopCounter >= 1){

                    if(loopCounter == 1 && userWantsOwnPuzzle){
                        checkIfValidString(userInput);  // Checks for errors: if the string user input works for an N-puzzle
                        theBoard = new Board(userInput);
                        System.out.println("This is the board you will start with: ");
                    }
                    else if (!theBoard.checkValidAndMove(Integer.parseInt(userInput)))      // Checks to see if user input is a valid move. If it is valid, then it moves it
                        System.out.println("Invalid move. Try again.\n"); loopCounter--;
                        isGoalBoardMet(theBoard, loopCounter);  // Checks to see if Heuristic = 0
                        printPuzzleUI(theBoard, loopCounter);   //Prints the board and heuristic value for the user.
                }
                loopCounter++;
            }
            catch (NumberFormatException e){
                System.out.println(e + " Please enter a valid number.");
            }
            catch (Exception e){
                System.out.println(e);
            }


        }//end of while(true)
    } // End of main()

    /**
     * Self explanatory: Prints the Author information and the game introduction
     */
    private static void printAuthorInformation(){

        // Header/Information for the game is printed out initially
        System.out.println("Author: Ashour Dankha" +
                "\nClass: CS 342: Software Design, Fall 2016 " +
                "\nProgram: #3, 8 Tiles" +
                "\n\nWelcome to the 8-tiles puzzle. " +
                "\nPlace the tiles in ascending numerical order. For each " +
                "\nmove enter the piece to be moved into the blank square, " +
                "\nor 0 to exit the program " +
                "\n\nChoose a game option: " +
                "\n1. Start playing. (Creates a random board for you!) " +
                "\n2. Set the starting configuration (You create the board!)" +
                "\n(Note: if you press \"s\" during the game, it will solve for you!)\n");
    }

    /**
     * Runs the A* Algorithm for the currently presented board displayed to the user.
     * If the algorithm doesn't find a solution, it prints the best board
     * @param theBoard
     */
    private static void userWantsToRunAlgorithm(Board theBoard){

        System.out.println("Initial Board is: ");
        theBoard.displayBoard();
        System.out.println("Heuristic value: "+ theBoard.computeHeuristic());
        System.out.println("Solving puzzle automatically...");

        SearchTree ST = new SearchTree(new Board(theBoard.getBoardAsString())); // Create an instance of our SearchTree class
        boolean checkForPath = ST.isSolvable();                                 // Run our A* Algorithm

        if(checkForPath) {  // If there is a solution we enter here
            ST.printPath(theBoard.getBoardAsString());
            System.out.println("Done.");
        }

        else{               // If there is not a solution, we enter here
            int minHeuristic = Integer.MAX_VALUE;
            int boardCount = 0;
            Node bestBoard = new Node();

            for( String key : ST.hmap.keySet() ){   // Helps us find the best puzzle that is not the actual solutio

                if(ST.hmap.get(key).heuristicValue < minHeuristic) {
                    minHeuristic = ST.hmap.get(key).heuristicValue;
                    bestBoard = ST.hmap.get(key);
                }
                boardCount++;
            }

            System.out.println("All "+ boardCount + " moves have been tried.");
            System.out.println("That puzzle is impossible to solve.  Best board found was: ");
            bestBoard.displayNode();
            System.out.println("Heuristic value: " +minHeuristic);
            System.out.println("Done.");
        }
    }

    /**
     * Parses the string user inputted (when user pressed 2 to create their own starting puzzle board) and checks to see if it is valid
     * @param userInput userInput is the string the user inputted
     * @return returns true if the board passed all the tests, throws an exception otherwise
     */
    private static void checkIfValidString(String userInput){

        char[] userInputInArray = userInput.toCharArray();          // Holds the string user inputs and stores into char array
        boolean [] isDupNumber = new boolean[Constants.BOARD_SIZE]; // Used to make sure no duplicates were inputted
        for(int i = 0; i < isDupNumber.length; ++i)                 // Initialize the boolean duplicate checker array to false
            isDupNumber[i] = false;

        if(userInput.equals(Constants.goalState8Puzzle)){
            System.out.println("Congratulations. You started off with the goal board. You're really good...");
            System.exit(0);
        }

        if(userInputInArray.length != Constants.BOARD_SIZE)         // If user didn't enter n-puzzle characters, then not enough to be placed on board.
            throw new NumberFormatException("Sorry, you did not enter in enough characters/numbers.");

        //Iterate through each character user input
        for(int i = 0; i < Constants.BOARD_SIZE; ++i){

            if (!Character.isDigit(userInputInArray[i]))                                                        // Every character must be a digit
                throw new NumberFormatException("You did not enter a valid digit.");
            if(userInputInArray[i] - '0' < 0 || userInputInArray[i] - '0' > (Constants.BOARD_SIZE-1) )          // Check to see if the parsed character is within the range of valid numbers ( 0 to (N - 1) )
                throw new NumberFormatException("One or more characters inputted is not between the range 0-8");
            if(!isDupNumber[(userInputInArray[i] - '0')])                                                       // If user character is not in the dup array, add it to the array
                isDupNumber[(userInputInArray[i] - '0')] = true;
            else if (isDupNumber[userInputInArray[i]- '0'])                                                     // Otherwise, it already is in the dup array and we have a problem!
                throw new NumberFormatException("You have a duplicated number. Rerun the program and try again.");
        }
    }

    /**
     * Prints the board we are currently on and it's heuristic value
     * @param theBoard Holds teh current board
     * @param loopCounter Holds the current loop counter
     */
    private static void printPuzzleUI(Board theBoard, int loopCounter){

        System.out.println(loopCounter+".");
        theBoard.displayBoard();
        System.out.println("Heuristic value: "+ theBoard.computeHeuristic());
    }

    /**
     * Prints the board we are currently on and it's heuristic value
     * @param theBoard Holds teh current board
     * @param loopCounter Holds the current loop counter
     */
    private static void isGoalBoardMet(Board theBoard, int loopCounter){
        if(theBoard.computeHeuristic() == 0){       // If user has found the solution path we enter here
            printPuzzleUI(theBoard, loopCounter);   //Prints the board and heuristic value for the user.
            System.out.println("Congratulations, you found a solution to the puzzle.\n Done.");
            System.exit(0);
        }
    }

}
