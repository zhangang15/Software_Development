import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.List;

/**
 * The class FileReader is used for read word from text file and pass it to
 * InvertedIndex, then InvertedIndex calls the method to store it by asking way.
 * 
 * @author ANG ZHANG
 * 
 */
public class FileReader {

	/**
	 * This InvertedIndex type dataStore is for store word when get word from
	 * text file.
	 */
	private InvertedIndex dataStore;
	/**
	 * This List fileList is used for storing the list of files obtained from
	 * the DirectoryParser.
	 */
	private List<File> fileList;

	/**
	 * Group of threads to implement multi-threading.
	 */
	private WorkQueue fileWorkQueue;

	/**
	 * The parameter pending is for counting how many threads are working right
	 * now.
	 */
	private int pending;

	/**
	 * Constructor
	 * 
	 * @param i
	 *            is reference of InvertedIndex
	 * @param fileList
	 *            is a list consist of files
	 * @param wQueue
	 *            is group of threads
	 */
	public FileReader(InvertedIndex i, List<File> fileList, WorkQueue wQueue) {
		this.dataStore = i;
		this.fileList = fileList;
		this.fileWorkQueue = wQueue;
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
	 * This method readFile is using for parsing one text file line by line. By
	 * implementing multi-threading, we create new class FileWorker and pass it
	 * to workQueue's execute method.
	 * 
	 * @param filepath
	 *            is the path from the fileList.
	 * 
	 */
	public void readFile(String filepath) {

		fileWorkQueue.execute(new FileWorker(filepath));

	}

	/**
	 * This inner runnable class FileWorker is using for running our worker
	 * threads. In this class, we put former readFile method into this run
	 * method to implement multi-threading.
	 * 
	 * @author ANG ZHANG
	 * 
	 */
	private class FileWorker implements Runnable {

		/**
		 * This filePath is the path from file list.
		 */
		private String filePath;

		/**
		 * This is constructor of FileWorker. After we initializing class
		 * FileWorker, we update pending by one.
		 * 
		 * @param filePath
		 *            is the path from file list.
		 */
		public FileWorker(String filePath) {

			this.filePath = filePath;
			// update the current number of threads
			updatePending(1);
		}

		// put former readFile method in run method

		@Override
		public void run() {
			// create a local index for one thread to store data.
			InvertedIndex localIndex = new InvertedIndex();

			File sourceFile = new File(filePath);

			if (!sourceFile.isFile() || !sourceFile.canRead()) {
				try {
					throw new IOException("Unable to open file");
				} catch (IOException e) {
					System.err.println("Error: " + e.getMessage());
				}

			}
			FileInputStream fir = null;
			try {
				fir = new FileInputStream(sourceFile.getAbsolutePath());
			} catch (FileNotFoundException e) {
				System.err.println("Error: " + e.getMessage());
			}
			BufferedReader reader = null;
			try {
				reader = new BufferedReader(new InputStreamReader(fir, "utf-8"));
				String sourceLine = "";

				int position = 0;
				while ((sourceLine = reader.readLine()) != null) {
					String[] wordArray = sourceLine.split(" ");

					for (int i = 0; i < wordArray.length; i++) {

						if (getWordWithoutSymbols(wordArray[i]).isEmpty()) {
							continue;
						} else {

							// call indexData method to store the word into the
							// local index data structure.
							localIndex.indexData(sourceFile,
									getWordWithoutSymbols(wordArray[i]),
									position);

							position++;
						}

					}

				}
				// after dealing with one text file, put one complete local
				// index to globe index by addAllIndex method.
				dataStore.addAllIndex(localIndex, filePath);
				// after running this thread, decrease the number of current
				// threads.
				updatePending(-1);
			} catch (IOException exception) {
				System.err.println("Error: " + exception.getMessage());

			} finally {
				try {
					fir.close();
					reader.close();
				} catch (Exception ignored) {
					// do nothing
				}
			}

		}

	}

	/**
	 * This method readFile is used to read all files from the fileList.
	 */
	public void readFiles() {

		for (File file : this.fileList) {
			this.readFile(file.getAbsolutePath());
		}
		// We need to wait all of threads have finished the job.
		while (getPending() > 0) {

			synchronized (this) {
				try {

					wait();
				} catch (InterruptedException ex) {
					System.out.println(ex.getMessage());
				}
			}
		}
	}

	/**
	 * This method is used for get word without symbols.
	 * 
	 * @param originalWord
	 * @return word without symbols.
	 */
	public static String getWordWithoutSymbols(String originalWord) {
		return originalWord.toLowerCase().replaceAll("\\_", "")
				.replaceAll("\\W", "");
	}

}
