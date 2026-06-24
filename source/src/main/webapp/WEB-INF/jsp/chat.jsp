<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<!DOCTYPE html>

<html>
<head>
<meta charset="UTF-8">
<meta name="viewport" content="width=device-width, initial-scale=1">
<title>家事提案</title>
<link rel="stylesheet" href="/b4/css/chat.css">
</head>
<body>
<header class="header">
	<div class="title">
		<h1>ここる</h1>
		<a href="/b4/LogoutServlet" id="logout"><img src="/b4/img/logout.png" class="logout-icon" ></a>
	</div>
</header>

<img src="${Iconlist.icon_path}" id="icon" style="display:none;" >
   <div id="chatArea"></div>
   
    <!-- やった家事ポップアップ -->
    <div id="modal" class="hidden">
        <div class="modal-content">
            <h3>何をやったか教えて！</h3>
            <div class="check-area">
                <label>
                    <input type="checkbox" value="1" data-name="掃除機">
                    掃除機
                </label>

                <label>
                    <input type="checkbox" value="2" data-name="洗濯">
                    洗濯
                </label>

                <label>
                    <input type="checkbox" value="3" data-name="食器洗い">
                    食器洗い
                </label>

                <label>
                    <input type="checkbox" value="4" data-name="料理">
                    料理
                </label>
                <label>
                    <input type="checkbox" value="5" data-name="片づけ">
                    片づけ
                </label>
                <label>
                    <input type="checkbox" value="6" data-name="ごみまとめ">
                    ごみまとめ
                </label>
                <label>
                    <input type="checkbox" value="7" data-name="寝具片付け">
                    寝具片付け
                </label>
            </div>

            <div id="close">
                <button onclick="reportHw()">報告する</button>
                <button onclick="closeModal()">閉じる</button>
            </div>
        </div>
    </div>
    
    <!-- 時間指定ポップアップ」 -->
    <div id="timeModal" class="hidden">
        <div class="modal-content">
            <h3>何分くらい頑張る？</h3>

            <select id="timeSelect">
<!--                 <option value="5">5分</option> -->
                <option value="10">10分</option>
                <option value="15">15分</option>
                <option value="30">30分</option>
                <option value="45">45分</option>
                <option value="60">60分</option>
            </select>

            <div id="decide">
                <button onclick="decideTime()">決定</button>
                <button onclick="closeTimeModal()">閉じる</button>
            </div>
        </div>
    </div>
    
    
    
    <script src="/b4/js/chat.js"> </script>
    
</body>
</html>