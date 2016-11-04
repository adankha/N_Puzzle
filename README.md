# N_Puzzle
The 8-Puzzle Program in Java using the Manhattan Distances for the Heuristic
Author: Ashour Dankha, Class: CS342, Software Design, Year: Fall, 2016

Main idea/components for this program:

TilesDriver Class: Helps us prompt the user to play the N-Puzzle Game. If a user wants to play the game, they must either press 1 or 2. 1 implies the board is created at random using a seed 2 implies the board is created by the user by inputting a string

If a user wants to run the (A* ALgorithm), they must press "s" If a user wants to quit, they must press 0.

Board Class: Helps us parse the current board as a string, computes the heuristic, and finds valid moves.

Node Class: Used to store board states, heuristic values, parents, and total cost

SearchTree Class: Helps us solve the board in an optimal way using the manhattan heuristic, a hashmap (for constant time look-ups) and a priority queue.

Constants Class: Used to hold any constants used throughout the program
