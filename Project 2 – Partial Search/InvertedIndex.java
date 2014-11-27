import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;
import java.util.TreeMap;

/**
 * This class is data structure for storing data obtained from text files.
 * 
 * @author ANG ZHANG
 * 
 */
public class InvertedIndex {

	/**
	 * This TreeMap named indexTreeMap is used for storing all single words
	 * existing in the text files, the outer key is the word, the value is the
	 * nested map storing file path and the position in related text file.
	 */
	private TreeMap<String, TreeMap<String, LinkedList<Integer>>> indexTreeMap;

	/**
	 * This is the constructor of InvertedIndex.
	 */
	public InvertedIndex() {

		indexTreeMap = new TreeMap<String, TreeMap<String, LinkedList<Integer>>>();

	}

	/**
	 * @param f
	 *            The file type parameter f is for getting the file path of the
	 *            word.
	 * @param word
	 *            The String word is the single word obtained from the text
	 *            file.
	 * @param position
	 *            The integer type parameter position is for recording the place
	 *            in the every text file. This method is used for storing the
	 *            word based on the word from related text file path, and all
	 *            the places in the every text file.
	 */
	public void indexData(File f, String word, int position) {

		// pathMap
		TreeMap<String, LinkedList<Integer>> pathMap = new TreeMap<String, LinkedList<Integer>>();
		// posList
		LinkedList<Integer> posList = new LinkedList<Integer>();
		// new word
		if (!indexTreeMap.containsKey(word)) {
			posList.add(position + 1);

			pathMap.put(f.getAbsolutePath(), posList);
			indexTreeMap.put(word, pathMap);
		} // exist word
		else {
			pathMap = indexTreeMap.get(word);
			// see if exiting in new file
			// yes
			if (pathMap.get(f.getAbsolutePath()) == null) {
				posList.add(position + 1);

				pathMap.put(f.getAbsolutePath(), posList);
				indexTreeMap.put(word, pathMap);
			}// no
			else {
				// get the position
				posList = pathMap.get(f.getAbsolutePath());
				posList.add(position + 1);

				pathMap.put(f.getAbsolutePath(), posList);
				indexTreeMap.put(word, pathMap);
			}
		}

	}

	/**
	 * @return the indexTreeMap
	 */
	@SuppressWarnings("unused")
	private TreeMap<String, TreeMap<String, LinkedList<Integer>>> getData() {
		return indexTreeMap;

	}

	/**
	 * This method is for print all inverted index results to the
	 * invertedindex.txt
	 * 
	 * @throws IOException
	 */
	public void printIndex() throws IOException {
		// Create file
		FileWriter fstream = new FileWriter("invertedindex.txt");
		BufferedWriter out = new BufferedWriter(fstream);
		try {
			for (Map.Entry<String, TreeMap<String, LinkedList<Integer>>> entry : indexTreeMap
					.entrySet()) {

				out.write(entry.getKey() + "\n");
				TreeMap<String, LinkedList<Integer>> map2 = entry.getValue();

				for (Map.Entry<String, LinkedList<Integer>> entry2 : map2
						.entrySet()) {
					out.write("\"" + entry2.getKey() + "\"");

					LinkedList<Integer> mylist = entry2.getValue();
					for (int j = 0; j < mylist.size(); j++) {
						out.write(", " + mylist.get(j));
					}
					out.write("\n");
				}
				out.write("\n");
			}
			// Close the output stream

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

	/**
	 * This method is used to search keyword passed from QueryFileHandler in the
	 * IndexMap
	 * 
	 * @param keyWords
	 *            passed from the readQueryFile method of QueryFileHandler
	 *            class.
	 */
	public ArrayList<SearchResult> searchResult(String[] keyWords) {

		HashMap<String, SearchResult> infoMap = new HashMap<String, SearchResult>();

		for (String word : keyWords) {
					

			for (String cur = indexTreeMap.ceilingKey(word); cur != null; cur = indexTreeMap
					.higherKey(cur)) {

				if (!cur.startsWith(word)) {

					break;
				}

				for (Map.Entry<String, LinkedList<Integer>> entry : indexTreeMap
						.get(cur).entrySet()) {
					// if inner map of resultMap does not contain the file path.
					if (!infoMap.containsKey(entry.getKey())) {

						// get the size and first element from the LinkedList in
						// the indexTreeMap then put the key which is current
						// path and put the path and initialize a new
						// SearchResult into infoMap.

						infoMap.put(entry.getKey(),
								new SearchResult(entry.getKey(), entry
										.getValue().size(), entry.getValue()
										.getFirst()));

						// if inner map of resultMap already contains the file
						// path.
					} else if (infoMap.containsKey(entry.getKey())) {

						// pass the current frequency and first position to the
						// infoMap and update SearchResult.

						infoMap.get(entry.getKey()).updateValues(
								entry.getValue().size(),
								entry.getValue().getFirst());

					}
				}

			}

		}
		// get the value of infoMap and put them into a list.
		ArrayList<SearchResult> results = new ArrayList<SearchResult>(
				infoMap.values());
		// call sort method to sort the results
		Collections.sort(results);

		// return the results to QueryFileHandler.
		return results;

	}

}
