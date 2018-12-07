
public class Operator implements Term {
	char operator;
	
	public Operator(char c) {
		operator = c;
	}
	
	public char getOperator() {
		return operator;
	}

	@Override
	public boolean isOperand() {
		return false;
	}
	
	public String toString() {
		return "" + operator;
	}
	
	
}
