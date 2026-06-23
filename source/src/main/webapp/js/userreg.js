/**
 * 
 */

document.getElementById('userreg').onsubmit = function(event){
	if(window.confirm('登録します。よろしいですか？') === false){
		event.preventDefault();
	}
    let name = document.getElementById('userreg').name.value;
    let pw = document.getElementById('userreg').pw.value;
    let pw2 = document.getElementById('userreg').pw2.value;
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