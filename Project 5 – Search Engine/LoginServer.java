import org.apache.log4j.Logger;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.servlet.ServletHandler;

/**
 * Comprehensive example. Uses JDBC and SQL, Jetty and Servlets, HTTP and
 * Cookies, HTML, enum types, and logging.
 *
 * @author Sophie Engle
 */
public class LoginServer {
	protected static Logger log = Logger.getLogger(LoginServer.class);
//	private static int PORT = 8080;

	public LoginServer() {
	Server server = new Server(Driver.port);

		ServletHandler handler = new ServletHandler();
		server.setHandler(handler);

		handler.addServletWithMapping(LoginUserServlet.class,    "/login");
		handler.addServletWithMapping(LoginRegisterServlet.class, "/register");
		handler.addServletWithMapping(LoginWelcomeServlet.class,  "/welcome");
		handler.addServletWithMapping(SearchServlet.class, "/search");
		handler.addServletWithMapping(LoginRedirectServlet.class, "/*");
		

		log.info("Starting server on port " + Driver.port + "...");

		try {
			server.start();
			server.join();

			log.info("Exiting...");
		}
		catch (Exception ex) {
			log.fatal("Interrupted while running server.", ex);
			System.exit(-1);
		}
	}
}