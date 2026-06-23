/**
 * 
 */

document.getElementById('userreg').onsubmit = function(event){
	if(window.confirm('登録します。よろしいですか？') === false){
		event.preventDefault();
	}
    let name = document.getElementById('userreg').name.value;
    let pw = document.getElementById('userreg').password.value;
    let pw2 = document.getElementById('userreg').password2.value;
    if (name === ''){
        document.getElementById('msg').textContent = 'ユーザーニックネームを入力してください。';
        event.preventDefault();
    } else if (pw === '' || pw2 === ''){
    	document.getElementById('msg').textContent = 'パスワードを入力してください。';
        event.preventDefault();
    } else if (pw !== pw2){
    	document.getElementById('msg').textContent = 'パスワードが一致しません。';
        event.preventDefault();
    }
}

//パスワード 表示切替
document.getElementById("togglePassword").addEventListener("click", function () {
  const pw = document.getElementById("password");

  if (pw.type === "password") {
    pw.type = "text";
    this.src = "img/eye-close.png"; // 非表示アイコンに切り替え
  } else {
    pw.type = "password";
    this.src = "img/eye.png"; // 表示アイコンに戻す
  }
});

//パスワード確認 表示切替
document.getElementById("togglePassword2").addEventListener("click", function () {
  const pw = document.getElementById("password2");

  if (pw.type === "password") {
    pw.type = "text";
    this.src = "img/eye-close.png"; // 非表示アイコンに切り替え
  } else {
    pw.type = "password";
    this.src = "img/eye.png"; // 表示アイコンに戻す
  }
});