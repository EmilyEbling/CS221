import java.util.NoSuchElementException;

public class ArrayStack<T> {
	@SuppressWarnings("unchecked")
	private T [] data = (T[]) new Object [10]; //generic array of T's
	private int size = 0;
	
	@SuppressWarnings("unchecked")
	public void push(T value) { //adds value to the top of the stack
		if (size == data.length) { //resizes the array
			T[] temp = (T[]) new Object[size*2];
			for (int i = 0; i < size; i++)
				temp[i] = data[i];
			data = temp;
		}
		data[size] = value;
		size++;
	}
	
	public T pop() { //removes value from stack
		if (size == 0)
			throw new NoSuchElementException();
		size--;
		return data[size];
		
	}
	
	public T peek() { //returns the top value in the stack
		if (size == 0)
			throw new NoSuchElementException();
		return data[size - 1];
	}
	
	public int size() { //keeps track of the stack's size
		return size; 
	}
	
	public String toString() { //converts the values in the stack to strings
		String answer ="";

		for (int i = 0; i < size; i++)
			answer += " " + data[i] + " ";
		
		return answer;
	}
}
