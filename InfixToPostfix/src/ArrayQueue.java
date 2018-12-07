import java.util.NoSuchElementException;

public class ArrayQueue<T> {
	@SuppressWarnings("unchecked")
	private T [] data = (T[]) new Object [10]; //generic array of T's
	private int start = 0;
	private int size = 0;
	
	@SuppressWarnings("unchecked")
	public void enqueue(T value) { //adds something to the front of the queue
		if (data.length == size) { //resizes array
			T [] newData = (T[]) new Object [size*2];
			for (int i = 0; i < size; i++) 
				newData[i] = data[(start + i) % data.length];
			data = (T[]) newData;
			start = 0;
		}
		data[(start + size) % data.length] = (T) value;
		size++;				
	}
	
	public T dequeue() { //removes the front item in the queue
		if (size == 0)
			throw new NoSuchElementException();
		T value = data[start];
		size--;
		start = (start + 1) % data.length;
		return value;
	}
	
	public T front() { //returns the value of the item in the front of the queue
		if (size == 0)
			throw new NoSuchElementException();
		return data[start];
	}
	
	public int size() { //keeps track of the queue's size
		return size;
	}
	
	public String toString() { //changes the elements in the queue to strings
		String answer = "";

		for (int i = 0; i < size; i++)
			answer += " " + data[i] + " ";
		
		return answer;
	}
	
}
