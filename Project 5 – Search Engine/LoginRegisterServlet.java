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
public class LoginRegisterServlet extends LoginBaseServlet {
	/**
	 * Displays registration form. If previous registration attempt
	 * failed, displays reason why using error names to avoid cross-site
	 * scripting attacks.
	 */
	@Override
	public void doGet(HttpServletRequest request, HttpServletResponse response) {
		try {
			prepareResponse("Register New User", response);

			PrintWriter out = response.getWriter();
			String error = request.getParameter("error");

			if(error != null) {
				String errorMessage = getStatusMessage(error);
				out.println("<p style=\"color: red;\">" + errorMessage + "</p>");
			}

			printForm(out);
			finishResponse(response);
		}
		catch(IOException ex) {
			log.debug("Unable to prepare response properly.", ex);
		}
	}

	/**
	 * Attempts to register a new user. If successful, redirects to the
	 * login page. Otherwise, redisplays registration form and error
	 * message.
	 */
	@Override
	public void doPost(HttpServletRequest request, HttpServletResponse response) {
		prepareResponse("Register New User", response);

		String newuser = request.getParameter("user");
		String newpass = request.getParameter("pass");
		Status status = db.registerUser(newuser, newpass);

		try {
			if(status == Status.OK) {
				response.sendRedirect(response.encodeRedirectURL("/login?newuser=true"));
			}
			else {
				String url = "/register?error=" + status.name();
				url = response.encodeRedirectURL(url);
				response.sendRedirect(url);
			}
		}
		catch(IOException ex) {
			log.warn("Unable to redirect user. " + status, ex);
			response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
		}
	}

	/**
	 * Prints registration form.
	 *
	 * @param out - writer for the servlet
	 */
	private void printForm(PrintWriter out) {
		assert out != null;

		out.println("<form action=\"/register\" method=\"post\">");
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
		out.println("<p><input type=\"submit\" value=\"Register\"></p>");
		out.println("</form>");
	}
}
