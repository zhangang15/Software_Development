import java.io.IOException;
import java.io.PrintWriter;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;

/**
 * Base servlet for the {@link LoginServer} example.
 *
 * @see LoginServer
 * @author Sophie Engle
 */
@SuppressWarnings("serial")
public class LoginBaseServlet extends HttpServlet {

	/** A {@link org.apache.log4j.Logger log4j} logger for debugging. */
	protected static Logger log = Logger.getLogger(LoginBaseServlet.class);

	/** Singleton database handler instance. */
	protected static LoginDatabaseHandler db = LoginDatabaseHandler.getInstance();

	/**
	 * Prepares the HTTP response with the HTML head and page title.
	 *
	 * @param title - title of page
	 * @param response - HTTP response for the servlet
	 */
	protected static void prepareResponse(String title, HttpServletResponse response) {
		// set response headers
		response.setContentType("text/html");
		response.setCharacterEncoding("utf-8");

		try {
			// write out initial html
			PrintWriter writer = response.getWriter();
			writer.printf("<!DOCTYPE HTML PUBLIC \"-//W3C//DTD HTML 4.01//EN\" \"http://www.w3.org/TR/html4/strict.dtd\">%n%n");
			writer.printf("<html>%n%n");
			writer.printf("<head>%n");
			writer.printf("\t<title>%s</title>%n", title);
			writer.printf("\t<meta http-equiv=\"Content-Type\" content=\"text/html;charset=utf-8\">%n");
			writer.printf("</head>%n%n");
			writer.printf("<body>%n%n");
		}
		catch (IOException ex) {
			log.debug(ex.getMessage(), ex);
			response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
		}
	}

	/**
	 * Finishes the HTTP response, adding footer HTML and setting the
	 * response status.
	 *
	 * @param response - HTTP response for the servlet
	 */
	protected static void finishResponse(HttpServletResponse response) {
		try {
			PrintWriter writer = response.getWriter();
			writer.printf("%n");
			writer.printf("<p style=\"font-size: 10pt; font-style: italic;\">");
			writer.printf("Last updated at %s.", getDate());
			writer.printf("</p>%n%n");
			writer.printf("</body>%n");
			writer.printf("</html>%n");
			writer.flush();

			response.setStatus(HttpServletResponse.SC_OK);
			response.flushBuffer();
		}
		catch (IOException ex) {
			log.warn("Unable to finish HTTP response.");
			log.debug(ex.getMessage(), ex);
			response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
		}
	}

	/**
	 * Formats the current date and time as a {@link String}.
	 *
	 * @return text containing the current date and time
	 */
	protected static String getDate() {
		String format = "hh:mm a 'on' EEE, MMM dd, yyyy";
		DateFormat dateFormat = new SimpleDateFormat(format);
		return dateFormat.format(Calendar.getInstance().getTime());
	}

	/**
	 * Gets the cookies included in the HTTP request and stores them in
	 * a map for easy retrieval by cookie name.
	 *
	 * @param request - HTTP request for the servlet
	 * @return map of cookie names to values
	 */
	protected static Map<String, String> getCookieMap(HttpServletRequest request) {
		HashMap<String, String> map = new HashMap<String, String>();
		Cookie[] cookies = request.getCookies();

		if (cookies != null) {
			for (Cookie cookie : cookies) {
				map.put(cookie.getName(), cookie.getValue());
			}
		}

		return map;
	}

	/**
	 * Removes all of the cookies returned in the HTTP request.
	 *
	 * @param request - HTTP request for the servlet
	 * @param response - HTTP response for the servlet
	 */
	protected void clearCookies(HttpServletRequest request, HttpServletResponse response) {
		Cookie[] cookies = request.getCookies();

		if (cookies == null) {
			return;
		}

		for (Cookie cookie : cookies) {
			cookie.setMaxAge(0);
			response.addCookie(cookie);
		}
	}

	/**
	 * Clears a specific cookie. Does not check if cookie was included in
	 * the original HTTP request.
	 *
	 * @param cookieName - name of cookie to delete
	 * @param response - HTTP response for the servlet
	 */
	protected void clearCookie(String cookieName, HttpServletResponse response) {
		Cookie cookie = new Cookie(cookieName, null);
		cookie.setMaxAge(0);
		response.addCookie(cookie);
	}

	/**
	 * Converts an error name into a {@link Status} enum type.
	 *
	 * @param errorName - name of enum type
	 * @return {@link Status} object associated with the name or
	 * {@link Status.ERROR} if name is invalid
	 */
	protected String getStatusMessage(String errorName) {
		Status status = null;

		try {
			status = Status.valueOf(errorName);
		}
		catch (Exception ex) {
			log.debug(errorName, ex);
			status = Status.ERROR;
		}

		return status.toString();
	}

	/**
	 * Converts an error code into a {@link Status} enum type.
	 * @param code - error code or ordinal of the enum type
	 * @return {@link Status} object associated with the code or
	 * {@link Status.ERROR} if code is invalid
	 */
	protected String getStatusMessage(int code) {
		Status status = null;

		try {
			status = Status.values()[code];
		}
		catch (Exception ex) {
			log.debug(code, ex);
			status = Status.ERROR;
		}

		return status.toString();
	}

	/**
	 * Retrieves the username from the login cookie.
	 *
	 * @param request - HTTP request for the servlet
	 * @return username stored in the login cookie or null
	 */
	protected String getUsername(HttpServletRequest request) {
		Map<String, String> cookies = getCookieMap(request);

		String login = cookies.get("login");
		String user  = cookies.get("name");

		if (login != null && login.equals("true") && user != null) {
			// this is not safe!
			return user;
		}

		return null;
	}
}