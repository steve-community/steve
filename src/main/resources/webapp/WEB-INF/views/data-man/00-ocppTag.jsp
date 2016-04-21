<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<table class="userInput">
	<tr>
		<td>ID Tag:</td>
		<td>
			<form:input path="idTag" readonly="${isUpdateForm}"/>
			<a class="tooltip" href="#"><img src="${ctxPath}/static/images/info.png" style="vertical-align:middle">
				<span>This field is set when adding an OCPP Tag, and cannot be changed later</span>
			</a>
		</td>
	</tr>
	<tr>
		<td>Parent ID Tag:</td>
		<td><form:select path="parentIdTag" items="${idTagList}"/></td>
	</tr>
	<tr><td>Expiry Date/Time:</td><td><form:input path="expiration" cssClass="dateTimePicker"/></td></tr>

	<%-- print inTransaction only when updating, otherwise unnecessary --%>
	<c:if test="${isUpdateForm}">
		<tr><td>In Transaction?:</td><td>${inTransaction}</td></tr>
	</c:if>

	<tr><td>Block the ID Tag:</td>
		<td><form:radiobutton path="blocked" value="false"/> false</td>
	</tr>
	<tr>
		<td></td>
		<td><form:radiobutton path="blocked" value="true"/> true</td>
	</tr>
	<tr><td>Additional Note:</td><td><form:textarea path="note"/></td></tr>
	<tr><td></td>
		<td id="add_space">
			<input type="submit" name="${submitButtonName}" value="${submitButtonValue}">
			<input type="submit" name="backToOverview" value="Back to Overview">
		</td>
	</tr>
</table>