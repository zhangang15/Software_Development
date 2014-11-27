import java.io.File;
import java.io.IOException;
import java.util.List;

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
	 * @param args from command-line.
	 * @throws IOException
	 * 
	 */
	public static void main(String[] args) {

		ArgumentParser parser = new ArgumentParser(args);
		String dir = null;
		try {
			if (parser.hasFlag("-d")) {
				dir = parser.getValue("-d");
			}

			InvertedIndex i = new InvertedIndex();
			FileReader fr = new FileReader(i,
					DirectoryParser.getFileList(new File(dir)));
			fr.readFiles();
			i.printFile();

		} catch (Exception ex) {
			System.out.println("Please input right directory.");

		}
		// TODO Auto-generated method stub

	}
}
