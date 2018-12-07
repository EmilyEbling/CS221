import java.io.File;
import java.io.FileNotFoundException;
import java.util.*;

public class Graphs {

	public static void main(String[] args) throws FileNotFoundException {
		Scanner in = new Scanner(System.in);

		System.out.print("Enter a graph file: ");
		File file = new File (in.nextLine()); //reads in file
		Scanner scanner = new Scanner (file);

		Matrix matrix = new Matrix(file); //creates new Matrix object

		int command = 0;

		while (command != 8) { //goes through the menu 
			System.out.print("1. Is Connected \n2. Minimum Spanning Tree \n3. Shortest Path \n4. Is Metric \n5. Make Metric \n6. Traveling Salesman Problem \n7. Approximate TSP \n8. Quit \n\nMake Your Choice (1 - 8): ");
			command = in.nextInt(); //reads in command

			if (command == 1) { //isConnected
				if (matrix.isConnected())
					System.out.println("Graph is connected.\n");
				else
					System.out.println("Graph is not connected.\n");
			}
			else if (command == 2) { //MST
				if (!matrix.isConnected())
					System.out.println("Error: Graph is not connected.\n");
				else {
					Matrix MST = matrix.minSpanningTree();
					MST.print();
				}
			}
			else if (command == 3) { //shortestPath
				System.out.print("From which node would you like to find the shortest paths (0 - " + (matrix.getSize() - 1) + "): "); 
				int startingNode = in.nextInt();
				matrix.shortestPath(startingNode, true);
			}
			else if (command == 4) { //isMetric
				matrix.isMetric();
			}
			else if (command == 5) { //makeMetric
				matrix.makeMetric();		
			}
			else if (command == 6) { //TSP
				matrix.TSP();
			}
			else if (command == 7) { //appriximate TSP
				matrix.approximateTSP();
			}
		}

	}
}