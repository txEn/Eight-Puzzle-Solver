# Eight-Puzzle-Solver
Java implementation of an eight puzzle solving algorithm.  

##Search methods used
The search methods used and their codes for the input are:  
BFS    - 1  
DFS    - 2  
IDDFS  - 3  
Greedy - 4  
A*     - 5  

##Input
Input is initial state followed by final state, followed by type of search you want to use.  
For optimal and complete results use A*.

###Example
1 2 3  
4 5 6  
7 8 0  
1 3 2  
4 6 5  
7 8 0  
5  

or just put that in a file and use "$cat < file.txt | java EightSearch"
