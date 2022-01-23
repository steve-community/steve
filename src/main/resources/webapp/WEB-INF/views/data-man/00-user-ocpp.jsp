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