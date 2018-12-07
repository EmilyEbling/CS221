
public class InvalidOperatorException extends Exception {

	private char operator;
	
	public InvalidOperatorException(char c) { //needed to print what the invalid operator is
		operator = c;
	}
	
	public char getOperator() {
		return operator;
	}
	
}
