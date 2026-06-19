package servlet;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
	
/**
 * Servlet implementation class RegistServlet
 */
@WebServlet("/ReportServlet")
public class ReportServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;
	    
	 /**
	  * @see HttpServlet#HttpServlet()
	  */
	protected void doGet(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
//			// もしもログインしていなかったらログインサーブレットにリダイレクトする
//			HttpSession session = request.getSession();
//			if (session.getAttribute("id") == null) {
//				response.sendRedirect("/LoginServlet");
//				return;
//			}

	}
}
