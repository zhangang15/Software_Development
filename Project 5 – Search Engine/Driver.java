import java.io.File;

import org.omg.CORBA.PUBLIC_MEMBER;

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
	public static InvertedIndex i;
	public static int port;
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
			if (parser.hasFlag("-p")) {
				 port = Integer.parseInt(parser.getValue("-p"));
			}
			
			
			if (parser.hasFlag("-t")) {
				thread = Integer.parseInt(parser.getValue("-t"));
			}

			// initialize the classes.

			WorkQueue workQueue = new WorkQueue(thread);
			 i = new InvertedIndex();
			if (seed != null) {
			    HTMLCrawler crawler = new HTMLCrawler(seed, i, workQueue);
			    crawler.parseWords();
			} else {
			FileReader fr = new FileReader(i,
					DirectoryParser.getFileList(new File(dir)), workQueue);
			fr.readFiles();
			}

			new LoginServer();
			if (parser.hasFlag("-i")) {
				i.printIndex();
			}
			workQueue.shutdown();
		} catch (Exception ex) {
			System.err.println("Error: " + ex.getMessage());

		}

	}
}
