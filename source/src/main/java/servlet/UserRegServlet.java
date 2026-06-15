package servlet;

import java.io.IOException;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import dao.UserDao;
import dto.User;

/**
 * Servlet implementation class UserRegServlet
 */
@WebServlet("/UserRegServlet")
public class UserRegServlet extends HttpServlet {
	
	private static final long serialVersionUID = 1L;

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse
	 *      response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		// リクエストパラメータを取得する
				request.setCharacterEncoding("UTF-8");
				int id;					
				String user_nickname = request.getParameter("user_nickname");	
				String password = request.getParameter("password");		
				String mail_address = request.getParameter("mail_address");	
				String c_at = request.getParameter("c_at");			
				String u_at = request.getParameter("u_at");
				
		// 登録処理を行う
		UserDao userDao = new UserDao();
		if (userDao.insert(new User(0,user_nickname,password,mail_address,c_at,u_at))) { 
			// 登録成功
			response.sendRedirect("/b4/LoginServlet");
			return;
		} 
		/*else { // 登録失敗
			request.setAttribute("result", new Result("登録失敗！", "レコードを登録できませんでした。", "/b4/UserRegServlet"));
		}*/
		
		// 新規登録ページにフォワードする
		RequestDispatcher dispatcher = request.getRequestDispatcher("/WEB-INF/jsp/userreg.jsp");
		dispatcher.forward(request, response);
	}

}
