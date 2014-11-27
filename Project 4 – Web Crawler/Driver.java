import java.io.File;

/**
 * 
 * Driver class is for initialize all the classes and class the related methods
 * to get the final results.
 * 
 * @author ANG ZHANG
 * 
 */
public class Driver {

	/**
	 * In the main method, initialize the ArgumentParser, InvertedIndex,
	 * QueryHandler, WorkQueue and FileReader class. Call the methods to output
	 * the result.
	 * 
	 * @param args
	 *            from command-line.
	 * 
	 * 
	 */
	public static void main(String[] args) {

		ArgumentParser parser = new ArgumentParser(args);
		String dir = null;
		String seed = null;
		String queryFilePath = null;
		int thread = 0;
		// to check the command-line parameters.
		try {
			if (parser.hasFlag("-u")) {
		        seed = parser.getValue("-u");
		    }
			if (parser.hasFlag("-d")) {
				dir = parser.getValue("-d");
			}
			if (parser.hasFlag("-q")) {
				queryFilePath = parser.getValue("-q");
			}
			if (parser.hasFlag("-t")) {
				thread = Integer.parseInt(parser.getValue("-t"));
			}

			// initialize the classes.

			WorkQueue workQueue = new WorkQueue(thread);
			InvertedIndex i = new InvertedIndex();
			if (seed != null) {
			    HTMLCrawler crawler = new HTMLCrawler(seed, i, workQueue);
			    crawler.parseWords();
			} else {
			FileReader fr = new FileReader(i,
					DirectoryParser.getFileList(new File(dir)), workQueue);
			fr.readFiles();
			}
			QueryFileHandler q = new QueryFileHandler(i, workQueue);
			// output the results.
			
			q.readQueryFile(queryFilePath);
			q.printSearchResult();
			if (parser.hasFlag("-i")) {
				i.printIndex();
			}
			workQueue.shutdown();
		} catch (Exception ex) {
			System.err.println("Error: " + ex.getMessage());

		}

	}
}
