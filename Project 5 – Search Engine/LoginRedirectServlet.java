import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * Servlet for the {@link LoginServer} example.
 *
 * @see LoginServer
 * @author Sophie Engle
 */
@SuppressWarnings("serial")
public class LoginRedirectServlet extends LoginBaseServlet {
	/**
	 * Redirects the user to <code>/welcome</code> if the user is logged
	 * in, otherwise redirects to <code>/login</code>.
	 */
	@Override
	public void doGet(HttpServletRequest request, HttpServletResponse response) {
		if (getUsername(request) != null) {
			try {
				response.sendRedirect("/welcome");
			}
			catch (Exception ex) {
				log.debug("Unable to redirect to /welcome page.", ex);
				response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
			}
		}
		else {
			try {
				response.sendRedirect("/login");
			}
			catch (Exception ex) {
				log.debug("Unable to redirect to /login page.", ex);
				response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
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
