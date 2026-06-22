package servlet;
import java.io.IOException;

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
		
		// アイコンテスト用 完成版では消す
		LoginUser loginUser = new LoginUser(1,"ueda","password");
		HttpSession session = request.getSession();
		session.setAttribute("idnamepw", loginUser);
		// ここまで
		/* テスト時コメントアウト
		 * LoginUser loginUser = (LoginUser)session.getAttribute("idnamepw");
		 */
		int id = loginUser.getUserId();
		
		//"${Iconlist}"を作成し、リクエストスコープに格納する
		
		//icon_idを使って、iconsテーブルから、データを取得する
		IconDao idao = new IconDao();		// DAOをインスタンス化
		Icon ic = idao.select(id);		// IconDAOのselectメソッドでicon_idのIconデータを取得

		// リクエストスコープに「Iconlist」と名付けて格納する
		request.setAttribute("Iconlist",ic);

		
		// チャットページにフォワードする
		RequestDispatcher dispatcher = request.getRequestDispatcher("/WEB-INF/jsp/chat.jsp");
		dispatcher.forward(request, response);
		

    }
	
	protected void doPost(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		
		request.setCharacterEncoding("UTF-8");
		
		String action = request.getParameter("action");
		
		//家事をやったか確認
		if("checkActivity".equals(action)) {
			int activityId = Integer.parseInt(request.getParameter("activityId"));
			
			//DAOで実施履歴登録
		}
        // 時間を選択して家事提案
        else if ("decideTime".equals(action)) {

            int time = Integer.parseInt(
                    request.getParameter("time"));

            // DAOで時間に合う家事取得
            // ActivityDAO.selectByTime(time);

            response.setContentType("application/json");
            response.setCharacterEncoding("UTF-8");

            // Gsonで提案リストを返す
            // response.getWriter().write(gson.toJson(list));
        }

        // 提案された家事が終了
        else if ("complete".equals(action)) {

            int activityId = Integer.parseInt(
                    request.getParameter("activityId"));

            // DAOで完了実績登録
            // HistoryDAO.insertResult(...);

        }
	    //int time = Integer.parseInt(request.getParameter("time"));
	}
		
}
