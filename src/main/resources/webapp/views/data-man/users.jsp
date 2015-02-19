<%@ include file="../00-header.jsp" %>
<script type="text/javascript">
    $(document).ready(function() {
        <%@ include file="../../static/js/snippets/populateUpdate.js" %>
        <%@ include file="../../static/js/snippets/dateTimePicker-future.js" %>
    });
</script>
<spring:hasBindErrors name="userAddForm">
    <div class="error">
        Error while trying to add a user:
        <ul>
            <c:forEach var="error" items="${errors.allErrors}">
                <li>${error.defaultMessage}</li>
            </c:forEach>
        </ul>
    </div>
</spring:hasBindErrors>
<spring:hasBindErrors name="userUpdateForm">
    <div class="error">
        Error while trying to update a user:
        <ul>
            <c:forEach var="error" items="${errors.allErrors}">
                <li>${error.defaultMessage}</li>
            </c:forEach>
        </ul>
    </div>
</spring:hasBindErrors>
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
        <form:form action="/steve/manager/users/add" modelAttribute="userAddForm">
			<table class="userInput">
                <tr><td>User ID Tag (string):</td><td><form:input path="idTag"/></td></tr>
				<tr><td>Parent ID Tag:</td>
					<td>
                        <form:select path="parentIdTag">
                            <option value="EMPTY-OPTION" selected="selected">-- Empty --</option>
                            <c:forEach items="${userList}" var="user">
                                <option value="${user.idTag}">${user.idTag}</option>
                            </c:forEach>
                        </form:select>
					</td></tr>
				<tr><td>Expiry Date/Time:</td>
					<td>
                        <form:input path="expiration" placeholder="optional" cssClass="dateTimePicker"/>
					</td>
				</tr>
				<tr><td></td><td id="add_space"><input type="submit" value="Add"></td></tr>
			</table>
		</form:form>
	</div>
	<div id="update">
        <form:form action="/steve/manager/users/update" modelAttribute="userUpdateForm">
			<table class="userInput">
				<tr><td>User ID Tag:</td><td>
                    <form:select path="idTag" id="idTagUpdate">
                        <option selected="selected" style="display:none;" disabled>Choose...</option>
                        <c:forEach items="${userList}" var="user">
                            <option value="${user.idTag}">${user.idTag}</option>
                        </c:forEach>
                    </form:select>
				</td></tr>
				<tr><td>Parent ID Tag:</td>
				<td>
                    <form:select path="parentIdTag" id="update-pid" disabled="true">
                        <option value="EMPTY-OPTION" selected="selected">-- Empty --</option>
                        <c:forEach items="${userList}" var="user">
                            <option value="${user.idTag}">${user.idTag}</option>
                        </c:forEach>
                    </form:select>
				</td></tr>
				<tr><td>Expiry Date/Time:</td>
					<td>
                        <form:input path="expiration" id="update-exdateTime" disabled="true" cssClass="dateTimePicker"/>
					</td>
				</tr>
				<tr><td>Block the ID Tag:</td>
                    <td><form:radiobutton path="blocked" value="false" id="update-block-false" disabled="true"/> false</td>
                </tr>
				<tr>
                    <td></td>
                    <td><form:radiobutton path="blocked" value="true" id="update-block-true" disabled="true"/> true</td>
                </tr>
				<tr><td></td><td id="add_space"><input type="submit" value="Update" id="update-submit" disabled></td></tr>
			</table>
		</form:form>
	</div>
	<div id="delete">
		<div class="warning"><b>Warning:</b> Deleting a user causes losing all related information including transactions and reservations.</div>
		<form:form action="/steve/manager/users/delete">
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
		</form:form>
	</div>
</div>
</div>
<%@ include file="../00-footer.jsp" %>