/**
 * 
 */

let state = 0;
let remainTime = 0;
let currentTaskTime = 0;
let suggestionViewState = null;
let nextwork = null;

// メッセージ追加
function addMessage(message, user) {


	if (user) {
		const row = document.createElement("div");
		const iconPath = document.getElementById("icon").src;
		row.className = "user-row";

		row.innerHTML = `
             
            <div class="user">${message}</div>
            <img src="${iconPath}" class="user-icon">
            `;

		document.getElementById("chatArea").appendChild(row)
	} else {
		const row = document.createElement("div");
		row.className = "bot-row";
		row.innerHTML = `
            <img src="/b4/img/penguin.png" class="bot-icon">
            <div class="bot">${message}</div>
        `;
		document.getElementById("chatArea").appendChild(row);
	}


	// 一番下までスクロール
	document.getElementById("chatArea").scrollTop =
		document.getElementById("chatArea").scrollHeight;
}


// ボタンをchatArea内に表示
function showButtons() {
	const existing = document.getElementById("buttons");
	if (existing) existing.remove();

	//家事やったかの選択ボタン
	const div = document.createElement("div");
	div.id = "buttons";

	const yes = document.createElement("button");
	yes.textContent = "やったよ";
	yes.onclick = () => answer("やったよ！");

	const no = document.createElement("button");
	no.textContent = "これから";
	no.onclick = () => answer("これからだよ！");

	div.appendChild(yes);
	div.appendChild(no);
	document.getElementById("chatArea").appendChild(div);

	document.getElementById("chatArea").scrollTop =
		document.getElementById("chatArea").scrollHeight;
}

//報告するボタン
function reportButtons() {
	const existing = document.getElementById("buttons1");
	if (existing) existing.remove();

	//家事やったかの選択ボタン
	const div = document.createElement("div");
	div.id = "buttons";

	const report = document.createElement("button");
	report.textContent = "報告";
	report.onclick = () => openModal();

	div.appendChild(report);
	document.getElementById("chatArea").appendChild(div);

	document.getElementById("chatArea").scrollTop =
		document.getElementById("chatArea").scrollHeight;
}

//報告完了ボタン
function reportCompleteButtons() {
	const existing = document.getElementById("buttons2");
	if (existing) existing.remove();

	//家事やったかの選択ボタン
	const div = document.createElement("div");
	div.id = "buttons";

	const report = document.createElement("button");
	report.textContent = "報告する";
	report.onclick = () => answer("○○と○○をやったよ！");

	div.appendChild(report);
	document.getElementById("chatArea").appendChild(div);

	document.getElementById("chatArea").scrollTop =
		document.getElementById("chatArea").scrollHeight;
}



//時間指定かおまかせか
function showTimeselect() {
	const existing = document.getElementById("buttons3");
	if (existing) existing.remove();

	const div = document.createElement("div");
	div.id = "buttons1";

	const sitei = document.createElement("button");
	sitei.textContent = "時間指定";
	sitei.onclick = () => openTimeModal();

	const omakase = document.createElement("button");
	omakase.textContent = "おまかせ";
	omakase.onclick = () => answer("おまかせする！");

	div.appendChild(sitei);
	div.appendChild(omakase);
	document.getElementById("chatArea").appendChild(div);

	document.getElementById("chatArea").scrollTop =
		document.getElementById("chatArea").scrollHeight;
}

//ポップアップを表示するためのjavascript
function openModal() {
	document.getElementById("modal")
		.classList.remove("hidden");
}

function openTimeModal() {
	// const buttons1 = document.getElementById("buttons1");
	// if (buttons1) buttons1.remove();
	document.getElementById("timeModal")
		.classList.remove("hidden");
}


function closeModal() {
	document.getElementById("modal")
		.classList.add("hidden");
}

function closeTimeModal() {
	document.getElementById("timeModal")
		.classList.add("hidden");
}

