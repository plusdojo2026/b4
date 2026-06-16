package servlet;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

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
        
        if ("profile".equals(action)) {
            // アイコン名を受け取る
            String iconName = request.getParameter("iconName");
            String userName = request.getParameter("userName");
            String childCountStr = request.getParameter("childCount");
            
            System.out.println("[プロフィールデータ受信]");
            System.out.println("選択されたアイコン: " + iconName); // 
            System.out.println("ユーザー名: " + userName);
            System.out.println("子どもの人数: " + childCountStr);
            
            request.setAttribute("message", "プロフィールを保存しました。");
   
        } else if ("garbage".equals(action)) {
            String garbageName = request.getParameter("garbage_name");
            String garbageDay = request.getParameter("garbage_day");
            
            System.out.println("[ゴミ捨てデータ受信]");
            System.out.println("ゴミ分類名: " + garbageName);
            System.out.println("ゴミ捨て曜日: " + garbageDay);
            
            request.setAttribute("message", "ゴミ捨て設定を保存しました。");
        }
        
        System.out.println("--------------------------------");
        
        request.getRequestDispatcher("/WEB-INF/jsp/setting.jsp").forward(request, response);
    }
}