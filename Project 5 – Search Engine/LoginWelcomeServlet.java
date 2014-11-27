import java.io.IOException;
import java.io.PrintWriter;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * Servlet for the {@link LoginServer} example.
 *
 * @see LoginServer
 * @author Sophie Engle
 */
@SuppressWarnings("serial")
public class LoginWelcomeServlet extends LoginBaseServlet {
	/**
	 * If the user is logged in, displays a welcome message. Otherwise,
	 * redirects user to the login page.
	 */
	@Override
	public void doGet(HttpServletRequest request, HttpServletResponse response) {
		String user = getUsername(request);

		if (user != null) {
			prepareResponse("Welcome", response);

			try {
				PrintWriter out = response.getWriter();
				out.println("<p>Hello " + user + "!</p>");
				out.println("<a href = \" /search\">search</a>");
				out.println("<p><a href=\"/login?logout\">(logout)</a></p>");
			}
			catch (IOException ex) {
				log.warn("Unable to write response body.", ex);
			}

			finishResponse(response);
		}
		else {
			try {
				response.sendRedirect("/login");
			}
			catch (Exception ex) {
				log.warn("Unable to redirect to /login page.", ex);
			}
		}
	}

	/**
	 * Operation not supported.
	 *
	 * @see #doGet(HttpServletRequest, HttpServletResponse)
	 */
	@Override
	public void doPost(HttpServletRequest request, HttpServletResponse response) {
		doGet(request, response);
	}
}
