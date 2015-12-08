<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<table class="userInput">
	<thead><tr><th>OCPP</th><th></th></thead>
	<tbody>
	<tr>
		<td>OCPP ID Tag:</td>
		<td><form:select path="ocppIdTag" items="${idTagList}" /></td>
	</tr>
	<tr><td></td>
		<td id="add_space">
			<input type="submit" name="${submitButtonName}" value="${submitButtonValue}">
			<input type="submit" name="backToOverview" value="Back to Overview">
		</td></tr>
	</tbody>
</table>