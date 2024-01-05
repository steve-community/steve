<%--

    SteVe - SteckdosenVerwaltung - https://github.com/steve-community/steve
    Copyright (C) 2013-2024 SteVe Community Team
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
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<table class="userInput">
	<tr>
		<td style="vertical-align:top">Select one:</td>
		<td>
			<form:select path="chargePointSelectList" size="5" multiple="false">
				<c:forEach items="${cpList}" var="cp">
					<form:option value="${cp.ocppTransport};${cp.chargeBoxId};${cp.endpointAddress}" label="${cp.chargeBoxId}"/>
				</c:forEach>
			</form:select>
		</td>
	</tr>
</table>
<br>