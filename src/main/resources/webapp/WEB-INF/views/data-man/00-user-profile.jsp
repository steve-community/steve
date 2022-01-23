<%--

    SteVe - SteckdosenVerwaltung - https://github.com/RWTH-i5-IDSG/steve
    Copyright (C) 2013-2022 RWTH Aachen University - Information Systems - Intelligent Distributed Systems Group (IDSG).
    All Rights Reserved.

    This program is free software: you can redistribute it and/or modify
    it under the terms of the GNU General Public License as published by
    the Free Software Foundation, either version 3 of the License, or
    (at your option) any later version.

    This program is distributed in the hope that it will be useful,
    but WITHOUT ANY WARRANTY; without even the implied warranty of
    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
    GNU General Public License for more details.

    You should have received a copy of the GNU General Public License
    along with this program.  If not, see <https://www.gnu.org/licenses/>.

--%>
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
	<tr><td>Additional Note:</td><td><form:textarea path="note"/></td></tr>
</table>