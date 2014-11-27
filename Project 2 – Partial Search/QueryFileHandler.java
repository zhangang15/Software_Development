import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map.Entry;

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
	 * This LinkedHashMap named resultMap is used for storing keyword from the
	 * query file and search them in the above TreeMap. The outer key is keyword
	 * and the value is SearchResult List. The SearchResult has three elements,
	 * they are file path, frequency of the keyword and the first position in
	 * the text file.
	 */
	private LinkedHashMap<String, ArrayList<SearchResult>> resultMap;

	/**
	 * This InvertedIndex type resultStore is for store word when get word from
	 * text file.
	 */

	private InvertedIndex resultStore;

	/**
	 * The constructor of QueryFileHandler.
	 * 
	 * @param resultStore
	 *            When reading one line from query file, then passing it to
	 *            serultStore's searchResult method and return a list of
	 *            SearchResult about one line.
	 */
	public QueryFileHandler(InvertedIndex resultStore) {

		resultMap = new LinkedHashMap<String, ArrayList<SearchResult>>();
		this.resultStore = resultStore;
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

				sourceLine = sourceLine.trim();

				String[] searchArray = sourceLine.split("\\s+");
				
				for(int i = 0; i < searchArray.length; i++ ) {
					searchArray[i] = searchArray[i].replaceAll("[^\\s && \\W ||  _  ]", "").toLowerCase();
					
				}

				// call InvertedIndex type resultStore's method searchResult to
				// get one line's result then return them as
				// a list.
				ArrayList<SearchResult> resultList = resultStore
						.searchResult(searchArray);

				// put sourceLine which is keywords and resultList into
				// resultMap.
				resultMap.put(sourceLine, resultList);

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
	 * Write resultMap based on the requirement of project to the text file
	 * named "searchresults.txt"
	 * 
	 * @throws IOException
	 */
	public void printSearchResult() throws IOException {
		// Create file
		FileWriter fstream = new FileWriter("searchresults.txt");
		BufferedWriter out = new BufferedWriter(fstream);
		try {

			for (Entry<String, ArrayList<SearchResult>> entry : resultMap
					.entrySet()) {
				out.write(entry.getKey() + "\n");

				ArrayList<SearchResult> list = entry.getValue();

				for (Iterator<SearchResult> i = list.iterator(); i.hasNext();) {
					SearchResult sr = i.next();

					if (sr != null) {
						out.write(sr.toString());
					}

				}

				out.write("\n");
				// close the output stream
			}

		} catch (Exception e) {// Catch exception if any
			System.err.println("Error: " + e.getMessage());
		} finally {
			try {
				out.close();
			} catch (Exception ignored) {
				// do nothing
			}
		}

	}
}
