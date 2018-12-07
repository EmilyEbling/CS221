
public class Operand implements Term { 
	double value;
	
	public Operand(double value) {
		this.value = value;
	}
	
	public boolean isOperand() {
		return true;
	}
	
	public String toString() {
		return "" + value;
	}
	
	public double getValue() {
		return value;
	}
}
