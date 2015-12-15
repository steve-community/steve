<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<table class="userInput">
	<thead><tr><th>Misc.</th><th></th></thead>
	<tr><td>Description:</td><td><form:input path="description"/></td></tr>
	<tr><td>Latitude:</td><td><form:input path="locationLatitude"/></td></tr>
	<tr><td>Longitude:</td><td><form:input path="locationLongitude"/></td></tr>
	<tr><td>Additional Note:</td><td><form:textarea path="note"/></td></tr>
	<tr><td></td>
		<td id="add_space">
			<input type="submit" name="${submitButtonName}" value="${submitButtonValue}">
			<input type="submit" name="backToOverview" value="Back to Overview">
		</td></tr>
</table>