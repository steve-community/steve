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
<script type="text/javascript">
	$(document).ready(function() {
		<%@ include file="snippets/sortable.js" %>
	});
</script>
<div class="content"><div>
<section><span>
Connector Status
	<a class="tooltip" href="#"><img src="${ctxPath}/static/images/info.png" style="vertical-align:middle">
		<span>Last status information and corresponding date/time of connectors received from charging stations.
			The OCPP term 'connector' refers to the charging socket of a station.</span>
	</a>
</span></section>

<form:form action="${ctxPath}/manager/home/connectorStatus/query" method="get" modelAttribute="params">
	<table class="userInput">
		<tr>
			<td>ChargeBox ID:</td>
			<td><form:select path="chargeBoxId">
				<option value="" selected>All</option>
				<form:options items="${cpList}"/>
			</form:select>
			</td>
		</tr>
		<tr>
			<td>Status:</td>
			<td><form:select path="status">
				<option value="" selected>All</option>
				<form:options items="${statusValues}"/>
			</form:select>
			</td>
		</tr>
		<tr>
			<td></td>
			<td id="add_space">
				<input type="submit" value="Get">
			</td>
		</tr>
	</table>
</form:form>
<br>

<table class="res" id="connectorStatusTable">
	<thead>
		<tr>
			<th data-sort="string">ChargeBox ID</th>
			<th data-sort="int">Connector ID</th>
			<th data-sort="date">Date/Time</th>
			<th data-sort="string">Status</th>
			<th data-sort="string">Error Code</th>
		</tr>
	</thead>
	<tbody>
		<c:forEach items="${connectorStatusList}" var="cs">
			<tr>
				<td>
                    <a href="${ctxPath}/manager/chargepoints/details/${cs.chargeBoxPk}">${cs.chargeBoxId}</a>
                    <c:if test="${cs.jsonAndDisconnected}">
                        <a class="tooltip" href="#"><img src="${ctxPath}/static/images/offline-icon.svg" style="height: 1em">
                            <span>This JSON charge point is currently disconnected. The status information of its
                            connectors might be not up-to-date.</span>
                        </a>
                    </c:if>
				</td>
				<td>${cs.connectorId}</td>
				<td data-sort-value="${cs.statusTimestamp.millis}">${cs.timeStamp}</td>
				<td>${cs.status}</td>
				<td>${cs.errorCode}</td>
			</tr>
		</c:forEach>
	</tbody>
</table>
</div></div>
<%@ include file="00-footer.jsp" %>