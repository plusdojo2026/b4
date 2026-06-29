let state = 0;
let remainTime = 0;
let currentTaskTime = 0;
let suggestionViewState = null;
let nextwork = null;
let isSubmitting = false;

// アプリケーションのパス
const CONTEXT_PATH =
	document.body.dataset.contextPath || "";

// チャットを一番下まで移動
function scrollChatArea() {
	const chatArea =document.getElementById("chatArea");

	chatArea.scrollTop =chatArea.scrollHeight;
}

// 指定した要素を削除
function removeElement(elementId) {
	const element =document.getElementById(elementId);

	if (element) {
		element.remove();
	}
}

// ユーザーアイコンのパスを取得
function getUserIconPath() {
	const icon = document.getElementById("icon");

	if (!icon) {
		return CONTEXT_PATH + "/img/penguin.png";
	}

	const iconPath = icon.getAttribute("src");

	if (iconPath && iconPath.trim() !== "") {
		return iconPath;
	}

	return icon.dataset.fallback || CONTEXT_PATH + "/img/penguin.png";
}

// ServletへPOST送信
async function postForm(servletPath, params) {
	const response = await fetch(
		CONTEXT_PATH + servletPath,
		{
			method: "POST",
			headers: {
				"Content-Type":
					"application/x-www-form-urlencoded; charset=UTF-8"
			},
			body: params.toString()
		}
	);

	const responseText = await response.text();

	let data = {};

	if (responseText !== "") {
		try {
			data = JSON.parse(responseText);
		} catch (error) {
			data = {message: responseText};
		}
	}

	if (!response.ok) {
		console.error(
			"Servletエラー",
			response.status,
			responseText
		);

		throw new Error(data.message || "サーバー処理に失敗しました");
	}

	if (data.success === false) {
		throw new Error(data.message || "サーバー処理に失敗しました");
	}

	return data;
}

// メッセージ追加
function addMessage(message, user) {
	const row =document.createElement("div");
	const icon =document.createElement("img");
	const balloon =document.createElement("div");

	if (user) {
		row.className = "user-row";
		balloon.className = "user";
		icon.className = "user-icon";
		icon.src = getUserIconPath();

		balloon.textContent = message;

		row.appendChild(balloon);
		row.appendChild(icon);

	} else {
		row.className = "bot-row";
		balloon.className = "bot";
		icon.className = "bot-icon";
		icon.src =CONTEXT_PATH + "/img/penguin.png";

		balloon.textContent = message;

		row.appendChild(icon);
		row.appendChild(balloon);
	}

	document
		.getElementById("chatArea")
		.appendChild(row);

	scrollChatArea();
}

// 最初の選択ボタンを表示
function showButtons() {
	removeElement("buttons");
	removeElement("buttons1");
	removeElement("buttons5");

	const div = document.createElement("div");

	div.id = "buttons";

	const yes = document.createElement("button");

	yes.type = "button";
	yes.textContent = "やったよ";
	yes.onclick =
		() => answer("やったよ！");

	const no = document.createElement("button");

	no.type = "button";
	no.textContent = "これから";
	no.onclick =
		() => answer("これからだよ！");

	div.appendChild(yes);
	div.appendChild(no);

	document
		.getElementById("chatArea")
		.appendChild(div);

	scrollChatArea();
}

// 報告ボタンを表示
function reportButtons() {
	removeElement("buttons");

	const div = document.createElement("div");

	div.id = "buttons";

	const report = document.createElement("button");

	report.type = "button";
	report.textContent = "報告";
	report.onclick = openModal;

	div.appendChild(report);

	document
		.getElementById("chatArea")
		.appendChild(div);

	scrollChatArea();
}

// 時間指定とおまかせを表示
function showTimeselect() {
	removeElement("buttons");
	removeElement("buttons1");
	removeElement("buttons5");

	const div = document.createElement("div");

	div.id = "buttons1";

	const timeButton = document.createElement("button");

	timeButton.type = "button";
	timeButton.textContent = "時間指定";
	timeButton.onclick = openTimeModal;

	const autoButton = document.createElement("button");

	autoButton.type = "button";
	autoButton.textContent = "おまかせ";
	autoButton.onclick =
		() => answer("おまかせする！");

	div.appendChild(timeButton);
	div.appendChild(autoButton);

	document
		.getElementById("chatArea")
		.appendChild(div);

	scrollChatArea();
}

// 活動報告画面を開く
function openModal() {
	document
		.getElementById("modal")
		.classList.remove("hidden");
}

// 時間指定画面を開く
function openTimeModal() {
	document
		.getElementById("timeModal")
		.classList.remove("hidden");
}

// 活動報告画面を閉じる
function closeModal() {
	document
		.getElementById("modal")
		.classList.add("hidden");
}

// 時間指定画面を閉じる
function closeTimeModal() {
	document
		.getElementById("timeModal")
		.classList.add("hidden");
}

