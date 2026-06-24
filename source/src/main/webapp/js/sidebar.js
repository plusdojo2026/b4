/**
 * 
 */
 
 document.getElementById('toggleBtn').addEventListener('click', function() {
  document.getElementById('toggleSidebar').classList.toggle('active');
  this.classList.toggle('active');
});