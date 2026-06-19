package servlet;

import java.io.IOException;
import java.util.List;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import dao.GarbageDao;
import dto.Garbage;
import dto.LoginUser;

@WebServlet("/ReminderServlet")
public class ReminderServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;

	protected void doGet(HttpServletRequest request, HttpServletResponse response)
	throws ServletException, IOException {
		// テスト用　完成版では消す
		LoginUser loginUser = new LoginUser(1,"ueda","password");
		HttpSession session = request.getSession();
		session.setAttribute("idnamepw", loginUser);
		// ここまで
		/* テスト時コメントアウト
		 * LoginUser loginUser = (LoginUser)session.getAttribute("idnamepw");
		*/
		
		int user_id = loginUser.getId();
			
		//"${GarbageDate}"を作成し、リクエストスコープに格納する
		//user_idを使って、garbageテーブルから、データを取得する
		GarbageDao gdao = new GarbageDao();		// DAOをインスタンス化
		List<Garbage> gb = gdao.select(user_id);		// GarbageDAOのselectメソッドでuser_idのGarbageデータを取得

		// リクエストスコープに「GarbageList」と名付けて格納する
		request.setAttribute("GarbageList",gb);
		// リマインダーページにフォワードする
		RequestDispatcher dispatcher = request.getRequestDispatcher("/WEB-INF/jsp/reminder.jsp");
		dispatcher.forward(request, response);
				
	}
}