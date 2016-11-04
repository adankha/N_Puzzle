import java.util.*;
import java.util.Random;

/**
 * One of our main classes is the Board class. The board class holds essentially most of the information for this project.
 * In short, the Board class helps us parse the current board as a string, computes the heuristic, and finds valid moves.
 * The board class takes 2 constructors.
 *      The first constructor is for when the user inputs 1 at the beginning of the program (to randomize)
 *      The second constructor is for when the user inputs 2 which is to provide their own board as a string
 *
 *      Moreover, the 2nd constructor is used to create new boards in our A* algorithm and add to our queue and our hashmap
 */
public class Board {

    private ArrayList<Integer> arrayOfNumbers = new ArrayList<>();  // Used to help with the randomization portion of the class
    private int freeSpaceXPosition; // Holds the open slot x coordinate
    private int freeSpaceYPosition; // Holds the open slot y coordinate
    private int[][] currentBoard = new int[Constants.EDGE_LENGTH][Constants.EDGE_LENGTH]; // This is where our Board is stored. In a 2D array

    /**
     * This is the constructor used to help randomize our board
     */
    protected Board(){

        for(int i = 0; i < Constants.BOARD_SIZE; i++)
            arrayOfNumbers.add(i);


        Random randomGenerator = new Random();                 // Create an instance of the Random class
        randomGenerator.setSeed(System.currentTimeMillis());   // Set the seed based on time
        Collections.shuffle(arrayOfNumbers, randomGenerator);  // Shuffle the numbers based in the array based on the seed

        int count = 0;
        for(int i = 0; i < Constants.EDGE_LENGTH; ++i){
            for(int j = 0; j < Constants.EDGE_LENGTH; ++j){
                currentBoard[i][j] = arrayOfNumbers.get(count);
                count++;

                // Set the grid position of the slot that is opened
                if(currentBoard[i][j] == 0){
                    setFreeSpacePosition(i, j);
                }
            }
        }
    }
    /**
     * This is our constructor that helps create our board based off of the user input and/or our A* algorithm
     * @param startingState
     */
    public Board(String startingState){

        char[] ch = startingState.toCharArray();
        int indexCounter = 0;
        for(int i = 0; i < Constants.EDGE_LENGTH; ++i){
            for(int j = 0; j < Constants.EDGE_LENGTH; ++j){
                currentBoard[i][j] = ch[indexCounter] - '0';
                // Set the grid position of the slot that is opened
                if(currentBoard[i][j] == 0){
                    setFreeSpacePosition(i, j);
                }
                indexCounter++;
            }
        }
    }

    /**
     * A setter that sets the zero position (open slot) for our board state
     * @param x holds the x coordinate position on our 2D Grid
     * @param y holds the y coordinate position on our 2D Grid
     */
    public void setFreeSpacePosition(int x, int y){
        freeSpaceXPosition = x;
        freeSpaceYPosition = y;
    }

    /**
     *  Grabs the x coordinate position of our zero
     * @return returns the x coordinate
     */
    public int getFreeSpaceXPosition(){
        return freeSpaceXPosition;
    }

    /**
     *  Grabs the y coordinate position of our zero (open slot)
     * @return returns the y coordinate
     */
    public int getFreeSpaceYPosition(){
        return freeSpaceYPosition;
    }


    /**
     *  Grabs the board we are evaluating and converts it into a string
     * @return returns a string of the boardState (ex: "345671280")
     */
    public String getBoardAsString(){

        String boardAsString = "";
        for(int i = 0; i < Constants.EDGE_LENGTH; ++i)
            for(int j = 0; j < Constants.EDGE_LENGTH; ++j)
                    boardAsString += Integer.toString(currentBoard[i][j]);

        return boardAsString;
    }

    /**
     * Displays the current board to the user.
     * Board substitutes an empty position where currentboard[i][j] = 0.
     */
    public void displayBoard(){

        for(int i = 0; i < Constants.EDGE_LENGTH; ++i) {
            System.out.print("   ");
            for (int j = 0; j < Constants.EDGE_LENGTH; ++j) {
                if(currentBoard[i][j] != 0)
                    System.out.print(currentBoard[i][j] + " ");
                else
                    System.out.print("  ");
            }
            System.out.println("");
        }
    }

    /**
     * Self explanatory- Computes the heuristic value of the current board.
     * @return the total heuristic value of the board
     */
    public int computeHeuristic(){

        int totalHeuristic = 0;
        int targetX;
        int targetY;

        for(int i = 0; i < Constants.EDGE_LENGTH; ++i) {
            for (int j = 0; j < Constants.EDGE_LENGTH; ++j) {

                // Finds the target (x,y) or (i,j) position the current number must sit in.
                if(currentBoard[i][j] != 0) {
                    targetX = (currentBoard[i][j] - 1) / Constants.EDGE_LENGTH;
                    targetY = (currentBoard[i][j] - 1) % Constants.EDGE_LENGTH;
                }
                // Case if the number being evaluated is 0 consider the bottom right corner of grid to be the position it needs to be in
                else{
                    targetX = Constants.EDGE_LENGTH-1;
                    targetY = Constants.EDGE_LENGTH-1;
                }
                totalHeuristic +=  Math.abs(i-targetX) + Math.abs(j-targetY);
            }
        }
        return totalHeuristic;
    }

