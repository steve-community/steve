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
<%@ include file="00-header.jsp" %>
<div class="content">
	<div>
		<section><span>GetConfigurationResponse from ${chargeBoxId}</span></section>
		<c:if test="${not empty response.unknownKeys}">
			<div class="warning">
				<b>Unknown keys:</b> ${response.unknownKeys}
			</div>
			<br>
		</c:if>
		<table class="res">
			<thead>
			<tr>
				<th>Key</th>
				<th>Value</th>
				<th>Read only?</th>
			</tr>
			</thead>
			<tbody>
			<c:forEach items="${response.configurationKeys}" var="element">
				<tr>
					<td>${element.key}</td>
					<td>${element.value}</td>
					<td>${element.readonly}</td>
				</tr>
			</c:forEach>
			</tbody>
		</table>
	</div>
</div>
<%@ include file="00-footer.jsp" %>
