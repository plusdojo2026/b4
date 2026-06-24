//サイドバー開閉
function toggleSidebar() {
    const sidebar = document.getElementById('sidebar');
    const overlay = document.getElementById('overlay');
    
    sidebar.classList.toggle('open');
    overlay.classList.toggle('open');
}

//アイコンを画面に反映
function selectIcon(id, name) {
    // メインの丸型アイコン
    document.getElementById('displayIcon').src = 'img/' + name;
    // サーバーに送る
    document.getElementById('hiddenIconName').value = id;
}

//アイコン選択を開く
function openModal() {
    document.getElementById('modal').style.display = 'block';
}

//アイコン選択を閉じる
function closeModal() {
    document.getElementById('modal').style.display = 'none';
}