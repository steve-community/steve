<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<table class="userInput">
	<thead><tr><th>Address</th><th></th></thead>
	<tr><td>Street:</td><td><form:input path="address.street"/></td></tr>
	<tr><td>House Number:</td><td><form:input path="address.houseNumber"/></td></tr>
	<tr><td>Zip code:</td><td><form:input path="address.zipCode"/></td></tr>
	<tr><td>City:</td><td><form:input path="address.city"/></td></tr>
	<tr><td>Country:</td><td><form:select path="address.country" items="${countryCodes}"/></td></tr>
</table>