// SuggestServletの結果を保存
function setSuggestionViewState(data) {
	if (!data || typeof data.status !== "string" || !Array.isArray(data.suggestions)) {

		throw new Error("提案データの形式が正しくありません");
	}

	suggestionViewState = {
		currentIndex: 0,
		status: data.status,
		mode: data.mode,
		remainingMinutes:
			data.remainingMinutes,
		message: data.message,
		suggestions: data.suggestions
	};

	if (data.remainingMinutes !== null && data.remainingMinutes !== undefined) {

		remainTime = Number(data.remainingMinutes);
	}
}

// 提案結果を画面へ表示
function showSuggestionResult(nextState, displayType) {
	removeElement("buttons5");

	if (!suggestionViewState) {
		addMessage("提案情報を取得できませんでした",false);
		return;
	}

	if (suggestionViewState.status === "PREPARE") {
		addMessage(suggestionViewState.message || "家事はここまでにして次の用事の準備をしよう！",false);
		return;
	}

	if (suggestionViewState.status === "FINISH") {
		addMessage(suggestionViewState.message || "お疲れ様！今日の提案はここまでにしよう！",false);
		return;
	}

	if (suggestionViewState.status === "NO_SUGGESTION") {
		addMessage(suggestionViewState.message || "現在の条件で提案できる活動がありません", false);

		state = 3;
		showTimeselect();
		return;
	}

	if (suggestionViewState.status !== "CONTINUE" || suggestionViewState.suggestions.length === 0) {

		addMessage("提案できる活動がありません", false);
		return;
	}

	const currentWork =suggestionViewState.suggestions[suggestionViewState.currentIndex];

	if (suggestionViewState.mode === "TIME"
		&& displayType === "next"
		&& suggestionViewState.remainingMinutes !== null) {

		addMessage(
			"残り時間は"
			+ suggestionViewState.remainingMinutes
			+ "分だよ！",
			false
		);
	}

	if (displayType === "next") {
		addMessage(
			"次は" + currentWork.message,
			false
		);
	} else {
		addMessage(
			currentWork.message,
			false
		);
	}

	addMessage(
		"終わったら教えてね！",
		false
	);

	suggestionButtons();
	state = nextState;
	scrollChatArea();
}

// 時間指定で提案を開始
async function decideTime() {
	if (isSubmitting) {
		return;
	}

	const time =document.getElementById("timeSelect").value;

	const allowedTimes =["10", "15", "30", "45", "60"];

	if (!allowedTimes.includes(time)) {
		alert("時間は10分、15分、30分、45分、60分から選択してください");
		return;
	}

	removeElement("buttons1");
	closeTimeModal();
	addMessage(time + "分間で家事をする！", true);
	addMessage(time + "分間だね！", false);

	remainTime = Number(time);
	isSubmitting = true;

	const params =new URLSearchParams();

	params.append("action", "start");
	params.append("mode", "TIME");
	params.append("time", time);

	try {
		const data =
			await postForm("/SuggestServlet",params);

		setSuggestionViewState(data);
		showSuggestionResult(5, "start");

	} catch (error) {
		console.error(error);

		addMessage("提案を取得できませんでした", false);

		alert(error.message);
		showTimeselect();

	} finally {
		isSubmitting = false;
	}
}

// おまかせで提案を開始
async function decideHw() {
	if (isSubmitting) {
		return;
	}

	removeElement("buttons1");
	isSubmitting = true;

	const params = new URLSearchParams();

	params.append("action", "start");
	params.append("mode", "AUTO");

	try {
		const data =
			await postForm("/SuggestServlet", params);

		setSuggestionViewState(data);
		showSuggestionResult(6, "start");

	} catch (error) {
		console.error(error);

		addMessage("提案を取得できませんでした",false);

		alert(error.message);
		showTimeselect();

	} finally {
		isSubmitting = false;
	}
}

// 実施済み活動を登録
async function reportHw() {
	if (isSubmitting) {
		return;
	}

	const checked =
		document.querySelectorAll('#modal input[type="checkbox"]:checked');

	const activityIds = [];
	const activityNames = [];

	checked.forEach(item => {activityIds.push(item.value);

		activityNames.push(item.dataset.name|| item.value);
	});

	if (activityIds.length === 0) {
		alert("家事を選択してください");
		return;
	}

	const invalidId =
		activityIds.some(
			activityId =>
				!/^[0-9]+$/.test(activityId)
		);

	if (invalidId) {
		alert("活動IDが正しく設定されていません");
		return;
	}

	const params = new URLSearchParams();

	params.append("action", "checkActivity");

	activityIds.forEach(activityId => {
		params.append("activityId", activityId);
	});

	isSubmitting = true;

	try {
		await postForm("/ReportServlet", params
		);

		removeElement("buttons");

		addMessage(
			activityNames.join("、")
			+ "をやったよ！",
			true
		);

		addMessage("OK！頑張ったね！",false);

		addMessage("時間指定する？おまかせにする？",false);

		checked.forEach(item => {
			item.checked = false;
		});

		closeModal();
		state = 3;
		showTimeselect();

	} catch (error) {
		console.error(error);

		alert(
			"履歴の登録に失敗しました\n"
			+ error.message
		);

	} finally {
		isSubmitting = false;
	}
}

