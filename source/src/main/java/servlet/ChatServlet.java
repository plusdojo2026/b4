package servlet;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import dao.IconDao;
import dto.Icon;
import dto.LoginUser;
import dto.Message;



/**
 * Servlet implementation class RegistServlet
 */
@WebServlet("/ChatServlet")
public class ChatServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;
    
    /**
     * @see HttpServlet#HttpServlet()
     */
	protected void doGet(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
//		// もしもログインしていなかったらログインサーブレットにリダイレクトする
//		HttpSession session = request.getSession();
//		if (session.getAttribute("id") == null) {
//			response.sendRedirect("/LoginServlet");
//			return;
//		}
		
		// アイコンテスト用　完成版では消す
		LoginUser loginUser = new LoginUser(1,"ueda","password");
		HttpSession session = request.getSession();
		session.setAttribute("idnamepw", loginUser);
		// ここまで
		/* テスト時コメントアウト
		 * LoginUser loginUser = (LoginUser)session.getAttribute("idnamepw");
		 */
		int id = loginUser.getId();
		
		//"${Iconlist}"を作成し、リクエストスコープに格納する
		
		//icon_idを使って、iconsテーブルから、データを取得する
		IconDao idao = new IconDao();		// DAOをインスタンス化
		Icon ic = idao.select(id);		// IconDAOのselectメソッドでicon_idのIconデータを取得

		// リクエストスコープに「Iconlist」と名付けて格納する
		request.setAttribute("Iconlist",ic);

		//チャット履歴を保存するためのリスト
		List<Message> messageList = new ArrayList<Message>();
		
		//最初の画面
		messageList.add(new Message("bot", "お疲れ様！何か家事やった？"));
		
		/* テスト時コメントアウト
		 * //セッションを取得		 
		HttpSession session = request.getSession();
		*/
		//チャット履歴を保存
		session.setAttribute("messageList", messageList);
		
		// チャットページにフォワードする
		RequestDispatcher dispatcher = request.getRequestDispatcher("/WEB-INF/jsp/chat.jsp");
		dispatcher.forward(request, response);
		

    }
		
}
