package servlet;

import java.io.IOException;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import dao.UserDao;
import dto.LoginUser;
import dto.User;

@WebServlet("/LoginServlet")
public class LoginServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;

	protected void doGet(HttpServletRequest request, HttpServletResponse response)
		throws ServletException, IOException {
		// ログインページにフォワードする
		RequestDispatcher dispatcher = request.getRequestDispatcher("/WEB-INF/jsp/login.jsp");
		dispatcher.forward(request, response);
	}
	
	protected void doPost(HttpServletRequest request, HttpServletResponse response)
		throws ServletException, IOException {
		// リクエストパラメータを取得する
		request.setCharacterEncoding("UTF-8");
		String name = request.getParameter("name");
		String pw = request.getParameter("password");
		
		// ログイン処理を行う
		UserDao uDao = new UserDao();
		if (request.getParameter("submit").equals("ログイン")) {
			if (uDao.isLoginOK(new User(0, name, pw, "", "", ""))) { // ログイン成功
				// ログイン情報の取得
				LoginUser loginUser = uDao.findLoginUser(name,pw);
				// セッションスコープにidとnameとpwを格納する
				HttpSession session = request.getSession();
				session.setAttribute("idnamepw", loginUser);
	
				// チャットサーブレットにリダイレクトする
				response.sendRedirect("/b4/ChatServlet");
				// ログイン情報が取得できない場合
				if(loginUser == null) {
					request.setAttribute("errorMessage", "ログインユーザー情報の取得に失敗しました");
					request.getRequestDispatcher("/WEB-INF/jsp/login.jsp").forward(request, response);
					return;
				}
					

			} else { // ログイン失敗
				request.setAttribute("errorMessage", "ニックネームまたはパスワードが間違っています");
				request.getRequestDispatcher("/WEB-INF/jsp/login.jsp").forward(request, response);
				
				return;
			}
		}
		
		// 新規登録画面へ遷移
		else if(request.getParameter("submit").equals("新規登録")) {
			// 結果ページにフォワードする
			response.sendRedirect("/b4/UserRegServlet");
		}
	} 
}