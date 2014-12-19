<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ include file="../00-header.jsp" %>
<script type="text/javascript">
$(document).ready(function() {
<%@ include file="../../static/js/snippets/datepicker-future.js" %>
<%@ include file="../../static/js/snippets/populateUpdate.js" %>
});
</script>
<div class="content">
<section><span>Registered Users</span></section>
<table class="res" id="usersTable">
	<thead><tr><th>User ID Tag</th><th>Parent ID Tag</th><th>Expiry Date/Time</th><th>In Transaction?</th><th>Blocked?</th></tr></thead>
	<tbody>
	<%-- Start --%>
	<c:forEach items="${userList}" var="user">
	<tr id="${user.idTag}"><td>${user.idTag}</td><td>${user.parentIdTag}</td><td>${user.expiryDate}</td><td>${user.inTransaction}</td><td>${user.blocked}</td></tr>
	</c:forEach>
	<%-- End --%>
</tbody>
</table>
<br>
<section><span>User Management</span></section>
<div class="left-menu">
	<ul id="dm-menu">
		<li><a href="#" name="add" class="highlight">Add</a></li>
		<li><a href="#" name="update">Update</a></li>
		<li><a href="#" name="delete">Delete</a></li>
	</ul>
</div>
<div class="right-content">
	<div id="add">
		<form method="POST" action="/steve/manager/users/add">
			<table class="userInput">
				<tr><td>User ID Tag (string):</td><td><input type="text" name="idTag" required></td></tr>
				<tr><td>Parent ID Tag:</td>
					<td>
						<select name="parentIdTag">
						<option value="" selected="selected">-- Empty --</option>
						<%-- Start --%>
						<c:forEach items="${userList}" var="user">
						<option value="${user.idTag}">${user.idTag}</option>
						</c:forEach>
						<%-- End --%>
						</select>
					</td></tr>
				<tr><td>Expiry Date/Time (ex: 2011-12-21 at 11:30):</td>
					<td>
						<input type="text" name="expiryDate" class="datepicker" placeholder="optional"> at 
						<input type="text" name="expiryTime" class="timepicker" placeholder="optional">
					</td>
				</tr>
				<tr><td></td><td id="add_space"><input type="submit" value="Add"></td></tr>
			</table>
		</form>
	</div>
	<div id="update">
		<form method="POST" action="/steve/manager/users/update">
			<table class="userInput">
				<tr><td>User ID Tag:</td><td>
					<select name="idTag" id="idTagUpdate">
					<option selected="selected" disabled="disabled" style="display:none;">Choose...</option>
					<%-- Start --%>
					<c:forEach items="${userList}" var="user">
					<option value="${user.idTag}">${user.idTag}</option>
					</c:forEach>
					<%-- End --%>
					</select>
				</td></tr>
				<tr><td>Parent ID Tag:</td>
				<td>
					<select name="parentIdTag" id="update-pid" disabled>
					<option value="" selected="selected">-- Empty --</option>
					<%-- Start --%>
					<c:forEach items="${userList}" var="user">
					<option value="${user.idTag}">${user.idTag}</option>
					</c:forEach>
					<%-- End --%>
					</select>
				</td></tr>
				<tr><td>Expiry Date/Time (ex: 2011-12-21 at 11:30):</td>
					<td>
						<input type="text" name="expiryDate" id="update-exdate" class="datepicker" disabled> at 
						<input type="text" name="expiryTime" id="update-extime" class="timepicker" disabled>
					</td>
				</tr>
				<tr><td>Block the ID Tag:</td><td><input type="radio" name="blockUser" value="false" id="update-block-false" disabled> false</td></tr>
				<tr><td></td><td><input type="radio" name="blockUser" value="true" id="update-block-true" disabled> true</td></tr>
				<tr><td></td><td id="add_space"><input type="submit" value="Update" id="update-submit" disabled></td></tr>
			</table>
		</form>
	</div>
	<div id="delete">
		<div class="warning"><b>Warning:</b> Deleting a user causes losing all related information including transactions and reservations.</div>
		<form method="POST" action="/steve/manager/users/delete">
			<table class="userInput">
				<tr><td>User ID Tag:</td><td>
					<select name="idTag" required>
					<option selected="selected" disabled="disabled" style="display:none;">Choose...</option>
					<%-- Start --%>
					<c:forEach items="${userList}" var="user">
					<option value="${user.idTag}">${user.idTag}</option>
					</c:forEach>
					<%-- End --%>
					</select>
				</td></tr>
				<tr><td></td><td id="add_space"><input type="submit" value="Delete"></td></tr>
			</table>
		</form>
	</div>
</div>
</div>
<%@ include file="../00-footer.jsp" %>