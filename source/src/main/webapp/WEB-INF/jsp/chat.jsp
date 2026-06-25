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
<div class="page-frame">
<jsp:include page="sidebar.jspf" flush="true|false" />

<header class="header">
	<div class="title">
	
<!-- サイドバー -->
<%-- <jsp:include page="sidebar.jspf" flush="true|false" /> --%>
<img src="/b4/img/sidebar.png" id="toggleBtn">
<link rel="stylesheet" href="/b4/css/sidebar.css">
<script src="/b4/js/sidebar.js"> </script>

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

			<details>
				<summary>掃除</summary>

				<details>
					<summary>リビング</summary>

					<label> <input type="checkbox" value="1"
						data-name="リビング掃除（掃除機）"> 掃除機
					</label>

					<label> <input type="checkbox" value="2"
						data-name="リビング掃除（フローリングワイパー）"> フローリングワイパー
					</label>
				</details>

				<details>
					<summary>キッチン</summary>

					<label> <input type="checkbox" value="3"
						data-name="キッチン掃除（掃除機）"> 掃除機
					</label>

					<label> <input type="checkbox" value="7"
						data-name="キッチン掃除（フローリングワイパー）"> フローリングワイパー
					</label>

					<label> <input type="checkbox" value="10"
						data-name="キッチン掃除（粘着ローラー）"> 粘着ローラー
					</label>
				</details>

				<details>
					<summary>洗面所</summary>

					<label> <input type="checkbox" value="4"
						data-name="洗面所掃除（掃除機）"> 掃除機
					</label>

					<label> <input type="checkbox" value="8"
						data-name="洗面所掃除（フローリングワイパー）"> フローリングワイパー
					</label>
				</details>

				<details>
					<summary>部屋</summary>

					<label> <input type="checkbox" value="5"
						data-name="部屋掃除（掃除機）"> 掃除機
					</label>

					<label> <input type="checkbox" value="9"
						data-name="部屋掃除（フローリングワイパー）"> フローリングワイパー
					</label>

					<label> <input type="checkbox" value="12"
						data-name="部屋掃除（粘着ローラー）"> 粘着ローラー
					</label>
				</details>

				<details>
					<summary>トイレ</summary>

					<label> <input type="checkbox" value="13"
						data-name="トイレ掃除（掃除機）"> 掃除機
					</label>

					<label> <input type="checkbox" value="11"
						data-name="トイレ掃除（フローリングワイパー）"> フローリングワイパー
					</label>

					<label> <input type="checkbox" value="14"
						data-name="トイレ掃除（粘着ローラー）"> 粘着ローラー
					</label>
				</details>

				<div class="cleaning-item">
					<label> <input type="checkbox" value="6" data-name="風呂掃除">
						風呂掃除
					</label>
				</div>
			</details>

			<details>
				<summary>片付け</summary>

				<label><input type="checkbox" value="15" data-name="おもちゃ片付け">おもちゃ</label>
				<br>
				<label><input type="checkbox" value="16" data-name="机の上片付け">机の上</label>
				<br>
				<label><input type="checkbox" value="17" data-name="服の片付け">服</label>
				<br>
				<label><input type="checkbox" value="18" data-name="キッチン片付け">キッチン</label>
				<br>
				<label><input type="checkbox" value="19" data-name="洗面所片付け">洗面所</label>
				<br>
				<label><input type="checkbox" value="20" data-name="部屋片付け">部屋</label>
				<br>
				<label><input type="checkbox" value="21" data-name="棚片付け">棚</label>
			</details>

			<details>
				<summary>洗濯</summary>

				<label><input type="checkbox" value="22"
					data-name="洗濯機回す・洗濯物干す">回す・干す</label>
				<br>
				<label><input type="checkbox" value="23" data-name="洗濯物取り込む">取り込む</label>
				<br>
				<label><input type="checkbox" value="24" data-name="洗濯物たたむ">たたむ</label>
			</details>

			<details>
				<summary>料理・買い物</summary>

				<label><input type="checkbox" value="25" data-name="料理">料理</label>
				<br>
				<label><input type="checkbox" value="27" data-name="買い出し">買い出し</label>
			</details>

			<details>
				<summary>その他</summary>
				<label><input type="checkbox" value="31" data-name="ゴミまとめ">ゴミまとめ</label>
				<br>
				<label><input type="checkbox" value="32" data-name="ゴミ出し">ゴミ出し</label>
			</details>

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
    
    
    
    <script src="${pageContext.request.contextPath}/js/chat.js"> </script>
</div>
</body>
</html>