import java.util.*;
import java.util.PriorityQueue;


/**
 *  This is our main class for our A* algorithm
 *  Main components include:
 *      A priority queue that holds the Node class which is organized based off of the totalCost of the node
 *      A hashmap that allows for immediate loop-ups to see if a node/boardState has been added to the queue yet and expanded
 *
 *      The algorithm used here is an A* algorithm with a heuristic that is famously known as the Manhattan Distance
 *
 *      A more extensive explanation is provided in the algorithmAStar method.
 */
public class SearchTree {

    private PriorityQueue<Node> pq = new PriorityQueue<>();
    protected HashMap<String, Node> hmap = new HashMap<>();
    private Board currentBoard;
    public int pathCount = 0;


    public SearchTree(Board startBoard){

        // Create a board class inside our search tree algorithm.
       currentBoard = startBoard;

        // Add the contents of our currentBoard into our pq. This will be our starting point.
        pq.add(new Node(currentBoard.getBoardAsString(), "NULL",      currentBoard.computeHeuristic(), 0));

        //Add the node to the closed list (So that we have unique nodes)
        hmap.put(currentBoard.getBoardAsString(), new Node(currentBoard.getBoardAsString(), "NULL", currentBoard.computeHeuristic(), 0));
    }

    /**
     *  This is where our A* algorithm lies. It returns a boolean which indicates if we have a solution or not.
     *  The algorithm uses two main components: A priority queue and a hashmap.
     *  The priority queue is used to store each node based off of their totalCost.
     *  We simply pop off the one with the lowest totalCost and then expand its neighbors.
     *  It's neighbors are dictated by what numbers/pieces are able to move into the empty slot.
     *  The hashmap is used for constant look-ups. We use this to see if a board state has already been
     *  pushed/added to our priority queue.
     *  If it has been added, we then check to see if we have found a better path with a lower cost to get to that state.
     *  If we have, then we update our totalCost for that state and we update the parent of that state.
     *  This ultimately helps us get our most optimal path.
     *
     * @return true if solution found, false otherwise
     */
    public boolean isSolvable(){


        // In our constructor above we have already added our start boardState into our queue
        // If our queue is empty, then we have expanded all states for our puzzle and there is no solution.
        while(!pq.isEmpty()){

            Node currentParent = pq.poll(); // Since we used compareTo in our Node class, we will always pop the one with the lowest totalCost

            // If our currently looked at Node has a heuristicValue of 0, then we found our solution.
            if(currentParent.heuristicValue == 0 || currentParent.boardState.equals(Constants.goalState8Puzzle)){
                return true;
            }

            // We make a new Board so that we can use the functionalities of the Board class to extract data to use in our Algorithm
            currentBoard = new Board(currentParent.boardState);

            // Loop through all the numbers (1-8) and check to see if we can move to our open space location
            // If we can, we then check to see if it is already contained in the hashmap.
            for(int i = 1; i < Constants.BOARD_SIZE; ++i){

                String childBoardState = "";  // Used to hold the string of our "child" board state
                boolean valid = false;        // Used to determine if we have found a valid move

                if(valid = currentBoard.isMoveValid(i, "UP")){          //Check to see if we can move up. If so, get the string of that new boardState
                    childBoardState = currentBoard.stringAfterSwap("UP");
                }
                else if(valid = currentBoard.isMoveValid(i, "RIGHT")) { //Check to see if we can move right. If so, get the string of that new boardState
                    childBoardState = currentBoard.stringAfterSwap("RIGHT");
                }
                else if(valid = currentBoard.isMoveValid(i, "DOWN")){   //Check to see if we can move down. If so, get the string of that new boardState
                    childBoardState = currentBoard.stringAfterSwap("DOWN");
                }
                else if(valid = currentBoard.isMoveValid(i, "LEFT")){   //Check to see if we can move left. If so, get the string of that new boardState
                    childBoardState = currentBoard.stringAfterSwap("LEFT");
                }


                if(valid && !hmap.containsKey(childBoardState)){        // Check to see if childBoard is in hashmap, if not, add to hashmap and to queue
                    Board b = new Board(childBoardState);               // Create a new board class with the childBoardState
                    hmap.put(childBoardState, new Node(b.getBoardAsString(), currentParent.boardState, b.computeHeuristic(), (b.computeHeuristic() + currentParent.totalCost) ));
                    pq.add(new Node(b.getBoardAsString(), currentParent.boardState, b.computeHeuristic(), (b.computeHeuristic() + currentParent.totalCost) ));
                }
                else if( hmap.containsKey(childBoardState) )            // If it is in hashmap, check to see if better solution path
                {
                    if( (currentParent.totalCost  + hmap.get(childBoardState).heuristicValue) < hmap.get(childBoardState).totalCost) {
                        hmap.get(childBoardState).totalCost   = currentParent.totalCost + hmap.get(childBoardState).heuristicValue; // Update totalCost if found better solution path
                        hmap.get(childBoardState).parentState = currentParent.boardState;   // Update parent
                        pq.add(hmap.get(childBoardState));
                    }
                }
            }
        }
        return false;   // If we reach here, then we know there is no solution.
    }

    /**
     *  This is a recursive print helper. It starts from the "tail" or goal state and works backwards until it reaches the startState
     *  This is a post order traversal.
     * @param start start holds the string value of the startState used for our algorithm
     * @param current begins with the tail end of our solution and works backwards by the parentState until it reachs the starts
     */
    public void printPathHelper(String start, String current){

        // If this condition is met, we have reached our end and it's time to loop back
        if(current.equals("NULL"))
            return;

        //Recursive helper call
        printPathHelper(start, hmap.get(current).parentState);


        pathCount++;                                    // Increment our pathCounter to show how many states needed for our solution
        int rowCounter = 0;                             // Used for visually orientation and to create a new line
        char[] boardAsArray = current.toCharArray();    // Convert the string to a char array and then print
        System.out.println("");
        System.out.println(pathCount +". ");
        System.out.print("   ");


        //Iterate through each character and print accordingly.
        for(int i = 0; i < boardAsArray.length; ++i){

            if( boardAsArray[i] != '0')
                System.out.print(boardAsArray[i] + " ");
            else
                System.out.print("  ");

            rowCounter++;

            if(rowCounter % Constants.EDGE_LENGTH == 0) {
                System.out.println("");
                if(rowCounter != Constants.BOARD_SIZE)
                  System.out.print("   ");
            }
        }
        System.out.println("Heuristic value: " + hmap.get(current).heuristicValue);
    }


    /**
     * printPath is a method that allows us to print our solution path of our puzzle. It uses the helper function above
     * @param start holds the startState of our algorithm
     */
    public void printPath(String start){

        String goal = Constants.goalState8Puzzle;
        printPathHelper(start, hmap.get(goal).parentState);

        pathCount++;
        System.out.println("");
        System.out.println(pathCount+".");
        System.out.println("   1 2 3");
        System.out.println("   4 5 6");
        System.out.println("   7 8  ");
        System.out.println("Heuristic value: 0");
    }
}
