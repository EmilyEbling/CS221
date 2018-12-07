/* Emily Ebling and Dillon Dotson
 * Project 3
 * CS221
 * November 14, 2016
 */

import java.util.*;
import java.io.*;

public class SentimentAnalyzer {
	
	public static void main(String[] args) throws IOException {
		
		WordTable stopTable = new WordTable(); //creates a WordTable to hold stop words
		WordTable reviewTable = new WordTable(); //creates a WordTable to hold words parsed from reviews document
		
		Scanner in = new Scanner(System.in);

		try {
			System.out.print("Enter a stop word file: ");
			File stopFile = new File(in.nextLine()); //creates a new file based off of the inputed text
			Scanner stop = new Scanner(stopFile); //runs through the stop file

			while (stop.hasNext()) { //while there are stop words in the document 
				stopTable.add(stop.next(), 0); //add them to the stopTable w/ a rating of 0
			}	

			System.out.print("Enter a review file: "); //creates a new file based off of the inputed text
			File reviewFile = new File(in.nextLine()); //runs through the review file
			ReviewParse(reviewFile, stopTable, reviewTable); //parse the words from the reviewFile

			File reviews = new File("reviews.txt.words"); //writes the parsed words in alphabetical order to a new file
			PrintWriter out = new PrintWriter(reviews); //prints the words out to the new file
			reviewTable.print(out);
			System.out.println("Word statistics written to file reviews.txt.words"); //informs user where the new words are being stored
			System.out.println("");

			String response; //keeps track of user's response
			String phraseResponse; //keeps track of the phrase the user inputs
			boolean yes = true; //continue asking for a phrase while the answer is yes

			while (yes) {
				System.out.print("Would you like to analyze a phrase? (yes/no) ");
				response = in.nextLine();
				if (!response.equals("yes")) {
					yes = false; //if the response isn't yes then exit the while loop, thus ending the program
				}		
				else { //otherwise ask for a phrase and then parse that phrase to give it a score
					System.out.print("Enter a phrase: ");
					phraseResponse = in.nextLine().toLowerCase();
					PhraseParse(phraseResponse, stopTable, reviewTable);
				}
			}
		} 
		
		catch (FileNotFoundException e) { //catch if the file is not valid/cannot be found
			e.printStackTrace();
		}

	}

	private static WordTable ReviewParse(File file, WordTable stopTable, WordTable reviewTable) throws IOException {
		//this method will parse all the words in the review file
		
		Scanner scanner = null;

		try {
			scanner = new Scanner(file);
		}
		
		catch (FileNotFoundException e) { //catches if the file is not found
			e.printStackTrace();
		}

		int i = 1; //keeps track of the current index, starts at one because location 0 of each line will always be a number
		String current = ""; //will hold the individual words
		int reviewScore = 0;

		while (scanner.hasNextLine()) { //while there are still lines left in the document 
			i = 1; 
			String line = scanner.nextLine().toLowerCase(); //scan in one line at a time, converting everything to lowercase
			reviewScore = Integer.parseInt("" + line.charAt(0)); //number to keep track of review score
			while (i < line.length()) { //while there is still stuff in the line you are on
				char c = (char)(line.charAt(i)); //keeps track of the current character you're on

				if (current.isEmpty()) { //if current is empty
					if (c == ' ') //ignores whitespace
						i++;
					else if (Character.isLetter(c)) { //if it's a letter, add it to the string
						current += c;
						i++;
					}
					else //otherwise skip past it
						i++;
				}
				else { //if current isn't empty
					if (c == ' ') { //if there is a space that means the word is over
						if (stopTable.contains(current)) { //if the word can be found in the stop table, don't add it and move on
							current = "";
							i++;
						}
						else { //otherwise add it to the reviewTable with the score from the beginning of the line, and move on to the next word
							reviewTable.add(current, reviewScore);
							current = "";
							i++;
						}
					}
					else if (Character.isLetter(c)) { //if letter, add to word
						current += c;
						i++;
					}
					else if (c == '\'' || c == '-') { //if apostrophe or hyphen 
						if (Character.isLetter(line.charAt(i-1))) { //if the character before the symbol is a letter then add current character to the string
							current += c;
							i++;
						}
						else { //otherwise it's not a word and move on
							current = "";
							i++;
						}
					}
					else  //some other character 
						i++;

				}
			}
		}

		return reviewTable; 

	} 

	private static String PhraseParse(String phrase, WordTable stopTable, WordTable reviewTable) {
		//this method parses the user's inputed phrase and gives it a score
		
		String current = ""; //keeps track of the current word
		int i = 0; //keeps track of the index
		double phraseScore = 0; //keeps track of the running score for the entire phrase, ignoring stop words
		int wordCount = 0; //keeps track of the number of words in the phrase, ignoring stop words


		while (i < phrase.length()) { //while there is still stuff in the string?
			char c = phrase.toLowerCase().charAt(i); //converts the line to lowercase and keeps track of the character you're on

			if (current.isEmpty()) { //if current is empty
				if (Character.isLetter(c)) { //if the character you're on is a letter, add it to current
					current += c;
					i++;
				}
				else //otherwise move past it
					i++;
			}
			else { //if current isn't empty
				if (c == ' ') { //if there is a space that means the word is over
					if (stopTable.contains(current)) { //if current is a stop word, move onto the next word
						i++;	
						current = "";
					}	
					else if (!reviewTable.contains(current)) { //not a stop word or in reviewTable give it a neutral score
						phraseScore += 2.0;
						wordCount++; //increment wordCount
						i++; //advance index
						current = "";
					}
					else if (reviewTable.contains(current)) { //if current is in the reviewTable
						phraseScore += reviewTable.getScore(current); //get its score from the reviewTable
						wordCount++; //increment wordCount
						i++;
						current = "";
					}	
				}
				else if (Character.isLetter(c)) { //if letter, add to word
					current += c;
					i++;
				}
				else if (c == '\'' || c == '-') { //if apostrophe or hyphen 
					if (Character.isLetter(phrase.charAt(i-1))) { //if the character before the symbol is a letter then add current character to the string
						current += c;
						i++;
					}
					else { //otherwise it's not a word and move on
						i++;
					}
				}
				else //some other character 
					i++;
			}
		}

		if (!current.isEmpty()) { //this takes care of one word phrases, or the last word in a phrase (if current isn't empty after breaking out of the loop)
			if (!stopTable.contains(current) && !reviewTable.contains(current)) { //not a stop word or in reviewTable give it a neutral score
				phraseScore += 2.0;
				wordCount++;
			}
			else if (reviewTable.contains(current)) {  //in the reviewTable
				phraseScore += reviewTable.getScore(current); //give it the score of the word in the table
				wordCount++;
			}
		}

		phraseScore /= wordCount; //gets the average score for the phrase
		String sentiment = "";

		if (phraseScore > 2.0) { //if the score is greater than 2
			System.out.format("Score: " + "%.3f", phraseScore);
			sentiment = "Your phrase was positive";
			System.out.println("\n" + sentiment);
		}
		else if (phraseScore < 2.0) { //if the score is less than 2
			System.out.format("Score: " + "%.3f", phraseScore);
			sentiment = "Your phrase was negative";
			System.out.println("\n" + sentiment);
		}	
		else if (phraseScore == 2) { //if the score is neutral
			System.out.format("Score: " + "%.3f", phraseScore);
			sentiment = "Your phrase was perfectly neutral";
			System.out.println("\n" + sentiment);
		}
		else { //if the phrase consisted of all stop words
			sentiment = "Your phrase contained no words that affect sentiment.";
			System.out.println(sentiment);
		}	

		return sentiment;
	}

}




