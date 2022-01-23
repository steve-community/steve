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
		<tr><td>Max. Active Transaction Count:</td><td><form:input path="maxActiveTransactionCount" placeholder="if empty, 1 will be assumed"/></td></tr>
		<tr><td><i>
			Set to <b>0</b> to block this tag. Set to a <b>negative</b> value to disable concurrent transaction checks
			(i.e. every transaction<br>will be allowed). Set to a <b>positive</b> value to control the number of
			active transactions that is allowed.
		</i></td><td></td></tr>
	</tbody>
</table>
<table class="userInput">
	<thead><tr><th>Misc.</th><th></th></thead>
	<tbody>
	<%-- print this only when updating, otherwise unnecessary --%>
	<c:if test="${isUpdateForm}">
		<tr><td>Active Transaction Count:</td><td>${activeTransactionCount}</td></tr>
	</c:if>
	<tr><td>Additional Note:</td><td><form:textarea path="note"/></td></tr>
	<tr><td></td>
		<td id="add_space">
			<input type="submit" name="${submitButtonName}" value="${submitButtonValue}">
			<input type="submit" name="backToOverview" value="Back to Overview">
		</td>
	</tr>
	</tbody>
</table>