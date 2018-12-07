import java.io.File;
import java.io.FileNotFoundException;
import java.util.Arrays;
import java.util.Scanner;

public class Matrix {

	private int[][] matrix;
	private int size;

	public Matrix(int[][] array) { //constructor
		matrix = array;
		size = array.length;
	}

	public Matrix(File file) { //reads a file and converts it to an adjacency matrix
		parse(file);
		size = matrix.length;
	}

	public int getSize() { //returns the size
		return size;
	}

	public void parse(File file) {
		Scanner scanner = null;
		try {
			scanner = new Scanner(file);
		}
		catch (FileNotFoundException e) { //catches if the file is not valid
			e.printStackTrace();
		}

		int totalVert = scanner.nextInt(); //should get the first number in the document which is the number of nodes in the graph

		matrix = new int[totalVert][totalVert]; //builds a matrix with nodes x nodes cells

		for (int i = 0; i < totalVert; i++) { //runs through entire document
			int edges = scanner.nextInt(); //gets the first number in each line
			for (int j = 0; j < edges; j++) { 
				int connectedNode = scanner.nextInt(); //the nodes that are connected to the node you are on
				int weight = scanner.nextInt(); //the weights of the connected nodes
				matrix[i][connectedNode] = weight; //puts everything in the matrix
			}
		}
	}

	public boolean isConnected() { //determines if the graph is connected or not
		int[] marked = new int[matrix.length + 1]; //keeps track of nodes that have been visited
		depthFirstSearch(0, marked); //runs through depth first search

		return marked[marked.length - 1] == matrix.length; //returns whether or not graph is connected
	}

	private void depthFirstSearch(int n, int[] marked) { //runs through all the node until all have been visited
		marked[n] = ++marked[marked.length-1]; //marks the starting node (n) 

		for (int i = 0; i < matrix.length; i++) {
			if (matrix[n][i] != 0 && marked[i] == 0) {
				depthFirstSearch(i, marked); //recursively depth first searches 
			}
		}
	}

	public Matrix minSpanningTree() { //creates a minimum spanning tree of the graph
		//no changes were made to this, but for some reason it will now only print out if the graph is metric
		boolean[] marked = new boolean[matrix.length]; //keeps track of visited nodes
		marked[0] = true; //marks the starting location as visited

		int[][] minTree = new int[matrix.length][matrix.length]; //create a new array to hold the MST

		for(int i = 0; i < matrix.length - 1; i++) { //iterates through matrix
			int edge = 0;
			int node = 0;
			int minDistance = Integer.MAX_VALUE;
			for(int j = 0; j < matrix.length; j++) { //iterates through all the edges
				if (marked[j]) //only enter third for loop if already marked
					for(int k = 0; k < matrix.length; k++) {
						if (!marked[k] && minDistance > matrix[j][k]) { //iterates through nodes
							edge = j;
							node = k;
							minDistance = matrix[j][k]; //set min distance 
						}
					}
			}
			minTree[node][edge] = minTree[edge][node] = minDistance; //add the node/edge pair to the MST
			marked[node] = true; //set the last node visited to true
		}
//		for (int i = 0; i < matrix.length; i++)
//			for (int j =0; j < matrix.length; j++)
//				System.out.print(minTree[i][j]);
//		
		return new Matrix(minTree); //return new MST
	}

	public int[] shortestPath(int startingNode, boolean call) { //finds the shortest path in the graph and prints it out
		int[] bestPath = new int[matrix.length]; //array to hold the best path
		boolean[] marked = new boolean[matrix.length]; //keeps track of visited nodes
		int[] back = new int[matrix.length]; //keeps track of previous node visited

		for (int i = 0; i < matrix.length; i++) //initializes bestPath[] all to infinity 
			bestPath[i] = Integer.MAX_VALUE;

		bestPath[startingNode] = 0; //sets the starting location to 0

		for (int i = 0; i < matrix.length; i++) { //iterates through matrix
			int minIndex = -1;
			int min = Integer.MAX_VALUE;
			for (int j = 0; j < matrix.length; j++) {
				if (!marked[j] && bestPath[j] < min) { //if hasn't been marked and the bestPath is less than the min
					min = bestPath[j]; //make the min the bestPath
					minIndex = j; //change the index
				}
			}
			marked[minIndex] = true; //mark the node as being visited
			for (int j = 0; j < matrix.length; j++)
				if (matrix[minIndex][j] != 0 && !marked[j] && bestPath[minIndex] + matrix[minIndex][j] < bestPath[j]) {
					bestPath[j] = bestPath[minIndex] + matrix[minIndex][j]; //adds the current bestPath to the additional edge being added to the shortest path
					back[j] = minIndex; //set previously visited node to the index 
				}	
		}	
		if (call) { //in order to work for make metric, only print this out if calling shortestPath
			for (int i = 0; i < matrix.length; i++) {
				System.out.print(i + ": (" + bestPath[i] + ") "); //best path from node i
				printShortestPath(back, startingNode, i); //recursive helper method call
				System.out.println();
			}
			System.out.println(); 
		}
		
		return bestPath; //used for make metric
	}
	
	private void printShortestPath(int[] array, int startingNode, int currentNode) { //recursive helper method
		if (currentNode == startingNode) {
			System.out.print(" " + currentNode);
			return;
		}

		printShortestPath(array, startingNode, array[currentNode]); //iterates through reverse
		System.out.print(" -> " + currentNode);
	}

