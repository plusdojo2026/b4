<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>
<head>
<meta charset="UTF-8">
<title>ユーザー設定</title>
</head>
<body>

    <div style="border: 1px solid #000; margin: 10px; padding: 10px;">
        <h3>プロフィール</h3>
        <form action="SettingServlet" method="POST">
            
            <p>現在のアイコン：
                <img id="displayIcon" src="images/icon1.png" width="50" height="50" style="border: 1px solid #999;">
            </p>
            <input type="hidden" name="iconName" id="hiddenIconName" value="icon1.png">
            
            <button type="button" onclick="document.getElementById('modal').style.display='block'">変更</button>

            <div id="modal" style="display:none; position:fixed; top:10%; left:10%; width:80%; background:white; border:2px solid #000; padding:20px; z-index:10;">
                <p>アイコンを選んでください</p>
                <% for(int i=1; i<=7; i++) { %>
                    <label style="display:inline-block; margin:5px;">
                        <input type="radio" name="tempIcon" value="icon<%=i%>.png" onclick="selectIcon('icon<%=i%>.png')">
                        <img src="images/icon<%=i%>.png" width="40" height="40">
                    </label>
                <% } %>
                <br><br>
                <button type="button" onclick="document.getElementById('modal').style.display='none'">閉じる</button>
            </div>

            <p>
                <label>名前：</label>
                <input type="text" name="userName" placeholder="名前を入力してください">
            </p>
            <p>
                <label>子どもの人数：</label>
                <input type="number" name="childCount" value="0">
            </p>
            <button type="submit" name="action" value="profile">登録</button>
        </form>
    </div>

    <div style="border: 1px solid #000; margin: 10px; padding: 10px;">
        <h3>ゴミ捨て設定</h3>
        <form action="SettingServlet" method="POST">
            <p>
                <label>ゴミ分類名：</label>
                <select name="garbage_name">
                    <option value="可燃ゴミ">可燃ゴミ</option>
                    <option value="不燃ゴミ">不燃ゴミ</option>
                    <option value="資源ゴミ">資源ゴミ</option>
                    <option value="ビン、缶、ペットボトル">ビン、缶、ペットボトル</option>
                    <option value="その他">その他</option>
                </select>
            </p>
            <p>
                <label>曜日：</label>
                <select name="garbage_day">
                    <option value="月曜日">月曜日</option>
                    <option value="火曜日">火曜日</option>
                    <option value="水曜日">水曜日</option>
                    <option value="木曜日">木曜日</option>
                    <option value="金曜日">金曜日</option>
                    <option value="土曜日">土曜日</option>
                    <option value="日曜日">日曜日</option>
                </select>
            </p>
            <button type="submit" name="action" value="garbage">登録</button>
        </form>
    </div>

    <% if(request.getAttribute("message") != null) { %>
        <p style="color: green; font-weight: bold;"><%= request.getAttribute("message") %></p>
    <% } %>

    <script>
        function selectIcon(name) {
            document.getElementById('displayIcon').src = 'images/' + name;
            document.getElementById('hiddenIconName').value = name;
        }
    </script>

</body>
</html>