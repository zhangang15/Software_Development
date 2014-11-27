import java.io.IOException;
import java.io.PrintWriter;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * Servlet for the {@link LoginServer} example.
 *
 * @see LoginServer
 * @author Sophie Engle
 */
@SuppressWarnings("serial")
public class LoginUserServlet extends LoginBaseServlet {

	/**
	 * Displays a login form, and any errors from previous login attempt.
	 * Uses error codes to avoid cross-site scripting attacks.
	 */
	@Override
	public void doGet(HttpServletRequest request, HttpServletResponse response) {
		prepareResponse("Login", response);

		try {
			PrintWriter out = response.getWriter();
			String error = request.getParameter("error");

			if (error != null) {

				int code = 0;

				try {
					code = Integer.parseInt(error);
				}
				catch (Exception ex) {
					code = -1;
				}

				String errorMessage = getStatusMessage(code);
				out.println("<p style=\"color: red;\">" + errorMessage + "</p>");
			}

			if (request.getParameter("newuser") != null) {
				out.println("<p>Registration was successful!");
				out.println("Login with your new username and password below.</p>");
			}

			if (request.getParameter("logout") != null) {
				clearCookies(request, response);
				out.println("<p>Successfully logged out.</p>");
			}

			printForm(out);
		}
		catch (IOException ex) {
			log.debug("Unable to prepare response body.", ex);
		}

		finishResponse(response);
	}

	/**
	 * Processes login form. If user properly logged in, adds login cookie
	 * and redirects user to <code>/welcome</code>. Otherwise, displays a
	 * message describing why login failed using error codes to avoid
	 * cross-site scripting attacks.
	 */
	@Override
	public void doPost(HttpServletRequest request, HttpServletResponse response) {
		String user = request.getParameter("user");
		String pass = request.getParameter("pass");

		Status status = db.authenticateUser(user, pass);

		try {
			if (status == Status.OK) {
				// should eventually change this to something more secure
				response.addCookie(new Cookie("login", "true"));
				response.addCookie(new Cookie("name", user));
				response.sendRedirect(response.encodeRedirectURL("/welcome"));
			}
			else {
				response.addCookie(new Cookie("login", "false"));
				response.addCookie(new Cookie("name", ""));
				response.sendRedirect(response.encodeRedirectURL("/login?error=" + status.ordinal()));
			}
		}
		catch (Exception ex) {
			log.error("Unable to process login form.", ex);
		}
	}

	/**
	 * Prints login form.
	 *
	 * @param out - writer for the servlet
	 */
	private void printForm(PrintWriter out) {
		assert out != null;

		out.println("<form action=\"/login\" method=\"post\">");
		out.println("<table border=\"0\">");
		out.println("\t<tr>");
		out.println("\t\t<td>Usename:</td>");
		out.println("\t\t<td><input type=\"text\" name=\"user\" size=\"30\"></td>");
		out.println("\t</tr>");
		out.println("\t<tr>");
		out.println("\t\t<td>Password:</td>");
		out.println("\t\t<td><input type=\"password\" name=\"pass\" size=\"30\"></td>");
		out.println("</tr>");
		out.println("</table>");
		out.println("<p><input type=\"submit\" value=\"Login\"></p>");
		out.println("</form>");

		out.println("<p>(<a href=\"/register\">new user? register here.</a>)</p>");
	}
}
