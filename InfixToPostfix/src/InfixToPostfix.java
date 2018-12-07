/*
 * Emily Ebling
 * Data Structures
 * */
import java.util.*;

public class InfixToPostfix {

	public static void main(String[] args) throws InvalidOperandException, InvalidOperatorException, MissingOperandException, UnbalancedRightParenthesisException, UnbalancedLeftParenthesisException  {
		try {
			ArrayQueue <Term> queue = input();
			System.out.print("Standardized infix: ");
			print(queue);
			ArrayQueue <Term> postfix = convert(queue);
			System.out.print("Postfix expression: ");
			print(postfix);
			double answer = evaluate(postfix);
			System.out.println("Answer: " + answer);
		}
		catch (InvalidOperandException e) { //catches invalid operand exception
			System.out.println("Invalid Operand");
		}
		catch (InvalidOperatorException e) { //catches invalid operator exception
			System.out.println("Invalid Operator: " + e.getOperator());
		}
		catch (MissingOperandException e) { //catches missing operand exception
			System.out.println("Missing Operand");
		}
		catch (UnbalancedRightParenthesisException e) { //catches unbalanced right parenthesis exception
			System.out.println("Unbalanced Right Parenthesis ')'");
		}
		catch (UnbalancedLeftParenthesisException e) { //catches unbalanced left parenthesis exception
			System.out.println("Unbalanced Left Parenthesis '('");
		}
	}

	private static ArrayQueue <Term> input() throws InvalidOperandException, InvalidOperatorException, MissingOperandException {
		/* This method will take in all the input and add it the the queue which can then be
		 * converted to its postfix form.
		 */
		try {
			ArrayQueue<Term> inputQueue = new ArrayQueue <Term>();

			Scanner in = new Scanner (System.in);

			System.out.print("Enter infix expression: ");
			String equation = in.nextLine();

			boolean operand = true; //keeps track if the current character is an operand or operator
			int i = 0;
			String current = ""; 

			while (i < equation.length()) { //run for the length of the equation
				char c = equation.charAt(i);
				if (operand) { //the the initial character is an operand
					if (current.isEmpty()) {
						if(c == ' ') //ignores whitespace
							i++; 
						else if(Character.isDigit(c) || c == '.' || c == '+' || c == '-') { //adds the operand or legal symbols to the string
							current += c;
							i++;
						}
						else if (c == '(') { //adds parenthesis to queue
							inputQueue.enqueue(new Operator('('));
							i++;
						}
						else //throws exception if the first character isn't a legal operand
							throw new InvalidOperandException();	
					}	
					else { //the string isn't empty and therefore the number must be multiple places long or you've hit an operator
						if (Character.isDigit(c) || c == '.') { //if the number is multiple places long or a decimal place then add that to the string
							current += c;
							i++;
						}	
						else { //change operand to false and convert the string into a double value which is then added to the queue
							operand = false;	
							double value = Double.parseDouble(current);
							inputQueue.enqueue(new Operand(value));
							current = "";
						}
					}	
				}
				else { //is an operator 
					if (c == ' ') //ignores whitespace 
						i++;
					else if (c == '+' || c == '-' || c == '*' || c == '/') { //if legal operator it will be added to the queue and operand will be set to true again
						inputQueue.enqueue(new Operator (c));
						i++;
						operand = true;
					}
					else if (c == ')') { //adds right parenthesis to queue but does not change operand to true because another operator would be expected afterwards
						inputQueue.enqueue(new Operator (c));
						i++;
					}	
					else 
						throw new InvalidOperatorException(c); //throws exception if operator is invalid
				}

			}
			if (!current.isEmpty()) //if there is a number still in current add that number remaining to the queue
				inputQueue.enqueue(new Operand (Double.parseDouble(current))); 
			else if (operand) //otherwise there must be a missing number so throw an exception
				throw new MissingOperandException();

			return inputQueue;

		}
		catch (NumberFormatException e) { //catches if a double has more than one decimal point 
			throw new InvalidOperandException(); 
		}


	}

