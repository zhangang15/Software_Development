import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;


public class SearchServlet extends LoginBaseServlet{
	@Override
	public void doGet(HttpServletRequest request, HttpServletResponse response) {
		prepareResponse("Search Engine", response);

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
		String searchword = request.getParameter("q");
		String[] words = searchword.split(" ");
		ArrayList<SearchResult> arrayList = Driver.i.searchResult(words);
		
		PrintWriter out;
		try {
			out = response.getWriter();
			//out.println("<ul>");
			
			for (SearchResult srResult : arrayList) {
				out.println( srResult.path );
			}
			//out.println("</ul>");

		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		
	}

	/**
	 * Prints login form.
	 *
	 * @param out - writer for the servlet
	 */
	private void printForm(PrintWriter out) {
		assert out != null;

		out.println("<!DOCTYPE HTML>");
		out.println("<html><head>");
		out.println("<meta http-equiv=\"Content-Type\" content=\"text/html; charset=UTF-8\">");
		out.println("<meta name=\"description\" content=\"Search\">");
		out.println("<title>Search Engine by Ang Zhang</title>");
		out.println("");
		out.println("<link href=\"public/css.css\" rel=\"stylesheet\" type=\"text/css\">");
		out.println("<style type=\"text/css\">");
		out.println("#yd{width:360px;height:135px;margin:0 auto;text-indent:-9999em;background:url(public/logo.png);_background:none;_filter:progid:DXImageTransform.Microsoft.AlphaImageLoader(src=\"public/logo.png\", sizingMethod=\"crop\")}");
		out.println("");
		out.println("</style>");
		out.println("<script src=\"public/core.js\"></script>");
		out.println("</head>");
		out.println("<body>");
		out.println("<div id=\"t\">");
		out.println("  <div id=\"u\"> ");
		out.println("  </div>");
		out.println("  <div id=\"n\"> </div>");
		out.println("</div>");
		out.println("<form action=\"/search\" method=\"post\">");
		out.println("  <div id=\"f\">");
		out.println("    <h1 id=\"yd\">Search Engine</h1>");
		out.println("    <div id=\"fw\"><div id=\"fc\" class=\"sp\"><div id=\"qc\"><input id=\"query\" name=\"q\" autocomplete=\"off\" type=\"text\"> </div><input value=\"Search\" id=\"qb\" class=\"sp\" type=\"submit\"></div>");
		out.println("    </div>");
		out.println("    <div id=\"ao\"> ");
		out.println("    <div class=\"li\"><span id=\"zs\"></span></div>");
		out.println("    <div class=\"li_con\" id=\"con\"></div>");
		out.println("    </div>");
		out.println("  </div>");
		out.println("  </form>");
		out.println("<script>document.getElementById('query').focus();</script>");
		out.println("    <div id=\"b\">");
		out.println("    <p> ");
		out.println("    </p>");
		out.println("    <p id=\"cr\">&nbsp;&nbsp;©2011  Search Engine bu Ang Zhang for Project5@CS212</p>");
		out.println("    </div>");
		out.println("     ");
		out.println("<div id=\"h\"></div>");

		out.println("<div style=\"position: absolute; display: none; z-index: 9999;\" id=\"livemargins_control\"><img src=\"public/monitor-background-horizontal.png\" style=\"position: absolute; left: -77px; top: -5px;\" width=\"77\" height=\"5\">	<img src=\"public/monitor-background-vertical.png\" style=\"position: absolute; left: 0pt; top: -5px;\">	<img id=\"monitor-play-button\" src=\"public/monitor-play-button.png\" onMouseOver=\"this.style.opacity=1\" onMouseOut=\"this.style.opacity=0.5\" style=\"position: absolute; left: 1px; top: 0pt; opacity: 0.5; cursor: pointer;\"></div><div></div><div style=\"position: absolute; z-index: 10000; display: none;\"></div>");
		out.println("</body></html>");

	}
}
