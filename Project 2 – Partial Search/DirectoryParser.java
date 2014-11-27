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
	 * This method getFileList is for return the fileList only including text files.
	 * 
	 * @param dir directory
	 * @return text type files
	 */
	public static List<File> getFileList(File dir) {
		List<File> fileList = new ArrayList<File>();
		getAllTxt(dir, fileList);
		return fileList;
	}

	//
	/**
	 * This method getAllTxt use recursive way to add all text files into a list.
	 * 
	 * @param dir directory contains text files and sub-directory.
	 * @param fileList obtain all the text files in the directory.
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
