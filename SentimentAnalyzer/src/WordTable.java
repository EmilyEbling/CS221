import java.io.*;

public class WordTable { 

	WordTree[] wordTree = new WordTree[26]; //creates an array holding wordTrees with each index being a letter from a-z

	public WordTable() { //initiates each location of the wordTable to a new wordTree
		for (int i = 0; i < 26; i++)
			wordTree[i] = new WordTree();
	}

	public int hash (String word) { //hash location based off of the beginning letter of the word you're trying to add to the tree
		return word.charAt(0) - 'a'; 
	}
	
	public void add (String word, int score) { //adds the word to the hash table (WordTable)
		wordTree[hash(word)].add(word, score);
	}
	
	public double getScore (String word) { //gets the score from the word
		return (double) wordTree[hash(word)].getScore(word);
	}
	
	public boolean contains (String word) { //determines if the tree already contains the word
		return wordTree[hash(word)].contains(word);
	}
	
	public void print (PrintWriter out) throws FileNotFoundException { //prints out all the information in the wordTrees
		for (int i = 0; i < 26; i++) 
			wordTree[i].print(out);	
	}
}
