package servlet;

import java.io.IOException;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import dao.IconDao;
import dto.Icon;

@WebServlet("/TestServlet")
public class TestServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;

	protected void doGet(HttpServletRequest request, HttpServletResponse response)
		throws ServletException, IOException {
		//"${Iconlist}"を作成し、リクエストスコープに格納する
				int icon_id = 1;							// LoginUserのgetIdメソッドでidを取得しSring型に
				
				//icon_idを使って、iconsテーブルから、データを取得する
				IconDao idao = new IconDao();		// DAOをインスタンス化
				Icon ic = idao.select(icon_id);		// IconDAOのselectメソッドでicon_idのIconデータを取得

				// リクエストスコープに「Iconlist」と名付けて格納する
				request.setAttribute("Iconlist",ic);
				
				
		// テストページにフォワードする
				RequestDispatcher dispatcher = request.getRequestDispatcher("/WEB-INF/jsp/test.jsp");
				dispatcher.forward(request, response);
	}
	
	protected void doPost(HttpServletRequest request, HttpServletResponse response)
		throws ServletException, IOException {
		// リクエストパラメータを取得する
		request.setCharacterEncoding("UTF-8");
		String icpsth = request.getParameter("icon_path");
		
		// 結果ページにフォワードする
				RequestDispatcher dispatcher = request.getRequestDispatcher("/WEB-INF/jsp/test.jsp");
				dispatcher.forward(request, response);
	}
		
}