//時間指定した後の処理
function decideTime() {

	const btn2 = document.getElementById("buttons1");
	if (btn2) btn2.remove();

    //console.log("decideTime実行");

	const time = document.getElementById("timeSelect").value;
   
	
	addMessage(time + "分間で家事をする！", true);

	//ユーザーの所要時間
	remainTime = time;

	addMessage(time + "分間だね！", false);

	closeTimeModal();

//	fetch("SuggestServlet", {
//		method: "POST",
//		headers: {
//			"Content-Type": "application/x-www-form-urlencoded"
//		},
//		body: "action=decideTime&time=" + time
//	})
//		.then(response => response.text())
//		.then(data => {
//			console.log(data);
//		})
//		.catch(error => {
//			console.error("エラー:", error);
//		})
//		;

	// ここで家事提案処理
	suggestionViewState = {
		currentIndex: 0,
//		status:"PREPARE",
//		remainingMinutes:5,
//	

		suggestions: [
			{
				activityId: 1,
				category: "HOUSEWORK",
				title: "テーブルを拭く",
				requiredTime: 5,
				message: "5分だけテーブルを整えましょう"
			},
			{
				activityId: 8,
				category: "REST",
				title: "暖かい飲み物を飲む",
				requiredTime: 10,
				message: "少し休憩しましょう"
			}
		]
	};

	const housework = suggestionViewState.suggestions[
		suggestionViewState.currentIndex
	];
	addMessage("まずは" + housework.title + "をやろう！", false
	);
	addMessage("終わったら教えてね！", false);
	
//	fetch("SuggestServlet", {
//		method: "POST",
//		headers: {
//			"Content-Type":
//				"application/x-www-form-urlencoded"
//		},
//		body:
//			"action=start" +
//			"&mode=TIME" +
//			"&time=" + encodeURIComponent(time)
//	})
//		.then(response => response.json())
//		.then(data => {
//
//			suggestionViewState = data;
//			const housework = suggestionViewState.suggestions[0];
//
//			addMessage(housework.message, false);
//			addMessage("終わったら教えてね！", false);
//		});
//
	suggestionButtons();
	state = 5;

	document.getElementById("chatArea").scrollTop = document.getElementById("chatArea").scrollHeight;
}

//おまかせの処理
function decideHw() {
	suggestionViewState = {
		currentIndex: 0,
//		status:"PREPARE",
//		remainingMinutes:5,

		suggestions: [
			{
				activityId: 1,
				category: "HOUSEWORK",
				title: "テーブルを拭く",
				requiredTime: 5,
				message: "5分だけテーブルを整えましょう"
			},
			{
				activityId: 8,
				category: "REST",
				title: "温かい飲み物を飲む",
				requiredTime: 10,
				message: "少し休憩しましょう"
			}
		]
	}

	const housework = suggestionViewState.suggestions[
		suggestionViewState.currentIndex
	];

	addMessage(housework.message, false
	);
	addMessage("終わったら教えてね！", false);
	
//	fetch("SuggestServlet", {
//		method: "POST",
//		headers: {
//			"Content-Type":
//				"application/x-www-form-urlencoded"
//		},
//		body:
//			"action=start" +
//			"&mode=AUTO" 
//	})
//		.then(response => response.json())
//		.then(data => {
//
//			suggestionViewState = data;
//			const housework = suggestionViewState.suggestions[0];
//
//			addMessage(housework.message, false);
//			addMessage("終わったら教えてね！", false);
//		});


	suggestionButtons();
	state = 6;

	document.getElementById("chatArea").scrollTop = document.getElementById("chatArea").scrollHeight;
}