// 提案操作ボタンを表示
function suggestionButtons() {
	removeElement("buttons5");

	const div = document.createElement("div");

	div.id = "buttons5";

	const resuggestion = document.createElement("button");

	resuggestion.type = "button";
	resuggestion.textContent = "再提案";
	resuggestion.onclick =
		() => answer("再提案！");

	const finish = document.createElement("button");

	finish.type = "button";
	finish.textContent = "終わったよ";
	finish.onclick =
		() => answer("終わったよ！");

	div.appendChild(resuggestion);
	div.appendChild(finish);

	document
		.getElementById("chatArea")
		.appendChild(div);

	scrollChatArea();
}

// 次の候補を表示
function showNextSuggestion(nextState) {
	if (!suggestionViewState) {
		return;
	}

	removeElement("buttons5");

	suggestionViewState.currentIndex++;

	if (suggestionViewState.currentIndex
		>= suggestionViewState.suggestions.length) {

		addMessage(
			"ほかの提案候補はありません",
			false
		);

		addMessage(
			"時間指定する？おまかせにする？",
			false
		);

		suggestionViewState = null;
		state = 3;
		showTimeselect();
		return;
	}
	nextwork =
		suggestionViewState.suggestions[
		suggestionViewState.currentIndex
		];

	addMessage(
		nextwork.message,
		false
	);

	addMessage(
		"終わったら教えてね！",
		false
	);

	suggestionButtons();
	state = nextState;
}

// 現在の提案を完了
async function completeCurrentSuggestion(nextState) {
	if (isSubmitting) {
		return;
	}

	if (!suggestionViewState
		|| !Array.isArray(
			suggestionViewState.suggestions
		)) {

		alert("提案情報がありません");
		return;
	}

	const currentWork =
		suggestionViewState.suggestions[
		suggestionViewState.currentIndex
		];

	if (!currentWork) {
		alert("完了する活動がありません");
		return;
	}

	isSubmitting = true;
	removeElement("buttons5");

	let historyRegistered = false;

	try {
		const reportParams = new URLSearchParams();

		reportParams.append(
			"action",
			"complete"
		);

		reportParams.append(
			"activityId",
			String(currentWork.activityId)
		);

		await postForm(
			"/ReportServlet",
			reportParams
		);

		historyRegistered = true;

		const suggestParams = new URLSearchParams();

		suggestParams.append(
			"action",
			"complete"
		);

		suggestParams.append(
			"activityId",
			String(currentWork.activityId)
		);

		const suggestData =
			await postForm(
				"/SuggestServlet",
				suggestParams
			);

		addMessage(
			"できたね！",
			false
		);

		setSuggestionViewState(
			suggestData
		);

		showSuggestionResult(
			nextState,
			"next"
		);

	} catch (error) {
		console.error(error);

		if (historyRegistered) {
			addMessage(
				"活動は記録できたけど次の提案を取得できなかったよ...",
				false
			);

			state = 3;
			showTimeselect();

		} else {
			alert(
				"活動の登録に失敗しました\n"
				+ error.message
			);

			suggestionButtons();
		}

	} finally {
		isSubmitting = false;
	}
}

// ボタン選択時の処理
function answer(value) {
	removeElement("buttons");

	addMessage(value, true);

	switch (state) {
		case 0:
			// 実施済み活動の確認
			if (value === "やったよ！") {
				addMessage(
					"もうやったんだ、すごい！何やったか教えて！",
					false
				);

				state = 1;
				reportButtons();

			} else {
				addMessage(
					"OK！これから頑張ろう！",
					false
				);

				state = 3;
				showTimeselect();
			}
			break;

		case 1:
			// 報告画面を表示
			if (value === "報告") {
				openModal();
			}
			break;

		case 2:
			// 報告完了
			if (value === "報告する！") {
				addMessage(
					"OK！頑張ったね！",
					false
				);
			}
			break;

		case 3:
			// おまかせを開始
			if (value === "おまかせする！") {
				removeElement("buttons1");

				addMessage(
					"おまかせだね！",
					false
				);

				decideHw();
			}
			break;

		case 5:
			// 時間指定の提案操作
			if (value === "再提案！") {
				showNextSuggestion(5);

			} else if (
				value === "終わったよ！"
			) {
				completeCurrentSuggestion(5);
			}
			break;

		case 6:
			// おまかせの提案操作
			if (value === "再提案！") {
				showNextSuggestion(6);

			} else if (
				value === "終わったよ！"
			) {
				completeCurrentSuggestion(6);
			}
			break;

		default:
			break;
	}
}

// チャットを開始
addMessage(
	"お疲れ様！何か家事やった？",
	false
);

showButtons();