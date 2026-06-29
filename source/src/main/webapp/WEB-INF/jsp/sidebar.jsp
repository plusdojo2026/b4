<%@ page language="java"
	pageEncoding="UTF-8"%>

<%
	String sidebarContextPath =
			request.getContextPath();
%>

<!-- サイドバー -->
<aside id="toggleSidebar"
	class="toggle-sidebar">

	<ul>
		<li>
			<a
				href="<%= sidebarContextPath %>/ChatServlet"
				class="side">
				チャット
			</a>
		</li>

		<li>
			<a
				href="<%= sidebarContextPath %>/SettingServlet"
				class="side">
				ユーザー設定
			</a>
		</li>

		<li>
			<a
				href="<%= sidebarContextPath %>/RecordDetailServlet"
				class="side">
				記録一覧
			</a>
		</li>

		<li>
			<a
				href="<%= sidebarContextPath %>/ReminderServlet"
				class="side">
				リマインダー
			</a>
		</li>
	</ul>

</aside>