function reportHw() {

	const checked =
		document.querySelectorAll(
			'#modal input[type="checkbox"]:checked'
		);

	const btn1 = document.getElementById("buttons");
	if (btn1) btn1.remove();


	//let result = [];
	const activityIds = [];
    const activityNames = [];

	checked.forEach(item => {
		//result.push(item.value);
		activityIds.push(item.value);
        activityNames.push(item.dataset.name);
	});

	if (activityIds.length === 0) {
		alert("家事を選択してください");
		return;
	}

	// Servletへ送るデータ作成
	const params = new URLSearchParams();

	params.append("action", "checkActivity");

//	result.forEach(activityId => {
//		params.append("activityId", activityId);
//	});

	activityIds.forEach(activityId => {
		params.append("activityId", activityId);
	});

    //ReportServletへデータを渡す
	fetch("ReportServlet", {
		method: "POST",
		headers: {
			"Content-Type":
				"application/x-www-form-urlencoded"
		},
		body: params.toString()
	})
	.then(response => response.text())
	.then(data => {

		console.log(data);

	addMessage(
		activityNames.join("、") + "をやったよ！",
		true
	);

	addMessage("ok！頑張ったね！", false);
	addMessage("時間指定する？お任せにする？", false);
	showTimeselect();

	state = 3;
	closeModal();

	document.getElementById("chatArea").scrollTop = document.getElementById("chatArea").scrollHeight;
})
	.catch(error => {
		console.error(error);
		alert("履歴の登録に失敗しました");
	});
}

//提案時表示ボタン
function suggestionButtons() {
	const existing = document.getElementById("buttons5");
	if (existing) existing.remove();

	const div = document.createElement("div");
	div.id = "buttons5";

	const resuggestion = document.createElement("button");
	resuggestion.textContent = "再提案";
	resuggestion.onclick = () => answer("再提案！");

	const finish = document.createElement("button");
	finish.textContent = "終わったよ";
	finish.onclick = () => answer("終わったよ！");

	div.appendChild(resuggestion);
	div.appendChild(finish);
	document.getElementById("chatArea").appendChild(div);

	document.getElementById("chatArea").scrollTop =
		document.getElementById("chatArea").scrollHeight;
}

// 最初の質問
addMessage("お疲れ様！何か家事やった？", false);
showButtons();

