import java.io.FileReader;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Properties;

import org.apache.log4j.Logger;

/**
 * Handles loading a database configuration from a properties file. Used
 * by the {@link LoginDatabaseHandler} class.
 *
 * @author Sophie Engle
 */
public class DatabaseConfigurator {

	/** A {@link org.apache.log4j.Logger log4j} logger for debugging. */
	private static Logger log = Logger.getLogger(DatabaseConfigurator.class);

	/** Tracks whether the database settings are configured properly. */
	private Status configStatus;

	/**
	 * Stores the database URI in the format:
	 *
	 * <code>protocol:subprotocol://host/database</code>
	 *
	 * where the protocol is assumed to be <code>jdbc</code> and the
	 * subprotocol is assumed to be <code>mysql</code>. (We might want to
	 * load the protocol and subprotocol from the database properties file
	 * as well.)
	 */
	private String dbURI;

	/**
	 * Stores the database login information.
	 */
	private Properties dbLogin;

	/**
	 * Default constructor. Will automatically attempt to load database
	 * configuration from the file <code>database.properties</code>.
	 *
	 * @see #DatabaseConfigurator(String)
	 */
	public DatabaseConfigurator() {
		this("database.properties");
	}

	/**
	 * Will automatically attempt to load database configuration from the
	 * provided file.
	 *
	 * @param filename - properties file with the database configuration
	 */
	public DatabaseConfigurator(String filename) {
		dbURI = null;
		dbLogin = null;
		loadConfig(filename);
	}

	/**
	 * Retrieves the current database URI.
	 *
	 * @return the current database URI
	 */
	public String getURI() {
		return dbURI;
	}

	/**
	 * Retrieves the {@link Status} of the last attempt to load the
	 * database configuration.
	 *
	 * @return the status of the last configuration attempt
	 */
	public Status getStatus() {
		return configStatus;
	}

	/**
	 * Loads the database configuration from the file
	 * <code>database.properties</code>.
	 *
	 * @see #loadConfig(String)
	 * @return {@link Status.OK} if the configuration was successful
	 */
	public Status loadConfig() {
		return loadConfig("database.properties");
	}

	/**
	 * Loads the database configuration from the provided file.
	 *
	 * @param filename - properties file with the database configuration
	 * @return {@link Status.OK} if the configuration was successful
	 */
	public Status loadConfig(String filename) {
		String username = null;
		String password = null;
		String database = null;
		String hostname = null;

		configStatus = null;

		log.debug("Loading configuration from " + filename + ".");

		try {
			// load configuration into a properties object
			Properties dbconfig = new Properties();
			dbconfig.load(new FileReader(filename));

			// retrieve values
			username = dbconfig.getProperty("username");
			password = dbconfig.getProperty("password");
			database = dbconfig.getProperty("database");
			hostname = dbconfig.getProperty("hostname");

			// check for any missing values
			if (!checkString(username) || !checkString(password) ||
					!checkString(database) || !checkString(hostname)) {
				configStatus = Status.MISSING_VALUES;
				log.debug("User: " + username + ", Pass: " + password);
				log.debug("Data: " + database + ", Host: " + hostname);
			}
			else {
				configStatus = Status.OK;
			}
		}
		catch (IOException ex) {
			configStatus = Status.MISSING_CONFIG;
			log.debug(configStatus, ex);
		}

		// load a default configuration if necessary
		if (configStatus != Status.OK) {
			log.warn("Loading default configuration for missing values.");

			username = (username == null) ? "user01" : username;
			password = (password == null) ? username : password;
			database = (database == null) ? username : database;
			hostname = (hostname == null) ? "sql.cs.usfca.edu" : hostname;
		}

		// create database uri string
		dbURI = String.format("jdbc:mysql://%s/%s", hostname, database);
		log.info("Using database " + database + " at " + hostname + ".");
		log.debug(dbURI);

		// setup database login information
		dbLogin = new Properties();
		dbLogin.put("user", username);
		dbLogin.put("password", password);

		return configStatus;
	}

	/**
	 * Creates a database connection from the current configuration.
	 *
	 * @return database connection
	 * @throws SQLException
	 */
	public Connection getConnection() throws SQLException {
		return DriverManager.getConnection(dbURI, dbLogin);
	}

	/**
	 * Tests the current database configuration by attempting to execute
	 * a simple SQL statement.
	 *
	 * @return {@link Status.OK} if the test was successful
	 */
	public Status testConfig() {
		Connection connection = null;
		Statement statement = null;
		ResultSet results = null;

		Status status = Status.ERROR;
		int num = 0;

		try {
			connection = getConnection();
			statement = connection.createStatement();
			results = statement.executeQuery("SHOW TABLES;");

			// count how many tables were returned.
			while (results.next()) {
				num++;
			}

			log.debug("Found " + num + " tables.");
			status = Status.OK;

			results.close();
			statement.close();
		}
		catch (Exception ex) {
			status = Status.CONNECTION_FAILED;
			log.debug(status, ex);
		}
		finally {
			try {
				// always close the connection
				connection.close();
			}
			catch (Exception ignored) {
				// do nothing
			}
		}

		return status;
	}

	/**
	 * Checks if a {@link String} is null or empty.
	 *
	 * @param text - {@link String} to check
	 * @return <code>true</code> if not null and not empty
	 */
	public static boolean checkString(String text) {
		return text != null && !text.trim().isEmpty();
	}

//	public static void main(String[] args) {
//		new DatabaseConfigurator().testConfig();
//	}

}
