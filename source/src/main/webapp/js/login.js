/**
 * 
 */
 
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