function answer(value) {

	// ボタンを消さない
	const buttons = document.getElementById("buttons");
	if (buttons) buttons.remove();

	// const buttons3 = document.getElementById("buttons3");
	// if (buttons3) buttons.remove();
	// const buttons5 = document.getElementById("buttons5");
	// if (buttons5) buttons.remove();
	addMessage(value, true);
	switch (state) {
		case 0: //家事をやったかの選択
			if (value == "やったよ！") {
				addMessage("もうやったんだ、すごい！何やったか教えて！", false);
				state = 1;
				reportButtons();
			} else {
				addMessage("OK！これから頑張ろう！", false);
				showTimeselect();
				state = 3;
			}
			break;

		case 1: //報告ボタン表示(6/18変更)
			if (value == "報告") {
				openModal();
			}
			break;

		case 2: //やった家事の報告
			if (value == "報告する！") {
				addMessage("OK！頑張ったね！", false);
			}

		case 3: //時間指定かお任せの選択
			if (value == "時間指定する！") {
				addMessage("時間指定", false);
				state = 4;
				specifiedButtons();
			} else {
				const btn2 = document.getElementById("buttons1");
				if (btn2) btn2.remove();
				addMessage("おまかせだね！", false);
				//addMessage("この家事をやろう！", false);
				//suggestionButtons();
				//state = 5;
				decideHw();
			}
			break;

		//case 4: //指定するボタン
		//    if (value == "○分間で家事をする！") {
		//        addMessage("○分間だね！", false);
		//        addMessage("この家事をやろう！", false);
		//        suggestionButtons();
		//        state = 5;
		//    }
		//    break;

		case 5: //時間指定の再提案と終わったよ
			console.log(value);
			if (value == "再提案！") {
				console.log(value);

				suggestionViewState.currentIndex++;

				if (suggestionViewState.currentIndex < suggestionViewState.suggestions.length){
					nextwork = suggestionViewState.suggestions[suggestionViewState.currentIndex];

					addMessage("じゃあ、" + nextwork.title + "はどう？", false);
					addMessage("終わったら教えてね！", false);
					suggestionButtons();
					state = 5;
				} else {
					addMessage("お疲れ様！家事はここまでにしよう！", false);
					const btn1 = document.getElementById("buttons");
					if (btn1) btn1.remove();

					const btn2 = document.getElementById("buttons1");
					if (btn2) btn2.remove();

					const btn5 = document.getElementById("buttons5");
					if (btn5) btn5.remove();
				}
			} else if (value == "終わったよ！") {

                //終わったよボタンが押された時に活動IDをjavaに送信
				const currentWork =suggestionViewState.suggestions[suggestionViewState.currentIndex];

				console.log("完了した活動ID:", currentWork.activityId);
				
				//reportServletに通信する
				fetch("SuggestServlet", {
					method: "POST",
					headers: {
						"Content-Type": "application/x-www-form-urlencoded"
					},
					//activityIdを取得
					body:
						"action=complete" +
						"&activityId=" +
						currentWork.activityId  
				})
				//サーバから返ってきたデータを文字列として受け取る
					.then(response => response.text())
				//受け取ったデータをコンソールに表示
					.then(data => {console.log(data);});

				suggestionViewState.currentIndex++;

				if (suggestionViewState.currentIndex < suggestionViewState.suggestions.length) {
					nextwork = suggestionViewState.suggestions[suggestionViewState.currentIndex];

                    //addMessage("残り時間は〇分だよ！", false);
					addMessage("次は" + nextwork.message, false);
					addMessage("終わったら教えてね！");
					suggestionButtons();
					state = 5;
				}
				else {
					addMessage("お疲れ様！家事はここまでにしよう！", false);
					const btn1 = document.getElementById("buttons");
					if (btn1) btn1.remove();

					const btn2 = document.getElementById("buttons1");
					if (btn2) btn2.remove();

					const btn5 = document.getElementById("buttons5");
					if (btn5) btn5.remove();
				}
			}

			break;

		case 6: //おまかせ→再提案/終わったよ
			if (value == "再提案！") {
				console.log(value);

				suggestionViewState.currentIndex++;

				if (suggestionViewState.currentIndex < suggestionViewState.suggestions.length) {

					nextwork = suggestionViewState.suggestions[suggestionViewState.currentIndex];

					addMessage("次は" + nextwork.title + "はどう？", false);
					addMessage("終わったら教えてね！", false);
					suggestionButtons();
					state = 6;
				} else {
					addMessage("お疲れ様！家事はここまでにしよう！", false);
					const btn1 = document.getElementById("buttons");
					if (btn1) btn1.remove();

					const btn2 = document.getElementById("buttons1");
					if (btn2) btn2.remove();

					const btn5 = document.getElementById("buttons5");
					if (btn5) btn5.remove();
				}

			} else if (value == "終わったよ！") {
				const currentWork = suggestionViewState.suggestions[suggestionViewState.currentIndex];

				console.log("完了した活動ID:", currentWork.activityId);

				//reportServletに通信する
				fetch("ChatServlet", {
					method: "POST",
					headers: {
						"Content-Type": "application/x-www-form-urlencoded"
					},
					//activityIdを取得
					body:
						"action=complete" +
						"&activityId=" +
						currentWork.activityId
				})
					//サーバから返ってきたデータを文字列として受け取る
					.then(response => response.text())
					//受け取ったデータをコンソールに表示
					.then(data => { console.log(data); });
				suggestionViewState.currentIndex++;

				if (suggestionViewState.currentIndex < suggestionViewState.suggestions.length) {
					nextwork = suggestionViewState.suggestions[suggestionViewState.currentIndex];

                    
					addMessage("次は、" + nextwork.message, false);
					addMessage("終わったら教えてね！", false);
					suggestionButtons();
					state = 6;

				} else {
					addMessage("お疲れ様！家事はここまでにしよう！", false);
					const btn1 = document.getElementById("buttons");
					if (btn1) btn1.remove();

					const btn2 = document.getElementById("buttons1");
					if (btn2) btn2.remove();

					const btn5 = document.getElementById("buttons5");
					if (btn5) btn5.remove();
				}
			}
			break;

	}
}