<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<!DOCTYPE html>

<html>
<head>
<meta charset="UTF-8">
<title>家事提案</title>
<link rel="stylesheet" href="css/chat.css">
</head>
<body>
<header class="header">
	<div class="title">
		<h1>ここる</h1>
		<img src="images/logout.png" class="logout-icon">
	</div>
</header>
<img src="${Iconlist.icon_path}" alt="アイコン" id="icon">
   <div id="chatArea"></div>
    <!-- ポップアップ -->
    <div id="modal" class="hidden">
        <div class="modal-content">
            <h3>何をやったか教えて！</h3>
            <div class="check-area">
                <label>
                    <input type="checkbox" value="掃除機">
                    掃除機
                </label>

                <label>
                    <input type="checkbox" value="洗濯">
                    洗濯
                </label>

                <label>
                    <input type="checkbox" value="食器洗い">
                    食器洗い
                </label>

                <label>
                    <input type="checkbox" value="料理">
                    料理
                </label>
                <label>
                    <input type="checkbox" value="片づけ">
                    片づけ
                </label>
                <label>
                    <input type="checkbox" value="ごみまとめ">
                    ごみまとめ
                </label>
                <label>
                    <input type="checkbox" value="寝具の片付け">
                    寝具片付け
                </label>
            </div>

            <div id="close">
                <button onclick="reportHw()">報告する</button>
                <button onclick="closeModal()">閉じる</button>
            </div>
        </div>
    </div>
    <script src="/webapp/js/chat.js"></script>
</body>
</html>