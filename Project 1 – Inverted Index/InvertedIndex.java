import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.util.LinkedList;
import java.util.List;
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
				// System.out.println(i+1);
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
	private TreeMap<String, TreeMap<String, LinkedList<Integer>>> getData() {
		return indexTreeMap;

	}

	/**
	 * This method is for print all inverted index results to the
	 * invertedindex.txt
	 */
	public void printFile() {
		try {
			// Create file
			FileWriter fstream = new FileWriter("invertedindex.txt");
			BufferedWriter out = new BufferedWriter(fstream);
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
			out.close();
		} catch (Exception e) {// Catch exception if any
			System.err.println("Error: " + e.getMessage());
		}
	}

}
