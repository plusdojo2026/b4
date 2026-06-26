package servlet;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import dao.GarbageDao;
import dao.usersettingDao;
import dto.Garbage;
import dto.LoginUser;

@WebServlet("/SettingServlet")
public class SettingServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;

    protected void doGet(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        request.getRequestDispatcher("/WEB-INF/jsp/setting.jsp").forward(request, response);
    }

    protected void doPost(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {

        request.setCharacterEncoding("UTF-8");
        
        String action = request.getParameter("action");
        
        System.out.println("--- ユーザー設定 doPost 開始 ---");
        System.out.println("押されたボタン: " + action);
        
        // アイコンテスト用 完成版では消す
        LoginUser loginUser = new LoginUser(1, "ueda", "password");
        HttpSession session = request.getSession();
        session.setAttribute("idnamepw", loginUser);
        // ここまで
        int userId = loginUser.getUserId();

        if ("profile".equals(action)) {
            int iconId = Integer.parseInt(request.getParameter("iconName"));
            String userName = request.getParameter("userName");
            String childCountStr = request.getParameter("childCount");
                       
            System.out.println("[プロフィールデータ受信]");
            System.out.println("選択されたアイコン: " + iconId); 
            System.out.println("ユーザー名: " + userName);
            System.out.println("子どもの人数: " + childCountStr);
            
            usersettingDao udao = new usersettingDao();
            boolean success = false;
            
            // データがあれば上書き、なければ挿入
            if (udao.userSearch(userId)) {
                success = udao.updateSetting(userId, iconId);
                System.out.println("既存のユーザー設定をUPDATEしました。");
            } else {
                success = udao.insertSetting(userId, iconId);
                System.out.println("新規のユーザー設定をINSERTしました。");
            }
            
            if (success) {
                request.setAttribute("message", "プロフィールを保存しました。");
            } else {
                request.setAttribute("message", "プロフィールの保存に失敗しました。");
            }
   
        } else if ("garbage".equals(action)) {
            String garbageName = request.getParameter("garbage_name");
            String garbageDay = request.getParameter("garbage_day");
            
            System.out.println("[ゴミ捨てデータ受信]");
            System.out.println("ゴミ分類名: " + garbageName);
            System.out.println("ゴミ捨て曜日: " + garbageDay);
            
            GarbageDao gdao = new GarbageDao();
            
            // DTOsを生成して引き渡す
            gdao.insert(new Garbage(userId, garbageDay, garbageName)); 
            
            request.setAttribute("message", "ゴミ捨て設定を保存しました。");
        }
        
        System.out.println("--------------------------------");
        
        request.getRequestDispatcher("/WEB-INF/jsp/setting.jsp").forward(request, response);
    }
}