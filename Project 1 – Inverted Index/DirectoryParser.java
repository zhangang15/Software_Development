import java.io.File;
import java.io.FileFilter;
import java.util.ArrayList;
import java.util.List;

/**
 * This class DirectoryParser parse a directory string and obtain all the text
 * files.
 * 
 * @author ANG ZHANG
 * 
 */
public class DirectoryParser {

	/**
	 * This method getFileList is for return the fileList.
	 * 
	 * @param dir which is directory passed from command-line.
	 * @return filelist which included in the directory.
	 */
	public static List<File> getFileList(File dir) {
		List<File> fileList = new ArrayList<File>();
		getAllTxt(dir, fileList);
		return fileList;
	}

	//
	/**
	 * This method getAllTxt use recursive way to get all text files.
	 * 
	 * @param dir
	 * @param fileList which is the list with all text files 
	 */
	public static void getAllTxt(File dir, List<File> fileList) {

		if (!dir.isDirectory()) {
			System.out.println("Please input valid path¡£");
		} else {
			TxtFileFilter filter = new TxtFileFilter();
			File[] files = dir.listFiles(filter);
			for (File file : files) {
				if (file.isDirectory()) {
					getAllTxt(file, fileList);
				} else if (file.isFile()) {
					fileList.add(file);
				}
			}

		}
	}

	/**
	 * This class TxtFileFileter is designed to filter text files.
	 * 
	 */
	private static class TxtFileFilter implements FileFilter {

		public boolean accept(File file) {
			if (file.isDirectory()) {
				return true;
			} else if (file.getName().toLowerCase().endsWith(".txt")
					&& !file.isHidden()) {
				return true;
			} else
				return false;
		}
	}
}