	public boolean isMetric() { //checks if a graph is metric
		if (!isConnected()) { //if the graph is metric, print error and return
			System.out.println("Graph is not metric: Graph is not connected.\n");
			return false;
		}

		for (int i = 0; i < matrix.length; i++) //these loops check if triangle inequality holds true
			for (int j = 0; j < matrix.length; j++)
				for (int k = 0; k < matrix.length; k++) {
					int side1 = matrix[i][j]; 
					int side2 = matrix[i][k];
					int side3 = matrix[j][k];
					if (side1 + side2 < side3) { 
						System.out.println("Graph is not metric: Edges do not obey the triangle inequality.\n");
						return false;
					}
				}	
		System.out.println("Graph is metric.\n"); //if false has not been returned then the graph must be metric
		return true;
	}

	public void makeMetric() { //fix
		if (!isConnected()) { //if the graph isn't connected, you can't make it metric
			System.out.println("Graph is not metric: Graph is not connected.\n");
			return;
		}
	
		for (int i = 0; i < matrix.length; i++) {
			int [] bestPath = shortestPath(i, false); //find shortest path
			for (int j = 0; j < matrix.length; j++) 
				matrix[i][j] = bestPath[j];
		}
		print(); //print out the new Matrix
		
		System.out.println();
	}


	public void TSP() {
		if (!isConnected()) { //cannot complete if graph isn't connected
			System.out.println("Error: Graph is not connected.\n");
			return;
		}
		int[] path = new int[matrix.length + 1]; //alows for the path length to be stored in the last location
		int[] bestPath = new int[matrix.length + 1]; //allows for the path length to be stored in the last location
		boolean[] marked = new boolean[matrix.length]; 
		
		marked[0] = true;
		
		TSP(path, bestPath, marked, 0);
		
		System.out.print(bestPath[bestPath.length - 1] + ": "); //prints length of best path
		for (int i = 0; i < matrix.length; i++)
			System.out.print(bestPath[i] + " -> "); //and the order in each node is visited 
		
		System.out.println();
		System.out.println();	
	}

	private void TSP(int[] path, int[] bestPath, boolean[] marked, int currentNode) { //recursive helper method
		int count = 0;
	
		if (count != matrix.length - 1) //as long as it doesn't equal the path length being stored in the last index
			for (int i = 0; i < matrix.length; i++) {
				if (!marked[i] && matrix[currentNode][i] != 0) {
					count++;
					marked[i] = true; //set node as visited
					path[count] = i; //update count
					path[bestPath.length - 1] += matrix[currentNode][i]; //adds to the bestPath
					currentNode++;
					TSP(path, bestPath, marked, i);
					count--; //reset to avoid repeating nodes
					marked[i] = false;
					path[path.length - 1] -= matrix[currentNode][i];
				}
			}
		else if (bestPath[bestPath.length - 1] == 0 || path[path.length - 1] + matrix[currentNode][0] < bestPath[bestPath.length - 1]) { //verifies that best is the best by comparing it to path and changing it if necessary
			path[path.length - 1] += matrix[currentNode][0];
			for (int i = 0; i < matrix.length + 1; i++)
				bestPath[i] = path[i];
		}
	}

	public void approximateTSP() { //calculates approximation to the TSP by finding the MST and touring all the edges
		if (!isMetric()) //if the graph isn't metric, return
			return;
		else {
			int[] bestPath = new int[matrix.length];
			//boolean[] marked = new boolean[matrix.length];
			//Matrix MST = minSpanningTree(); //creates new matrix that's the MST
			//int doubleEdges = 0; //will be used to hold the doubled values of MST

//			for (int i = 0; i < matrix.length; i++) 
//				bestPath[i] = Integer.MAX_VALUE; //initializes bestPath

//			for (int i = 0; i < matrix.length; i++)
//				for (int j = 0; j < matrix.length; j++)
//					doubleEdges += MST.matrix[i][j]; //adds all the edges

			depthFirstSearch(0, bestPath); //calls private helper method
			//System.out.print( + ": "); //prints out the path length
			
			for (int i = 0; i < matrix.length; i++)
				System.out.print(bestPath[i] + " -> ");
			
			System.out.print(bestPath[0] + "\n"); //pretty sure we aren't supposed to do this but...
		}
	}

//	private void depthFirstTraversal(boolean[] marked, int n, int[] path, Matrix MST) { //private helper method
//		marked[0] = true;
//		int position = 0;
//		
//		while (position != matrix.length && path[position] < matrix.length) //gets to correct location
//			position++;
//		
//		if (position == matrix.length)
//			position--;
//		
//		path[position] = n;
//		
//		for (int i = 0; i < MST.size; i++)  
//			if (!marked[i] && MST.matrix[n][i] != 0)
//				depthFirstTraversal(marked, i, path, MST); //recursive
//	}

	public void print() { //used to print out the graph 
		System.out.println(matrix.length);

		for (int i = 0; i < matrix.length; i++) {
			int count = 0;
			for (int j = 0; j < matrix.length; j++)
				if (matrix[i][j] != 0) 
					count++;
			System.out.print(count + " ");
			for (int j = 0; j < matrix.length; j++)
				if (matrix[i][j] != 0) {
					System.out.print(j + " ");
					System.out.print(matrix[i][j] + " ");
				}
			System.out.println(" ");
		}
	}
}
