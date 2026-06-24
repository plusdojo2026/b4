<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>
<head>
<meta charset="UTF-8">
<title>ユーザー設定</title>
<link rel="stylesheet" href="css/setting.css">
<script src="js/setting.js" defer></script>
</head>
<body>

    <div id="sidebar" class="sidebar">
        <div class="sidebar-user">
            <div class="sidebar-user-icon">
                <img src="img/icon1.png" alt="ユーザーアイコン">
            </div>
            <div class="sidebar-username">${user.userName}</div>
        </div>

        <div class="sidebar-menu">
            <button type="button" class="menu-item">ユーザー設定 <span class="arrow">＞</span></button>
            <button type="button" class="menu-item">記録一覧 <span class="arrow">＞</span></button>
            <button type="button" class="menu-item">リマインダー <span class="arrow">＞</span></button>
        </div>
    </div>

    <div id="overlay" class="sidebar-overlay" onclick="toggleSidebar()"></div>

    <div class="header">
        <div class="menu-btn" onclick="toggleSidebar()">
            <span></span>
            <span></span>
            <span></span>
        </div>
        <h1 class="header-title">ここる</h1>
        
        <a href="LogoutServlet" class="header-logout">
            <img src="img/logout.png" alt="ログアウト">
        </a>
    </div>

    <div class="container">

        <div class="icon-container">
            <div class="icon-circle" onclick="openModal()">
                <img id="displayIcon" src="img/icon1.png" alt="ユーザーアイコン">
            </div>
        </div>

        <div class="setting-section">
            <h3>プロフィール</h3>
            <form action="SettingServlet" method="POST">
                <input type="hidden" name="iconName" id="hiddenIconName" value="1">
                
                <div class="form-group">
                    <label>名前：</label>
                    <input type="text" name="userName" value="${user.userName}" placeholder="名前を入力してください">
                </div>
                <div class="form-group">
                    <label>子どもの人数：</label>
                    <input type="number" name="childCount" value="${user.childCount}">
                </div>
                
                <div class="btn-container">
                    <button type="submit" name="action" value="profile" class="submit-btn">登録</button>
                </div>
            </form>
        </div>

        <div class="setting-section">
            <h3>ゴミ捨て設定</h3>
            <form action="SettingServlet" method="POST">
                <div class="form-group">
                    <label>ゴミ分類名：</label>
                    <select name="garbage_name">
                        <option value="可燃ゴミ" ${user.garbageName == '可燃ゴミ' ? 'selected' : ''}>可燃ゴミ</option>
                        <option value="不燃ゴミ" ${user.garbageName == '不燃ゴミ' ? 'selected' : ''}>不燃ゴミ</option>
                        <option value="資源ゴミ" ${user.garbageName == '資源ゴミ' ? 'selected' : ''}>資源ゴミ</option>
                        <option value="ビン、缶、ペットボトル" ${user.garbageName == 'ビン、缶、ペットボトル' ? 'selected' : ''}>ビン、缶、ペットボトル</option>
                        <option value="other" ${user.garbageName == 'その他' ? 'selected' : ''}>その他</option>
                    </select>
                </div>
                <div class="form-group">
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
                </div>
                
                <div class="btn-container">
                    <button type="submit" name="action" value="garbage" class="submit-btn">登録</button>
                </div>
            </form>
        </div>

        <% if(request.getAttribute("message") != null) { %>
            <p class="success-message"><%= request.getAttribute("message") %></p>
        <% } %>

    </div>

    <div id="modal" class="modal-overlay">
        <p>アイコンを選んでください</p>
        <div class="modal-icons">
            <label class="icon-label"><input type="radio" name="tempIcon" value="2" onclick="selectIcon(2, 'cat.png')"><img src="img/cat.png" width="40" height="40"></label>
            <label class="icon-label"><input type="radio" name="tempIcon" value="7" onclick="selectIcon(7, 'dinosaur.png')"><img src="img/dinosaur.png" width="40" height="40"></label>
            <label class="icon-label"><input type="radio" name="tempIcon" value="1" onclick="selectIcon(1, 'dog.png')"><img src="img/dog.png" width="40" height="40"></label>
            <label class="icon-label"><input type="radio" name="tempIcon" value="6" onclick="selectIcon(6, 'dolphin.png')"><img src="img/dolphin.png" width="40" height="40"></label>
            <label class="icon-label"><input type="radio" name="tempIcon" value="4" onclick="selectIcon(4, 'gorilla.png')"><img src="img/gorilla.png" width="40" height="40"></label>
            <label class="icon-label"><input type="radio" name="tempIcon" value="3" onclick="selectIcon(3, 'rabbit.png')"><img src="img/rabbit.png" width="40" height="40"></label>
            <label class="icon-label"><input type="radio" name="tempIcon" value="5" onclick="selectIcon(5, 'whiteBear.png')"><img src="img/whiteBear.png" width="40" height="40"></label>
        </div>
        <br>
        <button type="button" onclick="closeModal()">閉じる</button>
    </div>

</body>
</html>