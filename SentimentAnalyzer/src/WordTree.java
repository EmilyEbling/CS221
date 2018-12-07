import java.io.*;

public class WordTree {

	private Node root;

	private static class Node {
		public String word; //key
		public double score; //value - review value
		public double count; //value - times word appears
		public boolean black;
		public Node left;
		public Node right;
	}

	public void add(String word, int score) { //adds the word to the tree at the appropriate location
		root = add(root, word, score);
		root.black = true; //the root should always be black
	}

	private Node add(Node node, String word, int score) { //helper method
		if (node == null) { //if the node is null, create a new node and add its appropriate information
			Node n = new Node();
			n.word = word;
			n.score = score;
			n.count = 1;
			n.black = false;
			return n;	
		}	

		int compare = word.compareTo(node.word); //compares the words to determine where they should be placed in the tree

		if (compare < 0) //if the word you're trying to add comes before the word you are looking at
			node.left = add(node.left, word, score); //move to the left
		else if (compare > 0) //if the word you're trying to add comes after the word you are looking at 
			node.right = add(node.right, word, score); //move to the right
		else { //otherwise the words are the same, so update the count and the score
			node.count++;
			node.score += score;
			return node;
		}	

		// these if statements check to make sure the red black tree remains balanced
		if (isRed(node.right) && !isRed(node.left)) 
			node = rotateLeft(node);
		if (isRed(node.left) && isRed(node.left.left))
			node = rotateRight(node);
		if (isRed(node.left) && isRed(node.right)) 
			flipColors(node);

		return node;
	}

	private boolean isRed(Node node) { //helper method to keep track of the color of a node
		if (node == null)
			return false;
		return node.black;			
	}

	private Node rotateLeft(Node node) { //helper method to rotate a node left
		Node n = node.right;
		node.right = n.left;
		n.left = node;
		n.black = node.black;
		node.black = false;
		return n;
	}

	private Node rotateRight(Node node) { //helper method to rotate a node right
		Node n = node.left;
		node.left = n.right;
		n.right = node;
		n.black = node.black;
		node.black = false;
		return n;
	}

	private void flipColors(Node node) { //helper method to flip colors if necessary
		node.black = false;
		node.left.black = true;
		node.right.black = true;
	}

	public boolean contains(String word) { //determines if the tree contains a word or not
		return contains(root, word);
	}

	private boolean contains(Node node, String word) { //helper method 
		if (node == null) 
			return false;
		
		int compare = node.word.compareToIgnoreCase(word);
		
		if (node.word.equals(word)) //if the words being compared are equal, return true
			return true;
		if (compare > 0) //if the word you're trying to find comes after the word you are looking at
			return contains(node.left, word); //move left
		else //otherwise move right
			return contains(node.right, word);
	}

	public double getScore(String word) { //get the score of the word in question
		return getScore(root, word);
	}

	private double getScore(Node node, String word) {  
		if (node == null) 
			return -1; 
	
		int compare = node.word.compareToIgnoreCase(word);
		
		if (node.word.equals(word)) //if the words being compared are equal, return the average score
			return node.score / node.count;
		if (compare > 0) //if the word you're trying to get the score of comes after the word you are looking at
			return getScore(node.left, word); //move left
		else //otherwise move right
			return getScore(node.right, word);
	
	}

	public void print (PrintWriter out) { //prints the information in the tree out
		print(root, out);
	}
	
	private Node print(Node node, PrintWriter out) {
		if (node == null)
			return node;
		
		//basically an inorder traversal needed to print out all the information in the tree
		print(node.left, out); //print the left information
		out.println(node.word + "\t" + node.score + "\t" + node.count); //print the node
		print(node.right, out); //print the right information
		return node;
	}

}
