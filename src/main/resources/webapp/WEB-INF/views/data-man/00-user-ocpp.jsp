<%--

    SteVe - SteckdosenVerwaltung - https://github.com/steve-community/steve
    Copyright (C) 2013-2025 SteVe Community Team
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
	<thead><tr><th>OCPP ID Tags</th><th></th></thead>
	<tbody>
	<tr>
		<td style="vertical-align:top">
			<input type="button" value="Select None" onClick="selectNone(document.getElementById('idTagList'))">
		</td>
		<td>
			<form:select path="idTagList" size="5" multiple="true">
				<c:forEach items="${idTagList}" var="idTag">
					<form:option value="${idTag}" label="${idTag}"/>
				</c:forEach>
			</form:select>
		</td>
	</tr>
	<tr><td></td>
		<td id="add_space">
			<input type="submit" name="${submitButtonName}" value="${submitButtonValue}">
			<input type="submit" name="backToOverview" value="Back to Overview">
		</td></tr>
	</tbody>
</table>
