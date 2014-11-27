import java.util.TreeMap;

/**
 * This class ArgumentParser parse a command-line string arrays and place them
 * in a TreeMap
 * 
 * @author ANG ZHANG
 * 
 */
public class ArgumentParser {

	/**
	 * Declare a object TreeMap,argMap is an instance of TreeMap.
	 */
	private TreeMap<String, String> argMap;

	/**
	 * Constructor
	 * 
	 * @param args from command-line
	 */
	public ArgumentParser(String[] args) {
		// Initialize a TreeMap as arMap
		argMap = new TreeMap<String, String>();
		// call the method parseArgs
		parseArgs(args);
	}

	/**
	 * Parses a String array of arguments into flag, value pairs and stores them
	 * in a map
	 * 
	 * @param args from command-line.
	 */
	private void parseArgs(String[] args) {
		if (args.length > 0) {
			for (int i = 0; i < args.length - 1; i++) {

				if (isFlag(args[i]) == true && isValue(args[i + 1]) == true) {

					argMap.put(args[i], args[i + 1]);
				}

				else if (isFlag(args[i]) == true
						&& isValue(args[i + 1]) == false) {

					argMap.put(args[i], null);

				}

			}
			// In case of out bond of array
			int i = args.length - 1;

			if (isFlag(args[i]) == true) {
				argMap.put(args[i], null);
			}
		}
	}

	/**
	 * Tests whether or not an argument is considered a flag (i.e. it is a
	 * non-null string that starts with "-").
	 * 
	 * @param arg from command-line.
	 * @return true or false depends on if is flag.
	 */
	public static boolean isFlag(String arg) {

		if (arg.startsWith("-") && arg != null) {

			return true;
		} else
			return false;
	}

	/**
	 * Tests whether or not an argument is considered a value (i.e. it is a
	 * non-null string that does not start with "-")
	 * 
	 * @param arg which is parameter from command-line.
	 * @return true or false depends on if is value.
	 */
	public static boolean isValue(String arg) {

		if (!arg.startsWith("-") && arg != null) {
			return true;
		}

		else
			return false;
	}

	/**
	 * Tests whether or not a particular flag was passed in as a command-line
	 * argument
	 * 
	 * @param flag which is parameter from command-line start with "-".
	 * @return true or false depends on if contains flag.
	 */
	public boolean hasFlag(String flag) {

		if (argMap.containsKey(flag)) {

			return true;
		} else
			return false;

	}

	/**
	 * Tests whether a particular flag both exists, and has a value associated
	 * with it
	 * 
	 * @param flag which is parameter from command-line start with "-".
	 * @return true or false depends on if contains value.
	 */
	public boolean hasValue(String flag) {

		if (argMap.containsKey(flag) == true && argMap.get(flag) != null) {

			return true;
		} else
			return false;

	}

	/**
	 * Retrieves the value associated with a flag (may return null if no value
	 * exists).
	 * 
	 * @param flag which is parameter from command-line start with "-".
	 * @return the value accompany the flag
	 */
	public String getValue(String flag) {

		return argMap.get(flag);
	}

	/**
	 * Retrieves the number of flags (not arguments) that were passed in as
	 * command-line arguments.
	 * 
	 * @return the size of argMap.
	 */
	public int numFlags() {

		return argMap.size();
	}

	/**
	 * Retrieves the total number of command-line arguments passed in (do not
	 * count null values).
	 * 
	 * @return the number of Arguments.
	 */
	public int numArguments() {

		int count = argMap.size();
		for (String flag : argMap.keySet()) {
			if (argMap.get(flag) != null) {
				count++;
			}
		}
		return count;

	}
}
