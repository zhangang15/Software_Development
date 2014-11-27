import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
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
	 * This InvertedIndex type datastore is for store word when get word from
	 * text file.
	 */
	private InvertedIndex dataStore;
	/**
	 * This List filelist is used for storing the list of files obtained from
	 * the DirectoryParser.
	 */
	private List<File> filelist;

	/**
	 * 
	 * Constructor
	 * 
	 * @param i
	 *            is reference of InvertedIndex
	 * @param filelist
	 *            is a list consist of files
	 */
	public FileReader(InvertedIndex i, List<File> filelist) {
		this.dataStore = i;
		this.filelist = filelist;

	}

	/**
	 * This method readFile is using for parsing one text file line by line.
	 * 
	 * @param filepath
	 * @throws IOException
	 */
	public void readFile(String filepath) throws IOException {
		File sourceFile = new File(filepath);

		if (!sourceFile.isFile() || !sourceFile.canRead()) {
			throw new IOException("Unable to open file");

		}
		FileInputStream fir = new FileInputStream(sourceFile.getAbsolutePath());
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

						// call indexData method to store the word.
						dataStore.indexData(sourceFile,
								getWordWithoutSymbols(wordArray[i]), position);

						position++;
					}

				}

			}

		} catch (IOException exception) {

			throw new IOException("Unable to count the word.", exception);
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
	 * This method readFile is used to read all files from the filelist.
	 */
	public void readFiles() {

		for (File file : this.filelist) {
			try {
				this.readFile(file.getAbsolutePath());
			} catch (IOException e) {

				System.out.println("The file cannot be read.");
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
