import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;

/**
 * This class named QueryFileHandler is used for dealing with all query files
 * and pass the keywords to InvertedIndex, after get the search results, put
 * keywords and search results into resultMap.
 * 
 * @author ANG ZHANG
 * 
 */
public class QueryFileHandler {

	/**
	 * This HashMap named resultMap is used for storing keyword from the query
	 * file and search them in the above TreeMap. The outer key is keyword and
	 * the value is SearchResult List. The SearchResult has three elements, they
	 * are file path, frequency of the keyword and the first position in the
	 * text file.
	 */
	private HashMap<String, ArrayList<SearchResult>> resultMap;

	/**
	 * This InvertedIndex type resultStore is for storing word when get word
	 * from text file.
	 */

	private InvertedIndex resultStore;

	/**
	 * This wordList is for record the order of keyword passed to every thread.
	 */
	private ArrayList<String> wordList;

	/**
	 * This is group of threads to implement multi-threading.
	 */
	private WorkQueue queryWorkQueue;
	/**
	 * This MultiReadersLock searchResultLock is for locking the shared objects
	 * may accessed by different workers.
	 */
	private MultiReadersLock searchResultLock;

	/**
	 * The parameter pending is for counting how many threads are working right
	 * now.
	 */
	private int pending;

	/**
	 * The constructor of QueryFileHandler.
	 * 
	 * @param resultStore
	 *            When reading one line from query file, then passing it to
	 *            serultStore's searchResult method and return a list of
	 *            SearchResult about one line.
	 * @param wQueue
	 *            is group of threads.
	 */
	public QueryFileHandler(InvertedIndex resultStore, WorkQueue wQueue) {

		resultMap = new HashMap<String, ArrayList<SearchResult>>();
		this.resultStore = resultStore;
		this.queryWorkQueue = wQueue;
		wordList = new ArrayList<String>();
		searchResultLock = new MultiReadersLock();
		pending = 0;
	}

	/**
	 * This shared method getPending is used for obtaining the current number of
	 * running threads.
	 * 
	 * @return the number of threads which are running.
	 */
	private synchronized int getPending() {
		return pending;
	}

	/**
	 * This shared method is using for update the number of threads that are
	 * processing.
	 * 
	 * @param amount
	 *            which stands for the number of threads we want to run.
	 */
	private synchronized void updatePending(int amount) {
		pending += amount;

		if (pending <= 0) {
			notifyAll();
		}

	}

	/**
	 * This method readQueryFile is used for read the searching keywords from
	 * the query file and pass the keywords to resultStore. After obtaining the
	 * results, put the keywords and results into resultMap.
	 * 
	 * @param filePath
	 *            the path from the command-line.
	 * @throws IOException
	 */
	public void readQueryFile(String filePath) throws IOException {

		File file = new File(filePath);

		if (!file.isFile() || !file.canRead()) {
			throw new IOException("Unable to open file.");
		}
		FileInputStream fir = new FileInputStream(file.getAbsolutePath());
		BufferedReader reader = null;
		try {
			reader = new BufferedReader(new InputStreamReader(fir, "utf-8"));

			String sourceLine = "";

			while ((sourceLine = reader.readLine()) != null) {

				// put every source line into wordList to record the input
				// order.
				wordList.add(sourceLine);

				// call worker thread to search every keyword line.
				queryWorkQueue.execute(new QueryWorker(sourceLine));

				// We need to wait all of threads have finished the job.
				while (getPending() > 0) {

					synchronized (this) {
						try {

							wait();
						} catch (InterruptedException ex) {

						}
					}
				}

			}

		} catch (IOException exception) {

			throw new IOException("Unable to search the word.", exception);
		} finally {
			try {
				fir.close();
				reader.close();
			} catch (Exception ignored) {
				// do nothing
			}
		}
	}

	/**
	 * This inner runnable class QueryWorker is using for running our worker
	 * threads. In this class, we put former search result in index method into
	 * this run method to implement multi-threading searching.
	 * 
	 * @author ANG ZHANG
	 * 
	 */
	private class QueryWorker implements Runnable {

		/**
		 * One line keyword from query text file.
		 */
		private String line;

		/**
		 * Constructor of class QueryWorker.
		 * 
		 * @param line
		 *            keyword from query text file.
		 */
		public QueryWorker(String line) {

			this.line = line;
			// after running a thread, update the pending number by one.
			updatePending(1);
		}

		// put former searching result method in run method.
		@Override
		public void run() {

			line = line.trim();

			String[] searchArray = line.split("\\s+");

			for (int i = 0; i < searchArray.length; i++) {
				searchArray[i] = searchArray[i].replaceAll(
						"[^\\s && \\W ||  _  ]", "").toLowerCase();

			}

			// call InvertedIndex type resultStore's method searchResult to
			// get one line's result then return them as
			// a list.
			ArrayList<SearchResult> resultList = resultStore
					.searchResult(searchArray);

			// Because here may have more than one thread will write data into
			// resultMap, we need to lock the shared object.
			searchResultLock.acquireWriteLock();
			// put sourceLine which is keywords and resultList into
			// resultMap.
			resultMap.put(line, resultList);
			// After writing the data, releasing the lock.
			searchResultLock.releaseWriteLock();

			// after all the processing, decrease the number of thread
			updatePending(-1);
		}

	}

	/**
	 * Write resultMap based on the requirement of project to the text file
	 * named "searchresults.txt"
	 * 
	 * @throws IOException
	 */
	public void printSearchResult() throws IOException {

		// before print search result, use lock to protect shared object.
		searchResultLock.acquireReadLock();

		// Create file
		FileWriter fstream = new FileWriter("searchresults.txt");
		BufferedWriter out = new BufferedWriter(fstream);
		try {
			for (String word : wordList) {

				out.write(word + "\n");

				ArrayList<SearchResult> list = resultMap.get(word);

				for (Iterator<SearchResult> i = list.iterator(); i.hasNext();) {
					SearchResult sr = i.next();

					if (sr != null) {
						out.write(sr.toString());
					}

				}

				out.write("\n");
				// close the output stream
			}

			// }

		} catch (Exception e) {// Catch exception if any
			System.err.println("Error: " + e.getMessage());
		} finally {
			try {
				out.close();
			} catch (Exception ignored) {
				// do nothing
			}
		}
		// after printing process, release the lock.
		searchResultLock.releaseReadLock();
	}
}
