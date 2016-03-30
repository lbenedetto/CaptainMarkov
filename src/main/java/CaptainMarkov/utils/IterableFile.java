package CaptainMarkov.utils;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.Iterator;

public class IterableFile implements Iterable<String> {
	private BufferedReader file;

	public IterableFile(String fileName) throws FileNotFoundException {
		file = new BufferedReader(new FileReader(fileName));
	}

	/**
	 * Returns an iterator over elements of type {@code T}.
	 *
	 * @return an Iterator.
	 */
	@Override
	public FileIterator iterator() {
		FileIterator fi = new FileIterator();
		fi.next();
		return fi;
	}

	private class FileIterator implements Iterator<String> {
		String nextLine;
		String curr;


		@Override
		public boolean hasNext() {
			return nextLine != null;
		}

		/**
		 * Returns the next element in the iteration.
		 *
		 * @return the next element in the iteration
		 */
		@Override
		public String next() {
			curr = nextLine;
			try {
				nextLine = file.readLine();
				while (nextLine.equals("#"))
					nextLine = file.readLine();
			} catch (IOException | NullPointerException e) {
				nextLine = null;
			}
			return curr;
		}
	}
}
