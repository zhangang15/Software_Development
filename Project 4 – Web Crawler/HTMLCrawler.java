import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.Socket;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class HTMLCrawler {

	/**
	 * PORT is port number of web.
	 */
	public static final int PORT = 80;

	/**
	 * seed is location of web page.
	 */
	private String seed;
	/**
	 * This is group of threads to implement multi-threading.
	 */
	private WorkQueue crawlerWorkQueue;
	/**
	 * The parameter pending is for counting how many threads are working right
	 * now.
	 */
	private int pending;

	/**
	 * This urlSet is a set using for store all the url-links during process.
	 */
	private Set<String> urlSet;

	/**
	 * This MultiReadersLock crawlerLock is for locking the shared objects may
	 * accessed by different workers.
	 */
	private MultiReadersLock crawlerLock;

	/**
	 * It is regular expression for HTML tags.
	 */
	private static String RE_HTMLTAG = "<[^>]*>";

	/**
	 * It is regular expression for HTML entities.
	 */
	private static String RE_ENTITY = "&#?\\w+;";

	/**
	 * This InvertedIndex type dataStore is for store word when getting word
	 * from web page.
	 */
	private InvertedIndex dataStore;

	/**
	 * Creates a new HTMLCleaner object for the specified URL. If the URL is not
	 * valid, uses 120.0.0.1 for the domain and / for the resource by default.
	 * 
	 * @param url
	 *            is location of web page.
	 * 
	 * @param index
	 *            is used for storing word parsed from web page.
	 * 
	 * @param wQueue
	 *            is group of threads used for implementing multi-threading.
	 */
	public HTMLCrawler(String url, InvertedIndex index, WorkQueue wQueue) {
		this.seed = url;
		this.dataStore = index;
		this.crawlerWorkQueue = wQueue;
		urlSet = new HashSet<String>();
		crawlerLock = new MultiReadersLock();
		pending = 0;
	}

	/**
	 * Parses a line of text into lowercase words with no symbols, and adds
	 * those words to a list.
	 * 
	 * @param buffer
	 *            is text containing words separated by white-spaces.
	 * 
	 * @param words
	 *            is an array list using for store the one line words.
	 */
	private void parseLine(String buffer, ArrayList<String> words) {

		for (String word : buffer.split("\\s+")) {
			word = word.toLowerCase().replaceAll("[\\W_]", "").trim();

			if (!word.isEmpty()) {
				words.add(word);
			}
		}
	}

	/**
	 * Tests whether a start tag exists for the element in the buffer.
	 * 
	 * @param element
	 *            the element name, like "style" or "script"
	 * @param buffer
	 *            the html code being checked
	 * @return true if the start element tag exists in the buffer
	 */
	public static boolean startElement(String element, String buffer) {
		if (buffer.matches(".*<" + element + ".*>.*")) {
			return true;
		} else {
			return false;
		}
	}

	/**
	 * Tests whether a close tag exists for the element in the buffer.
	 * 
	 * @param element
	 *            the element name, like "style" or "script"
	 * @param buffer
	 *            the html code being checked
	 * @return true if the start element tag exists in the buffer
	 */
	public static boolean closeElement(String element, String buffer) {
		if (buffer.matches(".*</" + element + ">.*")) {
			return true;
		} else {
			return false;
		}
	}

	/**
	 * Removes the element tags and all text between from the buffer.
	 * 
	 * @param element
	 *            the element name, like "style" or "script"
	 * @param buffer
	 *            the html code being checked
	 * @return text without the start and close tags and all text inbetween
	 */
	public static String stripElement(String element, String buffer) {
		return buffer.replaceAll("<" + element + "[^>]*>" + ".*" + "</"
				+ element + "[^>]*>", " ");

	}

	/**
	 * Replaces all HTML entities in the buffer with a single space.
	 * 
	 * @param buffer
	 *            the html code being checked
	 * @return text with HTML entities replaced by a space
	 */
	public static String stripEntities(String buffer) {

		return buffer.replaceAll(RE_ENTITY, " ");

	}

	/**
	 * Replaces all HTML tags in the buffer with a single space.
	 * 
	 * @param buffer
	 *            the html code being checked
	 * @return text with HTML tags replaced by a space
	 */
	public static String stripHTML(String buffer) {
		return buffer.replaceAll(RE_HTMLTAG, " ");
	}

	/**
	 * Get link urls in the buffer.
	 * 
	 * @param buffer
	 *            the html code being checked
	 * @return text with HTML tags replaced by a space
	 */
	public static String parseLink(String buffer) {
		Matcher matcher = Pattern.compile("(a href=\")([^>]*?)(\")").matcher(
				buffer);
		if (matcher.find()) {
			return matcher.group(2);
		}

		return null;
	}

	/**
	 * In this method, initialize a inner class and ask work queue to execute
	 * this class. In the inner class ,it opens a socket and downloads the
	 * webpage one line at a time, removing all text between style or script
	 * tags, removing HTML tags, and removing special symbols (HTML entities).
	 * Stores only one line of the web page at once unless additional lines are
	 * necessary to parse missing close tags. Each cleaned line of text is then
	 * parsed into words and placed into a list.
	 */
	public void parseWords() {
		URL url = null;
		try {
			url = new URL(seed);
			urlSet.add(url.getFile());

			// using this url to initialize the inner class CrawlerWorker.
			CrawlerWorker worker = new CrawlerWorker(url);

			// call crawlerWorkQueu to execute this runnable class.
			crawlerWorkQueue.execute(worker);

			// We need to wait all of threads have finished the job.
			while (getPending() > 0) {
				synchronized (this) {
					wait();
				}
			}

		} catch (Exception ex) {
			System.err.println("Error: " + ex.getMessage());
		}

	}

	/**
	 * This shared method getPending is used for obtaining the current number of
	 * running threads.
	 * 
	 * @return the number of threads which are running.
	 */
	private synchronized int getPending() {
		return pending;
	}

	/**
	 * This shared method is using for update the number of threads that are
	 * processing.
	 * 
	 * @param amount
	 *            which stands for the number of threads we want to run.
	 */
	private synchronized void updatePending(int amount) {
		pending += amount;

		if (pending <= 0) {
			notifyAll();
		}

	}

	/**
	 * This inner class is for implementing runnable interface. In the override
	 * method, opens a socket and downloads the web page one line at a time,
	 * removing all text between style or script tags, removing HTML tags, and
	 * removing special symbols (HTML entities). Stores only one line of the web
	 * page at once unless additional lines are necessary to parse missing close
	 * tags. Each cleaned line of text is then parsed into words and placed into
	 * a list.
	 * 
	 * @author ANG ZHANG
	 * 
	 */
	private class CrawlerWorker implements Runnable {
		/**
		 * It is URL type link.
		 */
		private URL base;
		/**
		 * It is domain.
		 */
		private String domain;

		/**
		 * It is resource.
		 */
		private String resource;

		/**
		 * @param url
		 *            is location of web.
		 */
		public CrawlerWorker(URL url) {
			try {
				base = url;
				this.domain = base.getHost();
				this.resource = base.getFile();
			} catch (Exception ex) {
				this.domain = "127.0.0.1";
				this.resource = "/";
			}
			// after running a thread, update the pending number by one.
			updatePending(1);
		}

		@Override
		public void run() {
			//store the word from current page.
			ArrayList<String> wordList = new ArrayList<String>();

			Socket socket = null;
			BufferedReader in = null;
			try {
				// initialize a socket class with domain and PORT.
				socket = new Socket(domain, PORT);
				// get output stream from web
				PrintWriter out = new PrintWriter(new BufferedWriter(
						new OutputStreamWriter(socket.getOutputStream())));

				StringBuffer output = new StringBuffer();
				output.append("GET " + resource + " HTTP/1.1\n");
				output.append("Host: " + domain + "\n");
				output.append("Connection: close\n");
				output.append("\r\n");

				out.println(output.toString());
				out.println();
				out.flush();
				// get the input stream from socket
				in = new BufferedReader(new InputStreamReader(
						socket.getInputStream()));

				boolean isHeader = true;
				String inputLine;
				URL url;
				String file;
				// read the context line by line.
				while ((inputLine = in.readLine()) != null) {
					// discard http header
					if (isHeader) {
						if (inputLine == null || inputLine.isEmpty()) {
							isHeader = false;
						} else {
							if (inputLine.contains("Content-Type")
									&& !inputLine.contains("text/html")) {
								break;
							}
						}
					} else {
						// main process
						// check if has scripts symbols
						if (startElement("script", inputLine)) {
							if (!closeElement("script", inputLine)) {
								boolean found = false;
								// not ending in the same line
								while (!found) {
									// read one more line
									inputLine += in.readLine();
									if (closeElement("script", inputLine)) {
										found = true;

									}
								}
							}
							// remove the "script"
							inputLine = stripElement("script", inputLine);
						}

						// check if has styles symbols
						if (startElement("style", inputLine)) {
							if (!closeElement("style", inputLine)) {
								boolean found = false;
								// not ending in the same line
								while (!found) {
									// read one more line to find end tag.
									inputLine += in.readLine();
									if (closeElement("style", inputLine)) {
										found = true;
									}
								}
							}
							// remove the "style"
							inputLine = stripElement("style", inputLine);
						}
						// to see if we have found 50 urls.
						if (urlSet.size() < 50) {

							String link = parseLink(inputLine);

							if (link != null && !link.isEmpty()) {

								url = new URL(base, link);
								file = url.getFile();
								// found new url link not included in urlSet.
								if (!urlSet.contains(link)) {

									// Because here may have more than one
									// thread will write data into
									// urlSet, we need to lock the shared
									// object.
									crawlerLock.acquireWriteLock();

									// add data to urlSet
									urlSet.add(file);

									// After writing the data, releasing the
									// lock.
									crawlerLock.releaseWriteLock();

									// star a new thread to deal with the url.
									crawlerWorkQueue.execute(new CrawlerWorker(
											url));
								}
							}
						}

						// strip html tag
						if (inputLine.matches("<.*")) {
							if (!inputLine.matches(RE_HTMLTAG + ".*")) {
								boolean found = false;
								// not in the same line
								while (!found) {
									// handle one more line
									inputLine += in.readLine();
									if (inputLine.matches(RE_HTMLTAG + ".*")) {
										found = true;
									}
								}
							}
						}
						// remove the HTML tags from line.
						inputLine = stripHTML(inputLine);
						// remove the HTML entities from line.
						inputLine = stripEntities(inputLine);
						// parse line and add word to the list
						parseLine(inputLine, wordList);
					}
				}

				// parse words
				if (wordList.size() > 0) {
					InvertedIndex localIndex = new InvertedIndex();
					int position = 0;
					for (int i = 0; i < wordList.size(); i++) {

						if (getWordWithoutSymbols(wordList.get(i)).isEmpty()) {
							continue;
						} else {

							// call indexData method to store the word.
							localIndex.indexData(base.toString(),
									getWordWithoutSymbols(wordList.get(i)),
									position);

							position++;
						}

					}
					// after dealing with the current page, add all local
					// content to dataStore.
					dataStore.addAllIndex(localIndex, base.toString());
				}
				// after all the processing, decrease the number of thread
				updatePending(-1);

			} catch (Exception ex) {
				System.err.println("Error: " + ex.getMessage());
			} finally {
				// close the output stream.
				try {
					in.close();
					socket.close();
				} catch (Exception ignored) {
					// do nothing
				}
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