	private static void print(ArrayQueue<Term> d) {
		//This method uses the toString method from the ArrayQueue class to transform the queue inputed to one big string

		System.out.println(" " + d.toString() + " ");		
	}

	private static ArrayQueue<Term> convert(ArrayQueue<Term> d) throws UnbalancedRightParenthesisException, UnbalancedLeftParenthesisException {
		ArrayQueue <Term> outputQueue = new ArrayQueue <Term>();
		ArrayStack <Operator> operatorStack = new ArrayStack <Operator>();

		while (d.size() != 0) { //while there are still elements left in the queue holding the input information
			if (d.front().isOperand()) { //if the front value is a number, add it to the outputQueue
				outputQueue.enqueue(d.dequeue());
			}
			else { //is operator
				Operator operator = (Operator)d.dequeue();

				//there are operators in the stack and therefore precedence needs to be taken into consideration
				if (operator.getOperator() == '(')  //add the left parenthesis to the operatorStack
					operatorStack.push(operator);

				else if (operator.getOperator() == ')') { //if there is a right parenthesis 
					while (operatorStack.size() != 0 && operatorStack.peek().getOperator() != '(') { //while the operatorStack still has elements in it and you have not hit a left parenthesis
						outputQueue.enqueue(operatorStack.pop()); //add the elements from the operatorStack to the outputQueue
					}
					if (operatorStack.size() == 0) //if everything is popped off the stack that means a left parenthesis was never reached so throw an exception
						throw new UnbalancedRightParenthesisException();
					else 
						operatorStack.pop(); //otherwise pop the left parenthesis off the stack

				}
				else { //as long as the stack isn't empty compare the precedence between the current operator and the one already on the stack
					while (operatorStack.size() != 0 && getPrecedence(operator.getOperator()) <= getPrecedence(operatorStack.peek().getOperator())) 
						outputQueue.enqueue(operatorStack.pop());
					operatorStack.push(operator);
				}
			}
		}
		while (operatorStack.size() != 0) { //if there are still elements on the operatorStack add them all to the outputQueue
			if (operatorStack.peek().getOperator() == '(')
				throw new UnbalancedLeftParenthesisException();	
			else
				outputQueue.enqueue(operatorStack.pop());
		}
		return outputQueue;
	}

	private static double getPrecedence(char c) {
		//gives each character a precedence
		if (c == '(')
			return 0.0;
		else if (c == '+' || c == '-')
			return 1.0;
		else 
			return 2.0;
	}

	private static double evaluate(ArrayQueue<Term> d) {
		ArrayStack <Operand> operandStack = new ArrayStack <Operand>();
		double answer;

		while (d.size() != 0) {
			if (d.front().isOperand()) { //if the first thing in queue is an operand add it to the operandStack
				Operand operand = (Operand) d.dequeue();
				operandStack.push(operand);
			}
			else { //is an operator
				double operandTwo = operandStack.pop().getValue(); //pop off the first two operands and assign them to a double variable
				double operandOne = operandStack.pop().getValue();
				if (d.front().toString().equals("/")) { //if the operator is / divide the two operands
					answer = operandOne / operandTwo;
					operandStack.push(new Operand(answer));
				}
				else if (d.front().toString().equals("*")) { //if the operator is * multiply the two operands
					answer = operandOne * operandTwo;
					operandStack.push(new Operand(answer)); 
				}
				else if (d.front().toString().equals("+")) { //if the operator is + add the two operators
					answer = operandOne + operandTwo;
					operandStack.push(new Operand(answer));
				}
				else if (d.front().toString().equals("-")) { //if the operator is - subtract the two operators 
					answer = operandOne - operandTwo;
					operandStack.push(new Operand(answer));
				}
				d.dequeue(); //remove the operator from the queue
			}
		}	
		return operandStack.peek().getValue(); //there should be one number remaining on the operandStack to be returned
	}

}
