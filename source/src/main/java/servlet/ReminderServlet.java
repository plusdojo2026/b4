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
import dao.TodoDao;
import dto.Garbage;
import dto.LoginUser;
import dto.Todo;

@WebServlet("/ReminderServlet")
public class ReminderServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;

	protected void doGet(HttpServletRequest request, HttpServletResponse response)
	throws ServletException, IOException {
		/*// テスト用　完成版では消す
		LoginUser loginUser = new LoginUser(1,"ueda","password");
		HttpSession session = request.getSession();
		session.setAttribute("idnamepw", loginUser);
		// ここまで*/
		
		  // もしもログインしていなかったらログインサーブレットにリダイレクトする
		HttpSession session = request.getSession();
		LoginUser loginUser = (LoginUser)session.getAttribute("idnamepw");
		if (loginUser == null) {
			response.sendRedirect("/b4/LoginServlet");
			return;
		}
		
		int userId = loginUser.getUserId();
			
		//"${GarbageDate}"を作成し、リクエストスコープに格納する
		//user_idを使って、garbageテーブルから、データを取得する
		GarbageDao gdao = new GarbageDao();		// DAOをインスタンス化
		List<Garbage> gb = gdao.select(userId);		// GarbageDAOのselectメソッドでuser_idのGarbageデータを取得
		// リクエストスコープに「GarbageList」と名付けて格納する
		request.setAttribute("GarbageList",gb);
		
		TodoDao tdao = new TodoDao();		// DAOをインスタンス化
		List<Todo> tod = tdao.select(userId);		// TodoDaoのselectメソッドでuserIdのTodoデータを取得
		// リクエストスコープに「TodoList」と名付けて格納する
		request.setAttribute("TodoList",tod);
		
		// リマインダーページにフォワードする
		RequestDispatcher dispatcher = request.getRequestDispatcher("/WEB-INF/jsp/reminder.jsp");
		dispatcher.forward(request, response);
				
	}
	
	protected void doPost(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		/*// テスト用　完成版では消す
		LoginUser loginUser = new LoginUser(1,"ueda","password");
		HttpSession session = request.getSession();
		session.setAttribute("idnamepw", loginUser);
		// ここまで*/
		
		/* // もしもログインしていなかったらログインサーブレットにリダイレクトする
		HttpSession session = request.getSession();
		if (session.getAttribute("userId") == null) {
			response.sendRedirect("/b4/LoginServlet");
			return;
		}*/
		
		// リクエストパラメータを取得する
		request.setCharacterEncoding("UTF-8");
		 HttpSession session = request.getSession();
		LoginUser user = (LoginUser)session.getAttribute("idnamepw"); 

		int userId =user.getUserId();
		String todoName = request.getParameter("todoName");	
		String todoDate = request.getParameter("todoDate");	
		
		GarbageDao gdao = new GarbageDao();		// DAOをインスタンス化
		List<Garbage> gb = gdao.select(userId);		// GarbageDAOのselectメソッドでuser_idのGarbageデータを取得
		// リクエストスコープに「GarbageList」と名付けて格納する
		request.setAttribute("GarbageList",gb);
					
		// 登録処理を行う
		TodoDao todoDao = new TodoDao();
		if (request.getParameter("id").equals("input")) {
			todoDao.insert(new Todo(userId, todoName, todoDate)); 
		}
		// 削除処理を行う
		else {
			int id = Integer.parseInt(request.getParameter("id")); 
			//idで指定したデータを削除
			todoDao.delete(id);
		}
		TodoDao tdao = new TodoDao();			// DAOをインスタンス化
		// TodoDaoのselectメソッドでuserIdのTodoデータを取得
		List<Todo> tod = tdao.select(userId);	
		// リクエストスコープに「TodoList」と名付けて格納する
		request.setAttribute("TodoList",tod);
		
		// リマインダーページにフォワードする
		RequestDispatcher dispatcher = request.getRequestDispatcher("/WEB-INF/jsp/reminder.jsp");
		dispatcher.forward(request, response);
		return;
	}
}