    /**
     *
     * @param x Checks to see if the x coordinate passed in is indeed where our open slot (zero in our case) is.
     * @param y Checks to see if the y coordinate passed in is indeed where our open slot (zero in our case) is.
     * @return returns true or false if it meets the condition. Self explanatory.
     */
    public boolean isValidPosition(int x, int y){

        return (x == getFreeSpaceXPosition() && y == getFreeSpaceYPosition());
    }

    /**
     *
     * A simple swap function that swaps the (x,y) coordinates of 2 numbers in our 2D Grid.
     * We also update our zero (open slot) here after we have swapped.
     *
     * @param currX Holds the current x position of the number we want to move
     * @param currY Holds the current y coordinate position of the number we want to move
     * @param zeroX Holds the x coordinate of our open slot (zero in our case)
     * @param zeroY Holds the y coordinate of our open slot (zeri in our case)
     *
     */
    public void swap(int currX, int currY, int zeroX, int zeroY){

        int tmp;
        tmp = currentBoard[currX][currY];
        currentBoard[currX][currY] = currentBoard[zeroX][zeroY];
        currentBoard[zeroX][zeroY] = tmp;
        setFreeSpacePosition(currX, currY);

    }
    /**
     * Checks to see if the move the user wants to move is a valid position.
     * This is based off of where the 0 (or empty slot) is on the grid.
     *
     * Note: This function is used NOT for our A* Algorithm, but for the user interaction of the program.
     *
     * @param pieceNumber The number the user wants to move
     * @return true if valid move, false otherwise
     */
    public boolean checkValidAndMove(int pieceNumber){

        for(int i = 0; i <Constants.EDGE_LENGTH; ++i){
            for(int j = 0; j < Constants.EDGE_LENGTH; ++j){
                if(currentBoard[i][j] == pieceNumber){
                    if( isValidPosition((i-1),j) ){
                        swap(i,j, i-1, j);
                        return true;
                    }
                    if( isValidPosition(i,j+1) ){
                        swap(i, j, i, j+1);
                        return true;
                    }
                    if( isValidPosition(i+1,j) ){
                        swap(i, j, i+1, j);
                        return true;
                    }
                    if( isValidPosition(i,(j-1)) ) {
                        swap(i,j, i, j-1);
                        return true;
                    }
                }
            }
        }
        return false;
    }


    /**
     * A function used for our A* ALgorithm. It determines if a particular piece number is valid given a particular direction
     * @param pieceNumber A piece number is passed (between 0 and Constances.BOARD_SIZE)
     * @param direction The directions passed are either up, right, down and left.
     * @return We return true if we have determined if the move is valid given a particular piece number and direction.
     */
    public boolean isMoveValid(int pieceNumber, String direction){
        for(int i = 0; i <Constants.EDGE_LENGTH; ++i) {
            for (int j = 0; j < Constants.EDGE_LENGTH; ++j) {
                if (currentBoard[i][j] == pieceNumber) {
                    if(direction == "UP"){
                        if( isValidPosition((i-1),j) )
                            return true;
                    }
                    if(direction == "RIGHT"){
                        if( isValidPosition((i),j+1) )
                            return true;
                    }
                    if(direction == "DOWN"){
                        if( isValidPosition((i+1),j) )
                            return true;
                    }
                    if(direction == "LEFT"){
                        if( isValidPosition((i),j-1) )
                            return true;
                    }
                }
            }
        }
        return false;
    }

    /**
     *
     * A function that swaps the board, holds the new string after the swap, then returns it. Once it holds the string after the swap,
     * it swaps back to its original state.
     *
     * Note: This function is only ever called after we have determined if a direction (up,right,down,left) is a valid move.
     *
     * @param direction Holds the direction of where the algorithm wants to go. Options include up, right, down and left.
     * @return Returns the string of the board after the swap.
     */
    public String stringAfterSwap(String direction){

        String stringAfterSwap = "";


        if(direction == "UP"){
            swap(getFreeSpaceXPosition()+1, getFreeSpaceYPosition(), getFreeSpaceXPosition(), getFreeSpaceYPosition());
            stringAfterSwap = getBoardAsString();
            swap(getFreeSpaceXPosition()-1, getFreeSpaceYPosition(), getFreeSpaceXPosition(), getFreeSpaceYPosition());
        }
        if(direction == "RIGHT"){
            swap(getFreeSpaceXPosition(), getFreeSpaceYPosition()-1, getFreeSpaceXPosition(), getFreeSpaceYPosition());
            stringAfterSwap = getBoardAsString();
            swap(getFreeSpaceXPosition(), getFreeSpaceYPosition()+1, getFreeSpaceXPosition(), getFreeSpaceYPosition());
        }
        if(direction == "DOWN"){
            swap(getFreeSpaceXPosition()-1, getFreeSpaceYPosition(), getFreeSpaceXPosition(), getFreeSpaceYPosition());
            stringAfterSwap = getBoardAsString();
            swap(getFreeSpaceXPosition()+1, getFreeSpaceYPosition(), getFreeSpaceXPosition(), getFreeSpaceYPosition());
        }
        if(direction == "LEFT"){
            swap(getFreeSpaceXPosition(), getFreeSpaceYPosition()+1, getFreeSpaceXPosition(), getFreeSpaceYPosition());
            stringAfterSwap = getBoardAsString();
            swap(getFreeSpaceXPosition(), getFreeSpaceYPosition()-1, getFreeSpaceXPosition(), getFreeSpaceYPosition());
        }

        return stringAfterSwap;
    }
}
