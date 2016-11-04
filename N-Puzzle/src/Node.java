
/**
 * A node class that implements the Comparable to allow us to use our compareTo method to compare nodes
 * based off of their total cost. This total cost is calculated by taking the total cost of their parent + their heuristic value.
 * Having this compareTo allows us to use our priority queue efficiently when our A* algorithm decides what to state to choose next.
 */
public class Node implements Comparable<Node> {


    String boardState;        // Holds the state of the board
    String parentState;       // Used to link the nodes
    int heuristicValue;       // Manhattan distance
    int totalCost;            // Total cost (parents total cost + current heuristic)

    /**
     *
     * @param bState holds the current board state
     * @param pState holds the parent board state (used in the A* Algorithm)
     * @param heuristic holds the Manhattan Distance
     * @param totCost holds the total cost from the parent node to get to the current node
     */
    public Node(String bState, String pState, int heuristic, int totCost)
    {
        boardState = bState;
        parentState = pState;
        heuristicValue = heuristic;
        totalCost = totCost;
    }

    /**
     * A constructor used to help create a node.
     */
    public Node(){

    }

    /**
     *
     * @param currentNode Holds the current node and evaluates to organize the priority queue as a min_heap.
     * @return returns 1 if node passed in is less than the current node, -1 if greater, else 0
     */
    public int compareTo(Node currentNode){
        if (this.totalCost < currentNode.totalCost)
            return -1;
        if(this.totalCost > currentNode.totalCost)
            return 1;
        else
            return 0;

    }

    /**
     * Method used to help display the node content. (Used when there isn't a solution for the puzzle and it prints the best node)
     */
    public void displayNode(){

        char[] boardAsArray = this.boardState.toCharArray();
        System.out.println("");
        int rowCounter = 0;
        System.out.print("   ");

        for(int i = 0; i < boardAsArray.length; ++i){

            if( boardAsArray[i] == '0')
                System.out.print("  ");
            else
                System.out.print(boardAsArray[i] + " ");
            rowCounter++;
            if(rowCounter % Constants.EDGE_LENGTH == 0) {
                System.out.println("");
                if(rowCounter != Constants.BOARD_SIZE)
                    System.out.print("   ");
            }
        }
    }

}
