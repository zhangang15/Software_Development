import java.io.File;
import java.io.IOException;

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
	 * In the main method, initialize the ArgumentParser, InvertedIndex and
	 * FileReader class. Call the method to output the result.
	 * 
	 * @param args
	 *            from command-line.
	 * @throws IOException
	 * 
	 */
	public static void main(String[] args) {

		ArgumentParser parser = new ArgumentParser(args);
		String dir = null;
		String queryFilePath = null;
		// to check the command-line parameters.
		try {
			if (parser.hasFlag("-d")) {
				dir = parser.getValue("-d");
			}
			if (parser.hasFlag("-q")) {
				queryFilePath = parser.getValue("-q");
			}
			// initialize the classes.
			InvertedIndex i = new InvertedIndex();
			FileReader fr = new FileReader(i,
					DirectoryParser.getFileList(new File(dir)));
			QueryFileHandler q = new QueryFileHandler(i);
			// output the results.
			fr.readFiles();
			q.readQueryFile(queryFilePath);
			q.printSearchResult();
			if (parser.hasFlag("-i")) {
				i.printIndex();
			}

		} catch (Exception ex) {
			System.err.println("Error: " + ex.getMessage());

		}

	}
}
