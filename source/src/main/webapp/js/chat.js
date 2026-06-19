/**
 * 
 */
 
        let state = 0;
        let remainTime = 0;
        let currentTaskTime = 0;

        // メッセージ追加
        function addMessage(message, user) {


            if (user) {
                const row = document.createElement("div");
                row.className = "user-row";

                row.innerHTML = `
            <div class="user">${message}</div>
            <img src="/b4/img/gorilla.png" class="user-icon">
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

        function decideTime() {

            const time = document.getElementById("timeSelect").value;

            addMessage(time + "分間で家事をする！", true);

            remainTime = time;

            addMessage(time + "分間だね！", false);

            closeTimeModal();

            // ここで家事提案処理
            const housework = "掃除機";
            addMessage("まずは" + housework + "をやろう！", false
            );
            addMessage("終わったら教えてね！");
            currentTaskTime = 20;

            suggestionButtons();
            state = 5;

            document.getElementById("chatArea").scrollTop = document.getElementById("chatArea").scrollHeight;
        }


        function reportHw() {

            const checked =
                document.querySelectorAll(
                    '#modal input[type="checkbox"]:checked'
                );

            let result = [];

            checked.forEach(item => {
                result.push(item.value);
            });

            if (result.length === 0) {
                alert("家事を選択してください");
                return;
            }

            addMessage(
                result.join("、") + "をやったよ！",
                true
            );

            addMessage("ok！頑張ったね！", false);
            addMessage("時間指定する？お任せにする？", false);
            showTimeselect();

            state = 3;
            closeModal();

            document.getElementById("chatArea").scrollTop = document.getElementById("chatArea").scrollHeight;
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
                        addMessage("おまかせだね！", false);
                        addMessage("この家事をやろう！", false);
                        suggestionButtons();
                        state = 5;
                    }
                    break;

                case 4: //指定するボタン
                    if (value == "○分間で家事をする！") {
                        addMessage("○分間だね！", false);
                        addMessage("この家事をやろう！", false);
                        suggestionButtons();
                        state = 5;
                    }
                    break;

                case 5: //再提案
                    console.log(value);
                    if (value == "再提案！") {
                        addMessage("じゃあ○○をやるのはどう？", false);
                        addMessage("終わったら教えてね！", false);
                        suggestionButtons();
                        state = 5;
                    }
                    else if (value == "終わったよ！") {
                        currentTaskTime = 15;
                        remainTime -= currentTaskTime;
                        if (remainTime > 0) {
                            addMessage("次は洗濯をやろう！", false);
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
                            state = 99;
                        }
                    }

                    break;




                    break;

                case 99:
                    addMessage("家事は終了しています。", false);

                    break;
         }
         }  