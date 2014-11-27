/**
 * This class SearchResult is using for storing the path, frequency and the
 * first position of keywords.
 * 
 * @author ANG ZHANG
 * 
 */

public class SearchResult implements Comparable<SearchResult> {

	/**
	 * path of keywords
	 */
	private String path;

	/**
	 * frequency of keyword
	 */
	private int freq;

	/**
	 * the first position of keyword
	 */
	private int pos;

	/**
	 * The constructor of class
	 * 
	 * @param path
	 *            path of keywords
	 * @param freq
	 *            frequency of keyword
	 * @param pos
	 *            the first position of keyword
	 */
	public SearchResult(String path, int freq, int pos) {

		this.path = path;
		this.freq = freq;
		this.pos = pos;

	}

	// rewrite the compareTo method to sort the search result.
	public int compareTo(SearchResult other) {

		if (this.freq < other.freq)
			return 1;
		else if (this.freq == other.freq) {
			if (this.pos < other.pos)
				return -1;
			else if (this.pos == other.pos)
				return path.compareTo(other.path);
			else
				return 1;
		} else
			return -1;
	}

	/**
	 * When get the new info of keyword, call this method to update the
	 * attributes.
	 * 
	 * @param frequency
	 *            This is frequency of keyword.
	 * @param position
	 *            This is the first position of keyword.
	 */
	public void updateValues(int frequency, int position) {

		this.freq += frequency;
		this.pos = this.pos < position ? this.pos : position;

	}

	// rewrite the toString method to easy to output the result based on the
	// requirement.
	public String toString() {
		return "\"" + this.path + "\"" + ", " + this.freq + ", " + this.pos
				+ "\n";
	}
}
