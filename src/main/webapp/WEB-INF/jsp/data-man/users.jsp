<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ include file="/WEB-INF/jsp/00-header.jsp" %>
<script type="text/javascript">
$(document).ready(function() {
	var idd = $('#idTagUpdate');
	idd.change(function(){ 
		// get the selected idTag
		var str = '#' + idd.find('option:selected').text();
		// get the row
		var row = $('#usersTable').find(str);
		// enable input fields
		$("#update-pid, #update-exdate, #update-block-false, #update-block-true").prop("disabled", false);		
	    // iterate over the row cells and populate inputs
	    $("#update-pid").val(row.find("td:eq(1)").html());
	    $("#update-exdate").val(row.find("td:eq(2)").html());
	    if (row.find("td:eq(4)").html() == 'false') {
	    	$("#update-block-false").prop("checked", true);
	    } else {
	    	$("#update-block-true").prop("checked", true);
	    }
	});
});
</script>
<section><span>Registered Users</span></section>
<table class="res" id="usersTable">
	<thead><tr><th>idTag</th><th>parentIdTag</th><th>expiryDate</th><th>inTransaction</th><th>blocked</th></tr></thead>
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
			<table>
				<tr><td>idTag (string):</td><td><input type="text" name="idTag"></td></tr>
				<tr><td>parentIdTag (string):</td><td><input type="text" name="parentIdTag" placeholder="optional"></td></tr>
				<tr><td>Expiry date and time (ex: 2011-12-21 11:30):</td><td><input type="text" name="expiryDate" placeholder="optional"></td></tr>
				<tr><td></td><td id="add_space"><input type="submit" value="Add"></td></tr>
			</table>
		</form>
	</div>
	<div id="update">
		<form method="POST" action="/steve/manager/users/update">
			<table>
				<tr><td>idTag:</td><td>
					<select name="idTag" id="idTagUpdate">
					<option selected="selected" disabled="disabled" style="display:none;">Choose...</option>
					<%-- Start --%>
					<c:forEach items="${userList}" var="user">
					<option value="${user.idTag}">${user.idTag}</option>
					</c:forEach>
					<%-- End --%>
					</select>
				</td></tr>
				<tr><td>parentIdTag (string):</td><td><input type="text" name="parentIdTag" id="update-pid" disabled></td></tr>
				<tr><td>Expiry date and time (ex: 2011-12-21 11:30):</td><td><input type="text" name="expiryDate" id="update-exdate" disabled></td></tr>
				<tr><td>Block the user:</td><td><input type="radio" name="blockUser" value="false" id="update-block-false" disabled> false</td></tr>
				<tr><td></td><td><input type="radio" name="blockUser" value="true" id="update-block-true" disabled> true</td></tr>
				<tr><td></td><td id="add_space"><input type="submit" value="Update"></td></tr>
			</table>
		</form>
	</div>
	<div id="delete">
		<div class="warning"><b>Warning:</b> Deleting a user causes losing all related information including transactions and reservations.</div>
		<form method="POST" action="/steve/manager/users/delete">
			<table>
				<tr><td>idTag:</td><td>
					<select name="idTag">
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
<%@ include file="/WEB-INF/jsp/00-footer.jsp" %>