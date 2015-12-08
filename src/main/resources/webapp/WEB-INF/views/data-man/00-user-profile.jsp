<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<table class="userInput">
	<thead><tr><th>Profile</th><th></th></thead>
	<tr><td>First name:</td><td><form:input path="firstName"/></td></tr>
	<tr><td>Last name:</td><td><form:input path="lastName"/></td></tr>
	<tr><td>Birthday:</td><td><form:input path="birthDay" cssClass="datePicker"/></td></tr>
	<tr>
		<td>Sex:</td>
		<td><form:select path="sex">
			<form:options items="${sex}" itemLabel="value"/>
		</form:select>
		</td>
	</tr>
	<tr><td>Phone:</td><td><form:input path="phone"/></td></tr>
	<tr><td>E-mail:</td><td><form:input path="eMail"/></td></tr>
	<tr><td>Additional Note:</td><td><form:input path="note"/></td></tr>
</table>